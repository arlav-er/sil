package it.eng.myportal.entity.decodifiche;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;


@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_sezione_info", schema = "myportal")
public class DeSezioneInfo extends AbstractDecodeEntity implements java.io.Serializable {
	
	private static final long serialVersionUID = 6215788653901199159L;
	private String codSezioneInfo;
	private String descrizione;
	private String testo;

	public DeSezioneInfo() {
	}

	public DeSezioneInfo(String codSezioneInfo, String descrizione, String testo) {
		super();
		this.codSezioneInfo = codSezioneInfo;
		this.descrizione = descrizione;
		this.testo = testo;
	}

	@Id
	@Column(name = "cod_sezione_info", unique = true, nullable = false, length = 32)
	public String getCodSezioneInfo() {
		return codSezioneInfo;
	}

	public void setCodSezioneInfo(String codSezioneInfo) {
		this.codSezioneInfo = codSezioneInfo;
	}

	@Column(name = "descrizione",length = 100,nullable=false)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	
	@Column(name = "testo", length = 18000, nullable=false)
	public String getTesto() {
		return testo;
	}

	public void setTesto(String testo) {
		this.testo = testo;
	}
}
