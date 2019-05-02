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

import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.io.FileUtil;
import com.xenoamess.cyan_potion.base.memory.AbstractResource;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.*;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.*;

/**
 * @author XenoAmess
 */
public class WaveData extends AbstractResource implements AutoCloseable {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(WaveData.class);

    private int alBufferInt = -1;

    private Buffer data = null;

    private int format = -1;

    private int sampleRate = -1;

    /**
     * Creates a new WaveData
     *
     * @param data       actual wavedata
     * @param format     format of wave data
     * @param sampleRate sample rate of data
     */
    public void bake(Buffer data, int format, int sampleRate) {
        this.setData(data);
        this.setFormat(format);
        this.setSampleRate(sampleRate);
        generate();

        this.setMemorySize(data.capacity());
        this.getGameManager().getResourceManager().load(this);
    }

    public WaveData(GameManager gameManager, String resourceURI) {
        super(gameManager, resourceURI);
    }

//    public int getAL_Buffer_Int() {
//        return this.alBufferInt;
//    }


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
            throw new Error("Buffer Type not defined : " + data.getClass().getCanonicalName());
        }
    }

    public void generate() {
        if (this.getAlBufferInt() == -1) {
            this.setAlBufferInt(alGenBuffers());
            if (this.getData() == null) {
                LOGGER.error("AbstractResource {} : this.data == null . " +
                        "error?", this.getFullResourceURI());
            }
            alBufferData(this.getAlBufferInt(), this.getFormat(),
                    this.getData(), this.getSampleRate());
        }
    }

//    public static WaveData create(URL url) {
//        return create(new File(url.getFile()));
//    }
//
//    public static WaveData create(String path) {
//        return create(FileUtil.GetURL(path));
//    }


//    public static WaveData create(ByteBuffer buffer) {
//        WaveData res = null;
//        try {
//            res = new WaveData(org.newdawn.slick.openal.WaveData.create
//            (buffer));
//        } catch (Exception e) {
//            res = null;
//        }
//        if (res == null) {
//            res = ReadVorbis(buffer);
//        }
//        return res;
//    }


    public void readVorbis(ByteBuffer vorbis) {
        try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
            IntBuffer error = BufferUtils.createIntBuffer(1);
            long decoder = stb_vorbis_open_memory(vorbis, error, null);
            if (decoder == MemoryUtil.NULL) {
                throw new RuntimeException("Failed to open Ogg Vorbis file. " +
                        "Error: " + error.get(0));
            }
            stb_vorbis_get_info(decoder, info);
            int channels = info.channels();
            ShortBuffer pcm =
                    BufferUtils.createShortBuffer(stb_vorbis_stream_length_in_samples(decoder) * channels);
            stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm);
            stb_vorbis_close(decoder);

//            ByteBuffer byteBuffer = ByteBuffer.allocate(pcm.capacity() * 2);
//            byteBuffer.asShortBuffer().put(pcm);
//            byteBuffer.flip();
            this.bake(pcm, info.channels() == 1 ? AL_FORMAT_MONO16 :
                    AL_FORMAT_STEREO16, info.sample_rate());
        }
    }

    public void readVorbis(File resourceFile) {
        ByteBuffer vorbis = FileUtil.loadFileBuffer(resourceFile);
        readVorbis(vorbis);
    }


    public void loadAsMusic(String[] resourceFileURIStrings) {
        String resourceFilePath = resourceFileURIStrings[1];
        try {
            org.newdawn.slick.openal.WaveData slickWaveData =
                    org.newdawn.slick.openal.WaveData.create(FileUtil.getFile(resourceFilePath).toURI().toURL());
            this.bake(slickWaveData.data, slickWaveData.format,
                    slickWaveData.samplerate);
        } catch (Exception e) {
            this.readVorbis(FileUtil.getFile(resourceFilePath));
        }
    }

    @Override
    public void forceLoad() {
        //example       com.xenoamess.gearbar.render
        // .WalkingAnimation4Dirs:/www/img/characters/Actor1.png:4:0
        String[] resourceFileURIStrings = this.getFullResourceURI().split(":");

        String resourceFilePath = resourceFileURIStrings[1];
        String resourceType = resourceFileURIStrings[2];

        switch (resourceType) {
            case "music":
                this.loadAsMusic(resourceFileURIStrings);
                break;
            default:
                throw new Error("WaveData Type not defined : " + resourceType);
        }
    }

    @Override
    public void forceClose() {
        if (this.getAlBufferInt() != -1) {
            alDeleteBuffers(this.getAlBufferInt());
            this.setAlBufferInt(-1);
        }
        if (getData() != null) {
            getData().clear();
            this.setData(null);
        }
        this.setMemorySize(0);
    }


    public int getAlBufferInt() {
        return alBufferInt;
    }

    public void setAlBufferInt(int alBufferInt) {
        this.alBufferInt = alBufferInt;
    }

    /**
     * actual wave data
     *
     * @return return data Buffer.
     */
    public Buffer getData() {
        return data;
    }

    public void setData(Buffer data) {
        this.data = data;
    }

    /**
     * @return format type of data
     */
    public int getFormat() {
        return format;
    }

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

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }
}
