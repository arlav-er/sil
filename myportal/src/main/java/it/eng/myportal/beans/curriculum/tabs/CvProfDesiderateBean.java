package it.eng.myportal.beans.curriculum.tabs;

import it.eng.myportal.beans.ICheckCliclavoro;
import it.eng.myportal.dtos.CvCandidaturaClDTO;
import it.eng.myportal.dtos.CvDatiPersonaliDTO;
import it.eng.myportal.dtos.CvProfDesiderateDTO;
import it.eng.myportal.dtos.DeMansioneDTO;
import it.eng.myportal.dtos.DeMansioneMinDTO;
import it.eng.myportal.dtos.DeSezioneInfoDTO;
import it.eng.myportal.entity.home.AbstractCurriculumEntityListHome;
import it.eng.myportal.entity.home.CvCandidaturaClHome;
import it.eng.myportal.entity.home.CvProfDesiderateHome;
import it.eng.myportal.entity.home.decodifiche.DeContrattoHome;
import it.eng.myportal.entity.home.decodifiche.DeContrattoSilHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneMinHome;
import it.eng.myportal.entity.home.decodifiche.DeOrarioHome;
import it.eng.myportal.entity.home.decodifiche.DeOrarioSilHome;
import it.eng.myportal.entity.home.decodifiche.DeSezioneInfoHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoInvioClHome;
import it.eng.myportal.entity.home.decodifiche.DeTrasfertaHome;
import it.eng.myportal.entity.home.decodifiche.DeTurnoSilHome;
import it.eng.myportal.utils.ConstantsSingleton;

import java.util.List;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

/**
 * Bean che implementa una tab con un master-detail all'interno
 * 
 * @author Turrini
 * 
 */
