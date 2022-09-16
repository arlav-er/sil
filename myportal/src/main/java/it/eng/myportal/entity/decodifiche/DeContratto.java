package it.eng.myportal.entity.decodifiche;

// Generated Mar 1, 2012 12:28:41 PM by Rodi Tools 3.4.0.BLBLBL
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import it.eng.myportal.entity.AbstractDecodeEntity;

@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_contratto", schema = "mycas")
public class DeContratto extends AbstractDecodeEntity implements java.io.Serializable, Comparable<DeContratto> {
	private static final long serialVersionUID = 255276L;
	private String codContratto;
	private String descrizione;
	private Boolean flagAtipico;

	@Transient
	private int occurencyRicerca;
	
	private DeContrattoInfo deContrattoInfo;

	public DeContratto() {
	}

	public DeContratto(String codContratto, String descrizione, Date dtInizioVal, Date dtFineVal) {
		super(dtInizioVal, dtFineVal);
		this.codContratto = codContratto;
		this.descrizione = descrizione;
		
		this.occurencyRicerca = 0;
	}

	@Id
	@Column(name = "cod_contratto", unique = true, nullable = false, length = 6)
	public String getCodContratto() {
		return this.codContratto;
	}

	public void setCodContratto(String codContratto) {
		this.codContratto = codContratto;
	}

	@Column(name = "descrizione", nullable = false, length = 200)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "deContratto")
	public DeContrattoInfo getDeContrattoInfo() {
		return this.deContrattoInfo;
	}

	public void setDeContrattoInfo(DeContrattoInfo deContrattoInfo) {
		this.deContrattoInfo = deContrattoInfo;
	}

	@Type(type = "yes_no")
	@Column(name = "flag_atipico", nullable = false, length = 1)
	public Boolean getFlagAtipico() {
		return this.flagAtipico;
	}

	public void setFlagAtipico(Boolean flagAtipico) {
		this.flagAtipico = flagAtipico;
	}
	@Transient
	public int getOccurencyRicerca() {
		return occurencyRicerca;
	}
	@Transient
	public void setOccurencyRicerca(int occurencyRicerca) {
		this.occurencyRicerca = occurencyRicerca;
	}
	
	@Override
	public int compareTo(DeContratto o) {
		int compareQuantity = o.getOccurencyRicerca();
		//ascending order
		return this.occurencyRicerca - compareQuantity;
	}
	
	public int compare(DeContratto obj1, DeContratto obj2) {
		Integer p1 = obj1.getOccurencyRicerca();
		Integer p2 = obj2.getOccurencyRicerca();

		if (p1 > p2) {
			return 1;
		}
		else if (p1 < p2) {
			return -1;
		}
		else {
			return 0;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || this.getCodContratto() == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof DeContratto)) {
			return false;
		} else {
			return this.getCodContratto().equalsIgnoreCase(((DeContratto) obj).getCodContratto());
		}
	}

	@Override
	public int hashCode() {
		return getCodContratto().hashCode();
	}
}
