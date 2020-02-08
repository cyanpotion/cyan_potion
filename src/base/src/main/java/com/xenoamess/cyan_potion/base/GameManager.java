/*
 * MIT License
 *
 * Copyright (c) 2020 XenoAmess
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

import com.codedisaster.steamworks.SteamApps;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xenoamess.commons.as_final_field.AsFinalField;
import com.xenoamess.commons.java.net.URLStreamHandlerFactorySet;
import com.xenoamess.cyan_potion.base.audio.AudioManager;
import com.xenoamess.cyan_potion.base.console.ConsoleThread;
import com.xenoamess.cyan_potion.base.events.Event;
import com.xenoamess.cyan_potion.base.events.MainThreadEvent;
import com.xenoamess.cyan_potion.base.game_window_components.AbstractGameWindowComponent;
import com.xenoamess.cyan_potion.base.game_window_components.GameWindowComponentTree;
import com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.EventProcessor;
import com.xenoamess.cyan_potion.base.io.input.gamepad.GamepadInputManager;
import com.xenoamess.cyan_potion.base.io.input.key.Keymap;
import com.xenoamess.cyan_potion.base.io.input.keyboard.CharEvent;
import com.xenoamess.cyan_potion.base.io.input.keyboard.TextEvent;
import com.xenoamess.cyan_potion.base.io.url.CyanPotionURLStreamHandlerFactory;
import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import com.xenoamess.cyan_potion.base.plugins.CodePluginManager;
import com.xenoamess.cyan_potion.base.plugins.CodePluginPosition;
import com.xenoamess.cyan_potion.base.runtime.RuntimeManager;
import com.xenoamess.cyan_potion.base.runtime.SaveManager;
import com.xenoamess.cyan_potion.base.setting_file.SettingFileParsers;
import com.xenoamess.cyan_potion.base.steam.SteamManager;
import com.xenoamess.cyan_potion.base.visual.Font;
import com.xenoamess.multi_language.MultiLanguageStructure;
import com.xenoamess.multi_language.MultiLanguageX8lFileUtil;
import com.xenoamess.x8l.ContentNode;
import com.xenoamess.x8l.X8lTree;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.vfs2.FileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.xenoamess.cyan_potion.base.GameManagerConfig.getString;
import static com.xenoamess.cyan_potion.base.plugins.CodePluginPosition.*;

/**
 * <p>GameManager class.</p>
 *
 * @author XenoAmess
 * @version 0.155.3-SNAPSHOT
 */
public class GameManager implements Closeable {
    @JsonIgnore
    private static transient final Logger LOGGER =
            LoggerFactory.getLogger(GameManager.class);

    /**
     * Constant <code>LINE_SEGMENT="---------------------------------------"{trunked}</code>
     */
    public static final String LINE_SEGMENT = "----------------------------------------";

    private final AtomicBoolean alive = new AtomicBoolean(false);

    private final List<Event> eventList = new ArrayList<>();
    private final List<Event> eventListCache = new ArrayList<>();
    private final AtomicBoolean ifSolvingEventList = new AtomicBoolean();

    @AsFinalField
    private ConsoleThread consoleThread;

    @AsFinalField
    private GameWindow gameWindow;
    private final Callbacks callbacks = new Callbacks(this);
    private final DataCenter dataCenter = new DataCenter(this);
    private final CodePluginManager codePluginManager = new CodePluginManager();

    private final Keymap keymap = new Keymap();
    private final GamepadInputManager gamepadInputManager = new GamepadInputManager(this);
    private final GameWindowComponentTree gameWindowComponentTree = new GameWindowComponentTree(this);
    private long nowFrameIndex = 0L;
    private Map<String, String> argsMap;

    private final AudioManager audioManager = new AudioManager(this);
    private final ResourceManager resourceManager = new ResourceManager(this);
    private final RuntimeManager runtimeManager = new RuntimeManager(this);
    private final SaveManager saveManager = new SaveManager(this);
    private final SteamManager steamManager = new SteamManager(this);

    private final ScheduledExecutorService scheduledExecutorService =
            Executors.newScheduledThreadPool(10);

    private float timeToLastUpdate = 0;

    private boolean canRender = false;

    /**
     * <p>generateArgsMap.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     * @return return
     */
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

