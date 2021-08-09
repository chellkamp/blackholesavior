package com.codingwithoutpants.gamelib.sprite;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;

import androidx.annotation.NonNull;

/**
 * Meteor class, duh
 */
public class Meteor {
    private static float _IMG_DIMENSION = 100f;
    private static PointF _IMG_CENTER = new PointF(50f, 50f);

    public final static float RADIUS = 45f;

    private Bitmap _bitmap;

    private PointF _center = new PointF();
    private float _angleDegrees;

    private PointF _velocity = new PointF(); // measured in pixels per nanosecond
    private float _rotVelocity; // measured in degrees per nanosecond

    private PointF _acceleration = new PointF(); // measured in pixels per nanosecond per nanosecond
    private float _rotAcceleration; // measured in degrees per nanosecond per nanosecond

    private Matrix _drawMatrix = new Matrix(); // used in the draw() method

    /**
     * Constructor
     * @param bitmap meteor image
     */
    public Meteor(@NonNull Bitmap bitmap) {
        _bitmap = bitmap;
    }

    /**
     * Position the meteor by its center
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public void setCenter(float x, float y) {
        _center.set(x, y);
    }

    /**
     * Set the angle
     * @param degrees angle, in degrees
     */
    public void setAngle(float degrees) {
        // normalize degrees to [0, 360) before saving to this object
        if (degrees < 0f) {
            degrees = 360f - (-degrees % 360f);
        }
        degrees = degrees % 360f;

        _angleDegrees = degrees;
    }

    /**
     * Sets the traveling velocity of this object
     * @param dXPerNs X-axis change in pixels per nanosecond
     * @param dYPerNs Y-axis change in pixels per nanosecond
     */
    public void setVelocity(float dXPerNs, float dYPerNs) {
        _velocity.set(dXPerNs, dYPerNs);
    }

    /**
     * Set the rotational velocity
     * @param degPerNs degrees per nanosecond
     */
    public void setRotationalVelocity(float degPerNs) {
        _rotVelocity = degPerNs;
    }

    /**
     * Set the acceleration vector
     * @param dXPerNsPerNs X-axis change in pixels per nanosecond per nanosecond
     * @param dYPerNsPerNs Y-axis change in pixels per nanosecond per nanosecond
     */
    public void setAcceleration(float dXPerNsPerNs, float dYPerNsPerNs) {
        _acceleration.set(dXPerNsPerNs, dYPerNsPerNs);
    }

    /**
     * Set the rotational acceleration.
     * @param degPerNsPerNs degrees per nanosecond per nanosecond
     */
    public void setRotationalAcceleration(float degPerNsPerNs) {
        _rotAcceleration = degPerNsPerNs;
    }

    /**
     * Update the object
     * @param elapsedNs elapsed time, in nanosec
     */
    public void update(long elapsedNs) {

        // update velocities based on their accelerations...

        _velocity.x += _acceleration.x * elapsedNs;
        _velocity.y += _acceleration.y * elapsedNs;

        _rotVelocity += _rotAcceleration * elapsedNs;

        // Now, update positioning based on velocities...

        _center.x += _velocity.x * elapsedNs;
        _center.y += _velocity.y * elapsedNs;

        // use setAngle() because it'll normalize the result
        setAngle(_angleDegrees + _rotVelocity * elapsedNs);
    }

    public void draw(@NonNull Canvas c) {
        // The set...() functions on the Matrix class clear out all previous operations in the
        // Matrix before applying the new operation.
        _drawMatrix.setRotate(_angleDegrees, _IMG_CENTER.x, _IMG_CENTER.y);

        // Notice this is a postTranslate() and not a preTranslate().  preTranslate() would've
        // applied the translation BEFORE the rotation around the image's center point.
        _drawMatrix.postTranslate(_center.x - _IMG_CENTER.x, _center.y - _IMG_CENTER.y);

        c.drawBitmap(_bitmap, _drawMatrix, null);
    }
}
