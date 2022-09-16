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
@Table(name="de_tipo_formazione"
    ,schema="myportal"
)
public class DeTipoFormazione extends AbstractDecodeEntity  implements java.io.Serializable {
	private static final long serialVersionUID = 884031L;
     private String codTipoFormazione;
     private String descrizione;
     private String descrizioneEstesa;
     
    public DeTipoFormazione() {
    }
    public DeTipoFormazione(String codTipoFormazione, String descrizione, Date dtInizioVal, Date dtFineVal) {
    	super(dtInizioVal, dtFineVal);
        this.codTipoFormazione = codTipoFormazione;
        this.descrizione = descrizione;
    
    }
    
     @Id 
    @Column(name="cod_tipo_formazione", unique=true, nullable=false, length=8)
    public String getCodTipoFormazione() {
        return this.codTipoFormazione;
    }
    public void setCodTipoFormazione(String codTipoFormazione) {
        this.codTipoFormazione = codTipoFormazione;
    }
    @Column(name="descrizione", nullable=false, length=100)
    public String getDescrizione() {
        return this.descrizione;
    }
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
    @Column(name="descrizione_estesa", length=4000)
    public String getDescrizioneEstesa() {
        return this.descrizioneEstesa;
    }
    public void setDescrizioneEstesa(String descrizioneEstesa) {
        this.descrizioneEstesa = descrizioneEstesa;
    }
  
}
