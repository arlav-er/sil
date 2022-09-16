package it.eng.myportal.entity;

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

@Entity
@Table(name="rv_agricolo"
    ,schema="myportal"
)
public class RvAgricolo extends AbstractEntity  implements java.io.Serializable {
	private static final long serialVersionUID = 3358489068506788612L;
	private Integer idRvAgricolo;

    private RvTestata rvTestata;
    private String descrizione;
    
    public RvAgricolo() {
    }
   
     @Id 
	@SequenceGenerator(name = "rv_agricolo_id_rv_agricolo_seq", sequenceName="rv_agricolo_id_rv_agricolo_seq", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "rv_agricolo_id_rv_agricolo_seq")
    @Column(name="id_rv_agricolo", unique=true, nullable=false)
    public Integer getIdRvAgricolo() {
        return this.idRvAgricolo;
    }
    public void setIdRvAgricolo(Integer idRvAgricolo) {
        this.idRvAgricolo = idRvAgricolo;
    }
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="id_rv_testata", nullable=false)
    public RvTestata getRvTestata() {
        return this.rvTestata;
    }
    public void setRvTestata(RvTestata rvTestata) {
        this.rvTestata = rvTestata;
    }
	@Column(name = "descrizione", nullable = false, length = 20)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

}
