package it.eng.myportal.dtos;

import java.util.Arrays;
import java.util.Date;

/**
 * Classe DTO con tutte le informazioni ulteriori su un utente.
 * 
 * @author Rodi A.
 * 
 */
public class UtenteInfoDTO extends AbstractUpdatablePkDTO implements IUpdatable {
	private static final long serialVersionUID = 1L;

	private Integer idUtente;
	private String telUfficio;
	private String telCasa;
	private String fax;
	private String cellulare;
	private String cellulareOTP;
	private byte[] foto;
	private String stileSelezionato;
	private DeComuneDTO comuneDomicilio;
	private DeComuneDTO comuneNascita;
	private String indirizzoDomicilio;
	private DeProvinciaDTO provinciaRiferimento;
	private String domanda;
	private String risposta;
	private String indirizzoPEC;
	private String codiceFiscale;
	private Date dataNascita;
	private DeCittadinanzaDTO cittadinanza;
	private String documentoIdentita;
	private DeTitoloSoggiornoDTO documentoSoggiorno;
	private String numeroDocumento;
	private Date dataScadenzaDocumento;
	private String numeroAssicurata;
	private Date dataAssicurata;
	private String codiceRichiestaAutForte;
	private String codStatoAtipico;
	private String codServiziAmministrativi;

	private Boolean acceptedInformativaDid;
	private Boolean acceptedInformativaStatoOcc;
	private Boolean acceptedInformativaPercLav;
	private Boolean acceptedInformativaConfermaPeriodica;
	private Boolean acceptedInformativaRinnovoPatto;

	private String capDomicilio;
	private DeGenereDTO genere;
	private DeMotivoPermessoDTO motivoPermesso;

	private DeComuneDTO comuneResidenza;
	private String indirizzoResidenza;
	private String capResidenza;

	private Boolean flgConsensoSms;

	public UtenteInfoDTO() {
		super();
		this.comuneDomicilio = new DeComuneDTO();
		this.comuneNascita = new DeComuneDTO();
		this.cittadinanza = new DeCittadinanzaDTO();
		this.documentoSoggiorno = new DeTitoloSoggiornoDTO();
	}

	public Integer getIdUtente() {
		return idUtente;
	}

	public void setIdUtente(Integer idUtente) {
		this.idUtente = idUtente;
	}

	public String getTelUfficio() {
		return telUfficio;
	}

	public void setTelUfficio(String telUfficio) {
		this.telUfficio = telUfficio;
	}

	public String getTelCasa() {
		return telCasa;
	}

