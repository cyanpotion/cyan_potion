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

package com.xenoamess.cyan_potion.rpg_module.plugins;

import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.render.Texture;

import java.util.function.Function;

import static com.xenoamess.cyan_potion.rpg_module.render.TextureUtils.*;

/**
 * This Class's contents are used by reflection and don't delete it if you don't know about the plugin mechanism here.
 *
 * @author XenoAmess
 * @version 0.156.0
 */
public class CodePlugins {
    private CodePlugins() {
    }

    /**
     * Constant <code>PLUGIN_RPG_MODULE_TEXTURE_LOADERS</code>
     */
    public static final Function<GameManager, Void> PLUGIN_RPG_MODULE_TEXTURE_LOADERS = (GameManager gameManager) -> {
        gameManager.getResourceManager().putResourceLoader(Texture.class, STRING_CHARACTER,
                (Texture texture) -> loadAsWalkingTexture(texture)
        );
        gameManager.getResourceManager().putResourceLoader(Texture.class, STRING_A5,
                (Texture texture) -> loadAsTilesetTextures8(texture)
        );
        gameManager.getResourceManager().putResourceLoader(Texture.class, STRING_B,
                (Texture texture) -> loadAsTilesetTextures8(texture)
        );
        gameManager.getResourceManager().putResourceLoader(Texture.class, STRING_C,
                (Texture texture) -> loadAsTilesetTextures8(texture)
        );
        gameManager.getResourceManager().putResourceLoader(Texture.class, STRING_A2,
                (Texture texture) -> loadAsTilesetTexturesA2(texture)
        );
        return null;
    };
}
