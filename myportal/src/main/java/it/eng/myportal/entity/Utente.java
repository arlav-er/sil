package it.eng.myportal.entity;
// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

@Deprecated
public class Utente extends AbstractEntity  implements java.io.Serializable {
	private static final long serialVersionUID = 414536L;
     private Integer idUtente;
     private PfPrincipal pfPrincipal;
     private Set<CvDatiPersonali> cvDatiPersonalis = new HashSet<CvDatiPersonali>(0);
     private UtenteInfo utenteInfo;
     private Set<RvTestata> rvTestatas = new HashSet<RvTestata>(0);
     private Set<CvLetteraAcc> cvLetteraAccs = new HashSet<CvLetteraAcc>(0);
    public Utente() {
    }
  
     @Id 
	@SequenceGenerator(name = "utente_id_utente_seq", sequenceName="utente_id_utente_seq", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "utente_id_utente_seq")
    @Column(name="id_utente", unique=true, nullable=false)
    public Integer getIdUtente() {
        return this.idUtente;
    }
    public void setIdUtente(Integer idUtente) {
        this.idUtente = idUtente;
    }
@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="id_pf_principal", nullable=false)
    public PfPrincipal getPfPrincipal() {
        return this.pfPrincipal;
    }
    public void setPfPrincipal(PfPrincipal pfPrincipalByIdPfPrincipal) {
        this.pfPrincipal = pfPrincipalByIdPfPrincipal;
    }
    
@OneToMany(fetch=FetchType.LAZY, mappedBy="utente")
    public Set<CvDatiPersonali> getCvDatiPersonalis() {
        return this.cvDatiPersonalis;
    }
    public void setCvDatiPersonalis(Set<CvDatiPersonali> cvDatiPersonalis) {
        this.cvDatiPersonalis = cvDatiPersonalis;
    }
@OneToOne(fetch=FetchType.LAZY, mappedBy="utente")
    public UtenteInfo getUtenteInfo() {
        return this.utenteInfo;
    }
    public void setUtenteInfo(UtenteInfo utenteInfo) {
        this.utenteInfo = utenteInfo;
    }
@OneToMany(fetch=FetchType.LAZY, mappedBy="utente")
    public Set<RvTestata> getRvTestatas() {
        return this.rvTestatas;
    }
    public void setRvTestatas(Set<RvTestata> rvTestatas) {
        this.rvTestatas = rvTestatas;
    }
@OneToMany(fetch=FetchType.LAZY, mappedBy="utente")
    public Set<CvLetteraAcc> getCvLetteraAccs() {
        return this.cvLetteraAccs;
    }
    public void setCvLetteraAccs(Set<CvLetteraAcc> cvLetteraAccs) {
        this.cvLetteraAccs = cvLetteraAccs;
    }
}
