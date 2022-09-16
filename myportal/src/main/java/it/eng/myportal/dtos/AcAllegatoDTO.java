package it.eng.myportal.dtos;

/**
 * DTO contenente un allegato. Questi oggetti possono essere collegati ad un
 * Curriculum.
 * 
 * @author Girotti S.
 * 
 */
public class AcAllegatoDTO extends AbstractUpdatablePkDTO {

	private static final long serialVersionUID = 5502519125703657262L;

	private byte[] contenuto;
	private Integer idAcCandidatura;
	private String filename;
	private String tempFilename;

	public byte[] getContenuto() {
		return contenuto;
	}

	public void setContenuto(byte[] contenuto) {
		this.contenuto = contenuto;
	}

	public Integer getIdAcCandidatura() {
		return idAcCandidatura;
	}

	public void setIdAcCandidatura(Integer _idAcCandidatura) {
		this.idAcCandidatura = _idAcCandidatura;
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
