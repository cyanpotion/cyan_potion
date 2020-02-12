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

package com.xenoamess.cyan_potion.base.runtime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.xenoamess.cyan_potion.base.DataCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * status of SaveFileObject
 *
 * @author XenoAmess
 * @version 0.158.1-SNAPSHOT
 * @see SaveFileObject
 */
public class SaveFileObjectStatus {
    @JsonIgnore
    private static transient final Logger LOGGER =
            LoggerFactory.getLogger(SaveFileObjectStatus.class);

    private String version;
    private long lastSaveTime;
    private long lastLoadTime;
    /**
     * the current used file index of save file object.
     * if this saveFile is empty then this shall be -1.
     */
    private int nowIndex = -1;

    /**
     * <p>Constructor for SaveFileObjectStatus.</p>
     */
    public SaveFileObjectStatus() {
    }

    /**
     * <p>Constructor for SaveFileObjectStatus.</p>
     *
     * @param saveFileObjectStatus a {@link com.xenoamess.cyan_potion.base.runtime.SaveFileObjectStatus} object.
     */
    public SaveFileObjectStatus(SaveFileObjectStatus saveFileObjectStatus) {
        if (saveFileObjectStatus != null) {
            this.version = saveFileObjectStatus.version;
            this.lastSaveTime = saveFileObjectStatus.lastSaveTime;
            this.lastLoadTime = saveFileObjectStatus.lastLoadTime;
            this.nowIndex = saveFileObjectStatus.nowIndex;
        }
    }

    /**
     * <p>Getter for the field <code>lastSaveTime</code>.</p>
     *
     * @return a long.
     */
    public long getLastSaveTime() {
        return lastSaveTime;
    }

    /**
     * <p>Setter for the field <code>lastSaveTime</code>.</p>
     *
     * @param lastSaveTime a long.
     */
    public void setLastSaveTime(long lastSaveTime) {
        this.lastSaveTime = lastSaveTime;
    }

    /**
     * <p>Getter for the field <code>lastLoadTime</code>.</p>
     *
     * @return a long.
     */
    public long getLastLoadTime() {
        return lastLoadTime;
    }

    /**
     * <p>Setter for the field <code>lastLoadTime</code>.</p>
     *
     * @param lastLoadTime a long.
     */
    public void setLastLoadTime(long lastLoadTime) {
        this.lastLoadTime = lastLoadTime;
    }

    /**
     * <p>Getter for the field <code>nowIndex</code>.</p>
     *
     * @return nowIndex
     */
    public int getNowIndex() {
        return nowIndex;
    }

    /**
     * <p>Setter for the field <code>nowIndex</code>.</p>
     *
     * @param nowIndex nowIndex
     */
    public void setNowIndex(int nowIndex) {
        this.nowIndex = nowIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        String result = "{}";
        try {
            result = DataCenter.getObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            LOGGER.error("SaveFileObjectStatus cannot toString() : version={}, lastLoadTime={}, lastSaveTime={}", this.version, this.lastLoadTime, this.lastSaveTime, e);
        }
        return result;
    }

    /**
     * <p>Getter for the field <code>version</code>.</p>
     *
     * @return version
     */
    public String getVersion() {
        return version;
    }

    /**
     * <p>Setter for the field <code>version</code>.</p>
     *
     * @param version version
     */
    public void setVersion(String version) {
        this.version = version;
    }
}
