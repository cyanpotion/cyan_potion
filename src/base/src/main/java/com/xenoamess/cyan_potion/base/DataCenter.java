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
 * <p>DataCenter class.</p>
 *
 * @author XenoAmess
 * @version 0.143.0
 */
public class DataCenter {

    private boolean debug = false;
    /**
     * Constant <code>ALLOW_RUN_WITHOUT_STEAM=true</code>
     */
    public static final boolean ALLOW_RUN_WITHOUT_STEAM = true;
    private boolean runWithSteam = true;

    /**
     * Constant <code>CONSOLE_PORT=13888</code>
     */
    public static final int CONSOLE_PORT = 13888;
    /**
     * Constant <code>FRAME_CAP=1 / 60.0</code>
     */
    public static final double FRAME_CAP = 1 / 60.0;
    /**
     * Constant <code>SCALE=2</code>
     */
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

    /**
     * <p>Constructor for DataCenter.</p>
     *
     * @param gameManager a {@link com.xenoamess.cyan_potion.base.GameManager} object.
     */
    public DataCenter(GameManager gameManager) {
        this.setGameManager(gameManager);
    }

    @AsFinalField
    private static ObjectMapper objectMapper = null;

    /**
     * <p>Getter for the field <code>objectMapper</code>.</p>
     *
     * @return a {@link com.fasterxml.jackson.databind.ObjectMapper} object.
     */
    public static ObjectMapper getObjectMapper() {
        /*
         * lazy init.
         * don't sync here because it will not cause big trouble if we have multiple objectMappers.
         */
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        return objectMapper;
    }


    /**
     * <p>isWindows.</p>
     *
     * @return a boolean.
     */
    public static boolean isWindows() {
        return System.getProperties().getProperty("os.name").toUpperCase().contains("WINDOWS");
    }

    /**
     * <p>Getter for the field <code>globalSettingsTree</code>.</p>
     *
     * @return a {@link com.xenoamess.x8l.X8lTree} object.
     */
    public X8lTree getGlobalSettingsTree() {
        return globalSettingsTree;
    }

    /**
     * <p>Setter for the field <code>globalSettingsTree</code>.</p>
     *
     * @param globalSettingsTree a {@link com.xenoamess.x8l.X8lTree} object.
     */
    public void setGlobalSettingsTree(X8lTree globalSettingsTree) {
        this.globalSettingsTree = globalSettingsTree;
    }

    /**
     * <p>Getter for the field <code>commonSettings</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, String> getCommonSettings() {
        return commonSettings;
    }


    /**
     * <p>Getter for the field <code>specialSettings</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, String> getSpecialSettings() {
        return specialSettings;
    }

    /**
     * <p>Getter for the field <code>views</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, String> getViews() {
        return views;
    }

    /**
     * <p>Getter for the field <code>textFilePath</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getTextFilePath() {
        return textFilePath;
    }

    /**
     * <p>Setter for the field <code>textFilePath</code>.</p>
     *
     * @param textFilePath a {@link java.lang.String} object.
     */
    public void setTextFilePath(String textFilePath) {
        this.textFilePath = textFilePath;
    }

    /**
     * <p>Getter for the field <code>iconFilePath</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getIconFilePath() {
        return iconFilePath;
    }

    /**
     * <p>Setter for the field <code>iconFilePath</code>.</p>
     *
     * @param iconFilePath a {@link java.lang.String} object.
     */
    public void setIconFilePath(String iconFilePath) {
        this.iconFilePath = iconFilePath;
    }

    /**
     * <p>Getter for the field <code>textStructure</code>.</p>
     *
     * @return a {@link com.xenoamess.multi_language.MultiLanguageStructure} object.
     */
    public MultiLanguageStructure getTextStructure() {
        return textStructure;
    }

    /**
     * <p>Setter for the field <code>textStructure</code>.</p>
     *
     * @param textStructure a {@link com.xenoamess.multi_language.MultiLanguageStructure} object.
     */
    public void setTextStructure(MultiLanguageStructure textStructure) {
        this.textStructure = textStructure;
    }

    /**
     * <p>Getter for the field <code>gameManager</code>.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.GameManager} object.
     */
    public GameManager getGameManager() {
        return gameManager;
    }

    /**
     * <p>Setter for the field <code>gameManager</code>.</p>
     *
     * @param gameManager a {@link com.xenoamess.cyan_potion.base.GameManager} object.
     */
    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * <p>Getter for the field <code>titleTextID</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getTitleTextID() {
        return titleTextID;
    }

    /**
     * <p>Setter for the field <code>titleTextID</code>.</p>
     *
     * @param titleTextID a {@link java.lang.String} object.
     */
    public void setTitleTextID(String titleTextID) {
        this.titleTextID = titleTextID;
    }

    /**
     * <p>isDebug.</p>
     *
     * @return a boolean.
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * <p>Setter for the field <code>debug</code>.</p>
     *
     * @param debug a boolean.
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * <p>isRunWithSteam.</p>
     *
     * @return a boolean.
     */
    public boolean isRunWithSteam() {
        return runWithSteam;
    }

    /**
     * <p>Setter for the field <code>runWithSteam</code>.</p>
     *
     * @param runWithSteam a boolean.
     */
    public void setRunWithSteam(boolean runWithSteam) {
        this.runWithSteam = runWithSteam;
    }

    /**
     * <p>Getter for the field <code>patchSettingsTree</code>.</p>
     *
     * @return a {@link com.xenoamess.x8l.X8lTree} object.
     */
    public X8lTree getPatchSettingsTree() {
        return patchSettingsTree;
    }

    /**
     * <p>Setter for the field <code>patchSettingsTree</code>.</p>
     *
     * @param patchSettingsTree a {@link com.xenoamess.x8l.X8lTree} object.
     */
    public void setPatchSettingsTree(X8lTree patchSettingsTree) {
        this.patchSettingsTree = patchSettingsTree;
    }

    /**
     * <p>patchGlobalSettingsTree.</p>
     */
    public void patchGlobalSettingsTree() {
        this.getGlobalSettingsTree().append(this.getPatchSettingsTree());
    }
}
