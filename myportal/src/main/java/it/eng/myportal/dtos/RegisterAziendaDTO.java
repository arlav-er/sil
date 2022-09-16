package it.eng.myportal.dtos;

import it.eng.myportal.enums.SoftwareSAREUtilizzato;

import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

/**
 * DTO per la registrazione di una nuova Azienda al sistema
 * 
 * @author Rodi A.
 * 
 */
public class RegisterAziendaDTO extends RegisterDTO {

    private static final long serialVersionUID = -3957256636721264654L;

    @Size(max = 80)
    private String ragioneSociale;
    @Valid
    private SedeDTO sedeOperativa;

    private DeProvinciaDTO provincia;

    private boolean flgAbilitato;

    private Date dtScadenzaPassword;

    private String strRequestStatus;

    /**
     * Dati soggetto che richiede la registrazione per conto azienda
     */
    private DeComuneDTO comuneNascitaRic;
    private Date dataNascitaRic;

    /**
     * Determina se l'azienda richiede l'accesso al SARE -> devono essere mostrati & compilati i campi aggiuntivi
     */
    private Boolean richiestaAccessoSARE;

    /**
     * Referente SARE
     */
    @Size(min = 1, max = 13)
    private String mittenteSare;
    @Size(min = 1, max = 50)
    private String referenteSare;
    @Email
    private String emailReferenteSare;

    /**
     * Telefono Referente SARE (utilizzo dei campi di RegisterDTO ) <br>
     * Domiciliato per la carica in (utilizzo dei campi di RegisterDTO )
     */

    private String tipoRichiedente;
    private String tipoDelegato;

    @Valid
    private AgenziaDTO agenzia;
    @Valid
    private SoggettoDTO soggettoAbilitato;

    @NotNull(message = "Specificare il Software utilizzato")
    private SoftwareSAREUtilizzato softwareSAREUtilizzato;

    private String esitoRegistrazione;
    private String warnRegistrazione;

    /**
     * Ulteriori dati aziendali
     */
    @Pattern(regexp = "[0-9]{11}", message = "Formato Partita Iva errato")
    private String partitaIva;
    @Valid
    private SedeDTO sedeLegale;

    private DeTipoUtenteSareDTO deTipoUtenteSare;

    private boolean flgValida;
    
    public RegisterAziendaDTO() {
        super();
        provincia = new DeProvinciaDTO();
        comuneNascitaRic = new DeComuneDTO();
        sedeOperativa = new SedeDTO();
        sedeLegale = new SedeDTO();
        agenzia = new AgenziaDTO();
        soggettoAbilitato = new SoggettoDTO();
        richiestaAccessoSARE = false;
    }

    public DeProvinciaDTO getProvincia() {
        return provincia;
    }

    public void setProvincia(DeProvinciaDTO provincia) {
        this.provincia = provincia;
    }

    public String getRagioneSociale() {
        return ragioneSociale;
    }

    public void setRagioneSociale(String ragioneSociale) {
        this.ragioneSociale = ragioneSociale;
    }

    public String getMittenteSare() {
        return mittenteSare;
    }

    public void setMittenteSare(String mittenteSare) {
        this.mittenteSare = mittenteSare;
    }

    public SoftwareSAREUtilizzato getSoftwareSAREUtilizzato() {
		return softwareSAREUtilizzato;
	}

	public void setSoftwareSAREUtilizzato(
			SoftwareSAREUtilizzato softwareSAREUtilizzato) {
		this.softwareSAREUtilizzato = softwareSAREUtilizzato;
	}
	
    public Date getDataNascitaRic() {
        return dataNascitaRic;
    }

	public void setDataNascitaRic(Date dataNascita) {
        this.dataNascitaRic = dataNascita;
    }

    public DeComuneDTO getComuneNascitaRic() {
        return comuneNascitaRic;
    }

    public void setComuneNascitaRic(DeComuneDTO comuneNascita) {
        this.comuneNascitaRic = comuneNascita;
    }

    public String getPartitaIva() {
        return partitaIva;
    }

