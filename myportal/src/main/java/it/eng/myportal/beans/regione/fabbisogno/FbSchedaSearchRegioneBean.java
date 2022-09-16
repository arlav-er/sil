package it.eng.myportal.beans.regione.fabbisogno;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.FbChecklistDTO;
import it.eng.myportal.dtos.FbSchedaFabbisognoDTO;
import it.eng.myportal.entity.home.FbChecklistHome;
import it.eng.myportal.entity.home.FbSchedaFabbisognoHome;

@ManagedBean(name = "fbSchedaSearchRegioneBean")
@ViewScoped
public class FbSchedaSearchRegioneBean extends AbstractBaseBean {

	@EJB
	private FbChecklistHome fbChecklistHome;

	@EJB
	private FbSchedaFabbisognoHome fbSchedaFabbisognoHome;

	private FbChecklistDTO checklist;
	private List<FbSchedaFabbisognoDTO> fbSchedafabbisognList;

	@PostConstruct
	public void init() {

		fbSchedafabbisognList = new ArrayList<FbSchedaFabbisognoDTO>();
		// Se mi è stato passato l'id di una checklist, la carico.
		if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.containsKey("fbChecklistId")) {
			try {
				String idFbChecklistParam = FacesContext.getCurrentInstance().getExternalContext()
						.getRequestParameterMap().get("fbChecklistId");
				Integer idFbChecklist = Integer.parseInt(idFbChecklistParam);
				checklist = fbChecklistHome.findDTOById(idFbChecklist);
				fbSchedafabbisognList = fbSchedaFabbisognoHome.findSchedaFabbisognByCheckListId(idFbChecklist);
			} catch (Exception e) {
				addErrorMessage("data.error_loading");
			}
		}
	}

	public FbChecklistDTO getChecklist() {
		return checklist;
	}

	public void setChecklist(FbChecklistDTO checklist) {
		this.checklist = checklist;
	}

	public List<FbSchedaFabbisognoDTO> getFbSchedafabbisognList() {
		return fbSchedafabbisognList;
	}

	public void setFbSchedafabbisognList(List<FbSchedaFabbisognoDTO> fbSchedafabbisognList) {
		this.fbSchedafabbisognList = fbSchedafabbisognList;
	}

}
