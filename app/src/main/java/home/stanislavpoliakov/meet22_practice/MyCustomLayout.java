package home.stanislavpoliakov.meet22_practice;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Класс, описывающий Custom ViewGroup
 */
public class MyCustomLayout extends ViewGroup {
    private static final String TAG = "meet22_logs";
    private final int maxColumnCount = 4; // Количество столбцов
    private final int maxRowCount = 8; // Количество строк
    private boolean[][] matrix; // Матрица элементов
    private int buttonCount = 0; // Количество созданных элементов (кнопок)
    private boolean isFull = false; // Триггер заполненности матрицы

    public MyCustomLayout(Context context) {
        super(context);
    }

    public MyCustomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        populateLayout(attrs);
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


    /** Метод инициализации матрицы элементов. Матрица булевых значений, изначально
     * инициализирована в false. По мере добавления дочерних элементов мы будет вносить
     * изменения и в матрицу. Она нам понадобится для того, чтобы отслеживать состояние нашего
     * поля (в данном случае, экрана), то есть чтобы понять какие элементы (размер) и где (положение)
     * нам необходимо создать
     * @param matrixWidth ширина матрицы, столбцов
     * @param matrixHeight высота матрицы, строк
     */
    private void initMatrix(int matrixWidth, int matrixHeight) {
         matrix = new boolean[matrixHeight][matrixWidth];
        for (int i = 0; i < matrixHeight; i++) {
            for (int j = 0; j < matrixWidth; j++) {
                matrix[i][j] = false;
            }
        }
    }

    /**
     * Метод получения первой пустой позиции, в которую можно вставить элемент. Если такой позиции
     * не существует (то есть вся матрица - true), то возвращается элемент с отрицательными значениями,
     * установленными в базовом конструкторе
     * @return позиция "пустого пространства", если таковая есть
     */
    private Position obtainStartingPosition() {
        Position position = new Position();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (!matrix[i][j]) {
                    position.set(i, j);
                    return position;
                }
            }
        }
        return position;
    }

    /**
     * Метод внесения изменений в матрицу состояний
     * @param start позиция верхнего левого угла дочернего элемента
     * @param end позиция нижнего правого угла дочернего элемента
     */
    private void markMatrix(Position start, Position end) {
        for (int i = start.row; i <= end.row; i++) {
            for (int j = start.column; j <= end.column; j++) {
                matrix[i][j] = true;
            }
        }
        //printMatrix();

        // После внесения изменений проверяем наличие свободных зон, и если их нет -
        // устанавливаем триггер заполненности матрицы в true (перестать заполнять)
        Position check = obtainStartingPosition();
        if (check.row == -1 && check.column == -1) isFull = true;
    }

    /**
     * Метод получения конечной точки (нижнего правого угла) дочернего элемента.
     * Именно здесь мы получаем Random-значения.
     * @param start стартовая позиция
     * @return конечная точка
     */
    private Position getEndingPosition(Position start) {
        Position end = new Position();

        // Генератор случайных чисел ограничиваем количество оставшихся "пустых" мест
        int rowHeight = Math.max(1, (int) Math.round(Math.random() * rowsLeft(start)));
        int columnWidth = Math.max(1, (int) Math.round(Math.random() * columnsLeft(start)));
        end.set(start.row + rowHeight - 1, start.column + columnWidth - 1);
        return end;
    }

    /**
     * Метод установки LayoutParams для дочернего элемента.
     * Без WRAP_CONTENT не отображает Button.text. В принципе, смотрится неплохо, просто
     * некорректно.
     * @param attrs Набор атрибутов declare-styleable
     * @param start стартовая точка элемента
     * @param end конечная точка элемента
     * @return параметры
     */
    private LayoutParams getParams(AttributeSet attrs, Position start, Position end) {
        LayoutParams layoutParams = generateLayoutParams(attrs);
        layoutParams.width = LayoutParams.WRAP_CONTENT;
        layoutParams.height = LayoutParams.WRAP_CONTENT;
        layoutParams.eRow = start.row;
        layoutParams.eColumn = start.column;
        layoutParams.eWidth = end.column - start.column + 1;
        layoutParams.eHeight = end.row - start.row + 1;

        return layoutParams;
    }

    /**
     * Метод создания и добавления дочернего элемента в ViewGroup
     * @param lp параметры дочернего элемента
     */
    private void addButton(LayoutParams lp) {
        Button button = new Button(getContext());
        String buttonText = "Button" + buttonCount++;
        button.setText(buttonText);
        button.setGravity(Gravity.CENTER); // Очень сомнительно это работает
        button.setLayoutParams(lp);
        addView(button);
    }

    /**
     * Метод вывода матрицы - для отладки
     */
    /*private void printMatrix() {
        StringBuilder stringBuilder = new StringBuilder("\n");
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                stringBuilder.append(matrix[i][j] ? "x" : ".");
            }
            stringBuilder.append("\n");
        }
        Log.d(TAG, "printMatrix: " + stringBuilder.toString());
    }*/

    /**
     * Метод проверки количества оставшихся столбцов, на которое можно создать новый элемент
     * @param point точка начала отсчета
     * @return количество столбцов
     */
    private int columnsLeft(Position point) {
        int count = 0;
        for (int i = point.column; i < matrix[point.row].length; i++) {
            if (!matrix[point.row][i]) count++;
            else return count;
        }
        return count;
    }

    /**
     * Метод проверки количества оставшихся строк, на которое можно создать новый элемент
     * @param point точка начала отсчета
     * @return количество строк
     */
    private int rowsLeft(Position point) {
        int count = 0;
        for (int i = point.row; i < matrix.length; i++) {
            if (!matrix[i][point.column]) count++;
            else return count;
        }
        return count;
    }

    /**
     * Общий метод наполнения нашей ViewGroup элементами
     * @param attrs Набор атрибутов для создания LayoutParams
     */
    private void populateLayout(AttributeSet attrs) {
        initMatrix(maxColumnCount, maxRowCount);

        while (!isFull) {
            Position start = obtainStartingPosition();
            Position end = getEndingPosition(start);

            LayoutParams lp = getParams(attrs, start, end);
            addButton(lp);
            markMatrix(start, end);
        }
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
        int eColumn, eRow, eWidth, eHeight;

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

    /**
     * Структура данных для начальной и конечной точки дочернего элемента
     */
    private class Position {

        // Специально не инкапсулирую потому что:
        // 1. Не хочу нагружать структуру данных стандартами JavaBean get/set
        // 2. Не хочу нагружать использование этих переменных скобками от методов
        // 3. Выглядит лаконично и доступно для понимания. Хотя, вероятно, и не совсем корректно!
        int column;
        int row;

         // Отрицательные значения - триггер отсутствия возможности найти пустое пространство
        Position() {
            this.column = -1;
            this.row = -1;
        }

        // Метод установки значений
        void set(int row, int column) {
            this.row = row;
            this.column = column;
        }
    }
}
