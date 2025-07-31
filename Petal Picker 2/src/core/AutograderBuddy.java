package core;


import tileengine.TETile;
import tileengine.Tileset;



public class AutograderBuddy {
    private static final String SAVE_GAME = "save_game.txt";


    private Movement movement;

    /**
     * Simulates a game, but doesn't render anything or call any StdDraw
     * methods. Instead, returns the world that would result if the input string
     * had been typed on the keyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quit and
     * save. To "quit" in this method, save the game to a file, then just return
     * the TETile[][]. Do not call System.exit(0) in this method.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public static TETile[][] getWorldFromInput(String input) {
        Main game = new Main();
        char[] inputs = input.toCharArray();
        StringBuilder seedString = new StringBuilder();
        StringBuilder movements = new StringBuilder();

        if (input.charAt(0) == 'n' || input.charAt(0) == 'N') {

            for (char num : inputs) { //extracts numbers from the input
                if (Character.isDigit(num)) {
                    seedString.append(num);
                } else {
                    movements.append(num);
                }

            }

            long seed = Long.parseLong(seedString.toString());
            World world = new World(seed);
            game.setBoard(world.getWorld());
            game.setAttributes(Tileset.LOCKED_DOOR, world.getSpawnPoint(), new Movement(world.getWorld()));
        } else if (input.charAt(0) == 'l' || input.charAt(0) == 'L') {
            game.setBoard(game.loadBoard());
            game.setMovement(new Movement(game.getBoard()));
            for (char c : inputs) {
                movements.append(c);
            }
        }
        char[] commands = movements.toString().toCharArray();
        for (int i = 1; i < commands.length; i++) {
            if (commands[i] == 'q' || commands[i] == 'Q' && commands[i - 1] == ':') {
                game.saveBoard();
                return game.getBoard();
            }
            game.move(commands[i]);
        }



        return game.getBoard();
    }



    /**
     * Used to tell the autograder which tiles are the floor/ground (including
     * any lights/items resting on the ground). Change this
     * method if you add additional tiles.
     */
    public boolean isGroundTile(TETile t) {
        return t.character() == Tileset.FLOOR.character()
                || t.character() == Tileset.AVATAR.character()
                || t.character() == Tileset.FLOWER.character()
                || t.character() == Tileset.GRASS.character()
                || t.character() == Tileset.LOCKED_DOOR.character()
                || t.character() == Tileset.UNLOCKED_DOOR.character();
    }

    /**
     * Used to tell the autograder while tiles are the walls/boundaries. Change
     * this method if you add additional tiles.
     */
    public boolean isBoundaryTile(TETile t) {
        return t.character() == Tileset.WALL.character();

    }
}
