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

package com.xenoamess.cyan_potion.rpg_module;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Objects;

/**
 * A version class that represent a package's version.
 * It will read the /VERSION/${package name of this class}.VERSION file
 * That file is a template and shall be replaced and filled by maven when
 * start up.
 * However if the file is not found then VERSION will be VERSION_MISSING,
 * And a waring message will be write to System.err
 * <p>
 * See pom of this project for more information.
 *
 * @author XenoAmess
 */
public class Version implements Comparable<Version> {

    private final String versionString;

    public Version(String versionString) {
        this.versionString = versionString;
    }

    /**
     * current version of this component.
     */
    public static final String VERSION = loadCurrentVersion();
    public static final String VERSION_MISSING = "VersionMissing";

    public static int compareVersions(String versionString1, String versionString2) {
        String snapshotString = "-SNAPSHOT";
        if (versionString1.endsWith(snapshotString)) {
            versionString1 = versionString1.substring(0, versionString1.length() - snapshotString.length());
            versionString1 += ".-2";
        }
        if (versionString2.endsWith(snapshotString)) {
            versionString2 = versionString2.substring(0, versionString2.length() - snapshotString.length());
            versionString2 += ".-2";
        }
        String[] versionStringSegments1 = versionString1.split("\\.");
        String[] versionStringSegments2 = versionString2.split("\\.");
        int length = Math.max(versionStringSegments1.length, versionStringSegments2.length);
        for (int i = 0; i < length; i++) {
            String versionStringSegment1 = versionStringSegments1.length > i ? versionStringSegments1[i] : "-1";
            String versionStringSegment2 = versionStringSegments2.length > i ? versionStringSegments2[i] : "-1";

            if (versionStringSegment1.equals(versionStringSegment2)) {
                continue;
            }
            int versionIntSegment1 = Integer.parseInt(versionStringSegment1);
            int versionIntSegment2 = Integer.parseInt(versionStringSegment2);
            if (versionIntSegment1 != versionIntSegment2) {
                return versionIntSegment1 - versionIntSegment2;
            }
        }
        return 0;
    }

    @Override
    public int compareTo(Version version) {
        return compareVersions(this.versionString, version.versionString);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Version version = (Version) o;
        return Objects.equals(versionString, version.versionString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(versionString);
    }

    private static String loadCurrentVersion() {
        String res;
        res = Version.loadFile("/VERSION/" + Version.class.getPackage().getName() + ".VERSION");
        if ("".equals(res)) {
            res = VERSION_MISSING;
            System.err.println("version missing!");
        }
        return res;
    }

    /**
     * We do have a good reason not to use FileUtil class here.
     * Because I also use this in other projects.
     * So.
     *
     * @param resourceFilePath path of the resource file
     * @return the url returned
     */
    @SuppressWarnings("Duplicates")
    public static URL getURL(String resourceFilePath) {
        return Version.class.getResource(resourceFilePath);
    }

    @SuppressWarnings("Duplicates")
    public static String loadFile(String resourceFilePath) {
        String res = "";
        try (
                BufferedReader bufferedReader =
                        new BufferedReader(new InputStreamReader(getURL(resourceFilePath).openStream()))
        ) {
            final StringBuilder sb = new StringBuilder();
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

    @Override
    public String toString() {
        return this.versionString;
    }
}
