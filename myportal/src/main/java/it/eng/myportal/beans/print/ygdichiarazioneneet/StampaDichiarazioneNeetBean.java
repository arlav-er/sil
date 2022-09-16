package it.eng.myportal.beans.print.ygdichiarazioneneet;

import it.eng.myportal.dtos.YgDichiarazioneNeetDTO;
import it.eng.myportal.entity.home.YgDichiarazioneNeetHome;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "stampaDichiarazioneNeetBean")
@ViewScoped
public class StampaDichiarazioneNeetBean {

	private YgDichiarazioneNeetDTO ygDichiarazioneNeetDTO;
	private Date oggi;

	@EJB
	private YgDichiarazioneNeetHome ygDichiarazioneNeetHome;

	@PostConstruct
	public void postConstruct() {
		String idYgDichiarazioneNeetString = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("idYgDichiarazioneNeet");

		if (idYgDichiarazioneNeetString != null) {
			ygDichiarazioneNeetDTO = ygDichiarazioneNeetHome.findDTObyId(Integer.parseInt(idYgDichiarazioneNeetString));
		}

		oggi = Calendar.getInstance().getTime();
	}

	public String dateToString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String str = sdf.format(date);
		return str;
	}

	public String dateToStringAnno(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String str = sdf.format(date);
		return str;
	}

	public String dateToStringFull(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
		String str = sdf.format(date);
		return str;
	}

	public YgDichiarazioneNeetDTO getYgDichiarazioneNeetDTO() {
		return ygDichiarazioneNeetDTO;
	}

	public void setYgDichiarazioneNeetDTO(YgDichiarazioneNeetDTO ygDichiarazioneNeetDTO) {
		this.ygDichiarazioneNeetDTO = ygDichiarazioneNeetDTO;
	}

	public Date getOggi() {
		return oggi;
	}

	public void setOggi(Date oggi) {
		this.oggi = oggi;
	}

}
