package com.xenoamess.cyan_potion.base.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * The event to describe a windows resize action.
 * Not implemented yet.
 *
 * @author XenoAmess
 */
public class WindowResizeEvent implements Event {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(WindowResizeEvent.class);

    private final long window;
    private final int width;
    private final int height;

    public WindowResizeEvent(long window, int width, int height) {
        super();
        this.window = window;
        this.width = width;
        this.height = height;
    }

    @Override
    public Set<Event> apply(Object object) {
        //TODO will do if we do implement windows resize.
        return null;
    }

    public long getWindow() {
        return window;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
