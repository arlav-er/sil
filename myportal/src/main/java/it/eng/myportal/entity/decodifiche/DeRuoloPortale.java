package it.eng.myportal.entity.decodifiche;

import it.eng.myportal.entity.AbstractDecodeEntity;
import it.eng.myportal.entity.PfAbilitazione;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "de_ruolo_portale", schema = "mycas")
public class DeRuoloPortale extends AbstractDecodeEntity {
	private static final long serialVersionUID = 8820183204244608378L;

	private String codRuoloPortale;
	private String descrizione;
	private List<PfAbilitazione> abilitazioneList;

	@Id
	@Column(name = "cod_ruolo_portale", unique = true, nullable = false, length = 20)
	public String getCodRuoloPortale() {
		return codRuoloPortale;
	}

	public void setCodRuoloPortale(String codRuoloPortale) {
		this.codRuoloPortale = codRuoloPortale;
	}

	@Column(name = "descrizione", nullable = false, length = 255)
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@OneToMany(mappedBy = "deRuoloPortale", fetch = FetchType.LAZY)
	public List<PfAbilitazione> getAbilitazioneList() {
		return abilitazioneList;
	}

	public void setAbilitazioneList(List<PfAbilitazione> abilitazioneList) {
		this.abilitazioneList = abilitazioneList;
	}

}
