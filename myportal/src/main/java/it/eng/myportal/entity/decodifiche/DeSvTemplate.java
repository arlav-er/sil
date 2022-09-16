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
@Table(name="de_sv_template"
    ,schema="myportal"
)
public class DeSvTemplate  extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 803858L;
     private String codSvTemplate;
     private String descrizione;
    
    public DeSvTemplate() {
    }
    public DeSvTemplate(String codSvTemplate, String descrizione, Date dtInizioVal, Date dtFineVal) {
    	super(dtInizioVal, dtFineVal);
        this.codSvTemplate = codSvTemplate;
        this.descrizione = descrizione;
        
    }
    
     @Id 
    @Column(name="cod_sv_template", unique=true, nullable=false, length=5)
    public String getCodSvTemplate() {
        return this.codSvTemplate;
    }
    public void setCodSvTemplate(String codSvTemplate) {
        this.codSvTemplate = codSvTemplate;
    }
    @Column(name="descrizione", nullable=false, length=100)
    public String getDescrizione() {
        return this.descrizione;
    }
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
    
}
