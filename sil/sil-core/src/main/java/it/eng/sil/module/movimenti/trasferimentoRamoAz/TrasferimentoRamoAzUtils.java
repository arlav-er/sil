/*
 * Created on 3-dic-07
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.module.movimenti.trasferimentoRamoAz;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.sil.module.movimenti.constant.DeTipoContrattoConstant;
import it.eng.sil.util.StatementUtils;
import it.eng.sil.util.Utils;

/**
 * @author mancinid/savino
 */
public class TrasferimentoRamoAzUtils {

	/**
	 * Mapping UNISIL - VARDATORI in base al documento "MAPPATURA_SARE_UNI_SIL_varDatori.xls":<br>
	 * Mappatura allineata al traduttore usato nell'NCR.
	 */
	protected static SourceBean getUniSilMovimentoAvv(HashMap datiTestata, TreeMap sedePrecedente,
			HashMap datiLavoratore, HashMap contrattoLavoratore) throws Exception {
		SourceBean mov = new SourceBean("MOVIMENTO");

		String value = "";
		String evento = "AVV";
		String codFiscLav = (String) datiLavoratore.get(TrasferimentoRamoAzXmlConst.ELEMENTI_DATILAVORATORE[2]);
		String codComResidenzaLav = (String) datiLavoratore.get(TrasferimentoRamoAzXmlConst.ELEMENTI_DATILAVORATORE[3]);
		value = (String) datiTestata.get(TrasferimentoRamoAzXmlConst.DELEGATO);
		if ((value == null) || (value.equals(""))) {
			value = (String) datiTestata.get(
					TrasferimentoRamoAzXmlConst.CODICE_FISCALE_DATORE + TrasferimentoRamoAzXmlConst.SUFFIX_AZ_ATTUALE);
		}
		String mittenteSare = (String) datiLavoratore.get(TrasferimentoRamoAzXmlConst.MITTENTE_SARE);
		mov = getUniSilValue(mov, "REFERENTENEW", value);
		if (mittenteSare != null && !"".equals(mittenteSare)) {
			String referenteNew = (String) mov.getAttribute("REFERENTENEW");
			if (referenteNew != null)
				referenteNew = mittenteSare + " " + referenteNew;
			else
				referenteNew = mittenteSare;
			mov.updAttribute("REFERENTENEW", referenteNew);
		}

		mov = getUniSilValue(mov, "CODSOGGETTO", datiTestata, TrasferimentoRamoAzXmlConst.TIPO_DELEGATO);
		mov = getUniSilValue(mov, "EVENTONEW", evento);
		mov = getUniSilValue(mov, "DATAEVENTO", datiTestata, TrasferimentoRamoAzXmlConst.DATA_COMUNICAZIONE);
		mov = getUniSilValue(mov, "CODFISCAZ", datiTestata,
				TrasferimentoRamoAzXmlConst.CODICE_FISCALE_DATORE + TrasferimentoRamoAzXmlConst.SUFFIX_AZ_PRECEDENTE);
		mov = getUniSilValue(mov, "RAGSOCAZ", datiTestata,
				TrasferimentoRamoAzXmlConst.RAGIONE_SOCIALE_DATORE + TrasferimentoRamoAzXmlConst.SUFFIX_AZ_PRECEDENTE);
		toUpper(mov, "RAGSOCAZ");
		mov = getUniSilValue(mov, "INDIRAZ", sedePrecedente, TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE[1]);
		tronca(mov, "INDIRAZ", 60);
		mov = getUniSilValue(mov, "CAPAZ", sedePrecedente, TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE[2]);
		mov = getUniSilValue(mov, "CODCOMAZ", sedePrecedente, TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE[0]);
		mov = getUniSilValue(mov, "TELAZ", sedePrecedente, TrasferimentoRamoAzXmlConst.ELEMENTI_RECAPITI[2]);
		mov = getUniSilValue(mov, "FAXAZ", sedePrecedente, TrasferimentoRamoAzXmlConst.ELEMENTI_RECAPITI[1]);
		mov = getUniSilValue(mov, "EMAILAZ", sedePrecedente, TrasferimentoRamoAzXmlConst.ELEMENTI_RECAPITI[0]);
		mov = getUniSilValue(mov, "CODATECOAZ", datiTestata,
				TrasferimentoRamoAzXmlConst.SETTORE + TrasferimentoRamoAzXmlConst.SUFFIX_AZ_PRECEDENTE,
				"TRA_GET_CODATECOAZ");
		mov = getUniSilValue(mov, "CCNLAZ", contrattoLavoratore, TrasferimentoRamoAzXmlConst.ELEMENTI_CONTRATTO[1]);
		mov = getUniSilValue(mov, "CCNLAVV", contrattoLavoratore, TrasferimentoRamoAzXmlConst.ELEMENTI_CONTRATTO[1]);
		mov = getUniSilValue(mov, "CODFISCLAV", codFiscLav);
		// calcolare gli altri dati del lavoratore
		value = (String) datiLavoratore.get(TrasferimentoRamoAzXmlConst.ELEMENTI_DATILAVORATORE[0]);
		mov = getUniSilValue(mov, "COGNOMELAV", getMaxLengthString(40, value));
		toUpper(mov, "COGNOMELAV");
		value = (String) datiLavoratore.get(TrasferimentoRamoAzXmlConst.ELEMENTI_DATILAVORATORE[1]);
		mov = getUniSilValue(mov, "NOMELAV", getMaxLengthString(40, value));
		toUpper(mov, "NOMELAV");
		mov = getUniSilValue(mov, "CODCOMRESIDENZALAV", codComResidenzaLav);
		// Query coretta (savino 06/06/2008)
		mov = getUniSilValue(mov, "CPILAV", datiLavoratore, TrasferimentoRamoAzXmlConst.ELEMENTI_DATILAVORATORE[3],
				"TRA_GET_CODCPI");
		String dataFine = (String) contrattoLavoratore.get(TrasferimentoRamoAzXmlConst.ATTRIBUTI_CONTRATTO[3]);
		String tipoContratto = (String) contrattoLavoratore.get(TrasferimentoRamoAzXmlConst.ATTRIBUTI_CONTRATTO[0]);

		// 10/06/2008 savino aggiornato da traduttore ncr
		// commento traduttore ncr: 21/02/2008 modifiche codici contratto: aggiunte condizioni su A.07.0X e L.01.0X
		if (tipoContratto != null && (tipoContratto.equals("A.07.00") || tipoContratto.equals("L.01.00")))
			mov = getUniSilValue(mov, "DATAAVVTEMPOIND", contrattoLavoratore,
					TrasferimentoRamoAzXmlConst.ATTRIBUTI_CONTRATTO[6]);
		else if (tipoContratto != null && (tipoContratto.equals("A.07.01") || tipoContratto.equals("L.01.01")))
			mov = getUniSilValue(mov, "DATAAVVTEMPODET", contrattoLavoratore,
					TrasferimentoRamoAzXmlConst.ATTRIBUTI_CONTRATTO[6]);
		// 21/02/2008 condizione originale
		else if (dataFine == null)
			mov = getUniSilValue(mov, "DATAAVVTEMPOIND", contrattoLavoratore,
					TrasferimentoRamoAzXmlConst.ATTRIBUTI_CONTRATTO[6]);
		else
			mov = getUniSilValue(mov, "DATAAVVTEMPODET", contrattoLavoratore,
					TrasferimentoRamoAzXmlConst.ATTRIBUTI_CONTRATTO[6]);

		mov = getUniSilValue(mov, "CODTIPOAVV", contrattoLavoratore,
				TrasferimentoRamoAzXmlConst.ATTRIBUTI_CONTRATTO[0]);
		mov = getUniSilValue(mov, "CODTIPOTRASF", datiTestata, TrasferimentoRamoAzXmlConst.CODICE_TRASFERIMENTO);
		mov = getUniSilValue(mov, "CODNORMATIVAAVV", contrattoLavoratore,
				TrasferimentoRamoAzXmlConst.ATTRIBUTI_CONTRATTO[0]);
		// forse il codtipocontratto non va piu' inviato....
		mov = getUniSilValue(mov, "CODTIPOCONTRATTO", contrattoLavoratore,
				TrasferimentoRamoAzXmlConst.ATTRIBUTI_CONTRATTO[0]);
		value = (String) contrattoLavoratore.get(TrasferimentoRamoAzXmlConst.ATTRIBUTI_CONTRATTO_TIPOORARIO[0]);
		if (value != null) {
			if (value.equalsIgnoreCase("F")) {
				mov.updAttribute("FULLTIMEAVV", "X");
			} else if (!value.equalsIgnoreCase("F") && !value.equalsIgnoreCase("N")) {
				value = (String) contrattoLavoratore.get(TrasferimentoRamoAzXmlConst.ATTRIBUTI_CONTRATTO_TIPOORARIO[0]);
				mov.updAttribute("PARTTIMEAVV", value);
				value = (String) contrattoLavoratore.get(TrasferimentoRamoAzXmlConst.ATTRIBUTI_CONTRATTO_TIPOORARIO[1]);
				mov.updAttribute("ORARIOMEDIOSETTAVV", value);
			}
		}
		mov = getUniSilValue(mov, "DATACESSAZTEMPODETAVV", contrattoLavoratore,
				TrasferimentoRamoAzXmlConst.ATTRIBUTI_CONTRATTO[3]);
		mov = getUniSilValue(mov, "GGPREVISTIAGRIC", contrattoLavoratore,
				TrasferimentoRamoAzXmlConst.ATTRIBUTI_CONTRATTO[7]);
		value = StatementUtils.getStringByStatement("GET_CODMANSIONE",
				(String) contrattoLavoratore.get(TrasferimentoRamoAzXmlConst.ELEMENTI_CONTRATTO[5]));
		// 09/06/2008 savino
		if (value == null)
			value = "NT";
		mov = getUniSilValue(mov, "QUALIFICAAVV", value);

		value = (String) contrattoLavoratore.get(TrasferimentoRamoAzXmlConst.ELEMENTI_CONTRATTO[2]);
		mov = getUniSilValue(mov, "LIVELLOAVV", value);
		mov = getUniSilValue(mov, "BENEFICIAVV", contrattoLavoratore,
				TrasferimentoRamoAzXmlConst.ELEMENTI_CONTRATTO[0]);
		String dataConvenzione = (String) contrattoLavoratore
				.get(TrasferimentoRamoAzXmlConst.ATTRIBUTI_CONTRATTO_LEGGE68[0]);
		String numConvenzione = (String) contrattoLavoratore
				.get(TrasferimentoRamoAzXmlConst.ATTRIBUTI_CONTRATTO_LEGGE68[1]);
		if ((dataConvenzione != null && !dataConvenzione.equals(""))
				&& (numConvenzione != null && !numConvenzione.equals(""))) {
			mov = getUniSilValue(mov, "FLGLEGGE68", "S");
		}
		mov = getUniSilValue(mov, "DATACONVL68AVV", contrattoLavoratore,
				TrasferimentoRamoAzXmlConst.ATTRIBUTI_CONTRATTO_LEGGE68[0]);
		mov = getUniSilValue(mov, "NUMCONVL68AVV", contrattoLavoratore,
				TrasferimentoRamoAzXmlConst.ATTRIBUTI_CONTRATTO_LEGGE68[1]);
		mov = getUniSilValue(mov, "DATATRASF", datiTestata, TrasferimentoRamoAzXmlConst.DATA_INIZIO_TRASFERIMENTO);
		if (tipoContratto != null && (DeTipoContrattoConstant.mapContrattiAut_Tra.containsKey(tipoContratto)
				|| DeTipoContrattoConstant.mapContrattiAgenzia.containsKey(tipoContratto))) {
			// considero il reddito solo se maggiore di 0
			String reddito = (String) contrattoLavoratore.get(TrasferimentoRamoAzXmlConst.ELEMENTI_CONTRATTO[6]);
			if (reddito != null && !"".equals(reddito) && Integer.parseInt(reddito) > 0)
				mov = getUniSilValue(mov, "DECRETRIBUZIONEMEN", contrattoLavoratore,
						TrasferimentoRamoAzXmlConst.ELEMENTI_CONTRATTO[6]);
		} else {
			String reddito = (String) contrattoLavoratore.get(TrasferimentoRamoAzXmlConst.ELEMENTI_CONTRATTO[6]);
			if (reddito != null && !"".equals(reddito))
				mov = getUniSilValue(mov, "DECRETRIBUZIONEMEN", contrattoLavoratore,
						TrasferimentoRamoAzXmlConst.ELEMENTI_CONTRATTO[6]);
		}
		mov = getUniSilValue(mov, "CODLAVORAZIONE", contrattoLavoratore,
				TrasferimentoRamoAzXmlConst.ATTRIBUTI_CONTRATTO[4]);
		mov = getUniSilValue(mov, "CODTIPOCOMUNIC", datiTestata, TrasferimentoRamoAzXmlConst.TIPO_COMUNICAZIONE);
		mov = getUniSilValue(mov, "CODCOMUNICAZIONE", datiTestata, TrasferimentoRamoAzXmlConst.CODICE_COMUNICAZIONE);
		mov = getUniSilValue(mov, "CODCOMUNICAZIONEPREC", datiTestata,
				TrasferimentoRamoAzXmlConst.CODICE_COMUNICAZIONE_PREC);
		mov = getUniSilValue(mov, "CODTIPOENTEPREV", contrattoLavoratore,
				TrasferimentoRamoAzXmlConst.ATTRIBUTI_CONTRATTO[2]);
		mov = getUniSilValue(mov, "STRCODICEENTEPREV", contrattoLavoratore,
				TrasferimentoRamoAzXmlConst.ATTRIBUTI_CONTRATTO[5]);
		value = getFlgSocio((String) contrattoLavoratore.get(TrasferimentoRamoAzXmlConst.ATTRIBUTI_CONTRATTO[8]));
		mov = getUniSilValue(mov, "FLGSOCIO", value);
		mov = getUniSilValue(mov, "SESSOLAV", getSesso(codFiscLav));
		mov = getUniSilValue(mov, "DATANASCITALAV", getDataNascita(codFiscLav));
		mov = getUniSilValue(mov, "CODCOMNASCITALAV", getCodComuneNascita(codFiscLav));
		// NECESSARIO PER PERMETTERE IL COMPLETAMENTO DELLA VALIDAZIONE.....
		mov = getUniSilValue(mov, "CODCITTADINANZALAV", "NT");
		value = (String) contrattoLavoratore.get(TrasferimentoRamoAzXmlConst.ATTRIBUTI_CONTRATTO[1]);
		mov = getUniSilValue(mov, "PATPOSINAIL", value);
		mov.updAttribute("VERSIONETRACCIATO", "3,0,0");

		String grado = null;
		String codTipoAvv = (String) mov.getAttribute("CODTIPOAVV");
		String codTipoTrasf = (String) mov.getAttribute("CODTIPOTRASF");
		if ((codTipoAvv.equals("NB7") || codTipoAvv.equals("NO7"))
				&& (codTipoTrasf == null || ("PP".equals(codTipoTrasf) || "TP".equals(codTipoTrasf)))) {
			grado = "15";
		} else {
			if (codTipoAvv.equals("NO4") || codTipoAvv.equals("NB6")) {
				grado = "16";
			} else {
				String qualificaIstat = (String) mov.getAttribute("QUALIFICAAVV");
				if (qualificaIstat != null) {
					if (qualificaIstat.startsWith("8"))
						grado = "14";
					else if (qualificaIstat.startsWith("7") || qualificaIstat.startsWith("6")
							|| qualificaIstat.startsWith("5") || qualificaIstat.startsWith("4")
							|| qualificaIstat.startsWith("3") || qualificaIstat.startsWith("2"))
						grado = "12";
					else if (qualificaIstat.startsWith("1"))
						grado = "00";
				}
			}
		}
		if (grado != null)
			mov.setAttribute("GRADOAVV", grado);
		String reddito = (String) mov.getAttribute("DECRETRIBUZIONEMEN");
		if (reddito != null) {
			String retribuzioneAnnua = (String) reddito;
			/*
			 * Arrotondamento alle due cifre decimali.<br> 1300,556 euro mensili diventano 1300,57 mentre 1300,554
			 * diventano 1300,55
			 */
			double retribMensile = (double) Long.parseLong(retribuzioneAnnua) / 12;
			double retribShiftata = retribMensile * 100;
			double risultato = (double) Math.round(retribShiftata) / 100;
			String retribuzioneMensile = String.valueOf(risultato);
			mov.updAttribute("DECRETRIBUZIONEMEN", retribuzioneMensile);
		}
		if (reddito == null || reddito.equals(""))
			mov.updAttribute("INBASECONTRNAZ", "S");

		return mov;
	}

