package it.eng.myportal.dtos;

import java.util.Arrays;
import java.util.Date;

import javax.validation.constraints.Pattern;

import it.eng.myportal.enums.SoftwareSAREUtilizzato;

/**
 * Classe DTO con tutte le informazioni ulteriori su un'azienda.
 * 
 * @author Rodi A.
 * 
 */
public class AziendaInfoDTO extends AbstractUpdatablePkDTO implements IUpdatable {
	private static final long serialVersionUID = 1L;

	private String ragioneSociale;
	@Pattern(regexp = "[0-9]{11}", message = "Formato Partita Iva errato")
	private String partitaIva;
	private String codiceFiscale;
	private String stileSelezionato;
	private DeComuneDTO comune;

	/**
	 * è uguale all'idPfPrincipal
	 */
	private Integer idVetrina;
	private byte[] logo;
	private Boolean flagAgenziaEstera;
	private String domanda;
	private String risposta;

	private DeProvinciaDTO provinciaRiferimento;
	/**
	 * richiedente
	 */
	private String nomeRic;
	private String cognomeRic;
	private DeComuneDTO comuneNascitaRic;
	private Date dataNascitaRic;
	private String indirizzoRic;
	private String capRic;
	/**
	 * email registrazione (richiedente) - da persistere in PfPrincipal
	 */
	private String email;
	/**
	 * referenteSare
	 */
	private String referenteSare;
	private String telefonoReferente;
	private String nomeReferente;
	private String cognomeReferente;
	private String emailReferente;
	private String cellReferente;

	/**
	 * mittenteSare
	 */
	private String mittenteSare;
	/**
	 * Sede Legale
	 */
	private SedeDTO sedeLegale = new SedeDTO();
	/**
	 * Sede (operativa)
	 */
	private SedeDTO sedeOperativa = new SedeDTO();
	/**
	 * DATI SARE
	 */
	private DeTipoAbilitatoDTO tipoAbilitato;
	private DeTipoDelegatoDTO tipoDelegato;

	private Boolean richiestaAccessoSARE;
	private String codAutorizzazioneSare;
	private String descAutorizzazioneSare;
	private String codTipoUtenteSare;
	private AgenziaDTO agenzia;
	private SoggettoDTO soggettoAbilitato;
	private SoftwareSAREUtilizzato softwareSAREUtilizzato;

	// il flag è impostato a true se ha già compilato i dati per la richiesta di
	// accesso a SARE
	private Boolean abilitatoSare;
	// è impostato a true se l'accesso a sare è stato consentito
	private Boolean confermatoSare;

	private Boolean flagValida;

	private Boolean flagAbilitatoDichiarazioneNeet;

	private DeNaturaGiuridicaDTO deNaturaGiuridicaDTO;

	public byte[] getLogo() {
		return logo;
	}

