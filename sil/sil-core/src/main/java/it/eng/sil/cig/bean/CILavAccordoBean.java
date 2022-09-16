package it.eng.sil.cig.bean;

import java.math.BigDecimal;
import java.text.ParseException;

import org.apache.log4j.Logger;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.coop.utils.Converter;

public class CILavAccordoBean {
	private String prglavaccordo;
	private String prgaccordo;
	private String flgaccolto;
	private String codmacroqualifica;
	private BigDecimal numoreptsett;
	private BigDecimal numoreftsett;
	private String datfinecigs;
	private String datiniziocigs;
	private BigDecimal numtotggcigs;
	private BigDecimal numtotorecigs;
	private String cdnutins;
	private String numklolavaccordo;
	private String dtmins;
	private String cdnutmod;
	private String dtmmod;
	private String strnota;
	private String codtipocontratto;
	private String prglavoratore;

	private TransactionQueryExecutor tex;

	private final Logger _logger = Logger.getLogger(CILavAccordoBean.class.getName());

	public CILavAccordoBean(String numtotorecigs, String numtotggcigs, String codmacroqualifica, String datfinecigs,
			String datiniziocigs, Object cdnUtIns, Object cdnUtMod, TransactionQueryExecutor tex)
			throws ParseException {
		if (numtotorecigs == null)
			throw new IllegalArgumentException("Il numtotorecigs non puo' essere null.");
		if (numtotggcigs == null)
			throw new IllegalArgumentException("Il numtotggcigs non puo' essere null.");
		if (codmacroqualifica == null)
			throw new IllegalArgumentException("Il codmacroqualifica non puo' essere null.");
		if (datfinecigs == null)
			throw new IllegalArgumentException("Il datfinecigs non puo' essere null.");
		if (datiniziocigs == null)
			throw new IllegalArgumentException("Il datiniziocigs non puo' essere null.");
		if (cdnUtIns == null)
			throw new IllegalArgumentException("Il cdnUtIns non puo' essere null.");
		if (cdnUtMod == null)
			throw new IllegalArgumentException("Il cdnUtMod non puo' essere null.");
		if (tex == null)
			throw new IllegalArgumentException("Il TransactionQueryExecutor non puo' essere null.");

		// sostituisco eventuali virgole con il punto per evitare problemi di conversione
		this.numtotorecigs = new BigDecimal(numtotorecigs.replace(',', '.'));
		this.numtotggcigs = new BigDecimal(numtotggcigs.replace(',', '.'));

		this.codmacroqualifica = codmacroqualifica;
		this.datfinecigs = Converter.dateConverter("yyyy-MM-dd", "dd/MM/yyyy", datfinecigs);
		this.datiniziocigs = Converter.dateConverter("yyyy-MM-dd", "dd/MM/yyyy", datiniziocigs);
		this.cdnutins = cdnUtIns.toString();
		this.cdnutmod = cdnUtMod.toString();
		this.tex = tex;
	}

	public CILavAccordoBean(SourceBean sb, Object cdnUtIns, Object cdnUtMod, TransactionQueryExecutor tex)
			throws ParseException {
		String numtotorecigs = (String) sb.getCharacters("NUMTOTORECIGS");
		String numtotggcigs = (String) sb.getCharacters("NUMTOTGGCIGS");
		String codmacroqualifica = (String) sb.getCharacters("CODMACROQUALIFICA");
		String datfinecigs = (String) sb.getCharacters("DATFINECIGS");
		String datiniziocigs = (String) sb.getCharacters("DATINIZIOCIGS");
		String numoreptsett = (String) sb.getCharacters("NUMOREPTSETT");
		String numoreftsett = (String) sb.getCharacters("NUMOREFTSETT");

		if (numtotorecigs == null)
			throw new IllegalArgumentException("Il numtotorecigs non puo' essere null.");
		if (numtotggcigs == null)
			throw new IllegalArgumentException("Il numtotggcigs non puo' essere null.");
		if (codmacroqualifica == null)
			throw new IllegalArgumentException("Il codmacroqualifica non puo' essere null.");
		if (datfinecigs == null)
			throw new IllegalArgumentException("Il datfinecigs non puo' essere null.");
		if (datiniziocigs == null)
			throw new IllegalArgumentException("Il datiniziocigs non puo' essere null.");
		if (cdnUtIns == null)
			throw new IllegalArgumentException("Il cdnUtIns non puo' essere null.");
		if (cdnUtMod == null)
			throw new IllegalArgumentException("Il cdnUtMod non puo' essere null.");
		if (tex == null)
			throw new IllegalArgumentException("Il TransactionQueryExecutor non puo' essere null.");
		//
		this.numtotorecigs = new BigDecimal(numtotorecigs.replace(',', '.'));
		this.numtotggcigs = new BigDecimal(numtotggcigs.replace(',', '.'));
		this.codmacroqualifica = codmacroqualifica;
		this.datfinecigs = Converter.dateConverter("yyyy-MM-dd", "dd/MM/yyyy", datfinecigs);
		this.datiniziocigs = Converter.dateConverter("yyyy-MM-dd", "dd/MM/yyyy", datiniziocigs);
		this.cdnutins = cdnUtIns.toString();
		this.cdnutmod = cdnUtMod.toString();
		this.tex = tex;

		this.setFlgaccolto(Converter.flagConverter((String) sb.getCharacters("FLGACCOLTO")));

		if (numoreptsett == null) {
			setNumoreptsett(null);
		} else {
			setNumoreptsett(new BigDecimal(numoreptsett.replace(',', '.')));
		}

		if (numoreftsett == null) {
			setNumoreftsett(null);
		} else {
			setNumoreftsett(new BigDecimal(numoreftsett.replace(',', '.')));
		}

		this.setStrnota((String) sb.getCharacters("STRNOTA"));
		this.setCodtipocontratto((String) sb.getCharacters("CODTIPOICONTRATTO"));

	}

