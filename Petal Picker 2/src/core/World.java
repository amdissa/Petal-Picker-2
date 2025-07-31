package core;



import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;


import java.util.Random;



public class World {

    public static final int DEFAULT_WIDTH = 80;
    public static final int DEFAULT_HEIGHT = 30;

    //integer representations of TETile objects
    private final int NOTHING_INDEX = 0;
    private final int WALL_INDEX = 1;
    private final int FLOOR_INDEX = 2;
    private final int FLOWER_INDEX = 3;
    private final int GRASS_INDEX = 4;

    //world geometry limits
    private final int MIN_ROOMS = 20;
    private final int MAX_ROOMS = 30;
    private final int MIN_HALLS = 40;
    private final int MAX_HALLS = 50;
    private final int MAX_ROOM_SIZE = 9;
    private final int MIN_ROOM_SIZE = 5;
    private final int MIN_HALL_LENGTH = 4;
    private final int MAX_HALL_LENGTH = 17;

    private static final int NUMFLOWERS = 10;
    private Random random;
    private TETile[][] tetWorld;
    private int[][] world;
    private int numRooms;
    private int numHalls;
    private UnionFind uni;
    private int[] spawnPoint;

    TERenderer rend;



    public World(long seed) {
        world = new int[DEFAULT_WIDTH][DEFAULT_HEIGHT];

        random = new Random(seed);
        numRooms = random.nextInt((MAX_ROOMS - MIN_ROOMS)) + MIN_ROOMS;
        numHalls = random.nextInt((MAX_HALLS - MIN_HALLS)) + MIN_HALLS;
        generateRooms();
        generateHalls();
        trimmer(); //deletes dead-ends
        borderer(); //deletes stuff touching the boundary whether floor or a wall
        sparser(); //deletes inaccessible rooms
        waller(); // makes walls
        setSpawnPoint();
        tetWorld = converter(); // integer to TETile converter
    }


    public TETile[][] getWorld() {
        return tetWorld;
    }
    public int[] getSpawnPoint() {
        return spawnPoint;
    }

    private void setSpawnPoint() {
        int x = 0;
        int y = 0;
        for (int i = 0; i < 10; i++) {
            while (world[x][y] != FLOOR_INDEX) {
                x = random.nextInt(DEFAULT_WIDTH);
                y = random.nextInt(DEFAULT_HEIGHT);
            }
            world[x][y] = FLOWER_INDEX;
        }
        while (world[x][y] != FLOOR_INDEX) {
            x = random.nextInt(DEFAULT_WIDTH);
            y = random.nextInt(DEFAULT_HEIGHT);
        }
        spawnPoint = new int[] {x, y};
    }
    private void sparser() { //deletes everything but the largest room
        uni = new UnionFind(DEFAULT_HEIGHT * DEFAULT_WIDTH);
        for (int i = 0; i < DEFAULT_HEIGHT; i++) {
            for (int j = 0; j < DEFAULT_WIDTH; j++) {
                if (world[j][i] == FLOOR_INDEX || world[j][i] == GRASS_INDEX) {
                    linkNeighbors(j, i);
                }
            }
        }
        int largest = uni.largestSetSize();
        for (int i = 0; i < DEFAULT_HEIGHT; i++) {
            for (int j = 0; j < DEFAULT_WIDTH; j++) {
                if (world[j][i] == FLOOR_INDEX || world[j][i] == GRASS_INDEX) {
                    if (uni.sizeOf(indexer(j, i)) != largest) {
                        world[j][i] = NOTHING_INDEX;
                    }
                }
            }
        }
    }

    private void linkNeighbors(int x, int y) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int a = x + j;
                int b = y + i;

