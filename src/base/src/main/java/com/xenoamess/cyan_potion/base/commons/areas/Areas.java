package com.xenoamess.cyan_potion.base.commons.areas;

/**
 * @author XenoAmess
 */
public class Areas {
    private Areas() {
    }

    /**
     * <p>generateImmutableArea.</p>
     *
     * @param centerPosX a float.
     * @param centerPosY a float.
     * @param width      a float.
     * @param height     a float.
     * @return return
     */
    public static SimpleImmutableArea generateImmutableArea(
            float centerPosX, float centerPosY, float width, float height) {
        return new SimpleImmutableArea(centerPosX, centerPosY, width, height);
    }

    /**
     * <p>generateImmutableArea.</p>
     *
     * @param abstractArea area
     * @return return
     */
    public static SimpleImmutableArea generateImmutableArea(
            AbstractArea abstractArea) {
        return new SimpleImmutableArea(abstractArea);
    }

    /**
     * <p>generateImmutableAreaFromLeftTop.</p>
     *
     * @param leftTopPosX a float.
     * @param leftTopPosY a float.
     * @param width       a float.
     * @param height      a float.
     * @return return
     */
    public static SimpleImmutableArea generateImmutableAreaFromLeftTop(
            float leftTopPosX, float leftTopPosY, float width, float height) {
        return new SimpleImmutableArea(leftTopPosX + width / 2F, leftTopPosY + height / 2F, width, height);
    }

    /**
     * <p>generateMutableArea.</p>
     *
     * @param centerPosX a float.
     * @param centerPosY a float.
     * @param width      a float.
     * @param height     a float.
     * @return return
     */
    public static SimpleMutableArea generateMutableArea(
            float centerPosX, float centerPosY, float width, float height) {
        return new SimpleMutableArea(centerPosX, centerPosY, width, height);
    }

    /**
     * <p>generateMutableArea.</p>
     *
     * @param abstractArea area
     * @return return
     */
    public static SimpleMutableArea generateMutableArea(
            AbstractArea abstractArea) {
        return new SimpleMutableArea(abstractArea);
    }

    /**
     * <p>generateMutableAreaFromLeftTop.</p>
     *
     * @param leftTopPosX a float.
     * @param leftTopPosY a float.
     * @param width       a float.
     * @param height      a float.
     * @return return
     */
    public static SimpleMutableArea generateMutableAreaFromLeftTop(
            float leftTopPosX, float leftTopPosY, float width, float height) {
        return new SimpleMutableArea(leftTopPosX + width / 2F, leftTopPosY + height / 2F, width, height);
    }
}
