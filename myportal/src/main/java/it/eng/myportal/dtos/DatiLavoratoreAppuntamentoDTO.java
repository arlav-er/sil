package it.eng.myportal.dtos;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

public class DatiLavoratoreAppuntamentoDTO {
	private boolean succeded;
	private String codiceFiscale;
	private String nome;
	private String cognome;
	private Date dataNascita;
	private String codComuneNascita;
	private String descComuneNascita;
	private String sesso;
	private String codComuneDomicilio;
	private String descComuneDomicilio;
	private String indirizzoDomicilio;
	private String codComuneResidenza;
	private String descComuneResidenza;
	private String indirizzoResidenza;
	private String cell;
	private String email;
	private String codCittadinanza;

	private DatiLavoratoreAppuntamentoDTO() {
		codCittadinanza = "000";
		succeded = false;
	}

	public static DatiLavoratoreAppuntamentoDTO makeEmpty() {
		return new DatiLavoratoreAppuntamentoDTO();
	}

	public static DatiLavoratoreAppuntamentoDTO makeFromStatoOccupazionaleJSON(JSONObject statoOccupazionaleRisposta)
			throws Exception {
		String EL_RISPOSTA = "Risposta";
		String EL_DATI_SO = "datiStatoOccupazionale";
		String EL_CODICE_FISCALE = "CodiceFiscale";
		String EL_COGNOME = "Cognome";
		String EL_NOME = "Nome";
		String EL_DATA_NASCITA = "DataNascita";
		String EL_COD_COMUNE_NASCITA = "CodComNascita";
		String EL_DESCR_COMUNE_NASCITA = "ComNascita";
		String EL_SESSO = "Sesso";
		String EL_DOMICILIO = "Domicilio";
		String EL_RESIDENZA = "Residenza";
		String EL_COD_COMUNE = "CodiceComune";
		String EL_DESCR_COMUNE = "DescrComune";
		String EL_INDIRIZZO = "Indirizzo";

		DatiLavoratoreAppuntamentoDTO dto = new DatiLavoratoreAppuntamentoDTO();
		JSONObject risposta = statoOccupazionaleRisposta.getJSONObject(EL_RISPOSTA);
		JSONObject datiSO = risposta.getJSONObject(EL_DATI_SO);
		dto.codiceFiscale = datiSO.getString(EL_CODICE_FISCALE);
		dto.nome = datiSO.getString(EL_NOME);
		dto.cognome = datiSO.getString(EL_COGNOME);
		String stringDate = datiSO.getString(EL_DATA_NASCITA);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		dto.dataNascita = sdf.parse(stringDate);
		dto.codComuneNascita = datiSO.getString(EL_COD_COMUNE_NASCITA);
		dto.descComuneNascita = datiSO.getString(EL_DESCR_COMUNE_NASCITA);
		dto.sesso = datiSO.getString(EL_SESSO);
		JSONObject domicilio = datiSO.getJSONObject(EL_DOMICILIO);
		dto.codComuneDomicilio = domicilio.getString(EL_COD_COMUNE);
		dto.descComuneDomicilio = domicilio.getString(EL_DESCR_COMUNE);
		dto.indirizzoDomicilio = domicilio.getString(EL_INDIRIZZO);
		JSONObject residenza = datiSO.getJSONObject(EL_RESIDENZA);
		dto.codComuneResidenza = residenza.getString(EL_COD_COMUNE);
		dto.descComuneResidenza = residenza.getString(EL_DESCR_COMUNE);
		dto.indirizzoResidenza = residenza.getString(EL_INDIRIZZO);
		dto.email = null;
		dto.succeded = true;

		return dto;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public Date getDataNascita() {
		return dataNascita;
	}

	public void setDataNascita(Date dataNascita) {
		this.dataNascita = dataNascita;
	}

	public String getCodComuneNascita() {
		return codComuneNascita;
	}

	public void setCodComuneNascita(String codComuneNascita) {
		this.codComuneNascita = codComuneNascita;
	}

	public String getDescComuneNascita() {
		return descComuneNascita;
	}

	public void setDescComuneNascita(String descComuneNascita) {
		this.descComuneNascita = descComuneNascita;
	}

	public String getSesso() {
		return sesso;
	}

	public void setSesso(String sesso) {
		this.sesso = sesso;
	}

	public String getCodComuneDomicilio() {
		return codComuneDomicilio;
	}

	public void setCodComuneDomicilio(String codComuneDomicilio) {
		this.codComuneDomicilio = codComuneDomicilio;
	}

	public String getDescComuneDomicilio() {
		return descComuneDomicilio;
	}

	public void setDescComuneDomicilio(String descComuneDomicilio) {
		this.descComuneDomicilio = descComuneDomicilio;
	}

	public String getIndirizzoDomicilio() {
		return indirizzoDomicilio;
	}

	public void setIndirizzoDomicilio(String indirizzoDomicilio) {
		this.indirizzoDomicilio = indirizzoDomicilio;
	}

	public String getCell() {
		return cell;
	}

	public void setCell(String cell) {
		this.cell = cell;
	}

	public boolean isSucceded() {
		return succeded;
	}

	public String getCodComuneResidenza() {
		return codComuneResidenza;
	}

	public void setCodComuneResidenza(String codComuneResidenza) {
		this.codComuneResidenza = codComuneResidenza;
	}

	public String getDescComuneResidenza() {
		return descComuneResidenza;
	}

	public void setDescComuneResidenza(String descComuneResidenza) {
		this.descComuneResidenza = descComuneResidenza;
	}

	public String getIndirizzoResidenza() {
		return indirizzoResidenza;
	}

	public void setIndirizzoResidenza(String indirizzoResidenza) {
		this.indirizzoResidenza = indirizzoResidenza;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCodCittadinanza() {
		return codCittadinanza;
	}

	public void setCodCittadinanza(String codCittadinanza) {
		this.codCittadinanza = codCittadinanza;
	}

}
