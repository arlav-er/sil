package it.eng.sil.module.movimenti;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.sql.DataConnection;

import it.eng.sil.Values;

/**
 * Recupera da DB le informazioni su un lavoratore a partire dal cdnLavoratore
 * <p>
 * 
 * @author Paolo Roccetti
 */
public class InfoLavoratore {

	private String cognome = "";
	private String nome = "";
	private String codFiscale = "";
	private String datNascita = "";
	private String strFlgCfOk = "";
	private String strSesso = "";
	private String codCpiLav = "";
	private String descrStatoOcc = "";
	private String datInizioOcc = "";
	private String datAnzOcc = "";
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InfoLavoratore.class.getName());

	public InfoLavoratore(BigDecimal cdnLavoratore) {
		if (cdnLavoratore != null) {
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
			String selectstat = "SELECT LAV.STRCODICEFISCALE STRCODICEFISCALE, LAV.STRCOGNOME STRCOGNOME, "
					+ " LAV.STRNOME STRNOME, TO_CHAR(LAV.DATNASC, 'DD/MM/YYYY') DATNASC, LAV.FLGCFOK FLGCFOK, "
					+ " LAV.STRSESSO STRSESSO, decode(ST.CODMONOTIPOCPI,'T',ST.CODCPIORIG, ST.CODCPITIT) CODCPILAV,"
					+ " decode (DE_STATO_OCCUPAZ_RAGG.codstatooccupazragg, DE_STATO_OCCUPAZ.codstatooccupaz, "
					+ " DE_STATO_OCCUPAZ_RAGG.strdescrizione, "
					+ " (DE_STATO_OCCUPAZ_RAGG.strdescrizione || ': ' || DE_STATO_OCCUPAZ.strdescrizione)) AS descrStatoOcc,"
					+ " TO_CHAR (OCC.DATINIZIO, 'DD/MM/YYYY') datInizioOcc,"
					+ " TO_CHAR (OCC.DATANZIANITADISOC, 'DD/MM/YYYY') datAnzOcc "
					+ " FROM AN_LAVORATORE LAV, AN_LAV_STORIA_INF ST,"
					+ " AM_STATO_OCCUPAZ OCC, DE_STATO_OCCUPAZ, DE_STATO_OCCUPAZ_RAGG "
					+ " WHERE LAV.CDNLAVORATORE = ST.CDNLAVORATORE " + " AND ST.DATFINE IS NULL "
					+ " AND OCC.CDNLAVORATORE (+) = st.CDNLAVORATORE AND OCC.DATFINE IS NULL "
					+ " AND OCC.CODSTATOOCCUPAZ = DE_STATO_OCCUPAZ.CODSTATOOCCUPAZ (+) "
					+ " AND de_stato_occupaz.codstatooccupazragg = de_stato_occupaz_ragg.codstatooccupazragg (+)"
					+ " AND LAV.CDNLAVORATORE = " + cdnLavoratore;

			ResultSet rs = null;
			Statement stmt = null;

			// Eseguo la query di ricerca dei dati
			try {
				stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				rs = stmt.executeQuery(selectstat);

				// Se ho un ResultSet provo a vedere se contiene i dati
				if (rs != null) {
					if (rs.next()) {
						codFiscale = rs.getString(1);
						cognome = rs.getString(2);
						nome = rs.getString(3);
						datNascita = rs.getString(4);
						strFlgCfOk = rs.getString(5);
						strSesso = rs.getString(6);
						codCpiLav = rs.getString(7);
						descrStatoOcc = rs.getString(8);
						datInizioOcc = rs.getString(9);
						datAnzOcc = rs.getString(10);
					}
					rs.close();
				}
				stmt.close();
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "Eccezione nella ricerca dei dati del Lavoratore", e);
			}

			// chiudo la connessione verso il DB
			if (dc != null) {
				try {
					dc.close();
				} catch (Exception e) {
					it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile chiudere la connessione del pool", e);
				}
			}
		}
	}

	/**
	 * Metodi per prelevare i dati
	 */
	public String getNome() {
		return nome;
	}

	public String getCognome() {
		return cognome;
	}

	public String getCodFisc() {
		return codFiscale;
	}

	public String getDataNasc() {
		return datNascita;
	}

	public String getFlgCfOk() {
		return strFlgCfOk;
	}

	public String getSesso() {
		return strSesso;
	}

	public String getCodCpiLav() {
		return codCpiLav;
	}

	public String getStatoOcc() {
		return descrStatoOcc;
	}

	public String getDatInizioOcc() {
		return datInizioOcc;
	}

	public String getDatAnzOcc() {
		return datAnzOcc;
	}

}