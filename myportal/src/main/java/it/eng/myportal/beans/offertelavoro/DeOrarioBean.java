package it.eng.myportal.beans.offertelavoro;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.RestoreParameters;
import it.eng.myportal.dtos.RvTestataDTO;
import it.eng.myportal.entity.RvOrario;
import it.eng.myportal.entity.decodifiche.DeOrario;
import it.eng.myportal.entity.home.RvRicercaVacancyHome;
import it.eng.myportal.entity.home.RvTestataHome;
import it.eng.myportal.entity.home.decodifiche.DeOrarioHome;
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
public class DeOrarioBean extends AbstractBaseBean implements Serializable {
	private static final long serialVersionUID = -651837798248140033L;

	private final static List<String> VALID_COLUMN_KEYS = Arrays.asList("descrizione");

	private String columnTemplate = "descrizione";

	private List<DeOrario> filteredDeOrario;

	private List<DeOrario> deOrarioSmall = new ArrayList<DeOrario>();

	private List<DataTableColumnModel> columns = new ArrayList<DataTableColumnModel>();;

	private DeOrario selectedDeOrario;
	private DeOrario[] selectedOrari = new DeOrario[0];
	private DeOrarioDataModel deOrarioModel;

	@EJB
	DeOrarioHome deOrarioHome;

	@EJB
	RvRicercaVacancyHome rvRicercaVacancyHome;

	@EJB
	RvTestataHome rvTestataHome;

	public DeOrarioBean() {
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
					List<RvOrario> listRv = rvTestataHome.findByIdRvTestata(RvOrario.class, new Integer(idString));
					selectedOrari = new DeOrario[listRv.size()];
					for (Iterator<RvOrario> iterator = listRv.iterator(); iterator.hasNext();) {
						DeOrario codCheck = ((RvOrario) iterator.next()).getDeOrario();

						selectedOrari[idx] = codCheck;
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

			deOrarioSmall = deOrarioHome.getAll();
			Map<String, String> occurencyMansione = rvRicercaVacancyHome.recuperaGruppoByPrimefaces(
					ConstantsSingleton.RvRicercaVacancy.CODORARIO, filter, Arrays.asList(selectedOrari));
			for (int i = 0; i < deOrarioSmall.size(); i++) {
				String nuovaDescrizione = "";
				DeOrario mans = deOrarioSmall.get(i);
				if (occurencyMansione.containsKey(mans.getCodOrario())) {
					String valueOccurency = occurencyMansione.get(mans.getCodOrario());
					nuovaDescrizione = mans.getDescrizione() + " (" + valueOccurency + ")";
					mans.setOccurencyRicerca(new Integer(valueOccurency));
				} else {
					nuovaDescrizione = mans.getDescrizione() + " (0)";
					mans.setOccurencyRicerca(0);
				}

				deOrarioSmall.get(i).setDescrizione(nuovaDescrizione);
			}
			Collections.sort(deOrarioSmall, Collections.reverseOrder());
			deOrarioModel = new DeOrarioDataModel(deOrarioSmall);
			createDynamicColumns();

			if (session.getParams().containsKey("TOKEN_RicercaOfferteLavoroNewBean")) {
				List<DeOrario> selectedSession = (List<DeOrario>) sessionParam.get("listSelectedOrari");
				selectedOrari = new DeOrario[selectedSession.size()];
				for (int i = 0; i < selectedSession.size(); i++) {
					selectedOrari[i] = selectedSession.get(i);
				}
			}
		} finally {
			jamonMonitor.stop();
		}
	}

	public List<DeOrario> getDeOrarioSmall() {
		return deOrarioSmall;
	}

	public List<DataTableColumnModel> getColumns() {
		return columns;
	}

	public List<DeOrario> getFilteredDeOrario() {
		return filteredDeOrario;
	}

	public void setFilteredDeOrario(List<DeOrario> filteredDeOrario) {
		this.filteredDeOrario = filteredDeOrario;
	}

	public String getColumnTemplate() {
		return columnTemplate;
	}

	public void setColumnTemplate(String columnTemplate) {
		this.columnTemplate = columnTemplate;
	}

	public void updateColumns() {
		// reset table state
		UIComponent table = FacesContext.getCurrentInstance().getViewRoot().findComponent(":form:orario");
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

	public DeOrario getSelectedDeOrario() {
		return selectedDeOrario;
	}

	public DeOrario[] getSelectedOrari() {
		return selectedOrari;
	}

	public DeOrarioDataModel getDeOrarioModel() {
		return deOrarioModel;
	}

	public void setSelectedDeOrari(DeOrario selectedDeOrario) {
		this.selectedDeOrario = selectedDeOrario;
	}

	public void setSelectedOrari(DeOrario[] selectedOrari) {
		this.selectedOrari = selectedOrari;
	}

	public void setDeOrarioModel(DeOrarioDataModel deOrarioModel) {
		this.deOrarioModel = deOrarioModel;
	}

	public void aggiornaListaByButton(String id, String tipoCodifica, String cosa, String dove) {

		// ricarico i checkbox flaggati
		DeOrario[] newselectedOrari = new DeOrario[0];
		if (("orario").equalsIgnoreCase(tipoCodifica)) {
			for (int i = 0; i < selectedOrari.length; i++) {
				DeOrario att = selectedOrari[i];
				if (att.getCodOrario().equalsIgnoreCase(id)) {
					newselectedOrari = ArrayUtils.removeElement(selectedOrari, att);
					break;
				}
			}
			selectedOrari = newselectedOrari;
		}

	}

	public void removeAllListaByButton(String cosa, String dove) {
		// ricarico i checkbox
		selectedOrari = new DeOrario[0];
	}

	public void aggiornaLista(String cosa, String dove) {
		// ricarico la lista delle mansioni dopo la ricerca per cosa e dove
		RvTestataDTO filter = new RvTestataDTO();
		filter.setCosa(cosa);
		filter.setDove(dove);
		Map<String, String> occurency = rvRicercaVacancyHome.recuperaGruppoByPrimefaces(
				ConstantsSingleton.RvRicercaVacancy.CODORARIO, filter, new ArrayList<DeOrario>());
		for (int i = 0; i < deOrarioSmall.size(); i++) {
			String nuovaDescrizione = "";
			DeOrario att = deOrarioSmall.get(i);
			String newDescrAtt = att.getDescrizione().substring(0, att.getDescrizione().lastIndexOf("("));
			if (occurency.containsKey(att.getCodOrario())) {
				String valueOccurency = occurency.get(att.getCodOrario());
				nuovaDescrizione = newDescrAtt + "(" + valueOccurency + ")";
				att.setOccurencyRicerca(new Integer(valueOccurency));
			} else {
				nuovaDescrizione = newDescrAtt + "(0)";
				att.setOccurencyRicerca(0);
			}
			deOrarioSmall.get(i).setDescrizione(nuovaDescrizione);
		}
		Collections.sort(deOrarioSmall, Collections.reverseOrder());
		deOrarioModel = new DeOrarioDataModel(deOrarioSmall);
	}
}
