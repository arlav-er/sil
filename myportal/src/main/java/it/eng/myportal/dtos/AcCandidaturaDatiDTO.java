package it.eng.myportal.dtos;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class AcCandidaturaDatiDTO extends AbstractUpdatablePkDTO {
	private static final long serialVersionUID = 1761556209365155032L;

	private Integer idAcCandidatura;
	private String codStatoOccupazionale;
	private String descStatoOccupazionale;
	private String listeSpeciali;
	private Boolean flagDisabile;
	private Boolean flagIntermittente;
	private Date dataDichiarazione;
	private DeProvenienzaDTO deProvenienzaDTO;

	public AcCandidaturaDatiDTO() {

	}

	public AcCandidaturaDatiDTO(JSONObject json) throws JSONException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		JSONObject datiAggiuntivi = json.getJSONObject("DatiAggiuntivi");
		String codStatoOccupazionale = datiAggiuntivi.getString("CodiceStatoOccupazione");
		String descStatoOccupazionale = datiAggiuntivi.getString("DescrizioneStatoOccupazione");
		String stringDisabile = datiAggiuntivi.getString("Disabilel68");
		String stringIntermittente = datiAggiuntivi.getString("Intermittente");
		boolean flgDisabile = stringDisabile != null && stringDisabile.equals("Y");
		boolean flgIntermittente = stringIntermittente != null && stringIntermittente.equals("Y");

		setCodStatoOccupazionale(codStatoOccupazionale);
		setDescStatoOccupazionale(descStatoOccupazionale);
		setFlagDisabile(flgDisabile);
		setFlagIntermittente(flgIntermittente);

		if (datiAggiuntivi.has("ListeSpeciali")) {
			String listeSpeciali = datiAggiuntivi.getString("ListeSpeciali");
			setListeSpeciali(listeSpeciali);
		}

		if (datiAggiuntivi.has("DataDichiarazione")) {
			String dataDichiarazioneString = datiAggiuntivi.getString("DataDichiarazione");
			if (dataDichiarazioneString != null && !dataDichiarazioneString.isEmpty()) {
				try {
					Date dataDichiarazione = dateFormat.parse(dataDichiarazioneString);
					setDataDichiarazione(dataDichiarazione);
				} catch (ParseException e) {
					setDataDichiarazione(null);
				}
			}
		}
	}

	public Integer getIdAcCandidatura() {
		return idAcCandidatura;
	}

	public void setIdAcCandidatura(Integer idAcCandidatura) {
		this.idAcCandidatura = idAcCandidatura;
	}

	public String getCodStatoOccupazionale() {
		return codStatoOccupazionale;
	}

	public void setCodStatoOccupazionale(String codStatoOccupazionale) {
		this.codStatoOccupazionale = codStatoOccupazionale;
	}

	public String getDescStatoOccupazionale() {
		return descStatoOccupazionale;
	}

	public void setDescStatoOccupazionale(String descStatoOccupazionale) {
		this.descStatoOccupazionale = descStatoOccupazionale;
	}

	public String getListeSpeciali() {
		return listeSpeciali;
	}

	public void setListeSpeciali(String listeSpeciali) {
		this.listeSpeciali = listeSpeciali;
	}

	public Boolean getFlagDisabile() {
		return flagDisabile;
	}

	public void setFlagDisabile(Boolean flagDisabile) {
		this.flagDisabile = flagDisabile;
	}

	public Boolean getFlagIntermittente() {
		return flagIntermittente;
	}

	public void setFlagIntermittente(Boolean flagIntermittente) {
		this.flagIntermittente = flagIntermittente;
	}

	public Date getDataDichiarazione() {
		return dataDichiarazione;
	}

	public void setDataDichiarazione(Date dataDichiarazione) {
		this.dataDichiarazione = dataDichiarazione;
	}

	public DeProvenienzaDTO getDeProvenienzaDTO() {
		return deProvenienzaDTO;
	}

	public void setDeProvenienzaDTO(DeProvenienzaDTO deProvenienzaDTO) {
		this.deProvenienzaDTO = deProvenienzaDTO;
	}
}
