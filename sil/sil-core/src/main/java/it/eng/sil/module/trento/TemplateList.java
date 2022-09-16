package it.eng.sil.module.trento;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.util.NavigationCache;

public class TemplateList implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(TemplateList.class.getName());

	public TemplateList() {
	}
	

	private static final String SELECT_SQL_BASE = ""
			+ " select st_template_stampa.ordinamento ordinamento,st_template_stampa.strnome         as  STRNOME, "
			+ "       st_template_stampa.strnote          as  STRNOTE , "
			+ "       de_doc_tipo.STRDESCRIZIONE   as  STRDESCRIZIONE, "
			+ "       st_template_stampa.CODTIPODOCUMENTO     as  CODAMBITOTEM, "
			+ "       st_template_stampa.CODTIPODOMINIO   as  CODTIPODOMINIO, "
			+ "       st_template_stampa.NUMKLOTEMP       as  NUMKLOTEMP, "
			+ "to_char(st_template_stampa.datinizioval, 'dd/MM/yyyy')    as  datinizioval, "
			+ "to_char(st_template_stampa.datfineval, 'dd/MM/yyyy')      as  datfineval, "
			+ "        de_doc_tipo.STRDESCRIZIONE  as  DESCCODAMBITOTEM, "
			+ "        de_tipo_dominio_dati_stampa.STRDESCRIZIONE  as  DESCDOMINIO, "
			+ "        st_template_stampa.PRGTEMPLATESTAMPA as PRGTEMPLATESTAMPA, st_template_stampa.PRGCONFIGPROT as PRGCONFIGPROT, "
			+ "        st_template_classif.STRNOME as NOMECLASSIF, st_template_classif.PRGCLASSIF " + "  from "
			+ " st_template_stampa " + "    JOIN " + " de_doc_tipo "
			+ "   ON st_template_stampa.CODTIPODOCUMENTO = de_doc_tipo.CODTIPODOCUMENTO " + "    JOIN "
			+ " de_tipo_dominio_dati_stampa "
			+ "   ON st_template_stampa.CODTIPODOMINIO = de_tipo_dominio_dati_stampa.CODTIPODOMINIO "
			+ "  LEFT JOIN  am_config_protocollo ON st_template_stampa.prgconfigprot = am_config_protocollo.prgconfigprot "
			+ "  LEFT JOIN st_template_classif ON st_template_stampa.PRGCLASSIF = st_template_classif.PRGCLASSIF ";

	public String getStatement(final RequestContainer requestContainer, final SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String query_totale = SELECT_SQL_BASE;
		
		SourceBean beanRows = (SourceBean) QueryExecutor.executeQuery("GET_CODREGIONE", null, "SELECT", "SIL_DATI");
		String regione = (String) beanRows.getAttribute("ROW.CODREGIONE");		
		
		if (regione.equals(Properties.UMB)) {
			query_totale += " where st_template_stampa.datcanc is null ";
		} else {
			query_totale += " where nvl(st_template_stampa.flgvisibile, '0') = '1' and st_template_stampa.datcanc is null ";
		}

		String codambitotemplate, template, CODTIPODOMINIO, dataInizio, dataFine, prgClassif;

		NavigationCache cache = null;
		if (requestContainer.getSessionContainer().getAttribute("TEMPLATECACHE") != null) {
			cache = (NavigationCache) requestContainer.getSessionContainer().getAttribute("TEMPLATECACHE");
			codambitotemplate = cache.getField("CODAMBITOTEM") != null ? cache.getField("CODAMBITOTEM").toString()
					: null;
			template = cache.getField("STRNOME") != null ? cache.getField("STRNOME").toString() : null;
			CODTIPODOMINIO = cache.getField("CODTIPODOMINIO") != null ? cache.getField("CODTIPODOMINIO").toString()
					: null;
			dataInizio = cache.getField("DATINIZIOVAL") != null ? cache.getField("DATINIZIOVAL").toString() : null;
			dataFine = cache.getField("DATFINEVAL") != null ? cache.getField("DATFINEVAL").toString() : null;
			prgClassif = cache.getField("PRGCLASSIF") != null ? cache.getField("PRGCLASSIF").toString() : null;
		} else {
			codambitotemplate = req.getAttribute("codAmbitoTem") != null ? req.getAttribute("codAmbitoTem").toString()
					: null;
			template = req.getAttribute("STRNOME") != null ? req.getAttribute("STRNOME").toString() : null;
			CODTIPODOMINIO = req.getAttribute("CODTIPODOMINIO") != null ? req.getAttribute("CODTIPODOMINIO").toString()
					: null;
			dataInizio = req.getAttribute("DATINIZIOVAL") != null ? req.getAttribute("DATINIZIOVAL").toString() : null;
			dataFine = req.getAttribute("DATFINEVAL") != null ? req.getAttribute("DATFINEVAL").toString() : null;
			prgClassif = req.getAttribute("PRGCLASSIF") != null ? req.getAttribute("PRGCLASSIF").toString() : null;

		}
		if (codambitotemplate != null && !codambitotemplate.equals("")) {
			query_totale += " and de_doc_tipo.codtipodocumento = '" + codambitotemplate + "'";
		}

		if (template != null && !template.equals("")) {
			query_totale += " and Upper(st_template_stampa.STRNOME) like '%" + template.toUpperCase() + "%'";
		}

		if (CODTIPODOMINIO != null && !CODTIPODOMINIO.equals("")) {
			query_totale += " and de_tipo_dominio_dati_stampa.CODTIPODOMINIO = '" + CODTIPODOMINIO + "'";
		}

		if (dataInizio != null && !dataInizio.equals("")) {
			query_totale += " and not trunc(st_template_stampa.datinizioval) < to_date('" + dataInizio
					+ "','DD/MM/YYYY')";
		}

		if (dataFine != null && !dataFine.equals("")) {
			query_totale += " and not trunc(st_template_stampa.datfineval) > to_date('" + dataFine + "','DD/MM/YYYY')";
		}

		if (prgClassif != null && !prgClassif.equals("")) {
			query_totale += " and st_template_classif.PRGCLASSIF = '" + prgClassif + "'";
		}

		query_totale += " order by st_template_stampa.ordinamento,st_template_stampa.datinizioval ASC";

		_logger.info("it.eng.sil.module.trento.TemplateList " + "::Stringa di ricerca:" + query_totale.toString());

		return query_totale;

	}

}