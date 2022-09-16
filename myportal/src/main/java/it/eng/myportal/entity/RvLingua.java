package it.eng.myportal.entity;
// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import it.eng.myportal.entity.decodifiche.DeLingua;
/**
 * RvLingua generated by hbm2java
 */
@Entity
@Table(name="rv_lingua"
    ,schema="myportal"
)
public class RvLingua extends AbstractEntity  implements java.io.Serializable {
	private static final long serialVersionUID = 852775L;
     private Integer idRvLingua;

     private DeLingua deLingua;

     private RvTestata rvTestata;


    public RvLingua() {
    }
   
     @Id 
	@SequenceGenerator(name = "rv_lingua_id_rv_lingua_seq", sequenceName="rv_lingua_id_rv_lingua_seq", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "rv_lingua_id_rv_lingua_seq")
    @Column(name="id_rv_lingua", unique=true, nullable=false)
    public Integer getIdRvLingua() {
        return this.idRvLingua;
    }
    public void setIdRvLingua(Integer idRvLingua) {
        this.idRvLingua = idRvLingua;
    }

@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="cod_lingua", nullable=false)
    public DeLingua getDeLingua() {
        return this.deLingua;
    }
    public void setDeLingua(DeLingua deLingua) {
        this.deLingua = deLingua;
    }

@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="id_rv_testata", nullable=false)
    public RvTestata getRvTestata() {
        return this.rvTestata;
    }
    public void setRvTestata(RvTestata rvTestata) {
        this.rvTestata = rvTestata;
    }
    
}
