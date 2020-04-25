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

import com.xenoamess.commonx.java.lang.IllegalArgumentExceptionUtilsx;
import com.xenoamess.cyan_potion.base.exceptions.ConfigFileBooleanValueStringIsNotBooleanException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * <p>GameManagerConfig class.</p>
 *
 * @author XenoAmess
 * @version 0.162.1
 */
@EqualsAndHashCode
@ToString
public class GameManagerConfig {

    /**
     * Don't let anyone instantiate this class.
     */
    private GameManagerConfig() {
        //shall never
    }

    /**
     * <p>getInteger.</p>
     *
     * @param value value
     * @return a boolean.
     */
    public static int getInteger(String value) {
        /*
         * if value is null or empty, we think that it have no value part,
         * and a key part alone is thought to mean -1.
         */
        if (StringUtils.isBlank(value)) {
            return -1;
        }

        return Integer.parseInt(value);
    }

    /**
     * <p>getInteger.</p>
     *
     * @param settingMap settingMap
     * @param key        a {@link java.lang.String} object.
     * @return a boolean.
     */
    public static int getInteger(final Map<String, String> settingMap,
                                 final String key) {
        return getInteger(settingMap, key, -1);
    }

    /**
     * <p>getInteger.</p>
     *
     * @param settingMap   a {@link java.util.Map} object.
     * @param key          a {@link java.lang.String} object.
     * @param defaultValue a boolean.
     * @return a boolean.
     */
    public static int getInteger(final Map<String, String> settingMap,
                                 final String key, int defaultValue) {
        IllegalArgumentExceptionUtilsx.isAnyNullInParamsThenThrowIllegalArgumentException(settingMap, key);
        if (!settingMap.containsKey(key)) {
            return defaultValue;
        }
        return getInteger(settingMap.get(key));
    }

    /**
     * <p>getFloat.</p>
     *
     * @param value value
     * @return a boolean.
     */
    public static float getFloat(String value) {
        /*
         * if value is null or empty, we think that it have no value part,
         * and a key part alone is thought to mean NaN.
         */
        if (StringUtils.isBlank(value)) {
            return Float.NaN;
        }

        return Float.parseFloat(value);
    }

    /**
     * <p>getFloat.</p>
     *
     * @param settingMap settingMap
     * @param key        a {@link java.lang.String} object.
     * @return a boolean.
     */
    public static float getFloat(final Map<String, String> settingMap,
                                 final String key) {
        return getFloat(settingMap, key, Float.NaN);
    }

    /**
     * <p>getFloat.</p>
     *
     * @param settingMap   a {@link java.util.Map} object.
     * @param key          a {@link java.lang.String} object.
     * @param defaultValue a boolean.
     * @return a boolean.
     */
    public static float getFloat(final Map<String, String> settingMap,
                                 final String key, float defaultValue) {
        IllegalArgumentExceptionUtilsx.isAnyNullInParamsThenThrowIllegalArgumentException(settingMap, key);
        if (!settingMap.containsKey(key)) {
            return defaultValue;
        }
        return getFloat(settingMap.get(key));
    }

    /**
     * <p>getBoolean.</p>
     *
     * @param value value
     * @return a boolean.
     */
    public static boolean getBoolean(String value) {
        /*
         * if value is null or empty, we think that it have no value part,
         * and a key part alone is thought to mean true.
         */
        if (StringUtils.isBlank(value)) {
            return true;
        }

        switch (value.toLowerCase()) {
            case "1":
            case "true":
            case "yes":
                return true;
            case "0":
            case "false":
            case "no":
                return false;
            default:
                throw new ConfigFileBooleanValueStringIsNotBooleanException(value);
        }
    }

    /**
     * <p>getBoolean.</p>
     *
     * @param settingMap settingMap
     * @param key        a {@link java.lang.String} object.
     * @return a boolean.
     */
    public static boolean getBoolean(final Map<String, String> settingMap,
                                     final String key) {
        return getBoolean(settingMap, key, false);
    }

