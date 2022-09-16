package it.eng.myportal.dtos;

import it.eng.myportal.validator.Cap;
import it.eng.myportal.validator.Telefono;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class SedeDTO implements IDTO {

    private static final long serialVersionUID = -6782614706617785145L;
    @Size(max = 80)
    private String indirizzo;
    @Cap
    private String cap;
    @NotNull
    private DeComuneDTO comune;
    @Telefono
    private String telefono;
    @Telefono
    private String fax;

    public SedeDTO() {
        super();
        this.comune = new DeComuneDTO();
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    public DeComuneDTO getComune() {
        return comune;
    }

    public void setComune(DeComuneDTO comune) {
        this.comune = comune;
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
}
