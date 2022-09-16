package it.eng.sil.myaccount.controller.mbeans.profile;

import it.eng.sil.myaccount.controller.mbeans.AbstractSecureBackingBean;
import it.eng.sil.myaccount.model.ejb.stateless.profile.AziendaInfoEJB;
import it.eng.sil.myaccount.model.ejb.stateless.profile.AziendaInfoRettificaEJB;
import it.eng.sil.myaccount.model.enums.TipoAbilitato;
import it.eng.sil.myaccount.model.enums.TipoDelegato;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.decodifiche.DeTipoDelegato;
import it.eng.sil.mycas.model.entity.profilo.AziendaInfo;
import it.eng.sil.mycas.model.entity.profilo.AziendaInfoRettifica;
import it.eng.sil.mycas.model.manager.decodifiche.DeComuneEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeProvinciaEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeTipoAbilitatoEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeTipoDelegatoEJB;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ViewScoped
@ManagedBean
public class AziendaEditBean extends AbstractSecureBackingBean implements Serializable {

	private static final long serialVersionUID = -4330843777739139874L;

	@EJB
	private AziendaInfoEJB aziendaInfoEJB;

	@EJB
	private AziendaInfoRettificaEJB aziendaInfoRettificaEJB;

	@EJB
	private DeComuneEJB deComuneEJB;

	@EJB
	private DeProvinciaEJB deProvinciaEJB;

	@EJB
	private DeTipoAbilitatoEJB deTipoAbilitatoEJB;

	@EJB
	private DeTipoDelegatoEJB deTipoDelegatoEJB;

	@EJB
	private DeTipoDelegatoEJB delegatoEJB;

	private AziendaInfo aziendaInfo;

	private Boolean showAgenziaSomministrazioneSection;
	private Boolean showDatiSoggettoAbilitatoSection;
	private Boolean editTipoDelegato;
	private Boolean editSoggettoAbilitato;
	private Boolean editAgenziaSomministrazione;

	private Boolean rettificaAlreadyExists;
	private AziendaInfoRettifica aziendaInfoRettifica;

	private DeTipoDelegato soggettoIntermediazione;
	private DeTipoDelegato promotoreTirocini;

	private String oldEmail;

	@Override
	protected void initPostConstruct() {
		initUser();
		tipoAbilitatoChanged();
	}

	public void initUser() {
		Integer idPfPrincipal = accountInfoBean.getUserInfo().getIdPfPrincipal();
		aziendaInfo = aziendaInfoEJB.loadUser(idPfPrincipal);
		oldEmail = aziendaInfo.getPfPrincipal().getEmail();

		// check se esiste già una rettifica
		try {
			aziendaInfoRettifica = aziendaInfoRettificaEJB.findById(idPfPrincipal);
			rettificaAlreadyExists = true;
			String msgok = utils.getUiProperty("msg.account.rettifica.already");
			addJSWarnMessage(msgok);
		} catch (MyCasNoResultException e) {
			aziendaInfoRettifica = new AziendaInfoRettifica();
			rettificaAlreadyExists = false;
			log.info("Rettifica non ancora presente.");
		}

		// Per evitare LazyLoadingException
		try {
			if (aziendaInfoRettifica != null) {
				if (aziendaInfoRettifica.getDeComuneSede() != null)
					aziendaInfoRettifica.setDeComuneSede(deComuneEJB.findById(aziendaInfoRettifica.getDeComuneSede()
							.getCodCom()));

				if (aziendaInfoRettifica.getDeComuneNascitaRic() != null)
					aziendaInfoRettifica.setDeComuneNascitaRic(deComuneEJB.findById(aziendaInfoRettifica
							.getDeComuneNascitaRic().getCodCom()));

				if (aziendaInfoRettifica.getDeComuneRichiedente() != null)
					aziendaInfoRettifica.setDeComuneRichiedente(deComuneEJB.findById(aziendaInfoRettifica
							.getDeComuneRichiedente().getCodCom()));

				if (aziendaInfoRettifica.getDeTipoAbilitato() != null)
					aziendaInfoRettifica.setDeTipoAbilitato(deTipoAbilitatoEJB.findById(aziendaInfoRettifica
							.getDeTipoAbilitato().getCodTipoAbilitato()));

				if (aziendaInfoRettifica.getDeTipoDelegato() != null)
					aziendaInfoRettifica.setDeTipoDelegato(deTipoDelegatoEJB.findById(aziendaInfoRettifica
							.getDeTipoDelegato().getCodTipoDelegato()));

				if (aziendaInfoRettifica.getDeComuneSedeLegale() != null)
					aziendaInfoRettifica.setDeComuneSedeLegale(deComuneEJB.findById(aziendaInfoRettifica
							.getDeComuneSedeLegale().getCodCom()));

				if (aziendaInfoRettifica.getDeComuneIscrizione() != null)
					aziendaInfoRettifica.setDeComuneIscrizione(deComuneEJB.findById(aziendaInfoRettifica
							.getDeComuneIscrizione().getCodCom()));
			}
		} catch (MyCasNoResultException e) {
			log.error(e);
			addJSDangerMessage("Errore nel caricamento della rettifica precedente.");
			// new RuntimeException("Errore nel caricamento della rettifica precedente.", e);
		}

	}

