package limaye.brian;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

public final class CrosswordPanel extends JPanel {

	private final static Border HIGHLIGHTED_BORDER = BorderFactory.createLineBorder(Color.BLUE, 5);

	private JTextField textFields[][];

	public void setCrossword(final char array[][], final String[] words) {
		removeAll();
		int w = array.length;
		int h = array[0].length;
		setLayout(new GridLayout(w, h));
		textFields = new JTextField[w][h];

		final List<String> selectionList = new ArrayList<String>();
		
		final List<String> sortedWordsList = toSortedList(words);

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				char c = array[x][y];
				if (c != 0) {
					textFields[x][y] = new JTextField(String.valueOf(c));
					textFields[x][y].setEditable(false);
					final Border defaultBorder = textFields[x][y].getBorder();

					textFields[x][y].addMouseListener(new MouseAdapter() {

						@Override
						public void mouseClicked(final MouseEvent e) {
							if (e.getClickCount() == 1) {
								final Object source = e.getSource();
								if (source instanceof JTextField) {
									final JTextField tf = (JTextField) source;
									final Border border = tf.getBorder();

									final Font font = tf.getFont();
									final Map attributes = font.getAttributes();

									if (border.equals(HIGHLIGHTED_BORDER)) {
										tf.setBorder(defaultBorder);
										attributes.remove(TextAttribute.STRIKETHROUGH);
										final Font newFont = new Font(attributes);
										tf.setFont(newFont);

										removeFromList(selectionList, tf.getText());

										
										
								        
									} else {
										tf.setBorder(HIGHLIGHTED_BORDER);
										attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
										final Font newFont = new Font(attributes);
										tf.setFont(newFont);
										
										addToList(selectionList, tf.getText());
										
								        final String selection =  selectionList.stream().map(String::valueOf).collect(Collectors.joining()); 
								        System.out.println("Selection is: " + selection);
								        System.out.println("Sorted Words are: " + sortedWordsList);
								        if (sortedWordsList.contains(selection))
								        {
								        	System.out.println("Found!");
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

	
	protected List<String> toSortedList(final String[] words )
	{
		final List<String> list = new ArrayList<String>();
		
		for (int i=0; i < words.length; i++)
		{
			list.add(sort(words[i].toCharArray()));
		}
		
		return list;
	}
	
	protected String sort (final char[] arr)
	{
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
