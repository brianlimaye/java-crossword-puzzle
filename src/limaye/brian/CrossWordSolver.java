package limaye.brian;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrossWordSolver {

	private final Map<String, String> lookupMap = new HashMap<String, String>();

	public static void main(String[] args) {
		CrosswordGenerator cw = new CrosswordGenerator();
		CrossWordSolver cs = new CrossWordSolver();
		List<String> list = new ArrayList<String>();
		String[] fittedWords = new String[list.size()];
		String[] words = { "Jelfkjaldfaf", "Ayanna", "Nick", "Yick", "Anna", "Kick", "KingKrule" };

		for (int i = 1; i <= 10000; i++) {
			list = cw.generate(words);
			fittedWords = (String[]) (list.toArray(new String[0]));
			char[][] grid = cw.getGrid();
			boolean b = cs.findAllWords(grid, fittedWords);
			if (b == false) {
				throw new Error("Case failed!!");
			}
			
			System.out.println("Lookup map equals " + cs.lookupMap);
		}
	}
	
	public boolean findAllWords(char[][] grid, String[] words) {
		List<Coordinates2D> allPositions = new ArrayList<Coordinates2D>();
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
					final String key = Coordinates2D.sortAndGenerateKey(allPositions);
					final List<Coordinates2D> copy = new ArrayList<Coordinates2D>();
					copy.addAll(allPositions);
					lookupMap.put(key, currentWord);
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
	
	public String matches(List<Coordinates2D> list)
	{
		final String key = Coordinates2D.sortAndGenerateKey(list);
		return lookupMap.get(key);
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
			System.out.println(word.toUpperCase() + " was found horizontally!");
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
			System.out.println(word.toUpperCase() + " was found Backwards Horizontally!");
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
			System.out.println(word.toUpperCase() + " was found Vertically!");
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
			System.out.println(word.toUpperCase() + " was found Backwards Vertically!");
			return true;
		}
		return false;
	}

}
