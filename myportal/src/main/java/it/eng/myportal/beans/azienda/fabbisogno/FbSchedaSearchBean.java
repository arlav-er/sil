package it.eng.myportal.beans.azienda.fabbisogno;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.DeMansioneMinDTO;
import it.eng.myportal.dtos.filter.FbSchedaFabbisognoFilterDTO;
import it.eng.myportal.entity.home.FbSchedaFabbisognoHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneMinHome;
import it.eng.myportal.helpers.LazyFbSchedaFabbisognoModel;

@ManagedBean(name = "fbSchedaSearchBean")
@ViewScoped
public class FbSchedaSearchBean extends AbstractBaseBean {

	@EJB
	FbSchedaFabbisognoHome fbSchedaFabbisognoHome;

	@EJB
	private DeMansioneMinHome deMansioneMinHome;

	private FbSchedaFabbisognoFilterDTO searchParams;
	private LazyFbSchedaFabbisognoModel fbSchedaList;

	private FacesContext context = FacesContext.getCurrentInstance();
	private HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
	private HttpSession httpSession = request.getSession(false);

	@PostConstruct
	protected void postConstruct() {
		// Solo gli enti accreditati accedono a questa pagina.
		if (!(session.isAzienda() && session.isEnteAccreditato())) {
			redirectHome();
		}

		searchParams = (FbSchedaFabbisognoFilterDTO) httpSession.getAttribute("searchParams");

		if (searchParams == null) {
			searchParams = new FbSchedaFabbisognoFilterDTO();
		} else {
			cerca();
		}
		searchParams.setEscludiInLavorazione(true);

	}

	/** Funzione autocomplete per la combo DeMansioneMin: tutti gli elementi, in base al codMansione scelto */
	public List<DeMansioneMinDTO> completeDeMansioneMin(String par) {
		List<DeMansioneMinDTO> deMansioneMinDTOList = new ArrayList<DeMansioneMinDTO>();
		if (searchParams.getDeMansione().getId() != null)
			deMansioneMinDTOList = deMansioneMinHome.findDTOBySuggestionAndCodMansione(par,
					searchParams.getDeMansione().getId());

		return deMansioneMinDTOList;

	}

	public void onMansioneSelected() {
		searchParams.setDeMansioneMin(null);
	}

	public void cerca() {
		fbSchedaList = new LazyFbSchedaFabbisognoModel(fbSchedaFabbisognoHome, searchParams);
		// httpSession.setAttribute("searchParams", searchParams);
	}

	public FbSchedaFabbisognoFilterDTO getSearchParams() {
		return searchParams;
	}

	public void setSearchParams(FbSchedaFabbisognoFilterDTO searchParams) {
		this.searchParams = searchParams;
	}

	public LazyFbSchedaFabbisognoModel getFbSchedaList() {
		return fbSchedaList;
	}

	public void setFbSchedaList(LazyFbSchedaFabbisognoModel fbSchedaList) {
		this.fbSchedaList = fbSchedaList;
	}
}
