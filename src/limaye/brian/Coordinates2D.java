package limaye.brian;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Coordinates2D implements Comparable{

	private int rowIndex;
	private int colIndex;
	
	public Coordinates2D(int row, int col)
	{
		this.rowIndex = row;
		this.colIndex = col;
	}
	
	public int getRow()
	{
		return rowIndex;
	}
	
	public int getColumn()
	{
		return colIndex;
	}
	
	public String toString()
	{
		return "(" + rowIndex + "," + " " + colIndex + ")" + ",";
	}
	
	public boolean equals(Object o)
	{
		if(o instanceof Coordinates2D)
		{
			final Coordinates2D otherObject = (Coordinates2D) o;
			return ((rowIndex == otherObject.rowIndex) && (colIndex == otherObject.colIndex));

		}
		return false;
	}
	
	public int hashCode()
	{
		return toString().hashCode();
	}

	@Override
	public int compareTo(Object o) {
		
		if(o instanceof Coordinates2D)
		{
			Coordinates2D otherObject = (Coordinates2D) o;
			if(rowIndex == otherObject.getRow())
			{
				return colIndex - otherObject.getColumn();
			}
			return rowIndex - otherObject.getRow();	
		}
		throw new UnsupportedOperationException("Requires 2DCoordinates class!");
	}
	
	public static String sortAndGenerateKey(List<Coordinates2D> coord)
	{
		Collections.sort(coord);
		return Arrays.toString(coord.toArray(new Coordinates2D[0]));	
	}
	
	public static void main(String[] args)
	{
		Coordinates2D[] coordinates = new Coordinates2D[4];
		Coordinates2D a = new Coordinates2D(0, 0);
		coordinates[0] = a;
		Coordinates2D b = new Coordinates2D(5, 1);
		coordinates[1] = b;
		Coordinates2D c = new Coordinates2D(0, 1);
		coordinates[2] = c;
		Coordinates2D d = new Coordinates2D(5, 0);
		coordinates[3] = d;
		
		System.out.println(Coordinates2D.sortAndGenerateKey(Arrays.asList(coordinates)));
		
		
		
		
		
	}
}
