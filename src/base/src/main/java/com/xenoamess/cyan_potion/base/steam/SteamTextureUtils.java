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
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.function.Function;

import static com.codedisaster.steamworks.SteamID.createFromNativeHandle;

/**
 * <p>SteamTextureUtils class.</p>
 *
 * @author xenoa
 * @version 0.156.0
 */
public class SteamTextureUtils {
    @JsonIgnore
    private static transient final Logger LOGGER =
            LoggerFactory.getLogger(SteamTextureUtils.class);

    /**
     * Constant <code>STRING_STEAM_AVATAR="steam_avatar"</code>
     */
    public static final String STRING_STEAM_AVATAR = "steam_avatar";
    /**
     * Constant <code>STRING_SMALL="small"</code>
     */
    public static final String STRING_SMALL = "small";
    /**
     * Constant <code>STRING_MEDIUM="medium"</code>
     */
    public static final String STRING_MEDIUM = "medium";
    /**
     * Constant <code>STRING_LARGE="large"</code>
     */
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

    /**
     * If not run with steam,
     * then return a all-black picture.
     *
     * @param texture the texture to load.
     * @return if load succeed
     */
    @MainThreadOnly
    public static boolean loadSteamAvatarTextures(Texture texture) {
        if (!DataCenter.ifMainThread()) {
            return false;
        }
        assert (texture != null);
        ResourceInfo<Texture> resourceInfo = texture.getResourceInfo();
        assert (STRING_STEAM_AVATAR.equals(resourceInfo.getType()));
        final GameManager gameManager = texture.getResourceManager().getGameManager();

        ByteBuffer byteBuffer = null;
        int width;
        int height;

        final SteamManager steamManager = gameManager.getSteamManager();
        if (steamManager.isRunWithSteam()) {
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
            try {
                byteBuffer = steamImage.getImageBuffer(steamUtils);
            } catch (SteamException e) {
                LOGGER.error("[steam]cannot getImageBuffer from avatar:{}", resourceInfo);
            }
            if (byteBuffer != null) {
                width = steamImage.getWidth(steamUtils);
                height = steamImage.getHeight(steamUtils);
                texture.bake(width, height, byteBuffer);
            } else {
                loadAsPureWhite(texture);
            }
        } else {
            loadAsPureWhite(texture);
        }

        return true;
    }

    private static void loadAsPureWhite(Texture texture) {
        LOGGER.error("[steam]cannot getImageBuffer from avatar:{}, generate a white texture instead.", texture.getResourceInfo());
        int width = 1;
        int height = 1;
        ByteBuffer byteBuffer = MemoryUtil.memAlloc(width * height * 4);
        for (int i = 0; i < width * height * 4; i++) {
            byteBuffer.put((byte) 255);
        }
        byteBuffer.flip();
        texture.bake(width, height, byteBuffer);
        MemoryUtil.memFree(byteBuffer);
    }
}