    /**
     * <p>getBoolean.</p>
     *
     * @param settingMap   a {@link java.util.Map} object.
     * @param key          a {@link java.lang.String} object.
     * @param defaultValue a boolean.
     * @return a boolean.
     */
    public static boolean getBoolean(final Map<String, String> settingMap,
                                     final String key, boolean defaultValue) {
        IllegalArgumentExceptionUtilsx.isAnyNullInParamsThenThrowIllegalArgumentException(settingMap, key);
        if (!settingMap.containsKey(key)) {
            return defaultValue;
        }
        return getBoolean(settingMap.get(key));
    }

    /**
     * <p>getString.</p>
     *
     * @param settingMap settingMap
     * @param key        a {@link java.lang.String} object.
     * @return return
     */
    public static String getString(final Map<String, String> settingMap,
                                   final String key) {
        return getString(settingMap, key, null);
    }

    /**
     * <p>getString.</p>
     *
     * @param settingMap   a {@link java.util.Map} object.
     * @param key          a {@link java.lang.String} object.
     * @param defaultValue defaultValue
     * @return return
     */
    public static String getString(final Map<String, String> settingMap,
                                   final String key,
                                   final String defaultValue) {
        IllegalArgumentExceptionUtilsx.isAnyNullInParamsThenThrowIllegalArgumentException(settingMap, key);
        if (!settingMap.containsKey(key)) {
            return defaultValue;
        }
        return settingMap.get(key);
    }


    /**
     * If exist this tag then does not start console thread when start up the
     * game.
     *
     * @see com.xenoamess.cyan_potion.base.console.ConsoleTalkThreadManager
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
     * The game name.
     *
     * @see com.xenoamess.multi_language
     * @see GameManager
     */
    public static final String STRING_GAME_NAME = "gameName";

    /**
     * The game version.
     *
     * @see com.xenoamess.multi_language
     * @see GameManager
     */
    public static final String STRING_GAME_VERSION = "gameVersion";


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
     * the max fps of this game.
     * if -1 or missing,
     * then no max fps limit will be set,
     * and your game will suck performance as much as possible.
     *
     * @see GameManager
     */
    public static final String STRING_MAX_FPS = "maxFPS";

    /**
     * The default font's path
     *
     * @see GameWindow
     * @see GameManager
     */
    public static final String STRING_DEFAULT_FONT_RESOURCE_URI =
            "defaultFontResourceURI";

    /**
     * Language.
     */
    public static final String STRING_LANGUAGE = "language";

    /**
     * runWithSteam.
     */
    public static final String STRING_RUN_WITH_STEAM = "runWithSteam";

    /**
     * SteamRunCallbacksTime.
     */
    public static final String STRING_STEAM_RUN_CALL_BACKS_TIME = "SteamRunCallbacksTime";


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
    public static final String STRING_LOGIC_WINDOW_WIDTH = "logicWindowWidth";

    /**
     * The game window height when startup.
     *
     * @see GameWindow
     * @see GameManager
     */
    public static final String STRING_LOGIC_WINDOW_HEIGHT = "logicWindowHeight";

    /**
     * The force game window width when startup.
     *
     * @see GameWindow
     * @see GameManager
     */
    public static final String STRING_REAL_WINDOW_WIDTH = "realWindowWidth";

    /**
     * The force game window height when startup.
     *
     * @see GameWindow
     * @see GameManager
     */
    public static final String STRING_REAL_WINDOW_HEIGHT = "realWindowHeight";


    /**
     * If full screen when startup.
     *
     * @see GameWindow
     * @see GameManager
     */
    public static final String STRING_FULL_SCREEN = "fullScreen";

    /**
     * If the window resizable.
     *
     * @see GameWindow
     */
    public static final String STRING_GAME_WINDOW_RESIZABLE = "gameWindowResizable";


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

    /**
     * The title class
     *
     * @see GameWindow
     * @see GameManager
     */
    public static final String STRING_TITLE_CLASS_NAME = "titleClassName";

    /**
     * The world class
     *
     * @see GameWindow
     * @see GameManager
     */
    public static final String STRING_WORLD_CLASS_NAME = "worldClassName";


    /**
     * The steam_appid of game.
     * will load it from settings.
     * if settings does not describe about this, then will load from steam_appid.txt.
     *
     * @see com.xenoamess.multi_language
     * @see GameManager
     */
    public static final String STRING_STEAM_APPID = "steam_appid";
}
