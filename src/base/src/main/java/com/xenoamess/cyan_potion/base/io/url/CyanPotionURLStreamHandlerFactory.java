package com.xenoamess.cyan_potion.base.io.url;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.xenoamess.cyan_potion.base.io.url.CyanPotionURLStreamHandler.PROTOCOL_CYAN_POTION;

/**
 * @author XenoAmess
 */
public class CyanPotionURLStreamHandlerFactory implements URLStreamHandlerFactory {
    private String userDir;

    public CyanPotionURLStreamHandlerFactory() {
        this(generateUserDir());
    }

    public CyanPotionURLStreamHandlerFactory(String userDir) {
        this.userDir = userDir;
    }

    private static String generateUserDir() {
        Path currentRelativePath = Paths.get("");
        String userDir = currentRelativePath.toAbsolutePath().toString();
        return userDir;
    }

    private final CyanPotionURLStreamHandler cyanPotionURLStreamHandler = new CyanPotionURLStreamHandler(this.userDir);

    /**
     * Creates a new {@code URLStreamHandler} instance with the specified
     * protocol.
     *
     * @param protocol the protocol ("{@code ftp}",
     *                 "{@code http}", "{@code nntp}", etc.).
     * @return a {@code URLStreamHandler} for the specific protocol, or {@code
     * null} if this factory cannot create a handler for the specific
     * protocol
     * @see URLStreamHandler
     */
    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if (PROTOCOL_CYAN_POTION.equalsIgnoreCase(protocol)) {
            return cyanPotionURLStreamHandler;
        }
        return null;
    }
}
