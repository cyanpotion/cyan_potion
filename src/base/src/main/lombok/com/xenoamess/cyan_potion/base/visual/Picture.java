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

package com.xenoamess.cyan_potion.base.visual;

import com.xenoamess.cyan_potion.base.render.Bindable;

import java.util.Objects;

/**
 * <p>Picture class.</p>
 *
 * @author XenoAmess
 * @version 0.160.0-SNAPSHOT
 */
public class Picture extends AbstractBindablePicture {
    private Bindable bindable;

    /**
     * <p>Constructor for Picture.</p>
     *
     * @param bindable bindable
     */
    public Picture(Bindable bindable) {
        this.setBindable(bindable);
    }

    /**
     * <p>Constructor for Picture.</p>
     */
    public Picture() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bindable getCurrentBindable() {
        return this.getBindable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Picture)) {
            return false;
        }
        Picture picture = (Picture) o;
        return Float.compare(picture.getCenterPosX(), getCenterPosX()) == 0 &&
                Float.compare(picture.getCenterPosY(), getCenterPosY()) == 0 &&
                Float.compare(picture.getWidth(), getWidth()) == 0 &&
                Float.compare(picture.getHeight(), getHeight()) == 0 &&
                Float.compare(picture.getRotateRadius(), getRotateRadius()) == 0 &&
                Objects.equals(getBindable(), picture.getBindable()) &&
                Objects.equals(getColorScale(), picture.getColorScale());
    }

    /**
     * <p>Getter for the field <code>bindable</code>.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.render.Bindable} object.
     */
    public Bindable getBindable() {
        return bindable;
    }

    /**
     * <p>Setter for the field <code>bindable</code>.</p>
     *
     * @param bindable a {@link com.xenoamess.cyan_potion.base.render.Bindable} object.
     */
    public void setBindable(Bindable bindable) {
        this.bindable = bindable;
    }
}