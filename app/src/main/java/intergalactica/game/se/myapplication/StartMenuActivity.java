package intergalactica.game.se.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.ByteArrayOutputStream;

public class StartMenuActivity extends AppCompatActivity {

    private Intent startGameActivityIntent;
    private Bitmap[] bitmaps;
    private byte[] byteArray;
    private ResourceLoader resourceLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_menu);

        resourceLoader = new ResourceLoader(this);
        preLoadResources();
    }

    public void onClickStartGame(View view) {


        startGameActivityIntent = new Intent(this, GameActivity.class);

        /*Handler handler = new Handler();
                    handler.post( new Runnable() {
                        public void run() {
                            startActivity(startGameActivityIntent);
                        }
                    } );

        */
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                startActivity(startGameActivityIntent);
            }
        });

    }

    private void preLoadResources() {

        float width = getResources().getDisplayMetrics().widthPixels;
        float height = getResources().getDisplayMetrics().heightPixels;
        float[][] bitmapDimens = { {width, height} };
        String[] bitmapNames = {"level_map"};
        bitmaps = resourceLoader.createBitmaps(bitmapDimens, bitmapNames, "drawable");

    }



}
