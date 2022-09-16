/**
 * BackingBean della pagina di visualizzazione del questionario utente
 */
package it.eng.myportal.beans.questionario;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.QuestionarioDTO;
import it.eng.myportal.entity.home.QuestionarioHome;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author iescone
 *
 */
@ManagedBean
@ViewScoped
public class VisualizzaQuestionarioBean extends AbstractBaseBean {

	/**
	 * Logger per registrare informazioni.
	 */
	protected static Log log = LogFactory.getLog(VisualizzaQuestionarioBean.class);
	
	@EJB
	QuestionarioHome questionarioHome;
	
	
	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		questionarioHome.findAll();
	}
	
	/**
	 * Calcola il punteggio della domanda in base alla stelletta cliccata.
	 * 
	 */
	public void calcolaPunteggio() {
	//	FacesContext fc = FacesContext.getCurrentInstance();
		
		QuestionarioDTO dto = (QuestionarioDTO) getExternalContext().getRequestMap().get("questionario");
		String punteggio = getRequestParameter("punteggio");
		//verifico che il punteggio sia già settato ad 1, per consentire il reset del punteggio
		if (dto.getPunteggio() == 1 && Long.parseLong(punteggio) == 1) {
			dto.setPunteggio(0);
		}else{
			dto.setPunteggio(new Integer(punteggio));
		}
	}
	
	public void salvaQuestionario() {
		addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Questionario salvato!", "Questionario salvato correttamente!"));
	}

	public QuestionarioHome getQuestionarioHome() {
		return questionarioHome;
	}


	public void setQuestionarioHome(QuestionarioHome questionarioHome) {
		this.questionarioHome = questionarioHome;
	}


}
