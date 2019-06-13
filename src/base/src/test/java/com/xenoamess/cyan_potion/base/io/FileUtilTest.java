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

package com.xenoamess.cyan_potion.base.io;

import org.junit.jupiter.api.Test;

/**
 * @author XenoAmess
 */
public class FileUtilTest {
    @Test
    public void test() {
        boolean success = true;
        System.out.println(FileUtil.createFolderIfAbsent("/a/a/a/a").getAbsolutePath());
        System.out.println(FileUtil.createFolderIfAbsent("/a/a/a/a").getAbsolutePath());
        System.out.println(FileUtil.createFolderIfAbsent("/b/b/b/b/").getAbsolutePath());
        System.out.println(FileUtil.getFile("/"));
//        System.out.println(FileUtil.createFolderIfAbsent("/").getAbsolutePath());
        System.out.println(FileUtil.createFileIfAbsent("/c/c/c/c").getAbsolutePath());
        System.out.println(FileUtil.createFileIfAbsent("/a/a/a/a/d").getAbsolutePath());
        System.out.println(FileUtil.createFileIfAbsent("/a/a/a/a/d").getAbsolutePath());
        System.out.println(FileUtil.createFileIfAbsent("/d").getAbsolutePath());
        System.out.println(FileUtil.createFileIfAbsent("d").getAbsolutePath());
        try {
            System.out.println(FileUtil.createFileIfAbsent("/d/").getAbsolutePath());
            success = false;
        } catch (IllegalArgumentException e) {
            System.out.println("should have exception here:");
            e.printStackTrace();
        }
        assert (success);
        try {
            System.out.println(FileUtil.createFileIfAbsent("/a/").getAbsolutePath());
            success = false;
        } catch (IllegalArgumentException e) {
            System.out.println("should have exception here:");
            e.printStackTrace();
        }
        assert (success);
    }
}
