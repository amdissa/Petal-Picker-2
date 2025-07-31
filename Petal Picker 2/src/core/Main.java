package core;


import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import utils.FileUtils;


import java.awt.*;


public class Main {


    private final double SCORE_X = 3;
    private final double SCORE_Y = 29.5;

    private Movement movement;
    private TERenderer TER = new TERenderer();
    private TETile[][] board;

    private char lastKeyTyped;
    private long currentSeed;
    private long startTime;
    private int numFlowers;
    private final long DURATION = 60000;

    Font text = new Font("Time New Roman", Font.BOLD, 40);
    Font header = new Font("Times New Roman", Font.BOLD, 60);
    Font scoreText = new Font("Time New Roman", Font.BOLD, 15);
    Font death = new Font("Algerian", Font.BOLD, 60);

    private World world; //create a getter method

    public TETile[][] getBoard() {
        return board;
    }

    public void setBoard(TETile[][] w) {
        board = w;
    }
    private int score;

    private int[] avatarPosition;
    private int[] spawn; //spawn location coordinates

    private TETile standingOn; //tile the avatar is currently standing on
    private boolean isGameOVer;
    private final String SAVE_FILE = "save.txt";
    private int health;
    private double[] mousePosition;
    private String healthString;
    private final int MAX_HEALTH = 5;


    private Music music = new Music();

    public void setAttributes(TETile t, int[] s, Movement m) {
        standingOn = t;
        spawn = s;
        avatarPosition = s;
        movement = m;
    }

    public void setMovement(Movement m) {
        movement = m;
    }


