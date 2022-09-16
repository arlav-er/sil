package it.eng.myportal.dtos;


public class YgAdesioneAziendaSedeDTO extends AbstractUpdatablePkDTO {

	private static final long serialVersionUID = 7024168194820927663L;
	private YgAdesioneAziendaDTO ygAdesioneAziendaDTO;
	private PfPrincipalDTO pfPrincipalDTO;
	private Boolean flgTirocinio;
	private Boolean flgSedeLegale;
	private String indirizzo;
	private DeComuneDTO deComuneDTO;
	private String cap;
	private String telefono;
	private String fax;

	public YgAdesioneAziendaDTO getYgAdesioneAziendaDTO() {
		return ygAdesioneAziendaDTO;
	}

	public void setYgAdesioneAziendaDTO(
			YgAdesioneAziendaDTO ygAdesioneAziendaDTO) {
		this.ygAdesioneAziendaDTO = ygAdesioneAziendaDTO;
	}

	public PfPrincipalDTO getPfPrincipalDTO() {
		return pfPrincipalDTO;
	}

	public void setPfPrincipalDTO(PfPrincipalDTO pfPrincipalDTO) {
		this.pfPrincipalDTO = pfPrincipalDTO;
	}

	public Boolean getFlgTirocinio() {
		return flgTirocinio;
	}

	public void setFlgTirocinio(Boolean flgTirocinio) {
		this.flgTirocinio = flgTirocinio;
	}

	public Boolean getFlgSedeLegale() {
		return flgSedeLegale;
	}

	public void setFlgSedeLegale(Boolean flgSedeLegale) {
		this.flgSedeLegale = flgSedeLegale;
	}

	public String getIndirizzo() {
		return indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	public DeComuneDTO getDeComuneDTO() {
		return deComuneDTO;
	}

	public void setDeComuneDTO(DeComuneDTO deComuneDTO) {
		this.deComuneDTO = deComuneDTO;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((cap == null) ? 0 : cap.hashCode());
		result = prime * result
				+ ((deComuneDTO == null) ? 0 : deComuneDTO.hashCode());
		result = prime * result + ((fax == null) ? 0 : fax.hashCode());
		result = prime * result
				+ ((flgSedeLegale == null) ? 0 : flgSedeLegale.hashCode());
		result = prime * result
				+ ((flgTirocinio == null) ? 0 : flgTirocinio.hashCode());
		result = prime * result
				+ ((indirizzo == null) ? 0 : indirizzo.hashCode());
		result = prime * result
				+ ((pfPrincipalDTO == null) ? 0 : pfPrincipalDTO.hashCode());
		result = prime * result
				+ ((telefono == null) ? 0 : telefono.hashCode());
		result = prime
				* result
				+ ((ygAdesioneAziendaDTO == null) ? 0 : ygAdesioneAziendaDTO
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		YgAdesioneAziendaSedeDTO other = (YgAdesioneAziendaSedeDTO) obj;
		if (cap == null) {
			if (other.cap != null)
				return false;
		} else if (!cap.equals(other.cap))
			return false;
		if (deComuneDTO == null) {
			if (other.deComuneDTO != null)
				return false;
		} else if (!deComuneDTO.equals(other.deComuneDTO))
			return false;
		if (fax == null) {
			if (other.fax != null)
				return false;
		} else if (!fax.equals(other.fax))
			return false;
		if (flgSedeLegale == null) {
			if (other.flgSedeLegale != null)
				return false;
		} else if (!flgSedeLegale.equals(other.flgSedeLegale))
			return false;
		if (flgTirocinio == null) {
			if (other.flgTirocinio != null)
				return false;
		} else if (!flgTirocinio.equals(other.flgTirocinio))
			return false;
		if (indirizzo == null) {
			if (other.indirizzo != null)
				return false;
		} else if (!indirizzo.equals(other.indirizzo))
			return false;
		if (pfPrincipalDTO == null) {
			if (other.pfPrincipalDTO != null)
				return false;
		} else if (!pfPrincipalDTO.equals(other.pfPrincipalDTO))
			return false;
		if (telefono == null) {
			if (other.telefono != null)
				return false;
		} else if (!telefono.equals(other.telefono))
			return false;
		if (ygAdesioneAziendaDTO == null) {
			if (other.ygAdesioneAziendaDTO != null)
				return false;
		} else if (!ygAdesioneAziendaDTO.equals(other.ygAdesioneAziendaDTO))
			return false;
		return true;
	}	

}
