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

import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cross-platform file access tests.
 * These tests verify that file operations work consistently across Windows and Linux.
 *
 * @author XenoAmess
 * @version 0.167.4
 */
public class CrossPlatformFileTest {

    @Test
    public void testResourceManagerFileResolution() throws FileSystemException {
        // Test that ResourceManager can resolve files consistently
        String testPath = System.getProperty("java.io.tmpdir") + "/test_cyan_potion_" + System.currentTimeMillis() + ".txt";
        
        FileObject fileObject = ResourceManager.resolveFile(testPath);
        assertNotNull(fileObject);
        
        // Cleanup - delete if exists
        try {
            fileObject.delete();
        } catch (Exception ignored) {
        }
    }

    @Test
    public void testPathSeparators() {
        // Test that paths with different separators are handled correctly
        String pathWithForwardSlash = "path/to/resource.txt";
        String pathWithBackslash = "path\\to\\resource.txt";
        
        String normalized1 = FilePathUtil.normalize(pathWithForwardSlash);
        String normalized2 = FilePathUtil.normalize(pathWithBackslash);
        
        assertEquals(normalized1, normalized2);
        assertFalse(normalized1.contains("\\"));
    }

    @Test
    public void testAbsolutePathDetection() {
        // Unix absolute paths
        assertTrue(FilePathUtil.isAbsolutePath("/home/user/file.txt"));
        assertTrue(FilePathUtil.isAbsolutePath("/"));
        
        // Windows absolute paths
        assertTrue(FilePathUtil.isAbsolutePath("C:/Users/file.txt"));
        assertTrue(FilePathUtil.isAbsolutePath("D:\\Data\\file.txt"));
        
        // Relative paths
        assertFalse(FilePathUtil.isAbsolutePath("relative/path.txt"));
        assertFalse(FilePathUtil.isAbsolutePath("./file.txt"));
        assertFalse(FilePathUtil.isAbsolutePath("../file.txt"));
    }

    @Test
    public void testConfigDirectoryCreation() {
        String configDir = FilePathUtil.getConfigDir();
        assertNotNull(configDir);
        
        // Ensure directory can be created
        assertTrue(FilePathUtil.ensureDirectoryExists(configDir));
    }

    @Test
    public void testDataDirectoryCreation() {
        String dataDir = FilePathUtil.getDataDir();
        assertNotNull(dataDir);
        
        // Ensure directory can be created
        assertTrue(FilePathUtil.ensureDirectoryExists(dataDir));
    }

    @Test
    public void testTempFileOperations() throws IOException {
        // Create a temp file
        Path tempFile = Files.createTempFile("cyan_potion_test_", ".txt");
        
        try {
            // Write to file
            String testContent = "Cross-platform test content";
            Files.writeString(tempFile, testContent);
            
            // Read from file using ResourceManager
            String loadedContent = ResourceManager.loadString(tempFile.toString());
            
            // Verify content
            assertEquals(testContent, loadedContent);
        } finally {
            // Cleanup
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    public void testFileWithSpacesInPath() throws IOException {
        // Create a temp file with spaces in the name
        Path tempDir = Files.createTempDirectory("cyan potion test dir");
        Path tempFile = tempDir.resolve("test file with spaces.txt");
        
        try {
            String testContent = "Content in file with spaces";
            Files.writeString(tempFile, testContent);
            
            // Verify file can be resolved
            FileObject fileObject = ResourceManager.resolveFile(tempFile.toString());
            assertNotNull(fileObject);
            assertTrue(fileObject.exists());
            
            // Verify content can be loaded
            String loadedContent = ResourceManager.loadString(tempFile.toString());
            assertEquals(testContent, loadedContent);
        } finally {
            // Cleanup
            Files.deleteIfExists(tempFile);
            Files.deleteIfExists(tempDir);
        }
    }

    @Test
    public void testUnicodeFilePaths() throws IOException {
        // Create a temp file with unicode characters
        Path tempDir = Files.createTempDirectory("测试目录");
        Path tempFile = tempDir.resolve("测试文件.txt");
        
        try {
            String testContent = "Unicode test content: 你好世界";
            Files.writeString(tempFile, testContent);
            
            // Verify file can be resolved and content loaded
            String loadedContent = ResourceManager.loadString(tempFile.toString());
            assertEquals(testContent, loadedContent);
        } finally {
            // Cleanup
            Files.deleteIfExists(tempFile);
            Files.deleteIfExists(tempDir);
        }
    }

    @Test
    public void testPlatformSpecificLineEndings() throws IOException {
        Path tempFile = Files.createTempFile("line_ending_test_", ".txt");
        
        try {
            // Write content with different line endings
            String content = "Line 1\nLine 2\r\nLine 3\rLine 4";
            Files.writeString(tempFile, content);
            
            // Read back
            String loaded = ResourceManager.loadString(tempFile.toString());
            
            // Verify content is preserved
            assertNotNull(loaded);
            assertTrue(loaded.contains("Line 1"));
            assertTrue(loaded.contains("Line 2"));
            assertTrue(loaded.contains("Line 3"));
            assertTrue(loaded.contains("Line 4"));
        } finally {
            // Cleanup
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    public void testFilePathCaseSensitivity() throws IOException {
        // This test behavior depends on the platform
        // Windows: case-insensitive
        // Linux: case-sensitive
        
        Path tempFile = Files.createTempFile("CASE_Test_", ".txt");
        
        try {
            String testContent = "Case sensitivity test";
            Files.writeString(tempFile, testContent);
            
            // Try to access with different case
            File fileLower = new File(tempFile.toString().toLowerCase());
            File fileUpper = new File(tempFile.toString().toUpperCase());
            
            // On Windows, both should exist
            // On Linux, only the exact case should exist
            if (org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS) {
                assertTrue(fileLower.exists() || fileUpper.exists());
            }
            
            // Original should always exist
            assertTrue(tempFile.toFile().exists());
        } finally {
            // Cleanup
            Files.deleteIfExists(tempFile);
        }
    }
}
