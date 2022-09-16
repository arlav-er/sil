/*
 * Created on Aug 17, 2006
 */
package it.eng.sil.module.anag;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.util.Utils;

/**
 * @author savino
 */
public class DynRicercaInfoDaPresaAtto implements IDynamicStatementProvider {
	private final static String STM = "SELECT prgInfoTrasferimento, cdnlavoratore, strcodicefiscale, strcognome, strnome, "
			+ "       to_char(dattrasferimento, 'dd/mm/yyyy') AS dattrasferimento, "
			+ "		de_cpi.strdescrizione AS cpi, "
			+ "       (case when CODCMTIPOISCR68_1 is null and CODCMTIPOISCR68_2 is null then 'N' else 'S' end)  as iscritto_cm "
			+ "  FROM ca_info_trasferimento, de_cpi " + " WHERE ca_info_trasferimento.codcpimitt = de_cpi.codcpi ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean serviceRequest = requestContainer.getServiceRequest();

		String tipoRicerca = (String) serviceRequest.getAttribute("TIPORICERCA");
		String cdnLavoratore = (String) serviceRequest.getAttribute("CDNLAVORATORE");
		// se e' presente il cdnLavoratore allora provengo dal menu contestuale
		// del lavoratore
		String codiceFiscale = Utils.notNull(serviceRequest.getAttribute("STRCODICEFISCALE"));
		String cognome = Utils.notNull(serviceRequest.getAttribute("STRCOGNOME"));
		String nome = Utils.notNull(serviceRequest.getAttribute("STRNOME"));
		String dataTrasferimentoDa = (String) serviceRequest.getAttribute("DATTRASFERIMENTO_DA");
		String dataTrasferimentoA = (String) serviceRequest.getAttribute("DATTRASFERIMENTO_A");
		String codCpiComp = (String) serviceRequest.getAttribute("CODCPICOMP");

		StringBuffer query = new StringBuffer(STM);
		if (cdnLavoratore == null) {
			String prefissoRicerca = " like upper('";
			String suffissoRicerca = "%')";
			if ("esatta".equals(tipoRicerca)) {
				prefissoRicerca = " = upper('";
				suffissoRicerca = "')";
			}
			if (!"".equals(codiceFiscale)) {
				query.append(" AND ca_info_trasferimento.STRCODICEFISCALE ");
				query.append(prefissoRicerca);
				query.append(codiceFiscale);
				query.append(suffissoRicerca);
			}
			if (!"".equals(cognome)) {
				query.append(" AND ca_info_trasferimento.STRCOGNOME ");
				query.append(prefissoRicerca);
				query.append(cognome);
				query.append(suffissoRicerca);
			}
			if (!"".equals(nome)) {
				query.append(" AND ca_info_trasferimento.STRNOME ");
				query.append(prefissoRicerca);
				query.append(nome);
				query.append(suffissoRicerca);
			}

		}
		if (cdnLavoratore != null) {
			query.append(" AND ca_info_trasferimento.CDNLAVORATORE = ");
			query.append(cdnLavoratore);
		}
		if (!"".equals(codCpiComp)) {
			query.append(" AND CODCPIMITT = '");
			query.append(codCpiComp);
			query.append("'");
		}
		if (!"".equals(dataTrasferimentoDa) && !"".equals(dataTrasferimentoA)) {
			query.append(" AND trunc(dtmins) between to_date('");
			query.append(dataTrasferimentoDa);
			query.append("', 'dd/mm/yyyy') and to_date('");
			query.append(dataTrasferimentoA);
			query.append("', 'dd/mm/yyyy')");
		} else {
			if (!"".equals(dataTrasferimentoDa)) {
				query.append(" AND trunc(dtmins) >= to_date('");
				query.append(dataTrasferimentoDa);
				query.append("', 'dd/mm/yyyy')");
			}
			if (!"".equals(dataTrasferimentoA)) {
				query.append(" AND trunc(dtmins) <= to_date('");
				query.append(dataTrasferimentoA);
				query.append("', 'dd/mm/yyyy')");
			}
		}
		query.append(" ORDER BY ca_info_trasferimento.dattrasferimento desc, strcognome, strnome ");
		return query.toString();
	}

}
