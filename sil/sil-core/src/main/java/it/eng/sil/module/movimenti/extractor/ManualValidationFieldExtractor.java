package it.eng.sil.module.movimenti.extractor;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Classe di utilità per estrarre i campi da una mappa basata sul file di configurazione
 * ManualValidationFieldsMapping.xml
 * 
 * @author pironi
 * 
 */
public class ManualValidationFieldExtractor {

	public String estraiFlgLavoroStagionale(Map record) {
		return record.get("FLGLAVOROSTAGIONALE") == null ? null : record.get("FLGLAVOROSTAGIONALE").toString();
	}

	public String estraiStrCodFiscPromotoreTir(Map record) {
		return record.get("STRCODFISCPROMOTORETIR") == null ? null : record.get("STRCODFISCPROMOTORETIR").toString();
	}

	public String estraiCodVariazione(Map record) {
		return record.get("CODVARIAZIONE") == null ? null : record.get("CODVARIAZIONE").toString();
	}

	public String estraiCodMonoTempo(Map record) {
		return record.get("CODMONOTEMPO") == null ? "" : record.get("CODMONOTEMPO").toString();
	}

	public String estraiCodTipoMov(Map record) {
		return record.get("CODTIPOMOV") == null ? "" : record.get("CODTIPOMOV").toString();
	}

	public String estraiDataFineMovimento(Map record) {
		return record.get("DATFINEMOV") == null ? "" : record.get("DATFINEMOV").toString();
	}

	public String estraiDataFineMovimentoPrecedente(Map record) {
		return record.get("DATFINEMOVPREC") == null ? "" : record.get("DATFINEMOVPREC").toString();
	}

	public String estraiDataFinePeriodoFormativo(Map record) {
		return record.get("DATFINEPF") == null ? "" : record.get("DATFINEPF").toString();
	}

	public String estraiCodTipoContratto(Map record) {
		return record.get("CODTIPOASS") == null ? "" : record.get("CODTIPOASS").toString();
	}

	public String estraiDataInizio(Map record) {
		return record.get("DATINIZIOMOV") == null ? "" : record.get("DATINIZIOMOV").toString();
	}

	public String estraiCodTipoOrario(Map record) {
		return record.get("CODORARIO") == null ? "" : record.get("CODORARIO").toString();
	}

	public BigDecimal estraiOreSettimanali(Map record) {
		return record.get("NUMORESETT") == null ? null : (BigDecimal) record.get("NUMORESETT");
	}

	public String estraiTipoAzienda(Map record) {
		return record.get("CODAZTIPOAZIENDA") == null ? "" : record.get("CODAZTIPOAZIENDA").toString();
	}

	public String estraiAssunzionePropria(Map record) {
		return record.get("FLGASSPROPRIA") == null ? "" : record.get("FLGASSPROPRIA").toString();
	}

	public String estraiTipoTrasformazione(Map record) {
		return record.get("CODTIPOTRASF") == null ? "" : record.get("CODTIPOTRASF").toString();
	}

	public String estraiInizioMissione(Map record) {
		return record.get("DATINIZIORAPLAV") == null ? "" : record.get("DATINIZIORAPLAV").toString();
	}

	public String estraiFineMissione(Map record) {
		return record.get("DATFINERAPLAV") == null ? "" : record.get("DATFINERAPLAV").toString();
	}

	public String estraiInizioContrattoSomm(Map record) {
		return record.get("DATAZINTINIZIOCONTRATTO") == null ? "" : record.get("DATAZINTINIZIOCONTRATTO").toString();
	}

	public String estraiFineContrattoSomm(Map record) {
		return record.get("DATAZINTFINECONTRATTO") == null ? "" : record.get("DATAZINTFINECONTRATTO").toString();
	}

	public String estraiCodMotivoCessazione(Map record) {
		return record.get("CODMVCESSAZIONE") == null ? "" : record.get("CODMVCESSAZIONE").toString();
	}

	public BigDecimal estraiPrgAzienda(Map record) {
		return record.get("PRGAZIENDA") == null ? null : (BigDecimal) record.get("PRGAZIENDA");
	}

	public BigDecimal estraiPrgUnita(Map record) {
		return record.get("PRGUNITAPRODUTTIVA") == null ? null : (BigDecimal) record.get("PRGUNITAPRODUTTIVA");
	}

	public BigDecimal estraiPrgAziendaUtiliz(Map record) {
		return record.get("PRGAZIENDAUTIL") == null ? null : (BigDecimal) record.get("PRGAZIENDAUTIL");
	}

	public BigDecimal estraiPrgUnitaUtiliz(Map record) {
		return record.get("PRGUNITAUTIL") == null ? null : (BigDecimal) record.get("PRGUNITAUTIL");
	}

	public BigDecimal estraiCdnLavoratore(Map record) {
		return record.get("CDNLAVORATORE") == null ? null : (BigDecimal) record.get("CDNLAVORATORE");
	}

	public Double estraiCompensoRetribuzioneAnno(Map record) {
		return record.get("DECRETRIBUZIONEANN") == null ? null
				: new Double(record.get("DECRETRIBUZIONEANN").toString());
	}

}
