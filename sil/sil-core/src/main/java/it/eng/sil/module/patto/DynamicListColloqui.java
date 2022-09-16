package it.eng.sil.module.patto;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

public class DynamicListColloqui implements IDynamicStatementProvider {
	final static String QUERY_BASE = "SELECT distinct to_char(coll.datcolloquio,'dd/mm/yyyy') as datcolloquio, "
			+ "coll.datcolloquio as datcolloquio_ord, coll.prgcolloquio, scheda.strazionicon, to_char(coll.datfineprogramma,'dd/mm/yyyy') as datafineprogramma, "
			+ "scheda.strobiettivo, scheda.strrispotenz, scheda.strdesaspir, scheda.strvincest, ser.codmonoprogramma, "
			+ "scheda.strvinclav, ser.CODSERVIZIO, ser.strdescrizione, coll.cdnlavoratore, "
			+ "lav.STRCOGNOME, lav.STRNOME, lav.STRCODICEFISCALE, coll.prgspi, de_cod_cpi.STRDESCRIZIONE || ' - ' || coll.CODCPI DESCRCPI";

	final static String QUERY_TAIL = "order by decode(ser.codmonoprogramma, null, 0, 1) desc, ser.codmonoprogramma, datcolloquio_ord desc, strcognome, strnome";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean request = requestContainer.getServiceRequest();

		// SourceBean request = getTestRequest();
		//
		String dataInizioDa = (String) request.getAttribute("dataInizioDa");
		String dataInizioA = (String) request.getAttribute("dataInizioA");
		String dataFineDa = (String) request.getAttribute("dataFineDa");
		String dataFineA = (String) request.getAttribute("dataFineA");

		String cf = (String) request.getAttribute("CF");
		String nome = (String) request.getAttribute("NOME");
		String cognome = (String) request.getAttribute("COGNOME");
		String codCPI = (String) request.getAttribute("codCPI");
		String codServizio = (String) request.getAttribute("codServizio");
		String tipoRic = StringUtils.getAttributeStrNotNull(request, "tipoRicerca");
		String progrAperti = (String) request.getAttribute("progrAperti");

		// se il cdnLavoratore e' presente allora la ricerca e' avvenuta a
		// partire dal menu del lavoratore
		// quindi posso, anzi debbo bypassare i campi nome, cognome, codice
		// fiscale
		String cdnLavoratore = (String) request.getAttribute("CDNLAVORATORE");
		boolean ricercaGenerale = request.containsAttribute("ricerca_generale")
				&& ((String) request.getAttribute("ricerca_generale")).equals("true");
		//
		StringBuffer query = new StringBuffer(QUERY_BASE);

		String canCIG = (String) request.getAttribute("canCIG");

		String progCond = (String) request.getAttribute("progCond");
		String azioniNegativo = (String) request.getAttribute("azioniNegativo");
		String azioniCond = (String) request.getAttribute("azioniCond");

		if (canCIG.equals("true")) {
			query.append(
					", decode(CIG.PRGALTRAISCR,null,'','dal ' || to_char(cig.datInizio,'dd/mm/yyyy') || ' al ' || to_char(cig.datFine,'dd/mm/yyyy') || ' az. ' || az.strRagioneSociale || decode(stato.strDescrizione,null,'',' stato ' || stato.strDescrizione)) as descrIscrCig");
		}

		if (!ricercaGenerale && StringUtils.isFilledNoBlank(cdnLavoratore)) {
			query.append(", decode( ser.FLGCONDIZIONALITA,  null, 'No', 'N', 'No', 'Sì') as flgCondizionalita");
			query.append(", ( CASE WHEN (SELECT COUNT(1) ");
			query.append("   					FROM or_percorso_concordato perc ");
			query.append("   					INNER JOIN de_esito e  ON (perc.codesito =e.codesito) ");
			query.append(
					"   					WHERE perc.prgcolloquio=coll.prgcolloquio AND e.flgnegativo   ='S' ) > 0 ");
			query.append("   			  THEN 'Sì' ELSE 'No'");
			query.append("   END) AS flgCountAzioniNegativo ");
		}

		query.append(" FROM or_colloquio coll ");
		query.append("INNER JOIN or_scheda_colloquio scheda on scheda.prgcolloquio = coll.prgcolloquio ");
		query.append("INNER JOIN an_lavoratore lav on lav.CDNLAVORATORE = coll.CDNLAVORATORE ");
		query.append("LEFT JOIN de_servizio ser on ser.codservizio = coll.codservizio ");
		query.append("INNER JOIN DE_CPI de_cod_cpi on de_cod_cpi.CODCPI = coll.CODCPI ");

