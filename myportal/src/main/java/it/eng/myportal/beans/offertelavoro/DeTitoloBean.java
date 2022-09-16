package it.eng.myportal.beans.offertelavoro;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.RestoreParameters;
import it.eng.myportal.dtos.RvTestataDTO;
import it.eng.myportal.entity.RvTitolo;
import it.eng.myportal.entity.decodifiche.DeTitolo;
import it.eng.myportal.entity.home.RvRicercaVacancyHome;
import it.eng.myportal.entity.home.RvTestataHome;
import it.eng.myportal.entity.home.decodifiche.DeTitoloHome;
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
public class DeTitoloBean extends AbstractBaseBean implements Serializable  {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final static List<String> VALID_COLUMN_KEYS = Arrays.asList("descrizioneParlante");

    private String columnTemplate = "descrizioneParlante";

    private List<DeTitolo> filteredDeTitolo;

	private List<DeTitolo> deTitoloSmall = new ArrayList<DeTitolo>();

    private List<DataTableColumnModel> columns = new ArrayList<DataTableColumnModel>();;
    
    private DeTitolo selectedDeTitolo; 
   	private DeTitolo[] selectedTitoli = new DeTitolo[0];
   	private DeTitoloDataModel deTitoloModel;
    
    @EJB 
    DeTitoloHome deTitoloHome;
    @EJB 
    RvRicercaVacancyHome rvRicercaVacancyHome;
    @EJB
    RvTestataHome rvTestataHome;
    
    
	public DeTitoloBean() {	
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
					List<RvTitolo> listRv = rvTestataHome.findByIdRvTestata(RvTitolo.class, new Integer(idString));		
					selectedTitoli = new DeTitolo[listRv.size()];
					for (Iterator<RvTitolo> iterator = listRv.iterator(); iterator.hasNext();) {						
						DeTitolo codCheck = ((RvTitolo) iterator.next()).getDeTitolo();
												
						selectedTitoli[idx] = codCheck;
						idx = idx+1; 
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
			
			deTitoloSmall = deTitoloHome.getAll();				
			Map<String, String> occurencyMansione = rvRicercaVacancyHome.recuperaGruppoByPrimefaces(ConstantsSingleton.RvRicercaVacancy.CODTITOLO_STUDIO, filter, Arrays.asList(selectedTitoli));
			for (int i = 0; i < deTitoloSmall.size(); i++) {
				String nuovaDescrizione = "";
				DeTitolo mans = deTitoloSmall.get(i);
				if (occurencyMansione.containsKey(mans.getCodTitolo())) {
					String valueOccurency = occurencyMansione.get(mans.getCodTitolo());
					nuovaDescrizione = mans.getDescrizioneParlante()+" ("+valueOccurency+")";		
					mans.setOccurencyRicerca(new Integer(valueOccurency));
				}
				else {
					nuovaDescrizione = mans.getDescrizioneParlante()+" (0)";
					mans.setOccurencyRicerca(0);
				}	
				
				deTitoloSmall.get(i).setDescrizioneParlante(nuovaDescrizione.toUpperCase());
			}	
			Collections.sort(deTitoloSmall, Collections.reverseOrder());
			deTitoloModel = new DeTitoloDataModel(deTitoloSmall);
	        createDynamicColumns();
	        	
	        if (session.getParams().containsKey("TOKEN_RicercaOfferteLavoroNewBean")) {			
				List<DeTitolo> selectedSession = (List<DeTitolo>) sessionParam.get("listSelectedTitoli");
				selectedTitoli = new DeTitolo[selectedSession.size()];						
				for (int i = 0; i < selectedSession.size(); i++) {
					selectedTitoli[i] = selectedSession.get(i);
				}							
			}
		} finally {
			jamonMonitor.stop();
		}
	}
		
	public List<DeTitolo> getDeTitoloSmall() {
		return deTitoloSmall;
	}
		
    public List<DataTableColumnModel> getColumns() {
        return columns;
    }

    
    public List<DeTitolo> getFilteredDeTitolo() {
        return filteredDeTitolo;
    }

