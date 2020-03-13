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

package com.xenoamess.cyan_potion.rpg_module.jsons;

import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.base.render.Texture;
import lombok.Data;

import java.io.Serializable;

import static com.xenoamess.cyan_potion.rpg_module.render.TextureUtils.STRING_CHARACTER;

/**
 * <p>ImageJson class.</p>
 *
 * @author XenoAmess
 * @version 0.162.0-SNAPSHOT
 */
@Data
public class ImageJson implements Serializable {
    public int tileId;
    public String characterName;
    public int direction;
    public int pattern;
    public int characterIndex;

    /**
     * <p>getBindable.</p>
     *
     * @param resourceManager resourceManager
     * @return return
     */
    public Bindable getBindable(ResourceManager resourceManager) {
        if ("".equals(this.characterName)) {
            //todo
            //if not from characters but from B
            return null;
        } else {
            int tmp = ((this.direction >> 1) - 1) * 3;
            tmp += this.pattern;
            return resourceManager.fetchResource(
                    Texture.class,
                    STRING_CHARACTER,
                    "resources/www/img/characters/" + this.characterName + ".png",
                    Integer.toString(this.characterIndex),
                    Integer.toString(tmp)
            );
        }
    }
}
