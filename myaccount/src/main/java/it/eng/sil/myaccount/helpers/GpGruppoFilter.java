package it.eng.sil.myaccount.helpers;

import it.eng.sil.base.enums.GpDeMacroTipoEnum;
import it.eng.sil.base.enums.GpDeTipoGruppoEnum;

public class GpGruppoFilter {

	private String descrizione;
	private Integer idPadre;
	private Integer idDaEscludere;
	private GpDeTipoGruppoEnum codTipoGruppo;
	private GpDeMacroTipoEnum codMacroTipo;
	private Boolean descrizioneEsatta;
	private Boolean includiGruppiConcreti;
	private Boolean includiGruppiAstratti;
	private Boolean soloGruppiSenzaPadre;
	private String codProvincia;
	private String codComune;
	private Boolean soloGruppiPadre;

	/**
	 * Di default, includo sia gruppi concreti che gruppi astratti e non faccio una ricerca esatta sulla descrizione.
	 */
	public GpGruppoFilter() {
		this.includiGruppiAstratti = true;
		this.includiGruppiConcreti = true;
		this.descrizioneEsatta = false;
		this.soloGruppiSenzaPadre = false;
		this.soloGruppiPadre = false;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public GpGruppoFilter setDescrizione(String descrizione) {
		this.descrizione = descrizione;
		return this;
	}

	public Boolean getDescrizioneEsatta() {
		return descrizioneEsatta;
	}

	public GpGruppoFilter setDescrizioneEsatta(Boolean descrizioneEsatta) {
		this.descrizioneEsatta = descrizioneEsatta;
		return this;
	}

	public Integer getIdPadre() {
		return idPadre;
	}

	public GpGruppoFilter setIdPadre(Integer idPadre) {
		this.idPadre = idPadre;
		return this;
	}

	public GpDeTipoGruppoEnum getCodTipoGruppo() {
		return codTipoGruppo;
	}

	public GpGruppoFilter setCodTipoGruppo(GpDeTipoGruppoEnum codTipoGruppo) {
		this.codTipoGruppo = codTipoGruppo;
		return this;
	}

	public Boolean getIncludiGruppiAstratti() {
		return includiGruppiAstratti;
	}

	public GpGruppoFilter setIncludiGruppiAstratti(Boolean includiGruppiAstratti) {
		this.includiGruppiAstratti = includiGruppiAstratti;
		return this;
	}

	public Boolean getIncludiGruppiConcreti() {
		return includiGruppiConcreti;
	}

	public GpGruppoFilter setIncludiGruppiConcreti(Boolean includiGruppiConcreti) {
		this.includiGruppiConcreti = includiGruppiConcreti;
		return this;
	}

	public GpDeMacroTipoEnum getCodMacroTipo() {
		return codMacroTipo;
	}

	public GpGruppoFilter setCodMacroTipo(GpDeMacroTipoEnum codMacroTipo) {
		this.codMacroTipo = codMacroTipo;
		return this;
	}

	public Boolean getSoloGruppiSenzaPadre() {
		return soloGruppiSenzaPadre;
	}

	public GpGruppoFilter setSoloGruppiSenzaPadre(Boolean soloGruppiSenzaPadre) {
		this.soloGruppiSenzaPadre = soloGruppiSenzaPadre;
		return this;
	}

	public Integer getIdDaEscludere() {
		return idDaEscludere;
	}

	public GpGruppoFilter setIdDaEscludere(Integer idDaEscludere) {
		this.idDaEscludere = idDaEscludere;
		return this;
	}

	public String getCodProvincia() {
		return codProvincia;
	}

	public GpGruppoFilter setCodProvincia(String codProvincia) {
		this.codProvincia = codProvincia;
		return this;
	}

	public String getCodComune() {
		return codComune;
	}

	public GpGruppoFilter setCodComune(String codComune) {
		this.codComune = codComune;
		return this;
	}

	public Boolean getSoloGruppiPadre() {
		return soloGruppiPadre;
	}

	public void setSoloGruppiPadre(Boolean soloGruppiPadre) {
		this.soloGruppiPadre = soloGruppiPadre;
	}
}
