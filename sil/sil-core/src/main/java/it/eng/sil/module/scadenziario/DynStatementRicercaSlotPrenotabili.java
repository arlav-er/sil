package it.eng.sil.module.scadenziario;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.security.User;

/**
 * Effettua la ricerca dinamica sullo scadenziario
 * 
 * @author Giovanni Landi
 * 
 */
public class DynStatementRicercaSlotPrenotabili implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE = " select ag_slot.PRGSLOT, "
			+ " to_char(ag_slot.DTMDATAORA,'dd/mm/yyyy') as DATA, "
			+ " to_char(ag_slot.DTMDATAORA,'hh24:mi')  as ORARIO, " + " ag_slot.NUMMINUTI, " + " ag_slot.CODSERVIZIO, "
			+ " de_servizio.STRDESCRIZIONE as SERVIZIO, "
			+ " concat(concat(an_spi.strNome,' '),an_spi.strCognome) as OPERATORE, "
			+ "(select nvl( (select to_char(ts_config_loc.num)  from ts_config_loc  "
			+ " where strcodrif=(select ts_generale.codprovinciasil from ts_generale) and codtipoconfig='UMBNGEAZ') , 0) "
			+ " from dual) choiceLabel " + " FROM ag_slot, " + " ag_spi_slot, " + " an_spi,   " + " de_servizio, "
			+ " de_stato_slot " + " WHERE " + " ag_slot.CODSTATOSLOT = de_stato_slot.CODSTATOSLOT "
			+ " AND ag_slot.CODSERVIZIO = de_servizio.CODSERVIZIO " + " AND UPPER(de_stato_slot.FLGPRENOTABILE) = 'S' "
			+ " AND ag_spi_slot.PRGSLOT = ag_slot.PRGSLOT " + " AND ag_spi_slot.CODCPI = ag_slot.CODCPI "
			+ " AND ag_spi_slot.PRGSPI = an_spi.PRGSPI" + " AND de_servizio.datinizioval <= SYSDATE"
			+ " AND de_servizio.datfineval >= SYSDATE" + " AND an_spi.datinizioval <= SYSDATE"
			+ " AND an_spi.datfineval >= SYSDATE";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		User user = (User) requestContainer.getSessionContainer().getAttribute(User.USERID);
		String tipoGruppo = user.getCodTipo();
		SourceBean req = requestContainer.getServiceRequest();
		String codCpi = req.containsAttribute("CODCPI") ? req.getAttribute("CODCPI").toString() : "";
		String codServizio = req.containsAttribute("CODSERVIZIO") ? req.getAttribute("CODSERVIZIO").toString() : "";
		String cdnLavoratore = req.containsAttribute("CDNLAVORATORE") ? req.getAttribute("CDNLAVORATORE").toString()
				: "";
		String prgOperatore = req.containsAttribute("PRGSPI") ? req.getAttribute("PRGSPI").toString() : "";
		String prgAzienda = req.containsAttribute("PRGAZIENDA") ? req.getAttribute("PRGAZIENDA").toString() : "";
		String prgUnita = req.containsAttribute("PRGUNITA") ? req.getAttribute("PRGUNITA").toString() : "";
		String dataDalSlot = req.containsAttribute("dataDalSlot") ? ((String) req.getAttribute("dataDalSlot")) : "";

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if ("".compareTo(cdnLavoratore) != 0) {
			buf.append(" AND nvl(ag_slot.NUMLAVORATORI,0) - nvl(ag_slot.NUMLAVPRENOTATI,0) > 0");
		} else {
			if (("".compareTo(prgAzienda) != 0) && ("".compareTo(prgUnita) != 0)) {
				buf.append(" AND nvl(ag_slot.NUMAZIENDE,0) - nvl(ag_slot.NUMAZIENDEPRENOTATE,0) > 0");
			}
		}

		if ("".equals(codCpi)) {
			codCpi = user.getCodRif();
		}
		buf.append(" AND ag_slot.CODCPI = '" + codCpi + "'");

		if ("".compareTo(codServizio) != 0) {
			buf.append(" AND AG_SLOT.CODSERVIZIO = '" + codServizio + "'");
		}

		if ("".compareTo(prgOperatore) != 0) {
			buf.append(" AND an_spi.PRGSPI = " + prgOperatore);
		}

		if (!"".equals(dataDalSlot)) {
			buf.append(" and trunc(DTMDATAORA) >= to_date('" + dataDalSlot + "','dd/mm/yyyy') ");
		} else {
			buf.append(" and trunc(DTMDATAORA) >= trunc(sysdate) ");
		}
		if (!"".equals(prgOperatore)) {
			buf.append(" AND an_spi.PRGSPI = " + prgOperatore);
		}

		if (tipoGruppo.equalsIgnoreCase(MessageCodes.Login.COD_TIPO_GRUPPO_PATRONATO)) {
			buf.append(
					" and de_servizio.codservizio in (SELECT ds.codservizio FROM de_servizio ds WHERE nvl(ds.flgPatronato, 'N') = 'S') ");
		}

		buf.append(" order by DTMDATAORA");

		query_totale.append(buf.toString());
		return query_totale.toString();
	}
}