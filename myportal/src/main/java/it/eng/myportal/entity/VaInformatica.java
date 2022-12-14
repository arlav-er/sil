package it.eng.myportal.entity;

// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * VaInformatica generated by hbm2java
 */
@Entity
@Table(name = "va_informatica", schema = "myportal")
public class VaInformatica extends AbstractEntity implements java.io.Serializable {
	private static final long serialVersionUID = 92894L;
	private String conoscenzaInfo;

	private Integer idVaDatiVacancy;
	private VaDatiVacancy vaDatiVacancy;

	public VaInformatica() {
	}

	@Column(name = "conoscenza_info", nullable = false, length = 5000)
	public String getConoscenzaInfo() {
		return this.conoscenzaInfo;
	}

	@GenericGenerator(name = "generator", strategy = "foreign", parameters = @Parameter(name = "property", value = "vaDatiVacancy"))
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "id_va_dati_vacancy", unique = true, nullable = false)
	public Integer getIdVaDatiVacancy() {
		return this.idVaDatiVacancy;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn
	public VaDatiVacancy getVaDatiVacancy() {
		return this.vaDatiVacancy;
	}

	public void setConoscenzaInfo(String conoscenzaInfo) {
		this.conoscenzaInfo = conoscenzaInfo;
	}

	public void setIdVaDatiVacancy(Integer idVaDatiVacancy) {
		this.idVaDatiVacancy = idVaDatiVacancy;
	}

	public void setVaDatiVacancy(VaDatiVacancy vaDatiVacancy) {
		this.vaDatiVacancy = vaDatiVacancy;
	}

}
