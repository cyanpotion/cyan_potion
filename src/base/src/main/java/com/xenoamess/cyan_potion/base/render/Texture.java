package com.xenoamess.cyan_potion.base.render;

import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.io.FileUtil;
import com.xenoamess.cyan_potion.base.memory.AbstractResource;
import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL13;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.glActiveTexture;

/**
 * @author XenoAmess
 */
public class Texture extends AbstractResource implements Bindable {
    public static final int MIN_SAMPLER = 0;
    public static final int MAX_SAMPLER = 31;

    private int glTexture2DInt = -1;
    private int width;
    private int height;

    public Texture(GameManager gameManager, String resourceURI) {
        super(gameManager, resourceURI);
    }


    public int getGlTexture2DInt() {
        return this.glTexture2DInt;
    }

    @Override
    public void bind(int sampler) {
        this.load();
        if ((this.getGlTexture2DInt() == -1) != (!this.isInMemory())) {
            throw new Error("Texture state chaos : " + this.getGlTexture2DInt() + " , " + this.isInMemory() + " , " + this.getFullResourceURI());
        }

        if (this.getGlTexture2DInt() == -1) {
            throw new Error("Binding non-ready texture : " + this.getFullResourceURI());
        }
        if (sampler >= MIN_SAMPLER && sampler <= MAX_SAMPLER) {
            glActiveTexture(GL13.GL_TEXTURE0 + sampler);
            glBindTexture(GL_TEXTURE_2D, this.getGlTexture2DInt());
        }
    }

    public void bake(int width, int height, ByteBuffer byteBuffer) {
        this.setWidth(width);
        this.setHeight(height);
        generate(byteBuffer);

        this.setMemorySize(width * height * 4);
        this.getGameManager().getResourceManager().load(this);
    }

    public void bake(int singleWidth, int singleHeight, int entireWidth, int entireHeight, int startWidth, int startHeight, int[] pixelsRaw) {
        this.setWidth(singleWidth);
        this.setHeight(singleHeight);

        final ByteBuffer byteBuffer = BufferUtils.createByteBuffer(getWidth() * getHeight() * 4);
        for (int i = 0; i < getHeight(); i++) {
            for (int j = 0; j < getWidth(); j++) {
                int pixel = pixelsRaw[(startHeight + i) * entireWidth + startWidth + j];
                //                if (pixel < 0) {
                //                    pixel += Integer.MAX_VALUE;
                //                    pixel += Integer.MAX_VALUE;
                //                }

                //                System.out.println((byte) ((pixel >> 16) & 0xFF));
                //                System.out.println((byte) ((pixel >> 8) & 0xFF));
                //                System.out.println((byte) ((pixel >> 0) & 0xFF));
                //                System.out.println((byte) ((pixel >> 24) & 0xFF));
                //                System.out.println(i + " " + j + " " + pixel);

                // RED
                byteBuffer.put((byte) ((pixel >> 16) & 0xFF));

                // GREEN
                byteBuffer.put((byte) ((pixel >> 8) & 0xFF));

                // BLUE
                byteBuffer.put((byte) (pixel & 0xFF));

                // ALPHA
                byteBuffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }
        byteBuffer.flip();
        generate(byteBuffer);
        byteBuffer.clear();

        this.setMemorySize(singleWidth * singleHeight * 4);
        this.getGameManager().getResourceManager().load(this);
    }

    void generate(ByteBuffer byteBuffer) {
        this.setGlTexture2DInt(glGenTextures());
        glBindTexture(GL_TEXTURE_2D, this.getGlTexture2DInt());
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, getWidth(), getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, byteBuffer);
        byteBuffer.clear();
    }

    public static List<List<Texture>> getWalkingTextures(ResourceManager resourceManager, String walkingTexturesFilepath) {
        final List<List<Texture>> res = new ArrayList<>();

        for (int k = 0; k < 8; k++) {
            final List<Texture> nowTextures = new ArrayList<>();
            for (int i = 0; i < 4; i++) {

                for (int j = 0; j < 3; j++) {
                    Texture nowTexture = resourceManager.fetchResourceWithShortenURI(Texture.class, walkingTexturesFilepath + ":characters:" + k + ":" + (i * 3 + j));
                    nowTextures.add(nowTexture);
                }
            }
            res.add(nowTextures);
        }

        return res;
    }


