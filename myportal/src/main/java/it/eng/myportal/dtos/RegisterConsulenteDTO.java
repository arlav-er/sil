package it.eng.myportal.dtos;

import java.util.Date;

public class RegisterConsulenteDTO extends RegisterDTO implements IDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private DeProvinciaDTO provincia;
	private DeTipoPraticaDTO deTipoPraticaDTO;
	private DeAtpEnteConsulenteDTO sindacato;
	private Integer monteOre;
	private Integer oreSvolte;
	private String opzPrivilegio;
	private String opzAtipicoPIva;
	private Date dtmInizio;
	private Date dtmFine;

	public RegisterConsulenteDTO() {
		super();
		provincia = new DeProvinciaDTO();
		deTipoPraticaDTO = new DeTipoPraticaDTO();
		sindacato = new DeAtpEnteConsulenteDTO();
	}

	public DeProvinciaDTO getProvincia() {
		return provincia;
	}

	public void setProvincia(DeProvinciaDTO provincia) {
		this.provincia = provincia;
	}

	public DeTipoPraticaDTO getDeTipoPraticaDTO() {
		return deTipoPraticaDTO;
	}

	public void setDeTipoPraticaDTO(DeTipoPraticaDTO deTipoPraticaDTO) {
		this.deTipoPraticaDTO = deTipoPraticaDTO;
	}

	public DeAtpEnteConsulenteDTO getSindacato() {
		return sindacato;
	}

	public void setSindacato(DeAtpEnteConsulenteDTO sindacato) {
		this.sindacato = sindacato;
	}

	public Integer getMonteOre() {
		return monteOre;
	}

	public void setMonteOre(Integer monteOre) {
		this.monteOre = monteOre;
	}

	public Integer getOreSvolte() {
		return oreSvolte;
	}

	public void setOreSvolte(Integer oreSvolte) {
		this.oreSvolte = oreSvolte;
	}

	public String getOpzPrivilegio() {
		return opzPrivilegio;
	}

	public void setOpzPrivilegio(String opzPrivilegio) {
		this.opzPrivilegio = opzPrivilegio;
	}

	public String getOpzAtipicoPIva() {
		return opzAtipicoPIva;
	}

	public void setOpzAtipicoPIva(String opzAtipicoPIva) {
		this.opzAtipicoPIva = opzAtipicoPIva;
	}

	public Date getDtmInizio() {
		return dtmInizio;
	}

	public void setDtmInizio(Date dtmInizio) {
		this.dtmInizio = dtmInizio;
	}

	public Date getDtmFine() {
		return dtmFine;
	}

	public void setDtmFine(Date dtmFine) {
		this.dtmFine = dtmFine;
	}

}
