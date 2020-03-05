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

import org.joml.Quaternionf;
import org.junit.jupiter.api.Test;

public class JomlTest {
    @Test
    public void test() {
        float x = -0.001600552F;
        float y = 3.4311073E-4F;
        float z = 4.991863E-4F;
        float w = 0.9999985F;
        Quaternionf q1 = new Quaternionf(x, y, z, w);
        q1.normalize(q1);
        float degree1 = (float) Math.sqrt(q1.angle());
        System.out.println(degree1);

        float invNorm = (float) (1.0 / Math.sqrt(x * x + y * y + z * z + w * w));
        x *= invNorm;
        y *= invNorm;
        z *= invNorm;
        w *= invNorm;
        System.out.println(x);
        System.out.println(y);
        System.out.println(z);
        System.out.println(w);
        Quaternionf q2 = new Quaternionf(x, y, z, w);
        float degree2 = (float) Math.sqrt(q2.angle());
        System.out.println(degree2);
    }
}
