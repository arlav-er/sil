package it.eng.sil.module.agenda;

import java.util.Calendar;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.security.User;

/*
 * Questa classe restituisce il sommario giornaliero degli slot 
 * relativi ad un CpI.
 * 
 * @author: Stefania Orioli
 * 
 */

public class cpi_slot_giorno implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(cpi_slot_giorno.class.getName());

	public cpi_slot_giorno() {
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
		String fmiei = (String) sessionContainer.getAttribute("slot_FMiei");
		String foperatore = (String) sessionContainer.getAttribute("slot_FOperatore");
		String fservizio = (String) sessionContainer.getAttribute("slot_FServizio");
		String faula = (String) sessionContainer.getAttribute("slot_FAula");
		if (cdnTipoGruppo == 1) {
			codCpi = user.getCodRif();
		} else {
			codCpi = req.getAttribute("agenda_codCpi").toString();
		}

		String giorno = (String) req.getAttribute("giornoDB");
		String mese = (String) req.getAttribute("meseDB");
		String anno = (String) req.getAttribute("annoDB");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if (fmiei != null && fmiei.equals("1")) {
			buf.append(" left join ts_utente on (ag_spi_slot.PRGSPI=ts_utente.PRGSPI) ");
		}

		buf.append(" WHERE ");

		// Data Odierna
		Calendar oggi = Calendar.getInstance();
		int oggi_gg = oggi.get(5);
		int oggi_mm = oggi.get(2);
		int oggi_aa = oggi.get(1);

		if ((giorno != null) && (!giorno.equals(""))) {
			buf.append(" to_number(to_char(ag_slot.dtmdataora, 'dd'))=" + giorno + " ");
		} else {
			buf.append(" to_number(to_char(ag_slot.dtmdataora, 'dd'))=" + oggi_gg + " ");
		}

		buf.append(" and ");

		if ((mese != null) && (!mese.equals(""))) {
			buf.append(" to_number(to_char(ag_slot.dtmdataora, 'mm'))=" + mese + " ");
		} else {
			buf.append(" to_number(to_char(ag_slot.dtmdataora, 'mm'))=" + (oggi_mm + 1) + " ");
		}

		buf.append(" and ");

		if ((anno != null) && (!anno.equals(""))) {
			buf.append(" to_number(to_char(ag_slot.dtmdataora, 'yyyy'))=" + anno + " ");
		} else {
			buf.append(" to_number(to_char(ag_slot.dtmdataora, 'yyyy'))=" + oggi_aa + " ");
		}

		/* FILTRI */
		// if (cdnTipoGruppo==1) {
		buf.append(" and ag_slot.codcpi='" + codCpi + "'");
		// } // else ==4 cittadino, ==5 azienda ....

		if (fmiei != null && fmiei.equals("1")) {
			buf.append("and (ts_utente.CDNUT = " + cdnUt + ") ");
		}
		if (foperatore != null && !foperatore.equals("")) {
			buf.append(" and (ag_spi_slot.PRGSPI= " + foperatore + ") ");
		}
		if (fservizio != null && !fservizio.equals("")) {
			buf.append(" and (ag_slot.CODSERVIZIO = '" + fservizio + "') ");
		}
		if (faula != null && !faula.equals("")) {
			buf.append(" and (ag_slot.PRGAMBIENTE = '" + faula + "') ");
		}
		// Costruisco la query
		query_totale.append(buf.toString());
		query_totale.append(" order by 1 ");

		// Debug
		_logger.debug("sil.module.agenda.cpi_slot_giorno" + "::Stringa di ricerca:" + query_totale.toString());

		return query_totale.toString();

	}

}