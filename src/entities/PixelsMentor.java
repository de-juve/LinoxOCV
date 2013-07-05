package entities;

import org.opencv.core.Mat;

import java.util.ArrayList;

public class PixelsMentor {
    public static ArrayList<Point> getNeighborhoodOfPixel(int x, int y, Mat image, int radius) {
        ArrayList<Point> neighbors = new ArrayList<>();
        for(int j = Math.max(0, y-radius); j <= Math.max(image.height()-1, y+radius); j++) {
            for(int i = Math.max(0, x-radius); i <= Math.max(image.width()-1, x+radius); i++) {
               if(i != x && j != y) {
                    neighbors.add(new Point(i, j));
               }
            }
        }
        return neighbors;
    }

}
