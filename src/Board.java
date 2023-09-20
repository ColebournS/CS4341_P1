import java.util.ArrayList;
import java.util.Arrays;

public class Board {
    
    public Line[][] vs;
    public Line[][] hs;
    public Box[][] boxes;
    public int[] lastLine;

    public Board() {

        this.vs = new Line[10][9];
        this.hs = new Line[9][10];
        this.boxes = new Box[9][9];
        this.lastLine = null;

        initLines();
        initBoxes();

    }

    public Board(Board board) {
        this.vs = deepCopyLines(board.vs);
        this.hs = deepCopyLines(board.hs);
        this.boxes = deepCopyBoxes(board.boxes);
        if(board.lastLine != null){
            this.lastLine = Arrays.copyOf(board.lastLine, board.lastLine.length);
        } else {
            this.lastLine = null;
        }
    }

    public Board copy() {
        return new Board(this);
    }

    public Board copyBoard(){
        Board newBoard = new Board();

        System.out.println("copying lines");
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 10; j++) {
                boolean vertVal = this.vs[j][i].isComplete();
                newBoard.vs[j][i].setComplete(vertVal);

                boolean horizVal = this.hs[i][j].isComplete();
                newBoard.hs[i][j].setComplete(horizVal);

            }
        }
        System.out.println("lines copied");
        newBoard.initBoxes();
        newBoard.lastLine = this.lastLine;

        return newBoard;
    }

    private Line[][] deepCopyLines(Line[][] original) {
        if (original == null) {
            return null;
        }
        
        int numRows = original.length;
        Line[][] copy = new Line[numRows][];
        
        for (int i = 0; i < numRows; i++) {
            if (original[i] != null) {
                copy[i] = Arrays.copyOf(original[i], original[i].length);
            }
        }
        
        return copy;
    }

    private Box[][] deepCopyBoxes(Box[][] original) {
        if (original == null) {
            return null;
        }
        
        int numRows = original.length;
        Box[][] copy = new Box[numRows][];
        
        for (int i = 0; i < numRows; i++) {
            if (original[i] != null) {
                copy[i] = Arrays.copyOf(original[i], original[i].length);
            }
        }
        
        return copy;
    }

    public void initBoxes() {

        for(int i = 0; i <= 8; i++) {
            for(int j = 0; j <= 8; j++) {
                this.boxes[j][i] = new Box(this.hs[j][i + 1], this.vs[j + 1][i], this.hs[j][i], this.vs[j][i]);
            }
        }
    }

    public void initLines() {

        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 10; j++) {
                this.vs[j][i] = new Line();
                this.hs[i][j] = new Line();
            }
        }
    }

    public void printBoard(){
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                System.out.print(".");
                if(hs[i][j].isComplete()) System.out.print("---");
                else System.out.print("\t");
            }
            System.out.print("\n");
            if(i < 8){
                for(int h =0; h < 3; h++){
                    for(int horisontal=0; horisontal <9; horisontal++){
                        if(vs[i][horisontal].isComplete()) System.out.print("|\t");
                        else System.out.print(" \t");
                    }
                    System.out.print("\n");
                }
            }
        }
    }

    public void updateLine(int x, int y, int direction, int player){
        if(direction == 0){
            //update Horiz
            Line toUpdate = hs[x][y];
            toUpdate.setComplete(true);
            if(y < 9){
                //Check top box
                Box horizCheck = boxes[x][y];
                if(horizCheck.isComplete()){
                    horizCheck.setCompletedBy(player);
                }
            }
            if(y > 0){
                //Check bottom box
                Box vertCheck = boxes[x][y-1];
                if(vertCheck.isComplete()){
                    vertCheck.setCompletedBy(player);
                }
            }
        } else if(direction == 1){
            //update vert
            Line toUpdate = vs[x][y];
            toUpdate.setComplete(true);
            if(x < 8){
                //Check right box
                Box vertCheck = boxes[x][y];
                if(vertCheck.isComplete()){
                    vertCheck.setCompletedBy(player);
                }
            }
            if(x > 0){
                //Check left box
                Box vertCheck = boxes[x-1][y];
                if(vertCheck.isComplete()){
                    vertCheck.setCompletedBy(player);
                }
            }
        }
    }

    public void setLastLine(int[] lastLine) {
        this.lastLine = lastLine;
    }

    //Three int array: direction, x, y
    public ArrayList<int[]> getLegalMoves(){
        ArrayList<int[]> legalLines = new ArrayList<>();
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 10; j++) {
                if(!this.hs[i][j].isComplete()){
                    int[] myVals = new int[]{0,i,j};
                    legalLines.add(myVals);
                }

                if(!this.vs[j][i].isComplete()){
                    int[] myVals = new int[]{1,j,i};
                    legalLines.add(myVals);
                }
            }
        }
        return legalLines;
    }

}
