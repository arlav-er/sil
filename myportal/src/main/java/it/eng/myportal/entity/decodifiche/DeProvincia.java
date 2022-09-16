package it.eng.myportal.entity.decodifiche;

// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import it.eng.myportal.entity.AbstractDecodeEntity;
import it.eng.myportal.entity.Provincia;
import it.eng.myportal.entity.WsEndpoint;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_provincia", schema = "myportal")
@NamedQueries({ @NamedQuery(name = "findProvinciaLike", query = "select d from DeProvincia d where UPPER(d.denominazione) like UPPER(:codLike)"), })
public class DeProvincia extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 836558L;
	private String codProvincia;
	private Set<DeComune> deComunes = new HashSet<DeComune>(0);
	private String denominazione;
	private DeRegione deRegione;
	private String destinatarioSare;
	private String faxRichiestaSare;
	private Boolean flagCapoluogo;

	private Set<Provincia> provincias = new HashSet<Provincia>(0);
	private String targa;
	private Set<WsEndpoint> wsEndpoints = new HashSet<WsEndpoint>(0);

	public DeProvincia() {
	}

	public DeProvincia(String codProvincia, DeRegione deRegione, String denominazione, String targa, Date dtInizioVal,
			Date dtFineVal) {
		super(dtInizioVal, dtFineVal);
		this.codProvincia = codProvincia;
		this.deRegione = deRegione;
		this.denominazione = denominazione;
		this.targa = targa;

	}

	@Id
	@Column(name = "cod_provincia", unique = true, length = 8)
	public String getCodProvincia() {
		return this.codProvincia;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "deProvincia")
	public Set<DeComune> getDeComunes() {
		return this.deComunes;
	}

	@Column(name = "denominazione", nullable = false, length = 100)
	public String getDenominazione() {
		return this.denominazione;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_regione", nullable = false)
	public DeRegione getDeRegione() {
		return this.deRegione;
	}

	@Column(name = "destinatario_sare", nullable = true, length = 500)
	public String getDestinatarioSare() {
		return this.destinatarioSare;
	}

	@Column(name = "fax_richiestasare", nullable = true, length = 20)
	public String getFaxRichiestaSare() {
		return faxRichiestaSare;
	}

	@Type(type = "yes_no")
	@Column(name = "flag_capoluogo", nullable = false, length = 1)
	public Boolean getFlagCapoluogo() {
		return flagCapoluogo;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "deProvincia")
	public Set<Provincia> getProvincias() {
		return this.provincias;
	}

	@Column(name = "targa", nullable = false, length = 2)
	public String getTarga() {
		return this.targa;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "deProvincia")
	public Set<WsEndpoint> getWsEndpoints() {
		return wsEndpoints;
	}

	public void setCodProvincia(String codProvincia) {
		this.codProvincia = codProvincia;
	}

	public void setDeComunes(Set<DeComune> deComunes) {
		this.deComunes = deComunes;
	}

	public void setDenominazione(String denominazione) {
		this.denominazione = denominazione;
	}

	public void setDeRegione(DeRegione deRegione) {
		this.deRegione = deRegione;
	}

	public void setDestinatarioSare(String strDestinatarioSare) {
		this.destinatarioSare = strDestinatarioSare;
	}

	public void setFaxRichiestaSare(String faxRixhiestaSare) {
		this.faxRichiestaSare = faxRixhiestaSare;
	}

	public void setFlagCapoluogo(Boolean flagCapoluogo) {
		this.flagCapoluogo = flagCapoluogo;
	}

	public void setProvincias(Set<Provincia> provincias) {
		this.provincias = provincias;
	}

	public void setTarga(String targa) {
		this.targa = targa;
	}

	public void setWsEndpoints(Set<WsEndpoint> wsEndpoints) {
		this.wsEndpoints = wsEndpoints;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || this.getCodProvincia() == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof DeProvincia)) {
			return false;
		} else {
			return this.getCodProvincia().equalsIgnoreCase(((DeProvincia) obj).getCodProvincia());
		}
	}

	@Override
	public int hashCode() {
		return getCodProvincia().hashCode();
	}
}
