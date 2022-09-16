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
@Table(name = "de_stato_adesione", schema = "myportal")
public class DeStatoAdesione extends AbstractDecodeEntity implements java.io.Serializable {
	
	private static final long serialVersionUID = 5437622641961006243L;
	
	private String codStatoAdesione;
	private String descrizione;
	private String codMonoClasse;
	
	@Transient
	private int occurencyRicerca;

	public DeStatoAdesione() {
	}

	public DeStatoAdesione(String codStatoAdesione, String descrizione, Date dtInizioVal, Date dtFineVal) {
		super(dtInizioVal, dtFineVal);
		this.codStatoAdesione = codStatoAdesione;
		this.descrizione = descrizione;
	}

	@Id
	@Column(name = "cod_stato_adesione", unique = true, nullable = false, length = 8)
	public String getCodStatoAdesione() {
		return this.codStatoAdesione;
	}

	public void setCodStatoAdesione(String codStatoAdesione) {
		this.codStatoAdesione = codStatoAdesione;
	}

	@Column(name = "descrizione", nullable = false, length = 100)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Column(name = "cod_mono_classe", nullable = false, length = 1)
	public String getCodMonoClasse() {
		return codMonoClasse;
	}

	public void setCodMonoClasse(String codMonoClasse) {
		this.codMonoClasse = codMonoClasse;
	}
	
}
