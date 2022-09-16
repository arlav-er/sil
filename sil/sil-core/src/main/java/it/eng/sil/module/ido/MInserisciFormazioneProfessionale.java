package it.eng.sil.module.ido;

import java.math.BigDecimal;
import java.util.Collection;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractAlternativaSimpleModule;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.collectionexecutor.Action;
import it.eng.sil.util.collectionexecutor.ActionException;
import it.eng.sil.util.collectionexecutor.CollectionExecutor;
import it.eng.sil.util.collectionexecutor.InsertElementAction;

public class MInserisciFormazioneProfessionale extends AbstractAlternativaSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		
		int prevIdSuccess = 0;
		int prevIdElementDuplicate = 0;

		disableMessageIdSuccess();
		
		getPrgAlternativa(request);

		String codCorso = StringUtils.getAttributeStrNotNull(request, "CODCORSO");
		String prgRichiestaAz = StringUtils.getAttributeStrNotNull(request, "prgRichiestaAz");
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		

		this.setSectionQuerySelect("QUERY_SELECT");
		SourceBean sbQueryDuplicate = doSelect(request, response);
		
		if (sbQueryDuplicate!=null && sbQueryDuplicate.containsAttribute("ROW") ) {
			reportOperation.reportFailure(MessageCodes.General.ELEMENT_DUPLICATED);
		}
		else {
			this.setSectionQueryInsert("QUERY_INSERT");
			boolean success= doInsertNoDuplicate(request, response);
			if (success) {
				reportOperation.reportSuccess(MessageCodes.General.INSERT_SUCCESS);
			}
			else {
				reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);
			}			
		}

	}
}
