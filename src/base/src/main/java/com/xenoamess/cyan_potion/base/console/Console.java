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

import com.xenoamess.cyan_potion.base.DataCenter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>Console class.</p>
 *
 * @author XenoAmess
 * @version 0.143.0
 */
public class Console implements Runnable {
    private final AtomicBoolean alive = new AtomicBoolean(true);
    private int consolePort;

    /**
     * <p>Constructor for Console.</p>
     */
    public Console() {
        this(DataCenter.DEFAULT_CONSOLE_PORT);
    }

    /**
     * <p>Constructor for Console.</p>
     *
     * @param consolePort a int.
     */
    public Console(int consolePort) {
        this.consolePort = consolePort;
    }

    /**
     * <p>Getter for the field <code>alive</code>.</p>
     *
     * @return a boolean.
     */
    public boolean getAlive() {
        return this.alive.get();
    }

    /**
     * <p>Setter for the field <code>alive</code>.</p>
     *
     * @param alive a boolean.
     */
    public void setAlive(boolean alive) {
        this.alive.set(alive);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        try (Socket socket = new Socket("localhost", this.consolePort)) {
            OutputStream os = null;
            while (os == null) {
                try {
                    os = socket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Scanner scanner = new Scanner(System.in);
            while (this.getAlive()) {
                try {
                    os.write((scanner.nextLine() + "\n").getBytes());
                    os.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * if args[0] is a number, then return it.
     * otherwise return DataCenter.DEFAULT_CONSOLE_PORT
     *
     * @param args args.
     * @return console port int.
     * @see DataCenter#DEFAULT_CONSOLE_PORT
     */
    public static int getConsolePort(String[] args) {
        int res = DataCenter.DEFAULT_CONSOLE_PORT;
        try {
            if (args != null && args.length >= 1) {
                res = Integer.parseInt(args[0]);
            }
        } catch (NumberFormatException e) {
            res = DataCenter.DEFAULT_CONSOLE_PORT;
        }

        return res;
    }


    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
        new Thread(new Console(getConsolePort(args))).start();
    }
}
