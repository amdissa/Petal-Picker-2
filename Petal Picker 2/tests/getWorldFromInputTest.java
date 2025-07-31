import core.AutograderBuddy;
import org.junit.Test;
import tileengine.TETile;

import static org.junit.Assert.*;

public class getWorldFromInputTest {


    @Test
    public void testGetWorldFromNewGameWithSeedAndQuit() {
        TETile[][] result = AutograderBuddy.getWorldFromInput("n1392967723524655428sddsaawws:q");
        assertNotNull("World should not be null when valid seed is provided", result);

    }

    @Test
    public void testGetWorldFromLoadGame() {
        TETile[][] result = AutograderBuddy.getWorldFromInput("laddw");
        assertNotNull("No save file created", result);
    }
}
