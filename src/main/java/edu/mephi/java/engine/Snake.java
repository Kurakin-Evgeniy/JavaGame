package edu.mephi.java.engine;

import java.awt.*;
import java.util.Iterator;

public class Snake implements Iterable<Point> {
    private final Point fieldSize;
    private Segment head;
    private boolean fruitIsEaten = false;

    public Snake(Point fieldSize) {
        this.fieldSize = fieldSize;
        head = new Segment(new Point(0, 0));
    }


    public boolean move(Direction direction) {
        int y = head.position.y;
        int x = head.position.x;

        switch (direction) {
            case Direction.UP -> {
                if (y - 1 < 0)
                    return false;
                head.position.y = y - 1;

            }
            case Direction.DOWN -> {
                if (y + 1 >= fieldSize.y)
                    return false;
                head.position.y = y + 1;
            }
            case Direction.LEFT -> {
                if (x - 1 < 0)
                    return false;
                head.position.x = x - 1;
            }
            case Direction.RIGHT -> {
                if (x + 1 >= fieldSize.x)
                    return false;
                head.position.x = x + 1;
            }
        }

        if (head.nextSegment != null)
            return moveSegment(head.nextSegment, x, y);


        if (fruitIsEaten) {
            fruitIsEaten = false;
            head.nextSegment = new Segment(new Point(x, y));
        }
        return true;
    }

    private boolean moveSegment(Segment segment, int x, int y) {
        int prevX = segment.position.x;
        int prevY = segment.position.y;
        if (head.position.equals(segment.position))
            return false;

        segment.position.x = x;
        segment.position.y = y;

        if (segment.nextSegment == null) {
            if (fruitIsEaten) {
                fruitIsEaten = false;
                segment.nextSegment = new Segment(new Point(prevX, prevY));
            }
        } else
            return moveSegment(segment.nextSegment, prevX, prevY);

        return true;
    }

    public void eatFruit() {
        fruitIsEaten = true;
    }

    public Point getHeadPosition() {
        return head.position;
    }

    @Override
    public Iterator<Point> iterator() {
        return new Iterator<>() {
            private Segment segment = head;

            @Override
            public boolean hasNext() {
                return segment != null;
            }

            @Override
            public Point next() {
                Point position = segment.position;
                segment = segment.nextSegment;
                return position;
            }
        };
    }

    private static class Segment {
        public Segment nextSegment = null;
        public Point position;

        public Segment(Point position) {
            this.position = position;
        }
    }
}
