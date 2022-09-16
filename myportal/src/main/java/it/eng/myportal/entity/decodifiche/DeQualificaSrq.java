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

import it.eng.myportal.entity.AbstractDecodeEntity;

@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_qualifica_srq", schema = "myportal")
@NamedQueries({
@NamedQuery(name = "DeQualificaSrqFindSuggestion", query = "select q from DeQualificaSrq as q where UPPER(q.descrizione) like UPPER(:query) and CURRENT_DATE BETWEEN q.dtInizioVal and q.dtFineVal"),
@NamedQuery(name = "DeQualificaSrqFindSuggestionFigli", query = "select q from DeQualificaSrq as q where UPPER(q.descrizione) like UPPER(:query) and cod_padre != '0' and CURRENT_DATE BETWEEN q.dtInizioVal and q.dtFineVal")
})
public class DeQualificaSrq extends AbstractDecodeEntity implements
		java.io.Serializable {
	private static final long serialVersionUID = 744637L;
	private String codQualificaSrq;
	private String descrizione;
	private String codPadre;

	public DeQualificaSrq() {
	}

	public DeQualificaSrq(String codQualificaSrq, String descrizione,
			Date dtInizioVal, Date dtFineVal) {
		super(dtInizioVal, dtFineVal);
		this.codQualificaSrq = codQualificaSrq;
		this.descrizione = descrizione;

	}

	@Id
	@Column(name = "cod_qualifica_srq", unique = true, nullable = false, length = 8)
	public String getCodQualificaSrq() {
		return this.codQualificaSrq;
	}

	public void setCodQualificaSrq(String codQualificaSrq) {
		this.codQualificaSrq = codQualificaSrq;
	}

	@Column(name = "descrizione", nullable = false, length = 100)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Column(name = "cod_padre", length = 8)
	public String getCodPadre() {
		return this.codPadre;
	}

	public void setCodPadre(String codPadre) {
		this.codPadre = codPadre;
	}

}
