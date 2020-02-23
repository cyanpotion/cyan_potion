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

package com.xenoamess.cyan_potion.base.console;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.SubManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;


/**
 * A thread to deal with console input.
 * Console input is designed to be used when debug.
 * When the GameManager init, it will start a ConsoleThread.
 * You can run Console to start a Console, and write your commands to Console.
 * The commands you wrote will be sent to ConsoleThread using TCP-IP
 * This thread uses TCP-IP and player will receive prompting message about it.
 * If you don't need this feature, you can just change config to not to start
 * the thread.
 * <p>
 * A thread to deal with console input.
 * Console input is designed to be used when debug.
 * When the GameManager init,it will start a ConsoleThread.
 * You can run Console to start a Console,and write your commands to Console.
 * The commands you wrote will be sent to ConsoleThread using TCP-IP
 * This thread uses TCP-IP and player will receive prompting message about it.
 * If you don't need this feature, you can just change config to not to start
 * the thread.
 *
 * @author XenoAmess
 * @version 0.161.2-SNAPSHOT
 * @see Console
 * @see GameManager
 * @see com.xenoamess.cyan_potion.base.GameManagerConfig
 */
public class ConsoleTalkThreadManager extends SubManager {
    @JsonIgnore
    private static final transient Logger LOGGER =
            LoggerFactory.getLogger(ConsoleTalkThreadManager.class);


    /**
     * <p>Constructor for ConsoleThread.</p>
     *
     * @param gameManager gameManager
     */
    public ConsoleTalkThreadManager(GameManager gameManager) {
        super(gameManager);
    }

    @Override
    public void init() {
        try (final AsynchronousServerSocketChannel listener =
                     AsynchronousServerSocketChannel.open().bind(
                             new InetSocketAddress(
                                     this.getGameManager().getDataCenter().getConsolePort()
                             )
                     )
        ) {
            listener.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
                @Override
                public void completed(AsynchronousSocketChannel asynchronousSocketChannel, Void att) {
                    // Accept the next connection
                    listener.accept(null, this);

                    // Allocate a byte buffer (4K) to read from the client
                    ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
                    StringBuilder stringBuilder = new StringBuilder();
                    try {
                        while (true) {
                            // Read the next line
                            int bytesRead = asynchronousSocketChannel.read(byteBuffer).get();
                            if (bytesRead != -1) {
                                break;
                            }

                            // Make the buffer ready to read
                            byteBuffer.flip();

                            // Convert the buffer into a line
                            byte[] lineBytes = new byte[bytesRead];
                            byteBuffer.get(lineBytes, 0, bytesRead);
                            String nowString = new String(lineBytes);

                            if (nowString.contains("\n")) {
                                String[] res = nowString.split("\n");
                                res[0] = stringBuilder.toString() + res[0];
                                for (int i = 0; i < res.length - 1; i++) {
                                    dealWithCommand(res[i]);
                                }
                                stringBuilder = new StringBuilder();
                                stringBuilder.append(res[res.length - 1]);
                            } else {
                                stringBuilder.append(nowString);
                            }

                            // Make the buffer ready to write
                            byteBuffer.clear();
                        }
                    } catch (Exception e) {
                        LOGGER.error("fails to load commands.", e);
                    }

                    try {
                        if (asynchronousSocketChannel.isOpen()) {
                            asynchronousSocketChannel.close();
                        }
                    } catch (IOException e) {
                        LOGGER.error("fails to close asynchronousSocketChannel.", e);
                    }
                }

                @Override
                public void failed(Throwable exc, Void att) {
                    //do nothing
                }
            });
        } catch (IOException e) {
            LOGGER.error("ConsoleTalkThreadManager.init() fails,", e);
        }
    }

    /**
     * <p>dealWithCommand.</p>
     *
     * @param command command
     */
    protected void dealWithCommand(String command) {
        this.getGameManager().eventListAdd(new ConsoleEvent(command));
    }

    @Override
    public void update() {
        //do nothing
    }

    @Override
    public void close() {
        //do nothing
    }
}
