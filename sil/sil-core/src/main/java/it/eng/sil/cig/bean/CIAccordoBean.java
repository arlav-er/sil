package it.eng.sil.cig.bean;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.coop.utils.Converter;

/**
 * @author Esposito,Rodi
 */

public class CIAccordoBean {
	private String prgaccordo; // not null
	private String codaccordo;
	private String strprotocollo;
	private String datprotocollo;
	private String codtipoconcessione;
	private String numconcessione;
	private String datconcessione;
	private BigDecimal numtotorecigconces;
	private BigDecimal numtotlavcigconces;
	private String strnota;
	private String datliqinps;
	private BigDecimal numliqinps;
	private String flgcigsanticip;
	private String codmonoprov; // not null
	private String numkloaccordo = "0"; // not null
	private String cdnutins; // not null
	private String cdnutmod; // not null
	private String dataccordo;
	private String codtipoaccordo; // not null
	private String codaccordoorig;
	private String codstatoatto; // not null
	private String strcognomelegale;
	private String strnomelegale;
	private String flgpagdiretto;
	private String datconsultasind;
	private String numlavforzacompl;
	private String strcognomeref;
	private String strnomeref;
	private String strtelref;
	private String strfaxref;
	private String stremailref;
	private String flguanoncoinvolta;
	private String dtminvio;
	private String flgrichpaganticipato;
	private String flgproroga;
	private String coddomandacigprorogata;
	private String flgimprartigiana;
	private String codnatgiuridicafse;
	private String codmacrocategoria;
	private String numversione;

	// valorizzato per le domande CIG di tipo MOBILITA' IN DEROGA
	private String codDomandaCIGSMb;

	private boolean esisteAltroCodAccordo = false;
	private boolean esisteDomandaPrec = false;
	private boolean esisteDomandaSucc = false;

	private TransactionQueryExecutor tex;

	/*
	 * public CIAccordoBean(String codaccordo, String codtipoaccordo, String codstatoatto, Object cdnUtIns, Object
	 * cdnUtMod, TransactionQueryExecutor tex) throws EMFInternalError{ if (codaccordo==null) throw new
	 * IllegalArgumentException("Il codaccordo non puo' essere null."); if (codtipoaccordo==null) throw new
	 * IllegalArgumentException("Il codtipoaccordo non puo' essere null."); if (codstatoatto==null) throw new
	 * IllegalArgumentException("Il codstatoatto non puo' essere null."); if (cdnUtIns==null) throw new
	 * IllegalArgumentException("Il cdnUtIns non puo' essere null."); if (cdnUtMod==null) throw new
	 * IllegalArgumentException("Il cdnUtMod non puo' essere null."); if (tex==null) throw new
	 * IllegalArgumentException("Il TransactionQueryExecutor non puo' essere null.");
	 * 
	 * this.codaccordo = codaccordo; this.codtipoaccordo = codtipoaccordo; this.codstatoatto = codstatoatto;
	 * this.cdnutins = cdnUtIns.toString(); this.cdnutmod = cdnUtMod.toString(); this.tex = tex; this.valorizzaFlags();
	 * }
	 */

	private static final Logger _logger = Logger.getLogger(CIAccordoBean.class.getName());

