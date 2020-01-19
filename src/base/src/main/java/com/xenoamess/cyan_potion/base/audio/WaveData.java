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

package com.xenoamess.cyan_potion.base.audio;

import com.xenoamess.commons.io.FileUtils;
import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.exceptions.FailedToOpenOggVorbisFileException;
import com.xenoamess.cyan_potion.base.exceptions.UnexpectedBufferClassTypeException;
import com.xenoamess.cyan_potion.base.memory.AbstractResource;
import com.xenoamess.cyan_potion.base.memory.ResourceInfo;
import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import org.apache.commons.vfs2.FileObject;
import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.*;
import java.util.function.Function;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.*;

/**
 * <p>WaveData class.</p>
 *
 * @author XenoAmess
 * @version 0.143.0
 */
public class WaveData extends AbstractResource implements AutoCloseable {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(WaveData.class);

    private int alBufferInt = -1;

    private int format = -1;

    private int sampleRate = -1;

    /**
     * Creates a new WaveData
     *
     * @param data       actual wave data
     * @param format     format of wave data
     * @param sampleRate sample rate of data
     */
    public void bake(Buffer data, int format, int sampleRate) {
        this.setFormat(format);
        this.setSampleRate(sampleRate);
        generate(data);

        this.setMemorySize(data.capacity());
        this.getResourceManager().load(this);
    }

    /**
     * !!!NOTICE!!!
     * <p>
     * This class shall never build from this constructor directly.
     * You shall always use ResourceManager.fetchResource functions to get this instance.
     *
     * @param resourceManager resource Manager
     * @param resourceInfo    resourceInfo
     * @see ResourceManager
     */
    public WaveData(ResourceManager resourceManager, ResourceInfo resourceInfo) {
        super(resourceManager, resourceInfo);
    }

    /**
     * !!!NOTICE!!!
     * This function is used by reflection and don't delete it if you don't know about the plugin mechanism here.
     */
    public static final Function<GameManager, Void> PUT_WAVEDATA_LOADER_MUSIC = (GameManager gameManager) -> {
        gameManager.getResourceManager().putResourceLoader(WaveData.class, "music",
                (WaveData waveData) -> {
                    waveData.loadAsMusicWaveData(waveData.getResourceInfo());
                    return null;
                }
        );
        return null;
    };


    /**
     * <p>alBufferData.</p>
     *
     * @param bufferName a int.
     * @param format     a int.
     * @param data       a {@link java.nio.Buffer} object.
     * @param frequency  a int.
     */
    public static void alBufferData(int bufferName, int format, Buffer data,
                                    int frequency) {
        if (data instanceof ByteBuffer) {
            AL10.alBufferData(bufferName, format, (ByteBuffer) data, frequency);
        } else if (data instanceof ShortBuffer) {
            AL10.alBufferData(bufferName, format, (ShortBuffer) data,
                    frequency);
        } else if (data instanceof IntBuffer) {
            AL10.alBufferData(bufferName, format, (IntBuffer) data, frequency);
        } else if (data instanceof FloatBuffer) {
            AL10.alBufferData(bufferName, format, (FloatBuffer) data,
                    frequency);
        } else {
            throw new UnexpectedBufferClassTypeException("Unexpected buffer type here : " + data.getClass().getCanonicalName());
        }
    }

    private void generate(Buffer data) {
        this.setAlBufferInt(alGenBuffers());
        alBufferData(this.getAlBufferInt(), this.getFormat(),
                data, this.getSampleRate());
    }

    /**
     * <p>readVorbis.</p>
     *
     * @param vorbis vorbis
     */
    public void readVorbis(ByteBuffer vorbis) {
        try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
            IntBuffer error = MemoryUtil.memAllocInt(1);
            long decoder = stb_vorbis_open_memory(vorbis, error, null);
            if (decoder == 0) {
                throw new FailedToOpenOggVorbisFileException("Failed to open Ogg Vorbis file. " +
                        "Error: " + error.get(0));
            }
            MemoryUtil.memFree(error);

            stb_vorbis_get_info(decoder, info);
            int channels = info.channels();
            ShortBuffer pcm =
                    MemoryUtil.memAllocShort(stb_vorbis_stream_length_in_samples(decoder) * channels);
            stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm);
            stb_vorbis_close(decoder);

            this.bake(pcm, info.channels() == 1 ? AL_FORMAT_MONO16 :
                    AL_FORMAT_STEREO16, info.sample_rate());
            MemoryUtil.memFree(pcm);
        }
    }

    /**
     * <p>readVorbis.</p>
     *
     * @param resourceFileObject resourceFileObject
     */
    public void readVorbis(FileObject resourceFileObject) {
        ByteBuffer vorbis = FileUtils.loadBuffer(resourceFileObject, true);
        readVorbis(vorbis);
        MemoryUtil.memFree(vorbis);
    }


    private void loadAsMusicWaveData(ResourceInfo resourceInfo) {
        FileObject resourceFileObject = resourceInfo.fileObject;


        try {
            InputStream inputStream = resourceFileObject.getContent().getInputStream();
            com.xenoamess.cyan_potion.base.com.xenoamess.cyan_potion.org.newdawn.slick.openal.WaveData slickWaveData =
                    com.xenoamess.cyan_potion.base.com.xenoamess.cyan_potion.org.newdawn.slick.openal.WaveData.create(
                            inputStream
                    );
            this.bake(slickWaveData.data, slickWaveData.format, slickWaveData.sampleRate);
        } catch (Exception e) {
            this.readVorbis(resourceFileObject);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void forceClose() {
        if (this.getAlBufferInt() != -1) {
            alDeleteBuffers(this.getAlBufferInt());
            this.setAlBufferInt(-1);
        }
        this.setMemorySize(0);
    }


    /**
     * <p>Getter for the field <code>alBufferInt</code>.</p>
     *
     * @return a int.
     */
    public int getAlBufferInt() {
        return alBufferInt;
    }

    /**
     * <p>Setter for the field <code>alBufferInt</code>.</p>
     *
     * @param alBufferInt a int.
     */
    public void setAlBufferInt(int alBufferInt) {
        this.alBufferInt = alBufferInt;
    }

    /**
     * <p>Getter for the field <code>format</code>.</p>
     *
     * @return format type of data
     */
    public int getFormat() {
        return format;
    }

    /**
     * <p>Setter for the field <code>format</code>.</p>
     *
     * @param format a int.
     */
    public void setFormat(int format) {
        this.format = format;
    }

    /**
     * sample rate of data
     *
     * @return return sample rate.
     */
    public int getSampleRate() {
        return sampleRate;
    }

    /**
     * <p>Setter for the field <code>sampleRate</code>.</p>
     *
     * @param sampleRate a int.
     */
    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }
}
