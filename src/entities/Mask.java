package entities;

import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by dm on 01.02.14.
 */
public class Mask {
    private Mat mask;
    private int roadWidth;
    private Part bg1, bg2, fg;
    private int threshold = 10;
    private int[] x_0, x_1, y_0, y_1;

    public ArrayList<Point> getRoad() {
        return fg.points;
    }


    public Mask(int cols, int rows, int _roadWidth, int type, int _threshold) {
        // while( rows % 3 != 0) rows++;

        mask = new Mat(rows, cols, type);
        roadWidth = _roadWidth;
        threshold = _threshold;
    }

    public void fill(Point start, Mat image) {
        byte[] buff = new byte[(int) mask.total() * mask.channels()];


        for (int y = start.y, j = 0; y < image.rows() && j < mask.rows(); y++, j++) {
            for (int x = start.x, i = 0; x < image.cols() && i < mask.cols(); x++, i++) {
                for (int k = 0; k < mask.channels(); k++) {
                    buff[i + j * mask.cols()] = (byte) image.get(y, x)[k];
                }
            }
        }
        mask.put(0, 0, buff);
    }

    public boolean analyze(MaskType type) {
        fg = new Part();
        bg1 = new Part();
        bg2 = new Part();

        partition(type);

        fg.countAvrL();
        bg1.countAvrL();
        bg2.countAvrL();
        if (Math.abs(fg.avrL - bg1.avrL) > threshold && Math.abs(fg.avrL - bg2.avrL) > threshold
                && Math.signum(fg.avrL - bg1.avrL) == Math.signum(fg.avrL - bg2.avrL)) {
            // System.out.println("yes "+ (fg.avrL - bg1.avrL) + " " + (fg.avrL - bg2.avrL));
            return true;
        }
        // System.out.println("no "+ (fg.avrL - bg1.avrL) + " " + (fg.avrL - bg2.avrL));
        return false;
    }

    public boolean analyzeNew() {
        x_0 = new int[roadWidth];
        x_1 = new int[roadWidth];
        y_0 = new int[roadWidth];
        y_1 = new int[roadWidth];

        for (int x = 0; x < mask.cols(); x++) {
            fg = new Part();
            bg1 = new Part();
            bg2 = new Part();
            for (int i = 0; i < roadWidth; i++) {
                x_0[i] = x + i;
                x_1[i] = mask.cols() - 1 - x_0[i];
                y_0[i] = 0;
                y_1[i] = mask.rows() - 1;
                // drawBresenhamLine(x_0[i], y_0[i], x_1[i], y_1[i]);
                line_s4(x_0[i], y_0[i], x_1[i], y_1[i]);
            }
            // System.out.println();
            // System.out.println(x);
            //checkRoad(0);
            if (checkRoad(0)) return true;
        }

        for (int y = 0; y < mask.rows(); y++) {
            fg = new Part();
            bg1 = new Part();
            bg2 = new Part();
            for (int i = 0; i < roadWidth; i++) {
                x_0[i] = 0;
                x_1[i] = mask.cols() - 1;
                y_0[i] = y + i;
                y_1[i] = mask.rows() - 1 - y_0[i];
                // drawBresenhamLine(x_0[i], y_0[i], x_1[i], y_1[i]);
                line_s4(x_0[i], y_0[i], x_1[i], y_1[i]);
            }
            //  System.out.println();
            //  System.out.println(y);

            checkRoad(1);
            if (checkRoad(1)) return true;
        }
        return false;

    }

    private boolean checkRoad(int type) {

        for (int yy = 0; yy < mask.rows(); yy++) {
            for (int xx = 0; xx < mask.cols(); xx++) {
                int part;
                if (type == 0) {
                    part = definePart1(xx, yy);
                } else {
                    part = definePart2(xx, yy);
                }

                if (part < 0) {
                    bg1.add(new Point(xx, yy));
                } else if (part == 0) {
                    //fg.add(new Point(xx, yy));
                } else {
                    bg2.add(new Point(xx, yy));
                }
            }
        }

        removeDuplicates(bg1);
        removeDuplicates(fg);
        removeDuplicates(bg2);

        // System.out.println(fg.points);
        // System.out.println(bg1.points);
        // System.out.println(bg2.points);
//        System.out.println();
//        System.out.println("BG1: " + bg1.points.toString());
//        System.out.println("FG:"+ fg.points.toString());
//        System.out.println("BG2: " + bg2.points.toString());


        fg.countAvrL();
        bg1.countAvrL();
        bg2.countAvrL();

        if (Math.abs(fg.avrL - bg1.avrL) > threshold && Math.abs(fg.avrL - bg2.avrL) > threshold
                && Math.signum(fg.avrL - bg1.avrL) == Math.signum(fg.avrL - bg2.avrL)) {
//            System.out.println();
//            System.out.println("true: " + Math.abs(fg.avrL - bg1.avrL) + " " + Math.abs(fg.avrL - bg2.avrL));
            return true;
        }
        // System.out.println("false: " + Math.abs(fg.avrL - bg1.avrL) + " " + Math.abs(fg.avrL - bg2.avrL));
        return false;
    }

