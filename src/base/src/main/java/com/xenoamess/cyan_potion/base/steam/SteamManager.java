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

package com.xenoamess.cyan_potion.base.steam;

import com.codedisaster.steamworks.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xenoamess.cyan_potion.base.DataCenter;
import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.SubManager;
import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import org.apache.commons.io.output.WriterOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * This class learned a lot on com.codedisaster.steamworks.test.SteamClientAPITest
 * thanks for steamworks4j of code-disaster.
 * the original codes are follow MIT license under code-disaster, but that file does not contain license head.
 * you can go https://github.com/code-disaster/steamworks4j for more info about steamworks4j.
 */
public class SteamManager extends SubManager {
    @JsonIgnore
    private static transient final Logger LOGGER =
            LoggerFactory.getLogger(SteamManager.class);

    private SteamUser user;
    private SteamUserStats userStats;
    private SteamRemoteStorage remoteStorage;
    private SteamUGC ugc;
    private SteamUtils utils;
    private SteamApps apps;
    private SteamFriends friends;
    private SteamUtils clientUtils;
    private SteamLeaderboardHandle currentLeaderboard = null;

    private SteamCallbacks steamCallbacks = new SteamCallbacks(this);

    public SteamManager(GameManager gameManager) {
        super(gameManager);
    }

