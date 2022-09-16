package it.eng.myportal.entity.decodifiche;

import it.eng.myportal.entity.AbstractDecodeEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "de_ambito_disciplinare", schema = "myportal")
public class DeAmbitoDisciplinare extends AbstractDecodeEntity {
	private static final long serialVersionUID = -1987483382681840635L;

	private String codAmbitoDisciplinare;
	private String descrizione;

	@Id
	@Column(name = "cod_ambito_disciplinare", unique = true, nullable = false, length = 8)
	public String getCodAmbitoDisciplinare() {
		return codAmbitoDisciplinare;
	}

	public void setCodAmbitoDisciplinare(String codAmbitoDisciplinare) {
		this.codAmbitoDisciplinare = codAmbitoDisciplinare;
	}

	@Column(name = "descrizione", nullable = false, length = 255)
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

}
