package it.eng.sil.util;

import java.io.IOException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.jsp.JspWriter;

import org.apache.log4j.Logger;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;

import it.eng.sil.Values;

/**
 * Classe che implementa la generazione del codice HTML per la visualizzazione del nome dell'utente di inserimento e
 * modifica dell'elemento in questione (lavoratore, conoscenza informatica ecc.), nonchè delle date di inserimento e
 * ultima modifica.
 * 
 * La classe può essere usata nella pagina JSP, normalmente solo in quelle che prevedono un aggiornamento dei dati
 * (quindi con un Salva o un Update).
 * 
 * Esempio:
 * 
 * Inizializza l'oggetto, consiglio di farlo ad inizio JSP Testata testata= new Testata(cdnUtIns, dtmIns, cdnUtMod,
 * dtmMod);
 * 
 * Inserire il codice seguente nel punto in cui si vuole che siano generate le informazioni <% testata.showHTML(out); %>
 * 
 * Risultato HTML: "Inserimento Franco Vuoto - 03/10/2003 Ultima Modifica Franco Vuoto - 03/10/2003"
 * 
 * E' anche possibile estrarre i singoli valori con i metodi getNomeUtenteInserimento() in modo da avere codice come
 * questo:
 * <p>
 * Utente che ha modificato: <%= testata.getNomeCognomeUtenteModifica() %>
 * </p>
 * 
 * @author Corrado Vaccari
 */
public class Testata {

	Object codUtenteInserimento;
	Object codUtenteModifica;

	Object dataInserimento;
	Object dataModifica;

	String utInsNome = "";
	String utInsCognome = "";
	String utModNome = "";
	String utModCognome = "";

	private Logger _logger = Logger.getLogger(Testata.class.getName());

	/**
	 * Costruttore.
	 * 
	 * @param codUtenteInserimento
	 * @param dataInserimento
	 * @param codUtenteModifica
	 * @param dataModifica
	 */
	public Testata(Object codUtenteInserimento, Object dataInserimento, Object codUtenteModifica, Object dataModifica) {

		this.codUtenteInserimento = codUtenteInserimento;
		this.dataInserimento = dataInserimento;
		this.codUtenteModifica = codUtenteModifica;
		this.dataModifica = dataModifica;
	}

	/**
	 * Genera il codice HTML basato sui parametri assegnati all'oggetto.
	 * 
	 * @param out
	 *            Writer della pagina JSP
	 * @throws IOException
	 */
	public void showHTML(JspWriter out) throws IOException {

		StringBuffer sb = new StringBuffer();

		List datiUtenteIns = null;
		List datiUtenteMod = null;

		if (codUtenteInserimento != null)
			datiUtenteIns = getNomeCognomeUtente(codUtenteInserimento);

		if (codUtenteModifica != null)
			datiUtenteMod = getNomeCognomeUtente(codUtenteModifica);

		if ((datiUtenteIns != null) && (datiUtenteIns.size() >= 2)) {

			this.utInsNome = (String) datiUtenteIns.get(0);
			this.utInsCognome = (String) datiUtenteIns.get(1);

			sb.append("<td class=\"info_mod\">Inserimento</td>");
			sb.append("<td class=\"info_mod\"><b>");

			appendUserInfo(sb, utInsNome, utInsCognome, codUtenteInserimento);

			if (this.dataInserimento != null)
				sb.append(" - " + dataInserimento);

			sb.append("</b></td>");
		}

		if ((datiUtenteMod != null) && (datiUtenteMod.size() >= 2)) {

			this.utModNome = (String) datiUtenteMod.get(0);
			this.utModCognome = (String) datiUtenteMod.get(1);

			sb.append("<td class=\"info_mod\">Ultima Modifica</td>");
			sb.append("<td class=\"info_mod\"><b>");

			appendUserInfo(sb, utModNome, utModCognome, codUtenteModifica);

			if (this.dataModifica != null)
				sb.append(" - " + dataModifica);

			sb.append("</b></td>");
		}

		if (sb.length() > 0) {
			sb.insert(0, "<table class=\"info_mod\" align=\"center\"><tr class=\"note\">");
			sb.append("</tr></table>");
		}

		out.println(sb.toString());
	}

