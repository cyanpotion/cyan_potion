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

import static org.lwjgl.BufferUtils.createByteBuffer;

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
        boolean success;

        ByteBuffer buffer = null;
        final String absolutePath = resourceFile.getAbsolutePath();
        Path path = Paths.get(absolutePath);
        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = BufferUtils.createByteBuffer((int) fc.size() + 1);
                while (fc.read(buffer) != -1) {
                    ;
                }
                success = true;
            } catch (IOException e) {
                e.printStackTrace();
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
            buffer = createByteBuffer((int) resourceFile.length() + 1);

            while (true) {
                int bytes = rbc.read(buffer);
                if (bytes == -1) {
                    break;
                }
                //                //                    if (buffer.remaining
                //                () == 0) {
                //                //                        buffer =
                //                resizeBuffer(buffer, buffer.capacity() * 3
                //                / 2); // 50%
                //                //                    }
            }
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
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
            LOGGER.error("getFile fail : {}", resourceFilePath);
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
            e.printStackTrace();
        }
        return res;
    }

    public static String loadFile(String resourceFilePath) {
        String res = "";
        try (
                BufferedReader bufferedReader =
                        new BufferedReader(new InputStreamReader(getURL(resourceFilePath).openStream()));
//                BufferedReader bufferedReader = new BufferedReader(new
//                InputStreamReader(new FileInputStream(file.getAbsoluteFile
//                ())));
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
            e.printStackTrace();
        }
        return res;
    }

    public static String loadFile(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            LOGGER.error("loadFile fail : {}", file);
            return "";
        }
        String res = "";
        try (
                BufferedReader bufferedReader =
                        new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsoluteFile())));
//                BufferedReader bufferedReader = new BufferedReader(new
//                InputStreamReader(new FileInputStream(file.getAbsoluteFile
//                ())));
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
            e.printStackTrace();
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
                FileWriter fileWriter = new FileWriter(file);
        ) {
            fileWriter.write(contentString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