	protected static SourceBean getUniSilMovimentoTra(HashMap datiTestata, TreeMap sedeLavoro, TreeMap sedePrecedente,
			HashMap datiLavoratore, HashMap contrattoLavoratore) throws Exception {
		// USO PRIMA LA MAPPATURA PER L'AVVIAMENTO E POI FACCIO LE DOVUTE CORREZIONI
		SourceBean mov = getUniSilMovimentoAvv(datiTestata, sedeLavoro, datiLavoratore, contrattoLavoratore);
		mov = getUniSilValue(mov, "CODFISCAZ", datiTestata,
				TrasferimentoRamoAzXmlConst.CODICE_FISCALE_DATORE + TrasferimentoRamoAzXmlConst.SUFFIX_AZ_ATTUALE);
		mov = getUniSilValue(mov, "RAGSOCAZ", datiTestata,
				TrasferimentoRamoAzXmlConst.RAGIONE_SOCIALE_DATORE + TrasferimentoRamoAzXmlConst.SUFFIX_AZ_ATTUALE);
		mov = getUniSilValue(mov, "CODATECOAZ", datiTestata,
				TrasferimentoRamoAzXmlConst.SETTORE + TrasferimentoRamoAzXmlConst.SUFFIX_AZ_ATTUALE,
				"TRA_GET_CODATECOAZ");
		//
		mov = getUniSilValue(mov, "STRCODICEFISCALEAZPREC", datiTestata,
				TrasferimentoRamoAzXmlConst.CODICE_FISCALE_DATORE + TrasferimentoRamoAzXmlConst.SUFFIX_AZ_PRECEDENTE);
		mov = getUniSilValue(mov, "STRRAGIONESOCIALEAZPREC", datiTestata,
				TrasferimentoRamoAzXmlConst.RAGIONE_SOCIALE_DATORE + TrasferimentoRamoAzXmlConst.SUFFIX_AZ_PRECEDENTE);
		toUpper(mov, "STRRAGIONESOCIALEAZPREC");
		mov = getUniSilValue(mov, "STRINDIRIZZOAZPREC", sedePrecedente, TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE[1]);
		tronca(mov, "STRINDIRIZZOAZPREC", 60);
		mov = getUniSilValue(mov, "STRCAPAZPREC", sedePrecedente, TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE[2]);
		mov = getUniSilValue(mov, "CODCOMAZPREC", sedePrecedente, TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE[0]);
		mov = getUniSilValue(mov, "STRTELAZPREC", sedePrecedente, TrasferimentoRamoAzXmlConst.ELEMENTI_RECAPITI[2]);
		mov = getUniSilValue(mov, "STRFAXAZPREC", sedePrecedente, TrasferimentoRamoAzXmlConst.ELEMENTI_RECAPITI[1]);
		mov = getUniSilValue(mov, "STREMAILAZPREC", sedePrecedente, TrasferimentoRamoAzXmlConst.ELEMENTI_RECAPITI[0]);
		mov = getUniSilValue(mov, "CODATECOAZPREC", datiTestata,
				TrasferimentoRamoAzXmlConst.SETTORE + TrasferimentoRamoAzXmlConst.SUFFIX_AZ_PRECEDENTE,
				"TRA_GET_CODATECOAZ");
		mov.updAttribute("EVENTONEW", "TRA");
		String ccnlAvv = (String) mov.getAttribute("CCNLAVV");
		mov.setAttribute("NUOVOCCNLPROTRASF", ccnlAvv);
		String orario = (String) mov.getAttribute("ORARIOMEDIOSETTAVV");
		if (orario != null && !orario.equalsIgnoreCase("F") && !orario.equalsIgnoreCase("N"))
			mov.setAttribute("NUOVOORARIOMEDIOSETTTRASF", orario);
		mov.delAttribute("ORARIOMEDIOSETTAVV");
		String benefici = (String) mov.getAttribute("BENEFICIAVV");
		if (benefici != null)
			mov.setAttribute("NUOVIBENEFICIPROTRASF", benefici);
		mov.delAttribute("BENEFICIAVV");
		if (mov.getAttribute("DATAAVVTEMPOIND") != null)
			mov.setAttribute("NUOVOTEMPOINDTRASF", mov.getAttribute("DATAAVVTEMPOIND"));
		else if (mov.getAttribute("DATAAVVTEMPODET") != null)
			mov.setAttribute("NUOVOTEMPODETTRASF", mov.getAttribute("DATAAVVTEMPODET"));
		if (mov.getAttribute("QUALIFICAAVV") != null)
			mov.setAttribute("NUOVAQUALPROTRASF", mov.getAttribute("QUALIFICAAVV"));
		if (mov.getAttribute("LIVELLOAVV") != null)
			mov.setAttribute("NUOVOLIVPROTRASF", mov.getAttribute("LIVELLOAVV"));
		if (mov.getAttribute("GRADOAVV") != null)
			mov.setAttribute("NUOVOGRADOPROTRASF", mov.getAttribute("GRADOAVV"));
		return mov;

	}

