package com.xenoamess.cyan_potion.rpg_module.jsons;

import java.io.Serializable;

/**
 * @author XenoAmess
 */
public class ConditionsJson implements Serializable {
    public int actorId;
    public boolean actorValid;
    public int itemId;
    public boolean itemValid;
    public String selfSwitchCh;
    public boolean selfSwitchValid;
    public int switch1Id;
    public boolean switch1Valid;
    public int switch2Id;
    public boolean switch2Valid;
    public int variableId;
    public boolean variableValid;
    public int variableValue;
}
