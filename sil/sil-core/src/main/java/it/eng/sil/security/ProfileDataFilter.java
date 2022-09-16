package it.eng.sil.security;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.tags.Util;

import it.eng.sil.Values;

/**
 * @author Franco Vuoto Classe utilizzata per caricare gli attributi di una pagina in relazione al profilo dell'utente
 *         che ha acceduto al sistema. Per attribbuto si intende un bottone (SALVA, NUOVO, STAMPA) o un collegamento
 *         ipertestuale.
 */
public class ProfileDataFilter {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ProfileDataFilter.class.getName());
	private String page;
	private User user = null;

	protected DataConnection dataConnection = null;
	private SQLCommand sqlCommand = null;
	private DataResult dataResult = null;

	private final static String LIST = "L";
	private final static String READ = "R";
	private final static String WRITE = "W";

	private final static char LAVORATORE = 'L';
	private final static char AZIENDA_UNITA = 'U';
	private final static char BLANK_CHAR = ' ';

	private final static char CTRL_COMPONENTE = 'C';

	// segnaposto per le liste, in cui non serve filtrare per entità

	private BigDecimal cdnLavoratore = null;
	private BigDecimal prgAzienda = null;
	private BigDecimal prgUnita = null;
	protected boolean isTransactional = false;

	/**
	 * Unico costruttore.
	 * 
	 * @param user
	 *            utente connesso al sistema
	 * @param _page
	 *            nome della page da verificare
	 */
	public ProfileDataFilter(User user, String _page) {
		this.page = _page.toUpperCase();
		this.user = user;

	}

	/**
	 * Non ammesso
	 */
	private ProfileDataFilter() {
	}

	/**
	 * Fornisce il dump dell'oggetto, utile in fase di debug
	 * 
	 * @return Dump dell'oggetto
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{ User [");
		sb.append(this.user.toString());
		sb.append("] Page [");
		sb.append(this.page);
		sb.append("] }");

		return sb.toString();
	}

	/**
	 * Description of the Method
	 */
	private String getSQL(String tipo, char entita) throws EMFInternalError {

		String retVal = null;
		List inputParameter = null;

		if (entita != BLANK_CHAR) {
			String statement = SQLStatements.getStatement("CARICA_FILTRO_SQL");
			sqlCommand = dataConnection.createSelectCommand(statement);
			inputParameter = new ArrayList();
			inputParameter
					.add(dataConnection.createDataField("", Types.NUMERIC, Integer.valueOf(this.user.getCdnGruppo())));

			inputParameter.add(dataConnection.createDataField("", Types.VARCHAR, this.page));
			inputParameter.add(dataConnection.createDataField("", Types.VARCHAR, tipo));
			inputParameter.add(dataConnection.createDataField("", Types.VARCHAR, new Character(entita).toString()));

		} else {
			String statement = SQLStatements.getStatement("CARICA_WHERE_SQL");
			sqlCommand = dataConnection.createSelectCommand(statement);

			inputParameter = new ArrayList();

			inputParameter
					.add(dataConnection.createDataField("", Types.NUMERIC, Integer.valueOf(this.user.getCdnGruppo())));

			inputParameter.add(dataConnection.createDataField("", Types.VARCHAR, this.page));
			inputParameter.add(dataConnection.createDataField("", Types.VARCHAR, tipo));
		}

		dataResult = sqlCommand.execute(inputParameter);

		ScrollableDataResult scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject();
		SourceBean sb = scrollableDataResult.getSourceBean();

		Utils.releaseResources(null, sqlCommand, dataResult);
		retVal = (String) sb.getAttribute("row.STRSQL");

		_logger.debug(this.getClass().getName() + " ::Filtro di lista non decodificato = [" + retVal + "]");

		retVal = rimpiazzaMarcatori(retVal);

		return retVal;

	}

	public String getSqlDiLista() {
		String retVal = null;
		try {
			open();
			retVal = getSQL(LIST, BLANK_CHAR);

		} catch (EMFInternalError ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "getSqlDiLista", (Exception) ex);

		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(dataConnection, sqlCommand, dataResult);
		}
		return retVal;

	}

	private String rimpiazzaMarcatori(String sql) {

		if (sql == null)
			return null;

		sql = Util.replace(sql, "<USER.CDNGRUPPO>", String.valueOf(user.getCdnGruppo()));
		sql = Util.replace(sql, "<USER.CDNTIPOGRUPPO>", String.valueOf(user.getCdnTipoGruppo()));
		sql = Util.replace(sql, "<USER.CODRIF>", user.getCodRif());
		sql = Util.replace(sql, "<USER.CDNPROFILO>", String.valueOf(user.getCdnProfilo()));
		sql = Util.replace(sql, "<USER.CDNUTENTE>", String.valueOf(user.getCodut()));

		return sql;

	}

	/**
	 * @return
	 */
	public BigDecimal getCdnLavoratore() {
		return cdnLavoratore;
	}

	/**
	 * @param decimal
	 */
	public void setCdnLavoratore(BigDecimal decimal) {
		cdnLavoratore = decimal;
	}

	public boolean canViewLavoratore() {
		return process(READ, LAVORATORE);

	}

	public boolean canEditLavoratore() {
		return process(WRITE, LAVORATORE);

	}

	public boolean canViewUnitaAzienda() {
		return process(READ, AZIENDA_UNITA);

	}

	public boolean canEditUnitaAzienda() {
		return process(WRITE, AZIENDA_UNITA);

	}

	public boolean canView() {
		return process(WRITE, CTRL_COMPONENTE);

	}

	private boolean process(String tipo, char entita) {

		boolean retVal = true;
		try {
			if (!isTransactional)
				open();

			if (!hasComponent())
				return false;

			if (entita == CTRL_COMPONENTE) {
				return true;
			}

			String statement = getSQL(tipo, entita);

			if ((statement != null) && (!statement.equals(""))) {

				sqlCommand = dataConnection.createSelectCommand(statement);
				List inputParameter = new ArrayList();

				switch (entita) {
				case LAVORATORE:

					if (this.cdnLavoratore == null)
						throw new Exception("process:: cdnLavoratore Null");
					inputParameter.add(dataConnection.createDataField("", Types.NUMERIC, this.cdnLavoratore));
					dataResult = sqlCommand.execute(inputParameter);

					break;

				case AZIENDA_UNITA:
					if (this.prgAzienda == null)
						throw new Exception("process:: prgAzienda Null");

					if (this.prgUnita == null)
						throw new Exception("process:: prgUnita Null");

					inputParameter.add(dataConnection.createDataField("", Types.NUMERIC, this.prgAzienda));
					inputParameter.add(dataConnection.createDataField("", Types.NUMERIC, this.prgUnita));
					dataResult = sqlCommand.execute(inputParameter);

					break;

				default:

					throw new Exception("process:: switch non valido");
				}

				ScrollableDataResult scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject();
				SourceBean sb = scrollableDataResult.getSourceBean();

				if (sb.getAttributeAsVector("row").size() != 0) {
					retVal = true;
				} else {
					retVal = false;
				}
			}
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "process::", (Exception) ex);

		} finally {
			if (!isTransactional)
				com.engiweb.framework.dbaccess.Utils.releaseResources(dataConnection, sqlCommand, dataResult);
			else
				com.engiweb.framework.dbaccess.Utils.releaseResources(null, sqlCommand, dataResult);
		}

		return retVal;

	}

	private void open() throws EMFInternalError {

		DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
		dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

	}

	/**
	 * @return
	 */
	public BigDecimal getPrgAzienda() {
		return prgAzienda;
	}

	/**
	 * @return
	 */
	public BigDecimal getPrgUnita() {
		return prgUnita;
	}

	/**
	 * @param decimal
	 */
	public void setPrgAzienda(BigDecimal decimal) {
		prgAzienda = decimal;
	}

	/**
	 * @param decimal
	 */
	public void setPrgUnita(BigDecimal decimal) {
		prgUnita = decimal;
	}

	private boolean hasComponent() throws EMFInternalError {

		boolean retVal;
		List inputParameter = null;

		String statement = SQLStatements.getStatement("CONTROLLA_ABIL_COMP");
		sqlCommand = dataConnection.createSelectCommand(statement);

		inputParameter = new ArrayList();

		inputParameter.add(dataConnection.createDataField("", Types.VARCHAR, this.page));

		inputParameter.add(dataConnection.createDataField("", Types.NUMERIC, Integer.valueOf(user.getCdnProfilo())));

		dataResult = sqlCommand.execute(inputParameter);

		ScrollableDataResult scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject();
		SourceBean sb = scrollableDataResult.getSourceBean();

		Utils.releaseResources(null, sqlCommand, dataResult);

		_logger.debug(sb.toXML(false));

		retVal = sb.containsAttribute("row.cdncomponente");

		return retVal;

	}

}