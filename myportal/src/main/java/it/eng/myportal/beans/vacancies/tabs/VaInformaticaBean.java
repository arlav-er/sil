package it.eng.myportal.beans.vacancies.tabs;

import it.eng.myportal.dtos.VaInformaticaDTO;
import it.eng.myportal.entity.home.VaInformaticaHome;
import it.eng.myportal.entity.home.local.IVacancyEntityHome;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class VaInformaticaBean extends
		AbstractVacancyTabBean<VaInformaticaDTO> {

	@EJB
	private VaInformaticaHome vaInformaticaHome;

	@Override
	public VaInformaticaDTO buildNewDataIstance() {
		return new VaInformaticaDTO();
	}

	@Override
	protected IVacancyEntityHome<VaInformaticaDTO> getHome() {
		return vaInformaticaHome;
	}	 
}
