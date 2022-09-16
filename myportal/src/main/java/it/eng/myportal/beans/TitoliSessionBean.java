package it.eng.myportal.beans;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import it.eng.myportal.beans.session.GenericTreeNodeVisit;
import it.eng.myportal.entity.decodifiche.DeBpMansione;
import it.eng.myportal.entity.decodifiche.DeTitolo;
import it.eng.myportal.entity.home.decodifiche.DeTitoloHome;

@ManagedBean(name = "titoliSessionBean")
@SessionScoped
public class TitoliSessionBean implements Serializable {

	
	private static final long serialVersionUID = 5205055318358781090L;
	
	protected static final Log log = LogFactory.getLog(TitoliSessionBean.class);

	
	@EJB
	DeTitoloHome deTitoloEJB;
	
	private TreeNode deTitoloTreeNode;
	private String filerTreeKeyword;

	// NEW ATTR
	private TreeNode deTitoloSelectedNode; 

	@ManagedProperty(value = "#{decodificheBean}")
	DecodificheBean decodificheBean;

	@PostConstruct
	protected void postConstruct() {
		deTitoloTreeNode = decodificheBean.getDeTitoloTreeNode();
	}

	public void initialize() {
		if (!FacesContext.getCurrentInstance().isPostback()) {
			if (getRequestParameter("jobsMansione") != null) {
				
				if(deTitoloSelectedNode != null){
				removeSelectedTitolo();
				}
				
				log.info("jobsMansione ricevuto da parametro: " + getRequestParameter("jobsMansione"));
				RequestContext.getCurrentInstance().execute("BorsaProfessioni.removeJobsMansioneParam()");
				String quartoLivello = getRequestParameter("jobsMansione").substring(0, 4);
				log.info("jobsMansione quartoLivello: " + quartoLivello);
				try {
					TreeNode remapped = GenericTreeNodeVisit.findMansioneInTree(deTitoloTreeNode,
							quartoLivello);
					log.info("jobsMansione rimappato: " + ((DeBpMansione)remapped.getData()).getCodMansione());
					setDeTitoloSelectedNode(remapped);
					getDeTitoloSelectedNode().setSelected(true);
					//buildInfoList();
				} catch (Exception e) {
					log.error("Errore nel ritorno da Jobs (mappatura professione) :" + e.getMessage());
				}
			}
		}
	}

	
	
	public void titoliDiStudioNodeSelected(NodeSelectEvent event) {
	  	TreeNode node = event.getTreeNode();
	  
	  }
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Funzione richiamata quando si seleziona una mansione nell'albero
	public void onTreeSelectEvent(NodeSelectEvent event) {
		//buildInfoList();
	}

	public void onTreeUnselectEvent(NodeUnselectEvent event) {
		//buildInfoList();
	}

	public void nodeExpand(NodeExpandEvent event) {
		event.getTreeNode().setExpanded(true);
	}

