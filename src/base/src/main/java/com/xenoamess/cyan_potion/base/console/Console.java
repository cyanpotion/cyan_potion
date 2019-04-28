package com.xenoamess.cyan_potion.base.console;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author XenoAmess
 */
public class Console {
    public static void main(String[] args) {
        Socket socket = null;
        while (socket == null) {
            try {
                socket = new Socket("localhost", 13888);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        OutputStream os = null;
        try {
            os = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                os.write((scanner.nextLine() + "\n").getBytes());
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
