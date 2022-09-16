package it.eng.myportal.entity.decodifiche;
// Generated Apr 20, 2012 10:40:45 AM by Hibernate Tools 3.4.0.CR1
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;

@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name="de_tipo_servizio",schema="mycas")
public class DeTipoServizio extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 248738L;
     private String codTipoServizio;
     private String descrizione;
   
    public DeTipoServizio() {
    }
    public DeTipoServizio(String codTipoServizio, Date dtInizioVal, Date dtFineVal) {
    	super(dtInizioVal, dtFineVal);
        this.codTipoServizio = codTipoServizio;
        
    }
    
    @Id 
    @Column(name="cod_tipo_servizio", unique=true, nullable=false, length=8)
    public String getCodTipoServizio() {
        return this.codTipoServizio;
    }
    public void setCodTipoServizio(String codTipoServizio) {
        this.codTipoServizio = codTipoServizio;
    }
    @Column(name="descrizione", length=100)
    public String getDescrizione() {
        return this.descrizione;
    }
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
    
}
