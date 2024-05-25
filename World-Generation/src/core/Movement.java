package core;
//import tileengine.TETile;
import tileengine.Tileset;

import static core.MusicPlayer.playMusic2;

public class Movement {

    private int x;
    private int y;
    private World world;

    public Movement(int x, int y, World world) {
        this.world = world;
        this.x = world.width;
        this.y = world.height;
    }

    public void canMove(int x1, int y1) {
        if (world.playerX + x1 < 0 || world.playerX + x1 >= world.width) {
            return;
        }
        if (world.playerY + y1 < 0 || world.playerY + y1 >= world.height) {
            return;
        }
        if (world.getBoard()[world.playerX + x1][world.playerY + y1] == Tileset.WALL) {
            playMusic2("src/core/Fail Sound Effect.wav");
            return;
        }
        world.getBoard()[world.playerX + x1][world.playerY + y1] = world.player;
        world.getBoard()[world.playerX][world.playerY] = Tileset.FLOOR;
        world.playerX += x1;
        world.playerY += y1;


    }
}
