package it.eng.myportal.entity;

// Generated 9-feb-2015 15.25.41 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "soggetto_pubblico_import", schema = "mycas")
@NamedQueries({
	@NamedQuery(name = "findAllSoggetti", query = "select m from SoggettoPubblicoImport m")
})
public class SoggettoPubblicoImport implements java.io.Serializable {

	private static final long serialVersionUID = 3352571473657307923L;
	
	private Integer codSoggettoPubblico;
	
	private String strCodiceFiscale;
	private String strIndirizzo;
	private String strRagioneSociale;
	private String codComune;
	private String codCatSoggPubbl;

	private String nome;
	private String cognome;
	private String email;
	private String telefono;
	
	public SoggettoPubblicoImport() {
	}

	@Id
	@Column(name = "cod_soggetto_pubblico", unique = true, nullable = false)
	public Integer getCodSoggettoPubblico() {
		return this.codSoggettoPubblico;
	}

	@Column(name = "cod_cat_sogg_pubbl", length=5, nullable = true)
	public String getCodCatSoggPubbl() {
		return this.codCatSoggPubbl;
	}
	

	@Column(name = "cod_comune", length=4, nullable = true)
	public String getCodComune() {
		return this.codComune;
	}


	@Column(name = "str_codice_fiscale", length =16, nullable = false)
	public String getStrCodiceFiscale() {
		return strCodiceFiscale;
	}

	@Column(name = "str_indirizzo", length =255, nullable = true)
	public String getStrIndirizzo() {
		return strIndirizzo;
	}
	
	@Column(name = "str_ragione_sociale", length = 100, nullable = false)
	public String getStrRagioneSociale() {
		return strRagioneSociale;
	}

	@Override
	public int hashCode() {
		return getCodSoggettoPubblico().hashCode();
	}

	public void setCodSoggettoPubblico(Integer codSoggetto) {
		this.codSoggettoPubblico = codSoggetto;
	}
	
	public void setCodCatSoggPubbl(String deEsitoIstruttoria) {
		this.codCatSoggPubbl = deEsitoIstruttoria;
	}

	public void setCodComune(String deComuneByCodComTir) {
		this.codComune = deComuneByCodComTir;
	}

	public void setStrCodiceFiscale(String strCodiceFiscale) {
		this.strCodiceFiscale = strCodiceFiscale;
	}
	
	
	public void setStrIndirizzo(String strIndirizzo) {
		this.strIndirizzo = strIndirizzo;
	}

	public void setStrRagioneSociale(String strRagioneSociale) {
		this.strRagioneSociale = strRagioneSociale;
	}

	@Column(name = "str_nome", length = 100, nullable = false)
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "str_cognome", length = 100, nullable = false)
	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	@Column(name = "str_email", length = 100, nullable = false)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "str_telefono", length = 50, nullable = false)
	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

}
