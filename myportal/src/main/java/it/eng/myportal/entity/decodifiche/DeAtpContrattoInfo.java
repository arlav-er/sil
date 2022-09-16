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
@Table(name = "de_atp_contratto_info", schema = "myportal")
public class DeAtpContrattoInfo extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 552425L;
	private String codAtpContratto;
	private DeAtpContratto deAtpContratto;
	private String titoloInfo;
	private String descrizioneInfo;

	public DeAtpContrattoInfo() {
	}

	public DeAtpContrattoInfo(DeAtpContratto deAtpContratto, Date dtInizioVal, Date dtFineVal) {
		super(dtInizioVal, dtFineVal);
		this.deAtpContratto = deAtpContratto;
	}

	@GenericGenerator(name = "generator", strategy = "foreign", parameters = @Parameter(name = "property", value = "deAtpContratto"))
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "cod_atp_contratto", unique = true, nullable = false, length = 6)
	public String getCodAtpContratto() {
		return this.codAtpContratto;
	}

	public void setCodAtpContratto(String codAtpContratto) {
		this.codAtpContratto = codAtpContratto;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn
	public DeAtpContratto getDeAtpContratto() {
		return this.deAtpContratto;
	}

	public void setDeAtpContratto(DeAtpContratto deAtpContratto) {
		this.deAtpContratto = deAtpContratto;
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
