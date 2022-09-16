package it.eng.myportal.entity.decodifiche;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;
import it.eng.myportal.entity.MaPatentino;

/**
 * 
 * @author Rodi A.
 *
 */
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_patentino", schema = "myportal")
@NamedQueries({
	@NamedQuery(name = "DePatentinoFindSuggestionValide", query = "select q from DePatentino as q where UPPER(q.descrizione) like UPPER(:query) and CURRENT_DATE BETWEEN q.dtInizioVal and q.dtFineVal"),
})
public class DePatentino extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 578815L;

	private String codPatentino;
	private String descrizione;
	private MaPatentino maPatentino;

	public DePatentino() {
	}

	public DePatentino(String codPatentino, Date dtInizioVal, Date dtFineVal) {
		super(dtInizioVal, dtFineVal);
		this.codPatentino = codPatentino;

	}

	@Id
	@Column(name = "cod_patentino", unique = true, nullable = false, length = 8)
	public String getCodPatentino() {
		return this.codPatentino;
	}

	public void setCodPatentino(String codPatentino) {
		this.codPatentino = codPatentino;
	}

	@Column(name = "descrizione", length = 100)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "patentino")
	public MaPatentino getMaPatentino() {
		return maPatentino;
	}

	public void setMaPatentino(MaPatentino maPatentino) {
		this.maPatentino = maPatentino;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codPatentino == null) ? 0 : codPatentino.hashCode());
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
		DePatentino other = (DePatentino) obj;
		if (codPatentino == null) {
			if (other.codPatentino != null)
				return false;
		} else if (!codPatentino.equals(other.codPatentino))
			return false;
		return true;
	}

}
