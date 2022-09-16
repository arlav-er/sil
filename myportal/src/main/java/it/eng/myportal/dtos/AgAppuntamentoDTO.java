package it.eng.myportal.dtos;

import it.eng.myportal.entity.YgAdesione;

import java.util.Date;

public class AgAppuntamentoDTO extends AbstractUpdatablePkDTO {
	private static final long serialVersionUID = 4951104113034424206L;

	private PfPrincipalDTO pfPrincipalDTO;
	private Date dtAppuntamento;
	private String oraAppuntamento;
	private DeCpiDTO deCpiDTO;
	private String denominazioneCpi;
	private String indirizzoCpiStampa;
	private String siglaOperatore;
	private String ambiente;
	private DeTipoAppuntamentoDTO deTipoAppuntamentoDTO;
	private YgAdesione ygAdesione;

	public PfPrincipalDTO getPfPrincipalDTO() {
		return pfPrincipalDTO;
	}

	public void setPfPrincipalDTO(PfPrincipalDTO pfPrincipalDTO) {
		this.pfPrincipalDTO = pfPrincipalDTO;
	}

	public Date getDtAppuntamento() {
		return dtAppuntamento;
	}

	public void setDtAppuntamento(Date dtAppuntamento) {
		this.dtAppuntamento = dtAppuntamento;
	}

	public String getOraAppuntamento() {
		return oraAppuntamento;
	}

	public void setOraAppuntamento(String oraAppuntamento) {
		this.oraAppuntamento = oraAppuntamento;
	}

	public DeCpiDTO getDeCpiDTO() {
		return deCpiDTO;
	}

	public void setDeCpiDTO(DeCpiDTO deCpiDTO) {
		this.deCpiDTO = deCpiDTO;
	}

	public String getDenominazioneCpi() {
		return denominazioneCpi;
	}

	public void setDenominazioneCpi(String denominazioneCpi) {
		this.denominazioneCpi = denominazioneCpi;
	}

	public String getIndirizzoCpiStampa() {
		return indirizzoCpiStampa;
	}

	public void setIndirizzoCpiStampa(String indirizzoCpiStampa) {
		this.indirizzoCpiStampa = indirizzoCpiStampa;
	}

	public String getSiglaOperatore() {
		return siglaOperatore;
	}

	public void setSiglaOperatore(String siglaOperatore) {
		this.siglaOperatore = siglaOperatore;
	}

	public String getAmbiente() {
		return ambiente;
	}

	public void setAmbiente(String ambiente) {
		this.ambiente = ambiente;
	}

	public DeTipoAppuntamentoDTO getDeTipoAppuntamentoDTO() {
		return deTipoAppuntamentoDTO;
	}

	public void setDeTipoAppuntamentoDTO(DeTipoAppuntamentoDTO deTipoAppuntamentoDTO) {
		this.deTipoAppuntamentoDTO = deTipoAppuntamentoDTO;
	}

	public YgAdesione getYgAdesione() {
		return ygAdesione;
	}

	public void setYgAdesione(YgAdesione ygAdesione) {
		this.ygAdesione = ygAdesione;
	}

}
