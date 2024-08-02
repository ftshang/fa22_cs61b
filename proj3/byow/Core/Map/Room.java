package byow.Core.Map;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.*;

/** Class that represents Room objects. Room objects can perform its own drawings to the Tile world. */
public class Room {
    private final Point upperLeftPoint;
    private final Point upperRightPoint;
    private final Point bottomLeftPoint;
    private final Point bottomRightPoint;

    private final int width;
    private final int height;

    private Point openingPoint;

    private final int roomID;

    /** Four parameter constructor that takes in an upper-left coordinate point, an ID, a room's width and height. */
    public Room(Point p, int id, int w, int h) {
        upperLeftPoint = p;
        width = w;
        height = h;
        upperRightPoint = upperLeftPoint.newRightPoint(width - 1);
        bottomLeftPoint = upperLeftPoint.newDownPoint(height - 1);
        bottomRightPoint = bottomLeftPoint.newRightPoint(width - 1);
        roomID = id;
        openingPoint = null;
    }

    /** Retrieves the upperLeftPoint of the calling Room object. */
    public Point getUpperLeftPoint() {
        return upperLeftPoint;
    }

    /** Retrieves the upperRightPoint of the calling Room object. */
    public Point getUpperRightPoint() {
        return upperRightPoint;
    }

    /** Retrieves the bottomRightPoint of the calling Room object. */
    public Point getBottomRightPoint() {
        return bottomRightPoint;
    }

    /** Retrieves the bottomLeftPoint of the calling Room object. */
    public Point getBottomLeftPoint() {
        return bottomLeftPoint;
    }

    /** Retrieves the calling Room object's ID. */
    public int getID() {
        return roomID;
    }

    /** Retrieves the calling Room object's width. */
    public int getWidth() {
        return width;
    }

    /** Retrieves the calling Room object's height. */
    public int getHeight() {
        return height;
    }

    /** Retrieves the calling Room object's opening Point. */
    public Point getOpeningPoint() {
        return openingPoint;
    }

    /** Sets the calling Room's objet's opening Point. */
    public void setOpeningPoint(Point p) {
        openingPoint = p;
    }

    /** Private helper method that draws the Room object's walls. */
    public void drawWalls(TETile[][] world) {
        // Draw ceiling
        drawRow(world, Tileset.WALL, upperLeftPoint, upperRightPoint);
        // Draw floor
        drawRow(world, Tileset.WALL, bottomLeftPoint, bottomRightPoint);
        // Draw left wall
        drawColumn(world, Tileset.WALL, bottomLeftPoint, upperLeftPoint);
        // Draw right wall
        drawColumn(world, Tileset.WALL, bottomRightPoint, upperRightPoint);
    }

    /** Method that draws in the Room object's floors.
     * Fills in the room from FLOOR to CEILING, also known as, BOTTOM to TOP.
     * */
    public void fillRoom(TETile[][] world, Random r) {
        // Starting row point
        Point rowStartPoint = new Point(bottomLeftPoint.getX() + 1, bottomLeftPoint.getY() + 1);
        // Ending row point
        Point rowEndPoint = new Point(bottomRightPoint.getX() - 1, bottomRightPoint.getY() + 1);

        int start = rowStartPoint.getY();
        int end = upperLeftPoint.getY();
        // Start filling the room from bottom to top
        for (int currentColumn = start; currentColumn < end; currentColumn += 1) {
            drawRow(world, Tileset.FLOOR, rowStartPoint, rowEndPoint);
            rowStartPoint.moveUp(1);
            rowEndPoint.moveUp(1);
        }
        generateOpeningPoint(r);
    }

    /** Randomly generates an opening point for the Room object.
     * The opening point serves as the point where a hallway can be placed at. */
    private void generateOpeningPoint(Random r) {
        int startX = upperLeftPoint.getX() + 1;
        int endX = upperRightPoint.getX();

        int startY = bottomRightPoint.getY() + 1;
        int endY = upperLeftPoint.getY();

        int randomX = r.nextInt(startX, endX);
        int randomY = r.nextInt(startY, endY);
        setOpeningPoint(new Point(randomX, randomY));
    }

    /** Private helper method that draws a tile to the row. Draws from LEFT to RIGHT.
     * @param world - the 2D array of TETile objects - represents the world being generated
     * @param tile - the tile type that should be drawn to the screen
     * @param startPoint - the starting point of the row (inclusive)
     *                   - startPoint's x value must be less than or equal to endPoint's x value
     * @param endPoint - the ending point of the row (inclusive)
     *                 - endPoint's x value must be greater than or equal to startPoint's x value
     * */
    private void drawRow(TETile[][] world, TETile tile, Point startPoint, Point endPoint) {
        if (startPoint.getY() == endPoint.getY()) {
            int y = startPoint.getY();
            int endX = endPoint.getX();
            int startX = startPoint.getX();
            for (int currentX = startX; currentX <= endX; currentX += 1) {
                if (world[currentX][y].equals(Tileset.NOTHING)) {
                    world[currentX][y] = tile;
                }
            }
        }
    }

    /** Private helper method that draws a tile to the column. Draws from BOTTOM to TOP.
     * @param world - the 2D array of TETile objects - represents the world being generated
     * @param tile - the tile type that should be drawn to the screen
     * @param startPoint - the starting point of the row (inclusive)
     *                   - startPoint's y value must be less than or equal to endPoint's y value
     * @param endPoint - the ending point of the row (inclusive)
     *                 - endPoint's y value must be greater than or equal to startPoint's y value
     * */
    private void drawColumn(TETile[][] world, TETile tile, Point startPoint, Point endPoint) {
        if (startPoint.getX() == endPoint.getX()) {
            int x = startPoint.getX();
            int endY = endPoint.getY();
            int startY = startPoint.getY();
            for (int currentY = startY; currentY <= endY; currentY += 1) {
                if (world[x][currentY].equals(Tileset.NOTHING)) {
                    world[x][currentY] = tile;
                }
            }
        }
    }
}
