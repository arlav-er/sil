package it.eng.myportal.dtos;

public class FbCandidaturaAllegatoDTO extends AbstractUpdatablePkDTO {

	private static final long serialVersionUID = 516269893468114959L;

	private FbCandidaturaDTO fbCandidaturaDTO;
	private byte[] dataFile;
	private String nomeFile;
	private String mimeFile;

	public FbCandidaturaAllegatoDTO() {

	}

	public FbCandidaturaDTO getFbCandidaturaDTO() {
		return fbCandidaturaDTO;
	}

	public void setFbCandidaturaDTO(FbCandidaturaDTO fbCandidaturaDTO) {
		this.fbCandidaturaDTO = fbCandidaturaDTO;
	}

	public byte[] getDataFile() {
		return dataFile;
	}

	public void setDataFile(byte[] dataFile) {
		this.dataFile = dataFile;
	}

	public String getNomeFile() {
		return nomeFile;
	}

	public void setNomeFile(String nomeFile) {
		this.nomeFile = nomeFile;
	}

	public String getMimeFile() {
		return mimeFile;
	}

	public void setMimeFile(String mimeFile) {
		this.mimeFile = mimeFile;
	}

}