	public CIAccordoBean(SourceBean sb, Object cdnUtIns, Object cdnUtMod, TransactionQueryExecutor tex)
			throws EMFInternalError, ParseException {
		_logger.info("Inizio la costruzione del bean accordo.");
		String codAccordo = sb.getCharacters("CODACCORDO");
		String codTipoAcc = sb.getCharacters("CODTIPOACCORDO");
		String codStatoAtto = sb.getCharacters("CODSTATOATTO");
		String codMonoProv = sb.getCharacters("CODMONOPROV");

		String codDomandaCIGSMb = sb.getCharacters("codDomandaCIGSMb");

		if (codAccordo == null)
			throw new IllegalArgumentException("Il codaccordo non puo' essere null.");
		if (codTipoAcc == null)
			throw new IllegalArgumentException("Il codtipoaccordo non puo' essere null.");
		if (codStatoAtto == null)
			throw new IllegalArgumentException("Il codstatoatto non puo' essere null.");
		// if (codMonoProv==null) throw new IllegalArgumentException("Il codmonoprov non puo' essere null.");
		if (cdnUtIns == null)
			throw new IllegalArgumentException("Il cdnUtIns non puo' essere null.");
		if (cdnUtMod == null)
			throw new IllegalArgumentException("Il cdnUtMod non puo' essere null.");
		if (tex == null)
			throw new IllegalArgumentException("Il TransactionQueryExecutor non puo' essere null.");

		// if (CigConst.AM_MOBILITA.equals(codTipoAcc) && (codDomandaCIGSMb == null))
		// throw new IllegalArgumentException("Il codDomandaCIGSMb non può essere null in una domanda di tipo MOBILITA
		// IN DEROGA.");
		this.codDomandaCIGSMb = codDomandaCIGSMb;

		this.codaccordo = codAccordo;
		this.codtipoaccordo = codTipoAcc;
		this.codstatoatto = codStatoAtto;
		this.codmonoprov = codMonoProv;
		this.cdnutins = cdnUtIns.toString();
		this.cdnutmod = cdnUtMod.toString();
		this.tex = tex;

		this.setStrprotocollo((String) sb.getCharacters("STRPROTOCOLLO"));
		this.setDatprotocollo(
				Converter.dateConverter("yyyy-MM-dd", "dd/MM/yyyy", (String) sb.getCharacters("DATPROTOCOLLO")));

		if (sb.getCharacters("CODTIPOCONCESSIONE") == null)
			this.setCodtipoconcessione("");
		else
			this.setCodtipoconcessione((String) sb.getCharacters("CODTIPOCONCESSIONE"));

		this.setNumconcessione((String) sb.getCharacters("NUMCONCESSIONE"));
		this.setDatconcessione(
				Converter.dateConverter("yyyy-MM-dd", "dd/MM/yyyy", (String) sb.getCharacters("DATCONCESSIONE")));

		String totconces = (String) sb.getCharacters("NUMTOTORECIGCONCES");
		if (totconces == null) {
			setNumtotorecigconces(null);
		} else {
			setNumtotorecigconces(new BigDecimal(totconces.replace(',', '.')));
		}

		String totlav = (String) sb.getCharacters("NUMTOTLAVCIGCONCES");
		if (totlav == null) {
			setNumtotlavcigconces(null);
		} else {
			setNumtotlavcigconces(new BigDecimal(totlav.replace(',', '.')));
		}

		this.setStrnota((String) sb.getCharacters("STRNOTA"));
		this.setDatliqinps(
				Converter.dateConverter("yyyy-MM-dd", "dd/MM/yyyy", (String) sb.getCharacters("DATLIQINPS")));

		String numliqinps = (String) sb.getCharacters("NUMLIQINPS");
		if (numliqinps == null) {
			setNumliqinps(null);
		} else {
			setNumliqinps(new BigDecimal(numliqinps.replace(',', '.')));
		}

		this.setFlgcigsanticip((String) sb.getCharacters("FLGCIGSANTICIP"));
		this.setDataccordo(
				Converter.dateConverter("yyyy-MM-dd", "dd/MM/yyyy", (String) sb.getCharacters("DATACCORDO")));
		this.setCodaccordoorig((String) sb.getCharacters("CODACCORDOORIG"));
		this.setStrcognomelegale((String) sb.getCharacters("STRCOGNOMELEGALE"));
		this.setStrnomelegale((String) sb.getCharacters("STRNOMELEGALE"));
		this.setFlgpagdiretto((String) sb.getCharacters("FLGPAGDIRETTO"));
		this.setDatconsultasind(
				Converter.dateConverter("yyyy-MM-dd", "dd/MM/yyyy", (String) sb.getCharacters("DATCONSULTASIND")));
		this.setNumlavforzacompl((String) sb.getCharacters("NUMLAVFORZACOMPL"));
		this.setStrcognomeref((String) sb.getCharacters("STRCOGNOMEREF"));
		this.setStrnomeref((String) sb.getCharacters("STRNOMEREF"));
		this.setStrtelref((String) sb.getCharacters("STRTELREF"));
		this.setStrfaxref((String) sb.getCharacters("STRFAXREF"));
		this.setStremailref((String) sb.getCharacters("STREMAILREF"));
		this.setFlguanoncoinvolta(Converter.flagConverter((String) sb.getCharacters("FLGUANONCOINVOLTA")));
		this.setDtminvio(Converter.dateConverter("yyyy-MM-dd", "dd/MM/yyyy", (String) sb.getCharacters("DTMINVIO")));
		this.setFlgrichpaganticipato((String) sb.getCharacters("FLGRICHPAGANTICIPATO"));
		this.setFlgproroga((String) sb.getCharacters("FLGPROROGA"));
		this.setCoddomandacigprorogata((String) sb.getCharacters("CODDOMANDACIGPROROGATA"));
		this.setFlgimprartigiana((String) sb.getCharacters("FLGIMPRARTIGIANA"));
		this.setCodnatgiuridicafse((String) sb.getCharacters("CODNATGIURIDICAFSE"));
		this.setCodmacrocategoria((String) sb.getCharacters("CODMACROCATEGORIA"));
		this.setNumversione((String) sb.getCharacters("NUMVERSIONE"));

		this.valorizzaFlags();
	}

