package com.columnhack.notekeeper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.customview.widget.ExploreByTouchHelper;

import java.util.List;

public class ModuleStatusView extends View {

    public static final int EDIT_MODE_MODULE_COUNT = 7;
    public static final int INVALID_INDEX = -1;
    public static final int SHAPE_CIRCLE = 0;
    public static final float DEFAULT_OUTLINE_WIDTH_DP = 2f;
    private boolean[] mModuleStatus;
    private float mOutlineWidth;
    private float mShapeSize;
    private float mSpacing;
    private Rect[] mModuleRectangles;
    private Paint mPaintOutline;
    private int mOutlineColor;
    private Paint mPaintFill;
    private float mRadius;
    private int mMaxHorizontalModules;
    private int mShape;
    private ModuleStatusAccessibilityHelper mAccessibilityHelper;

    public boolean[] getModuleStatus() {
        return mModuleStatus;
    }

    public void setModuleStatus(boolean[] moduleStatus) {
        mModuleStatus = moduleStatus;
    }

    public ModuleStatusView(Context context) {
        super(context);
        init(null, 0);
    }

    public ModuleStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ModuleStatusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        if(isInEditMode())
            setupEditModeValue();

        setFocusable(true); // This makes the custom view selectable

        // Pass in a reference of this= custom view
        mAccessibilityHelper = new ModuleStatusAccessibilityHelper(this);

        // We need to inform the accessibility system that this
        // helper class provides accessibility information for our custom view
        ViewCompat.setAccessibilityDelegate(this, mAccessibilityHelper);

        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        float displayDensity = dm.density; // The factor to use to convert from dp to pixel
        float defaultOutlineWidthPixels = displayDensity * DEFAULT_OUTLINE_WIDTH_DP;


        // Prep work is done in init method
        // Load attributes
        final TypedArray typedArray = getContext().obtainStyledAttributes(
                attrs, R.styleable.ModuleStatusView, defStyle, 0);

        mOutlineColor = typedArray.getColor(R.styleable.ModuleStatusView_outlineColor, Color.BLACK);
        mShape = typedArray.getInt(R.styleable.ModuleStatusView_shape, SHAPE_CIRCLE);
        mOutlineWidth = typedArray.getDimension(R.styleable.ModuleStatusView_outlineWidth,
                defaultOutlineWidthPixels);
//        Color fillColor = typedArray.getColor(R.styleable.ModuleStatusView_fillColor, Color.BLUE);

        // Once we call recycle, we can't interact with typeArray any more
        typedArray.recycle();

        // Set up sizing values
        // a variable to specify the with of the outline
        // that we want to draw around each of our circles
        mShapeSize = 144f;
        mSpacing = 30f;
        // get the circle's radius
        mRadius = (mShapeSize - mOutlineWidth) / 2;


        // ANTI_ALIAS_FLAG makes all edges smooth, not rough
        mPaintOutline = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintOutline.setStyle(Paint.Style.STROKE);
        mPaintOutline.setStrokeWidth(mOutlineWidth);
        mPaintOutline.setColor(mOutlineColor);

