import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;

/**
 * The sudoku class is a CGI application which takes two parameters, "solution"
 * and "email", and, if they are found to be valid, appends them to a file.
 * Then, if everything has been completed successfully, it prints a message to
 * the user saying "Thank you."
 * 
 * @author Ole Sandbu
 */
public class sudoku extends CgiApp {
	private static final String FILE_NAME = "/cs/home/os75/public_html/sudoku/solutions.txt";

	// regular expression which matches strings containing exactly 81 numbers.
	private static final String SOLUTION_REGEX = "[\\d]{81}";
	private static final Pattern SOLUTION_PATTERN = Pattern
			.compile(SOLUTION_REGEX);

	/**
	 * Constructor which reads the arguments from the URL.
	 */
	public sudoku() {
		super();
	}

	/**
	 * Main method which is called when the CGI script is opened. Reads the
	 * arguments and attempts to save them to a file. Says thank you if
	 * everything is completed successfully, otherwise reports the error which
	 * has occured.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		sudoku sudoku = new sudoku();
		String email = sudoku.get("email");
		if (!Validator.isValidEmail(email)) {
			printXHTMLPage("Invalid email",
					"The solution was not submitted, because the email provided is invalid.");
			return;
		}
		String solution = sudoku.get("solution");
		if (!isValidSolution(solution)) {
			printXHTMLPage("Invalid solution",
					"The solution was not submitted, because it was found to be invalid.");
			return;
		}
		try {
			appendFile(email, solution);
		} catch (IOException e) {
			printXHTMLPage("Error", e.toString());
			return;
		}
		printThankYouPage();
	}

	/**
	 * Append an email and a solution to a Sudoku puzzle to the file with all
	 * the solutions.
	 * 
	 * @param email
	 *            An email address.
	 * @param solution
	 *            The given solution.
	 * @throws IOException
	 *             If an error occurs while attempting to write to the file.
	 */
	private static void appendFile(String email, String solution)
			throws IOException {
		PrintWriter out = new PrintWriter(new FileWriter(FILE_NAME, true));
		out.println(email + " " + solution);
		out.close();
	}

	/**
	 * Print a webpage saying "Thank you" to the user.
	 */
	public static void printThankYouPage() {
		printXHTMLPage("Thank you", "<p>Thank you</p>");
	}

	/**
	 * Print a XHTML document to the user.
	 * 
	 * @param title
	 *            The title of the document.
	 * @param content
	 *            The content (body) of the document.
	 */
	private static void printXHTMLPage(String title, String content) {
		printXHTMLHeader(title);
		System.out.println(content);
		System.out.println("</body></html>");
	}

	/**
	 * Print a XHTML-header with a given title.
	 * 
	 * @param title
	 *            A title.
	 */
	private static void printXHTMLHeader(String title) {
		System.out.println(CgiApp.getXHTMLHeader());
		System.out.println("<head><title>" + title + "</title></head><body>");
	}

	/**
	 * Determines if a string is a valid solution to a a sudoku puzzle.
	 * 
	 * @param solution
	 *            A solution to be validated.
	 * @return true if the solution is valid, false otherwise.
	 */
	private static boolean isValidSolution(String solution) {
		return solution != null && SOLUTION_PATTERN.matcher(solution).matches();
	}
}