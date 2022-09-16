package it.eng.myportal.beans;

import java.util.*;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import it.eng.myportal.entity.decodifiche.*;
import it.eng.myportal.entity.enums.CodStatoVacancyEnum;
import it.eng.myportal.entity.home.decodifiche.*;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import it.eng.myportal.entity.decodifiche.sil.DeContrattoSil;
import it.eng.myportal.entity.decodifiche.sil.DeGradoLinSil;
import it.eng.myportal.entity.ejb.DeBpMansioneTreeEJB;
import it.eng.myportal.entity.ejb.DeTitoloTreeEJB;
import it.eng.myportal.entity.home.decodifiche.nodto.DeAreaTitoloHome;
import it.eng.myportal.entity.home.decodifiche.nodto.DeGruppoAreaTitoloHome;
import it.eng.myportal.utils.ConstantsSingleton;
/**
 * Nuova classe per l'autocomplete su primefaces
 * 
 * @author pegoraro
 *
 */
@ManagedBean
@ApplicationScoped
public class DecodificheBean {
	public static final int LIVELLO_FOGLIE = 4;

	public static final int LIVELLO_NIPOTI = 3;

	public static final int LIVELLO_FIGLI = 2;

	public static final int LIVELLO_PADRI = 1;

	protected static Log log = LogFactory.getLog(DecodificheBean.class);

	@EJB
	transient DeCpiHome deCpiHome;

	@EJB
	private DePatenteHome dePatenteHome;

	@EJB
	private DeProvenienzaHome deProvenienzaHome;

	@EJB
	private DeEvasioneRichHome evasioneRichHome;
	
	@EJB
	private DeBpMansioneHome deBpMansioneHome;

	@EJB
	DeBpMansioneTreeEJB deBpMansioneTreeEJB;

	@EJB
	DeTitoloTreeEJB deTitoloTreeEJB;

	@EJB
	DeTitoloHome deTitoloHome;

	@EJB
	private DeGruppoAreaTitoloHome deGruppoAreaTitoloHome;

	@EJB
	private DeAreaTitoloHome deAreaTitoloHome;

	@EJB
	private DeLinguaHome deLinguaHome;

	@EJB
	private DeGradoLinHome deGradoLinHome;

	@EJB
	private  DeGradoLinSilHome deGradoLinSilHome;

	@EJB
	private DeModalitaLinguaHome deModalitaLinguaHome;

	@EJB
	private DeContrattoHome deContrattoHome;

	@EJB
    private DeContrattoSilHome deContrattoSilHome;

	@EJB
	private DeTrasfertaHome deTrasfertaHome;
	@EJB
	private DeIdoneitaCandidaturaHome deIdoneitaCandidaturaHome;

	private Map<Integer, List<DeBpMansione>> deMansioneMap;
	private Map<Integer, List<DeTitolo>> deTitoloMap;

	private TreeNode deMansioneTreeNode;

	private TreeNode deTitoloTreeNode;
	
	private Map<Integer, List<DeBpMansione>> deMansioneMapAgricoli;
	private TreeNode deMansioneTreeNodeAgricoli;

	private CodStatoVacancyEnum statoVacancyEnum;
	
	@PostConstruct
	public void postConstruct() {
		deMansioneMap = deBpMansioneTreeEJB.getLevelsMap();
		deMansioneMapAgricoli = deBpMansioneTreeEJB.getLevelsMapAgricoli();
		deTitoloMap = deTitoloTreeEJB.getLevelsMap();
		deMansioneTreeNode = buildDeBpMansioneTreeNode();
		deMansioneTreeNodeAgricoli = buildDeBpMansioneTreeNodeAgricoli();
		deTitoloTreeNode = buildDeTitoloTreeNode();
	}

	public List<DeCpi> findCpiByCodRegione(String regione) {
		return deCpiHome.findByCodRegione(regione);
	}

	public List<DeTrasferta> findDeTrasfertaValidi() {
		return deTrasfertaHome.findDeTrasfertaValidi();
	}

	public List<DeCpi> findCpiByCurrentRegione() {
		return deCpiHome.findByCodRegione(String.valueOf(ConstantsSingleton.COD_REGIONE));
	}

