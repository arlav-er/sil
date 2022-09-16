package it.eng.myportal.dtos;

import java.util.ArrayList;
import java.util.List;

public class MsgSupportoDTO extends MsgMessaggioDTO implements IHasRisposte {
	private static final long serialVersionUID = 5044442102247973972L;

	private List<IDecode> listaCurricula;
	private List<IDecode> listaLettere;

	public MsgSupportoDTO() {
		super();
		listaCurricula = new ArrayList<IDecode>();
		listaLettere = new ArrayList<IDecode>();
		risposte = new ArrayList<MsgMessaggioDTO>();
	}

	public MsgSupportoDTO(MsgMessaggioDTO msg) {
		this();
		this.setDtmIns(msg.getDtmIns());
		this.setOggetto(msg.getOggetto());
		this.setMittente(msg.getMittente());
		this.setIdFrom(msg.getIdFrom());
		// this.setLetto(msg.getLetto());
		this.setId(msg.getId());
		this.setCorpo(msg.getCorpo());
		this.setTicket(msg.getTicket());
		this.setIdTo(msg.getIdTo());
		this.setProvinciaTo(msg.getProvinciaTo());
		this.setTemaConsulenza(msg.getTemaConsulenza());
	}

	public List<IDecode> getListaCurricula() {
		return listaCurricula;
	}

	public void setListaCurricula(List<IDecode> listaCurricula) {
		this.listaCurricula = listaCurricula;
	}

	public List<IDecode> getListaLettere() {
		return listaLettere;
	}

	public void setListaLettere(List<IDecode> listaLettere) {
		this.listaLettere = listaLettere;
	}

}
