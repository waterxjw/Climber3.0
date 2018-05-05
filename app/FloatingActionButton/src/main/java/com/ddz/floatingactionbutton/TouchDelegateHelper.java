package com.ddz.floatingactionbutton;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;

import java.util.ArrayList;

/**
 * Author : ddz
 * Creation time   : 17.4.28 10:27
 * Fix time   :  17.4.28 10:27
 */

// TODO: 17.4.28 扩大View的点击范围·
public class TouchDelegateHelper extends TouchDelegate {

    private static final Rect USELESS_RECT = new Rect();
    private boolean enabled;
    private TouchDelegate currentDelegate;

    private final ArrayList<TouchDelegate> touchDelegates = new ArrayList<TouchDelegate>();

    /**
     * Constructor
     * <p>
     * Bounds in local coordinates of the containing view that should be mapped to
     * the delegate view
     *
     * @param delegateView The view that should receive motion events
     */
    public TouchDelegateHelper(View delegateView) {
        super(USELESS_RECT, delegateView);
    }

    public void addTouchDelegate(@NonNull TouchDelegate touchDelegate) {
        touchDelegates.add(touchDelegate);
    }

    public void removeTouchDelegate(TouchDelegate touchDelegate) {
        touchDelegates.remove(touchDelegate);
        if (currentDelegate == touchDelegate) {
            currentDelegate = null;
        }
    }

    public void clearTouchDelegates() {
        touchDelegates.clear();
        currentDelegate = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!enabled) {
            return false;
        }
        TouchDelegate touchDelegate = null;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (int i = 0; i < touchDelegates.size(); i++) {
                    TouchDelegate delegate = touchDelegates.get(i);
                    if (delegate.onTouchEvent(event)) {
                        currentDelegate = delegate;
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                touchDelegate = currentDelegate;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                touchDelegate = currentDelegate;
                currentDelegate = null;
                break;
        }
        return touchDelegate != null && touchDelegate.onTouchEvent(event);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
