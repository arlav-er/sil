package it.eng.sil.module.agenda;

import java.util.Calendar;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.security.User;

/*
 * Questa classe restituisce il sommario giornaliero dei contatti 
 * relativi ad un CpI 
 * 
 * @author: Stefania Orioli
 * 
 */

public class cpi_app_contatti implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(cpi_app_contatti.class.getName());

	public cpi_app_contatti() {
	}

	private static final String SELECT_SQL_BASE = "select distinct " + "to_char(datcontatto, 'dd/mm/yyyy') as data, "
			+ "strOraContatto as orario, " + "an_spi.strNome || ' ' || an_spi.strCognome as operatore, "
			+ "prgContatto, " + "codCpiContatto, "
			+ "an_lavoratore.strNome || ' ' || an_lavoratore.strCognome as Lav_AZ " + "FROM ag_contatto "
			+ "LEFT JOIN an_spi ON (ag_contatto.prgSpiContatto = an_spi.prgSpi) "
			+ "LEFT JOIN AN_LAVORATORE ON (ag_contatto.CDNLAVORATORE=an_lavoratore.CDNLAVORATORE) ";

	private static final String SELECT_SQL_UNION = "select distinct " + "to_char(datcontatto, 'dd/mm/yyyy') as data, "
			+ "strOraContatto as orario, " + "an_spi.strNome || ' ' || an_spi.strCognome as operatore, "
			+ "prgContatto, " + "codCpiContatto, " + "an_azienda.STRRAGIONESOCIALE as Lav_Az " + "FROM ag_contatto "
			+ "LEFT JOIN an_spi ON (ag_contatto.prgSpiContatto = an_spi.prgSpi) "
			+ "INNER JOIN an_azienda ON (ag_contatto.PRGAZIENDA=an_azienda.PRGAZIENDA) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		int cdnUt = user.getCodut();
		int cdnTipoGruppo = user.getCdnTipoGruppo();
		String fmiei = (String) sessionContainer.getAttribute("agenda_FMiei");
		String foperatore = (String) sessionContainer.getAttribute("agenda_FOperatore");
		String fservizio = (String) sessionContainer.getAttribute("agenda_FServizio");
		String codCpi = StringUtils.getAttributeStrNotNull(req, "CODCPI");
		if (codCpi.equalsIgnoreCase("")) {
			if (cdnTipoGruppo == 1) {
				codCpi = user.getCodRif();
			}
		}

		String giorno = (String) req.getAttribute("giornoDB");
		String mese = (String) req.getAttribute("meseDB");
		String anno = (String) req.getAttribute("annoDB");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if (fmiei != null && fmiei.equals("1")) {
			buf.append(" left join ts_utente on (ag_contatto.PRGSPICONTATTO=ts_utente.PRGSPI) ");
		}

		buf.append(" WHERE ");

		// Data Odierna
		Calendar oggi = Calendar.getInstance();
		int oggi_gg = oggi.get(5);
		int oggi_mm = oggi.get(2);
		int oggi_aa = oggi.get(1);

		if ((giorno != null) && (!giorno.equals(""))) {
			buf.append(" to_number(to_char(datcontatto, 'dd'))=" + giorno + " ");
		} else {
			buf.append(" to_number(to_char(datcontatto, 'dd'))=" + oggi_gg + " ");
		}

		buf.append(" and ");

		if ((mese != null) && (!mese.equals(""))) {
			buf.append(" to_number(to_char(datcontatto, 'mm'))=" + mese + " ");
		} else {
			buf.append(" to_number(to_char(datcontatto, 'mm'))=" + (oggi_mm + 1) + " ");
		}

		buf.append(" and ");

		if ((anno != null) && (!anno.equals(""))) {
			buf.append(" to_number(to_char(datcontatto, 'yyyy'))=" + anno + " ");
		} else {
			buf.append(" to_number(to_char(datcontatto, 'yyyy'))=" + oggi_aa + " ");
		}

		/* FILTRI */
		/*
		 * if (cdnTipoGruppo==1) { buf.append(" and ag_agenda.codcpi='" + codCpi + "'"); } // else ==4 cittadino, ==5
		 * azienda ....
		 */
		buf.append(" and codcpicontatto='" + codCpi + "'");

		if (fmiei != null && fmiei.equals("1")) {
			buf.append("and (ts_utente.CDNUT = " + cdnUt + ") ");
		}
		if (foperatore != null && !foperatore.equals("")) {
			buf.append(" and (ag_contatto.PRGSPICONTATTO= " + foperatore + ") ");
		}

		// Costruisco la query
		// 1. Contatti con solo i lavoratori o per i quali non sono stati
		// annotati nè lavoratori nè aziende
		query_totale.append(buf.toString());
		String notAzienda = " and (ag_contatto.PRGAZIENDA is null and ag_contatto.PRGUNITA is null) ";
		query_totale.append(notAzienda);

		// 2. Contatti per le Aziende
		query_totale.append(" UNION ");
		String notLavoratori = " and (ag_contatto.CDNLAVORATORE is null and ag_contatto.PRGAZIENDA is not null and ag_contatto.PRGUNITA is not null) ";

		query_totale.append(SELECT_SQL_UNION);
		query_totale.append(buf.toString());
		query_totale.append(notLavoratori);

		// Debug
		_logger.debug("sil.module.agenda.cpi_app_contatti" + "::Stringa di ricerca:" + query_totale.toString());

		return query_totale.toString();

	}
}