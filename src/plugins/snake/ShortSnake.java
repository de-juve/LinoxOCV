package plugins.snake;

import java.util.LinkedList;

public class ShortSnake<T extends SnakePoint> {
    private LinkedList<T> head, baseSetPoints;
    private int headSize = 10, baseSetPointsSize;
    private int headId;
    private double step = 1;
    private double inc = 0.01;
    private double minStep = 1E-4;
    private double maxStep = 100;

    public ShortSnake(int _baseSetPointsSize) {
        head = new LinkedList<>();
        baseSetPoints = new LinkedList<>();
        headId = 0;
        baseSetPointsSize = headSize = _baseSetPointsSize;
    }

    public boolean addElementToHead(T element) {
        if (head.size() + 1 > headSize) {
            head.clear();
        }
        head.add(element);
        headId = (int) element.x;
        return true;
    }

    public boolean addElementToBaseSetPoints(T element) {
        if (baseSetPoints.size() + 1 > baseSetPointsSize) {
            return false;
        }
        baseSetPoints.add(element);
        return true;
    }

    public LinkedList<T> getHead() {
        return head;
    }

    public LinkedList<T> getBaseSetPoints() {
        return baseSetPoints;
    }

    public boolean increaseStep() {
        if (step >= maxStep) {
            return false;
        }
        step += inc;
        return true;
    }

    public boolean reduceStep() {
        if (step <= minStep) {
            return false;
        }
        while (step - inc <= 0) {
            inc /= 2;
        }
        step -= inc;
        return true;
    }

    public double getStep() {
        return step;
    }

    public double getHeadId() {
        return headId;
    }
}
