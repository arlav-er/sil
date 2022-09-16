package it.eng.myportal.beans.messaggi;

import it.eng.myportal.beans.AbstractMasterDetailTabBean;
import it.eng.myportal.dtos.MsgContattoDTO;
import it.eng.myportal.utils.PaginationHandler;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;

public abstract class AbstractContattiBean extends AbstractMasterDetailTabBean<MsgContattoDTO> {

	private String linkRiferimentoMessaggio;
	protected PaginationHandler paginationHandler;

	/**
	 * La risposta al contatto. Campo utilizzato per inserire la risposta.
	 */
	private MsgContattoDTO risposta;

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		this.setLinkRiferimentoMessaggio(this.componiBaseLinkRiferimentoMessaggio());
		try {
			resetPagination();
			data = buildNewDataIstance();
			risposta = new MsgContattoDTO();

			if (getSession().getIdMsgMessaggio() != null) {
				for (MsgContattoDTO element : list) {
					if (element.getId().intValue() == getSession().getIdMsgMessaggio().intValue()) {
						data = element;
						showPanel = true;
						editing = false;
						saved = true;
						break;
					}
				}

			}

		} catch (EJBException e) {
			addErrorMessage("data.error_loading", e);
		}
	}

	public abstract String componiBaseLinkRiferimentoMessaggio();

	public abstract String getTestoLinkRiferimentoMessaggio();
	
	protected abstract void resetPagination();

	public String getTestoCvNonPresente() {
		return "Il CV che si sta cercando non è presente nel sistema.";
	}

	public String getTestVacancyNonPresente() {
		return "L'offerta di lavoro che si vuole visualizzare non è presente nel sistema";
	}

	public void inizializzaNuovaRisposta() {
		risposta = new MsgContattoDTO();
	}

	public String getLinkRiferimentoMessaggio() {
		return linkRiferimentoMessaggio;
	}

	public void setLinkRiferimentoMessaggio(String linkRiferimentoMessaggio) {
		this.linkRiferimentoMessaggio = linkRiferimentoMessaggio;
	}

	public MsgContattoDTO getRisposta() {
		return risposta;
	}

	public void setRisposta(MsgContattoDTO risposta) {
		this.risposta = risposta;
	}
	
	public PaginationHandler getPaginationHandler() {
		return paginationHandler;
	}

}
