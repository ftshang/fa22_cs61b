package byow.Core.Game;

import byow.Core.InputSource.InputSource;
import byow.Core.InputSource.KeyboardInputDevice;
import byow.Core.InputSource.StringInputDevice;
import byow.Core.Map.MapGenerator;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {
    private final int width;
    private final int height;
    private static final int Y_OFFSET = 3;
    private static final int X_OFFSET = 0;
    private TETile[][] world;
    private MapGenerator map;
    private Random RANDOM;

    private boolean gameOver;
    private boolean saved;
    private boolean newWorld;
    private boolean tryAgain;
    private boolean load;
    private boolean stringGame;

    private final InputSource inputSource;
    private TERenderer ter;
    private List<Character> gameFunctions;

    private long seed;
    private StringBuilder seedString;

    private int score;
    private int goal;

    private final String inputFileName = "saved.txt";

    private String inputString;

    /** Constructor used for Keyboard inputs. */
    public Game(int w, int h, TERenderer ter) {
        width = w;
        height = h;
        this.ter = ter;
        inputSource = new KeyboardInputDevice(width, height);
        gameFunctions = new ArrayList<>();
        seedString = new StringBuilder();
        score = 0;
        tryAgain = true;
        stringGame = false;

        try {
            In in = new In("saved.txt");
            saved = true;
        } catch (Exception IllegalArgumentException) {
            saved = false;
        }

        mainMenu();
    }

    /** Constructor used for String inputs. */
    public Game(int w, int h, String s) {
        width = w;
        height = h;
        inputSource = new StringInputDevice(s);
        gameFunctions = new ArrayList<>();
        score = 0;
        inputString = s;
        stringGame = true;

        if (Parser.isLoadString(inputString)) {
            saved = true;
            loadPreviousWorld();
        } else {
            saved = false;
            // it is a new world
            initializeNewStringWorld();
            long seed = Parser.parseInputForSeed(inputString);
            setMap(new MapGenerator(width - X_OFFSET, height - Y_OFFSET, seed));
        }
        startStringGame();
    }

    /** Method that initializes the world if the game is being played as a string. */
    private void initializeNewStringWorld() {
        int lastIndex = inputString.indexOf('S');
        lastIndex += 1;

        for (int i = 0; i < lastIndex; i += 1) {
            gameFunctions.add(inputString.charAt(i));
        }
    }

    /** Method that starts the game if being played as a string. */
    private void startStringGame() {
        // finish the movements
        moveString(Parser.parseInputForMovements(inputString));

        // Check save
        int lastIndex = inputString.length() - 1;
        int colonIndex = inputString.length() - 2;

        char quitChar = inputString.charAt(lastIndex);
        char colonChar = inputString.charAt(colonIndex);

        if (isColon(colonChar) && isQuit(quitChar)) {
            gameFunctions.add(colonChar);
            gameFunctions.add(quitChar);
            saveFile();
        }
    }

    /** Method that restarts the game if user wants to replay the game. */
    private void restartGame() {
        seedString = new StringBuilder();
        score = 0;

        while (inputSource.possibleNextInput()) {
            drawRestart();
            char c = Character.toUpperCase(inputSource.getNextKey());

            if (c >= 48 && c <= 57) {
                gameFunctions.add(c);
                seedString.append(c);
            }

            if (c == 'S') {
                seed = Long.parseLong(seedString.toString());
                RANDOM = new Random(seed);
                gameFunctions.add(c);
                break;
            }
        }
        initGameWorld();
        gameOver = false;
    }

    /** Method that draws the restart screen. */
    private void drawRestart() {
        StdDraw.clear(Color.BLACK);
        StdDraw.text(width / 2.0, height / 2.0, "Enter Seed: " + seedString);
        StdDraw.show();
    }

    /** On start, sets the canvas to default settings. Enables double buffering so graphics are drawn simultaneously. */
    private void initializeScreen() {
        StdDraw.setCanvasSize(width * 16, height * 16);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }

    /** Resets the frame for the canvas. Needed to draw a new frame for the StdDraw library. */
    private void resetMenuFrame() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
    }

    /** Method that draws the menu to the screen. Will draw the seed as well. */
    private void drawMenu() {
        resetMenuFrame();
        double heightAlignment = 6.0;
        double x = width / 2.0;
        double y = height - 3.0 * heightAlignment;
        double space = 2.0;

        StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
        StdDraw.text(width / 2.0, height - 6.0, "CS61B: The Game.");

        StdDraw.setFont(new Font("Monaco", Font.BOLD, 15));
        StdDraw.text(x, y, "New Game (N)");
        StdDraw.text(x, y - space, "Load Game (L)");
        StdDraw.text(x, y - 2 * space, "Quit (Q)");

        if (newWorld) {
            StdDraw.text(x, y - 3 * space, "Enter Seed: " + seedString);
        }

        StdDraw.show();
    }

    /** Method that serves as the Main Menu for the game. */
    public void mainMenu() {
        initializeScreen();

        // Step 1. display the menu
        while (inputSource.possibleNextInput()) {
            drawMenu();
            char c = Character.toUpperCase(inputSource.getNextKey());
            if (c == 'N') {
                newWorld = true;
                gameFunctions.add(c);
                break;
            }
            if (c == 'L') {
                if (!saved) {
                    System.exit(0);
                } else {
                    load = true;
                    loadPreviousWorld();
                    break;
                }
            }
            if (c == 'Q') {
                System.exit(0);
            }
        }
        // Step 2: Get the seed if user pressed generate new world
        if (newWorld) {
            menuGetSeed();
            initGameWorld();
        }

        ter.initialize(width, height, X_OFFSET, Y_OFFSET);
        startGame();
    }

    /** Used for new games. Used to set up the map, world, and score. */
    private void initGameWorld() {
        setMap(new MapGenerator(width - X_OFFSET, height - Y_OFFSET, this.seed));
    }

    /** Method that retrieves the seed from the user. Displays it to the menu screen as well. */
    private void menuGetSeed() {
        while (inputSource.possibleNextInput()) {
            drawMenu();
            char c = Character.toUpperCase(inputSource.getNextKey());
            if (c >= 48 && c <= 57) {
                gameFunctions.add(c);
                seedString.append(c);
            }
            if (c == 'S') {
                seed = Long.parseLong(seedString.toString());
                RANDOM = new Random(seed);
                gameFunctions.add(c);
                break;
            }
        }
    }

    /** Method that displays the current game moves. */
    public void printCommands() {
        System.out.println(gameFunctions.toString());
    }

    /** Returns the Game's seed. */
    public long getSeed() {
        return seed;
    }

    /** Returns the Game's TETile[][] world array. */
    public TETile[][] getGameWorld() {
        return this.world;
    }

    /** Sets the Game's map, world, and goal variables. */
    private void setMap(MapGenerator map) {
        this.map = map;
        this.world = map.getWorld();
        this.goal = this.map.getTotalRooms() - 1;
    }


    /** Draws the generated world and HUD to the screen.
     * @param s - the tile that will be displayed */
    private void drawGameFrame(String s) {
        ter.renderFrame(world);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(5.0, Y_OFFSET / 2.0, s);
        StdDraw.text(width - 3.0, Y_OFFSET / 2.0, "score: " + this.score);
        StdDraw.show();
    }

    /** Gets the next inputSource's key. */
    public char solicitOneCharsInput() {
        return inputSource.getNextKey();
    }

    private void createBonusWorld() {
        if (!load && !stringGame) {
            MiniGame miniGame = new MiniGame(width, height, RANDOM.nextLong(), ter);
        }
    }

    /** Serves as the basic move functionality. */
    private void move(char c) {
        if (c == 'W') {
            gameFunctions.add(c);
            if (map.getPlayer().moveUp(world)) {
                createBonusWorld();
            }
        } else if (c == 'S') {
            gameFunctions.add(c);
            if (map.getPlayer().moveDown(world)) {
                createBonusWorld();
            }
        } else if (c == 'A') {
            gameFunctions.add(c);
            if (map.getPlayer().moveLeft(world)) {
                createBonusWorld();
            }
        } else if (c == 'D') {
            gameFunctions.add(c);
            if (map.getPlayer().moveRight(world)) {
                createBonusWorld();
            }
        }
    }

    /** Main loop for the game. */
    public void startGame() {
        String hud = "";

        while (tryAgain) {
            while(!gameOver) {
                drawGameFrame(hud);
                checkAndUpdateGame();

                if (inputSource.possibleNextInput()) {
                    char c = solicitOneCharsInput();
                    int x = (int) StdDraw.mouseX();
                    int y = (int) StdDraw.mouseY();

                    // if the mouse is on the game board
                    if (isHover(c) && isValidIndices(x, y)) {
                        hud = getTileString(x, y);
                        // if mouse is off the game board
                    } else if (isOffScreen(c)) {
                        hud = "";
                        // if the character is a colon and the last key in the gameFunctions list is not a colon
                    } else if (isColon(c) && !isLastColon()) {
                        gameFunctions.add(c);
                        // if the character is a colon and the last key in the gameFunctions list is a colon
                    } else if (isColon(c) && isLastColon()) {
                        removeColon();
                        gameFunctions.add(c);
                        // if the character is a Q and the last key in the gameFunctions list is a colon
                    } else if (isQuit(c) && isLastColon()) {
                        saveQuit(c);
                        // if the character is something else
                    } else {
                        removeColon();
                        move(c);
                        updateScore();
                    }
                }
            }
            displayGameOver();
        }
    }

    /** Removes the last colon character from the gameFunctions list. */
    private void removeColon() {
        if (isLastColon()) {
            gameFunctions.remove(gameFunctions.size() - 1);
        }
    }

    /** Updates the current score of the game. */
    private void updateScore() {
        score = map.getPlayer().getScore();
    }

    /** Returns true if c is the character value of 'Q', false otherwise. */
    private boolean isQuit(char c) {
        return c == 'Q';
    }

    /** Returns true if c is the character value of ':', false otherwise. */
    private boolean isColon(char c) {
        return c == ':';
    }

    /** Returns true if c is the character value of '-', false otherwise. */
    private boolean isOffScreen(char c) {
        return c == '-';
    }

    /** Returns true if c is the character value of '+', false otherwise. */
    private boolean isHover(char c) {
        return c == '+';
    }

    /** Method that saves and quits the program. */
    private void saveQuit(char c) {
        gameFunctions.add(c);
        saveFile();
        System.exit(0);
    }

    /** Game is over if score is equal to goal. */
    private void checkAndUpdateGame() {
        if (score == goal) {
            gameOver = true;
        }
    }

    /** Displays the Game Over screen and waits for user to quit. */
    private void displayGameOver() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
