package entities;

import org.opencv.core.Mat;

import java.util.ArrayList;

public class PixelsMentor {
    public static ArrayList<Point> getNeighborhoodOfPixel(int x, int y, Mat image, int radius) {
        ArrayList<Point> neighbors = new ArrayList<>();
        for (int j = Math.max(0, y - radius); j <= Math.min(image.height() - 1, y + radius); j++) {
            for (int i = Math.max(0, x - radius); i <= Math.min(image.width() - 1, x + radius); i++) {
                if (i == x && j == y) {
                    continue;
                }
                neighbors.add(new Point(i, j));
            }
        }
        return neighbors;
    }

    public static ArrayList<Integer> getNeighborsOfPixel(int x, int y, Mat image, int radius) {
        ArrayList<Integer> neighbors = new ArrayList<>();
        ArrayList<Point> n = getNeighborhoodOfPixel(x, y, image, radius);
        for (Point p : n) {
            neighbors.add(p.x + p.y * image.width());
        }
        return neighbors;
    }

    public static ArrayList<Integer> defineNeighboursIdsWithLowerValue(int id, Mat image) {
        int x = id % image.width();
        int y = id / image.width();
        ArrayList<Integer> resultArray = new ArrayList<>();
        ArrayList<Integer> neighbouresIds = getNeighborsOfPixel(x, y, image, 1);
        for (Integer nid : neighbouresIds) {
            int nx = nid % image.width();
            int ny = nid / image.width();
            if (image.get(y, x)[0] > image.get(ny, nx)[0]) {
                resultArray.add(nid);
            }
        }
        return resultArray;
    }

    public static ArrayList defineNeighboursIdsWithSameValue(int id, Mat image) {
        int x = id % image.width();
        int y = id / image.width();
        ArrayList<Integer> resultArray = new ArrayList<>();
        ArrayList<Integer> neighbouresIds = getNeighborsOfPixel(x, y, image, 1);
        for (Integer nid : neighbouresIds) {
            int nx = nid % image.width();
            int ny = nid / image.width();
            if (image.get(y, x)[0] == image.get(ny, nx)[0]) {
                resultArray.add(nid);
            }
        }
        return resultArray;
    }

    public static ArrayList<Integer> defineNeighboursIdsWidthDiagonalCondition(int id, Mat image) {
        ArrayList<Integer> resultArray = new ArrayList<>();
        ArrayList<Integer> neighbouresIds = getNeighborsOfPixel(id % image.width(), id / image.width(), image, 1);
        for (Integer nid : neighbouresIds) {
            if (isDiagonalNeighboure(id, nid, image.width())) {
                if (diagonalNeighboureCondition(id, nid, image) && id != nid) {
                    resultArray.add(nid);
                }
            } else if (id != nid) {
                resultArray.add(nid);
            }
        }
        return resultArray;
    }

    private static boolean isDiagonalNeighboure(int p, int n, int width) {
        int xp = p % width;
        int yp = p / width;
        int xn = n % width;
        int yn = n / width;
        return xp != xn && yp != yn;
    }


    private static boolean diagonalNeighboureCondition(int p, int n, Mat image) {
        int xp = p % image.width();
        int yn = n / image.width();
        int p1 = xp + yn * image.width();//getId(xp, yn, width, height);
        int yp = p / image.width();
        int xn = n % image.width();
        int p2 = xn + yp * image.width();//getId(xn, yp, width, height);

        return !((image.get(yn, xp)[0] > image.get(yp, xp)[0] || image.get(yn, xp)[0] > image.get(yn, xn)[0]) &&
                (image.get(yp, xn)[0] > image.get(yp, xp)[0] || image.get(yp, xn)[0] > image.get(yn, xn)[0]));
    }
}