    private String mainMenu() {
        int width = 30;
        int height = 15;
        StdDraw.clear();
        StdDraw.clear(Color.black);

        StdDraw.setFont(header);
        StdDraw.setPenColor(Color.PINK);
        StdDraw.text(width + 12, height + 8, "Petal Picker 2");
        StdDraw.setFont(text);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(width + 12, height + 4, "New Game (N)");
        StdDraw.text(width + 12, height + 1.5, "Load Game (L)");
        StdDraw.text(width + 12, height - 1, "Quit (Q)");
        StdDraw.show();
        StringBuilder seed = new StringBuilder();
        boolean s = true;
        while (s) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                switch (key) {
                    default:
                    case 'N', 'n':
                        s = false;
                        StdDraw.clear(Color.BLACK);
                        StdDraw.setFont(header);
                        StdDraw.setPenColor(Color.WHITE);
                        StdDraw.text(width + 12, height + 8, "61B: The Game");
                        StdDraw.setFont(text);
                        StdDraw.setPenColor(Color.WHITE);
                        StdDraw.text(width + 12, height + 4, "Enter Seed:");
                        StdDraw.show();
                        break;
                    case 'L', 'l':
                        return "L";
                    case 'Q', 'q':
                        saveBoard(); //edited
                        System.exit(0);
                }
            }

        }

        s = true;

        while (s) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                switch (key) {
                    default:
                        if (Character.isDigit(key)) {
                            StdDraw.setPenColor(Color.BLACK);
                            StdDraw.text(width + 12, height, seed.toString());
                            seed.append(key);
                            StdDraw.setPenColor(Color.WHITE);
                            StdDraw.text(width + 12, height, seed.toString());
                            StdDraw.show();
                        }
                        break;
                    case 'S', 's':
                        if (!seed.isEmpty()) {
                            s = false;
                        }
                        break;

                }
            }
        }
        TER.resetFont();
        return seed.toString();
    }


    void start() {
        TER.initialize(World.DEFAULT_WIDTH, World.DEFAULT_HEIGHT);
        score = 0;
        numFlowers = 0;
        mousePosition = new double[]{0, 0};
        String seed = mainMenu();
        music.loadMusic("src/slow8bit.wav");
        music.loadSoundEffect("src/lossSound.wav");
        music.play();
        if (seed.equals("L")) {
            board = loadBoard();
        } else {
            world = new World(Long.parseLong(seed));
            board = world.getWorld();
            currentSeed = Long.parseLong(seed);
            standingOn = Tileset.LOCKED_DOOR;
            spawn = world.getSpawnPoint();
            avatarPosition = spawn;
            health = MAX_HEALTH;
            updateHealth();
        }

        movement = new Movement(board);
        TER.renderFrame(board);

        startTime = System.currentTimeMillis();

    }

    private void runGame() {
        while (!isGameOVer) {
            updateBoard();
            renderBoard();
            if (System.currentTimeMillis() - startTime > DURATION) {
                isGameOVer = true;
                gameOver();
                music.stop();
                music.close();
            }

        }
    }

    private void gameOver() {
        int width = 40;
        int height = 15;
        StdDraw.clear();
        StdDraw.clear(Color.black);
        StdDraw.setFont(death);
        StdDraw.setPenColor(Color.RED);
        StdDraw.text(width, height, "Mortis");
        StdDraw.show();
        StdDraw.pause(3000); //show the game over screen for 4 secs
        isGameOVer = false;
        music.stop();
        music.close();
        start();
    }

    private void gameWon() {
        int width = 40;
        int height = 15;
        StdDraw.clear();
        StdDraw.clear(Color.BLACK);
        StdDraw.setFont(header);
        StdDraw.setPenColor(Color.ORANGE);
        StdDraw.text(width, height, "Congratulations! You won!");
        StdDraw.show();
        StdDraw.pause(5000); //show the game over screen for 5 secs
        isGameOVer = false;
        music.stop();
        music.close();
        start();
    }


    private void updateBoard() {
        if (StdDraw.hasNextKeyTyped()) {
            char key = StdDraw.nextKeyTyped();
            move(key);
            lastKeyTyped = key;

        }
        mousePosition[0] = StdDraw.mouseX();
        mousePosition[1] = StdDraw.mouseY();
    }


    public void move(char c) {
        switch (c) {
            default:
                break;
            case 'a', 'A':
                avatarPosition = movement.move(avatarPosition[0], avatarPosition[1], -1, 0);
                nextGeneration();
                updateHealth();
                break;
            case 'd', 'D':
                avatarPosition = movement.move(avatarPosition[0], avatarPosition[1], 1, 0);
                nextGeneration();
                updateHealth();
                break;
            case 's', 'S':
                avatarPosition = movement.move(avatarPosition[0], avatarPosition[1], 0, -1);
                nextGeneration();
                updateHealth();
                break;
            case 'w', 'W':
                avatarPosition = movement.move(avatarPosition[0], avatarPosition[1], 0, 1);
                nextGeneration();
                updateHealth();
                break;
            case ' ':
                avatarPosition = movement.move(avatarPosition[0], avatarPosition[1], 0, 0);
                nextGeneration();
                updateHealth();
                break;
            case 'Q', 'q':
                if (lastKeyTyped == ':') {
                    saveBoard();
                    System.exit(0);
                }
                break;
            case 'E', 'e':
                if (numFlowers > 0) {
                    attack();
                }
        }
    }


    private void attack() {
        numFlowers--;
        attackHelper(avatarPosition[0], avatarPosition[1]);
    }

    private void attackHelper(int x, int y) {
        board[x][y] = Tileset.FLOOR;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int a = x + i;
                int b = y + j;
                if (a < 0 || b < 0 || a >= World.DEFAULT_WIDTH || b >= World.DEFAULT_HEIGHT) {
                    continue;
                }
                if (board[a][b] == Tileset.GRASS) {
                    attackHelper(a, b);
                }
            }
        }
    }

    private void renderBoard() {
        moveAvatar();
        TER.drawTiles(board);
        renderScore();


    }

    public void nextGeneration() {
        // The board is filled with Tileset.NOTHING
        for (int i = 0; i < World.DEFAULT_WIDTH; i++) {
            for (int j = 0; j < World.DEFAULT_HEIGHT; j++) {
                int x = numNeighbors(i, j, board);
                if (board[i][j] == Tileset.GRASS) {
                    if (x == 2 || x == 3) {
                        board[i][j] = Tileset.GRASS;
                    }
                }
                if (board[i][j] == Tileset.GRASS && x > 3) {
                    board[i][j] = Tileset.FLOOR;
                }
                if (board[i][j] == Tileset.GRASS && x < 2) {
                    board[i][j] = Tileset.FLOOR;
                }
                if (board[i][j] == Tileset.FLOOR) {
                    if (x == 3) {
                        board[i][j] = Tileset.GRASS;
                    }
                }
            }

        }
    }

    private int numNeighbors(int a, int b, TETile[][] c) {
        int n = 0;
        for (int i = a - 1; i <= a + 1; i++) {
            for (int j = b - 1; j <= b + 1; j++) {
                if (i < 0 || j < 0) {
                    continue;
                }
                if (i >= World.DEFAULT_WIDTH || j >= World.DEFAULT_HEIGHT) {
                    continue;
                }
                if (i == a && j == b) {
                    continue;
                }
                if (c[i][j] == Tileset.GRASS || c[i][j] == Tileset.FLOWER || c[i][j] == Tileset.AVATAR) {
                    n++;
                }
            }
        }
        return n;
    }

    private void renderScore() {
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(scoreText);
        StdDraw.text(SCORE_X, SCORE_Y, "Score: " + score);
        StdDraw.text(SCORE_X + 5, SCORE_Y, "Flowers:" + numFlowers);
        int x = (int) Math.min(79, Math.floor(mousePosition[0]));
        int y = (int) Math.min(29, Math.floor(mousePosition[1]));
        x = Math.max(0, x);
        y = Math.max(0, y);
        String des = board[x][y].description();
        StdDraw.text((double) World.DEFAULT_WIDTH / 2, SCORE_Y, des);
        double heartXPos = World.DEFAULT_WIDTH - 3; //puts it on right side and leaves enough space for maxHealth hearts
        StdDraw.setPenColor(Color.RED);
        StdDraw.text(heartXPos, SCORE_Y, healthString);

        //converting milliseconds to seconds
        long remainingTime = (DURATION - (System.currentTimeMillis() - startTime)) / 1000;
        String time = "Timer: " + remainingTime + "s";
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(heartXPos, SCORE_Y - 1, time);
        StdDraw.show();
        TER.resetFont();
    }

    private void moveAvatar() {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (board[i + avatarPosition[0]][j + avatarPosition[1]] == Tileset.AVATAR) {
                    board[i + avatarPosition[0]][j + avatarPosition[1]] = standingOn;
                }
            }
        }
        standingOn = board[avatarPosition[0]][avatarPosition[1]];
        checkWinner();
        if (standingOn == Tileset.FLOWER) {
            standingOn = Tileset.FLOOR;
            score += 100;
            health++;
            updateHealth();
            numFlowers++;

        }
        if (Math.floorMod(score, 1000) == 0 && score != 0) {
            board[spawn[0]][spawn[1]] = Tileset.UNLOCKED_DOOR;
        }
        board[avatarPosition[0]][avatarPosition[1]] = Tileset.AVATAR;
    }


    private void checkWinner() {
        if (standingOn == Tileset.UNLOCKED_DOOR) {
            isGameOVer = true;
            gameWon();

            music.stop();
            music.close();
        }
    }


    private void updateHealth() {
        if (standingOn == Tileset.GRASS) {
            health -= 1;
        }
        StringBuilder x = new StringBuilder();
        if (health > MAX_HEALTH) {
            health = MAX_HEALTH;
        }
        if (health < 0) {
            health = 0;
        }
        x.append("♥".repeat(health));
        if (health < MAX_HEALTH) {
            x.append("♡".repeat(MAX_HEALTH - health));
        }
        x.append(health);
        healthString = x.toString();
        if (health == 0) {
            isGameOVer = true;
            gameOver();
        }
    }



    public void saveBoard() {
        StringBuilder boardGrid = new StringBuilder();
        boardGrid.append(currentSeed).append(" ").append(score).append(" ").append(avatarPosition[0]);
        boardGrid.append(" ").append(avatarPosition[1]).append(" ").append(tileToChar(standingOn));
        boardGrid.append(" ").append(health).append("\n");
        for (int y = 0; y < World.DEFAULT_HEIGHT; y++) {
            for (int x = 0; x < World.DEFAULT_WIDTH; x++) {
                boardGrid.append(tileToChar(board[x][y]));
            }
            boardGrid.append("\n");
        }

        FileUtils.writeFile(SAVE_FILE, boardGrid.toString());
        System.out.println("Board saved");
    }


    private char tileToChar(TETile tile) {
        if (tile.equals(Tileset.FLOOR)) {
            return '2';
        }
        if (tile.equals(Tileset.WALL)) {
            return '1';
        }
        if (tile.equals(Tileset.LOCKED_DOOR)) {
            return 'L';
        }
        if (tile.equals(Tileset.GRASS)) {
            return '4';
        }
        if (tile.equals(Tileset.FLOWER)) {
            return '3';
        }
        if (tile.equals(Tileset.AVATAR)) {
            return 'A';
        }
        if (tile.equals(Tileset.UNLOCKED_DOOR)) {
            return 'U';
        }
        return '0';
    }

    private TETile charToTile(char c) {
        return switch (c) {
            case '2' -> Tileset.FLOOR;
            case '1' -> Tileset.WALL;
            case 'L' -> Tileset.LOCKED_DOOR;
            case 'A' -> Tileset.AVATAR;
            case '4' -> Tileset.GRASS;
            case '3' -> Tileset.FLOWER;
            case 'U' -> Tileset.UNLOCKED_DOOR;
            default -> Tileset.NOTHING;
        };
    }


    public TETile[][] loadBoard() {
        if (!FileUtils.fileExists(SAVE_FILE)) {
            System.exit(0);
        }
        String grid = FileUtils.readFile(SAVE_FILE);
        if (grid.isEmpty()) {
            System.exit(0);
        }
        String[] lineChar = grid.split("\n");
        String[] attributes = lineChar[0].split(" ");
        avatarPosition = new int[2];
        avatarPosition[0] = Integer.parseInt(attributes[2]);
        avatarPosition[1] = Integer.parseInt(attributes[3]);
        standingOn = charToTile(attributes[4].toCharArray()[0]);
        health = Integer.parseInt(attributes[5]);
        updateHealth();
        long seed = Long.parseLong(attributes[0]);
        score = Integer.parseInt(attributes[1]);
        world = new World(seed);
        currentSeed = seed;
        TETile[][] loadedBoard = world.getWorld();
        spawn = world.getSpawnPoint();

        for (int i = 1; i < lineChar.length; i++) {
            char[] tileGrid = lineChar[i].toCharArray(); //height
            for (int j = 0; j < tileGrid.length; j++) {
                char x = tileGrid[j];
                loadedBoard[j][i - 1] = charToTile(x);
            }
        }
        return loadedBoard;
    }


    public static void main(String[] args) {

        Main game = new Main();
        game.start();
        game.runGame();
    }

}
