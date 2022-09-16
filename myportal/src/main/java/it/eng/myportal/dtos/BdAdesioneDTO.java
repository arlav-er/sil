package it.eng.myportal.dtos;

import java.math.BigInteger;
import java.util.Date;

public class BdAdesioneDTO extends AbstractUpdatablePkDTO implements IDTO {

	private static final long serialVersionUID = -2939008487938903139L;
	
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
	private DeCpiDTO deCpiDTO;
	private String dichiarazione; //Può assumere valori: NASPI, CIGS; NULL
	private DeProvenienzaDTO deProvenienzaDTO;
	private PfPrincipalDTO pfPrincipalDTO;
	private DeBandoProgrammaDTO deBandoProgrammaDTO;
	
	//Getter and setter
	public Integer getIdBdAdesione() {
		return idBdAdesione;
	}
	public void setIdBdAdesione(Integer idBdAdesione) {
		this.idBdAdesione = idBdAdesione;
	}
	public String getCodiceFiscale() {
		return codiceFiscale;
	}
	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}
	public String getCognome() {
		return cognome;
	}
	public void setCognome(String cognome) {
		this.cognome = cognome;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Date getDtNascita() {
		return dtNascita;
	}
	public void setDtNascita(Date dtNascita) {
		this.dtNascita = dtNascita;
	}
	public String getCodStatoOccupazionale() {
		return codStatoOccupazionale;
	}
	public void setCodStatoOccupazionale(String codStatoOccupazionale) {
		this.codStatoOccupazionale = codStatoOccupazionale;
	}
	public String getDescStatoOccupazionale() {
		return descStatoOccupazionale;
	}
	public void setDescStatoOccupazionale(String descStatoOccupazionale) {
		this.descStatoOccupazionale = descStatoOccupazionale;
	}
	public BigInteger getMesiAnzianita() {
		return mesiAnzianita;
	}
	public void setMesiAnzianita(BigInteger mesiAnzianita) {
		this.mesiAnzianita = mesiAnzianita;
	}
	public Date getDtDid() {
		return dtDid;
	}
	public void setDtDid(Date dtDid) {
		this.dtDid = dtDid;
	}
	public Date getDtAdesione() {
		return dtAdesione;
	}
	public void setDtAdesione(Date dtAdesione) {
		this.dtAdesione = dtAdesione;
	}
	public DeCpiDTO getDeCpiDTO() {
		return deCpiDTO;
	}
	public void setDeCpiDTO(DeCpiDTO deCpiDTO) {
		this.deCpiDTO = deCpiDTO;
	}
	public String getDichiarazione() {
		return dichiarazione;
	}
	public void setDichiarazione(String dichiarazione) {
		this.dichiarazione = dichiarazione;
	}
	public DeProvenienzaDTO getDeProvenienzaDTO() {
		return deProvenienzaDTO;
	}
	public void setDeProvenienzaDTO(DeProvenienzaDTO deProvenienzaDTO) {
		this.deProvenienzaDTO = deProvenienzaDTO;
	}
	public PfPrincipalDTO getPfPrincipalDTO() {
		return pfPrincipalDTO;
	}
	public void setPfPrincipalDTO(PfPrincipalDTO pfPrincipalDTO) {
		this.pfPrincipalDTO = pfPrincipalDTO;
	}
	public DeBandoProgrammaDTO getDeBandoProgrammaDTO() {
		return deBandoProgrammaDTO;
	}
	public void setDeBandoProgrammaDTO(DeBandoProgrammaDTO deBandoProgrammaDTO) {
		this.deBandoProgrammaDTO = deBandoProgrammaDTO;
	}
}
