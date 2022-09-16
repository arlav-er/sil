package it.eng.myportal.entity.decodifiche;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import it.eng.myportal.entity.AbstractDecodeEntity;


@Entity
@Table(name = "de_sistema_mycas", schema = "mycas")
@NamedQueries({
     @NamedQuery(name = "findSistemaById", query = "select s from DeSistemaMyCas s where s.codSistema = :codSitemaParam") 
})

public class DeSistemaMyCas extends AbstractDecodeEntity implements java.io.Serializable {
	
	private static final long serialVersionUID = -8926348166123194893L;

	private String codSistema;
	private String descrizione;
	private Boolean flgInstallato;
	private String versionePom;
	private String webContext;
	// questo campo è gestito solo manualmente
	private String versioneDb;

	@Id
	@Column(name = "cod_sistema", length = 8, unique = true, nullable = false)
	public String getCodSistema() {
		return codSistema;
	}

	public void setCodSistema(String codSistema) {
		this.codSistema = codSistema;
	}
	
	@Column(name = "descrizione", length = 255, nullable = true)
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Column(name = "flg_installato", nullable = true)
	public Boolean getFlgInstallato() {
		return flgInstallato;
	}

	public void setFlgInstallato(Boolean flgInstallato) {
		this.flgInstallato = flgInstallato;
	}

	@Column(name = "versione_pom")
	public String getVersionePom() {
		return versionePom;
	}

	public void setVersionePom(String versioneWeb) {
		this.versionePom = versioneWeb;
	}

	@Column(name = "versione_db")
	public String getVersioneDb() {
		return versioneDb;
	}

	public void setVersioneDb(String versioneDb) {
		this.versioneDb = versioneDb;
	}

	@Column(name = "web_context" , length = 50)
	public String getWebContext() {
		return webContext;
	}

	public void setWebContext(String webContext) {
		this.webContext = webContext;
	}

	@Transient
	public String getId() {
		return getCodSistema();
	}

	
	public void setId(String id) {
		setCodSistema(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || this.getCodSistema() == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof DeSistemaMyCas)) {
			return false;
		} else {
			return this.getCodSistema().equals(((DeSistemaMyCas) obj).getCodSistema());
		}
	}
}
