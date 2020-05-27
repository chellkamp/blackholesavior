package com.codingwithoutpants.gamelib.activity;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.codingwithoutpants.gamelib.R;
import com.codingwithoutpants.gamelib.view.ExtendedSurfaceView;

/**
 * Provides an activity that holds a drawable canvas of a fixed surface resolution.
 * The canvas is scaled to fit in the screen but maintain the same aspect ratio outlined by
 * the portraitWidthPx and portraitHeightPx config values.
 */
public abstract class FixedResPortraitActivity extends Activity
        implements SurfaceHolder.Callback, View.OnTouchListener{

    private ExtendedSurfaceView _surfaceView; // all drawing is done for this control

    private final Object _threadLock = new Object();
    private RenderThread _renderThread;

    private final Object _objLock = new Object();

    // translates from real screen coordinates to virtual coordinates on canvas surface.
    // We'll need this for event handling.
    private Matrix _coordinateTransform = new Matrix();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goFullScreen();

        Resources res = getResources();
        Rect displayFrame = new Rect();

        final int virtualWidth = res.getInteger(R.integer.portraitWidthPx);
        final int virtualHeight = res.getInteger(R.integer.portraitHeightPx);

        // retrieve the display frame for screen dimensions in pixels
        getWindow().getDecorView().getWindowVisibleDisplayFrame(displayFrame);

        float virtualRatio = (float)virtualWidth / (float)virtualHeight;
        float windowRatio = (float)displayFrame.width() / (float)displayFrame.height();

        int surfaceViewWidth;
        int surfaceViewHeight;

        // Calculate the maximum real size in pixels that our SurfaceView can be to fit in
        // the screen but still have the same aspect ratio as our virtual drawing surface
        if (virtualRatio > windowRatio) {
            // virtual surface is proportionally wider than our screen; fit to width
            surfaceViewWidth = displayFrame.width();
            surfaceViewHeight = (int)((float)displayFrame.width() * (float)virtualHeight / (float)virtualWidth);
        } else {
            // virtual surface is proportionally taller or scales to same shape; fit to height
            surfaceViewWidth = (int)((float)displayFrame.height() * (float)virtualWidth / (float)virtualHeight);
            surfaceViewHeight = displayFrame.height();
        }

        // Populate the coordinate transform to properly change coordinates on MotionEvents
        // into coordinates for the virtual space that we're defining and drawing objects in
        float scaleRatio = (float)virtualWidth / (float)surfaceViewWidth;
        _coordinateTransform.setScale(scaleRatio, scaleRatio);

        init(); // initialize objects

        // Note to anyone: SurfaceView needs to be created in the UI thread.
        _surfaceView = new ExtendedSurfaceView(this);

        SurfaceHolder holder = _surfaceView.getHolder();
        holder.setFixedSize(virtualWidth, virtualHeight);
        holder.addCallback(this);

        _surfaceView.setFocusable(true); // allow inputs
        _surfaceView.setOnTouchListener(this);

        // Set the SurfaceView to the fixed size that we calculated earlier and center it
        // within a full-screen layout that only contains the SurfaceView.

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(surfaceViewWidth, surfaceViewHeight);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        RelativeLayout containingLayout = new RelativeLayout(this);
        containingLayout.setBackgroundColor(0xff000000);
        containingLayout.addView(_surfaceView, lp);

        setContentView(containingLayout);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            goFullScreen();
        } else{
            // code to handle window going out of focus goes here
        }
    }

    /**
     * Hides system UI components for full-screen display.
     */
    protected void goFullScreen() {


        View decorView = getWindow().getDecorView();

        int viewFlags = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            viewFlags |= View.SYSTEM_UI_FLAG_IMMERSIVE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        viewFlags |=
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN;

        decorView.setSystemUiVisibility(viewFlags);
    }

    /**
     * Called when the drawing surface is created/re-created
     * @param holder surfaceHolder object
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        int maxHz = getResources().getInteger(R.integer.maxHz);
        synchronized (_threadLock) {
            if (_renderThread == null) {
                _renderThread = new RenderThread(this, maxHz);
                _renderThread.start();
            }
        }
    }

    /**
     * Called when the drawing surface is being destroyed.
     * @param holder SurfaceHolder for surface being destroyed.
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;

        while (retry) {
            try {
                synchronized (_threadLock) {
                    if (_renderThread != null) {
                        _renderThread.stopRunning();
                        _renderThread.join();
                    }
                    _renderThread = null;
                }
                retry = false;
            } catch (InterruptedException e) {
                // do nothing.  The thread will have to quit sooner or later
            }
        }
    }

    /**
     * Called when the size of the surface changes, including right after surfaceCreated() is called.
     * @param holder SurfaceHolder for surface that changed
     * @param format format
     * @param width width (in pixels)
     * @param height height (in pixels)
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    /**
     * Called when a touch event happens on the screen
     * @param v SurfaceView that was touched
     * @param event event
     * @return true if consumed; false otherwise
     */
    @Override
    final public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_UP) {
            v.performClick();
        }

        // scale view coordinates to match the underlying surface
        event.transform(_coordinateTransform);

        synchronized (_objLock) {
            onTouch(event);
        }

        return true;
    }

    /**
     * Override this method to handle touch events
     * @param event event
     */
    protected void onTouch(MotionEvent event) {
        // do nothing
    }

    /**
     * Override this method to initialize objects before render thread is created.
     */
    protected void init() {
        // do nothing
    }

    /**
     * Update objects based on an elapsed time
     * @param elapsedTimeNs elapsed time, in nanoseconds
     */
    protected abstract void update(long elapsedTimeNs);

    /**
     * Draw current state
     * @param c canvas to draw on.  This value can never be null.
     */
    protected abstract void draw(@NonNull Canvas c);

    /**
     * Contains rendering loop
     */
    private static class RenderThread extends Thread {
        private double _maxDrawPeriodNano;

        private FixedResPortraitActivity _activity;

        private final Object _runningLock = new Object();
        private boolean _isRunning;

        /**
         * Constructor
         * @param activity activity that this thread will be operating on
         */
        RenderThread(FixedResPortraitActivity activity, int maxRefreshHz) {
            super();
            _activity = activity;
            _maxDrawPeriodNano = 1E9 / (double)maxRefreshHz;
        }

        /**
         * Tells this thread to stop its loop and finish up
         */
        void stopRunning() {
            synchronized (_runningLock) {
                _isRunning = false;
            }
        }

        @Override
        public void run() {

            long prevTime = -1;
            long curTime;
            long advTime;

            synchronized (_runningLock) {
                _isRunning = true;
            }

            Canvas c;
            SurfaceHolder surfaceHolder;

            while (true) {

                synchronized (_runningLock) {
                    if (!_isRunning) {
                        break;
                    }
                }

                curTime = System.nanoTime();

                if (prevTime > 0) {
                    advTime = curTime - prevTime;
                    if (advTime < _maxDrawPeriodNano) {
                        continue;
                    }
                    synchronized (_activity._objLock) {
                        _activity.update(advTime);
                    }
                }
                prevTime = curTime;

                c = null;
                surfaceHolder = null;


                try {
                    surfaceHolder = _activity._surfaceView.getHolder();
                    c = surfaceHolder.lockCanvas(); // get the next buffer for the surface
                    synchronized (_activity._objLock) {
                        _activity.draw(c); // draw on the buffer
                    }
                } catch (Exception e) {
                    // do nothing.
                } finally {
                    if (c != null) {
                        try {
                            // post the buffer back into the display queue for the surface
                            surfaceHolder.unlockCanvasAndPost(c);
                        } catch (Exception e) {
                            // Aaaand we've lost all hope.  Abandon ship.
                            Log.e("RenderThread",
                                    "Error unlocking canvas",
                                    e);
                            System.exit(1);
                        }
                    }
                }
            }// end while(true)
        }// end run()
    }// end inner class RenderThread

}
