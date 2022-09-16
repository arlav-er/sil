package it.eng.myportal.beans.curriculum;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.AcCandidaturaDTO;
import it.eng.myportal.dtos.CvVisualizzaDTO;
import it.eng.myportal.dtos.PrimoContattoDTO;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.ejb.ClicLavoroPrimoContattoEjb;
import it.eng.myportal.entity.home.AcCandidaturaHome;
import it.eng.myportal.entity.home.CvAltreInfoHome;
import it.eng.myportal.entity.home.CvCompetenzeTrasvHome;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;
import it.eng.myportal.entity.home.CvEsperienzeProfHome;
import it.eng.myportal.entity.home.CvFormazioneHome;
import it.eng.myportal.entity.home.CvInformaticaHome;
import it.eng.myportal.entity.home.CvIstruzioneHome;
import it.eng.myportal.entity.home.CvLinguaHome;
import it.eng.myportal.entity.home.decodifiche.DeIdoneitaCandidaturaHome;
import it.eng.myportal.entity.home.local.ICvAbilitazioneHome;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * BackingBean della pagina di visualizzazione di un CV.
 * 
 * @author Rodi A.
 * 
 */
@ManagedBean
@ViewScoped
public class VisualizzaCurriculumVitaeBean extends AbstractBaseBean {
	protected static Log log = LogFactory.getLog(VisualizzaCurriculumVitaeBean.class);

	private CvVisualizzaDTO cvVisualizzaDTO;
	private boolean visualizzaRicercabilita;
	private Integer curriculumId;
	private Integer vacancyId;
	private Integer idAcCandidatura;
	private AcCandidaturaDTO candidatura;

	/**
	 * Contatto da parte dell'azienda che visualizza il CV
	 */
	private PrimoContattoDTO primoContatto;

	/*
	 * livelli di valutazione del CV che l'azienda puo' assegnare
	 */
	private List<SelectItem> livelliValutazione;
	/*
	 * livello di valutazione del CV assegnato dall'azienda
	 */
	private String codIdoneitaCandidatura;

	// Abilitazioni
	@EJB
	ICvAbilitazioneHome cvAbilitazioneHome;

	// Altre Info
	@EJB
	CvAltreInfoHome cvAltreInfoHome;

	// Competenze trasversali
	@EJB
	CvCompetenzeTrasvHome cvCompetenzeTrasvHome;

	@EJB
	CvDatiPersonaliHome cvDatiPersonaliHome;

	// Esperienze professionali
	@EJB
	CvEsperienzeProfHome cvEsperienzeProfHome;

	// Formazione
	@EJB
	CvFormazioneHome cvFormazioneHome;

	// Conoscenze informatiche
	@EJB
	CvInformaticaHome cvInformaticaHome;

	// Istruzione
	@EJB
	CvIstruzioneHome cvIstruzioneHome;

	// Lingua
	@EJB
	CvLinguaHome cvLinguaHome;

	@EJB
	DeIdoneitaCandidaturaHome deIdoneitaCandidaturaHome;

	@EJB
	AcCandidaturaHome acCandidaturaHome;

