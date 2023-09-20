import java.util.ArrayList;

public class Board {
    
    public Line[][] vs;
    public Line[][] hs;
    public Box[][] boxes;

    public Board() {

        this.vs = new Line[10][9];
        this.hs = new Line[9][10];
        this.boxes = new Box[9][9];
        
        initBoxes();
        initLines();
    }

    public void initBoxes() {

        for(int i = 0; i <= 8; i++) {
            for(int j = 0; j <= 8; j++) {
                this.boxes[j][i] = new Box(this.hs[j][i + 1], this.vs[j + 1][i], this.hs[j][i], this.vs[j][i]);
            }
        }
    }

    public void initLines() {

        for(int i = 0; i <= 9; i++) {
            for(int j = 0; j <= 10; j++) {
                this.hs[j][i] = new Line();
                this.vs[i][j] = new Line();
            }
        }
    }

    public ArrayList<Line> getLegalMoves(){
        ArrayList<Line> legalLines = new ArrayList<>();
        for(int i = 0; i <= 9; i++) {
            for(int j = 0; j <= 10; j++) {
                if(!this.hs[j][i].isComplete()) legalLines.add(this.hs[j][i]);

                if(!this.vs[i][j].isComplete()) legalLines.add(this.vs[i][j]);
            }
        }
        return legalLines;
    }


}
