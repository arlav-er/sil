package it.eng.sil.util.batch;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.mappers.OracleSQLMapper;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.module.batch.Constants;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.sms.ContattoSMS;

public class InviaSMS {

	private int tipoBatch;
	private String servizio;
	private String statoAppuntamento;
	private BigDecimal motivoContatto;
	private BigDecimal giorniContatto;
	private BigDecimal giorniPeriodoCheck;
	private BigDecimal prgAzione;
	private String codEsitoAz;
	private String tipoSMS;
	private BigDecimal prgTipoEvidenza;
	private String motivoFineAttoDid;
	private String iscrizioneCM;
	private String sqlQuery;
	private BigDecimal prgSpi;
	private String cognomeSpi;
	private String nomeSpi;
	private String telSpi;
	private String descrizioneBatch;
	private BigDecimal giorniRangeDataFineDid;
	private String codCpi;

	private String QUERY_PREFIX = " SELECT * FROM ( ";

	// QUERY PER INVIO SMS APPUNTAMENTI
	private String QUERY_SMS_APPUNTAMENTI = " SELECT LAV.CDNLAVORATORE, LAV.STRCODICEFISCALE, LAV.FLGINVIOSMS, LAV.STRCELL, LAV.STRCOGNOME, LAV.STRNOME, "
			+ " TO_CHAR(LAV.DATNASC, 'DD/MM/YYYY') DATNASC, (CASE WHEN LAV.STRCELL IS NOT NULL THEN 'S' ELSE 'N' END) FLAGCELL, "
			+ " TO_CHAR(AG_AGENDA.DTMDATAORA, 'DD/MM/YYYY') DATRIL, CPICOMP.STRDESCRIZIONE CPICOMPTIT, "
			+ " AG_AGENDA.CODCPI, DE_CPI.STRDESCRIZIONE DESCCPI, DE_CPI.STRINDIRIZZO INDIRIZZOCPI, DE_CPI.STRTEL TELCPI, "
			+ " TO_CHAR(AG_AGENDA.DTMDATAORA, 'DD/MM/YYYY') DATACONTATTO, TO_CHAR(AG_AGENDA.DTMDATAORA, 'HH24:MI') ORACONTATTO "
			+ " FROM AG_AGENDA INNER JOIN AG_LAVORATORE ON (AG_AGENDA.PRGAPPUNTAMENTO = AG_LAVORATORE.PRGAPPUNTAMENTO) "
			+ " INNER JOIN DE_CPI ON (AG_AGENDA.CODCPI = DE_CPI.CODCPI) "
			+ " INNER JOIN AN_LAVORATORE LAV ON (AG_LAVORATORE.CDNLAVORATORE = LAV.CDNLAVORATORE) "
			+ " INNER JOIN AN_LAV_STORIA_INF LAVINF ON (LAVINF.CDNLAVORATORE = LAV.CDNLAVORATORE AND LAVINF.DATFINE IS NULL) "
			+ " INNER JOIN DE_CPI CPICOMP ON (LAVINF.CODCPITIT = CPICOMP.CODCPI) ";

	private String QUERY_LEFT_CONTATTO_APPUNTAMENTI = " LEFT JOIN AG_CONTATTO ON (AG_LAVORATORE.CDNLAVORATORE = AG_CONTATTO.CDNLAVORATORE AND AG_CONTATTO.PRGTIPOCONTATTO = 5 "
			+ " AND AG_CONTATTO.FLGINVIATOSMS = 'S' ";

	// QUERY PER INVIO SMS AZIONI PROGRAMMATE
	private String QUERY_SMS_AZIONI_CONCORDATE = " SELECT LAV.CDNLAVORATORE, LAV.STRCODICEFISCALE, LAV.FLGINVIOSMS, LAV.STRCELL, LAV.STRCOGNOME, LAV.STRNOME, "
			+ " TO_CHAR(LAV.DATNASC, 'DD/MM/YYYY') DATNASC, (CASE WHEN LAV.STRCELL IS NOT NULL THEN 'S' ELSE 'N' END) FLAGCELL, "
			+ " TO_CHAR(PERCORSO.DATSTIMATA, 'DD/MM/YYYY') DATRIL, CPICOMP.STRDESCRIZIONE CPICOMPTIT, "
			+ " OR_COLLOQUIO.CODCPI, DE_CPI.STRDESCRIZIONE DESCCPI, DE_CPI.STRINDIRIZZO INDIRIZZOCPI, DE_CPI.STRTEL TELCPI, DE_AZIONE.STRDESCRIZIONE DESCAZIONE, "
			+ " TO_CHAR(PERCORSO.DATSTIMATA, 'DD/MM/YYYY') DATACONTATTO, TO_CHAR(PERCORSO.DATSTIMATA, 'HH24:MI') ORACONTATTO "
			+ " FROM AN_LAVORATORE LAV INNER JOIN OR_COLLOQUIO ON (LAV.CDNLAVORATORE = OR_COLLOQUIO.CDNLAVORATORE) "
			+ " INNER JOIN AN_LAV_STORIA_INF LAVINF ON (LAVINF.CDNLAVORATORE = LAV.CDNLAVORATORE AND LAVINF.DATFINE IS NULL) "
			+ " INNER JOIN OR_PERCORSO_CONCORDATO PERCORSO ON (OR_COLLOQUIO.PRGCOLLOQUIO = PERCORSO.PRGCOLLOQUIO) "
			+ " INNER JOIN DE_AZIONE ON (PERCORSO.PRGAZIONI = DE_AZIONE.PRGAZIONI) "
			+ " INNER JOIN DE_CPI ON (OR_COLLOQUIO.CODCPI = DE_CPI.CODCPI) "
			+ " INNER JOIN DE_CPI CPICOMP ON (LAVINF.CODCPITIT = CPICOMP.CODCPI) ";

