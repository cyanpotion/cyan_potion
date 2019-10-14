package com.xenoamess.cyan_potion.base.io.url;

import com.xenoamess.cyan_potion.base.memory.AbstractResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownServiceException;

import static com.xenoamess.cyan_potion.base.io.url.CyanPotionURLStreamHandler.assureProtocolBeProtocolCyanPotion;

/**
 * @author XenoAmess
 */
public class CyanPotionURLConnection extends URLConnection {
    private String userDir;

    /**
     * Constructs a URL connection to the specified URL. A connection to
     * the object referenced by the URL is not created.
     *
     * @param url the specified URL.
     */
    protected CyanPotionURLConnection(String userDir, URL url) {
        super(url);
        this.userDir = userDir;

        assureProtocolBeProtocolCyanPotion(url);
        this.setUseCaches(true);
    }

    /**
     * Opens a communications link to the resource referenced by this
     * URL, if such a connection has not already been established.
     * <p>
     * If the {@code connect} method is called when the connection
     * has already been opened (indicated by the {@code connected}
     * field having the value {@code true}), the call is ignored.
     * <p>
     * URLConnection objects go through two phases: first they are
     * created, then they are connected.  After being created, and
     * before being connected, various options can be specified
     * (e.g., doInput and UseCaches).  After connecting, it is an
     * error to try to set them.  Operations that depend on being
     * connected, like getContentLength, will implicitly perform the
     * connection, if necessary.
     *
     * @throws IOException if an I/O error occurs while opening the
     *                     connection.
     * @see URLConnection#connected
     * @see #getConnectTimeout()
     * @see #setConnectTimeout(int)
     */
    @Override
    public void connect() throws IOException {

    }

    /**
     * Returns an input stream that reads from this open connection.
     * <p>
     * A SocketTimeoutException can be thrown when reading from the
     * returned input stream if the read timeout expires before data
     * is available for read.
     *
     * @return an input stream that reads from this open connection.
     * @throws IOException             if an I/O error occurs while
     *                                 creating the input stream.
     * @throws UnknownServiceException if the protocol does not support
     *                                 input.
     * @see #setReadTimeout(int)
     * @see #getReadTimeout()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        throw new UnknownServiceException("protocol doesn't support input");
    }

    /**
     * Returns an output stream that writes to this connection.
     *
     * @return an output stream that writes to this connection.
     * @throws IOException             if an I/O error occurs while
     *                                 creating the output stream.
     * @throws UnknownServiceException if the protocol does not support
     *                                 output.
     */
    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new UnknownServiceException("protocol doesn't support output");
    }

    @Override
    //TODO
    public AbstractResource getContent() throws IOException {
        String path = url.getPath();
        url.getProtocol();
        return null;
    }


    @Override
    public Object getContent(Class[] classes) throws IOException {
        return this.getContent();
    }
}
