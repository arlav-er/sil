package it.eng.myportal.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.ejb.EJBException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.dom.ElementNSImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import it.eng.myportal.auth.FacebookAppSingleton;
import it.eng.myportal.auth.FederaAppSingleton;
import it.eng.myportal.auth.GoogleAppSingleton;
import it.eng.myportal.auth.IcarAppSingleton;
import it.eng.myportal.auth.TwitterAppSingleton;
import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.enums.ErroriFissaAppuntamento;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.siler.appuntamento.fissaAppuntamento.output.Risposta;

public class Utils {

	public static final String DES_KEY = "TODOTODO";
	private static Log log = LogFactory.getLog(Utils.class);

	/**
	 * Fa il trim di una stringa HTML. Elimina tutti gli spazi, e tag BR e i caratteri &nbsp; dall'inizio e fine di una
	 * stringa
	 * 
	 * @param string
	 *            stringa HTML
	 * @return stringa HTML dopo il trim
	 */
	public static String trimHTML(String string) {
		String returnString = "";
		if (string != null) {
			StringBuilder sb = new StringBuilder(string);

			// pattern da eliminare
			String patternString = "((&nbsp;)|(\\s)|(<br>)|(</br>)|(<br />))";

			// cerca il patterno solo all'inizio della stringa
			Pattern leadingSpaces = Pattern.compile("^" + patternString + "*", Pattern.CASE_INSENSITIVE);
			// cerca il pattern solo alla fine della stringa
			Pattern trailingSpaces = Pattern.compile(patternString + "*$", Pattern.CASE_INSENSITIVE);

			// match dei pattern nella stringa
			Matcher matcher;

			matcher = leadingSpaces.matcher(sb.toString());
			sb = new StringBuilder(matcher.replaceAll(""));

			matcher = trailingSpaces.matcher(sb.toString());
			sb = new StringBuilder(matcher.replaceAll(""));

			returnString = sb.toString();
		}

		return returnString;
	}

	/**
	 * Questo metodo applica il trimHTML sulla stringa. Se questa risulta vuota allora restituisce null, altrimenti
	 * restituisce la stringa originale
	 * 
	 * @param stringHTML
	 *            la stringa da controllare
	 * @return null se il trimHTML restituisce una stringa vuota, la stringa originale altrimenti
	 */
	public static String nullIfEmptyHTML(String stringHTML) {
		String stringHTMLTrim = trimHTML(stringHTML);

		if (stringHTMLTrim.isEmpty()) {
			stringHTML = null;
		}

		return stringHTML;
	}

	public static class MD5 {

		public static String encrypt(String message) {
			try {
				MessageDigest m = MessageDigest.getInstance("MD5");
				m.update(message.getBytes());
				return String.format("%032x", new BigInteger(1, m.digest()));
			} catch (Exception e) {
				return null;
			}
		}
	}

	public static class SHA1 {

		public static String encrypt(String message) {
			Formatter formatter = new Formatter();
			try {
				MessageDigest m = MessageDigest.getInstance("SHA1");
				m.update(message.getBytes());

				for (byte b : m.digest()) {
					formatter.format("%02x", b);
				}
				String encryptedString = formatter.toString();
				return encryptedString;
			} catch (Exception e) {
				return null;
			} finally {
				formatter.close();
			}
		}

		public static boolean compare(String plainMessage, String encryptedMessage) {
			String encryptedPlainMessage = encrypt(plainMessage);
			if (encryptedPlainMessage != null && encryptedPlainMessage.equals("")) {
				return encryptedMessage.equals(encryptedPlainMessage);
			}
			return false;
		}
	}

	public static class SHA256 {

		public static String encrypt(String message) {
			Formatter formatter = new Formatter();
			try {
				MessageDigest m = MessageDigest.getInstance("SHA-256");
				m.update(message.getBytes());

				for (byte b : m.digest()) {
					formatter.format("%02x", b);
				}
				String encryptedString = formatter.toString();
				return encryptedString;
			} catch (Exception e) {
				return null;
			} finally {
				formatter.close();
			}
		}
	}

	public static class PDF {
		public static InputStream htmlToPDF(String html, String documentPath) {
			if (html == null) {
				return null;
			}

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			com.itextpdf.text.Document document = new com.itextpdf.text.Document();
			try {
				PdfWriter writer = PdfWriter.getInstance(document, output);
				document.setPageSize(PageSize.A4);
				document.setMargins(10, 10, 15, 10);
				document.open();

				StringBuilder HTMLtoPDF = new StringBuilder();
				HTMLtoPDF.append(html);
				InputStream stream = new ByteArrayInputStream(HTMLtoPDF.toString().getBytes("UTF-8"));
				XMLWorkerHelper.getInstance().parseXHtml(writer, document, stream);
				document.close();
			} catch (Exception e) {
				String errMsg = "Errore durante la creazione del documento pdf: " + documentPath;
				log.error(errMsg);
				log.error("Eccezione originale: " + e.getClass() + " - " + e.getLocalizedMessage());
				throw new MyPortalException(errMsg, e, true);
			}
			byte[] data = output.toByteArray();
			return new ByteArrayInputStream(data);
		}

		public static String htmltoXHTML(String html) {
			InputStream htmlIS = new ByteArrayInputStream(html.getBytes(Charset.forName("UTF-8")));
			ByteArrayOutputStream htmlOutput = new ByteArrayOutputStream();
			Tidy tidy = new Tidy(); // HTML parser and pretty printer.
			tidy.setXHTML(true); // true if tidy should output XHTML
			tidy.setInputEncoding("UTF-8");
			tidy.setOutputEncoding("UTF-8");
			tidy.setQuiet(true);
			tidy.setShowErrors(0);
			tidy.setShowWarnings(false);
			tidy.setPrintBodyOnly(true);
			tidy.parse(htmlIS, htmlOutput);
			return new String(htmlOutput.toByteArray(), Charset.forName("UTF-8"));
		}
	}

	public static class MyPortalStringUtils {
		/**
		 * Converte una stringa che rappresenta una lista in un oggetto di tipo List<String>, la stringa di input deve
		 * avere il formato: "[<elemento> <elemento> ...]"
		 * 
		 * @param listString
		 * @return
		 */
		public static List<String> toList(String listString) {
			List<String> list = new ArrayList<String>();

			/* converto la stringa in lista di stringhe */
			listString = org.apache.commons.lang3.StringUtils.replace(listString, "[", "");
			listString = org.apache.commons.lang3.StringUtils.replace(listString, "]", "");
			Collections.addAll(list, listString.split(", "));

			return list;
		}
	}

	public static class CodiceFiscale {

		public static String verificaAzienda(String codiceFiscale) {
			String errorMessage = "";
			codiceFiscale = codiceFiscale.toUpperCase();

			Pattern p = Pattern
					.compile("[A-Z]{6}[0-9LMNPQRSTUV]{2}[A-Z][0-9LMNPQRSTUV]{2}[A-Z][0-9LMNPQRSTUV]{3}[A-Z]|[0-9]{11}");
			Matcher m = p.matcher(codiceFiscale);
			boolean matchFound = m.matches();

			if (!matchFound) {
				errorMessage = "Formato errato.";
			}

			return errorMessage;
		}

