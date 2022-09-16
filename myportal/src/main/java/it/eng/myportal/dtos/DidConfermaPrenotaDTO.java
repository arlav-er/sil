package it.eng.myportal.dtos;

import java.util.Date;


public class DidConfermaPrenotaDTO extends AbstractUpdatablePkDTO implements IUpdatable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	
	private PfPrincipalDTO principal;	
	private String codiceFiscale;
	private DeComuneDTO comuneDomicilio;
	private Date dtConferma;
	
	
	public PfPrincipalDTO getPrincipal() {
		return principal;
	}
	public void setPrincipal(PfPrincipalDTO principal) {
		this.principal = principal;
	}
	public String getCodiceFiscale() {
		return codiceFiscale;
	}
	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}
	public DeComuneDTO getComuneDomicilio() {
		return comuneDomicilio;
	}
	public void setComuneDomicilio(DeComuneDTO comuneDomicilio) {
		this.comuneDomicilio = comuneDomicilio;
	}
	public Date getDtConferma() {
		return dtConferma;
	}
	public void setDtConferma(Date dtConferma) {
		this.dtConferma = dtConferma;
	}
}
