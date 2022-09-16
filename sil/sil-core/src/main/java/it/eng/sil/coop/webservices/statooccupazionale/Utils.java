package it.eng.sil.coop.webservices.statooccupazionale;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.activation.DataHandler;

public class Utils {

	private static final int BUFFER_SIZE = 4096;

	public static byte[] getBytesFromDataHandler(DataHandler data) throws IOException {
		InputStream in = null;
		byte out[] = null;
		in = data.getInputStream();
		if (in != null) {
			out = new byte[in.available()];
			in.read(out);
		} else {
			out = new byte[0];
		}
		return out;
	}

	/**
	 * Loads the contents of a file into a java.lang.String
	 *
	 * @param fileName
	 *            the file to load into a string
	 * @return the contents of the file loaded into String format
	 */
	public static String extractContents(String fileName) throws IOException {
		StringBuffer buffer = new StringBuffer();

		Reader reader = new FileReader(fileName);
		char[] cbuf = new char[BUFFER_SIZE];
		try {
			while (reader.read(cbuf) > 0) {
				buffer.append(cbuf);
				// memory is cheap - nullify the old cbuf so GC will collect & create new memory in a 4k chunk
				cbuf = null;
				cbuf = new char[BUFFER_SIZE];
			}
			return buffer.toString().trim();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (Throwable t) {
				// do nothing
			}
		}
	}

}
