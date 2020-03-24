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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;


/**
 * This class learned a lot on com.codedisaster.steamworks.test.SteamClientAPITest
 * thanks for steamworks4j of code-disaster.
 * the original codes are follow MIT license under code-disaster, but that file does not contain license head.
 * you can go https://github.com/code-disaster/steamworks4j for more info about steamworks4j.
 *
 * @author XenoAmess
 * @version 0.162.1-SNAPSHOT
 */
@EqualsAndHashCode
@ToString
public class SteamCallbacks {
    @JsonIgnore
    private static final transient Logger LOGGER =
            LoggerFactory.getLogger(SteamCallbacks.class);
    @Getter
    @Setter
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private final SteamManager steamManager;

    /**
     * <p>Constructor for Callbacks.</p>
     *
     * @param steamManager steamManager
     */
    public SteamCallbacks(SteamManager steamManager) {
        this.steamManager = steamManager;
    }

    @Getter
    @Setter
    private SteamUserCallback steamUserCallback = new SteamUserCallback() {

        @Override
        public void onValidateAuthTicket(SteamID steamID, SteamAuth.AuthSessionResponse authSessionResponse, SteamID ownerSteamID) {
            //do nothing
        }

        @Override
        public void onMicroTxnAuthorization(int appID, long orderID, boolean authorized) {
            //do nothing
        }

        @Override
        public void onEncryptedAppTicket(SteamResult result) {
            //do nothing
        }
    };

