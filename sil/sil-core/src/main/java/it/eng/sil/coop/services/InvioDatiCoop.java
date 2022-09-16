package it.eng.sil.coop.services;

import java.io.OutputStream;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.sil.coop.CoopApplicationException_Lavoratore;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.bean.XMLCoopMessage;
import it.eng.sil.coop.utils.CoopMessageCodes;
import it.eng.sil.coop.utils.QueryExecutor;
import it.eng.sil.util.Utils;
import oracle.sql.BLOB;

/**
 * @author savino
 */
public class InvioDatiCoop implements IFaceService {

	private static final Logger log = Logger.getLogger(InvioDatiCoop.class.getName());

	private String XMLStringMessage;

	private String msgEvidenza;

	private String codStatoOccupaz;

	private String codCpiProvenienza;

	private String desCpiProvenienza;

	private String dataNascita;

	private String cognome;

	private String nome;

	private String numMesiSosp;

	private String numMesiAnzianita;

	private String dataAnzianitaDisocc;

	private String codiceFiscale;

	private String cdnLavoratore;

	private String desProvinciaPoloLocale;

	private String dataTrasferimento;

	private byte[] data;

	private boolean lavoratorePresente;

	// 28/5/2007 aggiunte info sul collocamento mirato
	private String codCMTipoIscr_1, codCMTipoIscr_2;
	private String dataIscrCM_1, dataIscrCM_2;
	private String dataAnzLista68_1, dataAnzLista68_2;

	private Connection dataConnection;
	private String dataSourceJNDI;
	private String stato;

	public void send(javax.jms.Message msg) throws CoopApplicationException_Lavoratore, JMSException {

		ObjectMessage message = (ObjectMessage) msg;
		Serializable arrObj = message.getObject();
		List in = (ArrayList) arrObj;

		data = (byte[]) in.get(0);
		XMLStringMessage = (String) in.get(1);
		XMLCoopMessage coopMessage = null;
		try {
			coopMessage = new XMLCoopMessage(XMLStringMessage);

			nome = coopMessage.getDati("nome");
			cognome = coopMessage.getDati("cognome");
			dataNascita = coopMessage.getDati("datanascita");
			codiceFiscale = coopMessage.getDati("codicefiscale");
			codStatoOccupaz = coopMessage.getDati("codstatooccupaz");
			dataAnzianitaDisocc = coopMessage.getDati("datanzianitadisoc");
			numMesiAnzianita = coopMessage.getDati("nummesianzianita");
			numMesiSosp = coopMessage.getDati("nummesisosp");
			dataTrasferimento = coopMessage.getDati("datatrasf");
			codCpiProvenienza = (String) coopMessage.getAttribute("provenienza.codiceCPI");
			desCpiProvenienza = (String) coopMessage.getAttribute("provenienza.descrizione");
			msgEvidenza = (String) coopMessage.getAttribute("messaggio.testo");

			// 28/5/2007 dati iscrizione collocamento mirato
			this.codCMTipoIscr_1 = Utils.notNull(coopMessage.getDati("codCMTipoIscr_1"));
			this.codCMTipoIscr_2 = Utils.notNull(coopMessage.getDati("codCMTipoIscr_2"));
			this.dataAnzLista68_1 = Utils.notNull(coopMessage.getDati("dataAnzLista68_1"));
			this.dataAnzLista68_2 = Utils.notNull(coopMessage.getDati("dataAnzLista68_2"));
			this.dataIscrCM_1 = Utils.notNull(coopMessage.getDati("dataIscrCM_1"));
			this.dataIscrCM_2 = Utils.notNull(coopMessage.getDati("dataIscrCM_2"));

			leggiInfoPoloLocale();
			check();

			dataConnection = getConnection();
			dataConnection.setAutoCommit(false);

			if (!lavoratorePresente()) {
				log.info(
						"Registrazione dei dati inviati dal servizio di presa atto: il lavoratore non e' presente nell'archivio del polo: "
								+ "codiceFiscale=" + codiceFiscale);
				// prosegue comunque
				// throw new Exception("Impossibile registrare i dati inviati dal servizio di presa atto: il lavoratore
				// non e' presente nell'archivio del polo");
			}
			inserisciInformazioni();
			if (lavoratorePresente)
				inserisciEvidenza(msgEvidenza, codiceFiscale);
			dataConnection.commit();

		} catch (Exception e) {
			log.error("errore in invio dati presa atto in cooperazione. " + this, e);
			if (dataConnection != null)
				try {
					dataConnection.rollback();
				} catch (SQLException e1) {
					log.error("Invio dati da presa atto: impossibile eseguire la rollback", e1);
				}
			// invio la notifica al mittente che l'operazione e' fallita

			// String codCpiLocale="", descCpiLocale="";
			// Savino: reperire codCpiLocale e descCpiLocale da utilizzare nella notifica di errore? per ora no
			Vector param = new Vector(5);
			param.add(desProvinciaPoloLocale);
			param.add(cognome);
			param.add(nome);
			param.add(codiceFiscale);
			param.add(codCpiProvenienza + " : " + desCpiProvenienza);
			// param.add(codCpiLocale + " : " + descCpiLocale);
			throw new CoopApplicationException_Lavoratore(CoopMessageCodes.NOTIFICHE_COOP.INVIO_DATI_FAIL, param,
					codiceFiscale);
		} finally {
			// in this.data c'e' un pdf che potrebbe essere bello grande; meglio dare una mano allo spazzino!
			if (dataConnection != null) {
				try {
					dataConnection.close();
				} catch (SQLException e1) {
					log.error("Invio dati da presa atto: impossibile chiudere la connessione al db", e1);
				}
			}
			release();
			message = null;
			arrObj = null;
			in = null;
			coopMessage = null;
		}
	}

