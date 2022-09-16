package it.eng.myportal.beans.offertelavoro;

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

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.RestoreParameters;
import it.eng.myportal.dtos.RvTestataDTO;
import it.eng.myportal.entity.RvOrario;
import it.eng.myportal.entity.decodifiche.sil.DeOrarioSil;
import it.eng.myportal.entity.home.RvRicercaVacancyHome;
import it.eng.myportal.entity.home.RvTestataHome;
import it.eng.myportal.entity.home.decodifiche.DeOrarioSilHome;
import it.eng.myportal.utils.ConstantsSingleton;

@ManagedBean
@ViewScoped
public class DeOrarioSilBean extends AbstractBaseBean implements Serializable {
	private static final long serialVersionUID = 5345456061535468313L;
	private final static List<String> VALID_COLUMN_KEYS = Arrays.asList("descrizione");

	private String columnTemplate = "descrizione";

	private List<DeOrarioSil> filteredDeOrario;

	private List<DeOrarioSil> deOrarioSmall = new ArrayList<DeOrarioSil>();

	private List<DataTableColumnModel> columns = new ArrayList<DataTableColumnModel>();;

	private DeOrarioSil selectedDeOrario;
	private DeOrarioSil[] selectedOrari = new DeOrarioSil[0];
	private DeOrarioSilDataModel deOrarioModel;

	@EJB
	DeOrarioSilHome deOrarioSilHome;

	@EJB
	RvRicercaVacancyHome rvRicercaVacancyHome;

	@EJB
	RvTestataHome rvTestataHome;

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
					selectedOrari = new DeOrarioSil[listRv.size()];
					for (Iterator<RvOrario> iterator = listRv.iterator(); iterator.hasNext();) {
						DeOrarioSil codCheck = ((RvOrario) iterator.next()).getDeOrarioSil();

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
			
			if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
				deOrarioSmall = deOrarioSilHome.findValideByFlagIdo(true);
			} else {
				deOrarioSmall = deOrarioSilHome.getAll();
			}
			Map<String, String> occurencyMansione = rvRicercaVacancyHome.recuperaGruppoByPrimefaces(
					ConstantsSingleton.RvRicercaVacancy.CODORARIOSIL, filter, Arrays.asList(selectedOrari));
			for (int i = 0; i < deOrarioSmall.size(); i++) {
				String nuovaDescrizione = "";
				DeOrarioSil mans = deOrarioSmall.get(i);
				if (occurencyMansione.containsKey(mans.getCodOrarioSil())) {
					String valueOccurency = occurencyMansione.get(mans.getCodOrarioSil());
					if(ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER){
						nuovaDescrizione = mans.getDescrizioneIdo() + " (" + valueOccurency + ")";
						mans.setOccurencyRicerca(new Integer(valueOccurency));
					}else{
						nuovaDescrizione = mans.getDescrizione() + " (" + valueOccurency + ")";
						mans.setOccurencyRicerca(new Integer(valueOccurency));
					}

				} else {
					if(ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER){
						nuovaDescrizione = mans.getDescrizioneIdo() + " (0)";
						mans.setOccurencyRicerca(0);
					}else{
						nuovaDescrizione = mans.getDescrizione() + " (0)";
						mans.setOccurencyRicerca(0);
					}
				}

				deOrarioSmall.get(i).setDescrizione(nuovaDescrizione);
			}
			Collections.sort(deOrarioSmall, Collections.reverseOrder());
			deOrarioModel = new DeOrarioSilDataModel(deOrarioSmall);
			createDynamicColumns();