	private void valorizzaFlags() throws EMFInternalError {
		Object pAccordo[] = new Object[3];
		pAccordo[0] = this.codaccordo;
		pAccordo[1] = this.codaccordoorig;
		pAccordo[2] = this.codaccordo;

		SourceBean res = (SourceBean) tex.executeQuery("CHECK_VALORIZZA_FLAGS", pAccordo, "SELECT");

		Vector rows = res.getAttributeAsVector("ROW");

		for (int i = 0; i < rows.size(); i++) {
			SourceBean row = (SourceBean) rows.get(i);

			String nomeflag = (String) row.getAttribute("NOMEFLAG");

			if (nomeflag.equals("ESISTE_ALTRO_ACCORDO")) {
				_logger.info("Esiste un altro accordo con lo stesso codice:" + codaccordo);
				this.esisteAltroCodAccordo = true;
			} else if (nomeflag.equals("ESISTE_DOMANDA_PREC")) {
				_logger.info("Esiste un accordo precedente con codice:" + codaccordoorig);
				this.esisteDomandaPrec = true;
			} else if (nomeflag.equals("ESISTE_DOMANDA_SUCC")) {
				_logger.info("Esiste un accordo successivo che ha come codaccordoorig:" + codaccordo);
				this.esisteDomandaSucc = true;
			}
		}
	}

	/**
	 * Inserisce il presente accordo all'interno della tabella CI_ACCORDO
	 * 
	 * @throws EMFInternalError
	 */
	public void insert() throws EMFInternalError {
		Object pAccordo[] = new Object[41];
		Object nextPrgAccordo = getNextPrgAccordo();
		pAccordo[0] = nextPrgAccordo;
		pAccordo[1] = codaccordo;
		pAccordo[2] = strprotocollo;
		pAccordo[3] = datprotocollo;
		pAccordo[4] = codtipoconcessione;
		pAccordo[5] = numconcessione;
		pAccordo[6] = datconcessione;
		pAccordo[7] = numtotorecigconces;
		pAccordo[8] = numtotlavcigconces;
		pAccordo[9] = strnota;
		pAccordo[10] = datliqinps;
		pAccordo[11] = numliqinps;
		pAccordo[12] = flgcigsanticip;
		pAccordo[13] = codmonoprov;
		pAccordo[14] = numkloaccordo;
		pAccordo[15] = cdnutins;
		pAccordo[16] = cdnutmod;
		pAccordo[17] = dataccordo;
		pAccordo[18] = codtipoaccordo;
		pAccordo[19] = codaccordoorig;
		pAccordo[20] = codstatoatto;
		pAccordo[21] = strcognomelegale;
		pAccordo[22] = strnomelegale;
		pAccordo[23] = flgpagdiretto;
		pAccordo[24] = datconsultasind;
		pAccordo[25] = numlavforzacompl;
		pAccordo[26] = strcognomeref;
		pAccordo[27] = strnomeref;
		pAccordo[28] = strtelref;
		pAccordo[29] = strfaxref;
		pAccordo[30] = stremailref;
		pAccordo[31] = flguanoncoinvolta;
		pAccordo[32] = dtminvio;
		pAccordo[33] = flgrichpaganticipato;
		pAccordo[34] = flgproroga;
		pAccordo[35] = coddomandacigprorogata;
		pAccordo[36] = flgimprartigiana;
		pAccordo[37] = codnatgiuridicafse;
		pAccordo[38] = codmacrocategoria;
		pAccordo[39] = numversione;
		pAccordo[40] = codDomandaCIGSMb;

		tex.executeQuery("INSERT_CI_ACCORDO", pAccordo, "INSERT");
		// solo se l'inserimento va a buon fine setto il prgAccordo con cui e' stato registrato il record
		setPrgaccordo(nextPrgAccordo.toString());

		_logger.info("inserito l'accordo " + codaccordo + " di tipo " + codtipoaccordo + " su DB. " + "codstatoatto = "
				+ codstatoatto + "; codtipoconcessione = " + codtipoconcessione);
	}

