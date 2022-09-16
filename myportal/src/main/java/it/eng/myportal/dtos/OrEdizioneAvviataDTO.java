package it.eng.myportal.dtos;

import java.io.Serializable;
import java.util.Date;

public class OrEdizioneAvviataDTO extends AbstractUpdatablePkDTO implements
		Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6734276163271770668L;

	private String codComune;
	private String strComune;
	private String targa;
	private Integer idOrCorso;
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
	public Integer getIdOrCorso() {
		return idOrCorso;
	}
	public void setIdOrCorso(Integer idOrCorso) {
		this.idOrCorso = idOrCorso;
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
	public String getTarga() {
		return targa;
	}
	public void setTarga(String targa) {
		this.targa = targa;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OrEdizioneAvviataDTO [codComune=");
		builder.append(codComune);
		builder.append(", strComune=");
		builder.append(strComune);
		builder.append(", targa=");
		builder.append(targa);
		builder.append(", idOrCorso=");
		builder.append(idOrCorso);
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
