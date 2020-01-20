package com.xenoamess.cyan_potion.base.commons.areas;

/**
 * @author XenoAmess
 */
public interface AbstractMutableArea extends AbstractArea {

    @Override
    default boolean ifMutable() {
        return true;
    }

    void setLeftTopPosX(float leftTopPosX);

    void setLeftTopPosY(float leftTopPosY);

    /**
     * <p>Setter for the field <code>centerPosX</code>.</p>
     *
     * @param newCenterPosX a float.
     */
    default void setCenterPosX(float newCenterPosX) {
        this.setLeftTopPosX(newCenterPosX - this.getWidth() / 2F);
    }

    /**
     * <p>Setter for the field <code>centerPosY</code>.</p>
     *
     * @param newCenterPosY a float.
     */
    default void setCenterPosY(float newCenterPosY) {
        this.setLeftTopPosY(newCenterPosY - this.getHeight() / 2F);
    }

    /**
     * <p>setSize.</p>
     *
     * @param width  a float.
     * @param height a float.
     */
    default void setSize(float width, float height) {
        this.setWidth(width);
        this.setHeight(height);
    }

    /**
     * <p>setCenterPos.</p>
     *
     * @param leftTopPosX a float.
     * @param leftTopPoxY a float.
     */
    default void setLeftTopPos(float leftTopPosX, float leftTopPoxY) {
        this.setLeftTopPosX(leftTopPosX);
        this.setLeftTopPosY(leftTopPoxY);
    }

    /**
     * <p>setCenterPos.</p>
     *
     * @param centerPosX a float.
     * @param centerPosY a float.
     */
    default void setCenterPos(float centerPosX, float centerPosY) {
        this.setCenterPosX(centerPosX);
        this.setCenterPosY(centerPosY);
    }

    /**
     * <p>setCenter.</p>
     *
     * @param abstractArea area
     */
    default void setCenter(AbstractArea abstractArea) {
        this.setCenterPosX(abstractArea.getCenterPosX());
        this.setCenterPosY(abstractArea.getCenterPosY());
    }

    /**
     * <p>setSize.</p>
     *
     * @param abstractArea area
     */
    default void setSize(AbstractArea abstractArea) {
        this.setWidth(abstractArea.getWidth());
        this.setHeight(abstractArea.getHeight());
    }

    /**
     * <p>cover.</p>
     *
     * @param abstractArea area
     */
    default void cover(AbstractArea abstractArea) {
        this.setSize(abstractArea);
        this.setCenter(abstractArea);
    }

    /**
     * <p>move.</p>
     *
     * @param centerMovementX a float.
     * @param centerMovementY a float.
     */
    default void move(float centerMovementX, float centerMovementY) {
        this.moveX(centerMovementX);
        this.moveY(centerMovementY);
    }

    /**
     * <p>moveX.</p>
     *
     * @param centerMovementX a float.
     */
    default void moveX(float centerMovementX) {
        this.setCenterPosX(this.getCenterPosX() + centerMovementX);
    }

    /**
     * <p>moveY.</p>
     *
     * @param centerMovementY a float.
     */
    default void moveY(float centerMovementY) {
        this.setCenterPosY(this.getCenterPosY() + centerMovementY);
    }

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

    /**
     * <p>enlargeWidth.</p>
     *
     * @param newWidth a float.
     */
    default void enlargeWidth(float newWidth) {
        this.setWidth(this.getWidth() + newWidth);
    }

    /**
     * <p>enlargeHeight.</p>
     *
     * @param newHeight a float.
     */
    default void enlargeHeight(float newHeight) {
        this.setHeight(this.getHeight() + newHeight);
    }

    /**
     * <p>enlargeSize.</p>
     *
     * @param newWidth  a float.
     * @param newHeight a float.
     */
    default void enlargeSize(float newWidth, float newHeight) {
        this.enlargeWidth(newWidth);
        this.enlargeHeight(newHeight);
    }

    /**
     * <p>scaleWidth.</p>
     *
     * @param ratio a float.
     */
    default void scaleWidth(float ratio) {
        this.setWidth(this.getWidth() * ratio);
    }

    /**
     * <p>scaleHeight.</p>
     *
     * @param ratio a float.
     */
    default void scaleHeight(float ratio) {
        this.setHeight(this.getHeight() * ratio);
    }

    /**
     * <p>scaleSize.</p>
     *
     * @param ratio a float.
     */
    default void scaleSize(float ratio) {
        this.scaleWidth(ratio);
        this.scaleHeight(ratio);
    }
}
