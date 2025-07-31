import core.AutograderBuddy;
import edu.princeton.cs.algs4.StdDraw;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import tileengine.TERenderer;
import tileengine.TETile;

import java.io.IOException;

public class WorldGenTests {



    @Test
    public void basicTest() {
        // put different seeds here to test different worlds
        TETile[][] tiles = AutograderBuddy.getWorldFromInput("n1234567890123456789s");

        TERenderer ter = new TERenderer();
        ter.initialize(tiles.length, tiles[0].length);
        ter.renderFrame(tiles);
        StdDraw.pause(5000); // pause for 5 seconds so you can see the output

        //edited by naz
        TETile[][] tile = AutograderBuddy.getWorldFromInput("n12345678901s");
        ter.initialize(tile.length, tile[0].length);
        ter.renderFrame(tile);
        StdDraw.pause(5000); // pause for 5 seconds so you can see the output
    }

    @Test
    public void basicInteractivityTest() {

    }

    @Test
    public void basicSaveTest() {

    }
}