//        StdDraw.setFont(new Font("Monaco", Font.BOLD, 15));
        StdDraw.text(width / 2.0, height / 2.0, "Congratulations! You won!");
        StdDraw.text(width / 2.0, height / 2.0 - 3.0, "Press N for a new game.");
        StdDraw.text(width / 2.0, height / 2.0 - 6.0, "Press Q to quit.");
        StdDraw.show();

        while (inputSource.possibleNextInput()) {
            char c = solicitOneCharsInput();
            if (c == 'Q') {
                System.exit(0);
            }
            if (c == 'N') {
                gameFunctions = new ArrayList<>();
                gameFunctions.add(c);
                restartGame();
                break;
            }
        }
    }

    /** Method that returns the TETile's description. */
    private String getTileString(int x, int y) {
        return world[x - X_OFFSET][y - Y_OFFSET].description();
    }

    /** Used to determine whether x and y are current indices of the game screen. */
    private boolean isValidIndices(int x, int y) {
        return x >= 0 && x < width && y >= Y_OFFSET && y < height;
    }

    /** Returns true if the last key that registered was a colon. */
    private boolean isLastColon() {
        int lastIndex = gameFunctions.size() - 1;
        char c = gameFunctions.get(lastIndex);
        return c == ':';
    }

    /** Saves the current world to a text file, containing the inputs of the game. */
    private void saveFile() {
        // Writes to a file named "saved.txt"
        Out out = new Out(inputFileName);
        StringBuilder string = new StringBuilder();
        for (char c : gameFunctions) {
            string.append(c);
        }
        out.println(string.toString());
    }

    /** Loads the previous world, if there is a text file named "saved.txt". */
    private void loadPreviousWorld() {
        // read in the file
        In in = new In(inputFileName);

        String inputLine = in.readLine();
        long seed = Parser.parseInputForSeed(inputLine);
        RANDOM = new Random(seed);
        String movement = Parser.parseInputForMovements(inputLine);

        addSeedToList(inputLine);

        // reload to a blank state
        reloadMapSettings(seed);
        // reload the move settings
        moveString(movement);
        load = false;
    }

    /** Method that reloads the map settings based on the seed of the saved world. */
    private void reloadMapSettings(long seed) {
        this.seed = seed;
        setMap(new MapGenerator(width - X_OFFSET, height - Y_OFFSET, this.seed));
    }

    /** Adds seed to the gameFunctions list, in case the game has another save and quit command. */
    private void addSeedToList(String input) {
        int index = input.indexOf('S');
        index += 1;

        for (int i = 0; i < index; i += 1) {
            char c = input.charAt(i);
            gameFunctions.add(c);
        }
    }

    /** Updates the game and world based on the last saved world's movements. */
    private void moveString(String movement) {
        for (int i = 0; i < movement.length(); i += 1) {
            char c = movement.charAt(i);
            move(c);
            updateScore();
        }
    }
}
