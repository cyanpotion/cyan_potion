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

package com.xenoamess.cyan_potion.base.io.url;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * TODO This class is not stable yet. Will be removed or modified in future.
 *
 * @author XenoAmess
 * @version 0.148.8
 */
public class CyanPotionURLStreamHandler extends URLStreamHandler {
    /**
     * Constant <code>PROTOCOL_CYAN_POTION="cyan_potion"</code>
     */
    public static final String PROTOCOL_CYAN_POTION = "cyan_potion";

    private String userDir;

    /**
     * <p>Constructor for CyanPotionURLStreamHandler.</p>
     *
     * @param userDir a {@link java.lang.String} object.
     */
    public CyanPotionURLStreamHandler(String userDir) {
        this.userDir = userDir;
    }

    static void assureProtocolBeProtocolCyanPotion(URL url) {
        String protocol = url.getProtocol();
        if (!PROTOCOL_CYAN_POTION.equalsIgnoreCase(protocol)) {
            throw new IllegalArgumentException(String.format("Wrong protocol : Expected = %s , Get = %s",
                    PROTOCOL_CYAN_POTION, protocol));
        }
    }

    /**
     * {@inheritDoc}
     * <p>
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
     */
    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        assureProtocolBeProtocolCyanPotion(url);
        return new CyanPotionURLConnection(this.userDir, url);
    }


