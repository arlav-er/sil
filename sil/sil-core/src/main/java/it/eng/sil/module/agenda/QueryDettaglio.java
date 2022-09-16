package it.eng.sil.module.agenda;

/*
 * 
 * Questa classe ricava il nome della query da eseguire dall'attributo
 * statement definito nel file di configurazione MODULES.XML:
 * 
 * <MODULE name="MDett_Appuntamento" class="it.eng.sil.module.agenda.DettAppuntamentoModule">
 *   <CONFIG pool="SIL_DATI" title="Appuntamenti">
 *     <QUERIES>
 *       <QUERY statement="GET_DETT_APPUNTAMENTO">
 *         <PARAMETER scope="SERVICE_REQUEST" type="RELATIVE" value="CODCPI"/>
 *         <PARAMETER scope="SERVICE_REQUEST" type="RELATIVE" value="PRGAPPUNTAMENTO"/>
 *     </QUERY>
 *    </QUERIES>
 *  </CONFIG>
 * </MODULE>
 *
 * ed esegue lo statement specificato in STATEMENTS.XML:
 * <STATEMENT name="GET_DETT_APPUNTAMENTO"
 *          query="select ag_agenda.CODCPI, de_cpi.STRDESCRIZIONE as cpi, ag_agenda.PRGAPPUNTAMENTO, 
 *               to_char(ag_agenda.DTMDATAORA, 'dd/mm/yyyy') as data,
 *                 to_char(ag_agenda.DTMDATAORA, 'hh24:mi')  as orario, 
 *                 ag_agenda.NUMMINUTI, 
 *                 ag_agenda.CODSERVIZIO, de_servizio.STRDESCRIZIONE as servizio, 
 *                 ag_agenda.PRGSPI, concat(concat(an_spi.strNome,' '),an_spi.strCognome) as operatore, 
 *                 ag_agenda.TXTNOTE, 
 *                 ag_agenda.PRGTIPOPRENOTAZIONE, de_tipo_prenotazione.STRDESCRIZIONE as tipo_prenotazione, 
 *                 ag_agenda.STRTELRIF, ag_agenda.STREMAILRIF, ag_agenda.STRTELMOBILERIF, 
 *                 ag_agenda.CODEFFETTOAPPUNT, de_effetto_appunt.STRDESCRIZIONE as effetto_app, 
 *                 ag_agenda.CODESITOAPPUNT, de_esito_appunt.STRDESCRIZIONE as esito_app, 
 *                 ag_agenda.PRGEVENTOAZIENDA,
 *                 ag_agenda.NUMKLOAGENDA,ag_agenda.PRGAZIENDA, ag_agenda.PRGUNITA
 *                 FROM ag_agenda
 *                 INNER JOIN de_cpi ON (ag_agenda.CODCPI = de_cpi.CODCPI)
 *                 INNER JOIN de_servizio ON (ag_agenda.CODSERVIZIO = de_servizio.CODSERVIZIO)
 *                 INNER JOIN an_spi ON (ag_agenda.PRGSPI = an_spi.PRGSPI) 
 *                 INNER JOIN de_tipo_prenotazione ON (ag_agenda.PRGTIPOPRENOTAZIONE = de_tipo_prenotazione.PRGTIPOPRENOTAZIONE)
 *                 INNER JOIN de_effetto_appunt ON (ag_agenda.CODEFFETTOAPPUNT = de_effetto_appunt.CODEFFETTOAPPUNT)
 *                 INNER JOIN de_esito_appunt ON (ag_agenda.CODESITOAPPUNT = de_esito_appunt.CODESITOAPPUNT)
 *                 WHERE ag_agenda.CODCPI=? AND ag_agenda.PRGAPPUNTAMENTO=?"
 * />
 * 
 * Restituisce al modulo che la invoca la riga di DETTAGLIO risultato della query:
 * <MDETT_APPUNTAMENTO>
 *   <ROW CODCPI="04830110" CODEFFETTOAPPUNT="SOLDI" CODESITOAPPUNT="BENE" CODSERVIZIO="PRESEL" CPI="VIGO DI FASSA" DATA="13/08/2003" EFFETTO_APP="Si guadagna un tot" ESITO_APP="Molto bene" NUMKLOAGENDA="7345842" NUMMINUTI="60" OPERATORE="Stefania Orioli" ORARIO="00:00" PRGAPPUNTAMENTO="7" PRGAZIENDA="1" PRGSPI="1" PRGTIPOPRENOTAZIONE="1" PRGUNITA="1" SERVIZIO="Preselezione" STREMAILRIF="a7@a.a" STRTELMOBILERIF="7777" STRTELRIF="77771" TIPO_PRENOTAZIONE="Prenotazione per la finale NBA"/>
 * </MDETT_APPUNTAMENTO>
 * 
 * @author: Stefania Orioli
 */

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.sql.DataRow;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

public class QueryDettaglio extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(QueryDettaglio.class.getName());

	public QueryDettaglio() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		String pool = (String) getConfig().getAttribute("POOL");
		SourceBean statement = (SourceBean) getConfig().getAttribute("QUERIES.QUERY");

		SourceBean rowsSourceBean = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(),
				getResponseContainer(), pool, statement, "SELECT");

		it.eng.sil.util.TraceWrapper.debug(_logger, className + "::select: rowsSourceBean", rowsSourceBean);

		Object rowObject = null;
		try {
			if (rowsSourceBean != null) {
				rowObject = rowsSourceBean.getAttribute(DataRow.ROW_TAG);
				if (rowObject != null)
					response.setAttribute((SourceBean) rowObject);
			} else {
				SourceBean errBean = new SourceBean("ERR");
				response.setAttribute(errBean);
			}
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					className + "::select: response.setAttribute((SourceBean)rowObject)", ex);

		} // catch (Exception ex)
	}

}