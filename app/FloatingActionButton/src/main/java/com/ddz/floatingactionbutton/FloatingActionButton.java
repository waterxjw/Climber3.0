package com.ddz.floatingactionbutton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Author : ddz
 * Creation time   : 17.4.24 18:21
 * Fix time   :  17.4.24 18:21
 */

public class FloatingActionButton extends ImageButton {


    int colorNormal;
    int colorDisabled;
    int colorPressed;
    public static final int SIZE_NORMAL = 0;
    public static final int SIZE_MINI = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SIZE_NORMAL, SIZE_MINI})
    public @interface FAB_SIZE {
    }

    private int size;
    @DrawableRes
    private int icon;
    private String title;
    boolean strokeVisiable;
    private float shadowRadius;
    private float shadowOffest;
    private float circleSize;
    private int drawableSize;
    private Drawable iconDrawable;

    public FloatingActionButton(Context context) {
        this(context, null);
    }

    public FloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    public FloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FloatingActionButton, 0, 0);
        //默认按钮状态
        colorNormal = array.getColor(R.styleable.FloatingActionButton_fab_colorNormal, getColor(android.R.color.holo_blue_dark));
        //禁用时按钮的颜色
        colorDisabled = array.getColor(R.styleable.FloatingActionButton_fab_colorDisabled, getColor(android.R.color.darker_gray));
        //点击时颜色
        colorPressed = array.getColor(R.styleable.FloatingActionButton_fab_colorPressed, getColor(android.R.color.holo_blue_light));
        //按钮悬浮圆的大小
        size = array.getInt(R.styleable.FloatingActionButton_fab_size, SIZE_NORMAL);
        //按钮图标
        icon = array.getResourceId(R.styleable.FloatingActionButton_fab_icon, 0);
        //展开式按钮文字，仅当Button是Menu的子视图，才展示
        title = array.getString(R.styleable.FloatingActionButton_fab_title);
        //边框线是否可见
        strokeVisiable = array.getBoolean(R.styleable.FloatingActionButton_fab_stroke_visible, true);
        array.recycle();

        //计算按钮大小
        updateCircleSize();
        //阴影半径大小
        shadowRadius = getDimension(R.dimen.fab_shadow_radius);
        //阴影偏移量大小
        shadowOffest = getDimension(R.dimen.fab_shadow_offset);

        //计算按钮填充图片大小
        updateDrawableSize();

        //计算圆形边框线大小
        updateBackground();

    }


    /**
     * 确定悬浮圆形半径大小
     */
    void updateCircleSize() {
        circleSize = getDimension(size == SIZE_NORMAL ? R.dimen.fab_size_normal : R.dimen.fab_size_mini);
    }


    /**
     * 根据悬浮圆形的 size 和阴影的长度计算这个自定义 View 的 size
     */
    void updateDrawableSize() {
        drawableSize = (int) (circleSize + 2 * shadowRadius);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(drawableSize, drawableSize);  //设置长和宽
    }

    void updateBackground() {
        final float strokeWidth = getDimension(R.dimen.fab_stroke_width);
        final float halfStrokeWidth = strokeWidth / 2f;
        //创建Drawable
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{
                getResources().getDrawable(size == SIZE_NORMAL ? R.drawable.fab_bg_normal : R.drawable.fab_bg_mini), //第一层，其实是图片
                createFillDrawable(strokeWidth), //第二层Drawable
                createOuterStokeDrawable(strokeWidth),  //第三层Drawable
                getIconDrawable()  //第四层Drawable
        });

        //icon偏移量
        int iconOffest = (int) (circleSize - getDimension(R.dimen.fab_icon_size)) / 2;

        int circleTop = (int) (shadowRadius - shadowOffest);
        int circleBottom = (int) (shadowRadius + shadowOffest);
        int circleHorizontal = (int) (shadowRadius);

        layerDrawable.setLayerInset(1, //第二层drawable，设置偏移量
                circleHorizontal,
                circleTop,
                circleHorizontal,
                circleBottom);

        layerDrawable.setLayerInset(2,          //第三层Drawable，设置最外层边框的偏移量
                (int) (circleHorizontal - halfStrokeWidth),
                (int) (circleTop - halfStrokeWidth),
                (int) (circleHorizontal - halfStrokeWidth),
                (int) (circleBottom - halfStrokeWidth));

        layerDrawable.setLayerInset(3,  //第四层，icon偏移量
                circleHorizontal + iconOffest,
                circleTop + iconOffest,
                circleHorizontal + iconOffest,
                circleBottom + iconOffest);

        setBackgroundCompat(layerDrawable);
    }

    float getDimension(@DimenRes int id) {
        return getResources().getDimension(id);
    }


    int getColor(int id) {
        return getResources().getColor(id);
    }

    private StateListDrawable createFillDrawable(float strokeWidth) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        //禁用的Drawable
        stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, createCircleDrawable(colorDisabled, strokeWidth));
        //按下的Drawable
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, createCircleDrawable(colorPressed, strokeWidth));
        //默认的Drawable
        stateListDrawable.addState(new int[]{}, createCircleDrawable(colorNormal, strokeWidth));
        return stateListDrawable;
    }


    Drawable createCircleDrawable(int color, float strokeWidth) {
        //拿到color中透明度
        int alpha = Color.alpha(color);
        //去掉Color中透明度
        int opaqueColor = opaque(color);
        //创建圆形Drawable
        ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
        //新建画笔
        Paint paint = shapeDrawable.getPaint();
        paint.setAntiAlias(true);
        paint.setColor(opaqueColor);
        Drawable[] layers = {shapeDrawable,
                createInnerStokeDrawable(opaqueColor, strokeWidth)};
        //根据是否有透明度和是否显示边框来决定填充色是否显示透明
        LayerDrawable drawable = alpha == 255 || !strokeVisiable ? new LayerDrawable(layers) : new TranslucentLayerDrawable(alpha, layers);
        int halfStrokeWidth = (int) (strokeWidth / 2f);  //内边距
        drawable.setLayerInset(1, halfStrokeWidth, halfStrokeWidth, halfStrokeWidth, halfStrokeWidth);
        return drawable;
    }

    Drawable createOuterStokeDrawable(float strokeWidth) {
        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
        Paint paint = drawable.getPaint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(strokeWidth); //设置边框
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setAlpha(opacityToAlpha(0.02f));
        return drawable;
    }

    Drawable getIconDrawable() {
        if (null != iconDrawable) {
            return iconDrawable;
        } else if (icon != 0) {
            return getResources().getDrawable(icon);
        } else {
            return new ColorDrawable(Color.TRANSPARENT);
        }
    }

    /**
     * @param color
     * @param stokeWidth
     * @return 得到渐变边框
     */
    Drawable createInnerStokeDrawable(final int color, float stokeWidth) {
        if (!strokeVisiable) {
            return new ColorDrawable(Color.TRANSPARENT);
        }
        ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
        //底部边框颜色
        final int bottomStrokeColor = darkenColor(color);
        //底部边框透明度
        final int bottomStrokeColorHalfTransParent = halfTransparent(bottomStrokeColor);
        //顶部边框颜色
        final int topStrokeColor = lightenColor(color);
        //顶部边框颜色透明度
        final int topStrokeColorHalfTransParent = halfTransparent(topStrokeColor);
        Paint paint = shapeDrawable.getPaint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(stokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        shapeDrawable.setShaderFactory(new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                return new LinearGradient(width / 2, 0, width / 2, height,
                        new int[]{topStrokeColor, topStrokeColorHalfTransParent, color, bottomStrokeColorHalfTransParent, bottomStrokeColor},
                        new float[]{0f, 0.2f, 0.5f, 0.8f, 1f}, //设置不同透明度
                        Shader.TileMode.CLAMP);
            }
        });
        return shapeDrawable;
    }


    private int opaque(int argb) {
        return Color.rgb(Color.red(argb)
                , Color.green(argb)
                , Color.blue(argb));
    }

    private static class TranslucentLayerDrawable extends LayerDrawable {

        /**
         * Creates a new layer drawable with the list of specified layers.
         *
         * @param layers a list of drawables to use as layers in this new drawable,
         * must be non-null
         */
        private final int alpha;

        public TranslucentLayerDrawable(int alpha, Drawable[] layers) {
            super(layers);
            this.alpha = alpha;
        }

        @Override
        public void draw(Canvas canvas) {
            Rect bounds = getBounds();
            canvas.saveLayerAlpha(bounds.left, bounds.top, bounds.right, bounds.bottom, alpha, Canvas.ALL_SAVE_FLAG);
            super.draw(canvas);
            canvas.restore();
        }
    }

    int opacityToAlpha(float alpha) {
        return (int) (255 * alpha);
    }

    int darkenColor(int argb) {
        return adjustColorBrightness(argb, 0.9f);
    }

    int lightenColor(int argb) {
        return adjustColorBrightness(argb, 1.1f);
    }


    int adjustColorBrightness(int argb, float factor) {
        float[] fl = new float[3];
        Color.colorToHSV(argb, fl);
        fl[2] = Math.min(fl[2] * factor, 1f);  //取小
        return Color.HSVToColor(Color.alpha(argb), fl);
    }

    int halfTransparent(int argb) {
        return Color.argb(Color.alpha(argb) / 2,
                Color.red(argb),
                Color.green(argb),
                Color.blue(argb));
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    void setBackgroundCompat(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(drawable);
        } else {
            setBackgroundDrawable(drawable);
        }
    }

    public void setSize(@FAB_SIZE int size) {
        if (size != SIZE_NORMAL && size != SIZE_MINI) {
            return;
        }
        if (this.size != size) {
            this.size = size;
            updateCircleSize();
            updateDrawableSize();
            updateBackground();
        }
    }

    @FloatingActionButton.FAB_SIZE
    public int getSize() {
        return this.size;
    }

    public void setIcon(@DrawableRes int icon) {
        if (this.icon != icon) {
            this.icon = icon;
            iconDrawable = null;
            updateBackground();
        }
    }

    public void setIconDrawable(Drawable drawable) {
        if (this.iconDrawable != drawable) {
            icon = 0;
            iconDrawable = drawable;
            updateBackground();
        }
    }

    public int getColorNormal() {
        return colorNormal;
    }

    public void setColorNormarResId(@ColorRes int colorNormal) {
        setColorNormal(getColor(colorNormal));
    }

    public void setColorNormal(int color) {
        if (colorNormal != color) {
            colorNormal = color;
            updateBackground();
        }
    }


    public int getColorPressed() {
        return colorPressed;
    }

    public void setColorPressedResId(@ColorRes int colorPressed) {
        setColorPressed(getColor(colorPressed));
    }

    public void setColorPressed(int color) {
        if (colorPressed != color) {
            colorPressed = color;
            updateBackground();
        }
    }

    public int getColorDisabled() {
        return colorDisabled;
    }

    public void setColorDisabledResId(@ColorRes int colorDisabled) {
        setColorDisabled(getColor(colorDisabled));
    }

    public void setColorDisabled(int color) {
        if (colorDisabled != color) {
            colorDisabled = color;
            updateBackground();
        }
    }

    public void setStrokeVisible(boolean visible) {
        if (strokeVisiable != visible) {
            strokeVisiable = visible;
            updateBackground();
        }
    }

    public boolean isStrokeVisible() {
        return strokeVisiable;
    }

    public void setTitle(String title) {
        this.title = title;
        TextView label = getLabelView();
        if (label != null) {
            label.setText(title);
        }
    }

    TextView getLabelView() {
        return (TextView) getTag(R.id.fab_label);
    }

    public String getTitle() {
        return title;
    }

    @Override
    public void setVisibility(int visibility) {
        TextView label = getLabelView();
        if (label != null) {
            label.setVisibility(visibility);
        }
        super.setVisibility(visibility);
    }
}


