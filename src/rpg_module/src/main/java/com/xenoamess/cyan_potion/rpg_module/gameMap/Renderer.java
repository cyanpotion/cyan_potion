//package com.xenoamess.gearbar.gameMap;
//
//import com.xenoamess.gearbar.common.LogUtil;
//import com.xenoamess.gearbar.GameWindow;
//import com.xenoamess.gearbar.render.Texture;
//import org.joml.Matrix4f;
//import org.joml.Vector2f;
//import org.joml.Vector3f;
//
//import com.xenoamess.gearbar.render.*;
//
//public class Renderer {
//    //    private HashMap<Texture, Texture> tileTextures;
//    public Model tileModel;
//    public GameWindow gameWindow;
//
//    public Renderer(GameWindow gameWindow) {
//        this.gameWindow = gameWindow;
//
//        //        tileTextures = new HashMap<>();
//        float[] vertices = new float[]{-1f, 1f, 0, // TOP LEFT 0
//                1f, 1f, 0,  // TOP RIGHT 1
//                1f, -1f, 0, // BOTTOM RIGHT 2
//                -1f, -1f, 0,// BOTTOM LEFT 3
//        };
//
//        float[] texture = new float[]{0, 0, 1, 0, 1, 1, 0, 1,};
//
//        int[] indices = new int[]{0, 1, 2, 2, 3, 0};
//
//        tileModel = new Model(vertices, texture, indices);
//
//        //        for (GameTile au : GameTile.tiles) {
//        //            if (au == null) {
//        //                continue;
//        //            }
//        //
//        //            //            if (!tileTextures.containsKey(au)) {
//        //            //                Texture tex = au.getTexture();
//        //            //                tileTextures.put(tex, tex);
//        //            //            }
//        //        }
//        //        for (int i = 0; i < GameTile.tiles.size(); i++) {
//        //
//        //        }
//    }
//
//    public void renderTile(GameTile tile, float x, float y, Shader shader,
//    Matrix4f scaleMatrix4f, Camera cam) {
//        for (Texture au : tile.textures) {
//            this.gameWindow.drawTexture(au, x, y, 32, shader);
//        }
//    }
//}
