package it.eng.myportal.entity.decodifiche;

import it.eng.myportal.entity.AbstractEntity;

import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "de_ambiente_sil", schema = "myportal")
@NamedQueries({ @NamedQuery(name = "findDeAmbienteSilValideByCpi", query = "SELECT a FROM DeAmbienteSil a WHERE a.cpi.codCpi = :codCpi AND a.dtInizioVal < :now AND :now < a.dtFineVal") })
public class DeAmbienteSil extends AbstractEntity implements java.io.Serializable {
	private static final long serialVersionUID = -8182079115613830127L;

	private Integer idAmbienteSil;
	private Integer prgAmbienteSil;
	private DeProvincia provinciaSil;
	private String descrizione;
	private DeCpi cpi;
	private Date dtInizioVal;
	private Date dtFineVal;

	@Id
	@SequenceGenerator(name = "de_ambiente_sil_id_ambiente_sil_seq", sequenceName = "de_ambiente_sil_id_ambiente_sil_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "de_ambiente_sil_id_ambiente_sil_seq")
	@Column(name = "id_ambiente_sil", unique = true, nullable = false)
	public Integer getIdAmbienteSil() {
		return idAmbienteSil;
	}

	public void setIdAmbienteSil(Integer idAmbienteSil) {
		this.idAmbienteSil = idAmbienteSil;
	}

	@Column(name = "prg_ambiente_sil", nullable = false)
	public Integer getPrgAmbienteSil() {
		return prgAmbienteSil;
	}

	public void setPrgAmbienteSil(Integer prgAmbienteSil) {
		this.prgAmbienteSil = prgAmbienteSil;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_provincia_sil", nullable = false)
	public DeProvincia getProvinciaSil() {
		return provinciaSil;
	}

	public void setProvinciaSil(DeProvincia provinciaSil) {
		this.provinciaSil = provinciaSil;
	}

	@Column(name = "descrizione", length = 100, nullable = false)
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_cpi", nullable = false)
	public DeCpi getCpi() {
		return cpi;
	}

	public void setCpi(DeCpi cpi) {
		this.cpi = cpi;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "dt_inizio_val", nullable = false)
	public Date getDtInizioVal() {
		return dtInizioVal;
	}

	public void setDtInizioVal(Date dtInizioVal) {
		this.dtInizioVal = dtInizioVal;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "dt_fine_val", nullable = false)
	public Date getDtFineVal() {
		return dtFineVal;
	}

	public void setDtFineVal(Date dtFineVal) {
		this.dtFineVal = dtFineVal;
	}
}
