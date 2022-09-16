package it.eng.myportal.entity.decodifiche;

// Generated Mar 1, 2012 12:28:41 PM by Hibernate Tools 3.4.0.CR1
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;

@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_cittadinanza", schema = "myportal")
public class DeCittadinanza extends AbstractDecodeEntity implements java.io.Serializable {

	private static final long serialVersionUID = 929070L;
	private String codCittadinanza;
	private String descrizione;
	private String nazione;
	private Integer cdnIstat;
	private String codEds;
	private String flgCee;

	
	
	public DeCittadinanza() {
	}

	
	
	

	public DeCittadinanza(String codCittadinanza, String descrizione,
			String nazione, Integer cdnIstat, String codEds, String flgCee,
			Date dtInizioVal, Date dtFineVal) {
		super();
		this.codCittadinanza = codCittadinanza;
		this.descrizione = descrizione;
		this.nazione = nazione;
		this.cdnIstat = cdnIstat;
		this.codEds = codEds;
		this.flgCee = flgCee;

	}





	@Column(name = "descrizione", nullable = false, length = 300)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Id
	@Column(name = "cod_cittadinanza", unique = true, nullable = false, length = 8)
	public String getCodCittadinanza() {
		return codCittadinanza;
	}

	public void setCodCittadinanza(String codCittadinanza) {
		this.codCittadinanza = codCittadinanza;
	}

	@Column(name = "nazione", nullable = false, length = 100)
	public String getNazione() {
		return nazione;
	}

	public void setNazione(String nazione) {
		this.nazione = nazione;
	}

	@Column(name = "cdn_istat")
	public Integer getCdnIstat() {
		return cdnIstat;
	}

	public void setCdnIstat(Integer cdnIstat) {
		this.cdnIstat = cdnIstat;
	}

	@Column(name = "cod_eds",length=8)
	public String getCodEds() {
		return codEds;
	}

	public void setCodEds(String codEds) {
		this.codEds = codEds;
	}

	@Column(name = "flg_cee", nullable = false, length = 1)
	public String getFlgCee() {
		return flgCee;
	}

	public void setFlgCee(String flgCee) {
		this.flgCee = flgCee;
	}



}
