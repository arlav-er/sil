package it.eng.myportal.beans.print.patronato;

import it.eng.myportal.dtos.AppuntamentoDTO;
import it.eng.myportal.entity.home.AgAppuntamentoHome;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "patronatoPromemoriaAppuntamentoBean")
@ViewScoped
public class PatronatoPromemoriaAppuntamentoBean {

	private AppuntamentoDTO appuntamentoPatronatoDTO;
	private Date oggi;

	@EJB
	private AgAppuntamentoHome agAppuntamentoHome;

	@PostConstruct
	public void postConstruct() {
		String idAppuntamentoDTOString = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("idAgAppuntamento");

		if (idAppuntamentoDTOString != null) {
			appuntamentoPatronatoDTO = agAppuntamentoHome.findAppuntamentoDTObyIdAgAppuntamento(Integer
					.parseInt(idAppuntamentoDTOString));
		}

		oggi = Calendar.getInstance().getTime();
	}

	public String dateToString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String str = sdf.format(date);
		return str;
	}

	public AppuntamentoDTO getAppuntamentoPatronatoDTO() {
		return appuntamentoPatronatoDTO;
	}

	public void setAppuntamentoPatronatoDTO(AppuntamentoDTO appuntamentoPatronatoDTO) {
		this.appuntamentoPatronatoDTO = appuntamentoPatronatoDTO;
	}

	public Date getOggi() {
		return oggi;
	}

	public void setOggi(Date oggi) {
		this.oggi = oggi;
	}

}
