package it.eng.myportal.entity.decodifiche.sil;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;
import it.eng.myportal.entity.decodifiche.DeMansione;

/**
 * 
 * @author Rodi A.
 *
 */
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_mansione_sil", schema = "mycas")
public class DeMansioneSil extends AbstractDecodeEntity implements java.io.Serializable {
	private static final long serialVersionUID = 47268L;
	private String codMansioneSil;
	private DeMansione deMansione;

	public DeMansioneSil() {
	}

	public DeMansioneSil(String codMansioneSil, DeMansione deMansione, Date dtInizioVal, Date dtFineVal) {
		super(dtInizioVal, dtFineVal);
		this.codMansioneSil = codMansioneSil;
		this.deMansione = deMansione;
	}

	@Id
	@Column(name = "cod_mansione_sil", unique = true, nullable = false, length = 12)
	public String getCodMansioneSil() {
		return this.codMansioneSil;
	}

	public void setCodMansioneSil(String codMansioneSil) {
		this.codMansioneSil = codMansioneSil;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_mansione", nullable = false)
	public DeMansione getDeMansione() {
		return this.deMansione;
	}

	public void setDeMansione(DeMansione deMansione) {
		this.deMansione = deMansione;
	}

}
