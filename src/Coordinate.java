// Class representing a location on the board
public class Coordinate implements Comparable<Coordinate> {
    private short row;
    private short col;

    public Coordinate(short row, short col) {
        this.row = row;
        this.col = col;
    }

    public short getRow() {
        return row;
    }

    public void setRow(short row) {
        this.row = row;
    }

    public void setCol(short col) {
        this.col = col;
    }

    public short getCol() {
        return col;
    }

    @Override
    public int compareTo(Coordinate other) {
        if (row < other.getRow())
            return -1;
        else if (row == other.getRow())
            if (col < other.getCol())
                return -1;
            else if (col == other.getCol())
                return 0;
            else
                return 1;
        else if (row > other.getRow())
            return 1;
        return 0;
    }
}