    private void removeDuplicates(Part part) {
        HashSet hs = new HashSet();
        hs.addAll(part.points);
        part.points.clear();
        part.points.addAll(hs);
    }

    private int definePart2(int xx, int yy) {
        int result = 1;
        boolean p1 = false, p2 = false, p3 = false;
        for (Point p : fg.points) {
            if (p.y > yy && p.x == xx) {
                p1 = true;
                continue;
            }
            if (p.x == xx && p.y == yy) {
                p2 = true;
                continue;
            }
            if (p.y < yy && p.x == xx) {
                p3 = true;
            }
        }

        if (p1 && !p2 && !p3) {
            result = -1;
        }
        if (p2) {
            result = 0;
        }
        if (!p1 && !p2 && p3) {
            result = 1;
        }
        return result;
    }

    private int definePart1(int xx, int yy) {
        int result = 1;
        boolean p1 = false, p2 = false, p3 = false;
        for (Point p : fg.points) {
            if (p.x > xx && p.y == yy) {
                p1 = true;
                continue;
            }
            if (p.x == xx && p.y == yy) {
                p2 = true;
                continue;
            }
            if (p.x < xx && p.y == yy) {
                p3 = true;
            }
        }

        if (p1 && !p2 && !p3) {
            result = -1;
        }
        if (p2) {
            result = 0;
        }
        if (!p1 && !p2 && p3) {
            result = 1;
        }

        return result;
    }

    private void line_s4(int x1, int y1, int x2, int y2) {
        int x = x1, y = y1;
        int dx = Math.abs(x2 - x1), dy = Math.abs(y2 - y1);
        int sx = (x2 - x1) > 0 ? 1 : ((x2 - x1) == 0 ? 0 : -1);
        int sy = (y2 - y1) > 0 ? 1 : ((y2 - y1) == 0 ? 0 : -1);
        int e = 2 * dy - dx;
        boolean change = false;
        if (dy > dx) {
            int z = dx;
            dx = dy;
            dy = z;
            change = true;
        }

        // g.drawLine(x, y, x, y);
        if (x >= 0 && x < mask.cols() && y >= 0 && y < mask.rows()) {
            fg.add(new Point(x, y));
        }
        for (int k = 1; k <= (dx + dy); k++) {
            if (e < dx) {
                if (change) y += sy;
                else x += sx;
                e += 2 * dy;
            } else {
                if (change) x += sx;
                else y = y + sy;
                e -= 2 * dx;
            }
            //g.drawLine(x, y, x, y);
            if (x >= 0 && x < mask.cols() && y >= 0 && y < mask.rows()) {
                fg.add(new Point(x, y));
            }
        }
    }


    // Этот код "рисует" все 9 видов отрезков. Наклонные (из начала в конец и из конца в начало каждый), вертикальный и горизонтальный - тоже из начала в конец и из конца в начало, и точку.
    private int sign(int x) {
        return (x > 0) ? 1 : (x < 0) ? -1 : 0;
        //возвращает 0, если аргумент (x) равен нулю; -1, если x < 0 и 1, если x > 0.
    }

    private void drawBresenhamLine(int xstart, int ystart, int xend, int yend)
    /**
     * xstart, ystart - начало;
     * xend, yend - конец;
     * "g.drawLine (x, y, x, y);" используем в качестве "setPixel (x, y);"
     * Можно писать что-нибудь вроде g.fillRect (x, y, 1, 1);
     */
    {
        int x, y, dx, dy, incx, incy, pdx, pdy, es, el, err;

        dx = xend - xstart;//проекция на ось икс
        dy = yend - ystart;//проекция на ось игрек

        incx = sign(dx);
    /*
     * Определяем, в какую сторону нужно будет сдвигаться. Если dx < 0, т.е. отрезок идёт
	 * справа налево по иксу, то incx будет равен -1.
	 * Это будет использоваться в цикле постороения.
	 */
        incy = sign(dy);
	/*
	 * Аналогично. Если рисуем отрезок снизу вверх -
	 * это будет отрицательный сдвиг для y (иначе - положительный).
	 */

        if (dx < 0) dx = -dx;//далее мы будем сравнивать: "if (dx < dy)"
        if (dy < 0) dy = -dy;//поэтому необходимо сделать dx = |dx|; dy = |dy|
        //эти две строчки можно записать и так: dx = Math.abs(dx); dy = Math.abs(dy);

        if (dx > dy)
        //определяем наклон отрезка:
        {
	 /*
	  * Если dx > dy, то значит отрезок "вытянут" вдоль оси икс, т.е. он скорее длинный, чем высокий.
	  * Значит в цикле нужно будет идти по икс (строчка el = dx;), значит "протягивать" прямую по иксу
	  * надо в соответствии с тем, слева направо и справа налево она идёт (pdx = incx;), при этом
	  * по y сдвиг такой отсутствует.
	  */
            pdx = incx;
            pdy = 0;
            es = dy;
            el = dx;
        } else//случай, когда прямая скорее "высокая", чем длинная, т.е. вытянута по оси y
        {
            pdx = 0;
            pdy = incy;
            es = dx;
            el = dy;//тогда в цикле будем двигаться по y
        }

        x = xstart;
        y = ystart;
        err = el / 2;
        if (x >= 0 && x < mask.cols() && y >= 0 && y < mask.rows()) {
            fg.add(new Point(x, y));
        }
        // g.drawLine (x, y, x, y);//ставим первую точку
        //все последующие точки возможно надо сдвигать, поэтому первую ставим вне цикла

        for (int t = 0; t < el; t++)//идём по всем точкам, начиная со второй и до последней
        {
            err -= es;
            if (err < 0) {
                err += el;
                x += incx;//сдвинуть прямую (сместить вверх или вниз, если цикл проходит по иксам)
                y += incy;//или сместить влево-вправо, если цикл проходит по y
            } else {
                x += pdx;//продолжить тянуть прямую дальше, т.е. сдвинуть влево или вправо, если
                y += pdy;//цикл идёт по иксу; сдвинуть вверх или вниз, если по y
            }
            if (x >= 0 && x < mask.cols() && y >= 0 && y < mask.rows()) {
                fg.add(new Point(x, y));
            }

            //g.drawLine (x, y, x, y);
        }
    }

