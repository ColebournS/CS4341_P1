import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;

public class Gameplay {

    public static final String moveFile = "move_file";
    public static final String goFile = "dannydevito.go";
    public static final String passFile = "dannydevito.pass";
    public static final String endFile = "end_game";

    //Main function that is while loop that handles all gameplay

    //What to do on groupname.go

    //What to do on groupname.pass

    //What to do when game over
    public static void main(String[] args) throws IOException, InterruptedException {
        boolean gameRunning = true;
        Path dir = Paths.get(System.getProperty("user.dir")); //Can manually change Directory as necessary

        //-----------------------------------------
        System.out.println(dir);
        //-----------------------------------------

        WatchService watchService = FileSystems.getDefault().newWatchService();

        dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

        Board gameBoard = new Board();

        while(gameRunning){
            WatchKey key = watchService.take();
            for (WatchEvent<?> event : key.pollEvents()) {
                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    if(isFilePresent(endFile)){
                        //Game is Over
                        String endfileContents = fileContents(endFile);
                        System.out.println("game over");
                        System.out.println(endfileContents);
                        gameRunning = false;
                    } else{
                        Path filePath = (Path) event.context();
                        String fileName = filePath.getFileName().toString();

                        if(fileName.equals(goFile)){
                            //TODO: Make a move here

                            //-----------------------------------------
                            System.out.println("GO File Detected!\n");
                            //-----------------------------------------

                            //Log opponents Move info here, make into a single func (used 2x)
                            String oppMove = fileContents(moveFile);
                            int[] oppCoords = coordSanitization(oppMove);
                            saveOpponentMove(gameBoard, oppCoords);

                            //Calculate Move
                            int[] moveVals = Minimax.getBestMove(gameBoard);
                            String ourMove = "dannydevito " + moveVals[0] + "," + moveVals[1] + " " + moveVals[2] + "," + moveVals[3];

                            //Write to moveFile to end turn
                            overwriteFile(moveFile, ourMove);

                        }
                        else if (fileName.equals(passFile)){

                            //Pass here, make empty move

                            //-----------------------------------------
                            System.out.println("PASS File Detected!\n");
                            //-----------------------------------------

                            String oppMove = fileContents(moveFile);
                            int[] oppCoords = coordSanitization(oppMove);
                            saveOpponentMove(gameBoard, oppCoords);

                            //Write Empty move to moveFile
                            String passMove = "dannydevito 0,0 0,0";
                            overwriteFile(moveFile, passMove);

                        }
                    }
                }
            }
            gameBoard.printBoard();
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
            return Files.readString(Path.of(fileName));
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }


    public static int[] coordSanitization(String move){
        int[] coordVals = new int[5];

        String coords = move.substring(move.length() - 7);
        String coord1 = coords.substring(0,3);
        String coord2 = coords.substring(4);

        String[] parts1 = coord1.split(",");
        String[] parts2 = coord2.split(",");

        int x1 = Integer.parseInt(parts1[0]);
        int y1 = Integer.parseInt(parts1[1]);
        int x2 = Integer.parseInt(parts2[0]);
        int y2 = Integer.parseInt(parts2[1]);

        if(x2-x1 < 0 || y2-y1 < 0){
            coordVals[0] = x2;
            coordVals[1] = y2;
            coordVals[2] = x1;
            coordVals[3] = y1;
        } else{
            coordVals[0] = x1;
            coordVals[1] = y1;
            coordVals[2] = x2;
            coordVals[3] = y2;
        }
        if(x2-x1 != 0){
            coordVals[4] = 0;
        } else {
            coordVals[4] = 1;
        }

        return coordVals;
    }


    public static void saveOpponentMove(Board board, int[] sanitizedCoords){
        board.updateLine(sanitizedCoords[0], sanitizedCoords[1], sanitizedCoords[4], -1);
    }

}
