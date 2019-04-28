package com.xenoamess.cyan_potion.base.io.input.key;

/**
 * @author XenoAmess
 */
public class Key {
    public static final int TYPE_XENOAMESS_KEY = -1;
    public static final int TYPE_KEY = 0;
    public static final int TYPE_MOUSE = 1;
    /**
     * not implemented yet
     */
    public static final int TYPE_JOYSTICK = 2;
    public static final int TYPE_GAMEPAD = 3;

    public static final Key NULL = new Key(-666, -666);

    private final int type;
    private final int key;

    public Key(int key) {
        this(TYPE_XENOAMESS_KEY, key);
    }

    public Key(int type, int key) {
        this.type = type;
        this.key = key;
    }

    @Override
    public boolean equals(Object object) {
        if (!object.getClass().equals(this.getClass())) {
            return false;
        }
        final Key key = (Key) (object);
        return (this.getType() == key.getType() && this.getKey() == key.getKey());
    }

    @Override
    public int hashCode() {
        return (this.getType() << 10) | this.getKey();
    }

    public int getType() {
        return type;
    }

    public int getKey() {
        return key;
    }
}
