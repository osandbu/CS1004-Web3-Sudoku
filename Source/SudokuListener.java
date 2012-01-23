import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JTextField;

/**
 * SudokuListener class listens for KeyEvents in each of the textfields in the
 * sudoku-grid. It only allows a single number to be input. The implementation
 * is based on the code provided on Studres which was written by Kevin Hammond.
 * 
 * @author Ole Sandbu
 */
public class SudokuListener extends KeyAdapter {
	private static final Color VALID_COLOR = Color.WHITE;
	private static final Color INVALID_COLOR = Color.PINK;
	private static final Color UNEDITABLE_VALID_COLOR = new Color(238, 238, 238);
	private static final Color UNEDITABLE_INVALID_COLOR = new Color(247, 207,
			207);
	private ArrayList<JTextField> crashes = new ArrayList<JTextField>();
	private JTextField[][] fields;
	private JTextField tf;

	/**
	 * Create a new SudokuListener which makes a JTextField only accept number
	 * input.
	 * 
	 * @param fields
	 *            A 2d array of JTextFields.
	 */
	public SudokuListener(JTextField[][] fields, JTextField tf) {
		this.fields = fields;
		this.tf = tf;
	}

	/**
	 * Verifies that the user input is valid, i.e. that it is an integer in the
	 * range 1..9
	 * 
	 * Overrides the default keyTyped method
	 * 
	 * @param e
	 *            (KeyEvent) - The event describing the key that has been
	 *            pressed.
	 */
	public void keyTyped(KeyEvent e) {
		// check that the key is in the correct range, and if so set the text
		if (e.getKeyChar() >= '1' && e.getKeyChar() <= '9') {
			tf.setText(String.valueOf(e.getKeyChar()));
			// make the cursor appear after the character
			tf.setSelectionStart(1);
			tf.setSelectionEnd(1);
		} else {
			// clear the current value of the text field
			tf.setText("");
		}
		setColor();
		// consume the input
		e.consume();
	}

	/**
	 * Check if the number in a cell is valid, and set the color accordingly.
	 * 
	 * @param tf
	 *            A textfield.
	 */
	private void setColor() {
		resetCrashes();
		if (isValid()) {
			if (tf.isEditable())
				tf.setBackground(VALID_COLOR);
			else
				tf.setBackground(UNEDITABLE_VALID_COLOR);
		} else {
			if (tf.isEditable())
				tf.setBackground(INVALID_COLOR);
			else
				tf.setBackground(UNEDITABLE_INVALID_COLOR);
		}
	}

	/**
	 * Reset the fields crashing with this field.
	 */
	private void resetCrashes() {
		for (JTextField f : crashes) {
			SudokuListener sl = (SudokuListener) f.getKeyListeners()[0];
			// uncrash fields
			sl.crashes.remove(tf);
			// if the other field no longer crashes with another field
			if (!sl.crashes())
				setValid(f);
		}
		crashes.clear();
	}

	/**
	 * Checks if this field crashes with another field. I.e. if the value in the
	 * textfield is valid in it's row, column and subgrid.
	 * 
	 * @return true if it crashes, false otherwise.
	 */
	private boolean crashes() {
		return crashes.size() > 0;
	}

	/**
	 * Check if the value in the textfield is valid in it's row, column and
	 * subgrid.
	 * 
	 * @param tf
	 *            A textfield.
	 * @return If it is the same as another number in the same row, column or
	 *         subgrid, return false, otherwise return true.
	 */
	private boolean isValid() {
		String text = tf.getText();
		String[] split = tf.getName().split(",");
		int row = Integer.parseInt(split[0]);
		int col = Integer.parseInt(split[1]);
		boolean validRow = validInRow(text, row, col);
		boolean validCol = validInCol(text, row, col);
		boolean validSubgrid = validInSubgrid(text, row, col);
		return validRow && validCol && validSubgrid;
	}

	/**
	 * Determines if the number in a cell is valid in its row.
	 * 
	 * @param text
	 *            The text in the cell.
	 * @param tfrow
	 *            The row number.
	 * @param tfcol
	 *            The column number.
	 * @return If it is the same as another number in its row, return false,
	 *         otherwise return true.
	 */
	private boolean validInRow(String text, int tfrow, int tfcol) {
		boolean valid = true;
		for (int col = 0; col < 9; col++) {
			// skip the edited cell.
			if (col == tfcol)
				continue;
			if (!text.equals("") && text.equals(fields[tfrow][col].getText())) {
				valid = false;
				setInvalid(fields[tfrow][col]);
			}
		}
		return valid;
	}

	/**
	 * Determines if the number in a cell is valid in its column.
	 * 
	 * @param text
	 *            The text in the cell.
	 * @param tfrow
	 *            The row number.
	 * @param tfcol
	 *            The column number.
	 * @return If it is the same as another number in its column, return false,
	 *         otherwise return true.
	 */
	private boolean validInCol(String text, int tfrow, int tfcol) {
		boolean valid = true;
		for (int row = 0; row < 9; row++) {
			// skip the edited cell.
			if (row == tfrow)
				continue;
			if (!text.equals("") && text.equals(fields[row][tfcol].getText())) {
				valid = false;
				setInvalid(fields[row][tfcol]);
			}
		}
		return valid;
	}

