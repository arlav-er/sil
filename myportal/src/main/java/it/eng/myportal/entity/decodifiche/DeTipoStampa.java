package it.eng.myportal.entity.decodifiche;
// Generated Apr 20, 2012 10:40:45 AM by Hibernate Tools 3.4.0.CR1
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;

@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY) @Entity
@Table(name="de_tipo_stampa"
    ,schema="myportal"
)
public class DeTipoStampa extends AbstractDecodeEntity   implements java.io.Serializable {
	private static final long serialVersionUID = 822232L;
     private String codTipoStampa;
     private String descrizione;
     
    public DeTipoStampa() {
    }
    public DeTipoStampa(String codTipoStampa, String descrizione, Date dtInizioVal, Date dtFineVal) {
    	super(dtInizioVal, dtFineVal);
        this.codTipoStampa = codTipoStampa;
        this.descrizione = descrizione;        
    }
    
    @Id 
    @Column(name="cod_tipo_stampa", unique=true, nullable=false, length=8)
    public String getCodTipoStampa() {
        return this.codTipoStampa;
    }
    public void setCodTipoStampa(String codTipoStampa) {
        this.codTipoStampa = codTipoStampa;
    }
    @Column(name="descrizione", nullable=false)
    public String getDescrizione() {
        return this.descrizione;
    }
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
   }
