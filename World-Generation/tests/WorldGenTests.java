import core.AutograderBuddy;
import core.World;
import edu.princeton.cs.algs4.StdDraw;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tileengine.TERenderer;
import tileengine.TETile;
import utils.RandomUtils;

import java.util.Random;

public class WorldGenTests {
    @Test
    public void basicTest() {
        // put different seeds here to test different worlds
        TETile[][] tiles = AutograderBuddy.getWorldFromInput("n1234567890123456789s");
        TERenderer ter = new TERenderer();
        ter.initialize(tiles.length, tiles[0].length);
        ter.renderFrame(tiles);
        StdDraw.pause(5000); // pause for 5 seconds so you can see the output
    }

    @Test
    public void basicInteractivityTest() {
        Random random = new Random(1);
        Random random2 = new Random(123);
        //     World test = new World("N3456");
        World test2 = new World("n732420s");


    }

    @Test
    public void basicSaveTest() {
        // TODO: write a test that calls getWorldFromInput twice, with "n123swasd:q" and with "lwasd"
        TETile[][] tiles1 = AutograderBuddy.getWorldFromInput("n1392967723524655428sddsaawwsaddw");
        TETile[][] tiles = AutograderBuddy.getWorldFromInput("n1392967723524655428sddsaawws:q");

        TETile[][] tiles2 = AutograderBuddy.getWorldFromInput("laddw");
        TERenderer ter = new TERenderer();
      //  ter.initialize(tiles2.length, tiles2[0].length);
       // ter.renderFrame(tiles1);
        Assertions.assertArrayEquals(tiles1, tiles2);
     //   StdDraw.pause(1000000000); // pause for 5 seconds so you can see the output

    }
}
