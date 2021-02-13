import java.util.*;

// Class that
public class Player {

    public static final int ROWS = 17;
    public static final int COLS = 17;
    public static final int PIECESPERPLAYER = 10;
    public static final int EMPTYSPACE = -1;

    public Player() {
    }

    // check if the player has won the game
    public static boolean isWinState(short[][] board, int playerNum) {
        switch (playerNum) {
            case 1: {   // check if player 1 won
                int count = 0;
                int coloffset = 0;
                for (int row = 13; row < ROWS; row++) {
                    for (int col = 9; col < COLS - 4; col++) {
                        if (col + coloffset < COLS - 4) {
                            if (board[row][col] == 0) {
                                count++;
                            }
                        }
                    }
                    coloffset++;
                }
                return count == PIECESPERPLAYER;
            }
            case 2: {   // check if player 2 won
                int count = 0;
                int coloffset = 0;
                for (int row = 0; row < 4; row++) {
                    for (int col = 4; col < 8; col++) {
                        if (col <= coloffset) {
                            if (board[row][col] == 1) {
                                count++;
                            }
                        }
                    }
                    coloffset++;
                }
                return count == PIECESPERPLAYER;
            }
        }
        return false;
    }


    // is this space taken, if so, a piece can jump over it
    public static boolean isJumpableSquare(short[][] board, int row, int col) {
        return isValidSquare(board, row, col) && !isSquareAvailable(board, row, col);
    }

    // recursively finds all the jump moves that can be done starting from a given location
    public static Set<Coordinate> getAvailableJumps(short[][] board, Coordinate move, Set<Coordinate> tried) {
        short row = move.getRow();
        short col = move.getCol();
        Set<Coordinate> reached = new TreeSet<>();

        if (isValidSquare(board, row, col) && isSquareAvailable(board, row, col)
                && !tried.contains(move)) {
            reached.add(move);
            tried.add(move);
        }
        else {
            tried.add(move);
            return reached;
        }

        Coordinate jump = new Coordinate((short) (row - 2), col);
        if (isJumpableSquare(board,row - 1, col) && tried.contains(jump)) {
            Set<Coordinate> moves = getAvailableJumps(board, jump, tried);
            reached.addAll(moves);
            tried.addAll(moves);
        }
        jump = new Coordinate((short) (row + 2), col);
        if (isJumpableSquare(board, row + 1, col) && tried.contains(jump)) {
            Set<Coordinate> moves = getAvailableJumps(board, jump, tried);
            reached.addAll(moves);
            tried.addAll(moves);
        }
        jump = new Coordinate(row, (short) (col - 2));
        if (isJumpableSquare(board, row, col - 1) && tried.contains(jump)) {
            Set<Coordinate> moves = getAvailableJumps(board, jump, tried);
            tried.addAll(moves);
            reached.addAll(moves);
        }
        jump = new Coordinate(row, (short) (col + 2));
        if (isJumpableSquare(board, row, col + 1) && tried.contains(jump)) {
            Set<Coordinate> moves = getAvailableJumps(board, jump, tried);
            tried.addAll(moves);
            reached.addAll(moves);
        }
        jump = new Coordinate((short)(row - 2), (short)(col - 2));
        if (isJumpableSquare(board, row - 1, col - 1) && tried.contains(jump)) {
            Set<Coordinate> moves = getAvailableJumps(board, jump, tried);
            tried.addAll(moves);
            reached.addAll(moves);
        }
        jump = new Coordinate((short)(row + 2), (short)(col + 2));
        if (isJumpableSquare(board, row + 1, col + 1) && tried.contains(jump)) {
            Set<Coordinate> moves = getAvailableJumps(board, jump, tried);
            tried.addAll(moves);
            reached.addAll(moves);
        }

        return reached;
    }

