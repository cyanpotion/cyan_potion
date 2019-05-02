package com.xenoamess.cyan_potion.rpg_module.jsons;

import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.base.render.Texture;

import java.io.Serializable;

/**
 * @author XenoAmess
 */
public class ImageJson implements Serializable {
    public int tileId;
    public String characterName;
    public int direction;
    public int pattern;
    public int characterIndex;

    public Bindable getBindable(ResourceManager resourceManager) {
        if ("".equals(this.characterName)) {
            //todo
            //if not from characters but from B
            return null;
        } else {
//            int tmp;
//            switch (direction) {
//                case 2:
//                    tmp = 3 * 0;
//                case 4:
//                    tmp = 3 * 1;
//                case 6:
//                    tmp = 3 * 2;
//                case 8:
//                    tmp = 3 * 3;
//            }
            int tmp = ((this.direction >> 1) - 1) * 3;
            tmp += this.pattern;
            return resourceManager.fetchResourceWithShortenURI(Texture.class,
                    "/www/img/characters/" + this.characterName + ".png" +
                            ":" + "characters" + ":" + this.characterIndex +
                            ":" + tmp);
        }
    }
}
