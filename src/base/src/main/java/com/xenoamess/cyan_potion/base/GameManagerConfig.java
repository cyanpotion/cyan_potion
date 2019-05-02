package com.xenoamess.cyan_potion.base;

import java.util.Map;

/**
 * @author XenoAmess
 */
public class GameManagerConfig {
    /**
     * Don't let anyone instantiate this class.
     */
    private GameManagerConfig() {
    }

    public static boolean getBoolean(String key) {
        switch (key.toLowerCase()) {
            case "1":
            case "true":
            case "yes":
                return true;
            case "0":
            case "false":
            case "no":
                return false;
            default:
                throw new RuntimeException("key is not a legal boolean");
        }
    }

    public static boolean getBoolean(final Map<String, String> settingMap,
                                     final String key) {
        assert (settingMap != null);
        assert (key != null);
        if (!settingMap.containsKey(key)) {
            return false;
        }
        return getBoolean(settingMap.get(key));
    }

    public static String getString(final Map<String, String> settingMap,
                                   final String key) {
        assert (settingMap != null);
        assert (key != null);
        if (!settingMap.containsKey(key)) {
            return null;
        }
        return settingMap.get(key);
    }

    public static String getString(final Map<String, String> settingMap,
                                   final String key,
                                   final String defaultValue) {
        String res = getString(settingMap, key);
        if (res == null) {
            res = defaultValue;
        }
        return res;
    }

    /**
     * If exist this tag then does not start console thread when start up the
     * game.
     *
     * @see com.xenoamess.cyan_potion.base.console.ConsoleThread
     */
    public static final String STRING_NO_CONSOLE_THREAD = "noConsoleThread";

    /**
     * The title text's ID.
     *
     * @see com.xenoamess.multi_language
     * @see GameManager
     */
    public static final String STRING_TITLE_TEXT_ID = "titleTextID";

    /**
     * The text file's path
     *
     * @see GameManager
     */
    public static final String STRING_TEXT_FILE_PATH = "textFilePath";

    /**
     * The icon file's path
     *
     * @see GameWindow
     * @see GameManager
     */
    public static final String STRING_ICON_FILE_PATH = "iconFilePath";

    /**
     * The default font's path
     *
     * @see GameWindow
     * @see GameManager
     */
    public static final String STRING_DEFAULT_FONT_FILE_PATH =
            "defaultFontFilePath";


    /**
     * Language.
     */
    public static final String STRING_LANGUAGE = "language";

    /**
     * The class that start as game window.
     * This class must be derived from GameWindow
     *
     * @see GameWindow
     * @see GameManager
     */
    public static final String STRING_GAME_WINDOW_CLASS_NAME =
            "gameWindowClassName";

    /**
     * The game window width when startup.
     *
     * @see GameWindow
     * @see GameManager
     */
    public static final String STRING_WINDOW_WIDTH = "windowWidth";

    /**
     * The game window height when startup.
     *
     * @see GameWindow
     * @see GameManager
     */
    public static final String STRING_WINDOW_HEIGHT = "windowHeight";

    /**
     * The force game window width when startup.
     *
     * @see GameWindow
     * @see GameManager
     */
    public static final String STRING_FORCE_WINDOW_WIDTH = "forceWindowWidth";

    /**
     * The force game window height when startup.
     *
     * @see GameWindow
     * @see GameManager
     */
    public static final String STRING_FORCE_WINDOW_HEIGHT = "forceWindowHeight";


    /**
     * If full screen when startup.
     *
     * @see GameWindow
     * @see GameManager
     */
    public static final String STRING_FULL_SCREEN = "fullScreen";


    /**
     * if auto show the window when init.
     *
     * @see GameWindow
     * @see GameManager
     */
    public static final String STRING_AUTO_SHOW_GAME_WINDOW_AFTER_INIT =
            "autoShowGameWindowAfterInit";

    /**
     * The logo class(show when start up).
     *
     * @see GameWindow
     * @see GameManager
     */
    public static final String STRING_LOGO_CLASS_NAME = "logoClassName";

}