    private void partition(MaskType type) {


        if (mask.cols() != mask.rows()) {
            if (type.equals(MaskType.LUToRD)) {
                type = MaskType.LToR;
            } else if (type.equals(MaskType.LDToRU.LDToRU)) {
                type = MaskType.UToD;
            }
        }

        if (type.equals(MaskType.LToR)) {
            int bg1_y_end = (mask.rows() - roadWidth) / 2 - 1;
            int fg_y_end = bg1_y_end + roadWidth;

            for (int y = 0; y < mask.rows(); y++) {
                for (int x = 0; x < mask.cols(); x++) {
                    if (y >= 0 && y <= bg1_y_end) {
                        bg1.add(new Point(x, y));
                    } else if (y > bg1_y_end && y <= fg_y_end) {
                        fg.add(new Point(x, y));
                    } else {
                        bg2.add(new Point(x, y));
                    }
                }
            }
        } else if (type.equals(MaskType.UToD)) {
            int bg1_x_end = (mask.cols() - roadWidth) / 2 - 1;
            int fg_x_end = bg1_x_end + roadWidth;

            for (int y = 0; y < mask.rows(); y++) {
                for (int x = 0; x < mask.cols(); x++) {
                    if (x >= 0 && x <= bg1_x_end) {
                        bg1.add(new Point(x, y));
                    } else if (x > bg1_x_end && x <= fg_x_end) {
                        fg.add(new Point(x, y));
                    } else {
                        bg2.add(new Point(x, y));
                    }
                }
            }
        } else if (type.equals(MaskType.LUToRD)) {
            int half = (int) Math.floor((double) roadWidth / 2);
            int big_half = (int) Math.ceil((double) roadWidth / 2);
            for (int y = 0; y < mask.rows(); y++) {
                for (int x = Math.max(0, y - half); x < Math.min(mask.cols(), y + big_half); x++) {
                    fg.add(new Point(x, y));
                }
                for (int x = Math.min(mask.cols() - 1, y + big_half); x < mask.cols(); x++) {
                    Point p = new Point(x, y);
                    if (!fg.contains(p)) {
                        bg1.add(p);
                    }
                }
                for (int x = 0; x < Math.max(0, y - half); x++) {
                    Point p = new Point(x, y);
                    if (!fg.contains(p)) {
                        bg2.add(p);
                    }
                }
            }
        } else {
            int half = (int) Math.floor((double) roadWidth / 2);
            int big_half = (int) Math.ceil((double) roadWidth / 2);
            for (int y = 0; y < mask.rows(); y++) {
                for (int x = Math.max(0, mask.rows() - y - half - 1); x < Math.min(mask.cols(), mask.rows() - y + big_half - 1); x++) {
                    fg.add(new Point(x, y));
                }
                for (int x = 0; x < Math.max(1, mask.rows() - y - half - 1); x++) {
                    Point p = new Point(x, y);
                    if (!fg.contains(p)) {
                        bg1.add(p);
                    }
                }
                for (int x = Math.min(mask.cols() - 1, mask.rows() - y + big_half - 1); x < mask.cols(); x++) {
                    Point p = new Point(x, y);
                    if (!fg.contains(p)) {
                        bg2.add(p);
                    }
                }
            }
        }
    }

    public Mat getMask() {
        return mask;
    }


    public enum MaskType {
        LToR, UToD, LUToRD, LDToRU;
    }

    public class Part {
        ArrayList<Point> points;
        double avrL;
        double contrast;

        public Part() {
            points = new ArrayList<>();
            avrL = 0;
            contrast = 0;
        }

        private void add(Point p) {
            points.add(p);
        }

        private boolean contains(Point p) {
            return points.contains(p);
        }

        private void countAvrL() {
            for (Point p : points) {
                avrL += mask.get(p.y, p.x)[0];
                // System.out.println(mask.get(p.y, p.x)[0]);
            }
            avrL /= points.size();
        }
    }
}


