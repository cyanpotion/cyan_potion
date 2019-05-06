/*
 * MIT License
 *
 * Copyright (c) 2019 XenoAmess
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.xenoamess.cyan_potion.base;

import com.codedisaster.steamworks.SteamAPI;
import com.codedisaster.steamworks.SteamApps;
import com.codedisaster.steamworks.SteamException;
import com.codedisaster.steamworks.SteamUserStats;
import com.xenoamess.cyan_potion.base.audio.AudioManager;
import com.xenoamess.cyan_potion.base.console.ConsoleThread;
import com.xenoamess.cyan_potion.base.events.Event;
import com.xenoamess.cyan_potion.base.gameWindowComponents.AbstractGameWindowComponent;
import com.xenoamess.cyan_potion.base.gameWindowComponents.GameWindowComponentTree;
import com.xenoamess.cyan_potion.base.io.FileUtil;
import com.xenoamess.cyan_potion.base.io.input.Gamepad.GamepadInput;
import com.xenoamess.cyan_potion.base.io.input.key.Keymap;
import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import com.xenoamess.cyan_potion.base.plugins.CodePluginManager;
import com.xenoamess.cyan_potion.base.plugins.CodePluginPosition;
import com.xenoamess.cyan_potion.base.visual.Font;
import com.xenoamess.multi_language.MultiLanguageStructure;
import com.xenoamess.multi_language.MultiLanguageX8lFileUtil;
import com.xenoamess.x8l.AbstractTreeNode;
import com.xenoamess.x8l.ContentNode;
import com.xenoamess.x8l.TextNode;
import com.xenoamess.x8l.X8lTree;
import org.lwjgl.system.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.xenoamess.cyan_potion.base.GameManagerConfig.*;
import static com.xenoamess.cyan_potion.base.plugins.CodePluginPosition.*;

/**
 * @author XenoAmess
 */
public class GameManager implements AutoCloseable {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(GameManager.class);

    private final AtomicBoolean alive = new AtomicBoolean(false);

    private final ConcurrentLinkedDeque<Event> eventList =
            new ConcurrentLinkedDeque<>();
    private ConsoleThread consoleThread = new ConsoleThread(this);
    private GameWindow gameWindow = null;
    private final Callbacks callbacks = new Callbacks(this);
    private SteamUserStats steamUserStats = null;
    private final DataCenter dataCenter = new DataCenter(this);
    private final CodePluginManager codePluginManager = new CodePluginManager();

    private Keymap keymap;
    private GamepadInput gamepadInput;
    private GameWindowComponentTree gameWindowComponentTree;
    private long nowFrameIndex = 0L;
    private Map<String, String> argsMap;

    private final AudioManager audioManager = new AudioManager(this);
    private ResourceManager resourceManager;
    private final ExecutorService executorService =
            Executors.newCachedThreadPool();

    public static Map<String, String> generateArgsMap(String[] args) {
        Map<String, String> res = new LinkedHashMap<>();
        if (args == null) {
            return res;
        }
        for (String au : args) {
            int index = au.indexOf('=');
            if (index == -1) {
                res.put(au, "");
            } else {
                res.put(au.substring(0, index), au.substring(index + 1));
            }
        }
        return res;
    }


    public GameManager(String[] args) {
        super();
        LOGGER.info("----------------------------------------");
        LOGGER.info("----------------------------------------");
        LOGGER.info("----------------------------------------");
        LOGGER.info("New game start at time : {}", new Date().toString());
        this.setArgsMap(generateArgsMap(args));

        LOGGER.info("----------------------------------------");
        LOGGER.info("Args : ->");
        for (Map.Entry entry : this.getArgsMap().entrySet()) {
            LOGGER.info("    {} : {}", entry.getKey(), entry.getValue());
        }
        LOGGER.info("----------------------------------------");

        LOGGER.info("Platform : ->");
        Properties properties = System.getProperties();
        for (Map.Entry entry : properties.entrySet()) {
            LOGGER.info("    {} : {}", entry.getKey(), entry.getValue());
        }
        LOGGER.info("----------------------------------------");
        LOGGER.info("cyan_potion engine version : {}", Version.VERSION);
    }

    public GameManager() {
        this(new String[0]);
    }


    public GameManager(Map<String, String> argsMap) {
        this();
        this.setArgsMap(argsMap);
    }

    public void eventListAdd(Event event) {
        if (event == null) {
            return;
        }
        synchronized (getEventList()) {
            getEventList().add(event);
        }
    }