	private BigDecimal getNextPrgAccordo() throws EMFInternalError {

		String query = "select s_ci_accordo.nextval from dual";
		SourceBean row = (SourceBean) tex.executeQueryByStringStatement(query, new Object[] {}, "SELECT");
		BigDecimal nextval = (BigDecimal) row.getAttribute("row.nextval");

		if (nextval == null)
			throw new EMFInternalError(EMFErrorSeverity.BLOCKING,
					"Impossibile calcolare la sequence per la tabella CI_ACCORDO");
		return nextval;

	}

	public String getPrgaccordo() {
		return prgaccordo;
	}

	public void setPrgaccordo(String prgaccordo) {
		this.prgaccordo = prgaccordo;
	}

	public String getCodaccordo() {
		return codaccordo;
	}

	public void setCodaccordo(String codaccordo) {
		this.codaccordo = codaccordo;
	}

	public String getStrprotocollo() {
		return strprotocollo;
	}

	public void setStrprotocollo(String strprotocollo) {
		this.strprotocollo = strprotocollo;
	}

	public String getDatprotocollo() {
		return datprotocollo;
	}

	public void setDatprotocollo(String datprotocollo) {
		this.datprotocollo = datprotocollo;
	}

	public String getCodtipoconcessione() {
		return codtipoconcessione;
	}

	public void setCodtipoconcessione(String codtipoconcessione) {
		this.codtipoconcessione = codtipoconcessione;
	}

	public String getNumconcessione() {
		return numconcessione;
	}

	public void setNumconcessione(String numconcessione) {
		this.numconcessione = numconcessione;
	}

	public String getDatconcessione() {
		return datconcessione;
	}

	public void setDatconcessione(String datconcessione) {
		this.datconcessione = datconcessione;
	}

	public BigDecimal getNumtotorecigconces() {
		return numtotorecigconces;
	}

	public void setNumtotorecigconces(BigDecimal numtotorecigconces) {
		this.numtotorecigconces = numtotorecigconces;
	}

	public BigDecimal getNumtotlavcigconces() {
		return numtotlavcigconces;
	}

	public void setNumtotlavcigconces(BigDecimal numtotlavcigconces) {
		this.numtotlavcigconces = numtotlavcigconces;
	}

	public String getStrnota() {
		return strnota;
	}

	public void setStrnota(String strnota) {
		this.strnota = strnota;
	}

	public String getDatliqinps() {
		return datliqinps;
	}

	public void setDatliqinps(String datliqinps) {
		this.datliqinps = datliqinps;
	}

	public BigDecimal getNumliqinps() {
		return numliqinps;
	}

	public void setNumliqinps(BigDecimal numliqinps) {
		this.numliqinps = numliqinps;
	}

	public String getFlgcigsanticip() {
		return flgcigsanticip;
	}

	public void setFlgcigsanticip(String flgcigsanticip) {
		this.flgcigsanticip = flgcigsanticip;
	}

	public String getCodmonoprov() {
		return codmonoprov;
	}

	public void setCodmonoprov(String codmonoprov) {
		this.codmonoprov = codmonoprov;
	}

	public String getNumkloaccordo() {
		return numkloaccordo;
	}

	public void setNumkloaccordo(String numkloaccordo) {
		this.numkloaccordo = numkloaccordo;
	}

	public String getCdnutins() {
		return cdnutins;
	}

	public void setCdnutins(String cdnutins) {
		this.cdnutins = cdnutins;
	}

	public String getCdnutmod() {
		return cdnutmod;
	}

	public void setCdnutmod(String cdnutmod) {
		this.cdnutmod = cdnutmod;
	}

	public String getDataccordo() {
		return dataccordo;
	}

	public void setDataccordo(String dataccordo) {
		this.dataccordo = dataccordo;
	}

	public String getCodtipoaccordo() {
		return codtipoaccordo;
	}

	public void setCodtipoaccordo(String codtipoaccordo) {
		this.codtipoaccordo = codtipoaccordo;
	}

