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
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>GameSettings class.</p>
 *
 * @author XenoAmess
 * @version 0.161.2
 */
@Data
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

    private int maxFPS;

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
}
