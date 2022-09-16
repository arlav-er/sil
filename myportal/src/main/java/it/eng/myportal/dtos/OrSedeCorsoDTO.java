package it.eng.myportal.dtos;

import java.io.Serializable;
import java.util.Date;

public class OrSedeCorsoDTO extends AbstractUpdatablePkDTO implements
		Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6734276163271770668L;

	private String codComune;
	private String strComune;
	private String targa;
	private Integer idOrCorso;
	private String nominativoReferente;
	private String telefonoReferente;
	private String emailReferente;
	private Date dtmIns;
	private Date dtmMod;
	private Integer idPrincipalIns;
	private Integer idPrincipalMod;
	
	public String getCodComune() {
		return codComune;
	}
	public void setCodComune(String codComune) {
		this.codComune = codComune;
	}
	public String getStrComune() {
		return strComune;
	}
	public void setStrComune(String strComune) {
		this.strComune = strComune;
	}
	public String getTarga() {
		return targa;
	}
	public void setTarga(String targa) {
		this.targa = targa;
	}
	public Integer getIdOrCorso() {
		return idOrCorso;
	}
	public void setIdOrCorso(Integer idOrCorso) {
		this.idOrCorso = idOrCorso;
	}
	public String getNominativoReferente() {
		return nominativoReferente;
	}
	public void setNominativoReferente(String nominativoReferente) {
		this.nominativoReferente = nominativoReferente;
	}
	public String getTelefonoReferente() {
		return telefonoReferente;
	}
	public void setTelefonoReferente(String telefonoReferente) {
		this.telefonoReferente = telefonoReferente;
	}
	public String getEmailReferente() {
		return emailReferente;
	}
	public void setEmailReferente(String emailReferente) {
		this.emailReferente = emailReferente;
	}
	public Date getDtmIns() {
		return dtmIns;
	}
	public void setDtmIns(Date dtmIns) {
		this.dtmIns = dtmIns;
	}
	public Date getDtmMod() {
		return dtmMod;
	}
	public void setDtmMod(Date dtmMod) {
		this.dtmMod = dtmMod;
	}
	public Integer getIdPrincipalIns() {
		return idPrincipalIns;
	}
	public void setIdPrincipalIns(Integer idPrincipalIns) {
		this.idPrincipalIns = idPrincipalIns;
	}
	public Integer getIdPrincipalMod() {
		return idPrincipalMod;
	}
	public void setIdPrincipalMod(Integer idPrincipalMod) {
		this.idPrincipalMod = idPrincipalMod;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OrSedeCorsoDTO [codComune=");
		builder.append(codComune);
		builder.append(", strComune=");
		builder.append(strComune);
		builder.append(", targa=");
		builder.append(targa);
		builder.append(", idOrCorso=");
		builder.append(idOrCorso);
		builder.append(", nominativoReferente=");
		builder.append(nominativoReferente);
		builder.append(", telefonoReferente=");
		builder.append(telefonoReferente);
		builder.append(", emailReferente=");
		builder.append(emailReferente);
		builder.append(", dtmIns=");
		builder.append(dtmIns);
		builder.append(", dtmMod=");
		builder.append(dtmMod);
		builder.append(", idPrincipalIns=");
		builder.append(idPrincipalIns);
		builder.append(", idPrincipalMod=");
		builder.append(idPrincipalMod);
		builder.append("]");
		return builder.toString();
	}
}