    public void setPartitaIva(String partitaIva) {
        this.partitaIva = partitaIva;
    }

    public String getReferenteSare() {
        return referenteSare;
    }

    public void setReferenteSare(String referenteSare) {
        this.referenteSare = referenteSare;
    }

    public String getEmailReferenteSare() {
        return emailReferenteSare;
    }

    public void setEmailReferenteSare(String emailReferenteSare) {
        this.emailReferenteSare = emailReferenteSare;
    }

    public SedeDTO getSedeOperativa() {
        return sedeOperativa;
    }

    public void setSedeOperativa(SedeDTO sedeOperativa) {
        this.sedeOperativa = sedeOperativa;
    }

    public SedeDTO getSedeLegale() {
        return sedeLegale;
    }

    public void setSedeLegale(SedeDTO sedeLegale) {
        this.sedeLegale = sedeLegale;
    }

    public String getTipoRichiedente() {
        return tipoRichiedente;
    }

    public void setTipoRichiedente(String tipoRichiedente) {
        this.tipoRichiedente = tipoRichiedente;
    }

    public String getTipoDelegato() {
        return tipoDelegato;
    }

    public void setTipoDelegato(String tipoDelegato) {
        this.tipoDelegato = tipoDelegato;
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

    public String getNomeRic() {
        return getNome();
    }

    public void setNomeRic(String nomeRic) {
        setNome(nomeRic);
    }

    public String getCognomeRic() {
        return getCognome();
    }

    public void setCognomeRic(String cognomeRic) {
        setCognome(cognomeRic);
    }

    public Boolean getRichiestaAccessoSARE() {
        return richiestaAccessoSARE;
    }

    public void setRichiestaAccessoSARE(Boolean richiestaAccessoSARE) {
        this.richiestaAccessoSARE = richiestaAccessoSARE;
    }

    /**
     * @return the flgAbilitato
     */
    public boolean isFlgAbilitato() {
        return flgAbilitato;
    }

    /**
     * @param flgAbilitato
     *            the flgAbilitato to set
     */
    public void setFlgAbilitato(boolean flgAbilitato) {
        this.flgAbilitato = flgAbilitato;
    }

    /**
     * @return the dtScadenzaPassword
     */
    public Date getDtScadenzaPassword() {
        return dtScadenzaPassword;
    }

    /**
     * @param dtScadenzaPassword
     *            the dtScadenzaPassword to set
     */
    public void setDtScadenzaPassword(Date dtScadenzaPassword) {
        this.dtScadenzaPassword = dtScadenzaPassword;
    }

    /**
     * @return the strRequestStatus
     */
    public String getStrRequestStatus() {
        return strRequestStatus;
    }

    /**
     * @param strRequestStatus
     *            the strRequestStatus to set
     */
    public void setStrRequestStatus(String strRequestStatus) {
        this.strRequestStatus = strRequestStatus;
    }

    /**
     * @return the esitoRegistrazione
     */
    public String getEsitoRegistrazione() {
        return esitoRegistrazione;
    }

    /**
     * @param esitoRegistrazione
     *            the esitoRegistrazione to set
     */
    public void setErroriImportazione(String esitoRegistrazione) {
        this.esitoRegistrazione = esitoRegistrazione;
    }

    /**
     * @return the warnRegistrazione
     */
    public String getWarnRegistrazione() {
        return warnRegistrazione;
    }

    /**
     * @param warnRegistrazione
     *            the warnRegistrazione to set
     */
    public void setWarnRegistrazione(String warnRegistrazione) {
        this.warnRegistrazione = warnRegistrazione;
    }

    public DeTipoUtenteSareDTO getDeTipoUtenteSare() {
        return deTipoUtenteSare;
    }

    public void setDeTipoUtenteSare(DeTipoUtenteSareDTO deTipoUtenteSare) {
        this.deTipoUtenteSare = deTipoUtenteSare;
    }

	public boolean isFlgValida() {
		return flgValida;
	}

	public void setFlgValida(boolean flgValida) {
		this.flgValida = flgValida;
	}

}
