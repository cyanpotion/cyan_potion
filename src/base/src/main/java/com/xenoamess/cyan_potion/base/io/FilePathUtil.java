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

import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for cross-platform file path handling.
 * Provides methods for normalizing paths and getting platform-specific directories.
 *
 * @author XenoAmess
 * @version 0.167.4
 */
public final class FilePathUtil {

    private FilePathUtil() {
        // Utility class, prevent instantiation
    }

    /**
     * Normalizes a file path to use forward slashes and handle platform-specific separators.
     * This ensures consistent path handling across Windows and Linux.
     *
     * @param path the path to normalize
     * @return normalized path string
     */
    public static String normalize(String path) {
        if (path == null) {
            return null;
        }
        // Replace backslashes with forward slashes for consistency
        String normalized = path.replace('\\', '/');
        // Remove trailing slash except for root paths
        if (normalized.length() > 1 && normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    /**
     * Normalizes a File object to a consistent path string.
     *
     * @param file the file to normalize
     * @return normalized path string
     */
    public static String normalize(File file) {
        if (file == null) {
            return null;
        }
        return normalize(file.getPath());
    }

    /**
     * Normalizes a Path object to a consistent path string.
     *
     * @param path the path to normalize
     * @return normalized path string
     */
    public static String normalize(Path path) {
        if (path == null) {
            return null;
        }
        return normalize(path.toString());
    }

    /**
     * Gets the platform-specific configuration directory.
     * <ul>
     *   <li>Windows: %APPDATA%/cyan_potion</li>
     *   <li>Linux: ~/.config/cyan_potion</li>
     *   <li>macOS: ~/Library/Application Support/cyan_potion</li>
     * </ul>
     *
     * @return the configuration directory path
     */
    public static String getConfigDir() {
        String configDir;
        if (SystemUtils.IS_OS_WINDOWS) {
            configDir = System.getenv("APPDATA");
            if (configDir == null) {
                configDir = System.getProperty("user.home") + "/AppData/Roaming";
            }
        } else if (SystemUtils.IS_OS_MAC) {
            configDir = System.getProperty("user.home") + "/Library/Application Support";
        } else {
            // Linux and other Unix-like systems
            String xdgConfig = System.getenv("XDG_CONFIG_HOME");
            if (xdgConfig != null) {
                configDir = xdgConfig;
            } else {
                configDir = System.getProperty("user.home") + "/.config";
            }
        }
        return normalize(configDir + "/cyan_potion");
    }

    /**
     * Gets the platform-specific data directory.
     * <ul>
     *   <li>Windows: %LOCALAPPDATA%/cyan_potion</li>
     *   <li>Linux: ~/.local/share/cyan_potion</li>
     *   <li>macOS: ~/Library/Application Support/cyan_potion</li>
     * </ul>
     *
     * @return the data directory path
     */
    public static String getDataDir() {
        String dataDir;
        if (SystemUtils.IS_OS_WINDOWS) {
            dataDir = System.getenv("LOCALAPPDATA");
            if (dataDir == null) {
                dataDir = System.getProperty("user.home") + "/AppData/Local";
            }
        } else if (SystemUtils.IS_OS_MAC) {
            dataDir = System.getProperty("user.home") + "/Library/Application Support";
        } else {
            // Linux and other Unix-like systems
            String xdgData = System.getenv("XDG_DATA_HOME");
            if (xdgData != null) {
                dataDir = xdgData;
            } else {
                dataDir = System.getProperty("user.home") + "/.local/share";
            }
        }
        return normalize(dataDir + "/cyan_potion");
    }

    /**
     * Creates the necessary directories if they don't exist.
     *
     * @param path the directory path to create
     * @return true if the directory exists or was created successfully
     */
    public static boolean ensureDirectoryExists(String path) {
        if (path == null) {
            return false;
        }
        File dir = new File(path);
        if (dir.exists()) {
            return dir.isDirectory();
        }
        return dir.mkdirs();
    }

    /**
     * Checks if the given path is an absolute path.
     *
     * @param path the path to check
     * @return true if the path is absolute
     */
    public static boolean isAbsolutePath(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        // Unix absolute path
        if (path.startsWith("/")) {
            return true;
        }
        // Windows absolute path (e.g., C:/path or C:\path)
        if (path.length() >= 2 && Character.isLetter(path.charAt(0)) && path.charAt(1) == ':') {
            return true;
        }
        return false;
    }
}
