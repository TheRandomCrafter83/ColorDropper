package com.coderzf1.colordropper.ui.colorpicker.listeners;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * Handles compound drawable click events.
 * @see TextView#getCompoundDrawables()
 * @see TextView#setCompoundDrawablesRelativeWithIntrinsicBounds(int, int, int, int)
 * @see CompoundDrawableTouchListener
 */
@SuppressWarnings("ALL")
public abstract class CompoundDrawableClickListener extends CompoundDrawableTouchListener {

    /**
     * Default constructor
     */
    public CompoundDrawableClickListener() {
        super();
    }

    @Override
    protected boolean onDrawableTouch(View v, int drawableIndex, Rect drawableBounds, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) onDrawableClick(v, drawableIndex);
        return true;
    }

    /**
     * Compound drawable touch-event handler
     * @param v wrapping view
     * @param drawableIndex index of compound drawable which received the event
     */
    protected abstract void onDrawableClick(View v, int drawableIndex);
}