		/**
		 * 
		 * Esegue una verifica parziale del codice fiscale controllandone lunghezza, caratteri numerici e caratteri
		 * alfabetici, check digit. Effettua un controllo valido anche per gli omocodi
		 * 
		 * @param codiceFiscale
		 *            String codice fiscale da esaminare
		 * @return messaggio di errore se il controllo fallisce, stringa vuota altrimenti
		 */
		public static String verificaPersona(String codiceFiscale) {
			/* "normalizzo" il codice fiscale */
			String codfiscaleOrig = StringUtils.defaultString(codiceFiscale).toUpperCase();
			String errorMessage = "";

			// controllo codice fiscale temporaneo da 11 cifre
			if (codfiscaleOrig.length() == 11) {
				if (!CfUtils.checkNumericoCF(codiceFiscale)) {
					return "Il codice fiscale numerico è errato.";
				}
			} else {
				// controllo della lunghezza
				if (!(codfiscaleOrig.length() == 16)) {
					return "Il codice fiscale deve essere lungo 16 caratteri.";
				}
				String codfiscaleNoOmocodia = replaceOmocodia(codfiscaleOrig);
				// controllo omocodia
				if (codfiscaleNoOmocodia == null) {
					errorMessage = "Il codice fiscale è errato.";
				}
				// controllo della correttezza posizionale dei caratteri
				// numerici
				else if (!checkCaratteriNumerici(codfiscaleNoOmocodia)) {
					errorMessage = "Controllo caratteri numerici fallito.";
				}
				// controllo della correttezza posizionale dei caratteri
				// alfabetici
				else if (!checkCaratteriAlfabetici(codfiscaleNoOmocodia)) {
					errorMessage = "Controllo caratteri alfabetici fallito.";
				}
				// controllo del check digit sul codice fiscale originale
				else if (!checkDigit(codfiscaleOrig)) {
					errorMessage = "Il codice di controllo è errato.";
				}
				// controllo del carattere del mese
				else if (!checkCarattereMese(codfiscaleNoOmocodia)) {
					errorMessage = "Errore nel carattere che indica il mese.";
				}
			}
			return errorMessage;
		}

		/**
		 * checkCaratteriNumerici Controlla la correttezza posizionale dei caratteri numerici.
		 * 
		 * @param codfiscale
		 *            String Il codice fiscale
		 * @return boolean true se i caratteri numerici del codice fiscale sono ben posizionati false se i caratteri
		 *         numerici del codice fiscale non sono ben posizionati.
		 */
		private static boolean checkCaratteriNumerici(String codfiscale) {
			boolean ok = true;
			for (int k = 0; (k < codfiscale.length()) && ok; k += 1) {
				char car = codfiscale.charAt(k);
				if (k == 6 || k == 7 || k == 9 || k == 10)// numerici
				{
					/*
					 * i caratteri numerici possono essere anche alfabetici in caso di omonimie se il carattere non è
					 * numerico
					 */
					if (!isDigit(car)) {
						ok = false;
					}
				}

			}
			return ok;
		}

		/**
		 * checkCaratteriAlfabetici Controlla la correttezza posizionale dei caratteri numerici.
		 * 
		 * N.B. solo i primi sei caratteri devono essere alfabetici
		 * 
		 * @param codfiscale
		 *            String
		 * @return boolean true: se i caratteri numerici del codice fiscale sono ben posizionati false: se i caratteri
		 *         numerici del codice fiscale non sono ben posizionati.
		 */
		private static boolean checkCaratteriAlfabetici(String codfiscale) {
			String nomCogn = org.apache.commons.lang3.StringUtils.substring(codfiscale, 0, 6);
			return org.apache.commons.lang3.StringUtils.isAlpha(nomCogn);
		}

		/**
		 * Controlla l'ottavo carattere
		 * 
		 * @param codicefiscale
		 *            String
		 * @return boolean
		 */
		private static boolean checkCarattereMese(String codicefiscale) {
			char c = codicefiscale.charAt(8);
			switch (c) {
			case 'A':
			case 'B':
			case 'C':
			case 'D':
			case 'E':
			case 'H':
			case 'L':
			case 'M':
			case 'P':
			case 'R':
			case 'S':
			case 'T':
				return true;
			default:
				return false;
			}
		}

		/**
		 * Restituisce il codice fiscale originale se, quello passato, rpesenta un'omocodia
		 * 
		 * @param codfiscale
		 * @return il codice fiscale originale, null in caso di errore
		 */
		private static String replaceOmocodia(String codfiscale) {
			char[] arry = codfiscale.toCharArray();

			// sostituisci eventuali lettere con i numeri corrispondenti
			if (!Character.isDigit(arry[6])) {
				arry[6] = replaceOmocodia(arry[6]);
			}
			if (!Character.isDigit(arry[7])) {
				arry[7] = replaceOmocodia(arry[7]);
			}
			if (!Character.isDigit(arry[9])) {
				arry[9] = replaceOmocodia(arry[9]);
			}
			if (!Character.isDigit(arry[10])) {
				arry[10] = replaceOmocodia(arry[10]);
			}
			if (!Character.isDigit(arry[12])) {
				arry[12] = replaceOmocodia(arry[12]);
			}
			if (!Character.isDigit(arry[13])) {
				arry[13] = replaceOmocodia(arry[13]);
			}
			if (!Character.isDigit(arry[14])) {
				arry[14] = replaceOmocodia(arry[14]);
			}

			if (arry[12] == 'X' || arry[13] == 'X' || arry[14] == 'X') {
				return null;
			} else {
				/*
				 * ricompongo il codice. Se qualcosa è andato storto ci sara' una X di mezzo che fa saltare il controllo
				 * successivo.
				 */
				String ncodfiscale = new String(arry);
				return ncodfiscale;
			}
		}

		/**
		 * Tabella B di conversione di cifre in caratteri alfabetici (per omocodici) 0 = L 4 = Q 8 = U 1 = M 5 = R 9 = V
		 * 2 = N 6 = S 3 = P 7 = T
		 * 
		 * @param conIn
		 *            char
		 * @return char
		 */
		private static char replaceOmocodia(char conIn) {
			switch (conIn) {
			case 'L':
				return '0';
			case 'M':
				return '1';
			case 'N':
				return '2';
			case 'P':
				return '3';
			case 'Q':
				return '4';
			case 'R':
				return '5';
			case 'S':
				return '6';
			case 'T':
				return '7';
			case 'U':
				return '8';
			case 'V':
				return '9';
			default:
				return 'X';
			}
		}

		/**
		 * inorridisco, ma non voglio fare un patternMatcher. Il primo che legge 'sto commento LO DEVE implementare
		 * 
		 * @param c
		 *            char
		 * @return boolean
		 */
		private static boolean isDigit(char c) {
			return (c >= '0') && (c <= '9');
		}

