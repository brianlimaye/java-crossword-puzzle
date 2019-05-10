package limaye.brian;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class CrosswordSolver {

	public void solve(final char[][] crosswordArray, final String[] words) {
		if ((crosswordArray == null) || (crosswordArray.length == 0)) {
			throw new IllegalArgumentException("Crosswordarray was null/empty!");
		}

		final JFrame frame = new JFrame();
		final CrosswordPanel crosswordPanel = new CrosswordPanel();
		crosswordPanel.setCrossword(crosswordArray, words);

		frame.setLayout(new BorderLayout());
		frame.add(crosswordPanel, BorderLayout.CENTER);
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public static void main(final String argv[]) throws Throwable {

		final char[][] crosswordArray = new char[][] { new char[] { 't', 'd', 'i', 's', 'a', 'b' },
				new char[] { 'h', 'a', 'g', 'd', 'e', 'f' }, new char[] { 'e', 'h', 'g', 'd', 'e', 'f' } };
				
				
		final String[] wordsArray = new String[] { "the", "had" };

		final CrosswordSolver crosswordSolver = new CrosswordSolver();
		crosswordSolver.solve(crosswordArray, wordsArray);

	}

}
