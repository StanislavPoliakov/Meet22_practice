package home.stanislavpoliakov.meet22_practice;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Класс, описывающий Custom ViewGroup
 */
public class MyCustomLayout extends ViewGroup {
    private static final String TAG = "meet22_logs";
    private int maxColumnCount; // Количество столбцов
    private int maxRowCount; // Количество строк

    public MyCustomLayout(Context context) {
        super(context);
    }

    public MyCustomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyCustomLayoutLP);
        maxColumnCount = typedArray.getInt(R.styleable.MyCustomLayoutLP_maxColumnCount, 1);
        maxRowCount = typedArray.getInt(R.styleable.MyCustomLayoutLP_maxRowCount, 1);
        typedArray.recycle();
    }

    public MyCustomLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Google просит добавлять этот метод для нескролящихся Layout'-ов
     * @return
     */
    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    /**
     * @return считанное из attrs.xml значение максимального количества столбцов
     */
    public int getMaxColumnCount() {
        return maxColumnCount;
    }

    /**
     * @return считанное из attrs.xml значение максимального количества строк
     */
    public int getMaxRowCount() {
        return maxRowCount;
    }

    /**
     * Метод измерения всех дочерних элементов. Вызывается дважды!
     * @param widthMeasureSpec спецификация ширины
     * @param heightMeasureSpec спецификация высоты
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        int maxWidth = 0, maxHeight = 0, childState = 0;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();

            // Измерения максимальной высоты будем проводить по "0"-му столбцу, поскольку
            // все столбцы одинаковы
            if (layoutParams.eColumn == 0) {
                maxHeight += child.getMeasuredHeight() * layoutParams.eHeight
                        + layoutParams.topMargin + layoutParams.bottomMargin;
            }

            // Также и измерение максимальной ширины проведем по "0"-ой строке, поскольку
            // все строки одинаковы
            if (layoutParams.eRow == 0) {
                maxWidth += child.getMeasuredWidth() * layoutParams.eWidth
                        + layoutParams.leftMargin + layoutParams.rightMargin;
            }
            childState = combineMeasuredStates(childState, child.getMeasuredState());
        }

        // Растягивает все наши элементы на весь экран
        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec,
                        childState << MEASURED_HEIGHT_STATE_SHIFT));
    }

    /**
     * Метод расположения всех дочерних элементов на ViewGroup
     * @param changed
     * @param l самая левая точка Layout (фактический 0)
     * @param t самая верхняя точка Layout (фактический 0)
     * @param r самая правая точка Layout (ширина всех элементов, посчитанная в onMeasure)
     * @param b самамя нижная точка Layout (высота всех элементов, посчитанная в onMeasure)
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();

        // Ширина столбца
        int singleWidth = r / maxColumnCount;

        // Ширина строки
        int sigleHeight = b / maxRowCount;
        for (int i = 0; i < count; i ++) {
            View child = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();

            // Левая точка элемента = номер столбца * ширина столбца
            int childLeft = layoutParams.eColumn * singleWidth;

            // Верхняя точка элемента = номер строки * высота строки
            int childTop = layoutParams.eRow * sigleHeight;

            // Правая точка элемента = (номер столбца + ширина элемента) * ширина столбца
            int childRight = (layoutParams.eColumn + layoutParams.eWidth) * singleWidth;

            // Нижняя точка элемента = (номер строки + высота элемента) * высота строки
            int childBottom = (layoutParams.eHeight +layoutParams.eRow) * sigleHeight;

            // Размещаем элемент
            child.layout(childLeft, childTop, childRight, childBottom);
        }
    }

    /**
     * Переопределяем метод получения LayoutParams для measureChildWithMargins.
     * Более того, мы должны вернуть LayoutParams extends MarginLayoutParams
     * @param attrs Набор атрибутов
     * @return
     */
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

    /**
     * Наши параметры для Layout, для дочерних элементов, если быть точнее
     */
    public static class LayoutParams extends MarginLayoutParams {

        //Не хочу инкапсулировать и реализоваывать JavaBean set/get
        int eColumn = 0, eRow = 0, eWidth = 1, eHeight = 1;

        /**
         * Считываем параметры из XML
         * @param c
         * @param attrs
         */
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
