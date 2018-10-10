package intergalactica.game.se.myapplication;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Skapad: 2018-10-04
 * Björn Hallström
 * Version: 1
 * Används för att klippa ut delar av en textur och skapa animationsframes, vilket i sin tur innebär en animerad textur
 */
public class AnimationComponent extends BaseComponent {

    //Map<String, Animation*> → String = t.ex “Idle”, “Shooting”, “Death” osv
    private Map <String, Animation> animationMap = new HashMap<>(); //String = t.ex “Idle”, “Shooting”, “Death” osv
    private int nFrames;
    private Animation currentAnimation;
    private float[][] spriteTextData; // texturdata - fem kolumner, första två är offsetvärdena för x- och y dvs var på atlas texturen befinner sig. kolumn 3 och fyra är skalningsvärden (fraktioner) för x,y = texturens storlek på atlas
    private int uvOffset, wHfrac;
    private int mProgramHandle;
    private TransformComponent transformComponent;

    public void create() {

        currentAnimation = new Animation();
        transformComponent = (TransformComponent)getOwner().getComponent(ComponentFactory.TRANSFORMCOMPONENT);

    }

    public void destroy() {

    }

    public void update() {

        currentAnimation.animate();
        int id = currentAnimation.getID();

        GLES30.glUseProgram(mProgramHandle);
        //animationstest
        float[] xyOffsets = {spriteTextData[id][0], spriteTextData[id][1]};
        float[] whFracs = {spriteTextData[id][2], spriteTextData[id][3]};

        float wHratio = spriteTextData[id][4];
        float width = wHratio * transformComponent.getSize();
        float height = transformComponent.getSize();
        transformComponent.setScaleX(width);
        transformComponent.setScaleY(height);



        GLES30.glUniform2fv(uvOffset, 1, xyOffsets, 0);
        GLES30.glUniform2fv(wHfrac, 1, whFracs, 0);


        Log.d("frame", "" + id);

    }

    /**
     *
     * @param spriteTextData
     */
    public void setSpriteTextData(float[][] spriteTextData) {

        this.spriteTextData = spriteTextData;
        this.nFrames = spriteTextData.length;
    }

    public float[][] getSpriteTextData() {

        return spriteTextData;
    }

    public void activateAnimation() { //Sätter currentAnimation till en specifik animation



    }


    public Animation getCurrentAnimation() {
        return currentAnimation;
    }

    public void setUVcoordsHandles(int uvOffset, int wHfrac) {

        this.uvOffset = uvOffset;
        this.wHfrac = wHfrac;

    }

    public void setmProgramHandle(int mProgramHandle) {
        this.mProgramHandle = mProgramHandle;
    }
}
