package it.eng.myportal.beans.session;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import it.eng.myportal.entity.decodifiche.DeBpMansione;
import it.eng.myportal.entity.decodifiche.IDecodificaDescEntity;

public class GenericTreeNodeVisit {

	protected static final Log log = LogFactory.getLog(GenericTreeNodeVisit.class);

	/**
	 * 
	 * @param node
	 *            : nodo esaminato
	 * @param padre
	 *            : padre del nodo esaminato
	 * @param filterString
	 *            : stringa su cui viene effettuato il filtraggio
	 * @param savedTree
	 *            : contiene le informazioni dei nodi, utilizzato per copiare
	 *            queste informazioni su questo nuovo albero
	 * @param selectedList
	 *            : lista dei nodi selezionati
	 * @param level
	 *            : livello dell'alberatura
	 * @param filterng
	 *            : indica se attivare o meno il filtraggio. Utilizzato anche
	 *            per definire se espandere o meno i rami. Necessario inserirlo
	 *            poichè funzione richiamata anche quando stringa di filtraggio
	 *            viene svuotata
	 */
	public static <T extends IDecodificaDescEntity> void dfsTreeFiltering(TreeNode node, TreeNode padre,
			String filterString, List<TreeNode> savedTree, List<TreeNode> selectedList, int level) {
		T dataObj = (T) node.getData(); // Se nodo foglia filtro per descrizione
		TreeNode newNode = new DefaultTreeNode(dataObj, null);

		if (level == 0) { // Ultimo livello che voglio esaminare
			if (dataObj.getDescrizione().toLowerCase().contains(filterString.toLowerCase())
					|| IsfoglieContainsFilterString(node.getChildren(), filterString)) {
				// Aggiungo all'albero filtrato e i figli nel caso la stringa
				// ricercata sia parte della descrizione del nodo o di uno dei
				// suoi figli
				newNode.setParent(padre);
				newNode.setExpanded(true);
				padre.getChildren().add(newNode);
				setNodeOption(newNode, savedTree, selectedList, node.isSelectable());
				// Ultimo livello aggiunto
				for (TreeNode child : node.getChildren()) {
					TreeNode childNode = new DefaultTreeNode(child.getData(), newNode);
					setNodeOption(childNode, savedTree, selectedList, child.isSelectable());
				}
			}
		} else {
			// Se radice dell'albero la rimuovo a favore della nuova radice
			// (ramo else)
			if (dataObj.getDescrizione() != null) {
				// Filtro i figli
				if (dataObj.getDescrizione().toLowerCase().contains(filterString.toLowerCase())) {
					drawDown(node, newNode, savedTree, selectedList);
					newNode.setExpanded(true);
					newNode.setParent(padre);
					padre.getChildren().add(newNode);
					GenericTreeNodeVisit.setNodeOption(newNode, savedTree, selectedList, node.isSelectable());
				} else {
					for (TreeNode child : node.getChildren())
						dfsTreeFiltering(child, newNode, filterString, savedTree, selectedList, level - 1);
					// Se ho figli filtrati aggiungo all'albero anche questo
					// nodo O Se non ho figli filtro per descrizione
					if (newNode.getChildCount() > 0
							|| dataObj.getDescrizione().toLowerCase().contains(filterString.toLowerCase())) {
						newNode.setParent(padre);
						newNode.setExpanded(true);
						padre.getChildren().add(newNode);
						setNodeOption(newNode, savedTree, selectedList, node.isSelectable());
					} else {
						newNode.setExpanded(false);
					}
				}
			} else {
				// Filtro i figli, uso padre al posto di node come secondo
				// argomento per correggere l'aggiunta di un livello di troppo
				// tra la
				// radice e gli elementi durante la creazione dell'albero
				for (TreeNode child : node.getChildren())
					dfsTreeFiltering(child, padre, filterString, savedTree, selectedList, level - 1);
			}

		}
	}

	// Indica se tra i figli vi è uno di essi con descrizione che contiene la
	// stringa di filtering
	public static <T extends IDecodificaDescEntity> boolean IsfoglieContainsFilterString(
			List<TreeNode> children, String filterString) {
		for (TreeNode child : children) {
			DeBpMansione dataObj = (DeBpMansione) child.getData();
			if (dataObj.getDescrizione().toLowerCase().contains(filterString.toLowerCase()))
				return true;
		}
		return false;
	}

	// Gestisce le opzioni relative al nodo aggiunto all'albero filtrato
	public static void setNodeOption(TreeNode node, List<TreeNode> savedTree, List<TreeNode> selectedList,
			boolean selectable) {
		node.setSelectable(selectable);
		if (savedTree.contains(node)) {
			if (savedTree.get(savedTree.indexOf(node)).isSelected()) {
				node.setSelected(true);
				expandPredecessor(node);
				if (selectedList != null && node.isSelected())
					selectedList.add(node);
			}
		}
	}

	private static void expandPredecessor(TreeNode node) {
		while (node != null) {
			node.setExpanded(true);
			node = node.getParent();
		}
	}

	/**
	 * Aggiunge solo le informazioni sui nodi senza filtrare l'albero. Ricorsiva
	 * 
	 * @param node
	 * @param savedTree
	 * @param selectedList
	 */
	public static void dsfTree(TreeNode node, List<TreeNode> savedTree, List<TreeNode> selectedList) {
		GenericTreeNodeVisit.setNodeOption(node, savedTree, selectedList, node.isSelectable());

		for (TreeNode child : node.getChildren()) {
			dsfTree(child, savedTree, selectedList);
		}
	}

	/**
	 * Save in array the relevant nodes where user manipulated expansion and/or
	 * selection
	 * 
	 * @param deMansioneTreeNode2
	 * @return
	 */
	public static List<TreeNode> saveTreeSettings(TreeNode deMansioneTreeNode2) {
		List<TreeNode> saved = new ArrayList<>();
		// dalle foglie alla radice
		for (TreeNode child : deMansioneTreeNode2.getChildren()) {
			saved.addAll(saveTreeSettings(child));
		}
		if (deMansioneTreeNode2.isSelected()) {
			saved.add(deMansioneTreeNode2);
		}

		// if (deMansioneTreeNode2.isExpanded())
		// saved.add(deMansioneTreeNode2);

		return saved;
	}

	// Aggiunge tutti i nodi presenti nel sottoalbero con radice nel nodo
	// indicato
	public static void drawDown(TreeNode node, TreeNode padre, List<TreeNode> savedTree, List<TreeNode> selectedList) {
		for (TreeNode child : node.getChildren()) {
			TreeNode childNode = new DefaultTreeNode(child.getData(), padre);
			GenericTreeNodeVisit.setNodeOption(childNode, savedTree, selectedList, child.isSelectable());
			if (!child.isLeaf())
				drawDown(child, childNode, savedTree, selectedList);
		}
	}

	public static TreeNode findMansioneInTree(TreeNode root, String startsWith) {
		DeBpMansione mansione = (DeBpMansione) root.getData();
		// TODO valutare equals(), in teoria migliore
		if (mansione.getCodMansione() != null && mansione.getCodMansione().startsWith(startsWith))
			return root;
		List<TreeNode> children = root.getChildren();
		TreeNode res = null;
		for (int i = 0; res == null && i < root.getChildCount(); i++) {
			res = findMansioneInTree(children.get(i), startsWith);
		}
		return res;

	}
}