	private String QUERY_LEFT_CONTATTO_AZIONI_CONCORDATE = " LEFT JOIN AG_CONTATTO ON (LAV.CDNLAVORATORE = AG_CONTATTO.CDNLAVORATORE AND AG_CONTATTO.PRGTIPOCONTATTO = 5 "
			+ " AND AG_CONTATTO.FLGINVIATOSMS = 'S' ";

	// QUERY PER INVIO SMS CONFERMA ANNUALE DID
	private String QUERY_SMS_DID_ANNUALE = " SELECT LAV.CDNLAVORATORE, LAV.STRCODICEFISCALE, LAV.FLGINVIOSMS, LAV.STRCELL, LAV.STRCOGNOME, LAV.STRNOME, "
			+ " TO_CHAR(LAV.DATNASC, 'DD/MM/YYYY') DATNASC, (CASE WHEN LAV.STRCELL IS NOT NULL THEN 'S' ELSE 'N' END) FLAGCELL, "
			+ " TO_CHAR(DID.DATDICHIARAZIONE, 'DD/MM/YYYY') DATRIL, DE_CPI.STRDESCRIZIONE CPICOMPTIT, "
			+ " LAVINF.CODCPITIT CODCPI, DE_CPI.STRDESCRIZIONE DESCCPI, DE_CPI.STRINDIRIZZO INDIRIZZOCPI, DE_CPI.STRTEL TELCPI, "
			+ " TO_CHAR(SYSDATE, 'DD/MM/YYYY') DATACONTATTO, TO_CHAR(SYSDATE, 'HH24:MI') ORACONTATTO "
			+ " FROM AN_LAVORATORE LAV INNER JOIN AM_ELENCO_ANAGRAFICO ELENCO ON (LAV.CDNLAVORATORE = ELENCO.CDNLAVORATORE AND ELENCO.DATCAN IS NULL) "
			+ " INNER JOIN AN_LAV_STORIA_INF LAVINF ON (LAVINF.CDNLAVORATORE = LAV.CDNLAVORATORE AND LAVINF.DATFINE IS NULL) "
			+ " INNER JOIN DE_CPI ON (LAVINF.CODCPITIT = DE_CPI.CODCPI) "
			+ " INNER JOIN AM_DICH_DISPONIBILITA DID ON (ELENCO.PRGELENCOANAGRAFICO = DID.PRGELENCOANAGRAFICO AND DID.DATFINE IS NULL AND DID.CODSTATOATTO = 'PR') ";

	private String QUERY_LEFT_MOVIMENTI_DID_ANNUALE = " LEFT JOIN AM_MOVIMENTO MOV ON (LAV.CDNLAVORATORE = MOV.CDNLAVORATORE AND MOV.CODSTATOATTO = 'PR' "
			+ " AND MOV.CODTIPOMOV <> 'CES' ";

	private String QUERY_LEFT_CONFERMA_DID_ANNUALE = " LEFT JOIN AM_DID_ANNUALE ON (DID.PRGDICHDISPONIBILITA = AM_DID_ANNUALE.PRGDICHDISPONIBILITA "
			+ " AND AM_DID_ANNUALE.CODSTATOATTO = 'PR' ";

	private String QUERY_LEFT_MOBILITA_DID_ANNUALE = " LEFT JOIN AM_MOBILITA_ISCR ON (LAV.CDNLAVORATORE = AM_MOBILITA_ISCR.CDNLAVORATORE "
			+ " AND AM_MOBILITA_ISCR.CODTIPOMOB <> 'NT' ";

	private String QUERY_LEFT_CONTATTO_DID_ANNUALE = " LEFT JOIN AG_CONTATTO ON (LAV.CDNLAVORATORE = AG_CONTATTO.CDNLAVORATORE AND AG_CONTATTO.PRGTIPOCONTATTO = 5 "
			+ " AND AG_CONTATTO.FLGINVIATOSMS = 'S' ";

	// QUERY PER INVIO SMS PERDITA STATO DI DISOCCUPAZIONE
	private String QUERY_SMS_PERDITA_DISOCCUPAZIONE = " SELECT LAV.CDNLAVORATORE, LAV.STRCODICEFISCALE, LAV.FLGINVIOSMS, LAV.STRCELL, LAV.STRCOGNOME, LAV.STRNOME, "
			+ " TO_CHAR(LAV.DATNASC, 'DD/MM/YYYY') DATNASC, (CASE WHEN LAV.STRCELL IS NOT NULL THEN 'S' ELSE 'N' END) FLAGCELL, "
			+ " TO_CHAR(DID.DTMMOD, 'DD/MM/YYYY') DATRIL, DE_CPI.STRDESCRIZIONE CPICOMPTIT, DE_MOTIVO_FINE_ATTO.STRDESCRIZIONE DESCMOTFINEATTO, "
			+ " LAVINF.CODCPITIT CODCPI, DE_CPI.STRDESCRIZIONE DESCCPI, DE_CPI.STRINDIRIZZO INDIRIZZOCPI, DE_CPI.STRTEL TELCPI, "
			+ " TO_CHAR(SYSDATE, 'DD/MM/YYYY') DATACONTATTO, TO_CHAR(SYSDATE, 'HH24:MI') ORACONTATTO "
			+ " FROM AN_LAVORATORE LAV INNER JOIN AM_ELENCO_ANAGRAFICO ELENCO ON (LAV.CDNLAVORATORE = ELENCO.CDNLAVORATORE AND ELENCO.DATCAN IS NULL) "
			+ " INNER JOIN AN_LAV_STORIA_INF LAVINF ON (LAVINF.CDNLAVORATORE = LAV.CDNLAVORATORE AND LAVINF.DATFINE IS NULL) "
			+ " INNER JOIN DE_CPI ON (LAVINF.CODCPITIT = DE_CPI.CODCPI) "
			+ " INNER JOIN AM_DICH_DISPONIBILITA DID ON (ELENCO.PRGELENCOANAGRAFICO = DID.PRGELENCOANAGRAFICO AND DID.DATFINE IS NOT NULL AND DID.CODSTATOATTO = 'PR') "
			+ " LEFT JOIN DE_MOTIVO_FINE_ATTO ON (DID.CODMOTIVOFINEATTO = DE_MOTIVO_FINE_ATTO.CODMOTIVOFINEATTO) ";

