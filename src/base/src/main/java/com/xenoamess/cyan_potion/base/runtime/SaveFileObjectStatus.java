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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.xenoamess.cyan_potion.base.DataCenter;

public class SaveFileObjectStatus {
    private String version;
    private long lastSaveTime;
    private long lastLoadTime;
    private int nowIndex;

    public SaveFileObjectStatus() {

    }

    public SaveFileObjectStatus(SaveFileObjectStatus saveFileObjectStatus) {
        if (saveFileObjectStatus != null) {
            this.version = saveFileObjectStatus.version;
            this.lastSaveTime = saveFileObjectStatus.lastSaveTime;
            this.lastLoadTime = saveFileObjectStatus.lastLoadTime;
            this.nowIndex = saveFileObjectStatus.nowIndex;
        }
    }

    public long getLastSaveTime() {
        return lastSaveTime;
    }

    public void setLastSaveTime(long lastSaveTime) {
        this.lastSaveTime = lastSaveTime;
    }

    public long getLastLoadTime() {
        return lastLoadTime;
    }

    public void setLastLoadTime(long lastLoadTime) {
        this.lastLoadTime = lastLoadTime;
    }

    public int getNowIndex() {
        return nowIndex;
    }

    public void setNowIndex(int nowIndex) {
        this.nowIndex = nowIndex;
    }

    @Override
    public String toString() {
        String result = "null";
        try {
            result = DataCenter.getObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
