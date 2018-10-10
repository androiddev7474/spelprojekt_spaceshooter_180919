package intergalactica.game.se.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLES30;

import java.util.ArrayList;

import libkitv1.opengles.se.opengllibkit1.TextureDataFormatter;

public class Level1 extends Level {

    public static final int LEVEL_ID = 1;
    private Bitmap[] bitmaps;
    private ActorFactory actorFactory;
    private Context context;

    private float[][] alienTextData, batBoogTextData;
    public static final String BAT_BOOGER = "batbooger";

    private Actor backgroundActor, batboogerActor;
    private ArrayList <Actor> boogerList = new ArrayList();

    public Level1(Context context, ActorFactory actorFactory) {

        super(context, LEVEL_ID, actorFactory);
        this.context = context;
        this.actorFactory = actorFactory;

    }

    @Override
    public void createLevel() {

        String[] attributes = new String[] {"a_Position",  "a_Color", "a_Normal", "a_TexCoordinate"};
        int[] shaders = {R.raw.per_pixel_vertex_shader, R.raw.per_pixel_fragment_shader};
        String[] attrsUnifs = {"u_MVPMatrix", "a_Position", "a_TexCoordinate"};
        actorFactory.setShaders(shaders, attrsUnifs, attributes);
        actorFactory.setBitmapID(ActorFactory.LEVEL1_BITMAP_IDX);
        backgroundActor = actorFactory.createActor("Background");
        backgroundActor.create();



        createEnemies();



        levelCreated = true;
    }

    private void createEnemies() {

        //batbooger
        for (int i = 0; i < 10; i++) {

            TextureDataFormatter textureDataFormatter = new TextureDataFormatter(context);
            textureDataFormatter.getTextureData(R.array.aliendata, R.array.alienatlas_dimen);
            batBoogTextData = textureDataFormatter.getSortedSpriteData(BAT_BOOGER);

            String[] attributes = new String[]{"a_Position", "a_Color", "a_Normal", "a_TexCoordinate"};
            int[] shaders = {R.raw.per_pixel_vertex_shader_sprite, R.raw.per_pixel_fragment_shader_sprite};
            String[] attrsUnifs = {"u_MVPMatrix", "a_Position", "a_TexCoordinate"};
            actorFactory.setShaders(shaders, attrsUnifs, attributes);
            actorFactory.setBitmapID(ActorFactory.TEXTUREATLAS_IDX);


            batboogerActor = actorFactory.createActor(ActorFactory.BATBOOGER_ACTOR);


            batboogerActor.create();

            boogerList.add(batboogerActor);

        }

    }

    @Override
    public void update() {

        backgroundActor.update();
        //batboogerActor.update();
        for (Actor actor: boogerList) {

            actor.update();
            boxVsBox(actor);
        }


    }

    @Override
    public void draw() {

        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        backgroundActor.render();
        //batboogerActor.render();
        for (Actor actor: boogerList) {

            actor.render();
            //boxVsBox(actor);
        }

    }

    public void boxVsBox(Actor actor) {

        BoxColliderComponent colliderC = (BoxColliderComponent)actor.getComponent(ComponentFactory.BOXCOLLIDERCOMPONENT);

        if (colliderC.getLeft() < GameRenderer.GAMESCENE_LEFT) {

            //float c = colliderC.getRight();
            //float gr = GameRenderer.getGameSceneRight();

            colliderC.setCollisionType(BoxColliderComponent.COLLISION_LEFT);

        }

        if (colliderC.getRight() > GameRenderer.getGameSceneRight()) {

            colliderC.setCollisionType(BoxColliderComponent.COLLISION_RIGHT);
        }

        if (colliderC.getTop() > GameRenderer.GAMESCENE_TOP) {

            //float c = colliderC.getRight();
            //float gr = GameRenderer.getGameSceneRight();

            colliderC.setCollisionType(BoxColliderComponent.COLLISION_TOP);

        }

        if (colliderC.getBottom() < GameRenderer.GAMESCENE_BOTTOM) {

            //float c = colliderC.getRight();
            //float gr = GameRenderer.getGameSceneRight();

            colliderC.setCollisionType(BoxColliderComponent.COLLISION_BOTTOM);

        }


    }

}
