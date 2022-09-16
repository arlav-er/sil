package it.eng.myportal.dtos;

import it.eng.myportal.validator.Telefono;

import java.util.Date;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

/**
 * Classe per la registrazione di un utente. Permette di memorizzare tutte le
 * informazioni ricevute in fase di iscrizione. Oltre ai dati dell'utente
 * permette di avere un campo per la mail di conferma, uno per la password di
 * conferma ed uno per l'accettazione dei termini.
 * 
 * @author Rodi A.
 *
 */
public class RegisterUtenteDTO extends RegisterDTO implements IDTO {

	private static final long serialVersionUID = -485650209369727111L;

	private DeComuneDTO domicilio;
	private DeProvinciaDTO provincia;
	@Email
	private String indirizzoPEC;

	private Date dataNascita;
	private DeCittadinanzaDTO cittadinanza;
	@Size(max = 255)
	private String documentoIdentita;
	private DeTitoloSoggiornoDTO documentoSoggiorno;
	@Size(max = 255)
	private String numeroDocumento;
	private Date dataScadenzaDocumento;
	@Size(max = 255)
	private String numeroAssicurata;
	private Date dataAssicurata;
	private String codiceRichiestaAutForte;

	@Telefono
	private String cellulare;

	// se impostata a true, l'utente è direttamente
	// abilitato ai servizi online
	private Boolean autenticazioneForte;

	// se impostato a true, l'utente si sta registrando attraverso un IDP
	private Boolean registerFromProvider;

	public RegisterUtenteDTO() {
		super();
		domicilio = new DeComuneDTO();
		provincia = new DeProvinciaDTO();
		cittadinanza = new DeCittadinanzaDTO();
		documentoSoggiorno = new DeTitoloSoggiornoDTO();
		autenticazioneForte = false;
	}

	public DeComuneDTO getDomicilio() {
		return domicilio;
	}

	public void setDomicilio(DeComuneDTO domicilio) {
		this.domicilio = domicilio;
	}

	public DeProvinciaDTO getProvincia() {
		return provincia;
	}

	public void setProvincia(DeProvinciaDTO provincia) {
		this.provincia = provincia;
	}

	public String getIndirizzoPEC() {
		return indirizzoPEC;
	}

	public void setIndirizzoPEC(String indirizzoPEC) {
		this.indirizzoPEC = indirizzoPEC;
	}

	public Date getDataNascita() {
		return dataNascita;
	}

	public void setDataNascita(Date dataNascita) {
		this.dataNascita = dataNascita;
	}

	public String getDocumentoIdentita() {
		return documentoIdentita;
	}

	public void setDocumentoIdentita(String documentoIdentita) {
		this.documentoIdentita = documentoIdentita;
	}

	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	public Date getDataScadenzaDocumento() {
		return dataScadenzaDocumento;
	}

	public void setDataScadenzaDocumento(Date dataScadenzaDocumento) {
		this.dataScadenzaDocumento = dataScadenzaDocumento;
	}

	public String getNumeroAssicurata() {
		return numeroAssicurata;
	}

	public void setNumeroAssicurata(String numeroAssicurata) {
		this.numeroAssicurata = numeroAssicurata;
	}

	public Date getDataAssicurata() {
		return dataAssicurata;
	}

	public void setDataAssicurata(Date dataAssicurata) {
		this.dataAssicurata = dataAssicurata;
	}

	public String getCodiceRichiestaAutForte() {
		return codiceRichiestaAutForte;
	}

	public void setCodiceRichiestaAutForte(String codiceRichiestaAutForte) {
		this.codiceRichiestaAutForte = codiceRichiestaAutForte;
	}

	public DeCittadinanzaDTO getCittadinanza() {
		return cittadinanza;
	}

	public void setCittadinanza(DeCittadinanzaDTO cittadinanza) {
		this.cittadinanza = cittadinanza;
	}

	public DeTitoloSoggiornoDTO getDocumentoSoggiorno() {
		return documentoSoggiorno;
	}

	public void setDocumentoSoggiorno(DeTitoloSoggiornoDTO documentoSoggiorno) {
		this.documentoSoggiorno = documentoSoggiorno;
	}

	public Boolean getAutenticazioneForte() {
		return autenticazioneForte;
	}

	public void setAutenticazioneForte(Boolean autenticazioneForte) {
		this.autenticazioneForte = autenticazioneForte;
	}

	public Boolean getRegisterFromProvider() {
		return registerFromProvider;
	}

	public void setRegisterFromProvider(Boolean registerFromProvider) {
		this.registerFromProvider = registerFromProvider;
	}

	public String getCellulare() {
		return cellulare;
	}

	public void setCellulare(String cellulare) {
		this.cellulare = cellulare;
	}

}
