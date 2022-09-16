package it.eng.myportal.entity.decodifiche;
// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;
@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY) @Entity
@Table(name="de_ampiezza"
    ,schema="myportal"
)
public class DeAmpiezza extends AbstractDecodeEntity  implements java.io.Serializable {
	private static final long serialVersionUID = 255276L;
     private String codAmpiezza;
     private String descrizione;
     
    
    public DeAmpiezza() {
    }
    
    @Id 
    @Column(name="cod_ampiezza", unique=true, nullable=false, length=8)
    public String getCodAmpiezza() {
		return codAmpiezza;
	}

	public void setCodAmpiezza(String codAmpiezza) {
		this.codAmpiezza = codAmpiezza;
	}
    
    @Column(name="descrizione", nullable=false, length=100)
    public String getDescrizione() {
        return this.descrizione;
    }
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
   
	
}
