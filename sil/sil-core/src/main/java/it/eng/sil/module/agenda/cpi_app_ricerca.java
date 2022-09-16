package it.eng.sil.module.agenda;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.tags.Util;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.security.User;

/*
 * Questa classe restituisce la query per la ricerca degli appuntamenti 
 * relativi ad un CpI (ristretto agli appuntamenti personali se si tratta
 * di un lavoratore oppure di una azienda).
 * 
 * @author: Stefania Orioli
 * 
 */

public class cpi_app_ricerca implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(cpi_app_ricerca.class.getName());

	public cpi_app_ricerca() {
	}

	private static final String SELECT_SQL_BASE = "select " + "ag_agenda.prgSpi, "
			+ "ag_agenda.dtmdataora as data_ora, " + "to_char(ag_agenda.dtmdataora, 'dd/mm/yy') as data_cod, "
			+ "to_char(ag_agenda.dtmdataora, 'dd/mm/yy') as data, "
			+ "to_char(ag_agenda.dtmdataora, 'hh24:mi')  as orario, " + "ag_agenda.numminuti, "
			+ "de_servizio.strDescrizione as servizio, "
			+ "(Initcap(an_spi.strNome) || ' ' || Initcap(an_spi.strCognome)) as operatore, " + "0 as NRO_OPE, "
			+ "ag_agenda.prgappuntamento, " + "ag_agenda.codcpi, " + "de_esito_appunt.STRDESCRIZIONE as esito, "
			+ "to_char(PG_AGENDA.pdAppErr(ag_agenda.CODCPI, ag_agenda.PRGAPPUNTAMENTO))  as APP_ERR, " + "1 as Tipo, "
			+ "(select count(*) from ag_lavoratore where ag_lavoratore.PRGAPPUNTAMENTO=ag_agenda.PRGAPPUNTAMENTO and ag_lavoratore.CODCPI=ag_agenda.CODCPI) as nro, "
			+ "(PG_AGENDA.PDFIRSTLAVAPP(ag_agenda.CODCPI, ag_agenda.PRGAPPUNTAMENTO)) as Lav_AZ "
			+ ", de_stato_appuntamento.STRDESCRIZIONE as Stato, "
			+ "(select nvl( (select to_char(ts_config_loc.num)  from ts_config_loc  "
			+ " where strcodrif=(select ts_generale.codprovinciasil from ts_generale) and codtipoconfig='UMBNGEAZ') , 0) "
			+ " from dual) choiceLabel " + "FROM ag_agenda "
			+ "left join ag_lavoratore on (ag_agenda.codcpi=ag_lavoratore.codcpi and ag_agenda.PRGappuntamento=ag_lavoratore.prgappuntamento) "
			+ "inner join an_lavoratore on (ag_lavoratore.cdnLavoratore=an_lavoratore.cdnLavoratore) "
			+ " LEFT JOIN de_servizio ON (ag_agenda.codservizio = de_servizio.codservizio) "
			+ "LEFT JOIN an_spi ON (ag_agenda.prgSpi = an_spi.prgSpi) "
			+ "LEFT JOIN de_esito_appunt ON (ag_agenda.CODESITOAPPUNT = de_esito_appunt.CODESITOAPPUNT) "
			+ "left join de_stato_appuntamento on (ag_agenda.CODSTATOAPPUNTAMENTO=de_stato_appuntamento.CODSTATOAPPUNTAMENTO) ";

	private static final String SELECT_SQL_UNION = "select " + "ag_agenda.prgSpi, "
			+ "ag_agenda.dtmdataora as data_ora, " + "to_char(ag_agenda.dtmdataora, 'dd/mm/yy') as data_cod, "
			+ "to_char(ag_agenda.dtmdataora, 'dd/mm/yy') as data, "
			+ "to_char(ag_agenda.dtmdataora, 'hh24:mi')  as orario, " + "ag_agenda.numminuti, "
			+ "de_servizio.strDescrizione as servizio, "
			+ "(Initcap(an_spi.strNome) || ' ' || Initcap(an_spi.strCognome)) as operatore, " + "0 as NRO_OPE, "
			+ "ag_agenda.prgappuntamento, " + "ag_agenda.codcpi, " + "de_esito_appunt.STRDESCRIZIONE as esito, "
			+ "to_char(PG_AGENDA.pdAppErr(ag_agenda.CODCPI, ag_agenda.PRGAPPUNTAMENTO))  as APP_ERR, " + "2 as Tipo, "
			+ "-1 as nro, " + "an_azienda.STRRAGIONESOCIALE as Lav_Az "
			+ ", de_stato_appuntamento.STRDESCRIZIONE as Stato, "
			+ "(select nvl( (select to_char(ts_config_loc.num)  from ts_config_loc  "
			+ " where strcodrif=(select ts_generale.codprovinciasil from ts_generale) and codtipoconfig='UMBNGEAZ') , 0) "
			+ " from dual) choiceLabel " + "FROM ag_agenda "
			+ "LEFT JOIN de_servizio ON (ag_agenda.codservizio = de_servizio.codservizio) "
			+ "LEFT JOIN an_spi ON (ag_agenda.prgSpi = an_spi.prgSpi) "
			+ "LEFT JOIN de_esito_appunt ON (ag_agenda.CODESITOAPPUNT = de_esito_appunt.CODESITOAPPUNT) "
			+ "left join de_stato_appuntamento on (ag_agenda.CODSTATOAPPUNTAMENTO=de_stato_appuntamento.CODSTATOAPPUNTAMENTO) "
			+ "INNER JOIN an_azienda ON (ag_agenda.PRGAZIENDA=an_azienda.PRGAZIENDA) ";

	private static final String SELECT_SQL_UNION3 = "select " + "ag_agenda.prgSpi, "
			+ "ag_agenda.dtmdataora as data_ora, " + "to_char(ag_agenda.dtmdataora, 'dd/mm/yy') as data_cod, "
			+ "to_char(ag_agenda.dtmdataora, 'dd/mm/yy') as data, "
			+ "to_char(ag_agenda.dtmdataora, 'hh24:mi')  as orario, "
			+ "ag_agenda.numminuti, de_servizio.strDescrizione as servizio, "
			+ "(Initcap(an_spi.strNome) ||' ' || Initcap(an_spi.strCognome)) as operatore, " + "0 as NRO_OPE, "
			+ "ag_agenda.prgappuntamento, " + "ag_agenda.codcpi, " + "de_esito_appunt.STRDESCRIZIONE as esito, "
			+ "to_char(PG_AGENDA.pdAppErr(ag_agenda.CODCPI, ag_agenda.PRGAPPUNTAMENTO))  as APP_ERR, " + "3 as Tipo, "
			+ "(select count(*) from ag_lavoratore where ag_lavoratore.PRGAPPUNTAMENTO=ag_agenda.PRGAPPUNTAMENTO and ag_lavoratore.CODCPI=ag_agenda.CODCPI) as nro, "
			+ "an_azienda.STRRAGIONESOCIALE as Lav_Az " + ", de_stato_appuntamento.STRDESCRIZIONE as Stato, "
			+ "(select nvl( (select to_char(ts_config_loc.num)  from ts_config_loc  "
			+ " where strcodrif=(select ts_generale.codprovinciasil from ts_generale) and codtipoconfig='UMBNGEAZ') , 0) "
			+ " from dual) choiceLabel " + "FROM ag_agenda "
			+ "LEFT JOIN de_servizio ON (ag_agenda.codservizio = de_servizio.codservizio) "
			+ "LEFT JOIN an_spi ON (ag_agenda.prgSpi = an_spi.prgSpi) "
			+ "LEFT JOIN de_esito_appunt ON (ag_agenda.CODESITOAPPUNT = de_esito_appunt.CODESITOAPPUNT) "
			+ "INNER JOIN an_azienda ON (ag_agenda.PRGAZIENDA=an_azienda.PRGAZIENDA)"
			+ "left join de_stato_appuntamento on (ag_agenda.CODSTATOAPPUNTAMENTO=de_stato_appuntamento.CODSTATOAPPUNTAMENTO) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		int cdnUt = user.getCodut();
		int cdnTipoGruppo = user.getCdnTipoGruppo();
		/*
		 * String fmiei = (String) sessionContainer.getAttribute("agenda_FMiei"); String foperatore = (String)
		 * sessionContainer.getAttribute("agenda_FOperatore"); String fservizio = (String)
		 * sessionContainer.getAttribute("agenda_FServizio");
		 */
		String codCpi = StringUtils.getAttributeStrNotNull(req, "CODCPI");
		if (codCpi.equalsIgnoreCase("")) {
			if (cdnTipoGruppo == 1) {
				codCpi = user.getCodRif();
			}
		}

		String giorno = (String) req.getAttribute("giornoDB");
		String mese = (String) req.getAttribute("meseDB");
		String anno = (String) req.getAttribute("annoDB");

		// StringBuffer query_totale=new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		/*
		 * if(fmiei!=null && fmiei.equals("1")) { buf.append(" left join ts_utente on
		 * (ag_agenda.PRGSPI=ts_utente.PRGSPI) "); }
		 */

		buf.append(" WHERE ");

		/* FILTRI */
		if (cdnTipoGruppo == 1) {
			buf.append(" (ag_agenda.codcpi='" + codCpi + "') ");
		} // else ==4 cittadino, ==5 azienda ....

		/*
		 * if(fmiei!=null && fmiei.equals("1")) { buf.append("and (ts_utente.CDNUT = " + cdnUt + ") "); }
		 * if(foperatore!=null && !foperatore.equals("")) { buf.append(" and (ag_agenda.PRGSPI= " + foperatore + ") ");
		 * } if(fservizio!=null && !fservizio.equals("")) { buf.append(" and (ag_agenda.CODSERVIZIO = '" + fservizio +
		 * "') "); }
		 */

		// PARAMETRI DELLA RICERCA
		String sel_operatore = (String) req.getAttribute("sel_operatore");
		String sel_servizio = (String) req.getAttribute("sel_servizio");
		String sel_aula = (String) req.getAttribute("sel_aula");
		String sel_esito = (String) req.getAttribute("esitoApp");
		// Appuntamento prenotato da ANPAL
		String sel_anpal = null;
		if (req.containsAttribute("strIdCoap")) {
			sel_anpal = "SI";
		}

		String piva = (String) req.getAttribute("piva");
		String strRagSoc = (String) req.getAttribute("strRagSoc");
		String strCodiceFiscaleAz = (String) req.getAttribute("strCodiceFiscaleAz");

		String strCodiceFiscale = (String) req.getAttribute("strCodiceFiscale");
		String strCognome = (String) req.getAttribute("strCognome");
		String strNome = (String) req.getAttribute("strNome");
		String strDataDa = (String) req.getAttribute("dataDal");
		String strDataA = (String) req.getAttribute("dataAl");

		if (sel_servizio != null && !sel_servizio.equals("")) {
			buf.append(" and (ag_agenda.codservizio='" + sel_servizio + "') ");
		}
		if (sel_operatore != null && !sel_operatore.equals("")) {
			buf.append(" and (ag_agenda.prgspi='" + sel_operatore + "') ");
		}
		if (sel_aula != null && !sel_aula.equals("")) {
			buf.append(" and (ag_agenda.PRGAMBIENTE='" + sel_aula + "') ");
		}
		if (sel_esito != null && !sel_esito.equals("")) {
			buf.append(" and (ag_agenda.CODESITOAPPUNT='" + sel_esito + "') ");
		}
		if (sel_anpal != null && sel_anpal.equals("SI")) {
			buf.append(" and (ag_agenda.STRIDCOAP is not null or trim(ag_agenda.STRIDCOAP) !='') ");
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

		// Costruisco filtri lavoratore
		StringBuffer where_lav = new StringBuffer();
		if (strCodiceFiscale != null && !strCodiceFiscale.equals("")) {
			where_lav.append(" (upper(an_lavoratore.strCodiceFiscale) like upper('"
					+ Util.replace(strCodiceFiscale, "'", "''") + "%')) ");
		}
		if (strCognome != null && !strCognome.equals("")) {
			if (!where_lav.toString().equals("")) {
				where_lav.append("and");
			}
			where_lav.append(
					" (upper(an_lavoratore.strCognome) like upper('" + Util.replace(strCognome, "'", "''") + "%')) ");
		}
		if (strNome != null && !strNome.equals("")) {
			if (!where_lav.toString().equals("")) {
				where_lav.append("and");
			}
			where_lav
					.append(" (upper(an_lavoratore.strNome) like upper('" + Util.replace(strNome, "'", "''") + "%')) ");
		}

		// Costruisco filtri azienda
		StringBuffer where_az = new StringBuffer();
		if (strRagSoc != null && !strRagSoc.equals("")) {
			where_az.append(" (upper(an_azienda.strRagioneSociale) like upper('%" + Util.replace(strRagSoc, "'", "''")
					+ "%')) ");
		}
		if (piva != null && !piva.equals("")) {
			where_az.append(
					" (upper(an_azienda.strPartitaIva) like upper('%" + Util.replace(piva, "'", "''") + "%')) ");
		}
		if (strCodiceFiscaleAz != null && !strCodiceFiscaleAz.equals("")) {
			where_az.append(" (upper(an_azienda.strCodiceFiscale) like upper('%"
					+ Util.replace(strCodiceFiscaleAz, "'", "''") + "%')) ");
		}

		// Costruisco la query
		StringBuffer query_totale = new StringBuffer();
		String notAzienda = " and (ag_agenda.PRGAZIENDA is null and ag_agenda.PRGUNITA is null) ";
		String notLavoratori = " and NOT EXISTS (" + "select * from ag_agenda a1 "
				+ "inner join ag_lavoratore on (a1.PRGAPPUNTAMENTO=ag_lavoratore.PRGAPPUNTAMENTO and a1.CODCPI=ag_lavoratore.CODCPI) "
				+ "where a1.CODCPI = ag_agenda.CODCPI and a1.PRGAPPUNTAMENTO = ag_agenda.PRGAPPUNTAMENTO " + ") ";
		String andLavoratori = " and EXISTS (" + "select * from ag_agenda a1 "
				+ "inner join ag_lavoratore on (a1.PRGAPPUNTAMENTO=ag_lavoratore.PRGAPPUNTAMENTO and a1.CODCPI=ag_lavoratore.CODCPI) "
				+ "where a1.CODCPI = ag_agenda.CODCPI and a1.PRGAPPUNTAMENTO = ag_agenda.PRGAPPUNTAMENTO " + ") ";
		String strJoinAgLav = " LEFT JOIN AG_LAVORATORE ON (ag_agenda.PRGAPPUNTAMENTO=ag_lavoratore.PRGAPPUNTAMENTO and ag_agenda.CODCPI=ag_lavoratore.CODCPI) "
				+ " LEFT JOIN AN_LAVORATORE ON (ag_lavoratore.CDNLAVORATORE=an_lavoratore.CDNLAVORATORE) ";

		// Caso 1: non cerco nè per lavoratore nè per azienda
		if (where_az.toString().equals("") && where_lav.toString().equals("")) {
			query_totale.append(SELECT_SQL_BASE);
			query_totale.append(buf.toString());
			query_totale.append(notAzienda);
			query_totale.append(" UNION ALL ");
			query_totale.append(SELECT_SQL_UNION);
			query_totale.append(buf.toString());
			query_totale.append(notLavoratori);
			query_totale.append(" UNION ALL ");
			query_totale.append(SELECT_SQL_UNION3);
			query_totale.append(buf.toString());
			query_totale.append(andLavoratori);
		}
		// Caso 2: cerco sia per lavoratore sia per azienda
		if (!where_az.toString().equals("") && !where_lav.toString().equals("")) {
			query_totale.append(SELECT_SQL_BASE);
			query_totale.append(buf.toString());
			query_totale.append(notAzienda);
			query_totale.append(" and " + where_lav);
			query_totale.append(" UNION ALL ");
			query_totale.append(SELECT_SQL_UNION);
			query_totale.append(buf.toString());
			query_totale.append(" and " + where_az);
			query_totale.append(notLavoratori);
			query_totale.append(" UNION ALL ");
			// La Select_sql_union3 cerca sia per lavoratore sia per azienda
			query_totale.append(SELECT_SQL_UNION3);
			query_totale.append(strJoinAgLav);
			query_totale.append(buf.toString());
			query_totale.append(" and (" + where_az + " or " + where_lav + ") ");
			query_totale.append(andLavoratori);
		}
		// Caso 3: cerco solo per lavoratore
		if (!where_lav.toString().equals("") && where_az.toString().equals("")) {
			query_totale.append(SELECT_SQL_BASE);
			query_totale.append(buf.toString());
			query_totale.append(notAzienda);
			query_totale.append(" and " + where_lav);
			query_totale.append(" UNION ALL ");
			query_totale.append(SELECT_SQL_UNION3);
			query_totale.append(strJoinAgLav);
			query_totale.append(buf.toString());
			query_totale.append(" and " + where_lav);
			query_totale.append(andLavoratori);
		}
		// Caso 4: cerco solo per Azienda
		if (!where_az.toString().equals("") && where_lav.toString().equals("")) {
			query_totale.append(SELECT_SQL_UNION);
			query_totale.append(buf.toString());
			query_totale.append(" and " + where_az);
			query_totale.append(notLavoratori);
			query_totale.append(" UNION ALL ");
			query_totale.append(SELECT_SQL_UNION3);
			query_totale.append(buf.toString());
			query_totale.append(andLavoratori);
			query_totale.append(" and " + where_az);
		}

		query_totale.append(" order by 1 ");

		// Debug
		_logger.debug("sil.module.agenda.cpi_app_giorno" + "::Stringa di ricerca:" + query_totale.toString());

		return query_totale.toString();

	}
}