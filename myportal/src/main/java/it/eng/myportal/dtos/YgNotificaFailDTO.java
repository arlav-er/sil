package it.eng.myportal.dtos;

import java.util.Date;

public class YgNotificaFailDTO extends AbstractUpdatablePkDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4703374440935794192L;

	private String codiceFiscale;
	private Date dtAdesione;
	private String codRegioneAdesione;
	private String codStatoAdesioneMin;
	private Date dtStatoAdesioneMin;
	private String codErrore;
	private String strMessErrore;
	private YgAdesioneDTO ygAdesioneDTO;
	private String codMonoTipoNotifica;

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public Date getDtAdesione() {
		return dtAdesione;
	}

	public void setDtAdesione(Date dtAdesione) {
		this.dtAdesione = dtAdesione;
	}

	public String getCodRegioneAdesione() {
		return codRegioneAdesione;
	}

	public void setCodRegioneAdesione(String codRegioneAdesione) {
		this.codRegioneAdesione = codRegioneAdesione;
	}

	public String getCodStatoAdesioneMin() {
		return codStatoAdesioneMin;
	}

	public void setCodStatoAdesioneMin(String codStatoAdesioneMin) {
		this.codStatoAdesioneMin = codStatoAdesioneMin;
	}

	public Date getDtStatoAdesioneMin() {
		return dtStatoAdesioneMin;
	}

	public void setDtStatoAdesioneMin(Date dtStatoAdesioneMin) {
		this.dtStatoAdesioneMin = dtStatoAdesioneMin;
	}

	public String getCodErrore() {
		return codErrore;
	}

	public void setCodErrore(String codErrore) {
		this.codErrore = codErrore;
	}

	public String getStrMessErrore() {
		return strMessErrore;
	}

	public void setStrMessErrore(String strMessErrore) {
		this.strMessErrore = strMessErrore;
	}

	public YgAdesioneDTO getYgAdesioneDTO() {
		return ygAdesioneDTO;
	}

	public void setYgAdesioneDTO(YgAdesioneDTO ygAdesioneDTO) {
		this.ygAdesioneDTO = ygAdesioneDTO;
	}

	public String getCodMonoTipoNotifica() {
		return codMonoTipoNotifica;
	}

	public void setCodMonoTipoNotifica(String codMonoTipoNotifica) {
		this.codMonoTipoNotifica = codMonoTipoNotifica;
	}

}