	// La query identificata da "statementName" serve per l'accesso alla tabella di mapping associata:
	private static SourceBean getUniSilValue(SourceBean sb, String uniSilField, Map vardatoriMap,
			String vardatoriMapKey, String statementName) throws EMFInternalError, SourceBeanException {
		if (vardatoriMap.containsKey(vardatoriMapKey)) {
			String value = StatementUtils.getStringByStatement(statementName,
					(String) vardatoriMap.get(vardatoriMapKey));
			sb = getUniSilValue(sb, uniSilField, value);
		}
		return sb;
	}

	private static SourceBean getUniSilValue(SourceBean sb, String uniSilField, Map vardatoriMap,
			String vardatoriMapKey) throws SourceBeanException {
		String value = (String) vardatoriMap.get(vardatoriMapKey);
		sb = getUniSilValue(sb, uniSilField, value);
		return sb;
	}

	private static SourceBean getUniSilValue(SourceBean sb, String uniSilField, String value)
			throws SourceBeanException {
		if ((value != null) && (!value.equals(""))) {
			if ((uniSilField != null) && (!uniSilField.equals(""))) {
				sb.updAttribute(uniSilField, value);
			}
		}
		return sb;
	}

	private static boolean isApprendistato(String tipologiaContrattuale) {
		for (int i = 0; i < TrasferimentoRamoAzXmlConst.VALORI_APPRENDISTATO.length; i++) {
			if (tipologiaContrattuale.equals(TrasferimentoRamoAzXmlConst.VALORI_APPRENDISTATO[i])) {
				return true;
			}
		}
		return false;
	}