    /**
     * Load runWithSteam from CommonSettings.
     * if runWithSteam == true,
     * -- try load steamAPI.
     * -- if succeed
     * ---- then start game, and set RunWithSteam be true.
     * -- else
     * ---- if DataCenter.ALLOW_RUN_WITHOUT_STEAM is true
     * ------ then still start the game but RunWithSteam will be false.
     * ---- else
     * ------ exit 1
     * else,
     * -- if DataCenter.ALLOW_RUN_WITHOUT_STEAM is true,
     * ---- then still start the game but RunWithSteam will be false.
     * -- else
     * ---- exit 1
     */
    public void init() {
        if (this.getGameManager().getDataCenter().getGameSettings().isRunWithSteam()) {
            this.renewSteam_appid();
            try {
                LOGGER.debug("[steam]Load native libraries ...");
                SteamAPI.loadLibraries();
                if (!SteamAPI.init()) {
                    try (StringWriter stringWriter = new StringWriter();
                         WriterOutputStream writerOutputStream = new WriterOutputStream(stringWriter, StandardCharsets.UTF_8);
                         PrintStream printStream = new PrintStream(writerOutputStream)) {
                        SteamAPI.printDebugInfo(printStream);
                        printStream.flush();
                        writerOutputStream.flush();
                        stringWriter.flush();
                        String errorString = "[steam]Steamworks initialization error:" + stringWriter.toString();
                        LOGGER.error(errorString);
                        throw new SteamException(errorString);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                LOGGER.debug("[steam]Register user ...");
                this.setUser(new SteamUser(this.getSteamCallbacks().getUserCallback()));

                LOGGER.debug("[steam]Register user stats callback ...");
                this.setUserStats(new SteamUserStats(this.getSteamCallbacks().getSteamUserStatsCallback()));

                LOGGER.debug("[steam]Register remote storage ...");
                setRemoteStorage(new SteamRemoteStorage(this.getSteamCallbacks().getRemoteStorageCallback()));

                LOGGER.debug("[steam]Register UGC ...");
                setUgc(new SteamUGC(this.getSteamCallbacks().getUgcCallback()));

                LOGGER.debug("[steam]Register Utils ...");
                setUtils(new SteamUtils(this.getSteamCallbacks().getUtilsCallback()));

                LOGGER.debug("[steam]Register Apps ...");
                setApps(new SteamApps());

                LOGGER.debug("[steam]Register Friends ...");
                setFriends(new SteamFriends(this.getSteamCallbacks().getFriendsCallback()));

                setClientUtils(new SteamUtils(this.getSteamCallbacks().getClUtilsCallback()));
                getClientUtils().setWarningMessageHook(this.getSteamCallbacks().getClMessageHook());

                LOGGER.debug("Local user account ID: " + getUser().getSteamID().getAccountID());
                LOGGER.debug("Local user steam ID: " + SteamID.getNativeHandle(getUser().getSteamID()));
                LOGGER.debug("Local user friends name: " + getFriends().getPersonaName());
                LOGGER.debug("App ID: " + getUtils().getAppID());

                LOGGER.debug("App build ID: " + getApps().getAppBuildId());
                LOGGER.debug("App owner: " + getApps().getAppOwner().getAccountID());

                LOGGER.debug("Current game language: " + getApps().getCurrentGameLanguage());
                LOGGER.debug("Available game languages: " + getApps().getAvailableGameLanguages());

                this.getGameManager().getDataCenter().getGameSettings().setRunWithSteam(true);
            } catch (SteamException e) {
                // Error extracting or loading native libraries
                this.getGameManager().getDataCenter().getGameSettings().setRunWithSteam(false);
                LOGGER.warn("[steam]SteamAPI.init() fails", e);
                shutIfNotAllowRunWithoutSteam();
            }
        } else {
            shutIfNotAllowRunWithoutSteam();
        }


        long steamRunCallbacksNanoLong = this.getGameManager().getDataCenter().getGameSettings().getSteamRunCallbacksNanoLong();
        if (this.getGameManager().getDataCenter().getGameSettings().isRunWithSteam()) {
            this.getGameManager().getScheduledExecutorService().scheduleAtFixedRate(
                    this::steamRunCallbacks,
                    0,
                    steamRunCallbacksNanoLong, TimeUnit.NANOSECONDS);
        }
    }

    private static void shutIfNotAllowRunWithoutSteam() {
        if (DataCenter.ALLOW_RUN_WITHOUT_STEAM) {
            LOGGER.warn("Steam load failed but somehow we cannot prevent " +
                    "you from playing it.");
        } else {
            LOGGER.error("Steam load failed, thus the game shut.");
            System.exit(1);
        }
    }

    /**
     * renew steam_appid.txt
     * notice that this function shall only be invoked when runWithSteam=true
     * (runWithSteam is false by default)
     */
    protected void renewSteam_appid() {
        String steam_appid = this.getGameManager().getDataCenter().getGameSettings().getSteam_appid();
        FileObject steam_appidFileObject = ResourceManager.resolveFile("steam_appid.txt");
        if (!StringUtils.isBlank(steam_appid)) {
            try (OutputStream outputStream = steam_appidFileObject.getContent().getOutputStream();
                 PrintWriter printWriter = new PrintWriter(outputStream);
            ) {
                printWriter.write(steam_appid.trim());
            } catch (IOException e) {
                LOGGER.error("write to steam_appid.txt failed!", e);
            }
        } else {
            try {
                steam_appid = steam_appidFileObject.getContent().getString(StandardCharsets.UTF_8).trim();
            } catch (IOException e) {
                LOGGER.error("read from steam_appid.txt failed! If you are not using steam, please set runWithSteam=false in common setting.", e);
            }
        }
        if (StringUtils.isBlank(steam_appid)) {
            LOGGER.error("OMG, steam_appid is still empty??? I really suggest you go check steam_works documents about [steam_appid.txt]'s format.");
        }
    }

    /**
     * <p>steamRunCallbacks.</p>
     */
    protected void steamRunCallbacks() {
        if (this.getGameManager().getDataCenter().getGameSettings().isRunWithSteam() && SteamAPI.isSteamRunning()) {
            SteamAPI.runCallbacks();
        } else {
            if (!DataCenter.ALLOW_RUN_WITHOUT_STEAM) {
                LOGGER.error("SteamAPI.isSteamRunning() fails thus shut down this game.");
                this.getGameManager().shutdown();
            }
        }
    }

    public void update() {

    }

    @Override
    public void close() {
        if (getClientUtils() != null) {
            getClientUtils().dispose();
        }
        if (getUser() != null) {
            getUser().dispose();
        }
        if (getUserStats() != null) {
            getUserStats().dispose();
        }
        if (getRemoteStorage() != null) {
            getRemoteStorage().dispose();
        }
        if (getUgc() != null) {
            getUgc().dispose();
        }
        if (getUtils() != null) {
            getUtils().dispose();
        }
        if (getApps() != null) {
            getApps().dispose();
        }
        if (getFriends() != null) {
            getFriends().dispose();
        }
        SteamAPI.shutdown();
    }


    /**
     * This function is remained here as a hint of how to deal with steamworks4j.
     * of course this function is from steamworks4j.
     *
     * @param input
     * @throws SteamException
     */
    protected void processInput(String input) throws SteamException {

        if (input.startsWith("stats global ")) {
            String[] cmd = input.substring("stats global ".length()).split(" ");
            if (cmd.length > 0) {
                if (cmd[0].equals("request")) {
                    int days = 0;
                    if (cmd.length > 1) {
                        days = Integer.parseInt(cmd[1]);
                    }
                    getUserStats().requestGlobalStats(days);
                } else if (cmd[0].equals("lget") && cmd.length > 1) {
                    int days = 0;
                    if (cmd.length > 2) {
                        days = Integer.parseInt(cmd[2]);
                    }
                    if (days == 0) {
                        long value = getUserStats().getGlobalStat(cmd[1], -1);
                        LOGGER.debug("global stat (L) '" + cmd[1] + "' = " + value);
                    } else {
                        long[] data = new long[days];
                        int count = getUserStats().getGlobalStatHistory(cmd[1], data);
                        System.out.print("global stat history (L) for " + count + " of " + days + " days:");
                        for (int i = 0; i < count; i++) {
                            System.out.print(" " + Long.toString(data[i]));
                        }
                        LOGGER.debug("");
                    }
                } else if (cmd[0].equals("dget") && cmd.length > 1) {
                    int days = 0;
                    if (cmd.length > 2) {
                        days = Integer.parseInt(cmd[2]);
                    }
                    if (days == 0) {
                        double value = getUserStats().getGlobalStat(cmd[1], -1.0);
                        LOGGER.debug("global stat (D) '" + cmd[1] + "' = " + value);
                    } else {
                        double[] data = new double[days];
                        int count = getUserStats().getGlobalStatHistory(cmd[1], data);
                        System.out.print("global stat history (D) for " + count + " of " + days + " days:");
                        for (int i = 0; i < count; i++) {
                            System.out.print(" " + Double.toString(data[i]));
                        }
                        LOGGER.debug("");
                    }
                }
            }
        } else if (input.equals("stats request")) {
            getUserStats().requestCurrentStats();
        } else if (input.equals("stats store")) {
            getUserStats().storeStats();
        } else if (input.startsWith("achievement set ")) {
            String achievementName = input.substring("achievement set ".length());
            LOGGER.debug("- setting " + achievementName + " to 'achieved'");
            getUserStats().setAchievement(achievementName);
        } else if (input.startsWith("achievement clear ")) {
            String achievementName = input.substring("achievement clear ".length());
            LOGGER.debug("- clearing " + achievementName);
            getUserStats().clearAchievement(achievementName);
        } else if (input.equals("file list")) {
            int numFiles = getRemoteStorage().getFileCount();
            LOGGER.debug("Num of files: " + numFiles);

            for (int i = 0; i < numFiles; i++) {
                int[] sizes = new int[1];
                String file = getRemoteStorage().getFileNameAndSize(i, sizes);
                boolean exists = getRemoteStorage().fileExists(file);
                LOGGER.debug("# " + i + " : name=" + file + ", size=" + sizes[0] + ", exists=" + (exists ? "yes" : "no"));
            }
        } else if (input.startsWith("file write ")) {
            String path = input.substring("file write ".length());
            File file = new File(path);
            try (FileInputStream in = new FileInputStream(file)) {
                SteamUGCFileWriteStreamHandle remoteFile = getRemoteStorage().fileWriteStreamOpen(path);
                if (remoteFile != null) {
                    byte[] bytes = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(bytes, 0, bytes.length)) > 0) {
                        ByteBuffer buffer = ByteBuffer.allocateDirect(bytesRead);
                        buffer.put(bytes, 0, bytesRead);
                        buffer.flip();
                        getRemoteStorage().fileWriteStreamWriteChunk(remoteFile, buffer);
                    }
                    getRemoteStorage().fileWriteStreamClose(remoteFile);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (input.startsWith("file delete ")) {
            String path = input.substring("file delete ".length());
            if (getRemoteStorage().fileDelete(path)) {
                LOGGER.debug("deleted file '" + path + "'");
            }
        } else if (input.startsWith("file share ")) {
            getRemoteStorage().fileShare(input.substring("file share ".length()));
        } else if (input.startsWith("file publish ")) {
            String[] paths = input.substring("file publish ".length()).split(" ");
            if (paths.length >= 2) {
                LOGGER.debug("publishing file: " + paths[0] + ", preview file: " + paths[1]);
                getRemoteStorage().publishWorkshopFile(paths[0], paths[1], getUtils().getAppID(),
                        "Test UGC!", "Dummy UGC file published by test application.",
                        SteamRemoteStorage.PublishedFileVisibility.Private, null,
                        SteamRemoteStorage.WorkshopFileType.Community);
            }
        } else if (input.startsWith("file republish ")) {
            String[] paths = input.substring("file republish ".length()).split(" ");
            if (paths.length >= 3) {
                LOGGER.debug("republishing id: " + paths[0] + ", file: " + paths[1] + ", preview file: " + paths[2]);

                SteamPublishedFileID fileID = new SteamPublishedFileID(Long.parseLong(paths[0]));

                SteamPublishedFileUpdateHandle updateHandle = getRemoteStorage().createPublishedFileUpdateRequest(fileID);
                if (updateHandle != null) {
                    getRemoteStorage().updatePublishedFileFile(updateHandle, paths[1]);
                    getRemoteStorage().updatePublishedFilePreviewFile(updateHandle, paths[2]);
                    getRemoteStorage().updatePublishedFileTitle(updateHandle, "Updated Test UGC!");
                    getRemoteStorage().updatePublishedFileDescription(updateHandle, "Dummy UGC file *updated* by test application.");
                    getRemoteStorage().commitPublishedFileUpdate(updateHandle);
                }
            }
        } else if (input.equals("ugc query")) {
            SteamUGCQuery query = getUgc().createQueryUserUGCRequest(getUser().getSteamID().getAccountID(), SteamUGC.UserUGCList.Subscribed,
                    SteamUGC.MatchingUGCType.UsableInGame, SteamUGC.UserUGCListSortOrder.TitleAsc,
                    getUtils().getAppID(), getUtils().getAppID(), 1);

            if (query.isValid()) {
                LOGGER.debug("sending UGC query: " + query.toString());
                //ugc.setReturnTotalOnly(query, true);
                getUgc().sendQueryUGCRequest(query);
            }
        } else if (input.startsWith("ugc download ")) {
            String name = input.substring("ugc download ".length());
            SteamUGCHandle handle = new SteamUGCHandle(Long.parseLong(name, 16));
            getRemoteStorage().ugcDownload(handle, 0);
        } else if (input.startsWith("ugc subscribe ")) {
            Long id = Long.parseLong(input.substring("ugc subscribe ".length()), 16);
            getUgc().subscribeItem(new SteamPublishedFileID(id));
        } else if (input.startsWith("ugc unsubscribe ")) {
            Long id = Long.parseLong(input.substring("ugc unsubscribe ".length()), 16);
            getUgc().unsubscribeItem(new SteamPublishedFileID(id));
        } else if (input.startsWith("ugc state ")) {
            Long id = Long.parseLong(input.substring("ugc state ".length()), 16);
            Collection<SteamUGC.ItemState> itemStates = getUgc().getItemState(new SteamPublishedFileID(id));
            LOGGER.debug("UGC item states: " + itemStates.size());
            for (SteamUGC.ItemState itemState : itemStates) {
                LOGGER.debug("  " + itemState.name());
            }
        } else if (input.startsWith("ugc details ")) {
            LOGGER.debug("requesting UGC details (deprecated API call)");
            Long id = Long.parseLong(input.substring("ugc details ".length()), 16);
            getUgc().requestUGCDetails(new SteamPublishedFileID(id), 0);

            SteamUGCQuery query = getUgc().createQueryUGCDetailsRequest(new SteamPublishedFileID(id));
            if (query.isValid()) {
                LOGGER.debug("sending UGC details query: " + query.toString());
                getUgc().sendQueryUGCRequest(query);
            }
        } else if (input.startsWith("ugc info ")) {
            Long id = Long.parseLong(input.substring("ugc info ".length()), 16);
            SteamUGC.ItemInstallInfo installInfo = new SteamUGC.ItemInstallInfo();
            if (getUgc().getItemInstallInfo(new SteamPublishedFileID(id), installInfo)) {
                LOGGER.debug("  folder: " + installInfo.getFolder());
                LOGGER.debug("  size on disk: " + installInfo.getSizeOnDisk());
            }
            SteamUGC.ItemDownloadInfo downloadInfo = new SteamUGC.ItemDownloadInfo();
            if (getUgc().getItemDownloadInfo(new SteamPublishedFileID(id), downloadInfo)) {
                LOGGER.debug("  bytes downloaded: " + downloadInfo.getBytesDownloaded());
                LOGGER.debug("  bytes total: " + downloadInfo.getBytesTotal());
            }
        } else if (input.startsWith("leaderboard find ")) {
            String name = input.substring("leaderboard find ".length());
            getUserStats().findLeaderboard(name);
        } else if (input.startsWith("leaderboard list ")) {
            String[] params = input.substring("leaderboard list ".length()).split(" ");
            if (getCurrentLeaderboard() != null && params.length >= 2) {
                getUserStats().downloadLeaderboardEntries(getCurrentLeaderboard(),
                        SteamUserStats.LeaderboardDataRequest.Global,
                        Integer.parseInt(params[0]), Integer.parseInt(params[1]));
            }
        } else if (input.startsWith("leaderboard users ")) {
            String[] params = input.substring("leaderboard users ".length()).split(" ");
            if (getCurrentLeaderboard() != null && params.length > 0) {
                SteamID[] users = new SteamID[params.length];
                for (int i = 0; i < params.length; i++) {
                    users[i] = SteamID.createFromNativeHandle(Long.parseLong(params[i]));
                }
                getUserStats().downloadLeaderboardEntriesForUsers(getCurrentLeaderboard(), users);
            }
        } else if (input.startsWith("leaderboard score ")) {
            String score = input.substring("leaderboard score ".length());
            if (getCurrentLeaderboard() != null) {
                LOGGER.debug("uploading score " + score + " to leaderboard " + getCurrentLeaderboard().toString());
                getUserStats().uploadLeaderboardScore(getCurrentLeaderboard(),
                        SteamUserStats.LeaderboardUploadScoreMethod.KeepBest, Integer.parseInt(score), new int[]{});
            }
        } else if (input.startsWith("apps subscribed ")) {
            String appId = input.substring("apps subscribed ".length());
            boolean subscribed = getApps().isSubscribedApp(Integer.parseInt(appId));
            LOGGER.debug("user described to app #" + appId + ": " + (subscribed ? "yes" : "no"));
        }
    }

    public SteamUser getUser() {
        return user;
    }

    public void setUser(SteamUser user) {
        this.user = user;
    }

    public SteamUserStats getUserStats() {
        return userStats;
    }

    public void setUserStats(SteamUserStats userStats) {
        this.userStats = userStats;
    }

    public SteamRemoteStorage getRemoteStorage() {
        return remoteStorage;
    }

    public void setRemoteStorage(SteamRemoteStorage remoteStorage) {
        this.remoteStorage = remoteStorage;
    }

    public SteamUGC getUgc() {
        return ugc;
    }

    public void setUgc(SteamUGC ugc) {
        this.ugc = ugc;
    }

    public SteamUtils getUtils() {
        return utils;
    }

    public void setUtils(SteamUtils utils) {
        this.utils = utils;
    }

    public SteamApps getApps() {
        return apps;
    }

    public void setApps(SteamApps apps) {
        this.apps = apps;
    }

    public SteamFriends getFriends() {
        return friends;
    }

    public void setFriends(SteamFriends friends) {
        this.friends = friends;
    }

    public SteamUtils getClientUtils() {
        return clientUtils;
    }

    public void setClientUtils(SteamUtils clientUtils) {
        this.clientUtils = clientUtils;
    }

    public SteamLeaderboardHandle getCurrentLeaderboard() {
        return currentLeaderboard;
    }

    public void setCurrentLeaderboard(SteamLeaderboardHandle currentLeaderboard) {
        this.currentLeaderboard = currentLeaderboard;
    }

    public SteamCallbacks getSteamCallbacks() {
        return steamCallbacks;
    }

    public void setSteamCallbacks(SteamCallbacks steamCallbacks) {
        this.steamCallbacks = steamCallbacks;
    }
}
