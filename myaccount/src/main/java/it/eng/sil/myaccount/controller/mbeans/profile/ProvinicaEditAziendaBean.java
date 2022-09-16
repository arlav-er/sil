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

@ManagedBean(name = "provEditAz")
@ViewScoped
public class ProvinicaEditAziendaBean extends AbstractSecureBackingBean implements Serializable {

	private static final long serialVersionUID = -5956923587780843404L;

	@EJB
	private AziendaInfoEJB aziendaInfoEJB;

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

	@EJB
	AziendaInfoRettificaEJB aziendaInfoRettificaEJB;

	private AziendaInfo aziendaInfo;

	private AziendaInfoRettifica aziendaInfoRettifica;

	private Boolean showAgenziaSomministrazioneSection;
	private Boolean showDatiSoggettoAbilitatoSection;
	private Boolean editTipoDelegato;
	private Boolean editSoggettoAbilitato;
	private Boolean editAgenziaSomministrazione;

	private DeTipoDelegato soggettoIntermediazione;
	private DeTipoDelegato promotoreTirocini;

	private String oldEmail;

	private Boolean isRettifica;
	private Boolean canRifiutaRettifica;

	@Override
	protected void initPostConstruct() {
		loadUser();
		tipoAbilitatoChanged();
	}

	public void loadUser() {
		try {
			aziendaInfo = aziendaInfoEJB.findById(Integer.parseInt(getRequestParameter("aziendaId")));

			log.info("Loading User with ID:" + accountInfoBean.getUserInfo().getIdPfPrincipal());

			if (aziendaInfo.getDeComuneSede() != null)
				aziendaInfo.setDeComuneSede(deComuneEJB.findById(aziendaInfo.getDeComuneSede().getCodCom()));

			if (aziendaInfo.getDeComuneNascitaRic() != null)
				aziendaInfo
						.setDeComuneNascitaRic(deComuneEJB.findById(aziendaInfo.getDeComuneNascitaRic().getCodCom()));

			if (aziendaInfo.getDeComuneRichiedente() != null)
				aziendaInfo.setDeComuneRichiedente(deComuneEJB.findById(aziendaInfo.getDeComuneRichiedente()
						.getCodCom()));

			if (aziendaInfo.getDeProvincia() != null)
				aziendaInfo.setDeProvincia(deProvinciaEJB.findById(aziendaInfo.getDeProvincia().getCodProvincia()));

			if (aziendaInfo.getDeTipoAbilitato() != null)
				aziendaInfo.setDeTipoAbilitato(deTipoAbilitatoEJB.findById(aziendaInfo.getDeTipoAbilitato()
						.getCodTipoAbilitato()));

			if (aziendaInfo.getDeTipoDelegato() != null)
				aziendaInfo.setDeTipoDelegato(deTipoDelegatoEJB.findById(aziendaInfo.getDeTipoDelegato()
						.getCodTipoDelegato()));

			if (aziendaInfo.getDeComuneSedeLegale() != null)
				aziendaInfo
						.setDeComuneSedeLegale(deComuneEJB.findById(aziendaInfo.getDeComuneSedeLegale().getCodCom()));

			if (aziendaInfo.getDeComuneIscrizione() != null)
				aziendaInfo
						.setDeComuneIscrizione(deComuneEJB.findById(aziendaInfo.getDeComuneIscrizione().getCodCom()));

			oldEmail = aziendaInfo.getPfPrincipal().getEmail();

			try {
				aziendaInfoRettifica = aziendaInfoRettificaEJB.findById(Integer
						.parseInt(getRequestParameter("aziendaId")));
				isRettifica = true;
				canRifiutaRettifica = true;
				String msgok = utils.getUiProperty("msg.account.rettifica.provalready");
				addJSWarnMessage(msgok);
			} catch (MyCasNoResultException ma) {
				// non esiste una rettifica
				// oppure la rettifica è stata già effettuata o rifiutata
				aziendaInfoRettifica = new AziendaInfoRettifica();
				isRettifica = false;
				log.warn("Rettifica non trovata per id: " + Integer.parseInt(getRequestParameter("aziendaId")));
			}

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

				if (aziendaInfoRettifica.getDeProvincia() != null)
					aziendaInfoRettifica.setDeProvincia(deProvinciaEJB.findById(aziendaInfoRettifica.getDeProvincia()
							.getCodProvincia()));

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
			addJSDangerMessage("Errore nel caricamento della provincia.");
			// new RuntimeException("Errore nel caricamento della Provincia", e);
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

	public AziendaInfoRettifica getAziendaInfoRettifica() {
		return aziendaInfoRettifica;
	}

	public void setAziendaInfoRettifica(AziendaInfoRettifica aziendaInfoRettifica) {
		this.aziendaInfoRettifica = aziendaInfoRettifica;
	}

	public Boolean getIsRettifica() {
		return isRettifica;
	}

	public void setIsRettifica(Boolean isRettifica) {
		this.isRettifica = isRettifica;
	}

	public Boolean getCanRifiutaRettifica() {
		return canRifiutaRettifica;
	}

	public void setCanRifiutaRettifica(Boolean canRifiutaRettifica) {
		this.canRifiutaRettifica = canRifiutaRettifica;
	}

	public void sync() {
		try {
			aziendaInfoRettificaEJB.approvaRettifica(aziendaInfo, accountInfoBean.getUserInfo().getIdPfPrincipal(),
					editSoggettoAbilitato, editAgenziaSomministrazione);
			String msgok = utils.getUiProperty("msg.updated");
			addJSSuccessMessage(msgok);
			if (isRettifica) {
				canRifiutaRettifica = false;
			}

			loadUser();
		} catch (Exception e) {
			String msgko = utils.getUiProperty("msg.updated.ko");
			log.error(msgko, e);
			addJSDangerMessage(msgko);
		}
	}

	public void cancelUpdate() {
		try {
			aziendaInfoRettificaEJB.deleteRettificaProvincia(aziendaInfo.getIdPfPrincipal(), accountInfoBean
					.getUserInfo().getIdPfPrincipal());
			loadUser();
			String msgok = utils.getUiProperty("msg.account.rettifica.delete");
			addJSSuccessMessage(msgok);

		} catch (Exception e) {
			String msgko = utils.getUiProperty("msg.account.rettifica.not-deleted");
			log.error(msgko, e);
			addJSDangerMessage(msgko);
		}
	}

	public void copyData() {
		AziendaInfo aziendaCopy = aziendaInfoRettificaEJB.copyData(aziendaInfo, aziendaInfoRettifica);
		setAziendaInfo(aziendaCopy);
		tipoAbilitatoChanged();
	}

}
