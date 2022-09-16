package it.eng.myportal.entity;

// Generated Apr 20, 2012 10:40:45 AM by Hibernate Tools 3.4.0.CR1
import java.util.Date;

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
@Table(name = "cv_allegato", schema = "myportal")
public class CvAllegato extends AbstractEntity implements java.io.Serializable {
	private static final long serialVersionUID = 257731L;

	private Integer idCvAllegato;
	private CvDatiPersonali cvDatiPersonali;
	private String filename;
	private byte[] contenuto;
	
	
	public CvAllegato() {
	}

	public CvAllegato(PfPrincipal pfPrincipalIns, PfPrincipal pfPrincipalMod,
			Date dtmIns, Date dtmMod, Integer idCvAllegato, String filename,
			CvDatiPersonali cvDatiPersonali) {
		super(pfPrincipalIns, pfPrincipalMod, dtmIns, dtmMod);
		this.idCvAllegato = idCvAllegato;
		this.filename = filename;
		this.cvDatiPersonali = cvDatiPersonali;
	}

	@Column(name = "contenuto")
	public byte[] getContenuto() {
		return this.contenuto;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cv_dati_personali", nullable = false)
	public CvDatiPersonali getCvDatiPersonali() {
		return this.cvDatiPersonali;
	}

	@Column(name = "filename", nullable = false)
	public String getFilename() {
		return this.filename;
	}

	@Id
	@SequenceGenerator(name = "cv_allegato_id_cv_allegato_seq", sequenceName = "cv_allegato_id_cv_allegato_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cv_allegato_id_cv_allegato_seq")
	@Column(name = "id_cv_allegato", unique = true, nullable = false)
	public Integer getIdCvAllegato() {
		return this.idCvAllegato;
	}

	public void setContenuto(byte[] contenuto) {
		this.contenuto = contenuto;
	}

	public void setCvDatiPersonali(CvDatiPersonali cvDatiPersonali) {
		this.cvDatiPersonali = cvDatiPersonali;
	}
	
	public void setFilename(String _filename) {
		this.filename = _filename;
	}

	public void setIdCvAllegato(Integer _idCvAllegato) {
		this.idCvAllegato = _idCvAllegato;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idCvAllegato == null) ? 0 : idCvAllegato.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CvAllegato other = (CvAllegato) obj;
		if (idCvAllegato == null) {
			if (other.idCvAllegato != null)
				return false;
		} else if (!idCvAllegato.equals(other.idCvAllegato))
			return false;
		return true;
	}

}