/*
 * Created on Oct 14, 2005
 * 
 */
package it.eng.sil.module.patto.bean;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.util.Utils;

/**
 * Info correnti utilizzate nella pagina del patto. Servono quando il patto non e' ancora stato inserito e/o quando e'
 * in prepatto (da verificare). Utilizzato sia nel 150 che nell'accordo generico.
 * 
 * @author savino
 */
public class InfoCorrenti {

	private SourceBean source;

	public InfoCorrenti() throws Exception {
		this(null);
	}

	public InfoCorrenti(SourceBean s) throws Exception {
		this.source = (s == null) ? new SourceBean("EMPTY") : s;
	}

	public String getPrgStatoOccupaz() {
		return Utils.notNull(source.getAttribute("PRGSTATOOCCUPAZ"));
	}

	/** de_stato_occupaz.strDescrizione */
	public String getDescrizioneStato() {
		return Utils.notNull(source.getAttribute("DESCRIZIONESTATO"));
	}

	/** elenco anagrafico */
	public String getDatInizio() {
		return Utils.notNull(source.getAttribute("DATINIZIO"));
	}

	/** descrizione */
	public String getCpiTitolare() {
		return Utils.notNull(source.getAttribute("CPITITOLARE"));
	}

	public String getDescCpi() {
		return getCpiTitolare();
	}

	/** did */
	public String getDatDichiarazione() {
		return Utils.notNull(source.getAttribute("DATDICHIARAZIONE"));
	}

	public String getCodCpiTit() {
		return Utils.notNull(source.getAttribute("CODCPITIT"));
	}

	public String getCodStatoAttoDid() {
		return Utils.notNull(source.getAttribute("codStatoAttoDid"));
	}

	public String getPrgDichDisponibilita() {
		return Utils.notNull(source.getAttribute("PRGDICHDISPONIBILITA"));
	}
}