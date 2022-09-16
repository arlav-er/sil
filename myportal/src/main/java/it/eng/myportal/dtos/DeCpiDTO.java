/**
 * 
 */
package it.eng.myportal.dtos;

/**
 * @author girotti
 * 
 */
public class DeCpiDTO extends GenericDecodeDTO implements ISuggestible {
	private static final long serialVersionUID = -872862179682010239L;

	private String codCom;
	private String strCom;
	private String emailServiziOnline;
	private String descrizione;
	private String indirizzo;
	private String localita;
	private String cap;
	private String tel;
	private String fax;
	private String email;
	private String orario;
	private String note;
	private String responsabile;
	private String emailMigrazione;
	private String indirizzoStampa;
	private String rifSms;
	private String emailRifCl;
	private String telRifCl;
	private String emailPortale;
	private String descrizioneMin;
	private String codProvincia;
	private Boolean flgPatronato;
	private String telPatronato;
	private String codCpiMin;

	public DeCpiDTO() {
		super();
	}

	public String getCodCom() {
		return codCom;
	}

	public String getStrCom() {
		return strCom;
	}

	public String getEmailServiziOnline() {
		return emailServiziOnline;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public String getIndirizzo() {
		return indirizzo;
	}

	public String getLocalita() {
		return localita;
	}

	public String getCap() {
		return cap;
	}

	public String getTel() {
		return tel;
	}

	public String getFax() {
		return fax;
	}

	public String getEmail() {
		return email;
	}

	public String getOrario() {
		return orario;
	}

	public String getNote() {
		return note;
	}

	public String getResponsabile() {
		return responsabile;
	}

	public String getEmailMigrazione() {
		return emailMigrazione;
	}

	public String getIndirizzoStampa() {
		return indirizzoStampa;
	}

	public String getRifSms() {
		return rifSms;
	}

	public String getEmailRifCl() {
		return emailRifCl;
	}

	public String getTelRifCl() {
		return telRifCl;
	}

	public String getEmailPortale() {
		return emailPortale;
	}

	public void setCodCom(String codCom) {
		this.codCom = codCom;
	}

	public void setStrCom(String strCom) {
		this.strCom = strCom;
	}

	public void setEmailServiziOnline(String emailServiziOnline) {
		this.emailServiziOnline = emailServiziOnline;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	public void setLocalita(String localita) {
		this.localita = localita;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setOrario(String orario) {
		this.orario = orario;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void setResponsabile(String responsabile) {
		this.responsabile = responsabile;
	}

	public void setEmailMigrazione(String emailMigrazione) {
		this.emailMigrazione = emailMigrazione;
	}

	public void setIndirizzoStampa(String indirizzoStampa) {
		this.indirizzoStampa = indirizzoStampa;
	}

	public void setRifSms(String rifSms) {
		this.rifSms = rifSms;
	}

	public void setEmailRifCl(String emailRifCl) {
		this.emailRifCl = emailRifCl;
	}

	public void setTelRifCl(String telRifCl) {
		this.telRifCl = telRifCl;
	}

	public void setEmailPortale(String emailPortale) {
		this.emailPortale = emailPortale;
	}

	public String getDescrizioneMin() {
		return descrizioneMin;
	}

	public void setDescrizioneMin(String descrizioneMin) {
		this.descrizioneMin = descrizioneMin;
	}

	public String getCodProvincia() {
		return codProvincia;
	}

	public void setCodProvincia(String codProvincia) {
		this.codProvincia = codProvincia;
	}

	public Boolean getFlgPatronato() {
		return flgPatronato;
	}

	public void setFlgPatronato(Boolean flgPatronato) {
		this.flgPatronato = flgPatronato;
	}

	public String getTelPatronato() {
		return telPatronato;
	}

	public void setTelPatronato(String telPatronato) {
		this.telPatronato = telPatronato;
	}

	public String getCodCpiMin() {
		return codCpiMin;
	}

	public void setCodCpiMin(String codCpiMin) {
		this.codCpiMin = codCpiMin;
	}

}
