package it.eng.myportal.entity.decodifiche;

// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;
import it.eng.myportal.entity.CvAgevolazione;
import it.eng.myportal.entity.VaAgevolazione;

@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_agevolazione", schema = "myportal")
public class DeAgevolazione extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 392371L;
	private String codAgevolazione;
	private String descrizione;
	private Set<VaAgevolazione> vaAgevolaziones = new HashSet<VaAgevolazione>(0);
	private Set<CvAgevolazione> cvAgevolaziones = new HashSet<CvAgevolazione>(0);

	public DeAgevolazione() {
	}

	@Id
	@Column(name = "cod_agevolazione", unique = true, nullable = false, length = 8)
	public String getCodAgevolazione() {
		return this.codAgevolazione;
	}

	public void setCodAgevolazione(String codAgevolazione) {
		this.codAgevolazione = codAgevolazione;
	}

	@Column(name = "descrizione", nullable = false, length = 150)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "deAgevolazione")
	public Set<VaAgevolazione> getVaAgevolaziones() {
		return this.vaAgevolaziones;
	}

	public void setVaAgevolaziones(Set<VaAgevolazione> vaAgevolaziones) {
		this.vaAgevolaziones = vaAgevolaziones;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "deAgevolazione")
	public Set<CvAgevolazione> getCvAgevolaziones() {
		return this.cvAgevolaziones;
	}

	public void setCvAgevolaziones(Set<CvAgevolazione> cvAgevolaziones) {
		this.cvAgevolaziones = cvAgevolaziones;
	}
}
