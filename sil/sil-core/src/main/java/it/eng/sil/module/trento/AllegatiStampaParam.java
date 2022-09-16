package it.eng.sil.module.trento;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

public class AllegatiStampaParam implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AllegatiStampaParam.class.getName());

	public AllegatiStampaParam() {
	}

	private static final String SELECT_SQL_BASE = " select allegato.prgdocumentoallegato as prgdocumentoallegato, de_doc_tipo.strdescrizione as strdescrizione, "
			+ " allegato.flgpresavisione, allegato.flgcaricsuccessivo, doc.codcpi, allegato.prgdocumentopadre, allegato.prgdocumentofiglio, "
			// modifica relativa alla segnalazione 4351 punto 6 aggiungere una colonna Tipo Allegato e modificare il
			// campo nome
			+ "doc.STRDESCRIZIONE as AM_DOC_DESCRIZIONE, CASE WHEN DOC.NUMANNOPROT IS NOT NULL AND DOC.NUMPROTOCOLLO IS NOT NULL THEN DOC.NUMANNOPROT || '/' || DOC.NUMPROTOCOLLO || ' '"
			+ " WHEN DOC.NUMANNOPROT IS NOT NULL AND DOC.NUMPROTOCOLLO IS NULL     THEN DOC.NUMANNOPROT || ' '"
			+ " WHEN DOC.NUMANNOPROT IS NULL     AND DOC.NUMPROTOCOLLO IS NOT NULL THEN DOC.NUMPROTOCOLLO || ' '"
			+ "  END  || TO_CHAR(DOC.DATPROTOCOLLO,'DD/MM/YYYY') || ' ' || CASE"
			+ " WHEN UPPER(DOC.CODMONOIO) = 'I' THEN 'IN'"
			+ "	 	WHEN UPPER(DOC.CODMONOIO) = 'O' THEN 'OUT' 	 END AS AM_NUMERO_PROTOCOLLO, "
			+ "	 	DE_STATO_ATTO.STRDESCRIZIONE statoatto " + "  from am_documento_allegato allegato "
			+ "  inner join am_documento doc on (allegato.prgdocumentofiglio = doc.prgdocumento) "
			+ "  inner join de_doc_tipo on (doc.codtipodocumento = de_doc_tipo.codtipodocumento) "
			+ "  inner join  DE_STATO_ATTO on (doc.codstatoatto = de_stato_atto.codstatoatto)  ";

	// inner join am_config_prot_doc_tipo on (st_template_stampa.prgconfigprot = am_config_prot_doc_tipo.prgconfigprot)
	// (case
	// when am_config_prot_doc_tipo.prgtemplatestampa is not null then 0
	// else 1
	// end) as is_template
	public String getStatement(final RequestContainer requestContainer, final SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String query_totale = SELECT_SQL_BASE;

		String prgDocumento = req.getAttribute("PRGDOCUMENTO") != null ? req.getAttribute("PRGDOCUMENTO").toString()
				: null;

		query_totale += " where allegato.prgdocumentopadre = " + prgDocumento;

		query_totale += " order by doc.strdescrizione asc";

		_logger.info(
				"it.eng.sil.module.trento.AllegatiStampaParam " + "::Stringa di ricerca:" + query_totale.toString());

		return query_totale;

	}

}