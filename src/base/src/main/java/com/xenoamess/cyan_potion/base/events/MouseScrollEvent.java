package com.xenoamess.cyan_potion.base.events;

import com.xenoamess.cyan_potion.base.DataCenter;
import com.xenoamess.cyan_potion.base.GameWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @author XenoAmess
 */
public class MouseScrollEvent implements Event {
    private static final Logger LOGGER = LoggerFactory.getLogger(MouseScrollEvent.class);

    private final long window;
    private final double xoffset;
    private final double yoffset;

    public MouseScrollEvent(long window, double xoffset, double yoffset) {
        super();
        this.window = window;
        this.xoffset = xoffset;
        this.yoffset = yoffset;
    }

    @Override
    public Set<Event> apply(Object object) {

        LOGGER.debug("MouseScrollEvent : {} {} {}", getWindow(), getXoffset(), getYoffset());

        //        GameManager gameManager = DataCenter.currentGameManager;
        GameWindow gameWindow = DataCenter.getGameWindow(getWindow());
//        switch (action) {
//            case 0:
//                gameWindow.input.keyRelease(key);
//                break;
//            case 1:
//                gameWindow.input.keyPress(key);
//                break;
//            case 2:
//                break;
//            default:
//        }
        return gameWindow.getGameManager().getGameWindowComponentTree().process(this);
    }

    public long getWindow() {
        return window;
    }

    public double getXoffset() {
        return xoffset;
    }

    public double getYoffset() {
        return yoffset;
    }
}
