package it.eng.sil.module.trento;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

public class AllegatiTemplateList implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AllegatiTemplateList.class.getName());

	public AllegatiTemplateList() {
	}

	private static final String SELECT_SQL_BASE = " select (case when am_config_prot_doc_tipo.codtipodocumento is not null then "
			+ " de_doc_tipo.strdescrizione || '/' || am_config_prot_doc_tipo.strdescrizione "
			+ " else st_template_stampa.strNome " + " end) as strdescrizione, "
			+ " decode(am_config_prot_doc_tipo.flgobbl, '1', 'S', 'N') flgobbl, "
			+ " am_config_prot_doc_tipo.prgConfigProtDocTipo, am_config_prot_doc_tipo.PRGCONFIGPROT, am_config_prot_doc_tipo.numkloconfprotdoc "
			+ "  from am_config_prot_doc_tipo "
			+ "  left join de_doc_tipo on (am_config_prot_doc_tipo.codtipodocumento = de_doc_tipo.codtipodocumento) "
			+ " left join st_template_stampa on (am_config_prot_doc_tipo.prgtemplatestampa = st_template_stampa.prgtemplatestampa) ";

	public String getStatement(final RequestContainer requestContainer, final SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String query_totale = SELECT_SQL_BASE;

		if (req.getAttribute("PRGCONFIGPROT") != null && !req.getAttribute("PRGCONFIGPROT").equals("")) {

			query_totale += " where am_config_prot_doc_tipo.datcanc is null and am_config_prot_doc_tipo.PRGCONFIGPROT = "
					+ req.getAttribute("PRGCONFIGPROT").toString();
		} else {
			query_totale += " where am_config_prot_doc_tipo.datcanc is null and am_config_prot_doc_tipo.PRGCONFIGPROT is null";

		}

		query_totale += " order by decode(am_config_prot_doc_tipo.flgobbl, '1', 1, 0) desc, am_config_prot_doc_tipo.strdescrizione asc";

		_logger.debug(
				"it.eng.sil.module.trento.AllegatiTemplateList " + "::Stringa di ricerca:" + query_totale.toString());

		return query_totale;

	}

}