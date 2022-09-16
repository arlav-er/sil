package it.eng.myportal.beans.amministrazione;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.entity.ejb.DbManagerEjb;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@ManagedBean
@ViewScoped
public class DbManagerBean extends AbstractBaseBean {
	//private static final int STEP = 10;

	protected static Log log = LogFactory.getLog(DbManagerBean.class);

	@EJB
	private transient DbManagerEjb dbManagerEjb;
		
	
	
	
	public String adeguaMessaggi() {
		FacesMessage doneMessage = new FacesMessage();
		String msg = "";
		try {
			int num = dbManagerEjb.adeguaMessaggi();
			msg = num  + " messaggi adeguati";

		} catch (EJBException e) {
			msg = e.getMessage();
		}
		doneMessage.setDetail(msg);
		FacesContext.getCurrentInstance().addMessage(
				"myform:update_messaggi", doneMessage);
		return "";
	}

	/**
	 * procedura SPOT 1.3.0
	 * @author pegoraro
	 */
	public void adeguaNoteCurriculum() {
		FacesMessage doneMessage = new FacesMessage();
		String msg = "";
		try {
			int res = dbManagerEjb.adeguaNoteCurriculum();
			msg = res  + " note curriculum aggiunte";
		} catch (EJBException e) {
			gestisciErrore(e, "generic.error");
		}
		doneMessage.setDetail(msg);
		FacesContext.getCurrentInstance().addMessage(
				"myform:adeguaNoteCurriculum", doneMessage);
	}
	/**
	 * procedura SPOT 1.3.0
	 * @author pegoraro
	 */
	public void adeguaNoteVacancy() {
		FacesMessage doneMessage = new FacesMessage();
		String msg = "";
		try {
			int res = dbManagerEjb.adeguaNoteVacancy();
			msg = res  + " note Vacancy aggiunte";
		} catch (EJBException e) {
			gestisciErrore(e, "generic.error");
		}
		doneMessage.setDetail(msg);
		FacesContext.getCurrentInstance().addMessage(
				"myform:adeguaNoteVacancy", doneMessage);
	}



	public DbManagerEjb getDbManagerEjb() {
		return dbManagerEjb;
	}

	

	public DbManagerEjb getTestEjb() {
		return dbManagerEjb;
	}

	
	

	

	public void setDbManagerEjb(DbManagerEjb dbManagerEjb) {
		this.dbManagerEjb = dbManagerEjb;
	}

	

	public void setTestEjb(DbManagerEjb testEjb) {
		this.dbManagerEjb = testEjb;
	}

	

}
