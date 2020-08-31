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

package com.xenoamess.cyan_potion.rpg_module.render;

import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.exceptions.URITypeNotDefinedException;
import com.xenoamess.cyan_potion.base.memory.NormalResource;
import com.xenoamess.cyan_potion.base.memory.ResourceInfo;
import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import com.xenoamess.cyan_potion.base.render.Texture;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.function.Function;

import static com.xenoamess.cyan_potion.rpg_module.render.TextureUtils.STRING_CHARACTER;

/**
 * <p>WalkingAnimation4Dirs class.</p>
 *
 * @author XenoAmess
 * @version 0.162.3
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class WalkingAnimation4DirsResource extends NormalResource {

    /**
     * !!!NOTICE!!!
     * This function is used by reflection and don't delete it if you don't know about the plugin mechanism here.
     */
    @SuppressWarnings("unused")
    public static final Function<GameManager, Void> PUT_WALKING_ANIMATION_4_DIRS_RESOURCE_LOADERS =
            (GameManager gameManager) -> {
        ResourceManager resourceManager = gameManager.getResourceManager();
        resourceManager.putResourceLoader(
                WalkingAnimation4DirsResource.class,
                STRING_CHARACTER,
                WalkingAnimation4DirsResource::loadAsWalkingAnimation4DirsResource
        );
        return null;
    };

    public static boolean loadAsWalkingAnimation4DirsResource(WalkingAnimation4DirsResource walkingAnimation4DirsResource) {
        final ResourceInfo<WalkingAnimation4DirsResource> resourceInfo = walkingAnimation4DirsResource.getResourceInfo();
        final String resourceFilePath = resourceInfo.getFileString();
        //noinspection SwitchStatementWithTooFewBranches
        switch (resourceInfo.getType()) {
            case STRING_CHARACTER:
                int peopleIndex = Integer.parseInt(resourceInfo.getValues()[0]);
                walkingAnimation4DirsResource.walkingTextures =
                        TextureUtils.getWalkingTextures(walkingAnimation4DirsResource.getResourceManager(),
                                resourceFilePath).get(peopleIndex);
                return true;
            default:
                throw new URITypeNotDefinedException(resourceInfo);
        }
    }

    @Getter
    @Setter
    List<Texture> walkingTextures;

    /**
     * !!!NOTICE!!!
     * <p>
     * This class shall never build from this constructor directly.
     * You shall always use ResourceManager.fetchResource functions to get this instance.
     *
     * @param resourceManager resource Manager
     * @param resourceInfo    resource info
     * @see ResourceManager#fetchResource(Class, ResourceInfo)
     */
    public WalkingAnimation4DirsResource(ResourceManager resourceManager, ResourceInfo<Texture> resourceInfo) {
        super(resourceManager, resourceInfo);
    }

    @Override
    protected void forceClose() {
        if (this.walkingTextures != null) {
            this.walkingTextures.clear();
        }
    }


}
