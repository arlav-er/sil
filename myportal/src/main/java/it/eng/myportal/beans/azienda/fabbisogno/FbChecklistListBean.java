package it.eng.myportal.beans.azienda.fabbisogno;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.DeStatoFbChecklistDTO;
import it.eng.myportal.dtos.FbChecklistDTO;
import it.eng.myportal.dtos.filter.FbChecklistFilterDTO;
import it.eng.myportal.entity.home.FbChecklistHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoFbChecklistHome;
import it.eng.myportal.helpers.LazyFbChecklistModel;
import it.eng.myportal.utils.ConstantsSingleton;

@ManagedBean(name = "fbChecklistListBean")
@ViewScoped
public class FbChecklistListBean extends AbstractBaseBean {

	@EJB
	private FbChecklistHome fbChecklistHome;

	@EJB
	private DeStatoFbChecklistHome deStatoFbChecklistHome;

	private FbChecklistFilterDTO searchParams;
	private LazyFbChecklistModel checklists;
	private FbChecklistDTO eleminaChecklist;

	private FacesContext context = FacesContext.getCurrentInstance();
	private HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
	private HttpSession httpSession = request.getSession(false);

	@PostConstruct
	protected void postConstruct() {
		searchParams = (FbChecklistFilterDTO) httpSession.getAttribute("searchParams");

		if (searchParams == null) {
			searchParams = new FbChecklistFilterDTO();
		} else {
			cerca();
		}

		if (session.isEnteOspitanteCalabria()) {
			// Se sono un ente ospitante, carico subito tutte le mie checklist.
			searchParams.setIdPfPrincipal(session.getPrincipalId());
			cerca();
		} else if (session.isRegione()) {
			// Se sono una regione, escludo le checklist in lavorazione e aspetto una ricerca.
			searchParams.setEscludiInLavorazione(true);
		} else {
			// Se non sono nè azienda nè regione, non posso accedere.
			redirectHome();
		}
	}

	public boolean isEnteOspitante() {
		return session.isAzienda() && session.isEnteOspitanteCalabria();
	}

	/** Usato solo dagli utenti regione: cerca tra tutte le checklist */
	public void cerca() {
		checklists = new LazyFbChecklistModel(fbChecklistHome, searchParams, session.isRegione());
		httpSession.setAttribute("searchParams", searchParams);
	}

	/** Elimina una checklist e ricarica la lista */
	// public void eliminaChecklist(FbChecklistDTO checklist) {
	public void eliminaChecklist() {
		fbChecklistHome.removeById(eleminaChecklist.getId(), session.getPrincipalId());
		cerca();
	}

	public List<DeStatoFbChecklistDTO> getStatiRicercabiliList() {
		List<DeStatoFbChecklistDTO> stati = deStatoFbChecklistHome.findAllDTO();
		DeStatoFbChecklistDTO tempInLavorazione = new DeStatoFbChecklistDTO();
		tempInLavorazione.setId(ConstantsSingleton.DeStatoFbChecklist.IN_LAVORAZIONE);
		stati.remove(tempInLavorazione);
		return stati;
	}

	public FbChecklistFilterDTO getSearchParams() {
		return searchParams;
	}

	public void setSearchParams(FbChecklistFilterDTO searchParams) {
		this.searchParams = searchParams;
	}

	public LazyFbChecklistModel getChecklists() {
		return checklists;
	}

	public void setChecklists(LazyFbChecklistModel checklists) {
		this.checklists = checklists;
	}

	public FbChecklistDTO getEleminaChecklist() {
		return eleminaChecklist;
	}

	public void setEleminaChecklist(FbChecklistDTO eleminaChecklist) {
		this.eleminaChecklist = eleminaChecklist;
	}

}
