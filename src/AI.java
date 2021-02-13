import java.util.*;

// Class representing the AI implementation, uses alpha-beta pruning to choose moves
public class AI {
    private short maxDepth;

    public AI(short maxDepth) {
        this.maxDepth = maxDepth;
    }

    // returns a list of coordinates, the first is the piece to move, the second is where to move that piece
    public List<Coordinate> getAlphaBetaMove(short[][] board, short playerNum) {
        return bestAlphaBetaMove(board, Short.MIN_VALUE, Short.MAX_VALUE, (short)0, playerNum);
    }

    // essentially a maxvalue function, but keeps track of the action as well
    public List<Coordinate> bestAlphaBetaMove(short[][] board, short alpha, short beta, short depth, short playerNum) {
        List<Coordinate> playerPieces = Player.getPlayerPieces(board, playerNum);
        short v = Short.MIN_VALUE;
        List<Coordinate> bestChoice = new ArrayList<>();
        for (Coordinate c : playerPieces) {
            Set<Coordinate> moves = Player.getAvailableMoves(board, c);
            for (Coordinate m : moves) {
                short[][] newBoard = Player.makeMove(board, c, m, playerNum);
                short val = (short) Math.max(v, minValue(newBoard, alpha, beta, (short) (depth + 1), playerNum));
                if (val > v)
                    bestChoice.clear();
                    bestChoice.add(c);
                    bestChoice.add(m);
                v = val;
                if (v >= beta) {
                    bestChoice.clear();
                    bestChoice.add(c);
                    bestChoice.add(m);
                    return bestChoice;
                }
                alpha = (short) Math.max(alpha, v);
            }
        }
        return bestChoice;
    }

    public short minValue(short[][] board, short alpha, short beta, short depth, short playerNum) {
        List<Coordinate> playerPieces = Player.getPlayerPieces(board, playerNum);
        if (depth >= maxDepth || Player.isWinState(board, playerNum))
            return (short) evaluationFunction(board, playerNum, depth);
        short v = Short.MAX_VALUE;
        for (Coordinate c : playerPieces) {
            Set<Coordinate> moves = Player.getAvailableMoves(board, c);
            for (Coordinate m : moves) {
                short[][] newBoard = Player.makeMove(board, c, m, playerNum);
                v = (short) Math.min(v, maxValue(newBoard, alpha, beta, (short)(depth + 1), playerNum));
                if (v <= alpha) {
                    return v;
                }
                beta = (short) Math.min(beta, v);
            }
        }
        return v;
    }

    public short maxValue(short[][] board, short alpha, short beta, short depth, short playerNum) {
        List<Coordinate> playerPieces = Player.getPlayerPieces(board, playerNum);
        if (depth >= maxDepth || Player.isWinState(board, playerNum))
            return (short) evaluationFunction(board, playerNum, depth);
        int v = Short.MIN_VALUE;
        for (Coordinate c : playerPieces) {
            Set<Coordinate> moves = Player.getAvailableMoves(board, c);
            for (Coordinate m : moves) {
                short[][] newBoard = Player.makeMove(board, c, m, playerNum);
                v = Math.max(v, minValue(newBoard, alpha, beta, (short)(depth + 1), playerNum));
                if (v >= beta) {
                    return (short) v;
                }
                alpha = (short) Math.max(alpha, v);
            }
        }
        return (short) v;
    }

    public int evaluationFunction(short[][] board, int playerNum, int depth) {
        int curPlayerScore = calcDistToGoal(board, playerNum, depth);
        int curPlayerHomePiecesScore = countPiecesInHome(board, playerNum);
//        curPlayerScore += calcNumOfJumps(board, playerNum);

        int opponentNum = -1;
        if (playerNum == 2)
            opponentNum = 1;
        else
            opponentNum = 2;
        int opponentScore = calcDistToGoal(board, playerNum, depth);
        int opponentPlayerHomePiecesScore = countPiecesInHome(board, opponentNum);
//        opponentScore += calcNumOfJumps(board, opponentNum);
        int curPlayerDistToMiddle = calcDistToMiddle(board, playerNum);
        int opponentDistToMiddle = calcDistToMiddle(board, opponentNum);
        curPlayerScore += curPlayerDistToMiddle;
        opponentScore += opponentDistToMiddle;
        curPlayerScore += curPlayerHomePiecesScore;
        opponentScore += opponentPlayerHomePiecesScore;

        if (playerNum == 1)
            opponentNum = 2;
        else
            opponentNum = 1;
        // measuring how far each action moves a piece, the farther the better
//        List<Coordinate> pieces = Player.getPlayerPieces(board, playerNum);
//        for (Coordinate p : pieces) {
//            Set<Coordinate> moves = Player.getAvailableMoves(board, p);
//            for (Coordinate c : moves)
//                curPlayerScore += Player.manhattan(p, c);
//        }
//        int opponentNum = 0;
//        pieces = Player.getPlayerPieces(board, opponentNum);
//        for (Coordinate p : pieces) {
//            Set<Coordinate> moves = Player.getAvailableMoves(board, p);
//            for (Coordinate c : moves)
//                opponentPlayerScore += Player.manhattan(p, c);
//        }
        return curPlayerScore - opponentScore;
    }

