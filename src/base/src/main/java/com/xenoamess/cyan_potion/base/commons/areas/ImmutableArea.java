/*
 * MIT License
 *
 * Copyright (c) 2019 XenoAmess
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

package com.xenoamess.cyan_potion.base.commons.areas;

/**
 * @author XenoAmess
 */
public class ImmutableArea implements AbstractArea {
    private final float centerPosX;
    private final float centerPosY;
    private final float width;
    private final float height;

    public ImmutableArea(AbstractArea abstractArea) {
        this.centerPosX = abstractArea.getCenterPosX();
        this.centerPosY = abstractArea.getCenterPosY();
        this.width = abstractArea.getWidth();
        this.height = abstractArea.getHeight();
    }

    public ImmutableArea(float centerPosX, float centerPosY, float width, float height) {
        this.centerPosX = centerPosX;
        this.centerPosY = centerPosY;
        this.width = width;
        this.height = height;
    }

    public static ImmutableArea generateImmutableArea(
            float centerPosX, float centerPosY, float width, float height) {
        return new ImmutableArea(centerPosX, centerPosY, width, height);
    }

    public static ImmutableArea generateImmutableArea(
            AbstractArea abstractArea) {
        return new ImmutableArea(abstractArea);
    }

    public static ImmutableArea generateImmutableAreaFromLeftTop(
            float leftTopPosX, float leftTopPosY, float width, float height) {
        return new ImmutableArea(leftTopPosX + width / 2F, leftTopPosY + height / 2F, width, height);
    }

    @Override
    public float getCenterPosX() {
        return this.centerPosX;
    }

    @Override
    public float getCenterPosY() {
        return this.centerPosY;
    }

    @Override
    public float getWidth() {
        return this.width;
    }

    @Override
    public float getHeight() {
        return this.height;
    }
}