	public List<DeCpi> findCpiActiveByCurrentRegione() {
		return deCpiHome.findActiveByCodRegione(String.valueOf(ConstantsSingleton.COD_REGIONE));
	}

	private TreeNode buildDeTitoloTreeNode() {
		TreeNode root = new DefaultTreeNode(new DeTitolo(), null);
		List<DeTitolo> deTitoloLivello;
		int sizePadri;
		int levelIterator;
		/*
		 * List<DeTitolo> radici = deTitoloHome.findPadriValidiAt(new Date());
		 * TreeNode[] treeNodePadriLevel1 = new TreeNode[radici.size()]; // Struttura
		 * usata per ritrovare velocemente il padre di un nodo Map<DeTitolo, TreeNode>
		 * dtoTreeNodeMapLevel1 = new HashMap<DeTitolo, TreeNode>();
		 * 
		 * for (DeTitolo padre : radici) { treeNodePadriLevel1[radici.indexOf(padre)] =
		 * new DefaultTreeNode(padre, root); dtoTreeNodeMapLevel1.put(padre,
		 * treeNodePadriLevel1[radici.indexOf(padre)]); }
		 * 
		 * for (TreeNode treeNode : treeNodePadriLevel1) { DeTitolo figlioL1 =
		 * (DeTitolo) treeNode.getData(); List<DeTitolo> figli1 =
		 * deTitoloHome.findByCodPadreNoDTO(figlioL1.getCodTitolo()); TreeNode[]
		 * treeNodePadriLevel2 = new TreeNode[figli1.size()]; for (DeTitolo deTitolo :
		 * figli1) { treeNodePadriLevel1[radici.indexOf(figlioL1)] = new
		 * DefaultTreeNode(figlioL1, dtoTreeNodeMapLevel1.get(deTitolo.getPadre())); } }
		 * 
		 * TreeNode[] treeNodePadriLevel2 = new TreeNode[radici.size()];
		 */

		// Costruisce l'albero per livelli
		try {
			// Livello 1
			deTitoloLivello = deTitoloMap.get(LIVELLO_PADRI);
			sizePadri = deTitoloLivello.size();
			// Livello dell'albero
			TreeNode[] treeNodePadriLevel1 = new TreeNode[sizePadri];
			// Struttura usata per ritrovare velocemente il padre di un nodo
			Map<DeTitolo, TreeNode> dtoTreeNodeMapLevel1 = new HashMap<DeTitolo, TreeNode>();
			levelIterator = 0;
			for (DeTitolo mansione : deTitoloLivello) {
				// Inserisce nel livello
				treeNodePadriLevel1[levelIterator] = new DefaultTreeNode(mansione, root);
				// Inserisce nella struttura per il ritrovamento veloce del padre
				dtoTreeNodeMapLevel1.put(mansione, treeNodePadriLevel1[levelIterator]);
				treeNodePadriLevel1[levelIterator].setSelectable(true);
				/*
				 * List<DeTitolo> tits =
				 * deTitoloHome.findDeTitoloFigliById(mansione.getCodTitolo()); if
				 * (!tits.isEmpty()) { treeNodePadriLevel1[levelIterator].setSelectable(false);
				 * } else { treeNodePadriLevel1[levelIterator].setSelectable(true); }
				 */
				levelIterator++;
			}

			// Livello 2
			deTitoloLivello = deTitoloMap.get(LIVELLO_FIGLI);
			sizePadri = deTitoloLivello.size();
			TreeNode[] treeNodePadriLevel2 = new TreeNode[sizePadri];
			Map<DeTitolo, TreeNode> dtoTreeNodeMapLevel2 = new HashMap<DeTitolo, TreeNode>();
			levelIterator = 0;
			for (DeTitolo mansione : deTitoloLivello) {
				treeNodePadriLevel2[levelIterator] = new DefaultTreeNode(mansione,
						dtoTreeNodeMapLevel1.get(mansione.getPadre()));
				dtoTreeNodeMapLevel2.put(mansione, treeNodePadriLevel2[levelIterator]);
				treeNodePadriLevel2[levelIterator].setSelectable(true);
				levelIterator++;
			}

			// livello 3
			deTitoloLivello = deTitoloMap.get(LIVELLO_NIPOTI);
			sizePadri = deTitoloLivello.size();
			TreeNode[] treeNodePadriLevel3 = new TreeNode[sizePadri];
			Map<DeTitolo, TreeNode> dtoTreeNodeMapLevel3 = new HashMap<DeTitolo, TreeNode>();
			levelIterator = 0;
			for (DeTitolo mansione : deTitoloLivello) {
				treeNodePadriLevel3[levelIterator] = new DefaultTreeNode(mansione,
						dtoTreeNodeMapLevel2.get(mansione.getPadre()));
				dtoTreeNodeMapLevel3.put(mansione, treeNodePadriLevel3[levelIterator]);
				treeNodePadriLevel3[levelIterator].setSelectable(true);
				levelIterator++;
			}

			// Livello 4, non usato se l'albero e` stato potato
			deTitoloLivello = deTitoloMap.get(LIVELLO_FOGLIE);
			sizePadri = deTitoloLivello.size();
			TreeNode[] treeNodePadriLevel4 = new TreeNode[sizePadri];
			Map<DeBpMansione, TreeNode> dtoTreeNodeMapLevel4 = new HashMap<DeBpMansione, TreeNode>();
			levelIterator = 0;
			for (DeTitolo mansione : deTitoloLivello) {
				treeNodePadriLevel4[levelIterator] = new DefaultTreeNode(mansione,
						dtoTreeNodeMapLevel3.get(mansione.getPadre()));
				treeNodePadriLevel4[levelIterator].setSelectable(true);
				// treeNodePadriLevel4.put(mansione, treeNodePadriLevel4[levelIterator]);
				levelIterator++;
			}
		} catch (Exception e) {
			log.error("Errore durnte la costruzione dell'albero dei titoli di studio");
		}

		return root;
	}
	
