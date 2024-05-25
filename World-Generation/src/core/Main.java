package core;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import static core.MusicPlayer.playMusic;
//import static edu.princeton.cs.algs4.StdDraw.filledRectangle;
//import static edu.princeton.cs.algs4.StdDraw.show;

public class Main {
    private static final int PAUSEDURATION = 200;
    private static final int HEIGHT = 50;
    private static final int WIDTH = 50;
    private static final int COLOR = 255;
    private static final int POS = 25;
    private static final int POSD = 20;
    private static final int POSW = 30;

    private static final int POSQ = 15;




    public static void main(String[] args) {
        playMusic("src/core/minecraft.wav");
        TERenderer ter = new TERenderer();
        TETile[][] holder = new TETile[HEIGHT][WIDTH];
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                holder[i][j] = Tileset.NOTHING;
            }
        }

        ter.initialize(WIDTH, HEIGHT);
        String holder2 = "";
        boolean first = true;
        boolean please = false;
        while (true) {
            char c = ' ';
            StdDraw.clear();
            ter.renderFrame(holder);
            StdDraw.setPenColor(COLOR, COLOR, COLOR);
            StdDraw.text(POS, POS, "CS61B: THE GAME");
            StdDraw.text(POSD, POSD, "New Game (N)");
            StdDraw.text(POSW, POSD, "Load Game (L)");
            if (please) {
                StdDraw.text(POS, POSQ, "Enter Seed: " + holder2);
            }
            StdDraw.show(); // Show the text
            StdDraw.pause(PAUSEDURATION);
            if (StdDraw.hasNextKeyTyped()) {
                c = StdDraw.nextKeyTyped();
                if (c == 'n' || c == 'N') {
                    please = true;
                    holder2 = holder2 + c;
                    if (first) {
                        first = false;
                        holder2 = holder2.substring(1);
                    }
                }
                if (c == 's' || c == 'S') {
                    holder2 = holder2 + c;
                    display(holder2);
                    break;
                }
                if (c == 'l' || c == 'L') {
                    loadBoard("save_file.txt");
                    break;
                }
                holder2 = holder2 + c;
            }
        }
       // display(holder2);

    }
    //@source AI helpped debug
    public static void display(String seed) {
        TERenderer ter = new TERenderer();
        World world = new World(seed);
        TETile[][] tiles = world.getBoard();
        ter.initialize(tiles.length, tiles[0].length);
        ter.renderFrame(tiles);
        TETile[][] cloneBoardNoYou = world.cloneBoardNoYou();
        StdDraw.pause(PAUSEDURATION);
        boolean playerMovement = true;
        while (true) {
            world.generateUI();
            ter.renderFrame(world.getBoard());
            world.moving(true, cloneBoardNoYou);
        }
    }

    //@source AI helpped debug
    public static void loadBoard(String filename) {

        In in = new In(filename);
        String seed = "";
        while (in.hasNextLine()) {
            String line = in.readLine();
            String[] dimensions = line.split(" ");
            seed = dimensions[0];
            break;
        }
        int y = HEIGHT - 1;
        TETile[][] board = new TETile[HEIGHT + 5][HEIGHT];
        for (int i = 0; i < HEIGHT + 5; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                board[i][j] = Tileset.NOTHING;
            }
        }
        while (in.hasNextLine()) {
            String line = in.readLine();
            for (int x = 0; x < HEIGHT; x++) {
                if (y == -1) {
                    break;
                }
                if (line.charAt(x) == ' ') {
                    board[x][y] = Tileset.NOTHING;
                } else if (line.charAt(x) == '·') {
                    board[x][y] = Tileset.FLOOR;
                } else if (line.charAt(x) == '#') {
                    board[x][y] = Tileset.WALL;
                } else if (line.charAt(x) == '@') {
                    board[x][y] = Tileset.AVATAR;
                } else if (line.charAt(x) == 'f') {
                    board[x][y] = Tileset.FLOWER;
                } else if (line.charAt(x) == 'l') {
                    board[x][y] = Tileset.LOCKED_DOOR;
                } else if (line.charAt(x) == 'u') {
                    board[x][y] = Tileset.UNLOCKED_DOOR;
                }

            }
            y--;
        }
        TERenderer ter = new TERenderer();
        World world = new World(seed);
        board = transpose(flip(board));
        // Copy all floor tiles from world.board to board
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (board[i][j] == Tileset.AVATAR) {
                    world.playerX = i;
                    world.playerY = j;
                }
            }
        }

        world.board = board;
        TETile[][] tiles = world.getBoard();
        ter.initialize(tiles.length, tiles[0].length);
        TETile[][] cloneBoardNoYou = world.cloneBoardNoYou();
        ter.renderFrame(tiles);
        StdDraw.pause(PAUSEDURATION);
        world.generateUI();
        while (true) {
            world.generateUI();
            ter.renderFrame(world.getBoard());
            world.moving(true, cloneBoardNoYou);
        }
    }
    //Taken from lab09
    private static TETile[][] transpose(TETile[][] tiles) {
        int w = tiles[0].length;
        int h = tiles.length;

        TETile[][] transposeState = new TETile[w][h];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                transposeState[x][y] = tiles[y][x];
            }
        }
        return transposeState;
    }
    //Taken from lab09
    private static TETile[][] flip(TETile[][] tiles) {
        int w = tiles.length;
        int h = tiles[0].length;

        TETile[][] rotateMatrix = new TETile[w][h];
        int y = h - 1;
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                rotateMatrix[i][y] = tiles[i][j];
            }
            y--;
        }
        return rotateMatrix;
    }
}
