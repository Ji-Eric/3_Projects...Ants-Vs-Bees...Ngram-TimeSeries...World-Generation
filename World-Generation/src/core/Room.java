package core;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//Used chatGPT to help write this code, used for debugging;
public class Room {
    int x;
    int y;
    int width;
    int height;


    public Room(int x, int y, int width, int height) {
        // creates a room with x-y coordinates and width and height
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    // checks if the room intersects with another room returns true if it does, false otherwise
    // @source https://www.baeldung.com/java-check-if-two-rectangles-overlap
    public Boolean intersects(Room other) {
        return this.x + this.width > other.x && this.x < other.x + other.width
                && this.y < other.y + other.height && this.y + this.height > other.y;
    }
    public static List<Room> generateRooms(int numRooms, int width, int height, Random seed) {
        //uses our seed to create a random object this will ensure that we get the same word
        //for a duplicate seed
        Random random = seed;
        // list of room objects that will later be generated
        List<Room> rooms = new ArrayList<>();
        for (int i = 0; i < numRooms; i++) {
            // creates a random room width and height
            int roomWidth = (random.nextInt(8));
            while (roomWidth <= 5) {
                roomWidth = (random.nextInt(8));
            }
            int roomHeight = (random.nextInt(8));
            while (roomHeight <= 5) {
                roomHeight = (random.nextInt(8));
            }
            // creates a random x and y coordinate for the room
            int x = random.nextInt(width - roomWidth);
            int y = random.nextInt(height - roomHeight);
            Room newRoom = new Room(x, y, roomWidth, roomHeight);
            boolean intersects = false;
            // checks if the room intersects with another room if it does it will not add the room
            for (Room room : rooms) {
                if (newRoom.intersects(room)) {
                    intersects = true;
                    break;
                }
            }
            if (!intersects) {
                rooms.add(newRoom);
            }
        }
        return rooms;
    }



}
