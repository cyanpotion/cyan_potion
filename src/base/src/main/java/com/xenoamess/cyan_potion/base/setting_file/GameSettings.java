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

package com.xenoamess.cyan_potion.base.setting_file;

import com.xenoamess.cyan_potion.base.plugins.CodePluginPosition;
import com.xenoamess.multi_language.MultiLanguageStructure;
import com.xenoamess.x8l.X8lTree;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>GameSettings class.</p>
 *
 * @author XenoAmess
 * @version 0.157.1-SNAPSHOT
 */
public class GameSettings {

    private final X8lTree settingTree;

    /**
     * <p>Constructor for GameSettings.</p>
     *
     * @param settingTree a {@link com.xenoamess.x8l.X8lTree} object.
     */
    public GameSettings(X8lTree settingTree) {
        this.settingTree = settingTree;
    }


    //----------

    private final Map<String, String> commonSettings = new ConcurrentHashMap<>();
    private final Map<String, String> specialSettings = new ConcurrentHashMap<>();
    private final Map<String, String> views = new ConcurrentHashMap<>();
    private final List<Pair<String, String>> keymapSettings = new ArrayList<>();
    private final List<Pair<CodePluginPosition, String>> codePluginManagerSettings = new ArrayList<>();

    //commonSettings----------

    /**
     * ID of title text of game window.
     * get title text with it from MultiLanguageStructure.
     *
     * @see MultiLanguageStructure#getText(String)
     * @see MultiLanguageStructure#getText(String, String)
     */
    private String titleTextID;

    /**
     * Name of the game.
     * notice that this name is used on save file path(and maybe other similar places),
     * and shall not be translated
     * (for different language version of the game can share save folders)
     */
    private String gameName;

    /**
     * version of the game.
     * this is used to determine save file version.
     */
    private String gameVersion;

    /**
     * path of text file
     */
    private String textFilePath;

    /**
     * path of icon file
     */
    private String iconFilePath;

    /**
     * steam app id
     */
    private String steam_appid;

    /**
     * to detect whether now is under debug mode.
     */
    private boolean debug;

    /**
     * default Font Resource Json String
     */
    private String defaultFontResourceJsonString;

    /**
     * language string loaded from setting file
     */
    private String settingLanguage;

    /**
     * if run With Steam
     */
    private boolean runWithSteam;

    private long steamRunCallbacksNanoLong;

    //specialSettings----------

    /**
     * if true then don't start console thread.
     */
    private boolean noConsoleThread;

    //views----------

    private int logicWindowWidth;
    private int logicWindowHeight;
    private int realWindowWidth;
    private int realWindowHeight;

    private boolean fullScreen;
    private boolean autoShowGameWindowAfterInit;

    //class names----------

    private String gameWindowClassName;
    private String logoClassName;
    private String titleClassName;
    private String worldClassName;

    //----------

    /**
     * <p>Getter for the field <code>commonSettings</code>.</p>
     *
     * @return return
     */
    public Map<String, String> getCommonSettings() {
        return commonSettings;
    }

    /**
     * <p>Getter for the field <code>specialSettings</code>.</p>
     *
     * @return return
     */
    public Map<String, String> getSpecialSettings() {
        return specialSettings;
    }

    /**
     * <p>Getter for the field <code>views</code>.</p>
     *
     * @return return
     */
    public Map<String, String> getViews() {
        return views;
    }

    /**
     * <p>Getter for the field <code>titleTextID</code>.</p>
     *
     * @return return
     */
    public String getTitleTextID() {
        return titleTextID;
    }

    /**
     * <p>Setter for the field <code>titleTextID</code>.</p>
     *
     * @param titleTextID titleTextID
     */
    public void setTitleTextID(String titleTextID) {
        this.titleTextID = titleTextID;
    }

    /**
     * <p>Getter for the field <code>gameName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * <p>Setter for the field <code>gameName</code>.</p>
     *
     * @param gameName a {@link java.lang.String} object.
     */
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    /**
     * <p>Getter for the field <code>gameVersion</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getGameVersion() {
        return gameVersion;
    }

    /**
     * <p>Setter for the field <code>gameVersion</code>.</p>
     *
     * @param gameVersion a {@link java.lang.String} object.
     */
    public void setGameVersion(String gameVersion) {
        this.gameVersion = gameVersion;
    }


