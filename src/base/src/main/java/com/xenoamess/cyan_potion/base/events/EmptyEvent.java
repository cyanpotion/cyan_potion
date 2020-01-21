package com.xenoamess.cyan_potion.base.events;

public interface EmptyEvent extends Event {
    static boolean isNullOrEmpty(Event event) {
        return event == null || event instanceof EmptyEvent;
    }
}
