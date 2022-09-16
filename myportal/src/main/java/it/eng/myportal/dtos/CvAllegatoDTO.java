package it.eng.myportal.dtos;

/**
 * DTO contenente un allegato. Questi oggetti possono essere collegati ad un
 * Curriculum.
 * 
 * @author Girotti S.
 * 
 */
public class CvAllegatoDTO extends AbstractUpdatablePkDTO {

	private static final long serialVersionUID = 5502519125703657262L;

	private byte[] contenuto;
	private Integer idCvDatiPersonali;
	private String filename;
	private String tempFilename;

	public byte[] getContenuto() {
		return contenuto;
	}

	public void setContenuto(byte[] contenuto) {
		this.contenuto = contenuto;
	}

	public Integer getIdCvDatiPersonali() {
		return idCvDatiPersonali;
	}

	public void setIdCvDatiPersonali(Integer _idCvDatiPersonali) {
		this.idCvDatiPersonali = _idCvDatiPersonali;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getTempFilename() {
		return tempFilename;
	}

	public void setTempFilename(String tempFilename) {
		this.tempFilename = tempFilename;
	}

}
