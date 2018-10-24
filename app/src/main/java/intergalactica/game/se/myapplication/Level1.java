package intergalactica.game.se.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES30;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import libkitv1.opengles.se.opengllibkit1.TextureDataFormatter;

import static intergalactica.game.se.myapplication.Texture.TYPE;
import static intergalactica.game.se.myapplication.Texture.WRAPMODE;

public class Level1 extends Level {

    private static final int N_BOOGERS =7;
    private static final int N_SHOTS = 16;
    private final static int PENDING_STATE = 0;
    private final static int BOOGER_ATTACK1 = 1;
    private final static int BOOGER_ATTACK2 = 2;
    private final static int BOOGER_ATTACK3 = 3;
    private final static int BOOGER_ATTACK4 = 4;
    private final static int NEXT_LEVEL = 5;
    private int current_obstacle = PENDING_STATE;
    private boolean nextObstacle = true;
    private boolean delay = true;
    private boolean playerOnScene = true;


    public static final int LEVEL_ID = 1;
    private ActorLoader actorLoader;
    private Context context;

    private int frameCntr;

    private float[][] alienTextData, batBoogTextData;
    public static final String BAT_BOOGER = "batbooger";

    Timer timer = new Timer();


    int testVal;

    public Level1(Context context, ActorLoader actorLoader) {

        super(context, LEVEL_ID, actorLoader);
        this.context = context;
        this.actorLoader = actorLoader;

    }


    public void createLevel() {

        {
            String[] attributes = new String[]{"a_Position", "a_Color", "a_Normal", "a_TexCoordinate"};
            int[] shadersBackground = {R.raw.per_pixel_vertex_shader, R.raw.per_pixel_fragment_shader};
            String[] attrsUnifs = {"u_MVPMatrix", "a_Position", "a_TexCoordinate"};
            createBackground(attributes, shadersBackground, attrsUnifs, ActorFactory.LEVEL1_BITMAP_IDX, ActorFactory.BACKGROUND_ACTOR, 0);
        }

        {
            String[] attributes = new String[]{"a_Position", "a_Color", "a_Normal", "a_TexCoordinate"};
            int[] shadersBackground = {R.raw.per_pixel_vertex_shader, R.raw.per_pixel_fragment_shader};
            String[] attrsUnifs = {"u_MVPMatrix", "a_Position", "a_TexCoordinate"};
            createBackground(attributes, shadersBackground, attrsUnifs, ActorFactory.LEVEL1_L2_BITMAP_IDX, ActorFactory.BACKGROUND_L2_ACTOR, 1);
        }

        {
            String[] attributes = new String[]{"a_Position", "a_Color", "a_Normal", "a_TexCoordinate"};
            int[] shadersBackground = {R.raw.per_pixel_vertex_shader, R.raw.per_pixel_fragment_shader};
            String[] attrsUnifs = {"u_MVPMatrix", "a_Position", "a_TexCoordinate"};
            createBackground(attributes, shadersBackground, attrsUnifs, ActorFactory.LEVEL1_L3_BITMAP_IDX, ActorFactory.BACKGROUND_L3_ACTOR, 2);
        }





        levelCreated = true;

    }



    public void update() {

        run();

        for (Actor playerActor: playerActorList) {
            if (!isNotTouching)
                ((ControlComponent) playerActor.getComponent(ComponentFactory.CONTROLCOMPONENT)).setTouchCoords(touchX * fractionWidth, touchY * fractionHeight);
        }

        backgroundActor[0].update();
        backgroundActor[1].update();
        backgroundActor[2].update();



        frameCntr++;

        /*if (frameCntr % 100 == 0) {

            this.testVal = (int)(Math.random() * 5000);
        }*/

        //scoreManager.generateScore(testVal);
        for (Actor actor: scoreActorList)
            actor.update();

        for (Actor playerBurnerActor: playerBurnerActorList)
            playerBurnerActor.update();

        for (Actor playerActor: playerActorList)
            playerActor.update();


        spaceShipManager.burner();
        deathTouchActor.update();

        for (Actor actor: playerLifeList)
            actor.update();


        for (Actor actor : boogerList) {
            actor.update();
            CollisionManager.sceneCollider(actor);
        }

        for (Actor actor : brainsList) {
            actor.update();
            CollisionManager.sceneCollider(actor);
        }

        for (Actor actor: explosionList)
            actor.update();


        explosionManager.removeExplosions();

        shotManager.shotIssuer();
        Iterator<Actor> iter = shotList.iterator();
        while (iter.hasNext()) {
            Actor actor = iter.next();
            actor.update();
        }
        shotManager.removeShot();


        boogerVSplayerShotCollision.checkShotCollision(shotList, boogerList, boogerPoolList);
        boogerVSplayerShotCollision.checkShotCollision(shotList, brainsList, brainsPoolList);


        boogerCollisionManager.checkSingleListCollision(boogerList);
        boogerCollisionManager.checkDeathActorCollision(deathTouchActor, boogerList, boogerPoolList);
        boogerCollisionManager.checkDeathActorCollision(deathTouchActor, brainsList, brainsPoolList);

        boolean playerHitByBooger = CollisionManager.checkCollisionPlayer(playerActorList, boogerList);
        boolean playerHitByBrains = CollisionManager.checkCollisionPlayer(playerActorList, brainsList);
        if(playerHitByBooger || playerHitByBrains) {

            handlePlayerHit();
        }


        boogerCollisionManager.save(boogerList);
        boogerCollisionManager.save(deathTouchActor);
        //boogerVSplayerShotCollision.save(shotList);
        boogerCollisionManager.reflectOnCollision();


    }