    /**
     * <p>generateArgsArray.</p>
     *
     * @param argsMap argsMap.
     * @return return
     */
    public static String[] generateArgsArray(Map<String, String> argsMap) {
        ArrayList<String> res = new ArrayList<>();
        if (argsMap == null) {
            return res.toArray(new String[0]);
        }
        for (Map.Entry<String, String> au : argsMap.entrySet()) {
            res.add(au.getKey() + '=' + au.getValue());
        }
        return res.toArray(new String[0]);
    }


    /**
     * <p>Constructor for GameManager.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public GameManager(String[] args) {
        this(generateArgsMap(args));
    }

    /**
     * <p>Constructor for GameManager.</p>
     */
    public GameManager() {
        this(new String[0]);
    }


    /**
     * <p>Constructor for GameManager.</p>
     *
     * @param argsMap argsMap
     */
    public GameManager(Map<String, String> argsMap) {
        super();
        LOGGER.info(LINE_SEGMENT);
        LOGGER.info(LINE_SEGMENT);
        LOGGER.info(LINE_SEGMENT);
        LOGGER.info("New game start at time : {}", new Date());
        this.setArgsMap(argsMap);

        LOGGER.info(LINE_SEGMENT);
        LOGGER.info("Args : ->");
        for (Map.Entry entry : this.getArgsMap().entrySet()) {
            LOGGER.info("    {} : {}", entry.getKey(), entry.getValue());
        }
        LOGGER.info(LINE_SEGMENT);

        LOGGER.info("Platform : ->");
        Properties properties = System.getProperties();
        for (Map.Entry entry : properties.entrySet()) {
            LOGGER.info("    {} : {}", entry.getKey(), entry.getValue());
        }
        LOGGER.info(LINE_SEGMENT);
        LOGGER.info("cyan_potion engine version : {}", PackageVersion.VERSION);
    }

    /**
     * <p>eventListAdd.</p>
     *
     * @param event event
     */
    public void eventListAdd(Event event) {
        if (event == null) {
            return;
        }
        if (!ifSolvingEventList.get()) {
            List<Event> localEventList = getEventList();
            synchronized (localEventList) {
                localEventList.add(event);
            }
        } else {
            List<Event> localEventListCache = getEventListCache();
            synchronized (localEventListCache) {
                localEventListCache.add(event);
            }
        }
    }

    private void initConsoleThread() {

        if (this.getDataCenter().getGameSettings().isNoConsoleThread()) {
            this.consoleThread = null;
        } else {
            this.consoleThread = new ConsoleThread(this);
        }
        if (getConsoleThread() != null) {
            getConsoleThread().start();
        }
    }

    /**
     * <p>registerCyanPotionURLStreamHandlerFactory.</p>
     */
    public void registerCyanPotionURLStreamHandlerFactory() {
        try {
            URLStreamHandlerFactorySet factorySet = URLStreamHandlerFactorySet.wrapURLStreamHandlerFactory();
            factorySet.register(new CyanPotionURLStreamHandlerFactory());
        } catch (IllegalAccessException e) {
            LOGGER.error("URLStreamHandlerFactorySet wrapURLStreamHandlerFactory failed.", e);
        }
    }

    /**
     * <p>startup.</p>
     */
    public void startup() {
        if (this.getAlive()) {
            return;
        }
        setAlive(true);

        this.registerCyanPotionURLStreamHandlerFactory();

        this.initGameSettings();

        this.initKeyMap();

        this.initCodePluginManager();

        this.getSteamManager().init();

        this.loadText();

        this.initConsoleThread();

        this.codePluginManager.apply(this, rightBeforeResourceManagerCreate);
        this.getResourceManager().init();
        this.codePluginManager.apply(this, rightAfterResourceManagerCreate);

        this.codePluginManager.apply(this, rightBeforeGameWindowInit);
        this.initGameWindow();
        this.codePluginManager.apply(this, rightAfterGameWindowInit);

        this.codePluginManager.apply(this, rightBeforeAudioManagerInit);
        this.getAudioManager().init();
        this.codePluginManager.apply(this, rightAfterAudioManagerInit);

        this.codePluginManager.apply(this, rightBeforeGamepadInputManagerInit);
        this.getGamepadInputManager().init();
        this.codePluginManager.apply(this, rightAfterGamepadInputManagerInit);

        this.getSaveManager().init();

        this.getGameWindowComponentTree().init();

        this.setStartingContent();
        final String defaultFontResourceJsonString = this.getDataCenter().getGameSettings().getDefaultFontResourceJsonString();

        if (!StringUtils.isBlank(defaultFontResourceJsonString)) {
            Font.setDefaultFont(this.getResourceManager().fetchResource(Font.class, defaultFontResourceJsonString));
            Font.getDefaultFont().startLoad();
            Font.setCurrentFont(Font.getDefaultFont());
        }

        this.loop();
    }

