package it.eng.myportal.entity.decodifiche;

// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import it.eng.myportal.entity.AbstractDecodeEntity;

@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_mansione_info", schema = "myportal")
public class DeMansioneInfo extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 741130L;
	private String codMansione;
	private DeMansione deMansione;
	private String titoloInfo;
	private String descrizioneInfo;

	public DeMansioneInfo() {
	}

	public DeMansioneInfo(DeMansione deMansione, Date dtInizioVal, Date dtFineVal) {
		super(dtInizioVal, dtFineVal);
		this.deMansione = deMansione;

	}

	@GenericGenerator(name = "generator", strategy = "foreign", parameters = @Parameter(name = "property", value = "deMansione"))
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "cod_mansione", unique = true, nullable = false, length = 6)
	public String getCodMansione() {
		return this.codMansione;
	}

	public void setCodMansione(String codMansione) {
		this.codMansione = codMansione;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn
	public DeMansione getDeMansione() {
		return this.deMansione;
	}

	public void setDeMansione(DeMansione deMansione) {
		this.deMansione = deMansione;
	}

	@Column(name = "titolo_info", length = 100)
	public String getTitoloInfo() {
		return this.titoloInfo;
	}

	public void setTitoloInfo(String titoloInfo) {
		this.titoloInfo = titoloInfo;
	}

	@Column(name = "descrizione_info", length = 10000)
	public String getDescrizioneInfo() {
		return this.descrizioneInfo;
	}

	public void setDescrizioneInfo(String descrizioneInfo) {
		this.descrizioneInfo = descrizioneInfo;
	}

}
