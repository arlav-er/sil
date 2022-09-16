package it.eng.myportal.beans.offertelavoro;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.RestoreParameters;
import it.eng.myportal.dtos.RvTestataDTO;
import it.eng.myportal.entity.RvAttivita;
import it.eng.myportal.entity.decodifiche.DeAttivita;
import it.eng.myportal.entity.home.RvRicercaVacancyHome;
import it.eng.myportal.entity.home.RvTestataHome;
import it.eng.myportal.entity.home.decodifiche.DeAttivitaHome;
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
public class DeAttivitaBean extends AbstractBaseBean implements Serializable  {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final static List<String> VALID_COLUMN_KEYS = Arrays.asList("descrizione");

    private String columnTemplate = "descrizione";

    private List<DeAttivita> filteredDeAttivita;

	private List<DeAttivita> deAttivitaSmall = new ArrayList<DeAttivita>();

    private List<DataTableColumnModel> columns = new ArrayList<DataTableColumnModel>();;
    
    private DeAttivita selectedDeAttivita; 
   	private DeAttivita[] selectedAttivita = new DeAttivita[0];;
   	private DeAttivitaDataModel deAttivitaModel;
    
    @EJB 
    DeAttivitaHome deAttivitaHome;  
    @EJB 
    RvRicercaVacancyHome rvRicercaVacancyHome;
    @EJB 
    RvTestataHome rvTestataHome;
    
	public DeAttivitaBean() {	
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
					
					List<RvAttivita> listRv = rvTestataHome.findByIdRvTestata(RvAttivita.class, Integer.parseInt(idString));		
					selectedAttivita = new DeAttivita[listRv.size()];
					for (Iterator<RvAttivita> iterator = listRv.iterator(); iterator.hasNext();) {						
						DeAttivita codCheck = ((RvAttivita) iterator.next()).getDeAttivita();
												
						selectedAttivita[idx] = codCheck;
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
			
			deAttivitaSmall = deAttivitaHome.getAll();					
			Map<String, String> occurencyMansione = rvRicercaVacancyHome.recuperaGruppoByPrimefaces(ConstantsSingleton.RvRicercaVacancy.CODSETTORE, filter, Arrays.asList(selectedAttivita));
			for (int i = 0; i < deAttivitaSmall.size(); i++) {
				String nuovaDescrizione = "";
				DeAttivita mans = deAttivitaSmall.get(i);
				if (occurencyMansione.containsKey(mans.getCodAteco())) {
					String valueOccurency = occurencyMansione.get(mans.getCodAteco());
					nuovaDescrizione = mans.getDescrizione()+" ("+valueOccurency+")";		
					mans.setOccurencyRicerca(new Integer(valueOccurency));
				}
				else {
					nuovaDescrizione = mans.getDescrizione()+" (0)";
					mans.setOccurencyRicerca(0);
				}	
				
				deAttivitaSmall.get(i).setDescrizione(nuovaDescrizione);
			}	
			Collections.sort(deAttivitaSmall, Collections.reverseOrder()); 
			deAttivitaModel = new DeAttivitaDataModel(deAttivitaSmall);
	        createDynamicColumns();	        	       
	        			
	        if (session.getParams().containsKey("TOKEN_RicercaOfferteLavoroNewBean")) {			
	        	List<DeAttivita> selectedSession = (List<DeAttivita>) sessionParam.get("listSelectedAttivita");
				selectedAttivita = new DeAttivita[selectedSession.size()];						
				for (int i = 0; i < selectedSession.size(); i++) {
					selectedAttivita[i] = selectedSession.get(i);
				}							
			}
	        
		} finally {
			jamonMonitor.stop();
		}
	}
		
	public List<DeAttivita> getDeAttivitaSmall() {
		return deAttivitaSmall;
	}
		
    public List<DataTableColumnModel> getColumns() {
        return columns;
    }

    
    public List<DeAttivita> getFilteredDeAttivita() {
        return filteredDeAttivita;
    }

    public void setFilteredDeAttivita(List<DeAttivita> filteredDeAttivita) {
        this.filteredDeAttivita = filteredDeAttivita;
    }
            
    public String getColumnTemplate() {
        return columnTemplate;
    }
    public void setColumnTemplate(String columnTemplate) {
        this.columnTemplate = columnTemplate;
    }
    
    public void updateColumns() {
        //reset table state
        UIComponent table = FacesContext.getCurrentInstance().getViewRoot().findComponent(":form:attivita");
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

	public DeAttivita getSelectedDeAttivita() {
		return selectedDeAttivita;
	}

	public DeAttivita[] getSelectedAttivita() {
		return selectedAttivita;
	}

	public DeAttivitaDataModel getDeAttivitaModel() {
		return deAttivitaModel;
	}

	public void setSelectedDeAttivita(DeAttivita selectedDeAttivita) {
		this.selectedDeAttivita = selectedDeAttivita; 
	}

	public void setSelectedAttivita(DeAttivita[] selectedAttivita) {
		this.selectedAttivita = selectedAttivita;
	}

	public void setDeAttivitaModel(DeAttivitaDataModel deAttivitaModel) {
		this.deAttivitaModel = deAttivitaModel;
	}
	
	public void aggiornaListaByButton(String id,String tipoCodifica,String cosa, String dove) {
		
		// ricarico i checkbox flaggati
		DeAttivita[] newselectedAttivita = new DeAttivita[0];
		if (("attivita").equalsIgnoreCase(tipoCodifica)) {
			for (int i = 0; i < selectedAttivita.length; i++) {
				DeAttivita att = selectedAttivita[i];
				if (att.getCodAteco().equalsIgnoreCase(id)) {
					newselectedAttivita = ArrayUtils.removeElement(selectedAttivita, att);
					break;
				}
			}
			selectedAttivita = newselectedAttivita;
		}
		
	}
	
	public void removeAllListaByButton(String cosa, String dove) {		
		// ricarico i checkbox flaggati
		selectedAttivita = new DeAttivita[0];
				
	}
	
	public void aggiornaLista(String cosa, String dove) {  		
		//ricarico la lista delle mansioni dopo la ricerca per cosa e dove 
		RvTestataDTO filter = new RvTestataDTO();
		filter.setCosa(cosa);
		filter.setDove(dove);
		Map<String, String> occurencyAttivita = rvRicercaVacancyHome.recuperaGruppoByPrimefaces(ConstantsSingleton.RvRicercaVacancy.CODSETTORE, filter, new ArrayList<DeAttivita>());
		for (int i = 0; i < deAttivitaSmall.size(); i++) {
			String nuovaDescrizione = "";
			DeAttivita att = deAttivitaSmall.get(i);			
			String newDescrAtt = att.getDescrizione().substring(0, att.getDescrizione().lastIndexOf("("));
			
			if (occurencyAttivita.containsKey(att.getCodAteco())) {
				String valueOccurency = occurencyAttivita.get(att.getCodAteco());				
				nuovaDescrizione = newDescrAtt+"("+valueOccurency+")";			
				att.setOccurencyRicerca(new Integer(valueOccurency));
			}
			else {
				nuovaDescrizione = newDescrAtt+"(0)";
				att.setOccurencyRicerca(0);
			}				
			deAttivitaSmall.get(i).setDescrizione(nuovaDescrizione);
		}		
		Collections.sort(deAttivitaSmall, Collections.reverseOrder());
		deAttivitaModel = new DeAttivitaDataModel(deAttivitaSmall);		
	}
}
				