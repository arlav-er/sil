package it.eng.myportal.beans.lettere;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import it.eng.myportal.beans.AbstractEditableBean;
import it.eng.myportal.beans.RestoreParameters;
import it.eng.myportal.beans.UtenteHomeBean;
import it.eng.myportal.dtos.CvLetteraAccDTO;
import it.eng.myportal.dtos.DeSezioneInfoDTO;
import it.eng.myportal.entity.home.CvLetteraAccHome;
import it.eng.myportal.entity.home.decodifiche.DeSezioneInfoHome;
import it.eng.myportal.utils.ConstantsSingleton;

/**
 * Classe per gestire la pagina di visualizzazione di una lettera di
 * accompagnamento
 * 
 * @author Rodi A.
 * 
 *         TODO commentare adeguatamente e controlli di sicurezza
 * 
 */
@ManagedBean
@ViewScoped
public class LetteraAccompagnamentoBean extends AbstractEditableBean<CvLetteraAccDTO> {

	@EJB
	CvLetteraAccHome cvLetteraAccHome;
	
	@EJB
	DeSezioneInfoHome deSezioneInfoHome;
	
	DeSezioneInfoDTO brevePresentazioneInfo;
	DeSezioneInfoDTO motivazioniObiettiviInfo;
	DeSezioneInfoDTO beneficiInfo;
	DeSezioneInfoDTO puntiForzaInfo;
	DeSezioneInfoDTO salutiInfo;

	private Integer letteraId;
	private boolean isOwnerLogged;

	private String letteraIdStr;

	private int numeroLettere;

	@ManagedProperty(value = "#{utenteHomeBean}")
	UtenteHomeBean utenteHomeBean;

	private static final String LETTERA_ID = "letteraId";
	
	public UtenteHomeBean getUtenteHomeBean() {
		return utenteHomeBean;
	}

	public void setUtenteHomeBean(UtenteHomeBean utenteHomeBean) {
		this.utenteHomeBean = utenteHomeBean;
	}

	@Override
	protected CvLetteraAccDTO buildNewDataIstance() {
		return new CvLetteraAccDTO();
	}
	
	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		numeroLettere = utenteHomeBean.getNumeroLettere();
		
		brevePresentazioneInfo = deSezioneInfoHome.findDTOById(ConstantsSingleton.DeServizioInfo.COD_LETTERA_BREVE_PRESENTAZIONE);
		motivazioniObiettiviInfo = deSezioneInfoHome.findDTOById(ConstantsSingleton.DeServizioInfo.COD_LETTERA_MOTIVAZIONI_OBIETTIVI);
		beneficiInfo = deSezioneInfoHome.findDTOById(ConstantsSingleton.DeServizioInfo.COD_LETTERA_BENEFICI);
		puntiForzaInfo = deSezioneInfoHome.findDTOById(ConstantsSingleton.DeServizioInfo.COD_LETTERA_PUNTI_FORZA);
		salutiInfo = deSezioneInfoHome.findDTOById(ConstantsSingleton.DeServizioInfo.COD_LETTERA_SALUTI);
	}

	/**
	 * @see it.eng.myportal.beans.AbstractEditableBean#dontedit()
	 * 
	 *      La pressione di un pulsante implica sempre il post dei valori
	 *      attualmente presenti nella form;<br>
	 *      per implementare la funzionalità di annullamento delle modifiche<br>
	 *      vengono ricaricati i dati.
	 * 
	 * @return CvLetteraAccDTO
	 */
	@Override
	protected CvLetteraAccDTO retrieveData() {
		Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String id = StringUtils.defaultString(map.get("id"), letteraIdStr);
		if (id != null) {
			try {
				letteraId = Integer.valueOf(id);
				putParamsIntoSession();
			} catch (Exception ex) {
				throw new RuntimeException("Errore durante il recupero dei dati");
			}
			CvLetteraAccDTO lettDto = cvLetteraAccHome.findDTOById(letteraId);

			if (session.getConnectedUtente() != null) {
				isOwnerLogged = lettDto.getIdPfPrincipal().intValue() != session.getConnectedUtente().getId();
			}
			return lettDto;
		} else {
			return null;
		}
	}

	@Override
	protected void saveData() {
		// Inserisco una lettera solo se non ho ancora raggiunto il numero
		// massimo
		if (numeroLettere < ConstantsSingleton.LETTEREMAXCURR) {
			Integer idUtente = session.getConnectedUtente().getId();

			if (data.getIdPfPrincipal() == null) {
				data.setIdPfPrincipal(idUtente);
				data.setFlagInviato(false);
			}
			data = homePersist(cvLetteraAccHome, data);

			// messaggio di avvenuto salvataggio
			addInfoMessage("data.created");
			log.debug("Lettera di presentazione salvata.");
		} else {
			redirectGrave("generic.manipulation_error");
		}
	}

	@Override
	protected void updateData() {
		data = homeMerge(cvLetteraAccHome, data);
		addInfoMessage("data.updated");
		log.debug("Lettera di presentazione aggiornata.");
	}

	@Override
	public void dontedit() {
		editing = false;
		log.debug("");
		try {
			data = cvLetteraAccHome.findDTOById(letteraId);
		} catch (EJBException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Errore durante la lettura dei dati."));
			log.error("Errore durante il recupero dei dati: " + e.getMessage());

		}
	}

	@Override
	protected RestoreParameters generateRestoreParams() {
		RestoreParameters ret = super.generateRestoreParams();
		ret.put(LETTERA_ID, letteraId);
		return ret;
	}

	@Override
	public void ricreaStatoDaSessione(RestoreParameters restoreParameters) {
		super.ricreaStatoDaSessione(restoreParameters);
		letteraIdStr = ObjectUtils.toString(restoreParameters.get(LETTERA_ID));
	}

	public DeSezioneInfoDTO getBrevePresentazioneInfo() {
		return brevePresentazioneInfo;
	}

	public void setBrevePresentazioneInfo(DeSezioneInfoDTO brevePresentazioneInfo) {
		this.brevePresentazioneInfo = brevePresentazioneInfo;
	}

	public DeSezioneInfoDTO getMotivazioniObiettiviInfo() {
		return motivazioniObiettiviInfo;
	}

	public void setMotivazioniObiettiviInfo(DeSezioneInfoDTO motivazioniObiettiviInfo) {
		this.motivazioniObiettiviInfo = motivazioniObiettiviInfo;
	}

	public DeSezioneInfoDTO getBeneficiInfo() {
		return beneficiInfo;
	}

	public void setBeneficiInfo(DeSezioneInfoDTO beneficiInfo) {
		this.beneficiInfo = beneficiInfo;
	}

	public DeSezioneInfoDTO getPuntiForzaInfo() {
		return puntiForzaInfo;
	}

	public void setPuntiForzaInfo(DeSezioneInfoDTO puntiForzaInfo) {
		this.puntiForzaInfo = puntiForzaInfo;
	}

	public DeSezioneInfoDTO getSalutiInfo() {
		return salutiInfo;
	}

	public void setSalutiInfo(DeSezioneInfoDTO salutiInfo) {
		this.salutiInfo = salutiInfo;
	}
}