    /**
     * <p>Getter for the field <code>textFilePath</code>.</p>
     *
     * @return return
     */
    public String getTextFilePath() {
        return textFilePath;
    }

    /**
     * <p>Setter for the field <code>textFilePath</code>.</p>
     *
     * @param textFilePath textFilePath
     */
    public void setTextFilePath(String textFilePath) {
        this.textFilePath = textFilePath;
    }

    /**
     * <p>Getter for the field <code>iconFilePath</code>.</p>
     *
     * @return return
     */
    public String getIconFilePath() {
        return iconFilePath;
    }

    /**
     * <p>Setter for the field <code>iconFilePath</code>.</p>
     *
     * @param iconFilePath iconFilePath
     */
    public void setIconFilePath(String iconFilePath) {
        this.iconFilePath = iconFilePath;
    }

    /**
     * <p>Getter for the field <code>steam_appid</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getSteam_appid() {
        return steam_appid;
    }

    /**
     * <p>Setter for the field <code>steam_appid</code>.</p>
     *
     * @param steam_appid a {@link java.lang.String} object.
     */
    public void setSteam_appid(String steam_appid) {
        this.steam_appid = steam_appid;
    }

    /**
     * <p>Getter for the field <code>keymapSettings</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Pair<String, String>> getKeymapSettings() {
        return keymapSettings;
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
     * <p>isNoConsoleThread.</p>
     *
     * @return a boolean.
     */
    public boolean isNoConsoleThread() {
        return noConsoleThread;
    }

    /**
     * <p>Setter for the field <code>noConsoleThread</code>.</p>
     *
     * @param noConsoleThread a boolean.
     */
    public void setNoConsoleThread(boolean noConsoleThread) {
        this.noConsoleThread = noConsoleThread;
    }

    /**
     * <p>Getter for the field <code>defaultFontResourceJsonString</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDefaultFontResourceJsonString() {
        return defaultFontResourceJsonString;
    }

    /**
     * <p>Setter for the field <code>defaultFontResourceJsonString</code>.</p>
     *
     * @param defaultFontResourceJsonString a {@link java.lang.String} object.
     */
    public void setDefaultFontResourceJsonString(String defaultFontResourceJsonString) {
        this.defaultFontResourceJsonString = defaultFontResourceJsonString;
    }