    // returns a list of locations where the player's pieces are
    public static List<Coordinate> getPlayerPieces(short[][] board, int playerNum) {
        List<Coordinate> coordinates = new ArrayList<>();
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (board[row][col] == playerNum)
                    coordinates.add(new Coordinate((short)row, (short)col));
            }
        }
        return coordinates;
    }

    // calculates the Manhattan distance between two locations
    public static int manhattan(Coordinate start, Coordinate end) {
        return Math.abs(start.getRow() - end.getRow()) + Math.abs(start.getCol() - end.getCol());
    }

    // finds all the adjacent and jumping moves that can be done from a given location
    public static Set<Coordinate> getAvailableMoves(short[][] board, Coordinate c) {
        Set<Coordinate> moves = new TreeSet<>();

        int row = c.getRow();
        int col = c.getCol();

        boolean canJumpTopRowNeighbor = true;
        boolean canJumpBottomRowNeighbor = true;
        boolean canJumpLeftColNeighbor = true;
        boolean canJumpRightColNeighbor = true;
        boolean canJumpTopLeftNeighbor = true;
        boolean canJumpBottomRightNeighbor = true;

        // check for adjacent moves
        if (isValidSquare(board, row - 1, col)) {
            if (isSquareAvailable(board, row - 1, col)) {   // up a row
                canJumpTopRowNeighbor = false;
                moves.add(new Coordinate((short)(row - 1), (short)col));
            }
            else if (isValidSquare(board, row - 2, col))
                if (!isSquareAvailable(board, row - 2, col)) {
                    canJumpTopRowNeighbor = false;
                }
        }
        else {
            canJumpTopRowNeighbor = false;
        }
        if (isValidSquare(board, row + 1, col)) {
            if (isSquareAvailable(board, row + 1, col)) {   // down a row
                canJumpBottomRowNeighbor = false;
                moves.add(new Coordinate((short)(row + 1), (short)col));
            }
            else if (isValidSquare(board, row + 2, col))
                if (!isSquareAvailable(board, row + 2, col)) {
                    canJumpBottomRowNeighbor = false;
                }
        }
        else {
            canJumpBottomRowNeighbor = false;
        }
        if (isValidSquare(board, row, col - 1)) {
            if (isSquareAvailable(board, row, col - 1)) {   // back a col/left
                canJumpLeftColNeighbor = false;
                moves.add(new Coordinate((short)row, (short)(col - 1)));
            }
            else if (isValidSquare(board, row, col - 2))
                if (!isSquareAvailable(board, row, col - 2)) {
                    canJumpLeftColNeighbor = false;
                }
        }
        else {
            canJumpLeftColNeighbor = false;
        }
        if (isValidSquare(board, row, col + 1)) {
            if (isSquareAvailable(board, row, col + 1)) {   // forward a col/right
                canJumpRightColNeighbor = false;
                moves.add(new Coordinate((short)row, (short)(col + 1)));
            }
            else if (isValidSquare(board, row, col + 2))
                if (!isSquareAvailable(board, row, col + 2)) {
                    canJumpRightColNeighbor = false;
                }
        }
        else {
            canJumpRightColNeighbor = false;
        }
        if (isValidSquare(board, row - 1, col - 1)) {
            if (isSquareAvailable(board, row - 1, col - 1)) {   // up and left a row and col
                canJumpTopLeftNeighbor = false;
                moves.add(new Coordinate((short)(row - 1), (short)(col - 1)));
            }
            else if (isValidSquare(board, row - 2, col - 2))
                if (!isSquareAvailable(board, row - 2, col - 2)) {
                    canJumpTopLeftNeighbor = false;
                }
        }
        else {
            canJumpTopLeftNeighbor = false;
        }
        if (isValidSquare(board, row + 1, col + 1)) {
            if (isSquareAvailable(board, row + 1, col + 1)) {   // down and right a row and col
                canJumpBottomRightNeighbor = false;
                moves.add(new Coordinate((short)(row + 1), (short)(col + 1)));
            }
            else if (isValidSquare(board, row + 2, col + 2))
                if (!isSquareAvailable(board, row + 2, col + 2)) {
                    canJumpBottomRightNeighbor = false;
                }
        }
        else {
            canJumpBottomRightNeighbor = false;
        }

        Set<Coordinate> attempted = new TreeSet<>();

        // check for available jumping moves
        if (canJumpTopRowNeighbor) {
            Coordinate move = new Coordinate((short)(row - 2), (short)col);
            moves.addAll(getAvailableJumps(board, move, attempted));
            attempted.addAll(moves);
        }
        if (canJumpBottomRowNeighbor) {
            Coordinate move = new Coordinate((short)(row + 2), (short)col);
            moves.addAll(getAvailableJumps(board, move, attempted));
        }
        if (canJumpLeftColNeighbor) {
            Coordinate move = new Coordinate((short)row, (short)(col - 2));
            moves.addAll(getAvailableJumps(board, move, attempted));
            attempted.addAll(moves);
        }
        if (canJumpRightColNeighbor) {
            Coordinate move = new Coordinate((short)row, (short)(col + 2));
            moves.addAll(getAvailableJumps(board, move, attempted));
            attempted.addAll(moves);
        }
        if (canJumpTopLeftNeighbor) {
            Coordinate move = new Coordinate((short)(row - 2), (short)(col - 2));
            moves.addAll(getAvailableJumps(board, move, attempted));
            attempted.addAll(moves);
        }
        if (canJumpBottomRightNeighbor) {
            Coordinate move = new Coordinate((short)(row + 2), (short)(col + 2));
            moves.addAll(getAvailableJumps(board, move, attempted));
            attempted.addAll(moves);
        }

        return moves;
    }

    // some squares in the board are not part of the game
    public static boolean isValidSquare(short[][] board, int row, int col) {
        if (row > -1 && row < ROWS)
            if (col > -1 && col < COLS)
                return board[row][col] != Short.MIN_VALUE;
        return false;
    }

    // check a square to see if it is vacant
    public static boolean isSquareAvailable(short[][] board, int row, int col) {
        return board[row][col] == EMPTYSPACE;
    }

    // update the board with a give move
    public static short[][] makeMove(short[][] board, Coordinate piece, Coordinate move, short playerNum) {
        short[][] newBoard = new short[ROWS][COLS];
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (row == move.getRow() && col == move.getCol()) {
                    if (board[row][col] == EMPTYSPACE) {
                        newBoard[row][col] = playerNum;
                    } else {
                        System.out.println("Invalid move from (" + piece.getRow() + "," + piece.getCol() + ") to (" + move.getRow() + "," + move.getCol() + ")");
                        newBoard[row][col] = board[row][col];
                    }
                }
                else {
                    if (row == piece.getRow() && col == piece.getCol())
                        newBoard[row][col] = EMPTYSPACE;
                    else
                        newBoard[row][col] = board[row][col];
                }
            }
        }
        return newBoard;
    }

}
