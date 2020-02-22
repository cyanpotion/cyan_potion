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

import com.xenoamess.commons.version.Version;
import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import com.xenoamess.cyan_potion.base.plugins.CodePluginPosition;
import com.xenoamess.cyan_potion.base.visual.Font;
import com.xenoamess.x8l.AbstractTreeNode;
import com.xenoamess.x8l.ContentNode;
import com.xenoamess.x8l.TextNode;
import com.xenoamess.x8l.X8lTree;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.system.Configuration;

import java.awt.*;

import static com.xenoamess.cyan_potion.base.GameManagerConfig.*;

/**
 * <p>SettingFIleParser_0_3_0 class.</p>
 *
 * @author XenoAmess
 * @version 0.161.1-SNAPSHOT
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class SettingFIleParser_0_3_0 extends AbstractSettingFileParser {
    /**
     * Constant <code>version_0_3_0</code>
     */
    public static final Version version_0_3_0 = new Version("0.3.0");

    /**
     * <p>Constructor for SettingFIleParser_0_3_0.</p>
     */
    protected SettingFIleParser_0_3_0() {
        super(version_0_3_0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GameSettings parse(X8lTree settingTree) {
        GameSettings gameSettings = new GameSettings(settingTree);

        readCommonSettings(gameSettings, settingTree);
        readKeymap(gameSettings, settingTree);
        readOthers(gameSettings, settingTree);
        readViews(gameSettings, settingTree);
        readSteamSettings(gameSettings, settingTree);
        readClassNames(gameSettings, settingTree);
        return gameSettings;
    }


    /**
     * <p>readCommonSettings.</p>
     *
     * @param gameSettings a {@link com.xenoamess.cyan_potion.base.setting_file.GameSettings} object.
     * @param settingTree  a {@link com.xenoamess.x8l.X8lTree} object.
     */
    protected void readCommonSettings(GameSettings gameSettings, X8lTree settingTree) {
        ContentNode baseNode = settingTree.getRoot().getContentNodesFromChildrenThatNameIs("settingFile").get(0);
        for (ContentNode contentNode : baseNode.getContentNodesFromChildrenThatNameIs("commonSettings")
        ) {
            gameSettings.getCommonSettings().putAll(contentNode.getAttributes());
        }

        for (ContentNode contentNode : baseNode.getContentNodesFromChildrenThatNameIs("specialSettings")) {
            gameSettings.getSpecialSettings().putAll(contentNode.getAttributes());
        }
        for (ContentNode contentNode : baseNode.getContentNodesFromChildrenThatNameIs("views")) {
            gameSettings.getViews().putAll(contentNode.getAttributes());
        }
        for (ContentNode pluginNode : baseNode.getContentNodesFromChildrenThatNameIs("codePlugins")) {
            for (ContentNode simplePluginNode : pluginNode.getContentNodesFromChildren()) {
                gameSettings.getCodePluginManagerSettings().add(
                        Pair.of(
                                CodePluginPosition.valueOf(simplePluginNode.getName()),
                                simplePluginNode.getTextNodesFromChildren(1).get(0).getTextContent()
                        )
                );
            }
        }

        gameSettings.setTitleTextID(getString(gameSettings.getCommonSettings(),
                STRING_TITLE_TEXT_ID, ""));
        gameSettings.setGameName(getString(gameSettings.getCommonSettings(),
                STRING_GAME_NAME, ResourceManager.resolveFile(System.getProperty("user.dir")).getName().getBaseName()));
        gameSettings.setGameVersion(getString(gameSettings.getCommonSettings(),
                STRING_GAME_VERSION, "1.0"));
        gameSettings.setTextFilePath(getString(gameSettings.getCommonSettings(),
                STRING_TEXT_FILE_PATH, "resources/text/text.x8l"));
        gameSettings.setIconFilePath(getString(gameSettings.getCommonSettings(),
                STRING_ICON_FILE_PATH, "resources/www/icon/icon.png"));
        gameSettings.setMaxFPS(getInteger(gameSettings.getCommonSettings(),
                STRING_MAX_FPS, -1));
    }


    /**
     * <p>readKeymap.</p>
     *
     * @param gameSettings a {@link com.xenoamess.cyan_potion.base.setting_file.GameSettings} object.
     * @param settingTree  a {@link com.xenoamess.x8l.X8lTree} object.
     */
    protected void readKeymap(GameSettings gameSettings, X8lTree settingTree) {
        ContentNode baseNode = settingTree.getRoot().getContentNodesFromChildrenThatNameIs("settingFile").get(0);
        for (ContentNode contentNode : baseNode.getContentNodesFromChildrenThatNameIs("keymap")) {
            if (getBoolean(contentNode.getAttributes(), "using")) {
                for (AbstractTreeNode au2 : contentNode.getChildren()) {
                    if (au2 instanceof ContentNode) {
                        ContentNode contentNode2 = (ContentNode) au2;
                        if (!contentNode2.getAttributes().isEmpty() && !contentNode2.getChildren().isEmpty()) {
                            String rawInput = contentNode2.getName();
                            String myInput = null;
                            for (AbstractTreeNode au3 : contentNode2.getChildren()) {
                                if (au3 instanceof TextNode) {
                                    myInput = ((TextNode) au3).getTextContent();
                                    break;
                                }
                            }
                            gameSettings.getKeymapSettings().add(Pair.of(rawInput, myInput));
                        }
                    }
                }
            }
        }

        for (ContentNode contentNode : baseNode.getContentNodesFromChildrenThatNameIs("debug")) {
            boolean debug = getBoolean(contentNode.getAttributes(), "debug");
            gameSettings.setDebug(debug);
            Configuration.DEBUG.set(debug);
            Configuration.DEBUG_LOADER.set(debug);
        }
    }

    /**
     * <p>readKeymap.</p>
     *
     * @param gameSettings a {@link com.xenoamess.cyan_potion.base.setting_file.GameSettings} object.
     * @param settingTree  a {@link com.xenoamess.x8l.X8lTree} object.
     */
    protected void readOthers(GameSettings gameSettings, X8lTree settingTree) {
        gameSettings.setNoConsoleThread(
                getBoolean(gameSettings.getSpecialSettings(), STRING_NO_CONSOLE_THREAD)
        );
        gameSettings.setDefaultFontResourceJsonString(
                getString(
                        gameSettings.getCommonSettings(),
                        STRING_DEFAULT_FONT_RESOURCE_URI,
                        Font.DEFAULT_DEFAULT_FONT_RESOURCE_URI
                )
        );
        gameSettings.setSettingLanguage(
                getString(
                        gameSettings.getCommonSettings(),
                        STRING_LANGUAGE,
                        ""
                )
        );

    }

    private void readViews(GameSettings gameSettings, X8lTree settingTree) {
        gameSettings.setLogicWindowWidth(
                Integer.parseInt(
                        getString(
                                gameSettings.getViews(),
                                STRING_LOGIC_WINDOW_WIDTH, "1280"
                        )
                )
        );

        gameSettings.setLogicWindowHeight(
                Integer.parseInt(
                        getString(
                                gameSettings.getViews(),
                                STRING_LOGIC_WINDOW_HEIGHT, "1024"
                        )
                )
        );

        gameSettings.setLogicWindowHeight(
                Integer.parseInt(
                        getString(
                                gameSettings.getViews(),
                                STRING_LOGIC_WINDOW_HEIGHT, "1024"
                        )
                )
        );
        gameSettings.setRealWindowWidth(gameSettings.getLogicWindowWidth());
        gameSettings.setRealWindowHeight(gameSettings.getLogicWindowHeight());

        {
            String tmpString;
            if ((tmpString = getString(gameSettings.getViews(), STRING_REAL_WINDOW_WIDTH)) != null) {
                gameSettings.setRealWindowWidth(Integer.parseInt(tmpString));
                gameSettings.setRealWindowHeight((int) (1f * gameSettings.getRealWindowWidth() / gameSettings.getLogicWindowWidth() * gameSettings.getRealWindowHeight()));
            }
            if ((tmpString = getString(gameSettings.getViews(), STRING_REAL_WINDOW_HEIGHT)) != null) {
                gameSettings.setRealWindowHeight(Integer.parseInt(tmpString));
                gameSettings.setRealWindowWidth((int) (1f * gameSettings.getRealWindowHeight() / gameSettings.getLogicWindowHeight() * gameSettings.getLogicWindowWidth()));
            }
            if (gameSettings.getRealWindowHeight() > Toolkit.getDefaultToolkit().getScreenSize().getHeight()) {
                gameSettings.setRealWindowHeight((int) Toolkit.getDefaultToolkit().getScreenSize().getHeight());
                gameSettings.setRealWindowWidth((int) (1f * gameSettings.getRealWindowHeight() / gameSettings.getLogicWindowHeight() * gameSettings.getLogicWindowWidth()));
            }
            if (gameSettings.getRealWindowWidth() > Toolkit.getDefaultToolkit().getScreenSize().getWidth()) {
                gameSettings.setRealWindowWidth((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth());
                gameSettings.setRealWindowHeight((int) (1f * gameSettings.getRealWindowWidth() / gameSettings.getLogicWindowWidth() * gameSettings.getRealWindowHeight()));
            }
        }
        gameSettings.setFullScreen(
                getBoolean(
                        gameSettings.getViews(), STRING_FULL_SCREEN
                )
        );

        gameSettings.setAutoShowGameWindowAfterInit(
                getBoolean(
                        gameSettings.getSpecialSettings(),
                        STRING_AUTO_SHOW_GAME_WINDOW_AFTER_INIT, true
                )
        );
    }

    private void readSteamSettings(GameSettings gameSettings, X8lTree settingTree) {
        gameSettings.setRunWithSteam(
                getBoolean(
                        gameSettings.getCommonSettings(),
                        STRING_RUN_WITH_STEAM,
                        true
                )
        );

        gameSettings.setSteamRunCallbacksNanoLong(
                Long.parseLong(
                        getString(
                                gameSettings.getCommonSettings(),
                                STRING_STEAM_RUN_CALL_BACKS_TIME,
                                "10000000000"
                        )
                )
        );

        gameSettings.setSteam_appid(
                getString(
                        gameSettings.getCommonSettings(),
                        STRING_STEAM_APPID
                )
        );
    }

    private void readClassNames(GameSettings gameSettings, X8lTree settingTree) {
        gameSettings.setGameWindowClassName(
                getString(
                        gameSettings.getCommonSettings(),
                        STRING_GAME_WINDOW_CLASS_NAME,
                        "com.xenoamess.cyan_potion.base.GameWindow"
                )
        );

        gameSettings.setLogoClassName(
                getString(
                        gameSettings.getCommonSettings(),
                        STRING_LOGO_CLASS_NAME,
                        "com.xenoamess.cyan_potion.base.game_window_components.Logo"
                )
        );

        gameSettings.setTitleClassName(
                getString(
                        gameSettings.getCommonSettings(),
                        STRING_TITLE_CLASS_NAME,
                        "com.xenoamess.cyan_potion.base.game_window_components.TitleExample"
                )
        );

        gameSettings.setWorldClassName(
                getString(
                        gameSettings.getCommonSettings(),
                        STRING_WORLD_CLASS_NAME,
                        "com.xenoamess.cyan_potion.rpg_module.world.World"
                )
        );
    }
}
