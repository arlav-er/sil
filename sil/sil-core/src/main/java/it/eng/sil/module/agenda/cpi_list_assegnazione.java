package it.eng.sil.module.agenda;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.security.User;

/*
 * Questa classe restituisce la lista delle Assegnazioni
 * relativi ad un CpI
 * 
 * @author: Giovanni Landi
 * 
 */

public class cpi_list_assegnazione implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(cpi_list_assegnazione.class.getName());

	public cpi_list_assegnazione() {
	}

	private static final String SELECT_SQL_BASE = "SELECT " + "AG_ASSEGNAZIONE.CODCPI, "
			+ "AG_ASSEGNAZIONE.PRGASSEGNAZIONE, " + "AG_ASSEGNAZIONE.CODSERVIZIO, " + "AG_ASSEGNAZIONE.PRGSPI, "
			+ "AG_ASSEGNAZIONE.PRGAMBIENTE, " + "AN_SPI.STRCOGNOME, " + "AN_SPI.STRNOME, "
			+ "AN_SPI.STRCOGNOME || ' ' || AN_SPI.STRNOME as OPERATORE, "
			+ "DE_SERVIZIO.STRDESCRIZIONE AS DescrizioneServizio, "
			+ "DE_AMBIENTE.STRDESCRIZIONE AS DescrizioneAmbiente, "
			+ "(select nvl( (select to_char(ts_config_loc.num)  from ts_config_loc  "
			+ " where strcodrif=(select ts_generale.codprovinciasil from ts_generale) and codtipoconfig='UMBNGEAZ') , 0) "
			+ " from dual) choiceLabel " + "FROM AG_ASSEGNAZIONE "
			+ "INNER JOIN AN_SPI ON (AG_ASSEGNAZIONE.PRGSPI=AN_SPI.PRGSPI) "
			+ "INNER JOIN DE_SERVIZIO ON (AG_ASSEGNAZIONE.CODSERVIZIO=DE_SERVIZIO.CODSERVIZIO) "
			+ "INNER JOIN DE_AMBIENTE ON (AG_ASSEGNAZIONE.PRGAMBIENTE=DE_AMBIENTE.PRGAMBIENTE) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		int cdnUt = user.getCodut();
		int cdnTipoGruppo = user.getCdnTipoGruppo();
		String codCpi;
		if (cdnTipoGruppo == 1) {
			codCpi = user.getCodRif();
		} else {
			codCpi = req.getAttribute("agenda_codCpi").toString();
		}
		String codServizio = (String) req.getAttribute("CODSERVIZIO");
		String prgAmbiente = (String) req.getAttribute("PRGAMBIENTE");
		String prgSpi = (String) req.getAttribute("PRGSPI");
		boolean listaCompleta = req.containsAttribute("LISTA_COMPLETA");
		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		query_totale.append("WHERE AG_ASSEGNAZIONE.CODCPI='");
		query_totale.append(codCpi);
		query_totale.append("'");
		// solo operatori e servizi validi ad oggi
		query_totale.append(" AND AN_SPI.DATFINEVAL >= SYSDATE  AND AN_SPI.DATINIZIOVAL <= SYSDATE");
		query_totale.append(" AND DE_SERVIZIO.DATFINEVAL >= SYSDATE AND DE_SERVIZIO.DATINIZIOVAL <= SYSDATE");
		if (!listaCompleta && codServizio != null && !codServizio.equals("")) {
			query_totale.append(" AND AG_ASSEGNAZIONE.CODSERVIZIO='");
			query_totale.append(codServizio);
			query_totale.append("'");
		}
		if (!listaCompleta && prgAmbiente != null && !prgAmbiente.equals("")) {
			query_totale.append(" AND AG_ASSEGNAZIONE.PRGAMBIENTE=");
			query_totale.append(prgAmbiente);
		}
		if (!listaCompleta && prgSpi != null && !prgSpi.equals("")) {
			query_totale.append(" AND AG_ASSEGNAZIONE.PRGSPI=");
			query_totale.append(prgSpi);
		}
		// Debug
		_logger.debug("sil.module.agenda.cpi_list_assegnazione" + "::Stringa di ricerca:" + query_totale.toString());

		return query_totale.toString();
	}
}