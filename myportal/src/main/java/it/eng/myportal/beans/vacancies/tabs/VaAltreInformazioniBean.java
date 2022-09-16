/**
 * 
 */
package it.eng.myportal.beans.vacancies.tabs;

import it.eng.myportal.dtos.VaAltreInfoDTO;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.home.VaAltreInfoHome;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.home.decodifiche.DeAgevolazioneHome;
import it.eng.myportal.entity.home.decodifiche.DeAgevolazioneSilHome;
import it.eng.myportal.entity.home.decodifiche.DeGenereHome;
import it.eng.myportal.entity.home.decodifiche.DeMotivoGenereSilHome;
import it.eng.myportal.entity.home.local.IVacancyEntityHome;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

/**
 * @author iescone, rodi
 *
 */
@ManagedBean
@ViewScoped
public class VaAltreInformazioniBean extends AbstractVacancyTabBean<VaAltreInfoDTO> {

	@EJB
	VaAltreInfoHome vaAltreInfoHome;

	@EJB
	private DeAgevolazioneHome deAgevolazioneHome;

	@EJB
	private DeAgevolazioneSilHome deAgevolazioneSilHome;

	@EJB
	VaDatiVacancyHome vaDatiVacancyHome;

	@EJB
	DeGenereHome deGenereHome;

	@EJB
	DeMotivoGenereSilHome deMotivoGenereSilHome;

	private List<SelectItem> agevolazioni;
	private List<SelectItem> deGenereList;
	private List<SelectItem> deMotivoGenereSilList;

	private static final String COD_MOTIVO_GENERE_ALTRO = "ALT";

	@Override
	protected VaAltreInfoDTO buildNewDataIstance() {
		VaAltreInfoDTO vaAltreInfo = new VaAltreInfoDTO();
		VaDatiVacancy ret = vaDatiVacancyHome.findById(vacancyId);
		if (ret != null) {
			String opzNO = ret.getOpzNullaOsta();
			vaAltreInfo.setOpzNullaOsta(opzNO);
		}

		return vaAltreInfo;
	}

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		deGenereList = deGenereHome.getListItems(true);
		deMotivoGenereSilList = deMotivoGenereSilHome.getListItems(true);
		if (usaDecodificheSil) {
			agevolazioni = deAgevolazioneSilHome.getListItems(false);
		} else {
			agevolazioni = deAgevolazioneHome.getListItems(false);
		}
	}

	@Override
	public IVacancyEntityHome<VaAltreInfoDTO> getHome() {
		return vaAltreInfoHome;
	}

	/** La textarea "nota motivo genere" è abilitata solo se de_genere non è null e de_motivo_genere_sil è 'altro' */
	public Boolean isNotaMotivoGenereDisabled() {
		if (data.getDeGenere() != null && data.getDeGenere().getId() != null && data.getDeMotivoGenereSil() != null
				&& COD_MOTIVO_GENERE_ALTRO.equalsIgnoreCase(data.getDeMotivoGenereSil().getId())) {
			return false;
		} else {
			return true;
		}
	}

	public List<SelectItem> getAgevolazioni() {
		return agevolazioni;
	}

	public void setAgevolazioni(List<SelectItem> agevolazioni) {
		this.agevolazioni = agevolazioni;
	}

	public List<SelectItem> getDeGenereList() {
		return deGenereList;
	}

	public void setDeGenereList(List<SelectItem> deGenereList) {
		this.deGenereList = deGenereList;
	}

	public List<SelectItem> getDeMotivoGenereSilList() {
		return deMotivoGenereSilList;
	}

	public void setDeMotivoGenereSilList(List<SelectItem> deMotivoGenereSilList) {
		this.deMotivoGenereSilList = deMotivoGenereSilList;
	}

}
