import java.util.*;

// Class representing the entire game. The board is represented as a 17x17 array, but only the 121 locations of the game
// board are actually used because of the star shape of the board.
public class Game {
    private final int ROWS = 17;
    private final int COLS = 17;
    public short[][] board;
//    private final short TOTALNODES = 121;
//    private final short TOTALHOMES = 6;
//    private final short PIECESPERPLAYER = 10;
    private final short EMPTYSPACE = -1;
    private final short PLAYERONE = 1;
    private final short PLAYERTWO = 2;
    private final short PLAYERTHREE = 3;
    private final short PLAYERFOUR = 4;
    private final short PLAYERFIVE = 5;
    private final short PLAYERSIX = 6;
    private final String HUMAN = "human";
    private final String AI = "ai";
    private String player1type;
    private String player2type;
    private Map<Short, AI> players;
    private int winner;

    public Game(String player1type, String player2type, int maxDepth) {
        board = new short[Player.ROWS][Player.COLS];
        this.player1type = player1type;
        this.player2type = player2type;
        players = new HashMap<>();
        if (player1type.equals(AI))
            players.put((short)1, new AI((short)maxDepth));
        if (player2type.equals(AI))
            players.put((short)2, new AI((short)maxDepth));
        initBoard();

    }

    // the board is not orthogonal
    // the x axis is horizontal, but the y axis is rotated about 30 degrees clockwise from being perpendicular with the x axis
    public void initBoard() {
        for (short[] row : board)
            Arrays.fill(row, Short.MIN_VALUE);
        initTopAndBottom();
        initTopMiddle();
        initBottomMiddle();
        for (int col = 4; col < 13; col++)
            board[8][col] = EMPTYSPACE;
    }

