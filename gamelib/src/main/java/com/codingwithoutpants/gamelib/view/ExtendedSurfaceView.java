package com.codingwithoutpants.gamelib.view;

import android.content.Context;
import android.view.SurfaceView;

/**
 * Extended SurfaceView, to resolve Lint warnings
 */
public class ExtendedSurfaceView extends SurfaceView {

    /**
     * Constructor
     * @param c context
     */
    public ExtendedSurfaceView(Context c) {
        super(c);
    }

    /**
     * Override performClick(), because Lint won't stop complaining.
     * @return true or false; who cares
     */
    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
