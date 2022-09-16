package it.eng.myportal.beans.azienda.fabbisogno;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.FbChecklistDTO;
import it.eng.myportal.entity.home.FbChecklistHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoFbChecklistHome;
import it.eng.myportal.utils.ConstantsSingleton;

@ManagedBean(name = "fbChecklistFormBean")
@ViewScoped
public class FbChecklistFormBean extends AbstractBaseBean {

	@EJB
	FbChecklistHome fbChecklistHome;

	@EJB
	DeStatoFbChecklistHome deStatoFbChecklistHome;

	private FbChecklistDTO checklist;
	private boolean showDialog;

	@PostConstruct
	protected void postConstruct() {
		// Solo enti ospitanti e regioni possono passare.
		if (!(isEnteOspitante() || session.isRegione())) {
			redirectHome();
		}

		// Se mi è stato passato l'id di una checklist, la carico.
		if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.containsKey("idFbChecklist")) {
			try {
				String idFbChecklistParam = FacesContext.getCurrentInstance().getExternalContext()
						.getRequestParameterMap().get("idFbChecklist");
				Integer idFbChecklist = Integer.parseInt(idFbChecklistParam);
				checklist = fbChecklistHome.findDTOById(idFbChecklist);
			} catch (Exception e) {
				addErrorMessage("data.error_loading");
			}
		}

		// Se sono un ente ospitante e sto modificando una checklist, DEVO esserne il creatore o vengo cacciato.
		if (isEnteOspitante() && checklist != null) {
			if (!session.getPrincipalId().equals(checklist.getIdPfPrincipal())) {
				redirectHome();
			}
		}

		// Se sono un ente ospitante e non sto modificando una checklist, ne creo una nuova, stato 'LAV'.
		if (isEnteOspitante() && checklist == null) {
			checklist = new FbChecklistDTO();
			checklist.setIdPfPrincipal(session.getPrincipalId());
			checklist.setDeStatoFbChecklist(
					deStatoFbChecklistHome.findDTOById(ConstantsSingleton.DeStatoFbChecklist.IN_LAVORAZIONE));
		}
	}

	public boolean isEnteOspitante() {
		return session.isAzienda() && session.isEnteOspitanteCalabria();
	}

	/** Solo l'ente ospitante può salvare una checklist, e solo se è in lavorazione */
	public boolean isSaveAvailable() {
		return (isEnteOspitante() && ConstantsSingleton.DeStatoFbChecklist.IN_LAVORAZIONE
				.equals(checklist.getDeStatoFbChecklist().getId()));
	}

	/** Solo la regione può validare una checklist, e solo se è in stato 'pubblicata' */
	public boolean isValidaAvailable() {
		return (session.isRegione()
				&& ConstantsSingleton.DeStatoFbChecklist.PUBBLICATA.equals(checklist.getDeStatoFbChecklist().getId()));
	}

	public boolean isRevocable() {
		return (session.isRegione()
				&& !ConstantsSingleton.DeStatoFbChecklist.REVOCATA.equals(checklist.getDeStatoFbChecklist().getId()));
	}

	/** Salva la checklist visualizzata */
	public boolean save() {
		if (checklist.getId() == null) {
			checklist = fbChecklistHome.persistDTO(checklist, session.getPrincipalId());
		} else {
			checklist = fbChecklistHome.mergeDTO(checklist, session.getPrincipalId());
		}
		if (checklist != null) {
			RequestContext.getCurrentInstance().addCallbackParam("success", true);
			return true;
		} else
			return false;

	}

	/** Pubblica la checklist visualizzata (cambia stato e salva) */
	public void pubblica() {

		checklist.setDeStatoFbChecklist(
				deStatoFbChecklistHome.findDTOById(ConstantsSingleton.DeStatoFbChecklist.PUBBLICATA));
		checklist.setDtPubblicazione(new Date());
		save();
	}

	/**
	 * Revoca la checklist
	 */
	public void revoca() {
		checklist.setDeStatoFbChecklist(
				deStatoFbChecklistHome.findDTOById(ConstantsSingleton.DeStatoFbChecklist.REVOCATA));
		save();
		RequestContext.getCurrentInstance().addCallbackParam("success", true);
	}

	/** Valuta una checklist (cambia stato, aggiunge dtValutazione e salva) */
	public void valida() {

		session.putProfilaturaByDesc(checklist.getIdPfPrincipal(), ConstantsSingleton.GpGruppo.AZIENDA_DEFAULT,
				ConstantsSingleton.GpRuolo.MyStage.ENTE_OSPITANTE_DESC);

		checklist.setDeStatoFbChecklist(
				deStatoFbChecklistHome.findDTOById(ConstantsSingleton.DeStatoFbChecklist.VALIDATA));
		checklist.setDtValutazione(new Date());
		boolean saveValue = save();

		if (saveValue)
			RequestContext.getCurrentInstance().addCallbackParam("success", true);
		else
			RequestContext.getCurrentInstance().addCallbackParam("success", false);

	}

	/** Elimina una checklist e rimanda alla pagina 'lista checklist' */
	public void elimina() {
		fbChecklistHome.removeById(checklist.getId(), session.getPrincipalId());
		if (isEnteOspitante()) {
			redirect("/faces/secure/azienda/convenzioni_quadro/checklist/checklist_list.xhtml");
		} else if (session.isRegione()) {
			redirect("/faces/secure/regione/convenzioni_quadro/checklist/checklist_list.xhtml");
		} else {
			redirectHome();
		}
	}

	public FbChecklistDTO getChecklist() {
		return checklist;
	}

	public void setChecklist(FbChecklistDTO checklist) {
		this.checklist = checklist;
	}

	public boolean isShowDialog() {
		return showDialog;
	}

	public void setShowDialog(boolean showDialog) {
		this.showDialog = showDialog;
	}

}
