package it.eng.myportal.entity.decodifiche;
// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;
@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY) @Entity
@Table(name="de_sv_sezione"
    ,schema="myportal"
)
public class DeSvSezione  extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 11502L;
     private String codSvSezione;
     private String descrizione;
    
    public DeSvSezione() {
    }
    public DeSvSezione(String codSvSezione, String descrizione, Date dtInizioVal, Date dtFineVal) {
    	super(dtInizioVal, dtFineVal);
        this.codSvSezione = codSvSezione;
        this.descrizione = descrizione;
        
    }
    
     @Id 
    @Column(name="cod_sv_sezione", unique=true, nullable=false, length=5)
    public String getCodSvSezione() {
        return this.codSvSezione;
    }
    public void setCodSvSezione(String codSvSezione) {
        this.codSvSezione = codSvSezione;
    }
    @Column(name="descrizione", nullable=false, length=100)
    public String getDescrizione() {
        return this.descrizione;
    }
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
   
}
