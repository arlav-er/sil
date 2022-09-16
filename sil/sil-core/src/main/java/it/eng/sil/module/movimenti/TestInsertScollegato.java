package it.eng.sil.module.movimenti;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.sql.DataConnection;

import it.eng.sil.Values;

/**
 * Testa se è possibile inserire un movimento che non sia di assunzione senza alcun movimento precedente
 * <p>
 * 
 * @author Paolo Roccetti
 */
public class TestInsertScollegato {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(TestInsertScollegato.class.getName());

	public TestInsertScollegato() {
	}

	public boolean test(String cdnLavoratore) {
		boolean insScollegato = false;

		if (cdnLavoratore != null && !cdnLavoratore.equals("")) {
			// Apro una connessione verso il DB
			DataConnection dc = null;
			Connection conn = null;
			try {
				DataConnectionManager dcm = DataConnectionManager.getInstance();
				dc = dcm.getConnection(Values.DB_SIL_DATI);
				conn = dc.getInternalConnection();
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile ottenere una connessione dal pool", e);

			}

			// Creo il testo della query
			String selectstat = "SELECT COUNT(*) FROM AM_DICH_DISPONIBILITA DISP, AM_ELENCO_ANAGRAFICO ELAN"
					+ " WHERE ELAN.CDNLAVORATORE = " + cdnLavoratore
					+ " AND ELAN.PRGELENCOANAGRAFICO = DISP.PRGELENCOANAGRAFICO " + " AND DISP.DATFINE IS NULL ";

			ResultSet rs = null;
			Statement stmt = null;

			// Eseguo la query di ricerca dei dati
			try {
				stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				rs = stmt.executeQuery(selectstat);

				// Se ho un ResultSet provo a vedere se contiene il risultato
				if (rs != null) {
					if (rs.next()) {
						BigDecimal count = rs.getBigDecimal(1);
						// Controllo il numero di righe con null, se == 0
						// l'inserimento scollegato è possibile
						if (count.compareTo(new BigDecimal(0)) == 0) {
							insScollegato = true;
						}
					}
					rs.close();
				}
				stmt.close();
			} catch (Exception e) {
				_logger.debug("[" + this.getClass().getName() + "] Eccezione nella ricerca dei dati: " + e.toString());

			}

			// chiudo la connessione verso il DB
			if (dc != null) {
				try {
					dc.close();
				} catch (Exception e) {
					it.eng.sil.util.TraceWrapper.debug(_logger,
							"Impossibile chiudere la connessione del pool " + Values.DB_SIL_DATI, e);

				}
			}
		}
		return insScollegato;
	}
}