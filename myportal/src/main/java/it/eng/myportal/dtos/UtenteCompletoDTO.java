package it.eng.myportal.dtos;

import it.eng.myportal.enums.TipoAccount;
import it.eng.myportal.enums.TipoRegistrazione;

import java.util.Date;
import java.util.List;

/**
 * DTO che contiene sia le informazioni di UtenteDTO, che estende, che di UtenteInfoDTO permettendo di recuperare
 * immediatamente tutte le informazioni su un utente.
 * 
 * @author Rodi A.
 * 
 */
public class UtenteCompletoDTO implements IDTO {

	private static final long serialVersionUID = 1958724094512553933L;

	private UtenteDTO utenteDTO;
	private UtenteInfoDTO utenteInfo;
	private String username;
	private String indirizzoDomicilioCompleto;
	private String tipoAccount;
	private String tipoRegistrazione;

	public UtenteCompletoDTO() {
		super();
		utenteDTO = new UtenteDTO();
		utenteInfo = new UtenteInfoDTO();
	}

	public UtenteCompletoDTO(UtenteDTO dto) {
		super();
		this.utenteDTO = dto;
	}

	public UtenteCompletoDTO(UtenteDTO utenteDTO, UtenteInfoDTO utenteInfo) {
		super();
		this.utenteDTO = utenteDTO;
		this.utenteInfo = utenteInfo;
	}

	public UtenteCompletoDTO(String username, String codiceFiscale, String nome, String cognome, Date dtNascita,
			String email, String cittadinanzaDesc, boolean flagAbilitato, boolean flagAbilitatoServizi) {
		utenteDTO = new UtenteDTO();
		utenteInfo = new UtenteInfoDTO();
		this.setUsername(username);
		this.setCodiceFiscale(codiceFiscale);
		this.setNome(nome);
		this.setCognome(cognome);
		this.setDataNascita(dtNascita);
		this.setEmail(email);
		if (cittadinanzaDesc != null) {
			this.setCittadinanza(new DeCittadinanzaDTO());
			this.getCittadinanza().setDescrizione(cittadinanzaDesc);
		}
		if (flagAbilitato) {
			this.setTipoAccount(TipoAccount.ACCOUNT_ATTIVO.getLabel());
		} else {
			this.setTipoAccount(TipoAccount.ACCOUNT_NON_ATTIVO.getLabel());
		}
		if (flagAbilitatoServizi) {
			this.setTipoRegistrazione(TipoRegistrazione.REGISTRAZIONE_FORTE.getLabel());
		} else {
			this.setTipoRegistrazione(TipoRegistrazione.REGISTRAZIONE_DEBOLE.getLabel());
		}
	}

	public Integer getIdUtenteInfo() {
		return utenteInfo.getId();
	}

	public Date getDtmInsUtenteInfo() {
		return utenteInfo.getDtmIns();
	}

	public void setIdUtenteInfo(Integer id) {
		utenteInfo.setId(id);
	}

	public Integer getIdUtente() {
		return utenteInfo.getIdUtente();
	}

	public void setDtmInsUtenteInfo(Date dtmIns) {
		utenteInfo.setDtmIns(dtmIns);
	}

	public void setIdUtente(Integer idUtente) {
		utenteInfo.setIdUtente(idUtente);
	}

	public Date getDtmModUtenteInfo() {
		return utenteInfo.getDtmMod();
	}

	public String getTelUfficio() {
		return utenteInfo.getTelUfficio();
	}

	public void setDtmModUtenteInfo(Date dtmMod) {
		utenteInfo.setDtmMod(dtmMod);
	}

	public void setTelUfficio(String telUfficio) {
		utenteInfo.setTelUfficio(telUfficio);
	}

	public Integer getIdPrincipalInsUtenteInfo() {
		return utenteInfo.getIdPrincipalIns();
	}

	public void setIdPrincipalInsUtenteInfo(Integer idPrincipalIns) {
		utenteInfo.setIdPrincipalIns(idPrincipalIns);
	}

	public String getTelCasa() {
		return utenteInfo.getTelCasa();
	}

	public void setTelCasa(String telCasa) {
		utenteInfo.setTelCasa(telCasa);
	}