    // calculates the utility based on how many times the pieces can jump
    public int calcNumOfJumps(short[][] board, int playerNum) {
        List<Coordinate> pieces = Player.getPlayerPieces(board, playerNum);
        Map<Coordinate, Set<Coordinate>> moveMap = new HashMap<>();
        for (Coordinate p : pieces) {
            moveMap.put(p, Player.getAvailableMoves(board, p));
        }

        return countJumps(moveMap);
    }

    // count how many times each move makes the pieces jump
    public int countJumps(Map<Coordinate, Set<Coordinate>> moves) {
        int count = 0;
        for (Map.Entry<Coordinate, Set<Coordinate>> entry : moves.entrySet()) {
            Coordinate start = entry.getKey();
            for (Coordinate m : entry.getValue()) {
                count += Player.manhattan(start, m);
            }
        }
        return count;
    }

    // the closer the pieces are to the middle of the board the better
    public int calcDistToMiddle(short[][] board, int playerNum) {
        List<Coordinate> pieces = Player.getPlayerPieces(board, playerNum);
        int score = 0;
        for (Coordinate piece : pieces) {
            int col = piece.getCol();
            int centercol = 6;
            for (int i = 5; i < 13; i++) {
                if (i % 2 == 0) {
                    score += 6 - Math.abs(col - centercol);
                } else {
                    score += 6 - Math.floor(Math.abs(col - (centercol + 0.5)));
                    centercol++;
                }
            }
        }
        return score;
    }

    // measure how far each piece is from the goal area
    public int calcDistToGoal(short[][] board, int playerNum, int depth) {
        List<Coordinate> pieces = Player.getPlayerPieces(board, playerNum);

        int score = 0;
        for (Coordinate piece : pieces) {
            score += (18 - minDistToGoalArea(board, piece, playerNum)) * depth;
        }
        return score;
    }

    // lower the utility score for each piece still in the home area
    public int countPiecesInHome(short[][] board, int playerNum) {
        int count = 0;
        List<Coordinate> pieces = Player.getPlayerPieces(board, playerNum);
        switch (playerNum) {
            case 1: {
                for (Coordinate p : pieces) {
                    for (int row = 0; row < 4; row++) {
                        for (int col = 4; col < 8; col++) {
                            if (Player.isValidSquare(board, row, col)) {
                                if (p.getRow() == row)
                                    if (p.getCol() == col)
                                        count++;
                            }
                        }
                    }
                }
                break;
            }
            case 2: {
                for (Coordinate p : pieces) {
                    for (int row = 13; row < 17; row++) {
                        for (int col = 9; col < 13; col++) {
                            if (Player.isValidSquare(board, row, col)) {
                                if (p.getRow() == row)
                                    if (p.getCol() == col)
                                        count++;
                            }
                        }
                    }
                }
                break;
            }
        }
        return count * -20;
    }

    // measure how far a piece is from the player's goal area
    public int minDistToGoalArea(short[][] board, Coordinate piece, int playerNum) {
        int min = Short.MAX_VALUE;
        switch (playerNum) {
            case 1: {
                for (int row = 13; row < 17; row++) {
                    for (int col = 9; col < 13; col++) {
                        if (Player.isValidSquare(board, row, col)) {
                            int manhattanDist = Math.abs(piece.getRow() - row) + Math.abs(piece.getCol() - col);
                            if (manhattanDist < min)
                                min = manhattanDist;
                        }
                    }
                }
                return min;
            }
            case 2: {
                for (int row = 0; row < 4; row++) {
                    for (int col = 4; col < 8; col++) {
                        if (Player.isValidSquare(board, row, col)) {
                            int manhattanDist = Math.abs(piece.getRow() - row) + Math.abs(piece.getCol() - col);
                            if (manhattanDist < min)
                                min = manhattanDist;
                        }
                    }
                }
                return min;
            }
        }
        return 0;
    }
}
