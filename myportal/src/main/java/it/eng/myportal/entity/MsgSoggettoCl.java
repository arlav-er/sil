package it.eng.myportal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import it.eng.myportal.entity.decodifiche.DeComune;

/**
 * MsgSoggettoCl
 * 
 * @author Rodi
 */
@Entity
@Table(name = "msg_soggetto_cl", schema = "myportal")
public class MsgSoggettoCl extends AbstractEntity implements java.io.Serializable {
	private static final long serialVersionUID = 978984L;

	private Integer idMsgSoggettoCl;
	private String codiceFiscale;	
	private String codIntermediario;	
	private String denominazione;
			 
    private String indirizzo;
    private DeComune deComune;
    private String telefono;
    private String email;
    private String codComunicazione;
    

	public MsgSoggettoCl() {
	}

	
	@Column(name="cod_comunicazione", length=25)
    public String getCodComunicazione() {
        return this.codComunicazione;
    }



	@Column(name="codice_fiscale", nullable=false, length=16)
    public String getCodiceFiscale() {
        return this.codiceFiscale;
    }



	@Column(name="cod_intermediario", length=11)
    public String getCodIntermediario() {
        return this.codIntermediario;
    }
    
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_comune", nullable=false)
    public DeComune getDeComune() {
        return this.deComune;
    }

    
    @Column(name="denominazione", nullable=false, length=100)
    public String getDenominazione() {
        return this.denominazione;
    }
    
    @Column(name="email", nullable=false, length=256)
    public String getEmail() {
        return this.email;
    }

    
    @Id
	@SequenceGenerator(name = "msg_soggetto_cl_id_msg_soggetto_cl_seq", sequenceName = "msg_soggetto_cl_id_msg_soggetto_cl_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "msg_soggetto_cl_id_msg_soggetto_cl_seq")
	@Column(name = "id_msg_soggetto_cl", unique = true, nullable = false)
	public Integer getIdMsgSoggettoCl() {
		return idMsgSoggettoCl;
	}
    
    @Column(name="indirizzo", length=100)
    public String getIndirizzo() {
        return this.indirizzo;
    }

    
    @Column(name="telefono", nullable=false, length=15)
    public String getTelefono() {
        return this.telefono;
    }
    
    public void setCodComunicazione(String codiceOffertacandidatura) {
        this.codComunicazione = codiceOffertacandidatura;
    }

    
	public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }
    
    public void setCodIntermediario(String codIntermediario) {
        this.codIntermediario = codIntermediario;
    }

    
    public void setDeComune(DeComune deComune) {
        this.deComune = deComune;
    }
    
    public void setDenominazione(String denominazione) {
        this.denominazione = denominazione;
    }

    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setIdMsgSoggettoCl(Integer idMsgSoggettoCl) {
		this.idMsgSoggettoCl = idMsgSoggettoCl;
	}

    
    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

  
	

}
