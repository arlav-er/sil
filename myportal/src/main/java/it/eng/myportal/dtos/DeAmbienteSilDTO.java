package it.eng.myportal.dtos;

import java.util.Date;

public class DeAmbienteSilDTO extends AbstractUpdatablePkDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8533968312666072424L;
	private Integer prgAmbienteSil;
	private DeProvinciaDTO provinciaSilDTO;
	private String descrizione;
	private DeCpiDTO cpiDTO;
	private Date dtInizioVal;
	private Date dtFineVal;

	public DeAmbienteSilDTO() {
	}

	public Integer getPrgAmbienteSil() {
		return prgAmbienteSil;
	}

	public void setPrgAmbienteSil(Integer prgAmbienteSil) {
		this.prgAmbienteSil = prgAmbienteSil;
	}

	public DeProvinciaDTO getProvinciaSilDTO() {
		return provinciaSilDTO;
	}

	public void setProvinciaSilDTO(DeProvinciaDTO provinciaSilDTO) {
		this.provinciaSilDTO = provinciaSilDTO;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public DeCpiDTO getCpiDTO() {
		return cpiDTO;
	}

	public void setCpiDTO(DeCpiDTO cpiDTO) {
		this.cpiDTO = cpiDTO;
	}

	public Date getDtInizioVal() {
		return dtInizioVal;
	}

	public void setDtInizioVal(Date dtInizioVal) {
		this.dtInizioVal = dtInizioVal;
	}

	public Date getDtFineVal() {
		return dtFineVal;
	}

	public void setDtFineVal(Date dtFineVal) {
		this.dtFineVal = dtFineVal;
	}

}
