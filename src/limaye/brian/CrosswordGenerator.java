package limaye.brian;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CrosswordGenerator {

	private char[][] gridDefault = { { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' },
			{ ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' }, { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' },
			{ ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' }, { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' },
			{ ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' }, { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' },
			{ ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' }, { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' },
			{ ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' } };

	private char[][] grid = null;
	private boolean showRandom = false;
	private boolean overLap = true;
	private List<Integer> possiblePos;

	public List<String> generate(String[] words) {
		this.grid = Arrays.stream(gridDefault).map(r -> r.clone()).toArray(char[][]::new);

		possiblePos = new ArrayList<Integer>();
		resetPositions();
		int random = 0;
		int randomIndex = 0;

		List<String> wordsThatFit = new ArrayList<String>();

		List<String> wordList = convertToList(words);
		Collections.sort(wordList);
		
		String[] newWords = (String[]) wordList.toArray();
		String direction = "";

		loop: for (int j = 0; j < words.length; j++) {

			resetPositions();
			String currentWord = newWords[j];
			boolean b = false;
			while (b == false) {

				if (possiblePos.size() == 0) {
					continue loop;
				}
				randomIndex = (int) (Math.random() * possiblePos.size());
				random = possiblePos.get(randomIndex);

				while (possiblePos.indexOf(random) == -1) {
					randomIndex = (int) (Math.random() * possiblePos.size());
					random = possiblePos.get(randomIndex);
				}
				possiblePos.remove(randomIndex);
				direction = direction(random);

				if ((random == 0) || (random == 1)) {
					b = fillRow(direction, newWords, j);
				}

				if ((random == 2) || (random == 3)) {
					b = fillCol(direction, newWords, j);
				}

				if (b == true) {
					wordsThatFit.add(currentWord);
				}
			}
		}

		if (showRandom == false) {
			fillRemainingSpots();
		}

		for (int k = 0; k < grid.length; k++) {
			System.out.print(Arrays.toString(grid[k]));
			System.out.println();
		}
		System.out.println();
		System.out.println("Words that fit: ");
		return wordsThatFit;
	}

	public String direction(int n) {
		String s = "";

		if ((n > 5) || (n < 0)) {
			throw new IllegalArgumentException("Invalid int!");
		}

		switch (n) {
		case 0:
			s = "Horizontal";
			break;
		case 1:
			s = "Backwards Horizontal";
			break;
		case 2:
			s = "Vertical";
			break;
		case 3:
			s = "Backwards Vertical";
			break;
		}
		return s;

	}

	public int calculateRandom() {
		return ((int) (Math.random() * 4));
	}

	public boolean fillRow(String direction, String[] words, int pos) {

		if (direction.equals("Vertical") || (direction.equals("Backwards Vertical")) || (direction.equals("Diagonal"))
				|| (direction.equals("Backwards Diagonal"))) {
			throw new Error("Incorrect Method Call.");
		}

		if (words[pos].length() > grid[0].length) {
			return false;
		}

		if (overLap == true) {
			boolean success = overlapHorizontal(direction, words, pos);
			if (success == true) {
				return true;
			}
		}

		String currWord = words[pos];
		String s = "";
		int row = 0;
		int index = 0;
		int startIndex;
		int endIndex;
		StringBuilder sb = new StringBuilder();

		while (row < grid.length) {

			for (int i = 0; i < grid[0].length; i++) {
				startIndex = i;
				endIndex = i + currWord.length();

				if (endIndex > grid[0].length) {
					endIndex = grid[0].length;
				}

				for (int k = startIndex; k < endIndex; k++) {
					sb.append(grid[row][k]);
				}

				s = sb.toString();
				sb.setLength(0);

				if ((s.trim().isEmpty()) && (endIndex - startIndex >= currWord.length())) {
					if (direction.equals("Backwards Horizontal")) {
						index = currWord.length() - 1;
					}

					for (int j = startIndex; j < endIndex; j++) {
						if (direction.equals("Horizontal")) {
							grid[row][j] = currWord.charAt(index);
							index++;
						}

						if (direction.equals("Backwards Horizontal")) {
							grid[row][j] = currWord.charAt(index);
							index--;
						}
					}
					return true;
				}

				if ((startIndex >= grid[0].length - 1) && (row < grid.length)) {
					row++;
					startIndex = -1;
					s = "";
				}
			}

		}
		return false;
	}

	public boolean fillCol(String direction, String[] words, int pos) {
		if (direction.equals("Horizontal") || (direction.equals("Backwards Horizontal"))
				|| (direction.equals("Diagonal")) || (direction.equals("Backwards Diagonal"))) {
			throw new Error("Incorrect Method Call.");
		}

		if (words[pos].length() > grid.length) {
			return false;
		}

		if (overLap == true) {
			boolean success = overlapVertical(direction, words, pos);
			if (success == true) {
				return true;
			}
		}

		String currWord = words[pos];
		int startIndex;
		int endIndex;
		int col = 0;
		int index = 0;
		StringBuilder sb = new StringBuilder();
		String s = "";

		while (col < grid[0].length) {
			for (int i = 0; i < grid.length; i++) {
				startIndex = i;
				endIndex = i + currWord.length();
				if (endIndex > grid.length) {
					endIndex = grid.length;
				}

				for (int k = startIndex; k < endIndex; k++) {
					sb.append(grid[k][col]);
				}

				s = sb.toString();
				sb.setLength(0);

				if ((s.trim().isEmpty()) && (endIndex - startIndex >= currWord.length())) {
					if (direction.equals("Backwards Vertical")) {
						index = currWord.length() - 1;
					}

					for (int j = startIndex; j < endIndex; j++) {
						if (direction.equals("Vertical")) {
							grid[j][col] = currWord.charAt(index);
							index++;
						}

						if (direction.equals("Backwards Vertical")) {
							grid[j][col] = currWord.charAt(index);
							index--;
						}

					}
					return true;

				}

				if ((startIndex >= grid.length - 1) && (col < grid[0].length)) {
					col++;
					startIndex = -1;
					s = "";
				}
			}
		}
		return false;
	}

	public List<String> convertToList(String[] list) {
		return Arrays.asList(list);
	}

	public List<Character> commonIntersection(String[] words, int pos) {

		int b = 0;

		String currentElement = words[pos];
		List<Character> common = new ArrayList<Character>();
		for (int i = 0; i < pos; i++) {
			String nElement = words[i];
			for (int j = 0; j < currentElement.length(); j++) {
				for (int k = 0; k < nElement.length(); k++) {
					if (currentElement.charAt(j) == nElement.charAt(k)) {
						common.add(new Character(currentElement.charAt(j)));
					}
				}
			}

		}
		Collections.sort(common);

		while (b < common.size() - 1) {
			if (common.get(b).charValue() == common.get(b + 1).charValue()) {
				common.remove(b);
			} else {
				b++;
			}
		}
		return common;
	}

	public List<Coordinates2D> getPositions(List<Character> c, String[] words, int pos) {
		if (c.size() == 0) {
			return null;
		}
		List<Character> temp = commonIntersection(words, pos);
		List<Coordinates2D> positions = new ArrayList<Coordinates2D>();
		for (int i = 0; i < temp.size(); i++) {
			char curr = temp.get(i).charValue();
			for (int row = 0; row < grid[0].length; row++) {
				for (int col = 0; col < grid.length; col++) {
					if (grid[row][col] == curr) {
						positions.add(new Coordinates2D(row, col));
					}
				}
			}

		}
		return positions;
	}

	public boolean overlapHorizontal(String direction, String[] words, int pos) {
		List<Character> commons = commonIntersection(words, pos);
		if ((commons.size() == 0) || (words[pos].length() > grid[0].length)) {
			return false;
		}
		List<Coordinates2D> positions = getPositions(commons, words, pos);
		StringBuilder sb = new StringBuilder();
		String s;
		int col;
		int row;
		int index;
		int count = 0;
		int startPos = 0;
		int endPos = 0;
		String currElement = words[pos];

		for (int i = 0; i < positions.size(); i++) {
			sb.setLength(0);
			currElement = words[pos];
			row = positions.get(i).getRow();
			col = positions.get(i).getColumn();
			char test = grid[row][col];
			int iterations = numberOfOccurrences(test, currElement);

			loop: for (int k = 1; k <= iterations; k++) {
				currElement = words[pos];

				if (direction.equals("Backwards Horizontal")) {
					currElement = reverse(currElement);
				}

				index = getNthRowIndex(k, test, currElement);
				startPos = col - index;
				endPos = startPos + currElement.length();

				if ((endPos > grid[0].length) || (startPos < 0)) {
					continue loop;
				}

				for (int c = startPos; c < endPos; c++) {
					sb.append(grid[row][c]);
				}

				s = sb.toString().trim();

				if (s.equals(Character.toString(test))) {
					for (int c = startPos; c < endPos; c++) {

						grid[row][c] = currElement.charAt(count);
						count++;
					}
					return true;
				}
				continue loop;
			}
		}
		return false;

	}

	public boolean overlapVertical(String direction, String[] words, int pos) {
		List<Character> commons = commonIntersection(words, pos);
		if ((commons.size() == 0) || (words[pos].length() > grid.length)) {
			return false;
		}
		List<Coordinates2D> positions = getPositions(commons, words, pos);
		StringBuilder sb = new StringBuilder();
		String s;
		int col;
		int row;
		int index;
		int count = 0;
		int startPos = 0;
		int endPos = 0;
		String currElement = words[pos];

		int length = currElement.length();

		for (int i = 0; i < positions.size(); i++) {
			currElement = words[pos];
			row = positions.get(i).getRow();
			col = positions.get(i).getColumn();
			char test = grid[row][col];
			int iterations = numberOfOccurrences(test, currElement);

			loop: for (int k = 1; k <= iterations; k++) {
				currElement = words[pos];
				count = 0;
				sb.setLength(0);

				if (direction.equals("Backwards Vertical")) {
					currElement = reverse(currElement);
				}

				index = getNthRowIndex(k, test, currElement);
				startPos = row - index;
				endPos = startPos + length;

				if ((endPos > grid.length) || (startPos < 0)) {
					continue loop;
				}

				for (int roW = startPos; roW < endPos; roW++) {
					sb.append(grid[roW][col]);
				}

				s = sb.toString().trim();

				if (s.equals(Character.toString(test))) {
					for (int c = startPos; c < endPos; c++) {

						grid[c][col] = currElement.charAt(count);
						count++;
					}
					return true;
				}
				continue loop;
			}
		}
		return false;

	}

	public String reverse(String s) {
		StringBuilder sb = new StringBuilder();

		for (int i = s.length() - 1; i >= 0; i--) {
			sb.append(s.charAt(i));
		}
		return sb.toString();
	}

	protected int numberOfOccurrences(char c, String word) {
		int count = 0;
		for (int i = 0; i < word.length(); i++) {
			if (word.charAt(i) == c) {
				count++;
				continue;
			}
		}
		return count;
	}

	protected int getNthRowIndex(int occurrence, char c, String element) {
		int count = 0;
		for (int i = 0; i < element.length(); i++) {
			if (element.charAt(i) == c) {
				count++;
				if (occurrence == count) {
					return i;
				}
			}
		}
		return -1;
	}

	protected char[][] getGrid() {

		return grid;
	}

	protected boolean getOverlap() {
		return overLap;
	}

	protected char generateRandomChar() {
		int rand = (int) (Math.random() * 26) + 97;
		return (char) (rand);
	}

	protected void fillRemainingSpots() {
		for (int i = 0; i < grid[0].length; i++) {
			for (int k = 0; k < grid.length; k++) {
				if (grid[i][k] == ' ') {
					grid[i][k] = generateRandomChar();
				}
			}
		}
	}

	private void resetPositions() {
		possiblePos.clear();
		possiblePos.add(0);
		possiblePos.add(1);
		possiblePos.add(2);
		possiblePos.add(3);
	}

	public static void main(String[] args) {
		CrosswordGenerator c = new CrosswordGenerator();
		String[] words = { "AijackJamesWorth", "Elevatoronmyheadandmytoes", "Hammer", "Jeffie", "Mickie", "Elevator",
				"Porsche" };
		System.out.println(c.generate(words));
	}
}
