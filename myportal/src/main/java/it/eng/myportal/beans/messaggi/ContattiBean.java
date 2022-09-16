package it.eng.myportal.beans.messaggi;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.entity.home.MsgMessaggioHome;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 * BackingBean della tab CONTATTI nella sezione Messaggi dell'Utente
 * 
 * @author Rodi A.
 * 
 */
@ManagedBean
@ViewScoped
public class ContattiBean extends AbstractBaseBean {

	private static final String CONTATTI = "contatti";

	@EJB
	MsgMessaggioHome msgMessaggioHome;

	// COPIATO DA NOTIFICHE BEAN MA ANDREBBE RIVISTA LA GERARCHIA DEI BEAN
	// MAGARI...
	UtenteMessagesBean utenteMessagesBean;
	AziendaMessagesBean aziendaMessagesBean;
	
	private Integer nuoviMsgInviati;
	private Integer nuoviMsgRicevuti;

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();

		nuoviMsgInviati = 0;
		nuoviMsgRicevuti = 0;

		popolaMasterMsgBean();
		setTab2open(CONTATTI);

	}

	/**
	 * carica il corretto bean in base all'utente loggato
	 */
	private void popolaMasterMsgBean() {
		FacesContext context = FacesContext.getCurrentInstance();
		if (session.isAzienda()) {
			setAziendaMessagesBean(context.getApplication().evaluateExpressionGet(context, "#{aziendaMessagesBean}",
					AziendaMessagesBean.class));
		} else if (session.isUtente()) {
			setUtenteMessagesBean(context.getApplication().evaluateExpressionGet(context, "#{utenteMessagesBean}",
					UtenteMessagesBean.class));
		}
	}

	/**
	 * @param tab2open
	 *            - salvo in sessione la tab aperta, cosi' da riaprirla in caso
	 *            di torna indietro
	 */
	public void setTab2open(String tab2open) {
		if (session.isAzienda()) {
			getAziendaMessagesBean().setTab2open(tab2open);
		} else if (session.isUtente()) {
			getUtenteMessagesBean().setTab2open(tab2open);
		}
	}

	public UtenteMessagesBean getUtenteMessagesBean() {
		return utenteMessagesBean;
	}

	public void setUtenteMessagesBean(UtenteMessagesBean utenteMessagesBean) {
		this.utenteMessagesBean = utenteMessagesBean;
	}

	public AziendaMessagesBean getAziendaMessagesBean() {
		return aziendaMessagesBean;
	}

	public void setAziendaMessagesBean(AziendaMessagesBean aziendaMessagesBean) {
		this.aziendaMessagesBean = aziendaMessagesBean;
	}

	public Integer getNuoviMsgInviati() {
		return nuoviMsgInviati;
	}

	public void setNuoviMsgInviati(Integer nuoviMsgInviati) {
		this.nuoviMsgInviati = nuoviMsgInviati;
	}

	public Integer getNuoviMsgRicevuti() {
		return nuoviMsgRicevuti;
	}

	public void setNuoviMsgRicevuti(Integer nuoviMsgRicevuti) {
		this.nuoviMsgRicevuti = nuoviMsgRicevuti;
	}

}
