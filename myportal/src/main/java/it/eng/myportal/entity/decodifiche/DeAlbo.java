package it.eng.myportal.entity.decodifiche;

// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import it.eng.myportal.entity.AbstractDecodeEntity;

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_albo", schema = "myportal")
@NamedQueries({
	@NamedQuery(name = "DeAlboFindSuggestionValide", query = "select q from DeAlbo as q where UPPER(q.descrizione) like UPPER(:query) and CURRENT_DATE BETWEEN q.dtInizioVal and q.dtFineVal"),
})
public class DeAlbo extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 84004L;
	private String codAlbo;
	private String descrizione;
	private Boolean flagMin;

	@Type(type = "yes_no")
	@Column(name = "flag_min", nullable = false, length = 1)
	public Boolean getFlagMin() {
		return flagMin;
	}

	public void setFlagMin(Boolean flagMin) {
		this.flagMin = flagMin;
	}

	public DeAlbo() {
	}

	public DeAlbo(String codAlbo, Date dtInizioVal, Date dtFineVal) {
		super(dtInizioVal, dtFineVal);
		this.codAlbo = codAlbo;

	}

	@Id
	@Column(name = "cod_albo", unique = true, nullable = false, length = 8)
	public String getCodAlbo() {
		return this.codAlbo;
	}

	public void setCodAlbo(String codAlbo) {
		this.codAlbo = codAlbo;
	}

	@Column(name = "descrizione", length = 100)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

}
