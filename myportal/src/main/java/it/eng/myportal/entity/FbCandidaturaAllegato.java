package it.eng.myportal.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
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

@Entity
@Table(name = "fb_candidatura_allegato", schema = "myportal")
@NamedQueries(value = {
		@NamedQuery(name = "findAllegatoByCandidaturaId", query = "SELECT allegato FROM FbCandidaturaAllegato allegato WHERE allegato.idCandidatura.idFbCandidatura = :idCandidatura") })
public class FbCandidaturaAllegato extends AbstractEntity implements Serializable {

	private static final long serialVersionUID = 1379090592218039902L;

	private Integer idAllegato;
	private FbCandidatura idCandidatura;
	private byte[] dataFile;
	private String nomeFile;
	private String mimeFile;

	public FbCandidaturaAllegato() {

	}

	@Id
	@SequenceGenerator(name = "fb_candidatura_allegato_id_fb_candidatura_allegato_seq", sequenceName = "fb_candidatura_allegato_id_fb_candidatura_allegato_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fb_candidatura_allegato_id_fb_candidatura_allegato_seq")
	@Column(name = "id_fb_candidatura_allegato", unique = true, nullable = false)
	public Integer getIdAllegato() {
		return idAllegato;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_fb_candidatura")
	public FbCandidatura getIdCandidatura() {
		return idCandidatura;
	}

	@Column(name = "dati_file")
	public byte[] getDataFile() {
		return dataFile;
	}

	@Column(name = "nome_file")
	public String getNomeFile() {
		return nomeFile;
	}

	@Column(name = "mime_file")
	public String getMimeFile() {
		return mimeFile;
	}

	public void setIdAllegato(Integer idAllegato) {
		this.idAllegato = idAllegato;
	}

	public void setIdCandidatura(FbCandidatura idFbCandidatura) {
		this.idCandidatura = idFbCandidatura;
	}

	public void setNomeFile(String nomeFile) {
		this.nomeFile = nomeFile;
	}

	public void setMimeFile(String mimeFile) {
		this.mimeFile = mimeFile;
	}

	public void setDataFile(byte[] dataFile) {
		this.dataFile = dataFile;
	}
}
