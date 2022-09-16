package it.eng.myportal.beans.vacancies.tabs;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.dtos.VaLinguaDTO;
import it.eng.myportal.entity.home.AbstractVacancyEntityListHome;
import it.eng.myportal.entity.home.VaLinguaHome;
import it.eng.myportal.entity.home.decodifiche.DeGradoLinHome;
import it.eng.myportal.entity.home.decodifiche.DeGradoLinSilHome;

@ManagedBean
@ViewScoped
public class VaLinguaBean extends AbstractVaMasterDetailTabBean<VaLinguaDTO> {
	/**
	 * Logger per registrare informazioni.
	 */
	protected static Log log = LogFactory.getLog(VaLinguaBean.class);

	@EJB
	private VaLinguaHome vaLinguaHome;

	@EJB
	private DeGradoLinHome deGradoLinHome;

	@EJB
	private DeGradoLinSilHome deGradoLinSilHome;

	private List<SelectItem> gradiLinguaOptions;

	/**
	 * Costruisco un'istaziona vuota del DTO
	 * 
	 * @return VaLinguaDTO
	 */
	@Override
	public VaLinguaDTO buildNewDataIstance() {
		return new VaLinguaDTO();
	}

	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		boolean addBlank = true;
		if (usaDecodificheSil) {
			setGradiLinguaOptions(deGradoLinSilHome.getListItems(addBlank));
		} else {
			setGradiLinguaOptions(deGradoLinHome.getListItems(addBlank));
		}
	}

	public List<SelectItem> getGradiLinguaOptions() {
		return gradiLinguaOptions;
	}

	public void setGradiLinguaOptions(List<SelectItem> gradiLinguaOptions) {
		this.gradiLinguaOptions = gradiLinguaOptions;
	}

	@Override
	protected AbstractVacancyEntityListHome<?, VaLinguaDTO> getHome() {
		return vaLinguaHome;
	}

	@Override
	protected List<VaLinguaDTO> retrieveData() {
		return vaLinguaHome.findDTOByVacancyId(vacancyId);
	}

	@Override
	public void save() {
		checkMadreLingua();
		super.save();
	}

	@Override
	public void update() {
		checkMadreLingua();
		super.update();
	}

	@Override
	public void saveData() {
		super.saveData();
		list.add(data);
	}

	private void checkMadreLingua() {
		if (data.isMadrelingua()) {
			data.setCodGradoLinguaLetto("");
			data.setCodGradoLinguaScritto("");
			data.setCodGradoLinguaParlato("");
		}
	}
}