	private static String getFlgSocio(String socioLavoratore) {
		if (socioLavoratore.equalsIgnoreCase("SI")) {
			return "S";
		} else if (socioLavoratore.equalsIgnoreCase("NO")) {
			return "N";
		}
		return "";
	}

	private static String getMaxLengthString(int maxLength, String str) {
		if (str.length() > maxLength) {
			return str.substring(0, maxLength);
		}
		return str;
	}

	public static HashMap getDatiTestataVardatori(TrasferimentoRamoAzHandler handler) {
		HashMap datiTestata = new HashMap();

		datiTestata.put(TrasferimentoRamoAzXmlConst.DATA_COMUNICAZIONE, handler.getDataComunicazione()); // dataInvio
		datiTestata.put(TrasferimentoRamoAzXmlConst.TIPO_DELEGATO, handler.getTipoDelegato());
		datiTestata.put(TrasferimentoRamoAzXmlConst.DELEGATO, handler.getDelegato());
		datiTestata.put(TrasferimentoRamoAzXmlConst.CODICE_COMUNICAZIONE, handler.getCodiceComunicazione());
		datiTestata.put(TrasferimentoRamoAzXmlConst.CODICE_COMUNICAZIONE_PREC, handler.getCodiceComunicazionePrec());
		datiTestata.put(
				TrasferimentoRamoAzXmlConst.RAGIONE_SOCIALE_DATORE + TrasferimentoRamoAzXmlConst.SUFFIX_AZ_ATTUALE,
				handler.getRagioneSocialeAzAttuale());
		datiTestata.put(
				TrasferimentoRamoAzXmlConst.CODICE_FISCALE_DATORE + TrasferimentoRamoAzXmlConst.SUFFIX_AZ_ATTUALE,
				handler.getCodFiscaleAzAttuale());
		datiTestata.put(TrasferimentoRamoAzXmlConst.SETTORE + TrasferimentoRamoAzXmlConst.SUFFIX_AZ_ATTUALE,
				handler.getSettoreAzAttuale());
		datiTestata.put(TrasferimentoRamoAzXmlConst.TIPO_COMUNICAZIONE, handler.getTipoComunicazione());
		datiTestata.put(TrasferimentoRamoAzXmlConst.CODICE_TRASFERIMENTO, handler.getCodiceTrasferimento());
		datiTestata.put(TrasferimentoRamoAzXmlConst.DATA_INIZIO_TRASFERIMENTO, handler.getDataInizioTrasferimento());
		datiTestata.put(
				TrasferimentoRamoAzXmlConst.RAGIONE_SOCIALE_DATORE + TrasferimentoRamoAzXmlConst.SUFFIX_AZ_PRECEDENTE,
				handler.getRagioneSocialeAzPrecedente());
		datiTestata.put(
				TrasferimentoRamoAzXmlConst.CODICE_FISCALE_DATORE + TrasferimentoRamoAzXmlConst.SUFFIX_AZ_PRECEDENTE,
				handler.getCodFiscaleAzPrecedente());
		datiTestata.put(TrasferimentoRamoAzXmlConst.SETTORE + TrasferimentoRamoAzXmlConst.SUFFIX_AZ_PRECEDENTE,
				handler.getSettoreAzPrecedente());

		return datiTestata;
	}

