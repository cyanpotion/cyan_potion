/*
 * MIT License
 *
 * Copyright (c) 2020 XenoAmess
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.xenoamess.cyan_potion.rpg_module.event_unit;

import com.xenoamess.cyan_potion.base.visual.Picture;
import com.xenoamess.cyan_potion.coordinate.AbstractEntityScene;
import com.xenoamess.cyan_potion.rpg_module.RpgModuleDataCenter;
import com.xenoamess.cyan_potion.rpg_module.jsons.EventUnitJson;
import com.xenoamess.cyan_potion.rpg_module.jsons.ImageJson;
import com.xenoamess.cyan_potion.rpg_module.jsons.PageJson;
import com.xenoamess.cyan_potion.rpg_module.units.Unit;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>EventUnit class.</p>
 *
 * @author XenoAmess
 * @version 0.155.3
 */
public class EventUnit extends Unit {
    private EventUnitJson eventUnitJson;
    private List<EventPage> eventPages = new ArrayList<>();

    /**
     * <p>Constructor for EventUnit.</p>
     *
     * @param scene         a {@link com.xenoamess.cyan_potion.coordinate.AbstractEntityScene} object.
     * @param eventUnitJson eventUnitJson
     */
    public EventUnit(AbstractEntityScene scene, EventUnitJson eventUnitJson) {
        //todo
        super(scene,
                new Vector3f(eventUnitJson.x * RpgModuleDataCenter.TILE_SIZE,
                        eventUnitJson.y * RpgModuleDataCenter.TILE_SIZE,
                        Unit.DEFAULT_UNIT_LAYER),
                new Vector3f(RpgModuleDataCenter.TILE_SIZE,
                        RpgModuleDataCenter.TILE_SIZE, 0), null);
        this.setEventUnitJson(eventUnitJson);
        for (PageJson pageJson : eventUnitJson.pages) {
            getEventPages().add(new EventPage(pageJson, this));
        }
        if (!getEventPages().isEmpty()) {
            ImageJson imageJson = getEventPages().get(0).getPageJson().image;
            this.setPicture(new Picture(imageJson.getBindable(scene.getGameWindow().getGameManager().getResourceManager())));
            this.getPicture().cover(this);
        }
    }

    void initFromEventUnitJson(EventUnitJson eventUnitJson) {
        if (this.getEventUnitJson() != eventUnitJson) {
            this.setEventUnitJson(eventUnitJson);
        }
        //        this.
    }


    /**
     * <p>Getter for the field <code>eventUnitJson</code>.</p>
     *
     * @return return
     */
    public EventUnitJson getEventUnitJson() {
        return eventUnitJson;
    }

    /**
     * <p>Setter for the field <code>eventUnitJson</code>.</p>
     *
     * @param eventUnitJson eventUnitJson
     */
    public void setEventUnitJson(EventUnitJson eventUnitJson) {
        this.eventUnitJson = eventUnitJson;
    }

    /**
     * <p>Getter for the field <code>eventPages</code>.</p>
     *
     * @return return
     */
    public List<EventPage> getEventPages() {
        return eventPages;
    }

    /**
     * <p>Setter for the field <code>eventPages</code>.</p>
     *
     * @param eventPages eventPages
     */
    public void setEventPages(List<EventPage> eventPages) {
        this.eventPages = eventPages;
    }
}
