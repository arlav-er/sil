package it.eng.myportal.beans.azienda.fabbisogno;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.FbChecklistDTO;
import it.eng.myportal.dtos.FbSchedaFabbisognoDTO;
import it.eng.myportal.entity.home.FbChecklistHome;
import it.eng.myportal.entity.home.FbDatiAziendaHome;
import it.eng.myportal.entity.home.FbSchedaFabbisognoHome;

@ManagedBean(name = "fbSchedaListBean")
@ViewScoped
public class FbSchedaListBean extends AbstractBaseBean {

	@EJB
	FbChecklistHome fbChecklistHome;

	@EJB
	private FbSchedaFabbisognoHome fbSchedaFabbisognoHome;

	@EJB
	private FbDatiAziendaHome fbDatiAziendaHome;

	private FbChecklistDTO checklist;
	private FbSchedaFabbisognoDTO eliminateFbSchedaFabbisognoDTO;
	private boolean showEliminaSchedaError;

	@PostConstruct
	protected void postConstruct() {
		// Solo gli enti ospitanti accedono a questa pagina.
		if (!(session.isAzienda() && session.isEnteOspitanteCalabria())) {
			redirectHome();
		}

		// Carico la checklist a cui agganciare le schede fabbisogno.
		if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.containsKey("idFbChecklist")) {
			try {
				String idFbChecklistParam = FacesContext.getCurrentInstance().getExternalContext()
						.getRequestParameterMap().get("idFbChecklist");
				Integer idFbChecklist = Integer.parseInt(idFbChecklistParam);
				setChecklist(fbChecklistHome.findDTOById(idFbChecklist));
			} catch (Exception e) {
				addErrorMessage("data.error_loading");
				setChecklist(new FbChecklistDTO());
			}
		}

		// Se non sono il proprietario della checklist, vengo cacciato.
		if (!session.getPrincipalId().equals(checklist.getIdPfPrincipal())) {
			redirectHome();
		}
	}

	// Delete Scheda from the list of FbSchedaFabbisognoDTO
	public void eliminaSchedaFabbisogno() {
		// 1-to-1 foriegn key R/P in FbDataAzienda Table
		// fbDatiAziendaHome.removeById(eliminateFbSchedaFabbisognoDTO.getId(), session.getPrincipalId());
		try {
			showEliminaSchedaError = false;
			fbDatiAziendaHome.removeByFbSchedaFabbisognoId(eliminateFbSchedaFabbisognoDTO.getId(),
					session.getPrincipalId());
			// Delete from FbSchedaFabbisogno table
			fbSchedaFabbisognoHome.removeById(eliminateFbSchedaFabbisognoDTO.getId(), session.getPrincipalId());

			// delete from the list
			checklist.getFbSchedaFabbisognoList().remove(eliminateFbSchedaFabbisognoDTO);
		} catch (Exception e) {
			showEliminaSchedaError = true;
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Scheda... ", "Fbmmm "));
		}

	}

	public FbChecklistDTO getChecklist() {
		return checklist;
	}

	public void setChecklist(FbChecklistDTO checklist) {
		this.checklist = checklist;
	}

	public FbSchedaFabbisognoDTO getEliminateFbSchedaFabbisognoDTO() {
		return eliminateFbSchedaFabbisognoDTO;
	}

	public void setEliminateFbSchedaFabbisognoDTO(FbSchedaFabbisognoDTO eliminateFbSchedaFabbisognoDTO) {
		this.eliminateFbSchedaFabbisognoDTO = eliminateFbSchedaFabbisognoDTO;
	}

	public boolean isShowEliminaSchedaError() {
		return showEliminaSchedaError;
	}

	public void setShowEliminaSchedaError(boolean showEliminaSchedaError) {
		this.showEliminaSchedaError = showEliminaSchedaError;
	}

}
