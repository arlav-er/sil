/*
 * Creato il 13-giu-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.agenda;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

/**
 * @author gritti
 * 
 * Per modificare il modello associato al commento di questo tipo generato,
 * aprire Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e
 * commenti
 */
public class DynRicercaAgendaLavoratoriSMS implements IDynamicStatementProvider {
	public DynRicercaAgendaLavoratoriSMS() {
	}

	private static final String SELECT_SQL_BASE = " SELECT "
	
    + " an_lavoratore.STRCELL,"
    + " an_lavoratore.STRCOGNOME,"
    + " an_lavoratore.STRNOME,"
    + " de_cpi.strtel AS TELCPI,"
    + " de_cpi.STRTELRDC as TELRDCCPI,"
			
			
			+ " AG_AGENDA.PRGSPI, "
			+ " AG_AGENDA.PRGSPIEFF, "
			+ " AG_AGENDA.CODCPI ||'|'|| AG_AGENDA.PRGAPPUNTAMENTO ||'|'|| AG_LAVORATORE.CDNLAVORATORE as SMSKEY,"
			+ " AG_AGENDA.CODCPI ,"
			+ " DE_CPI.STRDESCRIZIONE AS strdescrizione,"
			+ " AG_AGENDA.PRGAPPUNTAMENTO, "
			+ " AG_LAVORATORE.CDNLAVORATORE, "
			+ " TO_CHAR(AG_AGENDA.DTMDATAORA,'DD/MM/YYYY') AS data, "
			+ " TO_CHAR(AG_AGENDA.DTMDATAORA,'hh24:mi') AS ora, "
			+ " AG_AGENDA.NUMMINUTI AS durata, "
			+ " nvl(AN_LAVORATORE.STRCOGNOME, '') ||'&nbsp; '|| nvl(AN_LAVORATORE.STRNOME,'') AS lavoratore, "
			+ " AN_LAVORATORE.STRCOGNOME AS cognome, "
			+ " AN_LAVORATORE.STRNOME AS nome, "
			+ " AN_LAVORATORE.STRCODICEFISCALE AS codicefiscale, "
			+ " DE_SERVIZIO.CODSERVIZIO , "
			+ " DE_SERVIZIO.STRDESCRIZIONE AS servizio, "
			+ " DE_AMBIENTE.PRGAMBIENTE , "
			+ " nvl(AN_SPI.STRCOGNOME, '') || '&nbsp;' || nvl(AN_SPI.STRNOME,'') AS operatore, "
			+ " DE_ESITO_APPUNT.STRDESCRIZIONE AS esito, "
			//+ " AN_LAVORATORE.FLGINVIOSMS AS flgInvioSMS "
			+   " DECODE (trunc(SYSDATE-1), GREATEST(TRUNC(SYSDATE-1), nvl(trunc(AN_LAVORATORE.datinviosms),TRUNC(sysdate+1))),'N',AN_LAVORATORE.FLGINVIOSMS) AS flgInvioSMS "
			+ " FROM   AG_AGENDA "
			+ " INNER JOIN ag_lavoratore ON (AG_AGENDA.CODCPI = AG_LAVORATORE.CODCPI AND AG_AGENDA.PRGAPPUNTAMENTO = AG_LAVORATORE.PRGAPPUNTAMENTO) "
			+ " join AN_LAVORATORE on (AN_LAVORATORE.CDNLAVORATORE=AG_LAVORATORE.CDNLAVORATORE) "
			+ " left outer join DE_SERVIZIO on (AG_AGENDA.CODSERVIZIO = DE_SERVIZIO.CODSERVIZIO) "
			+ " left outer join DE_AMBIENTE on (AG_AGENDA.PRGAMBIENTE = DE_AMBIENTE.PRGAMBIENTE) "
			+ " left outer join DE_CPI on (AG_AGENDA.CODCPI = DE_CPI.CODCPI) "
			+ " left outer join AN_SPI on (AG_AGENDA.PRGSPI=AN_SPI.PRGSPI) "
			+ " LEFT OUTER JOIN DE_ESITO_APPUNT ON (AG_AGENDA.CODESITOAPPUNT = DE_ESITO_APPUNT.CODESITOAPPUNT) "
			+ " INNER JOIN DE_STATO_APPUNTAMENTO ON (AG_AGENDA.CODSTATOAPPUNTAMENTO = DE_STATO_APPUNTAMENTO.CODSTATOAPPUNTAMENTO) "
			+ " AND DE_STATO_APPUNTAMENTO.FLGATTIVO = 'S'"
			;

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String cpi = (String) req.getAttribute("sel_cpi");
		String nome = (String) req.getAttribute("strNome");
		String cognome = (String) req.getAttribute("strCognome");
		String cf = (String) req.getAttribute("strCodiceFiscale");
		String operatore = (String) req.getAttribute("sel_operatore");
		String servizio = (String) req.getAttribute("sel_servizio");
		String ambiente = (String) req.getAttribute("sel_aula");		
		String esito = (String) req.getAttribute("esitoApp");
		String dataDal = (String) req.getAttribute("dataDal");
		String dataAl = (String) req.getAttribute("dataAl");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if ((cpi != null) && (!cpi.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			cpi = StringUtils.replace(cpi, "'", "''");
			buf.append(" upper(ag_agenda.codcpi) = '" + cpi.toUpperCase() + "'");
		}

		/* DA DECOMMENTARE --- E' COMMENTATA SOLO PER FARE TEST DI CARICO SU PARMATEST */
		if ((cognome != null) && (!cognome.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			cognome = StringUtils.replace(cognome, "'", "''");
			buf.append(" upper(an_lavoratore.strcognome) = '" + cognome.toUpperCase() + "'");
		}
		
		if ((nome != null) && (!nome.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			nome = StringUtils.replace(nome, "'", "''");
			buf.append(" upper(an_lavoratore.strnome) = '" + nome.toUpperCase() + "'");
		}

		if ((cf != null) && (!cf.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" upper(an_lavoratore.strCodiceFiscale) = '" + cf.toUpperCase() + "'");
		}

		if ((operatore != null) && (!operatore.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" an_spi.prgspi = " + operatore);

		}

		if ((servizio != null) && (!servizio.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" upper(de_servizio.codservizio) = '" + servizio.toUpperCase() + "'");
		}

		if ((ambiente != null) && (!ambiente.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" upper(de_ambiente.prgambiente) = '" + ambiente.toUpperCase() + "'");
		}

		if ((esito != null) && (!esito.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" upper(de_esito_appunt.codesitoappunt) = '" + esito.toUpperCase() + "'");
		}

		if ((dataDal != null) && (!dataDal.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" upper(ag_agenda.dtmdataora)>=TO_DATE('" + dataDal + "','DD/MM/YYYY') ");
		}

		if ((dataAl != null) && (!dataAl.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" upper(ag_agenda.dtmdataora)<=TO_DATE('" + dataAl + "','DD/MM/YYYY') ");
		}

		buf.append(" ORDER BY AN_LAVORATORE.STRCOGNOME, AN_LAVORATORE.STRNOME");
		query_totale.append(buf.toString());
		return query_totale.toString();

	}

}