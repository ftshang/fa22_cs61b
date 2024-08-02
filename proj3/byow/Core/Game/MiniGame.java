package byow.Core.Game;

import byow.Core.Map.Player;
import byow.Core.Map.Point;
import byow.Core.Map.Room;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.util.Random;

public class MiniGame {
    private final int X_OFFSET = 0;
    private final int Y_OFFSET = 3;
    private final int width;
    private final int height;
    private final Room room;
    private int score;
    private final TETile[][] world;
    private final Random random;
    private final TERenderer ter;
    private final Player player;
    private int totalTrees;
    private boolean gameOver;

    /** Four parameter constructor that initializes the MiniGame object. */
    public MiniGame(int w, int h, long s, TERenderer t) {
        width = w;
        height = h;
        random = new Random(s);
        ter = t;
        world = new TETile[width - X_OFFSET][height - Y_OFFSET];
        totalTrees = 0;
        score = 0;
        gameOver = false;

        int roomW = random.nextInt(Y_OFFSET, width / 2);
        int roomH = random.nextInt(Y_OFFSET, height / 2);

        room = new Room(new Point(width / 2, (height - Y_OFFSET) / 2), 0, roomW, roomH);
        initializeBoard();
        room.fillRoom(world, random);
        room.drawWalls(world);
        player = new Player(room.getOpeningPoint());
        world[player.getPoint().getX()][player.getPoint().getY()] = Tileset.AVATAR;
        // create trees
        createTrees();
        startGame();
    }

    /** Method that creates the trees for the MiniGame board. */
    private void createTrees() {
        int startX = room.getBottomLeftPoint().getX() + 1;
        int endX = room.getBottomRightPoint().getX();

        int startY = room.getBottomLeftPoint().getY() + 1;
        int endY = room.getUpperLeftPoint().getY();

        for (int y = startY; y < endY; y += 1) {
            int x = random.nextInt(startX, endX);
            if (!world[x][y].equals(Tileset.AVATAR)) {
                world[x][y] = Tileset.TREE;
                totalTrees += 1;
            }
        }

    }

    /** Method used to initialize the game to a blank screen. */
    private void initializeBoard() {
        for (int x = 0; x < width - X_OFFSET; x += 1) {
            for (int y = 0; y < height - Y_OFFSET; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    /** Method that returns the TETile[][] array named world. */
    public TETile[][] getWorld() {
        return world;
    }

    /** Method that serves as the movement for the mini game. */
    private void move(char c) {
        if (c == 'W') {
            player.moveUp(world);
        } else if (c == 'S') {
           player.moveDown(world);
        } else if (c == 'A') {
            player.moveLeft(world);
        } else if (c == 'D') {
            player.moveRight(world);
        }
    }

    /** Method that serves as the main game loop. */
    public void startGame() {
        drawInstructions("You entered a secret room! Collect all trees to continue.");
        StdDraw.pause(2000);

        while (!gameOver) {
            ter.renderFrame(world);

            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                move(c);
                updateScore();
            }

            if (score == totalTrees) {
                gameOver = true;
            }
        }
        drawGameOver();
        StdDraw.pause(2000);
    }

    /** Method that draws the game over screen when the user finishes the mini game. */
    private void drawGameOver() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(width / 2.0, height / 2.0, "Nicely done! Going back to the main world. ");
        StdDraw.show();
    }

    /** Method that updates the score. */
    private void updateScore() {
        score = player.getScore();
    }

    /** Method that draws the instructions to the screen. */
    private void drawInstructions(String s) {
        StdDraw.clear(Color.BLACK);
        StdDraw.text(width / 2.0, height / 2.0, s);
        StdDraw.show();
    }
}
