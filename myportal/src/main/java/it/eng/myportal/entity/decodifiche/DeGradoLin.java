package it.eng.myportal.entity.decodifiche;

// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import it.eng.myportal.entity.AbstractDecodeEntity;
import it.eng.myportal.entity.decodifiche.sil.DeGradoLinSil;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CacheConcurrencyStrategy;

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_grado_lin", schema = "mycas")
public class DeGradoLin extends AbstractDecodeEntity implements java.io.Serializable, Comparable<DeGradoLin> {
	private static final long serialVersionUID = 95994L;
	private String codGrado;
	private String descrizione;
	private Integer numOrdine;
	private Set<DeGradoLinSil> deGradoLinSilList;

	public DeGradoLin() {
	}

	public DeGradoLin(String codGrado, String descrizione, Date dtInizioVal, Date dtFineVal) {
		super(dtInizioVal, dtFineVal);
		this.codGrado = codGrado;
		this.descrizione = descrizione;
	}

	@Id
	@Column(name = "cod_grado_lin", unique = true, nullable = false, length = 2)
	public String getCodGrado() {
		return this.codGrado;
	}

	public void setCodGrado(String codGrado) {
		this.codGrado = codGrado;
	}

	@Column(name = "descrizione", nullable = false, length = 60)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Column(name = "num_ordine", nullable = true)
	public Integer getNumOrdine() {
		return numOrdine;
	}

	public void setNumOrdine(Integer numOrdine) {
		this.numOrdine = numOrdine;
	}

	@Override
	public int compareTo(DeGradoLin o) {
		return this.getNumOrdine().compareTo(o.getNumOrdine());
	}

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(schema = "myportal", name = "ma_grado_lin", uniqueConstraints = { @UniqueConstraint(columnNames = {
			"cod_grado_lin", "cod_grado_lin_sil" }) }, joinColumns = { @JoinColumn(name = "cod_grado_lin", referencedColumnName = "cod_grado_lin", nullable = false) }, inverseJoinColumns = { @JoinColumn(name = "cod_grado_lin_sil", referencedColumnName = "cod_grado_lin_sil", nullable = false) })
	public Set<DeGradoLinSil> getDeGradoLinSilList() {
		return deGradoLinSilList;
	}

	public void setDeGradoLinSilList(Set<DeGradoLinSil> deGradoLinSilList) {
		this.deGradoLinSilList = deGradoLinSilList;
	}


	@Override
	public boolean equals(Object obj) {
		if (obj == null || this.getCodGrado() == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof DeGradoLin)) {
			return false;
		} else {
			return this.getCodGrado().equalsIgnoreCase(((DeGradoLin) obj).getCodGrado());
		}
	}

	@Override
	public int hashCode() {
		return getCodGrado().hashCode();
	}
}
