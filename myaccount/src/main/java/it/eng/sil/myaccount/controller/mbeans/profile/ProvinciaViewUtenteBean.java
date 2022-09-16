package it.eng.sil.myaccount.controller.mbeans.profile;

import it.eng.sil.myaccount.controller.mbeans.AbstractSecureBackingBean;
import it.eng.sil.myaccount.model.ejb.stateless.profile.UtenteInfoEJB;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.profilo.UtenteInfo;
import it.eng.sil.mycas.model.manager.decodifiche.DeCittadinanzaEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeComuneEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeProvinciaEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeTitoloSoggiornoEJB;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ViewScoped
@ManagedBean(name = "provinciaViewUtenteBean")
public class ProvinciaViewUtenteBean extends AbstractSecureBackingBean implements Serializable {

	private static final long serialVersionUID = -8353879124529108183L;

	private UtenteInfo utenteInfo;

	@EJB
	private UtenteInfoEJB utenteInfoEJB;

	@EJB
	private DeComuneEJB deComuneEJB;

	@EJB
	private DeProvinciaEJB deProvinciaEJB;

	@EJB
	private DeCittadinanzaEJB deCittadinanzaEJB;

	@EJB
	private DeTitoloSoggiornoEJB deTitoloSoggiornoEJB;

	@Override
	protected void initPostConstruct() {
		loadUser();
	}

	// Carica i dati dell'utente specificato nel parametro della richiesta HTTP.
	public void loadUser() {
		try {
			setUtenteInfo(utenteInfoEJB.findById(Integer.parseInt(getRequestParameter("utenteId"))));

			if (utenteInfo.getDeComuneNascita() != null)
				utenteInfo.setDeComuneNascita(deComuneEJB.findById(utenteInfo.getDeComuneNascita().getCodCom()));

			if (utenteInfo.getDeComuneDomicilio() != null)
				utenteInfo.setDeComuneDomicilio(deComuneEJB.findById(utenteInfo.getDeComuneDomicilio().getCodCom()));

			if (utenteInfo.getDeComuneResidenza() != null)
				utenteInfo.setDeComuneResidenza(deComuneEJB.findById(utenteInfo.getDeComuneResidenza().getCodCom()));

			if (utenteInfo.getDeCittadinanza() != null)
				utenteInfo.setDeCittadinanza(deCittadinanzaEJB.findById(utenteInfo.getDeCittadinanza()
						.getCodCittadinanza()));

			if (utenteInfo.getDocumentoSoggiorno() != null)
				utenteInfo.setDocumentoSoggiorno(deTitoloSoggiornoEJB.findById(utenteInfo.getDocumentoSoggiorno()
						.getCodTitoloSoggiorno()));

			if (utenteInfo.getDeProvincia() != null)
				utenteInfo.setDeProvincia(deProvinciaEJB.findById(utenteInfo.getDeProvincia().getCodProvincia()));
		} catch (EJBException e) {
			log.error("Errore nel caricamento dell'utente: " + e.getMessage());
		} catch (MyCasNoResultException e) {
			log.error("Errore nel caricamento dell'utente: " + e.getMessage());
		}
	}

	public UtenteInfo getUtenteInfo() {
		return utenteInfo;
	}

	public void setUtenteInfo(UtenteInfo utenteInfo) {
		this.utenteInfo = utenteInfo;
	}
}
