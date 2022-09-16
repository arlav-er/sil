package it.eng.myportal.dtos;

import java.util.Date;

public class FbConvenzioneFilterDTO {
	private DeTipoFbConvenzioneDTO filterDeTipoConvenzione;
	private Date dataStipulaDa;
	private Date dataStipulaA;
	private Date dataRepertorioDa;
	private Date dataRepertorioA;
	private String soggettoPromotore;
	private String numeroRepertorio;
	private DeStatoFbConvenzioneDTO filterDeStatoConvenzione;

	public FbConvenzioneFilterDTO() {
		super();
		filterDeTipoConvenzione = new DeTipoFbConvenzioneDTO();
		filterDeStatoConvenzione = new DeStatoFbConvenzioneDTO();
	}

	public DeTipoFbConvenzioneDTO getFilterDeTipoConvenzione() {
		return filterDeTipoConvenzione;
	}

	public void setFilterDeTipoConvenzione(DeTipoFbConvenzioneDTO filterDeTipoConvenzione) {
		this.filterDeTipoConvenzione = filterDeTipoConvenzione;
	}

	public Date getDataStipulaDa() {
		return dataStipulaDa;
	}

	public void setDataStipulaDa(Date dataStipulaDa) {
		this.dataStipulaDa = dataStipulaDa;
	}

	public Date getDataStipulaA() {
		return dataStipulaA;
	}

	public void setDataStipulaA(Date dataStipulaA) {
		this.dataStipulaA = dataStipulaA;
	}

	public Date getDataRepertorioDa() {
		return dataRepertorioDa;
	}

	public void setDataRepertorioDa(Date dataRepertorioDa) {
		this.dataRepertorioDa = dataRepertorioDa;
	}

	public Date getDataRepertorioA() {
		return dataRepertorioA;
	}

	public void setDataRepertorioA(Date dataRepertorioA) {
		this.dataRepertorioA = dataRepertorioA;
	}

	public String getSoggettoPromotore() {
		return soggettoPromotore;
	}

	public void setSoggettoPromotore(String soggettoPromotore) {
		this.soggettoPromotore = soggettoPromotore;
	}

	public String getNumeroRepertorio() {
		return numeroRepertorio;
	}

	public void setNumeroRepertorio(String numeroRepertorio) {
		this.numeroRepertorio = numeroRepertorio;
	}

	public DeStatoFbConvenzioneDTO getFilterDeStatoConvenzione() {
		return filterDeStatoConvenzione;
	}

	public void setFilterDeStatoConvenzione(DeStatoFbConvenzioneDTO filterDeStatoConvenzione) {
		this.filterDeStatoConvenzione = filterDeStatoConvenzione;
	}

}