                if (a < 0 || b < 0 || a >= DEFAULT_WIDTH || b >= DEFAULT_HEIGHT) {
                    continue;
                }
                if (a != x && b != y) {
                    continue;
                }
                if (a == x && b == y) {
                    continue;
                }
                if (world[a][b] == FLOOR_INDEX) {
                    uni.union(indexer(x, y), indexer(a, b));
                }
            }
        }
    }

    //removes and floors touching the border
    private void borderer() {
        for (int i = 0; i < DEFAULT_WIDTH; i++) {
            world[i][0] = 0;
            world[i][DEFAULT_HEIGHT - 2] = 0;
            world[i][DEFAULT_HEIGHT - 1] = 0;
        }
        for (int j = 0; j < DEFAULT_HEIGHT; j++) {
            world[0][j] = 0;
            world[DEFAULT_WIDTH - 1][j] = 0;
        }
    }

    //converts integer matrix to Tetile
    private TETile[][] converter() {
        TETile[][] returnVar = new TETile[DEFAULT_WIDTH][DEFAULT_HEIGHT];
        for (int i = 0; i < DEFAULT_HEIGHT; i++) {
            for (int j = 0; j < DEFAULT_WIDTH; j++) {
                int index = world[j][i];
                switch (index) {
                    case NOTHING_INDEX:
                        returnVar[j][i] = Tileset.NOTHING;
                        break;
                    case WALL_INDEX:
                        returnVar[j][i] = Tileset.WALL;
                        break;
                    case FLOOR_INDEX:
                        returnVar[j][i] = Tileset.FLOOR;
                        break;
                    case FLOWER_INDEX:
                        returnVar[j][i] = Tileset.FLOWER;
                        break;
                    case GRASS_INDEX:
                        returnVar[j][i] = Tileset.GRASS;
                        break;
                    default:
                        break;
                }
            }
        }
        returnVar[spawnPoint[0]][spawnPoint[1]] = Tileset.LOCKED_DOOR;
        return returnVar;
    }


    private void generateRooms() {
        for (int i = 0; i < numRooms; i++) {
            int[] o = spawnRoom();
            growRoom(o[0], o[1]);

        }
    }

    private void generateHalls() {
        UnionFind rows = new UnionFind(DEFAULT_HEIGHT);
        UnionFind cols = new UnionFind(DEFAULT_WIDTH);
        for (int i = 0; i < numHalls; i++) {
            int[] p = spawnRoom();
            while (numNeighbors(p[0], p[1])[0] > 0 && rows.sizeOf(p[1]) > 1 || cols.sizeOf(p[0]) > 1) {
                p = spawnRoom();
            }
            rows.union(p[1] + 1, p[1] - 1);
            cols.union(p[0] + 1, p[0] - 1);
            growHall(p[0], p[1]);
        }
    }


    //spawns hall in empty location
    private int[] spawnHall() {
        int[] p = spawnRoom();
        while (numNeighbors(p[0], p[1])[0] > 0) {
            p = spawnRoom();
        }
        return p;
    }

    private void growHall(int x, int y) {
        int d1 = random.nextInt(4);
        int d2 = 0;
        while (d2 == d1) {
            d2 = random.nextInt(4);
        }
        int l1 = random.nextInt(MAX_HALL_LENGTH - MIN_HALL_LENGTH) + MIN_HALL_LENGTH;
        int l2 = random.nextInt(MAX_HALL_LENGTH - MIN_HALL_LENGTH) + MIN_HALL_LENGTH;
        growHallHelper(x, y, d1, l1);
        growHallHelper(x, y, d2, l2);
    }

    private void growHallHelper(int x, int y, int d, int l) {
        if (x < 0 || x >= DEFAULT_WIDTH || y < 0 || y >= DEFAULT_HEIGHT || l == 0) {
            return;
        }
        int a = 0;
        int b = 0;
        switch (d) {
            case 0:
                b = 1;
                break;
            case 1:
                b = -1;
                break;
            case 2:
                a = 1;
                break;
            case 3:
                a = -1;
                break;
            default:
                System.err.println("Can't go in this direction: " + d);
                return;
        }
        world[x][y] = FLOOR_INDEX;
        growHallHelper(x + a, y + b, d, l - 1);
    }




    //places a point for a room
    private int[] spawnRoom() {
        int xCoord = random.nextInt(DEFAULT_WIDTH - MAX_ROOM_SIZE / 2) + MAX_ROOM_SIZE / 4;
        int yCoord = random.nextInt(DEFAULT_HEIGHT - MAX_ROOM_SIZE / 2) + MAX_ROOM_SIZE / 4;
        return new int[] {xCoord, yCoord};
    }

    //grows room from point
    private void growRoom(int x, int y) {
        int height = random.nextInt(MAX_ROOM_SIZE - MIN_ROOM_SIZE) + MIN_ROOM_SIZE;
        int width = random.nextInt(MAX_ROOM_SIZE - MIN_ROOM_SIZE) + MIN_ROOM_SIZE;

        for (int i = -height / 2; i < height - height / 2; i++) {
            for (int j = -width / 2; j < width - width / 2; j++) {
                int a = x + j;
                int b = y + i;
                if (a < 0 || a >= DEFAULT_WIDTH || b < 0 || b >= DEFAULT_HEIGHT) {
                    continue;
                }
                int n = random.nextInt(10);
                if (n < 2) {
                    world[a][b] = GRASS_INDEX;
                } else {
                    world[a][b] = FLOOR_INDEX;
                }
            }
        }

    }


    // converts coordinates into index value
    private int indexer(int x, int y) {
        return y * DEFAULT_WIDTH + x;
    }

    //converts index into coordinates
    private int[] coordinator(int i) {
        int b = Math.floorMod(i, DEFAULT_WIDTH);
        int a = (i - b) / DEFAULT_WIDTH;
        return new int[] {a, b};
    }

    //determines number of adjacent (not diagonal)
    // floor tiles and gives the coordinates of the last observed neighbor tile [num, x, y]
    private int[] numNeighbors(int x, int y) {
        int num = 0;
        int c = -1;
        int d = -1;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int a = x + j;
                int b = y + i;

                if (a < 0 || b < 0 || a >= DEFAULT_WIDTH || b >= DEFAULT_HEIGHT) {
                    continue;
                }
                if (a != x && b != y) {
                    continue;
                }
                if (a == x && b == y) {
                    continue;
                }
                if (world[a][b] == FLOOR_INDEX || world[a][b] == GRASS_INDEX) {
                    num++;
                    c = a;
                    d = b;
                }
            }
        }
        return new int[] {num, c, d};
    }

    private void trimmer() {
        for (int i = 0; i < DEFAULT_HEIGHT; i++) {
            for (int j = 0; j < DEFAULT_WIDTH; j++) {
                trim(j, i);
            }
        }
    }

    //gets rid of dead end hallways
    private void trim(int x, int y) {
        int[] a = numNeighbors(x, y);
        if (a[0] <= 1 && world[x][y] == FLOOR_INDEX) {
            trimHelper(x, y);
        }
    }

    private void trimHelper(int x, int y) {
        if (x == -1 && y == -1) {
            return;
        }
        int[] a = numNeighbors(x, y);
        if (a[0] > 1) {
            return;
        }
        world[x][y] = NOTHING_INDEX;
        trimHelper(a[1], a[2]);
    }

    private void waller() {
        for (int i = 0; i < DEFAULT_HEIGHT; i++) {
            for (int j = 0; j < DEFAULT_WIDTH; j++) {
                wall(j, i);
            }
        }
    }

    //builds walls on nothing tiles that are next to floors
    private void wall(int x, int y) {
        if (world[x][y] == NOTHING_INDEX) {
            int[] a = numNeighbors(x, y);
            if (a[0] >= 1) {
                world[x][y] = WALL_INDEX;
            }
        }
    }


}
