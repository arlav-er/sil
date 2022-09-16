package it.eng.sil.module.trento;

import java.io.BufferedReader;
import java.io.Reader;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.agenda.SelectAgendaModule;
import oracle.sql.CLOB;

public class MDettaglioEditor extends AbstractSimpleModule {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SelectAgendaModule.class.getName());

	public MDettaglioEditor() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		String pool = (String) getConfig().getAttribute("POOL");
		SourceBean statement = null;
		SourceBean rowsSourceBean = null;

		try {

			statement = (SourceBean) getConfig().getAttribute("QUERIES.SELECT_QUERY");
			rowsSourceBean = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(),
					pool, statement, "SELECT");
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::select: rowsSourceBean", rowsSourceBean);
			String clobdata = "";
			if (rowsSourceBean.getAttribute("ROW.FILETEMPLATE") != null) {
				oracle.sql.CLOB filetemplate = (CLOB) rowsSourceBean.getAttribute("ROW.FILETEMPLATE");

				StringBuffer sb1 = new StringBuffer();

				Reader reader = filetemplate.getCharacterStream();
				BufferedReader in = new BufferedReader(reader);
				String line = null;
				while ((line = in.readLine()) != null) {
					sb1.append(line);
				}
				if (in != null) {
					in.close();
				}

				clobdata = sb1.toString(); // this is the clob data converted into string
			}

			rowsSourceBean.updAttribute("ROW.FILETEMPLATE", clobdata);
			response.setAttribute(rowsSourceBean);

		} // try

		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					className + "::select: response.setAttribute((SourceBean)rowObject)", ex);

		} // catch (Exception ex)
	}

}