	public Date getCurrentDate() {
		return new Date();
	}

	
	public static Date addDaysToDate(Date initialDate, Integer days) {
		Date date = DateUtils.addDays(initialDate, days);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		calendar.set(Calendar.HOUR, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);

		return calendar.getTime();
	}


	public List<DeBpMansione> completeDeBpMansioneLevel4Only(String suggest){
		ArrayList<DeBpMansione> ret = new ArrayList<DeBpMansione>();
		List<DeBpMansione> tmp =  deBpMansioneHome.findBySuggestionNoDTO(suggest);
		for (DeBpMansione deBpMansione : tmp) {
			if (deMansioneMap.get(LIVELLO_FOGLIE).contains(deBpMansione))
				ret.add(deBpMansione);
		}
		return ret;
	}

	// Crea l'albero utilizzato a frontend partendo dalla mappa delle liste
	private TreeNode buildDeBpMansioneTreeNode() {
		TreeNode root = new DefaultTreeNode(new DeBpMansione(), null);
		List<DeBpMansione> deBpMansioniLivello;
		int sizePadri;
		int levelIterator;
		// Costruisce l'albero per livelli
		try {
			// Livello 1
			deBpMansioniLivello = deMansioneMap.get(LIVELLO_PADRI);
			sizePadri = deBpMansioniLivello.size();
			// Livello dell'albero
			TreeNode[] treeNodePadriLevel1 = new TreeNode[sizePadri];
			// Struttura usata per ritrovare velocemente il padre di un nodo
			Map<DeBpMansione, TreeNode> dtoTreeNodeMapLevel1 = new HashMap<DeBpMansione, TreeNode>();
			levelIterator = 0;
			for (DeBpMansione mansione : deBpMansioniLivello) {
				// Inserisce nel livello
				treeNodePadriLevel1[levelIterator] = new DefaultTreeNode(mansione, root);
				// Inserisce nella struttura per il ritrovamento veloce del padre
				dtoTreeNodeMapLevel1.put(mansione, treeNodePadriLevel1[levelIterator]);
				treeNodePadriLevel1[levelIterator].setSelectable(false);
				levelIterator++;
			}

			// Livello 2
			deBpMansioniLivello = deMansioneMap.get(LIVELLO_FIGLI);
			sizePadri = deBpMansioniLivello.size();
			TreeNode[] treeNodePadriLevel2 = new TreeNode[sizePadri];
			Map<DeBpMansione, TreeNode> dtoTreeNodeMapLevel2 = new HashMap<DeBpMansione, TreeNode>();
			levelIterator = 0;
			for (DeBpMansione mansione : deBpMansioniLivello) {
				treeNodePadriLevel2[levelIterator] = new DefaultTreeNode(mansione,
						dtoTreeNodeMapLevel1.get(mansione.getPadre()));
				dtoTreeNodeMapLevel2.put(mansione, treeNodePadriLevel2[levelIterator]);
				treeNodePadriLevel2[levelIterator].setSelectable(false);
				levelIterator++;
			}

			// livello 3
			deBpMansioniLivello = deMansioneMap.get(LIVELLO_NIPOTI);
			sizePadri = deBpMansioniLivello.size();
			TreeNode[] treeNodePadriLevel3 = new TreeNode[sizePadri];
			Map<DeBpMansione, TreeNode> dtoTreeNodeMapLevel3 = new HashMap<DeBpMansione, TreeNode>();
			levelIterator = 0;
			for (DeBpMansione mansione : deBpMansioniLivello) {
				treeNodePadriLevel3[levelIterator] = new DefaultTreeNode(mansione,
						dtoTreeNodeMapLevel2.get(mansione.getPadre()));
				dtoTreeNodeMapLevel3.put(mansione, treeNodePadriLevel3[levelIterator]);
				treeNodePadriLevel3[levelIterator].setSelectable(false);
				levelIterator++;
			}

			// Livello 4
			deBpMansioniLivello = deMansioneMap.get(LIVELLO_FOGLIE);
			sizePadri = deBpMansioniLivello.size();
			TreeNode[] treeNodePadriLevel4 = new TreeNode[sizePadri];
			Map<DeBpMansione, TreeNode> dtoTreeNodeMapLevel4 = new HashMap<DeBpMansione, TreeNode>();
			levelIterator = 0;
			for (DeBpMansione mansione : deBpMansioniLivello) {
				treeNodePadriLevel4[levelIterator] = new DefaultTreeNode(mansione,
						dtoTreeNodeMapLevel3.get(mansione.getPadre()));
				treeNodePadriLevel4[levelIterator].setSelectable(true);
				dtoTreeNodeMapLevel4.put(mansione, treeNodePadriLevel4[levelIterator]);
				levelIterator++;
			}

			// livello 5
			deBpMansioniLivello = deMansioneMap.get(5);
			sizePadri = deBpMansioniLivello.size();
			TreeNode[] treeNodePadriLevel5 = new TreeNode[sizePadri];
			Map<DeBpMansione, TreeNode> dtoTreeNodeMapLevel5 = new HashMap<DeBpMansione, TreeNode>();
			levelIterator = 0;
			for (DeBpMansione mansione : deBpMansioniLivello) {
				treeNodePadriLevel5[levelIterator] = new DefaultTreeNode(mansione,
						dtoTreeNodeMapLevel4.get(mansione.getPadre()));
				treeNodePadriLevel5[levelIterator].setSelectable(false);
				dtoTreeNodeMapLevel5.put(mansione, treeNodePadriLevel5[levelIterator]);
				levelIterator++;
			}

			// Livello 6
			// deBpMansioniLivello = deMansioneMap.get(6);
			// sizePadri = deBpMansioniLivello.size();
			// TreeNode[] treeNodePadriLevel6 = new TreeNode[sizePadri];
			// levelIterator = 0;
			// for (DeBpMansione mansione : deBpMansioniLivello) {
			// treeNodePadriLevel6[levelIterator] = new DefaultTreeNode(mansione,
			// dtoTreeNodeMapLevel5.get(mansione
			// .getPadre()));
			// treeNodePadriLevel6[levelIterator].setSelectable(false);
			// levelIterator++;
			// }
		} catch (Exception e) {
			// TODO:Error
		}

		return root;
	}