    @Getter
    @Setter
    private SteamUserStatsCallback steamUserStatsCallback = new SteamUserStatsCallback() {
        @Override
        public void onUserStatsReceived(long gameId, SteamID steamIDUser, SteamResult result) {
            LOGGER.debug("User stats received: gameId=" + gameId + ", userId=" + steamIDUser.getAccountID() +
                    ", result=" + result.toString());

            int numAchievements = getSteamManager().getSteamUserStats().getNumAchievements();
            LOGGER.debug("Num of achievements: " + numAchievements);

            for (int i = 0; i < numAchievements; i++) {
                String name = getSteamManager().getSteamUserStats().getAchievementName(i);
                boolean achieved = getSteamManager().getSteamUserStats().isAchieved(name, false);
                LOGGER.debug("# " + i + " : name=" + name + ", achieved=" + (achieved ? "yes" : "no"));
            }
        }

        @Override
        public void onUserStatsStored(long gameId, SteamResult result) {
            LOGGER.debug("User stats stored: gameId=" + gameId +
                    ", result=" + result.toString());
        }

        @Override
        public void onUserStatsUnloaded(SteamID steamIDUser) {
            LOGGER.debug("User stats unloaded: userId=" + steamIDUser.getAccountID());
        }

        @Override
        public void onUserAchievementStored(long gameId, boolean isGroupAchievement, String achievementName,
                                            int curProgress, int maxProgress) {
            LOGGER.debug("User achievement stored: gameId=" + gameId + ", name=" + achievementName +
                    ", progress=" + curProgress + "/" + maxProgress);
        }

        @Override
        public void onLeaderboardFindResult(SteamLeaderboardHandle leaderboard, boolean found) {
            LOGGER.debug("Leaderboard find result: handle=" + leaderboard.toString() +
                    ", found=" + (found ? "yes" : "no"));

            if (found) {
                LOGGER.debug("Leaderboard: name=" + getSteamManager().getSteamUserStats().getLeaderboardName(leaderboard) +
                        ", entries=" + getSteamManager().getSteamUserStats().getLeaderboardEntryCount(leaderboard));

                SteamCallbacks.this.getSteamManager().setSteamLeaderboardHandle(leaderboard);
            }
        }

        @Override
        public void onLeaderboardScoresDownloaded(SteamLeaderboardHandle leaderboard,
                                                  SteamLeaderboardEntriesHandle entries,
                                                  int numEntries) {

            LOGGER.debug("Leaderboard scores downloaded: handle=" + leaderboard.toString() +
                    ", entries=" + entries.toString() + ", count=" + numEntries);

            int[] details = new int[16];

            for (int i = 0; i < numEntries; i++) {

                SteamLeaderboardEntry entry = new SteamLeaderboardEntry();
                if (getSteamManager().getSteamUserStats().getDownloadedLeaderboardEntry(entries, i, entry, details)) {

                    int numDetails = entry.getNumDetails();

                    LOGGER.debug("Leaderboard entry #" + i +
                            ": accountID=" + entry.getSteamIDUser().getAccountID() +
                            ", globalRank=" + entry.getGlobalRank() +
                            ", score=" + entry.getScore() +
                            ", numDetails=" + numDetails);

                    for (int detail = 0; detail < numDetails; detail++) {
                        LOGGER.debug("  ... detail #" + detail + "=" + details[detail]);
                    }

                    if (getSteamManager().getSteamFriends().requestUserInformation(entry.getSteamIDUser(), false)) {
                        LOGGER.debug("  ... requested user information for entry");
                    } else {
                        LOGGER.debug("  ... user name is '" +
                                getSteamManager().getSteamFriends().getFriendPersonaName(entry.getSteamIDUser()) + "'");

                        int smallAvatar = getSteamManager().getSteamFriends().getSmallFriendAvatar(entry.getSteamIDUser());
                        if (smallAvatar != 0) {
                            int w = getSteamManager().getSteamUtils().getImageWidth(smallAvatar);
                            int h = getSteamManager().getSteamUtils().getImageHeight(smallAvatar);
                            LOGGER.debug("  ... small avatar size: " + w + "x" + h + " pixels");

                            ByteBuffer image = ByteBuffer.allocateDirect(w * h * 4);

                            try {
                                if (getSteamManager().getSteamUtils().getImageRGBA(smallAvatar, image)) {
                                    LOGGER.debug("  ... small avatar retrieve avatar image successful");

                                    int nonZeroes = w * h;
                                    for (int y = 0; y < h; y++) {
                                        for (int x = 0; x < w; x++) {
                                            //System.out.print(String.format(" %08x", image.getInt(y * w + x)));
                                            if (image.getInt(y * w + x) == 0) {
                                                nonZeroes--;
                                            }
                                        }
                                        //LOGGER.debug();
                                    }

                                    if (nonZeroes == 0) {
                                        LOGGER.error("Something's wrong! Avatar image is empty!");
                                    }

                                } else {
                                    LOGGER.debug("  ... small avatar retrieve avatar image failed!");
                                }
                            } catch (SteamException e) {
                                LOGGER.error("Something's wrong.", e);
                            }
                        } else {
                            LOGGER.debug("  ... small avatar image not available!");
                        }

                    }
                }

            }
        }

        @Override
        public void onLeaderboardScoreUploaded(boolean success,
                                               SteamLeaderboardHandle leaderboard,
                                               int score,
                                               boolean scoreChanged,
                                               int globalRankNew,
                                               int globalRankPrevious) {

            LOGGER.debug("Leaderboard score uploaded: " + (success ? "yes" : "no") +
                    ", handle=" + leaderboard.toString() +
                    ", score=" + score +
                    ", changed=" + (scoreChanged ? "yes" : "no") +
                    ", globalRankNew=" + globalRankNew +
                    ", globalRankPrevious=" + globalRankPrevious);
        }

        @Override
        public void onGlobalStatsReceived(long gameId, SteamResult result) {
            LOGGER.debug("Global stats received: gameId=" + gameId + ", result=" + result.toString());
        }
    };

