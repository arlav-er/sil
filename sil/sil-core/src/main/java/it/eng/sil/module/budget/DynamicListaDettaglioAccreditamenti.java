/**
 * 
 */
package it.eng.sil.module.budget;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

/**
 * @author Fatale
 *
 */
public class DynamicListaDettaglioAccreditamenti implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DynamicListaSoggettiAccVoucher.class.getName());

	private static final String SELECT_SQL_BASE = " SELECT "
			+ "acc.prgenteaccreditato prgEnteAcc, az.strdescrizione descrizione , to_char(acc.datinizioval,'dd/MM/yyyy') dataInizio,  to_char(acc.datfineval,'dd/MM/yyyy') dataFine "
			+ " ,acc.strcodicefiscale codicefiscale, acc.codsede  codsede " + " from vch_ente_accreditato acc "
			+ " inner join de_azione az " + "  on acc.prgazioni =az.prgazioni" + "   WHERE                   ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		_logger.debug("Sono dentro l'azione della ricerca per lista per dei dettagli della misura");

		// Modifca per impostazioni per errore

		StringBuffer buf = new StringBuffer(SELECT_SQL_BASE);

		SourceBean serviceReq = requestContainer.getServiceRequest();

		String cfSel = (String) serviceReq.getAttribute("CODICEFISCALE");
		String codComuneSel = (String) serviceReq.getAttribute("CODSEDE");

		// Dopo la where non serve l and
		// La richiamo dall update
		if (serviceReq.getAttribute("AF_MODULE_NAME").equals("M_ListaMisureSedeSoggetto")
				&& (serviceReq.getAttribute("Page").equals("UpdateAccreditamentoPage")
						|| serviceReq.getAttribute("Page").equals("InsertAccreditamentoPage")
						|| serviceReq.getAttribute("Page").equals("MakeSoggettoAccreditatoPage"))) {
			cfSel = (String) serviceReq.getAttribute("cfsel");
			codComuneSel = (String) serviceReq.getAttribute("codSedeSel");
		}

		if (cfSel != null && !cfSel.equals("")) {
			buf.append(" acc.strcodicefiscale = '" + cfSel + "'");
		}
		// per la seconda condizione si
		if (codComuneSel != null && !codComuneSel.equals("")) {
			buf.append(" AND acc.codsede  ='" + codComuneSel + "'");
		}

		_logger.debug("Query ottenuta per la lista dettaglio ::: " + buf.toString());

		return buf.toString();
	}

}