	public Integer getIdPrincipalModUtenteInfo() {
		return utenteInfo.getIdPrincipalMod();
	}

	public String getFax() {
		return utenteInfo.getFax();
	}

	public void setIdPrincipalModUtenteInfo(Integer idPrincipalMod) {
		utenteInfo.setIdPrincipalMod(idPrincipalMod);
	}

	public void setFax(String fax) {
		utenteInfo.setFax(fax);
	}

	public String getCellulare() {
		return utenteInfo.getCellulare();
	}

	public void setCellulare(String cellulare) {
		utenteInfo.setCellulare(cellulare);
	}

	public byte[] getFoto() {
		return utenteInfo.getFoto();
	}

	public void setFoto(byte[] foto) {
		utenteInfo.setFoto(foto);
	}

	public String getStileSelezionato() {
		return utenteInfo.getStileSelezionato();
	}

	public void setStileSelezionato(String stileSelezionato) {
		utenteInfo.setStileSelezionato(stileSelezionato);
	}

	public Date getDtmIns() {
		return utenteDTO.getDtmIns();
	}

	public void setDtmIns(Date dtmIns) {
		utenteDTO.setDtmIns(dtmIns);
	}

	public boolean equals(Object o) {
		return utenteDTO.equals(o);
	}

	public Date getDtmMod() {
		return utenteDTO.getDtmMod();
	}

	public void setDtmMod(Date dtmMod) {
		utenteDTO.setDtmMod(dtmMod);
	}

	public String getNome() {
		return utenteDTO.getNome();
	}

	public Integer getIdPrincipalIns() {
		return utenteDTO.getIdPrincipalIns();
	}

	public void setNome(String nome) {
		utenteDTO.setNome(nome);
	}

	public void setIdPrincipalIns(Integer idPrincipalIns) {
		utenteDTO.setIdPrincipalIns(idPrincipalIns);
	}

	public String getCognome() {
		return utenteDTO.getCognome();
	}

	public void setCognome(String cognome) {
		utenteDTO.setCognome(cognome);
	}

	public Integer getIdPrincipalMod() {
		return utenteDTO.getIdPrincipalMod();
	}

	public int hashCode() {
		return utenteDTO.hashCode();
	}

	public void setIdPrincipalMod(Integer idPrincipalMod) {
		utenteDTO.setIdPrincipalMod(idPrincipalMod);
	}

	public String getEmail() {
		return utenteDTO.getEmail();
	}

	public void setEmail(String email) {
		utenteDTO.setEmail(email);
	}

	public Integer getId() {
		return utenteDTO.getId();
	}

	public void setId(Integer idPfPrincipal) {
		utenteDTO.setId(idPfPrincipal);
	}

	public Boolean getAbilitatoServizi() {
		return utenteDTO.getAbilitatoServizi();
	}

	public void setAbilitatoServizi(Boolean abilitatoServizi) {
		utenteDTO.setAbilitatoServizi(abilitatoServizi);
	}

	public Boolean getAbilitato() {
		return utenteDTO.getAbilitato();
	}

	public void setAbilitato(Boolean abilitato) {
		utenteDTO.setAbilitato(abilitato);
	}

	public DeComuneDTO getComuneDomicilio() {
		return utenteInfo.getComuneDomicilio();
	}

	public void setComuneDomicilio(DeComuneDTO comuneDomicilio) {
		utenteInfo.setComuneDomicilio(comuneDomicilio);
	}

	public DeComuneDTO getComuneNascita() {
		return utenteInfo.getComuneNascita();
	}

	public void setComuneNascita(DeComuneDTO comuneNascita) {
		utenteInfo.setComuneNascita(comuneNascita);
	}

	public String getIndirizzoDomicilio() {
		return utenteInfo.getIndirizzoDomicilio();
	}

	public void setIndirizzoDomicilio(String indirizzoDomicilio) {
		utenteInfo.setIndirizzoDomicilio(indirizzoDomicilio);
	}

	public DeProvinciaDTO getProvinciaRiferimento() {
		return utenteInfo.getProvinciaRiferimento();
	}

	public void setProvinciaRiferimento(DeProvinciaDTO provinciaRiferimento) {
		utenteInfo.setProvinciaRiferimento(provinciaRiferimento);
	}

	public String getDomanda() {
		return utenteInfo.getDomanda();
	}

