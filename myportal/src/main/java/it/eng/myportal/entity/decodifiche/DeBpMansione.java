package it.eng.myportal.entity.decodifiche;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import it.eng.myportal.entity.AbstractDecodeEntity;

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = "decodifiche")
@Entity
@Table(name = "de_bp_mansione", schema = "myportal")
@NamedQueries({
		@NamedQuery(name = "findDeBpMansioneForTree", query = "SELECT m FROM DeBpMansione m LEFT JOIN FETCH m.padre WHERE m.dtFineVal >= (:parDate) AND m.dtInizioVal <= (:parDate)"),
		@NamedQuery(name = "findDeBpMansioneAgricoliForTree", query = "SELECT m FROM DeBpMansione m LEFT JOIN FETCH m.padre WHERE m.dtFineVal >= (:parDate) AND m.dtInizioVal <= (:parDate) AND m.flgAgricoltura = 'Y' "),
		@NamedQuery(name = "findBpMansioniLike", query = "SELECT m FROM DeBpMansione m where UPPER(m.descrizione) like UPPER(:codLike)"),
		@NamedQuery(name = "findDeBpMansionePadreById", query = "SELECT m.padre FROM DeBpMansione m WHERE m.dtFineVal >= (:parDate) AND m.dtInizioVal <= (:parDate) AND m.codMansione = :parCodMans"),
		@NamedQuery(name = "findDeBpMansioneFigliById", query = "SELECT m FROM DeBpMansione m WHERE m.dtFineVal >= (:parDate) AND m.dtInizioVal <= (:parDate) AND m.padre.codMansione = :parCodMans") })
public class DeBpMansione extends AbstractDecodeEntity  implements java.io.Serializable, IDecodificaDescEntity {

	private static final long serialVersionUID = 1L;
	private String codMansione;
	private String descrizione;
	private DeBpMansione padre;
	private String codMansioneDot;
	private String codMansioneDotPadre;

	private Boolean flgAgricoltura;
	private String codMansioneMap;

	@Id
	@Column(name = "cod_mansione", unique = true, nullable = false, length = 8)
	public String getCodMansione() {
		return codMansione;
	}

	public void setCodMansione(String codMansione) {
		this.codMansione = codMansione;
	}

	@Column(name = "descrizione", length = 255)
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_padre")
	public DeBpMansione getPadre() {
		return padre;
	}

	public void setPadre(DeBpMansione padre) {
		this.padre = padre;
	}

	@Transient
	public Integer getLivello() {
		return (StringUtils.countMatches(codMansioneDot, ".") + 1);
	}
	
	@Transient
	public boolean isNodeSelectable() {
		return getLivello() > 2;
	}

	@Column(name = "cod_mansione_dot", length = 12)
	public String getCodMansioneDot() {
		return codMansioneDot;
	}

	public void setCodMansioneDot(String codMansioneDot) {
		this.codMansioneDot = codMansioneDot;
	}

	

	@Column(name = "cod_mansione_dot_padre", length = 12)
	public String getCodMansioneDotPadre() {
		return codMansioneDotPadre;
	}

	public void setCodMansioneDotPadre(String codMansioneDotPadre) {
		this.codMansioneDotPadre = codMansioneDotPadre;
	}

	@Column(name = "cod_mansione_map", length = 8)
	public String getCodMansioneMap() {
		return codMansioneMap;
	}

	public void setCodMansioneMap(String codMansioneMap) {
		this.codMansioneMap = codMansioneMap;
	}

	@Type(type = "yes_no")
	@Column(name = "flg_agricoltura", nullable = true, length = 1)
	public Boolean getFlgAgricoltura() {
		return flgAgricoltura;
	}

	public void setFlgAgricoltura(Boolean flgAgricoltura) {
		this.flgAgricoltura = flgAgricoltura;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || this.getCodMansione() == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof DeBpMansione)) {
			return false;
		} else {
			return this.getCodMansione().equals(((DeBpMansione) obj).getCodMansione());
		}
	}

	@Override
	public int hashCode() {
		return getCodMansione().hashCode();
	}

 

}
