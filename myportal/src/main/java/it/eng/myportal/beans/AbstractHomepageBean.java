package it.eng.myportal.beans;

import it.eng.myportal.dtos.PtScrivaniaDTO;
import it.eng.myportal.entity.enums.TipoAbilitazione;
import it.eng.myportal.entity.home.PtScrivaniaHome;
import it.eng.myportal.utils.ConstantsSingleton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;

public abstract class AbstractHomepageBean extends AbstractBaseBean {

	/**
	 * Elenco delle portlet della homepage.
	 * Mantiene tutte le portlet anche in una mappa per poterle recuperare
	 * velocemente in fase di aggiornamento.
	 */
	protected Map<Integer, PtScrivaniaDTO> portlets;
	protected SortedSet<PtScrivaniaDTO> leftColumn;
	protected SortedSet<PtScrivaniaDTO> rightColumn;
	
	@EJB
	private PtScrivaniaHome ptScrivaniaHome;
	
	public AbstractHomepageBean() {
		super();
	}
	
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		leftColumn = new TreeSet<PtScrivaniaDTO>();
		rightColumn = new TreeSet<PtScrivaniaDTO>();
		portlets = new HashMap<Integer, PtScrivaniaDTO>();
		getAllPortlets();
		
		if (log.isDebugEnabled()) {
			log.debug("Costruito il Bean per Home Page");
			log.debug("Portlet in colonna a sx:");
			for (PtScrivaniaDTO portlet : leftColumn) {
				log.debug(portlet.getNome()+".xhtml");
			}
			log.debug("Portlet in colonna a dx:");
			for (PtScrivaniaDTO portlet : rightColumn) {
				log.debug(portlet.getNome()+".xhtml");
			}
		}
	}

	protected void getAllPortlets() {
	
		// TODO le chiavi per le portlet andrebbero tra le costanti!
		List<PtScrivaniaDTO> scrivania = ptScrivaniaHome.findPortletsScrivania(session.getPrincipalId());
		
		if (scrivania.size() > 0) {
			for (PtScrivaniaDTO portlet : scrivania) {				
				// verifico l'abilitazione alla portlet
				if (isAbilitato(portlet.getNome(), TipoAbilitazione.VISIBILE)) {
					log.debug("Aggiungo portlet '" + portlet.getNome() + "'");				
					if (ConstantsSingleton.PtScrivania.LEFT_COLUMN.equals(portlet.getColonna())) {					
						leftColumn.add(portlet);
					}
					else {					
						rightColumn.add(portlet);
					}
					portlets.put(portlet.getIdPortlet(), portlet);
				}
			}
		}
	
	}

	public void deletePortlet() {
		Map<String, String> map = getRequestParameterMap();
		Integer deleteId = null;
		for (Entry<String, String> entry : map.entrySet()) {
			if (entry.getKey().endsWith("idPortlet")) {
				deleteId = Integer.parseInt(entry.getValue());
				break;
			}
		}
		if (deleteId != null) {
			PtScrivaniaDTO scrDTO = portlets.get(deleteId);
			scrDTO.setVisualizza(false);
			scrDTO = homeMerge(ptScrivaniaHome,scrDTO);
			//postConstruct();
		}
	}

	public void riduciPortlet() {
		Map<String, String> map = getRequestParameterMap();
		Integer riduciId = null;
		String flagRiduzione = null;
		for (Entry<String, String> entry : map.entrySet()) {
			if (entry.getKey().endsWith("idPortlet")) {
				riduciId = Integer.parseInt(entry.getValue());
			}
			if (entry.getKey().endsWith("flagRidotta")) {
				flagRiduzione = entry.getValue();
			}
		}
		if (riduciId != null) {
			PtScrivaniaDTO scrDTO = portlets.get(riduciId);
			scrDTO.setRidotta(!(new Boolean(flagRiduzione)));
			scrDTO = homeMerge(ptScrivaniaHome,scrDTO);
	
			//postConstruct();
		}
	}

	/**
	 * Riposiziona le portlet aggiornandone lo stato su DB
	 * Prende in input una stringa contenete le informazioni sulle portlet all'interno della scrivania.
	 * id=ID,pos=X,col=R|L ; id=ID,pos=X,col=R|L 
	 */
	public void posizionaPortlet() {
		try {
			String params = getRequestParameter("portlet_positioner:params");
			String[] portletsInfo = params.split(";");
			for (String portletInfos : portletsInfo) {
				Map<String,String> infoMap = new HashMap<String, String>(); 
				String[] infos = portletInfos.split(",");
				for (String info : infos) {
					String[] singleInfo = info.split("=");
					infoMap.put(singleInfo[0], singleInfo[1]);
				}
				Integer portletId = Integer.parseInt(infoMap.get("id"));
				Integer position = Integer.parseInt(infoMap.get("pos"));
				String column = infoMap.get("col");
				
				PtScrivaniaDTO portlet = portlets.get(portletId);
				//aggiornala solo se è cambiata la posizione o la colonna
				if (!position.equals(portlet.getPosizione()) ||
					!column.equals(portlet.getColonna())) {				
					portlet.setPosizione(position);
					portlet.setColonna(column);
					portlet = homeMerge(ptScrivaniaHome, portlet);
				}
			}
		} catch (EJBException e) {
			gestisciErrore(e, "errro.positioning");
		}
	}

	public SortedSet<PtScrivaniaDTO> getLeftColumn() {
		return leftColumn;
	}

	public void setLeftColumn(SortedSet<PtScrivaniaDTO> leftColumn) {
		this.leftColumn = leftColumn;
	}

	public SortedSet<PtScrivaniaDTO> getRightColumn() {
		return rightColumn;
	}

	public void setRightColumn(SortedSet<PtScrivaniaDTO> rightColumn) {
		this.rightColumn = rightColumn;
	}

	public Map<Integer, PtScrivaniaDTO> getPortlets() {
		return portlets;
	}

	public void setPortlets(Map<Integer, PtScrivaniaDTO> portlets) {
		this.portlets = portlets;
	}

}