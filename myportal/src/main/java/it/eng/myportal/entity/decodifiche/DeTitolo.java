package it.eng.myportal.entity.decodifiche;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import it.eng.myportal.entity.AbstractDecodeEntity;
import it.eng.myportal.entity.CvIstruzione;
import it.eng.myportal.entity.RvTitolo;
import it.eng.myportal.entity.VaIstruzione;

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_titolo", schema = "myportal")
@NamedQueries({ 
	@NamedQuery(name = "findDeTitoloForTree", query = "SELECT m FROM DeTitolo m LEFT JOIN FETCH m.padre WHERE m.dtFineVal >= (:parDate) AND m.dtInizioVal <= (:parDate) AND m.flagIdo = :potatura"),
	@NamedQuery(name = "findDeTitoloPadreById", query = "SELECT m.padre FROM DeTitolo m WHERE m.dtFineVal >= (:parDate) AND m.dtInizioVal <= (:parDate) AND m.codTitolo = :parCodMans"),
	@NamedQuery(name = "findDeTitoloFigliById", query = "SELECT m FROM DeTitolo m WHERE m.dtFineVal >= (:parDate) AND m.dtInizioVal <= (:parDate) AND m.padre.codTitolo = :parCodMans"),
	@NamedQuery(name = "findDeTitoloPadri", query = "SELECT titolo FROM DeTitolo titolo WHERE titolo.padre IS NULL AND titolo.dtInizioVal < :data AND titolo.dtFineVal > :data "), 	
	@NamedQuery(name = "findDeTitoloConferimentoDidBySuggestion", query = "SELECT titolo FROM DeTitolo titolo WHERE titolo.flgConferimentoDid = true AND titolo.dtInizioVal < :data AND titolo.dtFineVal > :data AND UPPER(titolo.descrizione) LIKE UPPER(:par)  order by titolo.descrizione asc")})
public class DeTitolo extends AbstractDecodeEntity implements java.io.Serializable, Comparable<DeTitolo> {

	private static final long serialVersionUID = 200394L;
	private String codTitolo;
	private Set<CvIstruzione> cvIstruziones = new HashSet<CvIstruzione>(0);
	private String descrizione;
	private String descrizioneParlante;
	private Set<DeTitolo> figli = new HashSet<DeTitolo>(0);
	private Boolean flagLaurea;
	private Boolean flagIdo;
	private DeTitolo padre;
	private boolean flgConferimentoDid;
	private Set<RvTitolo> rvTitolos = new HashSet<RvTitolo>(0);
	private Set<VaIstruzione> vaIstruziones = new HashSet<VaIstruzione>(0);

	@Transient
	private int occurencyRicerca;

	public DeTitolo() {
	}

	public DeTitolo(String codTitolo, DeTitolo padre, String descrizione, Boolean flagLaurea,
			String descrizioneParlante, Date dtInizioVal, Date dtFineVal, Set<DeTitolo> figli, Set<RvTitolo> rvTitolos,
			Set<CvIstruzione> cvIstruziones, Set<VaIstruzione> vaIstruziones) {
		super(dtInizioVal, dtFineVal);
		this.codTitolo = codTitolo;
		this.padre = padre;
		this.descrizione = descrizione;
		this.flagLaurea = flagLaurea;
		this.descrizioneParlante = descrizioneParlante;
		this.figli = figli;
		this.rvTitolos = rvTitolos;
		this.cvIstruziones = cvIstruziones;
		this.vaIstruziones = vaIstruziones;

		this.occurencyRicerca = 0;
	}

	public DeTitolo(String codTitolo, String descrizione, Boolean flagLaurea, String descrizioneParlante,
			Date dtInizioVal, Date dtFineVal) {
		super(dtInizioVal, dtFineVal);
		this.codTitolo = codTitolo;
		this.descrizione = descrizione;
		this.flagLaurea = flagLaurea;
		this.descrizioneParlante = descrizioneParlante;
	}

	@Id
	@Column(name = "cod_titolo", unique = true, nullable = false, length = 8)
	public String getCodTitolo() {
		return this.codTitolo;
	}

	/**
	 * Restituisce il codice per clicLavoro. NULL se non è possibile generare un codice per questo titolo.
	 * 
	 * @return
	 */
	@Transient
	public String getCodTitoloClicLavoro() {
		String dueCifre = getCodTitolo().substring(0, 2);
		if ("00".equals(dueCifre))
			return null;
		else if ("81".equals(dueCifre) || "83".equals(dueCifre)) {
			return dueCifre;
		} else {
			return getCodTitolo().substring(0, 1) + "0";
		}
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "deTitolo")
	public Set<CvIstruzione> getCvIstruziones() {
		return this.cvIstruziones;
	}

