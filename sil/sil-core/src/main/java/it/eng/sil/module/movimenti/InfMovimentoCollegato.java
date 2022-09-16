package it.eng.sil.module.movimenti;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.sql.DataConnection;

import it.eng.sil.Values;

/**
 * Recupera da DB le informazioni su un movimento a partire dal prgMovimento passato al costruttore
 * <p>
 * 
 * @author Paolo Roccetti
 */
public class InfMovimentoCollegato {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InfMovimentoCollegato.class.getName());
	// Codice tipi movimento
	private static final String CODEAVVIAMENTO = "AVV";
	private static final String CODETRASFORMAZIONE = "TRA";
	private static final String CODEPROROGA = "PRO";
	private static final String CODECESSAZIONE = "CES";

	// Page dettaglio tipi movimento
	private static final String PAGEAVVIAMENTO = "MovDettaglioAvviamentoPage";
	private static final String PAGETRASFPRO = "MovDettaglioTrasfProPage";
	private static final String PAGECESSAZIONE = "MovDettaglioCessazionePage";

	private boolean exists = false;
	private String page = null;
	private String numKloMov = null;
	private String codTipoMov = null;
	private String datFineMov = null;
	private String datFineMovIniziale = null;

	// Genera le informazioni, se ha problemi exists rimane a false
	public InfMovimentoCollegato(String prgMovimento) {
		if (prgMovimento != null && !prgMovimento.equals("")) {
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
			String selectstat = "SELECT CODTIPOMOV, TO_CHAR(NUMKLOMOV), TO_CHAR(DATFINEMOVEFFETTIVA, 'DD/MM/YYYY') DATFINEMOV,  TO_CHAR(DATFINEMOV, 'DD/MM/YYYY') DATFINEMOVINIZIALE "
					+ " FROM AM_MOVIMENTO WHERE PRGMOVIMENTO = " + prgMovimento;

			ResultSet rs = null;
			Statement stmt = null;

			// Eseguo la query di ricerca dei dati
			try {
				stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				rs = stmt.executeQuery(selectstat);

				// Se ho un ResultSet provo a vedere se contiene i dati
				if (rs != null) {
					if (rs.next()) {
						codTipoMov = rs.getString(1);
						numKloMov = rs.getString(2);
						datFineMov = rs.getString(3);
						datFineMovIniziale = rs.getString(4);
					}
					rs.close();
				}
				stmt.close();
			} catch (Exception e) {
				_logger.debug("[" + this.getClass().getName() + "] Eccezione nella ricerca dei dati del Lavoratore: "
						+ e.toString());

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

			// Guardo se ho dei valori consistenti
			if (numKloMov != null && !numKloMov.equals("") && codTipoMov != null && !codTipoMov.equals("")) {
				exists = true;
				// Setto la page corrispondente al codice ripescato
				if (codTipoMov.equalsIgnoreCase(CODEAVVIAMENTO)) {
					page = PAGEAVVIAMENTO;
				} else if (codTipoMov.equalsIgnoreCase(CODETRASFORMAZIONE)) {
					page = PAGETRASFPRO;
				} else if (codTipoMov.equalsIgnoreCase(CODEPROROGA)) {
					page = PAGETRASFPRO;
				} else if (codTipoMov.equalsIgnoreCase(CODECESSAZIONE)) {
					page = PAGECESSAZIONE;
				}
				// Se non ho matching con i tipi di movimento conosciuti non
				// mostro nulla.
				else
					exists = false;
			}
		}
	}

	/**
	 * Metodi per prelevare i dati
	 */
	public boolean exists() {
		return exists;
	}

	public String getPage() {
		return page;
	}

	public String getNumKloMov() {
		return numKloMov;
	}

	public String getCodTipoMov() {
		return codTipoMov;
	}

	public String getDatFineMov() {
		return datFineMov;
	}

	public String getDatFineMovIniziale() {
		return datFineMovIniziale;
	}
}