	// Crea l'albero utilizzato a frontend partendo dalla mappa delle liste
		private TreeNode buildDeBpMansioneTreeNodeAgricoli() {
			TreeNode root = new DefaultTreeNode(new DeBpMansione(), null);
			List<DeBpMansione> deBpMansioniLivello;
			int sizePadri;
			int levelIterator;
			// Costruisce l'albero per livelli
			try {
				// Livello 1
				deBpMansioniLivello = deMansioneMapAgricoli.get(LIVELLO_PADRI);
				sizePadri = deBpMansioniLivello.size();
				// Livello dell'albero
				TreeNode[] treeNodePadriLevel1 = new TreeNode[sizePadri];
				// Struttura usata per ritrovare velocemente il padre di un nodo
				Map<DeBpMansione, TreeNode> dtoTreeNodeMapLevel1 = new HashMap<DeBpMansione, TreeNode>();
				levelIterator = 0;
				for (DeBpMansione mansione : deBpMansioniLivello) {
					// Inserisce nel livello
					treeNodePadriLevel1[levelIterator] = new DefaultTreeNode(mansione, root);
					// Inserisce nella struttura per il ritrovamento veloce del padre
					dtoTreeNodeMapLevel1.put(mansione, treeNodePadriLevel1[levelIterator]);
					treeNodePadriLevel1[levelIterator].setSelectable(false);
					levelIterator++;
				}

				// Livello 2
				deBpMansioniLivello = deMansioneMapAgricoli.get(LIVELLO_FIGLI);
				sizePadri = deBpMansioniLivello.size();
				TreeNode[] treeNodePadriLevel2 = new TreeNode[sizePadri];
				Map<DeBpMansione, TreeNode> dtoTreeNodeMapLevel2 = new HashMap<DeBpMansione, TreeNode>();
				levelIterator = 0;
				for (DeBpMansione mansione : deBpMansioniLivello) {
					treeNodePadriLevel2[levelIterator] = new DefaultTreeNode(mansione,
							dtoTreeNodeMapLevel1.get(mansione.getPadre()));
					dtoTreeNodeMapLevel2.put(mansione, treeNodePadriLevel2[levelIterator]);
					treeNodePadriLevel2[levelIterator].setSelectable(false);
					levelIterator++;
				}

				// livello 3
				deBpMansioniLivello = deMansioneMapAgricoli.get(LIVELLO_NIPOTI);
				sizePadri = deBpMansioniLivello.size();
				TreeNode[] treeNodePadriLevel3 = new TreeNode[sizePadri];
				Map<DeBpMansione, TreeNode> dtoTreeNodeMapLevel3 = new HashMap<DeBpMansione, TreeNode>();
				levelIterator = 0;
				for (DeBpMansione mansione : deBpMansioniLivello) {
					treeNodePadriLevel3[levelIterator] = new DefaultTreeNode(mansione,
							dtoTreeNodeMapLevel2.get(mansione.getPadre()));
					dtoTreeNodeMapLevel3.put(mansione, treeNodePadriLevel3[levelIterator]);
					treeNodePadriLevel3[levelIterator].setSelectable(false);
					levelIterator++;
				}

				// Livello 4
				deBpMansioniLivello = deMansioneMapAgricoli.get(LIVELLO_FOGLIE);
				sizePadri = deBpMansioniLivello.size();
				TreeNode[] treeNodePadriLevel4 = new TreeNode[sizePadri];
				Map<DeBpMansione, TreeNode> dtoTreeNodeMapLevel4 = new HashMap<DeBpMansione, TreeNode>();
				levelIterator = 0;
				for (DeBpMansione mansione : deBpMansioniLivello) {
					treeNodePadriLevel4[levelIterator] = new DefaultTreeNode(mansione,
							dtoTreeNodeMapLevel3.get(mansione.getPadre()));
					treeNodePadriLevel4[levelIterator].setSelectable(true);
					dtoTreeNodeMapLevel4.put(mansione, treeNodePadriLevel4[levelIterator]);
					levelIterator++;
				}

				// livello 5
				deBpMansioniLivello = deMansioneMapAgricoli.get(5);
				sizePadri = deBpMansioniLivello.size();
				TreeNode[] treeNodePadriLevel5 = new TreeNode[sizePadri];
				Map<DeBpMansione, TreeNode> dtoTreeNodeMapLevel5 = new HashMap<DeBpMansione, TreeNode>();
				levelIterator = 0;
				for (DeBpMansione mansione : deBpMansioniLivello) {
					treeNodePadriLevel5[levelIterator] = new DefaultTreeNode(mansione,
							dtoTreeNodeMapLevel4.get(mansione.getPadre()));
					treeNodePadriLevel5[levelIterator].setSelectable(false);
					dtoTreeNodeMapLevel5.put(mansione, treeNodePadriLevel5[levelIterator]);
					levelIterator++;
				}
			} catch (Exception e) {
				// TODO:Error
			}

			return root;
		}

	
	public List<SelectItem> findGroupedTipoAreaTitoli() throws Exception {

		List<SelectItem> ret = new ArrayList<SelectItem>();
		List<DeGruppoAreaTitolo> fathers = deGruppoAreaTitoloHome.findAll();
		for (DeGruppoAreaTitolo deGruppoAreaTitolo : fathers) {
			SelectItemGroup g1 = new SelectItemGroup(deGruppoAreaTitolo.getDescrizione());
			List<DeAreaTitolo> figli = deAreaTitoloHome.findByPadre(deGruppoAreaTitolo);
			if (figli.isEmpty())
				continue;
			ArrayList<SelectItem> ff = new ArrayList<SelectItem>();
			for (DeAreaTitolo f : figli) {
				// ff.add(new SelectItem(f,
				// f.getCodAreaTitolo() + ") " + f.getDescrizione()));
				ff.add(new SelectItem(f, f.getDescrizione()));
			}
			g1.setSelectItems(ff.toArray(new SelectItem[0]));
			ret.add(g1);
		}
		return ret;
	}

