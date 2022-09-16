package it.eng.sil.module.agenda;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.security.User;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.tags.Util;

/**
 * Questa classe restituisce la query per la ricerca degli appuntamenti 
 * relativi ad un CpI (ristretto agli appuntamenti personali se si tratta
 * di un lavoratore oppure di una azienda).
 * 
 * @author: Stefania Orioli
Data Modifica : 02/03/2010   
Programmatore : Alessandro Donisi
Analista      : Fiore Carmela
Motivo        : Cambio della ricerca contatti. é stato aggiunto un nuovo 
criterio di ricerca label “e-mail o sms invio” 
 

*/
public class cpi_con_ricerca implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(cpi_con_ricerca.class.getName());

	public cpi_con_ricerca() {
	}

	private static final String SELECT_SQL_BASE = "select "
			+ "ag_contatto.Prgspicontatto prgspi,"
			+ "to_char(ag_contatto.DATCONTATTO, 'dd/mm/yy') as data, "
			+ "ag_contatto.STRORACONTATTO as orario, "
			+ "ag_contatto.DATCONTATTO dataSort, "
			+ " to_date(ag_contatto.STRORACONTATTO, 'hh24:mi') as orarioSort, "
			+ "1 as Tipo, "
			+ "(Initcap(an_spi.strNome) || ' ' || Initcap(an_spi.strCognome)) as operatore, "
			+ "ag_contatto.prgcontatto, "
			+ "ag_contatto.codcpicontatto, "
			+ "DE_MOTIVO_CONTATTOAG.STRDESCRIZIONE as motivo, "
			+ "DE_EFFETTO_CONTATTO.STRDESCRIZIONE as effetto, "
			+ "DE_TIPO_CONTATTOAG.STRDESCRIZIONE as tipo_con, "
			+ "DECODE (STRIO,'O','Dal CpI','I','Al CpI') as STRIO, "
			+ "Initcap(an_lavoratore.strNome) || ' ' ||Initcap(an_lavoratore.strCognome) as Lav_Az "
			+ "FROM ag_contatto "
			+ "left join DE_MOTIVO_CONTATTOAG on (ag_contatto.PRGMOTCONTATTO=DE_MOTIVO_CONTATTOAG.PRGMOTCONTATTO) "
			+ "inner join an_lavoratore on (ag_contatto.cdnLavoratore=an_lavoratore.cdnLavoratore) "
			+ "LEFT JOIN DE_EFFETTO_CONTATTO ON (ag_contatto.PRGEFFETTOCONTATTO = DE_EFFETTO_CONTATTO.PRGEFFETTOCONTATTO) "
			+ "LEFT JOIN an_spi ON (ag_contatto.prgSpiContatto = an_spi.prgSpi) "
			+ "LEFT JOIN DE_TIPO_CONTATTOAG ON (ag_contatto.prgTipoContatto = DE_TIPO_CONTATTOAG.prgTipoContatto) ";

	private static final String SELECT_SQL_UNION = "select "
			+ "ag_contatto.Prgspicontatto prgspi,"
			+ "to_char(ag_contatto.DATCONTATTO, 'dd/mm/yy') as data, "
			+ "ag_contatto.STRORACONTATTO as orario, "
			+ "ag_contatto.DATCONTATTO dataSort, "
			+ " to_date(ag_contatto.STRORACONTATTO, 'hh24:mi') as orarioSort, "
			+ "2 as Tipo, "
			+ "(Initcap(an_spi.strNome) || ' ' || Initcap(an_spi.strCognome)) as operatore, "
			+ "ag_contatto.prgcontatto, "
			+ "ag_contatto.codcpicontatto, "
			+ "DE_MOTIVO_CONTATTOAG.STRDESCRIZIONE as motivo, "
			+ "DE_EFFETTO_CONTATTO.STRDESCRIZIONE as effetto, "
			+ "DE_TIPO_CONTATTOAG.STRDESCRIZIONE as tipo_con, "
			+ "DECODE (STRIO,'O','Dal CpI','I','Al CpI') as STRIO, "
			+ "an_azienda.STRRAGIONESOCIALE as Lav_Az "
			+ "FROM ag_contatto "
			+ "left join DE_MOTIVO_CONTATTOAG on (ag_contatto.PRGMOTCONTATTO=DE_MOTIVO_CONTATTOAG.PRGMOTCONTATTO) "
			+ "inner JOIN an_azienda ON (ag_contatto.PRGAZIENDA=an_azienda.PRGAZIENDA) "
			+ "LEFT JOIN DE_EFFETTO_CONTATTO ON (ag_contatto.PRGEFFETTOCONTATTO = DE_EFFETTO_CONTATTO.PRGEFFETTOCONTATTO) "
			+ "LEFT JOIN an_spi ON (ag_contatto.prgSpiContatto = an_spi.prgSpi) "
			+ "LEFT JOIN DE_TIPO_CONTATTOAG ON (ag_contatto.prgTipoContatto = DE_TIPO_CONTATTOAG.prgTipoContatto) ";

	private static final String SELECT_SQL_UNION0 = "select "
			+ "ag_contatto.Prgspicontatto prgspi,"
			+ "to_char(ag_contatto.DATCONTATTO, 'dd/mm/yy') as data, "
			+ "ag_contatto.STRORACONTATTO as orario, "
			+ "ag_contatto.DATCONTATTO dataSort, "
			+ " to_date(ag_contatto.STRORACONTATTO, 'hh24:mi') as orarioSort, "
			+ "0 as Tipo, "
			+ "(Initcap(an_spi.strNome) || ' ' || Initcap(an_spi.strCognome)) as operatore, "
			+ "ag_contatto.prgcontatto, "
			+ "ag_contatto.codcpicontatto, "
			+ "DE_MOTIVO_CONTATTOAG.STRDESCRIZIONE as motivo, "
			+ "DE_EFFETTO_CONTATTO.STRDESCRIZIONE as effetto, "
			+ "DE_TIPO_CONTATTOAG.STRDESCRIZIONE as tipo_con, "
			+ "DECODE (STRIO,'O','Dal CpI','I','Al CpI') as STRIO, "
			+ "'' as Lav_Az "
			+ "FROM ag_contatto "
			+ "left join DE_MOTIVO_CONTATTOAG on (ag_contatto.PRGMOTCONTATTO=DE_MOTIVO_CONTATTOAG.PRGMOTCONTATTO) "
			+ "LEFT JOIN DE_EFFETTO_CONTATTO ON (ag_contatto.PRGEFFETTOCONTATTO = DE_EFFETTO_CONTATTO.PRGEFFETTOCONTATTO) "
			+ "LEFT JOIN an_spi ON (ag_contatto.prgSpiContatto = an_spi.prgSpi) "
			+ "LEFT JOIN DE_TIPO_CONTATTOAG ON (ag_contatto.prgTipoContatto = DE_TIPO_CONTATTOAG.prgTipoContatto) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		int cdnUt = user.getCodut();
		int cdnTipoGruppo = user.getCdnTipoGruppo();
		/*
		 * String fmiei = (String)
		 * sessionContainer.getAttribute("agenda_FMiei"); String foperatore =
		 * (String) sessionContainer.getAttribute("agenda_FOperatore"); String
		 * fservizio = (String)
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
		 * if(fmiei!=null && fmiei.equals("1")) { buf.append(" left join
		 * ts_utente on (ag_contatto.PRGSPI=ts_utente.PRGSPI) "); }
		 */

		buf.append(" WHERE ");

		/* FILTRI */
		if (cdnTipoGruppo == 1) {
			buf.append(" (ag_contatto.codcpicontatto='" + codCpi + "') ");
		} // else ==4 cittadino, ==5 azienda ....

		/*
		 * if(fmiei!=null && fmiei.equals("1")) { buf.append("and
		 * (ts_utente.CDNUT = " + cdnUt + ") "); } if(foperatore!=null &&
		 * !foperatore.equals("")) { buf.append(" and (ag_contatto.PRGSPI= " +
		 * foperatore + ") "); } if(fservizio!=null && !fservizio.equals("")) {
		 * buf.append(" and (ag_contatto.CODSERVIZIO = '" + fservizio + "') "); }
		 */

		// PARAMETRI DELLA RICERCA
		String sel_operatore = (String) req.getAttribute("sel_operatore");
		String STRIO = (String) req.getAttribute("STRIO");
		String EmailoSMS = (String) req.getAttribute("EmailoSMS");		
		String sel_tipo = (String) req.getAttribute("sel_tipo");
		String sel_motivo = (String) req.getAttribute("sel_motivo");
		String sel_effetto = (String) req.getAttribute("effettoCon");

		String strCodiceFiscaleAz = (String) req.getAttribute("strCodiceFiscaleAz");
		String piva = (String) req.getAttribute("piva");
		String strRagSoc = (String) req.getAttribute("strRagSoc");

		String strCodiceFiscale = (String) req.getAttribute("strCodiceFiscale");
		String strCognome = (String) req.getAttribute("strCognome");
		String strNome = (String) req.getAttribute("strNome");

		String strDataDa = (String) req.getAttribute("dataDal");
		String strDataA = (String) req.getAttribute("dataAl");

		if (STRIO != null && !STRIO.equals("")) {
			buf.append(" and (ag_contatto.STRIO='" + STRIO + "') ");
		}
		if (!EmailoSMS.equals("")) {
			if(EmailoSMS.equals("null")){
				buf.append(" and (ag_contatto.FLGINVIATOSMS is null) ");
			}else buf.append(" and (ag_contatto.FLGINVIATOSMS='"+EmailoSMS+"') ");
			
		}
		if (sel_operatore != null && !sel_operatore.equals("")) {
			buf.append(" and (ag_contatto.prgspiContatto='" + sel_operatore + "') ");
		}
		if (sel_tipo != null && !sel_tipo.equals("")) {
			buf.append(" and (ag_contatto.PRGTIPOCONTATTO='" + sel_tipo + "') ");
		}
		if (sel_motivo != null && !sel_motivo.equals("")) {
			buf.append(" and (ag_contatto.PRGMOTCONTATTO='" + sel_motivo + "') ");
		}
		if (sel_effetto != null && !sel_effetto.equals("")) {
			buf.append(" and (ag_contatto.PRGEFFETTOCONTATTO='" + sel_effetto + "') ");
		}

		if ((strDataDa != null && !strDataDa.equals("")) && (strDataA != null && !strDataA.equals(""))) {
			buf.append(" and (trunc(DATCONTATTO) BETWEEN ");
			buf.append("to_date('" + strDataDa + "','dd/mm/yyyy')");
			buf.append(" and ");
			buf.append("to_date('" + strDataA + "','dd/mm/yyyy')");
			buf.append(") ");
		} else {
			if (strDataDa != null && !strDataDa.equals("")) {
				buf.append(" and (trunc(DATCONTATTO) >= ");
				buf.append("to_date('" + strDataDa + "','dd/mm/yyyy')");
				buf.append(") ");
			}
			if (strDataA != null && !strDataA.equals("")) {
				buf.append(" and (trunc(DATCONTATTO) <= ");
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
			where_lav.append(" (upper(an_lavoratore.strCognome) like upper('" + Util.replace(strCognome, "'", "''")
					+ "%')) ");
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

		if (strCodiceFiscaleAz != null && !strCodiceFiscaleAz.equals("")) {
			if (!where_az.toString().equals("")) {
				where_az.append("and");
			}
			where_az.append(" (upper(an_azienda.strCodiceFiscale) like upper('%"
					+ Util.replace(strCodiceFiscaleAz, "'", "''") + "%')) ");
		}

		if (piva != null && !piva.equals("")) {
			if (!where_az.toString().equals("")) {
				where_az.append("and");
			}
			where_az
					.append(" (upper(an_azienda.strPartitaIva) like upper('%" + Util.replace(piva, "'", "''") + "%')) ");
		}
		// Costruisco la query
		StringBuffer query_totale = new StringBuffer();
		String notAzienda = " and (nvl(ag_contatto.PRGAZIENDA, -1)=-1 and nvl(ag_contatto.PRGUNITA, -1)=-1) ";
		// String notLavoratori = " and NOT EXISTS ("
		// + "select * from ag_contatto a1 "
		// + "inner join ag_lavoratore on
		// (a1.PRGAPPUNTAMENTO=ag_lavoratore.PRGAPPUNTAMENTO and
		// a1.CODCPI=ag_lavoratore.CODCPI) "
		// + "where a1.CODCPI = ag_contatto.CODCPI and a1.PRGAPPUNTAMENTO =
		// ag_contatto.PRGAPPUNTAMENTO "
		// + ") ";
		String notLavoratori = " and (nvl(ag_contatto.CDNLAVORATORE, -1)=-1) ";

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
			query_totale.append(SELECT_SQL_UNION0);
			query_totale.append(buf.toString());
			query_totale.append(notAzienda);
			query_totale.append(notLavoratori);
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
		}
		// Caso 3: cerco solo per lavoratore
		if (!where_lav.toString().equals("") && where_az.toString().equals("")) {
			query_totale.append(SELECT_SQL_BASE);
			query_totale.append(buf.toString());
			query_totale.append(notAzienda);
			query_totale.append(" and " + where_lav);
		}
		// Caso 4: cerco solo per Azienda
		if (!where_az.toString().equals("") && where_lav.toString().equals("")) {
			query_totale.append(SELECT_SQL_UNION);
			query_totale.append(buf.toString());
			query_totale.append(" and " + where_az);
			query_totale.append(notLavoratori);
		}

		query_totale.append(" order by dataSort, orarioSort ");

		// Debug
		_logger.debug("sil.module.agenda.cpi_app_giorno" + "::Stringa di ricerca:" + query_totale.toString());

		return query_totale.toString();

	}
}