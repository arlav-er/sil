/*
 * Created on Aug 11, 2006
 */
package it.eng.sil.module.anag;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.util.Utils;

/**
 * @author savino
 */
public class DynRicercaRichiestePresaAtto implements IDynamicStatementProvider {

	private final static String STM = "SELECT ca_presa_atto.prgpresaatto, ca_presa_atto.cdnlavoratore, ca_presa_atto.strcodicefiscale, "
			+ "       ca_presa_atto.strcognome, ca_presa_atto.strnome, "
			+ "       TO_CHAR (ca_presa_atto.dattrasferimento, 'dd/mm/yyyy') AS dattrasferimento, de_cpi.strdescrizione AS cpi, "
			+ "		de_stato_presa_atto.strdescrizione AS statopresaatto, "
			+ "		decode(ca_presa_atto.codstatopresaatto, 'AT', '1','0') as attiva ";

	// " FROM ca_presa_atto, de_stato_presa_atto, de_cpi "+
	// " WHERE ca_presa_atto.codstatopresaatto = de_stato_presa_atto.codstatopresaatto "+
	// " AND ca_presa_atto.codcpirich = de_cpi.codcpi";
	/** 
	 * 
	 */
	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean serviceRequest = requestContainer.getServiceRequest();

		String tipoRicerca = (String) serviceRequest.getAttribute("TIPORICERCA");
		String cdnLavoratore = (String) serviceRequest.getAttribute("CDNLAVORATORE");
		// se e' presente il cdnLavoratore allora provengo dal menu contestuale del lavoratore
		String codiceFiscale = Utils.notNull(serviceRequest.getAttribute("STRCODICEFISCALE"));
		String cognome = Utils.notNull(serviceRequest.getAttribute("STRCOGNOME"));
		String nome = Utils.notNull(serviceRequest.getAttribute("STRNOME"));
		String codStatoPresaAtto = (String) serviceRequest.getAttribute("CODSTATOPRESAATTO");
		String dataTrasferimentoDa = (String) serviceRequest.getAttribute("DATTRASFERIMENTO_DA");
		String dataTrasferimentoA = (String) serviceRequest.getAttribute("DATTRASFERIMENTO_A");
		String codCpiComp = (String) serviceRequest.getAttribute("CODCPICOMP");
		String codCpiProv = (String) serviceRequest.getAttribute("CODCPIPROV");

		StringBuffer query = new StringBuffer(STM);

		if (codStatoPresaAtto.equals("CH") || codStatoPresaAtto.equals("AT")) {
			query.append(" ,de_cpi2.strdescrizione as cpiProv ");
		}

		query.append(" FROM ca_presa_atto, de_stato_presa_atto, de_cpi ");

		if (codStatoPresaAtto.equals("CH") || codStatoPresaAtto.equals("AT")) {
			query.append(" ,an_lav_storia_inf, de_cpi de_cpi2 ");
		}

		query.append(" WHERE ca_presa_atto.codstatopresaatto = de_stato_presa_atto.codstatopresaatto "
				+ " AND ca_presa_atto.codcpirich = de_cpi.codcpi ");

		if (codStatoPresaAtto.equals("CH") || codStatoPresaAtto.equals("AT")) {
			query.append(" AND an_lav_storia_inf.cdnlavoratore = ca_presa_atto.cdnlavoratore "
					+ " AND de_cpi2.codcpi = an_lav_storia_inf.CODCPITIT (+) ");
		}

		if (cdnLavoratore == null) {
			String prefissoRicerca = " like upper('";
			String suffissoRicerca = "%')";
			if ("esatta".equals(tipoRicerca)) {
				prefissoRicerca = " = upper('";
				suffissoRicerca = "')";
			}
			if (!"".equals(codiceFiscale)) {
				query.append(" AND ca_presa_atto.STRCODICEFISCALE ");
				query.append(prefissoRicerca);
				query.append(codiceFiscale);
				query.append(suffissoRicerca);
			}
			if (!"".equals(cognome)) {
				query.append(" AND ca_presa_atto.STRCOGNOME ");
				query.append(prefissoRicerca);
				query.append(cognome);
				query.append(suffissoRicerca);
			}
			if (!"".equals(nome)) {
				query.append(" AND ca_presa_atto.STRNOME ");
				query.append(prefissoRicerca);
				query.append(nome);
				query.append(suffissoRicerca);
			}

		}

		if (cdnLavoratore != null) {
			query.append(" AND ca_presa_atto.CDNLAVORATORE = ");
			query.append(cdnLavoratore);
		}
		if (!"".equals(codStatoPresaAtto)) {
			query.append(" AND ca_presa_atto.CODSTATOPRESAATTO = '");
			query.append(codStatoPresaAtto);
			query.append("'");
		}
		if (!"".equals(codCpiComp)) {
			query.append(" AND CODCPIRICH = '");
			query.append(codCpiComp);
			query.append("'");
		}
		if (!"".equals(dataTrasferimentoDa) && !"".equals(dataTrasferimentoA)) {
			query.append(" AND ca_presa_atto.dattrasferimento between to_date('");
			query.append(dataTrasferimentoDa);
			query.append("', 'dd/mm/yyyy') and to_date('");
			query.append(dataTrasferimentoA);
			query.append("', 'dd/mm/yyyy')");
		} else {
			if (!"".equals(dataTrasferimentoDa)) {
				query.append(" AND ca_presa_atto.dattrasferimento >= to_date('");
				query.append(dataTrasferimentoDa);
				query.append("', 'dd/mm/yyyy')");
			}
			if (!"".equals(dataTrasferimentoA)) {
				query.append(" AND ca_presa_atto.dattrasferimento <= to_date('");
				query.append(dataTrasferimentoA);
				query.append("', 'dd/mm/yyyy')");
			}
		}

		if ((codCpiProv != null) && (codCpiProv.compareTo("") != 0)) {
			query.append(" AND an_lav_storia_inf.CODCPITIT = ");
			query.append(codCpiProv);
		}

		if (codStatoPresaAtto.equals("CH")) {
			query.append(" AND an_lav_storia_inf.CODMONOTIPOCPI='T' ");
			query.append(" AND an_lav_storia_inf.CODCPIORIG=CA_PRESA_ATTO.codcpirich ");
			query.append(" AND an_lav_storia_inf.DATINIZIO=CA_PRESA_ATTO.DATTRASFERIMENTO ");
		}

		if (codStatoPresaAtto.equals("AT")) {
			query.append(" AND an_lav_storia_inf.CODMONOTIPOCPI='C' ");
			query.append(" AND AN_LAV_STORIA_INF.DATFINE IS NULL ");
		}

		query.append(" ORDER BY strcognome, strnome, attiva desc, ca_presa_atto.dattrasferimento desc");
		return query.toString();
	}

}
