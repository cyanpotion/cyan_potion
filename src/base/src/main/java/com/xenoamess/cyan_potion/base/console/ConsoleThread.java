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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class ConsoleTalkThread implements Runnable {
    @JsonIgnore
    private static transient final Logger LOGGER =
            LoggerFactory.getLogger(ConsoleTalkThread.class);

    private final Socket socket;
    private final ConsoleThread consoleThread;

    ConsoleTalkThread(Socket socket, ConsoleThread consoleThread) {
        this.socket = socket;
        this.consoleThread = consoleThread;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        InputStream is = null;
        try {
            is = socket.getInputStream();
        } catch (IOException e) {
            LOGGER.error("ConsoleTalkThread fails:", e);
        }

        if (is == null) {
            return;
        }

        Scanner scanner = new Scanner(is);

        while (scanner.hasNext()) {
            String command = scanner.nextLine();
            dealWithCommand(command);
        }
    }

    /**
     * <p>dealWithCommand.</p>
     *
     * @param command command
     */
    protected void dealWithCommand(String command) {
        this.consoleThread.getGameManager().eventListAdd(new ConsoleEvent(command));
    }
}

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
 * @version 0.158.1
 * @see Console
 * @see GameManager
 * @see com.xenoamess.cyan_potion.base.GameManagerConfig
 */
public class ConsoleThread extends Thread {
    @JsonIgnore
    private static transient final Logger LOGGER =
            LoggerFactory.getLogger(ConsoleThread.class);

    private final GameManager gameManager;


    /**
     * <p>Constructor for ConsoleThread.</p>
     *
     * @param gameManager gameManager
     */
    public ConsoleThread(GameManager gameManager) {
        this.gameManager = gameManager;
        this.setDaemon(true);

    }

    /**
     * <p>shutdown.</p>
     */
    public void shutdown() {
        this.interrupt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        try (ServerSocket serverSocket =
                     new ServerSocket(this.getGameManager().getDataCenter().getConsolePort())) {
            final ExecutorService executorService =
                    Executors.newCachedThreadPool();
            while (!this.isInterrupted()) {
                newConsoleTalkThread(serverSocket, executorService);
            }
            executorService.shutdown();
        } catch (IOException e) {
            LOGGER.error("ConsoleThread serverSocket fails:", e);
        }
    }

    /**
     * <p>Getter for the field <code>gameManager</code>.</p>
     *
     * @return return
     */
    public GameManager getGameManager() {
        return gameManager;
    }

    private void newConsoleTalkThread(ServerSocket serverSocket, ExecutorService executorService) {
        try {
            final Socket socket = serverSocket.accept();
            executorService.execute(new ConsoleTalkThread(socket,
                    this));
        } catch (Exception e) {
            LOGGER.error("ConsoleThread socket fails:", e);
        }
    }
}
