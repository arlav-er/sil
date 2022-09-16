package it.eng.myportal.entity;

import it.eng.myportal.entity.decodifiche.DeComune;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "yg_adesione_azienda_sede", schema = "myportal")
public class YgAdesioneAziendaSede extends AbstractEntity implements java.io.Serializable {
	private static final long serialVersionUID = -5387116634641867722L;

	private Integer idYgAdesioneAziendaSede;
	private YgAdesioneAzienda ygAdesioneAzienda;
	private PfPrincipal pfPrincipal;
	private Boolean flgTirocinio;
	private Boolean flgSedeLegale;
	private String indirizzo;
	private DeComune deComune;
	private String cap;
	private String telefono;
	private String fax;

	@Id
	@SequenceGenerator(name = "yg_adesione_azienda_sede_id_yg_adesione_azienda_sede_seq", sequenceName = "yg_adesione_azienda_sede_id_yg_adesione_azienda_sede_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "yg_adesione_azienda_sede_id_yg_adesione_azienda_sede_seq")
	@Column(name = "id_yg_adesione_azienda_sede", unique = true, nullable = false)
	public Integer getIdYgAdesioneAziendaSede() {
		return idYgAdesioneAziendaSede;
	}

	public void setIdYgAdesioneAziendaSede(Integer idYgAdesioneAziendaSede) {
		this.idYgAdesioneAziendaSede = idYgAdesioneAziendaSede;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_yg_adesione_azienda", nullable = false)
	public YgAdesioneAzienda getYgAdesioneAzienda() {
		return ygAdesioneAzienda;
	}

	public void setYgAdesioneAzienda(YgAdesioneAzienda ygAdesioneAzienda) {
		this.ygAdesioneAzienda = ygAdesioneAzienda;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pf_principal", nullable = false)
	public PfPrincipal getPfPrincipal() {
		return pfPrincipal;
	}

	public void setPfPrincipal(PfPrincipal pfPrincipal) {
		this.pfPrincipal = pfPrincipal;
	}

	@Type(type = "yes_no")
	@Column(name = "flg_tirocinio", length = 1, nullable = false)
	public Boolean getFlgTirocinio() {
		return flgTirocinio;
	}

	public void setFlgTirocinio(Boolean flgTirocinio) {
		this.flgTirocinio = flgTirocinio;
	}

	@Type(type = "yes_no")
	@Column(name = "flg_sede_legale", length = 1)
	public Boolean getFlgSedeLegale() {
		return flgSedeLegale;
	}

	public void setFlgSedeLegale(Boolean flgSedeLegale) {
		this.flgSedeLegale = flgSedeLegale;
	}

	@Column(nullable = true, length = 255)
	public String getIndirizzo() {
		return indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_com", nullable = false)
	public DeComune getDeComune() {
		return deComune;
	}

	public void setDeComune(DeComune deComune) {
		this.deComune = deComune;
	}

	@Column(nullable = true, length = 5)
	public String getCap() {
		return cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}

	@Column(nullable = true, length = 50)
	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	@Column(nullable = true, length = 50)
	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idYgAdesioneAziendaSede == null) ? 0 : idYgAdesioneAziendaSede.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		YgAdesioneAziendaSede other = (YgAdesioneAziendaSede) obj;
		if (idYgAdesioneAziendaSede == null) {
			if (other.idYgAdesioneAziendaSede != null)
				return false;
		} else if (!idYgAdesioneAziendaSede.equals(other.idYgAdesioneAziendaSede))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "YgAdesioneAziendaSede [idYgAdesioneAziendaSede=" + idYgAdesioneAziendaSede + ", ygAdesioneAzienda="
				+ ygAdesioneAzienda + ", pfPrincipal=" + pfPrincipal + ", flgTirocinio=" + flgTirocinio
				+ ", flgSedeLegale=" + flgSedeLegale + ", indirizzo=" + indirizzo + ", deComune=" + deComune + ", cap="
				+ cap + ", telefono=" + telefono + ", fax=" + fax + "]";
	}
}
