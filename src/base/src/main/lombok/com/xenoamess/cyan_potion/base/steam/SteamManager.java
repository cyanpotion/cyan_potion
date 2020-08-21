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
import com.xenoamess.cyan_potion.base.memory.ResourceInfo;
import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import com.xenoamess.cyan_potion.base.render.Texture;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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

import static com.codedisaster.steamworks.SteamNativeHandle.getNativeHandle;
import static com.xenoamess.cyan_potion.base.steam.SteamTextureUtils.*;

/**
 * This class learned a lot on com.codedisaster.steamworks.test.SteamClientAPITest
 * thanks for steamworks4j of code-disaster.
 * the original codes are follow MIT license under code-disaster, but that file does not contain license head.
 * you can go https://github.com/code-disaster/steamworks4j for more info about steamworks4j.
 *
 * @author XenoAmess
 * @version 0.162.3
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class SteamManager extends SubManager {
    @JsonIgnore
    private static final transient Logger LOGGER =
            LoggerFactory.getLogger(SteamManager.class);

    @Getter
    @Setter
    private SteamUser steamUser;

    @Getter
    @Setter
    private SteamUserStats steamUserStats;

    @Getter
    @Setter
    private SteamRemoteStorage steamRemoteStorage;

    @Getter
    @Setter
    private SteamUGC steamUGC;

    @Getter
    @Setter
    private SteamUtils steamUtils;

    @Getter
    @Setter
    private SteamApps steamApps;

    @Getter
    @Setter
    private SteamFriends steamFriends;

    @Getter
    @Setter
    private SteamLeaderboardHandle steamLeaderboardHandle = null;

    @Getter
    @Setter
    private SteamCallbacks steamCallbacks = new SteamCallbacks(this);

    /**
     * <p>Constructor for SteamManager.</p>
     *
     * @param gameManager a {@link com.xenoamess.cyan_potion.base.GameManager} object.
     */
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
    @Override
    public void init() {
        if (this.getGameManager().getDataCenter().getGameSettings().isRunWithSteam()) {
            this.renewSteam_appid();
            try {
                LOGGER.debug("[steam]Load native libraries ...");
                SteamAPI.loadLibraries();
                if (!SteamAPI.init()) {
                    try (StringWriter stringWriter = new StringWriter();
                         WriterOutputStream writerOutputStream = new WriterOutputStream(
                                 stringWriter,
                                 StandardCharsets.UTF_8
                         );
                         PrintStream printStream = new PrintStream(writerOutputStream)) {
                        SteamAPI.printDebugInfo(printStream);
                        printStream.flush();
                        writerOutputStream.flush();
                        stringWriter.flush();
                        String errorString = "[steam]Steamworks initialization error:" + stringWriter.toString();
                        LOGGER.error(errorString);
                        throw new SteamException(errorString);
                    } catch (IOException e) {
                        LOGGER.error("SteamManager.init() fails,", e);
                    }
                }


                LOGGER.debug("[steam]Register user ...");
                this.setSteamUser(new SteamUser(this.getSteamCallbacks().getSteamUserCallback()));

                LOGGER.debug("[steam]Register user stats callback ...");
                this.setSteamUserStats(new SteamUserStats(this.getSteamCallbacks().getSteamUserStatsCallback()));

                LOGGER.debug("[steam]Register remote storage ...");
                setSteamRemoteStorage(new SteamRemoteStorage(this.getSteamCallbacks().getSteamRemoteStorageCallback()));

                LOGGER.debug("[steam]Register UGC ...");
                setSteamUGC(new SteamUGC(this.getSteamCallbacks().getSteamUGCCallback()));

                LOGGER.debug("[steam]Register Utils ...");
                setSteamUtils(new SteamUtils(this.getSteamCallbacks().getSteamUtilsCallback()));
                getSteamUtils().setWarningMessageHook(this.getSteamCallbacks().getSteamAPIWarningMessageHook());

                LOGGER.debug("[steam]Register Apps ...");
                setSteamApps(new SteamApps());

                LOGGER.debug("[steam]Register Friends ...");
                setSteamFriends(new SteamFriends(this.getSteamCallbacks().getSteamFriendsCallback()));


                LOGGER.debug("[steam]Local user account ID: " + getSteamUser().getSteamID().getAccountID());
                LOGGER.debug("[steam]Local user steam ID: " + SteamID.getNativeHandle(getSteamUser().getSteamID()));
                LOGGER.debug("[steam]Local user friends name: " + getSteamFriends().getPersonaName());
                LOGGER.debug("[steam]App ID: " + getSteamUtils().getAppID());

                LOGGER.debug("[steam]App build ID: " + getSteamApps().getAppBuildId());
                LOGGER.debug("[steam]App owner: " + getSteamApps().getAppOwner().getAccountID());

                LOGGER.debug("[steam]Current game language: " + getSteamApps().getCurrentGameLanguage());
                LOGGER.debug("[steam]Available game languages: " + getSteamApps().getAvailableGameLanguages());

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


        long steamRunCallbacksNanoLong =
                this.getGameManager().getDataCenter().getGameSettings().getSteamRunCallbacksNanoLong();
        if (this.isRunWithSteam()) {
            this.getGameManager().getScheduledExecutorService().scheduleAtFixedRate(
                    this::steamRunCallbacks,
                    0,
                    steamRunCallbacksNanoLong, TimeUnit.NANOSECONDS);
        }
    }

    private static void shutIfNotAllowRunWithoutSteam() {
        if (DataCenter.ALLOW_RUN_WITHOUT_STEAM) {
            LOGGER.warn("[steam]Steam load failed but somehow we cannot prevent " +
                    "you from playing it.");
        } else {
            LOGGER.error("[steam]Steam load failed, thus the game shut.");
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
                 PrintWriter printWriter = new PrintWriter(outputStream)
            ) {
                printWriter.write(steam_appid.trim());
            } catch (IOException e) {
                LOGGER.error("[steam]write to steam_appid.txt failed!", e);
            }
        } else {
            try {
                steam_appid = steam_appidFileObject.getContent().getString(StandardCharsets.UTF_8).trim();
            } catch (IOException e) {
                LOGGER.error("[steam]read from steam_appid.txt failed! If you are not using steam, please set " +
                        "runWithSteam=false in common setting.", e);
            }
        }
        if (StringUtils.isBlank(steam_appid)) {
            LOGGER.error("[steam]OMG, steam_appid is still empty??? I really suggest you go check steam_works " +
                    "documents about [steam_appid.txt]'s format.");
        }
    }

    /**
     * if this game is now running with steam.
     * <p>
     * notice that this function shall never be called before call this.init
     *
     * @return this.getGameManager().getDataCenter().getGameSettings().isRunWithSteam() AND SteamAPI.isSteamRunning();
     */
    public boolean isRunWithSteam() {
        return this.getGameManager().getDataCenter().getGameSettings().isRunWithSteam() && SteamAPI.isSteamRunning();
    }

    /**
     * <p>steamRunCallbacks.</p>
     */
    protected void steamRunCallbacks() {
        if (isRunWithSteam()) {
            SteamAPI.runCallbacks();
        } else {
            if (!DataCenter.ALLOW_RUN_WITHOUT_STEAM) {
                LOGGER.error("[steam]SteamAPI.isSteamRunning() fails thus shut down this game.");
                this.getGameManager().shutdown();
            }
        }
    }

    /**
     * <p>update.</p>
     */
    @Override
    public void update() {
        //do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        if (getSteamUser() != null) {
            getSteamUser().dispose();
        }
        if (getSteamUserStats() != null) {
            getSteamUserStats().dispose();
        }
        if (getSteamRemoteStorage() != null) {
            getSteamRemoteStorage().dispose();
        }
        if (getSteamUGC() != null) {
            getSteamUGC().dispose();
        }
        if (getSteamUtils() != null) {
            getSteamUtils().dispose();
        }
        if (getSteamApps() != null) {
            getSteamApps().dispose();
        }
        if (getSteamFriends() != null) {
            getSteamFriends().dispose();
        }
        SteamAPI.shutdown();
    }


    /**
     * This function is remained here as a hint of how to deal with steamworks4j.
     * of course this function is from steamworks4j.
     *
     * @param input a {@link java.lang.String} object.
     */
    @SuppressWarnings("unused")
    protected void processInput(String input) {

        if (input.startsWith("stats global ")) {
            String[] cmd = input.substring("stats global ".length()).split(" ");
            if (cmd.length > 0) {
                if ("request".equals(cmd[0])) {
                    int days = 0;
                    if (cmd.length > 1) {
                        days = Integer.parseInt(cmd[1]);
                    }
                    getSteamUserStats().requestGlobalStats(days);
                } else if ("lget".equals(cmd[0]) && cmd.length > 1) {
                    int days = 0;
                    if (cmd.length > 2) {
                        days = Integer.parseInt(cmd[2]);
                    }
                    if (days == 0) {
                        long value = getSteamUserStats().getGlobalStat(cmd[1], -1);
                        LOGGER.debug("global stat (L) '" + cmd[1] + "' = " + value);
                    } else {
                        long[] data = new long[days];
                        int count = getSteamUserStats().getGlobalStatHistory(cmd[1], data);
                        System.out.print("global stat history (L) for " + count + " of " + days + " days:");
                        for (int i = 0; i < count; i++) {
                            System.out.print(" " + data[i]);
                        }
                        LOGGER.debug("");
                    }
                } else if ("dget".equals(cmd[0]) && cmd.length > 1) {
                    int days = 0;
                    if (cmd.length > 2) {
                        days = Integer.parseInt(cmd[2]);
                    }
                    if (days == 0) {
                        double value = getSteamUserStats().getGlobalStat(cmd[1], -1.0);
                        LOGGER.debug("global stat (D) '" + cmd[1] + "' = " + value);
                    } else {
                        double[] data = new double[days];
                        int count = getSteamUserStats().getGlobalStatHistory(cmd[1], data);
                        System.out.print("global stat history (D) for " + count + " of " + days + " days:");
                        for (int i = 0; i < count; i++) {
                            System.out.print(" " + data[i]);
                        }
                        LOGGER.debug("");
                    }
                }
            }
        } else if ("stats request".equals(input)) {
            getSteamUserStats().requestCurrentStats();
        } else if ("stats store".equals(input)) {
            getSteamUserStats().storeStats();
        } else if (input.startsWith("achievement set ")) {
            String achievementName = input.substring("achievement set ".length());
            LOGGER.debug("- setting " + achievementName + " to 'achieved'");
            getSteamUserStats().setAchievement(achievementName);
        } else if (input.startsWith("achievement clear ")) {
            String achievementName = input.substring("achievement clear ".length());
            LOGGER.debug("- clearing " + achievementName);
            getSteamUserStats().clearAchievement(achievementName);
        } else if ("file list".equals(input)) {
            int numFiles = getSteamRemoteStorage().getFileCount();
            LOGGER.debug("Num of files: " + numFiles);

            for (int i = 0; i < numFiles; i++) {
                int[] sizes = new int[1];
                String file = getSteamRemoteStorage().getFileNameAndSize(i, sizes);
                boolean exists = getSteamRemoteStorage().fileExists(file);
                LOGGER.debug("# " + i + " : name=" + file + ", size=" + sizes[0] + ", exists=" + (exists ? "yes" :
                        "no"));
            }
        } else if (input.startsWith("file write ")) {
            String path = input.substring("file write ".length());
            File file = new File(path);
            try (FileInputStream in = new FileInputStream(file)) {
                SteamUGCFileWriteStreamHandle remoteFile = getSteamRemoteStorage().fileWriteStreamOpen(path);
                if (remoteFile != null) {
                    byte[] bytes = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(bytes, 0, bytes.length)) > 0) {
                        ByteBuffer buffer = ByteBuffer.allocateDirect(bytesRead);
                        buffer.put(bytes, 0, bytesRead);
                        buffer.flip();
                        getSteamRemoteStorage().fileWriteStreamWriteChunk(remoteFile, buffer);
                    }
                    getSteamRemoteStorage().fileWriteStreamClose(remoteFile);
                }
            } catch (IOException e) {
                LOGGER.error("SteamManager.processInput(String input) fails,{}", input, e);
            }
        } else if (input.startsWith("file delete ")) {
            String path = input.substring("file delete ".length());
            if (getSteamRemoteStorage().fileDelete(path)) {
                LOGGER.debug("deleted file '" + path + "'");
            }
        } else if (input.startsWith("file share ")) {
            getSteamRemoteStorage().fileShare(input.substring("file share ".length()));
        } else if (input.startsWith("file publish ")) {
            String[] paths = input.substring("file publish ".length()).split(" ");
            if (paths.length >= 2) {
                LOGGER.debug("publishing file: " + paths[0] + ", preview file: " + paths[1]);
                getSteamRemoteStorage().publishWorkshopFile(paths[0], paths[1], getSteamUtils().getAppID(),
                        "Test UGC!", "Dummy UGC file published by test application.",
                        SteamRemoteStorage.PublishedFileVisibility.Private, null,
                        SteamRemoteStorage.WorkshopFileType.Community);
            }
        } else if (input.startsWith("file republish ")) {
            String[] paths = input.substring("file republish ".length()).split(" ");
            if (paths.length >= 3) {
                LOGGER.debug("republishing id: " + paths[0] + ", file: " + paths[1] + ", preview file: " + paths[2]);

                SteamPublishedFileID fileID = new SteamPublishedFileID(Long.parseLong(paths[0]));

                SteamPublishedFileUpdateHandle updateHandle =
                        getSteamRemoteStorage().createPublishedFileUpdateRequest(fileID);
                if (updateHandle != null) {
                    getSteamRemoteStorage().updatePublishedFileFile(updateHandle, paths[1]);
                    getSteamRemoteStorage().updatePublishedFilePreviewFile(updateHandle, paths[2]);
                    getSteamRemoteStorage().updatePublishedFileTitle(updateHandle, "Updated Test UGC!");
                    getSteamRemoteStorage().updatePublishedFileDescription(updateHandle, "Dummy UGC file *updated* by" +
                            " test application.");
                    getSteamRemoteStorage().commitPublishedFileUpdate(updateHandle);
                }
            }
        } else if ("ugc query".equals(input)) {
            SteamUGCQuery query = getSteamUGC().createQueryUserUGCRequest(getSteamUser().getSteamID().getAccountID(),
                    SteamUGC.UserUGCList.Subscribed,
                    SteamUGC.MatchingUGCType.UsableInGame, SteamUGC.UserUGCListSortOrder.TitleAsc,
                    getSteamUtils().getAppID(), getSteamUtils().getAppID(), 1);

            if (query.isValid()) {
                LOGGER.debug("sending UGC query: " + query.toString());
                //ugc.setReturnTotalOnly(query, true);
                getSteamUGC().sendQueryUGCRequest(query);
            }
        } else if (input.startsWith("ugc download ")) {
            String name = input.substring("ugc download ".length());
            SteamUGCHandle handle = new SteamUGCHandle(Long.parseLong(name, 16));
            getSteamRemoteStorage().ugcDownload(handle, 0);
        } else if (input.startsWith("ugc subscribe ")) {
            Long id = Long.parseLong(input.substring("ugc subscribe ".length()), 16);
            getSteamUGC().subscribeItem(new SteamPublishedFileID(id));
        } else if (input.startsWith("ugc unsubscribe ")) {
            Long id = Long.parseLong(input.substring("ugc unsubscribe ".length()), 16);
            getSteamUGC().unsubscribeItem(new SteamPublishedFileID(id));
        } else if (input.startsWith("ugc state ")) {
            long id = Long.parseLong(input.substring("ugc state ".length()), 16);
            Collection<SteamUGC.ItemState> itemStates = getSteamUGC().getItemState(new SteamPublishedFileID(id));
            LOGGER.debug("UGC item states: " + itemStates.size());
            for (SteamUGC.ItemState itemState : itemStates) {
                LOGGER.debug("  " + itemState.name());
            }
        } else if (input.startsWith("ugc details ")) {
            LOGGER.debug("requesting UGC details (deprecated API call)");
            long id = Long.parseLong(input.substring("ugc details ".length()), 16);
            getSteamUGC().requestUGCDetails(new SteamPublishedFileID(id), 0);

            SteamUGCQuery query = getSteamUGC().createQueryUGCDetailsRequest(new SteamPublishedFileID(id));
            if (query.isValid()) {
                LOGGER.debug("sending UGC details query: " + query.toString());
                getSteamUGC().sendQueryUGCRequest(query);
            }
        } else if (input.startsWith("ugc info ")) {
            long id = Long.parseLong(input.substring("ugc info ".length()), 16);
            SteamUGC.ItemInstallInfo installInfo = new SteamUGC.ItemInstallInfo();
            if (getSteamUGC().getItemInstallInfo(new SteamPublishedFileID(id), installInfo)) {
                LOGGER.debug("  folder: " + installInfo.getFolder());
                LOGGER.debug("  size on disk: " + installInfo.getSizeOnDisk());
            }
            SteamUGC.ItemDownloadInfo downloadInfo = new SteamUGC.ItemDownloadInfo();
            if (getSteamUGC().getItemDownloadInfo(new SteamPublishedFileID(id), downloadInfo)) {
                LOGGER.debug("  bytes downloaded: " + downloadInfo.getBytesDownloaded());
                LOGGER.debug("  bytes total: " + downloadInfo.getBytesTotal());
            }
        } else if (input.startsWith("leaderboard find ")) {
            String name = input.substring("leaderboard find ".length());
            getSteamUserStats().findLeaderboard(name);
        } else if (input.startsWith("leaderboard list ")) {
            String[] params = input.substring("leaderboard list ".length()).split(" ");
            if (getSteamLeaderboardHandle() != null && params.length >= 2) {
                getSteamUserStats().downloadLeaderboardEntries(getSteamLeaderboardHandle(),
                        SteamUserStats.LeaderboardDataRequest.Global,
                        Integer.parseInt(params[0]), Integer.parseInt(params[1]));
            }
        } else if (input.startsWith("leaderboard users ")) {
            String[] params = input.substring("leaderboard users ".length()).split(" ");
            if (getSteamLeaderboardHandle() != null && params.length > 0) {
                SteamID[] users = new SteamID[params.length];
                for (int i = 0; i < params.length; i++) {
                    users[i] = SteamID.createFromNativeHandle(Long.parseLong(params[i]));
                }
                getSteamUserStats().downloadLeaderboardEntriesForUsers(getSteamLeaderboardHandle(), users);
            }
        } else if (input.startsWith("leaderboard score ")) {
            String score = input.substring("leaderboard score ".length());
            if (getSteamLeaderboardHandle() != null) {
                LOGGER.debug("uploading score " + score + " to leaderboard " + getSteamLeaderboardHandle().toString());
                getSteamUserStats().uploadLeaderboardScore(getSteamLeaderboardHandle(),
                        SteamUserStats.LeaderboardUploadScoreMethod.KeepBest, Integer.parseInt(score), new int[]{});
            }
        } else if (input.startsWith("apps subscribed ")) {
            String appId = input.substring("apps subscribed ".length());
            boolean subscribed = getSteamApps().isSubscribedApp(Integer.parseInt(appId));
            LOGGER.debug("user described to app #" + appId + ": " + (subscribed ? "yes" : "no"));
        }
    }

    /**
     * <p>getPlayerAvatarTextureLarge.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.render.Texture} object.
     */
    public Texture getPlayerAvatarTextureLarge() {
        return getPlayerAvatarTexture(STRING_LARGE);
    }

    /**
     * <p>getPlayerAvatarTextureMedium.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.render.Texture} object.
     */
    @SuppressWarnings("unused")
    public Texture getPlayerAvatarTextureMedium() {
        return getPlayerAvatarTexture(STRING_MEDIUM);
    }

    /**
     * <p>getPlayerAvatarTextureSmall.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.render.Texture} object.
     */
    @SuppressWarnings("unused")
    public Texture getPlayerAvatarTextureSmall() {
        return getPlayerAvatarTexture(STRING_SMALL);
    }

    /**
     * <p>getPlayerAvatarTexture.</p>
     *
     * @param avatarType can only be one of "small", "medium" and "large".
     * @return texture.
     */
    public Texture getPlayerAvatarTexture(String avatarType) {
        SteamID steamID;
        if (this.isRunWithSteam()) {
            steamID = this.getSteamUser().getSteamID();
        } else {
            steamID = null;
        }
        return this.getUserAvatarTexture(steamID, avatarType);
    }

    /**
     * <p>getUserAvatarTexture.</p>
     *
     * @param steamID    a {@link com.codedisaster.steamworks.SteamID} object.
     * @param avatarType can only be one of "small", "medium" and "large".
     * @return texture
     */
    public Texture getUserAvatarTexture(SteamID steamID, String avatarType) {
        String steamIDhandleString;
        if (this.isRunWithSteam()) {
            steamIDhandleString = Long.toString(getNativeHandle(steamID));
        } else {
            steamIDhandleString = "";
        }
        return new ResourceInfo<>(
                Texture.class,
                STRING_STEAM_AVATAR,
                "",
                steamIDhandleString,
                avatarType
        ).fetchResource(this.getGameManager().getResourceManager());
    }

    @SuppressWarnings("unused")
    public String getPlayerName() {
        return this.getSteamFriends().getPersonaName();
    }

    @SuppressWarnings("unused")
    public String getUserName(SteamID steamID) {
        return this.getSteamFriends().getFriendPersonaName(steamID);
    }
}
