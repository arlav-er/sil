package it.eng.myportal.beans.messaggi;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.entity.home.MsgMessaggioHome;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

/**
 * Backing bean della sezione messaggi per gli utenti di tipo SOGGETTO PUBBLICO.
 * Dato che al momento i numerini nelle tab sono disabilitati, tutto ciò che fa è controllare che
 * l'utente sia effettivamente un certificatore e, in caso contrario, ridirezionarlo verso la home.
 * 
 * @author gcozza
 */
public class SoggettoPubblicoMessagesBean extends AbstractBaseBean {
	
	@EJB
	MsgMessaggioHome msgMessaggioHome;

	private Integer nuoviMsgNotifica = 0;

	private String tab2open;
	
	/**
	 * Questo metodo viene chiamato subito dopo la creazione del bean.
	 */
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		
		// Controllo che l'utente sia effettivamente un certificatore.
		if (!session.isSoggettoPubblico()) {
			addErrorMessage("soggetto.is_not");
			redirectHome();
		}
		
		// Clean-up delle informazioni in sessione
		session.getParams().clear();
		
		// Questa parte è commentata: per ora togliamo i numerini nelle tab.
		/*Map<String, Integer> res = msgMessaggioHome.findNumMessaggiNonLetti(getSession().getPrincipalId());
		nuoviMsgNotifica = res.get(MsgMessaggio.NOTIFICA); */
		return;
	}
	
	/*
	 * ========================== DA QUI IN POI CI SONO SEMPLICI GETTER E SETTER ================================
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
