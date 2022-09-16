package it.eng.myportal.entity.decodifiche;
// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;
@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY) @Entity
@Table(name="de_tipo_comunicazione_cl"
    ,schema="myportal"
)
public class DeTipoComunicazioneCl extends AbstractDecodeEntity  implements java.io.Serializable {
	private static final long serialVersionUID = 255276L;
     private String codTipoComunicazioneCl;
     private String descrizione;
     
    
    public DeTipoComunicazioneCl() {
    	codTipoComunicazioneCl = "";
        descrizione = "";
    }
    
    @Id 
    @Column(name="cod_tipo_comunicazione_cl", unique=true, nullable=false, length=8)
    public String getCodTipoComunicazioneCl() {
		return codTipoComunicazioneCl;
	}

	public void setCodTipoComunicazioneCl(String codTipoComunicazioneCl) {
		this.codTipoComunicazioneCl = codTipoComunicazioneCl;
	}
    
    @Column(name="descrizione", nullable=false, length=100)
    public String getDescrizione() {
        return this.descrizione;
    }
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
   
	
}
