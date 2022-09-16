package it.eng.sil.module.anag.profiloLavoratore.bean;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.sil.Values;

@SuppressWarnings("unchecked")
public class DomandeProfilo {
	private String codDomandaProf = null;
	private String strDescrizione = null;
	private String codDomandaProfRagg = null;
	private String strNumPosizione = null;
	private String strTipoInput = null;
	private int linguetta;

	public DomandeProfilo(int domandeLinguetta) {
		this.linguetta = domandeLinguetta;
	}

	public String getCodDomandaProf() {
		return codDomandaProf;
	}

	public String getStrDescrizione() {
		return strDescrizione;
	}

	public String getCodDomandaProfRagg() {
		return codDomandaProfRagg;
	}

	public String getStrNumPosizione() {
		return strNumPosizione;
	}

	public String getStrTipoInput() {
		return strTipoInput;
	}

	public int getLinguetta() {
		return linguetta;
	}

	public void setCodDomandaProf(String val) {
		this.codDomandaProf = val;
	}

	public void setStrDescrizione(String val) {
		this.strDescrizione = val;
	}

	public void setCodDomandaProfRagg(String val) {
		this.codDomandaProfRagg = val;
	}

	public void setStrNumPosizione(String val) {
		this.strNumPosizione = val;
	}

	public void setStrTipoInput(String val) {
		this.strTipoInput = val;
	}

	public void setLinguetta(int val) {
		this.linguetta = val;
	}

	public Vector<SourceBean> caricaDomande() {
		Vector<SourceBean> domande = null;
		Object params[] = new Object[1];
		params[0] = getLinguetta();
		SourceBean res = (SourceBean) QueryExecutor.executeQuery("LOAD_DOMANDE_LINGUETTA", params, "SELECT",
				Values.DB_SIL_DATI);
		if (res != null) {
			domande = res.getAttributeAsVector("ROW");
			return domande;
		} else {
			return null;
		}
	}

}
