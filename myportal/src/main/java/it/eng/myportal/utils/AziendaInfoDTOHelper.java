package it.eng.myportal.utils;

import it.eng.myportal.dtos.AziendaInfoDTO;

import java.util.HashMap;
import java.util.Map;

/**
 * Questa classe mette a disposizione tutta una serie di funzionalità
 * per le pagine che gestiscono oggetti di tipo AziendaInfoDTO
 * 
 * @author Rodi A.
 *
 */
public class AziendaInfoDTOHelper {

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
	AziendaInfoDTO iterable;
	
	public static void writeAttribute(String attributeName, AziendaInfoDTO from, AziendaInfoDTO data) {
		if (RAGIONE_SOCIALE.equals(attributeName)) {
			data.setRagioneSociale(from.getRagioneSociale());
		}
		else if (CODICE_FISCALE.equals(attributeName)) {
			data.setCodiceFiscale(from.getCodiceFiscale());
		}
		else if (INDIRIZZO.equals(attributeName)) {
			data.getSedeOperativa().setIndirizzo(from.getSedeOperativa().getIndirizzo());
		}
		else if (CAP.equals(attributeName)) {
			data.getSedeOperativa().setCap(from.getSedeOperativa().getCap());
		}
		else if (DESCRIZIONE_COMUNE.equals(attributeName)) {
			data.getSedeOperativa().setComune(from.getSedeOperativa().getComune());
		}
		else if (TELEFONO.equals(attributeName)) {
			data.getSedeOperativa().setTelefono(from.getSedeOperativa().getTelefono());
		}
		else if (FAX.equals(attributeName)) {
			data.getSedeOperativa().setFax(from.getSedeOperativa().getFax());
		}
		else if (NOME_RICHIEDENTE.equals(attributeName)) {
			data.setNomeRic(from.getNomeRic());
		}
		else if (COGNOME_RICHIEDENTE.equals(attributeName)) {
			data.setCognomeRic(from.getCognomeRic());
		}
		else if (DATA_DI_NASCITA.equals(attributeName)) {
			data.setDataNascitaRic(from.getDataNascitaRic());
		}
		else if (COMUNE_O_STATO_DI_NASCITA.equals(attributeName)) {
			data.setComuneNascitaRic(from.getComuneNascitaRic());
		}
		else if (E_MAIL_REGISTRAZIONE.equals(attributeName)) {
			data.setEmail(from.getEmail());
		}
		else if (DOMANDA_SEGRETA.equals(attributeName)) {
			data.setDomanda(from.getDomanda());
		}
		else if (RISPOSTA.equals(attributeName)) {
			data.setRisposta(from.getRisposta());
		}
		else if (MITTENTE_SARE.equals(attributeName)) {
			data.setMittenteSare(from.getMittenteSare());
		}
		else if (REFERENTE_SARE.equals(attributeName)) {
			data.setReferenteSare(from.getReferenteSare());
		}
		else if (TELEFONO_REFERENTE_SARE.equals(attributeName)) {
			data.setTelefonoReferente(from.getTelefonoReferente());
		}
		else if (INDIRIZZO_E_MAIL_REFERENTE_SARE.equals(attributeName)) {
			data.setEmailReferente(from.getEmailReferente());
		}
		else if (INDIRIZZO_REFERENTE_SARE.equals(attributeName)) {
			data.setIndirizzoRic(from.getIndirizzoRic());
		}
		else if (CAP_REFERENTE_SARE.equals(attributeName)) {
			data.setCapRic(from.getCapRic());
		}
		else if (DESCRIZIONE_COMUNE_REFERENTE_SARE.equals(attributeName)) {
			data.setComune(from.getComune());
		}
		else if (TIPO_RICHIEDENTE.equals(attributeName)) {
			data.setTipoAbilitato(from.getTipoAbilitato());
		}
		else if (TIPO_DELEGATO.equals(attributeName)) {
			data.setTipoDelegato(from.getTipoDelegato());
		}
		else if (SOFTWARE_UTILIZZATO.equals(attributeName)) {
			data.setSoftwareSAREUtilizzato(from.getSoftwareSAREUtilizzato());
		}
		else if (PARTITA_IVA.equals(attributeName)) {
			data.setPartitaIva(from.getPartitaIva());
		}
		//sede legale
		else if (INDIRIZZO_SEDE_LEGALE.equals(attributeName)) {
			data.getSedeLegale().setIndirizzo(from.getSedeLegale().getIndirizzo());
		}
		else if (CAP_SEDE_LEGALE.equals(attributeName)) {
			data.getSedeLegale().setCap(from.getSedeLegale().getCap());
		}
		else if (COMUNE_SEDE_LEGALE.equals(attributeName)) {
			data.getSedeLegale().setComune(from.getSedeLegale().getComune());
		}
		else if (TELEFONO_SEDE_LEGALE.equals(attributeName)) {
			data.getSedeLegale().setTelefono(from.getSedeLegale().getTelefono());
		}
		else if (FAX_SEDE_LEGALE.equals(attributeName)) {
			data.getSedeLegale().setFax(from.getSedeLegale().getFax());
		}
		//agenzia
		else if (AGENZIA_ESTERA.equals(attributeName)) {
			data.getAgenzia().setEstera(from.getAgenzia().getEstera());
		}
		else if (NUMERO_PROVVEDIMENTO.equals(attributeName)) {
			data.getAgenzia().setNumeroProvvedimento(from.getAgenzia().getNumeroProvvedimento());
		}
		else if (DATA_PROVVEDIMENTO.equals(attributeName)) {
			data.getAgenzia().setDataProvvedimento(from.getAgenzia().getDataProvvedimento());
		}
		else if (COMUNE_DI_ISCRIZIONE_AGENZIA.equals(attributeName)) {
			data.getAgenzia().setComune(from.getAgenzia().getComune());
		}
		else if (NUMERO_DI_ISCRIZIONE_ALBO.equals(attributeName)) {
			data.getAgenzia().setNumeroIscrizione(from.getAgenzia().getNumeroIscrizione());
		}
		else if (DATA_ISCRIZIONE.equals(attributeName)) {
			data.getAgenzia().setDataIscrizione(from.getAgenzia().getDataIscrizione());
		}
		//soggetto abilitato
		else if (ORDINE_DI_ISCRIZIONE.equals(attributeName)) {
			data.getSoggettoAbilitato().setOrdineIscrizione(from.getSoggettoAbilitato().getOrdineIscrizione());
		}
		else if (COMUNE_DI_ISCRIZIONE.equals(attributeName)) {
			data.getSoggettoAbilitato().setLuogoIscrizione(from.getSoggettoAbilitato().getLuogoIscrizione());
		}
		else if (NUMERO_DI_ISCRIZIONE.equals(attributeName)) {
			data.getSoggettoAbilitato().setNumeroIscrizione(from.getSoggettoAbilitato().getNumeroIscrizione());
		}
		else if (DATA_DI_ISCRIZIONE.equals(attributeName)) {
			data.getSoggettoAbilitato().setDataIscrizione(from.getSoggettoAbilitato().getDataIscrizione());
		}		
	}
	
