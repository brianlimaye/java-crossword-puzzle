package limaye.brian;

public class Coordinates2D {

	private int rowIndex;
	private int colIndex;

	public Coordinates2D(int row, int col) {
		this.rowIndex = row;
		this.colIndex = col;
	}

	public int getRow() {
		return rowIndex;
	}

	public int getColumn() {
		return colIndex;
	}

	public String toString() {
		return "(" + rowIndex + "," + " " + colIndex + ")" + ",";
	}

	public static void main(String[] args) {

	}
}
