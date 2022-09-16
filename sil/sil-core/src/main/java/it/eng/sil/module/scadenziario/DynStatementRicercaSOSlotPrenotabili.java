package it.eng.sil.module.scadenziario;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.security.User;

public class DynStatementRicercaSOSlotPrenotabili implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE = " select ag_slot.PRGSLOT, "
			+ "to_char(ag_slot.DTMDATAORA,'dd/mm/yyyy') as DATA, "
			+ "to_char(ag_slot.DTMDATAORA,'hh24:mi')  as ORARIO, " + "ag_slot.NUMMINUTI, " + "ag_slot.CODSERVIZIO, "
			+ "de_servizio.STRDESCRIZIONE as SERVIZIO, "
			+ "concat(concat(an_spi.strNome,' '),an_spi.strCognome) as OPERATORE, " + "ag_slot.CODCPI "
			+ "FROM an_spi, ag_spi_slot, ag_slot "
			+ "LEFT JOIN de_servizio ON (ag_slot.CODSERVIZIO = de_servizio.CODSERVIZIO) "
			+ "LEFT JOIN de_stato_slot ON (ag_slot.CODSTATOSLOT = de_stato_slot.CODSTATOSLOT) "
			+ "WHERE UPPER(de_stato_slot.FLGPRENOTABILE) = 'S' AND " + "UPPER(ag_slot.FLGPUBBLICO) = 'S' AND "
			+ "ag_spi_slot.PRGSLOT = ag_slot.PRGSLOT AND ag_spi_slot.CODCPI = ag_slot.CODCPI AND "
			+ "ag_spi_slot.PRGSPI = an_spi.PRGSPI";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		User user = (User) requestContainer.getSessionContainer().getAttribute(User.USERID);

		SourceBean req = requestContainer.getServiceRequest();
		String codCpi = req.containsAttribute("CODCPI") ? req.getAttribute("CODCPI").toString() : "";
		String codServizio = req.containsAttribute("CODSERVIZIO") ? req.getAttribute("CODSERVIZIO").toString() : "";
		String cdnLavoratore = req.containsAttribute("CDNLAVORATORE") ? req.getAttribute("CDNLAVORATORE").toString()
				: "";
		String prgAzienda = req.containsAttribute("PRGAZIENDA") ? req.getAttribute("PRGAZIENDA").toString() : "";
		String prgUnita = req.containsAttribute("PRGUNITA") ? req.getAttribute("PRGUNITA").toString() : "";
		String dataDalSlot = req.containsAttribute("dataDalSlot") ? ((String) req.getAttribute("dataDalSlot")) : "";

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if (cdnLavoratore.compareTo("") != 0) {
			buf.append(" AND nvl(ag_slot.NUMLAVORATORI,0) - nvl(ag_slot.NUMLAVPRENOTATI,0) > 0");
		} else {
			if ((prgAzienda.compareTo("") != 0) && (prgUnita.compareTo("") != 0)) {
				buf.append(" AND nvl(ag_slot.NUMAZIENDE,0) - nvl(ag_slot.NUMAZIENDEPRENOTATE,0) > 0");
			}
		}

		if (codCpi.equals("")) {
			if (cdnLavoratore.compareTo("") != 0) {
				buf.append(" AND ag_slot.CODCPI = (select distinct de_cpi.codcpi as codice "
						+ " FROM de_cpi, de_comune, de_provincia,an_lav_storia_inf "
						+ " WHERE de_cpi.codcpi = de_comune.codcpi "
						+ " and de_comune.codprovincia = de_provincia.codprovincia "
						+ " and an_lav_storia_inf.codcpitit = de_cpi.codcpi "
						+ " and de_provincia.codregione = (select de_provincia.CODREGIONE " + "	from   de_provincia "
						+ "	inner join ts_generale on (de_provincia.codprovincia = ts_generale.CODPROVINCIASIL)) "
						+ " and an_lav_storia_inf.datfine is null ");
				buf.append(" AND an_lav_storia_inf.cdnlavoratore = '" + cdnLavoratore + "')");
			}
			if ((prgAzienda.compareTo("") != 0) && (prgUnita.compareTo("") != 0)) {
				buf.append(" AND ag_slot.CODCPI = (select distinct de_cpi.codcpi as codice "
						+ " FROM de_cpi, de_comune, de_provincia,an_unita_azienda"
						+ " WHERE de_cpi.codcpi = de_comune.codcpi "
						+ " and de_comune.codprovincia = de_provincia.codprovincia "
						+ " and de_provincia.codregione = (select de_provincia.CODREGIONE " + "	from   de_provincia "
						+ "	inner join ts_generale on (de_provincia.codprovincia = ts_generale.CODPROVINCIASIL)) "
						+ " and an_unita_azienda.codcom = de_comune.codcom ");
				buf.append(" AND an_unita_azienda.prgAzienda = '" + prgAzienda + "'");
				buf.append(" AND an_unita_azienda.prgUnita = '" + prgUnita + "')");
			}
		} else {
			buf.append(" AND ag_slot.CODCPI = '" + codCpi + "'");
		}
		if (codServizio.compareTo("") != 0) {
			buf.append(" AND AG_SLOT.CODSERVIZIO = '" + codServizio + "'");
		}

		if (!dataDalSlot.equals("")) {
			buf.append(" AND ag_slot.DTMDATAORA is not null and trunc(DTMDATAORA) >= to_date('" + dataDalSlot
					+ "','dd/mm/yyyy') ");
		} else {
			buf.append(" AND ag_slot.DTMDATAORA is not null and trunc(DTMDATAORA) >= trunc(sysdate) ");
		}

		buf.append(" order by DTMDATAORA");

		query_totale.append(buf.toString());
		return query_totale.toString();
	}
}
