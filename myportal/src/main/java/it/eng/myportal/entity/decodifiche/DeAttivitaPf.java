package it.eng.myportal.entity.decodifiche;
// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;
@Entity
@Table(name="de_attivita_pf"
    ,schema="myportal"
)
@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
public class DeAttivitaPf extends AbstractDecodeEntity  implements java.io.Serializable {
	private static final long serialVersionUID = 335330L;
     private String codAttivitaPf;
     private String attivitaPf;
     private String descrizione;
    
    public DeAttivitaPf() {
    }
    public DeAttivitaPf(String codAttivitaPf, String attivitaPf, String descrizione, Date dtInizioVal, Date dtFineVal) {
    	super(dtInizioVal, dtFineVal);
    	this.codAttivitaPf = codAttivitaPf;
        this.attivitaPf = attivitaPf;
        this.descrizione = descrizione;
        
    }
    
     @Id 
    @Column(name="cod_attivita_pf", unique=true, nullable=false, length=50)
    public String getCodAttivitaPf() {
        return this.codAttivitaPf;
    }
    public void setCodAttivitaPf(String codAttivitaPf) {
        this.codAttivitaPf = codAttivitaPf;
    }
    @Column(name="attivita_pf", nullable=false, length=30)
    public String getAttivitaPf() {
        return this.attivitaPf;
    }
    public void setAttivitaPf(String attivitaPf) {
        this.attivitaPf = attivitaPf;
    }
    @Column(name="descrizione", nullable=false, length=100)
    public String getDescrizione() {
        return this.descrizione;
    }
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
   
}
