package limaye.brian;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrossWordSolver {

	private static final Map<String, String> lookupMap = new HashMap<String, String>();

	public static void main(String[] args) {
		CrosswordGenerator cw = new CrosswordGenerator();
		Coordinates2D[] arr = {new Coordinates2D(0,0), new Coordinates2D(0,1), new Coordinates2D(0,2), new Coordinates2D(0,3), new Coordinates2D(0,4), new Coordinates2D(0,5)}; 
		CrossWordSolver cs = new CrossWordSolver();
		List<String> list = new ArrayList<String>();
		String[] fittedWords = new String[list.size()];
		String[] words = {"Jimbom", "Target", "Laldig", "Doggop", "Rainbo", "Troute", "Gazebo"};

		for (int i = 1; i <= 1; i++) {
			list = cw.generate(words);
			fittedWords = (list.toArray(new String[0]));
			System.out.println(list);
			char[][] grid = cw.getGrid();
			boolean b = cs.solve(grid, fittedWords);
			if (b == false) {
				throw new Error("Case failed!!");
			}
		}
	 
		System.out.println();
		System.out.println(cs.find(arr)); 
	}

	public boolean solve(char[][] grid, String[] words) {
		
		List<Coordinates2D> allPositions = new ArrayList<Coordinates2D>();
		String direction = "";
		int wordsFound = 0;

		mloop: for (int i = 0; i < words.length; i++) {
			
			String currentWord = words[i];
			allPositions = posOfFirstChars(grid, currentWord);
			loop: for (int k = 0; k < allPositions.size(); k++) {
				Coordinates2D curr = allPositions.get(k);
				boolean tryHoriz = getHoriz(curr, grid, currentWord);
				boolean tryBackwardsHoriz = getBackwardsHoriz(curr, grid, currentWord);
				boolean tryVert = getVertical(curr, grid, currentWord);
				boolean tryBackwardsVertical = getBackwardsVertical(curr, grid, currentWord);

				if ((tryHoriz == false) && (tryBackwardsHoriz == false) && (tryVert == false)
						&& (tryBackwardsVertical == false)) {
					continue loop;
				} else {
					
					if(tryHoriz)
					{
						direction = "Horizontal";
					}
					
					if(tryBackwardsHoriz)
					{
						direction = "Backwards Horizontal";
					}
					
					if(tryVert)
					{
						direction = "Vertical";
					}
					if(tryBackwardsVertical)
					{
						direction = "Backwards Vertical";
					}
					generateAndAddKey(direction, curr, currentWord);
					wordsFound++;
					continue mloop;
				}
			}
		}

		if (wordsFound == words.length) {
			return true;
		}
		return false;
	}

	private List<Coordinates2D> posOfFirstChars(char[][] grid, String word) {
		List<Coordinates2D> positions = new ArrayList<Coordinates2D>();
		char firstChar = word.charAt(0);

		for (int row = 0; row < grid.length; row++) {
			for (int col = 0; col < grid[0].length; col++) {
				if (grid[row][col] == firstChar) {
					positions.add(new Coordinates2D(row, col));
				}
			}
		}
		return positions;
	}

	private boolean getHoriz(Coordinates2D coord, char[][] grid, String word) {
		StringBuilder sb = new StringBuilder();
		int row = coord.getRow();
		int col = coord.getColumn();

		if ((col + word.length()) - 1 >= grid[0].length) {
			return false;
		}

		for (int i = col; i < col + word.length(); i++) {
			sb.append("" + grid[row][i]);
		}

		if (sb.toString().equals(word)) {
			//System.out.println(word.toUpperCase() + " was found horizontally!");
			return true;
		}

		return false;
	}

	private boolean getBackwardsHoriz(Coordinates2D coord, char[][] grid, String word) {
		StringBuilder sb = new StringBuilder();
		int row = coord.getRow();
		int col = coord.getColumn();

		if ((col - word.length()) + 1 < 0) {
			return false;
		}

		for (int i = col; i > col - word.length(); i--) {
			sb.append("" + grid[row][i]);
		}

		if (sb.toString().equals(word)) {
			//System.out.println(word.toUpperCase() + " was found Backwards Horizontally!");
			return true;
		}

		return false;
	}

	private boolean getVertical(Coordinates2D coord, char[][] grid, String word) {
		StringBuilder sb = new StringBuilder();
		int row = coord.getRow();
		int col = coord.getColumn();

		if ((row + word.length()) - 1 >= grid.length) {
			return false;
		}

		for (int i = row; i < row + word.length(); i++) {
			sb.append("" + grid[i][col]);
		}

		if (sb.toString().equals(word)) {
			//System.out.println(word.toUpperCase() + " was found Vertically!");
			return true;
		}

		return false;
	}

	private boolean getBackwardsVertical(Coordinates2D coord, char[][] grid, String word) {
		StringBuilder sb = new StringBuilder();
		int row = coord.getRow();
		int col = coord.getColumn();

		if ((row - word.length()) + 1 < 0) {
			return false;
		}

		for (int i = row; i > row - word.length(); i--) {
			sb.append("" + grid[i][col]);
		}

		if (sb.toString().equals(word)) {
			//System.out.println(word.toUpperCase() + " was found Backwards Vertically!");
			return true;
		}
		return false;
	}
	
	private void generateAndAddKey(String direction, Coordinates2D startCoords, String word)
	{
		List<Coordinates2D> key = new ArrayList<Coordinates2D>();
		key.add(startCoords);
		int startRow = startCoords.getRow();
		int startCol = startCoords.getColumn();
		
		if(direction.equals("Horizontal"))
		{
			for(int col=startCol + 1; col < startCol + word.length(); col++)
			{
				key.add(new Coordinates2D(startRow, col));
			}		
		}
		
		if(direction.equals("Backwards Horizontal"))
		{
			for(int col=startCol - 1; col > startCol - word.length(); col--)
			{
				key.add(new Coordinates2D(startRow, col));
			}		
		}
		
		if(direction.equals("Vertical"))
		{
			for(int row=startRow + 1; row < startCol + word.length(); row++)
			{
				key.add(new Coordinates2D(row, startCol));
			}		
		}
		
		if(direction.equals("Backwards Vertical"))
		{
			for(int row=startRow - 1; row > startRow - word.length(); row--)
			{
				key.add(new Coordinates2D(row, startCol));
			}		
		}
		Coordinates2D.sortCoordinates(key);
		String keyString = key.toString();
		lookupMap.put(keyString, word);		
	}
	
	public String find(Coordinates2D[] array)
	{
		List<Coordinates2D> list = Arrays.asList(array);
		Coordinates2D.sortCoordinates(list);
		String key = list.toString();
		System.out.println("Key is: " + key);
		System.out.println("Map is: " + lookupMap);
		return lookupMap.get(key);
	}
	
	public static Map<String, String> getMap()
	{
		return lookupMap;
	}
	
	public void clear()
	{
		lookupMap.clear();
	}

}
