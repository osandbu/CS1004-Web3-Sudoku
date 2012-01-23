import java.util.*;
import java.io.*;

/**
 * The first part of this program deals with extracting the data passed by the
 * server using the CGI interface and storing it in the object formInput
 * 
 * You do not need to understand this. You only need to use the get and
 * getCheckBox methods in your own code.
 */
public class CgiApp {

	private Hashtable<String, String> formInput;
	Properties p;

	/**
	 * Constructor for a CgiApp, will parse the query into a table for access
	 * through get methods
	 */
	public CgiApp() {
		p = System.getProperties();
		formInput = new Hashtable<String, String>(31);
		parseQuery();
	}

	private void parseQuery() {
		String requestMethod = p.getProperty("request.method");
		String input = null;

		if (requestMethod != null && requestMethod.equals("POST")) {

			int contentLength = 0;
			String s = p.getProperty("content.length");

			if (s != null)
				contentLength = java.lang.Integer.parseInt(s);

			if (contentLength > 0) {
				byte buffer[] = new byte[contentLength];
				int bytesToRead = contentLength;
				int bytesRead = 0;
				int count = 0;
				do {
					try {
						count = System.in.read(buffer, bytesRead, bytesToRead);
					} catch (IOException e) {
					}
					bytesRead += count;
					bytesToRead -= count;
				} while (bytesToRead > 0);
				input = new String(buffer, 0, contentLength);
			}
		} else {
			input = p.getProperty("query.string");
		}
		StringTokenizer t = new StringTokenizer(input, "&\n");
		while (t.hasMoreTokens())
			put(t.nextToken());
	}

	private String unescape(String s) {
		String target = new String();
		String source = s;
		int nextEscape;

		do {
			nextEscape = source.indexOf('%');

			if (nextEscape != -1) {
				String header = source.substring(0, nextEscape);
				String trailer = source.substring(nextEscape + 3);
				String escape = source
						.substring(nextEscape + 1, nextEscape + 3);

				byte charValue[] = new byte[1];

				try {
					charValue[0] = (byte) Integer.parseInt(escape, 16);
				} catch (NumberFormatException e) {
				}

				String unescaped = new String(charValue, 0, 1);
				target = target.concat(header);
				target = target.concat(unescaped);
				source = trailer;
			} else
				target = target.concat(source);

		} while (nextEscape != -1);

		return target;
	}

	private void put(String s) {
		// First, change + to space

		s = s.replace('+', ' ');

		int equalSignPos = s.indexOf('=');
		if (equalSignPos > 0) {

			String key = unescape(s.substring(0, equalSignPos));

			String value = unescape(s.substring(equalSignPos + 1));

			formInput.put(key, value);
		}
	}

	/**
	 * recover the value of the field named `key'
	 * 
	 * @param key
	 *            the name of the checkbox
	 * @return the value associated with the key
	 */
	public String get(String key) {
		return formInput.get(key);
	}

	/**
	 * recover a Boolean indicating whether a particular checkbox was selected
	 * 
	 * @param key
	 *            the name of the checkbox
	 * @return true if the checkbox was selected
	 */

	public boolean getCheckbox(String key) {
		return formInput.get(key) != null;
	}

	/**
	 * Convenience method for retrieving the XHTML header
	 * 
	 * @return the XHTML header from Content-Type to the close of the <html> tag
	 */
	public static String getXHTMLHeader() {
		String out = "Content-Type: text/html\n\n";
		out += "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n";
		out += "<!DOCTYPE html PUBLIC  \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n";
		out += "<html  xmlns=\"http://www.w3.org/1999/xhtml\">\n";
		return out;
	}

	/**
	 * Gets the names of all the values sent from the form as an array of
	 * Strings.
	 * 
	 * @return Return n array containing all the names from the name=value pairs
	 */
	public String[] getAllNames() {
		Enumeration<String> keys = formInput.keys();
		ArrayList<String> keyArr = new ArrayList<String>();
		while (keys.hasMoreElements())
			keyArr.add(keys.nextElement());
		return keyArr.toArray(new String[keyArr.size()]);
	}

	/**
	 * This method is used to automatically generate a web page listing all the
	 * data from the form without knowing anything about the form. Students
	 * should write an application which makes use of the fact that you know
	 * what the fields in the form are, and what their values mean, to produce a
	 * more meaningful page to the client.
	 */

	public void dumpAll() {
		dumpAll(System.out);
	}

	/**
	 * This method is used to automatically generate a web page listing all the
	 * data from the form without knowing anything about the form. Students
	 * should write an application which makes use of the fact that you know
	 * what the fields in the form are, and what their values mean, to produce a
	 * more meaningful page to the client.
	 * 
	 * @param out
	 *            the PrintStream to write the output to, System.out will send
	 *            to the browser, System.err will send to the error_log
	 */
	public void dumpAll(PrintStream out) {
		out.println("Content-Type: text/html");
		out.println("");
		out.println("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>");
		out
				.println("<!DOCTYPE html PUBLIC  \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
		out.println("<html  xmlns=\"http://www.w3.org/1999/xhtml\">");
		out.print("  <head>\n    <title>\n");
		out.println("      CGI Test Application Output");
		out.print("    </title>\n  </head>\n  <body>\n");
		out.println("    <h1>CGI Test Application Output</h1>");
		out.println("    <h2>Form Data</h2>");
		out.println("    <dl>");

		// This is a device to run through all the fields from the form

		Enumeration<String> fieldsEnum = formInput.keys();
		while (fieldsEnum.hasMoreElements()) {
			String key = fieldsEnum.nextElement();

			// Now we've found a field called `key', so we add it to the page

			out.println("      <dt>" + key + "</dt>");
			out.println("      <dd>" + formInput.get(key) + "</dd>");
		}

		// Now we've seen all the fields, so we finish off the page

		out.print("   </dl>\n  </body>\n</html>\n");

	}

}
