package byow.Core.Map;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

import java.util.*;

public class MapGenerator {
    private final int windowWidth;
    private final int windowHeight;
    private final Random RANDOM;
    private static final int MAX_RETRIES = 1000;
    private static final int MIN_ROOM_WIDTH = 4;
    private static final int MIN_ROOM_HEIGHT = 4;
    private final List<Room> existingRooms;
    private final List<Hallway> usedHallways;
    private int totalRooms;

    private final WeightedQuickUnionUF wqu;

    private final TETile[][] world;
    private final Room[][] roomWorld;

    private final PriorityQueue<Hallway> allHallways;

    private Player player;

    /** Two parameter Constructor that takes in a width and height of the window screen. */
    public MapGenerator(int width, int height, long seed) {
        windowWidth = width;
        windowHeight = height;
        RANDOM = new Random(seed);
        world = new TETile[windowWidth][windowHeight];
        roomWorld = new Room[windowWidth][windowHeight];
        existingRooms = new ArrayList<>();
        allHallways = new PriorityQueue<>();
        usedHallways = new ArrayList<>();
        totalRooms = 0;

        initializeWorld();
        generateRooms();


        // Has to be instantiated after calling generateRooms(), since generateRooms() updates the totalRooms variable
        wqu = new WeightedQuickUnionUF(totalRooms);

        // Add all hallways to PQ
        insertAllHallwaysToPQ();
        connectHallways();

        // Draw in the walls again
        drawRoomWalls();
        fillHallwayWalls();

        // Create avatar
        initializeAvatar();

        // Create the trees in every room.
        createTrees();

        // Create unlocked doors.
        createUnlockedDoors();
    }

    /** Method that creates unlocked doors to the world. */
    private void createUnlockedDoors() {
        int numDoors = 0;

        while (numDoors < 2) {
            int index = RANDOM.nextInt(0, existingRooms.size());
            Room room = existingRooms.get(index);
            if (addUnlockedDoorToRoom(room)) {
                numDoors += 1;
            }
        }
    }

    /** Method that determines whether the tile at the location x, y is equal to the tile that is passed in. */
    private boolean equalTile(TETile tile, int x, int y) {
        return world[x][y].equals(tile);
    }

    /** Method that adds an unlocked door to the Room object that is passed in. */
    private boolean addUnlockedDoorToRoom(Room r) {
        Point upLeft = r.getUpperLeftPoint();
        Point botRight = r.getBottomRightPoint();

        int startX = upLeft.getX() + 1;
        int endX = botRight.getX();

        int startY = botRight.getY() + 1;
        int endY = upLeft.getY();

        int x = RANDOM.nextInt(startX, endX);
        int y  = RANDOM.nextInt(startY, endY);

        Point p = new Point(x, y);
        if (!p.equals(player.getPoint()) && !equalTile(Tileset.TREE, x, y)) {
            world[x][y] = Tileset.UNLOCKED_DOOR;
            return true;
        }
        return false;
    }

    /** Method that creates trees for each Room object that is located in the world. */
    private void createTrees() {
        for (Room room : existingRooms) {
            if (!room.getOpeningPoint().equals(player.getPoint())) {
                world[room.getOpeningPoint().getX()][room.getOpeningPoint().getY()] = Tileset.TREE;
            }
        }
    }

    /** Method that calls each Hallway object's drawHallwayWalls method.
     * Draws the hallway's surrounding walls to the screen.
     * */
    private void fillHallwayWalls() {
        for (Hallway hallway : usedHallways) {
            hallway.drawHallwayWalls(world);
        }
    }

    /** Method that calls each Room object's drawWalls method.
     * Draws the room's surrounding walls to the screen.
     * */
    private void drawRoomWalls() {
        for (Room room : existingRooms) {
            room.drawWalls(world);
        }
    }

    /** Method that connects all rooms to each other using hallways.
     * Draws the flooring of the hallways to the screen.
     * */
    private void connectHallways() {
        int numHallways = 0;
        while (numHallways < totalRooms - 1) {
            Hallway hallway = allHallways.remove();
            int roomOne = hallway.getRoomOne().getID();
            int roomTwo = hallway.getRoomTwo().getID();
            if (!wqu.connected(roomOne, roomTwo)) {
                wqu.union(roomOne, roomTwo);
                hallway.drawHallwayFloor(world);
                usedHallways.add(hallway);
                numHallways += 1;
            }
        }
    }

