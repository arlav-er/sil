package it.eng.myportal.entity;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import it.eng.myportal.entity.decodifiche.DeBandoProgramma;
import it.eng.myportal.entity.decodifiche.DeCpi;
import it.eng.myportal.entity.decodifiche.DeProvenienza;

@Entity
@Table(name = "bd_adesione", schema = "myportal")
@NamedQueries({
		@NamedQuery(name = "findBdAdesioneByIdPrincipal", query = "SELECT ba FROM BdAdesione ba WHERE ba.pfPrincipal.idPfPrincipal = :idPfPrincipal"),
		@NamedQuery(name = "findByCodFiscaleAndCodBandoProgramma", query = "SELECT ba FROM BdAdesione ba WHERE (ba.pfPrincipal.idPfPrincipal = :idPfPrincipal OR upper(ba.codiceFiscale) = :codiceFiscale) AND ba.deBandoProgramma.codBandoProgramma = :codBandoProgramma ORDER BY ba.dtAdesione"),
		@NamedQuery(name = "findBdAdesioneByCodBandoProgramma", query = "SELECT ba FROM BdAdesione ba WHERE ba.pfPrincipal.idPfPrincipal = :idPfPrincipal AND ba.deBandoProgramma.codBandoProgramma = :codBandoProgramma ORDER BY ba.dtAdesione") })
public class BdAdesione extends AbstractEntity {

	private static final long serialVersionUID = -3629162484014540158L;

	private Integer idBdAdesione;
	private String codiceFiscale;
	private String cognome;
	private String nome;
	private Date dtNascita;
	private String codStatoOccupazionale;
	private String descStatoOccupazionale;
	private BigInteger mesiAnzianita;
	private Date dtDid;
	private Date dtAdesione;
	private DeCpi deCpi;
	private String dichiarazione;
	private DeProvenienza deProvenienza;
	private PfPrincipal pfPrincipal;
	private DeBandoProgramma deBandoProgramma;

	private Date dtAdesioneGG;

	private String codStatoAdesioneMin;

	// Costruttore
	public BdAdesione() {
	}

	// Getter and setter
	@Id
	@SequenceGenerator(name = "bd_adesione_id_bd_adesione_seq", sequenceName = "bd_adesione_id_bd_adesione_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bd_adesione_id_bd_adesione_seq")
	@Column(name = "id_bd_adesione", unique = true, nullable = false)
	public Integer getIdBdAdesione() {
		return idBdAdesione;
	}

	public void setIdBdAdesione(Integer idBdAdesione) {
		this.idBdAdesione = idBdAdesione;
	}

	@Column(name = "codice_fiscale")
	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	@Column(name = "cognome")
	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	@Column(name = "nome")
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "dt_nascita")
	public Date getDtNascita() {
		return dtNascita;
	}

	public void setDtNascita(Date dtNascita) {
		this.dtNascita = dtNascita;
	}

	@Column(name = "cod_stato_occupazionale")
	public String getCodStatoOccupazionale() {
		return codStatoOccupazionale;
	}

	public void setCodStatoOccupazionale(String codStatoOccupazionale) {
		this.codStatoOccupazionale = codStatoOccupazionale;
	}

	@Column(name = "desc_stato_occupazionale")
	public String getDescStatoOccupazionale() {
		return descStatoOccupazionale;
	}

	public void setDescStatoOccupazionale(String descStatoOccupazionale) {
		this.descStatoOccupazionale = descStatoOccupazionale;
	}

	@Column(name = "mesi_anzianita")
	public BigInteger getMesiAnzianita() {
		return mesiAnzianita;
	}

	public void setMesiAnzianita(BigInteger mesiAnzianita) {
		this.mesiAnzianita = mesiAnzianita;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "dt_did")
	public Date getDtDid() {
		return dtDid;
	}

	public void setDtDid(Date dtDid) {
		this.dtDid = dtDid;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "dt_adesione_gg")
	public Date getDtAdesioneGG() {
		return dtAdesioneGG;
	}

	public void setDtAdesioneGG(Date dtAdesione2) {
		dtAdesioneGG = dtAdesione2;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "dt_adesione")
	public Date getDtAdesione() {
		return dtAdesione;
	}

	public void setDtAdesione(Date dtAdesione) {
		this.dtAdesione = dtAdesione;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_cpi")
	public DeCpi getDeCpi() {
		return deCpi;
	}

	public void setDeCpi(DeCpi deCpi) {
		this.deCpi = deCpi;
	}

	@Column(name = "cod_stato_adesione_min")
	public String getCodStatoAdesioneMin() {
		return codStatoAdesioneMin;
	}

	public void setCodStatoAdesioneMin(String codStatoAdesioneMinIn) {
		codStatoAdesioneMin = codStatoAdesioneMinIn;
	}

	@Column(name = "dichiarazione")
	public String getDichiarazione() {
		return dichiarazione;
	}

	public void setDichiarazione(String dichiarazione) {
		this.dichiarazione = dichiarazione;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_provenienza")
	public DeProvenienza getDeProvenienza() {
		return deProvenienza;
	}

	public void setDeProvenienza(DeProvenienza deProvenienza) {
		this.deProvenienza = deProvenienza;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pf_principal", nullable = false)
	public PfPrincipal getPfPrincipal() {
		return pfPrincipal;
	}

	public void setPfPrincipal(PfPrincipal pfPrincipal) {
		this.pfPrincipal = pfPrincipal;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cod_bando_programma")
	public DeBandoProgramma getDeBandoProgramma() {
		return deBandoProgramma;
	}

	public void setDeBandoProgramma(DeBandoProgramma deBandoProgramma) {
		this.deBandoProgramma = deBandoProgramma;
	}

	// Hashcode and equals
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idBdAdesione == null) ? 0 : idBdAdesione.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BdAdesione other = (BdAdesione) obj;
		if (idBdAdesione == null) {
			if (other.idBdAdesione != null)
				return false;
		} else if (!idBdAdesione.equals(other.idBdAdesione))
			return false;
		return true;
	}

}
