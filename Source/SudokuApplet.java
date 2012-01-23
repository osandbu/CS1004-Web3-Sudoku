import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * The SudokuApplet displays a Sudoku grid which has it's initial cell content
 * given by a parameter ("puzzle"). It also allows a user to complete the puzzle
 * and submit their email and solution.
 * 
 * @author Ole Sandbu
 */
public class SudokuApplet extends JApplet implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static final int COMPONENT_GAP_X = 0;
	private static final int COMPONENT_GAP_Y = 5;
	private JTextField[][] fields;
	private JButton sendButton;
	private JTextField emailField;

	/**
	 * Initialise the content of the Applet.
	 */
	public void init() {
		JPanel grid = initGrid();
		JPanel buttonPanel = initButtonPanel();
		setLayout(new BorderLayout(COMPONENT_GAP_X, COMPONENT_GAP_Y));
		add(grid, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		String puzzle = getParameter("puzzle");
		if (puzzle != null)
			setCellContent(puzzle);
	}

	/**
	 * Initalize the Sudoku 9x9 grid.
	 * 
	 * @return The grid.
	 */
	private JPanel initGrid() {
		JPanel grid = new JPanel();
		// create a new 9x9 grid
		GridLayout gridLayout = new GridLayout(9, 9);
		grid.setLayout(gridLayout);
		fields = new JTextField[9][9];
		for (int row = 0; row < 9; row++)
			for (int col = 0; col < 9; col++) {
				JTextField field = new JTextField();
				field.setName(row + "," + col);
				field.setHorizontalAlignment(JTextField.CENTER);
				grid.add(field);
				field.addKeyListener(new SudokuListener(fields, field));
				fields[row][col] = field;
			}
		return grid;
	}

	/**
	 * Initalise a panel containing a textfield in which the user can enter
	 * their email and a submit button.
	 * 
	 * @return A JPanel.
	 */
	private JPanel initButtonPanel() {
		JPanel buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.add(new JLabel("Email:"), BorderLayout.WEST);
		emailField = new JTextField();
		emailField.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent evt) {
				if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
					sendSolution();
				}
			}
		});
		buttonPanel.add(emailField, BorderLayout.CENTER);
		sendButton = new JButton("Submit");
		sendButton.addActionListener(this);
		buttonPanel.add(sendButton, BorderLayout.EAST);
		return buttonPanel;
	}

	/**
	 * Returns the content of the cells as an String of 81 characters containing
	 * numbers 0-9. 0 means that nothing has been entered into the corresponding
	 * cell while a number between 1 and 9 means that that number has been
	 * entered.
	 * 
	 * @return The content of the Sudoku grid.
	 */
	public String getCellContent() {
		StringBuilder sb = new StringBuilder();
		for (int row = 0; row < 9; row++)
			for (int col = 0; col < 9; col++) {
				String text = fields[row][col].getText();
				if (text.length() == 0)
					sb.append('0');
				else
					sb.append(text);
			}
		return sb.toString();
	}

	/**
	 * Convert a 81 character String to a 9x9 character array.
	 * 
	 * @param gridContent
	 * @return A 9x9 character array.
	 */
	public char[][] getGrid(String gridContent) {
		char[][] grid = new char[9][9];
		int idx = 0;
		for (int row = 0; row < 9; row++)
			for (int col = 0; col < 9; col++) {
				grid[row][col] = gridContent.charAt(idx);
				idx++;
			}
		return grid;
	}

	/**
	 * Set the content of the cells using a given String of 81 numbers
	 * corresponding to each of the cells.
	 * 
	 * @param gridContent
	 *            The cell content.
	 */
	public void setCellContent(String gridContent) {
		char[][] grid = getGrid(gridContent);
		for (int row = 0; row < 9; row++)
			for (int col = 0; col < 9; col++) {
				if (grid[row][col] > '0') {
					fields[row][col]
							.setText(Character.toString(grid[row][col]));
					fields[row][col].setFocusable(false);
					fields[row][col].setEditable(false);
				}
			}
	}

	/**
	 * Implements ActionListener.actionPerformed(ActionEvent). Makes the send
	 * button send the solution to the server.
	 */
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == sendButton) {
			sendSolution();
		}
	}

	/**
	 * Send the solution given in the grid to the server if it has been
	 * completed and a valid email address has been entered.
	 */
	private void sendSolution() {
		if (allCellsFilled()) {
			String email = emailField.getText();
			if (!Validator.isValidEmail(email)) {
				reportInvalidEmail();
				return;
			}
			String solution = getCellContent();
			String outputURL = getParameter("outputURL") + "?email=" + email
					+ "&solution=" + solution;
			try {
				getAppletContext().showDocument(new URL(outputURL));
			} catch (MalformedURLException e) {
				reportMalformedURL();
			}
		} else {
			reportIncomplete();
		}
	}

	/**
	 * Make a popup appear reporting that an invalid email address has been
	 * entered.
	 */
	private void reportInvalidEmail() {
		String message = "Please enter a valid email address.";
		String title = "Invalid email";
		reportError(message, title);
	}

	/**
	 * Make a popup appear reporting that the applet's outputURL argument is
	 * invalid.
	 */
	private void reportMalformedURL() {
		String message = "There is an error in the applet's outputURL argument.\nPlease contact the system administator.";
		String title = "Error";
		reportError(message, title);
	}

	/**
	 * Determines if all the cells have been filled in.
	 * 
	 * @return true if all the cells have been filled, false otherwise.
	 */
	private boolean allCellsFilled() {
		for (int row = 0; row < 9; row++)
			for (int col = 0; col < 9; col++)
				if (fields[row][col].getText().length() == 0)
					return false;
		return true;
	}

	/**
	 * Make a popup appear reporting that the puzzle needs to be completed
	 * before it can be submitted.
	 */
	public void reportIncomplete() {
		String message = "Please fill in all the cells before submitting.";
		String title = "Incomplete";
		reportError(message, title);
	}

	/**
	 * Make a popup appear reporting an error message with a given message and
	 * title.
	 * 
	 * @param message
	 *            A message.
	 * @param title
	 *            A title.
	 */
	private void reportError(String message, String title) {
		JOptionPane.showConfirmDialog(null, message, title,
				JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
	}
}
