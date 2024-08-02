package byow.Core.Map;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Player {
    private Point p;
    private int score;

    /** One parameter constructor that initializes a Player object. */
    public Player(Point p) {
        score = 0;
        this.p = p;
    }

    /** Method that updates the tile in the world at the x and y coordinates. */
    private void updateTile(TETile[][] world, TETile tile, int x, int y) {
        world[x][y] = tile;
    }

    /** Method that is used to move the Player object in the upward direction. */
    public boolean moveUp(TETile[][] world) {
        boolean isUnlocked = tileAbove(world, Tileset.UNLOCKED_DOOR);

        if (!tileAbove(world, Tileset.WALL)) {
            boolean increaseScore = tileAbove(world, Tileset.TREE);
            int x = p.getX();
            int y = p.getY();
            int endY = y + 1;
            updateTile(world, Tileset.FLOOR, x, y);
            updateTile(world, Tileset.AVATAR, x, endY);
            if (increaseScore) {
                score += 1;
            }
            p.setPoint(x, endY);
        }

        return isUnlocked;
    }

    /** Method that is used to check the tile type in the up direction. */
    private boolean tileAbove(TETile[][] world, TETile tile) {
        return world[p.getX()][p.getY() + 1].equals(tile);
    }

    /** Method that moves the Player object in the down direction. */
    public boolean moveDown(TETile[][] world) {
        boolean isUnlocked = tileBelow(world, Tileset.UNLOCKED_DOOR);

        if (!tileBelow(world, Tileset.WALL)) {
            boolean increaseScore = tileBelow(world, Tileset.TREE);
            int x = p.getX();
            int y = p.getY();
            int endY = y - 1;
            updateTile(world, Tileset.FLOOR, x, y);
            updateTile(world, Tileset.AVATAR, x, endY);
            if (increaseScore) {
                score += 1;
            }
            p.setPoint(x, endY);
        }

        return isUnlocked;
    }

    /** Method that checks the tile type in the downward direction. */
    private boolean tileBelow(TETile[][] world, TETile tile) {
        return world[p.getX()][p.getY() - 1].equals(tile);
    }

    /** Method that moves the Player object in the right direction. */
    public boolean moveRight(TETile[][] world) {
        boolean isUnlocked = tileRight(world, Tileset.UNLOCKED_DOOR);

        if (!tileRight(world, Tileset.WALL)) {
            boolean increaseScore = tileRight(world, Tileset.TREE);
            int x = p.getX();
            int y = p.getY();
            int endX = x + 1;
            updateTile(world, Tileset.FLOOR, x, y);
            updateTile(world, Tileset.AVATAR, endX, y);
            if (increaseScore) {
                score += 1;
            }
            p.setPoint(endX, y);
        }

        return isUnlocked;
    }

    /** Method that checks the tile type in the right direction. */
    private boolean tileRight(TETile[][] world, TETile tile) {
        return world[p.getX() + 1][p.getY()].equals(tile);
    }

    /** Method that moves the Player object in the left direction. */
    public boolean moveLeft(TETile[][] world) {
        boolean isUnlocked = tileLeft(world, Tileset.UNLOCKED_DOOR);

        if (!tileLeft(world, Tileset.WALL)) {
            boolean increaseScore = tileLeft(world, Tileset.TREE);
            int x = p.getX();
            int y = p.getY();
            int endX = x - 1;
            updateTile(world, Tileset.FLOOR, x, y);
            updateTile(world, Tileset.AVATAR, endX, y);

            if (increaseScore) {
                score += 1;
            }
            p.setPoint(endX, y);
        }

        return isUnlocked;
    }

    /** Method that checks the tile type in the left direction. */
    private boolean tileLeft(TETile[][] world, TETile tile) {
        return world[p.getX() - 1][p.getY()].equals(tile);
    }

    /** Method that retrieve's the Player's Point object. */
    public Point getPoint() {
        return p;
    }

    /** Method that retrieves the Player's score variable. */
    public int getScore() {
        return score;
    }
}