		/**
		 * checkDigit Controlla la correttezza del codice di controllo (check digit) del codice fiscale.
		 * 
		 * @param codfiscale
		 *            String
		 * @return boolean true: se il controllo è andato a buon fine false: se il controllo non è andato a buon fine
		 * 
		 * @author Finessi_M
		 */
		private static boolean checkDigit(String codfiscale) {
			// definizione array CARATTERI PARI
			String[][] tabCarPari = new String[36][2];
			for (int i = 0; i < tabCarPari.length; i += 1) {
				tabCarPari[i] = new String[2];
			}

			// valorizzazione array
			tabCarPari[0][0] = "0";
			tabCarPari[0][1] = "0";
			tabCarPari[1][0] = "1";
			tabCarPari[1][1] = "1";
			tabCarPari[2][0] = "2";
			tabCarPari[2][1] = "2";
			tabCarPari[3][0] = "3";
			tabCarPari[3][1] = "3";
			tabCarPari[4][0] = "4";
			tabCarPari[4][1] = "4";
			tabCarPari[5][0] = "5";
			tabCarPari[5][1] = "5";
			tabCarPari[6][0] = "6";
			tabCarPari[6][1] = "6";
			tabCarPari[7][0] = "7";
			tabCarPari[7][1] = "7";
			tabCarPari[8][0] = "8";
			tabCarPari[8][1] = "8";
			tabCarPari[9][0] = "9";
			tabCarPari[9][1] = "9";
			tabCarPari[10][0] = "A";
			tabCarPari[10][1] = "0";
			tabCarPari[11][0] = "B";
			tabCarPari[11][1] = "1";
			tabCarPari[12][0] = "C";
			tabCarPari[12][1] = "2";
			tabCarPari[13][0] = "D";
			tabCarPari[13][1] = "3";
			tabCarPari[14][0] = "E";
			tabCarPari[14][1] = "4";
			tabCarPari[15][0] = "F";
			tabCarPari[15][1] = "5";
			tabCarPari[16][0] = "G";
			tabCarPari[16][1] = "6";
			tabCarPari[17][0] = "H";
			tabCarPari[17][1] = "7";
			tabCarPari[18][0] = "I";
			tabCarPari[18][1] = "8";
			tabCarPari[19][0] = "J";
			tabCarPari[19][1] = "9";
			tabCarPari[20][0] = "K";
			tabCarPari[20][1] = "10";
			tabCarPari[21][0] = "L";
			tabCarPari[21][1] = "11";
			tabCarPari[22][0] = "M";
			tabCarPari[22][1] = "12";
			tabCarPari[23][0] = "N";
			tabCarPari[23][1] = "13";
			tabCarPari[24][0] = "O";
			tabCarPari[24][1] = "14";
			tabCarPari[25][0] = "P";
			tabCarPari[25][1] = "15";
			tabCarPari[26][0] = "Q";
			tabCarPari[26][1] = "16";
			tabCarPari[27][0] = "R";
			tabCarPari[27][1] = "17";
			tabCarPari[28][0] = "S";
			tabCarPari[28][1] = "18";
			tabCarPari[29][0] = "T";
			tabCarPari[29][1] = "19";
			tabCarPari[30][0] = "U";
			tabCarPari[30][1] = "20";
			tabCarPari[31][0] = "V";
			tabCarPari[31][1] = "21";
			tabCarPari[32][0] = "W";
			tabCarPari[32][1] = "22";
			tabCarPari[33][0] = "X";
			tabCarPari[33][1] = "23";
			tabCarPari[34][0] = "Y";
			tabCarPari[34][1] = "24";
			tabCarPari[35][0] = "Z";
			tabCarPari[35][1] = "25";

			// definizione array CARATTERI DISPARI
			String[][] tabCarDispari = new String[36][2];
			for (int i = 0; i < tabCarDispari.length; i += 1) {
				tabCarDispari[i] = new String[2];
			}

			// valorizzazione array
			tabCarDispari[0][0] = "1";
			tabCarDispari[0][1] = "00";
			tabCarDispari[1][0] = "2";
			tabCarDispari[1][1] = "5";
			tabCarDispari[2][0] = "3";
			tabCarDispari[2][1] = "7";
			tabCarDispari[3][0] = "4";
			tabCarDispari[3][1] = "9";
			tabCarDispari[4][0] = "5";
			tabCarDispari[4][1] = "13";
			tabCarDispari[5][0] = "6";
			tabCarDispari[5][1] = "15";
			tabCarDispari[6][0] = "7";
			tabCarDispari[6][1] = "17";
			tabCarDispari[7][0] = "8";
			tabCarDispari[7][1] = "19";
			tabCarDispari[8][0] = "9";
			tabCarDispari[8][1] = "21";
			tabCarDispari[9][0] = "0";
			tabCarDispari[9][1] = "1";
			tabCarDispari[10][0] = "A";
			tabCarDispari[10][1] = "1";
			tabCarDispari[11][0] = "B";
			tabCarDispari[11][1] = "0";
			tabCarDispari[12][0] = "C";
			tabCarDispari[12][1] = "5";
			tabCarDispari[13][0] = "D";
			tabCarDispari[13][1] = "7";
			tabCarDispari[14][0] = "E";
			tabCarDispari[14][1] = "9";
			tabCarDispari[15][0] = "F";
			tabCarDispari[15][1] = "13";
			tabCarDispari[16][0] = "G";
			tabCarDispari[16][1] = "15";
			tabCarDispari[17][0] = "H";
			tabCarDispari[17][1] = "17";
			tabCarDispari[18][0] = "I";
			tabCarDispari[18][1] = "19";
			tabCarDispari[19][0] = "J";
			tabCarDispari[19][1] = "21";
			tabCarDispari[20][0] = "K";
			tabCarDispari[20][1] = "2";
			tabCarDispari[21][0] = "L";
			tabCarDispari[21][1] = "4";
			tabCarDispari[22][0] = "M";
			tabCarDispari[22][1] = "18";
			tabCarDispari[23][0] = "N";
			tabCarDispari[23][1] = "20";
			tabCarDispari[24][0] = "O";
			tabCarDispari[24][1] = "11";
			tabCarDispari[25][0] = "P";
			tabCarDispari[25][1] = "3";
			tabCarDispari[26][0] = "Q";
			tabCarDispari[26][1] = "6";
			tabCarDispari[27][0] = "R";
			tabCarDispari[27][1] = "8";
			tabCarDispari[28][0] = "S";
			tabCarDispari[28][1] = "12";
			tabCarDispari[29][0] = "T";
			tabCarDispari[29][1] = "14";
			tabCarDispari[30][0] = "U";
			tabCarDispari[30][1] = "16";
			tabCarDispari[31][0] = "V";
			tabCarDispari[31][1] = "10";
			tabCarDispari[32][0] = "W";
			tabCarDispari[32][1] = "22";
			tabCarDispari[33][0] = "X";
			tabCarDispari[33][1] = "25";
			tabCarDispari[34][0] = "Y";
			tabCarDispari[34][1] = "24";
			tabCarDispari[35][0] = "Z";
			tabCarDispari[35][1] = "23";

			// definizione array CARATTERI CHECK-DIGIT
			String[][] tabCarCheck = new String[26][2];
			for (int i = 0; i < tabCarCheck.length; i++) {
				tabCarCheck[i] = new String[2];
			}

			// valorizzazione array
			tabCarCheck[0][0] = "A";
			tabCarCheck[0][1] = "0";
			tabCarCheck[1][0] = "B";
			tabCarCheck[1][1] = "1";
			tabCarCheck[2][0] = "C";
			tabCarCheck[2][1] = "2";
			tabCarCheck[3][0] = "D";
			tabCarCheck[3][1] = "3";
			tabCarCheck[4][0] = "E";
			tabCarCheck[4][1] = "4";
			tabCarCheck[5][0] = "F";
			tabCarCheck[5][1] = "5";
			tabCarCheck[6][0] = "G";
			tabCarCheck[6][1] = "6";
			tabCarCheck[7][0] = "H";
			tabCarCheck[7][1] = "7";
			tabCarCheck[8][0] = "I";
			tabCarCheck[8][1] = "8";
			tabCarCheck[9][0] = "J";
			tabCarCheck[9][1] = "9";
			tabCarCheck[10][0] = "K";
			tabCarCheck[10][1] = "10";
			tabCarCheck[11][0] = "L";
			tabCarCheck[11][1] = "11";
			tabCarCheck[12][0] = "M";
			tabCarCheck[12][1] = "12";
			tabCarCheck[13][0] = "N";
			tabCarCheck[13][1] = "13";
			tabCarCheck[14][0] = "O";
			tabCarCheck[14][1] = "14";
			tabCarCheck[15][0] = "P";
			tabCarCheck[15][1] = "15";
			tabCarCheck[16][0] = "Q";
			tabCarCheck[16][1] = "16";
			tabCarCheck[17][0] = "R";
			tabCarCheck[17][1] = "17";
			tabCarCheck[18][0] = "S";
			tabCarCheck[18][1] = "18";
			tabCarCheck[19][0] = "T";
			tabCarCheck[19][1] = "19";
			tabCarCheck[20][0] = "U";
			tabCarCheck[20][1] = "20";
			tabCarCheck[21][0] = "V";
			tabCarCheck[21][1] = "21";
			tabCarCheck[22][0] = "W";
			tabCarCheck[22][1] = "22";
			tabCarCheck[23][0] = "X";
			tabCarCheck[23][1] = "23";
			tabCarCheck[24][0] = "Y";
			tabCarCheck[24][1] = "24";
			tabCarCheck[25][0] = "Z";
			tabCarCheck[25][1] = "25";

			int numPari = 0; // sommatoria caratteri pari
			int numDispari = 0; // sommatoria caratteri dispari
			for (int i = 0; i < codfiscale.length() - 1; i++) {
				char c = codfiscale.charAt(i);

				if ((i + 1) % 2 != 0) // numeri dispari
				{
					int tmpNum = 0;
					char[] tmpchar;
					for (int j = 0; j < tabCarDispari.length; j++) {
						tmpchar = tabCarDispari[j][0].toCharArray();
						// if (tabCarDispari[j][0] == c)
						if (tmpchar[0] == c) {
							tmpNum = Integer.parseInt(tabCarDispari[j][1], 10);
							break;
						}
					}
					numDispari += tmpNum;
				} else // numeri pari
				{
					int tmpNum = 0;
					char[] tmpchar;
					for (int j = 0; j < tabCarPari.length; j++) {
						tmpchar = tabCarPari[j][0].toCharArray();
						if (tmpchar[0] == c) {
							tmpNum = Integer.parseInt(tabCarPari[j][1], 10);
							break;
						}
					}
					numPari += tmpNum;

				}
			} // for

			int checkDigit = (numPari + numDispari) % 26;

			char tmpCheck = codfiscale.charAt(codfiscale.length() - 1);
			int tmpCheckInt = 0;
			char[] tmpchar;

			for (int j = 0; j < tabCarCheck.length; j++) {
				tmpchar = tabCarCheck[j][0].toCharArray();
				if (tmpchar[0] == tmpCheck) {
					tmpCheckInt = Integer.parseInt(tabCarCheck[j][1], 10);
					break;
				}
			}

			if (checkDigit == tmpCheckInt) {
				return true;
			} else {
				return false;
			}
		}
	}