	/**
	 * Determines if the number in a cell is valid in its subgrid.
	 * 
	 * @param text
	 *            The text in the cell.
	 * @param tfrow
	 *            The row number.
	 * @param tfcol
	 *            The column number.
	 * @return If it is the same as another number in its subgrid, return false,
	 *         otherwise return true.
	 */
	public boolean validInSubgrid(String text, int tfrow, int tfcol) {
		boolean valid = true;
		// find start row of sub-grid
		int row = tfrow / 3 * 3;
		// find start col of sub-grid
		int col = tfcol / 3 * 3;
		for (int r = row; r < row + 3; r++) {
			for (int c = col; c < row + 3; c++) {
				// skip the edited cell.
				if (r == tfrow && c == tfcol)
					continue;
				if (!text.equals("") && text.equals(fields[r][c].getText())) {
					valid = false;
					setInvalid(fields[r][c]);
				}
			}
		}
		return valid;
	}

	/**
	 * Set the color of a cell (JTextField) to one which indicated that the
	 * number in it is valid. This color different depending on whether the cell
	 * is editable or not.
	 * 
	 * @param tf
	 *            A textfield.
	 */
	public void setValid(JTextField tf) {
		if (tf.isEditable())
			tf.setBackground(VALID_COLOR);
		else
			tf.setBackground(UNEDITABLE_VALID_COLOR);
	}

	/**
	 * Set the color of a cell (JTextField) to one which indicated that the
	 * number in it is invalid. This color different depending on whether the
	 * cell is editable or not.
	 * 
	 * @param tf
	 *            A textfield.
	 */
	public void setInvalid(JTextField tf) {
		crashes.add(tf);
		SudokuListener sl = (SudokuListener) tf.getKeyListeners()[0];
		sl.crashes.add(this.tf);
		if (tf.isEditable())
			tf.setBackground(INVALID_COLOR);
		else
			tf.setBackground(UNEDITABLE_INVALID_COLOR);
	}

	public boolean isInvalidColor(JTextField tf) {
		Color bg = tf.getBackground();
		return bg.equals(INVALID_COLOR) || bg.equals(UNEDITABLE_INVALID_COLOR);
	}

	/**
	 * Listens for key presses. If any of the arrow keys are pressed, the cursor
	 * is moved to the closest text field in the direction of the arrow
	 * movement.
	 */
	public void keyPressed(KeyEvent e) {
		JTextField tf = (JTextField) e.getSource();
		int code = e.getKeyCode();
		if (code == KeyEvent.VK_RIGHT) {
			nextField(tf);
		} else if (code == KeyEvent.VK_LEFT) {
			previousField(tf);
		} else if (code == KeyEvent.VK_DOWN) {
			selectFieldBelow(tf);
		} else if (code == KeyEvent.VK_UP) {
			selectFieldAbove(tf);
		}
	}

	/**
	 * Move the cursor to the first editable textfield below the currently
	 * selected one, if there is none, select the first editable textfield in
	 * the same column.
	 * 
	 * @param tf
	 *            The textfield in which the cursor currently is.
	 */
	private void selectFieldBelow(JTextField tf) {
		String[] split = tf.getName().split(",");
		int row = Integer.parseInt(split[0]);
		int col = Integer.parseInt(split[1]);
		do {
			row++;
			if (row >= 9)
				row = 0;
		} while (!fields[row][col].isEditable());
		fields[row][col].requestFocus();
	}

	/**
	 * Move the cursor to the first editable textfield above the currently
	 * selected one, if there is none, move the cursor to the last editable
	 * textfield in the same column.
	 * 
	 * @param tf
	 *            The textfield in which the cursor currently is.
	 */
	private void selectFieldAbove(JTextField tf) {
		String[] split = tf.getName().split(",");
		int row = Integer.parseInt(split[0]);
		int col = Integer.parseInt(split[1]);
		do {
			row--;
			if (row < 0)
				row = 8;
		} while (!fields[row][col].isEditable());
		fields[row][col].requestFocus();
	}

	/**
	 * Move the cursor to the next editable textfield. This method simulates,
	 * and is therefore equivalent to, pressing the tab key.
	 * 
	 * @param tf
	 *            The textfield in which the cursor currently is.
	 */
	private void nextField(JTextField tf) {
		pressTab(tf, 0);
	}

	/**
	 * Move the cursor to the previous editable textfield. This method
	 * simulates, and is therefore equivalent to, pressing the shift-tab keys.
	 * 
	 * @param tf
	 *            The textfield in which the cursor currently is.
	 */
	private void previousField(JTextField tf) {
		pressTab(tf, KeyEvent.SHIFT_DOWN_MASK);
	}

	/**
	 * Presses tab and a given modifier.
	 * 
	 * @param textfield
	 *            The textfield in which the cursor currently is.
	 * @param modifier
	 *            A modifier keycode, or 0 for none.
	 */
	private void pressTab(JTextField textfield, int modifier) {
		KeyEvent tab = new KeyEvent(textfield, KeyEvent.KEY_PRESSED, System
				.currentTimeMillis(), modifier, KeyEvent.VK_TAB,
				KeyEvent.CHAR_UNDEFINED);
		textfield.dispatchEvent(tab);
	}
}