	/**
	 * Ritorna in una lista il nome e il cognome dell'utente corrispondente al codice passato.
	 * 
	 * @param codUtente
	 *            Codice dell'utente
	 * @return Lista contenente alla posizione 0 una stringa col nome e alla posizione 1 una stringa col cognome
	 */
	private List getNomeCognomeUtente(Object codUtente) {

		// GG 31/08/2004 - recupera i dati solo se codUtente e' definito (non
		// nullo o vuoto)
		if (codUtente == null) {
			return Collections.EMPTY_LIST;
		}
		if ((codUtente instanceof String) && (((String) codUtente).length() == 0)) {
			return Collections.EMPTY_LIST;
		}

		ArrayList result = new ArrayList();

		DataConnection dataConnection = null;
		SQLCommand sqlCommand = null;
		DataResult dataResult = null;

		try {

			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			String statement = SQLStatements.getStatement("CARICA_NOME_UTENTE");
			sqlCommand = dataConnection.createSelectCommand(statement);

			List inputParameter = new ArrayList();
			inputParameter.add(dataConnection.createDataField("", Types.NUMERIC, codUtente));

			dataResult = sqlCommand.execute(inputParameter);

			ScrollableDataResult scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject();
			SourceBean sb = scrollableDataResult.getSourceBean();

			List righe = sb.getAttributeAsVector("ROW");
			if (righe.size() == 1) {

				SourceBean riga = (SourceBean) righe.get(0);

				result.add(riga.getAttribute("NOME"));
				result.add(riga.getAttribute("COGNOME"));
			} else {

				_logger.warn("The user with code [" + codUtente + "] is not found in the database");
				// LogUtils.logError("getNomeCognomeUtente", , this);
			}
		} catch (com.engiweb.framework.error.EMFInternalError ex) {
			_logger.error("Internal Error", ex);
		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(dataConnection, sqlCommand, dataResult);
		}

		return result;
	}

	/**
	 * Fornisce una rappresentazione delle proprietà dell'oggetto; utile in fase di debug.
	 */
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append(this.getClass());
		sb.append(" CodUtIns [");
		sb.append(this.codUtenteInserimento);
		sb.append("], CodUtMod [");
		sb.append(this.codUtenteModifica);
		sb.append("], DataIns [");
		sb.append(this.dataInserimento);
		sb.append("], DataMod [");
		sb.append(this.dataModifica);
		sb.append("]}");

		return sb.toString();
	}

	/**
	 * 
	 */
	public String getNomeUtenteInserimento() {
		return this.utInsNome;
	}

	/**
	 * 
	 */
	public String getCognomeUtenteInserimento() {
		return this.utInsCognome;
	}

	/**
	 * 
	 */
	public String getNomeUtenteModifica() {
		return this.utModNome;
	}

	/**
	 * 
	 */
	public String getCognomeUtenteModifica() {
		return this.utModCognome;
	}

	/**
	 * 
	 */
	public String getNomeCognomeUtenteInserimento() {
		return this.utInsNome + " " + this.utInsCognome;
	}

	/**
	 * 
	 */
	public String getNomeCognomeUtenteModifica() {
		return this.utModNome + " " + this.utModCognome;
	}

	/**
	 * Aggiunge le info utente allo string buffer.
	 * 
	 * @param sb
	 * @param utNome
	 * @param utCognome
	 * @param userCode
	 */
	private void appendUserInfo(StringBuffer sb, String utNome, String utCognome, Object userCode) {

		String userInfo = utCognome + " " + utNome;
		sb.append(userInfo);
	}
}