	public void insert() throws EMFInternalError {

		_logger.info("Inserisco lav_accordo da " + datiniziocigs + " a " + datfinecigs);
		Object pLavAccordo[] = new Object[16];
		Object nextPrgLavAccordo = getNextPrgLavAccordo();

		pLavAccordo[0] = nextPrgLavAccordo; // not null
		pLavAccordo[1] = prgaccordo; // not null
		pLavAccordo[2] = flgaccolto;
		pLavAccordo[3] = codmacroqualifica; // not null
		pLavAccordo[4] = numoreptsett;
		pLavAccordo[5] = numoreftsett;
		pLavAccordo[6] = datfinecigs; // not null
		pLavAccordo[7] = datiniziocigs; // not null
		pLavAccordo[8] = numtotggcigs; // not null
		pLavAccordo[9] = numtotorecigs; // not null
		pLavAccordo[10] = strnota;
		pLavAccordo[11] = codtipocontratto; // not null
		pLavAccordo[12] = prglavoratore; // not null
		pLavAccordo[13] = numklolavaccordo;
		pLavAccordo[14] = cdnutins;
		pLavAccordo[15] = cdnutmod; // not null

		tex.executeQuery("INSERT_CI_LAV_ACCORDO", pLavAccordo, "INSERT");
		// solo se l'inserimento va a buon fine setto il prglavaccordo con cui e' stato registrato il record
		setPrglavaccordo(nextPrgLavAccordo.toString());
	}

	private BigDecimal getNextPrgLavAccordo() throws EMFInternalError {

		String query = "select s_ci_lav_accordo.nextval from dual";
		SourceBean row = (SourceBean) tex.executeQueryByStringStatement(query, new Object[] {}, "SELECT");
		BigDecimal nextval = (BigDecimal) row.getAttribute("row.nextval");

		if (nextval == null)
			throw new EMFInternalError(EMFErrorSeverity.BLOCKING,
					"Impossibile calcolare la sequence per la tabella ci_lav_accordo");
		return nextval;
	}

	public String getPrglavaccordo() {
		return prglavaccordo;
	}

	public void setPrglavaccordo(String prglavaccordo) {
		this.prglavaccordo = prglavaccordo;
	}

	public String getPrgaccordo() {
		return prgaccordo;
	}

	public void setPrgaccordo(String prgaccordo) {
		this.prgaccordo = prgaccordo;
	}

	public String getFlgaccolto() {
		return flgaccolto;
	}

	public void setFlgaccolto(String flgaccolto) {
		this.flgaccolto = flgaccolto;
	}

	public String getCodmacroqualifica() {
		return codmacroqualifica;
	}

	public void setCodmacroqualifica(String codmacroqualifica) {
		this.codmacroqualifica = codmacroqualifica;
	}

	public BigDecimal getNumoreptsett() {
		return numoreptsett;
	}

	public void setNumoreptsett(BigDecimal numoreptsett) {
		this.numoreptsett = numoreptsett;
	}

	public BigDecimal getNumoreftsett() {
		return numoreftsett;
	}

	public void setNumoreftsett(BigDecimal numoreftsett) {
		this.numoreftsett = numoreftsett;
	}

	public String getDatfinecigs() {
		return datfinecigs;
	}

	public void setDatfinecigs(String datfinecigs) {
		this.datfinecigs = datfinecigs;
	}

	public String getDatiniziocigs() {
		return datiniziocigs;
	}

	public void setDatiniziocigs(String datiniziocigs) {
		this.datiniziocigs = datiniziocigs;
	}

	public BigDecimal getNumtotggcigs() {
		return numtotggcigs;
	}

	public void setNumtotggcigs(BigDecimal numtotggcigs) {
		this.numtotggcigs = numtotggcigs;
	}

	public BigDecimal getNumtotorecigs() {
		return numtotorecigs;
	}

	public void setNumtotorecigs(BigDecimal numtotorecigs) {
		this.numtotorecigs = numtotorecigs;
	}

	public String getCdnutins() {
		return cdnutins;
	}

	public void setCdnutins(String cdnutins) {
		this.cdnutins = cdnutins;
	}

	public String getNumklolavaccordo() {
		return numklolavaccordo;
	}

	public void setNumklolavaccordo(String numklolavaccordo) {
		this.numklolavaccordo = numklolavaccordo;
	}

	public String getDtmins() {
		return dtmins;
	}

	public void setDtmins(String dtmins) {
		this.dtmins = dtmins;
	}

	public String getCdnutmod() {
		return cdnutmod;
	}

	public void setCdnutmod(String cdnutmod) {
		this.cdnutmod = cdnutmod;
	}

	public String getDtmmod() {
		return dtmmod;
	}

	public void setDtmmod(String dtmmod) {
		this.dtmmod = dtmmod;
	}

	public String getStrnota() {
		return strnota;
	}

	public void setStrnota(String strnota) {
		this.strnota = strnota;
	}

	public String getCodtipocontratto() {
		return codtipocontratto;
	}

	public void setCodtipocontratto(String codtipocontratto) {
		this.codtipocontratto = codtipocontratto;
	}

	public String getPrglavoratore() {
		return prglavoratore;
	}

	public void setPrglavoratore(String prglavoratore) {
		this.prglavoratore = prglavoratore;
	}

	public TransactionQueryExecutor getTex() {
		return tex;
	}

	public void setTex(TransactionQueryExecutor tex) {
		this.tex = tex;
	}
}
