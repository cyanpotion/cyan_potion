package com.xenoamess.cyan_potion.rpg_module.eventUnit;

import com.xenoamess.cyan_potion.coordinate.AbstractScene;
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

    public EventUnit(AbstractScene scene, EventUnitJson eventUnitJson) {
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
            this.setBindable(imageJson.getBindable(scene.getGameWindow().getGameManager().getResourceManager()));
        }
    }


    //    public EventUnit(Transform transform, String walkingFileURI,
    //    EventUnitJson eventUnitJson) {
    //        super(transform, walkingFileURI);
    //        this.eventUnitJson = eventUnitJson;
    //    }

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
