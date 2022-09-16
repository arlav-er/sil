package it.eng.myportal.entity.decodifiche;

import it.eng.myportal.entity.AbstractDecodeEntity;

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

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_area_titolo", schema = "myportal")
@NamedQueries({ @NamedQuery(name = "findValideByPadre", query = "select d from DeAreaTitolo d where d.dtFineVal >= (:parDate) AND d.dtInizioVal <= (:parDate) AND d.codGruppoAreaTitolo = (:parPadre) order by d.descrizione ") })
public class DeAreaTitolo extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 3926743371L;
	private String codAreaTitolo;
	private String descrizione;
	private DeGruppoAreaTitolo codGruppoAreaTitolo;
	private Set<DeAreaTitolo> deAreaTitoliFigli = new HashSet<DeAreaTitolo>(0);

	public DeAreaTitolo() {
	}

	@Id
	@Column(name = "cod_area_titolo", unique = true, nullable = false, length = 4)
	public String getCodAreaTitolo() {
		return this.codAreaTitolo;
	}

	public void setCodAreaTitolo(String AreaTitolo) {
		this.codAreaTitolo = AreaTitolo;
	}

	@Column(name = "descrizione", nullable = false, length = 150)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_gruppo_area_titolo", nullable = false)
	public DeGruppoAreaTitolo getCodGruppoAreaTitolo() {
		return codGruppoAreaTitolo;
	}

	public void setCodGruppoAreaTitolo(DeGruppoAreaTitolo deGruppoAreaTitolo) {
		this.codGruppoAreaTitolo = deGruppoAreaTitolo;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "codGruppoAreaTitolo")
	public Set<DeAreaTitolo> getDeAreaTitoli() {
		return this.deAreaTitoliFigli;
	}

	public void setDeAreaTitoli(Set<DeAreaTitolo> deAreaTitoli) {
		this.deAreaTitoliFigli = deAreaTitoli;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || this.getCodAreaTitolo() == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof DeAreaTitolo)) {
			return false;
		} else {
			return this.getCodAreaTitolo().equalsIgnoreCase(((DeAreaTitolo) obj).getCodAreaTitolo());
		}
	}

	@Override
	public int hashCode() {
		return getCodAreaTitolo().hashCode();
	}
}
