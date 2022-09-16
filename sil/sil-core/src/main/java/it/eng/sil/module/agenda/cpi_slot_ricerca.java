package it.eng.sil.module.agenda;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.security.User;

/*
 * Questa classe restituisce la query per la ricerca degli slot 
 * relativi ad un CpI.
 * 
 * @author: Stefania Orioli
 * 
 */

public class cpi_slot_ricerca implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(cpi_slot_ricerca.class.getName());

	public cpi_slot_ricerca() {
	}

	private static final String SELECT_SQL_BASE = "select distinct " + "ag_slot.dtmdataora as data_ora, "
			+ "to_char(dtmdataora, 'dd/mm/yyyy') as data_cod, " + "to_char(dtmdataora, 'dd/mm/yyyy') as data, "
			+ "to_char(ag_slot.dtmdataora, 'hh24:mi')  as orario, " + "ag_slot.numminuti, "
			+ "de_servizio.strDescrizione as servizio,  " + "de_servizio.codServizio as codServizio,  "
			+ "ag_slot.prgSlot, " + "ag_slot.codCpi,  "
			+ "to_char(PG_AGENDA.pdSlotErr(ag_slot.CODCPI, ag_slot.PRGSLOT))  as APP_ERR, " + "1 as Tipo, "
			+ "(select count(*) from ag_spi_slot where ag_spi_slot.PRGSLOT=ag_slot.PRGSLOT and ag_spi_slot.CODCPI=ag_slot.CODCPI) as nro_ope, "
			+ "FIRST_VALUE(concat(concat(an_spi.strNome,' '),an_spi.strCognome)) OVER (PARTITION BY ag_slot.PRGSLOT, ag_slot.CODCPI ORDER BY an_spi.strCognome, an_spi.strNome) as Operatore, "
			+ "de_stato_slot.STRDESCRIZIONE as stato, "
			+ "(select nvl( (select to_char(ts_config_loc.num)  from ts_config_loc  "
			+ " where strcodrif=(select ts_generale.codprovinciasil from ts_generale) and codtipoconfig='UMBNGEAZ') , 0) "
			+ " from dual) choiceLabel " + "FROM ag_slot "
			+ "LEFT JOIN de_servizio ON (ag_slot.codservizio = de_servizio.codservizio) "
			+ "LEFT JOIN AG_SPI_SLOT ON (ag_slot.PRGSLOT=ag_spi_slot.PRGSLOT and ag_slot.CODCPI=ag_spi_slot.CODCPI) "
			+ "LEFT JOIN an_spi ON (ag_spi_slot.prgSpi = an_spi.prgSpi) "
			+ "LEFT JOIN de_stato_slot on (ag_slot.CODSTATOSLOT=de_stato_slot.CODSTATOSLOT)  ";

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

		String giorno = (String) req.getAttribute("giornoDB");
		String mese = (String) req.getAttribute("meseDB");
		String anno = (String) req.getAttribute("annoDB");

		StringBuffer buf = new StringBuffer();

		buf.append(" WHERE ");

		/* FILTRI */
		// if (cdnTipoGruppo==1) {
		buf.append(" (ag_slot.codcpi='" + codCpi + "') ");
		// } // else ==4 cittadino, ==5 azienda ....

		// PARAMETRI DELLA RICERCA
		String sel_operatore = (String) req.getAttribute("sel_operatore");
		String sel_servizio = (String) req.getAttribute("sel_servizio");
		String sel_aula = (String) req.getAttribute("sel_aula");
		String strDataDa = (String) req.getAttribute("dataDal");
		String strDataA = (String) req.getAttribute("dataAl");

		if (sel_servizio != null && !sel_servizio.equals("")) {
			buf.append(" and (ag_slot.codservizio='" + sel_servizio + "') ");
		}
		if (sel_operatore != null && !sel_operatore.equals("")) {
			buf.append(" and (ag_spi_slot.prgspi='" + sel_operatore + "') ");
		}
		if (sel_aula != null && !sel_aula.equals("")) {
			buf.append(" and (ag_slot.prgambiente='" + sel_aula + "') ");
		}
		if ((strDataDa != null && !strDataDa.equals("")) && (strDataA != null && !strDataA.equals(""))) {
			buf.append(" and (to_date(to_char(dtmdataora, 'dd/mm/yyyy'),'dd/mm/yyyy') BETWEEN ");
			buf.append("to_date('" + strDataDa + "','dd/mm/yyyy')");
			buf.append(" and ");
			buf.append("to_date('" + strDataA + "','dd/mm/yyyy')");
			buf.append(") ");
		} else {
			if (strDataDa != null && !strDataDa.equals("")) {
				buf.append(" and (to_date(to_char(dtmdataora, 'dd/mm/yyyy'),'dd/mm/yyyy') >= ");
				buf.append("to_date('" + strDataDa + "','dd/mm/yyyy')");
				buf.append(") ");
			}
			if (strDataA != null && !strDataA.equals("")) {
				buf.append(" and (to_date(to_char(dtmdataora, 'dd/mm/yyyy'),'dd/mm/yyyy') <= ");
				buf.append("to_date('" + strDataA + "','dd/mm/yyyy')");
				buf.append(") ");
			}
		}

		// Costruisco la query
		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		query_totale.append(buf.toString());
		query_totale.append(" order by 1 ");

		// Debug
		_logger.debug("sil.module.agenda.cpi_slot_ricerca" + "::Stringa di ricerca:" + query_totale.toString());

		return query_totale.toString();

	}
}