@ManagedBean
@ViewScoped
public class CvProfDesiderateBean extends AbstractCvMasterDetailTabBean<CvProfDesiderateDTO> implements
		ICheckCliclavoro {

	@EJB
	CvProfDesiderateHome cvProfessioniDesiderateHome;

	@EJB
	DeSezioneInfoHome deSezioneInfoHome;

	@EJB
	DeOrarioHome deOrarioHome;

	@EJB
	DeContrattoHome deContrattoHome;

	@EJB
	CvCandidaturaClHome cvCandidaturaClHome;

	@EJB
	DeMansioneHome deMansioneHome;

	@EJB
	DeMansioneMinHome deMansioneMinHome;

	@EJB
	DeStatoInvioClHome deStatoInvioClHome;

	@EJB
	DeTrasfertaHome deTrasfertaHome;

	@EJB
	DeTurnoSilHome deTurnoSilHome;

	@EJB
	DeOrarioSilHome deOrarioSilHome;

	@EJB
	DeContrattoSilHome deContrattoSilHome;

	DeSezioneInfoDTO info;

	private List<SelectItem> orario;
	private List<SelectItem> rapportiLavoro;
	private List<SelectItem> trasfertaOptions;
	private List<SelectItem> turnoOptions;
	private List<SelectItem> rapportoLavoroOptions;
	private List<SelectItem> orarioOptions;

	@PostConstruct
	@Override
	protected void postConstruct() {
		super.postConstruct();
		info = deSezioneInfoHome.findDTOById(ConstantsSingleton.DeServizioInfo.COD_CV_PROFESSIONI_DESIDERATE);

		if (curriculumVitaeBean.usaDecodificheSil()) {
			orario = deOrarioSilHome.getListItems(false);
			rapportiLavoro = deContrattoSilHome.getListItems(false);
		} else {
			orario = deOrarioHome.getListItems(false, "descrizione");
			rapportiLavoro = deContrattoHome.getListItems(false, "descrizione");
		}

		trasfertaOptions = deTrasfertaHome.getListItems(true);
		turnoOptions = deTurnoSilHome.getListItems(false, "descrizione");
	}

	/**
	 * Costruisco un'istaziona vuota del DTO
	 * 
	 * @return CvProfessioniDesiderateDTO
	 */
	@Override
	public CvProfDesiderateDTO buildNewDataIstance() {
		return new CvProfDesiderateDTO();
	}

	public DeSezioneInfoDTO getInfo() {
		return info;
	}

	public void setInfo(DeSezioneInfoDTO info) {
		this.info = info;
	}

	@Override
	protected AbstractCurriculumEntityListHome<?, CvProfDesiderateDTO> getHome() {
		return cvProfessioniDesiderateHome;
	}

	@Override
	protected List<CvProfDesiderateDTO> retrieveData() {

		return cvProfessioniDesiderateHome.findDTOByCurriculumId(curriculumId);
	}

	public List<SelectItem> getOrario() {
		return orario;
	}

	public void setOrario(List<SelectItem> orario) {
		this.orario = orario;
	}

	public List<SelectItem> getRapportiLavoro() {
		return rapportiLavoro;
	}

	public void setRapportiLavoro(List<SelectItem> rapportiLavoro) {
		this.rapportiLavoro = rapportiLavoro;
	}

	/**
	 * Sulle professioni desiderate c'è un controllo ulteriore che impedisce di cancellare l'ultima professione se è
	 * attiva la sincronizzazione con ClicLavoro.
	 */
	@Override
	public void delete() {
		CvDatiPersonaliDTO cv = cvDatiPersonaliHome.findDTOById(curriculumId);
		CvCandidaturaClDTO cvCandidaturaCl = cvCandidaturaClHome.findDTOById(curriculumId);

		String codStatoInvioCl = "";
		if (cvCandidaturaCl != null) {
			codStatoInvioCl = cvCandidaturaCl.getDeStatoInvioCl().getId();
		}

		boolean syncWithCLicLavoro = cv.isSincronizzatoCliclavoro();
		boolean erroreChiusura = deStatoInvioClHome.isErroreChiusura(codStatoInvioCl);
		boolean inAttesaChiusura = deStatoInvioClHome.isInAttesaChiusura(codStatoInvioCl);

		/*
		 * non posso eliminare l'ultima professione desiderata finche' sono sincronizzato con cliclavoro o finche'
		 * cliclavoro non ha risposto positivamente alla chiusura del CV
		 */
		if (syncWithCLicLavoro || erroreChiusura || inAttesaChiusura) {
			if (checkDatiCliclavoroSpecifico(null)) {
				super.delete();
			} else {
				addWarnMessage(getCheckMessageCliclavoro());
			}
		} else {
			/*
			 * non sono sincronizzato con cliclavoro quindi elimino la professione desiderata
			 */
			super.delete();
		}
	}

	@Override
	public void checkDatiCliclavoroListener(ComponentSystemEvent event) {
		/*
		 * qua non uso il listener perche' non riesco ad agganciarlo al pulsante "elimina" della tabella...
		 */
	}

	@Override
	public boolean checkDatiCliclavoroSpecifico(ComponentSystemEvent event) {
		boolean isLast = (list.size() == 1);

		if (isLast) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public String getCheckMessageCliclavoro() {
		return "cliclavoro.cv.warn.professioni_desiderate";
	}

	public void onSelectGruppoProfessionale(ValueChangeEvent event) {
		String val = (String) event.getNewValue();

		// TODO sarebbe meglio ottenere l'elemento per id
		List<DeMansioneDTO> lista = deMansioneHome.findByDescription(val);

		// se seleziono un elemento valido
		if (lista != null && lista.size() > 0) {
			String codMansione = lista.get(0).getId();

			List<String> listCodMansionePadre = deMansioneMinHome.getCodMansionePadreByCodMansione(codMansione);
			TreeSet<DeMansioneMinDTO> sortedList = deMansioneMinHome.findByCodMansioneMin(listCodMansionePadre);

			this.data.getListaDeMansioneMin().clear();
			this.data.getListaDeMansioneMin().addAll(sortedList);

		} else {
			// se non seleziono un elemento valido
			this.data.getListaDeMansioneMin().clear();

			this.data.setDeMansione(new DeMansioneDTO());
			this.data.setDeMansioneMin(new DeMansioneMinDTO());
		}

		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.eng.myportal.beans.curriculum.tabs.AbstractCvMasterDetailTabBean#saveData ()
	 */
	@Override
	protected void saveData() {
		data.setIdCvDatiPersonali(curriculumId);
		List<CvProfDesiderateDTO> lPersisted = cvProfessioniDesiderateHome.massivePersistByListaDeMansMinDTO(data,
				list, getSession().getPrincipalId());
		if (data.getListaDeMansioneMin() != null && lPersisted != null) {
			if (lPersisted.size() != data.getListaDeMansioneMin().size()) {
				addWarnMessage("curriculum.warn.professioni_desiderate_already_present");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.eng.myportal.beans.curriculum.tabs.AbstractCvMasterDetailTabBean#save ()
	 */
	@Override
	public void save() {
		super.save();
		list = retrieveData();
	}

	public List<SelectItem> getTrasfertaOptions() {
		return trasfertaOptions;
	}

	public void setTrasfertaOptions(List<SelectItem> trasfertaOptions) {
		this.trasfertaOptions = trasfertaOptions;
	}

	public List<SelectItem> getTurnoOptions() {
		return turnoOptions;
	}

	public void setTurnoOptions(List<SelectItem> turnoOptions) {
		this.turnoOptions = turnoOptions;
	}

	public List<SelectItem> getRapportoLavoroOptions() {
		return rapportoLavoroOptions;
	}

	public void setRapportoLavoroOptions(List<SelectItem> rapportoLavoroOptions) {
		this.rapportoLavoroOptions = rapportoLavoroOptions;
	}

	public List<SelectItem> getOrarioOptions() {
		return orarioOptions;
	}

	public void setOrarioOptions(List<SelectItem> orarioOptions) {
		this.orarioOptions = orarioOptions;
	}
}
