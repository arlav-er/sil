package it.eng.sil.cig.bean;

import java.math.BigDecimal;
import java.text.ParseException;

import org.apache.log4j.Logger;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.coop.utils.Converter;

public class CIEsameCongiuntoBean {
	private String prgesamecong; // not null
	private String prgaccordo; // not null
	private String strcognomerapprescong;
	private String strnomerapprescong;
	private String strcognomeorgdator;
	private String strnomeorgdator;
	private String strmotivorichiesta;
	private String datrichiestadal;
	private String datrichiestaal;
	private String nummesi;
	private String numlavoratori;
	private String flgesameconganno1;
	private String flgesameconganno2;
	private String flgesameconganno3;
	private String flgesameconganno4;
	private String flgesameconganno5;
	private String flgsospanno1;
	private String flgsospanno2;
	private String flgsospanno3;
	private String flgsospanno4;
	private String flgsospanno5;
	private String cdnutins; // not null
	private String dtmins; // not null
	private String cdnutmod; // not null
	private String dtmmod; // not null
	private String numkloesamecong = "0"; // not null

	private TransactionQueryExecutor tex;

	private final Logger _logger = Logger.getLogger(CIEsameCongiuntoBean.class.getName());

	public CIEsameCongiuntoBean(Object cdnUtIns, Object cdnUtMod, TransactionQueryExecutor tex) {
		if (cdnUtIns == null)
			throw new IllegalArgumentException("Il cdnUtIns non puo' essere null.");
		if (cdnUtMod == null)
			throw new IllegalArgumentException("Il cdnUtMod non puo' essere null.");
		if (tex == null)
			throw new IllegalArgumentException("Il TransactionQueryExecutor non puo' essere null.");

		this.cdnutins = cdnUtIns.toString();
		this.cdnutmod = cdnUtMod.toString();

		this.tex = tex;
	}

	public CIEsameCongiuntoBean(SourceBean sb, Object cdnUtIns, Object cdnUtMod, TransactionQueryExecutor tex)
			throws ParseException {
		_logger.info("Inizio la costruzione del bean esame congiunto.");
		if (cdnUtIns == null)
			throw new IllegalArgumentException("Il cdnUtIns non puo' essere null.");
		if (cdnUtMod == null)
			throw new IllegalArgumentException("Il cdnUtMod non puo' essere null.");
		if (tex == null)
			throw new IllegalArgumentException("Il TransactionQueryExecutor non puo' essere null.");

		this.cdnutins = cdnUtIns.toString();
		this.cdnutmod = cdnUtMod.toString();

		this.tex = tex;

		this.setStrcognomerapprescong(sb.getCharacters("STRCOGNOMERAPPRESCONG"));
		this.setStrnomerapprescong(sb.getCharacters("STRNOMERAPPRESCONG"));
		this.setStrcognomeorgdator(sb.getCharacters("STRCOGNOMEORGDATOR"));
		this.setStrnomeorgdator(sb.getCharacters("STRNOMEORGDATOR"));
		this.setStrmotivorichiesta(sb.getCharacters("STRMOTIVORICHIESTA"));
		this.setDatrichiestadal(
				Converter.dateConverter("yyyy-MM-dd", "dd/MM/yyyy", sb.getCharacters("DATRICHIESTADAL")));
		this.setDatrichiestaal(Converter.dateConverter("yyyy-MM-dd", "dd/MM/yyyy", sb.getCharacters("DATRICHIESTAAL")));
		this.setNummesi(sb.getCharacters("NUMMESI"));
		this.setNumlavoratori(sb.getCharacters("NUMLAVORATORI"));
		this.setFlgesameconganno1(sb.getCharacters("FLGESAMECONGANNO1"));
		this.setFlgesameconganno2(sb.getCharacters("FLGESAMECONGANNO2"));
		this.setFlgesameconganno3(sb.getCharacters("FLGESAMECONGANNO3"));
		this.setFlgesameconganno4(sb.getCharacters("FLGESAMECONGANNO4"));
		this.setFlgesameconganno5(sb.getCharacters("FLGESAMECONGANNO5"));
		this.setFlgsospanno1(sb.getCharacters("FLGSOSPANNO1"));
		this.setFlgsospanno2(sb.getCharacters("FLGSOSPANNO2"));
		this.setFlgsospanno3(sb.getCharacters("FLGSOSPANNO3"));
		this.setFlgsospanno4(sb.getCharacters("FLGSOSPANNO4"));
		this.setFlgsospanno5(sb.getCharacters("FLGSOSPANNO5"));
	}

	public void insert() throws EMFInternalError {
		Object pEsameCongiunto[] = new Object[24];
		Object nextPrgEsameCongiunto = getNextPrgEsameCongiunto();
		pEsameCongiunto[0] = nextPrgEsameCongiunto;
		pEsameCongiunto[1] = prgaccordo;
		pEsameCongiunto[2] = strcognomerapprescong;
		pEsameCongiunto[3] = strnomerapprescong;
		pEsameCongiunto[4] = strcognomeorgdator;
		pEsameCongiunto[5] = strnomeorgdator;
		pEsameCongiunto[6] = strmotivorichiesta;
		pEsameCongiunto[7] = datrichiestadal;
		pEsameCongiunto[8] = datrichiestaal;
		pEsameCongiunto[9] = nummesi;
		pEsameCongiunto[10] = numlavoratori;
		pEsameCongiunto[11] = flgesameconganno1;
		pEsameCongiunto[12] = flgesameconganno2;
		pEsameCongiunto[13] = flgesameconganno3;
		pEsameCongiunto[14] = flgesameconganno4;
		pEsameCongiunto[15] = flgesameconganno5;
		pEsameCongiunto[16] = flgsospanno1;
		pEsameCongiunto[17] = flgsospanno2;
		pEsameCongiunto[18] = flgsospanno3;
		pEsameCongiunto[19] = flgsospanno4;
		pEsameCongiunto[20] = flgsospanno5;
		pEsameCongiunto[21] = cdnutins;
		pEsameCongiunto[22] = cdnutmod;
		pEsameCongiunto[23] = numkloesamecong;

		tex.executeQuery("INSERT_CI_ESAME_CONGIUNTO", pEsameCongiunto, "INSERT");
		// solo se l'inserimento va a buon fine setto il pEsameCongiunto con cui e' stato registrato il record
		setPrgesamecong(nextPrgEsameCongiunto.toString());

		_logger.info("inserito l'esame congiunto.");
	}

