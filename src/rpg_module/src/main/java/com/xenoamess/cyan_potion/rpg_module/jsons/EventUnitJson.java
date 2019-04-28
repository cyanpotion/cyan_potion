package com.xenoamess.cyan_potion.rpg_module.jsons;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author XenoAmess
 */
public class EventUnitJson implements Serializable {
    public int id;
    public String name;
    public String note;
    public ArrayList<PageJson> pages;
    public int x;
    public int y;
}
