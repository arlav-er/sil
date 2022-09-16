package it.eng.sil.myaccount.model.utils;

import it.eng.sil.mycas.model.entity.profilo.AziendaInfoRettifica;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class AziendaInfoHelper {

	private static final String DATA_DI_ISCRIZIONE = "Data di iscrizione";
	private static final String NUMERO_DI_ISCRIZIONE = "Numero di iscrizione";
	private static final String ORDINE_DI_ISCRIZIONE = "Ordine di iscrizione";
	private static final String DATA_ISCRIZIONE = "Data iscrizione";
	private static final String NUMERO_DI_ISCRIZIONE_ALBO = "Numero di iscrizione albo";
	private static final String COMUNE_DI_ISCRIZIONE = "Comune di iscrizione";
	private static final String COMUNE_DI_ISCRIZIONE_AGENZIA = "Comune di iscrizione agenzia";
	private static final String DATA_PROVVEDIMENTO = "Data provvedimento";
	private static final String NUMERO_PROVVEDIMENTO = "Numero provvedimento";
	private static final String AGENZIA_ESTERA = "Agenzia estera";
	private static final String FAX_SEDE_LEGALE = "Fax Sede legale";
	private static final String TELEFONO_SEDE_LEGALE = "Telefono Sede legale";
	private static final String COMUNE_SEDE_LEGALE = "Comune Sede legale";
	private static final String CAP_SEDE_LEGALE = "CAP Sede legale";
	private static final String INDIRIZZO_SEDE_LEGALE = "Indirizzo Sede legale";
	private static final String PARTITA_IVA = "Partita IVA";
	private static final String SOFTWARE_UTILIZZATO = "Software utilizzato";
	private static final String TIPO_DELEGATO = "Tipo delegato";
	private static final String TIPO_RICHIEDENTE = "Tipo richiedente";
	private static final String DESCRIZIONE_COMUNE_REFERENTE_SARE = "Descrizione Comune Referente SARE";
	private static final String CAP_REFERENTE_SARE = "CAP Referente SARE";
	private static final String INDIRIZZO_REFERENTE_SARE = "Indirizzo Referente SARE";
	private static final String INDIRIZZO_E_MAIL_REFERENTE_SARE = "Indirizzo E-mail Referente SARE";
	private static final String TELEFONO_REFERENTE_SARE = "Telefono Referente SARE";
	private static final String REFERENTE_SARE = "Referente SARE";
	private static final String MITTENTE_SARE = "Mittente SARE";
	private static final String RISPOSTA = "Risposta";
	private static final String DOMANDA_SEGRETA = "Domanda segreta";
	private static final String E_MAIL_REGISTRAZIONE = "E-mail registrazione";
	private static final String COMUNE_O_STATO_DI_NASCITA = "Comune o stato di nascita";
	private static final String DATA_DI_NASCITA = "Data di nascita";
	private static final String COGNOME_RICHIEDENTE = "Cognome richiedente";
	private static final String NOME_RICHIEDENTE = "Nome richiedente";
	private static final String FAX = "Fax";
	private static final String TELEFONO = "Telefono";
	private static final String DESCRIZIONE_COMUNE = "Descrizione Comune";
	private static final String CAP = "CAP";
	private static final String INDIRIZZO = "Indirizzo";
	private static final String CODICE_FISCALE = "Codice fiscale";
	private static final String RAGIONE_SOCIALE = "Ragione sociale";

	public static Map<String, Object> asMap(AziendaInfoRettifica aziendaInfoRettificaDTO) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		HashMap<String, Object> ret = new HashMap<String, Object>();
		if (aziendaInfoRettificaDTO.getRagioneSociale() != null) {
			ret.put(RAGIONE_SOCIALE, aziendaInfoRettificaDTO.getRagioneSociale());
		}
		if (aziendaInfoRettificaDTO.getCodiceFiscale() != null) {
			ret.put(CODICE_FISCALE, aziendaInfoRettificaDTO.getCodiceFiscale());
		}
		// sede operativa
		if (aziendaInfoRettificaDTO.getIndirizzoSede() != null) {
			ret.put(INDIRIZZO, aziendaInfoRettificaDTO.getIndirizzoSede());
		}
		if (aziendaInfoRettificaDTO.getCapSede() != null) {
			ret.put(CAP, aziendaInfoRettificaDTO.getCapSede());
		}
		if (aziendaInfoRettificaDTO.getDeComuneSede() != null) {
			ret.put(DESCRIZIONE_COMUNE, aziendaInfoRettificaDTO.getDeComuneSede().getDescrizione());
		}
		if (aziendaInfoRettificaDTO.getTelefonoSede() != null) {
			ret.put(TELEFONO, aziendaInfoRettificaDTO.getTelefonoSede());
		}
		if (aziendaInfoRettificaDTO.getFaxSede() != null) {
			ret.put(FAX, aziendaInfoRettificaDTO.getFaxSede());
		}

		if (aziendaInfoRettificaDTO.getNomeRic() != null) {
			ret.put(NOME_RICHIEDENTE, aziendaInfoRettificaDTO.getNomeRic());
		}
		if (aziendaInfoRettificaDTO.getCognomeRic() != null) {
			ret.put(COGNOME_RICHIEDENTE, aziendaInfoRettificaDTO.getCognomeRic());
		}
		if (aziendaInfoRettificaDTO.getDtDataNascitaRic() != null) {
			ret.put(DATA_DI_NASCITA, dateFormat.format(aziendaInfoRettificaDTO.getDtDataNascitaRic()));
		}
		if (aziendaInfoRettificaDTO.getDeComuneNascitaRic() != null) {
			ret.put(COMUNE_O_STATO_DI_NASCITA, aziendaInfoRettificaDTO.getDeComuneNascitaRic().getDescrizione());
		}
		if (aziendaInfoRettificaDTO.getEmailRic() != null) {
			ret.put(E_MAIL_REGISTRAZIONE, aziendaInfoRettificaDTO.getEmailRic());
		}
		if (aziendaInfoRettificaDTO.getDomanda() != null) {
			ret.put(DOMANDA_SEGRETA, aziendaInfoRettificaDTO.getDomanda());
		}
		if (aziendaInfoRettificaDTO.getRisposta() != null) {
			ret.put(RISPOSTA, aziendaInfoRettificaDTO.getRisposta());
		}
		if (aziendaInfoRettificaDTO.getMittenteSare() != null) {
			ret.put(MITTENTE_SARE, aziendaInfoRettificaDTO.getMittenteSare());
		}
		if (aziendaInfoRettificaDTO.getReferenteSare() != null) {
			ret.put(REFERENTE_SARE, aziendaInfoRettificaDTO.getReferenteSare());
		}
		if (aziendaInfoRettificaDTO.getTelefonoReferente() != null) {
			ret.put(TELEFONO_REFERENTE_SARE, aziendaInfoRettificaDTO.getTelefonoReferente());
		}
		if (aziendaInfoRettificaDTO.getEmailReferente() != null) {
			ret.put(INDIRIZZO_E_MAIL_REFERENTE_SARE, aziendaInfoRettificaDTO.getEmailReferente());
		}
		if (aziendaInfoRettificaDTO.getIndirizzoRic() != null) {
			ret.put(INDIRIZZO_REFERENTE_SARE, aziendaInfoRettificaDTO.getIndirizzoRic());
		}
		if (aziendaInfoRettificaDTO.getCapRic() != null) {
			ret.put(CAP_REFERENTE_SARE, aziendaInfoRettificaDTO.getCapRic());
		}
		if (aziendaInfoRettificaDTO.getDeComuneRichiedente() != null) {
			ret.put(DESCRIZIONE_COMUNE_REFERENTE_SARE, aziendaInfoRettificaDTO.getDeComuneRichiedente()
					.getDescrizione());
		}
		if (aziendaInfoRettificaDTO.getDeTipoAbilitato() != null) {
			ret.put(TIPO_RICHIEDENTE, aziendaInfoRettificaDTO.getDeTipoAbilitato().getDescrizione());
		}
		if (aziendaInfoRettificaDTO.getDeTipoDelegato() != null) {
			ret.put(TIPO_DELEGATO, aziendaInfoRettificaDTO.getDeTipoDelegato().getDescrizione());
		}
		if (aziendaInfoRettificaDTO.getSwCreazioneCo() != null) {
			ret.put(SOFTWARE_UTILIZZATO, aziendaInfoRettificaDTO.getSwCreazioneCo());
		}
		if (aziendaInfoRettificaDTO.getPartitaIva() != null) {
			ret.put(PARTITA_IVA, aziendaInfoRettificaDTO.getPartitaIva());
		}
		if (aziendaInfoRettificaDTO.getIndirizzoSedeLegale() != null) {
			// sede legale
			ret.put(INDIRIZZO_SEDE_LEGALE, aziendaInfoRettificaDTO.getIndirizzoSedeLegale());
		}
		if (aziendaInfoRettificaDTO.getCapSedeLegale() != null) {
			ret.put(CAP_SEDE_LEGALE, aziendaInfoRettificaDTO.getCapSedeLegale());
		}
		if (aziendaInfoRettificaDTO.getDeComuneSedeLegale() != null) {
			ret.put(COMUNE_SEDE_LEGALE, aziendaInfoRettificaDTO.getDeComuneSedeLegale().getDescrizione());
		}
		if (aziendaInfoRettificaDTO.getTelefonoSedeLegale() != null) {
			ret.put(TELEFONO_SEDE_LEGALE, aziendaInfoRettificaDTO.getTelefonoSedeLegale());
		}
		if (aziendaInfoRettificaDTO.getFaxSedeLegale() != null) {
			ret.put(FAX_SEDE_LEGALE, aziendaInfoRettificaDTO.getFaxSedeLegale());
		}

		if (aziendaInfoRettificaDTO.getFlagAgenziaEstera() != null) {
			// agenzia
			ret.put(AGENZIA_ESTERA, aziendaInfoRettificaDTO.getFlagAgenziaEstera());
		}
		if (aziendaInfoRettificaDTO.getIscrProvvedNumero() != null) {
			ret.put(NUMERO_PROVVEDIMENTO, aziendaInfoRettificaDTO.getIscrProvvedNumero());
		}
		if (aziendaInfoRettificaDTO.getDtIscrProvvedData() != null) {
			ret.put(DATA_PROVVEDIMENTO, dateFormat.format(aziendaInfoRettificaDTO.getDtIscrProvvedData()));
		}
		if (aziendaInfoRettificaDTO.getDeComuneIscrizione() != null) {
			ret.put(COMUNE_DI_ISCRIZIONE_AGENZIA, aziendaInfoRettificaDTO.getDeComuneIscrizione().getDescrizione());
		}
		if (aziendaInfoRettificaDTO.getIscrNumero() != null) {
			ret.put(NUMERO_DI_ISCRIZIONE_ALBO, aziendaInfoRettificaDTO.getIscrNumero());
		}
		if (aziendaInfoRettificaDTO.getDtIscrData() != null) {
			ret.put(DATA_ISCRIZIONE, dateFormat.format(aziendaInfoRettificaDTO.getDtIscrData()));
		}

		if (aziendaInfoRettificaDTO.getIscrOrdine() != null) {
			// soggetto abilitato
			ret.put(ORDINE_DI_ISCRIZIONE, aziendaInfoRettificaDTO.getIscrOrdine());
		}
		if (aziendaInfoRettificaDTO.getDeComuneIscrizione() != null) {
			ret.put(COMUNE_DI_ISCRIZIONE, aziendaInfoRettificaDTO.getDeComuneIscrizione().getDescrizione());
		}
		if (aziendaInfoRettificaDTO.getIscrNumero() != null) {
			ret.put(NUMERO_DI_ISCRIZIONE, aziendaInfoRettificaDTO.getIscrNumero());
		}
		if (aziendaInfoRettificaDTO.getDtIscrData() != null) {
			ret.put(DATA_DI_ISCRIZIONE, dateFormat.format(aziendaInfoRettificaDTO.getDtIscrData()));
		}

		return ret;

	}

}