    private void initConsoleThread() {
        if (getBoolean(this.getDataCenter().getSpecialSettings(),
                STRING_NO_CONSOLE_THREAD)) {
            setConsoleThread(null);
        }
        if (getConsoleThread() != null) {
            getConsoleThread().start();
        }
    }

    public void startup() {
        this.codePluginManager.apply(this, rightBeforeGameManagerStartup);
        this.loadSettingFile();
        this.readCommonSettings();
        this.loadKeymap();
        this.initSteam();
        this.loadText();

        setAlive(true);
        this.initConsoleThread();

        this.codePluginManager.apply(this, rightBeforeResourceManagerCreate);
        resourceManager = new ResourceManager(this);
        this.codePluginManager.apply(this, rightAfterResourceManagerCreate);

        this.codePluginManager.apply(this, rightBeforeGameWindowInit);
        this.initGameWindow();
        this.codePluginManager.apply(this, rightAfterGameWindowInit);

        this.codePluginManager.apply(this, rightBeforeAudioManagerInit);
        this.getAudioManager().init();
        this.codePluginManager.apply(this, rightAfterAudioManagerInit);


        this.setGamepadInput(new GamepadInput());
        this.setStartingContent();
        String defaultFontFilePath =
                getString(this.getDataCenter().getCommonSettings(),
                        STRING_DEFAULT_FONT_FILE_PATH,
                        Font.DEFAULT_DEFAULT_FONT_FILE_PATH);

        final String tmpDefaultFontFilePath = defaultFontFilePath;

        this.getExecutorService().execute(() -> {
            Font font = new Font(tmpDefaultFontFilePath);
            font.loadBitmap();
            Font.setDefaultFont(font);
            Font.setCurrentFont(Font.getDefaultFont());
        });

        this.loop();
    }


