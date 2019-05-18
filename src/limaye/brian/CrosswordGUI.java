package limaye.brian;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

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
import javax.swing.border.Border;

public class CrosswordGUI {

	private final CrosswordGUIPanel crosswordPanel = new CrosswordGUIPanel();
	private final JButton refreshButton = new JButton("Refresh");
	private final JList<String> fittedWordsList = new JList<String>();

	public void play() {

		final JFrame frame = new JFrame();

		frame.setLayout(new BorderLayout());

		final JPanel toolsPanel = new JPanel();
		toolsPanel.setLayout(new FlowLayout());
		toolsPanel.add(fittedWordsList);
		toolsPanel.add(refreshButton);
		refreshButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				initGUIElements();
			}
		});

		frame.add(toolsPanel, BorderLayout.NORTH);
		frame.add(crosswordPanel, BorderLayout.CENTER);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		initGUIElements();

		frame.setTitle("Crossword Puzzle - by Brian Limaye - v0.1");
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public void setWordsInTextArea(final String[] words) {
		fittedWordsList.setEnabled(false);
		fittedWordsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		final DefaultListModel<String> listModel = new DefaultListModel<String>();
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
			final List<String> wordsList = new ArrayList<String>();

			final RandomDict dict = RandomDict.load("/usr/share/dict/words");

			for (int i = 0; i < 5; i++) {

				String nextWord = dict.nextWord();
				if (!wordsList.contains(nextWord)) {
					wordsList.add(nextWord);
				}
			}

			final CrosswordGenerator crosswordGenerator = new CrosswordGenerator();
			final List<String> fittedWords = crosswordGenerator.generate(wordsList.toArray(new String[0]));

			setWordsInTextArea(fittedWords.toArray(new String[0]));

			final char[][] crosswordArray = crosswordGenerator.getGrid();
			crosswordPanel.setCrossword(crosswordArray, fittedWords.toArray(new String[0]));
		} catch (IOException io) {
			showError(io);
		}

	}

	public static void main(final String argv[]) throws Throwable {
		final CrosswordGUI crosswordGUI = new CrosswordGUI();
		crosswordGUI.play();

	}

	final class CrosswordGUIPanel extends JPanel {

		private final Border HIGHLIGHTED_BORDER = BorderFactory.createLineBorder(Color.BLUE, 5);

		private JTextField textFields[][];

		public void setCrossword(final char array[][], final String[] words) {
			removeAll();
			int w = array.length;
			int h = array[0].length;
			setLayout(new GridLayout(w, h));
			textFields = new JTextField[w][h];

			final List<String> selectionList = new ArrayList<String>();

			final Map<String, JTextField> foundTextFields = new HashMap<String, JTextField>();

			final List<String> sortedWordsList = toSortedList(words);

			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					char c = array[x][y];
					if (c != 0) {
						textFields[x][y] = new JTextField(String.valueOf(c));
						textFields[x][y].putClientProperty("Coordinates2D", new Coordinates2D(y, x));
						textFields[x][y].setEditable(false);
						final Border defaultBorder = textFields[x][y].getBorder();

						textFields[x][y].addMouseListener(new MouseAdapter() {

							@Override
							public void mouseClicked(final MouseEvent e) {
								if (e.getClickCount() == 1) {
									final Object source = e.getSource();
									if (source instanceof JTextField) {
										final JTextField tf = (JTextField) source;
										final Object isFound = tf.getClientProperty("Found");
										final Coordinates2D coordinates2D = (Coordinates2D) tf
												.getClientProperty("Coordinates2D");
										System.out.println(coordinates2D);
										if (isFound != null) {
											return;
										}
										final String tfKey = String.valueOf(tf.hashCode());
										final Border border = tf.getBorder();

										final Font font = tf.getFont();
										final Map attributes = font.getAttributes();

										if (border.equals(HIGHLIGHTED_BORDER)) {
											tf.setBorder(defaultBorder);
											attributes.remove(TextAttribute.STRIKETHROUGH);
											final Font newFont = new Font(attributes);
											tf.setFont(newFont);

											removeFromList(selectionList, tf.getText());
											foundTextFields.remove(tfKey);

										} else {
											tf.setBorder(HIGHLIGHTED_BORDER);
											attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
											final Font newFont = new Font(attributes);
											tf.setFont(newFont);

											if (selectionList.isEmpty()) {
												System.out.println("Empty!");
											}

											addToList(selectionList, tf.getText());
											foundTextFields.put(tfKey, tf);

											final String selection = selectionList.stream().map(String::valueOf)
													.collect(Collectors.joining());
											System.out.println("Selection is: " + selection);
											System.out.println("Sorted Words are: " + sortedWordsList);
											if (sortedWordsList.contains(selection)) {
												System.out.println("Found!");
												setSelectionInList(selection);
												selectionList.clear();

												final Iterator<String> tfKeys = foundTextFields.keySet().iterator();
												while (tfKeys.hasNext()) {
													final String key = tfKeys.next();
													final JTextField value = (JTextField) foundTextFields.get(key);
													value.putClientProperty("Found", "True");
												}

												foundTextFields.clear();

											}

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

		protected void setSelectionInList(final String selection) {
			final DefaultListModel<String> listModel = (DefaultListModel<String>) fittedWordsList.getModel();
			final int size = listModel.getSize();
			for (int i = 0; i < size; i++) {
				final String element = (String) listModel.getElementAt(i);
				final char[] chars = element.toCharArray();
				Arrays.sort(chars);

				final String sortedElement = String.valueOf(chars);
				if (sortedElement.equals(selection)) {
					System.out.println("FOUND AGAIN!");
					int[] selectedIndices = fittedWordsList.getSelectedIndices();
					int[] newSelectedIndices = new int[selectedIndices.length + 1];
					if (selectedIndices.length > 0) {
						System.arraycopy(selectedIndices, 0, newSelectedIndices, 0, selectedIndices.length);
					}
					newSelectedIndices[newSelectedIndices.length - 1] = i;
					fittedWordsList.setSelectedIndices(newSelectedIndices);
					return;
				}

			}
		}

		protected void addToList(final List list, final String value) {
			list.add(value);
			Collections.sort(list);
		}

		protected void removeFromList(final List list, final String value) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).equals(value)) {
					list.remove(i);
					return;
				}
			}
			Collections.sort(list);
		}

		protected char[][] copy(final char[][] array) {
			if (array != null) {
				final char[][] copy = new char[array.length][];

				for (int i = 0; i < array.length; i++) {
					final char[] row = array[i];

					copy[i] = new char[row.length];
					System.arraycopy(row, 0, copy[i], 0, row.length);
				}

				return copy;
			}

			return null;
		}

		protected List<String> toSortedList(final String[] words) {
			final List<String> list = new ArrayList<String>();

			for (int i = 0; i < words.length; i++) {
				list.add(sort(words[i].toCharArray()));
			}

			return list;
		}

		protected String sort(final char[] arr) {
			Arrays.sort(arr);
			return new String(arr);
		}

		protected boolean isDefaultColor(final Color color) {
			final int blue = color.getBlue();
			final int green = color.getGreen();
			final int red = color.getRed();

			return blue == 255 && green == 255 && red == 255;
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

		protected Color getDefaultColor() {
			return new Color(255, 255, 255);
		}

		public char[][] getCrossword() {
			int w = textFields.length;
			int h = textFields[0].length;
			char crossword[][] = new char[w][h];
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					if (textFields[x][y] != null) {
						String s = textFields[x][y].getText();
						if (s.length() > 0) {
							crossword[x][y] = s.charAt(0);
						}

					}
				}
			}
			return crossword;
		}
	}

	static class RandomDict {
		public final static String[] NO_STRINGS = {};
		final Random random = new Random();
		final String[] words;

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
}
