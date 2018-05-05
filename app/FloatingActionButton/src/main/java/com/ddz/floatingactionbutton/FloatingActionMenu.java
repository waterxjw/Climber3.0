package com.ddz.floatingactionbutton;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;


/**
 * Author : ddz
 * Creation time   : 17.4.28 10:07
 * Fix time   :  17.4.28 10:07
 */

public class FloatingActionMenu extends ViewGroup {

    private int buttonSpacing;
    private int labelsMargin;
    private int labelsVerticalOffset;

    private int buttonPressedColor;
    private int buttonNormalColor;
    private int buttonOpenColor;

    private int buttonSize;
    private boolean buttonStokeVisible;
    private TouchDelegateHelper touchDelegateHelper;
    private int icon;

    //展开方向
    private int expandDirection;
    //动画时长
    private static int EXPANED_DURATION = 300;

    //文字标签位置
    public static final int LABELS_ON_LEFT_SIDE = 0;
    public static final int LABELS_ON_RIGHT_SIDE = 1;
    //折叠时角度
    private static float COLLAPSED_ROTATION = 0f;
    //展开时角度
    private static float EXPANDED_ROTATION = 90f + 45f;

    public static final int EXPAND_UP = 0x001; //默认向上展开
    public static final int EXPAND_DOWN = 0x002;
    public static final int EXPAND_LEFT = 0x003;
    public static final int EXPAND_RIGHT = 0x004;

    private AnimatorSet expandAnimation = new AnimatorSet().setDuration(EXPANED_DURATION);
    private AnimatorSet collapseAnimation = new AnimatorSet().setDuration(EXPANED_DURATION);

    private int maxButtonWidth;
    private int maxButtonHeight;
    private int labelStyle;
    private int labelPosition;
    private int buttonsCount;
    private AddFloatingActionButton actionButton;
    private OnFloatingActionsMenuUpdateListener updateListener;
    private RotiteDrawable mrotiteDrawable;

    private boolean expaned;


    public FloatingActionMenu(Context context) {
        this(context, null);
    }

    public FloatingActionMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FloatingActionMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        //先拿到button的区域
        buttonSpacing = (int) (getResources().getDimension(R.dimen.fab_actions_spacing) - getResources().getDimension(R.dimen.fab_shadow_radius) - getResources().getDimension(R.dimen.fab_shadow_offset));
        //标签的外边距
        labelsMargin = getResources().getDimensionPixelSize(R.dimen.fab_labels_margin);
        //标签移动距离
        labelsVerticalOffset = getResources().getDimensionPixelSize(R.dimen.fab_shadow_offset);

