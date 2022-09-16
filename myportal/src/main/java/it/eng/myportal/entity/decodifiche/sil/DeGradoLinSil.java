package it.eng.myportal.entity.decodifiche.sil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_grado_lin_sil", schema = "mycas")
@NamedQueries({
	@NamedQuery(name = "deGradoLinSilValideByCodIdoNotNull", query = "select dgls from DeGradoLinSil as dgls where dgls.codIdo IS NOT NULL and CURRENT_DATE BETWEEN dgls.dtInizioVal and dgls.dtFineVal ORDER BY dgls.codGradoLinSil"),
})
public class DeGradoLinSil extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = -4210192022205879162L;

	private String codGradoLinSil;
	private String descrizione;
	private String descrizioneIdoLetto;
	private String descrizioneIdoScritto;
	private String descrizioneIdoParlato;
	private String codIdo;

	@Id
	@Column(name = "cod_grado_lin_sil", unique = true, nullable = false, length = 2)
	public String getCodGradoLinSil() {
		return codGradoLinSil;
	}

	public void setCodGradoLinSil(String codGradoLinSil) {
		this.codGradoLinSil = codGradoLinSil;
	}

	@Column(name = "descrizione", nullable = false, length = 60)
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Column(name="descrizione_ido_letto", length=250)
	public String getDescrizioneIdoLetto() {
		return descrizioneIdoLetto;
	}

	public void setDescrizioneIdoLetto(String descrizioneIdo) {
		this.descrizioneIdoLetto = descrizioneIdo;
	}

	@Column(name="cod_ido", length=2)
	public String getCodIdo() {
		return codIdo;
	}

	public void setCodIdo(String codIdo) {
		this.codIdo = codIdo;
	}
	@Column(name="descrizione_ido_scritto", length=250)
	public String getDescrizioneIdoScritto() {
		return descrizioneIdoScritto;
	}

	public void setDescrizioneIdoScritto(String descrizioneIdoScritto) {
		this.descrizioneIdoScritto = descrizioneIdoScritto;
	}
	@Column(name="descrizione_ido_parlato", length=250)
	public String getDescrizioneIdoParlato() {
		return descrizioneIdoParlato;
	}

	public void setDescrizioneIdoParlato(String descrizioneIdoParlato) {
		this.descrizioneIdoParlato = descrizioneIdoParlato;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || this.getCodGradoLinSil() == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof DeGradoLinSil)) {
			return false;
		} else {
			return this.getCodGradoLinSil().equalsIgnoreCase(((DeGradoLinSil) obj).getCodGradoLinSil());
		}
	}

	@Override
	public int hashCode() {
		return getCodGradoLinSil().hashCode();
	}

}