        int fillColor = getContext().getResources().getColor(R.color.plural_sight_orange);
        mPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintFill.setStyle(Paint.Style.FILL);
        mPaintFill.setColor(fillColor);
    }

        // We have to forward some of the view class callbacks to the helper class
        // We do that by overriding these methods


    @Override
    public void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect){
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        mAccessibilityHelper.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return mAccessibilityHelper.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
    }

    @Override
    protected boolean dispatchHoverEvent(MotionEvent event) {
        return mAccessibilityHelper.dispatchHoverEvent(event) || super.dispatchHoverEvent(event);
    }

    private void setupEditModeValue() {
        boolean[] exampleModuleValues = new boolean[EDIT_MODE_MODULE_COUNT];
        int middle = EDIT_MODE_MODULE_COUNT / 2;
        for (int i = 0; i < middle; i++)
            exampleModuleValues[i] = true;
        setModuleStatus(exampleModuleValues);
    }

    private void setUpModuleRectangles(int width) {

        int availableWidth = width - getPaddingLeft() - getPaddingRight();
        // How many modules can fit horizontally
        int horizontalModulesThatCanFit = (int) (availableWidth / (mShapeSize + mSpacing));
        int maxHorizontalModules = Math.min(horizontalModulesThatCanFit, mModuleStatus.length);
        mModuleRectangles = new Rect[mModuleStatus.length];
        for (int moduleIndex = 0; moduleIndex < mModuleRectangles.length; moduleIndex++) {

            int column = moduleIndex % maxHorizontalModules;
            int row = moduleIndex / maxHorizontalModules;

            // Position of the left edge of the rectangle
            int x = getPaddingLeft() + (int) (column * (mShapeSize + mSpacing));

            // Top edge position of each of the rectangles, it's the same for all
            int y = getPaddingTop() + (int) (row * (mShapeSize + mSpacing));
            // x, y, position of the right edge, position of the left edge
            mModuleRectangles[moduleIndex] = new Rect(x, y, x + (int) mShapeSize,
                    y + (int) mShapeSize);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = 0;
        int desiredHeight = 0;

        // Check the values that were given to us if they have enough space
        // given values are encoded, we use methods
        // that access the values we want


        // this gives the total with, ie plus padding
        int specWidth = MeasureSpec.getSize(widthMeasureSpec);
        int availableWidth = specWidth - getPaddingLeft() - getPaddingRight();

        // Determine how many module circles will fit within this width
        int horizontalModulesThatCanFit = (int) (availableWidth / (mShapeSize + mSpacing));
        mMaxHorizontalModules = Math.min(horizontalModulesThatCanFit, mModuleStatus.length);

        desiredWidth = (int) ((mMaxHorizontalModules * (mShapeSize + mSpacing)) - mSpacing);
        desiredWidth += getPaddingLeft() + getPaddingRight();

        int rows = ((mModuleStatus.length - 1) / mMaxHorizontalModules) + 1;

        desiredHeight = (int) ((rows * (mShapeSize + mSpacing) - mSpacing));
        desiredHeight += getPaddingTop() + getPaddingBottom();

        int width = resolveSizeAndState(desiredWidth, widthMeasureSpec, 0);
        int height = resolveSizeAndState(desiredHeight, heightMeasureSpec, 0);

        // Inform the system of what values to use
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int currentWidth, int currentHeight, int oldWidth, int oldHeight) {
        setUpModuleRectangles(currentWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // The key part of the drawing is positioning
        // the circles in the correct place.
        // Loop thru the rectangle array we created earlier

        for (int moduleIndex = 0; moduleIndex < mModuleRectangles.length; moduleIndex++) {
            // we need to determine the x and y coordinates of our circle center points

            if (mShape == SHAPE_CIRCLE) {
                float x = mModuleRectangles[moduleIndex].centerX();
                float y = mModuleRectangles[moduleIndex].centerY();

                if (mModuleStatus[moduleIndex])
                    canvas.drawCircle(x, y, mRadius, mPaintFill);

                canvas.drawCircle(x, y, mRadius, mPaintOutline);
            } else {
                drawSquare(canvas, moduleIndex);
            }
        }
    } // end of onDraw method

    private void drawSquare(Canvas canvas, int moduleIndex) {
        Rect moduleRectangle = mModuleRectangles[moduleIndex];

        if(mModuleStatus[moduleIndex])
            canvas.drawRect(moduleRectangle, mPaintFill);

        canvas.drawRect(moduleRectangle.left + (mOutlineWidth /2),
                moduleRectangle.top + (mOutlineWidth /2),
                moduleRectangle.right - (mOutlineWidth /2),
                moduleRectangle.bottom - (mOutlineWidth/2),
                mPaintOutline);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // This tells us the specific action that occurs
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_UP:
                                    // get the module the user tapped
                int moduleIndex = findItemAtPoint(event.getX(), event.getY());
                onModuleSelected(moduleIndex);
                return true;
        }


        return super.onTouchEvent(event);
    }

    private void onModuleSelected(int moduleIndex) {
        if (moduleIndex == INVALID_INDEX)
            return;
        // As long as we get passed this if statement,
        // we know we have a valid moduleIndex

        // Set the mModuleStatus value that corresponds to that index
        mModuleStatus[moduleIndex] = !mModuleStatus[moduleIndex];
        // If a change occurs in our code that affects the value of our view,
        // we need to let the system know of the change,
        // so the view will be redrawn. We do this by calling invalidate()
        invalidate();

        mAccessibilityHelper.invalidateVirtualView(moduleIndex);
        mAccessibilityHelper.sendEventForVirtualView(moduleIndex, AccessibilityEvent.TYPE_VIEW_CLICKED);
    }

    private int findItemAtPoint(float x, float y) {
        // Use this method to figure out the index
        // of the rectangle the user touched
        int moduleIndex = INVALID_INDEX;
        for (int i = 0; i < mModuleRectangles.length; i++){
            if(mModuleRectangles[i].contains((int) x, (int) y)){
                moduleIndex = i;
                break;
            }
        }
        return moduleIndex;
    }

    private class ModuleStatusAccessibilityHelper extends ExploreByTouchHelper{

        /**
         * Constructs a new helper that can expose a virtual view hierarchy for the
         * specified host view.
         *
         * @param host view whose virtual view hierarchy is exposed by this helper
         */
        public ModuleStatusAccessibilityHelper(@NonNull View host) {
            super(host);
        }

        @Override
        protected int getVirtualViewAt(float x, float y) {
            int moduleIndex = findItemAtPoint(x, y);
            return moduleIndex == INVALID_INDEX ? ExploreByTouchHelper.INVALID_ID : moduleIndex;
        }

        @Override
        protected void getVisibleVirtualViews(List<Integer> virtualViewIds) {
            if (mModuleRectangles == null ) return;
            for (int moduleIndex = 0; moduleIndex < mModuleRectangles.length; moduleIndex++) {
                virtualViewIds.add(moduleIndex);
            }
        }

        @Override
        protected void onPopulateNodeForVirtualView(int virtualViewId,
                                                    @NonNull AccessibilityNodeInfoCompat node) {
            node.setFocusable(true);
            node.setBoundsInParent(mModuleRectangles[virtualViewId]);
            node.setContentDescription("Module " + virtualViewId);

            node.setCheckable(true);
            node.setChecked(mModuleStatus[virtualViewId]);

            node.addAction(AccessibilityNodeInfo.ACTION_CLICK);
        }

        @Override
        protected boolean onPerformActionForVirtualView(int virtualViewId,
                                                        int action, @Nullable Bundle arguments) {


            switch (action){
                case AccessibilityNodeInfo.ACTION_CLICK:

                    onModuleSelected(virtualViewId);
                    return true;
            }
            return false;
        }
    }
} // end of ModuleStatusView class
