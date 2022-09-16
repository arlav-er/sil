package it.eng.myportal.entity.decodifiche;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.AbstractDecodeEntity;

/**
 * Entity legata ai tipi di sindacato dei consulenti
 * @author Rodi A.
 *
 */
@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_atp_ente_consulente", schema = "mycas")
public class DeAtpEnteConsulente extends AbstractDecodeEntity implements java.io.Serializable {

	private static final long serialVersionUID = 929070L;
	private String codEnteConsulente;
	private String descrizione;

	public DeAtpEnteConsulente() {
	}

	@Id
	@Column(name = "cod_ente_consulente", unique = true, nullable = false, length = 8)
	public String getCodEnteConsulente() {
		return this.codEnteConsulente;
	}

	@Column(name = "descrizione", nullable = false, length = 255)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setCodEnteConsulente(String codEnteConsulente) {
		this.codEnteConsulente = codEnteConsulente;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

}