	public static void setTestataAziendaParamsInRequest(SourceBean request, String codFiscAzienda,
			String ragSocialeAzienda) throws SourceBeanException {
		request.updAttribute("strCodiceFiscale", codFiscAzienda);
		request.updAttribute("strRagioneSociale", ragSocialeAzienda);
		request.updAttribute("FLGDATIOK", "S");
		request.updAttribute("codTipoAzienda", "NT");
	}

	public static void setUnitaParamsInRequest(SourceBean request, TreeMap sede, String cfAzAttuale)
			throws SourceBeanException {
		String prgAzDestinazione = Utils
				.notNull(request.getAttribute(TrasferimentoRamoAzRequestParams.PRGAZIENDADESTINAZIONE));
		if ((!request.containsAttribute(TrasferimentoRamoAzRequestParams.PRGAZIENDA))
				&& (!prgAzDestinazione.equals(""))) {
			request.updAttribute(TrasferimentoRamoAzRequestParams.PRGAZIENDA, prgAzDestinazione);
		}
		request.updAttribute("strCodicefiscaleInsUnita", cfAzAttuale);
		updSourceBeanAttribute("codCom", (String) sede.get(TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE[0]), request);
		updSourceBeanAttribute("strIndirizzo", (String) sede.get(TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE[1]),
				request);
		updSourceBeanAttribute("strCap", (String) sede.get(TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE[2]), request);
		updSourceBeanAttribute("strEmail", (String) sede.get(TrasferimentoRamoAzXmlConst.ELEMENTI_RECAPITI[0]),
				request);
		updSourceBeanAttribute("strFax", (String) sede.get(TrasferimentoRamoAzXmlConst.ELEMENTI_RECAPITI[1]), request);
		updSourceBeanAttribute("strTel", (String) sede.get(TrasferimentoRamoAzXmlConst.ELEMENTI_RECAPITI[2]), request);

		request.updAttribute("codAzStato", "1"); // "1" In attività
		request.updAttribute("codAteco", "NT");
		request.updAttribute("codCCNL", "NT");
		request.updAttribute("flgSede", "");
	}

