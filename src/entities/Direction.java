package entities;

public enum Direction {
    NORTH {
        @Override
        public Direction opposite() {
            return SOUTH;
        }

        @Override
        public Direction perpendicular1() {
            return EAST;
        }

        @Override
        public Direction perpendicular2() {
            return WEST;
        }

        @Override
        public Direction collinear1() {
            return NORTH_EAST;
        }

        @Override
        public Direction collinear2() {
            return NORTH_WEST;
        }

        @Override
        public Integer getNeighboureId( int id, int width ) {
            return id - width;
        }

        @Override
        public Point getNeighboure( Point point ) {
            return new Point( point.x, point.y - 1 );
        }

        @Override
        public double weight() {
            return 1;
        }
    },
    SOUTH {
        @Override
        public Direction opposite() {
            return NORTH;
        }

        @Override
        public Direction perpendicular1() {
            return EAST;
        }

        @Override
        public Direction perpendicular2() {
            return WEST;
        }

        @Override
        public Direction collinear1() {
            return SOUTH_EAST;
        }

        @Override
        public Direction collinear2() {
            return SOUTH_WEST;
        }

        @Override
        public Integer getNeighboureId( int id, int width ) {
            return id + width;
        }

        @Override
        public Point getNeighboure( Point point ) {
            return new Point( point.x, point.y + 1 );
        }

        @Override
        public double weight() {
            return 1;
        }
    },
    EAST {
        @Override
        public Direction opposite() {
            return WEST;
        }

        @Override
        public Direction perpendicular1() {
            return NORTH;
        }

        @Override
        public Direction perpendicular2() {
            return SOUTH;
        }

        @Override
        public Direction collinear1() {
            return SOUTH_EAST;
        }

        @Override
        public Direction collinear2() {
            return NORTH_EAST;
        }

        @Override
        public Integer getNeighboureId( int id, int width ) {
            return id - 1;
        }

        @Override
        public Point getNeighboure( Point point ) {
            return new Point( point.x - 1, point.y );
        }

        @Override
        public double weight() {
            return 1;
        }
    },
    WEST {
        @Override
        public Direction opposite() {
            return EAST;
        }

        @Override
        public Direction perpendicular1() {
            return NORTH;
        }

        @Override
        public Direction perpendicular2() {
            return SOUTH;
        }

        @Override
        public Direction collinear1() {
            return SOUTH_WEST;
        }

        @Override
        public Direction collinear2() {
            return NORTH_WEST;
        }

        @Override
        public Integer getNeighboureId( int id, int width ) {
            return id + 1;
        }

        @Override
        public Point getNeighboure( Point point ) {
            return new Point( point.x + 1, point.y );
        }

        @Override
        public double weight() {
            return 1;
        }
    },
    NORTH_WEST {
        @Override
        public Direction opposite() {
            return SOUTH_EAST;
        }

        @Override
        public Direction perpendicular1() {
            return NORTH_EAST;
        }

        @Override
        public Direction perpendicular2() {
            return SOUTH_WEST;
        }

        @Override
        public Direction collinear1() {
            return NORTH;
        }

        @Override
        public Direction collinear2() {
            return WEST;
        }

        @Override
        public Integer getNeighboureId( int id, int width ) {
            return id - width + 1;
        }

        @Override
        public Point getNeighboure( Point point ) {
            return new Point( point.x + 1, point.y - 1 );
        }

        @Override
        public double weight() {
            return Math.sqrt( 2 );
        }
    },
    NORTH_EAST {
        @Override
        public Direction opposite() {
            return SOUTH_WEST;
        }

        @Override
        public Direction perpendicular1() {
            return NORTH_WEST;
        }

        @Override
        public Direction perpendicular2() {
            return SOUTH_EAST;
        }

        @Override
        public Direction collinear1() {
            return NORTH;
        }

        @Override
        public Direction collinear2() {
            return EAST;
        }

        @Override
        public Integer getNeighboureId( int id, int width ) {
            return id - width - 1;
        }

        @Override
        public Point getNeighboure( Point point ) {
            return new Point( point.x - 1, point.y - 1 );
        }

        @Override
        public double weight() {
            return Math.sqrt( 2 );
        }
    },
    SOUTH_WEST {
        @Override
        public Direction opposite() {
            return NORTH_EAST;
        }

        @Override
        public Direction perpendicular1() {
            return NORTH_WEST;
        }

        @Override
        public Direction perpendicular2() {
            return SOUTH_EAST;
        }

        @Override
        public Direction collinear1() {
            return SOUTH;
        }

        @Override
        public Direction collinear2() {
            return WEST;
        }

        @Override
        public Integer getNeighboureId( int id, int width ) {
            return id + width + 1;
        }

        @Override
        public Point getNeighboure( Point point ) {
            return new Point( point.x + 1, point.y + 1 );
        }

        @Override
        public double weight() {
            return Math.sqrt( 2 );
        }
    },
    SOUTH_EAST {
        @Override
        public Direction opposite() {
            return NORTH_WEST;
        }

        @Override
        public Direction perpendicular1() {
            return NORTH_EAST;
        }

        @Override
        public Direction perpendicular2() {
            return SOUTH_WEST;
        }

        @Override
        public Direction collinear1() {
            return SOUTH;
        }

        @Override
        public Direction collinear2() {
            return EAST;
        }

        @Override
        public Integer getNeighboureId( int id, int width ) {
            return id + width - 1;
        }

        @Override
        public Point getNeighboure( Point point ) {
            return new Point( point.x - 1, point.y + 1 );
        }

        @Override
        public double weight() {
            return Math.sqrt( 2 );
        }
    };

    public abstract Direction opposite();

    public abstract Direction perpendicular1();

    public abstract Direction perpendicular2();

    public abstract Direction collinear1();

    public abstract Direction collinear2();

    public abstract Integer getNeighboureId( int id, int width );

    public abstract Point getNeighboure( Point point );

    public abstract double weight();

    public static Direction defineDirection( int current, int next, int width ) {
        Direction direction = EAST;
        int xC = current % width;
        int yC = current / width;
        int xN = next % width;
        int yN = next / width;

        if ( xC == xN ) {
            if ( yC < yN ) {
                direction = SOUTH;
            } else {
                direction = NORTH;
            }
        } else if ( xC > xN ) {
            if ( yC == yN ) {
                direction = WEST;
            } else if ( yC < yN ) {
                direction = SOUTH_WEST;
            } else {
                direction = NORTH_WEST;
            }

        } else if ( xC < xN ) {
            if ( yC == yN ) {
                direction = EAST;
            } else if ( yC < yN ) {
                direction = SOUTH_EAST;
            } else {
                direction = NORTH_EAST;
            }
        }
        return direction;
    }

}
