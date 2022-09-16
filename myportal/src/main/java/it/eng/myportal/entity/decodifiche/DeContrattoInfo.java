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
@Table(name = "de_contratto_info", schema = "myportal")
public class DeContrattoInfo extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 552425L;
	private String codContratto;
	private DeContratto deContratto;
	private String titoloInfo;
	private String descrizioneInfo;

	public DeContrattoInfo() {
	}

	public DeContrattoInfo(DeContratto deContratto, Date dtInizioVal, Date dtFineVal) {
		super(dtInizioVal, dtFineVal);
		this.deContratto = deContratto;

	}

	@GenericGenerator(name = "generator", strategy = "foreign", parameters = @Parameter(name = "property", value = "deContratto"))
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "cod_contratto", unique = true, nullable = false, length = 6)
	public String getCodContratto() {
		return this.codContratto;
	}

	public void setCodContratto(String codContratto) {
		this.codContratto = codContratto;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn
	public DeContratto getDeContratto() {
		return this.deContratto;
	}

	public void setDeContratto(DeContratto deContratto) {
		this.deContratto = deContratto;
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
