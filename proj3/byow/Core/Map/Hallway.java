package byow.Core.Map;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import java.util.*;

/** Class that represents a hallway in the randomly generated world. */
public class Hallway implements Comparable<Hallway> {
    private final Room roomOne;
    private final Room roomTwo;
    private final Random random;
    private final Room[][] roomWorld;
    private Point cornerPoint, startRowPoint, endRowPoint, startColPoint, endColPoint;
    public int length;

    /** Four parameter constructor that takes in two Room objects,
     * an integer for the seed, and a 2D array of Room objects. */
    public Hallway(Room r1, Room r2, int r, Room[][] roomWorld) {
        random = new Random(r);
        roomOne = r1;
        roomTwo = r2;
        this.roomWorld = roomWorld;
        findCornerPoint();
        length = (endRowPoint.getX() - startRowPoint.getX()) + (endColPoint.getY() - startColPoint.getY());
    }

    /** Method that draws the Hallway object's floor tiles. */
    public void drawHallwayFloor(TETile[][] world) {
        drawRow(world, Tileset.FLOOR, startRowPoint, endRowPoint);
        drawColumn(world, Tileset.FLOOR, startColPoint, endColPoint);
    }

    /** Method that draws the Hallway object's wall tiles. */
    public void drawHallwayWalls(TETile[][] world) {
        // Draw the rows
        // Up Row
        Point startP = startRowPoint.newUpPoint(1);
        Point endP = endRowPoint.newUpPoint(1);
        startP.moveLeft(1);
        endP.moveRight(1);
        drawRow(world, Tileset.WALL, startP, endP);
        // Bottom Row
        startP = startRowPoint.newDownPoint(1);
        endP = endRowPoint.newDownPoint(1);
        startP.moveLeft(1);
        endP.moveRight(1);
        drawRow(world, Tileset.WALL, startP, endP);
        // Draw the columns
        // Left column
        startP = startColPoint.newLeftPoint(1);
        endP = endColPoint.newLeftPoint(1);
        drawColumn(world, Tileset.WALL, startP, endP);
        // Right column
        startP = startColPoint.newRightPoint(1);
        endP = endColPoint.newRightPoint(1);

        drawColumn(world, Tileset.WALL, startP, endP);
    }

    /** Method that draws the current Hallway's row to the screen.
     * @param world - the 2D array of TETile objects that represents the board
     * @param tile - the type of tile that should be printed to the screen
     * @param startPoint - the starting point of the drawing
     * @param endPoint - the ending point of the drawing
     * */
    private void drawRow(TETile[][] world, TETile tile, Point startPoint, Point endPoint) {
        int startX = startPoint.getX();
        int endX = endPoint.getX();
        int y = endPoint.getY();

        for (int currentX = startX; currentX <= endX; currentX += 1) {
            if (world[currentX][y].equals(Tileset.NOTHING)) {
                world[currentX][y] = tile;
            }
        }
    }

    /** Method that draws the current Hallway's column to the screen.
     * @param world - the 2D array of TETile objects that represents the board
     * @param tile - the type of tile that should be printed to the screen
     * @param startPoint - the starting point of the drawing
     * @param endPoint - the ending point of the drawing
     * */
    private void drawColumn(TETile[][] world, TETile tile, Point startPoint, Point endPoint) {
        int startY = startPoint.getY();
        int endY = endPoint.getY();
        int x = endPoint.getX();

        for (int currentY = startY; currentY <= endY; currentY += 1) {
            if (world[x][currentY].equals(Tileset.NOTHING)) {
                world[x][currentY] = tile;
            }
        }
    }

    /** Sets the starting and ending points if the Hallway object is L-shaped. */
    private void findStartAndEndPoints(Point p1, Point p2) {
        // Move vertically
        if (p1.getX() == cornerPoint.getX()) {
            startColPoint = new Point(p1.getX(), Math.min(p1.getY(), cornerPoint.getY()));
            endColPoint = new Point(p1.getX(), Math.max(p1.getY(), cornerPoint.getY()));
            startRowPoint = new Point(Math.min(p2.getX(), cornerPoint.getX()), p2.getY());
            endRowPoint = new Point(Math.max(p2.getX(), cornerPoint.getX()), p2.getY());
        } else if (p2.getX() == cornerPoint.getX()) {
            startColPoint = new Point(p2.getX(), Math.min(p2.getY(), cornerPoint.getY()));
            endColPoint = new Point(p2.getX(), Math.max(p2.getY(), cornerPoint.getY()));
            startRowPoint = new Point(Math.min(p1.getX(), cornerPoint.getX()), p1.getY());
            endRowPoint = new Point(Math.max(p1.getX(), cornerPoint.getX()), p1.getY());
            // Horizontally
        } else if (p1.getY() == cornerPoint.getY()) {
            startRowPoint = new Point(Math.min(p1.getX(), cornerPoint.getX()), p1.getY());
            endRowPoint = new Point(Math.max(p1.getX(), cornerPoint.getX()), p1.getY());
            startColPoint = new Point(p2.getX(), Math.min(p2.getY(), cornerPoint.getY()));
            endColPoint = new Point(p2.getX(), Math.max(p2.getY(), cornerPoint.getY()));
        } else {
            startRowPoint = new Point(Math.min(p2.getX(), cornerPoint.getX()), p2.getY());
            endRowPoint = new Point(Math.max(p2.getX(), cornerPoint.getX()), p2.getY());
            startColPoint = new Point(p1.getX(), Math.min(p1.getY(), cornerPoint.getY()));
            endColPoint = new Point(p1.getX(), Math.max(p1.getY(), cornerPoint.getY()));
        }
    }

    /** Determines the corner point (the point where the hallway changes direction). */
    private void findCornerPoint() {
        int roomOneX = roomOne.getOpeningPoint().getX();
        int roomTwoX = roomTwo.getOpeningPoint().getX();
        int roomOneY = roomOne.getOpeningPoint().getY();
        int roomTwoY = roomTwo.getOpeningPoint().getY();

        // Vertically moving to the right
        if (roomOneX == roomTwoX) {
            cornerPoint = new Point(roomOneX, Math.max(roomOneY, roomTwoY));
            startColPoint = new Point(roomOneX, Math.min(roomOneY, roomTwoY));
            endColPoint = startRowPoint = endRowPoint = cornerPoint;
        } else if (roomOneY == roomTwoY) {
            cornerPoint = new Point(Math.max(roomOneX, roomTwoX), roomOneY);
            startRowPoint = new Point(Math.min(roomOneX, roomTwoX), roomOneY);
            endRowPoint = startColPoint = endColPoint = cornerPoint;
        } else {
            int pointNum = random.nextInt(2);
            int cornerX;
            int cornerY;
            if (pointNum == 0) {
                cornerX = roomOneX;
                cornerY = roomTwoY;
            } else {
                cornerX = roomTwoX;
                cornerY = roomOneY;
            }
            cornerPoint = new Point(cornerX, cornerY);
            findStartAndEndPoints(roomOne.getOpeningPoint(), roomTwo.getOpeningPoint());
        }
    }

    /** Returns a negative number if this Hallway object is less than the other Hallway object.
     * Returns 0 if this Hallway object is equal to the other Hallway object.
     * Returns a positive number if this Hallway object is greater than the other Hallway object.
     * @param o - the other Hallway object.
     * */
    @Override
    public int compareTo(Hallway o) {
        return this.length - o.length;
    }

    /** Returns the Hallway object's first Room object. */
    public Room getRoomOne() {
        return roomOne;
    }

    /** Returns the Hallway object's second Room object. */
    public Room getRoomTwo() {
        return roomTwo;
    }
}

