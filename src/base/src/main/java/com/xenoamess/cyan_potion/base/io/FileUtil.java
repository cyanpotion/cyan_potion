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

import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author XenoAmess
 */
public class FileUtil {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(FileUtil.class);

    /**
     * Don't let anyone instantiate this class.
     */
    private FileUtil() {
    }

    /**
     * Resize buffer.
     * Do never use this to resize a buffer from MemUtil,
     * because If you do this I guess you can't use the MemUtil Buffer right.
     * If you want to resize MemUtil's Buffer, please learn about it first.
     *
     * @param buffer      old buffer to resize
     * @param newCapacity new buffer's capacity
     * @return resized new buffer
     */
    public static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        final ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

    /**
     * Reads the specified resource and returns the raw data as a ByteBuffer.
     *
     * @param resourceFile the resource file to read
     * @return the resource data
     */
    public static ByteBuffer loadFileBuffer(File resourceFile) {
        return loadFileBuffer(resourceFile, false);
    }


    /**
     * Reads the specified resource and returns the raw data as a ByteBuffer.
     * if ifUsingMemoryUtil == false, then use BufferUtil.
     * else, allocate it using MemoryUtil.
     *
     * @param resourceFile      the resource file to read
     * @param ifUsingMemoryUtil if using MemoryUtil here
     * @return the resource data
     */
    public static ByteBuffer loadFileBuffer(File resourceFile, boolean ifUsingMemoryUtil) {
        boolean success;

        ByteBuffer buffer = null;
        final String absolutePath = resourceFile.getAbsolutePath();
        Path path = Paths.get(absolutePath);
        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                if (ifUsingMemoryUtil) {
                    buffer = MemoryUtil.memAlloc((int) fc.size() + 1);
                } else {
                    buffer = BufferUtils.createByteBuffer((int) fc.size() + 1);
                }


                while (fc.read(buffer) != -1) {
                }
                success = true;
            } catch (IOException e) {
                if (buffer != null) {
                    buffer.clear();
                }
                success = false;
            }
            if (success) {
                buffer.flip();
                return buffer.slice();
            }
        }


        try (
                InputStream source = new FileInputStream(resourceFile);
                ReadableByteChannel rbc = Channels.newChannel(source)
        ) {

            if (ifUsingMemoryUtil) {
                buffer = MemoryUtil.memAlloc((int) resourceFile.length() + 1);
            } else {
                buffer = BufferUtils.createByteBuffer((int) resourceFile.length() + 1);
            }

            while (true) {
                int bytes = rbc.read(buffer);
                if (bytes == -1) {
                    break;
                }
            }
            success = true;
        } catch (IOException e) {
            success = false;
        }
        if (success) {
            buffer.flip();
            return buffer.slice();
        } else {
            LOGGER.error("loadFileBuffer fail : {}", resourceFile);
            return null;
        }
    }


    public static File getFile(String resourceFilePath) {
        final URL resUrl = getURL(resourceFilePath);
        if (resUrl == null) {
            return null;
        }
        return new File(resUrl.getFile().replaceAll("%20", " "));
    }

    public static URL getURL(String resourceFilePath) {
        final URL res = FileUtil.class.getResource(resourceFilePath);
        return res;
    }

    public static URI getURI(String resourceFilePath) {
        URI res = null;
        try {
            res = getURL(resourceFilePath).toURI();
        } catch (URISyntaxException e) {
            LOGGER.error("FileUtil.getURI(String resourceFilePath) fail", resourceFilePath, e);
        }
        return res;
    }


    public static String loadFile(InputStream inputStream) {
        assert (inputStream != null);
        String res = "";
        try (
                BufferedReader bufferedReader =
                        new BufferedReader(new InputStreamReader(inputStream))
        ) {
            final StringBuffer sb = new StringBuffer();
            String tmp;
            while (true) {
                tmp = bufferedReader.readLine();
                if (tmp == null) {
                    break;
                }
                sb.append(tmp);
                sb.append("\n");
            }
            res = sb.toString();
        } catch (IOException e) {
            LOGGER.error("FileUtil.loadFile(InputStream inputStream) fail", inputStream, e);
        }
        return res;
    }

    public static String loadFile(String resourceFilePath) {
        String res = "";
        try (InputStream inputStream = getURL(resourceFilePath).openStream()) {
            res = loadFile(inputStream);
        } catch (IOException e) {
            LOGGER.error("FileUtil.loadFile(String resourceFilePath) fail", resourceFilePath, e);
        }
        return res;
    }

    public static String loadFile(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            LOGGER.error("loadFile fail : {}", file);
            return "";
        }
        String res = "";
        try (FileInputStream fileInputStream = new FileInputStream(file.getAbsoluteFile())) {
            res = loadFile(fileInputStream);
        } catch (IOException e) {
            LOGGER.error("FileUtil.loadFile(File file) fail", file, e);
        }
        return res;
    }

    public static void saveFile(String resourceFilePath, String contentString) {
        saveFile(getFile(resourceFilePath), contentString);
    }

    public static void saveFile(File file, String contentString) {
        if (file == null) {
            LOGGER.error("saveFile fail : file=null");
            return;
        }
        //if is not a file.
        if (file.exists() && !file.isFile()) {
            return;
        }
        try (
                FileWriter fileWriter = new FileWriter(file)
        ) {
            fileWriter.write(contentString);
        } catch (IOException e) {
            LOGGER.error("FileUtil.saveFile(File file, String contentString) fail",
                    file, contentString, e);
        }
    }

}
