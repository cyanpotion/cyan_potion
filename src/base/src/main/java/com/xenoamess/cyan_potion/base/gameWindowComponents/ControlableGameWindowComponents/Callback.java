package com.xenoamess.cyan_potion.base.gameWindowComponents.ControlableGameWindowComponents;

import com.xenoamess.cyan_potion.base.events.Event;

/**
 * @author XenoAmess
 */
public interface Callback {
    default Event invoke(Event e) {
        return e;
    }
}
