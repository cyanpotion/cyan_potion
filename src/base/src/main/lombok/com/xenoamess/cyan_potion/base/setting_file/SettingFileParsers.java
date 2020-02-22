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
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

import static com.xenoamess.cyan_potion.base.setting_file.SettingFIleParser_0_3_0.version_0_3_0;

/**
 * <p>SettingFileParsers class.</p>
 *
 * @author XenoAmess
 * @version 0.161.1-SNAPSHOT
 */
@EqualsAndHashCode
@ToString
public class SettingFileParsers {
    private static final Map<Version, AbstractSettingFileParser> settingFileParserMap = new HashMap<>();

    static {
        settingFileParserMap.put(version_0_3_0, new SettingFIleParser_0_3_0());
    }

    /**
     * <p>getParser.</p>
     *
     * @param versionString a {@link java.lang.String} object.
     * @return a {@link com.xenoamess.cyan_potion.base.setting_file.AbstractSettingFileParser} object.
     */
    public static AbstractSettingFileParser getParser(String versionString) {
        return getParser(new Version(versionString));
    }

    /**
     * <p>getParser.</p>
     *
     * @param version a {@link com.xenoamess.commons.version.Version} object.
     * @return a {@link com.xenoamess.cyan_potion.base.setting_file.AbstractSettingFileParser} object.
     */
    public static AbstractSettingFileParser getParser(Version version) {
        return settingFileParserMap.get(version);
    }

    /**
     * <p>parse.</p>
     *
     * @param settingTree a {@link com.xenoamess.x8l.X8lTree} object.
     * @return a {@link com.xenoamess.cyan_potion.base.setting_file.GameSettings} object.
     */
    public static GameSettings parse(X8lTree settingTree) {
        Version version = getVersion(settingTree);
        return getParser(version).parse(settingTree);
    }

    /**
     * <p>getVersion.</p>
     *
     * @param settingTree a {@link com.xenoamess.x8l.X8lTree} object.
     * @return a {@link com.xenoamess.commons.version.Version} object.
     */
    public static Version getVersion(X8lTree settingTree) {
        return new Version(
                settingTree
                        .getRoot()
                        .getContentNodesFromChildrenThatNameIs("settingFile")
                        .get(0)
                        .getAttributes()
                        .get("settingFormatVersion")
        );
    }
}
