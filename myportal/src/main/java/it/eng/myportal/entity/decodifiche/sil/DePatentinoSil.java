package it.eng.myportal.entity.decodifiche.sil;

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
@Table(name = "de_patentino_sil", schema = "mycas")
@NamedQueries({
	@NamedQuery(name = "DePatentinoSilFindSuggestionValide", query = "select q from DePatentinoSil as q where UPPER(q.descrizione) like UPPER(:query) and CURRENT_DATE BETWEEN q.dtInizioVal and q.dtFineVal"),
})
public class DePatentinoSil extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = -4420276972768326638L;

	private String codPatentinoSil;
	private String descrizione;

	@Id
	@Column(name = "cod_patentino_sil", unique = true, nullable = false, length = 8)
	public String getCodPatentinoSil() {
		return codPatentinoSil;
	}

	public void setCodPatentinoSil(String codPatentinoSil) {
		this.codPatentinoSil = codPatentinoSil;
	}

	@Column(name = "descrizione", length = 100)
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || this.getCodPatentinoSil() == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof DePatentinoSil)) {
			return false;
		} else {
			return this.getCodPatentinoSil().equalsIgnoreCase(((DePatentinoSil) obj).getCodPatentinoSil());
		}
	}

	@Override
	public int hashCode() {
		return getCodPatentinoSil().hashCode();
	}

}
