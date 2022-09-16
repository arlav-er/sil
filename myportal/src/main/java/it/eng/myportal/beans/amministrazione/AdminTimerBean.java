package it.eng.myportal.beans.amministrazione;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.entity.ejb.ts.TsTimerEJB;
import it.eng.myportal.timer.TimerSendAppNotificaSingleton;
import it.eng.myportal.timer.TimerSendEmailVacancyInLavorazioneSingleton;
import it.eng.myportal.timer.TimerSendNotificaCVInScadenzaSingleton;
import it.eng.myportal.timer.TimerUpdaterSolrSingleton;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.URL;
import it.eng.myportal.utils.Utils;

@ManagedBean
@ViewScoped
public class AdminTimerBean extends AbstractBaseBean {

	private static final long MSEC_200 = 200;
	@EJB
	TimerUpdaterSolrSingleton timerUpdaterSolrSingleton;
	private boolean timerUpdaterSolrSingletonEnabled;

	@EJB
	TimerSendAppNotificaSingleton timerSendAppNotificaSingleton;
	private boolean timerSendAppNotificaSingletonEnabled;

	@EJB
	TimerSendEmailVacancyInLavorazioneSingleton timerSendEmailVacancyInLavorazioneSingleton;
	private boolean timerSendEmailVacancyInLavorazioneSingletonEnabled;

	@EJB
	TimerSendNotificaCVInScadenzaSingleton timerSendNotificaCVInScadenzaSingleton;
	private boolean timerSendNotificaCVInScadenzaSingletonEnabled;

	@EJB
	TsTimerEJB tsTimerEJB;
	
	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		timerUpdaterSolrSingletonEnabled  = timerUpdaterSolrSingleton.isLogicEnabled();
		timerSendAppNotificaSingletonEnabled = timerSendAppNotificaSingleton.isLogicEnabled();
		timerSendEmailVacancyInLavorazioneSingletonEnabled = timerSendEmailVacancyInLavorazioneSingleton.isLogicEnabled();
		timerSendNotificaCVInScadenzaSingletonEnabled = timerSendNotificaCVInScadenzaSingleton.isLogicEnabled();
	}

	private String paramQuery;
	private String xmlReturnSolr;

	public void invokeManualSOLR() {
		timerUpdaterSolrSingleton.setTimer(MSEC_200);
	}
	
	public void invokeManualInvioNotifiche() {
		timerSendAppNotificaSingleton.setTimer(MSEC_200);
	}
	
	public void  invokeManualVacInLavInvioNotifiche() {
		timerSendEmailVacancyInLavorazioneSingleton.setTimer(MSEC_200);
	}
	public void  invokeManualCvInScadInvioNotifiche() {
		timerSendNotificaCVInScadenzaSingleton.setTimer(MSEC_200);
	}

	public String getLastSOLRExecution() {
		return timerUpdaterSolrSingleton.getLastProgrammaticTimeout();
	}

	public String getLastInvioNotificheExecution() {
		return timerSendAppNotificaSingleton.getLastProgrammaticTimeout();
	}

	public String getLastInvioNotificheScheduledExecution() {
		return timerSendAppNotificaSingleton.getLastAutomaticTimeout();
	}

	public String getLastSOLRScheduledExecution() {
		return timerUpdaterSolrSingleton.getLastAutomaticTimeout();
	}

	public boolean isSOLRTimerEnabled() {
		return timerUpdaterSolrSingletonEnabled;
	}

	public boolean isInvioNotificheTimerEnabled() {
		return timerSendAppNotificaSingletonEnabled;
	}

	public void setSOLRTimerEnabled(boolean val) {
		timerUpdaterSolrSingletonEnabled = timerUpdaterSolrSingleton.setLogicEnabled(val);
	}

	public void setInvioNotificheTimerEnabled(boolean val) {
		timerSendAppNotificaSingletonEnabled = timerSendAppNotificaSingleton.setLogicEnabled(val);
	}
	
	public String getLastVacInLavInvioNotificheExecution() {
		return timerSendEmailVacancyInLavorazioneSingleton.getLastProgrammaticTimeout();
	}

	public String getLastVacInLavInvioNotificheScheduledExecution() {
		return timerSendEmailVacancyInLavorazioneSingleton.getLastAutomaticTimeout();
	}

	public boolean isVacInLavInvioNotificheTimerEnabled() {
		return timerSendEmailVacancyInLavorazioneSingletonEnabled;
	}

	public void setVacInLavInvioNotificheTimerEnabled(boolean val) {
		timerSendEmailVacancyInLavorazioneSingletonEnabled = timerSendEmailVacancyInLavorazioneSingleton.setLogicEnabled(val);
	}

	public String getLastCvInScadInvioNotificheExecution() {
		return timerSendNotificaCVInScadenzaSingleton.getLastProgrammaticTimeout();
	}

	public String getLastCvInScadInvioNotificheScheduledExecution() {
		return timerSendNotificaCVInScadenzaSingleton.getLastAutomaticTimeout();
	}

	public boolean isCvInScadInvioNotificheTimerEnabled() {
		return timerSendNotificaCVInScadenzaSingletonEnabled;
	}

	public void setCvInScadInvioNotificheTimerEnabled(boolean val) {
		timerSendNotificaCVInScadenzaSingletonEnabled = timerSendNotificaCVInScadenzaSingleton.setLogicEnabled(val);
	}


	
	@Deprecated // per me da eliminare, usando quello via timer
	public void recreateAll() {
		try {
			String baseDominio = ConstantsSingleton.getSolrUrl();
			String url = baseDominio + "/core0/import?command=full-import&clean=true";
			Utils.documentSOLR(url);
		} catch (EJBException e) {
			gestisciErrore(e, "generic.error");
		}
	}

	@Deprecated // per me da spostare nel EJB di SOLR, funzionalita` discutibile comunque
	public void eseguiQuerySolr() {
		try {
			String url = "";
			String baseDominio = ConstantsSingleton.getSolrUrl();

			if (paramQuery != null && !("").equalsIgnoreCase(paramQuery)) {
				url = baseDominio + URL.escapeSolr("/core0/select?rows=10&" + paramQuery);
			} else {
				url = baseDominio + "/core0/select?q=*%3A*&rows=10";
			}

			Document domXmlSolr = Utils.documentSOLR(url);

			try {
				String strXmlSolr = Utils.domToString(domXmlSolr);
				log.info("SOLR result per amministratore: " + strXmlSolr);
				setXmlReturnSolr(strXmlSolr);
			} catch (TransformerException e) {
				throw new EJBException(e);
			}

		} catch (EJBException e) {
			gestisciErrore(e, "generic.error");
		}
	}

	public String getParamQuery() {
		return paramQuery;
	}

	public void setParamQuery(String paramQuery) {
		this.paramQuery = paramQuery;
	}

	public String getXmlReturnSolr() {
		return xmlReturnSolr;
	}

	public void setXmlReturnSolr(String xmlReturnSolr) {
		this.xmlReturnSolr = xmlReturnSolr;
	}
}
