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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xenoamess.multi_language.MultiLanguageStructure;
import com.xenoamess.x8l.X8lTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
