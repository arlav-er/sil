package it.eng.myportal.dtos;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class OrEnteCorsoDTO extends AbstractUpdatablePkDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6409538905125818619L;
	private String codComune;
	private String strComune;
	private String targa;
    private Integer codiceOrganismo;
    private String ragioneSociale;
    private String indirizzo;
    private String cap;
    private String telefono;
    private String fax;
    private String email;
    private Date dtmIns;
    private Date dtmMod;
    private Integer idPrincipalIns;
    private Integer idPrincipalMod;
    private Set<OrCorsoDTO> orCorsos;
    
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
	public Integer getCodiceOrganismo() {
		return codiceOrganismo;
	}
	public void setCodiceOrganismo(Integer codiceOrganismo) {
		this.codiceOrganismo = codiceOrganismo;
	}
	public String getRagioneSociale() {
		return ragioneSociale;
	}
	public void setRagioneSociale(String ragioneSociale) {
		this.ragioneSociale = ragioneSociale;
	}
	public String getIndirizzo() {
		return indirizzo;
	}
	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}
	public String getCap() {
		return cap;
	}
	public void setCap(String cap) {
		this.cap = cap;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
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
	public Set<OrCorsoDTO> getOrCorsos() {
		return orCorsos;
	}
	public void setOrCorsos(Set<OrCorsoDTO> orCorsos) {
		this.orCorsos = orCorsos;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OrEnteCorsoDTO [codComune=");
		builder.append(codComune);
		builder.append(", strComune=");
		builder.append(strComune);
		builder.append(", targa=");
		builder.append(targa);
		builder.append(", codiceOrganismo=");
		builder.append(codiceOrganismo);
		builder.append(", ragioneSociale=");
		builder.append(ragioneSociale);
		builder.append(", indirizzo=");
		builder.append(indirizzo);
		builder.append(", cap=");
		builder.append(cap);
		builder.append(", telefono=");
		builder.append(telefono);
		builder.append(", fax=");
		builder.append(fax);
		builder.append(", email=");
		builder.append(email);
		builder.append(", dtmIns=");
		builder.append(dtmIns);
		builder.append(", dtmMod=");
		builder.append(dtmMod);
		builder.append(", idPrincipalIns=");
		builder.append(idPrincipalIns);
		builder.append(", idPrincipalMod=");
		builder.append(idPrincipalMod);
		builder.append(", orCorsos=");
		builder.append(orCorsos);
		builder.append("]");
		return builder.toString();
	}
}