	@EJB
	ClicLavoroPrimoContattoEjb primoContattoEjb;

	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		try {
			Map<String, String> map = getRequestParameterMap();
			curriculumId = Integer.parseInt(map.get("id"));
			String vacancyIdString = map.get("idVaDatiVacancy");
			if (!StringUtils.isBlank(vacancyIdString)) {
				vacancyId = Integer.parseInt(vacancyIdString);
			}
			String idAcCandidaturaString = map.get("idAcCandidatura");
			if (!StringUtils.isBlank(idAcCandidaturaString)) {
				idAcCandidatura = Integer.parseInt(idAcCandidaturaString);
			}
			/*
			 * parametro per indicare se deve essere visualizzata l'informazione di ricercabilita' del CV
			 */
			String visualizzaRicercabilitaString = map.get("ricercabilita");
			if (!StringUtils.isBlank(visualizzaRicercabilitaString)) {
				visualizzaRicercabilita = Boolean.parseBoolean(visualizzaRicercabilitaString);
			}

			Integer idPfPrincipal = getSession().getPrincipalId();
			cvVisualizzaDTO = cvDatiPersonaliHome.findVisualizzaDTOById(curriculumId, idPfPrincipal);

			/*
			 * se sono un'azienda e il CV e' associato ad una candidatura inizializzo anche i dati relativi alla
			 * valutazione del CV
			 */
			boolean hasCandidatura = idAcCandidatura != null;
			boolean isAziendaSession = getSession().isAzienda();

			if (isAziendaSession) {
				if (hasCandidatura) {
					livelliValutazione = deIdoneitaCandidaturaHome.getListItems(true);
					candidatura = acCandidaturaHome.findDTOById(idAcCandidatura);
					codIdoneitaCandidatura = candidatura.getDeIdoneitaCandidaturaDTO().getId();
				}
				primoContatto = new PrimoContattoDTO();
				Integer idCvDatiPersonali = cvVisualizzaDTO.getId();
				primoContatto.setIdCvDatiPersonali(idCvDatiPersonali);
				primoContatto.setIdPfPrincipalAzienda(idPfPrincipal);
			}
		} catch (EJBException e) {
			gestisciErrore(e, "data.error_loading");
			redirectHome();
		} catch (NumberFormatException e) {
			log.error("Errore di manipolazine dei parametri in ingresso.");
			redirectGrave("generic.format_error");
		}
		log.debug("Costruito il Bean per visualizzare il CurriculuVitae completo!");
	}

	public void inviaPrimoContatto() {
		try {
			primoContattoEjb.sendPrimoContatto(primoContatto);
			addInfoMessage("messaggio.primo_contatto_ok");
		} catch (EJBException e) {
			gestisciErrore(e, "generic.error");
		}
	}

	public void assegnaValutazione() {
		candidatura.setDeIdoneitaCandidaturaDTO(deIdoneitaCandidaturaHome.findDTOById(codIdoneitaCandidatura));

		acCandidaturaHome.mergeDTO(candidatura, getSession().getPrincipalId());

		addInfoMessage("candidatura.idoneita.ok");
	}

	// Uso le decodifiche SIL se il cv in questione ha opzTipoDecodifiche != null e == 'SIL'
	public boolean usaDecodificheSil() {
		return CvDatiPersonali.OpzTipoDecodifiche.SIL.equals(cvVisualizzaDTO.getCvDatiPersonaliDTO()
				.getOpzTipoDecodifiche());
	}

	public CvVisualizzaDTO getCvVisualizzaDTO() {
		return cvVisualizzaDTO;
	}

	public void setCvVisualizzaDTO(CvVisualizzaDTO cvVisualizzaDTO) {
		this.cvVisualizzaDTO = cvVisualizzaDTO;
	}

	public Integer getCurriculumId() {
		return curriculumId;
	}

	public void setCurriculumId(Integer curriculumId) {
		this.curriculumId = curriculumId;
	}

	public Integer getVacancyId() {
		return vacancyId;
	}

	public void setVacancyId(Integer vacancyId) {
		this.vacancyId = vacancyId;
	}

	public Integer getIdAcCandidatura() {
		return idAcCandidatura;
	}

	public void setIdAcCandidatura(Integer idAcCandidatura) {
		this.idAcCandidatura = idAcCandidatura;
	}

	public boolean isVisualizzaRicercabilita() {
		return visualizzaRicercabilita;
	}

	public void setVisualizzaRicercabilita(boolean visualizzaRicercabilita) {
		this.visualizzaRicercabilita = visualizzaRicercabilita;
	}

	public List<SelectItem> getLivelliValutazione() {
		return livelliValutazione;
	}

	public void setLivelliValutazione(List<SelectItem> livelliValutazione) {
		this.livelliValutazione = livelliValutazione;
	}

	public String getCodIdoneitaCandidatura() {
		return codIdoneitaCandidatura;
	}

	public void setCodIdoneitaCandidatura(String codIdoneitaCandidatura) {
		this.codIdoneitaCandidatura = codIdoneitaCandidatura;
	}

	public PrimoContattoDTO getPrimoContatto() {
		return primoContatto;
	}

	public void setPrimoContatto(PrimoContattoDTO primoContatto) {
		this.primoContatto = primoContatto;
	}
}