	public void setTelCasa(String telCasa) {
		this.telCasa = telCasa;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getCellulare() {
		return cellulare;
	}

	public void setCellulare(String cellulare) {
		this.cellulare = cellulare;
	}

	public byte[] getFoto() {
		return foto;
	}

	public void setFoto(byte[] foto) {
		this.foto = foto;
	}

	public String getStileSelezionato() {
		return stileSelezionato;
	}

	public void setStileSelezionato(String stileSelezionato) {
		this.stileSelezionato = stileSelezionato;
	}

	public DeComuneDTO getComuneDomicilio() {
		return comuneDomicilio;
	}

	public void setComuneDomicilio(DeComuneDTO comuneDomicilio) {
		this.comuneDomicilio = comuneDomicilio;
	}

	public DeComuneDTO getComuneNascita() {
		return comuneNascita;
	}

	public void setComuneNascita(DeComuneDTO comuneNascita) {
		this.comuneNascita = comuneNascita;
	}

	/**
	 * @return the indirizzoDomicilio
	 */
	public String getIndirizzoDomicilio() {
		return indirizzoDomicilio;
	}

	/**
	 * @param indirizzoDomicilio
	 *            the indirizzoDomicilio to set
	 */
	public void setIndirizzoDomicilio(String indirizzoDomicilio) {
		this.indirizzoDomicilio = indirizzoDomicilio;
	}

	@Override
	public String toString() {
		return String
				.format("UtenteInfoDTO [idUtente=%s, telUfficio=%s, telCasa=%s, fax=%s, cellulare=%s, foto=%s, stileSelezionato=%s, comuneDomicilio=%s, comuneNascita=%s, indirizzoDomicilio=%s]",
						idUtente, telUfficio, telCasa, fax, cellulare, Arrays.toString(foto), stileSelezionato,
						comuneDomicilio, comuneNascita, indirizzoDomicilio);
	}

	public DeProvinciaDTO getProvinciaRiferimento() {
		return provinciaRiferimento;
	}

	public void setProvinciaRiferimento(DeProvinciaDTO provinciaRiferimento) {
		this.provinciaRiferimento = provinciaRiferimento;
	}

	public String getDomanda() {
		return domanda;
	}

	public void setDomanda(String domanda) {
		this.domanda = domanda;
	}

	public String getRisposta() {
		return risposta;
	}

	public void setRisposta(String risposta) {
		this.risposta = risposta;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
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

	public DeTitoloSoggiornoDTO getDocumentoSoggiorno() {
		return documentoSoggiorno;
	}

	public void setDocumentoSoggiorno(DeTitoloSoggiornoDTO documentoSoggiorno) {
		this.documentoSoggiorno = documentoSoggiorno;
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

	public Boolean getAcceptedInformativaDid() {
		return acceptedInformativaDid;
	}

	public void setAcceptedInformativaDid(Boolean acceptedInformativaDid) {
		this.acceptedInformativaDid = acceptedInformativaDid;
	}

	public Boolean getAcceptedInformativaStatoOcc() {
		return acceptedInformativaStatoOcc;
	}

	public void setAcceptedInformativaStatoOcc(Boolean acceptedInformativaStatoOcc) {
		this.acceptedInformativaStatoOcc = acceptedInformativaStatoOcc;
	}

	public Boolean getAcceptedInformativaPercLav() {
		return acceptedInformativaPercLav;
	}

	public void setAcceptedInformativaPercLav(Boolean acceptedInformativaPercLav) {
		this.acceptedInformativaPercLav = acceptedInformativaPercLav;
	}

	public Boolean getAcceptedInformativaConfermaPeriodica() {
		return acceptedInformativaConfermaPeriodica;
	}

	public void setAcceptedInformativaConfermaPeriodica(Boolean acceptedInformativaConfermaPeriodica) {
		this.acceptedInformativaConfermaPeriodica = acceptedInformativaConfermaPeriodica;
	}

	public Boolean getAcceptedInformativaRinnovoPatto() {
		return acceptedInformativaRinnovoPatto;
	}

	public void setAcceptedInformativaRinnovoPatto(Boolean acceptedInformativaRinnovoPatto) {
		this.acceptedInformativaRinnovoPatto = acceptedInformativaRinnovoPatto;
	}

	public String getCodStatoAtipico() {
		return codStatoAtipico;
	}

	public void setCodStatoAtipico(String codStatoAtipico) {
		this.codStatoAtipico = codStatoAtipico;
	}

	public String getCapDomicilio() {
		return capDomicilio;
	}

	public void setCapDomicilio(String capDomicilio) {
		this.capDomicilio = capDomicilio;
	}

	public DeGenereDTO getGenere() {
		return genere;
	}

	public void setGenere(DeGenereDTO genere) {
		this.genere = genere;
	}

	public DeMotivoPermessoDTO getMotivoPermesso() {
		return motivoPermesso;
	}

	public void setMotivoPermesso(DeMotivoPermessoDTO motivoPermesso) {
		this.motivoPermesso = motivoPermesso;
	}

	public DeComuneDTO getComuneResidenza() {
		return comuneResidenza;
	}

	public void setComuneResidenza(DeComuneDTO comuneResidenza) {
		this.comuneResidenza = comuneResidenza;
	}

	public String getIndirizzoResidenza() {
		return indirizzoResidenza;
	}

	public void setIndirizzoResidenza(String indirizzoResidenza) {
		this.indirizzoResidenza = indirizzoResidenza;
	}

	public String getCapResidenza() {
		return capResidenza;
	}

	public void setCapResidenza(String capResidenza) {
		this.capResidenza = capResidenza;
	}

	public Boolean getFlgConsensoSms() {
		return flgConsensoSms;
	}

	public void setFlgConsensoSms(Boolean flgConsensoSms) {
		this.flgConsensoSms = flgConsensoSms;
	}

	public String getCodServiziAmministrativi() {
		return codServiziAmministrativi;
	}

	public void setCodServiziAmministrativi(String codServiziAmministrativi) {
		this.codServiziAmministrativi = codServiziAmministrativi;
	}

	public String getCellulareOTP() {
		return cellulareOTP;
	}

	public void setCellulareOTP(String cellulareOTP) {
		this.cellulareOTP = cellulareOTP;
	}

}