    /** Method that creates all Hallway objects and adds them into the Minimum Heap Priority Queue. */
    private void insertAllHallwaysToPQ() {
        Set<Room> roomsVisited = new HashSet<>();

        for (Room currentRoom : existingRooms) {
            roomsVisited.add(currentRoom);
            for (Room nextRoom : existingRooms) {
                if (!roomsVisited.contains(nextRoom)) {
                    allHallways.add(new Hallway(currentRoom, nextRoom, RANDOM.nextInt(), roomWorld));
                }
            }
        }
    }

    /** Returns the number of hallways in the world. Used for testing purposes. */
    public int getNumHallways() {
        return allHallways.size();
    }

    /** Generates the rooms for the world. */
    private void generateRooms() {
        int retries = 0;
        while (retries < MAX_RETRIES) {
            int xCoordinate = RANDOM.nextInt(windowWidth);
            int yCoordinate = RANDOM.nextInt(windowHeight);
            int width = RANDOM.nextInt(MIN_ROOM_WIDTH, windowWidth / 2);
            int height = RANDOM.nextInt(MIN_ROOM_HEIGHT, windowHeight / 2);

            Room potentialRoom = new Room(new Point(xCoordinate, yCoordinate), existingRooms.size(), width, height);
            // Find whether room contains a valid Point and Room Dimensions
            if (isValidRoom(potentialRoom)) {
                existingRooms.add(potentialRoom);
                totalRooms += 1;
                potentialRoom.fillRoom(world, RANDOM);
                addToRoomWorld(potentialRoom);
            } else {
                retries += 1;
            }
        }
    }

    /** Adds the Room object to the 2D array of Room objects.
     * @param room - the Room object that needs to be added to the array
     * */
    private void addToRoomWorld(Room room) {
        // Move from left to right, bottom to top
        Point startPoint = room.getBottomLeftPoint();
        Point endPoint = room.getUpperLeftPoint();

        int startY = startPoint.getY();
        int endY = endPoint.getY();

        for (int currentCol = startY; currentCol <= endY; currentCol += 1) {
            addRow(currentCol, room);
        }
    }

    /** Method that adds a Room object to the current row in the 2D array of Room objects.
     * @param y - the current column index of the roomWorld array
     * @param room - the Room object that is added to the roomWorld array
     * */
    private void addRow(int y, Room room) {
        Point startPoint = room.getUpperLeftPoint();
        Point endPoint = room.getUpperRightPoint();

        int startX = startPoint.getX();
        int endX = endPoint.getX();

        for (int currentX = startX; currentX <= endX; currentX += 1) {
            roomWorld[currentX][y] = room;
        }
    }

    /** Determines whether the Room object is a valid Room object that can be drawn to the world. */
    private boolean isValidRoom(Room room) {
        int roomStartX = room.getUpperLeftPoint().getX();
        int roomEndX = room.getUpperRightPoint().getX();
        int roomStartY = room.getBottomLeftPoint().getY();
        int roomEndY = room.getUpperLeftPoint().getY();

        if (roomStartX < 1 || roomEndX >= windowWidth - 1) {
            return false;
        }

        if (roomStartY < 1 || roomEndY >= windowHeight - 1) {
            return false;
        }

        return !isOverlapping(room);
    }

    /** Returns true if the Room object overlaps with another existing Room object.
     * Returns false if Room object does not overlap with an existing Room object. */
    private boolean isOverlapping(Room room) {
        int startX = room.getUpperLeftPoint().getX();
        int endX = room.getUpperRightPoint().getX();
        int startY = room.getBottomLeftPoint().getY();
        int endY = room.getUpperLeftPoint().getY();

        for (int currentX = startX; currentX <= endX; currentX += 1) {
            for (int currentY = startY; currentY <= endY; currentY += 1) {
                if (!world[currentX][currentY].equals(Tileset.NOTHING)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Helper method that initializes the world to blank tiles. */
    private void initializeWorld() {
        for (int x = 0; x < windowWidth; x += 1) {
            for (int y = 0; y < windowHeight; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    /** Method that returns the world that is generated. */
    public TETile[][] getWorld() {
        return world;
    }

    /** Method that returns the total number of rooms. Used for testing purposes. */
    public int getTotalRooms() {
        return totalRooms;
    }

    private void initializeAvatar() {
        int i = RANDOM.nextInt(usedHallways.size());
        Point p = usedHallways.get(i).getRoomOne().getOpeningPoint();
        player = new Player(p);
        world[p.getX()][p.getY()] = Tileset.AVATAR;
    }

    public Player getPlayer() {
        return player;
    }
}

