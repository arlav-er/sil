/*
 * Creato il 12-giu-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.sms;

/**
 * @author vuoto
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class Sms {

	private String cellNumber = null;
	private String text = null;
	private int maxLength = 160;

	public Sms(String _cellNumber, String _text, int _maxLength) throws SmsFormatException {

		cellNumber = _cellNumber;
		text = _text;
		maxLength = _maxLength;
		if (cellNumber == null) {
			throw new SmsFormatException(SmsFormatException.ERR_N_CELL_NULLO, "Numero di cellulare nullo");
		}

		cellNumber = cleanCellNumber(cellNumber);

		// controlli formali sul numero di telefono
		// il numero devo contenere solo cifre oppure cominciare col +

		if (cellNumber.length() < 3)
			throw new SmsFormatException(SmsFormatException.ERR_N_CELL_NON_VALIDO,
					"Numero di cellulare <" + cellNumber + "> troppo corto");

		// Controlli sul testo

		if (text == null) {
			throw new SmsFormatException(SmsFormatException.ERR_TESTO_NULLO, "Testo nullo");

		}

		text = text.trim();

		if (text.length() > maxLength) {
			throw new SmsFormatException(SmsFormatException.ERR_TESTO_TROPPO_LUNGO,
					"Testo del messaggio superiore a " + maxLength);
		}

	}

	public String toString() {

		String retVal = "";
		if (cellNumber != null)
			retVal += "<" + cellNumber + ">";
		else
			retVal += "< (numero cellulare non impostato)>";

		if (text != null)
			retVal += " \"" + text + " \"";
		else
			retVal += " \"(messaggio non impostato)\"";
		return retVal;
	}

	/**
	 * @return
	 */
	public String getCellNumber() {
		return cellNumber;
	}

	/**
	 * @return
	 */
	public String getText() {
		return text;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int m) {
		maxLength = m;
	}

	public static String cleanCellNumber(String cell) {

		if (cell == null) {
			return "";
		}

		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < cell.length(); i++) {
			char ch = cell.charAt(i);

			if (ch == '+') {
				if (buf.length() == 0)
					buf.append(ch);
			} else {

				if (Character.isDigit(ch)) {
					buf.append(ch);
				}
			}
		}

		return buf.toString();

	}

}
