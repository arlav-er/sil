package it.eng.sil.module.movimenti;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.sql.DataConnection;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;

/**
 * Esegue la selezione degli elementi della combo tipo assunzione in base alla tipologia dell'azienda del movimento,
 * alla sua natura giuridica e al tipo di contratto scelto. Distingue i casi di salvataggio, validazione e inserimento
 * del movimento
 */
public class ComboAvviamentoSelettiva extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(ComboAvviamentoSelettiva.class.getName());
	private String className = this.getClass().getName();

	public ComboAvviamentoSelettiva() {
	}

	public void service(SourceBean request, SourceBean response) throws SourceBeanException {
		// Se necessario mi procuro il codice del tipo di azienda e la natura
		// giuridica
		// e li inserisco nella request
		String context = (String) request.getAttribute("CURRENTCONTEXT");
		String selectstat = null;
		String prgAzienda = StringUtils.getAttributeStrNotNull(request, "PRGAZIENDA");
		String prgMovimento = StringUtils.getAttributeStrNotNull(request, "PRGMOVIMENTO");
		if (context.equalsIgnoreCase("inserisci") && !prgAzienda.equals("")) {
			// Devo estrarre il codice dalla tabella delle aziende
			selectstat = "SELECT codTipoAzienda, codNatGiuridica FROM AN_AZIENDA WHERE prgAzienda = " + prgAzienda;
		} else if (context.equalsIgnoreCase("salva") && !prgMovimento.equals("")) {
			// devo estrarre il codice a partire dal prgmovimento
			selectstat = "SELECT AZ.codTipoAzienda, AZ.codNatGiuridica FROM AN_AZIENDA AZ, AM_MOVIMENTO MOV WHERE MOV.prgAzienda = AZ.prgAzienda AND MOV.prgMovimento = "
					+ prgMovimento;
		}

		String codAzienda = "AZI";
		String codNatGiur = null;

		String pool = (String) getConfig().getAttribute("POOL");
		// Apro una connessione verso il DB
		DataConnection dc = null;
		Connection conn = null;
		try {
			try {
				DataConnectionManager dcm = DataConnectionManager.getInstance();
				dc = dcm.getConnection(pool);
				conn = dc.getInternalConnection();
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile ottenere una connessione dal pool", e);

			}

			ResultSet rs = null;
			Statement stmt = null;
			// Eseguo la query di ricerca del tipo azienda

			stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(selectstat);

			// Se ho un ResultSet provo a vedere se contiene il progressivo
			if (rs != null) {
				if (rs.next()) {
					codAzienda = rs.getString(1);
					codNatGiur = rs.getString(2);
				}
				rs.close();
			}
			stmt.close();
		} catch (Exception e) {
			_logger.debug(
					"[" + this.getClass().getName() + "] Eccezione nella ricerca del progressivo: " + e.toString());

		} finally {
			// chiudo la connessione verso il DB
			if (dc != null) {
				try {
					dc.close();
				} catch (Exception e) {
					it.eng.sil.util.TraceWrapper.debug(_logger,
							"Impossibile chiudere la connessione del pool " + (String) getConfig().getAttribute("POOL"),
							e);

				}
			}
		}

		// Inserisco il risultato nella request
		if (codAzienda != null && !codAzienda.equals("")) {
			request.setAttribute("CODTIPOAZIENDA", codAzienda);
		}
		if (codNatGiur != null && !codNatGiur.equals("")) {
			request.setAttribute("CODNATGIURIDICA", codNatGiur);
		}

		// Eseguo la query di ricerca dinamica per riempire la combo
		// e infilo il risultato nella response
		response.setAttribute(doDynamicSelect(request, response));
	}
}