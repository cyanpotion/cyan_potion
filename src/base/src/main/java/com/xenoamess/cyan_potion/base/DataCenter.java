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
import com.xenoamess.commons.as_final_field.AsFinalField;
import com.xenoamess.multi_language.MultiLanguageStructure;
import com.xenoamess.x8l.X8lTree;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author XenoAmess
 */
public class DataCenter {

    private boolean debug = false;
    public static final boolean ALLOW_RUN_WITHOUT_STEAM = true;
    private boolean runWithSteam = true;

    public static final int CONSOLE_PORT = 13888;
    public static final double FRAME_CAP = 1 / 60.0;
    public static final int SCALE = 2;

    private X8lTree globalSettingsTree;
    private X8lTree patchSettingsTree;
    private final Map<String, String> commonSettings = new ConcurrentHashMap<>();
    private final Map<String, String> specialSettings = new ConcurrentHashMap<>();
    private final Map<String, String> views = new ConcurrentHashMap<>();


    private String textFilePath = null;
    private String iconFilePath = null;

    private MultiLanguageStructure textStructure;

    private GameManager gameManager;
    private String titleTextID;

    public DataCenter(GameManager gameManager) {
        this.setGameManager(gameManager);
    }

    @AsFinalField
    private static ObjectMapper objectMapper;

    public static ObjectMapper getObjectMapper() {
        //lazy init.
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        return objectMapper;
    }


    public static boolean isWindows() {
        return System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1;
    }

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

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isRunWithSteam() {
        return runWithSteam;
    }

    public void setRunWithSteam(boolean runWithSteam) {
        this.runWithSteam = runWithSteam;
    }

    public X8lTree getPatchSettingsTree() {
        return patchSettingsTree;
    }

    public void setPatchSettingsTree(X8lTree patchSettingsTree) {
        this.patchSettingsTree = patchSettingsTree;
    }

    public void patchGlobalSettingsTree() {
        this.getGlobalSettingsTree().append(this.getPatchSettingsTree());
    }
}