	public void setLogo(byte[] foto) {
		this.logo = foto;
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

	public DeComuneDTO getComune() {
		return comune;
	}

	public void setComune(DeComuneDTO comune) {
		this.comune = comune;
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

	/**
	 * @deprecated
	 * @return
	 */
	@Deprecated
	public Integer getIdVetrina() {
		return idVetrina;
	}

	/**
	 * @deprecated
	 * @param idVetrina
	 */
	@Deprecated
	public void setIdVetrina(Integer idVetrina) {
		this.idVetrina = idVetrina;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format(
				"AziendaInfoDTO [ragioneSociale=%s, partitaIva=%s, codiceFiscale=%s, stileSelezionato=%s, comune=%s, idVetrina=%s, logo=%s, flagAgenziaEstera=%s, domanda=%s, risposta=%s, provinciaRiferimento=%s, nomeRic=%s, cognomeRic=%s, comuneNascitaRic=%s, dataNascitaRic=%s, indirizzoRic=%s, capRic=%s, email=%s, referenteSare=%s, telefonoReferente=%s, nomeReferente=%s, cognomeReferente=%s, emailReferente=%s, mittenteSare=%s, sedeLegale=%s, sedeOperativa=%s, tipoAbilitato=%s, tipoDelegato=%s, richiestaAccessoSARE=%s, codAutorizzazioneSare=%s, descAutorizzazioneSare=%s, codTipoUtenteSare=%s, agenzia=%s, soggettoAbilitato=%s, softwareSAREUtilizzato=%s]",
				ragioneSociale, partitaIva, codiceFiscale, stileSelezionato, comune, idVetrina, Arrays.toString(logo),
				flagAgenziaEstera, domanda, risposta, provinciaRiferimento, nomeRic, cognomeRic, comuneNascitaRic,
				dataNascitaRic, indirizzoRic, capRic, email, referenteSare, telefonoReferente, nomeReferente,
				cognomeReferente, emailReferente, mittenteSare, sedeLegale, sedeOperativa, tipoAbilitato, tipoDelegato,
				richiestaAccessoSARE, codAutorizzazioneSare, descAutorizzazioneSare, codTipoUtenteSare, agenzia,
				soggettoAbilitato, softwareSAREUtilizzato);
	}

	public AziendaInfoDTO() {
		super();
		comuneNascitaRic = new DeComuneDTO();
		sedeOperativa = new SedeDTO();
		sedeLegale = new SedeDTO();
		agenzia = new AgenziaDTO();
		soggettoAbilitato = new SoggettoDTO();
		richiestaAccessoSARE = false;
		comune = new DeComuneDTO();
		tipoDelegato = new DeTipoDelegatoDTO();
		tipoAbilitato = new DeTipoAbilitatoDTO();
		provinciaRiferimento = new DeProvinciaDTO();
		flagAbilitatoDichiarazioneNeet = false;
	}

	public String getReferenteSare() {
		return referenteSare;
	}

	public void setReferenteSare(String referenteSare) {
		this.referenteSare = referenteSare;
	}

	public String getTelefonoReferente() {
		return telefonoReferente;
	}

	public void setTelefonoReferente(String telefonoReferente) {
		this.telefonoReferente = telefonoReferente;
	}

	public String getPartitaIva() {
		return partitaIva;
	}

	public void setPartitaIva(String partitaIva) {
		this.partitaIva = partitaIva;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public DeComuneDTO getComuneNascitaRic() {
		return comuneNascitaRic;
	}

	public void setComuneNascitaRic(DeComuneDTO comuneNascitaRic) {
		this.comuneNascitaRic = comuneNascitaRic;
	}

	public Date getDataNascitaRic() {
		return dataNascitaRic;
	}

	public void setDataNascitaRic(Date dataNascitaRic) {
		this.dataNascitaRic = dataNascitaRic;
	}

	public String getIndirizzoRic() {
		return indirizzoRic;
	}

	public void setIndirizzoRic(String indirizzoRic) {
		this.indirizzoRic = indirizzoRic;
	}

	public String getCapRic() {
		return capRic;
	}

	public void setCapRic(String capRic) {
		this.capRic = capRic;
	}

	public String getCognomeRic() {
		return cognomeRic;
	}

	public void setCognomeRic(String cognomeRic) {
		this.cognomeRic = cognomeRic;
	}

	public String getNomeRic() {
		return nomeRic;
	}

	public void setNomeRic(String nomeRic) {
		this.nomeRic = nomeRic;
	}

	public String getMittenteSare() {
		return mittenteSare;
	}

	public void setMittenteSare(String mittenteSare) {
		this.mittenteSare = mittenteSare;
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

	public DeTipoAbilitatoDTO getTipoAbilitato() {
		return tipoAbilitato;
	}

	public void setTipoAbilitato(DeTipoAbilitatoDTO tipoAbilitato) {
		this.tipoAbilitato = tipoAbilitato;
	}

	public DeTipoDelegatoDTO getTipoDelegato() {
		return tipoDelegato;
	}

	public void setTipoDelegato(DeTipoDelegatoDTO tipoDelegato) {
		this.tipoDelegato = tipoDelegato;
	}

	public Boolean getRichiestaAccessoSARE() {
		return richiestaAccessoSARE;
	}

	public void setRichiestaAccessoSARE(Boolean richiestaAccessoSARE) {
		this.richiestaAccessoSARE = richiestaAccessoSARE;
	}

	public SedeDTO getSedeLegale() {
		return sedeLegale;
	}

	public void setSedeLegale(SedeDTO sedeLegale) {
		this.sedeLegale = sedeLegale;
	}

	public SedeDTO getSedeOperativa() {
		return sedeOperativa;
	}

	public void setSedeOperativa(SedeDTO sedeOperativa) {
		this.sedeOperativa = sedeOperativa;
	}

	public AgenziaDTO getAgenzia() {
		return agenzia;
	}

	public void setAgenzia(AgenziaDTO agenzia) {
		this.agenzia = agenzia;
	}

	public SoggettoDTO getSoggettoAbilitato() {
		return soggettoAbilitato;
	}

	public void setSoggettoAbilitato(SoggettoDTO soggettoAbilitato) {
		this.soggettoAbilitato = soggettoAbilitato;
	}

	public DeProvinciaDTO getProvinciaRiferimento() {
		return provinciaRiferimento;
	}

	public void setProvinciaRiferimento(DeProvinciaDTO provinciaRiferimento) {
		this.provinciaRiferimento = provinciaRiferimento;
	}

	public String getCodTipoUtenteSare() {
		return codTipoUtenteSare;
	}

	public void setCodTipoUtenteSare(String codTipoUtenteSare) {
		this.codTipoUtenteSare = codTipoUtenteSare;
	}

	public String getCodAutorizzazioneSare() {
		return codAutorizzazioneSare;
	}

	public void setCodAutorizzazioneSare(String codTipoUtenteSare) {
		this.codAutorizzazioneSare = codTipoUtenteSare;
	}

	public String getDescAutorizzazioneSare() {
		return descAutorizzazioneSare;
	}

	public void setDescAutorizzazioneSare(String descTipoUtenteSare) {
		this.descAutorizzazioneSare = descTipoUtenteSare;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getFlagAgenziaEstera() {
		return flagAgenziaEstera;
	}

	public void setFlagAgenziaEstera(Boolean flagAgenziaEstera) {
		this.flagAgenziaEstera = flagAgenziaEstera;
	}

	public SoftwareSAREUtilizzato getSoftwareSAREUtilizzato() {
		return softwareSAREUtilizzato;
	}

	public void setSoftwareSAREUtilizzato(SoftwareSAREUtilizzato softwareSAREUtilizzato) {
		this.softwareSAREUtilizzato = softwareSAREUtilizzato;
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

	public Boolean getFlagValida() {
		return flagValida;
	}

	public void setFlagValida(Boolean flagValida) {
		this.flagValida = flagValida;
	}

	public Boolean getFlagAbilitatoDichiarazioneNeet() {
		return flagAbilitatoDichiarazioneNeet;
	}

	public void setFlagAbilitatoDichiarazioneNeet(Boolean flagAbilitatoDichiarazioneNeet) {
		this.flagAbilitatoDichiarazioneNeet = flagAbilitatoDichiarazioneNeet;
	}

	public String getCellReferente() {
		return cellReferente;
	}

	public void setCellReferente(String cellReferente) {
		this.cellReferente = cellReferente;
	}

	public DeNaturaGiuridicaDTO getDeNaturaGiuridicaDTO() {
		return deNaturaGiuridicaDTO;
	}

	public void setDeNaturaGiuridicaDTO(DeNaturaGiuridicaDTO deNaturaGiuridicaDTO) {
		this.deNaturaGiuridicaDTO = deNaturaGiuridicaDTO;
	}

}