    public static List<Texture> getTilesetTexturesA2(ResourceManager resourceManager, String tilesetTexturesFilepath) {
        final List<Texture> res = new LinkedList<>();

        for (int k = 0; k < 32; k++) {
            for (int ti = 0; ti < 48; ti++) {
                res.add(resourceManager.fetchResourceWithShortenURI(Texture.class, tilesetTexturesFilepath + ":" + "A2" + ":" + k + ":" + ti));
            }
        }
        return res;
    }


    static void loadTilesetTextureA2SingleSingle(ResourceManager resourceManager, String resourceFilePath, int kk, int ti, int singleSingleWidth, int singleSingleHeight, int[] pixelsRaws0, int[] pixelsRaws1, int[] pixelsRaws2, int[] pixelsRaws3) {
        final Texture nowTexture = resourceManager.fetchResourceWithShortenURI(Texture.class, resourceFilePath + ":" + "A2" + ":" + kk + ":" + ti);
        if ((nowTexture.getGlTexture2DInt() == -1) != (!nowTexture.isInMemory())) {
            throw new Error("Texture state chaos : " + nowTexture.getGlTexture2DInt() + " , " + nowTexture.isInMemory() + " , " + nowTexture.getFullResourceURI());
        }
        if (nowTexture.isInMemory()) {
            return;
        }
        final ByteBuffer byteBuffer = BufferUtils.createByteBuffer(singleSingleWidth * 2 * singleSingleHeight * 2 * 4);

        for (int i = 0; i < singleSingleHeight; i++) {
            for (int j = 0; j < singleSingleWidth; j++) {
                int pixel = pixelsRaws0[i * singleSingleWidth + j];
                byteBuffer.put((byte) ((pixel >> 16) & 0xFF)); // RED
                byteBuffer.put((byte) ((pixel >> 8) & 0xFF));  // GREEN
                byteBuffer.put((byte) (pixel & 0xFF));          // BLUE
                byteBuffer.put((byte) ((pixel >> 24) & 0xFF)); // ALPHA
            }
            for (int j = 0; j < singleSingleWidth; j++) {
                int pixel = pixelsRaws1[i * singleSingleWidth + j];
                byteBuffer.put((byte) ((pixel >> 16) & 0xFF)); // RED
                byteBuffer.put((byte) ((pixel >> 8) & 0xFF));  // GREEN
                byteBuffer.put((byte) (pixel & 0xFF));          // BLUE
                byteBuffer.put((byte) ((pixel >> 24) & 0xFF)); // ALPHA
            }
        }
        for (int i = 0; i < singleSingleHeight; i++) {
            for (int j = 0; j < singleSingleWidth; j++) {
                int pixel = pixelsRaws2[i * singleSingleWidth + j];
                byteBuffer.put((byte) ((pixel >> 16) & 0xFF)); // RED
                byteBuffer.put((byte) ((pixel >> 8) & 0xFF));  // GREEN
                byteBuffer.put((byte) (pixel & 0xFF));          // BLUE
                byteBuffer.put((byte) ((pixel >> 24) & 0xFF)); // ALPHA
            }
            for (int j = 0; j < singleSingleWidth; j++) {
                int pixel = pixelsRaws3[i * singleSingleWidth + j];
                byteBuffer.put((byte) ((pixel >> 16) & 0xFF)); // RED
                byteBuffer.put((byte) ((pixel >> 8) & 0xFF));  // GREEN
                byteBuffer.put((byte) (pixel & 0xFF));          // BLUE
                byteBuffer.put((byte) ((pixel >> 24) & 0xFF)); // ALPHA
            }
        }
        byteBuffer.flip();

        if (!nowTexture.isInMemory()) {
            nowTexture.bake(singleSingleWidth * 2, singleSingleHeight * 2, byteBuffer);
        }
        byteBuffer.clear();
    }

