package it.eng.myportal.entity.decodifiche;

// Generated 31-lug-2012 16.32.42 by Hibernate Tools 3.4.0.CR1
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;

@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_rapporto_lavoro", schema = "myportal")
public class DeRapportoLavoro extends AbstractDecodeEntity implements
		java.io.Serializable {
	private static final long serialVersionUID = 600234L;
	private String codRapportoLavoro;
	private String descrizione;
	
	public DeRapportoLavoro() {
	}

	public DeRapportoLavoro(String codRapportoLavoro, String descrizione,
			Date dtInizioVal, Date dtFineVal) {
		super(dtInizioVal, dtFineVal);
		this.codRapportoLavoro = codRapportoLavoro;
		this.descrizione = descrizione;
	}

	@Id
	@Column(name = "cod_rapporto_lavoro", unique = true, nullable = false, length = 8)
	public String getCodRapportoLavoro() {
		return this.codRapportoLavoro;
	}

	public void setCodRapportoLavoro(String codRapportoLavoro) {
		this.codRapportoLavoro = codRapportoLavoro;
	}

	@Column(name = "descrizione", nullable = false, length = 200)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
		
}
