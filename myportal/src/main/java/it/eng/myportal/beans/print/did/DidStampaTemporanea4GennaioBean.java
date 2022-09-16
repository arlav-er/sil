package it.eng.myportal.beans.print.did;

import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "didStampaTemporanea4GennaioBean")
@ViewScoped
public class DidStampaTemporanea4GennaioBean {

	private UtenteCompletoDTO utenteCompletoDTO;

	private Date oggi;

	@EJB
	private PfPrincipalHome pfPrincipalHome;

	@EJB
	private UtenteInfoHome utenteInfoHome;

	@EJB
	private DeProvinciaHome deProvinciaHome;

	@PostConstruct
	public void postConstruct() {
		String idAppuntamentoDTOString = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("idPfPrincipal");

		if (idAppuntamentoDTOString != null) {
			utenteCompletoDTO = utenteInfoHome.findDTOCompletoById(Integer.parseInt(idAppuntamentoDTOString));
		}

		oggi = Calendar.getInstance().getTime();
	}

	public String dateToString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String str = sdf.format(date);
		return str;
	}

	public String getDenominazioneProvincia(DeComuneDTO deComuneDTO) {
		return deProvinciaHome.findById(deComuneDTO.getIdProvincia()).getDenominazione();
	}

	public Date getOggi() {
		return oggi;
	}

	public void setOggi(Date oggi) {
		this.oggi = oggi;
	}

	public UtenteCompletoDTO getUtenteCompletoDTO() {
		return utenteCompletoDTO;
	}

	public void setUtenteCompletoDTO(UtenteCompletoDTO utenteCompletoDTO) {
		this.utenteCompletoDTO = utenteCompletoDTO;
	}

}
