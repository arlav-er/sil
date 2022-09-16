package it.eng.myportal.beans.messaggi;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.entity.home.MsgMessaggioHome;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * BackingBean della sezione Messaggi della Provincia
 * 
 * @author Rodi A.
 * 
 */
@ManagedBean
@ViewScoped
public class ProvinciaMessagesBean extends AbstractBaseBean {

	@EJB
	MsgMessaggioHome msgMessaggioHome;

	private Integer nuoviMsgNotifica = 0;
	private Integer nuoviMsgSupporto = 0;
	private Integer nuoviMsgCertificati = 0;
	private Integer nuoviMsgEsperto = 0;
	private Integer nuoviMsgPrimoContatto = 0;
	private Integer nuoviMsgRispostaPrimoContatto = 0;
	private Integer nuoviMsgContatti = 0;

	private String tab2open;

	/**
	 * Questo metodo viene chiamato subito dopo la creazione del bean.
	 */
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		
		// Controllo che l'utente sia effettivamente una provincia.
		if (!session.isProvincia()) {
			addErrorMessage("provincia.is_not");
			redirectHome();
		}
		
		// Clean-up delle informazioni in sessione
		session.getParams().clear();
		
		// Questa parte è commentata: per ora togliamo i numerini nelle tab.
		/*Map<String, Integer> res = msgMessaggioHome.findNumMessaggiNonLetti(getSession().getPrincipalId());
		nuoviMsgCertificati = res.get(MsgMessaggio.CERTIFICATO);
		nuoviMsgEsperto = res.get(MsgMessaggio.ESPERTO);
		nuoviMsgNotifica = res.get(MsgMessaggio.NOTIFICA);
		nuoviMsgSupporto = res.get(MsgMessaggio.SUPPORTO);
		nuoviMsgPrimoContatto = res.get(MsgMessaggio.PRIMO_CONTATTO);
		nuoviMsgRispostaPrimoContatto = res.get(MsgMessaggio.RISPOSTA_CONTATTO);
		nuoviMsgContatti = nuoviMsgPrimoContatto + nuoviMsgRispostaPrimoContatto;*/
		return;
	}

	public Integer getNuoviMsgCertificati() {
		return nuoviMsgCertificati;
	}

	public void setNuoviMsgCertificati(Integer nuoviMsgCertificati) {
		this.nuoviMsgCertificati = nuoviMsgCertificati;
	}

	public Integer getNuoviMsgEsperto() {
		return nuoviMsgEsperto;
	}

	public void setNuoviMsgEsperto(Integer nuoviMsgEsperto) {
		this.nuoviMsgEsperto = nuoviMsgEsperto;
	}

	public Integer getNuoviMsgNotifica() {
		return nuoviMsgNotifica;
	}

	public void setNuoviMsgNotifica(Integer nuoviMsgNotifica) {
		this.nuoviMsgNotifica = nuoviMsgNotifica;
	}

	public Integer getNuoviMsgSupporto() {
		return nuoviMsgSupporto;
	}

	public void setNuoviMsgSupporto(Integer nuoviMsgSupporto) {
		this.nuoviMsgSupporto = nuoviMsgSupporto;
	}

	public Integer getNuoviMsgPrimoContatto() {
		return nuoviMsgPrimoContatto;
	}

	public void setNuoviMsgPrimoContatto(Integer nuoviMsgPrimoContatto) {
		this.nuoviMsgPrimoContatto = nuoviMsgPrimoContatto;
	}

	public Integer getNuoviMsgRispostaPrimoContatto() {
		return nuoviMsgRispostaPrimoContatto;
	}

	public void setNuoviMsgRispostaPrimoContatto(Integer nuoviMsgRispostaPrimoContatto) {
		this.nuoviMsgRispostaPrimoContatto = nuoviMsgRispostaPrimoContatto;
	}

	public Integer getNuoviMsgContatti() {
		return nuoviMsgContatti;
	}

	public void setNuoviMsgContatti(Integer nuoviMsgContatti) {
		this.nuoviMsgContatti = nuoviMsgContatti;
	}

	public String getTab2open() {
		return tab2open;
	}

	public void setTab2open(String tab2open) {
		this.tab2open = tab2open;
	}
}
