///*
// * MIT License
// *
// * Copyright (c) 2020 XenoAmess
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in all
// * copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// * SOFTWARE.
// */
//
//package com.xenoamess.cyan_potion.base.io.url;
//
//import com.xenoamess.cyan_potion.base.memory.AbstractResource;
//import org.apache.commons.lang3.NotImplementedException;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.URL;
//import java.net.URLConnection;
//import java.net.UnknownServiceException;
//
//import static com.xenoamess.cyan_potion.base.io.url.CyanPotionURLStreamHandler.assureProtocolBeProtocolCyanPotion;
//
///**
// * TODO This class is not stable yet. Will be removed or modified in future.
// *
// * @author XenoAmess
// * @version 0.162.3
// */
//public class CyanPotionURLConnection extends URLConnection {
//    private final String userDir;
//
//    /**
//     * Constructs a URL connection to the specified URL. A connection to
//     * the object referenced by the URL is not created.
//     *
//     * @param url     the specified URL.
//     * @param userDir a {@link java.lang.String} object.
//     */
//    protected CyanPotionURLConnection(String userDir, URL url) {
//        super(url);
//        this.userDir = userDir;
//
//        assureProtocolBeProtocolCyanPotion(url);
//        this.setUseCaches(true);
//    }
//
//    /**
//     * {@inheritDoc}
//     * <p>
//     * Opens a communications link to the resource referenced by this
//     * URL, if such a connection has not already been established.
//     * <p>
//     * If the {@code connect} method is called when the connection
//     * has already been opened (indicated by the {@code connected}
//     * field having the value {@code true}), the call is ignored.
//     * <p>
//     * URLConnection objects go through two phases: first they are
//     * created, then they are connected.  After being created, and
//     * before being connected, various options can be specified
//     * (e.g., doInput and UseCaches).  After connecting, it is an
//     * error to try to set them.  Operations that depend on being
//     * connected, like getContentLength, will implicitly perform the
//     * connection, if necessary.
//     *
//     * @see URLConnection#connected
//     * @see #getConnectTimeout()
//     * @see #setConnectTimeout(int)
//     */
//    @Override
//    public void connect() throws IOException {
//        //TODO
//        throw new NotImplementedException("connect()");
//    }
//
//    /**
//     * {@inheritDoc}
//     * <p>
//     * Returns an input stream that reads from this open connection.
//     * <p>
//     * A SocketTimeoutException can be thrown when reading from the
//     * returned input stream if the read timeout expires before data
//     * is available for read.
//     *
//     * @see #setReadTimeout(int)
//     * @see #getReadTimeout()
//     */
//    @Override
//    public InputStream getInputStream() throws IOException {
//        throw new UnknownServiceException("protocol doesn't support input");
//    }
//
//    /**
//     * {@inheritDoc}
//     * <p>
//     * Returns an output stream that writes to this connection.
//     */
//    @Override
//    public OutputStream getOutputStream() throws IOException {
//        throw new UnknownServiceException("protocol doesn't support output");
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    //TODO
//    public AbstractResource getContent() throws IOException {
//        String path = url.getPath();
//        url.getProtocol();
//        return null;
//    }
//
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public Object getContent(Class[] classes) throws IOException {
//        return this.getContent();
//    }
//}
