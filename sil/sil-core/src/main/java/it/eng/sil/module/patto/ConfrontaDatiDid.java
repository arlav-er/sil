package it.eng.sil.module.patto;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.sil.Values;

/**
 * Controlla se alcuni dati della DID sono cambiati (solo per DID in attesa di essere protocollati), in modo da chiudere
 * la DID al momento aperto e riaprirne un'altra con i dati aggiornati. I dati da considerare sono: 1) Data inserimento
 * nell'elenco anagrafico; 2) Cpi titolare dei dati; 3) Stato occupazionale; 4) movimento precedente.
 */
public class ConfrontaDatiDid {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ConfrontaDatiDid.class.getName());

	private BigDecimal cdnLavoratore;

	public ConfrontaDatiDid(BigDecimal cdnLav) {
		cdnLavoratore = cdnLav;
	}

	/*
	 * Confronta le informazioni sui due SourceBean passati, restituendo true se sono uguali, false se sono diversi.
	 * naturalmente il confronto lo effettua sulle informazioni che ci interessano e che ho elencato sopra. sb1:
	 * SourceBean di come è memorizzata la DID sb2: SourceBean di come dovrebbe essere la DID
	 */
	public boolean confrontaDati(SourceBean sb1, SourceBean sb2) {
		/*
		 * 1) Data inserimento nell'elenco anagrafico; 2) Cpi titolare dei dati; 3) Stato occupazionale; 4) movimento
		 * precedente.
		 */
		boolean isEqual = true;
		String codStatoOccupaz = "";
		String codUltimoContratto = "";
		String codCPI = "";
		String datInizio = "";
		String codStatoOccupaz2 = "";
		String codUltimoContratto2 = "";
		String codCPI2 = "";
		String datInizio2 = "";

		if ((sb1 != null) && (sb2 != null) && (sb1.getAttribute("ROW.CODSTATOATTO") != null)
				&& !sb1.getAttribute("ROW.CODSTATOATTO").equals("PR")) {
			codStatoOccupaz = (String) sb1.getAttribute("ROW.codStatoOccupazRagg");
			codUltimoContratto = (String) sb1.getAttribute("ROW.CODULTIMOCONTRATTO");
			codCPI = (String) sb1.getAttribute("ROW.codCpi");
			datInizio = (String) sb1.getAttribute("ROW.datInizio");

			codStatoOccupaz2 = (String) sb2.getAttribute("ROW.codStatoOccupazRagg");
			codUltimoContratto2 = (String) sb2.getAttribute("ROW.CODULTIMOCONTRATTO");
			codCPI2 = (String) sb2.getAttribute("ROW.codCpiTit");
			datInizio2 = (String) sb2.getAttribute("ROW.datInizio");

			if ((codStatoOccupaz != null) && (codStatoOccupaz2 != null) && (codUltimoContratto != null)
					&& (codUltimoContratto2 != null)) {
				if (!codStatoOccupaz.equals(codStatoOccupaz2) || !codUltimoContratto.equals(codUltimoContratto2)
						|| !codCPI.equals(codCPI2) || !datInizio.equals(datInizio2)) {
					isEqual = false;
				}
			}
		}

		return isEqual;
	}

	/*
	 * Restituisce il SourceBean con le informazioni della DID aggiornate (come dovrebbe essere)
	 */
	public SourceBean getDidAttuale() throws SourceBeanException {
		String prgElTmp = "";
		Object params[];
		SourceBean row = null;
		SourceBean prgElAn = null;
		SourceBean ret = new SourceBean("ROWS");
		SourceBean ret1 = new SourceBean("ROW");

		try {
			ret.setAttribute(ret1);
			// Seleziona del prgelencoanagrafico a partire dal cdnlavoratore
			params = new Object[1];
			params[0] = cdnLavoratore;
			prgElAn = (SourceBean) com.engiweb.framework.util.QueryExecutor.executeQuery("GET_ULTIMOELANAG_DA_CDNLAV",
					params, "SELECT", Values.DB_SIL_DATI);

			if (prgElAn.getAttribute("row.prgelencoanagrafico") != null) {
				prgElTmp = prgElAn.getAttribute("row.prgelencoanagrafico").toString();
				params[0] = prgElTmp;
				row = (SourceBean) QueryExecutor.executeQuery("GET_ELANAG", params, "SELECT", Values.DB_SIL_DATI);

				if (row.getAttribute("row.prgelencoanagrafico") != null) {
					Object prgelencoAnag = row.getAttribute("row.prgelencoanagrafico");
					Object datInizio = row.getAttribute("row.datInizio");
					Object descCPI = row.getAttribute("row.descCPI");
					Object codCpiTit = row.getAttribute("row.CODCPI");
					ret1.setAttribute("prgelencoanagrafico", prgelencoAnag);
					ret1.setAttribute("datInizio", datInizio);
					if (descCPI != null)
						ret1.setAttribute("CPITITOLARE", descCPI);
					if (codCpiTit != null)
						ret1.setAttribute("codCpiTit", codCpiTit);
				}
			}

			params = new Object[5];
			params[0] = cdnLavoratore;
			params[1] = cdnLavoratore;
			params[2] = cdnLavoratore;
			params[3] = cdnLavoratore;
			params[4] = cdnLavoratore;
			row = (SourceBean) QueryExecutor.executeQuery("GET_STATO_OCCUPAZ", params, "SELECT", Values.DB_SIL_DATI);

			if (row.getAttribute("row.prgstatoOccupaz") != null) {
				Object prgstatoOccupaz = row.getAttribute("row.prgstatoOccupaz");
				Object descrizione = row.getAttribute("row.descrizionestato");
				Object codstatooccupazragg = row.getAttribute("row.codstatooccupazragg");
				ret1.setAttribute("prgstatoOccupaz", prgstatoOccupaz);
				ret1.setAttribute("descrizionestato", descrizione);
				ret1.setAttribute("codstatooccupazragg", codstatooccupazragg);
			}

			// ULTIMO MOV
			params = new Object[1];
			params[0] = cdnLavoratore;
			row = (SourceBean) QueryExecutor.executeQuery("GET_ULTIMO_MOV", params, "SELECT", Values.DB_SIL_DATI);
			if (row.getAttribute("row.codContratto") != null) {
				Object codContrattoMovimento = row.getAttribute("ROW.codContratto");
				ret1.setAttribute("codUltimoContratto", codContrattoMovimento);
			}
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"ConfrontaDatiDid" + "::select: response.setAttribute((SourceBean)rowObject)", ex);

		} // catch (Exception ex)

		return ret;
	}

	/*
	 * Restituisce il SourceBean con le informazioni della DID attualmente memorizzata nel DB.
	 */
	public SourceBean getDidMemorizzata(BigDecimal prgDichDisponibilita) throws SourceBeanException {
		Object params[] = new Object[1];
		SourceBean rowsSourceBean = null;
		boolean prgPresente = true;

		try {
			if (prgDichDisponibilita == null) {
				params[0] = cdnLavoratore;
				SourceBean rowsDichDispo = (SourceBean) QueryExecutor.executeQuery("GET_ULTIMO_AN_DISPO_DA_CDNLAV",
						params, "SELECT", Values.DB_SIL_DATI);
				if (rowsDichDispo != null && rowsDichDispo.containsAttribute("ROW.PRGDICHDISPONIBILITA")
						&& !rowsDichDispo.getAttribute("ROW.PRGDICHDISPONIBILITA").equals("")) {
					prgDichDisponibilita = (BigDecimal) rowsDichDispo.getAttribute("ROW.PRGDICHDISPONIBILITA");
				}
			}

			params[0] = prgDichDisponibilita;
			rowsSourceBean = (SourceBean) QueryExecutor.executeQuery("GET_AN_DISPO", params, "SELECT",
					Values.DB_SIL_DATI);

			it.eng.sil.util.TraceWrapper.debug(_logger, "ConfrontaDatiDid" + "::select: rowsSourceBean",
					rowsSourceBean);

		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					"ConfrontaDatiDid" + "::select: response.setAttribute((SourceBean)rowObject)", ex);

		} // catch (Exception ex)

		return rowsSourceBean;
	}

	public boolean chiudiDid(Object params[]) {
		Boolean ris = new Boolean(false);
		try {
			ris = (Boolean) QueryExecutor.executeQuery("ST_CHIUDI_DID", params, "UPDATE", Values.DB_SIL_DATI);
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "ConfrontaDatiDid" + "::update: ST_CHIUDI_DID", ex);

		} // catch (Exception ex)
		return ris.booleanValue();
	}

}