    /**
     * <p>Getter for the field <code>settingLanguage</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getSettingLanguage() {
        return settingLanguage;
    }

    /**
     * <p>Setter for the field <code>settingLanguage</code>.</p>
     *
     * @param settingLanguage a {@link java.lang.String} object.
     */
    public void setSettingLanguage(String settingLanguage) {
        this.settingLanguage = settingLanguage;
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
     * <p>Getter for the field <code>steamRunCallbacksNanoLong</code>.</p>
     *
     * @return a long.
     */
    public long getSteamRunCallbacksNanoLong() {
        return steamRunCallbacksNanoLong;
    }

    /**
     * <p>Setter for the field <code>steamRunCallbacksNanoLong</code>.</p>
     *
     * @param steamRunCallbacksNanoLong a long.
     */
    public void setSteamRunCallbacksNanoLong(long steamRunCallbacksNanoLong) {
        this.steamRunCallbacksNanoLong = steamRunCallbacksNanoLong;
    }

    /**
     * <p>Getter for the field <code>gameWindowClassName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getGameWindowClassName() {
        return gameWindowClassName;
    }

    /**
     * <p>Setter for the field <code>gameWindowClassName</code>.</p>
     *
     * @param gameWindowClassName a {@link java.lang.String} object.
     */
    public void setGameWindowClassName(String gameWindowClassName) {
        this.gameWindowClassName = gameWindowClassName;
    }

    /**
     * <p>Getter for the field <code>codePluginManagerSettings</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Pair<CodePluginPosition, String>> getCodePluginManagerSettings() {
        return codePluginManagerSettings;
    }

    /**
     * <p>Getter for the field <code>logicWindowWidth</code>.</p>
     *
     * @return a int.
     */
    public int getLogicWindowWidth() {
        return logicWindowWidth;
    }

    /**
     * <p>Setter for the field <code>logicWindowWidth</code>.</p>
     *
     * @param logicWindowWidth a int.
     */
    public void setLogicWindowWidth(int logicWindowWidth) {
        this.logicWindowWidth = logicWindowWidth;
    }

    /**
     * <p>Getter for the field <code>logicWindowHeight</code>.</p>
     *
     * @return a int.
     */
    public int getLogicWindowHeight() {
        return logicWindowHeight;
    }

    /**
     * <p>Setter for the field <code>logicWindowHeight</code>.</p>
     *
     * @param logicWindowHeight a int.
     */
    public void setLogicWindowHeight(int logicWindowHeight) {
        this.logicWindowHeight = logicWindowHeight;
    }

    /**
     * <p>Getter for the field <code>realWindowWidth</code>.</p>
     *
     * @return a int.
     */
    public int getRealWindowWidth() {
        return realWindowWidth;
    }

    /**
     * <p>Setter for the field <code>realWindowWidth</code>.</p>
     *
     * @param realWindowWidth a int.
     */
    public void setRealWindowWidth(int realWindowWidth) {
        this.realWindowWidth = realWindowWidth;
    }

    /**
     * <p>Getter for the field <code>realWindowHeight</code>.</p>
     *
     * @return a int.
     */
    public int getRealWindowHeight() {
        return realWindowHeight;
    }

    /**
     * <p>Setter for the field <code>realWindowHeight</code>.</p>
     *
     * @param realWindowHeight a int.
     */
    public void setRealWindowHeight(int realWindowHeight) {
        this.realWindowHeight = realWindowHeight;
    }

    /**
     * <p>isFullScreen.</p>
     *
     * @return a boolean.
     */
    public boolean isFullScreen() {
        return fullScreen;
    }

    /**
     * <p>Setter for the field <code>fullScreen</code>.</p>
     *
     * @param fullScreen a boolean.
     */
    public void setFullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
    }

    /**
     * <p>isAutoShowGameWindowAfterInit.</p>
     *
     * @return a boolean.
     */
    public boolean isAutoShowGameWindowAfterInit() {
        return autoShowGameWindowAfterInit;
    }

    /**
     * <p>Setter for the field <code>autoShowGameWindowAfterInit</code>.</p>
     *
     * @param autoShowGameWindowAfterInit a boolean.
     */
    public void setAutoShowGameWindowAfterInit(boolean autoShowGameWindowAfterInit) {
        this.autoShowGameWindowAfterInit = autoShowGameWindowAfterInit;
    }

    /**
     * <p>Getter for the field <code>logoClassName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getLogoClassName() {
        return logoClassName;
    }

    /**
     * <p>Setter for the field <code>logoClassName</code>.</p>
     *
     * @param logoClassName a {@link java.lang.String} object.
     */
    public void setLogoClassName(String logoClassName) {
        this.logoClassName = logoClassName;
    }

    /**
     * <p>Getter for the field <code>titleClassName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getTitleClassName() {
        return titleClassName;
    }

    /**
     * <p>Setter for the field <code>titleClassName</code>.</p>
     *
     * @param titleClassName a {@link java.lang.String} object.
     */
    public void setTitleClassName(String titleClassName) {
        this.titleClassName = titleClassName;
    }

    /**
     * <p>Getter for the field <code>worldClassName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getWorldClassName() {
        return worldClassName;
    }

    /**
     * <p>Setter for the field <code>worldClassName</code>.</p>
     *
     * @param worldClassName a {@link java.lang.String} object.
     */
    public void setWorldClassName(String worldClassName) {
        this.worldClassName = worldClassName;
    }

    /**
     * <p>Getter for the field <code>settingTree</code>.</p>
     *
     * @return a {@link com.xenoamess.x8l.X8lTree} object.
     */
    public X8lTree getSettingTree() {
        return settingTree;
    }
}
