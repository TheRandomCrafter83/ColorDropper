package com.coderzf1.colordropper.ui.colorpicker.listeners;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * Handles compound drawable click events.
 * @see TextView#getCompoundDrawables()
 * @see TextView#setCompoundDrawablesRelativeWithIntrinsicBounds(int, int, int, int)
 * @see CompoundDrawableTouchListener
 */
@SuppressWarnings("WeakerAccess")
public abstract class CompoundDrawableClickListener extends CompoundDrawableTouchListener {

    /**
     * Default constructor
     */
    public CompoundDrawableClickListener() {
        super();
    }

    /**
     * Constructor with fuzz
     * @param fuzz desired fuzz in px
     */
    public CompoundDrawableClickListener(int fuzz) {
        super(fuzz);
    }

    @Override
    protected boolean onDrawableTouch(View v, int drawableIndex, Rect drawableBounds, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) onDrawableClick(v, drawableIndex);
        return true;
    }

    /**
     * Compound drawable touch-event handler
     * @param v wrapping view
     * @param drawableIndex index of compound drawable which recicved the event
     */
    protected abstract void onDrawableClick(View v, int drawableIndex);
}