package com.xenoamess.cyan_potion.base.events;

public class Events {
    public static boolean ifNullOrEmpty(Event event) {
        if (event == null) {
            return true;
        }
        if (event == EmptyEvent.EMPTY_EVENT) {
            return true;
        }
        return event == EmptyMainThreadEvent.EMPTY_MAIN_THREAD_EVENT;
    }
}
