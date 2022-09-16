package it.eng.myportal.entity.decodifiche;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;

@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_genere", schema = "mycas")
public class DeGenere extends AbstractDecodeEntity implements java.io.Serializable {
	
	private static final long serialVersionUID = 5437622641961006243L;
	
	private String codGenere;
	private String descrizione;
	
	@Transient
	private int occurencyRicerca;

	public DeGenere() {
	}

	public DeGenere(String codGenere, String descrizione, Date dtInizioVal, Date dtFineVal) {
		super(dtInizioVal, dtFineVal);
		this.codGenere = codGenere;
		this.descrizione = descrizione;
	}

	@Id
	@Column(name = "cod_genere", unique = true, nullable = false, length = 1)
	public String getCodGenere() {
		return this.codGenere;
	}

	public void setCodGenere(String codGenere) {
		this.codGenere = codGenere;
	}

	@Column(name = "descrizione", nullable = false, length = 20)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	
}
