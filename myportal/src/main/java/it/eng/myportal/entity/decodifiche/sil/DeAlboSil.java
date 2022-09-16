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
@Table(name = "de_albo_sil", schema = "mycas")
@NamedQueries({
	@NamedQuery(name = "DeAlboSilFindSuggestionValide", query = "select q from DeAlboSil as q where UPPER(q.descrizione) like UPPER(:query) and CURRENT_DATE BETWEEN q.dtInizioVal and q.dtFineVal"),
})
public class DeAlboSil extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 6126738815593268126L;

	private String codAlboSil;
	private String descrizione;

	@Id
	@Column(name = "cod_albo_sil", unique = true, nullable = false, length = 8)
	public String getCodAlboSil() {
		return codAlboSil;
	}

	public void setCodAlboSil(String codAlboSil) {
		this.codAlboSil = codAlboSil;
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
		if (obj == null || this.getCodAlboSil() == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof DeAlboSil)) {
			return false;
		} else {
			return this.getCodAlboSil().equalsIgnoreCase(((DeAlboSil) obj).getCodAlboSil());
		}
	}

	@Override
	public int hashCode() {
		return getCodAlboSil().hashCode();
	}
}
