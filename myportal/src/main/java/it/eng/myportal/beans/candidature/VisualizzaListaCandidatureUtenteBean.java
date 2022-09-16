package it.eng.myportal.beans.candidature;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.AcVisualizzaCandidaturaDTO;
import it.eng.myportal.dtos.UtenteDTO;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.utils.ConstantsSingleton;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 * BackingBean della pagina di visualizzazione dell'elenco delle candidature
 *
 * @author Enrico D'Angelo
 *
 */
@ManagedBean
@ViewScoped
public class VisualizzaListaCandidatureUtenteBean extends AbstractBaseBean {
	/**
	 * Elenco delle candidature effettuate dall'utente
	 */
	private List<AcVisualizzaCandidaturaDTO> candidature;
	private boolean flgCresco;

	@EJB
	private UtenteInfoHome utenteInfoHome;
	@EJB
	private VaDatiVacancyHome vaDatiVacancyHome;

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();

		// Controllo se sono arrivato alla pagina tramite la portlet per il pacchetto Cresco (solo Umbria)
		if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().containsKey("flgcresco")) {
			flgCresco = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
					.get("flgcresco").equalsIgnoreCase("true");
		}

		try {
			if (session.isUtente()) {
				UtenteDTO utente = session.getConnectedUtente();
				candidature = utenteInfoHome.getAllCandidature(utente.getId(), flgCresco);
				controlloCandidature();
				putParamsIntoSession();
				log.debug("Costruito il Bean per la visualizzazione dell'elenco delle candidature!");
			} else {
				addErrorMessage("authorized.is_not");
				redirectHome();
			}
		} catch (EJBException e) {
			addErrorMessage("data.error_loading", e);
			redirectHome();
		}
	}

	/**
	 * Nella lista delle candidature, il flag CRESCO viene mostrato solo nelle regioni in cui il pacchetto CRESCO esiste
	 * (per ora solo Umbria)
	 * 
	 */
	public boolean showFlagCresco() {
		return ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_UMBRIA);
	}

	public List<AcVisualizzaCandidaturaDTO> controlloCandidature(){
		if(!candidature.isEmpty()){
			for (int i=0; i< candidature.size(); i++){
				Integer id = candidature.get(i).getIdVaDatiVacancy();
				VaDatiVacancy vacancy = vaDatiVacancyHome.findById(id);
				if(vacancy.getDeEvasioneRich() != null) {
					String evasione = vacancy.getDeEvasioneRich().getCodEvasione();
					log.info("Stampa il codice di evasione: "+ evasione);
					if(evasione.equals(ConstantsSingleton.Evasione.PUBB_PALESE)){
						Integer idPalese = candidature.get(i).getIdPfPrincipalPalese();
						String aziendaPalese = candidature.get(i).getRagioneSocialePalese();
						log.info("Stampa l'azienda PALESE: "+ aziendaPalese);
						Integer idVetrinaPalese = candidature.get(i).getIdVetrinaPalese();
						candidature.get(i).setIdPfPrincipalAzienda(idPalese);
						candidature.get(i).setRagioneSocialeAz(aziendaPalese);
						candidature.get(i).setIdVetrina(idVetrinaPalese);
					}
				}
			}
		}

	return candidature;
	}

	public List<AcVisualizzaCandidaturaDTO> getCandidature() {
		return candidature;
	}

	public void setCandidature(List<AcVisualizzaCandidaturaDTO> candidature) {
		this.candidature = candidature;
	}

	@Override
	public String getToken() {
		return super.getToken() + FacesContext.getCurrentInstance().getViewRoot().getViewId();
	}
}
