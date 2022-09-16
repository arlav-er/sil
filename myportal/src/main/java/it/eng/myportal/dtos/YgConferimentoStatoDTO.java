package it.eng.myportal.dtos;

import java.util.Date;

public class YgConferimentoStatoDTO extends AbstractUpdatablePkDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8269923910449827093L;

	private String codiceFiscale;
	private DeProvinciaDTO deProvinciaDTO;
	private DeStatoAdesioneMinDTO deStatoAdesioneMinDTO;
	private YgAdesioneDTO ygAdesioneDTO;
	private Boolean flgProcessato;
	private Date dtmInvio;
	private String codEsito;
	private String strMessErrore;

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public DeProvinciaDTO getDeProvinciaDTO() {
		return deProvinciaDTO;
	}

	public void setDeProvinciaDTO(DeProvinciaDTO deProvinciaDTO) {
		this.deProvinciaDTO = deProvinciaDTO;
	}

	public DeStatoAdesioneMinDTO getDeStatoAdesioneMinDTO() {
		return deStatoAdesioneMinDTO;
	}

	public void setDeStatoAdesioneMinDTO(DeStatoAdesioneMinDTO deStatoAdesioneMinDTO) {
		this.deStatoAdesioneMinDTO = deStatoAdesioneMinDTO;
	}

	public YgAdesioneDTO getYgAdesioneDTO() {
		return ygAdesioneDTO;
	}

	public void setYgAdesioneDTO(YgAdesioneDTO ygAdesioneDTO) {
		this.ygAdesioneDTO = ygAdesioneDTO;
	}

	public Boolean getFlgProcessato() {
		return flgProcessato;
	}

	public void setFlgProcessato(Boolean flgProcessato) {
		this.flgProcessato = flgProcessato;
	}

	public Date getDtmInvio() {
		return dtmInvio;
	}

	public void setDtmInvio(Date dtmInvio) {
		this.dtmInvio = dtmInvio;
	}

	public String getCodEsito() {
		return codEsito;
	}

	public void setCodEsito(String codEsito) {
		this.codEsito = codEsito;
	}

	public String getStrMessErrore() {
		return strMessErrore;
	}

	public void setStrMessErrore(String strMessErrore) {
		this.strMessErrore = strMessErrore;
	}

}
