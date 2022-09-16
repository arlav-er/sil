package it.eng.myportal.beans.offertelavoro;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.RestoreParameters;
import it.eng.myportal.dtos.RvTestataDTO;
import it.eng.myportal.entity.RvContratto;
import it.eng.myportal.entity.decodifiche.DeContratto;
import it.eng.myportal.entity.home.RvRicercaVacancyHome;
import it.eng.myportal.entity.home.RvTestataHome;
import it.eng.myportal.entity.home.decodifiche.DeContrattoHome;
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
public class DeContrattoBean extends AbstractBaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private final static List<String> VALID_COLUMN_KEYS = Arrays.asList("descrizione");

	private String columnTemplate = "descrizione";

	private List<DeContratto> filteredDeContratto;

	private List<DeContratto> deContrattoSmall = new ArrayList<DeContratto>();

	private List<DataTableColumnModel> columns = new ArrayList<DataTableColumnModel>();;

	private DeContratto selectedDeContratto;
	private DeContratto[] selectedContratti = new DeContratto[0];
	private DeContrattoDataModel deContrattoModel;

	@EJB
	DeContrattoHome deContrattoHome;
	@EJB
	RvRicercaVacancyHome rvRicercaVacancyHome;
	@EJB
	RvTestataHome rvTestataHome;

	public DeContrattoBean() {

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
					List<RvContratto> listRv = rvTestataHome
							.findByIdRvTestata(RvContratto.class, new Integer(idString));
					selectedContratti = new DeContratto[listRv.size()];
					for (Iterator<RvContratto> iterator = listRv.iterator(); iterator.hasNext();) {
						DeContratto codCheck = ((RvContratto) iterator.next()).getDeContratto();

						selectedContratti[idx] = codCheck;
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

			deContrattoSmall = deContrattoHome.getAll();
			Map<String, String> occurencyMansione = rvRicercaVacancyHome.recuperaGruppoByPrimefaces(
					ConstantsSingleton.RvRicercaVacancy.CODCONTRATTO, filter, Arrays.asList(selectedContratti));
			for (int i = 0; i < deContrattoSmall.size(); i++) {
				String nuovaDescrizione = "";
				DeContratto mans = deContrattoSmall.get(i);
				if (occurencyMansione.containsKey(mans.getCodContratto())) {
					String valueOccurency = occurencyMansione.get(mans.getCodContratto());
					nuovaDescrizione = mans.getDescrizione() + " (" + valueOccurency + ")";
					mans.setOccurencyRicerca(new Integer(valueOccurency));
				} else {
					nuovaDescrizione = mans.getDescrizione() + " (0)";
					mans.setOccurencyRicerca(0);
				}

				deContrattoSmall.get(i).setDescrizione(nuovaDescrizione);
			}
			Collections.sort(deContrattoSmall, Collections.reverseOrder());
			deContrattoModel = new DeContrattoDataModel(deContrattoSmall);
			createDynamicColumns();

			if (session.getParams().containsKey("TOKEN_RicercaOfferteLavoroNewBean")) {
				List<DeContratto> selectedSession = (List<DeContratto>) sessionParam.get("listSelectedContratti");
				selectedContratti = new DeContratto[selectedSession.size()];
				for (int i = 0; i < selectedSession.size(); i++) {
					selectedContratti[i] = selectedSession.get(i);
				}
			}

		} finally {
			jamonMonitor.stop();
		}
	}

	public List<DeContratto> getDeContrattoSmall() {
		return deContrattoSmall;
	}

	public List<DataTableColumnModel> getColumns() {
		return columns;
	}

	public List<DeContratto> getFilteredDeContratto() {
		return filteredDeContratto;
	}

	public void setFilteredDeContratto(List<DeContratto> filteredDeContratto) {
		this.filteredDeContratto = filteredDeContratto;
	}

	public String getColumnTemplate() {
		return columnTemplate;
	}

	public void setColumnTemplate(String columnTemplate) {
		this.columnTemplate = columnTemplate;
	}

	public void updateColumns() {
		// reset table state
		UIComponent table = FacesContext.getCurrentInstance().getViewRoot().findComponent(":form:contratto");
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

	public DeContratto getSelectedDeContratto() {
		return selectedDeContratto;
	}

	public DeContratto[] getSelectedContratti() {
		return selectedContratti;
	}

	public DeContrattoDataModel getDeContrattoModel() {
		return deContrattoModel;
	}

	public void setSelectedDeContratto(DeContratto selectedDeContratto) {
		this.selectedDeContratto = selectedDeContratto;
	}

	public void setSelectedContratti(DeContratto[] selectedContratti) {
		this.selectedContratti = selectedContratti;
	}

	public void setDeContrattoModel(DeContrattoDataModel deContrattoModel) {
		this.deContrattoModel = deContrattoModel;
	}

	public void aggiornaListaByButton(String id, String tipoCodifica, String cosa, String dove) {

		// ricarico i checkbox flaggati
		DeContratto[] newselectedContratto = new DeContratto[0];
		if (("contratto").equalsIgnoreCase(tipoCodifica)) {
			for (int i = 0; i < selectedContratti.length; i++) {
				DeContratto att = selectedContratti[i];
				if (att.getCodContratto().equalsIgnoreCase(id)) {
					newselectedContratto = ArrayUtils.removeElement(selectedContratti, att);
					break;
				}
			}
			selectedContratti = newselectedContratto;
		}

	}

	public void removeAllListaByButton(String cosa, String dove) {
		// ricarico i checkbox flaggati
		selectedContratti = new DeContratto[0];

	}

	public void aggiornaLista(String cosa, String dove) {
		// ricarico la lista delle mansioni dopo la ricerca per cosa e dove
		RvTestataDTO filter = new RvTestataDTO();
		filter.setCosa(cosa);
		filter.setDove(dove);
		Map<String, String> occurency = rvRicercaVacancyHome.recuperaGruppoByPrimefaces(
				ConstantsSingleton.RvRicercaVacancy.CODCONTRATTO, filter, new ArrayList<DeContratto>());
		for (int i = 0; i < deContrattoSmall.size(); i++) {
			String nuovaDescrizione = "";
			DeContratto att = deContrattoSmall.get(i);
			String newDescrAtt = att.getDescrizione().substring(0, att.getDescrizione().lastIndexOf("("));
			if (occurency.containsKey(att.getCodContratto())) {
				String valueOccurency = occurency.get(att.getCodContratto());
				nuovaDescrizione = newDescrAtt + "(" + valueOccurency + ")";
				att.setOccurencyRicerca(new Integer(valueOccurency));
			} else {
				nuovaDescrizione = newDescrAtt + "(0)";
				att.setOccurencyRicerca(0);
			}

			deContrattoSmall.get(i).setDescrizione(nuovaDescrizione);
		}
		Collections.sort(deContrattoSmall, Collections.reverseOrder());
		deContrattoModel = new DeContrattoDataModel(deContrattoSmall);
	}
}