	public void nodeCollapse(NodeCollapseEvent event) {
		event.getTreeNode().setExpanded(false);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void filterDeMansioneTreeNode() {
		if (filerTreeKeyword == null)
			filerTreeKeyword = "";
		// mock root
		Date mileStone = new Date();
		TreeNode filteredTree = new DefaultTreeNode(new DeTitolo(), null);
		log.debug("Ricostruzione albero in corso. Size primo livello: " + deTitoloTreeNode.getChildCount());

		List<TreeNode> savedTree = GenericTreeNodeVisit.saveTreeSettings(deTitoloTreeNode);
		// Aggiungo i nodi selezionati in precedenza, necessario quando aggiorno
		// l'albero, altrimenti non li troverebbe
		addSelectedNode(savedTree);
		deTitoloTreeNode =decodificheBean.getDeMansioneTreeNode();
		// lista usata per tener traccia dei nodi selezionati
		List<TreeNode> selectedList = new ArrayList<TreeNode>();
		// recursive call
		if (filerTreeKeyword != null && filerTreeKeyword.length() > 0) { // se
																			// stringa
																			// di
																			// filtro
																			// valorizzata
			//Rimuove gli ultimi 2 caratteri della stringa di ricerca nel caso sia > 5
			filerTreeKeyword = filerTreeKeyword.length() > 5 ? filerTreeKeyword.substring(0, filerTreeKeyword.length()-2) : filerTreeKeyword;
			dfsTreeFilteringTitoli(deTitoloTreeNode, filteredTree, filerTreeKeyword, savedTree, selectedList, 4);
			// Aggiorno la tabella in relazione ai nodi selezionati
			deTitoloTreeNode = filteredTree;
		} else {
			GenericTreeNodeVisit.dsfTree(deTitoloTreeNode, savedTree, selectedList);
		}

		if (!selectedList.isEmpty()) {
			List<String> selectedListId = new ArrayList<String>();
			for (TreeNode node : selectedList) {
				DeBpMansione mansione = (DeBpMansione) node.getData();
				selectedListId.add(mansione.getCodMansione());
			}
			//deBpMansioneInfoList = deBpMansioneInfoEJB.getDeMansioneInfoListByListId(selectedListId);
		}

		log.info("Ricostruzione albero completata in " + (new Date().getTime() - mileStone.getTime())
				+ "msec. Size primo livello: " + deTitoloTreeNode.getChildCount());
	}

	/**
	 * Aggiunge alla lista dei nodi salvati che contenevano informazioni da riportare anche i nodi selezionati se non
	 * presenti
	 * 
	 * @param savedTree
	 */
	private void addSelectedNode(List<TreeNode> savedTree) {
	
	}



	public void removeSelectedTitolo() {
		for (TreeNode child : deTitoloSelectedNode.getChildren()) {
			child.setSelected(false);
		}
		deTitoloSelectedNode.setSelected(false);
		deTitoloSelectedNode = null;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 
	 * @param node
	 *            : nodo esaminato
	 * @param padre
	 *            : padre del nodo esaminato
	 * @param filterString
	 *            : stringa su cui viene effettuato il filtraggio
	 * @param savedTree
	 *            : contiene le informazioni dei nodi, utilizzato per copiare queste informazioni su questo nuovo albero
	 * @param selectedList
	 *            : lista dei nodi selezionati
	 * @param level
	 *            : livello dell'alberatura
	 * @param filterng
	 *            : indica se attivare o meno il filtraggio. Utilizzato anche per definire se espandere o meno i rami.
	 *            Necessario inserirlo poichè funzione richiamata anche quando stringa di filtraggio viene svuotata
	 */
	private void dfsTreeFilteringTitoli(TreeNode node, TreeNode padre, String filterString, List<TreeNode> savedTree,
			List<TreeNode> selectedList, int level) {
		DeTitolo dataObj = (DeTitolo) node.getData();
		TreeNode newNode = new DefaultTreeNode(dataObj, null);

		if (level == 0) { // Ultimo livello che voglio esaminare, se nodo foglia
							// filtro per descrizione lui e i suoi
							// figli
			if (compareDescrizioneAndCodiceWithFilterString(dataObj, filterString)
					|| IsfoglieContainsFilterString(node.getChildren(), filterString)) {
				// Se lui o uno dei figli contiene la stringa di filtering
				// aggiungo all'albero filtrato lui e i figli
				newNode.setExpanded(true);
				newNode.setParent(padre);
				padre.getChildren().add(newNode);
				GenericTreeNodeVisit.setNodeOption(newNode, savedTree, selectedList, node.isSelectable());
				// Ultimo livello aggiunto
				for (TreeNode child : node.getChildren()) {
					TreeNode childNode = new DefaultTreeNode(child.getData(), newNode);
					GenericTreeNodeVisit.setNodeOption(childNode, savedTree, selectedList, false);
				}
			}
		} else {
			// Se radice dell'albero la rimuovo a favore della nuova radice
			// (ramo else)
			if (dataObj.getDescrizione() != null) {
				// La stringa è contenuta nella descrizione del nodo, aggiungo
				// all'albero filtrato tutto il sottoalbero
				// con radice il nodo
				if (compareDescrizioneAndCodiceWithFilterString(dataObj, filterString)) {
					GenericTreeNodeVisit.drawDown(node, newNode, savedTree, selectedList);
					newNode.setExpanded(true);
					newNode.setParent(padre);
					padre.getChildren().add(newNode);
					GenericTreeNodeVisit.setNodeOption(newNode, savedTree, selectedList, node.isSelectable());
				} else {
					// Se stringa non contenuta nella descrizione filtro i figli
					for (TreeNode child : node.getChildren())
						dfsTreeFilteringTitoli(child, newNode, filterString, savedTree, selectedList, level - 1);
					// Se ho figli filtrati aggiungo all'albero anche questo
					// nodo
					if (newNode.getChildCount() > 0) {
						newNode.setParent(padre);
						newNode.setExpanded(true);
						padre.getChildren().add(newNode);
						GenericTreeNodeVisit.setNodeOption(newNode, savedTree, selectedList, node.isSelectable());
					} else {
						newNode.setExpanded(false);
					}
				}
			} else {
				// Filtro i figli, uso padre al posto di node come secondo
				// argomento per correggere l'aggiunta di un
				// livello di troppo tra la
				// radice e gli elementi durante la creazione dell'albero
				for (TreeNode child : node.getChildren())
					dfsTreeFilteringTitoli(child, padre, filterString, savedTree, selectedList, level - 1);
			}

		}
	}

	// Esegue la comparazione della stringa di filtering sia con la descrizione
	// che con il codice mansione
	private boolean compareDescrizioneAndCodiceWithFilterString(DeTitolo titolo, String filter) {
		if (titolo.getDescrizione().toLowerCase().contains(filter.toLowerCase()))
			return true;
//		if (titolo.getCodMansioneDot().toLowerCase().contentEquals(filter.toLowerCase()))
//			return true;
		return false;
	}

	// Indica se tra i figli vi è uno di essi con descrizione che contiene la
	// stringa di filtering
	private boolean IsfoglieContainsFilterString(List<TreeNode> children, String filterString) {
		for (TreeNode child : children) {
			DeTitolo dataObj = (DeTitolo) child.getData();
			if (compareDescrizioneAndCodiceWithFilterString(dataObj, filterString))
				return true;
		}
		return false;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/***
	 * SETTERS AND GETTERS
	 */

	public TreeNode getDeTitoloTreeNode() {
		return deTitoloTreeNode;
	}

	public void setDeTitoloTreeNode(TreeNode deTitoloTreeNode) {
		this.deTitoloTreeNode = deTitoloTreeNode;
	}

	public TreeNode getDeTitoloSelectedNode() {
		return deTitoloSelectedNode;
	}

	public void setDeTitoloSelectedNode(TreeNode deTitoloSelectedNode) {
		this.deTitoloSelectedNode = deTitoloSelectedNode;
	}

	public DecodificheBean getDecodificheBean() {
		return decodificheBean;
	}

	public void setDecodificheBean(DecodificheBean decodificheBean) {
		this.decodificheBean = decodificheBean;
	}

	public String getFilerTreeKeyword() {
		return filerTreeKeyword;
	}

	public void setFilerTreeKeyword(String filerTreeKeyword) {
		this.filerTreeKeyword = filerTreeKeyword;
	}

	protected FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	protected ExternalContext getExternalContext() {
		return getFacesContext().getExternalContext();
	}

	protected Map<String, String> getRequestParameterMap() {
		return getFacesContext().getExternalContext().getRequestParameterMap();
	}

	public String getRequestParameter(String name) {
		return getRequestParameterMap().get(name);
	}

}
