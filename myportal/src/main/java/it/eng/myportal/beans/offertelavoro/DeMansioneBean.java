package it.eng.myportal.beans.offertelavoro;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.RestoreParameters;
import it.eng.myportal.dtos.RvTestataDTO;
import it.eng.myportal.entity.RvMansione;
import it.eng.myportal.entity.decodifiche.DeMansione;
import it.eng.myportal.entity.home.RvRicercaVacancyHome;
import it.eng.myportal.entity.home.RvTestataHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneHome;
import it.eng.myportal.utils.ConstantsSingleton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.ArrayUtils;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@ManagedBean
@ViewScoped
public class DeMansioneBean extends AbstractBaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private final static List<String> VALID_COLUMN_KEYS = Arrays.asList("descrizione");

	private String columnTemplate = "descrizione";

	private List<DeMansione> filteredDeMansione;

	private List<DeMansione> deMansioneSmall = new ArrayList<DeMansione>();

	private List<DataTableColumnModel> columns = new ArrayList<DataTableColumnModel>();;

	private DeMansione selectedDeMansione;
	private DeMansione[] selectedMansioni = new DeMansione[0];
	private DeMansioneDataModel deMansioneModel;

	@EJB
	DeMansioneHome deMansioneHome;
	@EJB
	RvRicercaVacancyHome rvRicercaVacancyHome;
	@EJB
	RvTestataHome rvTestataHome;

	public DeMansioneBean() {
	}

	@Override
	@PostConstruct
	public void postConstruct() {
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + ".postConstruct");
		try {
			super.postConstruct();

			RvTestataDTO filter = new RvTestataDTO();
			// provo a recuperare i dati della ricerca salvata
			Map<String, String> map = getRequestParameterMap();
			try {
				String idString = map.get("id");
				if (idString != null) {
					int idx = 0;
					filter = rvTestataHome.findDTOById(Integer.parseInt(idString));
					List<RvMansione> listRvMansione = rvTestataHome.findByIdRvTestata(RvMansione.class, new Integer(
							idString));
					selectedMansioni = new DeMansione[listRvMansione.size()];
					for (Iterator<RvMansione> iterator = listRvMansione.iterator(); iterator.hasNext();) {
						DeMansione mansCheck = ((RvMansione) iterator.next()).getDeMansione();

						selectedMansioni[idx] = mansCheck;
						idx = idx + 1;
					}

				}
			} catch (NumberFormatException e) {
				throw new RuntimeException("Id non corretto");
			}

			RestoreParameters sessionParam = null;
			if (session.getParams().containsKey("TOKEN_RicercaOfferteLavoroNewBean")) {
				sessionParam = session.getParams().get("TOKEN_RicercaOfferteLavoroNewBean");
				filter = (RvTestataDTO) sessionParam.get("data");
			}

			deMansioneSmall = deMansioneHome.getAll();
			// aggiunge il numero di occorrenze
			Map<String, String> occurencyMansione = rvRicercaVacancyHome.recuperaGruppoByPrimefaces(
					ConstantsSingleton.RvRicercaVacancy.CODMANSIONE, filter, Arrays.asList(selectedMansioni));
			for (int i = 0; i < deMansioneSmall.size(); i++) {
				String nuovaDescrizione = "";
				DeMansione mans = deMansioneSmall.get(i);
				if (occurencyMansione.containsKey(mans.getCodMansione())) {
					String valueOccurency = occurencyMansione.get(mans.getCodMansione());
					nuovaDescrizione = mans.getDescrizione() + " (" + valueOccurency + ")";
					mans.setOccurencyRicerca(new Integer(valueOccurency));
				} else {
					nuovaDescrizione = mans.getDescrizione() + " (0)";
					mans.setOccurencyRicerca(0);
				}

				deMansioneSmall.get(i).setDescrizione(nuovaDescrizione);
			}

			Collections.sort(deMansioneSmall, Collections.reverseOrder());

			deMansioneModel = new DeMansioneDataModel(deMansioneSmall);
			createDynamicColumns();

			if (session.getParams().containsKey("TOKEN_RicercaOfferteLavoroNewBean")) {
				List<DeMansione> mansSelectedSession = (List<DeMansione>) sessionParam.get("listSelectedMansioni");
				selectedMansioni = new DeMansione[mansSelectedSession.size()];
				for (int i = 0; i < mansSelectedSession.size(); i++) {
					selectedMansioni[i] = mansSelectedSession.get(i);
				}
			}

		} finally {
			jamonMonitor.stop();
		}
	}

	public List<DeMansione> getDeMansioneSmall() {
		return deMansioneSmall;
	}

	public List<DataTableColumnModel> getColumns() {
		return columns;
	}

	public List<DeMansione> getFilteredDeMansione() {
		return filteredDeMansione;
	}

	public void setFilteredDeMansione(List<DeMansione> filteredDeMansione) {
		this.filteredDeMansione = filteredDeMansione;
	}

	public String getColumnTemplate() {
		return columnTemplate;
	}

	public void setColumnTemplate(String columnTemplate) {
		this.columnTemplate = columnTemplate;
	}

	public void updateColumns() {
		// reset table state
		UIComponent table = FacesContext.getCurrentInstance().getViewRoot().findComponent(":form:mansione");
		table.setValueExpression("sortBy", null);

		// update columns
		createDynamicColumns();
	}

	public void createDynamicColumns() {
		String[] columnKeys = columnTemplate.split(" ");
		columns.clear();

		for (String columnKey : columnKeys) {
			String key = columnKey.trim();

			if (VALID_COLUMN_KEYS.contains(key)) {
				columns.add(new DataTableColumnModel(columnKey.toUpperCase(), columnKey));
			}
		}
	}

	public DeMansione getSelectedDeMansione() {
		return selectedDeMansione;
	}

	public DeMansione[] getSelectedMansioni() {
		return selectedMansioni;
	}

	public DeMansioneDataModel getDeMansioneModel() {
		return deMansioneModel;
	}

	public void setSelectedDeMansione(DeMansione selectedDeMansione) {
		this.selectedDeMansione = selectedDeMansione;
	}

	public void setSelectedMansioni(DeMansione[] selectedMansioni) {
		this.selectedMansioni = selectedMansioni;
	}

	public void setDeMansioneModel(DeMansioneDataModel deMansioneModel) {
		this.deMansioneModel = deMansioneModel;
	}

	public void aggiornaListaByButton(String id, String tipoCodifica, String cosa, String dove) {
		// ricarico i checkbox flaggati
		DeMansione[] newselectedMansioni = new DeMansione[0];
		if (("mansione").equalsIgnoreCase(tipoCodifica)) {
			for (int i = 0; i < selectedMansioni.length; i++) {
				DeMansione mans = selectedMansioni[i];
				if (mans.getCodMansione().equalsIgnoreCase(id)) {
					newselectedMansioni = ArrayUtils.removeElement(selectedMansioni, mans);
					break;
				}
			}
			selectedMansioni = newselectedMansioni;
		}

	}

	public void removeAllListaByButton(String cosa, String dove) {
		// ricarico i checkbox
		selectedMansioni = new DeMansione[0];
	}

	public void aggiornaLista(String cosa, String dove) {
		// ricarico la lista delle mansioni dopo la ricerca per cosa e dove
		RvTestataDTO filter = new RvTestataDTO();
		filter.setCosa(cosa);
		filter.setDove(dove);
		// Map<String, String> occurencyMansione =
		// rvRicercaVacancyHome.recuperaGruppoByPrimefaces(ConstantsSingleton.RvRicercaVacancy.CODMANSIONE, filter,
		// Arrays.asList(selectedMansioni));
		Map<String, String> occurencyMansione = rvRicercaVacancyHome.recuperaGruppoByPrimefaces(
				ConstantsSingleton.RvRicercaVacancy.CODMANSIONE, filter, new ArrayList<DeMansione>());
		for (int i = 0; i < deMansioneSmall.size(); i++) {
			String nuovaDescrizione = "";
			DeMansione mans = deMansioneSmall.get(i);
			String newDescr = mans.getDescrizione().substring(0, mans.getDescrizione().lastIndexOf("("));

			if (occurencyMansione.containsKey(mans.getCodMansione())) {
				String valueOccurency = occurencyMansione.get(mans.getCodMansione());
				nuovaDescrizione = newDescr + "(" + valueOccurency + ")";
				mans.setOccurencyRicerca(new Integer(valueOccurency));
			} else {
				nuovaDescrizione = newDescr + "(0)";
				mans.setOccurencyRicerca(0);
			}

			deMansioneSmall.get(i).setDescrizione(nuovaDescrizione);
		}

		Collections.sort(deMansioneSmall, Collections.reverseOrder());
		deMansioneModel = new DeMansioneDataModel(deMansioneSmall);
	}

}
