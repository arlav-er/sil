package it.eng.sil.coop.webservices.agenda.appuntamento;

import java.math.BigDecimal;

public class EsitoAppuntamento {

	boolean success;
	String codErrore;
	String descrizioneErrore;

	String descrizioneCpi;
	String indirizzoCpi;
	String indirizzoStampaCpi;
	String telefonoCpi;

	boolean isSpiServizioDisp;
	BigDecimal prgSpiServizio;
	String nomeSpiServizio;
	String cognomeSpiServizio;
	String telSpiServizio;
	String siglaSpiServizio;

	boolean isSpiSlotDisp;
	BigDecimal prgSpiSlot;
	String nomeSpiSlot;
	String cognomeSpiSlot;
	String telSpiSlot;
	String siglaSpiSlot;

	String ambiente;

	BigDecimal prgAppuntamento;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getCodErrore() {
		return codErrore;
	}

	public void setCodErrore(String codErrore) {
		this.codErrore = codErrore;
	}

	public String getDescrizioneErrore() {
		return descrizioneErrore;
	}

	public void setDescrizioneErrore(String descrizioneErrore) {
		this.descrizioneErrore = descrizioneErrore;
	}

	public String getDescrizioneCpi() {
		return descrizioneCpi;
	}

	public void setDescrizioneCpi(String descrizioneCpi) {
		this.descrizioneCpi = descrizioneCpi;
	}

	public String getIndirizzoCpi() {
		return indirizzoCpi;
	}

	public void setIndirizzoCpi(String indirizzoCpi) {
		this.indirizzoCpi = indirizzoCpi;
	}

	public String getTelefonoCpi() {
		return telefonoCpi;
	}

	public void setTelefonoCpi(String telefonoCpi) {
		this.telefonoCpi = telefonoCpi;
	}

	public boolean isSpiServizioDisp() {
		return isSpiServizioDisp;
	}

	public void setSpiServizioDisp(boolean isSpiServizioDisp) {
		this.isSpiServizioDisp = isSpiServizioDisp;
	}

	public String getNomeSpiServizio() {
		return nomeSpiServizio;
	}

	public void setNomeSpiServizio(String nomeSpiServizio) {
		this.nomeSpiServizio = nomeSpiServizio;
	}

	public String getCognomeSpiServizio() {
		return cognomeSpiServizio;
	}

	public void setCognomeSpiServizio(String cognomeSpiServizio) {
		this.cognomeSpiServizio = cognomeSpiServizio;
	}

	public String getTelSpiServizio() {
		return telSpiServizio;
	}

	public void setTelSpiServizio(String telSpiServizio) {
		this.telSpiServizio = telSpiServizio;
	}

	public boolean isSpiSlotDisp() {
		return isSpiSlotDisp;
	}

	public void setSpiSlotDisp(boolean isSpiSlotDisp) {
		this.isSpiSlotDisp = isSpiSlotDisp;
	}

	public String getNomeSpiSlot() {
		return nomeSpiSlot;
	}

	public void setNomeSpiSlot(String nomeSpiSlot) {
		this.nomeSpiSlot = nomeSpiSlot;
	}

	public String getCognomeSpiSlot() {
		return cognomeSpiSlot;
	}

	public void setCognomeSpiSlot(String cognomeSpiSlot) {
		this.cognomeSpiSlot = cognomeSpiSlot;
	}

	public String getTelSpiSlot() {
		return telSpiSlot;
	}

	public void setTelSpiSlot(String telSpiSlot) {
		this.telSpiSlot = telSpiSlot;
	}

	public String getSiglaSpiServizio() {
		return siglaSpiServizio;
	}

	public void setSiglaSpiServizio(String siglaSpiServizio) {
		this.siglaSpiServizio = siglaSpiServizio;
	}

	public String getSiglaSpiSlot() {
		return siglaSpiSlot;
	}

	public void setSiglaSpiSlot(String siglaSpiSlot) {
		this.siglaSpiSlot = siglaSpiSlot;
	}

	public String getAmbiente() {
		return ambiente;
	}

	public void setAmbiente(String ambiente) {
		this.ambiente = ambiente;
	}

	public String getIndirizzoStampaCpi() {
		return indirizzoStampaCpi;
	}

	public void setIndirizzoStampaCpi(String indirizzoStampaCpi) {
		this.indirizzoStampaCpi = indirizzoStampaCpi;
	}

	public BigDecimal getPrgSpiServizio() {
		return prgSpiServizio;
	}

	public void setPrgSpiServizio(BigDecimal prgSpiServizio) {
		this.prgSpiServizio = prgSpiServizio;
	}

	public BigDecimal getPrgSpiSlot() {
		return prgSpiSlot;
	}

	public void setPrgSpiSlot(BigDecimal prgSpiSlot) {
		this.prgSpiSlot = prgSpiSlot;
	}

	public BigDecimal getPrgAppuntamento() {
		return prgAppuntamento;
	}

	public void setPrgAppuntamento(BigDecimal prgAppuntamento) {
		this.prgAppuntamento = prgAppuntamento;
	}

}