	private BigDecimal getNextPrgEsameCongiunto() throws EMFInternalError {

		String query = "select s_ci_esame_congiunto.nextval from dual";
		SourceBean row = (SourceBean) tex.executeQueryByStringStatement(query, new Object[] {}, "SELECT");
		BigDecimal nextval = (BigDecimal) row.getAttribute("row.nextval");

		if (nextval == null)
			throw new EMFInternalError(EMFErrorSeverity.BLOCKING,
					"Impossibile calcolare la sequence per la tabella CI_ESAME_CONGIUNTO");
		return nextval;
	}

	public String getPrgesamecong() {
		return prgesamecong;
	}

	public void setPrgesamecong(String prgesamecong) {
		this.prgesamecong = prgesamecong;
	}

	public String getPrgaccordo() {
		return prgaccordo;
	}

	public void setPrgaccordo(String prgaccordo) {
		this.prgaccordo = prgaccordo;
	}

	public String getStrcognomerapprescong() {
		return strcognomerapprescong;
	}

	public void setStrcognomerapprescong(String strcognomerapprescong) {
		this.strcognomerapprescong = strcognomerapprescong;
	}

	public String getStrnomerapprescong() {
		return strnomerapprescong;
	}

	public void setStrnomerapprescong(String strnomerapprescong) {
		this.strnomerapprescong = strnomerapprescong;
	}

	public String getStrcognomeorgdator() {
		return strcognomeorgdator;
	}

	public void setStrcognomeorgdator(String strcognomeorgdator) {
		this.strcognomeorgdator = strcognomeorgdator;
	}

	public String getStrnomeorgdator() {
		return strnomeorgdator;
	}

	public void setStrnomeorgdator(String strnomeorgdator) {
		this.strnomeorgdator = strnomeorgdator;
	}

	public String getStrmotivorichiesta() {
		return strmotivorichiesta;
	}

	public void setStrmotivorichiesta(String strmotivorichiesta) {
		this.strmotivorichiesta = strmotivorichiesta;
	}

	public String getDatrichiestadal() {
		return datrichiestadal;
	}

	public void setDatrichiestadal(String datrichiestadal) {
		this.datrichiestadal = datrichiestadal;
	}

	public String getDatrichiestaal() {
		return datrichiestaal;
	}

	public void setDatrichiestaal(String datrichiestaal) {
		this.datrichiestaal = datrichiestaal;
	}

	public String getNummesi() {
		return nummesi;
	}

	public void setNummesi(String nummesi) {
		this.nummesi = nummesi;
	}

	public String getNumlavoratori() {
		return numlavoratori;
	}

	public void setNumlavoratori(String numlavoratori) {
		this.numlavoratori = numlavoratori;
	}

	public String getFlgesameconganno1() {
		return flgesameconganno1;
	}

	public void setFlgesameconganno1(String flgesameconganno1) {
		this.flgesameconganno1 = flgesameconganno1;
	}

	public String getFlgesameconganno2() {
		return flgesameconganno2;
	}

	public void setFlgesameconganno2(String flgesameconganno2) {
		this.flgesameconganno2 = flgesameconganno2;
	}

	public String getFlgesameconganno3() {
		return flgesameconganno3;
	}

	public void setFlgesameconganno3(String flgesameconganno3) {
		this.flgesameconganno3 = flgesameconganno3;
	}

	public String getFlgesameconganno4() {
		return flgesameconganno4;
	}

	public void setFlgesameconganno4(String flgesameconganno4) {
		this.flgesameconganno4 = flgesameconganno4;
	}

	public String getFlgesameconganno5() {
		return flgesameconganno5;
	}

	public void setFlgesameconganno5(String flgesameconganno5) {
		this.flgesameconganno5 = flgesameconganno5;
	}

	public String getFlgsospanno1() {
		return flgsospanno1;
	}

	public void setFlgsospanno1(String flgsospanno1) {
		this.flgsospanno1 = flgsospanno1;
	}

	public String getFlgsospanno2() {
		return flgsospanno2;
	}

	public void setFlgsospanno2(String flgsospanno2) {
		this.flgsospanno2 = flgsospanno2;
	}

	public String getFlgsospanno3() {
		return flgsospanno3;
	}

	public void setFlgsospanno3(String flgsospanno3) {
		this.flgsospanno3 = flgsospanno3;
	}

	public String getFlgsospanno4() {
		return flgsospanno4;
	}

	public void setFlgsospanno4(String flgsospanno4) {
		this.flgsospanno4 = flgsospanno4;
	}

	public String getFlgsospanno5() {
		return flgsospanno5;
	}

	public void setFlgsospanno5(String flgsospanno5) {
		this.flgsospanno5 = flgsospanno5;
	}

	public String getCdnutins() {
		return cdnutins;
	}

	public void setCdnutins(String cdnutins) {
		this.cdnutins = cdnutins;
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

	public String getNumkloesamecong() {
		return numkloesamecong;
	}

	public void setNumkloesamecong(String numkloesamecong) {
		this.numkloesamecong = numkloesamecong;
	}
}
