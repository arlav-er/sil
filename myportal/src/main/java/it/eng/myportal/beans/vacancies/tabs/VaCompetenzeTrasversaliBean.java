package it.eng.myportal.beans.vacancies.tabs;

import it.eng.myportal.dtos.VaCompetenzeTrasvDTO;
import it.eng.myportal.entity.home.VaCompetenzeTrasvHome;
import it.eng.myportal.entity.home.local.IVacancyEntityHome;
import it.eng.myportal.utils.Utils;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputTextarea;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

/**
 * Bean delle Competenze Trasversali
 * 
 * @author iescone, rodi
 *
 */
@ManagedBean
@ViewScoped
public class VaCompetenzeTrasversaliBean extends AbstractVacancyTabBean<VaCompetenzeTrasvDTO> {

	/**
	 * Injection degli EJB che mi servono a recuperare i dati dal DB
	 */
	
	@EJB
	VaCompetenzeTrasvHome vaCompetenzeTrasvHome;

	/**
	 * Costruisco un'istaziona vuota del DTO
	 * @return VaCompetenzeTrasvDTO
	 */	
	@Override
	public VaCompetenzeTrasvDTO buildNewDataIstance() {		
		return new VaCompetenzeTrasvDTO();
	}


	@Override
	public IVacancyEntityHome<VaCompetenzeTrasvDTO> getHome() {
		return vaCompetenzeTrasvHome;
	}

	public void validaCampi(FacesContext context, UIComponent toValidate, Object value) {
		UIComponent view = context.getViewRoot();

		/*
		 * le stringhe in input delle funzioni devono corrispondere agli id dei
		 * componenti nella pagina xhtml
		 */
		HtmlInputTextarea relInterpersonaliComponent = (HtmlInputTextarea) view
				.findComponent("competenze_trasversali:rel_interpersonali:inputTextarea");

		HtmlInputTextarea competenzeTrasversaliComponent = (HtmlInputTextarea) view
				.findComponent("competenze_trasversali:tecniche:inputTextarea");

		HtmlInputTextarea compOrganizzativeComponent = (HtmlInputTextarea) view
				.findComponent("competenze_trasversali:comp_organizzative:inputTextarea");

		String relInterpersonaliValue = null;
		if (relInterpersonaliComponent != null) {
			relInterpersonaliValue = (String) relInterpersonaliComponent.getValue();
		}
		String competenzeTrasversaliValue = null;
		if (competenzeTrasversaliComponent != null) {
			competenzeTrasversaliValue = (String) competenzeTrasversaliComponent.getValue();
		}
		String compOrganizzativeValue = null;
		if (compOrganizzativeComponent != null) {
			compOrganizzativeValue = (String) compOrganizzativeComponent.getValue();
		}

		relInterpersonaliValue = Utils.trimHTML(relInterpersonaliValue);
		competenzeTrasversaliValue = Utils.trimHTML(competenzeTrasversaliValue);
		compOrganizzativeValue = Utils.trimHTML(compOrganizzativeValue);

		if ((relInterpersonaliValue == null || relInterpersonaliValue.isEmpty())
				&& (competenzeTrasversaliValue == null || competenzeTrasversaliValue.isEmpty())
				&& (compOrganizzativeValue == null || compOrganizzativeValue.isEmpty())) {
			String message = "È necessario inserire almeno uno dei tre campi.";
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			addMessage(null, msg);

			throw new ValidatorException(msg);
		}
	}
}
