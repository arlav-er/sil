package it.eng.myportal.entity.decodifiche;
// Generated 31-lug-2012 16.32.42 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;

@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY) @Entity
@Table(name="de_tipo_lavoro"
    ,schema="myportal"
)
public class DeTipoLavoro  extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 722793L;
     private String codLavoro;
     private String tipo;
     private String descrizione;
    public DeTipoLavoro() {
    }
    public DeTipoLavoro(String codLavoro, String tipo, String descrizione) {
       this.codLavoro = codLavoro;
       this.tipo = tipo;
       this.descrizione = descrizione;
    }
     @Id 
    @Column(name="cod_lavoro", unique=true, nullable=false, length=9)
    public String getCodLavoro() {
        return this.codLavoro;
    }
    public void setCodLavoro(String codLavoro) {
        this.codLavoro = codLavoro;
    }
    @Column(name="tipo", nullable=false, length=200)
    public String getTipo() {
        return this.tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    @Column(name="descrizione", nullable=false, length=1000)
    public String getDescrizione() {
        return this.descrizione;
    }
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
}
