package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

public class DynStatementDichRed implements IDynamicStatementProvider {
	final static String QUERY_BASE = "select an_lavoratore.cdnlavoratore as cdnlavoratore,"
			+ "an_lavoratore.strcognome as strcognome," + "an_lavoratore.strnome as strnome,"
			+ "an_lavoratore.strcodicefiscale as strcodicefiscale,"
			+ "to_char(am_dich_lav.datinizio, 'dd/mm/yyyy') as datDichRed," + "am_dich_lav.prgdichlav as prgdichlav,"
			+ "to_char (max(am_dich_disponibilita.datdichiarazione),'dd/mm/yyyy') as datDid, "
			+ "de_tipo_dich.strdescrizione as strtipodich, " + "de_stato_atto.strdescrizione stato "
			+ "from am_dich_lav " + "inner join an_lavoratore "
			+ "on (am_dich_lav.cdnlavoratore = an_lavoratore.cdnlavoratore) " + "inner join de_stato_atto "
			+ "on (am_dich_lav.codstatoatto= de_stato_atto.codstatoatto) " + "inner join de_tipo_dich "
			+ "on (am_dich_lav.codtipodich = de_tipo_dich.codtipodich) " + "inner join an_lav_storia_inf "
			+ "on (am_dich_lav.cdnlavoratore = an_lav_storia_inf.cdnlavoratore "
			+ "and (an_lav_storia_inf.datfine is null or an_lav_storia_inf.datfine >= am_dich_lav.datinizio)) "
			+ "inner join de_cpi cpitit " + "on (an_lav_storia_inf.codcpitit = cpitit.codcpi) "
			+ "left join de_cpi cpiorig " + "on (an_lav_storia_inf.codcpiorig = cpiorig.codcpi) "
			+ "left join am_elenco_anagrafico " + "on (am_dich_lav.cdnlavoratore = am_elenco_anagrafico.cdnlavoratore "
			+ "and am_elenco_anagrafico.datinizio <= am_dich_lav.datinizio "
			+ "and (am_elenco_anagrafico.datcan is null or am_elenco_anagrafico.datcan >= am_dich_lav.datinizio)) "
			+ "left join am_dich_disponibilita "
			+ "on (am_elenco_anagrafico.prgelencoanagrafico = am_dich_disponibilita.prgelencoanagrafico "
			+ "    and am_dich_disponibilita.datdichiarazione <= am_dich_lav.datinizio "
			+ "    and  (am_dich_disponibilita.datfine is null or am_dich_lav.datinizio <= am_dich_disponibilita.datfine)) "
			+ "where ";

	final static String QUERY_GROUP = "group by an_lavoratore.cdnlavoratore, an_lavoratore.strcognome,an_lavoratore.strnome,an_lavoratore.strcodicefiscale, "
			+ "to_char(am_dich_lav.datinizio, 'dd/mm/yyyy'), de_tipo_dich.strdescrizione, prgdichlav, de_stato_atto.strdescrizione ";

	final static String QUERY_TAIL = "order by to_date(datDichRed,'dd/mm/yyyy') desc, strCognome, strNome";

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
		String codTipoDich = (String) request.getAttribute("codTipoDich");
		String tipoRic = StringUtils.getAttributeStrNotNull(request, "tipoRicerca");
		String codStatoAtto = (String) request.getAttribute("codStatoAttoRicerca");

		// La ricerca è sempre esatta
		// tipoRic="esatta";

		// se il cdnLavoratore e' presente allora la ricerca e' avvenuta a
		// partire dal menu del lavoratore
		// quindi posso, anzi debbo bypassare i campi nome, cognome, codice
		// fiscale
		String cdnLavoratore = (String) request.getAttribute("CDNLAVORATORE");

		StringBuffer query = new StringBuffer();

