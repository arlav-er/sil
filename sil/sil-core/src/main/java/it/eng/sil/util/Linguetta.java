package it.eng.sil.util;

import java.math.BigDecimal;

final public class Linguetta {
	// BigDecimal numordine;
	// private BigDecimal cdncomponente;
	// private BigDecimal cdnfunzione;
	// private BigDecimal cdnvocefunzione;
	private BigDecimal livello;
	private String path;
	// private String flgvisibileComp;
	// private String strcodliv;
	// private BigDecimal cdnVoceFunzionePadre;
	// private String strdenominazioneComp;
	private String strdescliv;
	// private String strdescrizioneComp;
	// private String strnumpar;
	private String strpage;

	public Linguetta(
			// BigDecimal _cdnvocefunzione,
			// String _strcodliv,
			String _strdescliv,
			// BigDecimal _cdnVoceFunzionePadre,
			// String _strnumpar,
			// BigDecimal _cdnfunzione,
			// BigDecimal _cdncomponente,
			// String _strdenominazioneComp,
			// String _strdescrizioneComp,
			// String _flgvisibileComp,
			String _strpage,
			// BigDecimal _numordine,
			String _path, BigDecimal _livello) {

		// init

		// numordine = _numordine;
		// cdncomponente = _cdncomponente;
		// cdnfunzione = _cdnfunzione;
		// cdnvocefunzione = _cdnvocefunzione;
		livello = _livello;
		path = _path;
		// flgvisibileComp = _flgvisibileComp;
		// strcodliv = _strcodliv;
		// cdnVoceFunzionePadre = _cdnVoceFunzionePadre;
		// strdenominazioneComp = _strdenominazioneComp;
		strdescliv = _strdescliv;
		// strdescrizioneComp = _strdescrizioneComp;
		// strnumpar = _strnumpar;
		strpage = _strpage;
	}

	private Linguetta() {
	}

	public BigDecimal getLivello() {
		return livello;
	}

	public String getStrdescliv() {
		return strdescliv;
	}

	public String getPath() {
		return path;
	}

	public String getStrpage() {
		return strpage;
	}

	/*
	 * public String getStrnumpar() { return strnumpar; }
	 */
}