    public void draw() {

        GLES30.glClearColor(0.49f, 0.27f, 0.44f, 0.0f);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        backgroundActor[0].render();
        backgroundActor[1].render();
        backgroundActor[2].render();

        for (Actor actor: scoreActorList)
            actor.render();

        for (Actor playerBurnerActor: playerBurnerActorList)
            playerBurnerActor.render();

        for (Actor playerActor: playerActorList)
            playerActor.render();

        for (Actor actor: boogerList)
            actor.render();

        for (Actor actor: brainsList)
            actor.render();

        nextObstacle(nextObstacle);

        for (Actor actor: shotList)
            actor.render();

        for (Actor actor: explosionList)
            actor.render();

        for (Actor actor: playerLifeList)
            actor.render();


    }

    public void run() {

        if (brainsList.size() == 0 && boogerList.size() == 0) {

            triggerNextObstacle();
        } else {

            nextObstacle = false;
            delay = true;
        }

    }


    public void triggerNextObstacle() {

        if (delay && playerOnScene) {

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    nextObstacle = true;
                    current_obstacle++;
                }
            }, 5000);

            delay = false;
        }

    }


    public void nextObstacle(boolean nextObstacle) {

        if (nextObstacle) {

            switch (current_obstacle) {


                case PENDING_STATE:


                    break;
                case BOOGER_ATTACK1:
                    createObstacle();
                    break;
                case BOOGER_ATTACK2:
                    //createObstacle();
                    advanceToNextLevel = true;
                    break;
                case BOOGER_ATTACK3:
                    createObstacle();
                    break;
                case BOOGER_ATTACK4:
                    createObstacle();
                    break;
                case NEXT_LEVEL:
                    advanceToNextLevel = true;
                    break;

            }

        }

        frameCntr++;
    }

    public void createObstacle_() {



        for (ActorHolder actorHolder: boogerPoolList) {

            if ( actorHolder.isAvailable()) {

                Actor actor = actorHolder.getActor();
                ((BatBoogerBehaviourComponent)actor.getComponent(ComponentFactory.BATBOOGERBEHAVIOURCOMPONENT)).create();
                BoxColliderComponent collider = (BoxColliderComponent)actor.getComponent(ComponentFactory.BOXCOLLIDERCOMPONENT);
                ((LifeComponent)actor.getComponent(ComponentFactory.LIFECOMPONENT)).setCurrentHealth(50);
                collider.setPreviousBoxLeft(0);
                collider.setPreviousBoxTop(0);
                collider.setPreviousBoxRight(0);
                collider.setPreviousBoxBottom(0);
                boogerList.add(actorHolder.getActor());
                actorHolder.setAvailable(false);
            }

            if (boogerList.size() == 3)
                break;
        }
    }

    public void createObstacle() {

        BatBrainsBehaviourComponent.setIncr(0);
        for (ActorHolder actorHolder: brainsPoolList) {



            if ( actorHolder.isAvailable()) {

                Actor actor = actorHolder.getActor();
                ((BatBrainsBehaviourComponent)actor.getComponent(ComponentFactory.BATBRAINSBEHAVIOURCOMPONENT)).create();
                BoxColliderComponent collider = (BoxColliderComponent)actor.getComponent(ComponentFactory.BOXCOLLIDERCOMPONENT);
                ((LifeComponent)actor.getComponent(ComponentFactory.LIFECOMPONENT)).setCurrentHealth(100);
                collider.setPreviousBoxLeft(0);
                collider.setPreviousBoxTop(0);
                collider.setPreviousBoxRight(0);
                collider.setPreviousBoxBottom(0);
                brainsList.add(actorHolder.getActor());
                actorHolder.setAvailable(false);
            }

            if (brainsList.size() == 3)
                break;
        }
    }

    private void handlePlayerHit() {

        boolean b = playerLifeManager.removeLife();
        if (b) {

            boogerList.clear();
            brainsList.clear();
            reInitActorPools();

            playerOnScene = false;
            //actorLoader.createPlayerBurner(attributes, shaders, attrsUnifs, ActorFactory.TEXTUREATLAS_IDX, ActorFactory.SHIPBURNER_ACTOR);


            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    playerActorList.add(playerActor);
                    playerBurnerActorList.add(playerBurnerActor);
                    playerOnScene = true;
                }
            }, 5000);

            current_obstacle--; // börja om
            //return true;
        } else {

            game_over = true;
        }
        //return false;
    }

}