	public void setDomanda(String domanda) {
		utenteInfo.setDomanda(domanda);
	}

	public String getRisposta() {
		return utenteInfo.getRisposta();
	}

	public void setRisposta(String risposta) {
		utenteInfo.setRisposta(risposta);
	}

	public String getCodiceFiscale() {
		return utenteInfo.getCodiceFiscale();
	}

	public void setCodiceFiscale(String codiceFiscale) {
		utenteInfo.setCodiceFiscale(codiceFiscale);
	}

	public UtenteDTO getUtenteDTO() {
		return utenteDTO;
	}

	public void setUtenteDTO(UtenteDTO utenteDTO) {
		this.utenteDTO = utenteDTO;
	}

	public UtenteInfoDTO getUtenteInfo() {
		return utenteInfo;
	}

	public void setUtenteInfo(UtenteInfoDTO utenteInfo) {
		this.utenteInfo = utenteInfo;
	}

	public String getTokenAbilitazioneServizi() {
		return utenteDTO.getTokenAbilitazioneServizi();
	}

	public void setTokenAbilitazioneServizi(String tokenAbilitazioneServizi) {
		utenteDTO.setTokenAbilitazioneServizi(tokenAbilitazioneServizi);
	}

	public String getIndirizzoPEC() {
		return utenteInfo.getIndirizzoPEC();
	}

	public void setIndirizzoPEC(String indirizzoPEC) {
		utenteInfo.setIndirizzoPEC(indirizzoPEC);
	}

	public Date getDataNascita() {
		return utenteInfo.getDataNascita();
	}

	public void setDataNascita(Date dataNascita) {
		utenteInfo.setDataNascita(dataNascita);
	}

	public DeCittadinanzaDTO getCittadinanza() {
		if (utenteInfo.getCittadinanza() == null) {
			utenteInfo.setCittadinanza(new DeCittadinanzaDTO());
		}
		return utenteInfo.getCittadinanza();
	}

	public void setCittadinanza(DeCittadinanzaDTO cittadinanza) {
		utenteInfo.setCittadinanza(cittadinanza);
	}

	public String getDocumentoIdentita() {

		return utenteInfo.getDocumentoIdentita();
	}

	public void setDocumentoIdentita(String documentoIdentita) {
		utenteInfo.setDocumentoIdentita(documentoIdentita);
	}

	public DeTitoloSoggiornoDTO getDocumentoSoggiorno() {
		if (utenteInfo.getDocumentoSoggiorno() == null) {
			utenteInfo.setDocumentoSoggiorno(new DeTitoloSoggiornoDTO());
		}
		return utenteInfo.getDocumentoSoggiorno();
	}

	public void setDocumentoSoggiorno(DeTitoloSoggiornoDTO documentoSoggiorno) {
		utenteInfo.setDocumentoSoggiorno(documentoSoggiorno);
	}

	public String getNumeroDocumento() {
		return utenteInfo.getNumeroDocumento();
	}

	public void setNumeroDocumento(String numeroDocumento) {
		utenteInfo.setNumeroDocumento(numeroDocumento);
	}

	public Date getDataScadenzaDocumento() {
		return utenteInfo.getDataScadenzaDocumento();
	}

	public void setDataScadenzaDocumento(Date dataScadenzaDocumento) {
		utenteInfo.setDataScadenzaDocumento(dataScadenzaDocumento);
	}

	public String getNumeroAssicurata() {
		return utenteInfo.getNumeroAssicurata();
	}

	public void setNumeroAssicurata(String numeroAssicurata) {
		utenteInfo.setNumeroAssicurata(numeroAssicurata);
	}

	public Date getDataAssicurata() {
		return utenteInfo.getDataAssicurata();
	}

	public void setDataAssicurata(Date dataAssicurata) {
		utenteInfo.setDataAssicurata(dataAssicurata);
	}

	public String getCodiceRichiestaAutForte() {
		return utenteInfo.getCodiceRichiestaAutForte();
	}

	public void setCodiceRichiestaAutForte(String codiceRichiestaAutForte) {
		utenteInfo.setCodiceRichiestaAutForte(codiceRichiestaAutForte);
	}

