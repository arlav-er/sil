package it.eng.myportal.dtos;

import java.util.Date;

public class YgDichiarazioneNeetDTO extends AbstractUpdatablePkDTO {

	private static final long serialVersionUID = 4742021220604007503L;

	private Date dtDichiarazione;
	private String strCodiceFiscaleLav;
	private String strNomeLav;
	private String strCognomeLav;
	private Date dtNascitaLav;
	private String strCodiceFiscaleEnte;
	private String strRagioneSocialeEnte;
	private String extDocumentoFileName;
	private byte[] extDocumentoFile;
	private String extDocumentoFileMimeType;
	private Boolean flgCancellata;
	private String strDescrizioneStatoDichiarazione;
	private String opzDisoccupato;
	private String opzComObbl;
	private String opzLegge150;
	private String opzAutocertDisocc;
	private String opzIstruzione;
	private String opzFormazione;
	private String opzAutocertIstrForm;
	private String opzAutocertStatoOcc;
	private String opzAutocertStatoIstr;
	private String strNoteDisoccupato;
	private String strNoteComObbl;
	private String strNoteLegge150;
	private String strNoteAutocertDisocc;
	private String strNoteIstruzione;
	private String strNoteFormazione;
	private String strNoteAutocertIstrForm;
	private String strNoteAutocertStatoOcc;
	private String strNoteAutocertStatoIstr;
	private String extUploadNeetFileName;
	private byte[] extUploadNeetFile;
	private String extUploadNeetFileMimeType;
	private DeComuneDTO deComuneNascitaDTO;

	public YgDichiarazioneNeetDTO() {
		flgCancellata = false;
	}

	public Date getDtDichiarazione() {
		return dtDichiarazione;
	}

	public void setDtDichiarazione(Date dtDichiarazione) {
		this.dtDichiarazione = dtDichiarazione;
	}

	public String getStrCodiceFiscaleLav() {
		return strCodiceFiscaleLav;
	}

	public void setStrCodiceFiscaleLav(String strCodiceFiscaleLav) {
		this.strCodiceFiscaleLav = strCodiceFiscaleLav;
	}

	public String getStrNomeLav() {
		return strNomeLav;
	}

	public void setStrNomeLav(String strNomeLav) {
		this.strNomeLav = strNomeLav;
	}

	public String getStrCognomeLav() {
		return strCognomeLav;
	}

	public void setStrCognomeLav(String strCognomeLav) {
		this.strCognomeLav = strCognomeLav;
	}

	public Date getDtNascitaLav() {
		return dtNascitaLav;
	}

	public void setDtNascitaLav(Date dtNascitaLav) {
		this.dtNascitaLav = dtNascitaLav;
	}

	public String getStrCodiceFiscaleEnte() {
		return strCodiceFiscaleEnte;
	}

	public void setStrCodiceFiscaleEnte(String strCodiceFiscaleEnte) {
		this.strCodiceFiscaleEnte = strCodiceFiscaleEnte;
	}

	public String getStrRagioneSocialeEnte() {
		return strRagioneSocialeEnte;
	}

	public void setStrRagioneSocialeEnte(String strRagioneSocialeEnte) {
		this.strRagioneSocialeEnte = strRagioneSocialeEnte;
	}

	public String getExtDocumentoFileName() {
		return extDocumentoFileName;
	}

	public void setExtDocumentoFileName(String extDocumentoFileName) {
		this.extDocumentoFileName = extDocumentoFileName;
	}

	public byte[] getExtDocumentoFile() {
		return extDocumentoFile;
	}

	public void setExtDocumentoFile(byte[] extDocumentoFile) {
		this.extDocumentoFile = extDocumentoFile;
	}

	public Boolean getFlgCancellata() {
		return flgCancellata;
	}

	public void setFlgCancellata(Boolean flgCancellata) {
		this.flgCancellata = flgCancellata;
	}

	public String getStrDescrizioneStatoDichiarazione() {
		return strDescrizioneStatoDichiarazione;
	}

	public void setStrDescrizioneStatoDichiarazione(String strDescrizioneStatoDichiarazione) {
		this.strDescrizioneStatoDichiarazione = strDescrizioneStatoDichiarazione;
	}

	public String getExtDocumentoFileMimeType() {
		return extDocumentoFileMimeType;
	}

	public void setExtDocumentoFileMimeType(String extDocumentoFileMimeType) {
		this.extDocumentoFileMimeType = extDocumentoFileMimeType;
	}

	public String getOpzDisoccupato() {
		return opzDisoccupato;
	}

	public void setOpzDisoccupato(String opzDisoccupato) {
		this.opzDisoccupato = opzDisoccupato;
	}

	public String getOpzComObbl() {
		return opzComObbl;
	}

	public void setOpzComObbl(String opzComObbl) {
		this.opzComObbl = opzComObbl;
	}

