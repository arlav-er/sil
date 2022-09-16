package it.eng.myportal.beans.curriculum.pf;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;

/**
 * Base class per le sezioni del Cv. Va messo tutto il codice comune a tutte le tab/accordion
 * 
 * @author Ale
 *
 */
public class CurriculumVitaeBaseFormSection {

	public static final String SECTION_NAME = "UNDEFINED";

	protected Log log = LogFactory.getLog(CurriculumVitaeBaseFormSection.class);

	protected CurriculumVitaePfBean curriculumVitaePfBean;

	public CurriculumVitaeBaseFormSection(CurriculumVitaePfBean cvBean) {
		this.curriculumVitaePfBean = cvBean;
	}

	public CurriculumVitaePfBean getCurriculumVitaePfBean() {
		return curriculumVitaePfBean;
	}

	public void setCurriculumVitaePfBean(CurriculumVitaePfBean curriculumVitaePfBean) {
		this.curriculumVitaePfBean = curriculumVitaePfBean;
	}

	public void addAlertSuccessMessage(String title, String message) {
		title = StringEscapeUtils.escapeEcmaScript(title);
		message = StringEscapeUtils.escapeEcmaScript(message);
		RequestContext.getCurrentInstance().execute("MyPortal.successAlert('" + title + "','" + message + "')");
	}
	
	public void addJSSuccessMessage(String message) {
		message = StringEscapeUtils.escapeEcmaScript(message);
		RequestContext.getCurrentInstance().execute("MyPortal.sucessMessage('" + message + "')");
	}
	
	public void addAlertWarnMessage(String title, String message) {
		title = StringEscapeUtils.escapeEcmaScript(title);
		message = StringEscapeUtils.escapeEcmaScript(message);
		RequestContext.getCurrentInstance().execute("MyPortal.warnAlert('" + title + "','" + message + "')");
	}

	protected void addAlertErrorMessage(String title, String message) {
		title = StringEscapeUtils.escapeEcmaScript(title);
		message = StringEscapeUtils.escapeEcmaScript(message);
		RequestContext.getCurrentInstance().execute("MyPortal.errorAlert('" + title + "','" + message + "')");
	}
		
	public void addJSWarnMessage(String message) {
		message = StringEscapeUtils.escapeEcmaScript(message);
		RequestContext.getCurrentInstance().execute("MyPortal.warnMesssage('" + message + "')");
	}

	public void addJSDangerMessage(String message) {
		message = StringEscapeUtils.escapeEcmaScript(message);
		RequestContext.getCurrentInstance().execute("MyPortal.dangerMesssage('" + message + "')");
	}


}
