import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Minimax {
    
    public Minimax() {};

    public static int[] getBestMove(Board b) {

        Board searchBoard = b.copyBoard();
        int[] bestMove = search(searchBoard, 0, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
        int[] coords = new int[5];
        coords[0] = bestMove[2];
        coords[1] = bestMove[3];
        if(bestMove[1] == 0) {
            coords[2] = coords[0] + 1;
            coords[3] = coords[1];
        } else {
            coords[2] = coords[0];
            coords[3] = coords[1] + 1;
        }

        if(coords[0] - coords[2] != 0){
            coords[4] = 0;
        } else{
            coords[4] = 1;
        }

        return coords;
    }

    public static int[] search(Board b, int depth, boolean isMaxing, int alpha, int beta) {

        int[] curLine = b.lastLine;
        if(curLine == null){
            curLine = b.getLegalMoves().get(0);
        }

        LinkedList<Board> children = getChildren(b);
        if(children.isEmpty()) {
            return new int[]{evaluateBoard(b), curLine[0], curLine[1], curLine[2]};
        }

        if(isMaxing) {
            int[] bestMove = new int[]{-999999, curLine[0], curLine[1], curLine[2]};
            for(Board child : children) {
                int[] aMove = search(child, depth + 1, false, alpha, beta);
                bestMove[0] = Math.max(aMove[0], bestMove[0]);
                alpha = Math.max(bestMove[0], alpha);
                if(alpha >= beta) {
                    break;
                }
            }
            return bestMove;
        } else {
            int[] bestMove = new int[]{999999, curLine[0], curLine[1], curLine[2]};
            for(Board child : children) {
                int[] aMove = search(child, depth + 1, true, alpha, beta);
                bestMove[0] = Math.min(aMove[0], bestMove[0]);
                beta = Math.min(bestMove[0], beta);
                if(beta <= alpha) {
                    break;
                }
            }
            return bestMove;
        }
    }

    public static int evaluateBoard(Board b) {

        int sum = 0;
        for(int i = 0; i < 9; i++) {
            for(Box aBox: b.boxes[i]) {
                sum = sum + aBox.completedBy;
                if(aBox.isOneLineAway()) {
                    sum = sum - 1;
                }
            }
        }
        return sum;
    }

    public LinkedList<Line> getMoves(Board b) {
        LinkedList<Line> moves = new LinkedList<Line>();
        for(int i = 0; i <= 9; i++) {
            for(int j = 0; j <= 10; j++) {
                if(!b.hs[j][i].isComplete()) {
                    moves.add(b.hs[j][i]);
                }
                if(!b.vs[i][j].isComplete()) {
                    moves.add(b.vs[i][j]);
                }
            }
        }
        return moves;
    }


    public static LinkedList<Board> getChildren(Board b) {
        LinkedList<Board> children = new LinkedList<Board>();
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 10; j++) {
                if(!b.vs[j][i].isComplete()) {
                    children.add(b.copy());
                    children.getLast().vs[j][i].setComplete(true);
                    children.getLast().setLastLine(new int[]{0, j, i});
                }
                if(!b.hs[i][j].isComplete()) {
                    children.add(b.copy());
                    children.getLast().hs[i][j].setComplete(true);
                    children.getLast().setLastLine(new int[]{1, i, j});
                }
            }
        }
        // Sort the children list based on the evaluation scores in descending order.
        Collections.sort(children, Comparator.comparingDouble(child -> -evaluateBoard(child)));

        // Return the top 5 children or all if there are fewer than 5.
        return new LinkedList<>(children.subList(0, Math.min(5, children.size())));
    }
}