			if (session.getParams().containsKey("TOKEN_RicercaOfferteLavoroNewBean")) {
				//List<DeOrarioSil> selectedSession = (List<DeOrarioSil>) sessionParam.get("listSelectedOrari");
				List<DeOrarioSil> selectedSession = (List<DeOrarioSil>) sessionParam.get("listSelectedOrariSil");
				selectedOrari = new DeOrarioSil[selectedSession.size()];
				for (int i = 0; i < selectedSession.size(); i++) {
					selectedOrari[i] = selectedSession.get(i);
				}
			}
		} finally {
			jamonMonitor.stop();
		}
	}

	public List<DeOrarioSil> getDeOrarioSmall() {
		return deOrarioSmall;
	}

	public List<DataTableColumnModel> getColumns() {
		return columns;
	}

	public List<DeOrarioSil> getFilteredDeOrario() {
		return filteredDeOrario;
	}

	public void setFilteredDeOrario(List<DeOrarioSil> filteredDeOrario) {
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

	public DeOrarioSil getSelectedDeOrario() {
		return selectedDeOrario;
	}

	public DeOrarioSil[] getSelectedOrari() {
		return selectedOrari;
	}

	public DeOrarioSilDataModel getDeOrarioModel() {
		return deOrarioModel;
	}

	public void setSelectedDeOrari(DeOrarioSil selectedDeOrario) {
		this.selectedDeOrario = selectedDeOrario;
	}

	public void setSelectedOrari(DeOrarioSil[] selectedOrari) {
		this.selectedOrari = selectedOrari;
	}

	public void setDeOrarioModel(DeOrarioSilDataModel deOrarioModel) {
		this.deOrarioModel = deOrarioModel;
	}

	public void aggiornaListaByButton(String id, String tipoCodifica, String cosa, String dove) {

		// ricarico i checkbox flaggati
		DeOrarioSil[] newselectedOrari = new DeOrarioSil[0];
		if (("orario").equalsIgnoreCase(tipoCodifica)) {
			for (int i = 0; i < selectedOrari.length; i++) {
				DeOrarioSil att = selectedOrari[i];
				if (att.getCodOrarioSil().equalsIgnoreCase(id)) {
					newselectedOrari = ArrayUtils.removeElement(selectedOrari, att);
					break;
				}
			}
			selectedOrari = newselectedOrari;
		}

	}

	public void removeAllListaByButton(String cosa, String dove) {
		// ricarico i checkbox
		selectedOrari = new DeOrarioSil[0];
	}

	public void aggiornaLista(String cosa, String dove) {
		// ricarico la lista delle mansioni dopo la ricerca per cosa e dove
		RvTestataDTO filter = new RvTestataDTO();
		filter.setCosa(cosa);
		filter.setDove(dove);
		Map<String, String> occurency = rvRicercaVacancyHome.recuperaGruppoByPrimefaces(
				ConstantsSingleton.RvRicercaVacancy.CODORARIOSIL, filter, new ArrayList<DeOrarioSil>());
		for (int i = 0; i < deOrarioSmall.size(); i++) {
			String nuovaDescrizione = "";
			DeOrarioSil att = deOrarioSmall.get(i);
			String newDescrAtt = null;			
			if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
				//newDescrAtt = att.getDescrizioneIdo().substring(0, att.getDescrizioneIdo().lastIndexOf("("));
				newDescrAtt = att.getDescrizioneIdo();
			}else {
				newDescrAtt = att.getDescrizione().substring(0, att.getDescrizione().lastIndexOf("("));
			}
			
			if (occurency.containsKey(att.getCodOrarioSil())) {
				String valueOccurency = occurency.get(att.getCodOrarioSil());
				nuovaDescrizione = newDescrAtt + "(" + valueOccurency + ")";
				att.setOccurencyRicerca(new Integer(valueOccurency));
			} else {
				nuovaDescrizione = newDescrAtt + "(0)";
				att.setOccurencyRicerca(0);
			}
			deOrarioSmall.get(i).setDescrizione(nuovaDescrizione);
		}
		Collections.sort(deOrarioSmall, Collections.reverseOrder());
		deOrarioModel = new DeOrarioSilDataModel(deOrarioSmall);
	}
}
