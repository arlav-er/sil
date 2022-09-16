package it.eng.myportal.entity;

import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.enums.YgDichiarazioneNeetStatoEnum;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name = "yg_dichiarazione_neet", schema = "myportal")
public class YgDichiarazioneNeet extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	private Integer idYgDichiarazioneNeet;
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
	private DeComune deComuneNascita;

	public YgDichiarazioneNeet() {
		flgCancellata = false;
	}

	@Id
	@SequenceGenerator(name = "yg_dichiarazione_neet_id_yg_dichiarazione_neet_seq", sequenceName = "yg_dichiarazione_neet_id_yg_dichiarazione_neet_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "yg_dichiarazione_neet_id_yg_dichiarazione_neet_seq")
	@Column(name = "id_yg_dichiarazione_neet", unique = true, nullable = false)
	public Integer getIdYgDichiarazioneNeet() {
		return idYgDichiarazioneNeet;
	}

	public void setIdYgDichiarazioneNeet(Integer idYgDichiarazioneNeet) {
		this.idYgDichiarazioneNeet = idYgDichiarazioneNeet;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "dt_dichiarazione", nullable = false)
	public Date getDtDichiarazione() {
		return dtDichiarazione;
	}

	public void setDtDichiarazione(Date dtDichiarazione) {
		this.dtDichiarazione = dtDichiarazione;
	}

	@Column(name = "str_codice_fiscale_lav", length = 16, nullable = false)
	public String getStrCodiceFiscaleLav() {
		return strCodiceFiscaleLav;
	}

	public void setStrCodiceFiscaleLav(String strCodiceFiscaleLav) {
		this.strCodiceFiscaleLav = strCodiceFiscaleLav;
	}

	@Column(name = "str_nome_lav", length = 100, nullable = false)
	public String getStrNomeLav() {
		return strNomeLav;
	}

	public void setStrNomeLav(String strNomeLav) {
		this.strNomeLav = strNomeLav;
	}

	@Column(name = "str_cognome_lav", length = 100, nullable = false)
	public String getStrCognomeLav() {
		return strCognomeLav;
	}

	public void setStrCognomeLav(String strCognomeLav) {
		this.strCognomeLav = strCognomeLav;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "dt_nascita_lav", nullable = false)
	public Date getDtNascitaLav() {
		return dtNascitaLav;
	}

	public void setDtNascitaLav(Date dtNascitaLav) {
		this.dtNascitaLav = dtNascitaLav;
	}

	@Column(name = "str_codice_fiscale_ente", length = 16, nullable = false)
	public String getStrCodiceFiscaleEnte() {
		return strCodiceFiscaleEnte;
	}

	public void setStrCodiceFiscaleEnte(String strCodiceFiscaleEnte) {
		this.strCodiceFiscaleEnte = strCodiceFiscaleEnte;
	}

	@Column(name = "str_ragione_sociale_ente", length = 100, nullable = false)
	public String getStrRagioneSocialeEnte() {
		return strRagioneSocialeEnte;
	}

	public void setStrRagioneSocialeEnte(String strRagioneSocialeEnte) {
		this.strRagioneSocialeEnte = strRagioneSocialeEnte;
	}

	@Column(name = "ext_documento_file_name", length = 255, nullable = true)
	public String getExtDocumentoFileName() {
		return extDocumentoFileName;
	}

	public void setExtDocumentoFileName(String extDocumentoFileName) {
		this.extDocumentoFileName = extDocumentoFileName;
	}

	@Basic
	@Column(name = "ext_documento_file")
	public byte[] getExtDocumentoFile() {
		return extDocumentoFile;
	}

	public void setExtDocumentoFile(byte[] extDocumentoFile) {
		this.extDocumentoFile = extDocumentoFile;
	}

	@Column(name = "flg_cancellata", nullable = false)
	public Boolean getFlgCancellata() {
		return flgCancellata;
	}

	public void setFlgCancellata(Boolean flgCancellata) {
		this.flgCancellata = flgCancellata;
	}

	@Column(name = "ext_documento_file_mime_type", length = 255, nullable = true)
	public String getExtDocumentoFileMimeType() {
		return extDocumentoFileMimeType;
	}

	public void setExtDocumentoFileMimeType(String extDocumentoFileMimeType) {
		this.extDocumentoFileMimeType = extDocumentoFileMimeType;
	}

	@Column(name = "opz_disoccupato", length = 1, nullable = false)
	public String getOpzDisoccupato() {
		return opzDisoccupato;
	}

	public void setOpzDisoccupato(String opzDisoccupato) {
		this.opzDisoccupato = opzDisoccupato;
	}

	@Column(name = "opz_com_obbl", length = 1, nullable = true)
	public String getOpzComObbl() {
		return opzComObbl;
	}

	public void setOpzComObbl(String opzComObbl) {
		this.opzComObbl = opzComObbl;
	}

	@Column(name = "opz_legge_150", length = 1, nullable = true)
	public String getOpzLegge150() {
		return opzLegge150;
	}

	public void setOpzLegge150(String opzLegge150) {
		this.opzLegge150 = opzLegge150;
	}

	@Column(name = "opz_autocert_disocc", length = 1, nullable = false)
	public String getOpzAutocertDisocc() {
		return opzAutocertDisocc;
	}

	public void setOpzAutocertDisocc(String opzAutocertDisocc) {
		this.opzAutocertDisocc = opzAutocertDisocc;
	}

	@Column(name = "opz_istruzione", length = 1, nullable = false)
	public String getOpzIstruzione() {
		return opzIstruzione;
	}

	public void setOpzIstruzione(String opzIstruzione) {
		this.opzIstruzione = opzIstruzione;
	}

	@Column(name = "opz_formazione", length = 1, nullable = false)
	public String getOpzFormazione() {
		return opzFormazione;
	}

	public void setOpzFormazione(String opzFormazione) {
		this.opzFormazione = opzFormazione;
	}

	@Column(name = "opz_autocert_istr_form", length = 1, nullable = false)
	public String getOpzAutocertIstrForm() {
		return opzAutocertIstrForm;
	}

	public void setOpzAutocertIstrForm(String opzAutocertIstrForm) {
		this.opzAutocertIstrForm = opzAutocertIstrForm;
	}

	@Column(name = "opz_autocert_stato_occ", length = 1, nullable = false)
	public String getOpzAutocertStatoOcc() {
		return opzAutocertStatoOcc;
	}

	public void setOpzAutocertStatoOcc(String opzAutocertStatoOcc) {
		this.opzAutocertStatoOcc = opzAutocertStatoOcc;
	}

	@Column(name = "opz_autocert_stato_istr", length = 1, nullable = false)
	public String getOpzAutocertStatoIstr() {
		return opzAutocertStatoIstr;
	}

	public void setOpzAutocertStatoIstr(String opzAutocertStatoIstr) {
		this.opzAutocertStatoIstr = opzAutocertStatoIstr;
	}

	@Column(name = "str_note_disoccupato", length = 255, nullable = true)
	public String getStrNoteDisoccupato() {
		return strNoteDisoccupato;
	}

	public void setStrNoteDisoccupato(String strNoteDisoccupato) {
		this.strNoteDisoccupato = strNoteDisoccupato;
	}

	@Column(name = "str_note_com_obbl", length = 255, nullable = true)
	public String getStrNoteComObbl() {
		return strNoteComObbl;
	}

	public void setStrNoteComObbl(String strNoteComObbl) {
		this.strNoteComObbl = strNoteComObbl;
	}

	@Column(name = "str_note_legge_150", length = 255, nullable = true)
	public String getStrNoteLegge150() {
		return strNoteLegge150;
	}

	public void setStrNoteLegge150(String strNoteLegge150) {
		this.strNoteLegge150 = strNoteLegge150;
	}

	@Column(name = "str_note_autocert_disocc", length = 255, nullable = true)
	public String getStrNoteAutocertDisocc() {
		return strNoteAutocertDisocc;
	}

	public void setStrNoteAutocertDisocc(String strNoteAutocertDisocc) {
		this.strNoteAutocertDisocc = strNoteAutocertDisocc;
	}

	@Column(name = "str_note_istruzione", length = 255, nullable = true)
	public String getStrNoteIstruzione() {
		return strNoteIstruzione;
	}

	public void setStrNoteIstruzione(String strNoteIstruzione) {
		this.strNoteIstruzione = strNoteIstruzione;
	}

	@Column(name = "str_note_formazione", length = 255, nullable = true)
	public String getStrNoteFormazione() {
		return strNoteFormazione;
	}

	public void setStrNoteFormazione(String strNoteFormazione) {
		this.strNoteFormazione = strNoteFormazione;
	}

	@Column(name = "str_note_autocert_istr_form", length = 255, nullable = true)
	public String getStrNoteAutocertIstrForm() {
		return strNoteAutocertIstrForm;
	}

	public void setStrNoteAutocertIstrForm(String strNoteAutocertIstrForm) {
		this.strNoteAutocertIstrForm = strNoteAutocertIstrForm;
	}

	@Column(name = "str_note_autocert_stato_occ", length = 255, nullable = true)
	public String getStrNoteAutocertStatoOcc() {
		return strNoteAutocertStatoOcc;
	}

	public void setStrNoteAutocertStatoOcc(String strNoteAutocertStatoOcc) {
		this.strNoteAutocertStatoOcc = strNoteAutocertStatoOcc;
	}

	@Column(name = "str_note_autocert_stato_istr", length = 255, nullable = true)
	public String getStrNoteAutocertStatoIstr() {
		return strNoteAutocertStatoIstr;
	}

	public void setStrNoteAutocertStatoIstr(String strNoteAutocertStatoIstr) {
		this.strNoteAutocertStatoIstr = strNoteAutocertStatoIstr;
	}

	@Column(name = "ext_upload_neet_file_name", length = 255, nullable = true)
	public String getExtUploadNeetFileName() {
		return extUploadNeetFileName;
	}

	public void setExtUploadNeetFileName(String extUploadNeetFileName) {
		this.extUploadNeetFileName = extUploadNeetFileName;
	}

	@Basic
	@Column(name = "ext_upload_neet_file")
	public byte[] getExtUploadNeetFile() {
		return extUploadNeetFile;
	}

	public void setExtUploadNeetFile(byte[] extUploadNeetFile) {
		this.extUploadNeetFile = extUploadNeetFile;
	}

	@Column(name = "ext_upload_neet_file_mime_type", length = 255, nullable = true)
	public String getExtUploadNeetFileMimeType() {
		return extUploadNeetFileMimeType;
	}

	public void setExtUploadNeetFileMimeType(String extUploadNeetFileMimeType) {
		this.extUploadNeetFileMimeType = extUploadNeetFileMimeType;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_comune_nascita", nullable = true)
	public DeComune getDeComuneNascita() {
		return deComuneNascita;
	}

	public void setDeComuneNascita(DeComune deComuneNascita) {
		this.deComuneNascita = deComuneNascita;
	}

	@Transient
	public YgDichiarazioneNeetStatoEnum getStatoDichiarazione() {
		if (flgCancellata != null && flgCancellata) {
			return YgDichiarazioneNeetStatoEnum.CANCELLATO;
		} else if (flgCancellata != null && !flgCancellata && extDocumentoFile != null && extDocumentoFile.length != 0) {
			return YgDichiarazioneNeetStatoEnum.COMPLETO;
		} else {
			return YgDichiarazioneNeetStatoEnum.INCOMPLETO;
		}
	}

	public void setStatoDichiarazione(YgDichiarazioneNeetStatoEnum statoDichiarazione) {
		// do nothing
	}

}
