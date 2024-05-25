package core;

//import edu.princeton.cs.algs4.StdDraw;
//import tileengine.TERenderer;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
//import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import static utils.FileUtils.writeFile;
//import utils.RandomUtils;
//import edu.princeton.cs.algs4.StdIn; // don't know if you can use this
//import edu.princeton.cs.algs4.StdOut;  // don't know if you can use this
//import java.awt.*;
import java.util.*;
import java.util.List;

public class World {
    int width;
    private static final int NUMBEROFROOMS = 20;
    int height;
    private static final int WIDTHDONTCHANGE = 50;
    private static final int HEIGHTDONTCHANGE = 50;
    private static final int HUDHEIGHT = 5;
    private Random random;
    TETile[][] board;
    TETile player;
    int playerX;
    int playerY;
    Movement movement;
    String holder;
    boolean done = false;
    String seed;
    //private final TERenderer ter = new TERenderer();


    public World(String seed) {
        this.seed = seed;
        this.width = WIDTHDONTCHANGE;
        this.height = HEIGHTDONTCHANGE;
        this.player = Tileset.AVATAR;
        this.movement = new Movement(width, height, this);
        // ter.initialize(width, height);
        this.board = new TETile[width][height + HUDHEIGHT];
        //Creates a random Seed
        long num = 0;
        if (seed.charAt(0) == 'n' || seed.charAt(0) == 'N') {
            //Line from stack overflow
            // @source https://stackoverflow.com/questions/14974033/extract-digits-from-string-stringutils-java;
            num = Long.parseLong(seed.replaceAll("[^0-9]", ""));
            this.random = new Random(num);
            // Creates an empty world with Height and Width
            for (int i = 0; i < height + HUDHEIGHT; i++) {
                for (int j = 0; j < width; j++) {
                    board[j][i] = Tileset.NOTHING;
                }
            }
            this.renderRooms();


            while (true) {
                this.playerX = random.nextInt(width);
                this.playerY = random.nextInt(height);
                if (board[playerX][playerY] == Tileset.FLOOR) {
                    board[playerX][playerY] = player;
                    break;
                }
            }

        } else if (seed.charAt(0) == 'l' || seed.charAt(0) == 'L') {

            loadBoard("save_file.txt");
        }

        String trying = " ";
        boolean sawS = false;
        boolean onlyTime = true;
        for (int i = 0; i < seed.length(); i++) {
            if (seed.charAt(0) == 'l' || seed.charAt(0) == 'L') {
                trying = seed;
                break;
            }
            if (sawS && (seed.charAt(0) != 'l' || seed.charAt(0) != 'L')) {
                trying += seed.charAt(i);
            }
            if ((seed.charAt(i) == 's' || seed.charAt(i) == 'S') && onlyTime) {
                sawS = true;
                onlyTime = false;
                continue;
            }
        }
        for (int i = 0; i < trying.length(); i++) {
            if (trying.charAt(i) == 's' || seed.charAt(i) == 'S') {
                movement.canMove(0, -1);
            }
            if (trying.charAt(i) == 'w' || seed.charAt(i) == 'W') {
                movement.canMove(0, 1);
            }
            if (trying.charAt(i) == 'a' || seed.charAt(i) == 'S') {
                movement.canMove(-1, 0);
            }
            if (trying.charAt(i) == 'd' || seed.charAt(i) == 'D') {
                movement.canMove(1, 0);
            }
        }
        if (seed.length() >= 2 && seed.charAt(seed.length() - 2) == ':') {
            this.degeneratePov(cloneBoardNoYou());
            saveGame(board, seed);
        }


        //this.renderBoard();
    }

