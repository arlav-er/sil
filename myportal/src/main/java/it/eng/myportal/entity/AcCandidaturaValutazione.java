package it.eng.myportal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "ac_candidatura_valutaz", schema = "myportal")
@NamedQueries({
		@NamedQuery(name = "findAcCandidaturaValByCandidaturaId", query = "SELECT c FROM AcCandidaturaValutazione c WHERE c.acCandidatura.idAcCandidatura = :par")

})
public class AcCandidaturaValutazione extends AbstractEntity implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -300759238269424521L;
	private Integer idAcCandidaturaValutazione;
	private AcCandidatura acCandidatura;
	private CodValutazioneEnum punteggioEsperienze;
	private CodValutazioneEnum punteggioIstruzione;
	private CodValutazioneEnum punteggioAltriTitoli;
	private CodValutazioneEnum punteggioCompetenze;
	private CodValutazioneEnum punteggioAltreCondizioni;

	private String note;
	private String notePertinenzaTitoli;
	private String noteAltriTitoli;
	private String notePertinenzaCompetenze;
	private String noteAltreCondizioni;

	private String tipoSceltaPertinenzaEsp;
	private String tipoSceltaPertinenzaTitoli;
	private String tipoSceltaPertinenzaCompetenze;
	private String tipoSceltaAltreCondizioni;
	private String tipoSceltaPertinenzaAltriTitoli;

	private CodValutazioneEnum valutazioneComplessiva;
	private String ulterioriElementi;
	private String tipoValutazioneComplessiva;

	@Id
	@SequenceGenerator(name = "ac_candidatura_valutaz_id_ac_candidatura_valutaz_seq", sequenceName = "ac_candidatura_valutaz_id_ac_candidatura_valutaz_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ac_candidatura_valutaz_id_ac_candidatura_valutaz_seq")
	@Column(name = "id_ac_candidatura_valutaz", unique = true, nullable = false)
	public Integer getIdAcCandidaturaValutazione() {
		return idAcCandidaturaValutazione;
	}

	public void setIdAcCandidaturaValutazione(Integer idAcCandidaturaValutazione) {
		this.idAcCandidaturaValutazione = idAcCandidaturaValutazione;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_ac_candidatura")
	public AcCandidatura getAcCandidatura() {
		return acCandidatura;
	}

	public void setAcCandidatura(AcCandidatura acCandidatura) {
		this.acCandidatura = acCandidatura;
	}

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "punteggio_esperienze")
	public CodValutazioneEnum getPunteggioEsperienze() {
		return punteggioEsperienze;
	}

	public void setPunteggioEsperienze(CodValutazioneEnum punteggioEsperienze) {
		this.punteggioEsperienze = punteggioEsperienze;
	}

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "punteggio_istruzione")
	public CodValutazioneEnum getPunteggioIstruzione() {
		return punteggioIstruzione;
	}

	public void setPunteggioIstruzione(CodValutazioneEnum punteggioIstruzione) {
		this.punteggioIstruzione = punteggioIstruzione;
	}

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "punteggio_altri_titoli")
	public CodValutazioneEnum getPunteggioAltriTitoli() {
		return punteggioAltriTitoli;
	}

	public void setPunteggioAltriTitoli(CodValutazioneEnum punteggioAltriTitoli) {
		this.punteggioAltriTitoli = punteggioAltriTitoli;
	}

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "punteggio_competenze")
	public CodValutazioneEnum getPunteggioCompetenze() {
		return punteggioCompetenze;
	}

	public void setPunteggioCompetenze(CodValutazioneEnum punteggioCompetenze) {
		this.punteggioCompetenze = punteggioCompetenze;
	}

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "punteggio_altre_condizioni")
	public CodValutazioneEnum getPunteggioAltreCondizioni() {
		return punteggioAltreCondizioni;
	}

	public void setPunteggioAltreCondizioni(CodValutazioneEnum punteggioAltreCondizioni) {
		this.punteggioAltreCondizioni = punteggioAltreCondizioni;
	}

	@Column(length = 2000)
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "valutazione_complessiva", length = 2000)
	public CodValutazioneEnum getValutazioneComplessiva() {
		return valutazioneComplessiva;
	}

	public void setValutazioneComplessiva(CodValutazioneEnum valutazioneComplessiva) {
		this.valutazioneComplessiva = valutazioneComplessiva;
	}

	@Transient
	public String getTipoSceltaPertinenzaEsp() {
		if (punteggioEsperienze != null) {
			tipoSceltaPertinenzaEsp = punteggioEsperienze.getValue();
		}
		return tipoSceltaPertinenzaEsp;
	}

	public void setTipoSceltaPertinenzaEsp(String tipoSceltaPertinenzaEsp) {
		if (tipoSceltaPertinenzaEsp != null) {
			switch (tipoSceltaPertinenzaEsp) {
			case "L1":
				this.punteggioEsperienze = CodValutazioneEnum.L1;
				break;
			case "L2":
				this.punteggioEsperienze = CodValutazioneEnum.L2;
				break;
			case "L3":
				this.punteggioEsperienze = CodValutazioneEnum.L3;
				break;
			case "L4":
				this.punteggioEsperienze = CodValutazioneEnum.L4;
				break;
			default:
				break;
			}
		}
	}

	@Transient
	public String getTipoSceltaPertinenzaTitoli() {
		if (punteggioIstruzione != null) {
			tipoSceltaPertinenzaTitoli = punteggioIstruzione.getValue();
		}
		return tipoSceltaPertinenzaTitoli;
	}

	public void setTipoSceltaPertinenzaTitoli(String tipoSceltaPertinenzaTitoli) {
		if (tipoSceltaPertinenzaTitoli != null) {
			switch (tipoSceltaPertinenzaTitoli) {
			case "L1":
				this.punteggioIstruzione = CodValutazioneEnum.L1;
				break;
			case "L2":
				this.punteggioIstruzione = CodValutazioneEnum.L2;
				break;
			case "L3":
				this.punteggioIstruzione = CodValutazioneEnum.L3;
				break;
			case "L4":
				this.punteggioIstruzione = CodValutazioneEnum.L4;
				break;
			default:
				break;
			}
		}
	}

	@Transient
	public String getTipoSceltaPertinenzaAltriTitoli() {
		if (punteggioAltriTitoli != null) {
			tipoSceltaPertinenzaAltriTitoli = punteggioAltriTitoli.getValue();
		}
		return tipoSceltaPertinenzaAltriTitoli;
	}

	public void setTipoSceltaPertinenzaAltriTitoli(String tipoSceltaPertinenzaAltriTitoli) {
		if (tipoSceltaPertinenzaAltriTitoli != null) {
			switch (tipoSceltaPertinenzaAltriTitoli) {
			case "L1":
				this.punteggioAltriTitoli = CodValutazioneEnum.L1;
				break;
			case "L2":
				this.punteggioAltriTitoli = CodValutazioneEnum.L2;
				break;
			case "L3":
				this.punteggioAltriTitoli = CodValutazioneEnum.L3;
				break;
			case "L4":
				this.punteggioAltriTitoli = CodValutazioneEnum.L4;
				break;
			default:
				break;
			}
		}
	}

	
	@Transient
	public String getTipoSceltaPertinenzaCompetenze() {
		if (punteggioCompetenze != null) {
			tipoSceltaPertinenzaCompetenze = punteggioCompetenze.getValue();
		}
		return tipoSceltaPertinenzaCompetenze;
	}

	public void setTipoSceltaPertinenzaCompetenze(String tipoSceltaPertinenzaCompetenze) {
		if (tipoSceltaPertinenzaCompetenze != null) {
			switch (tipoSceltaPertinenzaCompetenze) {
			case "L1":
				this.punteggioCompetenze = CodValutazioneEnum.L1;
				break;
			case "L2":
				this.punteggioCompetenze = CodValutazioneEnum.L2;
				break;
			case "L3":
				this.punteggioCompetenze = CodValutazioneEnum.L3;
				break;
			case "L4":
				this.punteggioCompetenze = CodValutazioneEnum.L4;
				break;
			default:
				break;
			}
		}
	}

	@Transient
	public String getTipoSceltaAltreCondizioni() {
		if (punteggioAltreCondizioni != null) {
			tipoSceltaAltreCondizioni = punteggioAltreCondizioni.getValue();
		}
		return tipoSceltaAltreCondizioni;
	}

	public void setTipoSceltaAltreCondizioni(String tipoSceltaAltreCondizioni) {
		if (tipoSceltaAltreCondizioni != null) {
			switch (tipoSceltaAltreCondizioni) {
			case "L1":
				this.punteggioAltreCondizioni = CodValutazioneEnum.L1;
				break;
			case "L2":
				this.punteggioAltreCondizioni = CodValutazioneEnum.L2;
				break;
			case "L3":
				this.punteggioAltreCondizioni = CodValutazioneEnum.L3;
				break;
			case "L4":
				this.punteggioAltreCondizioni = CodValutazioneEnum.L4;
				break;
			default:
				break;
			}
		}
	}

	//@Column(length = 2000)
	@Column(name = "ulteriori_elementi", length = 2000)
	public String getUlterioriElementi() {
		return ulterioriElementi;
	}

	public void setUlterioriElementi(String ulterioriElementi) {
		this.ulterioriElementi = ulterioriElementi;
	}

	@Column(name = "note_pertinenza_titoli", length = 2000)
	public String getNotePertinenzaTitoli() {
		return notePertinenzaTitoli;
	}

	public void setNotePertinenzaTitoli(String notePertinenzaTitoli) {
		this.notePertinenzaTitoli = notePertinenzaTitoli;
	}
	
	@Column(name = "note_altri_titoli", length = 2000)
	public String getNoteAltriTitoli() {
		return noteAltriTitoli;
	}

	public void setNoteAltriTitoli(String noteAltriTitoli) {
		this.noteAltriTitoli = noteAltriTitoli;
	}
	

	@Column(name = "note_pertinenza_competenze", length = 2000)
	public String getNotePertinenzaCompetenze() {
		return notePertinenzaCompetenze;
	}

	public void setNotePertinenzaCompetenze(String notePertinenzaCompetenze) {
		this.notePertinenzaCompetenze = notePertinenzaCompetenze;
	}

	@Column(name = "note_altre_condizioni", length = 2000)
	public String getNoteAltreCondizioni() {
		return noteAltreCondizioni;
	}

	public void setNoteAltreCondizioni(String noteAltreCondizioni) {
		this.noteAltreCondizioni = noteAltreCondizioni;
	}

	@Transient
	public String getTipoValutazioneComplessiva() {
		if (valutazioneComplessiva != null) {
			tipoValutazioneComplessiva = valutazioneComplessiva.getValue();
		}
		return tipoValutazioneComplessiva;
	}

	public void setTipoValutazioneComplessiva(String tipoValutazioneComplessiva) {
		if (tipoValutazioneComplessiva != null) {
			switch (tipoValutazioneComplessiva) {
			case "L0":
				this.valutazioneComplessiva = CodValutazioneEnum.L0;
				break;
			case "L1":
				this.valutazioneComplessiva = CodValutazioneEnum.L1;
				break;
			case "L2":
				this.valutazioneComplessiva = CodValutazioneEnum.L2;
				break;
			case "L3":
				this.valutazioneComplessiva = CodValutazioneEnum.L3;
				break;
			case "L4":
				this.valutazioneComplessiva = CodValutazioneEnum.L4;
				break;
			
			default:
				break;
			}
		}
	}
		
}
