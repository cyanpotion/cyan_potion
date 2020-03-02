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

package com.xenoamess.cyan_potion.base;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Closeable;

/**
 * SubManager
 * SubManager means a sub manager of GameManager.
 * nearly all SubManagers a split from GameManager(some of them are done even before I put cyan_potion open source).
 * (for example,{@link com.xenoamess.cyan_potion.base.steam.SteamManager})
 * each SubManager deals with a part of this game,
 * and can be thought as a part of GameManager.
 * <p>
 * notice that although most of the SubManagers named ***Manager,
 * some of them are not named this way, due to historical or other reasons.
 *
 * @author XenoAmess
 * @version 0.161.4
 */
@EqualsAndHashCode
@ToString
public abstract class SubManager implements Closeable {
    @Getter
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private final GameManager gameManager;

    /**
     * <p>Constructor for SubManager.</p>
     *
     * @param gameManager gameManager
     */
    public SubManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * <p>init.</p>
     */
    public abstract void init();

    /**
     * <p>update.</p>
     */
    public abstract void update();

    /**
     * <p>close.</p>
     */
    public abstract void close();
}