	public TreeNode getDeMansioneTreeNode() {
		return deMansioneTreeNode;
	}

	public void setDeMansioneTreeNode(TreeNode deMansioneTreeNode) {
		this.deMansioneTreeNode = deMansioneTreeNode;
	}

	public TreeNode getDeTitoloTreeNode() {
		return deTitoloTreeNode;
	}

	public void setDeTitoloTreeNode(TreeNode deTitoloTreeNode) {
		this.deTitoloTreeNode = deTitoloTreeNode;
	}

	public List<DeLingua> getLingue() {
		return deLinguaHome.getAll();

	}

	public List<DeGradoLin> getGradoLingue() {
		return deGradoLinHome.findAll();
	}

	public List<DeGradoLinSil> getGradoLingueSil() {
		return deGradoLinSilHome.findAll();
	}
	
	public List<DeGradoLinSil> getGradoLingueSilByCodIdoNotNull() {
		return deGradoLinSilHome.findByCodIdoNotNull();
	}

	public List<DeModalitaLingua> getDeModalitaLingua() {
		return deModalitaLinguaHome.findAll();
	}

	public List<DeContratto> getTipologiaContratto() {
		return deContrattoHome.findAll();
	}

    public List<DeContrattoSil> getTipologiaContrattoSil() {
        return deContrattoSilHome.findAll();
    }
    