	private static void updSourceBeanAttribute(String name, String value, SourceBean sb) throws SourceBeanException {
		if ((value != null) && (!value.equals(""))) {
			sb.updAttribute(name, value);
		}
	}

	// Recupero il sesso dal codice fiscale:
	private static String getSesso(String codiceFiscale) {
		String result = "M";
		if (codiceFiscale.substring(9, 10).compareTo("3") > 0) {
			result = "F";
		}
		return result;
	}

	// Recupero la data di nascita dal codice fiscale:
	private static String getDataNascita(String codiceFiscale) {
		try {
			String result = "";
			char[] mese = new char[] { 'A', 'B', 'C', 'D', 'E', 'H', 'L', 'M', 'P', 'R', 'S', 'T' };

			int n = Integer.parseInt(codiceFiscale.substring(9, 11));
			if (n > 40) {
				n = n - 40;
			}
			if (n < 10) {
				result = "0";
			}
			result += String.valueOf(n) + "/"; // Imposto il giorno

			char appoggio = codiceFiscale.charAt(8);
			for (int i = 0; i < mese.length; i++) {
				if (appoggio == mese[i]) {
					if (i < 9) {
						result += "0";
					}
					result += String.valueOf(i + 1) + "/"; // Imposto il mese
				}
			}

			String prefixAnno = "19";
			String currentYear = getCurrentYear();
			String annoNascita = codiceFiscale.substring(6, 8);
			int intCurrentYear = Integer.parseInt(currentYear);
			int intAnno_ = Integer.parseInt(annoNascita) - 14;
			if (currentYear.compareTo(annoNascita) > 0) {
				if ((intAnno_ >= 0) && (intCurrentYear >= intAnno_)) {
					prefixAnno = "20";
				}
			}

			return result + prefixAnno + annoNascita; // Restituisco la data di nascita
		} catch (NumberFormatException e) {
			return "";
		}
	}

	// Recupero il comune di nascita dal codice fiscale:
	private static String getCodComuneNascita(String codiceFiscale) {
		return codiceFiscale.substring(11, 15);
	}

	private static String getCurrentYear() {
		DateFormat dateFormat = new SimpleDateFormat("yy");
		Date date = new Date();
		return dateFormat.format(date);
	}

	protected static SourceBean preparaSourceBean(SourceBean sb) throws Exception {
		Vector v = sb.getAttributeAsVector("MOVIMENTO");
		for (int i = 0; i < v.size(); i++) {
			((SourceBean) v.get(i)).updAttribute("CODMONOPROV", "C");
			((SourceBean) v.get(i)).updAttribute("CONTEXT", "importa");
		}
		return sb;
	}

	private static void tronca(SourceBean mov, String key, int n) throws Exception {
		String value = (String) mov.getAttribute(key);
		if (value != null && value.length() > n) {
			value = value.substring(0, n);
			mov.updAttribute(key, value);
		}
	}

	private static void toUpper(SourceBean mov, String key) throws Exception {
		String value = (String) mov.getAttribute(key);
		if (value != null) {
			value = value.toUpperCase();
			mov.updAttribute(key, value);
		}
	}
}