    protected void loadSettingFile() {
        String settingFilePath = this.getArgsMap().get("SettingFilePath");
        if (settingFilePath == null || settingFilePath.isEmpty()) {
            settingFilePath = "/settings/DefaultSettings.x8l";
        }
        File globalSettingsFile = FileUtil.getFile(settingFilePath);

        LOGGER.debug("SettingsFilePath : {}",
                globalSettingsFile.getAbsolutePath());
        try {
            this.getDataCenter().setGlobalSettingsTree(X8lTree.loadFromFile(globalSettingsFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (ContentNode contentNode :
                this.getDataCenter().getGlobalSettingsTree().root.getContentNodesFromChildrenThatNameIs(
                        "commonSettings")) {
            this.getDataCenter().getCommonSettings().putAll(contentNode.attributes);
        }
        for (ContentNode contentNode :
                this.getDataCenter().getGlobalSettingsTree().root.getContentNodesFromChildrenThatNameIs(
                        "specialSettings")) {
            this.getDataCenter().getSpecialSettings().putAll(contentNode.attributes);
        }
        for (ContentNode contentNode :
                this.getDataCenter().getGlobalSettingsTree().root.getContentNodesFromChildrenThatNameIs(
                        "views")) {
            this.getDataCenter().getViews().putAll(contentNode.attributes);
        }
        for (ContentNode pluginNode :
                this.getDataCenter().getGlobalSettingsTree().root.getContentNodesFromChildrenThatNameIs("codePlugins"
                )) {
            for (ContentNode simplePluginNode : pluginNode.getContentNodesFromChildren()) {
                this.codePluginManager.putCodePlugin(CodePluginPosition.valueOf(simplePluginNode.getName()),
                        simplePluginNode.getTextNodesFromChildren(1).get(0).textContent);
            }
        }
    }

    protected void readCommonSettings() {
        this.getDataCenter().setTitleTextID(getString(this.getDataCenter().getCommonSettings(),
                STRING_TITLE_TEXT_ID, ""));
        this.getDataCenter().setTextFilePath(getString(this.getDataCenter().getCommonSettings(),
                STRING_TEXT_FILE_PATH, "/text/text.x8l"));
        this.getDataCenter().setIconFilePath(getString(this.getDataCenter().getCommonSettings(),
                STRING_ICON_FILE_PATH, "/www/icon/icon.png"));
    }


    protected void loadKeymap() {
        this.setKeymap(new Keymap());
        for (AbstractTreeNode au :
                this.getDataCenter().getGlobalSettingsTree().root.children) {
            if (!(au instanceof ContentNode)) {
                continue;
            }
            ContentNode contentNode = (ContentNode) au;
            if (contentNode.attributes.isEmpty()) {
                continue;
            }
            if ("keymap".equals(contentNode.getName()) && contentNode.attributes.containsKey("using")) {
                for (AbstractTreeNode au2 : contentNode.children) {
                    if (au2 instanceof TextNode) {
                        continue;
                    }
                    ContentNode contentNode2 = (ContentNode) au2;
                    if (contentNode2.attributes.isEmpty() || contentNode2.children.isEmpty()) {
                        continue;
                    }
                    String rawInput = contentNode2.getName();
                    String myInput = null;
                    for (AbstractTreeNode au3 : contentNode2.children) {
                        if (!(au3 instanceof TextNode)) {
                            continue;
                        }
                        myInput = ((TextNode) au3).textContent;
                        break;
                    }
                    this.getKeymap().put(rawInput, myInput);
                }
                break;
            }
            if ("debug".equals(contentNode.getName()) && !"0".equals(contentNode.attributes.get("debug"))) {
                DataCenter.DEBUG = true;
                Configuration.DEBUG.set(true);
                Configuration.DEBUG_LOADER.set(true);
                GameWindow.openDebug();
            }
        }
    }

    protected void loadText() {
        MultiLanguageX8lFileUtil multiLanguageUtil =
                new MultiLanguageX8lFileUtil();
        try {
            multiLanguageUtil.loadFromMerge(FileUtil.getFile(this.getDataCenter().getTextFilePath()));
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("load text from this.getDataCenter().textFilePath " +
                    "fails!!");
            System.exit(1);
        }
        this.getDataCenter().setTextStructure(multiLanguageUtil.parse());
        String language = MultiLanguageStructure.ENGLISH;
        getString(this.getDataCenter().getCommonSettings(), STRING_LANGUAGE, MultiLanguageStructure.ENGLISH);
        if (DataCenter.RUN_WITH_STEAM) {
            language = new SteamApps().getCurrentGameLanguage();
        }
        if (!this.getDataCenter().getTextStructure().setCurrentLanguage(language)) {
            throw new Error("Lack language " + language + ".Please change the" +
                    " [language] in settings.");
        }
    }


    protected void initSteam() {
        DataCenter.RUN_WITH_STEAM = getBoolean(this.getDataCenter().getCommonSettings(), "runWithSteam", true);
        if (DataCenter.RUN_WITH_STEAM) {
            try {
                SteamAPI.loadLibraries();
                if (!SteamAPI.init()) {
                    throw new SteamException("Steamworks initialization error");
                }
                this.setSteamUserStats(new SteamUserStats(this.getCallbacks().steamUserStatsCallback));
                DataCenter.RUN_WITH_STEAM = true;
            } catch (SteamException e) {
                // Error extracting or loading native libraries
                DataCenter.RUN_WITH_STEAM = false;
                if (DataCenter.ALLOW_RUN_WITHOUT_STEAM) {
                    e.printStackTrace();
                    LOGGER.warn("Steam load failed but somehow we cannot prevent " +
                            "you from playing it.");
                } else {
                    LOGGER.error("Steam load failed, thus the game shut.");
                    throw new Error(e);
                }
            }
        } else {
            if (DataCenter.ALLOW_RUN_WITHOUT_STEAM) {
                LOGGER.warn("Steam load failed but somehow we cannot prevent " +
                        "you from playing it.");
            } else {
                LOGGER.warn("Steam load failed, thus the game shut.");
                throw new RuntimeException("Steam load failed, thus the game shut.");
            }
        }
    }


    public void shutdown() {
        setAlive(false);
    }

    @Override
    public void close() {
        this.getGameWindowComponentTree().close();
        this.getResourceManager().close();
        if (Font.getDefaultFont() != null) {
            Font.getDefaultFont().close();
        }

        this.getGameWindow().close();
        //        super.close();
        this.getAudioManager().close();

        setAlive(false);
//        DataCenter.getGameManagers().remove(this);

        if (getConsoleThread() != null) {
            getConsoleThread().shutdown();
        }

        if (DataCenter.RUN_WITH_STEAM) {
            this.getSteamUserStats().dispose();
            SteamAPI.shutdown();
        }
        this.getExecutorService().shutdown();
    }

    protected void initGameWindow() {
        if (this.getGameWindow() == null) {
            String gameWindowClassName = getString(this.getDataCenter().getCommonSettings(),
                    STRING_GAME_WINDOW_CLASS_NAME, "com.xenoamess.cyan_potion.base.GameWindow");
            try {
                this.setGameWindow((GameWindow) this.getClass().getClassLoader().loadClass(gameWindowClassName).getConstructor(this.getClass()).newInstance(this));
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
        getEventList().clear();

//        String tmpWindowWidthString = ;
//        String tmpWindowHeightString = ;

        this.getGameWindow().setLogicWindowWidth(Integer.parseInt(getString(this.getDataCenter().getViews(),
                STRING_LOGIC_WINDOW_WIDTH, "1280")));
        this.getGameWindow().setLogicWindowHeight(Integer.parseInt(getString(this.getDataCenter().getViews(),
                STRING_LOGIC_WINDOW_HEIGHT, "1024")));
        this.getGameWindow().setRealWindowWidth(this.getGameWindow().getLogicWindowWidth());
        this.getGameWindow().setRealWindowHeight(this.getGameWindow().getLogicWindowHeight());

        {
            String tmpString;
            if ((tmpString = getString(this.getDataCenter().getViews(), STRING_REAL_WINDOW_WIDTH)) != null) {
                getGameWindow().setRealWindowWidth(Integer.parseInt(tmpString));
                getGameWindow().setRealWindowHeight((int) (1f * getGameWindow().getRealWindowWidth() / getGameWindow().getLogicWindowWidth() * getGameWindow().getRealWindowHeight()));
            }
            if ((tmpString = getString(this.getDataCenter().getViews(), STRING_REAL_WINDOW_HEIGHT)) != null) {
                getGameWindow().setRealWindowHeight(Integer.parseInt(tmpString));
                getGameWindow().setRealWindowWidth((int) (1f * getGameWindow().getRealWindowHeight() / getGameWindow().getLogicWindowHeight() * getGameWindow().getLogicWindowWidth()));
            }
            if (getGameWindow().getRealWindowHeight() > Toolkit.getDefaultToolkit().getScreenSize().getHeight()) {
                getGameWindow().setRealWindowHeight((int) Toolkit.getDefaultToolkit().getScreenSize().getHeight());
                getGameWindow().setRealWindowWidth((int) (1f * getGameWindow().getRealWindowHeight() / getGameWindow().getLogicWindowHeight() * getGameWindow().getLogicWindowWidth()));
            }
            if (getGameWindow().getRealWindowWidth() > Toolkit.getDefaultToolkit().getScreenSize().getWidth()) {
                getGameWindow().setRealWindowWidth((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth());
                getGameWindow().setRealWindowHeight((int) (1f * getGameWindow().getRealWindowWidth() / getGameWindow().getLogicWindowWidth() * getGameWindow().getRealWindowHeight()));
            }
        }

        this.getGameWindow().setFullScreen(getBoolean(this.getDataCenter().getViews(), STRING_FULL_SCREEN));
        this.setGameWindowComponentTree(new GameWindowComponentTree(this.getGameWindow()));
        this.getGameWindow().init();


        if (getBoolean(this.getDataCenter().getSpecialSettings(),
                STRING_AUTO_SHOW_GAME_WINDOW_AFTER_INIT, true)) {
            this.getGameWindow().showWindow();
            this.getGameWindow().focusWindow();
        }
    }

    protected void setStartingContent() {
        final AbstractGameWindowComponent logo =
                AbstractGameWindowComponent.createGameWindowComponentFromClassName(this.getGameWindow(),
                        getString(this.dataCenter.getCommonSettings(), STRING_LOGO_CLASS_NAME,
                                "com.xenoamess.cyan_potion.base.gameWindowComponents.Logo"));
        logo.addToGameWindowComponentTree(null);
    }

    protected void loop() {
        double timeForFPS = 0;
        int drawFramesForFPS = 0;

        long time = System.currentTimeMillis();
        double unprocessed = 0;
//        int timerForSteamCallback = 0;

        while (getAlive()) {
            boolean canRender = false;

            long time2 = System.currentTimeMillis();
            double passed = (time2 - time) / 1000.0;
            time = time2;

            unprocessed += passed;
            timeForFPS += passed;


            while (unprocessed >= DataCenter.FRAME_CAP) {
                this.codePluginManager.apply(this, rightBeforeLogicFrame);
                //                if (getGameWindow().hasResized()) {
                //                    //                    camera
                //                    .setProjection(window.getWidth(),
                //                    window.getHeight());
                //                    //                    gui.resizeCamera
                //                    (window);
                //                    //                    world
                //                    .calculateView(window);
                //                    //                    glViewport(0, 0,
                //                    window.getWidth(), window.getHeight());
                //                }

                unprocessed -= DataCenter.FRAME_CAP;
                canRender = true;

                this.codePluginManager.apply(this, rightBeforeSolveEvents);
                solveEvents();
                this.codePluginManager.apply(this, rightAfterSolveEvents);

                this.codePluginManager.apply(this, rightBeforeUpdate);
                update();
                this.codePluginManager.apply(this, rightAfterUpdate);

                steamRunCallbacks();

                setNowFrameIndex(getNowFrameIndex() + 1);
                this.getResourceManager().suggestGc();

                this.codePluginManager.apply(this, rightAfterLogicFrame);
            }

            if (canRender) {
                draw();

                {//draw frame FPS calculate.
                    drawFramesForFPS++;
                    if (timeForFPS >= 10) {
                        LOGGER.info("FPS : {}", drawFramesForFPS / timeForFPS);
                        timeForFPS = 0;
                        drawFramesForFPS = 0;
                    }
                }

            }
        }

        this.close();
    }

    protected void solveEvents() {
        getGameWindow().pollEvents();
        synchronized (getEventList()) {
            ArrayList<Event> newEventList = new ArrayList<Event>();
            for (Event au : getEventList()) {
                Set<Event> res = au.apply(this);
                if (res != null) {
                    newEventList.addAll(res);
                }
            }
            getEventList().clear();
            getEventList().addAll(newEventList);
        }
    }

    protected void update() {
        this.getGamepadInput().update(this.getGameWindow());
        getGameWindow().update();
        this.getGameWindowComponentTree().update();
    }

    protected void draw() {
        getGameWindow().draw();
    }

    protected void steamRunCallbacks() {
        if (DataCenter.RUN_WITH_STEAM && SteamAPI.isSteamRunning()) {
            SteamAPI.runCallbacks();
        } else {
            if (!DataCenter.ALLOW_RUN_WITHOUT_STEAM) {
                this.shutdown();
            }
        }
    }


    /*
     * Getters and Setters
     */

    public boolean getAlive() {
        return alive.get();
    }

    public void setAlive(boolean newAlive) {
        this.alive.set(newAlive);
    }

    public ConsoleThread getConsoleThread() {
        return consoleThread;
    }

    public GameWindow getGameWindow() {
        return gameWindow;
    }

    public Callbacks getCallbacks() {
        return callbacks;
    }

    public SteamUserStats getSteamUserStats() {
        return steamUserStats;
    }

    public DataCenter getDataCenter() {
        return dataCenter;
    }

    public Keymap getKeymap() {
        return keymap;
    }

    public GamepadInput getGamepadInput() {
        return gamepadInput;
    }

    public GameWindowComponentTree getGameWindowComponentTree() {
        return gameWindowComponentTree;
    }

    public long getNowFrameIndex() {
        return nowFrameIndex;
    }

    public Map<String, String> getArgsMap() {
        return argsMap;
    }

    public AudioManager getAudioManager() {
        return audioManager;
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public ConcurrentLinkedDeque<Event> getEventList() {
        return eventList;
    }

    public void setConsoleThread(ConsoleThread consoleThread) {
        this.consoleThread = consoleThread;
    }

    public void setGameWindow(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
    }

    public void setSteamUserStats(SteamUserStats steamUserStats) {
        this.steamUserStats = steamUserStats;
    }

    public void setKeymap(Keymap keymap) {
        this.keymap = keymap;
    }

    public void setGamepadInput(GamepadInput gamepadInput) {
        this.gamepadInput = gamepadInput;
    }

    public void setGameWindowComponentTree(GameWindowComponentTree gameWindowComponentTree) {
        this.gameWindowComponentTree = gameWindowComponentTree;
    }

    public void setNowFrameIndex(long nowFrameIndex) {
        this.nowFrameIndex = nowFrameIndex;
    }

    public void setArgsMap(Map<String, String> argsMap) {
        this.argsMap = argsMap;
        LOGGER.info("----------------------------------------");
        LOGGER.info("Args : ->");
        for (Map.Entry entry : this.getArgsMap().entrySet()) {
            LOGGER.info("    {} : {}", entry.getKey(), entry.getValue());
        }
        LOGGER.info("----------------------------------------");
    }
}