        //标签文字添加到可触摸区域
        touchDelegateHelper = new TouchDelegateHelper(this);
        setTouchDelegate(touchDelegateHelper);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FloatingActionsMenu, 0, 0);
        //开关按钮"+"颜色
        buttonOpenColor = array.getColor(R.styleable.FloatingActionsMenu_fab_addButtonPlusIconColor, getColor(android.R.color.white));
        //开关按钮正常颜色
        buttonNormalColor = array.getColor(R.styleable.FloatingActionsMenu_fab_addButtonColorNormal, getColor(android.R.color.holo_blue_dark));
        //开关按钮按下颜色
        buttonPressedColor = array.getColor(R.styleable.FloatingActionsMenu_fab_addButtonColorPressed, getColor(android.R.color.holo_blue_light));
        //按钮"+" 大小
        buttonSize = array.getInt(R.styleable.FloatingActionsMenu_fab_addButtonSize, FloatingActionButton.SIZE_NORMAL);
        buttonStokeVisible = array.getBoolean(R.styleable.FloatingActionsMenu_fab_addButtonStrokeVisible, true);
        expandDirection = array.getInt(R.styleable.FloatingActionsMenu_fab_expandDirection, EXPAND_UP);
        //标签文字的文本样式
        labelStyle = array.getResourceId(R.styleable.FloatingActionsMenu_fab_labelStyle, 0);
        // Menu的图标
        icon = array.getResourceId(R.styleable.FloatingActionsMenu_fab_addButton_icon, 0);
        //文字标签位置，默认左边
        labelPosition = array.getInt(R.styleable.FloatingActionsMenu_fab_labelsPosition, LABELS_ON_LEFT_SIDE);
        //展开角度
        EXPANDED_ROTATION = array.getFloat(R.styleable.FloatingActionsMenu_fab_addButton_expanded_rotation, EXPANDED_ROTATION);
        //折叠时角度
        COLLAPSED_ROTATION = array.getFloat(R.styleable.FloatingActionsMenu_fab_addButton_collapsed_rotation, COLLAPSED_ROTATION);
        //旋转时间
        EXPANED_DURATION = array.getInteger(R.styleable.FloatingActionsMenu_fab_addButton_rotation_duration, EXPANED_DURATION);
        array.recycle();
        if (labelStyle != 0 && expandHorizontally()) {
            //Menu向左右展开时不能设置标签文字的文本样式
            throw new IllegalStateException("Action labels in horizontal expand orientation is not supported.");
        }
        //创建Menu按钮
        createButton(context);
    }


    private void createButton(Context context) {
        actionButton = new AddFloatingActionButton(context) {
            @Override
            void updateBackground() {
                plusColor = buttonOpenColor; //赋值
                colorNormal = buttonNormalColor;  //赋值
                colorPressed = buttonPressedColor;   //赋值
                strokeVisiable = buttonStokeVisible;   //赋值
                super.updateBackground();
            }

            @Override
            Drawable getIconDrawable() {
                final RotiteDrawable rotiteDrawable = new RotiteDrawable(super.getIconDrawable());
                mrotiteDrawable = rotiteDrawable;
                final OvershootInterpolator interpolator = new OvershootInterpolator();  //回弹插值器
                //折叠动画
                final ObjectAnimator collapseAnima = ObjectAnimator.ofFloat(rotiteDrawable, "rotation", EXPANDED_ROTATION, COLLAPSED_ROTATION);
                //展开动画
                final ObjectAnimator expandAnima = ObjectAnimator.ofFloat(rotiteDrawable, "rotation", COLLAPSED_ROTATION, EXPANDED_ROTATION);
                expandAnima.setInterpolator(interpolator);
                collapseAnima.setInterpolator(interpolator);
                expandAnimation.play(expandAnima);
                collapseAnimation.play(collapseAnima);
                return rotiteDrawable;
            }
        };
        if (icon > 0) {
            setButtonIcon(icon);
        }
        actionButton.setId(R.id.fab_expand_menu_button);
        actionButton.setSize(buttonSize);
        actionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });
        addView(actionButton, super.generateDefaultLayoutParams());
        buttonsCount++;
    }

    public void setIconDrawable(Drawable drawable) {
        if (null != actionButton) {
            actionButton.setDrawable(drawable);
        }
    }

    public void setButtonIcon(@DrawableRes int icon) {
        if (null != actionButton) {
            actionButton.setIcon(icon);
        }
    }

    public void addButton(FloatingActionButton button) {
        addView(button, buttonsCount - 1);
        buttonsCount++;
        if (labelStyle != 0) {
            createLabel();//设置标签文字
        }
    }

    public void removeButton(FloatingActionButton button) {
        removeView(button.getLabelView());
        removeView(button);
        button.setTag(R.id.fab_label, null);
        buttonsCount--;
    }


    private void createLabel() {
        Context context = new ContextThemeWrapper(getContext(), labelStyle);
        for (int i = 0; i < buttonsCount; i++) {
            FloatingActionButton button = (FloatingActionButton) getChildAt(i);
            String title = button.getTitle();  //得到标签文字

            //如果是Menu按钮或者文字为空或设置过标签，跳过
            if (button == actionButton || title == null || button.getTag(R.id.fab_label) != null)
                continue;
            TextView textView = new TextView(context);
            textView.setTextAppearance(getContext(), labelStyle);
            textView.setText(button.getTitle());
            addView(textView);
            button.setTag(R.id.fab_label, textView);
        }
    }

    public void toggle() {
        if (expaned) {
            collapse();  //折叠
        } else {
            expand(); //展开
        }
    }

    public void expand() {
        if (!expaned) {
            expaned = true;
            touchDelegateHelper.setEnabled(true);
            collapseAnimation.cancel();
            expandAnimation.start();
            if (null != updateListener) {
                updateListener.onMenuExpanded();
            }
        }
    }

    public void collapse() {
        collapse(false);
    }

    public void collapseImmediately() {
        collapse(true);
    }

    private void collapse(boolean collapse) {
        if (expaned) {
            expaned = false;
            touchDelegateHelper.setEnabled(false);
            collapseAnimation.setDuration(collapse ? 0 : EXPANED_DURATION);
            collapseAnimation.start();
            expandAnimation.cancel();
            if (null != updateListener) {
                updateListener.onMenuCollapsed();
            }
        }
    }

    public boolean isExpaned() {
        return expaned;
    }

    private static class RotiteDrawable extends LayerDrawable {

        private float rotation;

        /**
         * Creates a new layer drawable with the list of specified layers.
         * <p>
         * a list of drawables to use as layers in this new drawable,
         * must be non-null
         */
        public RotiteDrawable(@NonNull Drawable drawable) {
            super(new Drawable[]{drawable});
        }

        public float getRotation() {
            return rotation;
        }

        public void setRotation(float rotation) {
            this.rotation = rotation;
            invalidateSelf();
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.save();
            canvas.rotate(rotation, getBounds().centerX(), getBounds().centerY());
            super.draw(canvas);
            canvas.restore();
        }
    }

    public void setOnFloatingActionsMenuUpdateListener(OnFloatingActionsMenuUpdateListener listener) {
        updateListener = listener;
    }

    public interface OnFloatingActionsMenuUpdateListener {
        void onMenuExpanded();

        void onMenuCollapsed();
    }


    /**
     * @return 是否为水平方向
     */
    private boolean expandHorizontally() {
        return expandDirection == EXPAND_LEFT || expandDirection == EXPAND_RIGHT;
    }


    private int getColor(int colorId) {
        return getResources().getColor(colorId);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        //整个按钮列表区域大小
        int width = 0;
        int height = 0;

        //按钮最大宽高
        maxButtonWidth = 0;
        maxButtonHeight = 0;

        //标签文字最大宽度
        int maxLabelWidth = 0;

        for (int i = 0; i < buttonsCount; i++) {
            View childAt = getChildAt(i);
            if (childAt.getVisibility() == GONE) {  //不可见的子控件
                continue;
            }
            switch (expandDirection) {
                case EXPAND_UP:  //上
                case EXPAND_DOWN:  //上下展开
                    maxButtonWidth = Math.max(maxButtonWidth, childAt.getMeasuredWidth()); //取大值为最大宽度
                    //所有子控件高度和
                    height += childAt.getMeasuredHeight();
                    break;

                case EXPAND_LEFT:
                case EXPAND_RIGHT:  //左右展开
                    width += childAt.getMeasuredWidth();  //所有子控件宽度和
                    maxButtonHeight = Math.max(maxButtonHeight, childAt.getMeasuredHeight());  //取最大值为最大高度
                    break;
            }

            if (!expandHorizontally()) { //不是左右展开方向
                TextView textView = (TextView) childAt.getTag(R.id.fab_label);
                if (null != textView) {  //标签文字存在
                    //标签文字的最大宽度
                    maxLabelWidth = Math.max(maxLabelWidth, textView.getMeasuredWidth());
                }
            }
        }

        if (!expandHorizontally()) {
            //上下展开，存在标签文字
            width = maxButtonWidth + (maxLabelWidth > 0 ? maxLabelWidth + labelsMargin : 0);  //按钮最大宽度+标签文字及其外边界  才是整个按钮区域最终宽度
        } else {
            //左右展开，不可设置标签文字
            height = maxButtonHeight;  // 按钮区域总高度为不包含标签文字的高度
        }

        /**
         *  重点：   上下展开：1、有标签文字： 展开区域宽度= 按钮最大宽度 + 标签文字宽度 + 标签文字的外边距
         *                     2、没有标签文字： 展开区域宽度= 按钮最大宽度
         *                 展开区域高度= （所有按钮高度和 + 按钮间距和）* 1.2
         *
         *            左右展开：不可设置标签文字
         *                      展开区域高度 = 按钮最大高度
         *                      展开区域宽度 = （所有按钮宽度和 + 所有按钮间距和）* 1.2
         *
         */

        switch (expandDirection) {
            case EXPAND_UP:
            case EXPAND_DOWN:   //上下展开
                height += buttonSpacing * (buttonsCount - 1); //子控件间距和 + 子控件高度和=整个按钮区域最终总高度
                height = adJustOvershoot(height);  //设置插值器效果，所以乘了1.2，这样回弹时候不会超出控件范围
                break;
            case EXPAND_LEFT:
            case EXPAND_RIGHT:
                width += buttonSpacing * (buttonsCount - 1); //子控件间距和 + 子控件宽度和 = 整个按钮区域最终总宽度
                width = adJustOvershoot(width);
                break;
        }
        setMeasuredDimension(width, height);  //重新计算宽高
    }

    private int adJustOvershoot(int size) {
        return size * 12 / 10; //1.2倍
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        switch (expandDirection) {
            case EXPAND_UP:
            case EXPAND_DOWN:
                boolean expandUp = expandDirection == EXPAND_UP; //是否向上展开
                if (changed) {
                    touchDelegateHelper.clearTouchDelegates();
                }

                //向上 Menu按钮高度就是button - top - menu按钮测量高度，为负数，否则为0
                int actionButtonY = expandUp ? b - t - actionButton.getMeasuredHeight() : 0;
                //menu水平中心   ，文字在按钮左边则为 right - left - 按钮最大宽度的一半， 否则为按钮最大宽度的一半
                int buttonHorizontalCenter = labelPosition ==
                        LABELS_ON_LEFT_SIDE ? r - l - maxButtonWidth / 2 : maxButtonWidth / 2;
                //Menu按钮左边位置
                int actionButtonLeft = buttonHorizontalCenter - actionButton.getMeasuredWidth() / 2;
                actionButton.layout(actionButtonLeft, actionButtonY,
                        actionButtonLeft + actionButton.getMeasuredWidth(), actionButtonY + actionButton.getMeasuredHeight());

                int labelOffest = maxButtonWidth / 2 + labelsMargin;  //标签文字的偏移量
                //标签在按钮左边时   标签的right坐标  buttonHorizontalCenter - labelOffest   如果在右边，那就是left坐标
                int labelXNearButton = labelPosition == LABELS_ON_LEFT_SIDE ? buttonHorizontalCenter - labelOffest :
                        buttonHorizontalCenter + labelOffest;

                //下一个按钮Y坐标
                int buttonNextY = expandUp ?
                        actionButtonY - buttonSpacing :
                        actionButtonY + actionButton.getMeasuredHeight() + buttonSpacing;

                /**
                 * 完成了各个按钮位置的测量，处理标签文字的位置
                 */
                for (int i = buttonsCount - 1; i >= 0; i--) {
                    final View childAt = getChildAt(i);
                    if (childAt == actionButton || childAt.getVisibility() == GONE) continue;
                    //每个按钮的Left
                    int childX = buttonHorizontalCenter - childAt.getMeasuredWidth() / 2;
                    //每个按钮的Top
                    int childY = expandUp ? buttonNextY - childAt.getMeasuredHeight() : buttonNextY;
                    childAt.layout(childX, childY, childX + childAt.getMeasuredWidth(), childY + childAt.getMeasuredHeight());

                    float collapsedTranslation = actionButtonY - childY;  //折叠时的移动距离
                    float expandedTranslation = 0f;
                    //是否展开
                    childAt.setTranslationY(expaned ? expandedTranslation : collapsedTranslation);
                    childAt.setAlpha(expaned ? 1f : 0f);

                    //每个按钮的动画
                    LayoutParm parm = (LayoutParm) childAt.getLayoutParams();
                    parm.collapseDir.setFloatValues(expandedTranslation, collapsedTranslation);  //折叠动画
                    parm.expandDir.setFloatValues(collapsedTranslation, expandedTranslation);  //展开
                    parm.setAnimation(childAt);

                    //标签文字的位置
                    View view = (View) childAt.getTag(R.id.fab_label);
                    if (null != view) {
                        //与labelXNearButton相反
                        //标签文字在左边时， 标签文字的Left坐标， 在右边时，标签文字的right坐标
                        int labelXAwayFromButton = labelPosition == LABELS_ON_LEFT_SIDE
                                ? labelXNearButton - view.getMeasuredWidth()
                                : labelXNearButton + view.getMeasuredWidth();
                        int labelLeft = labelPosition == LABELS_ON_LEFT_SIDE ? labelXAwayFromButton : labelXNearButton;
                        int labelRight = labelPosition == LABELS_ON_LEFT_SIDE ? labelXNearButton : labelXAwayFromButton;
                        int labelTop = childY - labelsVerticalOffset + (childAt.getMeasuredHeight() - view.getMeasuredHeight()) / 2;
                        view.layout(labelLeft, labelTop, labelRight, labelTop + view.getMeasuredHeight());

                        //处理标签文字IDE触摸事件，将触摸事件交给与其对应的Button上
                        Rect touchArea = new Rect(Math.min(childX, labelLeft),
                                childY - buttonSpacing / 2,
                                Math.max(childX + childAt.getMeasuredWidth(), labelRight),
                                childY + childAt.getMeasuredHeight() + buttonSpacing / 2);
                        touchDelegateHelper.addTouchDelegate(new TouchDelegate(touchArea, childAt));

                        //根据展开设置动画
                        view.setTranslationY(expaned ? expandedTranslation : collapsedTranslation);
                        view.setAlpha(expaned ? 1f : 0f);

                        //每个标签文字的动画 -- 同按钮的动画
                        LayoutParm viewparm = (LayoutParm) view.getLayoutParams();
                        viewparm.collapseDir.setFloatValues(expandedTranslation, collapsedTranslation);  //折叠动画
                        viewparm.expandDir.setFloatValues(collapsedTranslation, expandedTranslation);  //展开
                        viewparm.setAnimation(view);
                    }
                    //按钮Y坐标
                    buttonNextY = expandUp ? childY - buttonSpacing : childY + childAt.getMeasuredHeight() + buttonSpacing;
                }
                break;
            case EXPAND_LEFT:
            case EXPAND_RIGHT:  //左右展开
                boolean expandLeft = expandDirection == EXPAND_LEFT;
                // Menu按钮在左右展开时，左坐标 r - l - 按钮宽度， 否则为0
                int actionButtonX = expandLeft ? r - l - actionButton.getMeasuredWidth() : 0;
                // menu按钮上部位置
                int actionButtonTop = b - t - maxButtonHeight + (maxButtonHeight - actionButton.getMeasuredHeight()) / 2;
                actionButton.layout(actionButtonX, actionButtonTop, actionButtonX + actionButton.getMeasuredWidth(), actionButtonTop + actionButton.getMeasuredHeight());
                //下一个按钮的X坐标  左展开就是 按钮X坐标 - 按钮间距，  反之则是 按钮X坐标+ 按钮宽度 + 按钮间距
                int buttonNextX = expandLeft ? actionButtonX - buttonSpacing : actionButtonX + actionButton.getMeasuredWidth() + buttonSpacing;
                for (int i = buttonsCount - 1; i >= 0; i--) {
                    View childAt = getChildAt(i);
                    if (childAt == actionButton || childAt.getVisibility() == GONE) continue;
                    int childX = expandLeft ? buttonNextX - childAt.getMeasuredWidth() : buttonNextX;
                    int childY = actionButtonTop + (actionButton.getMeasuredHeight() - childAt.getMeasuredHeight()) / 2;
                    //给每个Button定位
                    childAt.layout(childX, childY, childX + childAt.getMeasuredWidth(), childY + childAt.getMeasuredHeight());

                    float collapsedTranslation = actionButtonX - childX;
                    float expandedTranslation = 0f;
                    childAt.setTranslationX(expaned ? expandedTranslation : collapsedTranslation);
                    childAt.setAlpha(expaned ? 1f : 0f);
                    LayoutParm parms = (LayoutParm) childAt.getLayoutParams();
                    parms.collapseDir.setFloatValues(expandedTranslation, collapsedTranslation);
                    parms.expandDir.setFloatValues(collapsedTranslation, expandedTranslation);
                    parms.setAnimation(childAt);
                    actionButtonX = expandLeft ? childX - buttonSpacing : childX + childAt.getMeasuredWidth() + buttonSpacing;
                }
                break;

        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParm(super.generateDefaultLayoutParams());
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParm(super.generateLayoutParams(attrs));
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new LayoutParm(super.generateLayoutParams(p));
    }

    @Override
    protected boolean checkLayoutParams(LayoutParams p) {
        return super.checkLayoutParams(p);
    }

    private static Interpolator expandInterpolator = new OvershootInterpolator(); //展开用回弹插值器
    private static Interpolator collapseInterpolator = new DecelerateInterpolator(3f);  //折叠用3倍减速插值器
    private static Interpolator alphaExpandInterpolator = new DecelerateInterpolator();  //透明用减速插值器

    private class LayoutParm extends LayoutParams {
        private ObjectAnimator expandDir = new ObjectAnimator();
        private ObjectAnimator expandAlpha = new ObjectAnimator();
        private ObjectAnimator collapseDir = new ObjectAnimator();
        private ObjectAnimator collapseAlpha = new ObjectAnimator();
        private boolean animationPrePlay;

        public LayoutParm(LayoutParams source) {
            super(source);
            expandDir.setInterpolator(expandInterpolator);  //设置展开回弹插值器
            expandAlpha.setInterpolator(alphaExpandInterpolator);  //回弹透明插值器
            collapseDir.setInterpolator(collapseInterpolator);  //设置折叠减速插值器
            collapseAlpha.setInterpolator(collapseInterpolator); //设置折叠透明插值器

            expandAlpha.setProperty(View.ALPHA);
            expandAlpha.setFloatValues(0f, 1f);  //展开透明度 0 -1

            collapseAlpha.setProperty(View.ALPHA);
            collapseAlpha.setFloatValues(1f, 0f);  //折叠透明度 1 - 0

            switch (expandDirection) {
                case EXPAND_DOWN:
                case EXPAND_UP:
                    collapseDir.setProperty(View.TRANSLATION_Y); //Y轴方向
                    expandDir.setProperty(View.TRANSLATION_Y); //Y轴方向
                    break;
                case EXPAND_LEFT:
                case EXPAND_RIGHT:
                    collapseDir.setProperty(View.TRANSLATION_X); //X轴方向
                    expandDir.setProperty(View.TRANSLATION_X); //X轴方向
                    break;
            }
        }

        public void setAnimation(View view) {
            collapseAlpha.setTarget(view);
            collapseDir.setTarget(view);
            expandAlpha.setTarget(view);
            expandDir.setTarget(view);
            if (!animationPrePlay) {
                addLayerListener(expandDir, view);
                addLayerListener(collapseDir, view);
                collapseAnimation.play(collapseAlpha);
                collapseAnimation.play(collapseDir);
                expandAnimation.play(expandAlpha);
                expandAnimation.play(expandDir);
                animationPrePlay = true;
            }

        }

        private void addLayerListener(Animator animator, final View view) {
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setLayerType(LAYER_TYPE_NONE, null);
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    view.setLayerType(LAYER_TYPE_HARDWARE, null);
                }
            });
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        bringChildToFront(actionButton);
        buttonsCount = getChildCount();
        if (labelStyle != 0) {
            createLabel();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        actionButton.setEnabled(enabled);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        SavedState savedState = new SavedState(parcelable);
        savedState.mExpanded = expaned;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            SavedState savedState = (SavedState) state;
            expaned = savedState.mExpanded;
            touchDelegateHelper.setEnabled(expaned);
            if (mrotiteDrawable != null) {
                mrotiteDrawable.setRotation(expaned ? EXPANDED_ROTATION : COLLAPSED_ROTATION);
            }
            super.onRestoreInstanceState(savedState.getSuperState());
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    public static class SavedState extends BaseSavedState {
        public boolean mExpanded;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            mExpanded = in.readInt() == 1;
        }

        @Override
        public void writeToParcel(@NonNull Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mExpanded ? 1 : 0);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