		if (canCIG.equals("true")) {
			if (request.containsAttribute("conCig")) {
				query.append("INNER JOIN AM_ALTRA_ISCR CIG ON COLL.PRGALTRAISCR = CIG.PRGALTRAISCR ");
				query.append("LEFT JOIN AN_AZIENDA AZ ON AZ.PRGAZIENDA = CIG.PRGAZIENDA ");
				query.append("LEFT JOIN DE_STATO_ALTRA_ISCR STATO ON STATO.CODSTATO = CIG.CODSTATO ");
			} else {
				query.append("LEFT JOIN AM_ALTRA_ISCR CIG ON COLL.PRGALTRAISCR = CIG.PRGALTRAISCR ");
				query.append("LEFT JOIN AN_AZIENDA AZ ON AZ.PRGAZIENDA = CIG.PRGAZIENDA ");
				query.append("LEFT JOIN DE_STATO_ALTRA_ISCR STATO ON STATO.CODSTATO = CIG.CODSTATO ");
			}
		}

		query.append("WHERE 1=1");
		if ((dataInizioDa != null) && !dataInizioDa.trim().equals("")) {
			query.append(" and coll.DATCOLLOQUIO >= to_date('");
			query.append(dataInizioDa);
			query.append("', 'dd/mm/yyyy') ");
		}
		if ((dataInizioA != null) && !dataInizioA.trim().equals("")) {
			query.append(" and coll.DATCOLLOQUIO <= to_date('");
			query.append(dataInizioA);
			query.append("', 'dd/mm/yyyy') ");
		}

		if ((dataFineDa != null) && !dataFineDa.trim().equals("")) {
			query.append(" and coll.DATFINEPROGRAMMA >= to_date('");
			query.append(dataFineDa);
			query.append("', 'dd/mm/yyyy') ");
		}
		if ((dataFineA != null) && !dataFineA.trim().equals("")) {
			query.append(" and coll.DATFINEPROGRAMMA <= to_date('");
			query.append(dataFineA);
			query.append("', 'dd/mm/yyyy') ");
		}

		if (ricercaGenerale || (cdnLavoratore == null) || cdnLavoratore.trim().equals("")) {
			if (tipoRic.equalsIgnoreCase("esatta")) {
				if ((nome != null) && (!nome.equals(""))) {
					nome = StringUtils.replace(nome, "'", "''");
					query.append(" AND");
					query.append(" upper(lav.strnome) = '" + nome.toUpperCase() + "'");
				}

				if ((cognome != null) && (!cognome.equals(""))) {
					query.append(" AND");
					cognome = StringUtils.replace(cognome, "'", "''");
					query.append(" upper(lav.strcognome) = '" + cognome.toUpperCase() + "'");
				}

				if ((cf != null) && (!cf.equals(""))) {
					query.append(" AND");
					query.append(" upper(lav.strCodiceFiscale) = '" + cf.toUpperCase() + "'");
				}
			} else {
				if ((nome != null) && (!nome.equals(""))) {
					nome = StringUtils.replace(nome, "'", "''");
					query.append(" AND");
					query.append(" upper(lav.strnome) like '" + nome.toUpperCase() + "%'");
				}

				if ((cognome != null) && (!cognome.equals(""))) {
					query.append(" AND");
					cognome = StringUtils.replace(cognome, "'", "''");
					query.append(" upper(lav.strcognome) like '" + cognome.toUpperCase() + "%'");
				}

				if ((cf != null) && (!cf.equals(""))) {
					query.append(" AND");
					query.append(" upper(lav.strCodiceFiscale) like '" + cf.toUpperCase() + "%'");
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

		if (StringUtils.isFilledNoBlank(progrAperti)) {
			query.append(" and (TRUNC(coll.datfineprogramma) ");
			if (progrAperti.equalsIgnoreCase("S")) {
				query.append(" > TRUNC(SYSDATE) or datfineprogramma is null)");
			} else if (progrAperti.equalsIgnoreCase("N")) {
				query.append(" <= TRUNC(SYSDATE))");
			}
		}
		if (StringUtils.isFilledNoBlank(progCond) && progCond.equalsIgnoreCase("on")) {
			// Solo programmi sottoposti a condizionalità
			query.append(" and (NVL(ser.FLGCONDIZIONALITA, 'N') = 'S')");
		}
		if (StringUtils.isFilledNoBlank(azioniCond) && azioniCond.equalsIgnoreCase("on")) {
			// Solo azioni con eventi di condizionalità
			query.append(" and EXISTS ( ");
			query.append("    select 1 from am_condizionalita ");
			query.append("	    where am_condizionalita.prgcolloquio =  coll.prgcolloquio ");
			query.append(")");
		}
		if (StringUtils.isFilledNoBlank(azioniNegativo) && azioniNegativo.equalsIgnoreCase("on")) {
			// Solo azioni con esito negativo
			query.append(" and EXISTS (");
			query.append("	  select 1 ");
			query.append("	  from or_percorso_concordato");
			query.append("	  inner join de_esito on (de_esito.CODESITO = or_percorso_concordato.CODESITO");
			query.append("	    and NVL(de_esito.FLGNEGATIVO, 'N') ='S')");
			query.append("	    where or_percorso_concordato.prgcolloquio =  coll.prgcolloquio ");
			query.append("	)");
		}
		query.append(QUERY_TAIL);

		return query.toString();
	}

}
