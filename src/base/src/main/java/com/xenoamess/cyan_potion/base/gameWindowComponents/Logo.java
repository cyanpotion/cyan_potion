package com.xenoamess.cyan_potion.base.gameWindowComponents;

import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.audio.WaveData;
import com.xenoamess.cyan_potion.base.events.KeyEvent;
import com.xenoamess.cyan_potion.base.events.MouseButtonEvent;
import com.xenoamess.cyan_potion.base.io.input.key.Keymap;
import com.xenoamess.cyan_potion.base.render.Texture;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author XenoAmess
 */
public class Logo extends AbstractGameWindowComponent {
    private final Texture logoTexture;
    private final long lifeTime;
    private final long dieTimeStamp;


    public Logo(GameWindow gameWindow, long lifeTime) {
        super(gameWindow);
        this.logoTexture =
                this.getGameWindow().getGameManager().getResourceManager().fetchResourceWithShortenURI(Texture.class, "/www/img/pictures/logo.png:picture");
        this.lifeTime = lifeTime;
        this.dieTimeStamp = System.currentTimeMillis() + this.getLifeTime();
        this.getGameWindow().getGameManager().getAudioManager().playNew(this.getGameWindow().getGameManager().getResourceManager().fetchResourceWithShortenURI(WaveData.class, "/www/audio/se/logo.ogg:music"));
    }

    public Logo(GameWindow gameWindow) {
        this(gameWindow, 500L + 2000L + 1000L);
    }

    @Override
    public void initProcessors() {
        this.registerProcessor(KeyEvent.class.getCanonicalName(),
                event -> {
                    KeyEvent keyEvent = (KeyEvent) event;
                    switch (keyEvent.getKeyTranslated().getKey()) {
                        case Keymap.XENOAMESS_KEY_ESCAPE:
                        case Keymap.XENOAMESS_KEY_ENTER:
                        case Keymap.XENOAMESS_KEY_SPACE:
                            this.getAlive().set(false);
                            break;
                        default:
                            return event;
                    }
                    return null;
                }
        );

        this.registerProcessor(MouseButtonEvent.class.getCanonicalName(),
                event -> {
                    this.getAlive().set(false);
                    return null;
                }
        );
    }

    @Override
    public void update() {
        if (System.currentTimeMillis() > this.getDieTimeStamp()) {
            this.getAlive().set(false);
        }

        if (!this.getAlive().get()) {
            this.getGameWindowComponentTreeNode().close();
            MadeWithLogo madeWithLogo = new MadeWithLogo(this.getGameWindow());
            madeWithLogo.addToGameWindowComponentTree(null);
            madeWithLogo.enlargeAsFullWindow();

//            Font.defaultFont.loadBitmap();
//            this.getGameWindow().gameWindowComponentTree.deleteNode(this);
//            {
//                World world = new World(this.gameWindow);
//                world.addToGameWindowComponentTree();
//            }
        }
    }

    @Override
    public void draw() {
        if (!this.getAlive().get()) {
            glClearColor(1, 1, 1, 1);
            glClear(GL_COLOR_BUFFER_BIT);
            return;
        }
        glClearColor(1, 1, 1, 1);
        glClear(GL_COLOR_BUFFER_BIT);

        float t =
                this.getLifeTime() - this.getDieTimeStamp() + System.currentTimeMillis();
        float pscale;
//        System.out.println(t);
        float dynamicTime = 500f;
        float stayTime = 2000f;
        float fadeTime = 1000f;
        float seg = 0.6f;
        if (t < seg * dynamicTime) {
            pscale = t / seg / dynamicTime;
        } else if (t < (1 - (1 - seg) / 2) * dynamicTime) {
            pscale = t / seg / dynamicTime;
        } else if (t < dynamicTime) {
            pscale =
                    ((1 - (1 - seg) / 2) * 2 * dynamicTime - t) / seg / dynamicTime;
        } else {
            pscale = 1;
        }
        if (t < dynamicTime + stayTime) {
            this.getGameWindow().drawBindableRelative(getLogoTexture(),
                    0 + this.getGameWindow().getLogicWindowWidth() / 2,
                    -50 * 2 + this.getGameWindow().getLogicWindowHeight() / 2
                    , 480 / 2 * (pscale + 1) * 2, 60 / 2 * (pscale + 1) * 2,
                    new Vector4f(1, 1, 1, pscale));
        } else {
            pscale = (1 - (t - dynamicTime - stayTime) / fadeTime);
            if (pscale < 0) {
                pscale = 0;
            }
            this.getGameWindow().drawBindableRelative(getLogoTexture(),
                    0 + this.getGameWindow().getLogicWindowWidth() / 2,
                    -50 * 2 + this.getGameWindow().getLogicWindowHeight() / 2
                    , 480 * 2, 60 * 2, new Vector4f(1, 1, 1, pscale));
        }
    }

    @Override
    public void close() {

    }

    public Texture getLogoTexture() {
        return logoTexture;
    }

    public long getLifeTime() {
        return lifeTime;
    }

    public long getDieTimeStamp() {
        return dieTimeStamp;
    }
}
