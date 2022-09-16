package it.eng.myportal.beans;

import it.eng.myportal.dtos.DeRuoloPortaleDTO;
import it.eng.myportal.dtos.PfAbilitazioneDTO;
import it.eng.myportal.entity.home.PfAbilitazioneHome;
import it.eng.myportal.entity.home.decodifiche.DeRuoloPortaleHome;
import it.eng.myportal.security.Roles;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

//@SecurityDomain(SecurityDomains.MYPORTAL)
@RolesAllowed({ Roles.AMMINISTRATORE })
@ManagedBean
@ViewScoped
public class AbilitazioneBean extends AbstractBaseBean {

	private List<SelectItem> listaRuoliOptions;
	private List<PfAbilitazioneDTO> listaAbilitazioni;
	private Boolean ricercaEseguita = false;
	private DeRuoloPortaleDTO deRuoloPortaleDTO;
	private String codRuoloPortale;

	@EJB
	PfAbilitazioneHome pfAbilitazioneHome;

	@EJB
	DeRuoloPortaleHome deRuoloPortaleHome;

	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		if (deRuoloPortaleDTO == null) {
			deRuoloPortaleDTO = new DeRuoloPortaleDTO();
		}

		setListaRuoliOptions(deRuoloPortaleHome.getListItems(true));
		log.debug("Costruito il Bean per la gestione della profilatura.");
	}

	public void search() {
		emptyResults();
		listaAbilitazioni = pfAbilitazioneHome.cercaAbilitazioni(codRuoloPortale);
		ricercaEseguita = true;
	}

	public void modificaAbilitazione() {
		try {
			boolean aggiornamento = false;
			for (PfAbilitazioneDTO abilitazioneS : listaAbilitazioni) {
				pfAbilitazioneHome.mergeDTO(abilitazioneS, new Integer(0));
				aggiornamento = true;
			}

			if (aggiornamento) {
				addInfoMessage("data.updated");
			} else {
				addInfoMessage("data.no_update");
			}

			search();
		} catch (EJBException e) {
			gestisciErrore(e, "generic.error");
		}
	}

	private void emptyResults() {
		listaAbilitazioni = new ArrayList<PfAbilitazioneDTO>();
	}

	public List<SelectItem> getListaRuoliOptions() {
		return listaRuoliOptions;
	}

	public void setListaRuoliOptions(List<SelectItem> listaRuoliOptions) {
		this.listaRuoliOptions = listaRuoliOptions;
	}

	public Boolean getRicercaEseguita() {
		return ricercaEseguita;
	}

	public void setRicercaEseguita(Boolean ricercaEseguita) {
		this.ricercaEseguita = ricercaEseguita;
	}

	public List<PfAbilitazioneDTO> getListaAbilitazioni() {
		return listaAbilitazioni;
	}

	public void setListaAbilitazioni(List<PfAbilitazioneDTO> listaAbilitazioni) {
		this.listaAbilitazioni = listaAbilitazioni;
	}

	public String getCodRuoloPortale() {
		return codRuoloPortale;
	}

	public void setCodRuoloPortale(String codRuoloPortale) {
		this.codRuoloPortale = codRuoloPortale;
	}
}
