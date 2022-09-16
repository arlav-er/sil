package it.eng.sil.module.patto;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

public class CondizioneOccupazionale extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CondizioneOccupazionale.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		try {
			SourceBean stm = null;
			SourceBean bean = null;

			//
			/*
			 * bean = execute(getStm("QUERIES.SELECT_QUERY0")); response.setAttribute("INFO_CORRENTI", bean);
			 * it.eng.sil.util.TraceWrapper.debug( _logger, className + "::select:", bean);
			 * 
			 */
			//
			bean = execute(getStm("QUERIES.QUERY_OBBLIGO_FORMATIVO"));
			response.setAttribute("OBBLIGO_FORMATIVO", bean);
			log(bean);
			bean = execute(getStm("QUERIES.QUERY_PERM_SOGG"));
			response.setAttribute("PERMESSO_SOGGIORNO", bean);
			log(bean);

			//
			bean = execute(getStm("QUERIES.QUERY_STATO_OCCUPAZIONALE"));
			response.setAttribute("STATO_OCCUPAZIONALE", bean);
			log(bean);
			//
			//
			bean = execute(getStm("QUERIES.QUERY_ISCR_MOBILITA"));
			response.setAttribute("MOBILITA", bean);
			log(bean);
			//
			bean = execute(getStm("QUERIES.QUERY_ISRC_COLLOCAMENTO_MIRATO"));
			response.setAttribute("COLLOCAMENTO_MIRATO", bean);
			log(bean);
			//
			bean = execute(getStm("QUERIES.QUERY_INDISP_TEMP"));
			response.setAttribute("INDISPONIBILITA_TEMP", bean);
			log(bean);

			bean = execute(getStm("QUERIES.QUERY_FORMAZIONE_PROF"));
			response.setAttribute("FORMAZIONE_PROFESSIONALE", bean);
			log(bean);
			//

			bean = execute(getStm("QUERIES.QUERY_TITOLI_STUDIO"));
			response.setAttribute("TITOLI_LAVORATORE", bean);
			log(bean);
			//
			bean = execute(getStm("QUERIES.QUERY_ESPERIENZE_LAV"));
			response.setAttribute("MOVIMENTI_PRECEDENTI", bean);
			log(bean);
			//
			Object rowObject = null;

		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					className + "::select: response.setAttribute((SourceBean)rowObject)", ex);

		}
		// catch (Exception ex)
	}

	private SourceBean execute(SourceBean stm) throws Exception {
		return (SourceBean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(),
				(String) getConfig().getAttribute("POOL"), stm, "SELECT");
	}

	private SourceBean getStm(String queryName) {
		return (SourceBean) getConfig().getAttribute(queryName);
	}

	private void log(SourceBean bean) {
		it.eng.sil.util.TraceWrapper.debug(_logger, className + "::select:", bean);

	}
}