	/**
	 * 
	 */
	private void check() throws Exception {
		if (isEmpty(nome) || isEmpty(cognome) || isEmpty(dataNascita) || isEmpty(codiceFiscale)
				|| isEmpty(codCpiProvenienza) || isEmpty(msgEvidenza)) {

			log.debug(this);
			throw new Exception(
					"Invio dati a seguito richiesta presa atto: almeno un dato richiesto e' mancante: " + this);
		}

	}

	private boolean isEmpty(String s) {
		return s == null || "".equals(s);
	}

	private void leggiInfoPoloLocale() throws Exception {
		String sql = "select codcpicapoluogo, strdenominazione from ts_generale, de_provincia where codprovinciasil = codprovincia";
		Connection c = null;
		try {
			// c = new EjbDbConnection().getConnection();
			// TODO Savino: NEW DB_ACCESS
			c = DataConnectionManager.getInstance().getConnection().getInternalConnection();
			QueryExecutor qe = new QueryExecutor(c);
			SourceBean row = qe.executeQuery(sql, null);
			desProvinciaPoloLocale = (String) row.getAttribute("row.strdenominazione");
		} finally {
			if (c != null)
				c.close();
		}

	}

	/**
	 * 
	 */
	private void release() {
		this.cdnLavoratore = null;
		this.codCpiProvenienza = null;
		this.codiceFiscale = null;
		this.codStatoOccupaz = null;
		this.cognome = null;
		this.data = null;
		this.dataAnzianitaDisocc = null;
		this.dataConnection = null;
		this.dataNascita = null;
		this.dataSourceJNDI = null;
		this.msgEvidenza = null;
		this.nome = null;
		this.numMesiAnzianita = null;
		this.numMesiSosp = null;
		this.XMLStringMessage = null;
		this.dataTrasferimento = null;
	}

