package home.stanislavpoliakov.meet22_practice;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class MyCustomLayout extends ViewGroup {
    private static final String TAG = "meet22_logs";

    public MyCustomLayout(Context context) {
        super(context);
    }

    public MyCustomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCustomLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        int maxWidth = 0, maxHeight = 0, childState = 0;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            maxWidth = child.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
            maxHeight = child.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
            childState = combineMeasuredStates(childState, child.getMeasuredState());
        }
        //setMeasuredDimension(maxWidth * 4, maxHeight * 8);
        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec,
                        childState << MEASURED_HEIGHT_STATE_SHIFT));
        //Log.d(TAG, "onMeasure: ");
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        int childPosition = 0;
        int left = l, top = t, right = r / 4, bottom = b / 8;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 8; j++) {
                View child = getChildAt(childPosition++);
                child.layout(left, top, right, bottom);
                top = bottom;
                bottom += b / 8;
            }
            left = right;
            right += r / 4;
            top = t;
            bottom = b / 8;
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MyCustomLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public static class LayoutParams extends MarginLayoutParams {
        int eColumn, eRow, eWidth, eHeight;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray typedArray = c.obtainStyledAttributes(attrs, R.styleable.MyCustomLayoutLP);
            eColumn = typedArray.getInt(R.styleable.MyCustomLayoutLP_element_column, 0);
            eRow = typedArray.getInt(R.styleable.MyCustomLayoutLP_element_row, 0);
            eWidth = typedArray.getInt(R.styleable.MyCustomLayoutLP_element_width, 1);
            eHeight = typedArray.getInt(R.styleable.MyCustomLayoutLP_element_height, 1);
            typedArray.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
