package it.eng.myportal.beans.offertelavoro;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.RestoreParameters;
import it.eng.myportal.dtos.RvTestataDTO;
import it.eng.myportal.entity.RvContratto;
import it.eng.myportal.entity.decodifiche.DeContratto;
import it.eng.myportal.entity.decodifiche.sil.DeContrattoSil;
import it.eng.myportal.entity.home.RvRicercaVacancyHome;
import it.eng.myportal.entity.home.RvTestataHome;
import it.eng.myportal.entity.home.decodifiche.DeContrattoSilHome;
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
public class DeContrattoSilBean extends AbstractBaseBean implements Serializable {
	private static final long serialVersionUID = -7174249313512739053L;

	private final static List<String> VALID_COLUMN_KEYS = Arrays.asList("descrizione");

	private String columnTemplate = "descrizione";

	private List<DeContrattoSil> filteredDeContratto;

	private List<DeContrattoSil> deContrattoSmall = new ArrayList<DeContrattoSil>();

	private List<DataTableColumnModel> columns = new ArrayList<DataTableColumnModel>();;

	private DeContrattoSil selectedDeContratto;
	private DeContrattoSil[] selectedContratti = new DeContrattoSil[0];
	private DeContrattoSilDataModel deContrattoModel;

	@EJB
	DeContrattoSilHome deContrattoSilHome;
	@EJB
	RvRicercaVacancyHome rvRicercaVacancyHome;
	@EJB
	RvTestataHome rvTestataHome;

	public DeContrattoSilBean() {

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
					selectedContratti = new DeContrattoSil[listRv.size()];
					for (Iterator<RvContratto> iterator = listRv.iterator(); iterator.hasNext();) {
						DeContrattoSil codCheck = ((RvContratto) iterator.next()).getDeContrattoSil();

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
			if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
				deContrattoSmall = deContrattoSilHome.findValideByFlagIdo(true);
			} else {
				deContrattoSmall = deContrattoSilHome.getAll();
			}
			
			Map<String, String> occurencyMansione = rvRicercaVacancyHome.recuperaGruppoByPrimefaces(
					ConstantsSingleton.RvRicercaVacancy.CODCONTRATTOSIL, filter, Arrays.asList(selectedContratti));
			for (int i = 0; i < deContrattoSmall.size(); i++) {
				String nuovaDescrizione = "";
				DeContrattoSil mans = deContrattoSmall.get(i);
				if (occurencyMansione.containsKey(mans.getCodContrattoSil())) {
					String valueOccurency = occurencyMansione.get(mans.getCodContrattoSil());
					nuovaDescrizione = mans.getDescrizione() + " (" + valueOccurency + ")";
					mans.setOccurencyRicerca(new Integer(valueOccurency));
				} else {
					nuovaDescrizione = mans.getDescrizione() + " (0)";
					mans.setOccurencyRicerca(0);
				}

				deContrattoSmall.get(i).setDescrizione(nuovaDescrizione);
			}
			Collections.sort(deContrattoSmall, Collections.reverseOrder());
			deContrattoModel = new DeContrattoSilDataModel(deContrattoSmall);
			createDynamicColumns();

			if (session.getParams().containsKey("TOKEN_RicercaOfferteLavoroNewBean")) {
				List<DeContrattoSil> selectedSession = (List<DeContrattoSil>) sessionParam.get("listSelectedContrattiSil");
				selectedContratti = new DeContrattoSil[selectedSession.size()];
				for (int i = 0; i < selectedSession.size(); i++) {
					selectedContratti[i] = selectedSession.get(i);
				}
			}

		} finally {
			jamonMonitor.stop();
		}
	}

	public List<DeContrattoSil> getDeContrattoSmall() {
		return deContrattoSmall;
	}

	public List<DataTableColumnModel> getColumns() {
		return columns;
	}

	public List<DeContrattoSil> getFilteredDeContratto() {
		return filteredDeContratto;
	}

	public void setFilteredDeContratto(List<DeContrattoSil> filteredDeContratto) {
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

	public DeContrattoSil getSelectedDeContratto() {
		return selectedDeContratto;
	}

	public DeContrattoSil[] getSelectedContratti() {
		return selectedContratti;
	}

	public DeContrattoSilDataModel getDeContrattoModel() {
		return deContrattoModel;
	}

	public void setSelectedDeContratto(DeContrattoSil selectedDeContratto) {
		this.selectedDeContratto = selectedDeContratto;
	}

	public void setSelectedContratti(DeContrattoSil[] selectedContratti) {
		this.selectedContratti = selectedContratti;
	}

	public void setDeContrattoModel(DeContrattoSilDataModel deContrattoModel) {
		this.deContrattoModel = deContrattoModel;
	}

	public void aggiornaListaByButton(String id, String tipoCodifica, String cosa, String dove) {

		// ricarico i checkbox flaggati
		DeContrattoSil[] newselectedContratto = new DeContrattoSil[0];
		if (("contratto").equalsIgnoreCase(tipoCodifica)) {
			for (int i = 0; i < selectedContratti.length; i++) {
				DeContrattoSil att = selectedContratti[i];
				if (att.getCodContrattoSil().equalsIgnoreCase(id)) {
					newselectedContratto = ArrayUtils.removeElement(selectedContratti, att);
					break;
				}
			}
			selectedContratti = newselectedContratto;
		}

	}

	public void removeAllListaByButton(String cosa, String dove) {
		// ricarico i checkbox flaggati
		selectedContratti = new DeContrattoSil[0];

	}

	public void aggiornaLista(String cosa, String dove) {
		// ricarico la lista delle mansioni dopo la ricerca per cosa e dove
		RvTestataDTO filter = new RvTestataDTO();
		filter.setCosa(cosa);
		filter.setDove(dove);
		Map<String, String> occurency = rvRicercaVacancyHome.recuperaGruppoByPrimefaces(
				ConstantsSingleton.RvRicercaVacancy.CODCONTRATTOSIL, filter, new ArrayList<DeContratto>());
		for (int i = 0; i < deContrattoSmall.size(); i++) {
			String nuovaDescrizione = "";
			DeContrattoSil att = deContrattoSmall.get(i);
			String newDescrAtt = att.getDescrizione().substring(0, att.getDescrizione().lastIndexOf("("));
			if (occurency.containsKey(att.getCodContrattoSil())) {
				String valueOccurency = occurency.get(att.getCodContrattoSil());
				nuovaDescrizione = newDescrAtt + "(" + valueOccurency + ")";
				att.setOccurencyRicerca(new Integer(valueOccurency));
			} else {
				nuovaDescrizione = newDescrAtt + "(0)";
				att.setOccurencyRicerca(0);
			}

			deContrattoSmall.get(i).setDescrizione(nuovaDescrizione);
		}
		Collections.sort(deContrattoSmall, Collections.reverseOrder());
		deContrattoModel = new DeContrattoSilDataModel(deContrattoSmall);
	}
}
