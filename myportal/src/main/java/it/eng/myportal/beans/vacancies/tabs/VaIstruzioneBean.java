package it.eng.myportal.beans.vacancies.tabs;

import it.eng.myportal.dtos.VaIstruzioneDTO;
import it.eng.myportal.entity.home.AbstractHome;
import it.eng.myportal.entity.home.VaIstruzioneHome;
import it.eng.myportal.entity.home.decodifiche.DeTitoloHome;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Bean della tab Istruzione della vacancy
 * 
 * @author Rodi A.
 *
 */
@ManagedBean
@ViewScoped
public class VaIstruzioneBean extends AbstractVaMasterDetailTabBean<VaIstruzioneDTO> {

	private String conoscenze;

	/**
	 * Injection degli EJB che mi servono a recuperare i dati dal DB
	 */

	@EJB
	VaIstruzioneHome vaIstruzioneHome;

	@EJB
	DeTitoloHome deTitoloHome;

	/**
	 * @return List<VaIstruzioneDTO>
	 */
	@Override
	protected List<VaIstruzioneDTO> retrieveData() {
		return vaIstruzioneHome.findDTOByVacancyId(vacancyId);
	}

	/**
	 * Costruisco un'istaziona vuota del DTO
	 * 
	 * @return VaIstruzioneDTO
	 */
	@Override
	public VaIstruzioneDTO buildNewDataIstance() {
		return new VaIstruzioneDTO();
	}

	@Override
	protected AbstractHome<?, VaIstruzioneDTO, Integer> getHome() {
		return vaIstruzioneHome;
	}

	public String getConoscenze() {
		return conoscenze;
	}

	public void setConoscenze(String conoscenze) {
		this.conoscenze = conoscenze;
	}

	@Override
	public void saveData() {
		super.saveData();
		data.setTitolo(deTitoloHome.findDTOById(data.getTitolo().getId()));
		list.add(data);
	}
}
