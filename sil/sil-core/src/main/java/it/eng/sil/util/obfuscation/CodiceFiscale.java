/*
 * Created on 26-lug-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package it.eng.sil.util.obfuscation;

/**
 * @author vuoto
 * 
 *         TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style -
 *         Code Templates
 */
public class CodiceFiscale {

	public static void main(String[] args) {
		String cf = CalcolaCodiceFiscale("vuoto", "franco luigi", "M", "B903", "21", "06", "69");
		System.out.println(cf);

	}

	public static String CalcolaCodiceFiscale(String cogn, String nome, String sex, String comCF, String gg, String mm,
			String aa) {
		int gs = 0;
		int i = 0;
		int somma = 0;
		int sesso = 0;
		String strCodFis = "";
		String strcognome = "";
		String strnome = "";
		String strgiornosex = "";
		char chrcontrollo = ' ';

		if (aa == "")
			aa = "--";
		if (mm == "")
			mm = "--";
		if (gg == "")
			strgiornosex = "--";
		if (comCF == "")
			comCF = "----";

		if (sex.equalsIgnoreCase("F")) {
			sesso = 1;
		}

		// Processa il cognome
		// ----------------------------------------------------------------
		for (i = 0; i < cogn.length(); i++) {
			switch (cogn.toUpperCase().charAt(i)) {
			case 'A':
			case 'E':
			case 'I':
			case 'O':
			case 'U':
				break;
			default:
				if ((cogn.toUpperCase().charAt(i) <= 'Z') && (cogn.toUpperCase().charAt(i) > 'A'))
					strcognome = strcognome + cogn.toUpperCase().charAt(i);
			}
		}
		if (strcognome.length() < 3) {
			for (i = 0; i < cogn.length(); i++) {
				switch (cogn.toUpperCase().charAt(i)) {
				case 'A':
				case 'E':
				case 'I':
				case 'O':
				case 'U':
					strcognome = strcognome + cogn.toUpperCase().charAt(i);
				}
			}
			if (strcognome.length() < 3) {
				for (i = strcognome.length(); i <= 3; i++) {
					strcognome = strcognome + 'X';
				}
			}
		}
		strcognome = strcognome.substring(0, 3);

		// ------------------------------------------------------------
		// processa il nome
		// ----------------------------------------------------------------
		for (i = 0; i < nome.length(); i++) {
			switch (nome.toUpperCase().charAt(i)) {
			case 'A':
			case 'E':
			case 'I':
			case 'O':
			case 'U':
				break;
			default:
				if ((nome.toUpperCase().charAt(i) <= 'Z') && (nome.toUpperCase().charAt(i) > 'A'))
					strnome = strnome + nome.toUpperCase().charAt(i);
			}
		}
		if (strnome.length() > 3) {
			strnome = strnome.substring(0, 1) + strnome.substring(2, 4);
		} else {
			if (strnome.length() < 3) {
				for (i = 0; i < nome.length(); i++) {
					switch (nome.toUpperCase().charAt(i)) {
					case 'A':
					case 'E':
					case 'I':
					case 'O':
					case 'U':
						strnome = strnome + nome.toUpperCase().charAt(i);
					}
				}
				if (strnome.length() < 3) {
					for (i = strnome.length(); i <= 3; i++) {
						strnome = strnome + 'X';
					}
				}
			}
			strnome = strnome.substring(0, 3);
		}

		// --------------------------------------- Fine processa nome

		// processa giorno e sesso
		// --------------------------------------------
		if (gg != "") {
			gs = Integer.valueOf(gg).intValue() + (40 * sesso);
			if (gs < 10)
				strgiornosex = "0" + gs;
			else
				strgiornosex = new Integer(gs).toString();
		}
		// --------------------------------------------

		strCodFis = strcognome + strnome + aa + codificaMese(mm) + strgiornosex + comCF.toUpperCase();

		// calcola la cifra di controllo
		// --------------------------------------------
		for (i = 0; i < 15; i++) {
			if (((i + 1) % 2) != 0) {
				// caratteri dispari
				switch (strCodFis.charAt(i)) {
				case '0':
				case 'A': {
					somma += 1;
					break;
				}
				case '1':
				case 'B': {
					somma += 0;
					break;
				}
				case '2':
				case 'C': {
					somma += 5;
					break;
				}
				case '3':
				case 'D': {
					somma += 7;
					break;
				}
				case '4':
				case 'E': {
					somma += 9;
					break;
				}
				case '5':
				case 'F': {
					somma += 13;
					break;
				}
				case '6':
				case 'G': {
					somma += 15;
					break;
				}
				case '7':
				case 'H': {
					somma += 17;
					break;
				}
				case '8':
				case 'I': {
					somma += 19;
					break;
				}
				case '9':
				case 'J': {
					somma += 21;
					break;
				}
				case 'K': {
					somma += 2;
					break;
				}
				case 'L': {
					somma += 4;
					break;
				}
				case 'M': {
					somma += 18;
					break;
				}
				case 'N': {
					somma += 20;
					break;
				}
				case 'O': {
					somma += 11;
					break;
				}
				case 'P': {
					somma += 3;
					break;
				}
				case 'Q': {
					somma += 6;
					break;
				}
				case 'R': {
					somma += 8;
					break;
				}
				case 'S': {
					somma += 12;
					break;
				}
				case 'T': {
					somma += 14;
					break;
				}
				case 'U': {
					somma += 16;
					break;
				}
				case 'V': {
					somma += 10;
					break;
				}
				case 'W': {
					somma += 22;
					break;
				}
				case 'X': {
					somma += 25;
					break;
				}
				case 'Y': {
					somma += 24;
					break;
				}
				case 'Z': {
					somma += 23;
					break;
				}
				}
			} else {
				// caratteri pari
				switch (strCodFis.charAt(i)) {
				case '0':
				case 'A': {
					somma += 0;
					break;
				}
				case '1':
				case 'B': {
					somma += 1;
					break;
				}
				case '2':
				case 'C': {
					somma += 2;
					break;
				}
				case '3':
				case 'D': {
					somma += 3;
					break;
				}
				case '4':
				case 'E': {
					somma += 4;
					break;
				}
				case '5':
				case 'F': {
					somma += 5;
					break;
				}
				case '6':
				case 'G': {
					somma += 6;
					break;
				}
				case '7':
				case 'H': {
					somma += 7;
					break;
				}
				case '8':
				case 'I': {
					somma += 8;
					break;
				}
				case '9':
				case 'J': {
					somma += 9;
					break;
				}
				case 'K': {
					somma += 10;
					break;
				}
				case 'L': {
					somma += 11;
					break;
				}
				case 'M': {
					somma += 12;
					break;
				}
				case 'N': {
					somma += 13;
					break;
				}
				case 'O': {
					somma += 14;
					break;
				}
				case 'P': {
					somma += 15;
					break;
				}
				case 'Q': {
					somma += 16;
					break;
				}
				case 'R': {
					somma += 17;
					break;
				}
				case 'S': {
					somma += 18;
					break;
				}
				case 'T': {
					somma += 19;
					break;
				}
				case 'U': {
					somma += 20;
					break;
				}
				case 'V': {
					somma += 21;
					break;
				}
				case 'W': {
					somma += 22;
					break;
				}
				case 'X': {
					somma += 23;
					break;
				}
				case 'Y': {
					somma += 24;
					break;
				}
				case 'Z': {
					somma += 25;
					break;
				}
				}
			}
		}
		somma %= 26;
		switch (somma) {
		case 0: {
			chrcontrollo = 'A';
			break;
		}
		case 1: {
			chrcontrollo = 'B';
			break;
		}
		case 2: {
			chrcontrollo = 'C';
			break;
		}
		case 3: {
			chrcontrollo = 'D';
			break;
		}
		case 4: {
			chrcontrollo = 'E';
			break;
		}
		case 5: {
			chrcontrollo = 'F';
			break;
		}
		case 6: {
			chrcontrollo = 'G';
			break;
		}
		case 7: {
			chrcontrollo = 'H';
			break;
		}
		case 8: {
			chrcontrollo = 'I';
			break;
		}
		case 9: {
			chrcontrollo = 'J';
			break;
		}
		case 10: {
			chrcontrollo = 'K';
			break;
		}
		case 11: {
			chrcontrollo = 'L';
			break;
		}
		case 12: {
			chrcontrollo = 'M';
			break;
		}
		case 13: {
			chrcontrollo = 'N';
			break;
		}
		case 14: {
			chrcontrollo = 'O';
			break;
		}
		case 15: {
			chrcontrollo = 'P';
			break;
		}
		case 16: {
			chrcontrollo = 'Q';
			break;
		}
		case 17: {
			chrcontrollo = 'R';
			break;
		}
		case 18: {
			chrcontrollo = 'S';
			break;
		}
		case 19: {
			chrcontrollo = 'T';
			break;
		}
		case 20: {
			chrcontrollo = 'U';
			break;
		}
		case 21: {
			chrcontrollo = 'V';
			break;
		}
		case 22: {
			chrcontrollo = 'W';
			break;
		}
		case 23: {
			chrcontrollo = 'X';
			break;
		}
		case 24: {
			chrcontrollo = 'Y';
			break;
		}
		case 25: {
			chrcontrollo = 'Z';
			break;
		}
		}
		// --------------------------------------------

		return strCodFis + chrcontrollo;
	}

	public static String codificaMese(String mese) {
		String codM = "-";
		if (mese.equals("01")) {
			codM = "A";
		}
		if (mese.equals("02")) {
			codM = "B";
		}
		if (mese.equals("03")) {
			codM = "C";
		}
		if (mese.equals("04")) {
			codM = "D";
		}
		if (mese.equals("05")) {
			codM = "E";
		}
		if (mese.equals("06")) {
			codM = "H";
		}
		if (mese.equals("07")) {
			codM = "L";
		}
		if (mese.equals("08")) {
			codM = "M";
		}
		if (mese.equals("09")) {
			codM = "P";
		}
		if (mese.equals("10")) {
			codM = "R";
		}
		if (mese.equals("11")) {
			codM = "S";
		}
		if (mese.equals("12")) {
			codM = "T";
		}
		return codM;
	}

}