    // build your own world!
    //@source took inspiration from https://stackoverflow.com/questions/60661260/making-random-rectangles-on-processing
    public void renderRooms() {
        // creates a list of rooms (see room class)
        List<Room> boxes = Room.generateRooms(NUMBEROFROOMS, width, height, random);
        // goes through each room and finds the x-y boundaries and fill in the space
        // with floor tiles
        for (Room room : boxes) {
            for (int i = room.x; i < room.x + room.width; i++) { // room.x + room.width is the right boundary
                for (int j = room.y; j < room.y + room.height; j++) { // room.y + room.height is the top boundary
                    board[j][i] = Tileset.FLOOR;
                }
            }
            // room.x + room.width is the right boundary
            //took inspiration from my gameOfLife lab09 code to find adjacent
            for (int i = room.x - 1; i < room.x + room.width + 1; i++) {
                // room.y + room.height is the top boundary
                for (int j = room.y - 1; j < room.y + room.height + 1; j++) {
                    if (j >= 0 && i >= 0 && board[j][i] == Tileset.NOTHING) {
                        board[j][i] = Tileset.WALL;
                    }
                }
            }
        }
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x + 1 < width && x - 1 >= 0 && board[x][y] == Tileset.WALL && board[x - 1][y] == Tileset.FLOOR
                        && board[x + 1][y] == Tileset.FLOOR) {
                    board[x][y] = Tileset.FLOOR;
                }
                if (y + 1 < height && y - 1 >= 0 && board[x][y] == Tileset.WALL && board[x][y - 1] == Tileset.FLOOR
                        && board[x][y + 1] == Tileset.FLOOR) {
                    board[x][y] = Tileset.FLOOR;
                }
            }
        }
        generateHallways(boxes);
        borderPatrol();

    }



    //@source once entering proj3b there was an error with the hallways and the pov so AI helped debug the issue
    //@source inspiration from
    // https://roguesharp.wordpress.com/2016/04/03/roguesharp-v3-tutorial-connecting-rooms-with-hallways/
    public void generateHallways(List<Room> rooms) {
        for (int i = 0; i < rooms.size() - 1; i++) {
            Room room1 = rooms.get(i);
            Room room2 = rooms.get(i + 1);
            // Grabs the center of each room
            int x1 = room1.x + room1.width / 2;
            int y1 = room1.y + room1.height / 2;
            int x2 = room2.x + room2.width / 2;
            int y2 = room2.y + room2.height / 2;
            // loops until the two x coordinates are the same
            // and draws a path (run)
            while (x1 != x2) {
                if (x1 < x2) {
                    x1++;
                } else {
                    x1--;
                }
                board[y1][x1] = Tileset.FLOOR;
            }
            // loops until the two y coordinates are the same
            // and draws a path (rise)
            while (y1 != y2) {
                if (y1 < y2) {
                    y1++;
                } else {
                    y1--;
                }
                board[y1][x1] = Tileset.FLOOR;
            }
        }
        //Makes walls for each hallway by checking if a floor tile is surrounded by nothing
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (j + 1 < width && j - 1 >= 0 && i + 1 < height && i - 1 >= 0 && board[j][i] == Tileset.NOTHING
                        && (board[j - 1][i] == Tileset.FLOOR || board[j + 1][i] == Tileset.FLOOR
                        || board[j][i - 1] == Tileset.FLOOR || board[j][i + 1] == Tileset.FLOOR)) {
                    board[j][i] = Tileset.WALL;
                }
            }
        }
    }

    public void borderPatrol() {
        for (int horiz = 0; horiz < width; horiz++) {
            if (board[horiz][0] == Tileset.FLOOR) {
                board[horiz][0] = Tileset.WALL;
            }
            if (board[horiz][height - 1] == Tileset.FLOOR) {
                board[horiz][height - 1] = Tileset.WALL;
            }
        }
        for (int vert = 0; vert < height; vert++) {
            if (board[0][vert] == Tileset.FLOOR) {
                board[0][vert] = Tileset.WALL;
            }
            if (board[width - 1][vert] == Tileset.FLOOR) {
                board[width - 1][vert] = Tileset.WALL;
            }
        }
    }

    public boolean isItFatEnough(int percentofFatness) {
        int area = width * height;
        int sizeCheck = area * percentofFatness / WIDTHDONTCHANGE; // make it 100
        int counter = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (board[j][i] == Tileset.FLOOR || board[j][i] == Tileset.WALL) {
                    counter++;
                }
            }
        }
        return counter >= sizeCheck;
    }

    public void renderBoard() {
        while (true) {
            //ter.renderFrame(board);
        }
    }

    public TETile[][] getBoard() {
        return this.board;
    }

    char lastkey = ' ';

    //AI thought of lastkey @source github copilot;
    public void moving(boolean onOrOff, TETile[][] clone) {
        if (onOrOff && StdDraw.hasNextKeyTyped()) {
            char c = StdDraw.nextKeyTyped();
            if (true) {
                if (c == 'q' || c == 'e') {
                    lastKeyYes = c;
                }
                if ('q' == lastKeyYes) {
                    generatePov(clone);

                } else if ('e' == lastKeyYes) {
                    degeneratePov(clone);
                }
            }
            if (c == 'a') {
                movement.canMove(-1, 0);
            } else if (c == 'd') {
                movement.canMove(1, 0);
            } else if (c == 's') {
                movement.canMove(0, -1);
            } else if (c == 'w') {
                movement.canMove(0, 1);
            } else if (c == 'q') {
                if (lastkey == ':') {
                    this.degeneratePov(new World(this.seed).cloneBoardNoYou());
                    saveGame(board, seed);
                    System.exit(0);
                }
            }
            lastkey = c;
        }
    }

    int prevX = 0;
    int prevY = 0;
    //Used AI to help with the flickering of the UI @source gitHub copilot
    public void generateUI() {
        String tilePiece = Tileset.NOTHING.description();
        int top = HEIGHTDONTCHANGE + HUDHEIGHT - 1;
        int left = 2;
        Double posXDouble = StdDraw.mouseX();
        Double posYDouble = StdDraw.mouseY();
        int posX = posXDouble.intValue();
        int posY = posYDouble.intValue();
        if (posX < WIDTHDONTCHANGE && posY < HEIGHTDONTCHANGE) {
            tilePiece = board[posX][posY].description();
            StdDraw.setPenColor(StdDraw.BOOK_RED);
            StdDraw.text(left, top, tilePiece);
            StdDraw.show();
        } else {
            StdDraw.setPenColor(StdDraw.BOOK_RED);
            StdDraw.show();
        }
        prevX = posX;
        prevY = posY;
    }
    //heavily influenced by AI used to fix our broken code @source gitHub copilot orginally was square changed to circle
    public void generatePov(TETile[][] clone) {
        TETile[][] tempBoard = clone;
        int radiusX = 3;
        int radiusY = 3;
        int youX = playerX;
        int youY = playerY;

        // Set all tiles to NOTHING
        fillBoard(board);

        // Iterate over a circle around the player
        for (int dx = -radiusX; dx <= radiusX; dx++) {
            for (int dy = -radiusY; dy <= radiusY; dy++) {
                if (dx * dx + dy * dy > radiusX * radiusX + radiusY * radiusY) {
                    // Skip tiles outside the circle
                    continue;
                }

                // Calculate a line from the player to the tile
                int x = youX;
                int y = youY;
                while (x != youX + dx || y != youY + dy) {
                    if (x < youX + dx) {
                        x++;
                    }
                    if (x > youX + dx) {
                        x--;
                    }

                    if (y < youY + dy) {
                        y++;
                    }
                    if (y > youY + dy) {
                        y--;
                    }

                    // If the tile is a wall, stop
                    if (tempBoard[x][y] == Tileset.WALL) {
                        board[x][y] = tempBoard[x][y];
                        break;
                    }

                    // Otherwise, set the tile
                    board[x][y] = tempBoard[x][y];
                }
            }
        }

        // Set the player's position
        board[youX][youY] = Tileset.AVATAR;
    }



    public void degeneratePov(TETile[][] clone) {
        TETile[][] tempBoard = clone;

        int youX = this.playerX;
        int youY = this.playerY;
        for (int i = 0; i < HEIGHTDONTCHANGE; i++) {
            for (int j = 0; j < WIDTHDONTCHANGE; j++) {
                board[j][i] = tempBoard[j][i];
            }
            board[youX][youY] = Tileset.AVATAR;
        }
        return;
    }
    private char lastKeyYes = ' ';

    //    public void togglePov(TETile[][] clone) {
    //        StdDraw.pause(2);
    //        if (StdDraw.hasNextKeyTyped()) {
    //            char temp = StdDraw.nextKeyTyped();
    //            if (temp == 'q' || temp == 'e') {
    //                lastKeyYes = temp;
    //            }
    //        }
    //        if ('q' == lastKeyYes) {
    //            generatePov(clone);
    //
    //        } else if ('e' == lastKeyYes) {
    //            degeneratePov(clone);
    //        }
    //    }

    public void fillBoard(TETile[][] tempBoard) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                tempBoard[j][i] = Tileset.NOTHING;
            }
        }
    }

    public TETile[][] cloneBoardNoYou() {
        TETile[][] cloneBoard = new TETile[WIDTHDONTCHANGE][HEIGHTDONTCHANGE];
        for (int i = 0; i < HEIGHTDONTCHANGE; i++) {
            for (int j = 0; j < WIDTHDONTCHANGE; j++) {
                if (board[j][i] == Tileset.AVATAR) {
                    cloneBoard[j][i] = Tileset.FLOOR;
                } else {
                    cloneBoard[j][i] = board[j][i];
                }
            }
        }
        return cloneBoard;
    }
    public TETile[][] cloneBoardYesYou() {
        TETile[][] cloneBoard = new TETile[WIDTHDONTCHANGE][HEIGHTDONTCHANGE];
        for (int i = 0; i < HEIGHTDONTCHANGE; i++) {
            for (int j = 0; j < WIDTHDONTCHANGE; j++) {
                cloneBoard[j][i] = board[j][i];
            }
        }
        return cloneBoard;
    }
    //AI generated @source github copilot
    public void saveGame(TETile[][] board1, String random1) {

        String filename = "save_file.txt";
        String boardString = "";
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                boardString += board1[i][j].character();
            }
            boardString += "\n";
        }
        String playerString = player.character() + " " + playerX + " " + playerY;
        String saveString = seed + "\n" + boardString + playerString;
        writeFile(filename, saveString);
    }
    //used AI to help debug code @source github copilot
    public void loadBoard(String filename) {

        In in = new In(filename);
        String seed1 = "";
        while (in.hasNextLine()) {
            String line = in.readLine();
            String[] dimensions = line.split(" ");
            seed1 = dimensions[0];
            break;
        }
        int y = HEIGHTDONTCHANGE - 1;
        TETile[][] board1 = new TETile[HEIGHTDONTCHANGE + 5][HEIGHTDONTCHANGE];
        for (int i = 0; i < HEIGHTDONTCHANGE + 5; i++) {
            for (int j = 0; j < HEIGHTDONTCHANGE; j++) {
                board1[i][j] = Tileset.NOTHING;
            }
        }
        while (in.hasNextLine()) {
            String line = in.readLine();
            for (int x = 0; x < HEIGHTDONTCHANGE; x++) {
                if (y == -1) {
                    break;
                }
                if (line.charAt(x) == ' ') {
                    board1[x][y] = Tileset.NOTHING;
                } else if (line.charAt(x) == '·') {
                    board1[x][y] = Tileset.FLOOR;
                } else if (line.charAt(x) == '#') {
                    board1[x][y] = Tileset.WALL;
                } else if (line.charAt(x) == '@') {
                    board1[x][y] = Tileset.AVATAR;
                } else if (line.charAt(x) == 'f') {
                    board1[x][y] = Tileset.FLOWER;
                } else if (line.charAt(x) == 'l') {
                    board1[x][y] = Tileset.LOCKED_DOOR;
                } else if (line.charAt(x) == 'u') {
                    board1[x][y] = Tileset.UNLOCKED_DOOR;
                }

            }
            y--;
        }
        TERenderer ter = new TERenderer();
        World world = new World(seed1);
        board1 = transpose(flip(board1));
        // Copy all floor tiles from world.board to board
        for (int i = 0; i < HEIGHTDONTCHANGE; i++) {
            for (int j = 0; j < HEIGHTDONTCHANGE; j++) {
                if (board1[i][j] == Tileset.AVATAR) {
                    this.playerX = i;
                    this.playerY = j;
                }
            }
        }
        for (int i = 0; i < board1[0].length; i++) {
            for (int j = 0; j < board1.length; j++) {
                if (board1[j][i] == null) {
                    board1[j][i] = Tileset.NOTHING;
                }
            }

        }

        this.board = board1;
    }
    //Taken from the lab09
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
    //Taken from the lab09
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
