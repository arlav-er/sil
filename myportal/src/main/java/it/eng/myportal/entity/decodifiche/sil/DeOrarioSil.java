package it.eng.myportal.entity.decodifiche.sil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_orario_sil", schema = "mycas")

@NamedQueries({
	@NamedQuery(name = "DeOrarioSilFindSuggestionValide", query = "select q from DeOrarioSil as q where UPPER(q.descrizione) like UPPER(:query) and CURRENT_DATE BETWEEN q.dtInizioVal and q.dtFineVal"), 
	@NamedQuery(name = "deOrarioSilFindSuggestionValideFlagIdoTrue", query = "select q from DeOrarioSil as q where UPPER(q.descrizioneIdo) like UPPER(:query) and CURRENT_DATE BETWEEN q.dtInizioVal and q.dtFineVal and q.flgIdo = TRUE order by q.descrizioneIdo"),
	@NamedQuery(name = "deOrarioValideByFlagIdo", query= "select dos from DeOrarioSil as dos where dos.flgIdo = :flagIdo and CURRENT_DATE BETWEEN dos.dtInizioVal and dos.dtFineVal order by dos.descrizione"),
	@NamedQuery(name = "deOrarioValideSILByFlagIdo", query= "select distinct dos from DeOrarioSil as dos where dos.flgIdo = :flagIdo and dos.tipoOrario =:tipoOrario and CURRENT_DATE BETWEEN dos.dtInizioVal and dos.dtFineVal")
})
public class DeOrarioSil extends AbstractDecodeEntity implements java.io.Serializable, Comparable<DeOrarioSil> {
	private static final long serialVersionUID = -1596735150159941907L;

	private String codOrarioSil;
	private String descrizione;
	private int occurencyRicerca;
	private Boolean flgIdo;
	private String descrizioneIdo;
	private String tipoOrario;

	@Id
	@Column(name = "cod_orario_sil", unique = true, nullable = false, length = 8)
	public String getCodOrarioSil() {
		return codOrarioSil;
	}

	public void setCodOrarioSil(String codOrarioSil) {
		this.codOrarioSil = codOrarioSil;
	}

	@Column(name = "descrizione", nullable = false, length = 200)
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Transient
	public int getOccurencyRicerca() {
		return occurencyRicerca;
	}

	public void setOccurencyRicerca(int occurencyRicerca) {
		this.occurencyRicerca = occurencyRicerca;
	}

	@Column(name = "tipo_orario", nullable = true, length = 1)
	public String getTipoOrario() {
		return tipoOrario;
	}

	public void setTipoOrario(String tipoOrario) {
		this.tipoOrario = tipoOrario;
	}
	
	@Override
	public int compareTo(DeOrarioSil o) {
		int compareQuantity = o.getOccurencyRicerca();
		// ascending order
		return this.occurencyRicerca - compareQuantity;
	}

	@Column(name = "flag_ido")
	public Boolean getFlgIdo() {
		return flgIdo;
	}

	public void setFlgIdo(Boolean flgIdo) {
		this.flgIdo = flgIdo;
	}

	@Column(name = "descrizione_ido")
	public String getDescrizioneIdo() {
		return descrizioneIdo;
	}

	public void setDescrizioneIdo(String descrizioneIdo) {
		this.descrizioneIdo = descrizioneIdo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codOrarioSil == null) ? 0 : codOrarioSil.hashCode());
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
		DeOrarioSil other = (DeOrarioSil) obj;
		if (codOrarioSil == null) {
			if (other.codOrarioSil != null)
				return false;
		} else if (!codOrarioSil.equals(other.codOrarioSil))
			return false;
		return true;
	}

}
