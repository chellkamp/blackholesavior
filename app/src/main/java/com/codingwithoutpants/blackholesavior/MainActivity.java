package com.codingwithoutpants.blackholesavior;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

import androidx.annotation.NonNull;

import com.codingwithoutpants.gamelib.activity.FixedResPortraitActivity;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends FixedResPortraitActivity {

    private Bitmap _splashBitmap;

    @Override
    protected void init() {
        super.init();

        AssetManager assetMgr = getAssets();

        try {
            // load the splash image asset into memory
            InputStream is = assetMgr.open("img/titleart.png");
            _splashBitmap = BitmapFactory.decodeStream(is, null, null);
            try { is.close(); } catch (IOException closeEx) { /* do nothing.  We tried. */}
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
        // draw the splash image with top-left corner at (0, 0) of the canvas.
        // the bitmap is 1080 x 1920, so it should fit the fixed size of the canvas
        // perfectly.
        c.drawBitmap(_splashBitmap, 0f, 0f, null);
    }
}
