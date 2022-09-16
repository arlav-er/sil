package it.eng.sil.module.movimenti;

import java.math.BigDecimal;
import java.util.List;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;

import it.eng.sil.security.GoTo;
import it.eng.sil.security.PageAttribs;
import it.eng.sil.security.User;

public class SaltoInserisciLavoratore extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(SaltoInserisciLavoratore.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		// Recupero utente
		User user = (User) sessionContainer.getAttribute(User.USERID);
		PageAttribs attributi = new PageAttribs(user, "MovimentiListaSoggettoPage");
		String strPageTarget = "";
		BigDecimal cdnFunzTarget = null;
		String strCdnFunzTarget = "";
		GoTo goTo = null;
		List myGoToes = attributi.getGoToes();
		try {
			// COLUMNS
			for (int iList = 0; iList < myGoToes.size(); iList++) {
				goTo = (GoTo) myGoToes.get(iList);
				strPageTarget = goTo.getTargetPage().toUpperCase();
				cdnFunzTarget = goTo.getTargetFunz();
				if (cdnFunzTarget != null) {
					strCdnFunzTarget = cdnFunzTarget.toString();
				}
				/*
				 * CODICE ORIGINALE MODIFIFATO IN BASSO COME DA COMMENTO
				 * 
				 * 
				 * if (strPageTarget.compareTo("ANAGDETTAGLIOPAGEANAGINS") == 0) { SourceBean row = new
				 * SourceBean("ROW"); row.setAttribute("GOPAGE", strPageTarget); row.setAttribute("GOCDNFUNZ",
				 * strCdnFunzTarget); response.setAttribute((SourceBean) row); }
				 */
				/***************************************************************
				 * ATTENZIONE: E' STATA CANCELLLATA LA PROFILATURA DELLA PAGE "ANAGDETTAGLIOPAGEANAGINS" PER CUI NEGLI
				 * ATTRIBUTI DEL SALTO VERRA' SOSTITUITA DALLA PAGE "ANAGDETTAGLIOPAGEANAG" MA BISOGNERA' COMUNQUE
				 * CHIAMARE LA PAGE "ANAGDETTAGLIOPAGEANAGINS". QUINDI LA DINAMICITA' DELLA CHIAMATA RISULTERA'
				 * 'COMPROMESSA'. (Savino 06/04/05)
				 */
				if (strPageTarget.compareTo("ANAGDETTAGLIOPAGEANAG") == 0) {
					SourceBean row = new SourceBean("ROW");
					row.setAttribute("GOPAGE", "ANAGDETTAGLIOPAGEANAGINS");
					row.setAttribute("GOCDNFUNZ", strCdnFunzTarget);
					response.setAttribute((SourceBean) row);
				}
			}
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::getStatement()", ex);

		}
	}
}