    static void loadTilesetTexturesA2Single(ResourceManager resourceManager, String resourceFilePath, int kk, int singleWidth, int singleHeight, int entireWidth, int entireHeight, int startWidth, int startHeight, int[] pixelsRaw) {
        final int singleSingleWidth = singleWidth / 2;
        final int singleSingleHeight = singleHeight / 2;

        final int[][] pixelsRaws = new int[25][singleSingleWidth * singleSingleHeight];

        int nowPosx = startWidth;
        int nowPosy = startHeight;
        for (int k = 1; k <= 24; k++) {
            for (int i = 0; i < singleSingleHeight; i++) {
                for (int j = 0; j < singleSingleWidth; j++) {
                    //                    System.out.println(singleSingleWidth * singleSingleHeight + " " + i * singleSingleWidth + j);
                    //                    System.out.println(pixelsRaw.length + " " + (nowPosy + i) * entireWidth + nowPosx + j);
                    pixelsRaws[k][i * singleSingleWidth + j] = pixelsRaw[(nowPosy + i) * entireWidth + nowPosx + j];
                }
            }
            nowPosx += singleSingleWidth;
            if (k % 4 == 0) {
                nowPosy += singleSingleHeight;
                nowPosx = startWidth;
            }
        }
        {
            int ti = 0;
            //0
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[19], pixelsRaws[18], pixelsRaws[15], pixelsRaws[14]);
            ti++;
            ;
            //1
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[3], pixelsRaws[18], pixelsRaws[15], pixelsRaws[14]);
            ti++;
            ;
            //2
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[19], pixelsRaws[4], pixelsRaws[15], pixelsRaws[14]);
            ti++;
            ;
            //3
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[3], pixelsRaws[4], pixelsRaws[15], pixelsRaws[14]);
            ti++;
            ;
            //4
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[19], pixelsRaws[18], pixelsRaws[15], pixelsRaws[8]);
            ti++;
            ;
            //5
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[3], pixelsRaws[18], pixelsRaws[15], pixelsRaws[8]);
            ti++;
            ;
            //6
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[19], pixelsRaws[4], pixelsRaws[15], pixelsRaws[8]);
            ti++;
            ;
            //7
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[3], pixelsRaws[4], pixelsRaws[15], pixelsRaws[8]);
            ti++;
            ;
            //8
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[19], pixelsRaws[18], pixelsRaws[7], pixelsRaws[14]);
            ti++;
            ;
            //9
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[3], pixelsRaws[18], pixelsRaws[7], pixelsRaws[14]);
            ti++;
            ;
            //10
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[19], pixelsRaws[4], pixelsRaws[7], pixelsRaws[14]);
            ti++;
            ;
            //11
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[3], pixelsRaws[4], pixelsRaws[7], pixelsRaws[14]);
            ti++;
            ;
            //12
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[19], pixelsRaws[18], pixelsRaws[7], pixelsRaws[8]);
            ti++;
            ;
            //13
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[3], pixelsRaws[18], pixelsRaws[7], pixelsRaws[8]);
            ti++;
            ;
            //14
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[19], pixelsRaws[4], pixelsRaws[7], pixelsRaws[8]);
            ti++;
            ;
            //15
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[3], pixelsRaws[4], pixelsRaws[7], pixelsRaws[8]);
            ti++;
            ;
            //16
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[17], pixelsRaws[18], pixelsRaws[13], pixelsRaws[14]);
            ti++;
            ;
            //17
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[17], pixelsRaws[4], pixelsRaws[13], pixelsRaws[14]);
            ti++;
            ;
            //18
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[17], pixelsRaws[18], pixelsRaws[13], pixelsRaws[8]);
            ti++;
            ;
            //19
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[17], pixelsRaws[4], pixelsRaws[13], pixelsRaws[8]);
            ti++;
            ;
            //20
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[11], pixelsRaws[10], pixelsRaws[15], pixelsRaws[14]);
            ti++;
            ;
            //21
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[11], pixelsRaws[10], pixelsRaws[15], pixelsRaws[8]);
            ti++;
            ;
            //22
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[11], pixelsRaws[10], pixelsRaws[7], pixelsRaws[14]);
            ti++;
            ;
            //23
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[11], pixelsRaws[10], pixelsRaws[7], pixelsRaws[8]);
            ti++;
            ;
            //24
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[19], pixelsRaws[20], pixelsRaws[15], pixelsRaws[16]);
            ti++;
            ;
            //25
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[19], pixelsRaws[20], pixelsRaws[7], pixelsRaws[16]);
            ti++;
            ;
            //26
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[3], pixelsRaws[20], pixelsRaws[15], pixelsRaws[16]);
            ti++;
            ;
            //27
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[3], pixelsRaws[20], pixelsRaws[7], pixelsRaws[16]);
            ti++;
            ;
            //28
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[19], pixelsRaws[18], pixelsRaws[23], pixelsRaws[22]);
            ti++;
            ;
            //29
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[3], pixelsRaws[18], pixelsRaws[23], pixelsRaws[22]);
            ti++;
            ;
            //30
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[19], pixelsRaws[4], pixelsRaws[23], pixelsRaws[22]);
            ti++;
            ;
            //31
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[3], pixelsRaws[4], pixelsRaws[23], pixelsRaws[22]);
            ti++;
            ;
            //32
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[17], pixelsRaws[20], pixelsRaws[13], pixelsRaws[16]);
            ti++;
            ;
            //33
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[11], pixelsRaws[10], pixelsRaws[23], pixelsRaws[22]);
            ti++;
            ;
            //34
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[9], pixelsRaws[10], pixelsRaws[13], pixelsRaws[14]);
            ti++;
            ;
            //35
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[9], pixelsRaws[10], pixelsRaws[13], pixelsRaws[8]);
            ti++;
            ;
            //36
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[11], pixelsRaws[12], pixelsRaws[15], pixelsRaws[16]);
            ti++;
            ;
            //37
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[11], pixelsRaws[12], pixelsRaws[7], pixelsRaws[16]);
            ti++;
            ;
            //38
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[19], pixelsRaws[20], pixelsRaws[23], pixelsRaws[24]);
            ti++;
            ;
            //39
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[3], pixelsRaws[20], pixelsRaws[23], pixelsRaws[24]);
            ti++;
            ;
            //40
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[17], pixelsRaws[18], pixelsRaws[21], pixelsRaws[22]);
            ti++;
            ;
            //41
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[17], pixelsRaws[4], pixelsRaws[21], pixelsRaws[22]);
            ti++;
            ;
            //42
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[9], pixelsRaws[12], pixelsRaws[13], pixelsRaws[16]);
            ti++;
            ;
            //43
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[9], pixelsRaws[10], pixelsRaws[21], pixelsRaws[22]);
            ti++;
            ;
            //44
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[17], pixelsRaws[20], pixelsRaws[21], pixelsRaws[24]);
            ti++;
            ;
            //45
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[11], pixelsRaws[12], pixelsRaws[23], pixelsRaws[24]);
            ti++;
            ;
            //46
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[9], pixelsRaws[12], pixelsRaws[21], pixelsRaws[24]);
            ti++;
            ;
            //47
            loadTilesetTextureA2SingleSingle(resourceManager, resourceFilePath, kk, ti, singleSingleWidth, singleSingleHeight, pixelsRaws[1], pixelsRaws[2], pixelsRaws[5], pixelsRaws[6]);
            ti++;
            ;
        }
    }


    public void loadAsTilesetTexturesA2(String[] resourceFileURIStrings) {
        final String tilesetTexturesFilepath = resourceFileURIStrings[1];
        final String resourceType = resourceFileURIStrings[2];

        BufferedImage bufferedImage = null;

        try {
            bufferedImage = ImageIO.read(FileUtil.getFile(tilesetTexturesFilepath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        final int entireWidth = bufferedImage.getWidth();
        final int entireHeight = bufferedImage.getHeight();
        final int singleWidth = entireWidth / 8 / 2;
        final int singleHeight = singleWidth;

        final int[] pixelsRaw = bufferedImage.getRGB(0, 0, entireWidth, entireHeight, null, 0, entireWidth);


        int startStartPosx = 0;
        int startStartPosy = 0;
        int startPosx = 0;
        int startPosy = 0;


        for (int k = 0; k < 32; k++) {

            for (int ti = 0; ti < 48; ti++) {
                final Texture nowTexture = this.getGameManager().getResourceManager().fetchResourceWithShortenURI(this.getClass(), tilesetTexturesFilepath + ":" + "A2" + ":" + k + ":" + ti);
                if (!nowTexture.isInMemory()) {
                    loadTilesetTexturesA2Single(this.getGameManager().getResourceManager(), tilesetTexturesFilepath, k, singleWidth, singleHeight, entireWidth, entireHeight, startPosx, startPosy, pixelsRaw);
                    break;
                }
            }

            startPosx += singleWidth * 2;
            //            if (k % 16 == 15) {
            //                startPosy = startStartPosy;
            //                startStartPosx += singleWidth * 2 * 4;
            //                startPosx = startStartPosx;
            //            } else
            if (k % 8 == 7) {
                startPosy += singleHeight * 3;
                startPosx = startStartPosx;
            }
        }
    }


    public static List<Texture> getTilesetTexturesA5(ResourceManager resourceManager, String tilesetTexturesFilepath) {
        return getTilesetTextures8(resourceManager, "A5", tilesetTexturesFilepath, 1);
    }

    public static List<Texture> getTilesetTexturesB(ResourceManager resourceManager, String tilesetTexturesFilepath) {
        return getTilesetTextures8(resourceManager, "B", tilesetTexturesFilepath, 2);
    }

    public static List<Texture> getTilesetTexturesC(ResourceManager resourceManager, String tilesetTexturesFilepath) {
        return getTilesetTextures8(resourceManager, "C", tilesetTexturesFilepath, 2);
    }

    public static List<Texture> getTilesetTextures8(ResourceManager resourceManager, String resourceType, String
            tilesetTexturesFilepath, int columNum) {
        final List<Texture> res = new ArrayList<Texture>();

        BufferedImage bufferedImage = null;

        try {
            bufferedImage = ImageIO.read(FileUtil.getFile(tilesetTexturesFilepath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        final int entireWidth = bufferedImage.getWidth();
        final int entireHeight = bufferedImage.getHeight();
        final int singleWidth = entireWidth / columNum / 8;
        final int singleHeight = singleWidth;

        for (int k = 0; k < columNum; k++) {
            for (int i = 0; i < entireHeight / singleHeight; i++) {
                for (int j = 0; j < 8; j++) {
                    res.add(resourceManager.fetchResourceWithShortenURI(Texture.class, tilesetTexturesFilepath + ":" + resourceType + ":" + k + ":" + (i * 8 + j)));
                }
            }
        }
        return res;
    }


    protected void loadAsTilesetTextures8(String[] resourceFileURIStrings) {
        final String resourceType = resourceFileURIStrings[2];
        final String tilesetTexturesFilepath = resourceFileURIStrings[1];
        int columNum;
        switch (resourceType) {
            case "A5":
                columNum = 1;
                break;
            case "B":
                columNum = 2;
                break;
            case "C":
                columNum = 2;
                break;
            default:
                throw new Error("textureType not defined in URI: " + this.getFullResourceURI());
        }


        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(FileUtil.getURL(tilesetTexturesFilepath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        final int entireWidth = bufferedImage.getWidth();
        final int entireHeight = bufferedImage.getHeight();
        final int singleWidth = entireWidth / columNum / 8;
        final int singleHeight = singleWidth;

        final int[] pixelsRaw = bufferedImage.getRGB(0, 0, entireWidth, entireHeight, null, 0, entireWidth);

        int startPosx = 0;
        int startPosy = 0;
        int nowPosx;
        int nowPosy;

        for (int k = 0; k < columNum; k++) {
            nowPosx = startPosx;
            nowPosy = startPosy;
            for (int i = 0; i < entireHeight / singleHeight; i++) {

                nowPosx = startPosx;
                for (int j = 0; j < 8; j++) {
                    final Texture nowTexture = this.getGameManager().getResourceManager().fetchResourceWithShortenURI(Texture.class, tilesetTexturesFilepath + ":" + resourceType + ":" + k + ":" + (i * 8 + j));
                    if (!nowTexture.isInMemory()) {
                        nowTexture.bake(singleWidth, singleHeight, entireWidth, entireHeight, nowPosx, nowPosy, pixelsRaw);
                        if ((nowTexture.getGlTexture2DInt() == -1) != (!nowTexture.isInMemory())) {
                            throw new Error("Texture state chaos : " + nowTexture.getGlTexture2DInt() + " , " + nowTexture.isInMemory() + " , " + nowTexture.getFullResourceURI());
                        }
                    }
                    nowPosx += singleWidth;
                }
                nowPosy += singleHeight;
            }

            startPosy = 0;
            startPosx += entireWidth / columNum;
        }
    }

    protected void loadAsWalkingTexture(String[] resourceFileURIStrings) {
        final int peopleIndex = Integer.parseInt(resourceFileURIStrings[3]);
        final int textureIndex = Integer.parseInt(resourceFileURIStrings[4]);

        final String walkingTexturesFilepath = resourceFileURIStrings[1];

        final List<List<Texture>> res = new ArrayList<>();
        BufferedImage bufferedImage = null;

        final URI file = FileUtil.getURI(walkingTexturesFilepath);

        try {
            bufferedImage = ImageIO.read(new File(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        final int entireWidth = bufferedImage.getWidth();
        final int entireHeight = bufferedImage.getHeight();
        final int singleWidth = entireWidth / 4 / 3;
        final int singleHeight = entireHeight / 2 / 4;

        final int[] pixelsRaw = bufferedImage.getRGB(0, 0, entireWidth, entireHeight, null, 0, entireWidth);

        int startPosx = 0;
        int startPosy = 0;
        int nowPosx;
        int nowPosy;

        for (int k = 0; k < 8; k++) {
            if (k == 4) {
                startPosx = 0;
                startPosy = singleHeight * 4;
            }

            nowPosx = startPosx;
            nowPosy = startPosy;

            for (int i = 0; i < 4; i++) {
                nowPosx = startPosx;
                for (int j = 0; j < 3; j++) {
                    final Texture nowTexture = this.getGameManager().getResourceManager().fetchResourceWithShortenURI(this.getClass(), walkingTexturesFilepath + ":characters:" + k + ":" + (i * 3 + j));

                    if (!nowTexture.isInMemory()) {
                        nowTexture.bake(singleWidth, singleHeight, entireWidth, entireHeight, nowPosx, nowPosy, pixelsRaw);
                        if ((nowTexture.getGlTexture2DInt() == -1) != (!nowTexture.isInMemory())) {
                            throw new Error("Texture state chaos : " + nowTexture.getGlTexture2DInt() + " , " + nowTexture.isInMemory() + " , " + nowTexture.getFullResourceURI());
                        }
                    }
                    nowPosx += singleWidth;
                }
                nowPosy += singleHeight;
            }
            startPosx += singleWidth * 3;
        }

    }

    public void loadAsPictureTexture(String[] resourceFileURIStrings) {
        final String resourceFilePath = resourceFileURIStrings[1];
        BufferedImage bufferedImage = null;

        final URI fileURI = FileUtil.getURI(resourceFilePath);
        try {
            bufferedImage = ImageIO.read(new File(fileURI));
        } catch (IOException e) {
            e.printStackTrace();
        }
        final int entireWidth = bufferedImage.getWidth();
        final int entireHeight = bufferedImage.getHeight();
        final int[] pixelsRaw = bufferedImage.getRGB(0, 0, entireWidth, entireHeight, null, 0, entireWidth);
        this.bake(entireWidth, entireHeight, entireWidth, entireHeight, 0, 0, pixelsRaw);
    }

    @Override
    public void forceLoad() {
        /**
         * example       com.xenoamess.gearbar.render.WalkingAnimation4Dirs:/www/img/characters/Actor1.png:4:0
         */
        final String[] resourceFileURIStrings = this.getFullResourceURI().split(":");

        final String resourceFilePath = resourceFileURIStrings[1];
        final String resourceType = resourceFileURIStrings[2];

        switch (resourceType) {
            case "characters":
                this.loadAsWalkingTexture(resourceFileURIStrings);
                break;
            case "picture":
                this.loadAsPictureTexture(resourceFileURIStrings);
                break;
            case "A5":
                this.loadAsTilesetTextures8(resourceFileURIStrings);
                break;
            case "B":
                this.loadAsTilesetTextures8(resourceFileURIStrings);
                break;
            case "C":
                this.loadAsTilesetTextures8(resourceFileURIStrings);
                break;
            case "A2":
                this.loadAsTilesetTexturesA2(resourceFileURIStrings);
                break;
            default:
                throw new Error("textureType not defined in URI: " + this.getFullResourceURI());
        }
    }

    @Override
    public void forceClose() {
        if (this.getGlTexture2DInt() != -1) {
            glDeleteTextures(this.getGlTexture2DInt());
            this.setGlTexture2DInt(-1);
        }
        this.setMemorySize(0);
    }

    public void setGlTexture2DInt(int glTexture2DInt) {
        this.glTexture2DInt = glTexture2DInt;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
