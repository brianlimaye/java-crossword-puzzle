package limaye.brian;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;

public class CrosswordGUI {

	private CrosswordGUIPanel crosswordPanel = new CrosswordGUIPanel();

	private int currentCount = 0;
	private static int highRecord = 0;
	private final TimerPanel timerPanel = new TimerPanel();
	private final JLabel score = new JLabel("Score: 0");
	private final JLabel highScore = new JLabel("High Score: 0");
	private final JButton refreshButton = new JButton("Refresh");
	private final JLabel hintDisplay = new JLabel("                           ");
	private final JList<String> fittedWordsList = new JList<String>();

	// for crossword solver
	private final CrossWordSolver crosswordSolver = new CrossWordSolver();

	public void play() {

		highRecord = getPrevHighScore();
		final JFrame frame = new JFrame();

		frame.setLayout(new BorderLayout());

		JPanel toolsPanel = new JPanel();
		FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT, 20, 10);
		toolsPanel.setLayout(flowLayout);
		toolsPanel.add(timerPanel);
		toolsPanel.add(fittedWordsList);
		toolsPanel.add(refreshButton);
		toolsPanel.add(score);
		toolsPanel.add(highScore);
		toolsPanel.add(hintDisplay);

		refreshButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						initGUIElements();
					}
				});
			}
		});

		frame.add(toolsPanel, BorderLayout.NORTH);
		frame.add(crosswordPanel, BorderLayout.CENTER);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		initGUIElements();

		frame.setTitle("Crossword Puzzle v0.1 - by Brian Limaye");
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public void setWordsInTextArea(String[] words) {
		fittedWordsList.setEnabled(false);
		fittedWordsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		DefaultListModel<String> listModel = new DefaultListModel<String>();
		for (int i = 0; i < words.length; i++) {
			listModel.addElement(words[i]);
		}
		fittedWordsList.setModel(listModel);
	}

	protected void showError(String errorMessage) {
		showError(errorMessage, "Error!");
	}

	protected void showError(String errorMessage, String title) {
		JOptionPane.showMessageDialog(null, errorMessage, title, JOptionPane.ERROR_MESSAGE);
	}

	protected void showError(Exception exceptionError) {
		String errorMessage = "Message: " + exceptionError.getMessage() + "\nStackTrace: "
				+ Arrays.toString(exceptionError.getStackTrace());
		String title = exceptionError.getClass().getName();
		showError(errorMessage, title);
	}

	public void initGUIElements() {

		try {
			currentCount = 0;
			score.setText("Score: 0");
			char[][] grid = null;
			List<String> fittedWords = null;

			List<String> wordsList = new ArrayList<String>();

			RandomDict dict = RandomDict.load("/usr/share/dict/words");

			for (int i = 0; i < 10; i++) {

				String nextWord = dict.nextWord();
				if (!wordsList.contains(nextWord)) {
					wordsList.add(nextWord);
				}
			}

			CrosswordGenerator crosswordGenerator = new CrosswordGenerator();
			fittedWords = crosswordGenerator.generate(wordsList.toArray(new String[0]));
			String[] fittedWordsArray = fittedWords.toArray(new String[0]);

			// first clear out the solver
			crosswordSolver.clear();

			// now store in instance variable for later...
			grid = crosswordGenerator.getGrid();

			// next solve the puzzle

			crosswordSolver.solve(grid, fittedWordsArray);

			setWordsInTextArea(fittedWords.toArray(new String[0]));

			char[][] crosswordArray = crosswordGenerator.getGrid();
			crosswordPanel.setCrossword(crosswordArray, fittedWordsArray);

			timerPanel.init();

		} catch (IOException io) {
			showError(io);
		}
	}

	public static void main(final String argv[]) throws Throwable {
		CrosswordGUI crosswordGUI = new CrosswordGUI();
		crosswordGUI.play();

	}

	public class TimerPanel extends JPanel {
		JLabel label;
		Timer timer;
		int count;

		public TimerPanel() {
			init();
		}

		public void init() {
			removeAll();
			stopTimer();
			count = 0;

			label = new JLabel("Elapsed Time: ");
			setLayout(new GridBagLayout());
			add(label);
			timer = new Timer(1000, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					count++;
					if (count < 100000) {
						label.setText("Elapsed Time: " + Integer.toString(count) + " secs.");
						if (count % 10 == 0) {
							// Toolkit.getDefaultToolkit().beep();
							// hintDisplay.setText("Need a hint? Press H.");
							// JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(TimerPanel.this);
							// if (topFrame != null)
							// {
							// topFrame.pack();
							// }

						}
					} else {
						((Timer) (e.getSource())).stop();
					}
				}
			});
			timer.setInitialDelay(0);
			timer.start();
		}

		public void stopTimer() {
			if (timer != null) {
				timer.stop();
			}
		}

	}

	final class CrosswordGUIPanel extends JPanel {

		private Border HIGHLIGHTED_BORDER = BorderFactory.createLineBorder(Color.BLUE, 5);

		private JTextField textFields[][];

		public void setCrossword(final char array[][], final String[] words) {
			removeAll();
			int w = array.length;
			int h = array[0].length;
			setLayout(new GridLayout(w, h));
			textFields = new JTextField[w][h];

			final Map<String, JTextField> foundTextFields = new HashMap<String, JTextField>();

			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					char c = array[x][y];
					if (c != 0) {
						textFields[x][y] = new JTextField(String.valueOf(c));
						textFields[x][y].putClientProperty("Coordinates2D", new Coordinates2D(x, y));
						textFields[x][y].setEditable(false);
						final Border defaultBorder = textFields[x][y].getBorder();

						textFields[x][y].addMouseListener(new MouseAdapter() {

							@Override
							public void mouseClicked(final MouseEvent e) {
								if (e.getClickCount() == 1) {
									final Object source = e.getSource();
									if (source instanceof JTextField) {
										JTextField tf = (JTextField) source;
										Object isFound = tf.getClientProperty("Found");
										Coordinates2D coordinates2D = (Coordinates2D) tf
												.getClientProperty("Coordinates2D");

										String tfKey = String.valueOf(tf.hashCode());
										Border border = tf.getBorder();

										if (border.equals(HIGHLIGHTED_BORDER)) {
											tf.setBorder(defaultBorder);

											foundTextFields.remove(tfKey);

										} else {
											tf.setBorder(HIGHLIGHTED_BORDER);
											foundTextFields.put(tfKey, tf);

											SwingUtilities.invokeLater(new Runnable() {
												public void run() {
													checkIfFound(foundTextFields, defaultBorder);
												}

												private void checkIfFound(final Map<String, JTextField> foundTextFields,
														Border defaultBorder) {
													List<Coordinates2D> list = new ArrayList<Coordinates2D>();
													Iterator<String> tfKeys = foundTextFields.keySet().iterator();
													while (tfKeys.hasNext()) {
														String key = tfKeys.next();
														JTextField value = foundTextFields.get(key);
														Object object = value.getClientProperty("Coordinates2D");
														if (object instanceof Coordinates2D) {
															Coordinates2D foundObject = (Coordinates2D) object;
															list.add(foundObject);
														}
													}

													String word = crosswordSolver
															.find(list.toArray(new Coordinates2D[0]));
													if (word != null) {

														tfKeys = foundTextFields.keySet().iterator();
														while (tfKeys.hasNext()) {

															String key = tfKeys.next();
															JTextField value = foundTextFields.get(key);
															Font font = value.getFont();
															Map attributes = font.getAttributes();
															attributes.put(TextAttribute.STRIKETHROUGH,
																	TextAttribute.STRIKETHROUGH_ON);
															final Font newFont = new Font(attributes);
															value.setFont(newFont);

															value.setForeground(Color.red);

															value.setBorder(defaultBorder);

															value.putClientProperty("Found", "True");
														}
														currentCount += 10;
														score.setText("Score: " + currentCount);
														if (currentCount >= highRecord) {
															highRecord = currentCount;
														}
														highScore.setText("High Score: " + highRecord);

														try {
															SoundUtils.laser(5);
														} catch (LineUnavailableException e1) {
															// TODO Auto-generated catch block
															e1.printStackTrace();
														} catch (InterruptedException e1) {
															// TODO Auto-generated catch block
															e1.printStackTrace();
														}

														foundTextFields.clear();

														if (setSelectionInList(word)) {
															timerPanel.stopTimer();
															return;
														}

													}
												}
											});

										}
										repaintParent(tf);
									}
								}
							}
						});

						textFields[x][y].setFont(textFields[x][y].getFont().deriveFont(20.0f));
						add(textFields[x][y]);
					} else {
						add(new JLabel());
					}
				}
			}
			repaintParent(this);
			repaint();
		}

		protected boolean setSelectionInList(final String selection) {
			final DefaultListModel<String> listModel = (DefaultListModel<String>) fittedWordsList.getModel();

			int size = listModel.getSize();
			for (int i = 0; i < size; i++) {
				String element = listModel.getElementAt(i);
				if (selection.equals(element)) {
					int[] selectedIndices = fittedWordsList.getSelectedIndices();
					int[] newSelectedIndices = new int[selectedIndices.length + 1];
					if (selectedIndices.length > 0) {
						System.arraycopy(selectedIndices, 0, newSelectedIndices, 0, selectedIndices.length);
					}
					newSelectedIndices[newSelectedIndices.length - 1] = i;
					fittedWordsList.setSelectedIndices(newSelectedIndices);

					if (newSelectedIndices.length == listModel.getSize()) {
						return true;
					} else {
						return false;
					}

				}
			}
			return false;
		}

		protected void repaintParent(JComponent component) {

			// Get the parent of the component.
			JComponent parentComponent = (JComponent) SwingUtilities.getAncestorOfClass(JComponent.class, component);

			// Could we find a parent?
			if (parentComponent != null) {
				// Repaint the parent.
				parentComponent.revalidate();
				parentComponent.repaint();
			} else {
				// Repaint the component itself.
				component.revalidate();
				component.repaint();
			}

		}

	}

	public static class RandomDict {
		public static String[] NO_STRINGS = {};
		Random random = new Random();
		String[] words;

		private RandomDict(final String[] words) {
			this.words = words;
		}

		public static RandomDict load(String filename) throws IOException {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			Set<String> words = new LinkedHashSet<String>();
			try {
				for (String line; (line = br.readLine()) != null;) {
					if (line.indexOf('\'') >= 0)
						continue;
					words.add(line.toLowerCase());
				}
			} finally {
				br.close();
			}
			return new RandomDict(words.toArray(NO_STRINGS));
		}

		public String nextWord() {
			return words[random.nextInt(words.length)];
		}
	}

	public int getPrevHighScore() {
		return highRecord;
	}
}
