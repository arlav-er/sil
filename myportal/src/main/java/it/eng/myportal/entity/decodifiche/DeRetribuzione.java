package it.eng.myportal.entity.decodifiche;
// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;
@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY) @Entity
@Table(name="de_retribuzione"
    ,schema="myportal"
)
public class DeRetribuzione extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 748557L;
     private String codRetribuzione;
     private BigDecimal limInfDecimale;
     private BigDecimal limSupDecimale;
     
    public DeRetribuzione() {
    }
    public DeRetribuzione(String codRetribuzione, Date dtInizioVal, Date dtFineVal) {
    	super(dtInizioVal, dtFineVal);
        this.codRetribuzione = codRetribuzione;        
    }
    
     @Id 
    @Column(name="cod_retribuzione", unique=true, nullable=false, length=8)
    public String getCodRetribuzione() {
        return this.codRetribuzione;
    }
    public void setCodRetribuzione(String codRetribuzione) {
        this.codRetribuzione = codRetribuzione;
    }
    
    @Column(name="lim_inf_decimale", precision=12, scale=0)
    public BigDecimal getLimInfDecimale() {
        return this.limInfDecimale;
    }
    public void setLimInfDecimale(BigDecimal limInfDecimale) {
        this.limInfDecimale = limInfDecimale;
    }
    @Column(name="lim_sup_decimale", precision=12, scale=0)
    public BigDecimal getLimSupDecimale() {
        return this.limSupDecimale;
    }
    public void setLimSupDecimale(BigDecimal limSupDecimale) {
        this.limSupDecimale = limSupDecimale;
    }
  
}