    @Getter
    @Setter
    private SteamRemoteStorageCallback steamRemoteStorageCallback = new SteamRemoteStorageCallback() {
        @Override
        public void onFileWriteAsyncComplete(SteamResult result) {
            //do nothing
        }

        @Override
        public void onFileReadAsyncComplete(SteamAPICall fileReadAsync, SteamResult result, int offset, int read) {
            //do nothing
        }

        @Override
        public void onFileShareResult(SteamUGCHandle fileHandle, String fileName, SteamResult result) {
            LOGGER.debug("Remote storage file share result: handle='" + fileHandle.toString() +
                    ", name=" + fileName + "', result=" + result.toString());
        }

        @Override
        public void onDownloadUGCResult(SteamUGCHandle fileHandle, SteamResult result) {
            LOGGER.debug("Remote storage download UGC result: handle='" + fileHandle.toString() +
                    "', result=" + result.toString());

            ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
            int offset = 0, bytesRead;

            do {
                bytesRead = getSteamManager().getSteamRemoteStorage().ugcRead(fileHandle, buffer, buffer.limit(), offset,
                        SteamRemoteStorage.UGCReadAction.ContinueReadingUntilFinished);
                offset += bytesRead;
            } while (bytesRead > 0);

            LOGGER.debug("Read " + offset + " bytes from handle=" + fileHandle.toString());
        }

        @Override
        public void onPublishFileResult(SteamPublishedFileID publishedFileID, boolean needsToAcceptWLA, SteamResult result) {
            LOGGER.debug("Remote storage publish file result: publishedFileID=" + publishedFileID.toString() +
                    ", needsToAcceptWLA=" + needsToAcceptWLA + ", result=" + result.toString());
        }

        @Override
        public void onUpdatePublishedFileResult(SteamPublishedFileID publishedFileID, boolean needsToAcceptWLA, SteamResult result) {
            LOGGER.debug("Remote storage update published file result: publishedFileID=" + publishedFileID.toString() +
                    ", needsToAcceptWLA=" + needsToAcceptWLA + ", result=" + result.toString());
        }

        @Override
        public void onPublishedFileSubscribed(SteamPublishedFileID publishedFileID, int appID) {
            //do nothing
        }

        @Override
        public void onPublishedFileUnsubscribed(SteamPublishedFileID publishedFileID, int appID) {
            //do nothing
        }

        @Override
        public void onPublishedFileDeleted(SteamPublishedFileID publishedFileID, int appID) {
            //do nothing
        }
    };

    @Getter
    @Setter
    private SteamUGCCallback steamUGCCallback = new SteamUGCCallback() {
        @Override
        public void onUGCQueryCompleted(SteamUGCQuery query, int numResultsReturned, int totalMatchingResults,
                                        boolean isCachedData, SteamResult result) {
            LOGGER.debug("UGC query completed: handle=" + query.toString() + ", " + numResultsReturned + " of " +
                    totalMatchingResults + " results returned, result=" + result.toString());

            for (int i = 0; i < numResultsReturned; i++) {
                SteamUGCDetails details = new SteamUGCDetails();
                getSteamManager().getSteamUGC().getQueryUGCResult(query, i, details);
                printUGCDetails("UGC details #" + i, details);
            }

            getSteamManager().getSteamUGC().releaseQueryUserUGCRequest(query);
        }

        @Override
        public void onSubscribeItem(SteamPublishedFileID publishedFileID, SteamResult result) {
            LOGGER.debug("Subscribe item result: publishedFileID=" + publishedFileID + ", result=" + result);
        }

        @Override
        public void onUnsubscribeItem(SteamPublishedFileID publishedFileID, SteamResult result) {
            LOGGER.debug("Unsubscribe item result: publishedFileID=" + publishedFileID + ", result=" + result);
        }

        @Override
        public void onRequestUGCDetails(SteamUGCDetails details, SteamResult result) {
            LOGGER.debug("Request details result: result=" + result);
            printUGCDetails("UGC details ", details);
        }

        @Override
        public void onCreateItem(SteamPublishedFileID publishedFileID, boolean needsToAcceptWLA, SteamResult result) {
            //do nothing
        }

        @Override
        public void onSubmitItemUpdate(SteamPublishedFileID publishedFileID, boolean needsToAcceptWLA, SteamResult result) {
            //do nothing
        }

        @Override
        public void onDownloadItemResult(int appID, SteamPublishedFileID publishedFileID, SteamResult result) {
            //do nothing
        }

        @Override
        public void onUserFavoriteItemsListChanged(SteamPublishedFileID publishedFileID, boolean wasAddRequest, SteamResult result) {
            //do nothing
        }

        @Override
        public void onSetUserItemVote(SteamPublishedFileID publishedFileID, boolean voteUp, SteamResult result) {
            //do nothing
        }

        @Override
        public void onGetUserItemVote(SteamPublishedFileID publishedFileID, boolean votedUp, boolean votedDown, boolean voteSkipped, SteamResult result) {
            //do nothing
        }

        private void printUGCDetails(String prefix, SteamUGCDetails details) {
            LOGGER.debug(prefix +
                    ": publishedFileID=" + details.getPublishedFileID().toString() +
                    ", result=" + details.getResult().name() +
                    ", type=" + details.getFileType().name() +
                    ", title='" + details.getTitle() + "'" +
                    ", description='" + details.getDescription() + "'" +
                    ", tags='" + details.getTags() + "'" +
                    ", fileName=" + details.getFileName() +
                    ", fileHandle=" + details.getFileHandle().toString() +
                    ", previewFileHandle=" + details.getPreviewFileHandle().toString() +
                    ", url=" + details.getURL());
        }

        @Override
        public void onStartPlaytimeTracking(SteamResult result) {
            //do nothing
        }

        @Override
        public void onStopPlaytimeTracking(SteamResult result) {
            //do nothing
        }

        @Override
        public void onStopPlaytimeTrackingForAllItems(SteamResult result) {
            //do nothing
        }

        @Override
        public void onDeleteItem(SteamPublishedFileID publishedFileID, SteamResult result) {
            //do nothing
        }
    };

