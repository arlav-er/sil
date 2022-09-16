package it.eng.myportal.beans.offertelavoro;

import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import it.eng.myportal.dtos.VacancyDaRedazioneDTO;
import it.eng.myportal.entity.home.RvRicercaVacancyHome;
import it.eng.myportal.helpers.RedazioneSearchParams;
import it.eng.myportal.helpers.VacancyRedazioneSortEnum;

public class RicercaVacancyModel extends LazyDataModel<VacancyDaRedazioneDTO> {

	private static final long serialVersionUID = -2698965085713786797L;

	private RedazioneSearchParams redazioneSearchParams;
	private RvRicercaVacancyHome rvRicercaVacancyHome;

	public RicercaVacancyModel(RedazioneSearchParams redazioneSearchParams, RvRicercaVacancyHome rvRicercaVacancyHome) {
		this.redazioneSearchParams = redazioneSearchParams;
		this.rvRicercaVacancyHome = rvRicercaVacancyHome;
		//ordinamento di default
		redazioneSearchParams.setSortOrder(VacancyRedazioneSortEnum.AZIENDA);
	}

	@Override
	public List<VacancyDaRedazioneDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {

		redazioneSearchParams.setStartFrom(first);
		redazioneSearchParams.setChunkSize(pageSize);

		if (sortField != null)
			redazioneSearchParams.setSortOrder(VacancyRedazioneSortEnum.valueOf(sortField));
		
		redazioneSearchParams.setAscending(SortOrder.ASCENDING.equals(sortOrder));

		List<VacancyDaRedazioneDTO> risultato = rvRicercaVacancyHome.searchVacancyDaRedazione(redazioneSearchParams);

		Long tCou = rvRicercaVacancyHome.getVacancydaRedazioneCount(redazioneSearchParams);

		this.setRowCount(tCou.intValue());

		return risultato;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

}
