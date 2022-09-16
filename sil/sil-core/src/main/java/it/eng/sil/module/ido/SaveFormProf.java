package it.eng.sil.module.ido;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.module.AbstractAlternativaSimpleModule;

public class SaveFormProf extends AbstractAlternativaSimpleModule {
	public void service(SourceBean request, SourceBean response) throws EMFInternalError {
		getPrgAlternativa(request);

		int prevIdSuccess = 0;
		int prevIdElementDuplicate = 0;

		disableMessageIdSuccess();
		
		getPrgAlternativa(request);

		String codCorso = StringUtils.getAttributeStrNotNull(request, "CODCORSO");
		String prgRichiestaAz = StringUtils.getAttributeStrNotNull(request, "prgRichiestaAz");
		String prgAlternativa = StringUtils.getAttributeStrNotNull(request, "prgAlternativa");
		String prgFormProf = StringUtils.getAttributeStrNotNull(request, "prgFormProf");
		
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		
		SourceBean sbQueryDuplicate = (SourceBean)QueryExecutor.executeQuery(
				"SelectUniqueCorsoMod", 
				new Object[] { codCorso,
						prgRichiestaAz,
						prgAlternativa, prgFormProf},
				"SELECT",  
				Values.DB_SIL_DATI);

		
		if (sbQueryDuplicate!=null && sbQueryDuplicate.containsAttribute("ROW") ) {
			reportOperation.reportFailure(MessageCodes.General.ELEMENT_DUPLICATED);
		}
		else {
			boolean success= doUpdate(request, response);
			if (success) {
				reportOperation.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);
			}
			else {
				reportOperation.reportFailure(MessageCodes.General.UPDATE_FAIL);
			}		
			
		}
	}

}