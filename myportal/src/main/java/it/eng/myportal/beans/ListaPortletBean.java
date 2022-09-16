package it.eng.myportal.beans;

import it.eng.myportal.dtos.PtScrivaniaDTO;
import it.eng.myportal.entity.enums.TipoAbilitazione;
import it.eng.myportal.entity.home.PtPortletHome;
import it.eng.myportal.entity.home.PtScrivaniaHome;
import it.eng.myportal.utils.ConstantsSingleton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * BackingBean della pagina delle classifiche
 * 
 * @author Enrico D'Angelo
 * 
 */
@ManagedBean
@ViewScoped
public class ListaPortletBean extends AbstractBaseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6562323734328324612L;

	@EJB
	private PtScrivaniaHome ptScrivaniaHome;

	@EJB
	private PtPortletHome ptPortletHome;

	/**
	 * Mantengo ancora una volta le portlet in due strutture dati differenti: la
	 * lista perchè è più comodo per il rendering della pagina e l'hasMap perchè
	 * è più comodo per l'aggiornamento. In questo modo riesco a rendere più
	 * performanti tutte le possibili operazioni.
	 */
	private Map<Integer, PtScrivaniaDTO> portlets;
	private List<PtScrivaniaDTO> portletsAttive;

	@PostConstruct
	protected void postConstruct() {   
		super.postConstruct();
		portlets = new HashMap<Integer, PtScrivaniaDTO>();
		// tutte le portlet a disposizione dell'utente
		portletsAttive = new ArrayList<PtScrivaniaDTO>();
		List<PtScrivaniaDTO> portletAll = ptScrivaniaHome.findPortletsScrivania(session.getPrincipalId());
		for (PtScrivaniaDTO ptScrivaniaDTO : portletAll) {
			// verifico l'abilitazione alla portlet
			if (isAbilitato(ptScrivaniaDTO.getNome(), TipoAbilitazione.VISIBILE)) {  			  	
				portlets.put(ptScrivaniaDTO.getIdPortlet(), ptScrivaniaDTO);
				portletsAttive.add(ptScrivaniaDTO);
			}  			
		}

	}

	/*
	 * public void refresh() { portletsAttive =
	 * ptScrivaniaHome.findPortletsScrivania(session.getPrincipalId()); }
	 * 
	 * public String getNomePortlet(Integer idPtPortlet) { PtPortletDTO portlet
	 * = ptPortletHome.findDTOById(idPtPortlet); return portlet.getNome(); }
	 * 
	 * 
	 * public String getDescrizionePortlet(Integer idPtPortlet) { PtPortletDTO
	 * portlet = ptPortletHome.findDTOById(idPtPortlet); return
	 * portlet.getDescrizione(); }
	 */
	public void visualizzaPortletScrivania(Integer idPortlet) {
		try {
			Date now = new Date();
			PtScrivaniaDTO scriv = portlets.get(idPortlet);

			scriv.setDtmMod(now);
			scriv.setRidotta(false);
			scriv.setVisualizza(true);

			Integer posizione = ptScrivaniaHome.checkMaxPosizione(session.getPrincipalId());
			// TODO da inserire l'ultima posizione possibile
			scriv.setPosizione(posizione);
			scriv.setColonna(ConstantsSingleton.PtScrivania.LEFT_COLUMN);
			scriv = homeMerge(ptScrivaniaHome, scriv);

			// forzo la ricostruzione della lista
			// postConstruct();

			addInfoMessage("data.updated");
			log.debug("Portlet " + idPortlet + " aggiunta alla scrivania.");
		} catch (EJBException e) {
			gestisciErrore(e, "error.updating");
		}
	}

	/*
	 * public void inserisciPortletScrivania(Integer idPortlet) { Date now = new
	 * Date(); data.setIdPrincipalIns(session.getPrincipalId());
	 * data.setIdPrincipalMod(session.getPrincipalId()); data.setDtmIns(now);
	 * data.setDtmMod(now); data.setIdPrincipal(session.getPrincipalId());
	 * data.setIdPortlet(idPortlet); data.setRidotta(false);
	 * data.setVisualizza(true); //TODO da inserire l'ultima posizione possibile
	 * data.setPosizione(new Integer(1));
	 * 
	 * data = homePersist(ptScrivaniaHome,data); }
	 */

	public List<PtScrivaniaDTO> getPortletsAttive() {
		return portletsAttive;
	}

	public void setPortletsAttive(List<PtScrivaniaDTO> portletsAttive) {
		this.portletsAttive = portletsAttive;
	}

}
