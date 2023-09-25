import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;

public class Gameplay {

    public static final String moveFile = "move_file";
    public static final String goFile = "dannydevito.go";
    public static final String passFile = "dannydevito.pass";
    public static final String endFile = "end_game";

    public static final Path directoryPath = Path.of("C:\\Users\\Aidan\\Desktop\\Intro to AI\\refereeV6");

    public static void main(String[] args) throws IOException, InterruptedException {
        boolean gameRunning = true;
        Path dir = directoryPath;
        //-----------------------------------------
        System.out.println(dir);
        //-----------------------------------------

        WatchService watchService = FileSystems.getDefault().newWatchService();

        dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);


        //Initializing the array
        int arraySize=(9 * 2) + 1;
        int[][] array = new int[arraySize][arraySize];

        for (int i = 0; i < arraySize; i++) {
            for (int j = 0; j < arraySize; j++) {
                if (i % 2 == 0 && j % 2 ==0) {
                    array[i][j] = 8;
                }
                else if(i % 2 == 0 && j % 2 != 0 || i % 2 != 0 && j % 2 == 0 ){
                    array[i][j] = 2;
                }
                else array[i][j] = 0;
            }
        }

        //SHOULD IA Move be false here??
        Board2 gameBoard = new Board2(array, 0, 0, false);

        while(gameRunning){
            WatchKey key = watchService.take();
            for (WatchEvent<?> event : key.pollEvents()) {
                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    if(isFilePresent(endFile)){
                        //Game is Over
                        System.out.println("game over");
                        String endfileContents = fileContents(endFile);
                        System.out.println(endfileContents);
                        gameRunning = false;
                    } else{
                        Path filePath = (Path) event.context();
                        String fileName = filePath.getFileName().toString();

                        if(fileName.equals(goFile)){
                            //-----------------------------------------
                            System.out.println("GO File Detected!");
                            //-----------------------------------------

                            String oppMove = fileContents(moveFile);

                            if(oppMove.equals("")){
                                System.out.println("We have first turn");
                                gameBoard.myMove = true;
                            } else {
                                gameBoard.myMove = false;
                                System.out.println("saving opp move");
                                System.out.println(oppMove);
                                if(!isPassMove(oppMove)) {
                                    int[] oppCoords = getArrayCoordinates(oppMove);
                                    gameBoard.completeMove(oppCoords[0], oppCoords[1]);

                                    gameBoard.printboard();
                                }
                            }

                            System.out.println("calculating move");
                            long startTime = System.nanoTime();

                            gameBoard.myMove = true;
                            int[] moveVals = Minimax2.getBestMove(gameBoard);
                            int[] ourMove = arrayToBoardCoords(moveVals[0], moveVals[1]);

                            String moveString = "dannydevito " + ourMove[0] + "," + ourMove[1] + " " + ourMove[2] + "," + ourMove[3];
                            int[] ourCoords = getArrayCoordinates(moveString);

                            System.out.println(moveString);

                            gameBoard.completeMove(ourCoords[0], ourCoords[1]);

                            //Write to moveFile to end turn
                            overwriteFile(moveFile, moveString);

                            System.out.println("sending move");
                            long endTime = System.nanoTime();
                            long elapsedTimeMs = (endTime - startTime) / 1000000000; // Convert nanoseconds to milliseconds
                            System.out.println("Elapsed time: " + elapsedTimeMs + " seconds");


                            gameBoard.printboard();

                        }
                        else if (fileName.equals(passFile)){

                            //Pass here, make empty move

                            //-----------------------------------------
                            System.out.println("PASS File Detected!\n");
                            //-----------------------------------------
                            gameBoard.myMove = false;
                            String oppMove = fileContents(moveFile);
                            int[] oppCoords = getArrayCoordinates(oppMove);
                            gameBoard.completeMove(oppCoords[0], oppCoords[1]);

                            gameBoard.printboard();

                            //Write Empty move to moveFile
                            String passMove = "dannydevito 0,0 0,0";
                            overwriteFile(moveFile, passMove);

                        }
                    }
                }
            }

