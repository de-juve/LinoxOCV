package plugins.snake;

import org.opencv.core.Point;

import java.util.Stack;

public class SnakePoint extends Point {
    private Stack<Integer> neighbours;

    public int getStackSize() {
        return neighbours.size();
    }

    public void createStack() {
        neighbours = new Stack<>();
    }

    public void pushNeighbour( int neighbour ) {
        neighbours.push( neighbour );
    }

    public int popNeighbour() {
        return neighbours.pop();
    }

    public void removeStack() {
        if ( neighbours != null ) {
            neighbours.clear();
            neighbours = null;
        }
    }
}