	public static Map<String,Object> asMap(AziendaInfoDTO aziendaInfoDTO) {
		
		HashMap<String,Object> ret = new HashMap<String,Object>();
				
		//DTOField<String> dto = new DTOField<String>();
		ret.put(RAGIONE_SOCIALE, aziendaInfoDTO.getRagioneSociale());
		ret.put(CODICE_FISCALE, aziendaInfoDTO.getCodiceFiscale());
		
		//sede operativa
		ret.put(INDIRIZZO, aziendaInfoDTO.getSedeOperativa().getIndirizzo());
		ret.put(CAP, aziendaInfoDTO.getSedeOperativa().getCap());
		ret.put(DESCRIZIONE_COMUNE, aziendaInfoDTO.getSedeOperativa().getComune().getDescrizione());
		ret.put(TELEFONO, aziendaInfoDTO.getSedeOperativa().getTelefono());
		ret.put(FAX, aziendaInfoDTO.getSedeOperativa().getFax());
		
		ret.put(NOME_RICHIEDENTE, aziendaInfoDTO.getNomeRic());
		ret.put(COGNOME_RICHIEDENTE, aziendaInfoDTO.getCognomeRic());
		ret.put(DATA_DI_NASCITA, aziendaInfoDTO.getDataNascitaRic());
		ret.put(COMUNE_O_STATO_DI_NASCITA, aziendaInfoDTO.getComuneNascitaRic().getDescrizione());
		ret.put(E_MAIL_REGISTRAZIONE, aziendaInfoDTO.getEmail());
		ret.put(DOMANDA_SEGRETA, aziendaInfoDTO.getDomanda());
		ret.put(RISPOSTA, aziendaInfoDTO.getRisposta());
		ret.put(MITTENTE_SARE, aziendaInfoDTO.getMittenteSare());
		ret.put(REFERENTE_SARE, aziendaInfoDTO.getReferenteSare());
		ret.put(TELEFONO_REFERENTE_SARE, aziendaInfoDTO.getTelefonoReferente());
		ret.put(INDIRIZZO_E_MAIL_REFERENTE_SARE, aziendaInfoDTO.getEmailReferente());
		ret.put(INDIRIZZO_REFERENTE_SARE, aziendaInfoDTO.getIndirizzoRic());
		ret.put(CAP_REFERENTE_SARE, aziendaInfoDTO.getCapRic());
		ret.put(DESCRIZIONE_COMUNE_REFERENTE_SARE, aziendaInfoDTO.getComune().getDescrizione());
		ret.put(TIPO_RICHIEDENTE, aziendaInfoDTO.getTipoAbilitato().getDescrizione());
		ret.put(TIPO_DELEGATO, aziendaInfoDTO.getTipoDelegato().getDescrizione());
		ret.put(SOFTWARE_UTILIZZATO, aziendaInfoDTO.getSoftwareSAREUtilizzato());
		ret.put(PARTITA_IVA, aziendaInfoDTO.getPartitaIva());
		
		//sede legale
		ret.put(INDIRIZZO_SEDE_LEGALE, aziendaInfoDTO.getSedeLegale().getIndirizzo());
		ret.put(CAP_SEDE_LEGALE, aziendaInfoDTO.getSedeLegale().getCap());
		ret.put(COMUNE_SEDE_LEGALE, aziendaInfoDTO.getSedeLegale().getComune().getDescrizione());
		ret.put(TELEFONO_SEDE_LEGALE, aziendaInfoDTO.getSedeLegale().getTelefono());
		ret.put(FAX_SEDE_LEGALE, aziendaInfoDTO.getSedeLegale().getFax());
		
		//agenzia
		ret.put(AGENZIA_ESTERA, aziendaInfoDTO.getAgenzia().getEstera());
		ret.put(NUMERO_PROVVEDIMENTO, aziendaInfoDTO.getAgenzia().getNumeroProvvedimento());
		ret.put(DATA_PROVVEDIMENTO, aziendaInfoDTO.getAgenzia().getDataProvvedimento());
		ret.put(COMUNE_DI_ISCRIZIONE_AGENZIA, aziendaInfoDTO.getAgenzia().getComune().getDescrizione());
		ret.put(NUMERO_DI_ISCRIZIONE_ALBO, aziendaInfoDTO.getAgenzia().getNumeroIscrizione());
		ret.put(DATA_ISCRIZIONE, aziendaInfoDTO.getAgenzia().getDataIscrizione());
		
		//soggetto abilitato
		ret.put(ORDINE_DI_ISCRIZIONE, aziendaInfoDTO.getSoggettoAbilitato().getOrdineIscrizione());
		ret.put(COMUNE_DI_ISCRIZIONE, aziendaInfoDTO.getSoggettoAbilitato().getLuogoIscrizione().getDescrizione());
		ret.put(NUMERO_DI_ISCRIZIONE, aziendaInfoDTO.getSoggettoAbilitato().getNumeroIscrizione());
		ret.put(DATA_DI_ISCRIZIONE, aziendaInfoDTO.getSoggettoAbilitato().getDataIscrizione());
				
		return ret;
				
	}
	
}
