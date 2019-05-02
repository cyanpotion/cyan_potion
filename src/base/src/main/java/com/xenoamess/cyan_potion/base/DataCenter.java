package com.xenoamess.cyan_potion.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xenoamess.cyan_potion.base.gameWindowComponents.AbstractGameWindowComponent;
import com.xenoamess.multi_language.MultiLanguageStructure;
import com.xenoamess.x8l.X8lTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author XenoAmess
 */
public class DataCenter {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(DataCenter.class);

    public static boolean DEBUG = false;
    public static boolean ALLOW_RUN_WITHOUT_STEAM = true;
    public static boolean RUN_WITH_STEAM = true;

    public static final int CONSOLE_PORT = 13888;
    public static final double FRAME_CAP = 1 / 60.0;
    public static final float FRAME_CAP_F = (float) FRAME_CAP;
    public static final int SCALE = 2;

//    public String openglVersion = "3.2";

//    private static final ArrayList<GameManager> GAME_MANAGERS = new
//    ArrayList<>();

    private X8lTree globalSettingsTree;
    private final Map<String, String> commonSettings = new HashMap<>();
    private final Map<String, String> specialSettings = new HashMap<>();
    private final Map<String, String> views = new HashMap<>();


    private String textFilePath = null;
    private String iconFilePath = null;

    private MultiLanguageStructure textStructure;

    private GameManager gameManager;
    private String titleTextID;

    public DataCenter(GameManager gameManager) {
        this.setGameManager(gameManager);
    }

    public static void setObjectMapper(ObjectMapper objectMapper) {
        DataCenter.objectMapper = objectMapper;
    }

    public AbstractGameWindowComponent fetchGameWindowComponentFromCommonSetting(GameWindow gameWindow, String
            classNameKey, String backupClassName) {
        AbstractGameWindowComponent gameWindowComponent = null;
        String gameWindowComponentClassName = backupClassName;
        if (this.getCommonSettings().containsKey(classNameKey)) {
            gameWindowComponentClassName =
                    this.getCommonSettings().get(classNameKey);
        }
        try {
            gameWindowComponent =
                    (AbstractGameWindowComponent) this.getClass().getClassLoader().loadClass(gameWindowComponentClassName).getConstructor(GameWindow.class).newInstance(gameWindow);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return gameWindowComponent;
    }

    private static ObjectMapper objectMapper = null;

    public static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        return objectMapper;
    }


    public static boolean isWindows() {
        return System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1;
    }


    private static final Map<Long, GameWindow> GAME_WINDOW_MAP =
            new HashMap<Long, GameWindow>();

    public static GameWindow getGameWindow(long window) {
        return GAME_WINDOW_MAP.get(window);
    }

    public static void putGameWindow(long window, GameWindow gameWindow) {
        GAME_WINDOW_MAP.put(window, gameWindow);
    }

    public static void removeGameWindow(long window) {
        GAME_WINDOW_MAP.remove(window);
    }


//    public static ArrayList<GameManager> getGameManagers() {
//        return GAME_MANAGERS;
//    }

    public X8lTree getGlobalSettingsTree() {
        return globalSettingsTree;
    }

    public void setGlobalSettingsTree(X8lTree globalSettingsTree) {
        this.globalSettingsTree = globalSettingsTree;
    }

    public Map<String, String> getCommonSettings() {
        return commonSettings;
    }


    public Map<String, String> getSpecialSettings() {
        return specialSettings;
    }

    public Map<String, String> getViews() {
        return views;
    }

    public String getTextFilePath() {
        return textFilePath;
    }

    public void setTextFilePath(String textFilePath) {
        this.textFilePath = textFilePath;
    }

    public String getIconFilePath() {
        return iconFilePath;
    }

    public void setIconFilePath(String iconFilePath) {
        this.iconFilePath = iconFilePath;
    }

    public MultiLanguageStructure getTextStructure() {
        return textStructure;
    }

    public void setTextStructure(MultiLanguageStructure textStructure) {
        this.textStructure = textStructure;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public String getTitleTextID() {
        return titleTextID;
    }

    public void setTitleTextID(String titleTextID) {
        this.titleTextID = titleTextID;
    }

}
