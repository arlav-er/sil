package it.eng.myportal.beans;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJBException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.context.FacesContext;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import it.eng.myportal.dtos.IHasPrimaryKey;
import it.eng.myportal.dtos.IUpdatable;
import it.eng.myportal.entity.home.AbstractHome;
import it.eng.myportal.exception.MyPortalException;

/**
 * Classe astratta per gestire una tab che contiene una lista ed un dettaglio. Il DTO deve implementare l'interfaccia
 * HasPrimaryKey per poter essere utilizzato all'interno della lista.
 * 
 * @author Rodi A.
 * 
 * @param <DTO>
 *            Classe del DTO associato alla tab.
 */
public abstract class AbstractMasterDetailTabBean<DTO extends IUpdatable & IHasPrimaryKey<Integer>>
		extends AbstractBaseBean {

	/**
	 * Lista degli elementi già inseriti
	 */
	protected List<DTO> list;

	/**
	 * I dati che sto visualizzando sono già stati salvati su DB o ne sto inserendo di nuovi?
	 */
	protected boolean saved;

	/**
	 * Determina se si sta mostrando o meno il pannello per l'inserimento dei dati.
	 */
	protected boolean showPanel;

	/**
	 * Determina se è necessario eseguire uno switch alla tab successiva in seguito al salvataggio.
	 */
	protected boolean switchtab;

	/**
	 * Dati della form.
	 */
	protected DTO data;

	/**
	 * La variabile è a true se si è in fase di modifica di dati già salvati.
	 */
	protected boolean editing;

	/**
	 * La variabile viene impostata a true se il salvataggio sta avvenendo a causa di uno switching tra tab la variabile
	 * rimane a false se l'utente ha semplicemente cliccato il pulsante salva.
	 * 
	 */
	protected Boolean tabClick = false;

	/**
	 * Workaround per ANNULLA da elemento della lista. Utile per mantenere lo stesso id tra il backing bean e le pagine
	 * jsf
	 */
	private String insertFormId = "insert_form";

	/**
	 * Costruisco un'istaziona vuota del DTO
	 * 
	 * @return DTO
	 */
	public abstract DTO buildNewDataIstance();

	/**
	 * Workaround per un bug legato alle azioni 'immediate' e i master/detail. Cancella il valore di tutti gli
	 * HtmlInputText figli dell'elemento radice.
	 * 
	 * @param component
	 *            UIComponent
	 */
	private void clearChildren(UIComponent component) {
		if (component.getChildCount() > 0) // se l'elemento ha figli, scorrili
			for (UIComponent el : component.getChildren()) {
				if (el instanceof HtmlInputText) { // alla ricerca degli
													// HtmlInputText
					log.debug("clearing " + el.getClientId());
					((HtmlInputText) el).setValue(null);
					((HtmlInputText) el).setSubmittedValue(null);
				} else {
					clearChildren(el);
				}
			}
	}

	/**
	 * Metodo collegato al bottone 'Elimina' del dialog modale del master
	 */
	public void delete() {
		Integer deleteId = Integer.parseInt(getRequestParameterEndsWith("objectId"));
		try {
			getHome().removeById(deleteId, session.getPrincipalId());
			int index = 0;
			for (int i = 0; i < list.size(); i += 1) {
				DTO element = list.get(i);
				if (element.getId() != null && element.getId().equals(deleteId)) {
					index = i;
					break;
				}
			}
			list.remove(index);
			showPanel = false;
			editing = true;
			saved = true;
			switchtab = false;
			addInfoMessage("data.deleted");
		} catch (EJBException e) {
			addErrorMessage("data.error_deleting", e);
		}
	}

	/**
	 * Metodo collegato al bottone 'Annulla Modifiche' del detail. Si occupa di cancellare i dati del detail per evitare
	 * stati inconsistenti.
	 */
	public void dontedit() {
		editing = false;
		saved = false;
		showPanel = false;
		data = buildNewDataIstance();
		// recupera la viewRoot
		UIViewRoot vr = FacesContext.getCurrentInstance().getViewRoot();
		// cerca l'insert panel, ovvero il pannello che contiene i dati del
		// detail.
		HtmlPanelGrid insPanel = findInsertPanel(vr);
		if (insPanel != null)
			clearChildren(insPanel);
	}

	/**
	 * Metodo collegato al bottone 'Modifica elemento della lista' del master
	 */
	public void edit() {
		Map<String, String> map = getRequestParameterMap();
		String id = map.get("id");
		for (DTO element : list) {
			if (element.getId() != null && element.getId().toString().equals(id)) {
				data = element;
				break;
			}
		}
		showPanel = true;
		editing = true;
		saved = true;
	}

	/**
	 * Metodo collegato al bottone 'Visualizza elemento della lista' del master
	 */
	public void view() {
		Map<String, String> map = getRequestParameterMap();
		String id = map.get("id");
		for (DTO element : list) {
			if (element.getId() != null && element.getId().toString().equals(id)) {
				data = element;
				break;
			}
		}
		showPanel = true;
		editing = false;
		saved = true;
	}

	/**
	 * Cerca un HtmlPanelGrid che abbia come id 'insert_form'
	 * 
	 * @param component
	 *            root da cui partire nella ricerca ricorsiva
	 * @return null se non trova il pannello.
	 */
	private HtmlPanelGrid findInsertPanel(UIComponent component) {
		if (component instanceof HtmlPanelGrid) {
			if ("insert_form".equals(component.getId())) {
				// se l'elemento è un HtmlPanelGrid con l'id specificato, hai
				// vinto
				return (HtmlPanelGrid) component;
			}
		}
		// scorri tutti i figli dell'elemento
		HtmlPanelGrid panel; // e verifica se uno di loro è il pannello
		for (UIComponent el : component.getChildren()) {
			panel = findInsertPanel(el);
			if (panel != null) {
				return panel;
			}
		}
		return null; // se non ho trovato l'elemento in questo sotto-albero
						// ritorno null.
	}

	public DTO getData() {
		return data;
	}

	/**
	 * @return the insertFormId
	 */
	public String getInsertFormId() {
		return insertFormId;
	}

	public List<DTO> getList() {
		return list;
	}

	public Boolean getTabClick() {
		return tabClick;
	}

	public boolean isEditing() {
		return editing;
	}

	public boolean isSaved() {
		return saved;
	}

	public boolean isShowPanel() {
		return showPanel;
	}

	public boolean isSwitchtab() {
		return switchtab;
	}

	/**
	 * Implementare in questo metodo le operazioni necesdsarie per generare la lista degli elementi, reuperandola da DB.
	 * 
	 * @return List<DTO>
	 */
	protected abstract List<DTO> retrieveData();

	/**
	 * Metodo collegato al bottone 'Salva' del detail.
	 */
	public void save() {
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + ".save");
		try {
			saveData();
			/*
			 * List<DTO> newList = retrieveData(); for (DTO elem : newList) { if (!list.contains(elem)) {
			 * list.add(elem); } }
			 */
			// reint();
			data = buildNewDataIstance();
			showPanel = false;
			editing = false;
			if (tabClick)
				switchtab = true;
			addInfoMessage("data.created");
		} catch (EJBException e) {
			if (e instanceof MyPortalException) {
				if (((MyPortalException) e).getCodErrore() != null) {
					addErrorMessage(((MyPortalException) e).getCodErrore(), e);
				} else {
					addErrorMessage("data.error_saving", e);
				}
			} else {
				addErrorMessage("data.error_saving", e);
			}
		} finally {
			jamonMonitor.stop();
		}
	}

	/**
	 * Implementare le operazioni necessarie a salvare il DTO su DB.
	 */
	protected abstract void saveData();

	public void setData(DTO data) {
		this.data = data;
	}

	public void setEditing(boolean editing) {
		this.editing = editing;
	}

	/**
	 * @param insertFormId
	 *            the insertFormId to set
	 */
	public void setInsertFormId(String insertFormId) {
		this.insertFormId = insertFormId;
	}

	public void setList(List<DTO> list) {
		this.list = list;
	}

	public void setSaved(boolean saved) {
		this.saved = saved;
	}

	public void setShowPanel(boolean showPanel) {
		this.showPanel = showPanel;
	}

	public void setSwitchtab(boolean switchtab) {
		this.switchtab = switchtab;
	}

	public void setTabClick(Boolean tabClick) {
		this.tabClick = tabClick;
	}

	/**
	 * Metodo collegato al bottone 'Inserisci Nuovo'
	 */
	public void showInsertPanel() {
		showPanel = true;
		editing = true;
		saved = false;
		data = buildNewDataIstance();
	}

	/**
	 * Metodo collegato al bottone 'Aggiorna' del detail
	 * 
	 */
	public void update() {
		Monitor jamonMonitor = MonitorFactory.start(this.getClass().getSimpleName() + ".update");
		try {
			data.setDtmMod(new Date());
			data = homeMerge(getHome(), data);
			editing = false;
			saved = true;
			showPanel = false;
			int index = 0;
			for (int i = 0; i < list.size(); i++) {
				DTO element = list.get(i);
				if (element.equals(data)) {
					index = i;
					break;
				}
			}
			list.set(index, data);
			showPanel = false;
			addInfoMessage("data.updated");

		} catch (EJBException e) {
			addErrorMessage("data.error_updating", e);
		} finally {
			jamonMonitor.stop();
		}
	}

	/**
	 * Restituire, attraverso questo metodo, l'EJB che fornisce i servizi per gestire questa sezione.
	 * 
	 * @return
	 */
	protected abstract AbstractHome<?, DTO, Integer> getHome();

}
