package it.eng.myportal.entity.decodifiche;
// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import it.eng.myportal.entity.AbstractDecodeEntity;
@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY) @Entity
@Table(name="de_tipo_abilitato"
    ,schema="myportal"
)
public class DeTipoAbilitato  extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 665369L;
     private String codTipoAbilitato;
     private String descrizione;
     private Boolean flagDelegato;
     private Boolean flagAbilitato;
     
    public DeTipoAbilitato() {
    }
    public DeTipoAbilitato(String codTipoAbilitato, String descrizione, Boolean flagDelegato, Boolean flagAbilitato, Date dtInizioVal, Date dtFineVal) {
    	super(dtInizioVal, dtFineVal);
        this.codTipoAbilitato = codTipoAbilitato;
        this.descrizione = descrizione;
        this.flagDelegato = flagDelegato;
        this.flagAbilitato = flagAbilitato;
        
    }
    
     @Id 
    @Column(name="cod_tipo_abilitato", unique=true, nullable=false, length=2)
    public String getCodTipoAbilitato() {
        return this.codTipoAbilitato;
    }
    public void setCodTipoAbilitato(String codTipoAbilitato) {
        this.codTipoAbilitato = codTipoAbilitato;
    }
    @Column(name="descrizione", nullable=false, length=250)
    public String getDescrizione() {
        return this.descrizione;
    }
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
@Type(type="yes_no")
    @Column(name="flag_delegato", nullable=false, length=1)
    public Boolean getFlagDelegato() {
        return this.flagDelegato;
    }
    public void setFlagDelegato(Boolean flagDelegato) {
        this.flagDelegato = flagDelegato;
    }
@Type(type="yes_no")
    @Column(name="flag_abilitato", nullable=false, length=1)
    public Boolean getFlagAbilitato() {
        return this.flagAbilitato;
    }
    public void setFlagAbilitato(Boolean flagAbilitato) {
        this.flagAbilitato = flagAbilitato;
    }
  
}
