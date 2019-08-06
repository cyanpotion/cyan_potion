package com.xenoamess.cyan_potion.base.commons.areas;

/**
 * @author XenoAmess
 */
public interface AbstractImmutableArea extends AbstractArea {

    @Override
    default boolean ifMutable() {
        return false;
    }
}
