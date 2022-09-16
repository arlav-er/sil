package it.eng.myportal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import it.eng.myportal.entity.decodifiche.DeRapportoLavoro;

/**
 * @author Rodi
 */
@Entity
@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "ma_rapporto_lavoro", schema = "myportal")
public class MaRapportoLavoro extends AbstractDecodeEntity implements
		java.io.Serializable {
	private static final long serialVersionUID = 257731L;

	private String codContratto;
	private DeRapportoLavoro deRapportoLavoro;

	public MaRapportoLavoro() {
	}

	
	@Id
	@Column(name = "cod_contratto", unique = true, nullable = false,length=8)
	public String getCodContratto() {
		return codContratto;
	}

	public void setCodContratto(String codContratto) {
		this.codContratto = codContratto;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_rapporto_lavoro")
	public DeRapportoLavoro getDeRapportoLavoro() {
		return this.deRapportoLavoro;
	}

	public void setDeRapportoLavoro(DeRapportoLavoro deRapportoLavoro) {
		this.deRapportoLavoro = deRapportoLavoro;
	}
}