	public String getOpzLegge150() {
		return opzLegge150;
	}

	public void setOpzLegge150(String opzLegge150) {
		this.opzLegge150 = opzLegge150;
	}

	public String getOpzAutocertDisocc() {
		return opzAutocertDisocc;
	}

	public void setOpzAutocertDisocc(String opzAutocertDisocc) {
		this.opzAutocertDisocc = opzAutocertDisocc;
	}

	public String getOpzIstruzione() {
		return opzIstruzione;
	}

	public void setOpzIstruzione(String opzIstruzione) {
		this.opzIstruzione = opzIstruzione;
	}

	public String getOpzFormazione() {
		return opzFormazione;
	}

	public void setOpzFormazione(String opzFormazione) {
		this.opzFormazione = opzFormazione;
	}

	public String getOpzAutocertIstrForm() {
		return opzAutocertIstrForm;
	}

	public void setOpzAutocertIstrForm(String opzAutocertIstrForm) {
		this.opzAutocertIstrForm = opzAutocertIstrForm;
	}

	public String getOpzAutocertStatoOcc() {
		return opzAutocertStatoOcc;
	}

	public void setOpzAutocertStatoOcc(String opzAutocertStatoOcc) {
		this.opzAutocertStatoOcc = opzAutocertStatoOcc;
	}

	public String getOpzAutocertStatoIstr() {
		return opzAutocertStatoIstr;
	}

	public void setOpzAutocertStatoIstr(String opzAutocertStatoIstr) {
		this.opzAutocertStatoIstr = opzAutocertStatoIstr;
	}

	public String getStrNoteDisoccupato() {
		return strNoteDisoccupato;
	}

	public void setStrNoteDisoccupato(String strNoteDisoccupato) {
		this.strNoteDisoccupato = strNoteDisoccupato;
	}

	public String getStrNoteComObbl() {
		return strNoteComObbl;
	}

	public void setStrNoteComObbl(String strNoteComObbl) {
		this.strNoteComObbl = strNoteComObbl;
	}

	public String getStrNoteLegge150() {
		return strNoteLegge150;
	}

	public void setStrNoteLegge150(String strNoteLegge150) {
		this.strNoteLegge150 = strNoteLegge150;
	}

	public String getStrNoteAutocertDisocc() {
		return strNoteAutocertDisocc;
	}

	public void setStrNoteAutocertDisocc(String strNoteAutocertDisocc) {
		this.strNoteAutocertDisocc = strNoteAutocertDisocc;
	}

	public String getStrNoteIstruzione() {
		return strNoteIstruzione;
	}

	public void setStrNoteIstruzione(String strNoteIstruzione) {
		this.strNoteIstruzione = strNoteIstruzione;
	}

	public String getStrNoteFormazione() {
		return strNoteFormazione;
	}

	public void setStrNoteFormazione(String strNoteFormazione) {
		this.strNoteFormazione = strNoteFormazione;
	}

	public String getStrNoteAutocertIstrForm() {
		return strNoteAutocertIstrForm;
	}

	public void setStrNoteAutocertIstrForm(String strNoteAutocertIstrForm) {
		this.strNoteAutocertIstrForm = strNoteAutocertIstrForm;
	}

	public String getStrNoteAutocertStatoOcc() {
		return strNoteAutocertStatoOcc;
	}

	public void setStrNoteAutocertStatoOcc(String strNoteAutocertStatoOcc) {
		this.strNoteAutocertStatoOcc = strNoteAutocertStatoOcc;
	}

	public String getStrNoteAutocertStatoIstr() {
		return strNoteAutocertStatoIstr;
	}

	public void setStrNoteAutocertStatoIstr(String strNoteAutocertStatoIstr) {
		this.strNoteAutocertStatoIstr = strNoteAutocertStatoIstr;
	}

	public String getExtUploadNeetFileName() {
		return extUploadNeetFileName;
	}

	public void setExtUploadNeetFileName(String extUploadNeetFileName) {
		this.extUploadNeetFileName = extUploadNeetFileName;
	}

	public byte[] getExtUploadNeetFile() {
		return extUploadNeetFile;
	}

	public void setExtUploadNeetFile(byte[] extUploadNeetFile) {
		this.extUploadNeetFile = extUploadNeetFile;
	}

	public String getExtUploadNeetFileMimeType() {
		return extUploadNeetFileMimeType;
	}

	public void setExtUploadNeetFileMimeType(String extUploadNeetFileMimeType) {
		this.extUploadNeetFileMimeType = extUploadNeetFileMimeType;
	}

	public DeComuneDTO getDeComuneNascitaDTO() {
		return deComuneNascitaDTO;
	}

	public void setDeComuneNascitaDTO(DeComuneDTO deComuneNascitaDTO) {
		this.deComuneNascitaDTO = deComuneNascitaDTO;
	}

}
