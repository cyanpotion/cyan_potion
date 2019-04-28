package com.xenoamess.cyan_potion.base.gameWindowComponents;

import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.audio.WaveData;
import com.xenoamess.cyan_potion.base.events.KeyEvent;
import com.xenoamess.cyan_potion.base.events.MouseButtonEvent;
import com.xenoamess.cyan_potion.base.io.input.key.Keymap;
import com.xenoamess.cyan_potion.base.render.Texture;
import com.xenoamess.cyan_potion.base.visual.Font;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author XenoAmess
 */
public class MadeWithLogo extends AbstractGameWindowComponent {
    private final Texture logoTexture;
    private final long lifeTime;
    private final long dieTimeStamp;


    public MadeWithLogo(GameWindow gameWindow, long lifeTime) {
        super(gameWindow);
        this.logoTexture = this.getGameWindow().getGameManager().getResourceManager().fetchResourceWithShortenURI(Texture.class, "/www/img/pictures/madewith.png:picture");
        this.lifeTime = lifeTime;
        this.dieTimeStamp = System.currentTimeMillis() + this.getLifeTime();
        this.getGameWindow().getGameManager().getAudioManager().playNew(this.getGameWindow().getGameManager().getResourceManager().fetchResourceWithShortenURI(WaveData.class, "/www/audio/se/madewith.ogg:music"));

    }

    public MadeWithLogo(GameWindow gameWindow) {
        this(gameWindow, 5000L + 5000L);
    }

    @Override
    public void initProcessors() {
        this.registerProcessor(KeyEvent.class.getCanonicalName(), event -> {
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
        });

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

        if (!this.getAlive().get() && Font.getDefaultFont() != null) {
            this.getGameWindowComponentTreeNode().close();
            {
                Font.getDefaultFont().init(this.getGameWindow());
                AbstractGameWindowComponent title = this.getGameWindow().getGameManager().getDataCenter().fetchGameWindowComponentFromCommonSetting(this.getGameWindow(), "titleClassName", "com.xenoamess.cyan_potion.base.gameWindowComponents.TitleExample");
                title.addToGameWindowComponentTree(null);
                title.enlargeAsFullWindow();

                //                TitleExample title = null;
//                String titleClassName = ;
//                if (this.getGameWindow().getGameManager().dataCenter.commonSettings.containsKey("titleClassName")) {
//                    titleClassName = this.getGameWindow().getGameManager().dataCenter.commonSettings.get();
//                }
//                try {
//                    title = (TitleExample) this.getClass().getClassLoader().loadClass(titleClassName).getConstructor(GameWindow.class).newInstance(this.getGameWindow());
//                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
//                    e.printStackTrace();
//                    System.exit(-1);
//                }
//
            }
        }
    }

    @Override
    public void draw() {
        if (!this.getAlive().get()) {
            glClearColor(1, 1, 1, 1);
            glClear(GL_COLOR_BUFFER_BIT);
            return;
        }
//        glClearColor(1, 1, 1, 1);
//        glClear(GL_COLOR_BUFFER_BIT);

        long t = this.getLifeTime() - this.getDieTimeStamp() + System.currentTimeMillis();
        float cscale;

        long stayTime = 5000L;
        long fadeTime = 5000L;
        if (t < stayTime) {
            cscale = 1;
        } else {
            cscale = 1 + ((float) (t - stayTime)) / fadeTime * 400;
        }
//        cscale = 1.1f;


        this.getGameWindow().drawBindableRelativeCenter(this.getLogoTexture(), this.getGameWindow().getLogicWindowWidth(), this.getGameWindow().getLogicWindowHeight(), new Vector4f(1, cscale, cscale, 1));
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
