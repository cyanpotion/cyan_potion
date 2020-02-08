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

import com.xenoamess.cyan_potion.rpg_module.jsons.PageJson;

/**
 * This class is designed here for this engine to have ability to deal with
 * event in some type of data.
 * However I don't like this logic.
 * So when you are developing without some machining utils, please use pages
 * as less as possible.
 *
 * @author XenoAmess
 * @version 0.155.4-SNAPSHOT
 */
public class EventPage {
    private PageJson pageJson;
    private EventUnit eventUnit;

    /**
     * <p>Constructor for EventPage.</p>
     *
     * @param pageJson  a {@link com.xenoamess.cyan_potion.rpg_module.jsons.PageJson} object.
     * @param eventUnit eventUnit
     */
    public EventPage(PageJson pageJson, EventUnit eventUnit) {
        this.setPageJson(pageJson);
        this.setEventUnit(eventUnit);
    }


    /**
     * <p>Getter for the field <code>pageJson</code>.</p>
     *
     * @return return
     */
    public PageJson getPageJson() {
        return pageJson;
    }

    /**
     * <p>Setter for the field <code>pageJson</code>.</p>
     *
     * @param pageJson pageJson
     */
    public void setPageJson(PageJson pageJson) {
        this.pageJson = pageJson;
    }

    /**
     * <p>Getter for the field <code>eventUnit</code>.</p>
     *
     * @return return
     */
    public EventUnit getEventUnit() {
        return eventUnit;
    }

    /**
     * <p>Setter for the field <code>eventUnit</code>.</p>
     *
     * @param eventUnit eventUnit
     */
    public void setEventUnit(EventUnit eventUnit) {
        this.eventUnit = eventUnit;
    }
}
