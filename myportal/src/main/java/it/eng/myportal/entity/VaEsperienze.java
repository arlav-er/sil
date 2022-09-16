package it.eng.myportal.entity;

import it.eng.myportal.entity.decodifiche.sil.DeMotivoEtaSil;

import javax.persistence.*;

@Entity
@Table(name = "va_esperienze", schema = "myportal")
public class VaEsperienze extends AbstractEntity implements java.io.Serializable {
	private static final long serialVersionUID = 1287833767242275010L;

	private Integer idVaEsperienze;
	private VaDatiVacancy vaDatiVacancy;
	private Integer numDa;
	private Integer numA;
	private DeMotivoEtaSil deMotivoEtaSil;
	private String notaMotivoEta;
	private String opzEsperienza;
	private Integer numAnniEsperienza;
	private String opzFormazione;
	private String notaEsperienza;
	private String sceltaEsperienza;
	private String descEsperienzaLav;

	@Id
	@SequenceGenerator(name = "va_esperienze_id_va_esperienze_seq", sequenceName = "va_esperienze_id_va_esperienze_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "va_esperienze_id_va_esperienze_seq")
	@Column(name = "id_va_esperienze", unique = true, nullable = false)
	public Integer getIdVaEsperienze() {
		return idVaEsperienze;
	}

	public void setIdVaEsperienze(Integer idVaEsperienze) {
		this.idVaEsperienze = idVaEsperienze;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_va_dati_vacancy", nullable = false)
	public VaDatiVacancy getVaDatiVacancy() {
		return vaDatiVacancy;
	}

	public void setVaDatiVacancy(VaDatiVacancy vaDatiVacancy) {
		this.vaDatiVacancy = vaDatiVacancy;
	}

	@Column(name = "num_da")
	public Integer getNumDa() {
		return numDa;
	}

	public void setNumDa(Integer numDa) {
		this.numDa = numDa;
	}

	@Column(name = "num_a")
	public Integer getNumA() {
		return numA;
	}

	public void setNumA(Integer numA) {
		this.numA = numA;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_motivo_eta_sil")
	public DeMotivoEtaSil getDeMotivoEtaSil() {
		return deMotivoEtaSil;
	}

	public void setDeMotivoEtaSil(DeMotivoEtaSil deMotivoEtaSil) {
		this.deMotivoEtaSil = deMotivoEtaSil;
	}

	@Column(name = "nota_motivo_eta", length = 2000)
	public String getNotaMotivoEta() {
		return notaMotivoEta;
	}

	public void setNotaMotivoEta(String notaMotivoEta) {
		this.notaMotivoEta = notaMotivoEta;
	}

	@Column(name = "opz_esperienza", length = 1)
	public String getOpzEsperienza() {
		return opzEsperienza;
	}

	public void setOpzEsperienza(String opzEsperienza) {
		this.opzEsperienza = opzEsperienza;
	}

	@Column(name = "num_anni_esperienza")
	public Integer getNumAnniEsperienza() {
		return numAnniEsperienza;
	}

	public void setNumAnniEsperienza(Integer numAnniEsperienza) {
		this.numAnniEsperienza = numAnniEsperienza;
	}

	@Column(name = "opz_formazione", length = 1)
	public String getOpzFormazione() {
		return opzFormazione;
	}

	public void setOpzFormazione(String opzFormazione) {
		this.opzFormazione = opzFormazione;
	}

	@Column(name = "nota_esperienza", length = 2000)
	public String getNotaEsperienza() {
		return notaEsperienza;
	}

	public void setNotaEsperienza(String notaEsperienza) {
		this.notaEsperienza = notaEsperienza;
	}

	@Column(name = "desc_esperienza_lav", length = 100)
	public String getDescEsperienzaLav() {
		return descEsperienzaLav;
	}

	public void setDescEsperienzaLav(String descEperienzaLav) {
		this.descEsperienzaLav = descEperienzaLav;
	}

	@Transient
	public String getSceltaEsperienza() {
		if (opzEsperienza != null && this.opzEsperienza.equalsIgnoreCase("Y")) {
			sceltaEsperienza = "Y";
		} else if(opzEsperienza != null && this.opzEsperienza.equalsIgnoreCase("N")) {
			sceltaEsperienza =  "N";
		}if(sceltaEsperienza == null){
			sceltaEsperienza = "";
		}
		return sceltaEsperienza;
	}

	public void setSceltaEsperienza(String sceltaEsperienza) {
		if (sceltaEsperienza != null && sceltaEsperienza.equalsIgnoreCase("N")) {
			this.opzEsperienza = "N";
		} else if (sceltaEsperienza != null && sceltaEsperienza.equalsIgnoreCase("Y")) {

			this.opzEsperienza = "Y";
		}
		if (sceltaEsperienza == null) {
			this.opzEsperienza = "";
		}
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idVaEsperienze == null) ? 0 : idVaEsperienze.hashCode());
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
		VaEsperienze other = (VaEsperienze) obj;
		if (idVaEsperienze == null) {
			if (other.idVaEsperienze != null)
				return false;
		} else if (!idVaEsperienze.equals(other.idVaEsperienze))
			return false;
		return true;
	}

}
