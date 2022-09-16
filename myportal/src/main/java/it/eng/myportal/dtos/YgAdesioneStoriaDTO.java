package it.eng.myportal.dtos;

import java.util.Date;

public class YgAdesioneStoriaDTO extends AbstractUpdatablePkDTO {
	private static final long serialVersionUID = 6688478902480979220L;

	private YgAdesioneDTO ygAdesioneDTO;
	private DeStatoAdesioneMinDTO deStatoAdesioneMinDTO;
	private DeStatoAdesioneDTO deStatoAdesioneDTO;
	private Date dtStatoAdesione;
	private Date dtInizio;
	private Date dtFine;

	public YgAdesioneDTO getYgAdesioneDTO() {
		return ygAdesioneDTO;
	}

	public void setYgAdesioneDTO(YgAdesioneDTO ygAdesioneDTO) {
		this.ygAdesioneDTO = ygAdesioneDTO;
	}

	public DeStatoAdesioneMinDTO getDeStatoAdesioneMinDTO() {
		return deStatoAdesioneMinDTO;
	}

	public void setDeStatoAdesioneMinDTO(DeStatoAdesioneMinDTO deStatoAdesioneMinDTO) {
		this.deStatoAdesioneMinDTO = deStatoAdesioneMinDTO;
	}

	public DeStatoAdesioneDTO getDeStatoAdesioneDTO() {
		return deStatoAdesioneDTO;
	}

	public void setDeStatoAdesioneDTO(DeStatoAdesioneDTO deStatoAdesioneDTO) {
		this.deStatoAdesioneDTO = deStatoAdesioneDTO;
	}

	public Date getDtStatoAdesione() {
		return dtStatoAdesione;
	}

	public void setDtStatoAdesione(Date dtStatoAdesione) {
		this.dtStatoAdesione = dtStatoAdesione;
	}

	public Date getDtInizio() {
		return dtInizio;
	}

	public void setDtInizio(Date dtInizio) {
		this.dtInizio = dtInizio;
	}

	public Date getDtFine() {
		return dtFine;
	}

	public void setDtFine(Date dtFine) {
		this.dtFine = dtFine;
	}

}
