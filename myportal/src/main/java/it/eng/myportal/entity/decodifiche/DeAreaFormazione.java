package it.eng.myportal.entity.decodifiche;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_area_formazione", schema = "myportal")
@NamedQueries({
		@NamedQuery(name = "DeAreaFormazioneFindSuggestionValide", query = "select q from DeAreaFormazione as q where UPPER(q.descrizione) like UPPER(:query) and CURRENT_DATE BETWEEN q.dtInizioVal and q.dtFineVal"), })
public class DeAreaFormazione extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 3926743371L;
	private String codAreaFormazione;
	private String descrizione;

	public DeAreaFormazione() {
	}

	@Id
	@Column(name = "cod_area_formazione", unique = true, nullable = false, length = 8)
	public String getCodAreaFormazione() {
		return this.codAreaFormazione;
	}

	public void setCodAreaFormazione(String AreaTitolo) {
		this.codAreaFormazione = AreaTitolo;
	}

	@Column(name = "descrizione", nullable = false, length = 150)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || this.getCodAreaFormazione() == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof DeAreaFormazione)) {
			return false;
		} else {
			return this.getCodAreaFormazione().equalsIgnoreCase(((DeAreaFormazione) obj).getCodAreaFormazione());
		}
	}

	@Override
	public int hashCode() {
		return getCodAreaFormazione().hashCode();
	}
}
