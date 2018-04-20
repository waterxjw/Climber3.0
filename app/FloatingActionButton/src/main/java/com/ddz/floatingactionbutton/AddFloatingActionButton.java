package com.ddz.floatingactionbutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;

/**
 * Author : ddz
 * Creation time   : 17.4.28 12:14
 * Fix time   :  17.4.28 12:14
 */

public class AddFloatingActionButton extends FloatingActionButton {

    int plusColor;
    Drawable iconDrawable;

    public AddFloatingActionButton(Context context) {
        this(context, null);
    }

    public AddFloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AddFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    void init(Context context, AttributeSet attrs) {
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.AddFloatingActionButton, 0, 0);
        plusColor = attr.getColor(R.styleable.AddFloatingActionButton_fab_plusIconColor, getColor(android.R.color.white));
        attr.recycle();
        super.init(context, attrs);
    }

    public int getPlusColor() {
        return plusColor;
    }

    public void setPlusColorResId(@ColorRes int plusColor) {
        setPlusColor(getColor(plusColor));
    }

    public void setPlusColor(int plusColor) {
        if (this.plusColor != plusColor) {
            this.plusColor = plusColor;
            updateBackground();
        }
    }

    @Override
    public void setSize(@FAB_SIZE int size) {
        super.setSize(size);
    }

    public void setDrawable(Drawable drawable) {
        if (null != drawable && this.iconDrawable != drawable) {
            iconDrawable = drawable;
            updateBackground();
        }
    }

    @Override
    public void setIcon(@DrawableRes int icon) {
        if (icon > 0) {
            iconDrawable = getResources().getDrawable(icon);
            updateBackground();
        }
    }

    @Override
    Drawable getIconDrawable() {
        if (null != iconDrawable) {
            return iconDrawable;
        }
        final float iconSize = getDimension(R.dimen.fab_icon_size);
        final float iconHalfSize = iconSize / 2f;

        final float plusSize = getDimension(R.dimen.fab_plus_icon_size);
        final float plusHalfStroke = getDimension(R.dimen.fab_plus_icon_stroke) / 2f;
        final float plusOffset = (iconSize - plusSize) / 2f;

        final Shape shape = new Shape() {
            @Override
            public void draw(Canvas canvas, Paint paint) {
                canvas.drawRect(plusOffset, iconHalfSize - plusHalfStroke, iconSize - plusOffset, iconHalfSize + plusHalfStroke, paint);
                canvas.drawRect(iconHalfSize - plusHalfStroke, plusOffset, iconHalfSize + plusHalfStroke, iconSize - plusOffset, paint);
            }
        };

        ShapeDrawable drawable = new ShapeDrawable(shape);

        final Paint paint = drawable.getPaint();
        paint.setColor(plusColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        return drawable;
    }
}