	public String getCodaccordoorig() {
		return codaccordoorig;
	}

	public void setCodaccordoorig(String codaccordoorig) {
		this.codaccordoorig = codaccordoorig;
	}

	public String getCodstatoatto() {
		return codstatoatto;
	}

	public void setCodstatoatto(String codstatoatto) {
		this.codstatoatto = codstatoatto;
	}

	public String getStrcognomelegale() {
		return strcognomelegale;
	}

	public void setStrcognomelegale(String strcognomelegale) {
		this.strcognomelegale = strcognomelegale;
	}

	public String getStrnomelegale() {
		return strnomelegale;
	}

	public void setStrnomelegale(String strnomelegale) {
		this.strnomelegale = strnomelegale;
	}

	public String getFlgpagdiretto() {
		return flgpagdiretto;
	}

	public void setFlgpagdiretto(String flgpagdiretto) {
		this.flgpagdiretto = flgpagdiretto;
	}

	public String getDatconsultasind() {
		return datconsultasind;
	}

	public void setDatconsultasind(String datconsultasind) {
		this.datconsultasind = datconsultasind;
	}

	public String getNumlavforzacompl() {
		return numlavforzacompl;
	}

	public void setNumlavforzacompl(String numlavforzacompl) {
		this.numlavforzacompl = numlavforzacompl;
	}

	public String getStrcognomeref() {
		return strcognomeref;
	}

	public void setStrcognomeref(String strcognomeref) {
		this.strcognomeref = strcognomeref;
	}

	public String getStrnomeref() {
		return strnomeref;
	}

	public void setStrnomeref(String strnomeref) {
		this.strnomeref = strnomeref;
	}

	public String getStrtelref() {
		return strtelref;
	}

	public void setStrtelref(String strtelref) {
		this.strtelref = strtelref;
	}

	public String getStrfaxref() {
		return strfaxref;
	}

	public void setStrfaxref(String strfaxref) {
		this.strfaxref = strfaxref;
	}

	public String getStremailref() {
		return stremailref;
	}

	public void setStremailref(String stremailref) {
		this.stremailref = stremailref;
	}

	public String getFlguanoncoinvolta() {
		return flguanoncoinvolta;
	}

	public void setFlguanoncoinvolta(String flguanoncoinvolta) {
		this.flguanoncoinvolta = flguanoncoinvolta;
	}

	public String getDtminvio() {
		return dtminvio;
	}

	public void setDtminvio(String dtminvio) {
		this.dtminvio = dtminvio;
	}

	public String getFlgrichpaganticipato() {
		return flgrichpaganticipato;
	}

	public void setFlgrichpaganticipato(String flgrichpaganticipato) {
		this.flgrichpaganticipato = flgrichpaganticipato;
	}

	public String getFlgproroga() {
		return flgproroga;
	}

	public void setFlgproroga(String flgproroga) {
		this.flgproroga = flgproroga;
	}

	public String getCoddomandacigprorogata() {
		return coddomandacigprorogata;
	}

	public void setCoddomandacigprorogata(String coddomandacigprorogata) {
		this.coddomandacigprorogata = coddomandacigprorogata;
	}

	public String getFlgimprartigiana() {
		return flgimprartigiana;
	}

	public void setFlgimprartigiana(String flgimprartigiana) {
		this.flgimprartigiana = flgimprartigiana;
	}

	public String getCodnatgiuridicafse() {
		return codnatgiuridicafse;
	}

	public void setCodnatgiuridicafse(String codnatgiuridicafse) {
		this.codnatgiuridicafse = codnatgiuridicafse;
	}

	public String getCodmacrocategoria() {
		return codmacrocategoria;
	}

	public void setCodmacrocategoria(String codmacrocategoria) {
		this.codmacrocategoria = codmacrocategoria;
	}

	public String getNumversione() {
		return numversione;
	}

	public void setNumversione(String numversione) {
		this.numversione = numversione;
	}

	/**
	 * Restituisce <code>true</code> se esiste già, all'interno della tabella CI_ACCORDO un accordo con codice uguale a
	 * questo.
	 * 
	 * @return
	 */
	public boolean isEsisteAltroCodAccordo() {
		return esisteAltroCodAccordo;
	}

	public boolean isEsisteDomandaPrec() {
		return esisteDomandaPrec;
	}

	public boolean isEsisteDomandaSucc() {
		return esisteDomandaSucc;
	}

}
