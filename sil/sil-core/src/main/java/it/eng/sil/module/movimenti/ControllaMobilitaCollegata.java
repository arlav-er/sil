package it.eng.sil.module.movimenti;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.processors.ProcessorsUtils;
import it.eng.sil.security.User;

public class ControllaMobilitaCollegata {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(ControllaMobilitaCollegata.class.getName());
	private String className;
	private TransactionQueryExecutor trans;

	public ControllaMobilitaCollegata(TransactionQueryExecutor transexec) {
		this.className = this.getClass().getName();
		this.trans = transexec;
	}

	public int esegui(BigDecimal prgMov) throws SourceBeanException {
		String statement = "";
		SourceBean result = null;
		Object updateResult = null;
		Vector ris = null;
		String statementUpdate = "";
		try {
			statement = "SELECT PRGMOBILITAISCR FROM AM_MOBILITA_ISCR WHERE PRGMOVIMENTO = " + prgMov;
			result = ProcessorsUtils.executeSelectQuery(statement, trans);
			if (result == null)
				return -1;
			ris = result.getAttributeAsVector("ROW");
			if (ris.size() > 0) {
				RequestContainer req = RequestContainer.getRequestContainer();
				SessionContainer ses = req.getSessionContainer();
				User user = (User) ses.getAttribute(User.USERID);
				BigDecimal userid = new BigDecimal(user.getCodut());
				// il movimento può essere collegato ad un'unica iscrizione
				for (int i = 0; i < ris.size(); i++) {
					SourceBean sb = (SourceBean) ris.get(i);
					statementUpdate = "UPDATE AM_MOBILITA_ISCR SET PRGMOVIMENTO = null, DTMMOD = SYSDATE, "
							+ "CDNUTMOD = " + userid + ", NUMKLOMOBISCR = NUMKLOMOBISCR + 1 "
							+ "WHERE PRGMOBILITAISCR = " + sb.getAttribute("PRGMOBILITAISCR");
					updateResult = trans.executeQueryByStringStatement(statementUpdate, null,
							TransactionQueryExecutor.UPDATE);
				}
				return 1;
			} else {
				return 0;
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "Impossibile cercare la mobilità collegata al movimento", e);

			return -1;
		}
	}

}