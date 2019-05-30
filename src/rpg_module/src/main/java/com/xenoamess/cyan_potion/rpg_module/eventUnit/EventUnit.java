/*
 * MIT License
 *
 * Copyright (c) 2019 XenoAmess
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

package com.xenoamess.cyan_potion.rpg_module.eventUnit;

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
 * @author XenoAmess
 */
public class EventUnit extends Unit {
    private EventUnitJson eventUnitJson;
    private List<EventPage> eventPages = new ArrayList<>();

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
            this.getPicture().setBindable(imageJson.getBindable(scene.getGameWindow().getGameManager().getResourceManager()));
        }
    }

    void initFromEventUnitJson(EventUnitJson eventUnitJson) {
        if (this.getEventUnitJson() != eventUnitJson) {
            this.setEventUnitJson(eventUnitJson);
        }
        //        this.
    }


    public EventUnitJson getEventUnitJson() {
        return eventUnitJson;
    }

    public void setEventUnitJson(EventUnitJson eventUnitJson) {
        this.eventUnitJson = eventUnitJson;
    }

    public List<EventPage> getEventPages() {
        return eventPages;
    }

    public void setEventPages(List<EventPage> eventPages) {
        this.eventPages = eventPages;
    }
}
