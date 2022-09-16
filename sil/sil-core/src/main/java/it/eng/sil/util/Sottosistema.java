package it.eng.sil.util;

/*
 * Creato il 25-mag-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */

/**
 * @author vuoto
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public abstract class Sottosistema {

	private static boolean flagCM = true;
	private static boolean flagMO = true;
	private static boolean flagAS = true;

	private static boolean flagDWH = true;

	public abstract static class CM {
		public static boolean isOn() {
			return flagCM;

		}

		public static boolean isOff() {
			return !flagCM;

		}

	}

	public abstract static class MO {
		public static boolean isOn() {
			return flagMO;

		}

		public static boolean isOff() {
			return !flagMO;

		}

	}

	public abstract static class AS {
		public static boolean isOn() {
			return flagAS;

		}

		public static boolean isOff() {
			return !flagAS;

		}

	}

	public abstract static class DWH {
		public static boolean isOn() {
			return flagDWH;

		}

		public static boolean isOff() {
			return !flagDWH;

		}
	}

	/**
	 * @param b
	 */
	public static void setAS(boolean b) {
		flagAS = b;
	}

	/**
	 * @param b
	 */
	public static void setCM(boolean b) {
		flagCM = b;
	}

	/**
	 * @param b
	 */
	public static void setMO(boolean b) {
		flagMO = b;
	}

	public static void setDWH(boolean b) {
		flagDWH = b;
	}

	public static String showInfo() {

		return "AS=" + flagAS + " " + "CM=" + flagCM + " " + "MO=" + flagMO + "DWH=" + flagDWH;

	}

}
