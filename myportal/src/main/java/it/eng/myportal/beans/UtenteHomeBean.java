package it.eng.myportal.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import it.eng.myportal.dtos.CvDatiPersonaliDTO;
import it.eng.myportal.dtos.CvLetteraAccDTO;
import it.eng.myportal.dtos.RvTestataDTO;
import it.eng.myportal.dtos.UtenteDTO;
import it.eng.myportal.dtos.UtenteInfoDTO;
import it.eng.myportal.dtos.WsStampaDTO;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;
import it.eng.myportal.entity.home.CvLetteraAccHome;
import it.eng.myportal.entity.home.RvTestataHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.entity.home.WsStampaHome;
import it.eng.myportal.utils.ConstantsSingleton;

/**
 * BackingBean dell'HomePage dell'utente.
 * 
 * @author Rodi A.
 * 
 */
@ManagedBean
@ViewScoped
public class UtenteHomeBean extends AbstractHomepageBean {

	UtenteDTO utente;

	private String urlMyStage;
	private boolean didFatta;

	/**
	 * Elenco di tutti i curriculum appartenenti dall'utente
	 */
	private List<CvDatiPersonaliDTO> curricula;
	/**
	 * Elenco dei curriculum creati dall'utente
	 */
	private List<CvDatiPersonaliDTO> curriculaLive;
	/**
	 * Elenco dei curriculum intermediati per l'utente
	 */
	private List<CvDatiPersonaliDTO> curriculaIntermediati;
	/**
	 * Elenco delle lettere di presentazione create dall'utente
	 */
	private List<CvLetteraAccDTO> lettere;
	/**
	 * Elenco delle ricerche salvate dall'utente
	 */
	private List<RvTestataDTO> ricerche;

	private int numeroLiveCurricula;
	private int numeroLettere;
	private int numeroRicerche;

	private String tokenSARE;
	private String urlSARE;

	@EJB
	private UtenteInfoHome utenteInfoHome;

	@EJB
	private CvDatiPersonaliHome cvDatiPersonaliHome;

	@EJB
	private CvLetteraAccHome cvLetteraAccHome;

	@EJB
	private RvTestataHome rvTestataHome;

	@EJB
	private WsStampaHome wsStampaHome;

	@EJB
	WsEndpointHome wsEndpointHome;

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();

