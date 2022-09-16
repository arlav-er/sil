package it.eng.myportal.entity.decodifiche;

import it.eng.myportal.entity.AbstractDecodeEntity;

// Rinominata, da cancellare
//@Cacheable @Entity
//@Table(name="de_provenienza_vacancy"
//    ,schema="myportal"
//)
public class DeProvenienzaVacancy extends AbstractDecodeEntity  implements java.io.Serializable {
	/*private static final long serialVersionUID = 551991L;
     private String codProvenienzaVacancy;
     private String descrizione;
     
     private Set<VaDatiVacancy> vaDatiVacancies = new HashSet<VaDatiVacancy>(0);
    public DeProvenienzaVacancy() {
    }
    public DeProvenienzaVacancy(String codProvenienzaVacancy, String descrizione, Date dtInizioVal, Date dtFineVal) {
    	super(dtInizioVal, dtFineVal);
        this.codProvenienzaVacancy = codProvenienzaVacancy;
        this.descrizione = descrizione;
        
    }
    
     @Id 
    @Column(name="cod_provenienza_vacancy", unique=true, nullable=false, length=8)
    public String getCodProvenienzaVacancy() {
        return this.codProvenienzaVacancy;
    }
    public void setCodProvenienzaVacancy(String codProvenienzaVacancy) {
        this.codProvenienzaVacancy = codProvenienzaVacancy;
    }
    @Column(name="descrizione", nullable=false, length=200)
    public String getDescrizione() {
        return this.descrizione;
    }
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
    
@OneToMany(fetch=FetchType.LAZY, mappedBy="deProvenienzaVacancy")
    public Set<VaDatiVacancy> getVaDatiVacancies() {
        return this.vaDatiVacancies;
    }
    public void setVaDatiVacancies(Set<VaDatiVacancy> vaDatiVacancies) {
        this.vaDatiVacancies = vaDatiVacancies;
    }*/
}
