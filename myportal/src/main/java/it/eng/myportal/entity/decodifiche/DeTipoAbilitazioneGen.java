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
@Table(name="de_tipo_abilitazione_gen"
    ,schema="myportal"
)
public class DeTipoAbilitazioneGen  extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 312519L;
     private String codTipoAbilitazioneGen;
     private String descrizione;
     
    public DeTipoAbilitazioneGen() {
    }
    public DeTipoAbilitazioneGen(String codTipoAbilitazioneGen, String descrizione, Date dtInizioVal, Date dtFineVal) {
    	super(dtInizioVal, dtFineVal);
    	this.codTipoAbilitazioneGen = codTipoAbilitazioneGen;
        this.descrizione = descrizione;
        
    }
    
     @Id 
    @Column(name="cod_tipo_abilitazione_gen", unique=true, nullable=false, length=2)
    public String getCodTipoAbilitazioneGen() {
        return this.codTipoAbilitazioneGen;
    }
    public void setCodTipoAbilitazioneGen(String codTipoAbilitazioneGen) {
        this.codTipoAbilitazioneGen = codTipoAbilitazioneGen;
    }
    @Column(name="descrizione", nullable=false, length=200)
    public String getDescrizione() {
        return this.descrizione;
    }
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
   
}
