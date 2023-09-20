import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Minimax {

    private Map<Board, Integer> transpositionTable;

    public Minimax() {
        transpositionTable = new HashMap<>();
    }

    public int[] getBestMove(Board b) {
        LinkedList<Line> moves = getMoves(b);
        int[] bestMove = null;
        int bestValue = Integer.MIN_VALUE;

        for (Line move : moves) {
            // Create a copy of the current board and apply the move
            Board childBoard = b.copy();
            move.setComplete(true); 
            int value = search(childBoard, 0, false, Integer.MIN_VALUE, Integer.MAX_VALUE);

            if (value > bestValue) {
                bestValue = value;
                bestMove = move.getCoordinates(); //TODO: we need a way to find the coordinates of a line
            }
        }
        return bestMove;
    }


    public int search(Board b, int depth, boolean isMaxing, int alpha, int beta) {
        // Check if the board is already in the transposition table
        if (transpositionTable.containsKey(b)) {
            return transpositionTable.get(b);
        }

        LinkedList<Board> children = getChildren(b);
        if (children.isEmpty()) {
            int eval = evaluateBoard(b);
            transpositionTable.put(b, eval); // Store the board and its value in the table
            return eval;
        }

        if (isMaxing) {
            int curBest = Integer.MIN_VALUE;
            for (Board child : children) {
                int val = search(child, depth + 1, false, alpha, beta);
                curBest = Math.max(curBest, val);
                alpha = Math.max(curBest, alpha);
                if (alpha >= beta) {
                    break;
                }
            }
            transpositionTable.put(b, curBest); // Store the board and its value in the table
            return curBest;
        } else {
            int curBest = Integer.MAX_VALUE;
            for (Board child : children) {
                int val = search(child, depth + 1, true, alpha, beta);
                curBest = Math.min(curBest, val);
                beta = Math.min(curBest, beta);
                if (beta <= alpha) {
                    break;
                }
            }
            transpositionTable.put(b, curBest); // Store the board and its value in the table
            return curBest;
        }
    }

    public int evaluateBoard(Board b) {

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

    public LinkedList<Board> getChildren(Board b) {
        LinkedList<Board> children = new LinkedList<Board>();
        for(int i = 0; i <= 9; i++) {
            for(int j = 0; j <= 10; j++) {
                if(!b.hs[j][i].isComplete()) {
                    children.add(b);
                    children.getLast().hs[j][i].setComplete(true);
                }
                if(!b.vs[i][j].isComplete()) {
                    children.add(b);
                    children.getLast().vs[i][j].setComplete(true);
                }
            }
        }
        return children;

    }
}
