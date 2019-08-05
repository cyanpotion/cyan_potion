package com.xenoamess.cyan_potion.base.commons.areas;

/**
 * @author XenoAmess
 */
public interface AbstractMutableArea extends AbstractArea {

    @Override
    default boolean ifMutable() {
        return true;
    }

    /**
     * <p>Setter for the field <code>centerPosX</code>.</p>
     *
     * @param centerPosX a float.
     */
    void setCenterPosX(float centerPosX);

    /**
     * <p>Setter for the field <code>centerPosY</code>.</p>
     *
     * @param centerPosY a float.
     */
    void setCenterPosY(float centerPosY);

    /**
     * <p>Setter for the field <code>width</code>.</p>
     *
     * @param width a float.
     */
    void setWidth(float width);

    /**
     * <p>Setter for the field <code>height</code>.</p>
     *
     * @param height a float.
     */
    void setHeight(float height);
}
