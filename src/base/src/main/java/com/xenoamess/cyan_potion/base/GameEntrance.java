package com.xenoamess.cyan_potion.base;

/**
 * @author XenoAmess
 */
public class GameEntrance {
    public static void main(String[] args) {
        GameManager gameManager = new GameManager(args);
        gameManager.startup();
    }
}