package it.eng.myportal.entity.decodifiche;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;

@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_gruppo_area_titolo", schema = "myportal")
@NamedQueries({
	@NamedQuery(name = "DeGruppoAreaTitoloFindSuggestion", query = "select d from DeGruppoAreaTitolo d where d.descrizione like :query")
	})
public class DeGruppoAreaTitolo extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 39262743371L;
	private String codGruppoAreaTitolo;
	private String descrizione;
	 
	public DeGruppoAreaTitolo() {
	}

	@Id
	@Column(name = "cod_gruppo_area_titolo", unique = true, nullable = false, length = 4)
	public String getCodGruppoAreaTitolo() {
		return this.codGruppoAreaTitolo;
	}

	public void setCodGruppoAreaTitolo(String codAgevolazione) {
		this.codGruppoAreaTitolo = codAgevolazione;
	}

	@Column(name = "descrizione", nullable = false, length = 150)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

}
