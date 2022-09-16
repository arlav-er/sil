package it.eng.myportal.beans.vacancies.tabs;

import it.eng.myportal.dtos.VaAbilitazioniDTO;
import it.eng.myportal.entity.home.decodifiche.DeAlboHome;
import it.eng.myportal.entity.home.decodifiche.DeAlboSilHome;
import it.eng.myportal.entity.home.decodifiche.DePatenteHome;
import it.eng.myportal.entity.home.decodifiche.DePatenteSilHome;
import it.eng.myportal.entity.home.decodifiche.DePatentinoHome;
import it.eng.myportal.entity.home.decodifiche.DePatentinoSilHome;
import it.eng.myportal.entity.home.local.IVaAbilitazioneHome;
import it.eng.myportal.entity.home.local.IVacancyEntityHome;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

/**
 * Bean delle Competenze Trasversali
 * 
 * @author iescone, rodi
 *
 */
@ManagedBean
@ViewScoped
public class VaAbilitazioniBean extends AbstractVacancyTabBean<VaAbilitazioniDTO> {

	/**
	 * Injection degli EJB che mi servono a recuperare i dati dal DB
	 */
	@EJB
	IVaAbilitazioneHome vaAbilitazioniHome;

	@EJB
	DePatenteHome dePatenteHome;

	@EJB
	DePatentinoHome dePatentinoHome;

	@EJB
	DeAlboHome deAlboHome;

	@EJB
	DePatenteSilHome dePatenteSilHome;

	@EJB
	DePatentinoSilHome dePatentinoSilHome;

	@EJB
	DeAlboSilHome deAlboSilHome;

	private List<SelectItem> patenti;
	private List<SelectItem> patentini;
	private List<SelectItem> albi;

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		if (usaDecodificheSil) {
			patenti = dePatenteSilHome.getListItems(false, "descrizione");
			patentini = dePatentinoSilHome.getListItems(false, "descrizione");
			albi = deAlboSilHome.getListItems(false, "descrizione");
		} else {
			patenti = dePatenteHome.getListItems(false, "descrizione");
			patentini = dePatentinoHome.getListItems(false, "descrizione");
			albi = deAlboHome.getListItems(false, "descrizione");
		}
	}

	/**
	 * Costruisco un'istaziona vuota del DTO
	 * 
	 * @return VaCompetenzeTrasvDTO
	 */
	@Override
	public VaAbilitazioniDTO buildNewDataIstance() {
		return new VaAbilitazioniDTO();
	}

	@Override
	public IVacancyEntityHome<VaAbilitazioniDTO> getHome() {
		return vaAbilitazioniHome;
	}

	public List<SelectItem> getPatenti() {
		return patenti;
	}

	public void setPatenti(List<SelectItem> patenti) {
		this.patenti = patenti;
	}

	public List<SelectItem> getPatentini() {
		return patentini;
	}

	public void setPatentini(List<SelectItem> patentini) {
		this.patentini = patentini;
	}

	public List<SelectItem> getAlbi() {
		return albi;
	}

	public void setAlbi(List<SelectItem> albi) {
		this.albi = albi;
	}

}
