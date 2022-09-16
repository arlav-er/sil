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
@Table(name = "de_contratto_sil", schema = "mycas")

@NamedQueries({
	@NamedQuery(name = "DeDeContrattoSilFindSuggestionValide", query = "select q from DeContrattoSil as q where UPPER(q.descrizione) like UPPER(:query) and CURRENT_DATE BETWEEN q.dtInizioVal and q.dtFineVal"),
	@NamedQuery(name="deContrattoSilFindSuggestionValideFlagIdoTrue", query = "select q from DeContrattoSil as q where UPPER(q.descrizione) like UPPER(:query) and CURRENT_DATE BETWEEN q.dtInizioVal and q.dtFineVal and q.flagIdo = TRUE order by q.descrizione"),
	@NamedQuery(name="deContrattoValideByFlagIdo", query= "select dcs from DeContrattoSil as dcs where dcs.flagIdo = :flagIdo and CURRENT_DATE BETWEEN dcs.dtInizioVal and dcs.dtFineVal order by dcs.descrizione")
})
public class DeContrattoSil extends AbstractDecodeEntity implements java.io.Serializable, Comparable<DeContrattoSil> {
	private static final long serialVersionUID = -5575927042828290545L;

	private String codContrattoSil;
	private String descrizione;
	private Boolean flagTirocini;
	private int occurencyRicerca;
	private Boolean flagIdo;

	@Id
	@Column(name = "cod_contratto_sil", unique = true, nullable = false, length = 6)
	public String getCodContrattoSil() {
		return codContrattoSil;
	}

	public void setCodContrattoSil(String codContrattoSil) {
		this.codContrattoSil = codContrattoSil;
	}

	@Column(name = "descrizione", nullable = false, length = 200)
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Column(name = "flag_tirocini")
	public Boolean getFlagTirocini() {
		return flagTirocini;
	}

	public void setFlagTirocini(Boolean flagTirocini) {
		this.flagTirocini = flagTirocini;
	}
	
	
	@Column(name = "flag_Ido")
	public Boolean getFlagIdo() {
		return flagIdo;
	}

	public void setFlagIdo(Boolean flagIdo) {
		this.flagIdo = flagIdo;
	}

	@Transient
	public int getOccurencyRicerca() {
		return occurencyRicerca;
	}

	public void setOccurencyRicerca(int occurencyRicerca) {
		this.occurencyRicerca = occurencyRicerca;
	}

	@Override
	public int compareTo(DeContrattoSil o) {
		int compareQuantity = o.getOccurencyRicerca();
		// ascending order
		return this.occurencyRicerca - compareQuantity;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || this.getCodContrattoSil() == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof DeContrattoSil)) {
			return false;
		} else {
			return this.getCodContrattoSil().equalsIgnoreCase(((DeContrattoSil) obj).getCodContrattoSil());
		}
	}

	@Override
	public int hashCode() {
		return getCodContrattoSil().hashCode();
	}
}
