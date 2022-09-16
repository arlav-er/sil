package it.eng.myportal.rest.app.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NotifichePojoList implements Serializable {

	private static final long serialVersionUID = 6738250410050789335L;

	private Long numNotificheTotali;
	private Long numNuoveNotifiche;
	private List<NotifichePojo> listaNotifiche;

	public NotifichePojoList() {
		listaNotifiche = new ArrayList<NotifichePojo>();
		numNotificheTotali = Long.valueOf(0);
		numNuoveNotifiche = Long.valueOf(0);
	}

	public Long getNumNotificheTotali() {
		return numNotificheTotali;
	}

	public void setNumNotificheTotali(Long numNotificheTotali) {
		this.numNotificheTotali = numNotificheTotali;
	}

	public Long getNumNuoveNotifiche() {
		return numNuoveNotifiche;
	}

	public void setNumNuoveNotifiche(Long numNuoveNotifiche) {
		this.numNuoveNotifiche = numNuoveNotifiche;
	}

	public List<NotifichePojo> getListaNotifiche() {
		return listaNotifiche;
	}

	public void setListaNotifiche(List<NotifichePojo> listaNotifiche) {
		this.listaNotifiche = listaNotifiche;
	}
}
