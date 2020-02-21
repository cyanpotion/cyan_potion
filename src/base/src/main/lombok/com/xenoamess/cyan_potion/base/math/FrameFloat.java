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

package com.xenoamess.cyan_potion.base.math;

import com.xenoamess.cyan_potion.base.GameManager;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * FrameFloat
 * FrameFloat is a float that will be used to calculate things between each frame.
 * the {@link #valueFor1Second} means value for 1 second.
 * for example if this FrameFloat means a speed, and its valueFor1Second is 100, then its speed is 100/second.
 *
 * @author XenoAmess
 * @version 0.160.0
 */

@EqualsAndHashCode
@ToString
public class FrameFloat {
    @Getter
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private final GameManager gameManager;
    @Getter
    @Setter
    private float valueFor1Second;

    /**
     * <p>Constructor for FrameFloat.</p>
     *
     * @param gameManager a {@link com.xenoamess.cyan_potion.base.GameManager} object.
     */
    public FrameFloat(GameManager gameManager) {
        this(gameManager, Float.NaN);
    }

    /**
     * <p>Constructor for FrameFloat.</p>
     *
     * @param gameManager     a {@link com.xenoamess.cyan_potion.base.GameManager} object.
     * @param valueFor1Second a float.
     */
    public FrameFloat(GameManager gameManager, float valueFor1Second) {
        this.gameManager = gameManager;
        this.valueFor1Second = valueFor1Second;
    }

    /**
     * <p>getValue.</p>
     *
     * @return a float.
     */
    public float getValue() {
        return (float) this.getGameManager().getTimeToLastUpdate() * this.valueFor1Second;
    }
}
