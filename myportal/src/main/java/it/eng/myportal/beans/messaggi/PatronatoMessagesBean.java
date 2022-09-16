package it.eng.myportal.beans.messaggi;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.entity.home.MsgMessaggioHome;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * BackingBean della sezione Messaggi della Regione.
 * Dato che i numeri nelle tab sono disabilitati, al momento non fa altro che controllare che
 * l'utente sia effettivamente di tipo REGIONE.
 * 
 * @author Rodi A.
 */

@ManagedBean
@ViewScoped
public class PatronatoMessagesBean extends AbstractBaseBean {

	@EJB
	MsgMessaggioHome msgMessaggioHome;

	private Integer nuoviMsgNotifica = 0;
	private String tab2open;

	/**
	 * Questo metodo viene chiamato automaticamente alla creazione del bean.
	 */
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		
		// Se l'utente non è di tipo REGIONE, viene rimandato alla home.
		if (!session.isPatronato()) {
			addErrorMessage("patronato.is_not");
			redirectHome();
		}
		
		// Clean-up delle informazioni in sessione
		session.getParams().clear();

		return;
	}

	/*
	 * ========================== DA QUI IN POI, CI SONO SEMPLICI GETTER E SETTER =======================
	 */
	
	public Integer getNuoviMsgNotifica() {
		return nuoviMsgNotifica;
	}

	public void setNuoviMsgNotifica(Integer nuoviMsgNotifica) {
		this.nuoviMsgNotifica = nuoviMsgNotifica;
	}

	public String getTab2open() {
		return tab2open;
	}

	public void setTab2open(String tab2open) {
		this.tab2open = tab2open;
	}
}