    @Getter
    @Setter
    private SteamFriendsCallback steamFriendsCallback = new SteamFriendsCallback() {
        @Override
        public void onSetPersonaNameResponse(boolean success, boolean localSuccess, SteamResult result) {
            //do nothing
        }

        @Override
        public void onPersonaStateChange(SteamID steamID, SteamFriends.PersonaChange change) {
            //noinspection SwitchStatementWithTooFewBranches
            switch (change) {
                case Name:
                    LOGGER.debug("Persona name received: " +
                            "accountID=" + steamID.getAccountID() +
                            ", name='" + getSteamManager().getSteamFriends().getFriendPersonaName(steamID) + "'");
                    break;

                default:
                    LOGGER.debug("Persona state changed (unhandled): " +
                            "accountID=" + steamID.getAccountID() +
                            ", change=" + change.name());
                    break;
            }
        }

        @Override
        public void onGameOverlayActivated(boolean active) {
            //do nothing
        }

        @Override
        public void onGameLobbyJoinRequested(SteamID steamIDLobby, SteamID steamIDFriend) {
//do nothing
        }

        @Override
        public void onAvatarImageLoaded(SteamID steamID, int image, int width, int height) {
            //do nothing
        }

        @Override
        public void onFriendRichPresenceUpdate(SteamID steamIDFriend, int appID) {
            //do nothing
        }

        @Override
        public void onGameRichPresenceJoinRequested(SteamID steamIDFriend, String connect) {
            //do nothing
        }

        @Override
        public void onGameServerChangeRequested(String server, String password) {
            //do nothing
        }
    };

    @Getter
    @Setter
    private SteamUtilsCallback steamUtilsCallback = new SteamUtilsCallback() {
        @Override
        public void onSteamShutdown() {
            LOGGER.debug("Steam client wants to shut down!");
        }
    };

    @Getter
    @Setter
    private SteamAPIWarningMessageHook steamAPIWarningMessageHook = new SteamAPIWarningMessageHook() {
        @Override
        public void onWarningMessage(int severity, String message) {
            LOGGER.error("[client debug message] (" + severity + ") " + message);
        }
    };
}
