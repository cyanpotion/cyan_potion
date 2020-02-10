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

package com.xenoamess.cyan_potion.base.setting_file;

import com.xenoamess.commons.version.Version;
import com.xenoamess.x8l.X8lTree;

/**
 * <p>Abstract AbstractSettingFileParser class.</p>
 *
 * @author XenoAmess
 * @version 0.157.0
 */
public abstract class AbstractSettingFileParser {
    private final Version settingFormatVersion;

    /**
     * <p>Constructor for AbstractSettingFileParser.</p>
     *
     * @param settingFormatVersionString a {@link java.lang.String} object.
     */
    protected AbstractSettingFileParser(String settingFormatVersionString) {
        this(new Version(settingFormatVersionString));
    }

    /**
     * <p>Constructor for AbstractSettingFileParser.</p>
     *
     * @param settingFormatVersion a {@link com.xenoamess.commons.version.Version} object.
     */
    protected AbstractSettingFileParser(Version settingFormatVersion) {
        this.settingFormatVersion = settingFormatVersion;
    }

    /**
     * <p>Getter for the field <code>settingFormatVersion</code>.</p>
     *
     * @return a {@link com.xenoamess.commons.version.Version} object.
     */
    public Version getSettingFormatVersion() {
        return settingFormatVersion;
    }

    /**
     * <p>parse.</p>
     *
     * @param x8lTree a {@link com.xenoamess.x8l.X8lTree} object.
     * @return a {@link com.xenoamess.cyan_potion.base.setting_file.GameSettings} object.
     */
    public abstract GameSettings parse(X8lTree x8lTree);
}