	private String QUERY_LEFT_CONTATTO_PERDITA_DISOCC = " LEFT JOIN AG_CONTATTO ON (LAV.CDNLAVORATORE = AG_CONTATTO.CDNLAVORATORE AND AG_CONTATTO.PRGTIPOCONTATTO = 5 "
			+ " AND AG_CONTATTO.FLGINVIATOSMS = 'S' AND TRUNC(AG_CONTATTO.DATCONTATTO) >= TRUNC(DID.DATFINE) ";

	private String QUERY_ALL_MOTIVI_CHIUSURA_DID_PERDITA_DISOCC = " INNER JOIN TS_CONFIG_CODIFICA ON (TS_CONFIG_CODIFICA.NOMETABELLA = 'DE_MOTIVO_FINE_ATTO' AND "
			+ " TS_CONFIG_CODIFICA.CODTIPOCONFIG = 'CHDIDSMS' AND DID.CODMOTIVOFINEATTO = TS_CONFIG_CODIFICA.CODICE AND TS_CONFIG_CODIFICA.CONFIGURAZIONE = "
			+ " (SELECT NVL(TL.NUM, 0) FROM TS_CONFIG_LOC TL WHERE TL.STRCODRIF = (SELECT TS_GENERALE.CODPROVINCIASIL FROM TS_GENERALE) "
			+ " AND TL.CODTIPOCONFIG = 'CHDIDSMS')) ";

