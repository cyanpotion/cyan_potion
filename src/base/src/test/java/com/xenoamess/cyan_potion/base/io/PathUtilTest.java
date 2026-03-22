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

package com.xenoamess.cyan_potion.base.io;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FilePathUtil path normalization.
 *
 * @author XenoAmess
 * @version 0.167.4
 */
public class PathUtilTest {

    @Test
    public void testNormalizeNull() {
        assertNull(FilePathUtil.normalize((String) null));
        assertNull(FilePathUtil.normalize((File) null));
        assertNull(FilePathUtil.normalize((java.nio.file.Path) null));
    }

    @Test
    public void testNormalizeForwardSlash() {
        String input = "path/to/file.txt";
        String result = FilePathUtil.normalize(input);
        assertEquals("path/to/file.txt", result);
    }

    @Test
    public void testNormalizeBackslash() {
        String input = "path\\to\\file.txt";
        String result = FilePathUtil.normalize(input);
        assertEquals("path/to/file.txt", result);
    }

    @Test
    public void testNormalizeMixedSeparators() {
        String input = "path/to\\file.txt";
        String result = FilePathUtil.normalize(input);
        assertEquals("path/to/file.txt", result);
    }

    @Test
    public void testNormalizeTrailingSlash() {
        String input = "path/to/dir/";
        String result = FilePathUtil.normalize(input);
        assertEquals("path/to/dir", result);
    }

    @Test
    public void testNormalizeRootPath() {
        String input = "/";
        String result = FilePathUtil.normalize(input);
        assertEquals("/", result);
    }

    @Test
    public void testNormalizeWindowsPath() {
        String input = "C:\\Users\\test\\file.txt";
        String result = FilePathUtil.normalize(input);
        assertEquals("C:/Users/test/file.txt", result);
    }

    @Test
    public void testNormalizeFromFile() {
        File file = new File(new File("path", "to"), "file.txt");
        String result = FilePathUtil.normalize(file);
        // The result depends on the OS, but backslashes should be converted
        assertNotNull(result);
        assertFalse(result.contains("\\"));
    }

    @Test
    public void testNormalizeFromPath() {
        java.nio.file.Path path = Paths.get("path").resolve("to").resolve("file.txt");
        String result = FilePathUtil.normalize(path);
        // The result depends on the OS, but backslashes should be converted
        assertNotNull(result);
        assertFalse(result.contains("\\"));
    }

    @Test
    public void testIsAbsolutePathUnix() {
        assertTrue(FilePathUtil.isAbsolutePath("/home/user/file.txt"));
        assertTrue(FilePathUtil.isAbsolutePath("/"));
    }

    @Test
    public void testIsAbsolutePathWindows() {
        assertTrue(FilePathUtil.isAbsolutePath("C:/Users/file.txt"));
        assertTrue(FilePathUtil.isAbsolutePath("C:\\Users\\file.txt"));
    }

    @Test
    public void testIsAbsolutePathRelative() {
        assertFalse(FilePathUtil.isAbsolutePath("relative/path.txt"));
        assertFalse(FilePathUtil.isAbsolutePath("./file.txt"));
        assertFalse(FilePathUtil.isAbsolutePath("../file.txt"));
    }

    @Test
    public void testIsAbsolutePathNull() {
        assertFalse(FilePathUtil.isAbsolutePath(null));
        assertFalse(FilePathUtil.isAbsolutePath(""));
    }

    @Test
    public void testGetConfigDir() {
        String configDir = FilePathUtil.getConfigDir();
        assertNotNull(configDir);
        assertTrue(configDir.endsWith("cyan_potion"));
        assertFalse(configDir.contains("\\"));
    }

    @Test
    public void testGetDataDir() {
        String dataDir = FilePathUtil.getDataDir();
        assertNotNull(dataDir);
        assertTrue(dataDir.endsWith("cyan_potion"));
        assertFalse(dataDir.contains("\\"));
    }

    @Test
    public void testEnsureDirectoryExists() {
        String testDir = System.getProperty("java.io.tmpdir") + "/cyan_potion_test_" + System.currentTimeMillis();
        assertTrue(FilePathUtil.ensureDirectoryExists(testDir));
        
        // Cleanup
        File dir = new File(testDir);
        dir.delete();
    }

    @Test
    public void testEnsureDirectoryExistsNull() {
        assertFalse(FilePathUtil.ensureDirectoryExists(null));
    }
}
