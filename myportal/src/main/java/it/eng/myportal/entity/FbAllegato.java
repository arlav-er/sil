package it.eng.myportal.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import it.eng.myportal.entity.enums.OpzTipoAllegatoConvenzioneEnum;

@Entity
@Table(name = "fb_allegato", schema = "myportal")
@NamedQueries(value = {
		@NamedQuery(name = "findAllegatoByConvenzioneId", query = "SELECT allegato FROM FbAllegato allegato WHERE allegato.idFbConvenzione.idConvenzione = :convenzioneId") })
public class FbAllegato extends AbstractEntity implements Serializable {

	private static final long serialVersionUID = -9020755946405272093L;

	private Integer idAllegato;
	private FbConvenzione idFbConvenzione;
	private byte[] pdf;
	private String nomeFile;
	private String mimeFile;
	private OpzTipoAllegatoConvenzioneEnum tipoAllegato;

	public FbAllegato() {

	}

	@Id
	@SequenceGenerator(name = "fb_allegato_id_fb_allegato_seq", sequenceName = "fb_allegato_id_fb_allegato_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fb_allegato_id_fb_allegato_seq")
	@Column(name = "id_fb_allegato", unique = true, nullable = false)
	public Integer getIdAllegato() {
		return idAllegato;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_convenzione", columnDefinition = "integer", nullable = false)
	public FbConvenzione getIdFbConvenzione() {
		return idFbConvenzione;
	}

	@Column(name = "pdf", nullable = false)
	public byte[] getPdf() {
		return pdf;
	}

	@Column(name = "nome_pdf")
	public String getNomeFile() {
		return nomeFile;
	}

	@Column(name = "mime_file")
	public String getMimeFile() {
		return mimeFile;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_allegato", length = 11)
	public OpzTipoAllegatoConvenzioneEnum getTipoAllegato() {
		return tipoAllegato;
	}

	public void setIdAllegato(Integer idAllegato) {
		this.idAllegato = idAllegato;
	}

	public void setIdFbConvenzione(FbConvenzione idFbConvenzione) {
		this.idFbConvenzione = idFbConvenzione;
	}

	public void setPdf(byte[] pdf) {
		this.pdf = pdf;
	}

	public void setNomeFile(String nomeFile) {
		this.nomeFile = nomeFile;
	}

	public void setMimeFile(String mimeFile) {
		this.mimeFile = mimeFile;
	}

	public void setTipoAllegato(OpzTipoAllegatoConvenzioneEnum tipoAllegato) {
		this.tipoAllegato = tipoAllegato;
	}

}
