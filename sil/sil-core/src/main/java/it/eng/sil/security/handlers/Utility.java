/*
 * Created on 6-mar-07
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.security.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.log4j.Logger;

import it.eng.sil.crypto.ConvBase64;
import it.eng.sil.crypto.DesEncrypter;
import it.eng.sil.crypto.EncrypterException;

/**
 * @author vuoto
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class Utility {

	private static final Logger log = Logger.getLogger(Utility.class.getName());

	private static Date dataUltimaLetturaProps = null;
	private static Properties properties = null;

	private static final Map mappaOperazioniSensibili;

	// private static BufferedWriter out = null;

	static {
		mappaOperazioniSensibili = new HashMap();

		// SERVIZI RICHIAMATI DIRETTAMENTE

		/**
		 * I numeri si riferisco ai servizi elencati nel doc. word contententi i servizi di cooperazione
		 */

		mappaOperazioniSensibili.put("PutLavoratoreIR", Boolean.FALSE); // 1 e 8
		mappaOperazioniSensibili.put("getLavoratoreIR", Boolean.FALSE); // 2
		mappaOperazioniSensibili.put("AggiornaCompetenzaIR", Boolean.FALSE); // 3 e 7
		mappaOperazioniSensibili.put("AggiornaCompExtraRegioneIR", Boolean.FALSE); // 4
		mappaOperazioniSensibili.put("ModificaCPICompIR", Boolean.FALSE); // 5
		mappaOperazioniSensibili.put("getCpiMasterIR", Boolean.FALSE); // 6
		// mappaOperazioniSensibili.put("AggiornaCompetenzaIR", Boolean.FALSE); //7 e 3
		// mappaOperazioniSensibili.put("PutLavoratoreIR", Boolean.FALSE); //8 e 1
		mappaOperazioniSensibili.put("AccorpaLavoratoriIR", Boolean.FALSE);
		mappaOperazioniSensibili.put("ModificaAnagraficaLavoratoreIR", Boolean.FALSE);
		mappaOperazioniSensibili.put("ModificaCodiceFiscaleIR", Boolean.FALSE); // 11
		mappaOperazioniSensibili.put("putNotifica", Boolean.FALSE); // 12
		mappaOperazioniSensibili.put("NotificaLavoratoreSIL", Boolean.FALSE); // 12 (doppio)
		mappaOperazioniSensibili.put("PresaAttoCoop", Boolean.FALSE); // 13
		mappaOperazioniSensibili.put("InvioDatiCoop", Boolean.TRUE); //
		mappaOperazioniSensibili.put("InviaMigrazioni", Boolean.TRUE);
		mappaOperazioniSensibili.put("getDatiPersonali", Boolean.TRUE); // 16
		mappaOperazioniSensibili.put("execute", Boolean.TRUE); // 17 (dal NCR al SIL)
		mappaOperazioniSensibili.put("putNotificaAccorpamento", Boolean.FALSE);
		mappaOperazioniSensibili.put("putNotificaIscrizione", Boolean.FALSE);
		// servizi accreditamento forte myportal
		mappaOperazioniSensibili.put("putAccountCittadino", Boolean.FALSE);
		mappaOperazioniSensibili.put("reinvioMailAccreditamento", Boolean.FALSE);
		mappaOperazioniSensibili.put("getAccountCittadino", Boolean.FALSE);
		mappaOperazioniSensibili.put("getDettaglioCittadino", Boolean.FALSE);
		mappaOperazioniSensibili.put("getDataAdesioneYG", Boolean.FALSE);
		mappaOperazioniSensibili.put("getStatoAdesioneYG", Boolean.FALSE);
		mappaOperazioniSensibili.put("setStatoAdesioneYG", Boolean.FALSE);
		mappaOperazioniSensibili.put("putMovimento", Boolean.FALSE);
		mappaOperazioniSensibili.put("VerificaCondizioniNeet_YG", Boolean.FALSE);
		mappaOperazioniSensibili.put("RapportiAttivi", Boolean.FALSE);
		mappaOperazioniSensibili.put("StoricoCO", Boolean.FALSE);
		mappaOperazioniSensibili.put("ricevi_RDC_by_codiceFiscale", Boolean.FALSE);
		mappaOperazioniSensibili.put("ricevi_RDC_by_codProtocolloInps", Boolean.FALSE);
		mappaOperazioniSensibili.put("loadEventiCondizionalitaRDC", Boolean.FALSE); // condizionalita
		mappaOperazioniSensibili.put("deleteEventiCondizionalitaRDC", Boolean.FALSE); // condizionalita
		mappaOperazioniSensibili.put("notifica", Boolean.FALSE);
		mappaOperazioniSensibili.put("InvioPatto", Boolean.FALSE); // patto online
		mappaOperazioniSensibili.put("RichiestaPatto", Boolean.FALSE); // patto online
		mappaOperazioniSensibili.put("GetIstanze", Boolean.FALSE); // istanze as online
		mappaOperazioniSensibili.put("invoke", Boolean.FALSE); // istanze as online GetAttachmentService
																// GetAttachmentsListByProtocollo
	}

	public static Properties loadProperties() throws Exception {

		boolean mustRead = true;

		if (dataUltimaLetturaProps != null) {
			Date now = new Date();
			if (now.getTime() - dataUltimaLetturaProps.getTime() < 600000) // 10 minuti
				mustRead = false;
		}

		if (!mustRead)
			return properties;

		File wsSecurityPropsFile = getWsSecurityPropsFile();

		Properties props = new Properties();

		props.load(new java.io.FileInputStream(wsSecurityPropsFile));
		properties = props;

		dataUltimaLetturaProps = new Date();
		return props;

	}

	static void logInvoke(String s, char tipo) throws Exception {

		// FV 04-01-2008
		// Si scrive sul log4j per motivi di efficienza

		// BufferedWriter out = new BufferedWriter(new FileWriter(filename, true));
		String suf = null;
		switch (tipo) {
		case 'I':
			suf = "Web service invocato in uscita: ";
			break;

		case 'O':
			suf = "Web service invocato in entrata: ";
			break;

		default:
			suf = "";
			break;
		}

		log.info(suf + s);
		// out.write(s);
		// out.write("\r\n");
		// out.close();

	}

	public static String decrypt(String strTxtCifratoB64) throws AxisFault {

		try {

			DesEncrypter des = new DesEncrypter();

			byte[] bufTxtCifrato2 = ConvBase64.decodificaBuf(strTxtCifratoB64);
			byte[] bufTxtChiaro2 = des.decryptBuffer(bufTxtCifrato2);
			return new String(bufTxtChiaro2);

		} catch (EncrypterException e) {
			String msg = "Errore nella decrittazione ";
			log.error(msg, e);
			throw new AxisFault(msg, e);

		}

	}

	public static String encrypt(String strTxtChiaro) throws AxisFault {

		try {
			DesEncrypter des = new DesEncrypter();

			byte[] bufTxtCifrato = des.encryptBuffer(strTxtChiaro);
			return ConvBase64.codificaBuf(bufTxtCifrato);

		} catch (EncrypterException e) {
			String msg = "Errore nella crittazione ";
			log.error(msg, e);
			throw new AxisFault(msg, e);
		}

	}

	static boolean isOperazioneConDatiSens(String operazione) throws AxisFault {

		Boolean isSensibile = (Boolean) mappaOperazioniSensibili.get(operazione);

		if (isSensibile == null) {
			String msg = "Operazione [" + operazione
					+ "] inesistente. Non presente nel catalogo dei Web Service (vedere handler dei ws su Coop-Apis)";
			log.error(msg);
			throw new AxisFault(msg);
		}
		return isSensibile.booleanValue();

	}

	static String recuperaOperazione(MessageContext msgContext) throws AxisFault, SOAPException {
		SOAPBody sb = msgContext.getCurrentMessage().getSOAPBody();
		String operazione = msgContext.getOperation().getName();

		if (operazione.equalsIgnoreCase("receive")) {

			SOAPElement elem2 = find_Operation_Element(sb);
			SOAPElement elem3 = find_IN0_Element(elem2);
			return elem3.getValue();

		} else {
			return operazione;
		}
		// throw new AxisFault("(401) operazione [" + operazione + "] NON AUTORIZZATA");
	}

	static SOAPElement find_SILAuthentication_Element(SOAPBody sb) throws AxisFault {
		Iterator iter = sb.getChildElements();

		if (iter == null)
			throw new AxisFault("(500) AXIS::SOAPBody senza elementi");

		// ricerchiamo la busta di autenticazione

		SOAPElement elem = null;
		while (iter.hasNext()) {
			elem = (SOAPElement) iter.next();

			if (elem.getElementName().getQualifiedName().indexOf(":SILAuthentication") >= 0) {
				return elem;
			}

		}

		throw new AxisFault("(500) AXIS Manca la sezione di autorizzazione");

	}

	private static SOAPElement find_Operation_Element(SOAPBody sb) throws AxisFault {
		Iterator iter = sb.getChildElements();

		SOAPElement elem = null;
		while (iter.hasNext()) {
			elem = (SOAPElement) iter.next();

			if (elem.getElementName().getQualifiedName().equals("receive")
					|| elem.getElementName().getQualifiedName().equals("ns1:receive")) {
				return elem;
			}

		}

		throw new AxisFault("(500) AXIS Manca l'elemento operazione ns1:receive oppure receive");

	}

	private static SOAPElement find_IN0_Element(SOAPElement sb) throws AxisFault {
		Iterator iter = sb.getChildElements();

		SOAPElement elem = null;
		while (iter.hasNext()) {
			elem = (SOAPElement) iter.next();

			if (elem.getElementName().getQualifiedName().equals("in0")) {
				return elem;
			}

		}

		throw new AxisFault("(500) AXIS Manca il parametro <in0> dell'operazione");

	}

	/**
	 * Nei servizi per la protocollazione NON serve l'autenticazione
	 * 
	 * @param operazione
	 * @return boolean
	 */

	static boolean isOperazioneSenzaHandler(String operazione) {

		// tutte le operaiozni sono con gli handler
		// tranne quelle della protocollazione

		/* metodi per la protocollazione */
		if (operazione.equalsIgnoreCase("login") || operazione.equalsIgnoreCase("inserimento")
				|| operazione.equalsIgnoreCase("protocollazione") || operazione.equalsIgnoreCase("Import")
				|| operazione.equalsIgnoreCase("Update") || operazione.equalsIgnoreCase("Export")
				|| operazione.equalsIgnoreCase("getDocuments") || operazione.equalsIgnoreCase("Delete")
				|| operazione.equalsIgnoreCase("getVerbaleDiagnosiByCodFiscale")
				|| operazione.equalsIgnoreCase("requestService") || operazione.equalsIgnoreCase("getInfo")
				|| operazione.equalsIgnoreCase("invioSpot")
				|| operazione.equalsIgnoreCase("inviaMessaggiMittenteTestuale")
				|| operazione.equalsIgnoreCase("SendSMS") || operazione.equalsIgnoreCase("inserisciVacancy")
				|| operazione.equalsIgnoreCase("inserisciVacancySil")
				|| operazione.equalsIgnoreCase("inserisciCandidatura")
				|| operazione.equalsIgnoreCase("inserisciCandidaturaSil")
				|| operazione.equalsIgnoreCase("getEsitoMatching") || operazione.equalsIgnoreCase("invioSAP")
				|| operazione.equalsIgnoreCase("richiestaSAP") || operazione.equalsIgnoreCase("richiestaSAP_N00_A02")
				|| operazione.equalsIgnoreCase("recuperaListaSAPNonAttive")
				|| operazione.equalsIgnoreCase("verificaEsistenzaSAP") || operazione.equalsIgnoreCase("annullaSAP")
				|| operazione.equalsIgnoreCase("notificaSAP") || operazione.equalsIgnoreCase("invioUtenteYG")
				|| operazione.equalsIgnoreCase("checkUtenteYG") || operazione.equalsIgnoreCase("getDataAdesioneYG")
				|| operazione.equalsIgnoreCase("getStatoAdesioneYG")
				|| operazione.equalsIgnoreCase("setStatoAdesioneYG")
				|| operazione.equalsIgnoreCase("getMovimentiAgricoltura") || operazione.equalsIgnoreCase("getListaSap")
				|| operazione.equalsIgnoreCase("getSapUtente") || operazione.equalsIgnoreCase("invoke") // Ricerca-Inserimento-Revoca
																										// Consenso,
																										// ProcessConvertToPdfA,
																										// SetSignatureToDoc
				|| operazione.equalsIgnoreCase("getSapUtenteDTO") || operazione.equalsIgnoreCase("save")
				|| operazione.equalsIgnoreCase("GetAuthenticationToken") // Protocollazione Pi3 - Informatica Trentina
				|| operazione.equalsIgnoreCase("GetCorrespondent") // Protocollazione Pi3 - Informatica Trentina
				|| operazione.equalsIgnoreCase("AddCorrespondent") // Protocollazione Pi3 - Informatica Trentina
				|| operazione.equalsIgnoreCase("CreateDocumentAndAddInProject") // Protocollazione Pi3 - Informatica
																				// Trentina
				|| operazione.equalsIgnoreCase("UploadFileToDocument") // Protocollazione Pi3 - Informatica Trentina
				|| operazione.equalsIgnoreCase("GetDocument") // Protocollazione Pi3 - Informatica Trentina
				|| operazione.equalsIgnoreCase("GetProject") // Protocollazione Pi3 - Informatica Trentina
				|| operazione.equalsIgnoreCase("GetActiveClassificationScheme") // Protocollazione Pi3 - Informatica
																				// Trentina
				|| operazione.equalsIgnoreCase("GetTemplateDoc") // Protocollazione Pi3 - Informatica Trentina
				|| operazione.equalsIgnoreCase("ExecuteTransmissionDocument") // Protocollazione Pi3 - Informatica
																				// Trentina
				|| operazione.equalsIgnoreCase("getDataAdesioneGO") || operazione.equalsIgnoreCase("validazioneDomanda")
				|| operazione.equalsIgnoreCase("comunicazioneVariazioneResidenza")
				|| operazione.equalsIgnoreCase("validazioneComunicazioniSuccessive")
				|| operazione.equalsIgnoreCase("gestisciDID") || operazione.equalsIgnoreCase("request")
				|| operazione.equalsIgnoreCase("select") || operazione.equalsIgnoreCase("insert")
				|| operazione.equalsIgnoreCase("verificaCondizioniNEET") || operazione.equalsIgnoreCase("OpenSPCoop_PD")
				|| operazione.equalsIgnoreCase("verifyXml") // Rendicontazione Dote Calabria
				|| operazione.equalsIgnoreCase("getRapportiAttivi") || operazione.equalsIgnoreCase("COPerLavoratore")
				|| operazione.equalsIgnoreCase("ricevi_RDC_by_codiceFiscale")
				|| operazione.equalsIgnoreCase("ricevi_RDC_by_codProtocolloInps")
				|| operazione.equalsIgnoreCase("loadEventiCondizionalitaRDC")
				|| operazione.equalsIgnoreCase("deleteEventiCondizionalitaRDC")
				|| operazione.equalsIgnoreCase("notifica") || operazione.equalsIgnoreCase("InvioPatto")
				|| operazione.equalsIgnoreCase("RichiestaPatto") || operazione.equalsIgnoreCase("GetIstanze")
				|| operazione.equalsIgnoreCase("invoke"))
			return true;

		return false;

	}

	public static List loadPropertiesAsUnorderedMap() throws Exception {
		File wsSecurityPropsFile = getWsSecurityPropsFile();

		List props = new ArrayList();

		BufferedReader reader = new BufferedReader(new FileReader(wsSecurityPropsFile));
		String line = null;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (!line.startsWith("#") && !line.equals("")) {
				int index = line.indexOf("=");

				String propName = line.substring(0, index);
				String propValue = line.substring(index + 1);
				String[] prop = new String[2];
				prop[0] = propName;
				prop[1] = propValue;

				props.add(prop);

			}

			// use line here
		}

		return props;

		/*
		 * } catch (Exception e) {
		 * 
		 * String msg = "Errore nel caricamente delle proprietà da " + wsSecurityPropsFile.getAbsolutePath();
		 * log.error(msg, e); return null; }
		 */
	}

	public static File getWsSecurityPropsFile() throws Exception {
		String axisClientConfigFile = System.getProperty("axis.ClientConfigFile");

		if ((axisClientConfigFile == null) || axisClientConfigFile.equals("")) {
			String msg = "Proprietà axis.ClientConfigFile non configurata sul server";
			log.error(msg);
			throw new Exception(msg);
		}

		String dirAxisClientConfigFile = new File(axisClientConfigFile).getParentFile().getAbsolutePath();

		File wsSecurityPropsFile = new File(dirAxisClientConfigFile + File.separatorChar + "ws-security.properties");
		return wsSecurityPropsFile;
	}

}
