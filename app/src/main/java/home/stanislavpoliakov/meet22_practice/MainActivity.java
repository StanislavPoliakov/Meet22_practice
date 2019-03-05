package home.stanislavpoliakov.meet22_practice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "meet22_logs";
    private int maxColumnCount;
    private int maxRowCount;
    private boolean isFull = false;
    private boolean[][] matrix;
    private MyCustomLayout customLayout;
    private int buttonCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_group_layout);

        customLayout = findViewById(R.id.customLayout);
        maxColumnCount = customLayout.getMaxColumnCount();
        maxRowCount = customLayout.getMaxRowCount();
        populateLayout();
    }

    /**
     * Общий метод наполнения нашей ViewGroup элементами
     */
    private void populateLayout() {
        initMatrix(maxColumnCount, maxRowCount);

        while (!isFull) {
            Position start = obtainStartingPosition();
            Position end = getEndingPosition(start);

            MyCustomLayout.LayoutParams lp = getParams(start, end);
            addButton(lp);
            markMatrix(start, end);
        }
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

        // Генератор случайных чисел ограничиваем количеством оставшихся "пустых" мест
        int rowHeight = Math.max(1, (int) Math.round(Math.random() * rowsLeft(start)));
        int columnWidth = Math.max(1, (int) Math.round(Math.random() * columnsLeft(start)));
        end.set(start.row + rowHeight - 1, start.column + columnWidth - 1);
        return end;
    }

    /**
     * Метод установки LayoutParams для дочернего элемента.
     * Без WRAP_CONTENT не отображает Button.text. В принципе, смотрится неплохо, просто
     * некорректно.
     * @param start стартовая точка элемента
     * @param end конечная точка элемента
     * @return параметры
     */
    private MyCustomLayout.LayoutParams getParams(Position start, Position end) {
        MyCustomLayout.LayoutParams layoutParams = (MyCustomLayout.LayoutParams) customLayout.generateLayoutParams(
                customLayout.generateDefaultLayoutParams());
        layoutParams.width = MyCustomLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.height = MyCustomLayout.LayoutParams.WRAP_CONTENT;
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
    private void addButton(MyCustomLayout.LayoutParams lp) {
        Button button = new Button(this);
        String buttonText = "Button" + buttonCount++;
        button.setText(buttonText);
        button.setGravity(Gravity.CENTER); // Очень сомнительно это работает
        button.setLayoutParams(lp);
        customLayout.addView(button);
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
