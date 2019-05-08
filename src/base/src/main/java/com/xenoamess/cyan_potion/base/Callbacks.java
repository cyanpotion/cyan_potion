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

import com.codedisaster.steamworks.*;
import com.xenoamess.cyan_potion.base.events.*;
import org.lwjgl.glfw.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author XenoAmess
 */
public final class Callbacks {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(Callbacks.class);


    private final GameManager gameManager;

    public Callbacks(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    private GLFWWindowCloseCallbackI windowCloseCallback =
            window -> {
                LOGGER.debug("Alright I exit.");
                getGameManager().shutdown();
            };

    private GLFWKeyCallbackI keyCallback =
            (long window, int key, int scancode, int action, int mods) -> {
                Event event = new KeyEvent(window, key, scancode, action, mods);
                getGameManager().eventListAdd(event);
            };

    private GLFWJoystickCallbackI joystickCallback =
            (int jid, int event) -> LOGGER.debug("jid : {}, event : {}", jid, event);

    private GLFWMouseButtonCallbackI mouseButtonCallback =
            (long window, int button, int action, int mods) -> {
                Event event = new MouseButtonEvent(window, button, action
                        , mods);
                getGameManager().eventListAdd(event);
            };

    private GLFWScrollCallbackI scrollCallback =
            (long window, double xoffset, double yoffset) -> {
                Event event = new MouseScrollEvent(window, xoffset, yoffset);
                getGameManager().eventListAdd(event);
            };


    private GLFWWindowSizeCallbackI windowSizeCallback =
            (long window, int width, int height) -> {
                Event event = new WindowResizeEvent(window, width, height);
                getGameManager().eventListAdd(event);
            };

    private GLFWCharCallbackI charCallback =
            (long window, int codepoint) -> {
                Event event = new CharEvent(window, codepoint);
                getGameManager().eventListAdd(event);
            };


    private SteamUserStatsCallback steamUserStatsCallback =
            new SteamUserStatsCallback() {

                @Override
                public void onUserStatsReceived(long gameId,
                                                SteamID steamIDUser,
                                                SteamResult result) {
                    // do nothing here.
                }

                @Override
                public void onUserStatsStored(long gameId, SteamResult result) {
                    // do nothing here.
                }

                @Override
                public void onUserStatsUnloaded(SteamID steamIDUser) {
                    // do nothing here.
                }

                @Override
                public void onUserAchievementStored(long gameId,
                                                    boolean isGroupAchievement,
                                                    String achievementName,
                                                    int curProgress,
                                                    int maxProgress) {
                    // do nothing here.
                }

                @Override
                public void onLeaderboardFindResult(SteamLeaderboardHandle leaderboard, boolean found) {
                    // do nothing here.
                }

                @Override
                public void onLeaderboardScoresDownloaded(SteamLeaderboardHandle leaderboard,
                                                          SteamLeaderboardEntriesHandle entries, int numEntries) {
                    // do nothing here.
                }

                @Override
                public void onLeaderboardScoreUploaded(boolean success,
                                                       SteamLeaderboardHandle leaderboard, int score,
                                                       boolean scoreChanged, int globalRankNew,
                                                       int globalRankPrevious) {
                    // do nothing here.
                }

                @Override
                public void onGlobalStatsReceived(long gameId,
                                                  SteamResult result) {
                    // do nothing here.
                }
            };


    public GameManager getGameManager() {
        return gameManager;
    }

    public GLFWWindowCloseCallbackI getWindowCloseCallback() {
        return windowCloseCallback;
    }

    public void setWindowCloseCallback(GLFWWindowCloseCallbackI windowCloseCallback) {
        this.windowCloseCallback = windowCloseCallback;
    }

    public GLFWKeyCallbackI getKeyCallback() {
        return keyCallback;
    }

    public void setKeyCallback(GLFWKeyCallbackI keyCallback) {
        this.keyCallback = keyCallback;
    }

    public GLFWJoystickCallbackI getJoystickCallback() {
        return joystickCallback;
    }

    public void setJoystickCallback(GLFWJoystickCallbackI joystickCallback) {
        this.joystickCallback = joystickCallback;
    }

    public GLFWMouseButtonCallbackI getMouseButtonCallback() {
        return mouseButtonCallback;
    }

    public void setMouseButtonCallback(GLFWMouseButtonCallbackI mouseButtonCallback) {
        this.mouseButtonCallback = mouseButtonCallback;
    }

    public GLFWScrollCallbackI getScrollCallback() {
        return scrollCallback;
    }

    public void setScrollCallback(GLFWScrollCallbackI scrollCallback) {
        this.scrollCallback = scrollCallback;
    }

    public GLFWWindowSizeCallbackI getWindowSizeCallback() {
        return windowSizeCallback;
    }

    public void setWindowSizeCallback(GLFWWindowSizeCallbackI windowSizeCallback) {
        this.windowSizeCallback = windowSizeCallback;
    }

    public GLFWCharCallbackI getCharCallback() {
        return charCallback;
    }

    public void setCharCallback(GLFWCharCallbackI charCallback) {
        this.charCallback = charCallback;
    }

    public SteamUserStatsCallback getSteamUserStatsCallback() {
        return steamUserStatsCallback;
    }

    public void setSteamUserStatsCallback(SteamUserStatsCallback steamUserStatsCallback) {
        this.steamUserStatsCallback = steamUserStatsCallback;
    }
}