//    /**
//     * Parses the string representation of a {@code URL} into a
//     * {@code URL} object.
//     * <p>
//     * If there is any inherited context, then it has already been
//     * copied into the {@code URL} argument.
//     * <p>
//     * The {@code parseURL} method of {@code URLStreamHandler}
//     * parses the string representation as if it were an
//     * {@code http} specification. Most URL protocol families have a
//     * similar parsing. A stream protocol handler for a protocol that has
//     * a different syntax must override this routine.
//     *
//     * @param   u       the {@code URL} to receive the result of parsing
//     *                  the spec.
//     * @param   spec    the {@code String} representing the URL that
//     *                  must be parsed.
//     * @param   start   the character index at which to begin parsing. This is
//     *                  just past the '{@code :}' (if there is one) that
//     *                  specifies the determination of the protocol name.
//     * @param   limit   the character position to stop parsing at. This is the
//     *                  end of the string or the position of the
//     *                  "{@code #}" character, if present. All information
//     *                  after the sharp sign indicates an anchor.
//     */
//    protected void parseURL(URL u, String spec, int start, int limit) {
//        // These fields may receive context content if this was relative URL
//        String protocol = u.getProtocol();
//        String authority = u.getAuthority();
//        String userInfo = u.getUserInfo();
//        String host = u.getHost();
//        int port = u.getPort();
//        String path = u.getPath();
//        String query = u.getQuery();
//
//        // This field has already been parsed
//        String ref = u.getRef();
//
//        boolean isRelPath = false;
//        boolean queryOnly = false;
//
//// FIX: should not assume query if opaque
//        // Strip off the query part
//        if (start < limit) {
//            int queryStart = spec.indexOf('?');
//            queryOnly = queryStart == start;
//            if ((queryStart != -1) && (queryStart < limit)) {
//                query = spec.substring(queryStart+1, limit);
//                if (limit > queryStart)
//                    limit = queryStart;
//                spec = spec.substring(0, queryStart);
//            }
//        }
//
//        int i = 0;
//        // Parse the authority part if any
//        boolean isUNCName = (start <= limit - 4) &&
//                (spec.charAt(start) == '/') &&
//                (spec.charAt(start + 1) == '/') &&
//                (spec.charAt(start + 2) == '/') &&
//                (spec.charAt(start + 3) == '/');
//        if (!isUNCName && (start <= limit - 2) && (spec.charAt(start) == '/') &&
//                (spec.charAt(start + 1) == '/')) {
//            start += 2;
//            i = spec.indexOf('/', start);
//            if (i < 0 || i > limit) {
//                i = spec.indexOf('?', start);
//                if (i < 0 || i > limit)
//                    i = limit;
//            }
//
//            host = authority = spec.substring(start, i);
//
//            int ind = authority.indexOf('@');
//            if (ind != -1) {
//                if (ind != authority.lastIndexOf('@')) {
//                    // more than one '@' in authority. This is not server based
//                    userInfo = null;
//                    host = null;
//                } else {
//                    userInfo = authority.substring(0, ind);
//                    host = authority.substring(ind+1);
//                }
//            } else {
//                userInfo = null;
//            }
//            if (host != null) {
//                // If the host is surrounded by [ and ] then its an IPv6
//                // literal address as specified in RFC2732
//                if (host.length()>0 && (host.charAt(0) == '[')) {
//                    if ((ind = host.indexOf(']')) > 2) {
//
//                        String nhost = host ;
//                        host = nhost.substring(0,ind+1);
//                        if (!IPAddressUtil.
//                                isIPv6LiteralAddress(host.substring(1, ind))) {
//                            throw new IllegalArgumentException(
//                                    "Invalid host: "+ host);
//                        }
//
//                        port = -1 ;
//                        if (nhost.length() > ind+1) {
//                            if (nhost.charAt(ind+1) == ':') {
//                                ++ind ;
//                                // port can be null according to RFC2396
//                                if (nhost.length() > (ind + 1)) {
//                                    port = Integer.parseInt(nhost, ind + 1,
//                                            nhost.length(), 10);
//                                }
//                            } else {
//                                throw new IllegalArgumentException(
//                                        "Invalid authority field: " + authority);
//                            }
//                        }
//                    } else {
//                        throw new IllegalArgumentException(
//                                "Invalid authority field: " + authority);
//                    }
//                } else {
//                    ind = host.indexOf(':');
//                    port = -1;
//                    if (ind >= 0) {
//                        // port can be null according to RFC2396
//                        if (host.length() > (ind + 1)) {
//                            port = Integer.parseInt(host, ind + 1,
//                                    host.length(), 10);
//                        }
//                        host = host.substring(0, ind);
//                    }
//                }
//            } else {
//                host = "";
//            }
//            if (port < -1)
//                throw new IllegalArgumentException("Invalid port number :" +
//                        port);
//            start = i;
//            // If the authority is defined then the path is defined by the
//            // spec only; See RFC 2396 Section 5.2.4.
//            if (authority != null && !authority.isEmpty())
//                path = "";
//        }
//
//        if (host == null) {
//            host = "";
//        }
//
//        // Parse the file path if any
//        if (start < limit) {
//            if (spec.charAt(start) == '/') {
//                path = spec.substring(start, limit);
//            } else if (path != null && !path.isEmpty()) {
//                isRelPath = true;
//                int ind = path.lastIndexOf('/');
//                String separator = "";
//                if (ind == -1 && authority != null)
//                    separator = "/";
//                path = path.substring(0, ind + 1) + separator +
//                        spec.substring(start, limit);
//
//            } else {
//                String separator = (authority != null) ? "/" : "";
//                path = separator + spec.substring(start, limit);
//            }
//        } else if (queryOnly && path != null) {
//            int ind = path.lastIndexOf('/');
//            if (ind < 0)
//                ind = 0;
//            path = path.substring(0, ind) + "/";
//        }
//        if (path == null)
//            path = "";
//
//        if (isRelPath) {
//            // Remove embedded /./
//            while ((i = path.indexOf("/./")) >= 0) {
//                path = path.substring(0, i) + path.substring(i + 2);
//            }
//            // Remove embedded /../ if possible
//            i = 0;
//            while ((i = path.indexOf("/../", i)) >= 0) {
//                /*
//                 * A "/../" will cancel the previous segment and itself,
//                 * unless that segment is a "/../" itself
//                 * i.e. "/a/b/../c" becomes "/a/c"
//                 * but "/../../a" should stay unchanged
//                 */
//                if (i > 0 && (limit = path.lastIndexOf('/', i - 1)) >= 0 &&
//                        (path.indexOf("/../", limit) != 0)) {
//                    path = path.substring(0, limit) + path.substring(i + 3);
//                    i = 0;
//                } else {
//                    i = i + 3;
//                }
//            }
//            // Remove trailing .. if possible
//            while (path.endsWith("/..")) {
//                i = path.indexOf("/..");
//                if ((limit = path.lastIndexOf('/', i - 1)) >= 0) {
//                    path = path.substring(0, limit+1);
//                } else {
//                    break;
//                }
//            }
//            // Remove starting .
//            if (path.startsWith("./") && path.length() > 2)
//                path = path.substring(2);
//
//            // Remove trailing .
//            if (path.endsWith("/."))
//                path = path.substring(0, path.length() -1);
//        }
//
//        setURL(u, protocol, host, port, authority, userInfo, path, query, ref);
//    }
//
//    /**
//     * Provides the default equals calculation. May be overridden by handlers
//     * for other protocols that have different requirements for equals().
//     * This method requires that none of its arguments is null. This is
//     * guaranteed by the fact that it is only called by java.net.URL class.
//     * @param u1 a URL object
//     * @param u2 a URL object
//     * @return {@code true} if the two urls are
//     * considered equal, i.e. they refer to the same
//     * fragment in the same file.
//     * @since 1.3
//     */
//    protected boolean equals(URL u1, URL u2) {
//        String ref1 = u1.getRef();
//        String ref2 = u2.getRef();
//        return (ref1 == ref2 || (ref1 != null && ref1.equals(ref2))) &&
//                sameFile(u1, u2);
//    }
//
//    /**
//     * Compares the host components of two URLs.
//     * @param u1 the URL of the first host to compare
//     * @param u2 the URL of the second host to compare
//     * @return  {@code true} if and only if they
//     * are equal, {@code false} otherwise.
//     * @since 1.3
//     */
//    protected boolean hostsEqual(URL u1, URL u2) {
//        InetAddress a1 = getHostAddress(u1);
//        InetAddress a2 = getHostAddress(u2);
//        // if we have internet address for both, compare them
//        if (a1 != null && a2 != null) {
//            return a1.equals(a2);
//            // else, if both have host names, compare them
//        } else if (u1.getHost() != null && u2.getHost() != null)
//            return u1.getHost().equalsIgnoreCase(u2.getHost());
//        else
//            return u1.getHost() == null && u2.getHost() == null;
//    }
//
//    /**
//     * Converts a {@code URL} of a specific protocol to a
//     * {@code String}.
//     *
//     * @param   u   the URL.
//     * @return  a string representation of the {@code URL} argument.
//     */
//    protected String toExternalForm(URL u) {
//        String s;
//        return u.getProtocol()
//                + ':'
//                + ((s = u.getAuthority()) != null && !s.isEmpty()
//                ? "//" + s : "")
//                + ((s = u.getPath()) != null ? s : "")
//                + ((s = u.getQuery()) != null ? '?' + s : "")
//                + ((s = u.getRef()) != null ? '#' + s : "");
//    }

}
