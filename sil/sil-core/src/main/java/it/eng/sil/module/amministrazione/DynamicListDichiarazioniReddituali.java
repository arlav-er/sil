package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

public class DynamicListDichiarazioniReddituali implements IDynamicStatementProvider {
	final static String QUERY_BASE = "select " + "de_tipo_dich.codtipodich as codice,"
			+ "de_tipo_dich.strDescrizione as descrizione," + "an_lavoratore.strcognome," + "an_lavoratore.strnome,"
			+ "an_lavoratore.strcodicefiscale," + "an_lavoratore.datnasc," + "de_cpi.strdescrizione,"
			+ "de_cpi_orig.strdescrizione," + "to_char(am_dich_lav.datinizio,'dd/mm/yyyy') " + "from de_tipo_dich,"
			+ "am_dich_lav," + "an_lav_storia_inf," + "de_cpi," + "de_cpi de_cpi_orig," + "an_lavoratore "
			+ "where de_tipo_dich.codtipodich = am_dich_lav.codtipodich"
			+ " and an_lav_storia_inf.cdnlavoratore = am_dich_lav.cdnlavoratore"
			+ " and de_cpi.codcpi = an_lav_storia_inf.codcpitit"
			+ " and an_lav_storia_inf.codcpiorig = de_cpi_orig.codcpi"
			+ " and an_lavoratore.cdnlavoratore = am_dich_lav.cdnlavoratore";

	final static String QUERY_TAIL = "order by to_char(am_dich_lav.datinizio,'dd/mm/yyyy') desc";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean request = requestContainer.getServiceRequest();

		// SourceBean request = getTestRequest();
		//
		String dataInizio = (String) request.getAttribute("dataInizio");
		String dataFine = (String) request.getAttribute("dataFine");
		String cf = (String) request.getAttribute("CF");
		String nome = (String) request.getAttribute("NOME");
		String cognome = (String) request.getAttribute("COGNOME");
		String codCPI = (String) request.getAttribute("codCPI");
		String codServizio = (String) request.getAttribute("codTipoDich");
		String tipoRic = StringUtils.getAttributeStrNotNull(request, "tipoRicerca");

		// La ricerca è sempre esatta
		tipoRic = "esatta";

		// se il cdnLavoratore e' presente allora la ricerca e' avvenuta a
		// partire dal menu del lavoratore
		// quindi posso, anzi debbo bypassare i campi nome, cognome, codice
		// fiscale
		String cdnLavoratore = (String) request.getAttribute("CDNLAVORATORE");

		//
		StringBuffer query = new StringBuffer(QUERY_BASE);

		if ((dataInizio != null) && !dataInizio.trim().equals("")) {
			query.append(" and am_dich_lav.datinizio >= to_date('");
			query.append(dataInizio);
			query.append("', 'dd/mm/yyyy') ");
		}

		if ((dataFine != null) && !dataFine.trim().equals("")) {
			query.append(" and am_dich_lav.datinizio <= to_date('");
			query.append(dataFine);
			query.append("', 'dd/mm/yyyy') ");
		}
		boolean ricercaGenerale = request.containsAttribute("ricerca_generale")
				&& ((String) request.getAttribute("ricerca_generale")).equals("true");
		if (ricercaGenerale || (cdnLavoratore == null) || cdnLavoratore.trim().equals("")) {
			if (tipoRic.equalsIgnoreCase("esatta")) {
				if ((nome != null) && (!nome.equals(""))) {
					nome = StringUtils.replace(nome, "'", "''");
					query.append(" AND");
					query.append(" upper(strnome) = '" + nome.toUpperCase() + "'");
				}

				if ((cognome != null) && (!cognome.equals(""))) {
					query.append(" AND");
					cognome = StringUtils.replace(cognome, "'", "''");
					query.append(" upper(strcognome) = '" + cognome.toUpperCase() + "'");
				}

				if ((cf != null) && (!cf.equals(""))) {
					query.append(" AND");
					query.append(" upper(strCodiceFiscale) = '" + cf.toUpperCase() + "'");
				}
			} else {
				if ((nome != null) && (!nome.equals(""))) {
					nome = StringUtils.replace(nome, "'", "''");
					query.append(" AND");
					query.append(" upper(strnome) like '" + nome.toUpperCase() + "%'");
				}

				if ((cognome != null) && (!cognome.equals(""))) {
					query.append(" AND");
					cognome = StringUtils.replace(cognome, "'", "''");
					query.append(" upper(strcognome) like '" + cognome.toUpperCase() + "%'");
				}

				if ((cf != null) && (!cf.equals(""))) {
					query.append(" AND");
					query.append(" upper(strCodiceFiscale) like '" + cf.toUpperCase() + "%'");
				}
			} // else

		} else {
			query.append(" and coll.cdnLavoratore = ");
			query.append(cdnLavoratore);
			query.append(" ");
		}

		if ((codCPI != null) && !codCPI.trim().equals("")) {
			query.append(" and coll.codCPI = '");
			query.append(codCPI);
			query.append("' ");
		}

		if ((codServizio != null) && !codServizio.trim().equals("")) {
			query.append(" and ser.codServizio = '");
			query.append(codServizio);
			query.append("' ");
		}

		query.append(QUERY_TAIL);

		return query.toString();
	}

	/* test di generazione dello statement */
	public static void main(String[] a) {
		DynamicListDichiarazioniReddituali l = new DynamicListDichiarazioniReddituali();
		System.out.println(l.getStatement(null, null));
	}

	/**
	 * 
	 * genera una request di test
	 */
	private SourceBean getTestRequest() {
		SourceBean s = null;

		try {
			s = new SourceBean("TEST");
			s.setAttribute("dataInizio", "01/01/2004");
			s.setAttribute("dataFine", "01/01/2004");
			s.setAttribute("CF", "SVNNDRETC");
			s.setAttribute("NOME", "andrea");
			s.setAttribute("COGNOME", "savino");
			s.setAttribute("codCPI", "081300100");
			s.setAttribute("codServizio", "RLE");
			s.setAttribute("cdnLavoratore", "12");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return s;
	}
}