	public static void main(String[] args) {
		// Scanner in = new Scanner(System.in);

		/*
		 * String chiaveSicurezzaApp = "chiaveAndroid"; SimpleDateFormat dateFormat = new
		 * SimpleDateFormat("dd/MM/yyyy"); String dataFormattata = dateFormat.format(new Date()); String token =
		 * chiaveSicurezzaApp + dataFormattata; String password = token; // String password = "Ricreare2010"; String
		 * encrypted = SHA1.encrypt(password);
		 */

		/*
		 * String crypted = Utils.getTokenUtente("doncotic"); System.out.println(crypted);
		 * 
		 * String encrypted = Utils.getUserFromTokenUtente("a6548ece85e70661"); System.out.println(encrypted);
		 */

		/*
		 * CF Azienda: MLNLBT67S58H199S CF Lavoratore Tirocinio: RSSMRA75H21L219H Qualifica SRQ: 0204 Codice
		 * comunicazione: 0603714200001010 Data riferimento: 30/10/2014 12.34.00 CF Ente Tirocinio:03626690402
		 */

		String aaa = "00922170402|LRNCRD94H23D704E|80152680379|1608|0604014200147844||05/11/2014";
		StringBuilder padded = new StringBuilder(aaa);
		while ((padded.length() % 8) > 0) {
			padded.append(" ");
		}
		OracleObfuscation desObf = new OracleObfuscation(DES_KEY);
		byte[] encrypted = desObf.encrypt((padded.toString()).getBytes());
		// Encrypt
		String userCript = new String(Hex.encodeHex(encrypted));
		System.out.println(userCript);

	}

	/**
	 * Valida un xml con un file xsd
	 * 
	 * @param xsdFile
	 *            il percorso completo del file XSD per la validazione
	 * @throws IOException
	 * @throws SAXException
	 */
	public static void validateXml(String datiRichiestaXml, String xsdFile) throws SAXException, IOException {

		Schema schema = Utils.getXsdSchema(xsdFile);

		Validator validator = schema.newValidator();
		// at last perform validation:
		StringReader datiXmlReader = new StringReader(datiRichiestaXml);
		StreamSource datiXmlStreamSource = new StreamSource(datiXmlReader);
		validator.validate(datiXmlStreamSource);
	}

	public static Schema getXsdSchema(String xsdFile) throws SAXException {
		String schemaLang = "http://www.w3.org/2001/XMLSchema";

		// get validation driver:
		SchemaFactory factory = SchemaFactory.newInstance(schemaLang);

		Class<Utils> classe = Utils.class;
		ClassLoader loader = classe.getClassLoader();
		InputStream is = loader.getResourceAsStream("xsd" + File.separator + xsdFile);
		StreamSource streamSource = new StreamSource(is);
		Schema schema = factory.newSchema(streamSource);
		return schema;
	}

	/**
	 * Converte un Document in stringa
	 * 
	 * @param document
	 * @return
	 * @throws TransformerException
	 */
	public static String domToString(Document document) throws TransformerException {
		String result = null;

		if (document != null) {
			StringWriter strWtr = new StringWriter();
			StreamResult strResult = new StreamResult(strWtr);
			TransformerFactory tfac = TransformerFactory.newInstance();
			Transformer t = tfac.newTransformer();
			t.transform(new DOMSource(document.getDocumentElement()), strResult);
			result = strResult.getWriter().toString();
		}
		return result;
	}

	public static Document stringToDom(String docustring)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource source = new InputSource(new StringReader(docustring));

