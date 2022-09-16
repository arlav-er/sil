package it.eng.myportal.beans.offertelavoro;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.RestoreParameters;
import it.eng.myportal.dtos.RvTestataDTO;
import it.eng.myportal.entity.RvLingua;
import it.eng.myportal.entity.decodifiche.DeLingua;
import it.eng.myportal.entity.home.RvRicercaVacancyHome;
import it.eng.myportal.entity.home.RvTestataHome;
import it.eng.myportal.entity.home.decodifiche.DeLinguaHome;
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
public class DeLinguaBean extends AbstractBaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private final static List<String> VALID_COLUMN_KEYS = Arrays.asList("denominazione");

	private String columnTemplate = "denominazione";

	private List<DeLingua> filteredDeLingua;

	private List<DeLingua> deLinguaSmall = new ArrayList<DeLingua>();

	private List<DataTableColumnModel> columns = new ArrayList<DataTableColumnModel>();;

	private DeLingua selectedDeLingua;
	private DeLingua[] selectedLingue = new DeLingua[0];
	private DeLinguaDataModel deLinguaModel;

	@EJB
	DeLinguaHome deLinguaHome;
	@EJB
	RvRicercaVacancyHome rvRicercaVacancyHome;
	@EJB
	RvTestataHome rvTestataHome;

	public DeLinguaBean() {
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
					List<RvLingua> listRv = rvTestataHome.findByIdRvTestata(RvLingua.class, new Integer(idString));
					selectedLingue = new DeLingua[listRv.size()];
					for (Iterator<RvLingua> iterator = listRv.iterator(); iterator.hasNext();) {
						DeLingua codCheck = ((RvLingua) iterator.next()).getDeLingua();

						selectedLingue[idx] = codCheck;
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

			deLinguaSmall = deLinguaHome.getAll();
			Map<String, String> occurencyMansione = rvRicercaVacancyHome.recuperaGruppoByPrimefaces(
					ConstantsSingleton.RvRicercaVacancy.CODLINGUA, filter, Arrays.asList(selectedLingue));
			for (int i = 0; i < deLinguaSmall.size(); i++) {
				String nuovaDescrizione = "";
				DeLingua mans = deLinguaSmall.get(i);
				if (occurencyMansione.containsKey(mans.getCodLingua())) {
					String valueOccurency = occurencyMansione.get(mans.getCodLingua());
					nuovaDescrizione = mans.getDenominazione() + " (" + valueOccurency + ")";
					mans.setOccurencyRicerca(new Integer(valueOccurency));
				} else {
					nuovaDescrizione = mans.getDenominazione() + " (0)";
					mans.setOccurencyRicerca(0);
				}
				deLinguaSmall.get(i).setDenominazione(nuovaDescrizione);
			}
			Collections.sort(deLinguaSmall, Collections.reverseOrder());
			deLinguaModel = new DeLinguaDataModel(deLinguaSmall);
			createDynamicColumns();

			if (session.getParams().containsKey("TOKEN_RicercaOfferteLavoroNewBean")) {
				List<DeLingua> selectedSession = (List<DeLingua>) sessionParam.get("listSelectedLingue");
				selectedLingue = new DeLingua[selectedSession.size()];
				for (int i = 0; i < selectedSession.size(); i++) {
					selectedLingue[i] = selectedSession.get(i);
				}
			}

		} finally {
			jamonMonitor.stop();
		}
	}

	public List<DeLingua> getDeLinguaSmall() {
		return deLinguaSmall;
	}

	public List<DataTableColumnModel> getColumns() {
		return columns;
	}

	public List<DeLingua> getFilteredDeLingua() {
		return filteredDeLingua;
	}

	public void setFilteredDeLingua(List<DeLingua> filteredDeLingua) {
		this.filteredDeLingua = filteredDeLingua;
	}

	public String getColumnTemplate() {
		return columnTemplate;
	}

	public void setColumnTemplate(String columnTemplate) {
		this.columnTemplate = columnTemplate;
	}

	public void updateColumns() {
		// reset table state
		UIComponent table = FacesContext.getCurrentInstance().getViewRoot().findComponent(":form:lingua");
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

	public DeLingua getSelectedDeLingua() {
		return selectedDeLingua;
	}

	public DeLingua[] getSelectedLingue() {
		return selectedLingue;
	}

	public DeLinguaDataModel getDeLinguaModel() {
		return deLinguaModel;
	}

	public void setSelectedDeLingua(DeLingua selectedDeLingua) {
		this.selectedDeLingua = selectedDeLingua;
	}

	public void setSelectedLingue(DeLingua[] selectedLingue) {
		this.selectedLingue = selectedLingue;
	}

	public void setDeLinguaModel(DeLinguaDataModel deLinguaModel) {
		this.deLinguaModel = deLinguaModel;
	}

	public void aggiornaListaByButton(String id, String tipoCodifica, String cosa, String dove) {

		// ricarico i checkbox flaggati
		DeLingua[] newselectedLingue = new DeLingua[0];
		if (("lingua").equalsIgnoreCase(tipoCodifica)) {
			for (int i = 0; i < selectedLingue.length; i++) {
				DeLingua att = selectedLingue[i];
				if (att.getCodLingua().equalsIgnoreCase(id)) {
					newselectedLingue = ArrayUtils.removeElement(selectedLingue, att);
					break;
				}
			}
			selectedLingue = newselectedLingue;
		}

	}

	public void removeAllListaByButton(String cosa, String dove) {
		// ricarico i checkbox flaggati
		selectedLingue = new DeLingua[0];

	}

	public void aggiornaLista(String cosa, String dove) {
		// ricarico la lista delle mansioni dopo la ricerca per cosa e dove
		RvTestataDTO filter = new RvTestataDTO();
		filter.setCosa(cosa);
		filter.setDove(dove);
		Map<String, String> occurency = rvRicercaVacancyHome.recuperaGruppoByPrimefaces(
				ConstantsSingleton.RvRicercaVacancy.CODLINGUA, filter, new ArrayList<DeLingua>());
		for (int i = 0; i < deLinguaSmall.size(); i++) {
			String nuovaDescrizione = "";
			DeLingua att = deLinguaSmall.get(i);
			String newDescrAtt = att.getDenominazione().substring(0, att.getDenominazione().lastIndexOf("("));
			if (occurency.containsKey(att.getCodLingua())) {
				String valueOccurency = occurency.get(att.getCodLingua());
				nuovaDescrizione = newDescrAtt + "(" + valueOccurency + ")";
				att.setOccurencyRicerca(new Integer(valueOccurency));
			} else {
				nuovaDescrizione = newDescrAtt + "(0)";
				att.setOccurencyRicerca(0);
			}

			deLinguaSmall.get(i).setDenominazione(nuovaDescrizione);
		}
		Collections.sort(deLinguaSmall, Collections.reverseOrder());
		deLinguaModel = new DeLinguaDataModel(deLinguaSmall);
	}
}