	/**
	 * 
	 */
	private void inserisciInformazioni() throws Exception {
		String sqlPrgCaInfoTrasf = "select to_char(s_ca_info_trasferimento.nextval) as nextval from dual";
		String prg = null;
		QueryExecutor qe = new QueryExecutor(dataConnection);
		SourceBean row = qe.executeQuery(sqlPrgCaInfoTrasf, null);
		prg = (String) row.getAttribute("row.nextval");
		String sqlIns = new StringBuffer().append("insert into ca_info_trasferimento ").append("(")
				.append(" prginfotrasferimento, cdnlavoratore, strnome, strcognome, ")
				.append(" datnasc, strcodicefiscale,codcpimitt, ")
				.append(" codstatooccupaz, datanzianitadisoc, nummesianzianita, nummesisosp, ").append(" blbfile, ")
				.append(" dtmins, cdnutins, dtmmod, cdnutmod,").append(" dattrasferimento, ")
				.append(" datdatainizio68_1, datanzianita68_1, codcmtipoiscr68_1,")
				.append(" datdatainizio68_2, datanzianita68_2, codcmtipoiscr68_2").append(") values (")
				.append(" ?,to_number(?),?,?,").append(" to_date(?, 'dd/mm/yyyy'),?,?,")
				.append(" ?, to_date(?, 'dd/mm/yyyy'),to_number(?),to_number(?), ").append(" empty_blob(),")
				.append(" sysdate, 190, sysdate, 190,").append(" to_date(?, 'dd/mm/yyyy') ,")
				.append(" to_date(?, 'dd/mm/yyyy') ,").append(" to_date(?, 'dd/mm/yyyy'),").append(" ?,")
				.append(" to_date(?, 'dd/mm/yyyy') ,").append(" to_date(?, 'dd/mm/yyyy'),").append(" ? ").append(")")
				.toString();
		// normalizzo alcuni dati
		String _dataAnzianitaDisocc = "".equals(dataAnzianitaDisocc) ? null : dataAnzianitaDisocc;
		String _numMesiAnzianita = "".equals(numMesiAnzianita) || "0".equals(numMesiAnzianita) ? null
				: numMesiAnzianita;
		String _numMesiSosp = "".equals(numMesiSosp) || "0".equals(numMesiSosp) ? null : numMesiSosp;
		// 28/5/2007 collocamento mirato
		String _dataIscrCM_1 = "".equals(this.dataIscrCM_1) ? null : this.dataIscrCM_1;
		String _dataIscrCM_2 = "".equals(this.dataIscrCM_2) ? null : this.dataIscrCM_2;
		String _dataAnzLista68_1 = "".equals(this.dataAnzLista68_1) ? null : this.dataAnzLista68_1;
		String _dataAnzLista68_2 = "".equals(this.dataAnzLista68_2) ? null : this.dataAnzLista68_2;
		String params[] = new String[] { prg, cdnLavoratore, nome, cognome, dataNascita, codiceFiscale,
				codCpiProvenienza, codStatoOccupaz, _dataAnzianitaDisocc, _numMesiAnzianita, _numMesiSosp,
				dataTrasferimento, _dataIscrCM_1, _dataAnzLista68_1, codCMTipoIscr_1, _dataIscrCM_2, _dataAnzLista68_2,
				codCMTipoIscr_2 };

		qe.executeUpdate(sqlIns, params);
		// inserimento pdf
		String sqlQuery = "SELECT BLBFILE FROM ca_info_trasferimento WHERE prginfotrasferimento = " + prg
				+ " FOR UPDATE ";
		Statement stm = null;
		ResultSet rs = null;
		BLOB blob = null;
		OutputStream outStream = null;
		try {
			stm = dataConnection.createStatement();
			rs = stm.executeQuery(sqlQuery);
			if (rs.next()) {
				blob = (BLOB) rs.getObject("blbfile");
				outStream = blob.getBinaryOutputStream();
				int chunk = blob.getChunkSize();
				byte[] buffer = new byte[chunk];
				int len = data.length;
				for (int pos = 0; pos < len; pos += chunk) {
					int nBytes = (pos + chunk) < len ? chunk : (len - pos);
					outStream.write(data, pos, nBytes);
				}
				outStream.flush();
			}
		} finally {
			if (rs != null)
				rs.close();
			if (stm != null)
				stm.close();
			if (outStream != null)
				outStream.close();
			rs = null;
			blob = null;
			stm = null;
			outStream = null;
		}
	}

	private void inserisciEvidenza(String messaggio, String codiceFiscale) throws SQLException {
		CallableStatement command = null;
		try {
			String statement = "{ call ?:=PG_COOP.putNotificaLavoratore(?,?,?,?) }";
			command = dataConnection.prepareCall(statement);
			command.registerOutParameter(1, Types.TINYINT);
			command.setString(2, codiceFiscale);
			command.setString(3, messaggio);
			command.setString(4, "190");
			command.setString(5, "01/01/2100");
			command.execute();
			// se si verifica un errore viene lanciata una eccezione
			// la funzione ritorna sempre 0!!
			// codiceRit=command.getInt(1);
		} finally {
			if (command != null)
				command.close();
		}
	}

	private boolean lavoratorePresente() throws Exception {
		QueryExecutor qExec = new QueryExecutor(dataConnection);
		String query = "select to_char(cdnlavoratore) as cdnLavoratore " + "  from an_lavoratore "
				+ " where strcodicefiscale = upper(?)";
		String[] params = new String[] { codiceFiscale };
		SourceBean row = qExec.executeQuery(query, params);
		if (row != null && row.getAttribute("row.cdnLavoratore") != null) {
			this.lavoratorePresente = true;
			cdnLavoratore = (String) row.getAttribute("row.cdnLavoratore");
		} else
			this.lavoratorePresente = false;
		return this.lavoratorePresente;
	}

	public String toString() {
		return new StringBuffer("Invio dati in cooperazione:: ").append("codice fiscale").append(codiceFiscale)
				.append("nome=").append(nome).append(",cognome=").append(cognome).append(",dataNascita=")
				.append(dataNascita).append(",codStatoOccupaz=").append(codStatoOccupaz).append(",codCpiProvenienza=")
				.append(codCpiProvenienza).append(",data trasferimento=").append(dataTrasferimento).append(",evidenza=")
				.append(msgEvidenza).append(",data.length=").append(data.length).append(", \r\n XMLStringMessage=")
				.append(XMLStringMessage).toString();
	}

	public Connection getConnection() throws EMFInternalError {
		// EjbDbConnection ejbDbConnection=new EjbDbConnection();
		// dataSourceJNDI=ejbDbConnection.getDataSourceJndi();
		// TODO Savino: NEW DB_ACCESS
		dataSourceJNDI = new DataSourceJNDI().getJndi();
		return DataConnectionManager.getInstance().getConnection().getInternalConnection();

		// return ejbDbConnection.getConnection();
	}

	public String getStato() {
		return stato;
	}

}
