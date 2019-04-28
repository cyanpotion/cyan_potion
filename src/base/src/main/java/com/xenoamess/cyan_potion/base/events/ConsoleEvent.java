package com.xenoamess.cyan_potion.base.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @author XenoAmess
 */
public class ConsoleEvent implements Event {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleEvent.class);

    private final String command;

    public ConsoleEvent(String command) {
        super();
        this.command = command;
    }

    @Override
    public Set<Event> apply(Object object) {
        //TODO should add some real console methods here.
        LOGGER.info(getCommand());
        return null;
    }

    public String getCommand() {
        return command;
    }
}
