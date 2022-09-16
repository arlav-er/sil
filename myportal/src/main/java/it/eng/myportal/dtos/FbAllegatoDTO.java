package it.eng.myportal.dtos;

import it.eng.myportal.entity.enums.OpzTipoAllegatoConvenzioneEnum;

public class FbAllegatoDTO extends AbstractUpdatablePkDTO {

	private static final long serialVersionUID = 3348872050611593275L;

	private FbConvenzioneDTO idFbConvenzione;
	private byte[] pdf;
	private String nomeFile;
	private String mimeFile;
	private OpzTipoAllegatoConvenzioneEnum tipoAllegato;

	public FbAllegatoDTO() {

	}

	public FbConvenzioneDTO getIdFbConvenzione() {
		return idFbConvenzione;
	}

	public void setIdFbConvenzione(FbConvenzioneDTO fbConvenzioneDTO) {
		this.idFbConvenzione = fbConvenzioneDTO;
	}

	public byte[] getPdf() {
		return pdf;
	}

	public void setPdf(byte[] pdf) {
		this.pdf = pdf;
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

	public OpzTipoAllegatoConvenzioneEnum getTipoAllegato() {
		return tipoAllegato;
	}

	public void setTipoAllegato(OpzTipoAllegatoConvenzioneEnum tipoAllegato) {
		this.tipoAllegato = tipoAllegato;
	}

}
