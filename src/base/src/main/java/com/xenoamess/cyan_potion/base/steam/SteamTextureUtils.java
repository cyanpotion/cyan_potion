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

import com.codedisaster.steamworks.SteamException;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xenoamess.commons.main_thread_only.MainThreadOnly;
import com.xenoamess.cyan_potion.base.DataCenter;
import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.memory.ResourceInfo;
import com.xenoamess.cyan_potion.base.render.Texture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.function.Function;

import static com.codedisaster.steamworks.SteamID.createFromNativeHandle;

public class SteamTextureUtils {
    @JsonIgnore
    private static transient final Logger LOGGER =
            LoggerFactory.getLogger(SteamTextureUtils.class);

    public static final String STRING_STEAM_AVATAR = "steam_avatar";
    public static final String STRING_SMALL = "small";
    public static final String STRING_MEDIUM = "medium";
    public static final String STRING_LARGE = "large";

    /**
     * !!!NOTICE!!!
     * This function is used by reflection and don't delete it if you don't know about the plugin mechanism here.
     */
    public static final Function<GameManager, Void> PUT_TEXTURE_LOADER_STEAM_AVATAR = (GameManager gameManager) -> {
        gameManager.getResourceManager().putResourceLoader(
                Texture.class,
                STRING_STEAM_AVATAR,
                SteamTextureUtils::loadSteamAvatarTextures
        );
        return null;
    };

    @MainThreadOnly
    public static boolean loadSteamAvatarTextures(Texture texture) {
        if (!DataCenter.ifMainThread()) {
            return false;
        }

        ResourceInfo<Texture> resourceInfo = texture.getResourceInfo();
        assert (STRING_STEAM_AVATAR.equals(resourceInfo.getType()));
        final GameManager gameManager = texture.getResourceManager().getGameManager();
        if (!gameManager.getDataCenter().getGameSettings().isRunWithSteam()) {
            LOGGER.error("[steam]cannot load steam avatar when not run with steam!");
            return false;
        }

        final SteamManager steamManager = gameManager.getSteamManager();

        String[] values = resourceInfo.getValues();
        String steamUserHandleString = values[0];
        String sizeString = values[1];

        long steamUserHandleLong = Long.parseLong(steamUserHandleString);
        SteamID steamUserID = createFromNativeHandle(steamUserHandleLong);

        SteamImage steamImage = null;

        switch (sizeString) {
            case STRING_SMALL:
                steamImage = new SteamImage(steamManager.getSteamFriends().getSmallFriendAvatar(steamUserID));
                break;
            case STRING_MEDIUM:
                steamImage = new SteamImage(steamManager.getSteamFriends().getMediumFriendAvatar(steamUserID));
                break;
            case STRING_LARGE:
                steamImage = new SteamImage(steamManager.getSteamFriends().getLargeFriendAvatar(steamUserID));
                break;
            default:
                LOGGER.error("[steam]cannot understand sizeString:{}. can only accept {}/{}/{} here.", sizeString, STRING_SMALL, STRING_MEDIUM, STRING_LARGE);
        }

        SteamUtils steamUtils = steamManager.getSteamUtils();

        ByteBuffer byteBuffer = null;
        try {
            while (byteBuffer == null) {
                byteBuffer = steamImage.getImageBuffer(steamUtils);
            }
        } catch (SteamException e) {
            LOGGER.error("[steam]cannot getImageBuffer from avatar:{}", resourceInfo);
        }

        texture.bake(steamImage.getWidth(steamUtils), steamImage.getHeight(steamUtils), byteBuffer);
        return true;
    }
}