		try {
			if (session.isUtente()) {
				// Clean-up delle informazioni in sessione
				session.getParams().clear();
				utente = session.getConnectedUtente();

				tokenSARE = null;

				urlMyStage = session.getUrlMyStage();

				getAndCountCurricula();
				getAndCountLettere();
				getAndCountRicerche();
				checkDid();
			} else {
				addErrorMessage("authorized.is_not");
				redirectHome();
			}
		} catch (EJBException e) {
			addErrorMessage("data.error_loading", e);
			redirectHome();
		}
	}

	public UtenteDTO getUtente() {
		return utente;
	}

	public void setUtente(UtenteDTO utente) {
		this.utente = utente;
	}

	private void getAndCountCurricula() {
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + ".getAndCountCurricula");
		try {
			curriculaLive = utenteInfoHome.getAllCurriculaLive(utente.getId());
			/* conto solo i CV live, non quelli palesi */
			numeroLiveCurricula = curriculaLive != null ? curriculaLive.size() : 0;
			curriculaIntermediati = utenteInfoHome.getAllCurriculaIntermediatiNoMinScaduti(utente.getId());

			curricula = new ArrayList<CvDatiPersonaliDTO>();
			curricula.addAll(curriculaLive);
			curricula.addAll(curriculaIntermediati);
		} finally {
			jamonMonitor.stop();
		}
	}

	private void getAndCountLettere() {
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + ".getAndCountLettere");
		try {
			lettere = utenteInfoHome.getAllLiveLettereAcc(utente.getId());
			numeroLettere = lettere != null ? lettere.size() : 0;
		} finally {
			jamonMonitor.stop();
		}
	}

	private void getAndCountRicerche() {
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + ".getAndCountRicerche");
		try {
			ricerche = utenteInfoHome.getAllRicercheByIdPfPrincipal(utente.getId());
			numeroRicerche = ricerche != null ? ricerche.size() : 0;
		} finally {
			jamonMonitor.stop();
		}
	}

	/** Se l'utente ha almeno un WsStampa di tipo 'did', considero che ha fatto la DID. */
	private void checkDid() {
		List<WsStampaDTO> stampeDid = wsStampaHome.getStampeDTO("did", utente.getPfPrincipalDTO().getId());
		if (stampeDid != null && !stampeDid.isEmpty()) {
			didFatta = true;
		}
	}

	public List<CvDatiPersonaliDTO> getCurricula() {
		return curricula;
	}

	public void setCurricula(List<CvDatiPersonaliDTO> curricula) {
		this.curricula = curricula;
	}

	public int getNumeroLiveCurricula() {
		return numeroLiveCurricula;
	}

	public void setNumeroLiveCurricula(int numeroLiveCurricula) {
		this.numeroLiveCurricula = numeroLiveCurricula;
	}

	public List<CvLetteraAccDTO> getLettere() {
		return lettere;
	}

	public void setLettere(List<CvLetteraAccDTO> lettere) {
		this.lettere = lettere;
	}

	public int getNumeroLettere() {
		return numeroLettere;
	}

	public void setNumeroLettere(int numeroLettere) {
		this.numeroLettere = numeroLettere;
	}

	public void copyCurriculum() {
		if (numeroLiveCurricula < ConstantsSingleton.CVMAXCURR) {
			try {
				Map<String, String> map = getRequestParameterMap();
				Integer copyId = null;
				String copyDescrizione = null;

				for (Entry<String, String> entry : map.entrySet()) {
					if (entry.getKey().endsWith("descrizione_copia")) {
						copyDescrizione = entry.getValue();
					} else if (entry.getKey().endsWith("id_copia")) {
						copyId = Integer.parseInt(entry.getValue());
					}
				}

				if (copyId != null && copyDescrizione != null) {
					CvDatiPersonaliDTO cvDatiPersonaliCopia = cvDatiPersonaliHome
							.copyById(getSession().getPrincipalId(), copyId, copyDescrizione, false);
					addInfoMessage("curriculum.copied");

					getAndCountCurricula();
				}
			} catch (EJBException e) {
				gestisciErrore(e, "data.error.copying");
			}
		} else {
			redirectGrave("generic.manipulation_error");
		}
	}

	public void deleteCurriculum() {
		try {
			Map<String, String> map = getRequestParameterMap();
			Integer deleteId = null;

			for (Entry<String, String> entry : map.entrySet()) {
				if (entry.getKey().endsWith("id_delete")) {
					deleteId = Integer.parseInt(entry.getValue());
				}
			}

			if (deleteId != null) {
				cvDatiPersonaliHome.deleteCurriculum(deleteId, session.getPrincipalId());
				addInfoMessage("curriculum.deleted");

				getAndCountCurricula();
			}
		} catch (EJBException e) {
			gestisciErrore(e, "data.error_deleting");
		}
	}

	public void deleteRicercaVacancy() {
		try {
			Map<String, String> map = getRequestParameterMap();
			Integer deleteId = null;

			for (Entry<String, String> entry : map.entrySet()) {
				if (entry.getKey().endsWith("id_delete")) {
					deleteId = Integer.parseInt(entry.getValue());
				}
			}

			if (deleteId != null) {
				rvTestataHome.removeCascadeById(deleteId, session.getPrincipalId());
				addInfoMessage("candidatura.ricerca_deleted");

				getAndCountRicerche();
			}
		} catch (EJBException e) {
			gestisciErrore(e, "data.error_deleting");
		}
	}

	public void copyLettera() {
		if (numeroLettere < ConstantsSingleton.LETTEREMAXCURR) {
			try {
				Map<String, String> map = getRequestParameterMap();
				Integer copyId = null;
				String copyDescrizione = null;

				for (Entry<String, String> entry : map.entrySet()) {
					if (entry.getKey().endsWith("descrizione_copia")) {
						copyDescrizione = entry.getValue();
					} else if (entry.getKey().endsWith("id_copia")) {
						copyId = Integer.parseInt(entry.getValue());
					}
				}

				if (copyId != null && copyDescrizione != null) {
					CvLetteraAccDTO cvLetteraAccCopia = cvLetteraAccHome.copyById(getSession().getPrincipalId(), copyId,
							copyDescrizione);
					addInfoMessage("lettere.copied");

					getAndCountLettere();
				}
			} catch (EJBException e) {
				gestisciErrore(e, "data.error.copying");
			}
		} else {
			redirectGrave("generic.manipulation_error");
		}
	}

	public void deleteLettera() {
		try {
			Map<String, String> map = getRequestParameterMap();
			Integer deleteId = null;

			for (Entry<String, String> entry : map.entrySet()) {
				if (entry.getKey().endsWith("id_delete")) {
					deleteId = Integer.parseInt(entry.getValue());
				}
			}

			if (deleteId != null) {
				cvLetteraAccHome.removeCascadeById(deleteId, session.getPrincipalId());
				addInfoMessage("lettere.deleted");

				getAndCountLettere();
			}
		} catch (EJBException e) {
			gestisciErrore(e, "data.error_deleting");
		}
	}

	public List<RvTestataDTO> getRicerche() {
		return ricerche;
	}

	public void setRicerche(List<RvTestataDTO> ricerche) {
		this.ricerche = ricerche;
	}

	public int getNumeroRicerche() {
		return numeroRicerche;
	}

	public void setNumeroRicerche(int numeroRicerche) {
		this.numeroRicerche = numeroRicerche;
	}

	/**
	 * @return the tokenSARE
	 */
	public String getTokenSARE() {
		return tokenSARE;
	}

	/**
	 * @param tokenSARE
	 *            the tokenSARE to set
	 */
	public void setTokenSARE(String tokenSARE) {
		this.tokenSARE = tokenSARE;
	}

	public String getUrlSARE() {
		return urlSARE;
	}

	public void setUrlSARE(String urlSARE) {
		this.urlSARE = urlSARE;
	}

	public String getUrlMyStage() {
		return urlMyStage;
	}

	public void setUrlMyStage(String urlMyStage) {
		this.urlMyStage = urlMyStage;
	}

	public boolean isCodiceFiscaleSettato() {
		UtenteInfoDTO utenteInfoDTO = utenteInfoHome.findDTOById(session.getPrincipalId());
		if (utenteInfoDTO.getCodiceFiscale() != null && !utenteInfoDTO.getCodiceFiscale().equals("")) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isDidFatta() {
		return didFatta;
	}

	public void setDidFatta(boolean didFatta) {
		this.didFatta = didFatta;
	}

}
