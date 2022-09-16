package it.eng.myportal.entity.decodifiche;
// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;
@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name="de_corso"
    ,schema="myportal"
)
public class DeCorso extends AbstractDecodeEntity  implements java.io.Serializable {
	private static final long serialVersionUID = 362978L;
     private String codCorso;
     private String descrizione;
     
    public DeCorso() {
    }
    public DeCorso(String codCorso, String descrizione, Date dtInizioVal, Date dtFineVal) {
    	super(dtInizioVal, dtFineVal);
    	this.codCorso = codCorso;
        this.descrizione = descrizione;
        
    }
    
     @Id 
    @Column(name="cod_corso", unique=true, nullable=false, length=13)
    public String getCodCorso() {
        return this.codCorso;
    }
    public void setCodCorso(String codCorso) {
        this.codCorso = codCorso;
    }
    @Column(name="descrizione", nullable=false, length=300)
    public String getDescrizione() {
        return this.descrizione;
    }
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
  
}
