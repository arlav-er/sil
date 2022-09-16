package it.eng.myportal.dtos;

import it.eng.myportal.validator.Telefono;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

public class RegisterDTO implements IDTO {

    private static final long serialVersionUID = 174570859410746541L;

    /**
     * Dati account
     */
    @NotNull
    @Size(min = 3, max = 16)    
    private String username;
    @NotNull
    private String password;
    @NotNull
    private String passwordConfirm;
    @NotNull
    @Email
    private String email;
    @NotNull
    @Email
    private String emailConfirm;

    private String activateToken;
    private Boolean attivo;
    private Boolean acceptInformativa;
    @Size(max = 30)
    private String nome;
    @Size(max = 30)
    private String cognome;
    @Pattern(message = "Codice fiscale errato", regexp = "[a-zA-Z]{6}[0-9lmnpqrstuvLMNPQRSTUV]{2}[a-zA-Z][0-9lmnpqrstuvLMNPQRSTUV]{2}[a-zA-Z][0-9lmnpqrstuvLMNPQRSTUV]{3}[a-zA-Z]|[0-9]{11}")
    private String codiceFiscale;
    
    /**
     * va su principal.indirizzoUtente - 150 <br>
     * ma anche su aziendaInfo.IndirizzoRic -100<br>
     * lo metto come il più stringente<br>
     */
    @Size(max = 100)
    private String indirizzo;
    @Valid
    private DeComuneDTO comune;
    
    @Size(max = 5)
    private String cap;
    @Telefono
    private String telefono;
    
    @Size(max = 255)
    private String domanda;
    @Size(max = 255)
    private String risposta;

    public RegisterDTO() {
        super();
        comune = new DeComuneDTO();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmailConfirm() {
        return emailConfirm;
    }

    public void setEmailConfirm(String emailConfirm) {
        this.emailConfirm = emailConfirm;
    }

    public String getActivateToken() {
        return activateToken;
    }

    public void setActivateToken(String activateToken) {
        this.activateToken = activateToken;
    }

    public Boolean isAttivo() {
        return attivo;
    }

    public void setAttivo(Boolean attivo) {
        this.attivo = attivo;
    }

    public Boolean getAcceptInformativa() {
        return acceptInformativa;
    }

    public void setAcceptInformativa(Boolean acceptInformativa) {
        this.acceptInformativa = acceptInformativa;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public DeComuneDTO getComune() {
        return comune;
    }

    public void setComune(DeComuneDTO comune) {
        this.comune = comune;
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
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

}
