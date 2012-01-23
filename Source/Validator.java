import java.util.regex.Pattern;

public class Validator {
	private static final String VALID_EMAIL_REGEX = "\\b[\\w.%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}\\b";
	private static final Pattern VALID_EMAIL_PATTERN = Pattern
			.compile(VALID_EMAIL_REGEX);

	/**
	 * Determines if an email address is valid.
	 * 
	 * @param email
	 *            An email address.
	 * @return true if the given argument is a valid email address, otherwise
	 *         return false.
	 */
	public static boolean isValidEmail(String email) {
		return email != null && VALID_EMAIL_PATTERN.matcher(email).matches();
	}
}
