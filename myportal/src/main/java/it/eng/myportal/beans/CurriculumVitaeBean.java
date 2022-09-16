package it.eng.myportal.beans;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.dtos.CvCandidaturaClDTO;
import it.eng.myportal.dtos.CvDatiPersonaliDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.dtos.VaDatiVacancyDTO;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.home.CvAllegatoHome;
import it.eng.myportal.entity.home.CvCandidaturaClHome;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;
import it.eng.myportal.entity.home.CvProfDesiderateHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.home.decodifiche.DeAmbitoDiffusioneHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoInvioClHome;
import it.eng.myportal.exception.MyPortalException;

/**
 * Backing Bean associato al CurriculumVitae.
 * 
 * @author Rodi A.
 * 
 */
@ManagedBean
@ViewScoped
public class CurriculumVitaeBean extends AbstractEditableBean<CvDatiPersonaliDTO> {

	private static final String CURRICULUM_ID = "curriculumId";

	/**
	 * Logger per registrare informazioni.
	 */
	protected static Log log = LogFactory.getLog(CurriculumVitaeBean.class);

	@EJB
	private CvDatiPersonaliHome cvDatiPersonaliHome;

	@EJB
	private VaDatiVacancyHome vaDatiVacancyHome;

	@EJB
	private CvAllegatoHome cvAllegatoHome;

	@EJB
	private CvProfDesiderateHome cvProfDesiderateHome;

	@EJB
	private DeAmbitoDiffusioneHome deAmbitoDiffusioneHome;

	@EJB
	private CvCandidaturaClHome cvCandidaturaClHome;

	@EJB
	private DeStatoInvioClHome deStatoInvioClHome;

	@EJB
	private UtenteInfoHome utenteInfoHome;

	private List<VaDatiVacancyDTO> vaDatiVacancyDTOs;

	private int curriculumId;

	private long numVacancyCompatibili;

	private boolean mostraDatiCliclavoro;

	/**
	 * Numero delle professioni desiderate inserite dall'utente
	 */
	private int numProfessioniDesiderate;

	/**
	 * id curriculum recuperato dalla sessione in caso di 'torna indietro'
	 */
	private String curriculumIdStr;

	/* l'ambito di diffusione del CV prima delle modifiche applicate */
	private String codAmbitoDiffusionePrecedente;

