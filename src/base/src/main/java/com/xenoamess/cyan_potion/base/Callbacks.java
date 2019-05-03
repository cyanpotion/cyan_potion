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

/**
 * @author XenoAmess
 */
public final class Callbacks {
    private GameManager gameManager;

    public Callbacks(GameManager gameManager) {
        this.setGameManager(gameManager);
    }

    public GLFWWindowCloseCallbackI windowCloseCallback =
            new GLFWWindowCloseCallbackI() {
                @Override
                public void invoke(long window) {
                    System.out.println("Alright I exit.");
                    getGameManager().shutdown();
                }
            };

    public GLFWKeyCallbackI keyCallback = new GLFWKeyCallbackI() {
        @Override
        public void invoke(long window, int key, int scancode, int action,
                           int mods) {
            Event event = new KeyEvent(window, key, scancode, action, mods);
            getGameManager().eventListAdd(event);
        }
    };

    public GLFWJoystickCallbackI joystickCallback =
            new GLFWJoystickCallbackI() {
                @Override
                public void invoke(int jid, int event) {
                    System.out.println("jid" + jid + "event" + event);
                }
            };

    public GLFWMouseButtonCallbackI mouseButtonCallback =
            new GLFWMouseButtonCallbackI() {
                @Override
                public void invoke(long window, int button, int action,
                                   int mods) {
                    Event event = new MouseButtonEvent(window, button, action
                            , mods);
                    getGameManager().eventListAdd(event);
                }
            };

    public GLFWScrollCallbackI scrollCallback = new GLFWScrollCallbackI() {
        @Override
        public void invoke(long window, double xoffset, double yoffset) {
            Event event = new MouseScrollEvent(window, xoffset, yoffset);
            getGameManager().eventListAdd(event);
        }
    };


    public GLFWWindowSizeCallbackI windowSizeCallback =
            new GLFWWindowSizeCallbackI() {
                @Override
                public void invoke(long window, int width, int height) {
                    Event event = new WindowResizeEvent(window, width, height);
                    getGameManager().eventListAdd(event);
                }
            };

    public GLFWCharCallbackI charCallback = new GLFWCharCallbackI() {
        @Override
        public void invoke(long window, int codepoint) {
            Event event = new CharEvent(window, codepoint);
            getGameManager().eventListAdd(event);
        }
    };


    public SteamUserStatsCallback steamUserStatsCallback =
            new SteamUserStatsCallback() {

                @Override
                public void onUserStatsReceived(long gameId,
                                                SteamID steamIDUser,
                                                SteamResult result) {
                    //TODO
                }

                @Override
                public void onUserStatsStored(long gameId, SteamResult result) {
                    //TODO
                }

                @Override
                public void onUserStatsUnloaded(SteamID steamIDUser) {
                    //TODO
                }

                @Override
                public void onUserAchievementStored(long gameId,
                                                    boolean isGroupAchievement,
                                                    String achievementName,
                                                    int curProgress,
                                                    int maxProgress) {
                    //TODO
                }

                @Override
                public void onLeaderboardFindResult(SteamLeaderboardHandle leaderboard, boolean found) {
                    //TODO
                }

                @Override
                public void onLeaderboardScoresDownloaded(SteamLeaderboardHandle leaderboard,
                                                          SteamLeaderboardEntriesHandle entries, int numEntries) {
                    //TODO
                }

                @Override
                public void onLeaderboardScoreUploaded(boolean success,
                                                       SteamLeaderboardHandle leaderboard, int score,
                                                       boolean scoreChanged, int globalRankNew,
                                                       int globalRankPrevious) {
                    //TODO
                }

                @Override
                public void onGlobalStatsReceived(long gameId,
                                                  SteamResult result) {
                    //TODO
                }
            };


    public GameManager getGameManager() {
        return gameManager;
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }
}
