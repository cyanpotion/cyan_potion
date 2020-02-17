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

package com.xenoamess.cyan_potion.base.areas;

/**
 * Utility class for Area.
 *
 * @author XenoAmess
 * @version 0.159.1-SNAPSHOT
 */
public class Areas {
    private Areas() {
    }

    /**
     * generate a SimpleImmutableArea from center position.
     *
     * @param centerPosX a float.
     * @param centerPosY a float.
     * @param width      a float.
     * @param height     a float.
     * @return return
     */
    public static SimpleImmutableArea generateImmutableAreaFromArea(
            float centerPosX, float centerPosY, float width, float height) {
        return new SimpleImmutableArea(centerPosX - height / 2F, centerPosY - height / 2F, width, height);
    }

    /**
     * generate a SimpleImmutableArea from area.
     *
     * @param abstractArea area
     * @return return
     */
    public static SimpleImmutableArea generateImmutableAreaFromArea(
            AbstractArea abstractArea) {
        return new SimpleImmutableArea(abstractArea);
    }

    /**
     * generate a SimpleImmutableArea from left top position.
     *
     * @param leftTopPosX a float.
     * @param leftTopPosY a float.
     * @param width       a float.
     * @param height      a float.
     * @return return
     */
    public static SimpleImmutableArea generateImmutableAreaFromLeftTop(
            float leftTopPosX, float leftTopPosY, float width, float height) {
        return new SimpleImmutableArea(leftTopPosX, leftTopPosY, width, height);
    }

    /**
     * generate a SimpleMutableArea from center position.
     *
     * @param centerPosX a float.
     * @param centerPosY a float.
     * @param width      a float.
     * @param height     a float.
     * @return return
     */
    public static SimpleMutableArea generateMutableAreaFromArea(
            float centerPosX, float centerPosY, float width, float height) {
        return new SimpleMutableArea(centerPosX - height / 2F, centerPosY - width / 2F, width, height);
    }

    /**
     * generate a SimpleMutableArea from area.
     *
     * @param abstractArea area
     * @return return
     */
    public static SimpleMutableArea generateMutableAreaFromArea(
            AbstractArea abstractArea) {
        return new SimpleMutableArea(abstractArea);
    }

    /**
     * generate a SimpleMutableArea from left top position.
     *
     * @param leftTopPosX a float.
     * @param leftTopPosY a float.
     * @param width       a float.
     * @param height      a float.
     * @return return
     */
    public static SimpleMutableArea generateMutableAreaFromLeftTop(
            float leftTopPosX, float leftTopPosY, float width, float height) {
        return new SimpleMutableArea(leftTopPosX, leftTopPosY, width, height);
    }
}