		return builder.parse(source);
	}

	/**
	 * Genera una stringa casuale
	 * 
	 * @param iLen
	 *            lunghezza della stringa generata
	 * @returnla stringa generata da pegoraro recion
	 */
	public static String randomString(int iLen) {
		String sChrs = "qwertyuioplkjhgfdsazxcvbnm1234567890";
		StringBuilder sRnd = new StringBuilder("");
		for (int i = 0; i < iLen; i++) {
			int randomPoz = (int) (Math.floor(Math.random() * sChrs.length()));
			sRnd.append(sChrs.substring(randomPoz, randomPoz + 1));
		}
		return sRnd.toString();
	}

	/**
	 * Data una stringa che corrisponde al path di un'immagine ne restituisce il contenuto come array di byte
	 * 
	 * @param filename
	 *            il path dell'immagine
	 * @return il contenuto del file come array di byte
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static byte[] fileToByte(String filename) throws FileNotFoundException, IOException {
		File file = new File(filename);
		FileInputStream input = new FileInputStream(file);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] buffer = new byte[ConstantsSingleton.DEFAULT_BUFFER_SIZE];
		int length;
		while ((length = input.read(buffer)) > 0) {
			output.write(buffer, 0, length);
		}
		byte[] img = output.toByteArray();
		output.close();
		input.close();
		file.delete();

		return img;
	}

	public static String stringSOLR(String url) {
		HttpClient httpClient = new HttpClient();
		GetMethod method = new GetMethod(url);
		try {
			// Execute the method.
			int statusCode = httpClient.executeMethod(method);

			if (statusCode == HttpStatus.SC_OK) {
				return method.getResponseBodyAsString();
			}
			return "";
		} catch (HttpException e) {
			throw new EJBException(e);
		} catch (IOException e) {
			throw new EJBException(e);
		} finally {
			// Release the connection.
			method.releaseConnection();
		}
	}

	public static String stringSOLR(String url, HashMap<String, Object> params) {
		HttpClient httpClient = new HttpClient();
		PostMethod postMethod = new PostMethod(url);

		StringBuffer urlSolrLog = new StringBuffer();

		// Gestione parametri
		if (params != null && !params.isEmpty()) {
			for (String key : params.keySet()) {
				Object value = params.get(key);

				if (value.getClass().isArray()) {
					Object[] valueArray = (Object[]) value;
					for (Object obj : valueArray) {
						NameValuePair data = new NameValuePair(key, obj.toString());
						postMethod.addParameter(data);

						urlSolrLog.append("&" + data.getName() + "=" + data.getValue());
					}
				} else {
					NameValuePair data = new NameValuePair(key, params.get(key).toString());
					postMethod.addParameter(data);

					urlSolrLog.append("&" + data.getName() + "=" + data.getValue());
				}
			}
		}

		Scanner scanner = null;
		
		try {
			// Log Url SOLR
			log.debug("SOLR URL: " + postMethod.getURI().toString()
					+ (urlSolrLog.length() > 0 ? "?" + urlSolrLog.substring(1) : ""));

			// Execute the method
			int statusCode = httpClient.executeMethod(postMethod);

			switch (statusCode) {
			case HttpStatus.SC_OK:
				InputStream inputStream = postMethod.getResponseBodyAsStream();
				scanner = new Scanner(inputStream, "UTF-8");
				return scanner.useDelimiter("\\A").next();
			case HttpStatus.SC_BAD_REQUEST:
				throw new EJBException("Errore di sintassi nei parametri di richiesta");
			default:
				throw new EJBException("Errore durante la chiamata Solr - HttpResponse: " + statusCode);
			}

		} catch (HttpException e) {
			throw new EJBException(e);
		} catch (IOException e) {
			throw new EJBException(e);
		} finally {
			if (scanner != null) scanner.close();
			// Release the connection.
			postMethod.releaseConnection();
			
			((SimpleHttpConnectionManager) httpClient.getHttpConnectionManager()).shutdown();
		}
	}

	public static Document documentSOLR(String url) {
		Document document = null;
		HttpClient httpClient = new HttpClient();
		// Create a method instance.
		GetMethod method = new GetMethod(url);
		try {
			// Execute the method.
			int statusCode = httpClient.executeMethod(method);

			if (statusCode == HttpStatus.SC_OK) {

				// InputStream is =
				// IOUtils.toInputStream(IOUtils.toString(method.getResponseBodyAsStream()));
				// Reader reader = new InputStreamReader(is, "UTF-8");

				Reader reader = new InputStreamReader(method.getResponseBodyAsStream(), "UTF-8");

				InputSource inpsource = new InputSource(reader);
				inpsource.setEncoding("UTF-8");

				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				document = builder.parse(inpsource);
			}

		} catch (HttpException e) {
			throw new EJBException(e);
		} catch (IOException e) {
			throw new EJBException(e);
		} catch (ParserConfigurationException e) {
			throw new EJBException(e);
		} catch (SAXException e) {
			throw new EJBException(e);
		} finally {
			// Release the connection.
			method.releaseConnection();
		}
		return document;
	}

	public static String RawSOLR(String url) {
		HttpClient httpClient = new HttpClient();
		GetMethod method = new GetMethod(url);
		String rawString = null;
		try {
			int statusCode;

			statusCode = httpClient.executeMethod(method);
			if (statusCode == HttpStatus.SC_OK) {
				Reader reader = new InputStreamReader(method.getResponseBodyAsStream(), "UTF-8");
				StringWriter writer = new StringWriter();
				IOUtils.copy(reader, writer);
				rawString = writer.toString();

			}
		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			// Release the connection.
			method.releaseConnection();
		}

		return rawString;
	}

	/**
	 * Restiuisce un oggetto JSON di errore contenente due campi: status: "error" e description: message
	 * 
	 * @param message
	 *            messaggio da restituire
	 * @return l'oggetto JSON che rappresenta l'errore.
	 */
	public static JSONObject buildErrorResponse(String message) {
		return buildErrorResponse("error", message);
	}

	/**
	 * Restiuisce un oggetto JSON di errore contenente due campi: status: "errorDIDpresente" e description: message
	 * 
	 * @param message
	 *            messaggio da restituire
	 * @return l'oggetto JSON che rappresenta l'errore in caso di stipula DID.
	 */
	public static JSONObject buildErrorResponseDIDpresente(String message) {
		return buildErrorResponse("errorDIDpresente", message);
	}

	/**
	 * Restiuisce un oggetto JSON di errore contenente due campi: status: "error" e description: message
	 * 
	 * @param message
	 *            messaggio da restituire
	 * @return l'oggetto JSON che rappresenta l'errore.
	 */
	public static JSONObject buildErrorResponse(String status, String message) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("status", status);
			obj.put("description", message);
			return obj;
		} catch (JSONException ex) {
			log.error("Errore durante la costruzione dell'errore. o_O: " + ex.getMessage());
			return null;
		}
	}

	/**
	 * Restiuisce un oggetto JSON di errore contenente due campi: status: "error" e description: message
	 * 
	 * @param message
	 *            messaggio da restituire
	 * @return l'oggetto JSON che rappresenta l'errore.
	 */
	public static JSONObject buildAppuntamentoResponse(Risposta rispAppuntamento, boolean checkVisibilitaPeriodoDate) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("description", getErrorMessageFissaAppuntamento(rispAppuntamento.getEsito().getCodice(),
					rispAppuntamento.getEsito().getDescrizione(), checkVisibilitaPeriodoDate));

			if (rispAppuntamento.getDatiAppuntamento() != null) {
				JSONObject datiAppuntamento = new JSONObject();
				datiAppuntamento.put("dataAppuntamento",
						gregorianDateToString(rispAppuntamento.getDatiAppuntamento().getDataAppuntamento()));
				datiAppuntamento.put("oraAppuntamento", rispAppuntamento.getDatiAppuntamento().getOraAppuntamento());
				datiAppuntamento.put("denominazioneCPI", rispAppuntamento.getDatiAppuntamento().getDenominazioneCPI());
				datiAppuntamento.put("indirizzoCPIstampa",
						rispAppuntamento.getDatiAppuntamento().getIndirizzoCPIstampa());
				datiAppuntamento.put("siglaOperatore", rispAppuntamento.getDatiAppuntamento().getSiglaOperatore());
				if (rispAppuntamento.getDatiAppuntamento().getAmbiente() != null) {
					datiAppuntamento.put("ambiente", rispAppuntamento.getDatiAppuntamento().getAmbiente());
				}
				obj.put("datiAppuntamento", datiAppuntamento);
			}
			return obj;
		} catch (JSONException ex) {
			log.error("Errore durante la costruzione dell'errore. o_O: " + ex.getMessage());
			return null;
		}
	}

	public static String getErrorMessageFissaAppuntamento(String codice, String messaggioSil,
			boolean checkVisibilitaPeriodoDate) {
		String errorMessage;
		Map<String, String> errorMap = ErroriFissaAppuntamento.asMap();
		/*
		 * In caso di restituzione del codice di errore 22 o 98 per il metodo "fissaAppuntamento" occorre visualizzare
		 * integralmente il messaggio di risposta di KO del SIL contenuto nel tag <descrizione>.
		 */
		if ("22".equals(codice) || "98".equals(codice)) {
			errorMessage = messaggioSil;
		} else if ("18".equals(codice)) {
			if (checkVisibilitaPeriodoDate) {
				errorMessage = errorMap.get("18_DATE");
			} else {
				errorMessage = errorMap.get("18_NO_DATE");
			}
		} else {
			errorMessage = errorMap.get(codice);
		}

		return errorMessage;
	}

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

	private static final String CONTENT_DISPOSITION = "Content-Disposition";

	/**
	 * Costruisce la risposta del Web Service
	 * 
	 * @param curriculumId
	 *            Integer l'id del cv del quale vogliamo il pdf
	 * @param bis
	 *            il pdf da inviare in risposta, se presente.
	 * @return
	 */
	public static Response fileResponseBuilder(String filename, InputStream bis) {
		ResponseBuilder response;

		if (bis == null) {
			response = Response.noContent();
		} else {
			// ByteArrayInputStream bis = new ByteArrayInputStream(foto);
			response = Response.ok(bis);
			String fname = String.format("attachment; filename=\"%s\"", filename);
			response.header(CONTENT_DISPOSITION, fname);
			response.header("Expires", "0");
			response.header("Cache-Control", "Private");
			// response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
			// response.header("Pragma", "");
		}

		return response.build();
	}

	/**
	 * Costruisce la risposta del servizio REST per le stampe quando va in errore. La risposta constiste in una serie di
	 * chiamate javascript che mostrano un errore sulla pagina chiamante e chiudono la finestra aperta per la stampa.
	 * 
	 * @param curriculumId
	 *            Integer l'id del cv del quale vogliamo il pdf
	 * @param bis
	 *            il pdf da inviare in risposta, se presente.
	 * @return
	 */
	public static Response fileErrorResponseBuilder(String message) {
		String page = "<html>" + "<head></head>" + "<body>" + "<script type='text/javascript'>"
				+ "parent.jQuery.jnotify('" + message + "', 'error',true);" + "</script>" + "</body>" + "</html>";
		ResponseBuilder responseBuilder = Response.status(Response.Status.OK).entity(page);
		return responseBuilder.build();
	}

	public static boolean firstAccessFromProvider(String username) {
		return firstAccessFromICAR(username) || firstAccessFromFedera(username) || firstAccessFromTwitter(username)
				|| firstAccessFromGoogle(username) || firstAccessFromFacebook(username);
	}

	public static boolean accessFromProvider(String username) {
		return accessFromICAR(username) || accessFromFedera(username) || accessFromTwitter(username)
				|| accessFromGoogle(username) || accessFromFacebook(username);
	}

	/**
	 * Verifica se ci sono i parametri di fedERa nell'Header della sessione e se l'utente non è ancora stato creato.
	 * 
	 * @param username
	 *            username utilizzato
	 * @return true se ha eseguito l'accesso da Federa
	 */
	public static boolean firstAccessFromFedera(String username) {
		Map<String, String> federaHeaders = FederaAppSingleton.getInstance().get(username);
		if (federaHeaders != null && federaHeaders instanceof Map) {
			if (ConstantsSingleton.Federa.LOGGEDSECURITY.equals(federaHeaders.get(ConstantsSingleton.Federa.LOGGED))
					&& ConstantsSingleton.Federa.TYPE.equals(federaHeaders.get(ConstantsSingleton.Auth.TYPE))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Verifica se l'utente ha effettuate l'accesso con federa ma non è la prima volta. L'utente è quindi già presente
	 * su DB.
	 * 
	 * @param username
	 *            username utilizzato
	 * @return true o false
	 */
	public static boolean accessFromFedera(String username) {
		Map<String, String> federaHeaders = FederaAppSingleton.getInstance().get(username);
		if (federaHeaders != null && federaHeaders instanceof Map) {
			if (ConstantsSingleton.Federa.LOGGEDSECURITY.equals(federaHeaders.get(ConstantsSingleton.Federa.LOGGED))
					&& ConstantsSingleton.Federa.MYPORTALTYPE.equals(federaHeaders.get(ConstantsSingleton.Auth.TYPE))) {
				return true;
			}
		}
		return false;
	}

	public static boolean accessFromICAR(String username) {
		Map<String, String> icarHeaders = IcarAppSingleton.getInstance().get(username);
		if (icarHeaders != null && icarHeaders instanceof Map) {
			if (ConstantsSingleton.Icar.MYPORTALTYPE.equals(icarHeaders.get(ConstantsSingleton.Auth.TYPE))) {
				return true;
			}
		}
		return false;
	}

	public static boolean firstAccessFromICAR(String username) {
		Map<String, String> icarHeaders = IcarAppSingleton.getInstance().get(username);
		if (icarHeaders != null && icarHeaders instanceof Map) {
			if (ConstantsSingleton.Icar.TYPE.equals(icarHeaders.get(ConstantsSingleton.Auth.TYPE))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Verifica se ci sono i parametri di Twitter nel singleton e se l'utente non è ancora stato creato.
	 * 
	 * @param username
	 *            username utilizzato
	 * @return true se ha eseguito l'accesso da Twitter
	 */
	public static boolean firstAccessFromTwitter(String username) {
		Map<String, ? extends Object> twitterHeaders = TwitterAppSingleton.getInstance().get(username);
		return (twitterHeaders != null)
				&& (ConstantsSingleton.Twitter.TYPE.equals(twitterHeaders.get(ConstantsSingleton.Auth.TYPE)));
	}

	/**
	 * Verifica se l'utente ha effettuato l'accesso tramite Twitter
	 * 
	 * @param username
	 *            username utilizzato
	 * @return true o false
	 */
	public static boolean accessFromTwitter(String username) {
		Map<String, ? extends Object> twitterHeaders = TwitterAppSingleton.getInstance().get(username);
		return twitterHeaders != null;
	}

	/**
	 * Verifica se ci sono i parametri di Google nel singleton e se l'utente non è ancora stato creato.
	 * 
	 * @param username
	 *            username utilizzato
	 * @return true se ha eseguito l'accesso da Google
	 */
	public static boolean firstAccessFromGoogle(String username) {
		Map<String, ? extends Object> googleHeaders = GoogleAppSingleton.getInstance().get(username);
		return (googleHeaders != null)
				&& (ConstantsSingleton.Google.TYPE.equals(googleHeaders.get(ConstantsSingleton.Auth.TYPE)));
	}

	/**
	 * Verifica se l'utente ha effettuato l'accesso tramite Google
	 * 
	 * @param username
	 *            username utilizzato
	 * @return true o false
	 */
	public static boolean accessFromGoogle(String username) {
		Map<String, ? extends Object> googleHeaders = GoogleAppSingleton.getInstance().get(username);
		return googleHeaders != null;
	}

	/**
	 * Verifica se ci sono i parametri di Facebook nel singleton e se l'utente non è ancora stato creato.
	 * 
	 * @param username
	 *            username utilizzato
	 * @return true se ha eseguito l'accesso da Facebook
	 */
	public static boolean firstAccessFromFacebook(String username) {
		Map<String, ? extends Object> facebookHeaders = FacebookAppSingleton.getInstance().get(username);
		return (facebookHeaders != null)
				&& (ConstantsSingleton.Facebook.TYPE.equals(facebookHeaders.get(ConstantsSingleton.Auth.TYPE)));
	}

	/**
	 * Verifica se l'utente ha effettuato l'accesso tramite Facebook
	 * 
	 * @param username
	 *            username utilizzato
	 * @return true o false
	 */
	public static boolean accessFromFacebook(String username) {
		Map<String, ? extends Object> facebookHeaders = FacebookAppSingleton.getInstance().get(username);
		return facebookHeaders != null;
	}

	@SuppressWarnings("deprecation")
	public static String toXMLString(Element nodo) throws Exception {
		StringWriter stringOut = null;

		OutputFormat format = new OutputFormat(nodo.getOwnerDocument());
		format.setOmitXMLDeclaration(false);
		// format.setEncoding("ISO-8859-1");
		format.setEncoding("UTF-8");
		format.setIndenting(true);
		format.setIndent(2);
		format.setLineWidth(0);
		// format.setLineSeparator("");
		format.setPreserveEmptyAttributes(true);
		format.setPreserveSpace(true);
		stringOut = new StringWriter();
		XMLSerializer serial = new XMLSerializer(stringOut, format);

		serial.serialize(nodo);

		String outEncoded = org.apache.axis.utils.StringUtils.escapeNumericChar(stringOut.toString());
		return outEncoded;
	}

	public static String getNodeValue(Node n) {
		NodeList nl = n.getChildNodes();
		Node node = null;
		String nodeValue = null;
		for (int i = 0; i < nl.getLength(); i++) {
			node = nl.item(i);
			if (node.getNodeType() == Node.TEXT_NODE)
				break;
		}
		if (node != null)
			nodeValue = node.getNodeValue();
		return nodeValue;
	}

	public static XMLGregorianCalendar toXMLGregorianCalendar(Date date) throws DatatypeConfigurationException {
		if (date == null) {
			return null;
		}
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(date);
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
	}

	public static Date fromXMLGregorianCalendar(XMLGregorianCalendar calendar) throws ParseException {
		Date date = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		date = df.parse(calendar.toString());
		return date;
	}

	/**
	 * Taglia una stringa per farla stare dentro alla lunghezza massima. Aggiunge tre puntini alla fine della stringa se
	 * questa viene tagliata
	 * 
	 * @param value
	 *            stringa da tagliare
	 * @param maxLength
	 *            lunghezza massima consentita
	 * @return la stringa eventualmente modificata per rientrare nella lunghezza massima consentita
	 */
	public static String cut(String value, int maxlength) {
		return Utils.cut(value, maxlength, true);
	}

	/**
	 * Taglia una stringa per farla stare dentro alla lunghezza massima. Aggiunge eventualmente tre puntini alla fine
	 * della stringa se questa viene tagliata
	 * 
	 * @param value
	 *            stringa da tagliare
	 * @param maxLength
	 *            lunghezza massima consentita
	 * @param addDots
	 *            deve aggiungere i tre puntini?
	 * @return la stringa modificata per rientrare nella lunghezza massima consentita
	 */
	public static String cut(String value, int maxLength, boolean addDots) {
		if (value == null) {
			return null;
		} else {
			int length = value.length();
			if (length > maxLength) {
				if (addDots) {
					return value.substring(0, maxLength - 3) + "...";
				} else {
					return value.substring(0, maxLength);
				}
			} else {
				return value;
			}
		}
	}

	/**
	 * Aggiunge alla lista il valore di mappatura solo se non è gia contenuto al suo interno
	 * 
	 * @param list
	 * @param mapping
	 * @return
	 */
	public static List<String> addUniqueMapping(List<String> list, String mapping) {
		if (!list.contains(mapping)) {
			list.add(mapping);
		}
		return list;
	}

	public static boolean isTokenSecurityCorrect(String value, String completeUrl) {
		if (value == null || ("").equalsIgnoreCase(value)) {
			return false;
		}

		/////// TOKEN SHA1//////
		String token = composeTokenToEncrypt(completeUrl, ConstantsSingleton.DEFAULT_KEY, false /* useUTC */);
		String encrypted = SHA1.encrypt(token);

		return value.equals(encrypted);
	}

	public static boolean isTokenSecurityCorrectApp(String value, String completeUrl) {
		return isTokenSecurityCorrect(value, completeUrl, ConstantsSingleton.App.KEY, true /* useUTC */);
	}
	
	public static boolean isTokenSecurityCorrect(String value, String completeUrl, String key, boolean useUTC) {
		if (value == null || ("").equalsIgnoreCase(value)) {
			return false;
		}

		/////// TOKEN SHA256//////
		String token = composeTokenToEncrypt(completeUrl, key, useUTC);
		String encrypted = SHA256.encrypt(token);

		return value.equals(encrypted);
	}

	/**
	 * Composizione del token da criptare
	 * 
	 * @param completeUrl
	 * @param key
	 * @param useUTC
	 * @return
	 */
	private static String composeTokenToEncrypt(String completeUrl, String key, boolean useUTC) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		if (useUTC)
			dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		String dataFormattata = dateFormat.format(new Date());

		/////// TOKEN //////
		String token = key + dataFormattata + completeUrl + completeUrl.length();

		return token;
	}

	/**
	 * Ritorna il token utente specifico dell'App cifrato con algoritmo AES. Prima della cifratura al valore passato
	 * viene appeso il timestamp corrente.
	 * 
	 * @param value
	 * @return
	 */
	public static String getTokenUtente(String value) {

		String temporalValue = createTemporalValue(value);

		// Algoritmo AES
		Obfuscation obf = new AesObfuscation(ConstantsSingleton.App.APP_TOKEN_UTENTE_AES_KEY);
		byte[] encrypted = obf.encrypt((temporalValue).getBytes(Charset.forName("UTF-8")));
		String userCript = new String(Hex.encodeHex(encrypted));

		return userCript;
	}

	/**
	 * A partire dal token cifrato con algoritmo AES ritorna il valore decriptato. Da valore decriptato viene ritornato
	 * solo lo username, rimuovendo il timestamp utilizzato per la cifratura.
	 * 
	 * @param encryptTokenUtente
	 * @return
	 * @throws DecoderException
	 */
	public static String getUsernameFromTokenUtente(String encryptTokenUtente) throws DecoderException {
		Obfuscation obf = new AesObfuscation(ConstantsSingleton.App.APP_TOKEN_UTENTE_AES_KEY);
		byte[] decoded = Hex.decodeHex(encryptTokenUtente.toCharArray());
		String temporalValue = new String(obf.decrypt(decoded));
		return extractValueFromTemporal(temporalValue);
	}

	/**
	 * Appende al valore passato il timestamp con eventuale padding a 16 o multiplo (necessario per algoritmo AES)
	 * 
	 * @param value
	 * @return
	 */
	private static String createTemporalValue(String value) {
		// Il valore da criptare è dato dal timestamp corrente + "#" + value (padding 16 o multiplo)
		return paddAt(new Date().getTime() + "#" + value, 16);
	}

	/**
	 * Rimuove il timestamp dal valore decriptato
	 * 
	 * @param temporalValue
	 * @return
	 */
	private static String extractValueFromTemporal(String temporalValue) {
		String ret = null;
		if (temporalValue != null) {
			if (temporalValue.indexOf("#") > -1) {
				ret = temporalValue.substring(temporalValue.indexOf("#") + 1);
			} else {
				ret = temporalValue;
			}
			ret = ret.trim();
		}
		return ret;
	}

	/**
	 * Padding alla lunghezza passata o suo multiplo
	 * 
	 * @param s
	 * @param length
	 * @return
	 */
	private static String paddAt(String s, int length) {
		StringBuilder padded = new StringBuilder(s);
		while ((padded.length() % length) > 0) {
			padded.append(" ");
		}
		return padded.toString();
	}

	public static boolean isMailCorretta(String email) {

		Pattern p = Pattern.compile(
				"([A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+(\\.[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+)*@[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+(\\.[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+)*)?");
		Matcher m = p.matcher(email);
		boolean matchFound = m.matches();
		if (!matchFound) {
			return false;
		}

		return true;
	}

	public static String getStringFromSimpleXmlElement(Object inputElement) {

		if (inputElement == null) {
			return null;
		}

		if (inputElement.getClass().equals(String.class)) {
			return inputElement.toString();
		}

		try {

			ElementNSImpl element = (ElementNSImpl) inputElement;
			return element.getFirstChild().getTextContent();

		} catch (Exception e) {

			return null;

		}

	}

	public static XMLGregorianCalendar dateToGregorianDate(Date date)
			throws DatatypeConfigurationException, ParseException {

		if (date == null) {
			return null;
		}

		DateFormat dfOut = new SimpleDateFormat("yyyy-MM-dd");
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(dfOut.format(date));

	}

	public static Date gregorianDateToDate(XMLGregorianCalendar xmlGregorianCalendar)
			throws DatatypeConfigurationException, ParseException {
		// if (xmlGregorianCalendar == null) {
		// return null;
		// }
		//
		// Date date = null;
		// DateFormat dfIn = new SimpleDateFormat("yyyy-MM-dd");
		// String dateInXmlFormat = xmlGregorianCalendar.toXMLFormat();
		//
		// try {
		// date = dfIn.parse(dateInXmlFormat);
		// } catch (ParseException e) {
		// return null;
		// }
		//
		// return date;

		return xmlGregorianCalendar.toGregorianCalendar().getTime();
	}

	public static XMLGregorianCalendar stringToGregorianDate(String date)
			throws DatatypeConfigurationException, ParseException {

		if (date == null) {
			return null;
		}

		DateFormat dfIn = new SimpleDateFormat("dd/MM/yyyy");
		// DateFormat dfOut = new SimpleDateFormat("yyyy-MM-dd'T'00:00:00");
		DateFormat dfOut = new SimpleDateFormat("yyyy-MM-dd");
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(dfOut.format(dfIn.parse(date)));

	}

	public static String gregorianDateToString(XMLGregorianCalendar gregorianDate) {

		if (gregorianDate == null) {
			return null;
		}

		Date date = null;
		DateFormat dfIn = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dfOut = new SimpleDateFormat("dd/MM/yyyy");
		String dateInXmlFormat = gregorianDate.toXMLFormat();

		try {
			date = dfIn.parse(dateInXmlFormat);
		} catch (ParseException e) {
			return null;
		}

		return dfOut.format(date);

	}

	/**
	 * Controllo solo reg-ex del CF, corrispondente a quello ministeriale, piu` leggero rispetto a controllocompletocodicefiscale
	 */
	public static Boolean controlloRegexMinisterialeCodiceFiscale(String codFis) {
		Boolean result = Boolean.FALSE;
		//pattern by stefy O. 09/2020
		Pattern patternCodFis = Pattern
				.compile("[A-Z]{6}[0-9LMNPQRSTUV]{2}[A-Z][0-9LMNPQRSTUV]{2}[A-Z][0-9LMNPQRSTUV]{3}[A-Z]");
		Matcher matcherCodFis = patternCodFis.matcher(codFis);

		Pattern patternPiva = Pattern.compile("[0-9]{11}");
		Matcher matcherPiva = patternPiva.matcher(codFis);

		if (matcherCodFis.matches() || matcherPiva.matches())
			result = Boolean.TRUE;

		return result;
	}
	
	public static String controllocompletocodicefiscale(String codiceFiscale, String nome, String cognome, String sesso,
			DeComuneDTO comNasc, Date datNasc) {
		try {
			String codfiscaleOrig = StringUtils.defaultString(codiceFiscale).toUpperCase();

			if (CfUtils.isCodiceTemporaneoNumerico(codiceFiscale)) {
				if (!CfUtils.isCodiceTemporaneo(codiceFiscale)) {
					return "Il codice fiscale deve essere lungo 11 caratteri.";
				} else if (!CfUtils.checkNumericoCF(codiceFiscale)) {
					return "Codice fiscale errato: controllo carattere di verifica.";
				}
			} else {
				if (!CfUtils.checkLength(codiceFiscale)) {
					return "Il codice fiscale deve essere lungo 16 caratteri.";
				}

				// controllo codice fiscale temporaneo da 11 cifre
				if (codfiscaleOrig.length() == 11) {
					boolean ok = true;
					for (int k = 0; (k < codiceFiscale.length()) && ok; k += 1) {
						char car = codiceFiscale.charAt(k);
						if (!CfUtils.isDigit(car)) {
							ok = false;
						}
					}
					if (!ok) {
						return "Il codice fiscale è errato.";
					}
				}

				String codfiscaleNoOmocodia = CodiceFiscale.replaceOmocodia(codfiscaleOrig);
				// controllo omocodia
				if (codfiscaleNoOmocodia == null) {
					return "Il codice fiscale è errato.";
				}
				// controllo della correttezza posizionale dei caratteri
				// numerici
				else if (!CodiceFiscale.checkCaratteriNumerici(codfiscaleNoOmocodia)) {
					return "Codice fiscale errato: controllo caratteri numerici fallito.";
				}
				// controllo della correttezza posizionale dei caratteri
				// alfabetici
				else if (!CodiceFiscale.checkCaratteriAlfabetici(codfiscaleNoOmocodia)) {
					return "Codice fiscale errato: controllo caratteri alfabetici fallito.";
				}
				// controllo del carattere del mese
				else if (!CodiceFiscale.checkCarattereMese(codfiscaleNoOmocodia)) {
					return "Codice fiscale errato: errore nel carattere che indica il mese.";
				}

				SimpleDateFormat fd = new SimpleDateFormat("dd/MM/yyyy");
				String dataNascita = fd.format(datNasc);
				if (!CfUtils.checkSesso(codiceFiscale, sesso, dataNascita)) {
					return "Codice fiscale errato: errore nel carattere che indica il sesso.";
				}

				// controllo del check digit
				if (!CfUtils.checkDigit(codiceFiscale)) {
					return "Codice fiscale errato: errore nel carattere che indica il codice di controllo";
				}

				// controllo della congruenza con il cognome
				if (!CfUtils.checkCognome(codiceFiscale, cognome)) {
					return "Codice fiscale errato: errore nei caratteri che indicano il cognome.";
				}

				// controllo della congruenza con il nome
				if (!CfUtils.checkNome(codiceFiscale, nome)) {
					return "Codice fiscale errato: errore nei caratteri che indicano il nome.";
				}

				// controllo della congruenza del mese
				if (!CfUtils.checkMese(codiceFiscale, dataNascita)) {
					return "Codice fiscale errato: errore nei caratteri che indica il mese di nascita.";
				}

				// controllo della congruenza dell'anno di nascita
				if (!CfUtils.checkAnno(codiceFiscale, dataNascita)) {
					return "Codice fiscale errato: errore nei caratteri che indica l'anno di nascita.";
				}

				// controllo della congruenza del comune di nascita
				if (!CfUtils.checkComune(codiceFiscale, comNasc.getId())) {
					return "Codice fiscale errato: errore nei caratteri che il comune di nascita.";
				}
			}
		} catch (NullPointerException e) {
			return "Il codice fiscale deve essere valorizzato.";
		}

		return "";
	}

	public static int getAge(Date dateOfBirth, Date dataDiRiferimento) {
		Calendar dataRiferimento = Calendar.getInstance();
		Calendar birthDate = Calendar.getInstance();
		int age = 0;
		dataRiferimento.setTime(dataDiRiferimento);
		birthDate.setTime(dateOfBirth);
		age = dataRiferimento.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

		// If birth date is greater than todays date (after 2 days adjustment of
		// leap year) then decrement age one year
		if ((birthDate.get(Calendar.DAY_OF_YEAR) - dataRiferimento.get(Calendar.DAY_OF_YEAR) > 3)
				|| (birthDate.get(Calendar.MONTH) > dataRiferimento.get(Calendar.MONTH))) {
			age--;

			// If birth date and todays date are of same month and birth day of
			// month is greater than todays day of month then decrement age
		} else if ((birthDate.get(Calendar.MONTH) == dataRiferimento.get(Calendar.MONTH))
				&& (birthDate.get(Calendar.DAY_OF_MONTH) > dataRiferimento.get(Calendar.DAY_OF_MONTH))) {
			age--;
		}

		return age;
	}

	public static int getAge(Date dateOfBirth) {
		Date today = Calendar.getInstance().getTime();
		return getAge(dateOfBirth, today);
	}

	public static String escapeChrUsername(String input) {
		input = URL.replace(input, "ì", "i");
		input = URL.replace(input, "í", "i");
		input = URL.replace(input, "ò", "o");
		input = URL.replace(input, "ó", "o");
		input = URL.replace(input, "ù", "u");
		input = URL.replace(input, "ú", "u");
		input = URL.replace(input, "à", "a");
		input = URL.replace(input, "á", "a");
		input = URL.replace(input, "è", "e");
		input = URL.replace(input, "é", "e");
		return input;
	}

	public static String normalizeAscii(String input) {

		String output = input;
		output = Normalizer.normalize(output, Normalizer.Form.NFD);
		output = output.replaceAll("[^\\p{ASCII}]", "");
		return output;

	}

	public static JSONObject buildConfermaPeriodicaResponse(String codEsito, String descrizioneEsito,
			Map<String, String> errorMap) {
		JSONObject obj = new JSONObject();
		try {
			JSONArray erroriJa = new JSONArray();
			for (Entry<String, String> entry : errorMap.entrySet()) {
				JSONObject jo = new JSONObject();
				jo.put("codiceErrore", entry.getKey());
				jo.put("descrizioneErrore", entry.getValue());
				erroriJa.put(jo);
			}

			obj.put("codice", codEsito);
			obj.put("descrizione", descrizioneEsito);
			obj.put("Errori", erroriJa);
			return obj;
		} catch (JSONException ex) {
			log.error("Errore durante la costruzione dell'errore. o_O: " + ex.getMessage());
			return null;
		}
	}

	public static InputStream getTemplateEmail(String templateFile) {
		Class<Utils> classe = Utils.class;
		ClassLoader loader = classe.getClassLoader();
		String folderRegione = "";
		if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
			folderRegione = "emiliaromagna";
		} else if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_UMBRIA) {
			folderRegione = "umbria";
		} else if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_TRENTO) {
			folderRegione = "trento";
		} else if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_CALABRIA) {
			folderRegione = "calabria";
		} else if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_VDA) {
			folderRegione = "vda";
		}

		InputStream is = loader.getResourceAsStream("templates" + File.separator + folderRegione + File.separator
				+ "email" + File.separator + templateFile);
		return is;
	}

	public static boolean isStringEmpty(String string) {
		return (string == null) || string.trim().equalsIgnoreCase("");
	}
}
