package it.eng.myportal.beans.curriculum.tabs;

import it.eng.myportal.dtos.CvCompetenzeTrasvDTO;
import it.eng.myportal.dtos.DeSezioneInfoDTO;
import it.eng.myportal.entity.home.CvCompetenzeTrasvHome;
import it.eng.myportal.entity.home.decodifiche.DeSezioneInfoHome;
import it.eng.myportal.entity.home.local.ICurriculumEntityHome;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputTextarea;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

/**
 * Bean dell'Istruzione
 * 
 * @author Rodi A.
 * 
 */
@ManagedBean
@ViewScoped
public class CvCompetenzeTrasversaliBean extends AbstractCurriculumTabBean<CvCompetenzeTrasvDTO> {

	/**
	 * Injection degli EJB che mi servono a recuperare i dati dal DB
	 */

	@EJB
	CvCompetenzeTrasvHome cvCompetenzeTrasvHome;
	
	@EJB
	DeSezioneInfoHome deSezioneInfoHome;
	
	DeSezioneInfoDTO info;

	/**
	 * Costruisco un'istaziona vuota del DTO
	 * 
	 * @return CvCompetenzeTrasvDTO
	 */
	@Override
	public CvCompetenzeTrasvDTO buildNewDataIstance() {
		return new CvCompetenzeTrasvDTO();
	}
	
	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		info = deSezioneInfoHome.findDTOById(ConstantsSingleton.DeServizioInfo.COD_CV_COMPETENZE_TRASVERSALI);
	}

	@Override
	public ICurriculumEntityHome<CvCompetenzeTrasvDTO> getHome() {
		return cvCompetenzeTrasvHome;
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
			String message = "È necessario inserire almeno uno dei primi tre campi.";
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			addMessage(null, msg);

			throw new ValidatorException(msg);
		}
	}
	
	public DeSezioneInfoDTO getInfo() {
		return info;
	}

	public void setInfo(DeSezioneInfoDTO info) {
		this.info = info;
	}
}