    public void setFilteredDeTitolo(List<DeTitolo> filteredDeTitolo) {
        this.filteredDeTitolo = filteredDeTitolo;
    }
            
    public String getColumnTemplate() {
        return columnTemplate;
    }
    public void setColumnTemplate(String columnTemplate) {
        this.columnTemplate = columnTemplate;
    }
    
    public void updateColumns() {
        //reset table state
        UIComponent table = FacesContext.getCurrentInstance().getViewRoot().findComponent(":form:titoli");
        table.setValueExpression("sortBy", null);
        
        //update columns
        createDynamicColumns();
    }
    
    public void createDynamicColumns() {
        String[] columnKeys = columnTemplate.split(" ");
        columns.clear();      
        
        for(String columnKey : columnKeys) {
            String key = columnKey.trim();
            
            if(VALID_COLUMN_KEYS.contains(key)) {
                columns.add(new DataTableColumnModel(columnKey.toUpperCase(), columnKey));
            }
        }
    }

	public DeTitolo getSelectedDeTitolo() {
		return selectedDeTitolo;
	}

	public DeTitolo[] getSelectedTitoli() {
		return selectedTitoli;
	}

	public DeTitoloDataModel getDeTitoloModel() {
		return deTitoloModel;
	}

	public void setSelectedDeTitolo(DeTitolo selectedDeTitolo) {
		this.selectedDeTitolo = selectedDeTitolo;
	}

	public void setSelectedTitoli(DeTitolo[] selectedTitoli) {
		this.selectedTitoli = selectedTitoli;
	}

	public void setDeTitoloModel(DeTitoloDataModel deTitoloModel) {
		this.deTitoloModel = deTitoloModel;
	}
	
	public void aggiornaListaByButton(String id,String tipoCodifica,String cosa, String dove) {
		
		// ricarico i checkbox flaggati
		DeTitolo[] newselectedTitolo = new DeTitolo[0];
		if (("titolo").equalsIgnoreCase(tipoCodifica)) {
			for (int i = 0; i < selectedTitoli.length; i++) {
				DeTitolo att = selectedTitoli[i];
				if (att.getCodTitolo().equalsIgnoreCase(id)) {
					newselectedTitolo = ArrayUtils.removeElement(selectedTitoli, att);
					break;
				}
			}
			selectedTitoli = newselectedTitolo;
		}
		
	}
	

	public void removeAllListaByButton(String cosa, String dove) {	
		// ricarico i checkbox 
		selectedTitoli = new DeTitolo[0];				
	}
	
	public void aggiornaLista(String cosa, String dove) {  		
		//ricarico la lista delle mansioni dopo la ricerca per cosa e dove 
		RvTestataDTO filter = new RvTestataDTO();
		filter.setCosa(cosa);
		filter.setDove(dove);
		Map<String, String> occurency = rvRicercaVacancyHome.recuperaGruppoByPrimefaces(ConstantsSingleton.RvRicercaVacancy.CODTITOLO_STUDIO, filter, new ArrayList<DeTitolo>());
		for (int i = 0; i < deTitoloSmall.size(); i++) {
			String nuovaDescrizione = "";
			DeTitolo att = deTitoloSmall.get(i);
			String newDescrAtt = att.getDescrizioneParlante().substring(0, att.getDescrizioneParlante().lastIndexOf("("));
			if (occurency.containsKey(att.getCodTitolo())) {
				String valueOccurency = occurency.get(att.getCodTitolo());				
				nuovaDescrizione = newDescrAtt+"("+valueOccurency+")";		
				att.setOccurencyRicerca(new Integer(valueOccurency));
			}
			else {
				nuovaDescrizione = newDescrAtt+"(0)";
				att.setOccurencyRicerca(0);
			}	
			
			deTitoloSmall.get(i).setDescrizioneParlante(nuovaDescrizione.toUpperCase());
		}			
		Collections.sort(deTitoloSmall, Collections.reverseOrder());
		deTitoloModel = new DeTitoloDataModel(deTitoloSmall);		
	}
}
				