package com.xenoamess.cyan_potion.base.events;

import java.util.Set;
import java.util.function.Function;

/**
 * @author XenoAmess
 */
@FunctionalInterface
public interface Event extends Function<Object, Set<Event>> {

}
