package it.eng.myportal.beans.messaggi;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.RestoreParameters;
import it.eng.myportal.beans.SessionBean;
import it.eng.myportal.entity.home.MsgMessaggioHome;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.ObjectUtils;

/**
 * BackingBean della sezione Messaggi dell'Azienda
 * 
 * @author Rodi A.
 * 
 */
@ManagedBean
@ViewScoped
public class AziendaMessagesBean extends AbstractBaseBean {

	private static final String TAB_2_OPEN = "tab2open";
	private static final String OPEN_ELEM_ID = "openElemId";

	@EJB
	MsgMessaggioHome msgMessaggioHome;

	private Integer nuoviMsgCertificati = 0;
	private Integer nuoviMsgEsperto = 0;
	private Integer nuoviMsgNotifica = 0;
	private Integer nuoviMsgPrimoContatto = 0;
	private Integer nuoviMsgRispostaPrimoContatto = 0;
	private Integer nuoviMsgContatti = 0;
	
	private String tab2open;
	private String openElemId;

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		if (!session.isAzienda()) {
			addErrorMessage("azienda.is_not");
			redirectHome();
		}
		// Clean-up delle informazioni in sessione
		session.getParams().clear();
		putParamsIntoSession();
		
		// Tutta questa parte è commentata: per ora non mettiamo i numerini nelle tab.
		/*Map<String, Integer> res = msgMessaggioHome.findNumMessaggiNonLetti(getSession().getPrincipalId());
		nuoviMsgCertificati = res.get(MsgMessaggio.CERTIFICATO);
		nuoviMsgEsperto = res.get(MsgMessaggio.ESPERTO);
		nuoviMsgNotifica = res.get(MsgMessaggio.NOTIFICA);
		nuoviMsgPrimoContatto = res.get(MsgMessaggio.PRIMO_CONTATTO);
		nuoviMsgRispostaPrimoContatto = res.get(MsgMessaggio.RISPOSTA_CONTATTO);
		nuoviMsgContatti = nuoviMsgPrimoContatto + nuoviMsgRispostaPrimoContatto;*/

		/*
		 * se il parametro "sezione" e' valorizzato come "assiztenza_tecnica"
		 * allora vado subito nella sezione "Richiesta di consulenza" con una
		 * nuova richiesta di "assistenza tecnica"
		 */
		Map<String, String> map = getRequestParameterMap();
		String tema = map.get("tema");
		SessionBean sessionBean = getSession();
		if ("assistenza_tecnica".equals(tema)) {
			sessionBean.setAssistenzaTecnica(true);
		}
		
		sessionBean.setTabToOpen(map.get("tab"));
		String idStr = map.get("id");
		sessionBean.setIdMsgMessaggio((idStr == null) ? null : Integer.valueOf(idStr));

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

	public Integer getNuoviMsgContatti() {
		return nuoviMsgContatti;
	}

	public void setNuoviMsgContatti(Integer nuoviMsgContatti) {
		this.nuoviMsgContatti = nuoviMsgContatti;
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

	public String getTab2open() {
		return tab2open;
	}

	public void setTab2open(String tab2open) {
		this.tab2open = tab2open;
		putParamsIntoSession();
	}

	public String getOpenElemId() {
		return openElemId;
	}

	public void setOpenElemId(String openElemId) {
		this.openElemId = openElemId;
		putParamsIntoSession();
	}

	@Override
	protected RestoreParameters generateRestoreParams() {
		RestoreParameters rp = super.generateRestoreParams();
		rp.put(TAB_2_OPEN, tab2open);
		rp.put(OPEN_ELEM_ID, openElemId);
		return rp;
	}

	@Override
	protected void ricreaStatoDaSessione(RestoreParameters restoreParams) {
		super.ricreaStatoDaSessione(restoreParams);
		tab2open = ObjectUtils.toString(restoreParams.get(TAB_2_OPEN));
		openElemId = ObjectUtils.toString(restoreParams.get(OPEN_ELEM_ID));
	}

}