		if ((dataInizio != null) && !dataInizio.trim().equals("")) {
			if (!query.toString().equalsIgnoreCase(""))
				query.append(" and ");
			query.append("am_dich_lav.datinizio >= to_date('");
			query.append(dataInizio);
			query.append("', 'dd/mm/yyyy') ");
		}

		if ((dataFine != null) && !dataFine.trim().equals("")) {
			if (!query.toString().equalsIgnoreCase(""))
				query.append(" and ");
			query.append("am_dich_lav.datinizio <= to_date('");
			query.append(dataFine);
			query.append("', 'dd/mm/yyyy') ");
		}
		boolean ricercaGenerale = request.containsAttribute("ricerca_generale")
				&& ((String) request.getAttribute("ricerca_generale")).equals("true");
		if (ricercaGenerale || (cdnLavoratore == null) || cdnLavoratore.trim().equals("")) {
			if (tipoRic.equalsIgnoreCase("esatta")) {
				if ((nome != null) && (!nome.equals(""))) {
					if (!query.toString().equalsIgnoreCase(""))
						query.append(" and ");
					nome = StringUtils.replace(nome, "'", "''");
					query.append("upper(strnome) = '" + nome.toUpperCase() + "'");
				}

				if ((cognome != null) && (!cognome.equals(""))) {
					if (!query.toString().equalsIgnoreCase(""))
						query.append(" and ");
					cognome = StringUtils.replace(cognome, "'", "''");
					query.append(" upper(strcognome) = '" + cognome.toUpperCase() + "'");
				}

				if ((cf != null) && (!cf.equals(""))) {
					if (!query.toString().equalsIgnoreCase(""))
						query.append(" and ");
					query.append(" upper(strCodiceFiscale) = '" + cf.toUpperCase() + "'");
				}
			} else {
				if ((nome != null) && (!nome.equals(""))) {
					if (!query.toString().equalsIgnoreCase(""))
						query.append(" and ");
					nome = StringUtils.replace(nome, "'", "''");
					query.append(" upper(strnome) like '" + nome.toUpperCase() + "%'");
				}

				if ((cognome != null) && (!cognome.equals(""))) {
					if (!query.toString().equalsIgnoreCase(""))
						query.append(" and ");
					cognome = StringUtils.replace(cognome, "'", "''");
					query.append(" upper(strcognome) like '" + cognome.toUpperCase() + "%'");
				}

				if ((cf != null) && (!cf.equals(""))) {
					if (!query.toString().equalsIgnoreCase(""))
						query.append(" and ");
					query.append(" upper(strCodiceFiscale) like '" + cf.toUpperCase() + "%'");
				}
			} // else

		} else {
			if (!query.toString().equalsIgnoreCase(""))
				query.append(" and ");
			query.append("am_dich_lav.cdnLavoratore = ");
			query.append(cdnLavoratore);
			query.append(" ");
		}

		if ((codCPI != null) && !codCPI.trim().equals("")) {
			if (!query.toString().equalsIgnoreCase(""))
				query.append(" and ");
			query.append(
					"decode(an_lav_storia_inf.codMonoTipoCpi,'C',cpitit.codcpi,'E',cpitit.codcpi,'T',cpiorig.codcpi) = '");
			query.append(codCPI);
			query.append("' ");
		}

		if ((codTipoDich != null) && !codTipoDich.trim().equals("")) {
			if (!query.toString().equalsIgnoreCase(""))
				query.append(" and ");
			query.append("" + "am_dich_lav.codTipoDich = '");
			query.append(codTipoDich);
			query.append("' ");
		}

		if ((codStatoAtto != null) && !codStatoAtto.trim().equals("")) {
			if (!query.toString().equalsIgnoreCase(""))
				query.append(" and ");
			query.append("" + "am_dich_lav.codStatoAtto = '");
			query.append(codStatoAtto);
			query.append("' ");
		}
		query = new StringBuffer(QUERY_BASE).append(query);

		query.append(QUERY_GROUP);

		query.append(QUERY_TAIL);

		return query.toString();
	}
}
