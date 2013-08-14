package plugins.approximation;

import org.opencv.core.Point;

import java.util.LinkedList;

public class LineApproximation {
    public Integer lagrange(double x, LinkedList<Point> line) {
        Integer L;
        double l;
        L = 0;

        for (int j = 0; j < line.size(); j++) {
            l = 1;
            for (int i = 0; i < line.size(); i++) {
                if (i != j) {
                    l *= (x - line.get(i).x) / (line.get(j).x - line.get(i).x);
                }
            }
            L += (int) (line.get(j).y * l);
        }

        return L;
    }

    public class SplineTuple {
        public double a, b, c, d, x;
    }

    public void buildSpline(LinkedList<Point> line, SplineTuple[] splines) {
        int n = line.size();
        // Инициализация массива сплайнов
        //splines = new SplineTuple[n];
        for (int i = 0; i < n; ++i) {
            splines[i] = new SplineTuple();
            splines[i].x = line.get(i).x;//x.get(i);
            splines[i].a = line.get(i).y;//y.get(i);
        }
        splines[0].c = splines[n - 1].c = 0.0;

        // Решение СЛАУ относительно коэффициентов сплайнов c[i] методом прогонки для трехдиагональных матриц
        // Вычисление прогоночных коэффициентов - прямой ход метода прогонки
        double[] alpha = new double[n - 1];
        double[] beta = new double[n - 1];
        alpha[0] = beta[0] = 0.0;
        for (int i = 1; i < n - 1; ++i) {
            double hi = line.get(i).x - line.get(i - 1).x;//x.get(i) - x.get(i - 1);
            double hi1 = line.get(i + 1).x - line.get(i).x;//x.get(i + 1) - x.get(i);
            double A = hi;
            double C = 2.0 * (hi + hi1);
            double B = hi1;
            double F = 6.0 * ((line.get(i + 1).y - line.get(i).y) / hi1 - (line.get(i).y - line.get(i - 1).y) / hi);//((y.get(i + 1) - y.get(i)) / hi1 - (y.get(i) - y.get(i - 1)) / hi);
            double z = (A * alpha[i - 1] + C);
            alpha[i] = -B / z;
            beta[i] = (F - A * beta[i - 1]) / z;
        }

        // Нахождение решения - обратный ход метода прогонки
        for (int i = n - 2; i > 0; --i) {
            splines[i].c = alpha[i] * splines[i + 1].c + beta[i];
        }

        // По известным коэффициентам c[i] находим значения b[i] и d[i]
        for (int i = n - 1; i > 0; --i) {
            double hi = line.get(i).x - line.get(i - 1).x;//x.get(i) - x.get(i - 1);
            splines[i].d = (splines[i].c - splines[i - 1].c) / hi;
            splines[i].b = hi * (2.0 * splines[i].c + splines[i - 1].c) / 6.0 + (line.get(i).y - line.get(i - 1).y) / hi;//(y.get(i) - y.get(i - 1)) / hi;
        }
    }

    // Вычисление значения интерполированной функции в произвольной точке
    public double interpolateSpline(double x, SplineTuple[] splines) throws NullPointerException {
        if (splines == null) {
            throw new NullPointerException("splines is null");
        }

        int n = splines.length;
        SplineTuple s;

        if (x <= splines[0].x) // Если x меньше точки сетки x[0] - пользуемся первым эл-тов массива
        {
            s = splines[1];
        } else if (x >= splines[n - 1].x) // Если x больше точки сетки x[n - 1] - пользуемся последним эл-том массива
        {
            s = splines[n - 1];
        } else // Иначе x лежит между граничными точками сетки - производим бинарный поиск нужного эл-та массива
        {
            int i = 0;
            int j = n - 1;
            while (i + 1 < j) {
                int k = i + (j - i) / 2;
                if (x <= splines[k].x) {
                    j = k;
                } else {
                    i = k;
                }
            }
            s = splines[j];
        }

        double dx = x - s.x;
        // Вычисляем значение сплайна в заданной точке по схеме Горнера (в принципе, "умный" компилятор применил бы схему Горнера сам, но ведь не все так умны, как кажутся)
        return s.a + (s.b + (s.c / 2.0 + s.d * dx / 6.0) * dx) * dx;
    }
}
