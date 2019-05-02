package com.xenoamess.cyan_potion.base.gameWindowComponents.Co

import com.xenoamess.cyan_potion.base.events.Event;

ntrolableGameWindowComponents;

/**
 * @author XenoAmess
 */
public interface Callback {
    default Event invoke(Event e) {
        return e;
    }
}
