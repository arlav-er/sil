package it.eng.myportal.entity.decodifiche;
// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;
import it.eng.myportal.entity.Poi;
@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY) @Entity
@Table(name="de_tipo_poi"
    ,schema="myportal"
)
public class DeTipoPoi  extends AbstractDecodeEntity  implements java.io.Serializable {
	private static final long serialVersionUID = 565473L;
     private String codTipoPoi;
     private String descrizione;
     
     private Set<Poi> pois = new HashSet<Poi>(0);
    public DeTipoPoi() {
    }
    public DeTipoPoi(String codTipoPoi, String descrizione, Date dtInizioVal, Date dtFineVal) {
    	super(dtInizioVal, dtFineVal);
    	this.codTipoPoi = codTipoPoi;
        this.descrizione = descrizione;
       
    }
    public DeTipoPoi(String codTipoPoi, String descrizione, Date dtInizioVal, Date dtFineVal, Set<Poi> pois) {
    	super(dtInizioVal, dtFineVal);
    	this.codTipoPoi = codTipoPoi;
    	this.descrizione = descrizione;       
    	this.pois = pois;
    }
     @Id 
    @Column(name="cod_tipo_poi", unique=true, nullable=false, length=10)
    public String getCodTipoPoi() {
        return this.codTipoPoi;
    }
    public void setCodTipoPoi(String codTipoPoi) {
        this.codTipoPoi = codTipoPoi; 
    }
    @Column(name="descrizione", nullable=false, length=200)
    public String getDescrizione() {
        return this.descrizione;
    }
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
   
    @OneToMany(fetch=FetchType.LAZY, mappedBy="deTipoPoi")
    public Set<Poi> getPois() {
        return this.pois;
    }
    public void setPois(Set<Poi> pois) {
        this.pois = pois;
    }
}
