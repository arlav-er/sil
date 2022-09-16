package it.eng.myportal.beans.yg;

import java.util.Comparator;

import it.eng.myportal.siler.appuntamento.disponibilitaAppuntamento.output.Risposta;
import it.eng.myportal.siler.appuntamento.disponibilitaAppuntamento.output.Risposta.ElencoDisponibilita.DatiAppuntamento;

/**
 * Classe Comparator per ordinare gli slot secondo l'orario
 * 
 * @author enrico
 *
 */
public class SlotComparator implements Comparator<Risposta.ElencoDisponibilita.DatiAppuntamento> {

	/**
	 * Ordina gli appuntamenti per orario. L'orario si presuppone nel formato HH:MM, se c'e' un qualsiasi errore il
	 * primo appuntamento viene ordinato prima del secondo.
	 */
	@Override
	public int compare(DatiAppuntamento a1, DatiAppuntamento a2) {
		try {
			String a1OreStr, a1MinutiStr, a2OreStr, a2MinutiStr;
			Integer a1Ore, a1Minuti, a2Ore, a2Minuti;
			String[] a1Split = a1.getOraAppuntamento().split(":");
			String[] a2Split = a2.getOraAppuntamento().split(":");
			a1OreStr = a1Split[0];
			a1MinutiStr = a1Split[1];
			a2OreStr = a2Split[0];
			a2MinutiStr = a2Split[1];
			a1Ore = Integer.parseInt(a1OreStr);
			a1Minuti = Integer.parseInt(a1MinutiStr);
			a2Ore = Integer.parseInt(a2OreStr);
			a2Minuti = Integer.parseInt(a2MinutiStr);

			if (a1Ore.compareTo(a2Ore) == 0) {
				return a1Minuti.compareTo(a2Minuti);
			} else {
				return a1Ore.compareTo(a2Ore);
			}
		} catch (Exception e) {
			return -1;
		}
	}
}