	public Boolean getAcceptedInformativaDid() {
		return utenteInfo.getAcceptedInformativaDid();
	}

	public void setAcceptedInformativaDid(Boolean acceptedInformativaDid) {
		utenteInfo.setAcceptedInformativaDid(acceptedInformativaDid);
	}

	public Boolean getAcceptedInformativaStatoOcc() {
		return utenteInfo.getAcceptedInformativaStatoOcc();
	}

	public void setAcceptedInformativaStatoOcc(Boolean acceptedInformativaStatoOcc) {
		utenteInfo.setAcceptedInformativaStatoOcc(acceptedInformativaStatoOcc);
	}

	public Boolean getAcceptedInformativaPercLav() {
		return utenteInfo.getAcceptedInformativaPercLav();
	}

	public void setAcceptedInformativaPercLav(Boolean acceptedInformativaPercLav) {
		utenteInfo.setAcceptedInformativaPercLav(acceptedInformativaPercLav);
	}

	public String getEmailPEC() {
		return utenteInfo.getIndirizzoPEC();
	}

	public void setEmailPEC(String email) {
		utenteInfo.setIndirizzoPEC(email);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getIndirizzoDomicilioCompleto() {
		return indirizzoDomicilioCompleto;
	}

	public void setIndirizzoDomicilioCompleto(String indirizzoDomicilioCompleto) {
		this.indirizzoDomicilioCompleto = indirizzoDomicilioCompleto;
	}

	public String getTipoAccount() {
		return tipoAccount;
	}

	public void setTipoAccount(String tipoAccount) {
		this.tipoAccount = tipoAccount;
	}

	public String getTipoRegistrazione() {
		return tipoRegistrazione;
	}

	public void setTipoRegistrazione(String tipoRegistrazione) {
		this.tipoRegistrazione = tipoRegistrazione;
	}

	public List<CvDatiPersonaliDTO> getCurricula() {
		return utenteDTO.getCurricula();
	}

	public void setCurricula(List<CvDatiPersonaliDTO> curricula) {
		utenteDTO.setCurricula(curricula);
	}

	public List<CvDatiPersonaliDTO> getCurriculaPalesi() {
		return utenteDTO.getCurriculaPalesi();
	}

	public void setCurriculaPalesi(List<CvDatiPersonaliDTO> curricula) {
		utenteDTO.setCurriculaPalesi(curricula);
	}

	public List<CvLetteraAccDTO> getLettere() {
		return utenteDTO.getLettere();
	}

	public void setLettere(List<CvLetteraAccDTO> lettere) {
		utenteDTO.setLettere(lettere);
	}

	public DeGenereDTO getGenere() {
		return utenteInfo.getGenere();
	}

	public void setGenere(DeGenereDTO genere) {
		utenteInfo.setGenere(genere);
	}

	public DeMotivoPermessoDTO getMotivoPermesso() {
		return utenteInfo.getMotivoPermesso();
	}

	public void setMotivoPermesso(DeMotivoPermessoDTO motivoPermesso) {
		utenteInfo.setMotivoPermesso(motivoPermesso);
	}

	public String getCapDomicilio() {
		return utenteInfo.getCapDomicilio();
	}

	public void setCapDomicilio(String capDomicilio) {
		utenteInfo.setCapDomicilio(capDomicilio);
	}

	public String getCapResidenza() {
		return utenteInfo.getCapResidenza();
	}

	public void setCapResidenza(String capResidenza) {
		utenteInfo.setCapResidenza(capResidenza);
	}

	public DeComuneDTO getComuneResidenza() {
		return utenteInfo.getComuneResidenza();
	}

	public void setComuneResidenza(DeComuneDTO comuneResidenza) {
		utenteInfo.setComuneResidenza(comuneResidenza);
	}

	public String getIndirizzoResidenza() {
		return utenteInfo.getIndirizzoResidenza();
	}

	public void setIndirizzoResidenza(String indirizzoResidenza) {
		utenteInfo.setIndirizzoResidenza(indirizzoResidenza);
	}

	public Boolean getFlgConsensoSms() {
		return utenteInfo.getFlgConsensoSms();
	}

	public void setFlgConsensoSms(Boolean flgConsensoSms) {
		utenteInfo.setFlgConsensoSms(flgConsensoSms);
	}

}
