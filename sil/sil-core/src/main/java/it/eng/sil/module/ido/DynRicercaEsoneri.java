package it.eng.sil.module.ido;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

public class DynRicercaEsoneri implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE = " "
			+ "SELECT   strdescrizione, strcodicefiscale, strpartitaiva, strragionesociale,"
			+ "		 TO_CHAR (datrichiesta, 'DD/MM/YYYY') AS datrichiesta,"
			+ "		 TO_CHAR (datpagamento, 'dd/mm/yyyy') AS datpagamento,"
			+ "		 TO_CHAR (MAX (datiniziocomp), 'DD/MM/YYYY') AS datiniziocomp,"
			+ "		 strdenominazione,TO_CHAR (MAX (datfinecomp), 'DD/MM/YYYY') AS datfinecomp,"
			+ "		 strindirizzo, prgunita,TO_CHAR (datiniziovalidita, 'DD/MM/YYYY') AS datiniziovalidita, ambitoTerritoriale "
			+ "FROM (SELECT de_tipo_azienda.strdescrizione, an_azienda.strcodicefiscale,"
			+ "	  		 an_azienda.strpartitaiva, an_azienda.strragionesociale,"
			+ "			 cm_rich_esonero.datrichiesta, cm_pagamento_eson.datpagamento,"
			+ "			 cm_pagamento_eson.datiniziocomp, de_comune.strdenominazione,"
			+ "			 cm_pagamento_eson.datfinecomp, unita.strindirizzo,"
			+ "			 unita.prgunita, cm_rich_esonero.datiniziovalidita, provAmbito.strDenominazione ambitoTerritoriale "
			+ "	  FROM cm_rich_esonero,an_azienda,de_tipo_azienda,an_unita_azienda unita,"
			+ "		   de_comune,de_provincia,cm_pagamento_eson, de_provincia provAmbito "
			+ "	  WHERE an_azienda.prgazienda = cm_rich_esonero.prgazienda "
			+ "			AND cm_rich_esonero.codprovincia = provAmbito.codprovincia "
			+ "			AND de_tipo_azienda.codtipoazienda = an_azienda.codtipoazienda "
			+ "			AND unita.prgazienda = an_azienda.prgazienda "
			+ "			AND de_comune.codcom = unita.codcom "
			+ "			AND de_provincia.codprovincia = de_comune.codprovincia(+) "
			+ "			AND cm_rich_esonero.prgrichesonero = cm_pagamento_eson.prgrichesonero(+) "
			+ "			AND (unita.prgazienda, unita.prgunita) "
			+ "			IN (SELECT an_azienda.prgazienda,MAX (unita.prgunita) AS prgunita"
			+ "				FROM an_unita_azienda unita, an_azienda"
			+ "				WHERE an_azienda.prgazienda = unita.prgazienda"
			+ "				      AND (TO_CHAR (cm_pagamento_eson.datpagamento,'dd/mm/yyyy') ="
			+ "					  		(SELECT MAX (TO_CHAR (pag.datpagamento,'dd/mm/yyyy') )"
			+ "							 FROM cm_pagamento_eson pag "
			+ "							 WHERE pag.prgrichesonero = cm_pagamento_eson.prgrichesonero) "
			+ "								   OR TO_CHAR (cm_pagamento_eson.datpagamento,'dd/mm/yyyy') IS NULL ) "
			+ "							GROUP BY an_azienda.prgazienda) "
			+ "			AND cm_rich_esonero.codstatorichiesta = 'AP'";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		String pagamenti = StringUtils.getAttributeStrNotNull(req, "pagamenti");
		String dataRichDa = StringUtils.getAttributeStrNotNull(req, "dataRichDa");
		String dataRichA = StringUtils.getAttributeStrNotNull(req, "dataRichA");
		String ambitoTerr = StringUtils.getAttributeStrNotNull(req, "PROVINCIA_ISCR");

		if ((dataRichDa != null) && (!dataRichDa.equals(""))) {
			buf.append(" AND cm_rich_esonero.DATRICHIESTA >= to_date('" + dataRichDa + "', 'DD/MM/YYYY') ");
		}
		if ((dataRichA != null) && (!dataRichA.equals(""))) {
			buf.append(" AND cm_rich_esonero.DATRICHIESTA <= to_date('" + dataRichA + "', 'DD/MM/YYYY') ");
		}

		if (pagamenti.equals("noRegola")) {
			buf.append(" and pg_coll_mirato.checkSituazioneEsonero(cm_rich_esonero.prgrichesonero) = 1 ");
		}

		if ((ambitoTerr != null) && (!ambitoTerr.equals(""))) {
			buf.append(" AND cm_rich_esonero.codprovincia = '" + ambitoTerr + "' ");
		}

		buf.append(" ) ");

		buf.append(
				" GROUP BY (strdescrizione,strcodicefiscale,strpartitaiva,strragionesociale,datrichiesta,datpagamento,"
						+ "		 strdenominazione,prgunita,strindirizzo,datiniziovalidita,ambitoTerritoriale) ");

		query_totale.append(buf.toString());
		return query_totale.toString();
	}
}
