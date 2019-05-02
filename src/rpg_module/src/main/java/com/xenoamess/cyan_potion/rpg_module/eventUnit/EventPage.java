package com.xenoamess.cyan_potion.rpg_module.eventUnit;

import com.xenoamess.cyan_potion.rpg_module.jsons.PageJson;

/**
 * This class is designed here for this engine to have ability to deal with
 * event in some type of data.
 * However I don't like this logic.
 * So when you are developing without some machining utils, please use pages
 * as less as possible.
 *
 * @author XenoAmess
 */

public class EventPage {
    private PageJson pageJson;
    private EventUnit eventUnit;

    public EventPage(PageJson pageJson, EventUnit eventUnit) {
        this.setPageJson(pageJson);
        this.setEventUnit(eventUnit);
    }


    public PageJson getPageJson() {
        return pageJson;
    }

    public void setPageJson(PageJson pageJson) {
        this.pageJson = pageJson;
    }

    public EventUnit getEventUnit() {
        return eventUnit;
    }

    public void setEventUnit(EventUnit eventUnit) {
        this.eventUnit = eventUnit;
    }
}
