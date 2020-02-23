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

import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class FileManagerTest {
    @Test
    public void test() {
        System.out.println(org.apache.commons.httpclient.HttpClient.class);
        System.out.println(org.apache.jackrabbit.webdav.client.methods.DavMethod.class);
        FileSystemManager fileSystemManager = ResourceManager.getFileSystemManager();

        URL url2 = null;
        URL url1 = null;

        try {
            url2 = new File("D:/1 1.txt").toURI().toURL();
            System.out.println("File : " + url2);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            url1 = fileSystemManager.resolveFile("D:/1 1.txt").getURL();
            System.out.println("FileObject : " + url1);
        } catch (FileSystemException e) {
            e.printStackTrace();
        }

        try {
            System.out.println(new File(url2.toURI()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        try {
            System.out.println(new File(url1.toURI()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


    }
}
