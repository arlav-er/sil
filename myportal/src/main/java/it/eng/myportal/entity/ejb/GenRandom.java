package it.eng.myportal.entity.ejb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Random;

public class GenRandom {

	private static final String META_INF_LOREM_IPSUM_TXT = "META-INF/lorem_ipsum.txt";
	private String loremIpsum = null;
	private String[] pezziLoremIpsum = null;

	public Random getR() {
		return r;
	}

	public void setR(Random r) {
		this.r = r;
	}

	private Random r = null;

	public GenRandom() throws IOException {
		java.io.InputStream is = this.getClass().getClassLoader()
				.getResourceAsStream(META_INF_LOREM_IPSUM_TXT);

		loremIpsum = convertStreamToString(is);
		pezziLoremIpsum = loremIpsum.split("\n+");
		r = new Random(new Date().getTime());

	}

	private String convertStreamToString(InputStream is) throws IOException {
		/*
		 * To convert the InputStream to String we use the Reader.read(char[]
		 * buffer) method. We iterate until the Reader return -1 which means
		 * there's no more data to read. We use the StringWriter class to
		 * produce the string.
		 */
		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is,
						"UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}

	/**
	 * 
	 * @return mail generata a caso dal testo latino
	 */

	public String getEmail() {

		String frase = getParagrafo();
		String[] pezzi = frase.split("[\\s.,;]+");

		int d = pezzi.length;

		String email = pezzi[r.nextInt(d)] + "." + pezzi[r.nextInt(d)] + "@"
				+ pezzi[r.nextInt(d)] + "." + "spqr.com";
		return email.toLowerCase();

	}

	/**
	 * Il paragrafo e' lungo almeno 200 char
	 * 
	 * @return un singolo paragrafo del testo latino
	 */

	public String getParagrafo() {
		int index = r.nextInt(pezziLoremIpsum.length);
		return pezziLoremIpsum[index];
	}

	/**
	 * Il paragrafo e' lungo almeno 200 char
	 * 
	 * @param maxlen
	 *            lunghezza massima del testo restituito
	 * @return uno a piu' paragrafi del testo latino
	 */

	public String getParagrafi(int maxlen) {
		StringBuilder sb = new StringBuilder(maxlen);
		String next = getParagrafo();

		while (next.length() + sb.length() < maxlen) {
			sb.append(next);
			next = getParagrafo();
		}

		return sb.toString();
	}

	/**
	 * Per proposizione si intende una parte di paragrafo delimitato da punti.
	 * 
	 * @param maxlen
	 *            lunghezdza massima della proposizione restituita
	 * @return la proposizione
	 */

	public String getProposizione(int maxlen) {

		String frase = getParagrafo();
		String[] pezzi = frase.split("\\.");

		int d = 0;

		StringBuilder sb = new StringBuilder(maxlen);
		String next = pezzi[d];

		while (next.length() + sb.length() < maxlen && d < pezzi.length - 1) {
			sb.insert(0, next + ". ");
			next = pezzi[++d];
		}

		return sb.toString().trim();

	}
	
public String getVotazione(){
	
	return String.format("%d/110", r.nextInt(110) +1);
}

public int getAnnoDecennio(){
	
	return r.nextInt(10) +2000;
}



public String  getDurata(){
	
	return "" + (r.nextInt(100) +1) + " mesi" ;
}


	
	
	
	public PoiVO getPoi() {
		PoiVO poi = new PoiVO();

		poi.setLon( 2.59 * r.nextDouble() + 9.73);
		poi.setLat( 0.77 * r.nextDouble() + 44.22);
		return poi;

	}
	
	
	
}
