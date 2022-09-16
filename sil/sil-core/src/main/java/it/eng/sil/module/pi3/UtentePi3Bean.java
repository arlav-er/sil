package it.eng.sil.module.pi3;

import java.io.Serializable;

/***
 * 
 * @author User
 *
 *         AnLavoratore su SPIL / Correspondent su WebServices di Informatica Trentina
 *
 */
public class UtentePi3Bean implements Serializable {

	private static final long serialVersionUID = 1332475871228941993L;

	// proprietà Utente in SPIL
	private String idUtenteSPIL; // cdnLavoratore oppure prgAzienda o codCpi (UO)
	private String typeUtente; // Lavoratore oppure Azienda oppure Unita' Organizzativa

	// proprietà Correspondent in Informatica Trentina
	private String idCorrespondentINFTRENT;
	private String codeCorrespondentINFTRENT;
	private String codeAoooCorrespondentINFTRENT;
	private String admAoooCorrespondentINFTRENT;
	private String descriptionCorrespondentINFTRENT;
	private String correspondentTypeINFTRENT;
	private String noteCorrespondentINFTRENT;

	// proprietà generiche utente
	private String nome;
	private String cognome;
	private String codiceFiscale;
	private String dataNascita;
	private String sesso;
	private String indirizzoResidenza;
	private String localitaResidenza;
	private String cittaResidenza;
	private String capResidenza;
	private String nazioneResidenza;
	private String email;
	private String telefono;
	private String fax;

	private String ragioneSociale;
	private String partitaIva;

	private String flgDestinatarioPrincipale;

	public String getIdUtenteSPIL() {
		return idUtenteSPIL;
	}

	public void setIdUtenteSPIL(String idUtenteSPIL) {
		this.idUtenteSPIL = idUtenteSPIL;
	}

	public String getTypeUtente() {
		return typeUtente;
	}

	public void setTypeUtente(String typeUtente) {
		this.typeUtente = typeUtente;
	}

	public String getIdCorrespondentINFTRENT() {
		return idCorrespondentINFTRENT;
	}

	public void setIdCorrespondentINFTRENT(String idCorrespondentINFTRENT) {
		this.idCorrespondentINFTRENT = idCorrespondentINFTRENT;
	}

	public String getCodeCorrespondentINFTRENT() {
		return codeCorrespondentINFTRENT;
	}

	public void setCodeCorrespondentINFTRENT(String codeCorrespondentINFTRENT) {
		this.codeCorrespondentINFTRENT = codeCorrespondentINFTRENT;
	}

	public String getCodeAoooCorrespondentINFTRENT() {
		return codeAoooCorrespondentINFTRENT;
	}

	public void setCodeAoooCorrespondentINFTRENT(String codeAoooCorrespondentINFTRENT) {
		this.codeAoooCorrespondentINFTRENT = codeAoooCorrespondentINFTRENT;
	}

	public String getAdmAoooCorrespondentINFTRENT() {
		return admAoooCorrespondentINFTRENT;
	}

	public void setAdmAoooCorrespondentINFTRENT(String admAoooCorrespondentINFTRENT) {
		this.admAoooCorrespondentINFTRENT = admAoooCorrespondentINFTRENT;
	}

	public String getDescriptionCorrespondentINFTRENT() {
		return descriptionCorrespondentINFTRENT;
	}

	public void setDescriptionCorrespondentINFTRENT(String descriptionCorrespondentINFTRENT) {
		this.descriptionCorrespondentINFTRENT = descriptionCorrespondentINFTRENT;
	}

	public String getCorrespondentTypeINFTRENT() {
		return correspondentTypeINFTRENT;
	}

	public void setCorrespondentTypeINFTRENT(String correspondentTypeINFTRENT) {
		this.correspondentTypeINFTRENT = correspondentTypeINFTRENT;
	}

	public String getNoteCorrespondentINFTRENT() {
		return noteCorrespondentINFTRENT;
	}

	public void setNoteCorrespondentINFTRENT(String noteCorrespondentINFTRENT) {
		this.noteCorrespondentINFTRENT = noteCorrespondentINFTRENT;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public String getDataNascita() {
		return dataNascita;
	}

	public void setDataNascita(String dataNascita) {
		this.dataNascita = dataNascita;
	}

	public String getSesso() {
		return sesso;
	}

	public void setSesso(String sesso) {
		this.sesso = sesso;
	}

	public String getIndirizzoResidenza() {
		return indirizzoResidenza;
	}

	public void setIndirizzoResidenza(String indirizzoResidenza) {
		this.indirizzoResidenza = indirizzoResidenza;
	}

	public String getLocalitaResidenza() {
		return localitaResidenza;
	}

	public void setLocalitaResidenza(String localitaResidenza) {
		this.localitaResidenza = localitaResidenza;
	}

	public String getCittaResidenza() {
		return cittaResidenza;
	}

	public void setCittaResidenza(String cittaResidenza) {
		this.cittaResidenza = cittaResidenza;
	}

	public String getCapResidenza() {
		return capResidenza;
	}

	public void setCapResidenza(String capResidenza) {
		this.capResidenza = capResidenza;
	}

	public String getNazioneResidenza() {
		return nazioneResidenza;
	}

	public void setNazioneResidenza(String nazioneResidenza) {
		this.nazioneResidenza = nazioneResidenza;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getRagioneSociale() {
		return ragioneSociale;
	}

	public void setRagioneSociale(String ragioneSociale) {
		this.ragioneSociale = ragioneSociale;
	}

	public String getPartitaIva() {
		return partitaIva;
	}

	public void setPartitaIva(String partitaIva) {
		this.partitaIva = partitaIva;
	}

	public String getFlgDestinatarioPrincipale() {
		return flgDestinatarioPrincipale;
	}

	public void setFlgDestinatarioPrincipale(String flgDestinatarioPrincipale) {
		this.flgDestinatarioPrincipale = flgDestinatarioPrincipale;
	}

}
