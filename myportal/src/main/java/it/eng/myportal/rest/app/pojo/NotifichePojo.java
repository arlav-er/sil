package it.eng.myportal.rest.app.pojo;

import java.io.Serializable;

import org.json.JSONException;

import it.eng.myportal.entity.AppNotifica;
import it.eng.myportal.rest.app.helper.AdditionalDataNotification;

public class NotifichePojo implements Serializable {

	private static final long serialVersionUID = 324217405375085234L;

	private String idNotifica;
	private String titolo;
	private String sottotitolo;
	private String messaggio;
	private String dtNotifica;
	private boolean nuova;
	private AdditionalDataNotification data;

	public NotifichePojo() {

	}

	public NotifichePojo(String idNotifica, String titolo, String sottotitolo, String messaggio, String dtNotifica,
			boolean nuova, String data) throws JSONException {
		super();
		this.idNotifica = idNotifica;
		this.titolo = titolo;
		this.sottotitolo = sottotitolo;
		this.messaggio = messaggio;
		this.dtNotifica = dtNotifica;
		this.nuova = nuova;
		if (data != null)
			this.data = new AdditionalDataNotification(data);
	}

	public NotifichePojo(AppNotifica notifica) throws JSONException {
		this(notifica.getSidNotifica(), notifica.getTitolo(), notifica.getSottotitolo(), notifica.getMessaggio(),
				notifica.getDtNotifica(), notifica.isNuova(), notifica.getAdditionalData());
	}

	public String getIdNotifica() {
		return idNotifica;
	}

	public void setIdNotifica(String idNotifica) {
		this.idNotifica = idNotifica;
	}

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public String getSottotitolo() {
		return sottotitolo;
	}

	public void setSottotitolo(String sottotitolo) {
		this.sottotitolo = sottotitolo;
	}

	public String getMessaggio() {
		return messaggio;
	}

	public void setMessaggio(String messaggio) {
		this.messaggio = messaggio;
	}

	public String getDtNotifica() {
		return dtNotifica;
	}

	public void setDtNotifica(String dtNotifica) {
		this.dtNotifica = dtNotifica;
	}

	public boolean isNuova() {
		return nuova;
	}

	public void setNuova(boolean nuova) {
		this.nuova = nuova;
	}

	public AdditionalDataNotification getData() {
		return data;
	}

	public void setData(AdditionalDataNotification data) {
		this.data = data;
	}
}
