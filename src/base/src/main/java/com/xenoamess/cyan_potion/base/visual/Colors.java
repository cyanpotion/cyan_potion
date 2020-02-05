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

import org.joml.Vector4f;

import java.awt.*;

/**
 * <p>Colors class.</p>
 *
 * @author XenoAmess
 * @version 0.155.2
 */
public class Colors {
    public static String colorVector4fToColorString(Vector4f colorVector) {
        return "" + colorVector.x + "," + colorVector.y + "," + colorVector.z + "," + colorVector.w;
    }

    public static Vector4f colorStringToColorVector4f(String colorString) {
        String[] strings = colorString.split(",");
        return new Vector4f(
                Float.parseFloat(strings[0]),
                Float.parseFloat(strings[1]),
                Float.parseFloat(strings[2]),
                Float.parseFloat(strings[3])
        );
    }

    public static Color colorVector4fToColorAwt(Vector4f colorVector) {
        return new Color(colorVector.x, colorVector.y, colorVector.z, colorVector.w);
    }

    public static Vector4f colorAwtToColorVector4f(Color colorAwt) {
        return new Vector4f(colorAwt.getRed(), colorAwt.getGreen(), colorAwt.getBlue(), colorAwt.getAlpha());
    }

    public static Color colorStringToColorAwt(String colorString) {
        String[] strings = colorString.split(",");
        return new Color(
                Float.parseFloat(strings[0]),
                Float.parseFloat(strings[1]),
                Float.parseFloat(strings[2]),
                Float.parseFloat(strings[3])
        );
    }

    public static String colorAwtToColorString(Color colorAwt) {
        return colorVector4fToColorString(colorAwtToColorVector4f(colorAwt));
    }
}
