package com.codingwithoutpants.gamelib.sprite;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;

import androidx.annotation.NonNull;

/**
 * Meteor class, duh
 */
public class Meteor {
    private PointF _origin = new PointF();
    private float _angle;

    private PointF _velocity = new PointF();
    private float _rotVelocity; // rotational velocity

    private PointF _acceleration = new PointF();
    private float _rotAcceleration; // rotational acceleration

    private Matrix _drawMatrix = new Matrix(); // used in the draw() method

    public void update() {

    }

    public void draw(@NonNull Canvas c) {
        _
    }
}
