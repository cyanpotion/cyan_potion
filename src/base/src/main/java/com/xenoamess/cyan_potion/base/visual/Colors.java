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

import com.xenoamess.cyan_potion.base.memory.ResourceInfo;
import com.xenoamess.cyan_potion.base.render.Texture;
import org.joml.Vector4f;
import org.joml.Vector4fc;

import java.awt.*;

import static com.xenoamess.cyan_potion.base.render.Texture.STRING_PURE_COLOR;

/**
 * <p>Colors class.</p>
 *
 * @author XenoAmess
 * @version 0.156.1-SNAPSHOT
 */
public class Colors {

    public static String colorVector4fToColorString(Vector4fc colorVector) {
        return "" + colorVector.x() + "," + colorVector.y() + "," + colorVector.z() + "," + colorVector.w();
    }

    public static Vector4fc colorStringToColorVector4f(String colorString) {
        String[] strings = colorString.split(",");
        return new Vector4f(
                Float.parseFloat(strings[0]),
                Float.parseFloat(strings[1]),
                Float.parseFloat(strings[2]),
                Float.parseFloat(strings[3])
        );
    }

    public static Color colorVector4fToColorAwt(Vector4fc colorVector) {
        return new Color(colorVector.x(), colorVector.y(), colorVector.z(), colorVector.w());
    }

