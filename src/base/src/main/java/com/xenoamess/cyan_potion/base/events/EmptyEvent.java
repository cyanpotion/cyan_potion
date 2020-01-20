package com.xenoamess.cyan_potion.base.events;

import com.xenoamess.cyan_potion.base.GameManager;

import java.util.Set;

public class EmptyEvent implements Event {
    public static final EmptyEvent EMPTY_EVENT = new EmptyEvent();

    private EmptyEvent() {

    }

    @Override
    public Set<Event> apply(GameManager gameManager) {
        return null;
    }
}
