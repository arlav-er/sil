package it.eng.sil.coop.webservices.clicLavoro.ricercaPersonale.decodificatori;

/**
 * @author rodi
 *
 */
public class AmpiezzaDecoder extends Decoder {

	@Override
	public String decode(String ampiezza) {
		Integer i = Integer.valueOf(ampiezza);
		if (i >= 0 && i <= 15) {
			return "01";
		} else if (i >= 16 && i < 49) {
			return "02";
		} else if (i >= 50 && i < 249) {
			return "03";
		} else
			return "04";
	}
}