    public static Vector4fc colorAwtToColorVector4f(Color colorAwt) {
        return new Vector4f(colorAwt.getRed() / 255F, colorAwt.getGreen() / 255F, colorAwt.getBlue() / 255F, colorAwt.getAlpha() / 255F);
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

    public static ResourceInfo<Texture> getPureColorTextureResourceInfo(Color colorAWT) {
        return getPureColorTextureResourceInfo(colorAwtToColorString(colorAWT));
    }

    public static ResourceInfo<Texture> getPureColorTextureResourceInfo(Vector4fc colorVector) {
        return getPureColorTextureResourceInfo(colorVector4fToColorString(colorVector));
    }

    public static ResourceInfo<Texture> getPureColorTextureResourceInfo(String colorString) {
        ResourceInfo<Texture> resourceInfo = new ResourceInfo<>(
                Texture.class,
                STRING_PURE_COLOR,
                "",
                colorString
        );
        return resourceInfo;
    }


    /**
     * The color (0,0,0,0)
     */
    public static final Vector4fc empty = colorAwtToColorVector4f(Color.white);

    /**
     * The color (0,0,0,0)
     *
     * @since 0.156.1-SNAPSHOT
     */
    public static final Vector4fc EMPTY = empty;

    public static final String COLOR_STRING_EMPTY = colorVector4fToColorString(empty);

    public static final ResourceInfo<Texture> RESOURCE_INFO_EMPTY = getPureColorTextureResourceInfo(COLOR_STRING_EMPTY);


    /**
     * The color white.  In the default sRGB space.
     */
    public static final Vector4fc white = colorAwtToColorVector4f(Color.white);

    /**
     * The color white.  In the default sRGB space.
     *
     * @since 0.156.1-SNAPSHOT
     */
    public static final Vector4fc WHITE = white;

    public static final String COLOR_STRING_WHITE = colorVector4fToColorString(white);
    public static final ResourceInfo<Texture> RESOURCE_INFO_WHITE = getPureColorTextureResourceInfo(COLOR_STRING_WHITE);

    /**
     * The color light gray.  In the default sRGB space.
     */
    public static final Vector4fc lightGray = colorAwtToColorVector4f(Color.lightGray);

    /**
     * The color light gray.  In the default sRGB space.
     *
     * @since 0.156.1-SNAPSHOT
     */
    public static final Vector4fc LIGHT_GRAY = lightGray;

    public static final String COLOR_STRING_LIGHT_GRAY = colorVector4fToColorString(lightGray);
    public static final ResourceInfo<Texture> RESOURCE_INFO_LIGHT_GRAY = getPureColorTextureResourceInfo(COLOR_STRING_LIGHT_GRAY);


    /**
     * The color gray.  In the default sRGB space.
     */
    public static final Vector4fc gray = colorAwtToColorVector4f(Color.gray);

    /**
     * The color gray.  In the default sRGB space.
     *
     * @since 0.156.1-SNAPSHOT
     */
    public static final Vector4fc GRAY = gray;

    public static final String COLOR_STRING_GRAY = colorVector4fToColorString(gray);
    public static final ResourceInfo<Texture> RESOURCE_INFO_GRAY = getPureColorTextureResourceInfo(COLOR_STRING_GRAY);

    /**
     * The color dark gray.  In the default sRGB space.
     */
    public static final Vector4fc darkGray = colorAwtToColorVector4f(Color.darkGray);

    /**
     * The color dark gray.  In the default sRGB space.
     *
     * @since 0.156.1-SNAPSHOT
     */
    public static final Vector4fc DARK_GRAY = darkGray;

    public static final String COLOR_STRING_DARK_GRAY = colorVector4fToColorString(darkGray);
    public static final ResourceInfo<Texture> RESOURCE_INFO_DARK_GRAY = getPureColorTextureResourceInfo(COLOR_STRING_DARK_GRAY);


    /**
     * The color black.  In the default sRGB space.
     */
    public static final Vector4fc black = colorAwtToColorVector4f(Color.black);

    /**
     * The color black.  In the default sRGB space.
     *
     * @since 0.156.1-SNAPSHOT
     */
    public static final Vector4fc BLACK = black;

    public static final String COLOR_STRING_BLACK = colorVector4fToColorString(black);
    public static final ResourceInfo<Texture> RESOURCE_INFO_BLACK = getPureColorTextureResourceInfo(COLOR_STRING_BLACK);


    /**
     * The color red.  In the default sRGB space.
     */
    public static final Vector4fc red = colorAwtToColorVector4f(Color.red);

    /**
     * The color red.  In the default sRGB space.
     *
     * @since 0.156.1-SNAPSHOT
     */
    public static final Vector4fc RED = red;

    public static final String COLOR_STRING_RED = colorVector4fToColorString(red);
    public static final ResourceInfo<Texture> RESOURCE_INFO_RED = getPureColorTextureResourceInfo(COLOR_STRING_RED);


    /**
     * The color pink.  In the default sRGB space.
     */
    public static final Vector4fc pink = colorAwtToColorVector4f(Color.pink);

    /**
     * The color pink.  In the default sRGB space.
     *
     * @since 0.156.1-SNAPSHOT
     */
    public static final Vector4fc PINK = pink;

    public static final String COLOR_STRING_PINK = colorVector4fToColorString(pink);
    public static final ResourceInfo<Texture> RESOURCE_INFO_PINK = getPureColorTextureResourceInfo(COLOR_STRING_PINK);


    /**
     * The color orange.  In the default sRGB space.
     */
    public static final Vector4fc orange = colorAwtToColorVector4f(Color.orange);

    /**
     * The color orange.  In the default sRGB space.
     *
     * @since 0.156.1-SNAPSHOT
     */
    public static final Vector4fc ORANGE = orange;

    public static final String COLOR_STRING_ORANGE = colorVector4fToColorString(orange);
    public static final ResourceInfo<Texture> RESOURCE_INFO_ORANGE = getPureColorTextureResourceInfo(COLOR_STRING_ORANGE);


    /**
     * The color yellow.  In the default sRGB space.
     */
    public static final Vector4fc yellow = colorAwtToColorVector4f(Color.yellow);

    /**
     * The color yellow.  In the default sRGB space.
     *
     * @since 0.156.1-SNAPSHOT
     */
    public static final Vector4fc YELLOW = yellow;

    public static final String COLOR_STRING_YELLOW = colorVector4fToColorString(yellow);
    public static final ResourceInfo<Texture> RESOURCE_INFO_YELLOW = getPureColorTextureResourceInfo(COLOR_STRING_YELLOW);


    /**
     * The color green.  In the default sRGB space.
     */
    public static final Vector4fc green = colorAwtToColorVector4f(Color.green);

    /**
     * The color green.  In the default sRGB space.
     *
     * @since 0.156.1-SNAPSHOT
     */
    public static final Vector4fc GREEN = green;

    public static final String COLOR_STRING_GREEN = colorVector4fToColorString(green);
    public static final ResourceInfo<Texture> RESOURCE_INFO_GREEN = getPureColorTextureResourceInfo(COLOR_STRING_GREEN);


    /**
     * The color magenta.  In the default sRGB space.
     */
    public static final Vector4fc magenta = colorAwtToColorVector4f(Color.magenta);

    /**
     * The color magenta.  In the default sRGB space.
     *
     * @since 0.156.1-SNAPSHOT
     */
    public static final Vector4fc MAGENTA = magenta;

    public static final String COLOR_STRING_MAGENTA = colorVector4fToColorString(magenta);
    public static final ResourceInfo<Texture> RESOURCE_INFO_MAGENTA = getPureColorTextureResourceInfo(COLOR_STRING_MAGENTA);


    /**
     * The color cyan.  In the default sRGB space.
     */
    public static final Vector4fc cyan = colorAwtToColorVector4f(Color.cyan);

    /**
     * The color cyan.  In the default sRGB space.
     *
     * @since 0.156.1-SNAPSHOT
     */
    public static final Vector4fc CYAN = cyan;

    public static final String COLOR_STRING_CYAN = colorVector4fToColorString(cyan);
    public static final ResourceInfo<Texture> RESOURCE_INFO_CYAN = getPureColorTextureResourceInfo(COLOR_STRING_CYAN);


    /**
     * The color blue.  In the default sRGB space.
     */
    public static final Vector4fc blue = colorAwtToColorVector4f(Color.blue);

    /**
     * The color blue.  In the default sRGB space.
     *
     * @since 0.156.1-SNAPSHOT
     */
    public static final Vector4fc BLUE = blue;

    public static final String COLOR_STRING_BLUE = colorVector4fToColorString(blue);
    public static final ResourceInfo<Texture> RESOURCE_INFO_BLUE = getPureColorTextureResourceInfo(COLOR_STRING_BLUE);

}
