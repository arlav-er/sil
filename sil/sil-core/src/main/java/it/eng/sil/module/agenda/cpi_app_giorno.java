package it.eng.sil.module.agenda;

import java.util.Calendar;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.security.User;

/*
 * Questa classe restituisce il sommario giornaliero degli appuntamenti 
 * relativi ad un CpI (ristretto agli appuntamenti personali se si tratta
 * di un lavoratore oppure di una azienda).
 * 
 * @author: Stefania Orioli
 * 
 */

public class cpi_app_giorno implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(cpi_app_giorno.class.getName());

	public cpi_app_giorno() {
	}

	private static final String SELECT_SQL_BASE = "select " + "ag_agenda.dtmdataora as data_ora, "
			+ "to_char(dtmdataora, 'dd/mm/yyyy') as data_cod, " + "to_char(dtmdataora, 'dd/mm/yyyy') as data, "
			+ "to_char(ag_agenda.dtmdataora, 'hh24:mi')  as orario, " + "ag_agenda.numminuti, "
			+ "de_servizio.strDescrizione as servizio, " + "de_servizio.codServizio as codServizio, "
			+ "(Initcap(an_spi.strNome) || ' ' || Initcap(an_spi.strCognome)) as operatore, " + "0 as NRO_OPE, "
			+ "ag_agenda.prgappuntamento, " + "ag_agenda.codcpi, " + "NVL(ag_agenda.prgSpi, 0) AS PRGSPI, "
			+ "NVL(ag_agenda.prgSpiEff, 0) AS PRGSPIEFF, " + "decode(ag_agenda.stridcoap, null,'1','0') as STRIDCOAP, "
			+ "to_char(PG_AGENDA.pdAppErr(ag_agenda.CODCPI, ag_agenda.PRGAPPUNTAMENTO))  as APP_ERR, " + "1 as Tipo, "
			+ "(select count(*) from ag_lavoratore where ag_lavoratore.PRGAPPUNTAMENTO=ag_agenda.PRGAPPUNTAMENTO and ag_lavoratore.CODCPI=ag_agenda.CODCPI) as nro, "
			+ "(PG_AGENDA.PDFIRSTLAVAPP(ag_agenda.CODCPI, ag_agenda.PRGAPPUNTAMENTO)) as Lav_AZ, "
			+ "null as PRGSLOT, de_stato_appuntamento.STRDESCRIZIONE as Stato, " + "'SELECT_AGENDA_PAGE' as PAGE "
			+ "FROM ag_agenda " + " LEFT JOIN de_servizio ON (ag_agenda.codservizio = de_servizio.codservizio) "
			+ "LEFT JOIN an_spi ON (ag_agenda.prgSpi = an_spi.prgSpi) "
			+ "left join de_stato_appuntamento on (ag_agenda.CODSTATOAPPUNTAMENTO=de_stato_appuntamento.CODSTATOAPPUNTAMENTO) ";

	private static final String SELECT_SQL_UNION = "select " + "ag_agenda.dtmdataora as data_ora, "
			+ "to_char(ag_agenda.dtmdataora, 'dd/mm/yyyy') as data_cod, "
			+ "to_char(ag_agenda.dtmdataora, 'dd/mm/yyyy') as data, "
			+ "to_char(ag_agenda.dtmdataora, 'hh24:mi')  as orario, " + "ag_agenda.numminuti, "
			+ "de_servizio.strDescrizione as servizio, " + "de_servizio.codServizio as codServizio, "
			+ "(Initcap(an_spi.strNome) || ' ' || Initcap(an_spi.strCognome)) as operatore, " + "0 as NRO_OPE, "
			+ "ag_agenda.prgappuntamento, " + "ag_agenda.codcpi, " + "NVL(ag_agenda.prgSpi,0) AS PRGSPI, "
			+ "NVL(ag_agenda.prgSpiEff,0) AS PRGSPIEFF, " + "decode(ag_agenda.stridcoap, null,'1','0') as STRIDCOAP, "
			+ "to_char(PG_AGENDA.pdAppErr(ag_agenda.CODCPI, ag_agenda.PRGAPPUNTAMENTO))  as APP_ERR, " + "2 as Tipo, "
			+ "-1 as nro, " + "an_azienda.STRRAGIONESOCIALE as Lav_Az "
			+ ", null as PRGSLOT, de_stato_appuntamento.STRDESCRIZIONE as Stato," + "'SELECT_AGENDA_PAGE' as PAGE "
			+ "FROM ag_agenda " + "LEFT JOIN de_servizio ON (ag_agenda.codservizio = de_servizio.codservizio) "
			+ "LEFT JOIN an_spi ON (ag_agenda.prgSpi = an_spi.prgSpi) "
			+ "INNER JOIN an_azienda ON (ag_agenda.PRGAZIENDA=an_azienda.PRGAZIENDA) "
			+ "left join de_stato_appuntamento on (ag_agenda.CODSTATOAPPUNTAMENTO=de_stato_appuntamento.CODSTATOAPPUNTAMENTO) ";

	private static final String SELECT_SQL_UNION3 = "select " + "ag_agenda.dtmdataora as data_ora, "
			+ "to_char(ag_agenda.dtmdataora, 'dd/mm/yyyy') as data_cod, "
			+ "to_char(ag_agenda.dtmdataora, 'dd/mm/yyyy') as data, "
			+ "to_char(ag_agenda.dtmdataora, 'hh24:mi')  as orario, "
			+ "ag_agenda.numminuti, de_servizio.strDescrizione as servizio, "
			+ "de_servizio.codServizio as codServizio, "
			+ "(Initcap(an_spi.strNome) ||' ' || Initcap(an_spi.strCognome)) as operatore, " + "0 as NRO_OPE, "
			+ "ag_agenda.prgappuntamento, " + "ag_agenda.codcpi, " + "NVL(ag_agenda.prgSpi,0) AS PRGSPI, "
			+ "NVL(ag_agenda.prgSpiEff,0) AS PRGSPIEFF, " + "decode(ag_agenda.stridcoap, null,'1','0') as STRIDCOAP, "
			+ "to_char(PG_AGENDA.pdAppErr(ag_agenda.CODCPI, ag_agenda.PRGAPPUNTAMENTO))  as APP_ERR, " + "3 as Tipo, "
			+ "(select count(*) from ag_lavoratore where ag_lavoratore.PRGAPPUNTAMENTO=ag_agenda.PRGAPPUNTAMENTO and ag_lavoratore.CODCPI=ag_agenda.CODCPI) as nro, "
			+ "an_azienda.STRRAGIONESOCIALE as Lav_Az "
			+ ", null as PRGSLOT, de_stato_appuntamento.STRDESCRIZIONE as Stato, 'SELECT_AGENDA_PAGE' as PAGE "
			+ "FROM ag_agenda " + "LEFT JOIN de_servizio ON (ag_agenda.codservizio = de_servizio.codservizio) "
			+ "LEFT JOIN an_spi ON (ag_agenda.prgSpi = an_spi.prgSpi) "
			+ "INNER JOIN an_azienda ON (ag_agenda.PRGAZIENDA=an_azienda.PRGAZIENDA)"
			+ "left join de_stato_appuntamento on (ag_agenda.CODSTATOAPPUNTAMENTO=de_stato_appuntamento.CODSTATOAPPUNTAMENTO) ";

	private static final String SELECT_SLOT = "select " + "ag_slot.dtmdataora as data_ora, "
			+ "to_char(dtmdataora, 'dd/mm/yyyy') as data_cod, " + "to_char(dtmdataora, 'dd/mm/yyyy') as data, "
			+ "to_char(ag_slot.dtmdataora, 'hh24:mi')  as orario, " + "ag_slot.numminuti, "
			+ "de_servizio.strDescrizione as servizio,  " + "de_servizio.codServizio as codServizio, "
			+ "FIRST_VALUE(concat(concat(Initcap(an_spi.strNome),' '),Initcap(an_spi.strCognome))) OVER (PARTITION BY ag_slot.PRGSLOT, ag_slot.CODCPI ORDER BY an_spi.strCognome, an_spi.strNome) as Operatore, "
			+ "(select count(*) from ag_spi_slot where ag_spi_slot.PRGSLOT=ag_slot.PRGSLOT and ag_spi_slot.CODCPI=ag_slot.CODCPI) as nro_ope, "
			+ "ag_slot.prgAppuntamento, " + "ag_slot.codCpi,  " + "NVL(ag_spi_slot.prgSpi,0) AS PRGSPI, "
			+ "0 AS PRGSPIEFF, " + "'0' AS STRIDCOAP, " + "'-1' as APP_ERR, " + "4 as Tipo, " + "1 as nro, "
			+ "'?' as Lav_az, " + "ag_slot.prgSlot, "
			+ "decode(de_stato_slot.CODSTATOSLOT,'C','Prenotabile',de_stato_slot.STRDESCRIZIONE) as Stato, "
			+ "'NuovoAppSlotPage' as PAGE  " + "FROM ag_slot "
			+ "LEFT JOIN de_servizio ON (ag_slot.codservizio = de_servizio.codservizio) "
			+ "LEFT JOIN AG_SPI_SLOT ON (ag_slot.PRGSLOT=ag_spi_slot.PRGSLOT and ag_slot.CODCPI=ag_spi_slot.CODCPI) "
			+ "LEFT JOIN an_spi ON (ag_spi_slot.prgSpi = an_spi.prgSpi) "
			+ "LEFT JOIN de_stato_slot on (ag_slot.CODSTATOSLOT=de_stato_slot.CODSTATOSLOT)  ";

	private static final String SELECT_SLOT_P = "select " + "ag_slot.dtmdataora as data_ora, "
			+ "to_char(dtmdataora, 'dd/mm/yyyy') as data_cod, " + "to_char(dtmdataora, 'dd/mm/yyyy') as data, "
			+ "to_char(ag_slot.dtmdataora, 'hh24:mi')  as orario, " + "ag_slot.numminuti, "
			+ "de_servizio.strDescrizione as servizio,  " + "de_servizio.codServizio as codServizio, "
			+ "FIRST_VALUE(concat(concat(Initcap(an_spi.strNome),' '),Initcap(an_spi.strCognome))) OVER (PARTITION BY ag_slot.PRGSLOT, ag_slot.CODCPI ORDER BY an_spi.strCognome, an_spi.strNome) as Operatore, "
			+ "(select count(*) from ag_spi_slot where ag_spi_slot.PRGSLOT=ag_slot.PRGSLOT and ag_spi_slot.CODCPI=ag_slot.CODCPI) as nro_ope, "
			+ "ag_slot.prgAppuntamento, " + "ag_slot.codCpi,  " + "NVL(ag_slot.prgSpi,0) AS PRGSPI, "
			+ "0 AS PRGSPIEFF, " + "'0' AS STRIDCOAP, " + "'-1' as APP_ERR, " + "4 as Tipo, " + "1 as nro, "
			+ "'?' as Lav_az, " + "ag_slot.prgSlot, "
			+ "decode(de_stato_slot.CODSTATOSLOT,'C','Prenotabile',de_stato_slot.STRDESCRIZIONE) as Stato, "
			+ "'SELECT_AGENDA_PAGE' as PAGE  " + "FROM ag_slot "
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
		String fmiei = (String) sessionContainer.getAttribute("agenda_FMiei");
		String foperatore = (String) sessionContainer.getAttribute("agenda_FOperatore");
		String fservizio = (String) sessionContainer.getAttribute("agenda_FServizio");
		String faula = (String) sessionContainer.getAttribute("agenda_FAula");
		String fattivi = (String) sessionContainer.getAttribute("agenda_FAttivi");
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
		StringBuffer bslot = new StringBuffer();

		if (fmiei != null && fmiei.equals("1")) {
			buf.append(" left join ts_utente on (ag_agenda.PRGSPI=ts_utente.PRGSPI) ");
			bslot.append(" left join ts_utente on (ag_spi_slot.PRGSPI=ts_utente.PRGSPI) ");
		}

		buf.append(" WHERE ");
		bslot.append(" WHERE ");

		// Data Odierna
		Calendar oggi = Calendar.getInstance();
		int oggi_gg = oggi.get(5);
		int oggi_mm = oggi.get(2);
		int oggi_aa = oggi.get(1);

		if ((giorno != null) && (!giorno.equals(""))) {
			buf.append(" to_number(to_char(ag_agenda.dtmdataora, 'dd'))=" + giorno + " ");
			bslot.append(" to_number(to_char(ag_slot.dtmdataora, 'dd'))=" + giorno + " ");
		} else {
			buf.append(" to_number(to_char(ag_agenda.dtmdataora, 'dd'))=" + oggi_gg + " ");
			bslot.append(" to_number(to_char(ag_slot.dtmdataora, 'dd'))=" + oggi_gg + " ");
		}

		buf.append(" and ");
		bslot.append(" and ");

		if ((mese != null) && (!mese.equals(""))) {
			buf.append(" to_number(to_char(ag_agenda.dtmdataora, 'mm'))=" + mese + " ");
			bslot.append(" to_number(to_char(ag_slot.dtmdataora, 'mm'))=" + mese + " ");
		} else {
			buf.append(" to_number(to_char(ag_agenda.dtmdataora, 'mm'))=" + (oggi_mm + 1) + " ");
			bslot.append(" to_number(to_char(ag_slot.dtmdataora, 'mm'))=" + (oggi_mm + 1) + " ");
		}

		buf.append(" and ");
		bslot.append(" and ");

		if ((anno != null) && (!anno.equals(""))) {
			buf.append(" to_number(to_char(ag_agenda.dtmdataora, 'yyyy'))=" + anno + " ");
			bslot.append(" to_number(to_char(ag_slot.dtmdataora, 'yyyy'))=" + anno + " ");
		} else {
			buf.append(" to_number(to_char(ag_agenda.dtmdataora, 'yyyy'))=" + oggi_aa + " ");
			bslot.append(" to_number(to_char(ag_slot.dtmdataora, 'yyyy'))=" + oggi_aa + " ");
		}

		/* FILTRI */
		// if (cdnTipoGruppo==1) {
		buf.append(" and ag_agenda.codcpi='" + codCpi + "'");
		bslot.append(" and ag_slot.codcpi='" + codCpi + "'");
		// } // else ==4 cittadino, ==5 azienda ....

		if (fmiei != null && fmiei.equals("1")) {
			buf.append("and (ts_utente.CDNUT = " + cdnUt + ") ");
			bslot.append("and (ts_utente.CDNUT = " + cdnUt + ") ");
		}
		if (foperatore != null && !foperatore.equals("")) {
			buf.append(" and (ag_agenda.PRGSPI= " + foperatore + ") ");
			bslot.append(" and (ag_spi_slot.PRGSPI= " + foperatore + ") ");
		}
		if (fservizio != null && !fservizio.equals("")) {
			buf.append(" and (ag_agenda.CODSERVIZIO = '" + fservizio + "') ");
			bslot.append(" and (ag_slot.CODSERVIZIO = '" + fservizio + "') ");
		}
		if (faula != null && !faula.equals("")) {
			buf.append(" and (ag_agenda.PRGAMBIENTE = '" + faula + "') ");
			bslot.append(" and (ag_slot.PRGAMBIENTE = '" + faula + "') ");
		}
		if (fattivi != null && !fattivi.equals("")) {
			buf.append(" and (de_stato_appuntamento.FLGATTIVO = 'S') ");
		}

		// Seleziono gli slot prenotabili effettivamente
		bslot.append(" and de_stato_slot.FLGPRENOTABILE='S' ");
		bslot.append(
				"and ( ( (nvl(ag_slot.NUMAZIENDE,0)+nvl(ag_slot.NUMLAVORATORI,0)) - ( nvl(ag_slot.NUMAZIENDEPRENOTATE,0) + nvl(ag_slot.NUMLAVPRENOTATI,0)) )  >0) ");

		if (cdnTipoGruppo == 4 || cdnTipoGruppo == 5) { // 4 cittadino, 5
														// azienda
			bslot.append(" and ag_slot.FLGPUBBLICO='S' ");
		}

		// Costruisco la query
		// 1. Appuntamenti con solo i lavoratori o per i quali non sono stati
		// prenotati nè lavoratori nè aziende
		query_totale.append(buf.toString());
		String notAzienda = " and (ag_agenda.PRGAZIENDA is null and ag_agenda.PRGUNITA is null) ";
		query_totale.append(notAzienda);

		// 2. Appuntamenti con prenotazione per una Azienda e nessun lavoratore
		query_totale.append(" UNION ALL ");
		String notLavoratori = " and NOT EXISTS (" + "select * from ag_agenda a1 "
				+ "inner join ag_lavoratore on (a1.PRGAPPUNTAMENTO=ag_lavoratore.PRGAPPUNTAMENTO and a1.CODCPI=ag_lavoratore.CODCPI) "
				+ "where a1.CODCPI = ag_agenda.CODCPI and a1.PRGAPPUNTAMENTO = ag_agenda.PRGAPPUNTAMENTO " + ") ";

		query_totale.append(SELECT_SQL_UNION);
		query_totale.append(buf.toString());
		query_totale.append(notLavoratori);

		// 3. Appuntamenti con prenotazione per una Azienda e uno o più
		// Lavoratori
		query_totale.append(" UNION ALL ");
		String andLavoratori = " and EXISTS (" + "select * from ag_agenda a1 "
				+ "inner join ag_lavoratore on (a1.PRGAPPUNTAMENTO=ag_lavoratore.PRGAPPUNTAMENTO and a1.CODCPI=ag_lavoratore.CODCPI) "
				+ "where a1.CODCPI = ag_agenda.CODCPI and a1.PRGAPPUNTAMENTO = ag_agenda.PRGAPPUNTAMENTO " + ") ";

		query_totale.append(SELECT_SQL_UNION3);
		query_totale.append(buf.toString());
		query_totale.append(andLavoratori);

		// SLOT DISPONIBILI PER APPUNTAMENTI
		query_totale.append(" UNION ALL ");
		// Slot Completamente Liberi
		query_totale.append(SELECT_SLOT);
		query_totale.append(bslot.toString());
		// query_totale.append(" and ag_slot.PRGAPPUNTAMENTO is null");
		// Slot Parzialmente Prenotati
		/*
		 * query_totale.append(" UNION "); query_totale.append(SELECT_SLOT_P); query_totale.append(bslot.toString());
		 * query_totale.append(" and ag_slot.PRGAPPUNTAMENTO is not null ");
		 */

		query_totale.append(" order by 1 ");

		// Debug
		_logger.debug("sil.module.agenda.cpi_app_giorno" + "::Stringa di ricerca:" + query_totale.toString());

		return query_totale.toString();

	}

}