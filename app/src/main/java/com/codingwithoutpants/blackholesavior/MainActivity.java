package com.codingwithoutpants.blackholesavior;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.Log;

import androidx.annotation.NonNull;

import com.codingwithoutpants.gamelib.activity.FixedResPortraitActivity;
import com.codingwithoutpants.gamelib.sprite.Meteor;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends FixedResPortraitActivity {

    //OLD CODE
    //private Bitmap _splashBitmap;

    private float _screenWidth;
    private float _screenHeight;

    private Bitmap _meteorBitmap;

    // Range of valid center points we can use when we create a meteor sprite off screen
    private RectF _meteorSpawnRange;
    private RectF _meteorDirVecRange; // direction vector range

    @Override
    protected void init() {
        super.init();


        try {
            Resources res = getResources();
            _screenWidth =
                    (float) res.getInteger(com.codingwithoutpants.gamelib.R.integer.portraitWidthPx);
            _screenHeight =
                    (float) res.getInteger(com.codingwithoutpants.gamelib.R.integer.portraitHeightPx);

            _meteorSpawnRange = new RectF(
                    Meteor.RADIUS, -2f * Meteor.RADIUS,
                    _screenWidth - Meteor.RADIUS, -2f * Meteor.RADIUS
            );

            _meteorDirVecRange = new RectF(
                    _meteorSpawnRange.left, _meteorSpawnRange.top + _meteorSpawnRange.width(),
                    _meteorSpawnRange.right, _meteorSpawnRange.top + _meteorSpawnRange.width()
            );

            AssetManager assetMgr = getAssets();

            //OLD CODE
            //// load the splash image asset into memory
            //InputStream is = assetMgr.open("img/titleart.png");
            //_splashBitmap = BitmapFactory.decodeStream(is, null, null);
            //try { is.close(); } catch (IOException closeEx) { /* do nothing.  We tried. */}


            InputStream is;
            is = assetMgr.open("img/meteor.png");

            _meteorBitmap = BitmapFactory.decodeStream(is, null, null);
            try { is.close(); } catch (IOException closeEx) { /* do nothing.  We tried. */}



            _meteorSpawnRange = new RectF();

        } catch(Exception ex) {
            // I can't do anything from here but log the error, then roll over and die.
            Log.e("init", "Error initializing activity.", ex);
            System.exit(1);
        }

    }

    @Override
    protected void update(long elapsedTimeNs) {
        // do nothing, for now
    }

    @Override
    protected void draw(@NonNull Canvas c) {
        //OLD CODE
        //// draw the splash image with top-left corner at (0, 0) of the canvas.
        //// the bitmap is 1080 x 1920, so it should fit the fixed size of the canvas
        //// perfectly.
        //c.drawBitmap(_splashBitmap, 0f, 0f, null);
    }
}
