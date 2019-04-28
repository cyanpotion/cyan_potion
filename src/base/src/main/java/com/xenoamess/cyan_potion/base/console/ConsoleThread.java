package com.xenoamess.cyan_potion.base.console;

import com.xenoamess.cyan_potion.base.DataCenter;
import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.events.ConsoleEvent;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class ConsoleTalkThread implements Runnable {
    final private Socket socket;
    final private ConsoleThread consoleThread;

    ConsoleTalkThread(Socket socket, ConsoleThread consoleThread) {
        this.socket = socket;
        this.consoleThread = consoleThread;
    }

    @Override
    public void run() {
        InputStream is = null;
        try {
            is = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(is);

        while (scanner.hasNext()) {
            String command = scanner.nextLine();
            dealWithCommand(command);
        }
    }

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
 * If you don't need this feature, you can just change config to not to start the thread.
 *
 * @author XenoAmess
 * @see Console
 * @see GameManager
 * @see com.xenoamess.cyan_potion.base.GameManagerConfig
 */
public class ConsoleThread extends Thread {
    final private GameManager gameManager;

    public ConsoleThread(GameManager gameManager) {
        this.gameManager = gameManager;
        this.setDaemon(true);
    }

    public void shutdown() {
        this.interrupt();
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(DataCenter.CONSOLE_PORT)) {
            final ExecutorService executorService = Executors.newCachedThreadPool();
            while (!this.isInterrupted()) {
                try {
                    final Socket socket = serverSocket.accept();
                    executorService.execute(new ConsoleTalkThread(socket, this));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            executorService.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public GameManager getGameManager() {
        return gameManager;
    }
}