    /**
     * <p>loadSettingTree.</p>
     *
     * @param dataCenter a {@link com.xenoamess.cyan_potion.base.DataCenter} object.
     * @return a {@link com.xenoamess.x8l.X8lTree} object.
     */
    protected X8lTree loadSettingTree(DataCenter dataCenter) {
        String settingFilePath = getString(this.getArgsMap(), "SettingFilePath", "resources/settings/DefaultSettings.x8l");

        LOGGER.debug("SettingsFilePath : {}", settingFilePath);

        X8lTree settingsTree = null;
        FileObject settingFileObject = ResourceManager.resolveFile(settingFilePath);
        try {
            settingsTree = X8lTree.load(settingFileObject);
            settingsTree.trimForce();
            ContentNode settingNode = settingsTree.getRoot().getContentNodesFromChildrenThatNameIs("settingFile").get(0);
            if (dataCenter.getPatchSettingsTree() != null) {
                settingNode.append(dataCenter.getPatchSettingsTree().getRoot().getChildren().get(0));
            }
        } catch (Exception e) {
            LOGGER.error("X8lTree.loadString(settingFileObject) fails, settingFileObject : {}", settingFileObject, e);
        }
        return settingsTree;
    }

    /**
     * <p>initGameSettings.</p>
     */
    protected void initGameSettings() {
        X8lTree settingsTree = this.loadSettingTree(this.getDataCenter());
        this.getDataCenter().setGameSettings(
                SettingFileParsers.parse(settingsTree)
        );
    }

