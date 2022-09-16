package it.eng.sil.module.anag.profiloLavoratore.bean;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.TransactionQueryExecutor;

public class DomandaRispostaProfilo {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DomandaRispostaProfilo.class.getName());

	private String codDomandaProf = null;
	private String strDescrizione = null;
	private String codDomandaProfRagg = null;
	private String strNumPosizione = null;
	private String strTipoInput = null;

	private BigDecimal prgLavoratoreProfiloRis = null;
	private String codRispostaProf = null;
	private String strRisposta = null;
	private String strOsservazione = null;
	private BigDecimal cdnUtIns = null;
	private BigDecimal cdnUtMod = null;
	private BigDecimal numKloLavProfRisposte = null;

	public DomandaRispostaProfilo(SourceBean risposta) {
		this.prgLavoratoreProfiloRis = (BigDecimal) risposta.getAttribute("PRGLAVORATOREPROFILORIS");
		this.codDomandaProf = (String) risposta.getAttribute("CODDOMANDAPROF");
		this.codRispostaProf = (String) risposta.getAttribute("CODRISPOSTAPROF");
		this.strRisposta = (String) risposta.getAttribute("STRRISPOSTA");
		this.strOsservazione = (String) risposta.getAttribute("STROSSERVAZIONE");
		this.numKloLavProfRisposte = (BigDecimal) risposta.getAttribute("NUMKLOLAVPROFRISPOSTE");
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

	public BigDecimal getPrgLavoratoreProfiloRis() {
		return prgLavoratoreProfiloRis;
	}

	public String getCodRispostaProf() {
		return codRispostaProf;
	}

	public String getStrRisposta() {
		return strRisposta;
	}

	public String getStrOsservazione() {
		return strOsservazione;
	}

	public BigDecimal getCdnUtIns() {
		return cdnUtIns;
	}

	public BigDecimal getCdnUtMod() {
		return cdnUtMod;
	}

	public BigDecimal getNumKloLavProfRisposte() {
		return numKloLavProfRisposte;
	}

	public void setCodDomandaProf(String val) {
		this.codDomandaProf = val;
	}

	public void getStrDescrizione(String val) {
		this.strDescrizione = val;
	}

	public void getCodDomandaProfRagg(String val) {
		this.codDomandaProfRagg = val;
	}

	public void getStrNumPosizione(String val) {
		this.strNumPosizione = val;
	}

	public void getStrTipoInput(String val) {
		this.strTipoInput = val;
	}

	public void getPrgLavoratoreProfiloRis(BigDecimal val) {
		this.prgLavoratoreProfiloRis = val;
	}

	public void getCodRispostaProf(String val) {
		this.codRispostaProf = val;
	}

	public void getStrRisposta(String val) {
		this.strRisposta = val;
	}

	public void getStrOsservazione(String val) {
		this.strOsservazione = val;
	}

	public void getCdnUtIns(BigDecimal val) {
		this.cdnUtIns = val;
	}

	public void getCdnUtMod(BigDecimal val) {
		this.cdnUtMod = val;
	}

	public void getNumKloLavProfRisposte(BigDecimal val) {
		this.numKloLavProfRisposte = val;
	}

	public boolean cancella(TransactionQueryExecutor txExec) {
		Object params[] = new Object[1];
		params[0] = getPrgLavoratoreProfiloRis();
		Boolean res;
		try {
			res = (Boolean) txExec.executeQuery("DELETE_RISPOSTA_PROFILO_LAVORATORE", params, "DELETE");
			if (!res.booleanValue()) {
				return false;
			}
			return true;
		} catch (EMFInternalError e) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"DomandaRispostaProfilo::cancella(): Impossibile cancellare la risposta!", e);
			return false;
		}
	}

}
