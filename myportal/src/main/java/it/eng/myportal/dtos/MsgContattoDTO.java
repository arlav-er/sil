package it.eng.myportal.dtos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Oggetto per gestire i messaggi di primo contatto
 * 
 * @author Rodi A.
 * 
 */
public class MsgContattoDTO extends MsgMessaggioDTO implements IHasRisposte {

	private static final long serialVersionUID = 504444210224797397L;

	private Boolean esito;

	List<MsgMessaggioDTO> risposte;

	private Integer idVaDatiVacancy;

	private Integer idAcCandidatura;
	
	private Integer idCvDatiPersonali;

	public MsgContattoDTO() {
		super();
		risposte = new ArrayList<MsgMessaggioDTO>();
		esito = true;
	}

	public MsgContattoDTO(MsgMessaggioDTO msg) {
		this();
		this.setDtmIns(msg.getDtmIns());
		this.setOggetto(msg.getOggetto());
		this.setMittente(msg.getMittente());
		this.setDestinatario(msg.getDestinatario());
		this.setIdFrom(msg.getIdFrom());
		// this.setLetto(msg.getLetto());
		this.setId(msg.getId());
		this.setCorpo(msg.getCorpo());
		this.setTicket(msg.getTicket());
		this.setIdTo(msg.getIdTo());
	}

	public List<MsgMessaggioDTO> getRisposte() {
		return risposte;
	}

	public void setRisposte(List<MsgMessaggioDTO> risposte) {
		this.risposte = risposte;
	}

	public MsgMessaggioDTO getLast() {
		if (risposte == null || risposte.isEmpty())
			return this;
		return risposte.get(risposte.size() - 1);
	}

	public Date getDtmRisposta() {
		if (risposte == null || risposte.isEmpty())
			return null;
		return risposte.get(risposte.size() - 1).getDtmIns();
	}

	public Boolean getEsito() {
		return esito;
	}

	public void setEsito(Boolean esito) {
		this.esito = esito;
	}

	public Integer getIdVaDatiVacancy() {
		return idVaDatiVacancy;
	}

	public void setIdVaDatiVacancy(Integer idVaDatiVacancy) {
		this.idVaDatiVacancy = idVaDatiVacancy;
	}

	public Integer getIdCvDatiPersonali() {
		return idCvDatiPersonali;
	}

	public void setIdCvDatiPersonali(Integer idCvDatiPersonali) {
		this.idCvDatiPersonali = idCvDatiPersonali;
	}

	public Integer getIdAcCandidatura() {
		return idAcCandidatura;
	}

	public void setIdAcCandidatura(Integer idAcCandidatura) {
		this.idAcCandidatura = idAcCandidatura;
	}

}