            // Reset the key
            boolean valid = key.reset();
            if (!valid) {
                // Key is no longer valid, exit the loop
                break;
            }

        }

    }

    /**
     * Find out if a specific file is present in the project directory
     *
     * @param fileName name of the file we are checking for
     * @return True if the file exists
     */
    public static boolean isFilePresent(String fileName) {
        String dir = System.getProperty("user.dir");
        dir = directoryPath.toString();
        // Create a File object for the file you want to check
        File target = new File(dir, fileName);

        return target.exists();
    }

    /**
     * Provide the file to overwrite and the message to write as strings
     * @param fileName the name of the file
     * @param message the message to overwrite the file with
     * @throws IOException error if the file does not exist or is a directory
     */
    public static void overwriteFile(String fileName, String message) throws IOException {
        if(!isFilePresent(fileName)){
            System.out.println("File not Present");
        }
        else{
            try {
                String dir = System.getProperty("user.dir");
                dir = directoryPath.toString();
                File target = new File(dir, fileName);
                FileWriter writer = new FileWriter(target, false);
                writer.write(message);
                writer.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Get the contents of a file as a string
     * @param fileName File to read
     * @return String containing the file's message
     * @throws IOException Error if file DNE or is directory
     */
    public static String fileContents(String fileName) throws IOException {
        try{

            return Files.readString(directoryPath.resolve(fileName));
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Gives the coordinates of our game array to update the line of
     * @param move the string given by the move_file
     * @return an int array of size 3 with the x and y value of the board array to update and the direction
     */
    public static int[] getArrayCoordinates(String move){
        int[] arrayCoordsToUpdate = new int[3];

        String coords = move.substring(move.length() - 7);
        String coord1 = coords.substring(0,3);
        String coord2 = coords.substring(4);

        String[] parts1 = coord1.split(",");
        String[] parts2 = coord2.split(",");

        int x1 = Integer.parseInt(parts1[0]);
        int y1 = Integer.parseInt(parts1[1]);
        int x2 = Integer.parseInt(parts2[0]);
        int y2 = Integer.parseInt(parts2[1]);

        //Ensure we have bottom left coord
        if(x2-x1 < 0 || y2-y1 < 0){
            arrayCoordsToUpdate[0] = x2 *2;
            arrayCoordsToUpdate[1] = y2 *2;

        } else{
            arrayCoordsToUpdate[0] = x1 *2;
            arrayCoordsToUpdate[1] = y1 *2;
        }
        if(x2-x1 != 0){
            //Horizontal Line, add to x val
            arrayCoordsToUpdate[0]++;
            arrayCoordsToUpdate[2] = 0;
        } else {
            //Vert Line, add to Y
            arrayCoordsToUpdate[1]++;
            arrayCoordsToUpdate[2] = 1;
        }

        return arrayCoordsToUpdate;
    }

    public static boolean isPassMove(String input) {
        if (input.length() < 7) {
            return false;
        }
        String coordText = input.substring(input.length() - 7);

        return coordText.equals("0,0 0,0");
    }

    public static int[] arrayToBoardCoords(int x, int y){
        int[] boardCoords = new int[4];
        if(x % 2 == 1){
            //X is odd, we know it is a vert line
            //Need x above and below, y stays the same
            boardCoords[0] = (x-1) / 2;
            boardCoords[1] = y/2;
            boardCoords[2] = (x+1) / 2;
            boardCoords[3] = y/2;
        } else{
            //x is even, horiz line
            //need differences in y
            boardCoords[0] = x/2;
            boardCoords[1] = (y-1)/2;
            boardCoords[2] = x/2;
            boardCoords[3] = (y+1)/2;
        }
        return boardCoords;
    }

}
