/*
 * Copyright (c) 2002-2004 LWJGL Project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'LWJGL' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.xenoamess.cyan_potion.base.com.xenoamess.cyan_potion.org.newdawn.slick.openal;

import org.lwjgl.openal.AL10;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/**
 * Utility class for loading wave files.
 *
 * @author Brian Matzon brian@matzon.dk
 * @version $Revision: 2286 $
 * $Id: WaveData.java 2286 2006-03-23 19:32:21Z matzon $
 * modified by XenoAmess
 */
public class WaveData {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(WaveData.class);

    /**
     * actual wave data
     */
    public final ByteBuffer data;

    /**
     * format type of data
     */
    public final int format;

    /**
     * sample rate of data
     */
    public final int sampleRate;

    /**
     * Creates a new WaveData
     *
     * @param data       actual wave data
     * @param format     format of wave data
     * @param sampleRate sample rate of data
     */
    private WaveData(ByteBuffer data, int format, int sampleRate) {
        this.data = data;
        this.format = format;
        this.sampleRate = sampleRate;
    }

    /**
     * Disposes the WaveData
     */
    public void dispose() {
        data.clear();
    }

    /**
     * Creates a WaveData container from the specified url
     *
     * @param path URL to file
     * @return WaveData containing data, or null if a failure occurred
     */
    public static WaveData create(URL path) {
        WaveData res = null;
        try {
            res = create(
                    AudioSystem.getAudioInputStream(
                            new BufferedInputStream(path.openStream())));
        } catch (Exception e) {
            LOGGER.warn("WaveData.create(URL path) fails:{}", path, e);
        }
        return res;
    }

    /**
     * Creates a WaveData container from the specified in the classpath
     *
     * @param path path to file (relative, and in classpath)
     * @return WaveData containing data, or null if a failure occurred
     */
    public static WaveData create(String path) {
        return create(WaveData.class.getClassLoader().getResource(path));
    }

    /**
     * Creates a WaveData container from the specified InputStream
     *
     * @param inputStream InputStream to read from
     * @return WaveData containing data, or null if a failure occurred
     */
    public static WaveData create(InputStream inputStream) {
        WaveData res = null;
        try {
            res = create(AudioSystem.getAudioInputStream(inputStream));
        } catch (Exception e) {
            LOGGER.warn("WaveData.create(InputStream inputStream) fails:{}", inputStream, e);
        }
        return res;
    }

    /**
     * Creates a WaveData container from the specified bytes
     *
     * @param buffer array of bytes containing the complete wave file
     * @return WaveData containing data, or null if a failure occurred
     */
    public static WaveData create(byte[] buffer) {
        WaveData res = null;
        try {
            res = create(
                    AudioSystem.getAudioInputStream(
                            new BufferedInputStream(new ByteArrayInputStream(buffer))));
        } catch (Exception e) {
            LOGGER.warn("WaveData.create(byte[] buffer) fails:{}", buffer, e);
        }
        return res;

    }

    /**
     * Creates a WaveData container from the specified ByteBuffer.
     * If the buffer is backed by an array, it will be used directly,
     * else the contents of the buffer will be copied using get(byte[]).
     *
     * @param buffer ByteBuffer containing sound file
     * @return WaveData containing data, or null if a failure occurred
     */
    public static WaveData create(ByteBuffer buffer) {
        WaveData res = null;
        try {
            byte[] bytes;
            if (buffer.hasArray()) {
                bytes = buffer.array();
            } else {
                bytes = new byte[buffer.capacity()];
                buffer.get(bytes);
            }
            res = create(bytes);
        } catch (Exception e) {
            LOGGER.warn("WaveData.create(ByteBuffer buffer) fails:{}", buffer, e);

        }
        return res;
    }

    /**
     * Creates a WaveData container from the specified stream
     *
     * @param ais AudioInputStream to read from
     * @return WaveData containing data, or null if a failure occurred
     */
    public static WaveData create(AudioInputStream ais) {
        //get format of data
        AudioFormat audioformat = ais.getFormat();

        // get channels
        int channels;
        if (audioformat.getChannels() == 1) {
            if (audioformat.getSampleSizeInBits() == 8) {
                channels = AL10.AL_FORMAT_MONO8;
            } else if (audioformat.getSampleSizeInBits() == 16) {
                channels = AL10.AL_FORMAT_MONO16;
            } else {
                throw new RuntimeException("Illegal sample size");
            }
        } else if (audioformat.getChannels() == 2) {
            if (audioformat.getSampleSizeInBits() == 8) {
                channels = AL10.AL_FORMAT_STEREO8;
            } else if (audioformat.getSampleSizeInBits() == 16) {
                channels = AL10.AL_FORMAT_STEREO16;
            } else {
                throw new RuntimeException("Illegal sample size");
            }
        } else {
            throw new RuntimeException("Only mono or stereo is supported");
        }

        //read data into buffer
        byte[] buf =
                new byte[audioformat.getChannels()
                        * (int) ais.getFrameLength()
                        * audioformat.getSampleSizeInBits()
                        / 8];
        int read;
        int total = 0;

        try {
            while ((read = ais.read(buf, total, buf.length - total)) != -1
                    && total < buf.length) {
                total += read;
            }
        } catch (IOException ioe) {
            return null;
        }

        //insert data into ByteBuffer
        ByteBuffer buffer = convertAudioBytes(buf,
                audioformat.getSampleSizeInBits() == 16);

        //create our result
        WaveData wavedata =
                new WaveData(buffer, channels,
                        (int) audioformat.getSampleRate());

        //close stream
        try {
            ais.close();
        } catch (IOException ioe) {
        }

        return wavedata;
    }

    /**
     * Convert the audio bytes into the stream
     *
     * @param audioBytes   The audio bytes
     * @param twoBytesData True if we using double byte data
     * @return The byte buffer of data
     */
    private static ByteBuffer convertAudioBytes(byte[] audioBytes,
                                                boolean twoBytesData) {
        ByteBuffer dest = ByteBuffer.allocateDirect(audioBytes.length);
        dest.order(ByteOrder.nativeOrder());
        ByteBuffer src = ByteBuffer.wrap(audioBytes);
        src.order(ByteOrder.LITTLE_ENDIAN);
        if (twoBytesData) {
            ShortBuffer destShort = dest.asShortBuffer();
            ShortBuffer srcShort = src.asShortBuffer();
            while (srcShort.hasRemaining()) {
                destShort.put(srcShort.get());
            }
        } else {
            while (src.hasRemaining()) {
                dest.put(src.get());
            }
        }
        dest.rewind();
        return dest;
    }
}
