/*
 * Creato il 10-mar-04
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.security;

import java.math.BigDecimal;

/**
 * @author vuoto
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class GoTo {

	private String targetPage = "";
	private BigDecimal targetFunz = null;

	public GoTo(String _targetPage, BigDecimal _targetFunz) {
		targetPage = _targetPage;
		targetFunz = _targetFunz;

	}

	public String getTargetPage() {
		return targetPage;
	}

	public void setTargetFunz(BigDecimal decimal) {
		targetFunz = decimal;
	}

	public void setTargetPage(String string) {
		targetPage = string;
	}

	public BigDecimal getTargetFunz() {
		return targetFunz;
	}

}
