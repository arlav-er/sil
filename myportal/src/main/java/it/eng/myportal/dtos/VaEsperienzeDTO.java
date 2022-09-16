package it.eng.myportal.dtos;

import it.eng.myportal.utils.ConstantsSingleton;

public class VaEsperienzeDTO extends AbstractUpdatablePkDTO implements IVacancySection {
	private static final long serialVersionUID = -1426005738769603179L;

	private Integer idVaDatiVacancy;
	private Integer numDa;
	private Integer numA;
	private DeMotivoEtaSilDTO deMotivoEtaSil;
	private String notaMotivoEta;
	private String opzEsperienza;
	private Integer numAnniEsperienza;
	private String opzFormazione;
	private String notaEsperienza;

	public VaEsperienzeDTO() {
		super();
		deMotivoEtaSil = new DeMotivoEtaSilDTO();
	}

	@Override
	public Integer getIdVaDatiVacancy() {
		return idVaDatiVacancy;
	}

	@Override
	public void setIdVaDatiVacancy(Integer idVaDatiVacancy) {
		this.idVaDatiVacancy = idVaDatiVacancy;
	}

	public Integer getNumDa() {
		return numDa;
	}

	public void setNumDa(Integer numDa) {
		this.numDa = numDa;
	}

	public Integer getNumA() {
		return numA;
	}

	public void setNumA(Integer numA) {
		this.numA = numA;
	}

	public DeMotivoEtaSilDTO getDeMotivoEtaSil() {
		return deMotivoEtaSil;
	}

	public void setDeMotivoEtaSil(DeMotivoEtaSilDTO deMotivoEtaSil) {
		this.deMotivoEtaSil = deMotivoEtaSil;
	}

	public String getNotaMotivoEta() {
		return notaMotivoEta;
	}

	public void setNotaMotivoEta(String notaMotivoEta) {
		this.notaMotivoEta = notaMotivoEta;
	}

	public String getOpzEsperienza() {
		return opzEsperienza;
	}

	public void setOpzEsperienza(String opzEsperienza) {
		this.opzEsperienza = opzEsperienza;
	}

	public Integer getNumAnniEsperienza() {
		return numAnniEsperienza;
	}

	public void setNumAnniEsperienza(Integer numAnniEsperienza) {
		this.numAnniEsperienza = numAnniEsperienza;
	}

	public String getNotaEsperienza() {
		return notaEsperienza;
	}

	public void setNotaEsperienza(String notaEsperienza) {
		this.notaEsperienza = notaEsperienza;
	}

	public String getOpzFormazione() {
		return opzFormazione;
	}

	public void setOpzFormazione(String opzFormazione) {
		this.opzFormazione = opzFormazione;
	}

	public String getOpzione(String codice) {
		return ConstantsSingleton.opzValue(codice);
	}

}
