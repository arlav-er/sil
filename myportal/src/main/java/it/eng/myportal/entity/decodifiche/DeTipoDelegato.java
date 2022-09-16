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
@Table(name="de_tipo_delegato"
    ,schema="myportal"
)
public class DeTipoDelegato extends AbstractDecodeEntity  implements java.io.Serializable {
	private static final long serialVersionUID = 284353L;
     private String codTipoDelegato;
     private String descrizione;
     private Boolean flagAbilitato;
    
    
    public DeTipoDelegato() {
    }
    public DeTipoDelegato(String codTipoDelegato, String descrizione, Boolean flagAbilitato, Date dtInizioVal, Date dtFineVal) {
    	super(dtInizioVal, dtFineVal);
    	this.codTipoDelegato = codTipoDelegato;
        this.descrizione = descrizione;
        this.flagAbilitato = flagAbilitato;        
    }
    
     @Id 
    @Column(name="cod_tipo_delegato", unique=true, nullable=false, length=3)
    public String getCodTipoDelegato() {
        return this.codTipoDelegato;
    }
    public void setCodTipoDelegato(String codTipoDelegato) {
        this.codTipoDelegato = codTipoDelegato;
    }
    @Column(name="descrizione", nullable=false, length=250)
    public String getDescrizione() {
        return this.descrizione;
    }
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
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
