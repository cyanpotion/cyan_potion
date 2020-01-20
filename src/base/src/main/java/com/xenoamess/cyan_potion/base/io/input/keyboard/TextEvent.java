package com.xenoamess.cyan_potion.base.io.input.keyboard;

import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.events.EmptyEvent;
import com.xenoamess.cyan_potion.base.events.Event;
import net.jcip.annotations.GuardedBy;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * Text Event
 *
 * @author XenoAmess
 * @version 0.144.4
 */
public class TextEvent implements Event {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(TextEvent.class);

    private static class EmptyTextEvent extends TextEvent implements EmptyEvent {
        public EmptyTextEvent() {
            super(0, StringUtils.EMPTY);
        }
    }

    public static final TextEvent EMPTY = new EmptyTextEvent();

    private final long window;
    private final String contentString;

    /**
     * <p>Constructor for CharEvent.</p>
     *
     * @param window        window.
     * @param contentString contentString.
     */
    public TextEvent(long window, String contentString) {
        super();
        this.window = window;
        this.contentString = contentString;
    }

    /**
     * <p>Constructor for CharEvent.</p>
     *
     * @param window     window.
     * @param charEvents charEvents.
     */
    public TextEvent(long window, List<CharEvent> charEvents) {
        this(window, getStringFromCharEvents(charEvents));
    }

    public static String getStringFromCharEvents(List<CharEvent> charEvents) {
        StringBuilder stringBuilder = new StringBuilder();
        for (CharEvent event : charEvents) {
            stringBuilder.append((char) event.getCodepoint());
        }
        return stringBuilder.toString();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @GuardedBy("gameManager")
    public Set<Event> apply(GameManager gameManager) {
        if (gameManager.getDataCenter().isDebug()) {
            LOGGER.debug("{}", this);
        }
        return gameManager.getGameWindowComponentTree().process(this);
    }

    /**
     * <p>Getter for the field <code>window</code>.</p>
     *
     * @return a long.
     */
    public long getWindow() {
        return window;
    }

    /**
     * <p>Getter for the field <code>contentString</code>.</p>
     *
     * @return a String.
     */
    public String getContentString() {
        return contentString;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("TextEvent:{codepoint:");
        stringBuilder.append(this.getContentString());
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
