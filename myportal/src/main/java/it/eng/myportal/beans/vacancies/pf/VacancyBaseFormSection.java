package it.eng.myportal.beans.vacancies.pf;

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
public class VacancyBaseFormSection {

	public static final String SECTION_NAME = "UNDEFINED";

	protected Log log = LogFactory.getLog(VacancyBaseFormSection.class);

	protected VacancyFormPfBean vacancyFormPfBean;

	public VacancyBaseFormSection(VacancyFormPfBean cvBean) {
		this.vacancyFormPfBean = cvBean;
	}

	public VacancyFormPfBean getVacancyFormPfBean() {
		return vacancyFormPfBean;
	}

	public void setVacancyFormPfBeann(VacancyFormPfBean curriculumVitaePfBean) {
		this.vacancyFormPfBean = curriculumVitaePfBean;
	}
	
	public void addJSSuccessMessage(String message) {
		message = StringEscapeUtils.escapeEcmaScript(message);
		RequestContext.getCurrentInstance().execute("MyPortal.sucessMessage('" + message + "')");
	}

	public void addAlertSuccessMessage(String title, String message) {
		title = StringEscapeUtils.escapeEcmaScript(title);
		message = StringEscapeUtils.escapeEcmaScript(message);
		RequestContext.getCurrentInstance().execute("MyPortal.successAlert('" + title + "','" + message + "')");
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

}
