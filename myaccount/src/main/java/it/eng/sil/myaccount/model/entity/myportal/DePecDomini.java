package it.eng.sil.myaccount.model.entity.myportal;

import it.eng.sil.mycas.model.entity.BaseTabellaDecodificaEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.CacheConcurrencyStrategy;

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = "decodifiche")
@Entity
@Table(name = "de_pec_domini", schema = "myportal")
@NamedQueries({ @NamedQuery(name = "findAllDePecDominiValide", query = "select d from DePecDomini d where d.dtFineVal >= (:parDate) AND d.dtInizioVal <= (:parDate)") })
public class DePecDomini extends BaseTabellaDecodificaEntity<String> {
	private static final long serialVersionUID = -9041723332839894989L;

	private String codPecDomini;
	private String descrizione;

	public DePecDomini() {
	}

	@Id
	@Column(unique = true, nullable = false, length = 8)
	public String getCodPecDomini() {
		return codPecDomini;
	}

	public void setCodPecDomini(String codPecDomini) {
		this.codPecDomini = codPecDomini;
	}

	@Column(nullable = false, length = 200)
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Transient
	@Override
	public String getId() {
		return getCodPecDomini();
	}

	@Override
	public void setId(String id) {
		setCodPecDomini(id);
	}

}
