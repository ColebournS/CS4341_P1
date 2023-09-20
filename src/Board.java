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
        
        initBoxes();
        initLines();
    }

    public Board(Board board){
        this.vs = board.vs;
        this.hs = board.hs;
        this.boxes = board.boxes;
        this.lastLine = board.lastLine;
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

    public void setLastLine(int[] lastLine) {
        this.lastLine = lastLine;
    }

    public void printBoard(){
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                System.out.print(".");
                if(this.hs[i][j].isComplete()) System.out.print("-------");
                else System.out.print("\t");
            }
            System.out.print("\n");
            if(i < 8){
                for(int h =0; h < 3; h++){
                    for(int horisontal=0; horisontal <9; horisontal++){
                        if(this.vs[i][horisontal].isComplete()) System.out.print("|\t");
                        else System.out.print(" \t");
                    }
                    System.out.print("\n");
                }
            }
        }
    }

    public Board copy() {
        return new Board(this);
    }
}
