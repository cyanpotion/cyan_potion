package com.xenoamess.cyan_potion.base.events;

import com.xenoamess.cyan_potion.base.DataCenter;
import com.xenoamess.cyan_potion.base.GameWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @author XenoAmess
 */
public class CharEvent implements Event {
    private static final Logger LOGGER = LoggerFactory.getLogger(CharEvent.class);

    private final long window;
    private final int codepoint;


    public CharEvent(long window, int codepoint) {
        super();
        this.window = window;
        this.codepoint = codepoint;
    }

    @Override
    public Set<Event> apply(Object object) {
        LOGGER.debug("CharEvent : {}", (char) getCodepoint());

        GameWindow gameWindow = DataCenter.getGameWindow(getWindow());
        return gameWindow.getGameManager().getGameWindowComponentTree().process(this);
    }

    public long getWindow() {
        return window;
    }

    public int getCodepoint() {
        return codepoint;
    }
}
