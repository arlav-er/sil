package it.eng.myportal.dtos;


/**
 * Classe contenitore delle informazioni su un'Azienda in sessione.<br/>
 * Inserire in questo DTO <b>solo le informazioni che devono essere mantenute in sessione</b>
 * 
 * @author iescone, Rodi A.
 * @see AbstractUpdatableDTO
 * 
 */

public class AziendaSessionDTO extends AbstractUpdatablePkDTO {
	private static final long serialVersionUID = -2698965085713786797L;

	private DeComuneDTO comune;
	private String ragioneSociale;
	private String stileSelezionato;
	private String indirizzoSede;
	private String capSede;
	private String telefonoSede;
	private String faxSede;
	private String nomeReferente;
	private String cognomeReferente;
	private String emailReferente;
	private Integer idVetrina;

	// il flag è impostato a true se ha già compilato i dati per la richiesta di
	// accesso a SARE
	private Boolean abilitatoSare;
	// è impostato a true se l'accesso a sare è stato consentito
	private Boolean confermatoSare;

	private DeProvinciaDTO provincia;
	
	public AziendaSessionDTO() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRagioneSociale() {
		return ragioneSociale;
	}

	public void setRagioneSociale(String ragioneSociale) {
		this.ragioneSociale = ragioneSociale;
	}

	public String getStileSelezionato() {
		return stileSelezionato;
	}

	public void setStileSelezionato(String stileSelezionato) {
		this.stileSelezionato = stileSelezionato;
	}

	/**
	 * @return the idPfPrincipal
	 * @deprecated use getId()
	 */
	@Deprecated
	public Integer getIdPfPrincipal() {
		return id;
	}

	/**
	 * @param idPfPrincipal
	 *            the idPfPrincipal to set
	 * @deprecated use setId()           
	 */
	@Deprecated
	public void setIdPfPrincipal(Integer idPfPrincipal) {
		this.id = idPfPrincipal;
	}

	public DeComuneDTO getComune() {
		return comune;
	}

	public void setComune(DeComuneDTO comune) {
		this.comune = comune;
	}

	public String getIndirizzoSede() {
		return indirizzoSede;
	}

	public void setIndirizzoSede(String indirizzoSede) {
		this.indirizzoSede = indirizzoSede;
	}

	public String getCapSede() {
		return capSede;
	}

	public void setCapSede(String capSede) {
		this.capSede = capSede;
	}

	public String getTelefonoSede() {
		return telefonoSede;
	}

	public void setTelefonoSede(String telefonoSede) {
		this.telefonoSede = telefonoSede;
	}

	public String getFaxSede() {
		return faxSede;
	}

	public void setFaxSede(String faxSede) {
		this.faxSede = faxSede;
	}

	public String getNomeReferente() {
		return nomeReferente;
	}

	public void setNomeReferente(String nomeReferente) {
		this.nomeReferente = nomeReferente;
	}

	public String getCognomeReferente() {
		return cognomeReferente;
	}

	public void setCognomeReferente(String cognomeReferente) {
		this.cognomeReferente = cognomeReferente;
	}

	public String getEmailReferente() {
		return emailReferente;
	}

	public void setEmailReferente(String emailReferente) {
		this.emailReferente = emailReferente;
	}

	public Integer getIdVetrina() {
		return idVetrina;
	}

	public void setIdVetrina(Integer idVetrina) {
		this.idVetrina = idVetrina;
	}

	/**
	 * @deprecated use getId()
	 * @return
	 */
	@Deprecated
	public Integer getIdAziendaInfo() {
		return id;
	}

	/**
	 * @deprecated use setId();
	 * @param idAziendaInfo
	 */
	@Deprecated
	public void setIdAziendaInfo(Integer idAziendaInfo) {
		this.id = idAziendaInfo;
	}

	public Boolean getAbilitatoSare() {
		return abilitatoSare;
	}

	public void setAbilitatoSare(Boolean abilitatoSare) {
		this.abilitatoSare = abilitatoSare;
	}

	public Boolean getConfermatoSare() {
		return confermatoSare;
	}

	public void setConfermatoSare(Boolean richiestoSare) {
		this.confermatoSare = richiestoSare;
	}

	public DeProvinciaDTO getProvincia() {
		return provincia;
	}

	public void setProvincia(DeProvinciaDTO provincia) {
		this.provincia = provincia;
	}
}