    /**
     * <p>initKeyMap.</p>
     */
    protected void initKeyMap() {
        for (Pair<String, String> entry : this.getDataCenter().getGameSettings().getKeymapSettings()) {
            this.getKeymap().put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * <p>initCodePluginManager.</p>
     */
    protected void initCodePluginManager() {
        for (Pair<CodePluginPosition, String> entry : this.getDataCenter().getGameSettings().getCodePluginManagerSettings()) {
            this.codePluginManager.putCodePlugin(entry.getKey(), entry.getValue());
        }
    }


    /**
     * get current language from steam api
     * then load text structure from text file.
     * then set text structure's current language as current language from steam api.
     * all languages are String typed.
     */
    //todo
    protected void loadText() {
        MultiLanguageX8lFileUtil multiLanguageUtil = new MultiLanguageX8lFileUtil();
        try {
            multiLanguageUtil.loadFromMerge(
                    ResourceManager.resolveFile(
                            getDataCenter().getGameSettings().getTextFilePath()
                    )
            );
        } catch (IOException e) {
            LOGGER.error("multiLanguageUtil.loadFromMerge(AbstractResource.getFile(this.getDataCenter()" +
                    ".getTextFilePath())) " +
                    "fails", e);
            System.exit(1);
        }

        MultiLanguageStructure multiLanguageStructure = multiLanguageUtil.parse();

        String settingLanguage = this.getDataCenter().getGameSettings().getSettingLanguage();

        String steamLanguage = "";
        if (this.getSteamManager().isRunWithSteam()) {
            steamLanguage = new SteamApps().getCurrentGameLanguage();
        }

        if (!multiLanguageStructure.setCurrentLanguage(steamLanguage)) {
            if (!multiLanguageStructure.setCurrentLanguage(settingLanguage)) {
                LOGGER.error("Lack language : {} . Please change the [language] in settings.", settingLanguage);
                LOGGER.error("We will have to use english instead here.");
                multiLanguageStructure.setCurrentLanguage(MultiLanguageStructure.ENGLISH);
            }
        }

        this.getDataCenter().setTextStructure(multiLanguageStructure);
    }


    /**
     * shutdown.
     */
    public void shutdown() {
        setAlive(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        this.getGameWindowComponentTree().close();
        this.getResourceManager().close();
        if (Font.getDefaultFont() != null) {
            Font.getDefaultFont().close();
        }

        this.getGameWindow().close();
        this.getAudioManager().close();
        this.getGamepadInputManager().close();
        this.getSteamManager().close();

        setAlive(false);

        if (getConsoleThread() != null) {
            getConsoleThread().shutdown();
        }


        this.getScheduledExecutorService().shutdown();
    }

    /**
     * first fetch the window class name from the getCommonSettings
     * then this.setGameWindow(gameWindow);
     * then set the real width and height of this window.
     * if the width/height is larger than the screen, then it become to width/height of the screen, and the other is
     * scaled.
     * for example, if scree's width be 1024, height be 800, and real width/height in the config file is 2048/800,
     * then the real width will be 1024/400.
     * I think this shall be a precise example.
     * If you find things not work this way you can just put a bug issue.
     */
    protected void initGameWindow() {
        if (this.getGameWindow() == null) {
            String gameWindowClassName = this.getDataCenter().getGameSettings().getGameWindowClassName();
            try {
                GameWindow localGameWindow =
                        (GameWindow) this.getClass().getClassLoader().loadClass(gameWindowClassName).getConstructor(this.getClass()).newInstance(this);
                this.gameWindow = localGameWindow;
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                LOGGER.error("GameManager.initGameWindow() fails", e);
                System.exit(-1);
            }
        }

        getEventList().clear();

        this.getGameWindow().setLogicWindowWidth(this.getDataCenter().getGameSettings().getLogicWindowWidth());
        this.getGameWindow().setLogicWindowHeight(this.getDataCenter().getGameSettings().getLogicWindowHeight());
        this.getGameWindow().setRealWindowWidth(this.getGameWindow().getLogicWindowWidth());
        this.getGameWindow().setRealWindowHeight(this.getGameWindow().getLogicWindowHeight());

        this.getGameWindow().setFullScreen(this.getDataCenter().getGameSettings().isFullScreen());
        this.getGameWindowComponentTree().init(gameWindow);
        this.getGameWindow().init();

        if (this.getDataCenter().getGameSettings().isAutoShowGameWindowAfterInit()) {
            this.getGameWindow().showWindow();
            this.getGameWindow().focusWindow();
        }
    }

    /**
     * load logoClassName from config.
     * then load the logo and add it into the componentTree.
     * this shall be only called once(when starting the game).
     * this is used like a "made with CyanPotion" or something.(sounds like that one in Unity).
     * Noticed that unlike "made with Unity", this logo can be removed through overload the functions, and it is
     * legal to do so.
     * If you want to do that, you shall override this function and related functions in the logo class.
     */
    protected void setStartingContent() {
        final AbstractGameWindowComponent logo =
                AbstractGameWindowComponent.createGameWindowComponentFromClassName(
                        this.getGameWindow(),
                        this.getDataCenter().getGameSettings().getLogoClassName()
                );
        logo.addToGameWindowComponentTree(this.getGameWindowComponentTree().getRoot());
    }

    /**
     * one loop frame of the game engine's loop.
     */
    protected void loopOnce() {
        this.codePluginManager.apply(this, rightBeforeLogicFrame);
        this.codePluginManager.apply(this, rightBeforeSolveEvents);
        solveEvents();
        this.codePluginManager.apply(this, rightAfterSolveEvents);

        this.codePluginManager.apply(this, rightBeforeUpdate);
        update();
        this.codePluginManager.apply(this, rightAfterUpdate);

        setNowFrameIndex(getNowFrameIndex() + 1);
        this.getResourceManager().suggestGc();

        this.codePluginManager.apply(this, rightAfterLogicFrame);
    }

    /**
     * the main loop of the game engine.
     */
    protected void loop() {
        double timeForFPS = 0;
        int drawFramesForFPS = 0;

        long time = System.currentTimeMillis();
        double unprocessed = 0;

        while (getAlive()) {
            long time2 = System.currentTimeMillis();
            double passed = (time2 - time) / 1000.0;
            time = time2;

            unprocessed += passed;
            timeForFPS += passed;

            while (unprocessed >= DataCenter.FRAME_CAP) {
                this.setTimeToLastUpdate((float) DataCenter.FRAME_CAP);
                unprocessed -= DataCenter.FRAME_CAP;
                setCanRender(true);
                this.loopOnce();
            }

            this.setTimeToLastUpdate((float) unprocessed);
            unprocessed = 0;
            setCanRender(true);
            this.loopOnce();


            if (isCanRender()) {
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

    private final ConcurrentLinkedQueue<ImmutablePair<EventProcessor, Event>>
            mainThreadEventProcessPairs = new ConcurrentLinkedQueue<>();

    /**
     * Due to lwjgl's feature, some functions can only be invoked by main thread.
     * that is when this come.
     * when solving an event and finding it contains functions that shall only be called in main thread,
     * you can just invoke this function to delay it.
     * it will be dealt with main thread later in this loop frame.
     *
     * @param eventProcessor eventProcessor
     * @param event          a {@link com.xenoamess.cyan_potion.base.events.Event} object.
     * @param <T>            class of the event
     */
    public <T extends Event> void delayMainThreadEventProcess(EventProcessor<? super T> eventProcessor, T event) {
        mainThreadEventProcessPairs.add(new ImmutablePair<>(eventProcessor, event));
    }

    /**
     * <p>solveEvents.</p>
     */
    public void solveEvents() {
        ifSolvingEventList.set(true);

        getGameWindow().pollEvents();
        synchronized (this.getEventList()) {
            /*
             * notice that newEventList must be a thread safe collection.
             */
            final Collection<MainThreadEvent> mainThreadEvents = new ArrayList<>();
            final Collection<Event> newEventList = new ConcurrentLinkedQueue<>();
            final List<CharEvent> charEvents = new ArrayList<>();
            for (Event event : this.getEventList()) {
                if (event instanceof CharEvent) {
                    charEvents.add((CharEvent) event);
                }
            }
            if (!charEvents.isEmpty()) {
                charEvents.sort(Comparator.comparingLong(CharEvent::getId));
                this.getEventList().add(new TextEvent(this.getGameWindow().getWindow(), charEvents));
            }
            for (Event event : this.getEventList()) {
                if (event instanceof MainThreadEvent) {
                    mainThreadEvents.add((MainThreadEvent) event);
                }
            }


            this.getEventList().parallelStream().forEach(event -> {
                if (event instanceof MainThreadEvent) {
                    //do nothing
                } else {
                    Set<Event> res = event.apply(GameManager.this);
                    if (res != null) {
                        newEventList.addAll(res);
                    }
                }
            });

            for (Event event : mainThreadEvents) {
                Set<Event> res = event.apply(GameManager.this);
                if (res != null) {
                    newEventList.addAll(res);
                }
            }

            mainThreadEventProcessPairs.forEach((ImmutablePair<EventProcessor, Event> pair) -> {
                Event res = pair.left.apply(pair.right);
                if (res != null && res != pair.right) {
                    newEventList.add(res);
                }
            });
            mainThreadEventProcessPairs.clear();

            this.getEventList().clear();

            synchronized (this.getEventListCache()) {
                this.getEventList().addAll(this.getEventListCache());
                this.getEventListCache().clear();
            }

            this.getEventList().addAll(newEventList.stream().distinct().collect(Collectors.toList()));
        }

        ifSolvingEventList.set(false);
    }

    /**
     * <p>update.</p>
     */
    protected void update() {
        this.getGamepadInputManager().update();
        this.getGameWindow().update();
        this.getGameWindowComponentTree().update();
        this.getAudioManager().update();
        this.getSteamManager().update();
    }

    /**
     * <p>draw.</p>
     */
    protected void draw() {
        getGameWindow().draw();
    }


    /*
     * Getters and Setters
     */

    /**
     * <p>Getter for the field <code>alive</code>.</p>
     *
     * @return a boolean.
     */
    public boolean getAlive() {
        return alive.get();
    }

    /**
     * <p>Setter for the field <code>alive</code>.</p>
     *
     * @param newAlive a boolean.
     */
    public void setAlive(boolean newAlive) {
        this.alive.set(newAlive);
    }

    /**
     * <p>Getter for the field <code>consoleThread</code>.</p>
     *
     * @return return
     */
    public ConsoleThread getConsoleThread() {
        return consoleThread;
    }

    /**
     * <p>Getter for the field <code>gameWindow</code>.</p>
     *
     * @return return
     */
    public GameWindow getGameWindow() {
        return gameWindow;
    }

    /**
     * <p>Getter for the field <code>callbacks</code>.</p>
     *
     * @return return
     */
    public Callbacks getCallbacks() {
        return callbacks;
    }

    /**
     * <p>Getter for the field <code>dataCenter</code>.</p>
     *
     * @return return
     */
    public DataCenter getDataCenter() {
        return dataCenter;
    }

    /**
     * <p>Getter for the field <code>keymap</code>.</p>
     *
     * @return return
     */
    public Keymap getKeymap() {
        return keymap;
    }

    /**
     * <p>Getter for the field <code>gamepadInput</code>.</p>
     *
     * @return return
     */
    public GamepadInputManager getGamepadInputManager() {
        return gamepadInputManager;
    }

    /**
     * <p>Getter for the field <code>gameWindowComponentTree</code>.</p>
     *
     * @return return
     */
    public GameWindowComponentTree getGameWindowComponentTree() {
        return gameWindowComponentTree;
    }

    /**
     * <p>Getter for the field <code>nowFrameIndex</code>.</p>
     *
     * @return a long.
     */
    public long getNowFrameIndex() {
        return nowFrameIndex;
    }

    /**
     * <p>Getter for the field <code>argsMap</code>.</p>
     *
     * @return return
     */
    public Map<String, String> getArgsMap() {
        return argsMap;
    }

    /**
     * <p>Getter for the field <code>audioManager</code>.</p>
     *
     * @return return
     */
    public AudioManager getAudioManager() {
        return audioManager;
    }

    /**
     * <p>Getter for the field <code>resourceManager</code>.</p>
     *
     * @return return
     */
    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    /**
     * <p>Getter for the field <code>scheduledExecutorService</code>.</p>
     *
     * @return return
     */
    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    /**
     * <p>Getter for the field <code>eventList</code>.</p>
     *
     * @return return
     */
    protected List<Event> getEventList() {
        return eventList;
    }

    /**
     * <p>Getter for the field <code>eventListCache</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    protected List<Event> getEventListCache() {
        return eventListCache;
    }

    /**
     * <p>Setter for the field <code>nowFrameIndex</code>.</p>
     *
     * @param nowFrameIndex a long.
     */
    public void setNowFrameIndex(long nowFrameIndex) {
        this.nowFrameIndex = nowFrameIndex;
    }

    /**
     * <p>Setter for the field <code>argsMap</code>.</p>
     *
     * @param argsMap argsMap
     */
    public void setArgsMap(Map<String, String> argsMap) {
        this.argsMap = argsMap;
        LOGGER.info(LINE_SEGMENT);
        LOGGER.info("Args : ->");
        for (Map.Entry entry : this.getArgsMap().entrySet()) {
            LOGGER.info("    {} : {}", entry.getKey(), entry.getValue());
        }
        LOGGER.info(LINE_SEGMENT);
    }

    /**
     * <p>Getter for the field <code>timeToLastUpdate</code>.</p>
     *
     * @return a float.
     */
    public float getTimeToLastUpdate() {
        return timeToLastUpdate;
    }

    /**
     * <p>Setter for the field <code>timeToLastUpdate</code>.</p>
     *
     * @param timeToLastUpdate a float.
     */
    public void setTimeToLastUpdate(float timeToLastUpdate) {
        this.timeToLastUpdate = timeToLastUpdate;
    }

    /**
     * <p>isCanRender.</p>
     *
     * @return a boolean.
     */
    public boolean isCanRender() {
        return canRender;
    }

    /**
     * <p>Setter for the field <code>canRender</code>.</p>
     *
     * @param canRender a boolean.
     */
    public void setCanRender(boolean canRender) {
        this.canRender = canRender;
    }

    /**
     * <p>Getter for the field <code>runtimeManager</code>.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.runtime.RuntimeManager} object.
     */
    public RuntimeManager getRuntimeManager() {
        return runtimeManager;
    }

    /**
     * <p>Getter for the field <code>saveManager</code>.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.runtime.SaveManager} object.
     */
    public SaveManager getSaveManager() {
        return saveManager;
    }

    /**
     * <p>Getter for the field <code>steamManager</code>.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.steam.SteamManager} object.
     */
    public SteamManager getSteamManager() {
        return steamManager;
    }
}
