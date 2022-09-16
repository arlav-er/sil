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
@Table(name="de_filtro"
    ,schema="myportal"
)
public class DeFiltro extends AbstractDecodeEntity  implements java.io.Serializable {
	private static final long serialVersionUID = 838308L;
     private String codFiltro;
     private String filtro;
     private String descrizione;
     
     public DeFiltro() {
    }
    public DeFiltro(String codFiltro, String filtro, String descrizione, Date dtInizioVal, Date dtFineVal) {
    	super(dtInizioVal, dtFineVal);
        this.codFiltro = codFiltro;
        this.filtro = filtro;
        this.descrizione = descrizione;
       
    }
  
     @Id 
    @Column(name="cod_filtro", unique=true, nullable=false, length=8)
    public String getCodFiltro() {
        return this.codFiltro;
    }
    public void setCodFiltro(String codFiltro) {
        this.codFiltro = codFiltro;
    }
    @Column(name="filtro", nullable=false, length=30)
    public String getFiltro() {
        return this.filtro;
    }
    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }
    @Column(name="descrizione", nullable=false, length=100)
    public String getDescrizione() {
        return this.descrizione;
    }
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
   
}
