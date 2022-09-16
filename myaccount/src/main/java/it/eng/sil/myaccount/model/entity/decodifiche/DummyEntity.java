package it.eng.sil.myaccount.model.entity.decodifiche;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "dummy_entity", schema = "mycas")
@NamedQueries({
		@NamedQuery(name = "findAchievedGpBadgesByIdPrincipal", query = "SELECT b FROM GpBadge b WHERE b.pfPrincipal.idPfPrincipal = :idPfPrincipal AND b.value >= 1 ORDER BY b.dtmMod DESC"),
		@NamedQuery(name = "findPfPrincipalByDescrizioneRuolo", query = "SELECT pf FROM GpProfilatura p JOIN p.pfPrincipal pf JOIN p.gpRuoloGruppo rg JOIN rg.gpRuolo r WHERE r.descrizione = :descrizione"),
		@NamedQuery(name = "findGpGruppoAziendaSare", query = "SELECT g FROM GpGruppo g WHERE g.gpDeTipoGruppo.codTipoGruppo = :codTipoGruppoEnum AND g.note LIKE :noteParam") })
public class DummyEntity {
	private String codDummyEntity;

	@Id
	@Column(name = "cod_dummy_entity", unique = true, length = 8)
	public String getCodDummyEntity() {
		return codDummyEntity;
	}

	public void setCodDummyEntity(String codDummyEntity) {
		this.codDummyEntity = codDummyEntity;
	}
}
