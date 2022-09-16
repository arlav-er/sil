package it.eng.myportal.entity;

// Generated Apr 20, 2012 10:40:45 AM by Hibernate Tools 3.4.0.CR1
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author Girotti S.
 */
@Entity
@Table(name = "ac_allegato", schema = "myportal")
public class AcAllegato extends AbstractEntity implements java.io.Serializable {
	private static final long serialVersionUID = 257731L;

	private Integer idAcAllegato;
	private AcCandidatura acCandidatura;
	private String filename;
	private byte[] contenuto;

	public AcAllegato() {
	}

	@Column(name = "contenuto")
	public byte[] getContenuto() {
		return this.contenuto;
	}

	@Column(name = "filename", nullable = false)
	public String getFilename() {
		return this.filename;
	}

	@Id
	@SequenceGenerator(name = "ac_allegato_id_ac_allegato_seq", sequenceName = "ac_allegato_id_ac_allegato_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ac_allegato_id_ac_allegato_seq")
	@Column(name = "id_ac_allegato", unique = true, nullable = false)
	public Integer getIdAcAllegato() {
		return this.idAcAllegato;
	}

	public void setContenuto(byte[] contenuto) {
		this.contenuto = contenuto;
	}

	public void setFilename(String address) {
		this.filename = address;
	}

	public void setIdAcAllegato(Integer idWsEndpoint) {
		this.idAcAllegato = idWsEndpoint;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_ac_candidatura", nullable = false)
	public AcCandidatura getAcCandidatura() {
		return acCandidatura;
	}

	public void setAcCandidatura(AcCandidatura acCandidatura) {
		this.acCandidatura = acCandidatura;
	}
}