	private UtenteCompletoDTO utenteCompletoDTO;

	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		utenteCompletoDTO = utenteInfoHome.findDTOCompletoById(session.getPrincipalId());
		setEditing(true);
		log.debug("Costruito il Bean per il CurriculuVitae!");
	}

	@Override
	public void save() {
		super.save();
		editing = true; // le tab devono essere sempre in editing
	}

	@Override
	public void update() {
		// Controllo che il titolo non contenga riferimenti a dati personali.
		String erroreTitoloCv = cvDatiPersonaliHome.checkTitoloCv(data, utenteCompletoDTO);
		if (erroreTitoloCv != null && !erroreTitoloCv.trim().isEmpty()) {
			FacesMessage errorMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, erroreTitoloCv, erroreTitoloCv);
			FacesContext.getCurrentInstance().addMessage(null, errorMessage);
			return; // Non salvo nulla
		}

		super.update();
		editing = true; // le tab devono essere sempre in editing
	}

	/**
	 * Aggiorna il CV e i dati di invio a cliclavoro in due transazioni separate. Prima vengono aggiornati i dati del
	 * CV, escluso l'ambito di diffusione, poi vengono aggiornati i dati di invio a cliclavoro. In questo modo i dati di
	 * invio a cliclavoro vengono salvati solo se il salvataggio del CV va a buon fine. I dati del CV invece vengono
	 * salvati anche se il salvataggio dei dati di invio a cliclavoro fallisce.
	 */
	@Override
	public void updateData() {
		try {
			/* aggiorno il CV, escluso l'ambito di diffusione */
			String codAmbitoDiffusioneNuovo = data.getDeAmbitoDiffusione().getId();
			data.setDeAmbitoDiffusione(deAmbitoDiffusioneHome.findDTOById(codAmbitoDiffusionePrecedente));

			log.debug("==Aggiorno il CV " + data.getDescrizione() + "==");
			log.debug("ambito diffusione precedente: " + codAmbitoDiffusionePrecedente);
			log.debug("ambito diffusione nuovo: " + codAmbitoDiffusioneNuovo);

			data = homeMerge(cvDatiPersonaliHome, data);

			boolean needsCliclavoro = cvDatiPersonaliHome.needsMergeCliclavoro(curriculumId,
					codAmbitoDiffusionePrecedente, codAmbitoDiffusioneNuovo);

			if (needsCliclavoro) {
				CvDatiPersonaliDTO cvDatiPersonaliDTO = cvDatiPersonaliHome.mergeClicLavoro(session.getPrincipalId(),
						data.getId(), codAmbitoDiffusionePrecedente, codAmbitoDiffusioneNuovo);
				data = homeMerge(cvDatiPersonaliHome, cvDatiPersonaliDTO);

				/* avvisi riguardanti la sincronizzazione con cliclavoro */
				if (!codAmbitoDiffusionePrecedente.equals(codAmbitoDiffusioneNuovo)) {
					/* l'ambito di diffusione E' cambiato */
					boolean isPrecSincClicLavoro = deAmbitoDiffusioneHome
							.isSincronizzatoClicLavoro(codAmbitoDiffusionePrecedente);
					boolean isNuovoSincClicLavoro = deAmbitoDiffusioneHome
							.isSincronizzatoClicLavoro(codAmbitoDiffusioneNuovo);
					if (isPrecSincClicLavoro && !isNuovoSincClicLavoro) {
						CvCandidaturaClDTO cvCandidaturaClDTO = cvCandidaturaClHome.findDTOById(data.getId());
						boolean isGiaComunicatoCliclavoro = cvCandidaturaClDTO != null
								&& deStatoInvioClHome.giaComunicatoCliclavoro(cvCandidaturaClDTO.getDeStatoInvioCl()
										.getId());
						if (isGiaComunicatoCliclavoro) {
							addWarnMessage("cliclavoro.cv.warn.disattiva_sincronizzazione");
						} else {
							addWarnMessage("cliclavoro.cv.warn.annulla_sincronizzazione");
						}
					}
					if (!isPrecSincClicLavoro && isNuovoSincClicLavoro) {
						addWarnMessage("cliclavoro.cv.warn.nuova_sincronizzazione");
					}
				}
			}
			addInfoMessage("data.updated");
		} catch (EJBException e) {
			gestisciErrore(e, "data.retrieving");
		} finally {
			/* recupero i DTO aggiornati */
			refreshData();
		}
	}

	public void refresh() {
		refreshData();
	}

	public boolean usaDecodificheSil() {
		return (data != null && data.getOpzTipoDecodifiche() != null && CvDatiPersonali.OpzTipoDecodifiche.SIL
				.equals(data.getOpzTipoDecodifiche()));
	}

	@Override
	protected CvDatiPersonaliDTO buildNewDataIstance() {
		CvDatiPersonaliDTO data = new CvDatiPersonaliDTO();
		return data;
	}

	@Override
	protected CvDatiPersonaliDTO retrieveData() {
		if (session.isUtente()) {
			Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
			try {
				String idString = map.get("id");
				if (idString != null) {
					// passo l'id come parametro
					curriculumId = Integer.parseInt(idString);
				} else if (curriculumIdStr != null) {
					// torna indietro
					curriculumId = Integer.parseInt(curriculumIdStr);
				} else {
					// refresh della pagina (annullo le modifiche in corso)
					curriculumId = data.getId();
				}
				putParamsIntoSession();
			} catch (NumberFormatException e) {
				redirectGrave("generic.manipulation_error");
				return null;
			}

			refreshData();

			// /////////////////////////////////////////////////////////////////////////
			// *** Controllo di sicurezza ***
			// Il CV richiesto o una sua sezione devono appartenere all'utente
			// loggato e il CV deve essere live, cioe' non deve essere stato
			// inviato per una vacancy ne deve essere in attesa di eliminazione
			// /////////////////////////////////////////////////////////////////////////
			Integer idUtente = data.getIdPfPrincipal();
			if ((idUtente.intValue() != session.getConnectedUtente().getId()) || data.getFlagInviato()
					|| data.getFlagEliminato()) {
				redirectGrave("generic.manipulation_error");
				return null;
			}
			/*
			 * se il CV non e' stato originato su MYPORTAL non puo' essere modificato da nessuno
			 */
			if (!data.getFromMyPortal()) {
				redirectHome();
				throw new MyPortalException("curriculum.error.write");
			}

			return data;
		} else {
			log.error("Tentativo di accedere alla sezione utente.");
			addErrorMessage("user.is_not");
			redirectHome();
			return null;
		}
	}

	private void refreshData() {
		data = cvDatiPersonaliHome.findDTOById(curriculumId);
		codAmbitoDiffusionePrecedente = data.getDeAmbitoDiffusione().getId();
		mostraDatiCliclavoro = data.isSincronizzatoCliclavoro();

		numVacancyCompatibili = vaDatiVacancyHome.countVacancyCompatibiliByCurriculumId(curriculumId);
		numProfessioniDesiderate = cvProfDesiderateHome.countProfDesiderateByCurriculumId(curriculumId);
	}

	@Override
	protected void saveData() {
		data = homePersist(cvDatiPersonaliHome, data);
		addInfoMessage("data.created");
		log.debug("Testata curriculum salvata.");
	}

	/**
	 * @see it.eng.myportal.beans.AbstractEditableBean#dontedit()
	 * 
	 *      La pressione di un pulsante implica sempre il post dei valori attualmente presenti nella form;<br>
	 *      per implementare la funzionalità di annullamento delle modifiche<br>
	 *      vengono ricaricati i dati.
	 * 
	 */
	@Override
	public void dontedit() {
		editing = false;
		log.debug("");
		try {
			retrieveData();
		} catch (EJBException e) {
			addErrorMessage("data.error_loading", e);
		}
		editing = true;
	}

	/**
	 * @return the vaDatiVacancyDTOs
	 */
	public List<VaDatiVacancyDTO> getVaDatiVacancyDTOs() {
		return vaDatiVacancyDTOs;
	}

	/**
	 * @param vaDatiVacancyDTOs
	 *            the vaDatiVacancyDTOs to set
	 */
	public void setVaDatiVacancyDTOs(List<VaDatiVacancyDTO> vaDatiVacancyDTOs) {
		this.vaDatiVacancyDTOs = vaDatiVacancyDTOs;
	}

	/**
	 * @return the numVacancyCompatibili
	 */
	public long getNumVacancyCompatibili() {
		return numVacancyCompatibili;
	}

	/**
	 * @param numVacancyCompatibili
	 *            the numVacancyCompatibili to set
	 */
	public void setNumVacancyCompatibili(long numVacancyCompatibili) {
		this.numVacancyCompatibili = numVacancyCompatibili;
	}

	@Override
	protected RestoreParameters generateRestoreParams() {
		RestoreParameters ret = super.generateRestoreParams();
		ret.put(CURRICULUM_ID, curriculumId);
		return ret;
	}

	@Override
	public void ricreaStatoDaSessione(RestoreParameters restoreParams) {
		super.ricreaStatoDaSessione(restoreParams);
		curriculumIdStr = ObjectUtils.toString(restoreParams.get(CURRICULUM_ID));
	}

	public void removeCvAttach() {
		log.debug("removeCvAttach");
		if (data.getIdCvAllegato() != null) {
			cvAllegatoHome.removeById(data.getIdCvAllegato(), null);
			data.setFilename(null);
		}
		refreshData();
	}

	public String getCodAmbitoDiffusionePrecedente() {
		return codAmbitoDiffusionePrecedente;
	}

	public void setCodAmbitoDiffusionePrecedente(String codAmbitoDiffusionePrecedente) {
		this.codAmbitoDiffusionePrecedente = codAmbitoDiffusionePrecedente;
	}

	public boolean isMostraDatiCliclavoro() {
		return mostraDatiCliclavoro;
	}

	public void setMostraDatiCliclavoro(boolean mostraDatiCliclavoro) {
		this.mostraDatiCliclavoro = mostraDatiCliclavoro;
	}

	public int getNumProfessioniDesiderate() {
		return numProfessioniDesiderate;
	}

	public void setNumProfessioniDesiderate(int numProfessioniDesiderate) {
		this.numProfessioniDesiderate = numProfessioniDesiderate;
	}

	public UtenteCompletoDTO getUtenteCompletoDTO() {
		return utenteCompletoDTO;
	}

	public void setUtenteCompletoDTO(UtenteCompletoDTO utenteCompletoDTO) {
		this.utenteCompletoDTO = utenteCompletoDTO;
	}
}