	@Column(name = "descrizione", nullable = false, length = 150)
	public String getDescrizione() {
		return this.descrizione;
	}

	@Column(name = "descrizione_parlante", nullable = false, length = 150)
	public String getDescrizioneParlante() {
		return this.descrizioneParlante;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "padre")
	public Set<DeTitolo> getFigli() {
		return this.figli;
	}

	@Type(type = "yes_no")
	@Column(name = "flag_laurea", nullable = false, length = 1)
	public Boolean getFlagLaurea() {
		return this.flagLaurea;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_padre")
	public DeTitolo getPadre() {
		return this.padre;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "deTitolo")
	public Set<RvTitolo> getRvTitolos() {
		return this.rvTitolos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "deTitolo")
	public Set<VaIstruzione> getVaIstruziones() {
		return this.vaIstruziones;
	}

	@Type(type = "yes_no")
	@Column(name = "flg_conferimento_did", nullable = false, length = 1)
	public boolean getFlgConferimentoDid() {
		return flgConferimentoDid;
	}
	
	@Column(name="flag_ido")
	public Boolean getFlagIdo() {
		return flagIdo;
	}

	public void setFlagIdo(Boolean flagIdo) {
		this.flagIdo = flagIdo;
	}

	public void setFlgConferimentoDid(boolean flgConferimentoDid) {
		this.flgConferimentoDid = flgConferimentoDid;
	}

	public void setCodTitolo(String codTitolo) {
		this.codTitolo = codTitolo;
	}

	public void setCvIstruziones(Set<CvIstruzione> cvIstruziones) {
		this.cvIstruziones = cvIstruziones;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public void setDescrizioneParlante(String descrizioneParlante) {
		this.descrizioneParlante = descrizioneParlante;
	}

	public void setFigli(Set<DeTitolo> figli) {
		this.figli = figli;
	}

	public void setFlagLaurea(Boolean flagLaurea) {
		this.flagLaurea = flagLaurea;
	}

	public void setPadre(DeTitolo padre) {
		this.padre = padre;
	}

	public void setRvTitolos(Set<RvTitolo> rvTitolos) {
		this.rvTitolos = rvTitolos;
	}

	public void setVaIstruziones(Set<VaIstruzione> vaIstruziones) {
		this.vaIstruziones = vaIstruziones;
	}

	@Transient
	public int getOccurencyRicerca() {
		return occurencyRicerca;
	}

	@Transient
	public void setOccurencyRicerca(int occurencyRicerca) {
		this.occurencyRicerca = occurencyRicerca;
	}

	@Override
	public int compareTo(DeTitolo o) {
		int compareQuantity = o.getOccurencyRicerca();
		// ascending order
		return this.occurencyRicerca - compareQuantity;
	}

	public int compare(DeTitolo obj1, DeTitolo obj2) {
		Integer p1 = obj1.getOccurencyRicerca();
		Integer p2 = obj2.getOccurencyRicerca();

		if (p1 > p2) {
			return 1;
		} else if (p1 < p2) {
			return -1;
		} else {
			return 0;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || this.getCodTitolo() == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof DeTitolo)) {
			return false;
		} else {
			return this.getCodTitolo().equalsIgnoreCase(((DeTitolo) obj).getCodTitolo());
		}
	}

	@Override
	public int hashCode() {
		return getCodTitolo().hashCode();
	}

	/**
	 * Calcola il livello in base a codtitolo
	 * @return
	 */
	@Transient
	public int getLivello() {
		//in tempi normali avrei scritto qualcosa di piu` elegante,
		//ma siamo in regime COVID e ci sono impatti psicologici sul concetto di leggibilita` 
		//e la disponibilita` a scrivere elegantemente
		
		if (codTitolo.endsWith("0000000"))
			return 1;
		
		if (codTitolo.endsWith("00000"))
			return 2;
		
		if (codTitolo.endsWith("000"))
			return 3;
		
		return 4;//foglia
		//sembra codice scritto da un pivello. Mi perdonerete
	}

	@Transient
	public boolean isNodeSelectable() {
		return getLivello() > 2;
	}

}
