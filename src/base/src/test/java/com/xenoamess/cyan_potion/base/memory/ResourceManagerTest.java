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

package com.xenoamess.cyan_potion.base.memory;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ResourceManager.
 *
 * @author XenoAmess
 * @version 0.167.4
 */
public class ResourceManagerTest {

    @Test
    public void testGetFileSystemManager() {
        assertNotNull(ResourceManager.getFILE_SYSTEM_MANAGER());
    }

    @Test
    public void testResolveFileExisting() throws FileSystemException {
        // Test resolving the project directory (always exists)
        FileObject fileObject = ResourceManager.resolveFile(".");
        assertNotNull(fileObject);
        assertTrue(fileObject.exists());
    }

    @Test
    public void testResolveFileNonExisting() {
        // Test resolving a non-existing file
        FileObject fileObject = ResourceManager.resolveFile("/non/existing/path/file.txt");
        // Should return a FileObject even if it doesn't exist
        assertNotNull(fileObject);
    }

    @Test
    public void testResolveFileEmptyString() {
        // Test with empty string
        FileObject fileObject = ResourceManager.resolveFile("");
        assertNotNull(fileObject);
    }

    @Test
    public void testLoadStringExistingFile() throws Exception {
        // Create a temp file
        java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("test_load_", ".txt");
        String testContent = "Test content for ResourceManager";
        java.nio.file.Files.writeString(tempFile, testContent);

        try {
            String loaded = ResourceManager.loadString(tempFile.toString());
            assertEquals(testContent, loaded);
        } finally {
            java.nio.file.Files.deleteIfExists(tempFile);
        }
    }

    @Test
    public void testLoadStringNonExistingFile() {
        String result = ResourceManager.loadString("/non/existing/file.txt");
        assertEquals("", result);
    }

    @Test
    public void testLoadStringEmptyString() {
        String result = ResourceManager.loadString("");
        assertEquals("", result);
    }

    @Test
    public void testLoadStringFromFileObjectNull() {
        String result = ResourceManager.loadString((FileObject) null);
        assertEquals("", result);
    }

    @Test
    public void testLoadStringWithUnicode() throws Exception {
        java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("unicode_test_", ".txt");
        String testContent = "Unicode test: 你好世界 ñáéíóú 日本語";
        java.nio.file.Files.writeString(tempFile, testContent);

        try {
            String loaded = ResourceManager.loadString(tempFile.toString());
            assertEquals(testContent, loaded);
        } finally {
            java.nio.file.Files.deleteIfExists(tempFile);
        }
    }

    @Test
    public void testLoadStringWithSpecialCharacters() throws Exception {
        java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("special_chars_", ".txt");
        String testContent = "Special: <>&\"'\n\t\r\\/";
        java.nio.file.Files.writeString(tempFile, testContent);

        try {
            String loaded = ResourceManager.loadString(tempFile.toString());
            assertEquals(testContent, loaded);
        } finally {
            java.nio.file.Files.deleteIfExists(tempFile);
        }
    }

    @Test
    public void testLoadStringWithMultipleLines() throws Exception {
        java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("multi_line_", ".txt");
        String testContent = "Line 1\nLine 2\nLine 3\n";
        java.nio.file.Files.writeString(tempFile, testContent);

        try {
            String loaded = ResourceManager.loadString(tempFile.toString());
            assertEquals(testContent, loaded);
        } finally {
            java.nio.file.Files.deleteIfExists(tempFile);
        }
    }

    @Test
    public void testLoadStringEmptyFile() throws Exception {
        java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("empty_", ".txt");
        // Create empty file

        try {
            String loaded = ResourceManager.loadString(tempFile.toString());
            assertEquals("", loaded);
        } finally {
            java.nio.file.Files.deleteIfExists(tempFile);
        }
    }
}