    public List<DeContrattoSil> getTipologiaContrattoSilByFlagIdo(boolean flagIdo) {
		return deContrattoSilHome.findValideByFlagIdo(flagIdo);
  }

  public List<DeContrattoSil> getTipolgoiaContrattoSilFlagIdoByTirocinio(boolean flagIdo){
	 List <DeContrattoSil>  deContrattoSilAll =  deContrattoSilHome.findAll();
	  List <DeContrattoSil> deContrattoSilIdo = deContrattoSilHome.findValideByFlagIdo(flagIdo);
	  	  for (DeContrattoSil list:deContrattoSilAll
		   ) {
	  	if(list.getCodContrattoSil().contains("TI")){
	  		deContrattoSilIdo.add(list);
	  		break;
		}
	  }
	return 	deContrattoSilIdo;
  }

	public List<DeIdoneitaCandidatura> getLivelloCandidatura() {
		return deIdoneitaCandidaturaHome.findAll();
	}

	public List<DeProvenienza> getProvenienza() {
		return deProvenienzaHome.findAll();
	}

	public List<DeEvasioneRich> getEvasione() {
		return evasioneRichHome.findAll();
	}


	public TreeNode getDeMansioneTreeNodeAgricoli() {
		return deMansioneTreeNodeAgricoli;
	}

	public void setDeMansioneTreeNodeAgricoli(TreeNode deMansioneTreeNodeAgricoli) {
		this.deMansioneTreeNodeAgricoli = deMansioneTreeNodeAgricoli;
	}

}
