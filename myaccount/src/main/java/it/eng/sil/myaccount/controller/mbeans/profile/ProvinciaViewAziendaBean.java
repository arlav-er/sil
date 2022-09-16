package it.eng.sil.myaccount.controller.mbeans.profile;

import it.eng.sil.myaccount.controller.mbeans.AbstractSecureBackingBean;
import it.eng.sil.myaccount.model.ejb.stateless.profile.AziendaInfoEJB;
import it.eng.sil.myaccount.model.ejb.stateless.profile.PfPrincipalMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.sare.UtenteAziendaSareEJB;
import it.eng.sil.myaccount.model.enums.TipoAbilitato;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.profilo.AziendaInfo;
import it.eng.sil.mycas.model.manager.decodifiche.DeComuneEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeProvinciaEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeTipoAbilitatoEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeTipoDelegatoEJB;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@ViewScoped
@ManagedBean(name = "provinciaViewAziendaBean")
public class ProvinciaViewAziendaBean extends AbstractSecureBackingBean implements Serializable {

	private static final long serialVersionUID = -7109341944058024676L;

	protected static Log log = LogFactory.getLog(AziendaViewBean.class);

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
	private PfPrincipalMyAccountEJB pfPrincipalMyAccountEJB;

	@EJB
	private UtenteAziendaSareEJB utenteAziendaSareEJB;

	private AziendaInfo aziendaInfo;

	private Boolean showAgenziaSomministrazioneSection;
	private Boolean showDatiSoggettoAbilitatoSection;

	@Override
	protected void initPostConstruct() {
		loadUser();
		extraRicSareSections();
	}

	public void loadUser() {
		try {
			aziendaInfo = aziendaInfoEJB.findById(Integer.parseInt(getRequestParameter("aziendaViewId")));

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

		} catch (EJBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MyCasNoResultException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void extraRicSareSections() {
		if (aziendaInfo.getDeTipoAbilitato() != null) {
			String val = aziendaInfo.getDeTipoAbilitato().getCodTipoAbilitato();

			if (TipoAbilitato.AGENZIA_SOMMINISTRAZIONE.getCodice().equals(val)) {
				showAgenziaSomministrazioneSection = true;
				showDatiSoggettoAbilitatoSection = false;
			} else if (TipoAbilitato.SOGGETTO_ABILITATO.getCodice().equals(val)) {
				showAgenziaSomministrazioneSection = false;
				showDatiSoggettoAbilitatoSection = true;
			}
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

}