	private String ORDER_BY_COGNOME_NOME = " ) T ORDER BY T.STRCOGNOME, T.STRNOME";

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InviaSMS.class.getName());

	public InviaSMS(int tipoBatch, String servizio, String statoAppuntamento, BigDecimal motivoContatto,
			BigDecimal giorniContatto, BigDecimal giorniPeriodoCheck, BigDecimal prgAzione, String codEsitoAz,
			String codMotivoFineAttoDid, String flgIscrizioneCM, BigDecimal giorniRangeFineDid, String tipoSMS,
			BigDecimal prgTipoEvidenza, BigDecimal prgSpi, String cognomeSpi, String nomeSpi, String telSpi,
			String codCpi) throws Exception {

		setTipoBatch(tipoBatch);
		setPrgSpi(prgSpi);
		setCognomeSpi(cognomeSpi);
		setNomeSpi(nomeSpi);
		setTelSpi(telSpi);
		setPrgTipoEvidenza(prgTipoEvidenza);
		setTipoSMS(tipoSMS);
		setCodCpi(codCpi);

		switch (getTipoBatch()) {
		case Constants.APPUNTAMENTI: {
			setDescrizioneBatch("INVIO SMS APPUNTAMENTI");
			setMotivoContatto(motivoContatto);
			setGiorniContatto(giorniContatto);
			setServizio(servizio);
			setStatoAppuntamento(statoAppuntamento);
			setGiorniPeriodoCheck(giorniPeriodoCheck);

			if (getMotivoContatto() != null) {
				QUERY_LEFT_CONTATTO_APPUNTAMENTI = QUERY_LEFT_CONTATTO_APPUNTAMENTI
						+ " AND AG_CONTATTO.PRGMOTCONTATTO = " + getMotivoContatto();
			}
			if (getGiorniContatto() != null) {
				QUERY_LEFT_CONTATTO_APPUNTAMENTI = QUERY_LEFT_CONTATTO_APPUNTAMENTI
						+ " AND TRUNC(SYSDATE) - TRUNC(AG_CONTATTO.DATCONTATTO) >= 0 "
						+ " AND TRUNC(SYSDATE) - TRUNC(AG_CONTATTO.DATCONTATTO) <= " + getGiorniContatto().intValue();
			}
			QUERY_LEFT_CONTATTO_APPUNTAMENTI = QUERY_LEFT_CONTATTO_APPUNTAMENTI + ") ";

			QUERY_SMS_APPUNTAMENTI = QUERY_PREFIX + QUERY_SMS_APPUNTAMENTI + QUERY_LEFT_CONTATTO_APPUNTAMENTI;

			QUERY_SMS_APPUNTAMENTI = QUERY_SMS_APPUNTAMENTI + " WHERE AG_CONTATTO.PRGCONTATTO IS NULL";

			if (getServizio() != null && !getServizio().equals("")) {
				QUERY_SMS_APPUNTAMENTI = QUERY_SMS_APPUNTAMENTI + " AND AG_AGENDA.CODSERVIZIO = '" + getServizio()
						+ "'";
			}

			if (getStatoAppuntamento() != null && !getStatoAppuntamento().equals("")) {
				QUERY_SMS_APPUNTAMENTI = QUERY_SMS_APPUNTAMENTI + " AND AG_AGENDA.CODSTATOAPPUNTAMENTO = '"
						+ getStatoAppuntamento() + "'";
			}

			if (getGiorniPeriodoCheck() != null) {
				QUERY_SMS_APPUNTAMENTI = QUERY_SMS_APPUNTAMENTI
						+ " AND TRUNC(AG_AGENDA.DTMDATAORA) BETWEEN TRUNC(SYSDATE + 1) AND TRUNC(SYSDATE + "
						+ getGiorniPeriodoCheck().intValue() + ")";
			}

			if (!StringUtils.isEmptyNoBlank(getCodCpi())) {
				QUERY_SMS_APPUNTAMENTI = QUERY_SMS_APPUNTAMENTI + " AND AG_AGENDA.CODCPI = '" + getCodCpi() + "'";
			}

			QUERY_SMS_APPUNTAMENTI = QUERY_SMS_APPUNTAMENTI + ORDER_BY_COGNOME_NOME;

			setSqlQuery(QUERY_SMS_APPUNTAMENTI);
			break;
		}

		case Constants.AZIONI_PROGRAMMATE: {
			setDescrizioneBatch("INVIO SMS AZIONI PROGRAMMATE");
			setMotivoContatto(motivoContatto);
			setGiorniContatto(giorniContatto);
			setServizio(servizio);
			setPrgAzione(prgAzione);
			setCodEsitoAz(codEsitoAz);
			setGiorniPeriodoCheck(giorniPeriodoCheck);

			if (getMotivoContatto() != null) {
				QUERY_LEFT_CONTATTO_AZIONI_CONCORDATE = QUERY_LEFT_CONTATTO_AZIONI_CONCORDATE
						+ " AND AG_CONTATTO.PRGMOTCONTATTO = " + getMotivoContatto();
			}
			if (getGiorniContatto() != null) {
				QUERY_LEFT_CONTATTO_AZIONI_CONCORDATE = QUERY_LEFT_CONTATTO_AZIONI_CONCORDATE
						+ " AND TRUNC(SYSDATE) - TRUNC(AG_CONTATTO.DATCONTATTO) >= 0 "
						+ " AND TRUNC(SYSDATE) - TRUNC(AG_CONTATTO.DATCONTATTO) <= " + getGiorniContatto().intValue();
			}
			QUERY_LEFT_CONTATTO_AZIONI_CONCORDATE = QUERY_LEFT_CONTATTO_AZIONI_CONCORDATE + ") ";

			QUERY_SMS_AZIONI_CONCORDATE = QUERY_PREFIX + QUERY_SMS_AZIONI_CONCORDATE
					+ QUERY_LEFT_CONTATTO_AZIONI_CONCORDATE;

			QUERY_SMS_AZIONI_CONCORDATE = QUERY_SMS_AZIONI_CONCORDATE + " WHERE AG_CONTATTO.PRGCONTATTO IS NULL";

			if (getPrgAzione() != null) {
				QUERY_SMS_AZIONI_CONCORDATE = QUERY_SMS_AZIONI_CONCORDATE + " AND PERCORSO.PRGAZIONI = "
						+ getPrgAzione();
			}

			if (getCodEsitoAz() != null && !getCodEsitoAz().equals("")) {
				QUERY_SMS_AZIONI_CONCORDATE = QUERY_SMS_AZIONI_CONCORDATE + " AND PERCORSO.CODESITO = '"
						+ getCodEsitoAz() + "'";
			}

			if (getGiorniPeriodoCheck() != null) {
				QUERY_SMS_AZIONI_CONCORDATE = QUERY_SMS_AZIONI_CONCORDATE
						+ " AND TRUNC(PERCORSO.DATSTIMATA) BETWEEN TRUNC(SYSDATE + 1) AND TRUNC(SYSDATE + "
						+ getGiorniPeriodoCheck().intValue() + ")";
			}

			QUERY_SMS_AZIONI_CONCORDATE = QUERY_SMS_AZIONI_CONCORDATE + " AND NOT EXISTS (SELECT 1 FROM AG_LAVORATORE "
					+ " INNER JOIN AG_AGENDA ON (AG_LAVORATORE.PRGAPPUNTAMENTO = AG_AGENDA.PRGAPPUNTAMENTO) "
					+ " WHERE AG_LAVORATORE.CDNLAVORATORE = LAV.CDNLAVORATORE AND AG_AGENDA.CODSTATOAPPUNTAMENTO = '2' "
					+ " AND TRUNC(AG_AGENDA.DTMDATAORA) BETWEEN TRUNC(SYSDATE) AND TRUNC(PERCORSO.DATSTIMATA))";

			if (getServizio() != null && !getServizio().equals("")) {
				QUERY_SMS_AZIONI_CONCORDATE = QUERY_SMS_AZIONI_CONCORDATE
						+ " AND NOT EXISTS (SELECT 1 FROM AG_LAVORATORE "
						+ " INNER JOIN AG_AGENDA ON (AG_LAVORATORE.PRGAPPUNTAMENTO = AG_AGENDA.PRGAPPUNTAMENTO) "
						+ " WHERE AG_LAVORATORE.CDNLAVORATORE = LAV.CDNLAVORATORE AND AG_AGENDA.CODSTATOAPPUNTAMENTO = '2' "
						+ " AND AG_AGENDA.CODSERVIZIO = '" + getServizio()
						+ "' AND TRUNC(AG_AGENDA.DTMDATAORA) > TRUNC(SYSDATE))";
			}

			if (!StringUtils.isEmptyNoBlank(getCodCpi())) {
				QUERY_SMS_AZIONI_CONCORDATE = QUERY_SMS_AZIONI_CONCORDATE + " AND OR_COLLOQUIO.CODCPI = '" + getCodCpi()
						+ "'";
			}

			QUERY_SMS_AZIONI_CONCORDATE = QUERY_SMS_AZIONI_CONCORDATE + ORDER_BY_COGNOME_NOME;

			setSqlQuery(QUERY_SMS_AZIONI_CONCORDATE);
			break;
		}

		case Constants.CONFERMA_DID: {
			setDescrizioneBatch("INVIO SMS CONFERMA ANNUALE DID");
			setMotivoContatto(motivoContatto);
			setGiorniContatto(giorniContatto);

			int annoCorrente = DateUtils.getAnno(DateUtils.getNow());

			QUERY_LEFT_MOVIMENTI_DID_ANNUALE = QUERY_LEFT_MOVIMENTI_DID_ANNUALE
					+ " AND TO_NUMBER(TO_CHAR(MOV.DATINIZIOMOV, 'YYYY')) <= " + annoCorrente
					+ " AND TO_NUMBER(TO_CHAR(NVL(MOV.DATFINEMOVEFFETTIVA, SYSDATE), 'YYYY')) >= " + annoCorrente;
			QUERY_LEFT_MOVIMENTI_DID_ANNUALE = QUERY_LEFT_MOVIMENTI_DID_ANNUALE + ") ";

			QUERY_LEFT_CONFERMA_DID_ANNUALE = QUERY_LEFT_CONFERMA_DID_ANNUALE
					+ " AND AM_DID_ANNUALE.NUMANNODICHIARAZIONE = " + annoCorrente;
			QUERY_LEFT_CONFERMA_DID_ANNUALE = QUERY_LEFT_CONFERMA_DID_ANNUALE + ") ";

			if (getMotivoContatto() != null) {
				QUERY_LEFT_CONTATTO_DID_ANNUALE = QUERY_LEFT_CONTATTO_DID_ANNUALE + " AND AG_CONTATTO.PRGMOTCONTATTO = "
						+ getMotivoContatto();
			}
			if (getGiorniContatto() != null) {
				QUERY_LEFT_CONTATTO_DID_ANNUALE = QUERY_LEFT_CONTATTO_DID_ANNUALE
						+ " AND TRUNC(SYSDATE) - TRUNC(AG_CONTATTO.DATCONTATTO) >= 0 "
						+ " AND TRUNC(SYSDATE) - TRUNC(AG_CONTATTO.DATCONTATTO) <= " + getGiorniContatto().intValue()
						+ " AND TO_NUMBER(TO_CHAR(AG_CONTATTO.DATCONTATTO, 'YYYY')) = " + annoCorrente;
			}
			QUERY_LEFT_CONTATTO_DID_ANNUALE = QUERY_LEFT_CONTATTO_DID_ANNUALE + ") ";

			QUERY_LEFT_MOBILITA_DID_ANNUALE = QUERY_LEFT_MOBILITA_DID_ANNUALE
					+ " AND TO_NUMBER(TO_CHAR(AM_MOBILITA_ISCR.DATINIZIO, 'YYYY')) <= " + annoCorrente
					+ " AND TO_NUMBER(TO_CHAR(NVL(AM_MOBILITA_ISCR.DATFINE, SYSDATE), 'YYYY')) >= " + annoCorrente
					+ ") ";

			QUERY_SMS_DID_ANNUALE = QUERY_PREFIX + QUERY_SMS_DID_ANNUALE + QUERY_LEFT_CONFERMA_DID_ANNUALE
					+ QUERY_LEFT_MOVIMENTI_DID_ANNUALE + QUERY_LEFT_CONTATTO_DID_ANNUALE
					+ QUERY_LEFT_MOBILITA_DID_ANNUALE;

			QUERY_SMS_DID_ANNUALE = QUERY_SMS_DID_ANNUALE + " WHERE TO_NUMBER(TO_CHAR(DID.DATDICHIARAZIONE, 'YYYY')) < "
					+ annoCorrente;
			QUERY_SMS_DID_ANNUALE = QUERY_SMS_DID_ANNUALE
					+ " AND AM_DID_ANNUALE.PRGDIDANNUALE IS NULL AND MOV.PRGMOVIMENTO IS NULL AND AG_CONTATTO.PRGCONTATTO IS NULL";
			QUERY_SMS_DID_ANNUALE = QUERY_SMS_DID_ANNUALE + " AND AM_MOBILITA_ISCR.PRGMOBILITAISCR IS NULL";

			if (!StringUtils.isEmptyNoBlank(getCodCpi())) {
				QUERY_SMS_DID_ANNUALE = QUERY_SMS_DID_ANNUALE + " AND LAVINF.CODCPITIT = '" + getCodCpi() + "'";
			}

			QUERY_SMS_DID_ANNUALE = QUERY_SMS_DID_ANNUALE + ORDER_BY_COGNOME_NOME;

			setSqlQuery(QUERY_SMS_DID_ANNUALE);
			break;
		}

		case Constants.PERDITA_DISOCC: {
			setDescrizioneBatch("INVIO SMS PERDITA STATO DISOCCUPAZIONE");
			setMotivoContatto(motivoContatto);
			setMotivoFineAttoDid(codMotivoFineAttoDid);
			setIscrizioneCM(flgIscrizioneCM);
			setGiorniPeriodoCheck(giorniPeriodoCheck);
			setGiorniRangeDataFineDid(giorniRangeFineDid);

			String encryptKey = System.getProperty("_ENCRYPTER_KEY_");

			if (getMotivoContatto() != null) {
				QUERY_LEFT_CONTATTO_PERDITA_DISOCC = QUERY_LEFT_CONTATTO_PERDITA_DISOCC
						+ " AND AG_CONTATTO.PRGMOTCONTATTO = " + getMotivoContatto();
			}
			QUERY_LEFT_CONTATTO_PERDITA_DISOCC = QUERY_LEFT_CONTATTO_PERDITA_DISOCC + ") ";

			QUERY_SMS_PERDITA_DISOCCUPAZIONE = QUERY_PREFIX + QUERY_SMS_PERDITA_DISOCCUPAZIONE
					+ QUERY_LEFT_CONTATTO_PERDITA_DISOCC;

			if (getMotivoFineAttoDid() == null || getMotivoFineAttoDid().equals("")) {
				QUERY_SMS_PERDITA_DISOCCUPAZIONE = QUERY_SMS_PERDITA_DISOCCUPAZIONE
						+ QUERY_ALL_MOTIVI_CHIUSURA_DID_PERDITA_DISOCC;
			}

			QUERY_SMS_PERDITA_DISOCCUPAZIONE = QUERY_SMS_PERDITA_DISOCCUPAZIONE
					+ " WHERE DID.DATFINE = (SELECT MAX(DIDMAX.DATFINE) "
					+ " FROM AM_DICH_DISPONIBILITA DIDMAX WHERE DIDMAX.PRGELENCOANAGRAFICO = DID.PRGELENCOANAGRAFICO AND DIDMAX.DATFINE IS NOT NULL AND "
					+ " DIDMAX.CODSTATOATTO = 'PR') ";

			QUERY_SMS_PERDITA_DISOCCUPAZIONE = QUERY_SMS_PERDITA_DISOCCUPAZIONE
					+ " AND AG_CONTATTO.PRGCONTATTO IS NULL";

			QUERY_SMS_PERDITA_DISOCCUPAZIONE = QUERY_SMS_PERDITA_DISOCCUPAZIONE + " AND NOT EXISTS (SELECT 1 "
					+ " FROM AM_STATO_OCCUPAZ INNER JOIN DE_STATO_OCCUPAZ ON (AM_STATO_OCCUPAZ.CODSTATOOCCUPAZ = DE_STATO_OCCUPAZ.CODSTATOOCCUPAZ) "
					+ " WHERE AM_STATO_OCCUPAZ.CDNLAVORATORE = LAV.CDNLAVORATORE AND AM_STATO_OCCUPAZ.DATFINE IS NULL AND "
					+ " DE_STATO_OCCUPAZ.CODSTATOOCCUPAZRAGG IN ('D', 'I'))";

			if (getGiorniRangeDataFineDid() != null) {
				String dataInizioIndagineFineDid = DateUtils.getNow();
				String dataIndagineFineDid = DateUtils.aggiungiNumeroGiorni(dataInizioIndagineFineDid,
						-(getGiorniRangeDataFineDid().intValue()));
				QUERY_SMS_PERDITA_DISOCCUPAZIONE = QUERY_SMS_PERDITA_DISOCCUPAZIONE
						+ " AND TRUNC(DID.DATFINE) >= TO_DATE('" + dataIndagineFineDid + "', 'DD/MM/YYYY')";
			}
			if (getMotivoFineAttoDid() != null && !getMotivoFineAttoDid().equals("")) {
				QUERY_SMS_PERDITA_DISOCCUPAZIONE = QUERY_SMS_PERDITA_DISOCCUPAZIONE + " AND DID.CODMOTIVOFINEATTO = '"
						+ getMotivoFineAttoDid() + "'";
			}
			if (getGiorniPeriodoCheck() != null) {
				QUERY_SMS_PERDITA_DISOCCUPAZIONE = QUERY_SMS_PERDITA_DISOCCUPAZIONE
						+ " AND TRUNC(DID.DTMMOD) >= TRUNC(SYSDATE - " + getGiorniPeriodoCheck().intValue() + ")";
			}
			if (getIscrizioneCM() != null && getIscrizioneCM().equalsIgnoreCase("N")) {
				QUERY_SMS_PERDITA_DISOCCUPAZIONE = QUERY_SMS_PERDITA_DISOCCUPAZIONE
						+ " AND NOT EXISTS (SELECT 1 FROM AM_CM_ISCR I " + " WHERE DECRYPT(I.CDNLAVORATORE, '"
						+ encryptKey + "') = LAV.CDNLAVORATORE AND I.CODSTATOATTO = 'PR' "
						+ " AND TRUNC(I.DATDATAINIZIO) >= TRUNC(DID.DATDICHIARAZIONE))";
			}

			if (!StringUtils.isEmptyNoBlank(getCodCpi())) {
				QUERY_SMS_PERDITA_DISOCCUPAZIONE = QUERY_SMS_PERDITA_DISOCCUPAZIONE + " AND LAVINF.CODCPITIT = '"
						+ getCodCpi() + "'";
			}

			QUERY_SMS_PERDITA_DISOCCUPAZIONE = QUERY_SMS_PERDITA_DISOCCUPAZIONE + ORDER_BY_COGNOME_NOME;

			setSqlQuery(QUERY_SMS_PERDITA_DISOCCUPAZIONE);
			break;
		}

		default:
			_logger.debug("Tipo di richiesta non gestibile o inesistente");
		}
	}

	public int invia() throws Exception {
		TransactionQueryExecutor txExec = null;
		BigDecimal cdnutBatch = Properties.UT_OPERATORE_IMPOSTAZIONI;
		SourceBean rowTestoMessaggio = null;
		String msg1 = "";
		String msg2 = "";
		String msg3 = "";
		String msg4 = "";
		int numContattiOK = 0;
		DataConnection dc = null;
		PreparedStatement preparedStatementContatti = null;
		ResultSet resultContatti = null;
		try {
			QueryExecutorObject qExec = getQueryExecutorObject();
			dc = qExec.getDataConnection();
			qExec.setStatement(SQLStatements.getStatement("GET_TESTO_SMS"));
			qExec.setType(QueryExecutorObject.SELECT);
			List<DataField> params = new ArrayList<DataField>();
			params.add(dc.createDataField("CODTIPOSMS", Types.VARCHAR, getTipoSMS()));
			qExec.setInputParameters(params);
			rowTestoMessaggio = (SourceBean) qExec.exec();

			preparedStatementContatti = dc.getInternalConnection().prepareStatement(getSqlQuery());
			resultContatti = preparedStatementContatti.executeQuery();

			boolean success = false;
			if (rowTestoMessaggio != null) {
				rowTestoMessaggio = rowTestoMessaggio.containsAttribute("ROW")
						? (SourceBean) rowTestoMessaggio.getAttribute("ROW")
						: rowTestoMessaggio;
				msg1 = StringUtils.getAttributeStrNotNull(rowTestoMessaggio, "STR30MSG1");
				msg2 = StringUtils.getAttributeStrNotNull(rowTestoMessaggio, "STR30MSG2");
				msg3 = StringUtils.getAttributeStrNotNull(rowTestoMessaggio, "STR30MSG3");
				msg4 = StringUtils.getAttributeStrNotNull(rowTestoMessaggio, "STR30MSG4");
			}

			DataConnection dataConnection = null;
			InitialContext ctx = new InitialContext();
			Object objs = ctx.lookup(Values.JDBC_JNDI_NAME);

			if (resultContatti != null) {
				while (resultContatti.next()) {
					BigDecimal cdnLav = null;
					success = false;
					String txtcontatto = "";
					try {
						if (objs instanceof DataSource) {
							DataSource ds = (DataSource) objs;
							Connection conn = ds.getConnection();
							dataConnection = new DataConnection(conn, "2", new OracleSQLMapper());
						} else {
							_logger.error("Impossibile ottenere una connessione");
							throw new SQLException();
						}
						txExec = new TransactionQueryExecutor(dataConnection, null, null);
						txExec.initTransaction();

						cdnLav = resultContatti.getBigDecimal("CDNLAVORATORE");
						String strCellulare = resultContatti.getString("STRCELL");

						if (strCellulare != null && !strCellulare.equals("")) {
							String cognomeLav = resultContatti.getString("STRCOGNOME");
							String nomeLav = resultContatti.getString("STRNOME");
							String codCpi = resultContatti.getString("CODCPI");
							String descCpi = resultContatti.getString("DESCCPI");
							String indirizzoCpi = resultContatti.getString("INDIRIZZOCPI");
							String telCpi = resultContatti.getString("TELCPI");
							String dataContatto = resultContatti.getString("DATACONTATTO");
							String oraContatto = resultContatti.getString("ORACONTATTO");
							String cfLav = resultContatti.getString("STRCODICEFISCALE");

							if (getTipoBatch() == Constants.APPUNTAMENTI) {
								txtcontatto = msg1 + " il giorno " + dataContatto + " alle ore " + oraContatto
										+ " presso CPI di " + descCpi + " " + msg2 + " " + msg3 + " " + msg4;
							} else {
								if (getTipoBatch() == Constants.AZIONI_PROGRAMMATE) {
									String descAzione = resultContatti.getString("DESCAZIONE");
									if (!descAzione.equals("") && descAzione.length() > 40) {
										descAzione = descAzione.substring(0, 40);
									}
									txtcontatto = msg1 + " " + descCpi + " " + msg2 + " '" + descAzione + "': "
											+ dataContatto + " " + msg3;
								} else {
									if (getTipoBatch() == Constants.CONFERMA_DID) {
										txtcontatto = msg1 + " " + descCpi + " " + msg2 + " " + msg3 + " " + msg4;
									} else {
										if (getTipoBatch() == Constants.PERDITA_DISOCC) {
											String descMotivoFineAttoDid = resultContatti.getString("DESCMOTFINEATTO");
											txtcontatto = msg1 + " " + msg2 + " " + msg3 + " " + descMotivoFineAttoDid
													+ " " + ". " + msg4;
										}
									}
								}
							}

							ContattoSMS contattoSms = new ContattoSMS();
							success = contattoSms.creaContattoBatch(txExec, cdnLav, strCellulare, nomeLav, cognomeLav,
									codCpi, descCpi, indirizzoCpi, telCpi, dataContatto, oraContatto, prgSpi, nomeSpi,
									cognomeSpi, telSpi, getMotivoContatto(), cdnutBatch, txtcontatto);

							if (success) {
								txExec.commitTransaction();
								numContattiOK = numContattiOK + 1;
								_logger.info("Invio SMS al lavoratore " + cfLav + " avvenuto con successo.");
							} else {
								txExec.rollBackTransaction();
								_logger.info("Invio SMS al lavoratore " + cfLav + " fallito.");
								// tracciare evidenza se non esiste una già valida
								String strEvidenza = "'Fallito invio SMS. Il batch che ha fallito l''invio è "
										+ getDescrizioneBatch() + "'";
								tracciaEvidenzaTransazione(cdnLav, getPrgTipoEvidenza(), cdnutBatch, strEvidenza);
							}
						} else {
							// tracciare Evidenza se non esiste una già valida
							String strEvidenza = "'Fallito invio SMS per mancanza del numero di cellulare. Il batch che ha fallito l''invio è "
									+ getDescrizioneBatch() + "'";
							tracciaEvidenza(cdnLav, getPrgTipoEvidenza(), cdnutBatch, strEvidenza, txExec);
							txExec.commitTransaction();
						}
					} catch (Exception eT) {
						if (txExec != null) {
							txExec.rollBackTransaction();
						}
						_logger.error("Errore Batch " + getDescrizioneBatch() + ". Lavoratore: " + cdnLav, eT);
					}
				}
			}
		} catch (Exception e) {
			_logger.error("Errore recupero contatti Invio SMS", e);
		} finally {
			if (resultContatti != null) {
				resultContatti.close();
			}
			if (preparedStatementContatti != null) {
				preparedStatementContatti.close();
			}
			if (dc != null) {
				dc.close();
			}
		}

		return numContattiOK;
	}

	public void tracciaEvidenza(BigDecimal cdnLav, BigDecimal tipoEvidenza, BigDecimal cdnutBatch, String strEvidenza,
			TransactionQueryExecutor txExec) throws Exception {
		boolean insertEvidenza = true;
		String QUERY_ESISTE_EVIDENZA = " SELECT COUNT(*) NUM FROM AN_EVIDENZA WHERE CDNLAVORATORE = " + cdnLav
				+ " AND PRGTIPOEVIDENZA = " + tipoEvidenza + " AND TRUNC(SYSDATE) <= TRUNC(DATDATASCAD)";

		SourceBean evidenza = (SourceBean) txExec.executeQueryByStringStatement(QUERY_ESISTE_EVIDENZA, null,
				TransactionQueryExecutor.SELECT);
		if (evidenza != null) {
			evidenza = evidenza.containsAttribute("ROW") ? (SourceBean) evidenza.getAttribute("ROW") : evidenza;
			if (evidenza.containsAttribute("NUM")) {
				Integer numEvidenze = new Integer(evidenza.getAttribute("NUM").toString());
				if (numEvidenze.intValue() > 0) {
					insertEvidenza = false;
				}
			}
		}

		if (insertEvidenza) {
			String statementInsertEv = "insert into AN_EVIDENZA (PRGEVIDENZA, CDNLAVORATORE, "
					+ " DATDATASCAD, STREVIDENZA, PRGTIPOEVIDENZA, CDNUTINS, DTMINS, CDNUTMOD, DTMMOD) "
					+ " values (S_AN_EVIDENZA.nextVal, " + cdnLav + ", sysdate + 40, " + strEvidenza + ", "
					+ getPrgTipoEvidenza() + ", " + cdnutBatch + ", sysdate, " + cdnutBatch + ", sysdate)";
			txExec.executeQueryByStringStatement(statementInsertEv, null, TransactionQueryExecutor.INSERT);
		}
	}

	public void tracciaEvidenzaTransazione(BigDecimal cdnLav, BigDecimal tipoEvidenza, BigDecimal cdnutBatch,
			String strEvidenza) throws Exception {
		TransactionQueryExecutor txExecCurr = null;
		DataConnection dataConn = null;
		try {
			InitialContext ctxCurr = new InitialContext();
			Object objs = ctxCurr.lookup(Values.JDBC_JNDI_NAME);
			if (objs instanceof DataSource) {
				DataSource dsCurr = (DataSource) objs;
				Connection connCurr = dsCurr.getConnection();
				dataConn = new DataConnection(connCurr, "2", new OracleSQLMapper());
				txExecCurr = new TransactionQueryExecutor(dataConn, null, null);
				txExecCurr.initTransaction();

				tracciaEvidenza(cdnLav, tipoEvidenza, cdnutBatch, strEvidenza, txExecCurr);

				txExecCurr.commitTransaction();
			}
		} catch (Exception eT) {
			if (txExecCurr != null) {
				txExecCurr.rollBackTransaction();
			}
		}
	}

	public static QueryExecutorObject getQueryExecutorObject() throws Exception {
		InitialContext ctx = new InitialContext();
		Object objs = ctx.lookup(Values.JDBC_JNDI_NAME);
		DataConnection dc = null;
		QueryExecutorObject qExec;
		if (objs instanceof DataSource) {
			DataSource ds = (DataSource) objs;
			Connection conn = ds.getConnection();
			dc = new DataConnection(conn, "2", new OracleSQLMapper());
			qExec = new QueryExecutorObject();
			qExec.setRequestContainer(null);
			qExec.setResponseContainer(null);
			qExec.setDataConnection(dc);
			qExec.setType(QueryExecutorObject.SELECT);
			qExec.setTransactional(true);
			qExec.setDontForgetException(false);
		} else {
			_logger.error("Impossibile ottenere una connessione");
			return null;
		}
		return qExec;
	}

	public void setTipoBatch(int valore) {
		this.tipoBatch = valore;
	}

	public void setServizio(String valore) {
		this.servizio = valore;
	}

	public void setStatoAppuntamento(String valore) {
		this.statoAppuntamento = valore;
	}

	public void setMotivoContatto(BigDecimal valore) {
		this.motivoContatto = valore;
	}

	public void setGiorniContatto(BigDecimal valore) {
		this.giorniContatto = valore;
	}

	public void setGiorniPeriodoCheck(BigDecimal valore) {
		this.giorniPeriodoCheck = valore;
	}

	public void setPrgAzione(BigDecimal valore) {
		this.prgAzione = valore;
	}

	public void setCodEsitoAz(String valore) {
		this.codEsitoAz = valore;
	}

	public void setTipoSMS(String valore) {
		this.tipoSMS = valore;
	}

	public void setPrgTipoEvidenza(BigDecimal valore) {
		this.prgTipoEvidenza = valore;
	}

	public void setMotivoFineAttoDid(String valore) {
		this.motivoFineAttoDid = valore;
	}

	public void setIscrizioneCM(String valore) {
		this.iscrizioneCM = valore;
	}

	public void setSqlQuery(String valore) {
		this.sqlQuery = valore;
	}

	public void setPrgSpi(BigDecimal valore) {
		this.prgSpi = valore;
	}

	public void setCognomeSpi(String valore) {
		this.cognomeSpi = valore;
	}

	public void setNomeSpi(String valore) {
		this.nomeSpi = valore;
	}

	public void setTelSpi(String valore) {
		this.telSpi = valore;
	}

	public void setDescrizioneBatch(String valore) {
		this.descrizioneBatch = valore;
	}

	public void setGiorniRangeDataFineDid(BigDecimal valore) {
		this.giorniRangeDataFineDid = valore;
	}

	public int getTipoBatch() {
		return this.tipoBatch;
	}

	public BigDecimal getGiorniContatto() {
		return this.giorniContatto;
	}

	public BigDecimal getMotivoContatto() {
		return this.motivoContatto;
	}

	public String getServizio() {
		return this.servizio;
	}

	public String getStatoAppuntamento() {
		return this.statoAppuntamento;
	}

	public BigDecimal getGiorniPeriodoCheck() {
		return this.giorniPeriodoCheck;
	}

	public String getSqlQuery() {
		return this.sqlQuery;
	}

	public BigDecimal getPrgAzione() {
		return this.prgAzione;
	}

	public String getCodEsitoAz() {
		return this.codEsitoAz;
	}

	public String getDescrizioneBatch() {
		return this.descrizioneBatch;
	}

	public BigDecimal getPrgTipoEvidenza() {
		return this.prgTipoEvidenza;
	}

	public String getTipoSMS() {
		return this.tipoSMS;
	}

	public String getMotivoFineAttoDid() {
		return this.motivoFineAttoDid;
	}

	public String getIscrizioneCM() {
		return this.iscrizioneCM;
	}

	public BigDecimal getGiorniRangeDataFineDid() {
		return this.giorniRangeDataFineDid;
	}

	public String getCodCpi() {
		return codCpi;
	}

	public void setCodCpi(String codCpi) {
		this.codCpi = codCpi;
	}

}
