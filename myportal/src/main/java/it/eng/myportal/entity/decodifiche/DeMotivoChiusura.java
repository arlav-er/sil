package it.eng.myportal.entity.decodifiche;
// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;
@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY) @Entity
@Table(name="de_motivo_chiusura"
    ,schema="myportal"
)
public class DeMotivoChiusura extends AbstractDecodeEntity  implements java.io.Serializable {
	private static final long serialVersionUID = 255276L;
     private String codMotivoChiusura;
     private String descrizione;
     
    
    public DeMotivoChiusura() {
    }
    
    @Id 
    @Column(name="cod_motivo_chiusura", unique=true, nullable=false, length=8)
    public String getCodMotivoChiusura() {
		return codMotivoChiusura;
	}

	public void setCodMotivoChiusura(String codMotivoChiusura) {
		this.codMotivoChiusura = codMotivoChiusura;
	}
    
    @Column(name="descrizione", nullable=false, length=100)
    public String getDescrizione() {
        return this.descrizione;
    }
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
   
	
}