    private void initTopAndBottom() {
        int coloffset = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (row <= 3) {
                    if (col >= 4 && col <= 4 + coloffset)
                        board[row][col] = PLAYERONE;    // initialize player 1's home
                    else
                        board[row][col] = Short.MIN_VALUE;   // initialize the rest of the board
                } else if ( row >= ROWS - 4) {
                    if (col >= 9 && col < 9 + coloffset)
                        board[row][col] = PLAYERTWO;    // initialize player 2's home
                    else
                        board[row][col] = Short.MIN_VALUE;   // initialize the rest of the board
                }
            }
            if (row < 4) // for top section of board
                coloffset++;
            else if (row >= 13) // for bottom section of board
                coloffset--;
        }
    }

    private void initTopMiddle() {
        int coloffset = 0;
        for (int row = 4; row < 8; row++) {
            for (int col = 0; col < COLS; col++) {
                if (col < 4 && col >= coloffset)
                    board[row][col] = PLAYERTHREE;   // initialize player 3's home
                else if (col > 8 + coloffset && col < 13)
                    board[row][col] = PLAYERFOUR;   // initialize player 4's home
                else if (col >= 4 && col <= 8 + coloffset)
                    board[row][col] = EMPTYSPACE;
            }
            coloffset++;
        }
    }

    private void initBottomMiddle() {
        int coloffset = 0;
        for (int row = 9; row < 13; row++) {
            for (int col = 4; col < COLS; col++) {
                if (col < 8 && col <= 4 + coloffset)
                    board[row][col] = PLAYERFIVE;   // initialize player 5's home
                else if (col > 12 && col <= 13 + coloffset)
                    board[row][col] = PLAYERSIX;   // initialize player 6's home
                else if (col > 4 + coloffset && col <= 12 + coloffset)
                    board[row][col] = EMPTYSPACE;
            }
            coloffset++;
        }
    }

    // Creates a string representation of the board with the x and y axes being orthogonal to each other
    public String toStringOrthogonal() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                    if (board[row][col] == Short.MIN_VALUE)
                        sb.append(" ");
                    else
                        sb.append(board[row][col]);
                    sb.append(", ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public String toString() {
        int offset = ROWS;
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < ROWS;  row++) {
            for (int col = 0; col < COLS + offset; col++) {
                if (col > offset)
                    sb.append(",");
                if (col > offset) {
                    if (board[row][col - offset] == Short.MIN_VALUE)
                        sb.append(" ");
                    else
                        sb.append(board[row][col - offset]);

                }
                else
                    sb.append("  ,  ");
            }
            offset--;
            sb.append("\n");
        }
        return sb.toString();
    }

    // Intended for visualization purposes
    // Prints out the 2D space as it actually looks, with the y axis being 60 degrees offset from the x axis
    // Prints out the coordinates of each location in the space
    public void printCoordinateSpace() {
        int offset = ROWS;
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < ROWS;  row++) {
            for (int col = 0; col < COLS + offset; col++) {
                if (col > offset)
                    sb.append(" , ");
                if (col >= offset)
                    sb.append("(" + row + "," + (col - offset) + ")");
                else
                    sb.append(" , ");
            }
            offset--;
            sb.append("\n");
        }
        System.out.println(sb.toString());
    }

    public int getWinner() {
        return winner;
    }

    // checks whether either player has won, if so, returns true, otherwise false
    public boolean isGameOver() {
        if (Player.isWinState(board, 1)) {
            winner = 1;
            return true;
        }
        if (Player.isWinState(board, 2)) {
            winner = 2;
            return true;
        }
        if (Player.isWinState(board, 3)) {
            winner = 3;
            return true;
        }
        if (Player.isWinState(board, 4)) {
            winner = 4;
            return true;
        }
        if (Player.isWinState(board, 5)) {
            winner = 5;
            return true;
        }
        if (Player.isWinState(board,6)) {
            winner = 6;
            return true;
        }
        return false;
    }


    // returns a string representing all the pieces that can be moved by the current player
    public String moveablePiecesToString(int playerNum) {
        List<Coordinate> pieces = Player.getPlayerPieces(board, playerNum);
        Set<Coordinate> moveablePieces = new TreeSet<>();
        for (int i = 0; i < pieces.size(); i++) {
            Set<Coordinate> moves = Player.getAvailableMoves(board, new Coordinate(pieces.get(i).getRow(), pieces.get(i).getCol()));
            if (!moves.isEmpty()) {
                moveablePieces.add(pieces.get(i));
            }
        }

        StringBuilder sb = new StringBuilder();
        for (Coordinate c : moveablePieces) {
            sb.append("(");
            sb.append(c.getRow());
            sb.append(",");
            sb.append(c.getCol());
            sb.append(")");
            sb.append("\n");
        }
        return sb.toString();
    }

    // returns string representing all the locations that a given piece can be moved by the current player
    public String movesToString(Set<Coordinate> moves) {
        StringBuilder sb = new StringBuilder();
        for (Coordinate m : moves) {
            sb.append("(");
            sb.append(m.getRow());
            sb.append(",");
            sb.append(m.getCol());
            sb.append(")");
            sb.append("\n");
        }
        return sb.toString();
    }

    // checks whether the user input numbers to indicate their move is valid and the location they chose is valid
    private boolean isValidInput(String input) {
        String[] inputs = input.split(" ");
        int[] coordinates = new int[2];
        if (inputs.length == 2) {
            for (int i = 0; i < inputs.length; i++) {
                try {
                    coordinates[i] = Integer.parseInt(inputs[i]);
                } catch (NumberFormatException e) {
                    System.out.println("Input must be numbers");
                    return false;
                }
            }
            return Player.isValidSquare(board, coordinates[0], coordinates[1]);
        }
        return false;
    }

    // outputs the user's possible moves and takes in their choice of move
    public void chooseHumanMove(short playerNum) {
        System.out.println(toStringOrthogonal());
        System.out.println("PLAYER " + playerNum + " Choose which piece to move:");
        System.out.println(moveablePiecesToString(playerNum));

        Scanner in = new Scanner(System.in);
        String input = "";
        while (!isValidInput(input)) {
            System.out.println("\nInput coordinates (in this format [row col])");
            input = in.nextLine().trim();
            String[] inputs = input.split(" ");
            short pieceRow = (short)Integer.parseInt(inputs[0]);
            short pieceCol = (short)Integer.parseInt(inputs[1]);
            if (pieceRow >= 0 && pieceRow < ROWS)
                if (pieceCol >= 0 && pieceCol < COLS)
                    if (board[pieceRow][pieceCol] == playerNum) {
                        System.out.println(toStringOrthogonal());
                        System.out.println("Choose where to move (" + pieceRow + "," + pieceCol + "):");
                        input = "";
                        Set<Coordinate> moves = new TreeSet<>(Player.getAvailableMoves(board, new Coordinate(pieceRow, pieceCol)));
                        if (!moves.isEmpty()) {
                            System.out.println(movesToString(moves));
                            while (!isValidInput(input)) {
                                System.out.println("\nInput coordinates (in this format [row col])");
                                input = in.nextLine().trim();
                                inputs = input.split(" ");
                                int row = Integer.parseInt(inputs[0]);
                                int col = Integer.parseInt(inputs[1]);
                                int count = 0;
                                for (Coordinate move : moves) {
                                    count++;
                                    if (move.getRow() == row && move.getCol() == col) {
                                        board[row][col] = playerNum;
                                        board[pieceRow][pieceCol] = EMPTYSPACE;
                                        break;
                                    }
                                    if (count == moves.size())
                                        input = "";
                                }
                            }
                        } else {
                            input = "";
                            System.out.println("That piece can't be moved");
                        }
                    }
        }
    }

    // the AI chooses a move using alpha-beta pruning
    public void chooseAIMove(short playerNum) {
//        long start = System.currentTimeMillis();
        List<Coordinate> m = players.get(playerNum).getAlphaBetaMove(board, playerNum);
        if (!m.isEmpty()) {
            Coordinate pieceToMove = m.get(0);
            Coordinate action = m.get(1);
            board[action.getRow()][action.getCol()] = playerNum;
            board[pieceToMove.getRow()][pieceToMove.getCol()] = EMPTYSPACE;
            System.out.println("AI PLAYER " + playerNum + " moved (" + pieceToMove.getRow() + ","
                    + pieceToMove.getCol() + ") to (" + action.getRow() + "," + action.getCol() + ")");
        }
        else {
            System.out.println("No move chosen :(");
        }

//        long end = System.currentTimeMillis();
//        System.out.println("Runtime: " + (end - start) + " ms");
        System.out.println(toStringOrthogonal());
    }

    // each player chooses their move on their turn
    public void chooseMove(short playerNum) {
        if (playerNum == PLAYERONE) {
            if (player1type.equals(HUMAN)) {
                System.out.println("PLAYER 1 turn (HUMAN):");
                chooseHumanMove(playerNum);
            }
            else if (player1type.equals(AI)) {
                System.out.println("PLAYER 1 turn (AI):");
                chooseAIMove(playerNum);
            }
        }
        else if (playerNum == PLAYERTWO) {
            if (player2type.equals(HUMAN)) {
                System.out.println("PLAYER 2 turn (HUMAN):");
                chooseHumanMove(playerNum);
            }
            else if (player2type.equals(AI)) {
                System.out.println("PLAYER 2 turn (AI):");
                chooseAIMove(playerNum);
            }
        }
    }
}
