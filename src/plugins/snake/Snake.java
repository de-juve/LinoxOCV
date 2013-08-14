package plugins.snake;

import org.opencv.core.Point;

import java.util.LinkedList;

public class Snake<T extends SnakePoint> {
    private LinkedList<T> head, tail, baseSetPoints, line;
    private int headSize = 10, baseSetPointsSize, tailSize = 40;
    private int headId;
    private int step = 1;
    private boolean recount;

    public Snake(int _baseSetPointsSize) {
        head = new LinkedList<>();
        baseSetPoints = new LinkedList<>();
        tail = new LinkedList<>();
        line = new LinkedList<>();
        headId = 0;
        baseSetPointsSize = headSize = _baseSetPointsSize;
        recount = true;
    }

    public int getStep() {
        return step;
    }

    public boolean addElementToHead(T element) {
        if (head.size() + 1 > headSize) {
            for (T e : head) {
                e.removeStack();
            }
            addElementsToTail(head);
            head.clear();
        }

        head.add(element);
        headId = (int) element.x;
        return true;
    }

    public T getLastElementFromHead() {
        return head.getLast();
    }

    public T removeElementFromHead() {
        T element = head.removeLast();
        headId = (int) element.x;
        return element;
    }

    public boolean addElementToBaseSetPoints(T element) {
        if (baseSetPoints.size() + 1 > baseSetPointsSize) {
            return false;
        }
        baseSetPoints.add(element);
        return true;
    }

    public void removeElementsFromBaseSetPoints(int count) {
        for (int i = 0; i < Math.min(Math.abs(count), baseSetPointsSize - 1); i++) {
            baseSetPoints.removeFirst();//Last();
        }
    }

    private void addElementsToTail(LinkedList<T> elements) {
        if (tail.size() + elements.size() > tailSize) {
            addElementsToLine(tail);
            tail.clear();
        }
        tail.addAll(elements);
    }

    private void addElementsToLine(LinkedList<T> elements) {
        line.addAll(elements);
    }

    public int getBaseSetPointsSize() {
        return baseSetPoints.size();
    }

    public T getBaseSetPoint(int id) {
        return baseSetPoints.get(id);
    }

    public LinkedList<T> getLine() {
        return line;
    }

    public int getHeadSize() {
        return head.size();
    }

    public boolean isRecount() {
        if (recount) {
            recount = !recount;
            return !recount;
        }
        return recount;
    }

    public void merge() {
        if (head.size() > 0) {
            addElementsToTail(head);
            head.clear();
        }
        if (tail.size() > 0) {
            addElementsToLine(tail);
            tail.clear();
        }
    }

    public double getHeadId() {
        return headId;
    }

    public LinkedList<Integer> getTailValues() {
        LinkedList<Integer> values = new LinkedList<>();
        for (Point p : tail) {
            values.addFirst((int) p.y);
        }
        return values;
    }

    public boolean headContains(int value) {
        for (Point p : head) {
            if (p.y == value) {
                return true;
            }
        }
        return false;
    }

    public boolean tailContains(int value) {
        for (Point p : tail) {
            if (p.y == value) {
                return true;
            }
        }
        return false;
    }

    public LinkedList<Integer> getBaseSetPointsValues() {
        LinkedList<Integer> values = new LinkedList<>();
        for (Point p : baseSetPoints) {
            values.addFirst((int) p.y);
        }
        return values;
    }
}
