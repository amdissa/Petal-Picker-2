package core;

import tileengine.TETile;
import tileengine.Tileset;

public class Movement {

    private TETile[][] world;


    public Movement(TETile[][] w) {
        world = w;
    }

    public int[] move(int x, int y, int deltaX, int deltaY) {
        TETile current = world[x][y];
        if (world[x + deltaX][y + deltaY] != Tileset.WALL && world[x + deltaX][y + deltaY] != Tileset.NOTHING) {
            //world[x][y] = Tileset.FLOOR;
            //world[x+deltaX][y+deltaY] = current;
            return new int[] {x + deltaX, y + deltaY};
        }
        return new int[] {x, y};

    }

}