	public void tipoAbilitatoChanged() {
		if (aziendaInfo.getDeTipoAbilitato() == null)
			return;

		String val = aziendaInfo.getDeTipoAbilitato().getCodTipoAbilitato();
		if (val == null) {
			editTipoDelegato = false;
			editSoggettoAbilitato = false;
			editAgenziaSomministrazione = false;
			aziendaInfo.setDeTipoDelegato(null);
			showAgenziaSomministrazioneSection = false;
			showDatiSoggettoAbilitatoSection = false;
		} else if (TipoAbilitato.DATORE_PRIVATO.getCodice().equals(val)) {
			editTipoDelegato = false;
			editSoggettoAbilitato = false;
			editAgenziaSomministrazione = false;
			aziendaInfo.setDeTipoDelegato(null);
			showAgenziaSomministrazioneSection = false;
			showDatiSoggettoAbilitatoSection = false;
		} else if (TipoAbilitato.DATORE_PUBBLICO.getCodice().equals(val)) {
			editTipoDelegato = false;
			editSoggettoAbilitato = false;
			editAgenziaSomministrazione = false;
			aziendaInfo.setDeTipoDelegato(null);
			showAgenziaSomministrazioneSection = false;
			showDatiSoggettoAbilitatoSection = false;
		} else if (TipoAbilitato.AGENZIA_SOMMINISTRAZIONE.getCodice().equals(val)) {
			editTipoDelegato = false;
			editSoggettoAbilitato = false;
			editAgenziaSomministrazione = true;
			aziendaInfo.setDeTipoDelegato(null);
			showAgenziaSomministrazioneSection = true;
			showDatiSoggettoAbilitatoSection = false;
		} else if (TipoAbilitato.SOGGETTO_ABILITATO.getCodice().equals(val)) {
			editTipoDelegato = true;
			editSoggettoAbilitato = true;
			editAgenziaSomministrazione = false;
			showAgenziaSomministrazioneSection = false;
			showDatiSoggettoAbilitatoSection = true;
		} else if (TipoAbilitato.AGENZIA_LAVORO.getCodice().equals(val)) {
			editTipoDelegato = false;
			editSoggettoAbilitato = false;
			editAgenziaSomministrazione = false;
			showAgenziaSomministrazioneSection = false;
			showDatiSoggettoAbilitatoSection = false;

			if (soggettoIntermediazione == null) {
				try {
					soggettoIntermediazione = delegatoEJB.findById(TipoDelegato.SOGGETTO_INTERMEDIAZIONE.getCodice());
				} catch (MyCasNoResultException e) {
					log.error("Can't find TipoDelegato with id: " + TipoDelegato.SOGGETTO_INTERMEDIAZIONE.getCodice(),
							e);
				}
			}

			aziendaInfo.setDeTipoDelegato(soggettoIntermediazione);

		} else if (TipoAbilitato.SOGGETTO_TIROCINI.getCodice().equals(val)) {
			editTipoDelegato = false;
			editSoggettoAbilitato = false;
			editAgenziaSomministrazione = false;
			showAgenziaSomministrazioneSection = false;
			showDatiSoggettoAbilitatoSection = false;

			if (promotoreTirocini == null) {
				try {
					promotoreTirocini = delegatoEJB.findById(TipoDelegato.PROMOTORE_TIROCINI.getCodice());
				} catch (MyCasNoResultException e) {
					log.error("Can't find TipoDelegato with id: " + TipoDelegato.PROMOTORE_TIROCINI.getCodice(), e);
				}
			}

			aziendaInfo.setDeTipoDelegato(promotoreTirocini);
		} else {
			log.error("Errore nella gestione del tipo delegato. Tipo '" + val + "' sconosciuto.");
			editTipoDelegato = false;
			editSoggettoAbilitato = false;
			editAgenziaSomministrazione = false;
			aziendaInfo.setDeTipoDelegato(null);
			showAgenziaSomministrazioneSection = false;
			showDatiSoggettoAbilitatoSection = false;
		}
	}

