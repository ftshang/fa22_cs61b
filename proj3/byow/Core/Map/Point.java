package byow.Core.Map;

/** Class that serves as a Point (a coordinate on a graph). */
public class Point {
    private int x;
    private int y;

    /** Two parameter Constructor that takes in an x-coordinate and y-coordinate. */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /** Method that returns the x-coordinate of the Point object. */
    public int getX() {
        return x;
    }

    /** Method that returns the y-coordinate of the Point object. */
    public int getY() {
        return y;
    }

    /** Sets the calling Point object's location to the x and y position. */
    public void setPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /** Shifts the calling Point object's location to the left. */
    public void moveLeft(int x) {
        this.x -= x;
    }

    /** Shifts the calling Point object's location to the right. */
    public void moveRight(int x) {
        this.x += x;
    }

    /** Shifts the calling Point object's location upward in the vertical direction. */
    public void moveUp(int y) {
        this.y += y;
    }

    /** Shifts the calling Point object's location downward in the vertical direction. */
    public void moveDown(int y) {
        this.y -= y;
    }

    /** Returns a new Point object that is in the left direction of the calling Point object. */
    public Point newLeftPoint(int x) {
        return new Point(this.x - x, this.y);
    }

    /** Returns a new Point object that is in the right direction of the calling Point object. */
    public Point newRightPoint(int x) {
        return new Point(this.x + x, this.y);
    }

    /** Returns a new Point object that is directly north of the calling Point object. */
    public Point newUpPoint(int y) {
        return new Point(this.x, this.y + y);
    }

    /** Returns a new Point object that is directly below the calling Point object. */
    public Point newDownPoint(int y) {
        return new Point(this.x, this.y - y);
    }

    /** Overridden equals method. Two points are considered equal if both their x and y positions are the same. */
    @Override
    public boolean equals(Object otherPoint) {
        if (otherPoint instanceof Point p) {
            return this.x == p.x && this.y == p.y;
        }
        return false;
    }
}
