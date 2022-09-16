package it.eng.myportal.beans.offertelavoro;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.RestoreParameters;
import it.eng.myportal.dtos.RvTestataDTO;
import it.eng.myportal.entity.RvPatente;
import it.eng.myportal.entity.decodifiche.DePatente;
import it.eng.myportal.entity.home.RvRicercaVacancyHome;
import it.eng.myportal.entity.home.RvTestataHome;
import it.eng.myportal.entity.home.decodifiche.DePatenteHome;
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
public class DePatenteBean extends AbstractBaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private final static List<String> VALID_COLUMN_KEYS = Arrays.asList("descrizione");

	private String columnTemplate = "descrizione";

	private List<DePatente> filteredDePatente;

	private List<DePatente> dePatenteSmall = new ArrayList<DePatente>();

	private List<DataTableColumnModel> columns = new ArrayList<DataTableColumnModel>();;

	private DePatente selectedDePatente;
	private DePatente[] selectedPatente = new DePatente[0];
	private DePatenteDataModel dePatenteModel;

	@EJB
	DePatenteHome dePatenteHome;
	@EJB
	RvRicercaVacancyHome rvRicercaVacancyHome;
	@EJB
	RvTestataHome rvTestataHome;

	public DePatenteBean() {
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
					List<RvPatente> listRv = rvTestataHome.findByIdRvTestata(RvPatente.class, new Integer(idString));
					selectedPatente = new DePatente[listRv.size()];
					for (Iterator<RvPatente> iterator = listRv.iterator(); iterator.hasNext();) {
						DePatente codCheck = ((RvPatente) iterator.next()).getDePatente();

						selectedPatente[idx] = codCheck;
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

			dePatenteSmall = dePatenteHome.getAll();
			Map<String, String> occurencyMansione = rvRicercaVacancyHome.recuperaGruppoByPrimefaces(
					ConstantsSingleton.RvRicercaVacancy.CODPATENTE, filter, Arrays.asList(selectedPatente));
			for (int i = 0; i < dePatenteSmall.size(); i++) {
				String nuovaDescrizione = "";
				DePatente mans = dePatenteSmall.get(i);
				if (occurencyMansione.containsKey(mans.getCodPatente())) {
					String valueOccurency = occurencyMansione.get(mans.getCodPatente());
					nuovaDescrizione = mans.getDescrizione() + " (" + valueOccurency + ")";
					mans.setOccurencyRicerca(new Integer(valueOccurency));
				} else {
					nuovaDescrizione = mans.getDescrizione() + " (0)";
					mans.setOccurencyRicerca(0);
				}

				dePatenteSmall.get(i).setDescrizione(nuovaDescrizione);
			}
			Collections.sort(dePatenteSmall, Collections.reverseOrder());
			dePatenteModel = new DePatenteDataModel(dePatenteSmall);
			createDynamicColumns();

			if (session.getParams().containsKey("TOKEN_RicercaOfferteLavoroNewBean")) {
				List<DePatente> selectedSession = (List<DePatente>) sessionParam.get("listSelectedPatenti");
				selectedPatente = new DePatente[selectedSession.size()];
				for (int i = 0; i < selectedSession.size(); i++) {
					selectedPatente[i] = selectedSession.get(i);
				}
			}

		} finally {
			jamonMonitor.stop();
		}
	}

	public List<DePatente> getDePatenteSmall() {
		return dePatenteSmall;
	}

	public List<DataTableColumnModel> getColumns() {
		return columns;
	}

	public List<DePatente> getFilteredDePatente() {
		return filteredDePatente;
	}

	public void setFilteredDePatente(List<DePatente> filteredDePatente) {
		this.filteredDePatente = filteredDePatente;
	}

	public String getColumnTemplate() {
		return columnTemplate;
	}

	public void setColumnTemplate(String columnTemplate) {
		this.columnTemplate = columnTemplate;
	}

	public void updateColumns() {
		// reset table state
		UIComponent table = FacesContext.getCurrentInstance().getViewRoot().findComponent(":form:patente");
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

	public DePatente getSelectedDePatente() {
		return selectedDePatente;
	}

	public DePatente[] getSelectedPatente() {
		return selectedPatente;
	}

	public DePatenteDataModel getDePatenteModel() {
		return dePatenteModel;
	}

	public void setSelectedDePatente(DePatente selectedDePatente) {
		this.selectedDePatente = selectedDePatente;
	}

	public void setSelectedPatente(DePatente[] selectedPatente) {
		this.selectedPatente = selectedPatente;
	}

	public void setDePatenteModel(DePatenteDataModel dePatenteModel) {
		this.dePatenteModel = dePatenteModel;
	}

	public void aggiornaListaByButton(String id, String tipoCodifica, String cosa, String dove) {

		// ricarico i checkbox flaggati
		DePatente[] newselectedPatente = new DePatente[0];
		if (("patente").equalsIgnoreCase(tipoCodifica)) {
			for (int i = 0; i < selectedPatente.length; i++) {
				DePatente att = selectedPatente[i];
				if (att.getCodPatente().equalsIgnoreCase(id)) {
					newselectedPatente = ArrayUtils.removeElement(selectedPatente, att);
					break;
				}
			}
			selectedPatente = newselectedPatente;
		}

	}

	public void removeAllListaByButton(String cosa, String dove) {
		// ricarico i checkbox
		selectedPatente = new DePatente[0];
	}

	public void aggiornaLista(String cosa, String dove) {
		// ricarico la lista delle mansioni dopo la ricerca per cosa e dove
		RvTestataDTO filter = new RvTestataDTO();
		filter.setCosa(cosa);
		filter.setDove(dove);
		Map<String, String> occurency = rvRicercaVacancyHome.recuperaGruppoByPrimefaces(
				ConstantsSingleton.RvRicercaVacancy.CODPATENTE, filter, new ArrayList<DePatente>());
		for (int i = 0; i < dePatenteSmall.size(); i++) {
			String nuovaDescrizione = "";
			DePatente att = dePatenteSmall.get(i);
			String newDescrAtt = att.getDescrizione().substring(0, att.getDescrizione().lastIndexOf("("));
			if (occurency.containsKey(att.getCodPatente())) {
				String valueOccurency = occurency.get(att.getCodPatente());
				nuovaDescrizione = newDescrAtt + "(" + valueOccurency + ")";
				att.setOccurencyRicerca(new Integer(valueOccurency));
			} else {
				nuovaDescrizione = newDescrAtt + "(0)";
				att.setOccurencyRicerca(0);
			}

			dePatenteSmall.get(i).setDescrizione(nuovaDescrizione);
		}
		Collections.sort(dePatenteSmall, Collections.reverseOrder());
		dePatenteModel = new DePatenteDataModel(dePatenteSmall);
	}
}
