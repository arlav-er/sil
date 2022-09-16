package it.eng.myportal.entity.decodifiche;
// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;
@Entity
@Table(name="de_abilitazione_gen" ,schema="myportal")
@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
public class DeAbilitazioneGen extends AbstractDecodeEntity  implements java.io.Serializable {
	private static final long serialVersionUID = 962760L;
     private String codDeAbilitazioneGen;
     private DeTipoAbilitazioneGen deTipoAbilitazioneGen;
     private String descrizione;
     
    public DeAbilitazioneGen() {
    }
    public DeAbilitazioneGen(String codDeAbilitazioneGen, DeTipoAbilitazioneGen deTipoAbilitazioneGen, String descrizione, Date dtInizioVal, Date dtFineVal) {
       super(dtInizioVal, dtFineVal);
    	this.codDeAbilitazioneGen = codDeAbilitazioneGen;
       this.deTipoAbilitazioneGen = deTipoAbilitazioneGen;
       this.descrizione = descrizione;
       
    }
     @Id 
    @Column(name="cod_de_abilitazione_gen", unique=true, nullable=false, length=8)
    public String getCodDeAbilitazioneGen() {
        return this.codDeAbilitazioneGen;
    }
    public void setCodDeAbilitazioneGen(String codDeAbilitazioneGen) {
        this.codDeAbilitazioneGen = codDeAbilitazioneGen;
    }
@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="cod_tipo_abilitazione_gen", nullable=false)
    public DeTipoAbilitazioneGen getDeTipoAbilitazioneGen() {
        return this.deTipoAbilitazioneGen;
    }
    public void setDeTipoAbilitazioneGen(DeTipoAbilitazioneGen deTipoAbilitazioneGen) {
        this.deTipoAbilitazioneGen = deTipoAbilitazioneGen;
    }
    @Column(name="descrizione", nullable=false, length=100)
    public String getDescrizione() {
        return this.descrizione;
    }
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
  
}
