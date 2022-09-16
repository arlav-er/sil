package it.eng.myportal.beans.session;

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

//ATTENZIONE! COMMENTATO CLONE quindi albero bacato
//import com.rits.cloning.Cloner;

import it.eng.myportal.beans.DecodificheBean;
import it.eng.myportal.entity.decodifiche.DeBpMansione;
import it.eng.myportal.entity.home.decodifiche.DeBpMansioneHome;

@ManagedBean(name = "mansioniSessionBean")
@SessionScoped
public class MansioniSessionBean implements Serializable {

	private static final long serialVersionUID = 7415469024307877897L;

	protected static final Log log = LogFactory.getLog(MansioniSessionBean.class);
 

	@EJB
	DeBpMansioneHome deBpMansioneEJB;

	private TreeNode deMansioneTreeNode;
	private TreeNode deMansioneTreeNodeAgricoli;
	private String filerTreeKeyword;
	// private TreeNode[] deMansioneSelectedNodes;

	// NEW ATTR
	private TreeNode deMansioneSelectedNode; 

	@ManagedProperty(value = "#{decodificheBean}")
	DecodificheBean decodificheBean;

	@PostConstruct
	protected void postConstruct() {
		deMansioneTreeNode = decodificheBean.getDeMansioneTreeNode();
		deMansioneTreeNodeAgricoli = decodificheBean.getDeMansioneTreeNodeAgricoli();
	}

	public void initialize() {
		if (!FacesContext.getCurrentInstance().isPostback()) {
			if (getRequestParameter("jobsMansione") != null) {
				
				if(deMansioneSelectedNode != null){
				removeSelectedMansione();}
				
				log.info("jobsMansione ricevuto da parametro: " + getRequestParameter("jobsMansione"));
				RequestContext.getCurrentInstance().execute("BorsaProfessioni.removeJobsMansioneParam()");
				String quartoLivello = getRequestParameter("jobsMansione").substring(0, 4);
				log.info("jobsMansione quartoLivello: " + quartoLivello);
				try {
					TreeNode remapped = GenericTreeNodeVisit.findMansioneInTree(deMansioneTreeNode,
							quartoLivello);
					log.info("jobsMansione rimappato: " + ((DeBpMansione)remapped.getData()).getCodMansione());
					setDeMansioneSelectedNode(remapped);
					getDeMansioneSelectedNode().setSelected(true);
					//buildInfoList();
				} catch (Exception e) {
					log.error("Errore nel ritorno da Jobs (mappatura professione) :" + e.getMessage());
				}
			}
		}
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
		TreeNode filteredTree = new DefaultTreeNode(new DeBpMansione(), null);
		log.debug("Ricostruzione albero in corso. Size primo livello: " + deMansioneTreeNode.getChildCount());

		List<TreeNode> savedTree = GenericTreeNodeVisit.saveTreeSettings(deMansioneTreeNode);
		// Aggiungo i nodi selezionati in precedenza, necessario quando aggiorno
		// l'albero, altrimenti non li troverebbe
		addSelectedNode(savedTree);
		deMansioneTreeNode =decodificheBean.getDeMansioneTreeNode();
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
			dfsTreeFilteringMansioni(deMansioneTreeNode, filteredTree, filerTreeKeyword, savedTree, selectedList, 4);
			// Aggiorno la tabella in relazione ai nodi selezionati
			deMansioneTreeNode = filteredTree;
		} else {
			GenericTreeNodeVisit.dsfTree(deMansioneTreeNode, savedTree, selectedList);
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
				+ "msec. Size primo livello: " + deMansioneTreeNode.getChildCount());
	}

	/**
	 * Aggiunge alla lista dei nodi salvati che contenevano informazioni da riportare anche i nodi selezionati se non
	 * presenti
	 * 
	 * @param savedTree
	 */
	private void addSelectedNode(List<TreeNode> savedTree) {
		/*if (!deBpMansioneInfoList.isEmpty()) {
			List<TreeNode> nodesTosave = new ArrayList<TreeNode>();
			for (DeBpMansioneInfo mansioneInfo : deBpMansioneInfoList) {
				for (TreeNode node : savedTree) {
					DeBpMansione mansione = (DeBpMansione) node.getData();
					if (mansione.getId().equals(mansioneInfo.getId()))
						break;
				}
				try {
					TreeNode newNode = new DefaultTreeNode(deBpMansioneEJB.findById(mansioneInfo.getId()), null);
					newNode.setSelectable(true);
					newNode.setSelected(true);
					newNode.setExpanded(true);
					nodesTosave.add(newNode);
				} catch (MyCasNoResultException e) {
					log.error("Mansione non trovata:" + e.getMessage());
				}
			}
			savedTree.addAll(nodesTosave);
		}*/
	}



	public void removeSelectedMansione() {
		for (TreeNode child : deMansioneSelectedNode.getChildren()) {
			child.setSelected(false);
		}
		deMansioneSelectedNode.setSelected(false);
		//deBpMansioneInfoList.clear();
		deMansioneSelectedNode = null;
		// buildInfoList();
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
	private void dfsTreeFilteringMansioni(TreeNode node, TreeNode padre, String filterString, List<TreeNode> savedTree,
			List<TreeNode> selectedList, int level) {
		DeBpMansione dataObj = (DeBpMansione) node.getData();
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
						dfsTreeFilteringMansioni(child, newNode, filterString, savedTree, selectedList, level - 1);
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
					dfsTreeFilteringMansioni(child, padre, filterString, savedTree, selectedList, level - 1);
			}

		}
	}

	// Esegue la comparazione della stringa di filtering sia con la descrizione
	// che con il codice mansione
	private boolean compareDescrizioneAndCodiceWithFilterString(DeBpMansione mansione, String filter) {
		if (mansione.getDescrizione().toLowerCase().contains(filter.toLowerCase()))
			return true;
		if (mansione.getCodMansioneDot().toLowerCase().contentEquals(filter.toLowerCase()))
			return true;
		return false;
	}

	// Indica se tra i figli vi è uno di essi con descrizione che contiene la
	// stringa di filtering
	private boolean IsfoglieContainsFilterString(List<TreeNode> children, String filterString) {
		for (TreeNode child : children) {
			DeBpMansione dataObj = (DeBpMansione) child.getData();
			if (compareDescrizioneAndCodiceWithFilterString(dataObj, filterString))
				return true;
		}
		return false;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/***
	 * SETTERS AND GETTERS
	 */

	public TreeNode getDeMansioneTreeNode() {
		return deMansioneTreeNode;
	}

	public void setDeMansioneTreeNode(TreeNode deMansioneTreeNode) {
		this.deMansioneTreeNode = deMansioneTreeNode;
	}

	public TreeNode getDeMansioneSelectedNode() {
		return deMansioneSelectedNode;
	}

	public void setDeMansioneSelectedNode(TreeNode deMansioneSelectedNode) {
		this.deMansioneSelectedNode = deMansioneSelectedNode;
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

	public TreeNode getDeMansioneTreeNodeAgricoli() {
		return deMansioneTreeNodeAgricoli;
	}

	public void setDeMansioneTreeNodeAgricoli(TreeNode deMansioneTreeNodeAgricoli) {
		this.deMansioneTreeNodeAgricoli = deMansioneTreeNodeAgricoli;
	}

}
