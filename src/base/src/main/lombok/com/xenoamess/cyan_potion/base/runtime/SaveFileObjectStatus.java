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
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * status of SaveFileObject
 *
 * @author XenoAmess
 * @version 0.161.1-SNAPSHOT
 * @see SaveFileObject
 */
@Data
public class SaveFileObjectStatus {
    @JsonIgnore
    private static final transient Logger LOGGER =
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
        //do nothing
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

}