	public String sync() {
		try {
			aziendaInfoEJB.updateProfile(aziendaInfo, editSoggettoAbilitato, editAgenziaSomministrazione);
			return "/secure/profile/viewMain.xhtml?faces-redirect=true&saveSucess=yes";
		} catch (Exception e) {
			String msgko = utils.getUiProperty("msg.updated.ko");
			log.error(msgko, e);
			addJSDangerMessage(msgko);
		}
		return null;
	}

	/**
	 * cancella la rettifica inviata alla provincia
	 * 
	 * @return
	 * @throws Exception
	 */
	public String annulaRett() throws Exception {
		aziendaInfoRettificaEJB.annullaRettifica(aziendaInfo.getIdPfPrincipal(), accountInfoBean.getUserInfo()
				.getIdPfPrincipal());
		return "/secure/profile/viewMain.xhtml?faces-redirect=true&saveSucess=yes";
	}

	public AziendaInfo getAziendaInfo() {
		return aziendaInfo;
	}

	public void setAziendaInfo(AziendaInfo aziendaInfo) {
		this.aziendaInfo = aziendaInfo;
	}

	public Boolean getShowAgenziaSomministrazioneSection() {
		return showAgenziaSomministrazioneSection;
	}

	public void setShowAgenziaSomministrazioneSection(Boolean showAgenziaSomministrazioneSection) {
		this.showAgenziaSomministrazioneSection = showAgenziaSomministrazioneSection;
	}

	public Boolean getShowDatiSoggettoAbilitatoSection() {
		return showDatiSoggettoAbilitatoSection;
	}

	public void setShowDatiSoggettoAbilitatoSection(Boolean showDatiSoggettoAbilitatoSection) {
		this.showDatiSoggettoAbilitatoSection = showDatiSoggettoAbilitatoSection;
	}

	public String getOldEmail() {
		return oldEmail;
	}

	public void setOldEmail(String oldEmail) {
		this.oldEmail = oldEmail;
	}

	public Boolean getEditTipoDelegato() {
		return editTipoDelegato;
	}

	public void setEditTipoDelegato(Boolean editTipoDelegato) {
		this.editTipoDelegato = editTipoDelegato;
	}

	public Boolean getEditSoggettoAbilitato() {
		return editSoggettoAbilitato;
	}

	public void setEditSoggettoAbilitato(Boolean editSoggettoAbilitato) {
		this.editSoggettoAbilitato = editSoggettoAbilitato;
	}

	public Boolean getEditAgenziaSomministrazione() {
		return editAgenziaSomministrazione;
	}

	public void setEditAgenziaSomministrazione(Boolean editAgenziaSomministrazione) {
		this.editAgenziaSomministrazione = editAgenziaSomministrazione;
	}

	public DeTipoDelegato getSoggettoIntermediazione() {
		return soggettoIntermediazione;
	}

	public void setSoggettoIntermediazione(DeTipoDelegato soggettoIntermediazione) {
		this.soggettoIntermediazione = soggettoIntermediazione;
	}

	public DeTipoDelegato getPromotoreTirocini() {
		return promotoreTirocini;
	}

	public void setPromotoreTirocini(DeTipoDelegato promotoreTirocini) {
		this.promotoreTirocini = promotoreTirocini;
	}

	public Boolean getRettificaAlreadyExists() {
		return rettificaAlreadyExists;
	}

	public void setRettificaAlreadyExists(Boolean rettificaAlreadyExists) {
		this.rettificaAlreadyExists = rettificaAlreadyExists;
	}

	public AziendaInfoRettifica getAziendaInfoRettifica() {
		return aziendaInfoRettifica;
	}

	public void setAziendaInfoRettifica(AziendaInfoRettifica aziendaInfoRettifica) {
		this.aziendaInfoRettifica = aziendaInfoRettifica;
	}

}
