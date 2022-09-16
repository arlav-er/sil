package it.eng.myportal.beans.offertelavoro;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.RestoreParameters;
import it.eng.myportal.dtos.RvTestataDTO;
import it.eng.myportal.entity.RvPatente;
import it.eng.myportal.entity.decodifiche.DePatente;
import it.eng.myportal.entity.decodifiche.sil.DePatenteSil;
import it.eng.myportal.entity.home.RvRicercaVacancyHome;
import it.eng.myportal.entity.home.RvTestataHome;
import it.eng.myportal.entity.home.decodifiche.DePatenteSilHome;
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
public class DePatenteSilBean extends AbstractBaseBean implements Serializable {
	private static final long serialVersionUID = 7222267323304470863L;

	private final static List<String> VALID_COLUMN_KEYS = Arrays.asList("descrizione");

	private String columnTemplate = "descrizione";

	private List<DePatenteSil> filteredDePatente;

	private List<DePatenteSil> dePatenteSmall = new ArrayList<DePatenteSil>();

	private List<DataTableColumnModel> columns = new ArrayList<DataTableColumnModel>();;

	private DePatenteSil selectedDePatente;
	private DePatenteSil[] selectedPatente = new DePatenteSil[0];
	private DePatenteSilDataModel dePatenteModel;

	@EJB
	DePatenteSilHome dePatenteSilHome;
	@EJB
	RvRicercaVacancyHome rvRicercaVacancyHome;
	@EJB
	RvTestataHome rvTestataHome;

	public DePatenteSilBean() {
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
					selectedPatente = new DePatenteSil[listRv.size()];
					for (Iterator<RvPatente> iterator = listRv.iterator(); iterator.hasNext();) {
						DePatenteSil codCheck = ((RvPatente) iterator.next()).getDePatenteSil();
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
			
			if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
				dePatenteSmall = dePatenteSilHome.findBySuggestionNoDto("");
			} else {
				dePatenteSmall = dePatenteSilHome.getAll();
			}

			Map<String, String> occurencyMansione = rvRicercaVacancyHome.recuperaGruppoByPrimefaces(
					ConstantsSingleton.RvRicercaVacancy.CODPATENTESIL, filter, Arrays.asList(selectedPatente));
			for (int i = 0; i < dePatenteSmall.size(); i++) {
				String nuovaDescrizione = "";
				DePatenteSil mans = dePatenteSmall.get(i);
				if (occurencyMansione.containsKey(mans.getCodPatenteSil())) {
					String valueOccurency = occurencyMansione.get(mans.getCodPatenteSil());
					nuovaDescrizione = mans.getDescrizione() + " (" + valueOccurency + ")";
					mans.setOccurencyRicerca(new Integer(valueOccurency));
				} else {
					nuovaDescrizione = mans.getDescrizione() + " (0)";
					mans.setOccurencyRicerca(0);
				}

				dePatenteSmall.get(i).setDescrizione(nuovaDescrizione);
			}
			Collections.sort(dePatenteSmall, Collections.reverseOrder());
			dePatenteModel = new DePatenteSilDataModel(dePatenteSmall);
			createDynamicColumns();

			if (session.getParams().containsKey("TOKEN_RicercaOfferteLavoroNewBean")) {
				List<DePatenteSil> selectedSession = (List<DePatenteSil>) sessionParam.get("listSelectedPatentiSil");
				selectedPatente = new DePatenteSil[selectedSession.size()];
				for (int i = 0; i < selectedSession.size(); i++) {
					selectedPatente[i] = selectedSession.get(i);
				}
			}

		} finally {
			jamonMonitor.stop();
		}
	}

	public List<DePatenteSil> getDePatenteSmall() {
		return dePatenteSmall;
	}

	public List<DataTableColumnModel> getColumns() {
		return columns;
	}

	public List<DePatenteSil> getFilteredDePatente() {
		return filteredDePatente;
	}

	public void setFilteredDePatente(List<DePatenteSil> filteredDePatente) {
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

	public DePatenteSil getSelectedDePatente() {
		return selectedDePatente;
	}

	public DePatenteSil[] getSelectedPatente() {
		return selectedPatente;
	}

	public DePatenteSilDataModel getDePatenteModel() {
		return dePatenteModel;
	}

	public void setSelectedDePatente(DePatenteSil selectedDePatente) {
		this.selectedDePatente = selectedDePatente;
	}

	public void setSelectedPatente(DePatenteSil[] selectedPatente) {
		this.selectedPatente = selectedPatente;
	}

	public void setDePatenteModel(DePatenteSilDataModel dePatenteModel) {
		this.dePatenteModel = dePatenteModel;
	}

	public void aggiornaListaByButton(String id, String tipoCodifica, String cosa, String dove) {

		// ricarico i checkbox flaggati
		DePatenteSil[] newselectedPatente = new DePatenteSil[0];
		if (("patente").equalsIgnoreCase(tipoCodifica)) {
			for (int i = 0; i < selectedPatente.length; i++) {
				DePatenteSil att = selectedPatente[i];
				if (att.getCodPatenteSil().equalsIgnoreCase(id)) {
					newselectedPatente = ArrayUtils.removeElement(selectedPatente, att);
					break;
				}
			}
			selectedPatente = newselectedPatente;
		}

	}

	public void removeAllListaByButton(String cosa, String dove) {
		// ricarico i checkbox
		selectedPatente = new DePatenteSil[0];
	}

	public void aggiornaLista(String cosa, String dove) {
		// ricarico la lista delle mansioni dopo la ricerca per cosa e dove
		RvTestataDTO filter = new RvTestataDTO();
		filter.setCosa(cosa);
		filter.setDove(dove);
		Map<String, String> occurency = rvRicercaVacancyHome.recuperaGruppoByPrimefaces(
				ConstantsSingleton.RvRicercaVacancy.CODPATENTESIL, filter, new ArrayList<DePatente>());
		for (int i = 0; i < dePatenteSmall.size(); i++) {
			String nuovaDescrizione = "";
			DePatenteSil att = dePatenteSmall.get(i);
			String newDescrAtt = att.getDescrizione().substring(0, att.getDescrizione().lastIndexOf("("));
			if (occurency.containsKey(att.getCodPatenteSil())) {
				String valueOccurency = occurency.get(att.getCodPatenteSil());
				nuovaDescrizione = newDescrAtt + "(" + valueOccurency + ")";
				att.setOccurencyRicerca(new Integer(valueOccurency));
			} else {
				nuovaDescrizione = newDescrAtt + "(0)";
				att.setOccurencyRicerca(0);
			}

			dePatenteSmall.get(i).setDescrizione(nuovaDescrizione);
		}
		Collections.sort(dePatenteSmall, Collections.reverseOrder());
		dePatenteModel = new DePatenteSilDataModel(dePatenteSmall);
	}
}
