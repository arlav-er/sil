/*
 * Created on Jun 27, 2005
 *
 */
package it.eng.sil.module.amministrazione.accorpamento;

import java.util.ArrayList;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;

/**
 * recupera le informazioni sull'anagrafica lavoratore dei cdnLavoratore passati nella request, nel formato
 * cdnLavoratoreX, dove X=1..n, in progressione. I risultati vengono restituiti in SourceBean con chiave
 * NOME_MODULO.LAVX.ROWS.ROW
 * 
 * @author savino
 */
public class GetLavoratori extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GetLavoratori.class.getName());

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		try {
			SourceBean row = null;
			Object cdnLavoratore = null;
			ArrayList lavDaEstrarre = new ArrayList();
			int i = 1;
			for (;;) {
				cdnLavoratore = serviceRequest.getAttribute("cdnLavoratore" + i++);
				if (cdnLavoratore != null)
					lavDaEstrarre.add(cdnLavoratore);
				else
					break;
			}
			for (i = 0; i < lavDaEstrarre.size(); i++) {
				serviceRequest.updAttribute("cdnLavoratore", lavDaEstrarre.get(i).toString());
				row = doSelect(serviceRequest, serviceResponse, false);
				if (row == null)
					throw new Exception("Errore nel recupero delle info sul lavoratore " + cdnLavoratore);
				serviceResponse.setAttribute("LAV" + (i + 1), row);
			}
		} catch (Exception e) {
			ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
			reportOperation.reportFailure(MessageCodes.General.GET_ROW_FAIL);
			it.eng.sil.util.TraceWrapper.debug(_logger, "", e);

		}

	}
}