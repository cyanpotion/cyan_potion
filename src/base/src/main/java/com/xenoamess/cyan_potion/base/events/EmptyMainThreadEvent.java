package com.xenoamess.cyan_potion.base.events;

import com.xenoamess.cyan_potion.base.GameManager;

import java.util.Set;

public class EmptyMainThreadEvent implements MainThreadEvent {
    public static final EmptyMainThreadEvent EMPTY_MAIN_THREAD_EVENT = new EmptyMainThreadEvent();

    private EmptyMainThreadEvent() {

    }

    @Override
    public Set<Event> apply(GameManager gameManager) {
        return null;
    }
}
