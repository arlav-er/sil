package it.eng.afExt.utils;

/**
 * @author Alessio Rolfini
 * @version 1.0, 11/08/2003
 */

public class PIVA_utils {

	private static String REGEX = "^[0-9]{11}";

	public static int verifyPartitaIva(String piva) throws PivaException {
		// controllo lunghezza
		if (piva == null || piva.length() == 0)
			throw new PivaException(MessageCodes.PartivaIva.NON_VALORIZZATA);
		if (piva == null || piva.length() < 11) {
			throw new PivaException(MessageCodes.PartivaIva.ERR_LUNGHEZZA);
		}
		// controlla se tutti i caratteri sono numeri
		for (int i = 0; i < piva.length(); i++) {
			// controlla se il carattere è un numero
			char c = piva.charAt(i);
			if (!StringUtils.isDigit(c)) {
				throw new PivaException(MessageCodes.PartivaIva.ERR_CARATTERI);
			}
		}
		// controllo piva != '00000000000'
		if (piva == "00000000000")
			throw new PivaException(MessageCodes.PartivaIva.ERR_PIVA_FITTIZIA);

		// controllo check-digit
		int sumDispari = 0;
		int sumPari1 = 0;
		int sumPari2 = 0;

		for (int i = 0; i < piva.length() - 1; i++) {
			int c = piva.charAt(i) - '0';

			if ((i + 1) % 2 != 0) // numeri dispari
			{
				sumDispari += c;
			} else // numeri pari
			{
				if (c >= 0 && c <= 4)
					sumPari1 += c * 2;
				else if (c >= 5 && c <= 9)
					sumPari2 += (c * 2) - 9;
			}
		}

		int total = sumDispari + sumPari1 + sumPari2;

		String Totale = Integer.toString(total);

		char tmpCheck = Totale.charAt(Totale.length() - 1);

		int inttmpCheck = tmpCheck - '0';
		int CheckDigit = 0;

		if (inttmpCheck >= 1 && inttmpCheck <= 9)
			CheckDigit = 10 - inttmpCheck;
		// CheckDigit = 10 - tmpchk[0];
		else
			CheckDigit = 0;

		if (!(CheckDigit == (piva.charAt(piva.length() - 1) - '0'))) {
			throw new PivaException(MessageCodes.PartivaIva.ERR_CHECK_DIGIT);
		}

		return 0; // Se siamo arrivati fin qui è tutto ok!
	}

	public static void verifyPartitaIvaRegEx(String piva) throws PivaException {

		if (piva != null && piva.length() > 0) {
			if ((piva.length() == 11)) {
				if (!piva.matches(REGEX))
					throw new PivaException(MessageCodes.PartivaIva.ERR_CHECK_DIGIT);
			} else {
				throw new PivaException(MessageCodes.PartivaIva.ERR_LUNGHEZZA);
			}
		}
	}
}