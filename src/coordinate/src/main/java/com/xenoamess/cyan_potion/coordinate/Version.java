package com.xenoamess.cyan_potion.coordinate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * A version class that represent a package's version.
 * It will read the /VERSION/${package name of this class}.VERSION file
 * That file is a template and shall be replaced and filled by maven when start up.
 * However if the file is not found then VERSION will be VERSION_MISSING,
 * And a waring message will be write to System.err
 * <p>
 * See pom of this project for more information.
 *
 * @author XenoAmess
 */
public class Version {

    private Version() {

    }

    public static final String VERSION = getVersion();
    public static final String VERSION_MISSING = "VersionMissing";

    private static String getVersion() {
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
     * @param resourceFilePath
     * @return
     */
    public static URL getURL(String resourceFilePath) {
        final URL res = Version.class.getResource(resourceFilePath);
        return res;
    }

    public static String loadFile(String resourceFilePath) {
        String res = "";
        try (
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getURL(resourceFilePath).openStream()));
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
}
