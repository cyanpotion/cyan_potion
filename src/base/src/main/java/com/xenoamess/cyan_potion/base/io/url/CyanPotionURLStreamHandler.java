package com.xenoamess.cyan_potion.base.io.url;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * @author XenoAmess
 */
public class CyanPotionURLStreamHandler extends URLStreamHandler {
    public static final String PROTOCOL_CYAN_POTION = "cyan_potion";

    private String userDir;

    public CyanPotionURLStreamHandler(String userDir) {
        this.userDir = userDir;
    }

    static void assureProtocolBeProtocolCyanPotion(URL url) {
        String protocol = url.getProtocol();
        if (!PROTOCOL_CYAN_POTION.equalsIgnoreCase(protocol)) {
            throw new IllegalArgumentException(String.format("Wrong protocol.\nExpected : %s , Get : %s",
                    PROTOCOL_CYAN_POTION, protocol));
        }
    }

    /**
     * Opens a connection to the object referenced by the
     * {@code URL} argument.
     * This method should be overridden by a subclass.
     *
     * <p>If for the handler's protocol (such as HTTP or JAR), there
     * exists a public, specialized URLConnection subclass belonging
     * to one of the following packages or one of their subpackages:
     * java.lang, java.io, java.util, java.net, the connection
     * returned will be of that subclass. For example, for HTTP an
     * HttpURLConnection will be returned, and for JAR a
     * JarURLConnection will be returned.
     *
     * @param url the URL that this connects to.
     * @return a {@code URLConnection} object for the {@code URL}.
     * @throws IOException if an I/O error occurs while opening the
     *                     connection.
     */
    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        assureProtocolBeProtocolCyanPotion(url);
        return new CyanPotionURLConnection(this.userDir, url);
    }
}
