package it.eng.myportal.beans.curriculum.tabs;

import it.eng.myportal.dtos.CvEsperienzeProfDTO;
import it.eng.myportal.dtos.DeMansioneDTO;
import it.eng.myportal.dtos.DeMansioneMinDTO;
import it.eng.myportal.dtos.DeSezioneInfoDTO;
import it.eng.myportal.entity.decodifiche.sil.DeContrattoSil;
import it.eng.myportal.entity.home.AbstractCurriculumEntityListHome;
import it.eng.myportal.entity.home.CvEsperienzeProfHome;
import it.eng.myportal.entity.home.decodifiche.DeContrattoHome;
import it.eng.myportal.entity.home.decodifiche.DeContrattoSilHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneMinHome;
import it.eng.myportal.entity.home.decodifiche.DeSezioneInfoHome;
import it.eng.myportal.utils.ConstantsSingleton;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

@ManagedBean
@ViewScoped
public class CvEsperienzeProfessionaliBean extends AbstractCvMasterDetailTabBean<CvEsperienzeProfDTO> {

	@EJB
	CvEsperienzeProfHome cvEsperienzeProfHome;

	@EJB
	DeSezioneInfoHome deSezioneInfoHome;

	@EJB
	DeContrattoHome deContrattoHome;

	@EJB
	DeMansioneHome deMansioneHome;

	@EJB
	DeMansioneMinHome deMansioneMinHome;

	@EJB
	DeContrattoSilHome deContrattoSilHome;

	DeSezioneInfoDTO info;

	/* filtro per la selezione degli elementi dalla tabella de_mansione_min */
	private List<String> filtroQualifica = new ArrayList<String>();

	private List<SelectItem> rapportiLavoroOptions;

	@Override
	public CvEsperienzeProfDTO buildNewDataIstance() {
		return new CvEsperienzeProfDTO();
	}

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		if (curriculumVitaeBean.usaDecodificheSil()) {
			rapportiLavoroOptions = deContrattoSilHome.getListItems(true);
		} else {
			rapportiLavoroOptions = deContrattoHome.getListItems(true);
		}

		info = deSezioneInfoHome.findDTOById(ConstantsSingleton.DeServizioInfo.COD_CV_ESPERIENZE_PROFESSIONALI);
	}

	@Override
	public void edit() {
		super.edit();
		if (data.getCodMansione() != null) {
			this.filtroQualifica = deMansioneMinHome.getCodMansionePadreByCodMansione(data.getCodMansione());
		}
	}

	@Override
	protected AbstractCurriculumEntityListHome<?, CvEsperienzeProfDTO> getHome() {
		return cvEsperienzeProfHome;
	}

	/**
	 * Quando l'utente seleziona un tipo di contratto, prendo il contratto vero e proprio dal DB per settare subito il
	 * flagTirocini. Devo usare questo metodo perchè la pagina usa i componenti vecchi del portale e quindi non lo fa
	 * automaticamente.
	 */
	public void setDeContrattoFlagLavorativa() {
		if (data.getDeContratto() != null && data.getDeContratto().getId() != null) {
			DeContrattoSil contratto = deContrattoSilHome.findById(data.getDeContratto().getId());
			if (contratto != null) {
				data.getDeContratto().setFlagTirocini(contratto.getFlagTirocini());
			}
		}
	}

	public DeSezioneInfoDTO getInfo() {
		return info;
	}

	public void setInfo(DeSezioneInfoDTO info) {
		this.info = info;
	}

	public List<SelectItem> getRapportiLavoroOptions() {
		return rapportiLavoroOptions;
	}

	public void setRapportiLavoroOptions(List<SelectItem> rapportiLavoroOptions) {
		this.rapportiLavoroOptions = rapportiLavoroOptions;
	}

	public List<String> getFiltroQualifica() {
		return filtroQualifica;
	}

	public void setFiltroQualifica(List<String> filtroQualifica) {
		this.filtroQualifica = filtroQualifica;
	}

	public void setQualificaFilter(ValueChangeEvent event) {
		String val = (String) event.getNewValue();

		// TODO sarebbe meglio ottenere l'elemento per id
		List<DeMansioneDTO> lista = deMansioneHome.findByDescription(val);

		// se seleziono un elemento valido
		if (lista.size() > 0) {
			String codMansione = lista.get(0).getId();

			List<String> listCodMansionePadre = deMansioneMinHome.getCodMansionePadreByCodMansione(codMansione);

			this.filtroQualifica = listCodMansionePadre;
			this.data.setQualificaSvolta(new DeMansioneMinDTO());
		} else {
			// se non seleziono un elemento valido
			this.filtroQualifica = new ArrayList<String>();
			this.data.setStrMansione(null);
			this.data.setCodMansione(null);
			this.data.setQualificaSvolta(new DeMansioneMinDTO());
		}

		return;
	}
}