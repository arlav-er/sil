package it.eng.sil.myaccount.model.ejb.stateless.sare;

import java.io.File;
import java.text.SimpleDateFormat;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import it.eng.sare.secure.services.ServiceMyPortalSoapProxy;
import it.eng.sil.base.enums.DeSistemaEnum;
import it.eng.sil.myaccount.model.exceptions.MyAccountException;
import it.eng.sil.myaccount.model.utils.ConstantsSingleton;
import it.eng.sil.myaccount.utils.XmlUtils;
import it.eng.sil.mycas.model.entity.decodifiche.DeProvincia;
import it.eng.sil.mycas.model.entity.profilo.AziendaInfo;
import it.eng.sil.mycas.model.entity.profilo.PfPrincipal;
import it.eng.sil.mycas.model.entity.profilo.Provincia;
import it.eng.sil.mycas.model.entity.tabellesistema.TsWsClient;
import it.eng.sil.mycas.model.manager.decodifiche.DeSistemaEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeTipoServizioEJB;
import it.eng.sil.mycas.model.manager.tabellesistema.TsWsClientEJB;

@Stateless
public class UtenteAziendaSareEJB {

	protected static Log log = LogFactory.getLog(UtenteAziendaSareEJB.class);

	// una serie di costanti che corrispondono ai tag degli xml di input e di
	// risposta.
	private final static int ESITO_POSITIVO = 0;
	private final static int UTENTE_NON_TROVATO = 2;
	DeProvincia fage;
	// TAG XML INPUT
	private final static String EL_REG_AZ = "RegistrazioneAzienda";
	private final static String EL_ACCREDITAMENTO = "DatiAccreditamento";
	private final static String EL_ACCREDITAMENTO_USERNAME = "username";
	private final static String EL_ACCREDITAMENTO_PASSWORD = "password";
	private final static String EL_ACCREDITAMENTO_MITT_SARE = "mittenteSARE";
	private final static String EL_ACCREDITAMENTO_SOFT_UTIL = "softwareUtilizzato";
	private final static String EL_ACCREDITAMENTO_TIPOUTENTESARE = "tipoUtenteSare";
	private final static String EL_ACCREDITAMENTO_IDCPI = "idCpi";
	private final static String EL_ACCREDITAMENTO_ENTE_PROV = "enteProv";
	private final static String EL_ACCREDITAMENTO_CONVENZIONE = "strConvenzione";
	private final static String EL_RICHIEDENTE = "DatiRichiedente";
	private final static String EL_RICHIEDENTE_NOME = "nome";
	private final static String EL_RICHIEDENTE_COGNOME = "cognome";
	private final static String EL_RICHIEDENTE_DAT_NASC = "dataNascita";
	private final static String EL_RICHIEDENTE_COD_COM_NASC = "codComuneNascita";
	private final static String EL_RICHIEDENTE_EMAIL = "email";
	private final static String EL_RICHIEDENTE_IND_RICH = "indirizzoRichiedente";
	private final static String EL_RICHIEDENTE_CAP_RICH = "capRichiedente";
	private final static String EL_RICHIEDENTE_COD_COM_RICH = "codComuneRichiedente";
	private final static String EL_RICHIEDENTE_COD_TIPO_ABIL = "codTipoAbilitato";
	private final static String EL_RICHIEDENTE_COD_TIPO_DEL = "codTipoDelegato";
	private final static String EL_AZIENDA = "DatiAzienda";
	private final static String EL_AZIENDA_CF = "codiceFiscale";
	private final static String EL_AZIENDA_PI = "partitaIVA";
	private final static String EL_AZIENDA_RAG_SOC = "ragioneSociale";
	private final static String EL_AZIENDA_REF_SARE = "referenteSARE";
	private final static String EL_AZIENDA_TEL_REF_SARE = "telefonoReferenteSARE";
	private final static String EL_AZIENDA_EMAIL_REF_SARE = "emailReferenteSARE";
	private final static String EL_SEDE_LEGALE = "DatiSedeLegale";
	private final static String EL_SEDE_LEGALE_IND = "indirizzoSedeLegale";
	private final static String EL_SEDE_LEGALE_CAP = "capSedeLegale";
	private final static String EL_SEDE_LEGALE_COD_COM = "codComuneSedeLegale";
	private final static String EL_SEDE_LEGALE_TEL = "telefonoSedeLegale";
	private final static String EL_SEDE_LEGALE_FAX = "faxSedeLegale";
	private final static String EL_SEDE_OPERATIVA = "DatiSedeOperativa";
	private final static String EL_SEDE_OPERATIVA_IND = "indirizzoSedeOperativa";
	private final static String EL_SEDE_OPERATIVA_CAP = "capSedeOperativa";
	private final static String EL_SEDE_OPERATIVA_COD_COM = "codComuneSedeOperativa";
	private final static String EL_SEDE_OPERATIVA_TEL = "telefonoSedeOperativa";
	private final static String EL_SEDE_OPERATIVA_FAX = "faxSedeOperativa";
	private final static String EL_SOMMINISTRAZIONE = "DatiAgenziaSomministrazione";
	private final static String EL_SOMMINISTRAZIONE_FLG_AG_ESTERA = "flagAgenziaEstera";
	private final static String EL_SOMMINISTRAZIONE_NUM_PROV = "numeroProvvedimento";
	private final static String EL_SOMMINISTRAZIONE_DAT_PROV = "dataProvvedimento";
	private final static String EL_SOMMINISTRAZIONE_COD_COM_ISCR = "codComuneIscrizione";
	private final static String EL_SOMMINISTRAZIONE_NUM_ISCR_ALBO = "numeroIscrizioneAlbo";
	private final static String EL_SOMMINISTRAZIONE_DAT_ISCR = "dataIscrizione";
	private final static String EL_SOGG_ABILITATO = "DatiSoggettoAbilitato";
	private final static String EL_SOGG_ABILITATO_ISCR_ORD = "iscrizioneOrdine";
	private final static String EL_SOGG_ABILITATO_COD_COM_ISCR_ORD = "codComuneIscrizioneOrdine";
	private final static String EL_SOGG_ABILITATO_NUM_ISCR_ORD = "numeroIscrizioneOrdine";
	private final static String EL_SOGG_ABILITATO_DAT_ISCR_ORD = "dataIscrizioneOrdine";

	// ELEMENT ABILITAZIONE
	private final static String EL_ABILITA_AZ = "AbilitazioneAzienda";
	private final static String EL_ABILITA_ACCREDITAMENTO = "DatiAccreditamento";
	private final static String EL_ABILITA_ACCREDITAMENTO_USERNAME = "username";
	private final static String EL_ABILITA_ACCREDITAMENTO_CODABILITAZIONE = "codiceAbilitazione";
	private final static String EL_ABILITA_ACCREDITAMENTO_TIPOUTENTESARE = "tipoUtenteSare";
	private final static String EL_ABILITA_ACCREDITAMENTO_PASSWORD = "password";

	// TAG RISPOSTA WS
	private final static String EL_RISPOSTA = "RegistrazioneUtente";
	private final static String EL_RISPOSTA_AGGIORNAMENTO = "AggiornamentoUtente";
	private final static String EL_RISPOSTA_ABILITAZIONE = "AbilitazioneUtente";
	private final static String EL_RISPOSTA_AGGIORNA_TIPOUTENTE = "ModificaTipoUtente";
	private final static String EL_RISPOSTA_AGGIORNA_PASSWORD = "ModificaPasswordUtente";

	private final static String EL_ESITO = "Esito";
	private final static String EL_CODICE = "codice";
	private final static String EL_DESCRIZIONE = "Descrizione";

	private SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");

	@EJB
	private TsWsClientEJB tsWsClientEJB;

	@EJB
	private DeTipoServizioEJB deTipoServizioEJB;

	@EJB
	private DeSistemaEJB deSistemaEJB;

	/**
	 * Registra un utente su SARE
	 * 
	 * @param pfPrincipalDTO
	 * @param aziendaDTO
	 * @param aziendaInfoDTO
	 * @return
	 * @throws MyAccountException
	 */
	public String registraUtenteSare(PfPrincipal pfPrincipal, AziendaInfo aziendaInfo, Boolean editSoggettoAbilitato,
			Boolean editAgenziaSomministrazione) throws Exception {
		String descrizioneRisposta;
		String inputXML = null;
		try {
			inputXML = getXMLUtenteSARE(pfPrincipal, aziendaInfo, editSoggettoAbilitato, editAgenziaSomministrazione);
			log.debug("SARE xml:" + inputXML);
			XmlUtils.validateXml(inputXML, "autenticazione" + File.separator + "RegistrazioneUtente.xsd");

			// da recuperare l'ENDPOINT del aare
			String codProvincia = aziendaInfo.getDeProvincia().getCodProvincia();
			TsWsClient endpointSare = tsWsClientEJB.find(deSistemaEJB.findById(DeSistemaEnum.SARE.toString()),
					deTipoServizioEJB.findById(ConstantsSingleton.DeTipoServizio.SARE), codProvincia);
			log.info("Invocazione registrazione SARE url:" + endpointSare.getEndpoint());
			ServiceMyPortalSoapProxy service = new ServiceMyPortalSoapProxy(endpointSare.getEndpoint());
			String response = service.registraUtente(endpointSare.getUsername(), endpointSare.getPassword(), inputXML);
			log.info("Risposta SARE xml:" + response);

			JSONObject resp = XML.toJSONObject(response);
			JSONObject risposta = resp.getJSONObject(EL_RISPOSTA);
			descrizioneRisposta = risposta.getString(EL_DESCRIZIONE);
			Integer codEsito = risposta.getInt(EL_ESITO);
			if (codEsito.intValue() == ESITO_POSITIVO) {
				return risposta.toString();
			} else {
				throw new Exception(descrizioneRisposta);
			}
		} catch (Exception e) {
			log.error(e);
			log.error("XML in INPUT: " + inputXML);
			throw new Exception("La richiesta di registrazione verso il sistema SARE ha notificato un errore");
		}
	}

	public String registraUtenteProvinciaSare(PfPrincipal pfPrincipal, Provincia provincia) throws MyAccountException {
		String descrizioneRisposta;
		try {
			String inputXML = getXMLUtenteProvinciaSARE(pfPrincipal, provincia);
			log.debug("SARE xml:" + inputXML);
			XmlUtils.validateXml(inputXML, "autenticazione" + File.separator + "RegistrazioneUtente.xsd");

			// da recuperare l'ENDPOINT del aare
			String codProvincia = provincia.getDeProvincia().getCodProvincia();
			TsWsClient endpointSare = tsWsClientEJB.find(deSistemaEJB.findById(DeSistemaEnum.SARE.toString()),
					deTipoServizioEJB.findById(ConstantsSingleton.DeTipoServizio.SARE), codProvincia);
			ServiceMyPortalSoapProxy service = new ServiceMyPortalSoapProxy(endpointSare.getEndpoint());
			String response = service.registraUtente(endpointSare.getUsername(), endpointSare.getPassword(), inputXML);
			log.info("Risposta SARE xml:" + response);

			JSONObject resp = XML.toJSONObject(response);
			JSONObject risposta = resp.getJSONObject(EL_RISPOSTA);
			descrizioneRisposta = risposta.getString(EL_DESCRIZIONE);
			Integer codEsito = risposta.getInt(EL_ESITO);
			if (codEsito.intValue() == ESITO_POSITIVO) {
				return risposta.toString();
			} else {
				throw new Exception(descrizioneRisposta);
			}
		} catch (Exception e) {
			throw new MyAccountException("La richiesta di registrazione verso il sistema SARE ha notificato un errore");
		}
	}

	/**
	 * Aggiorna le info di un utente su SARE
	 * 
	 * @param pfPrincipalDTO
	 * @param aziendaDTO
	 * @param aziendaInfoDTO
	 * @return
	 * @throws MyAccountException
	 */
	public String modificaUtenteSare(PfPrincipal pfPrincipal, AziendaInfo aziendaInfo, Boolean editSoggettoAbilitato,
			Boolean editAgenziaSomministrazione) throws MyAccountException {
		String descrizioneRisposta;
		try {
			String inputXML = getXMLUtenteSARE(pfPrincipal, aziendaInfo, editSoggettoAbilitato,
					editAgenziaSomministrazione);
			XmlUtils.validateXml(inputXML, "autenticazione" + File.separator + "RegistrazioneUtente.xsd");

			// da recuperare l'ENDPOINT del aare
			String codProvincia = aziendaInfo.getDeProvincia().getCodProvincia();
			TsWsClient endpointSare = tsWsClientEJB.find(deSistemaEJB.findById(DeSistemaEnum.SARE.toString()),
					deTipoServizioEJB.findById(ConstantsSingleton.DeTipoServizio.SARE), codProvincia);
			ServiceMyPortalSoapProxy service = new ServiceMyPortalSoapProxy(endpointSare.getEndpoint());
			String response = service.aggiornaUtente(endpointSare.getUsername(), endpointSare.getPassword(), inputXML);
			log.info("Risposta SARE xml:" + response);

			JSONObject resp = XML.toJSONObject(response);
			JSONObject risposta = resp.getJSONObject(EL_RISPOSTA_AGGIORNAMENTO);
			descrizioneRisposta = risposta.getString(EL_DESCRIZIONE);
			Integer codEsito = risposta.getInt(EL_ESITO);
			//1= auth fail
			//3= codabil. non valido
			//4=tipo utente non valido
			//21 errore XML
			if (codEsito.intValue() == ESITO_POSITIVO) {
				return risposta.toString();
			}else if (codEsito.intValue() == UTENTE_NON_TROVATO) {
				//se l'utente non c'è la prendo buona
				return risposta.toString();
			} else {
				log.info("Risposta SARE esito NEGATIVO: " + codEsito.intValue());
				throw new Exception(descrizioneRisposta);
			}
		} catch (Exception e) {
			throw new MyAccountException(
					"La richiesta di aggiornamento dati verso il sistema SARE ha notificato un errore: "
							+ e.getMessage());
		}
	}

	private String getXMLUtenteSARE(PfPrincipal pfPrincipal, AziendaInfo aziendaInfo, Boolean editSoggettoAbilitato,
			Boolean editAgenziaSomministrazione) throws MyAccountException {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = builderFactory.newDocumentBuilder();
			Document document = builder.newDocument();
			Element registrazioneAzienda = (Element) document.createElement(EL_REG_AZ);
			Node root = document.appendChild(registrazioneAzienda);

			Element datiAccreditamento = getElementDatiAccreditamento(document, registrazioneAzienda, pfPrincipal,
					aziendaInfo);
			root.appendChild(datiAccreditamento);

			Element datiRichiedente = getElementDatiRichiedente(document, registrazioneAzienda, pfPrincipal,
					aziendaInfo);
			root.appendChild(datiRichiedente);

			Element datiAzienda = getElementDatiAzienda(document, aziendaInfo);
			root.appendChild(datiAzienda);

			Element datiSedeLegale = getElementDatiSedeLegale(document, aziendaInfo);
			root.appendChild(datiSedeLegale);

			Element datiSedeOperativa = getElementDatiSedeOperativa(document, aziendaInfo);
			root.appendChild(datiSedeOperativa);

			if (editAgenziaSomministrazione) {
				Element datiAgenziaSomministrazione = getElementDatiAgenziaSomministrazione(document, aziendaInfo);
				root.appendChild(datiAgenziaSomministrazione);
			}

			if (editSoggettoAbilitato) {
				Element datiSoggettoAbilitato = getElementDatiSoggettoAbilitato(document, aziendaInfo);
				root.appendChild(datiSoggettoAbilitato);
			}
			String inputXML = XmlUtils.getStringFromDoc(document);

			return inputXML;
		} catch (Exception e) {
			throw new MyAccountException(
					"La richiesta di aggiornamento dati verso il sistema SARE ha notificato un errore");
		}
	}

	private String getXMLUtenteProvinciaSARE(PfPrincipal pfPrincipal, Provincia provincia) throws MyAccountException {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = builderFactory.newDocumentBuilder();
			Document document = builder.newDocument();
			Element registrazioneAzienda = (Element) document.createElement(EL_REG_AZ);
			Node root = document.appendChild(registrazioneAzienda);

			Element datiAccreditamento = getElementDatiAccreditamentoProvincia(document, registrazioneAzienda,
					pfPrincipal, provincia);
			root.appendChild(datiAccreditamento);

			Element datiRichiedente = getElementDatiRichiedenteProvincia(document, registrazioneAzienda, pfPrincipal,
					provincia);
			root.appendChild(datiRichiedente);

			Element datiAzienda = getElementDatiAziendaProvincia(document, provincia);
			root.appendChild(datiAzienda);

			Element datiSedeOperativa = getElementDatiSedeOperativaProvincia(document, provincia);
			root.appendChild(datiSedeOperativa);

			String inputXML = XmlUtils.domToString(document);

			return inputXML;
		} catch (Exception e) {
			throw new MyAccountException(
					"La richiesta di aggiornamento dati verso il sistema SARE ha notificato un errore");
		}
	}

	/**
	 * Abilita un utente su SARE
	 * 
	 * @param utenteSARE
	 * @param aziendaInfoDTO
	 * @return
	 * @throws MyAccountException
	 */
	public String abilitaUtenteSare(PfPrincipal pfPrincipal, AziendaInfo aziendaInfo) throws MyAccountException {

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		String descrizioneRisposta;
		try {

			builder = builderFactory.newDocumentBuilder();
			Document document = builder.newDocument();
			Element abilitazioneAzienda = (Element) document.createElement(EL_ABILITA_AZ);
			Node root = document.appendChild(abilitazioneAzienda);

			Element datiAbiliazione = getElementDatiAbilitazione(document, abilitazioneAzienda, aziendaInfo);
			root.appendChild(datiAbiliazione);

			String inputXML = XmlUtils.domToString(document);
			log.info("SARE xml:" + inputXML);
			XmlUtils.validateXml(inputXML, "autenticazione" + File.separator + "AbilitazioneAzienda.xsd");

			// da recuperare l'ENDPOINT del aare
			String codProvincia = aziendaInfo.getDeProvincia().getCodProvincia();
			TsWsClient endpointSare = tsWsClientEJB.find(deSistemaEJB.findById(DeSistemaEnum.SARE.toString()),
					deTipoServizioEJB.findById(ConstantsSingleton.DeTipoServizio.SARE), codProvincia);
			ServiceMyPortalSoapProxy service = new ServiceMyPortalSoapProxy(endpointSare.getEndpoint());
			String response = service.abilitaUtente(endpointSare.getUsername(), endpointSare.getPassword(), inputXML);
			log.debug("Risposta SARE xml:" + response);

			JSONObject resp = XML.toJSONObject(response);
			JSONObject risposta = resp.getJSONObject(EL_RISPOSTA_ABILITAZIONE);
			descrizioneRisposta = risposta.getString(EL_DESCRIZIONE);
			Integer codEsito = risposta.getInt(EL_ESITO);
			if (codEsito.intValue() == ESITO_POSITIVO) {
				return risposta.toString();
			} else {
				throw new Exception(descrizioneRisposta);
			}
		} catch (Exception e) {
			throw new MyAccountException(
					"La richiesta di abilitazione utente verso il sistema SARE ha notificato un errore: "
							+ e.getMessage());
		}
	}

	/**
	 * Sincrtonizza la password dell'utente su SARE
	 * 
	 * @param utente
	 * @param aziendaInfoDTO
	 * @return
	 * @throws MyAccountException
	 */
	public String modificaPasswordUtenteSare(PfPrincipal utente, AziendaInfo aziendaInfo) throws MyAccountException {

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		String descrizioneRisposta;
		try {

			builder = builderFactory.newDocumentBuilder();
			Document document = builder.newDocument();

			Element abilitazioneAzienda = (Element) document.createElement(EL_ABILITA_AZ);

			Element datiAccred = document.createElement(EL_ABILITA_ACCREDITAMENTO);
			abilitazioneAzienda.appendChild(datiAccred);

			Element elUsername = document.createElement(EL_ABILITA_ACCREDITAMENTO_USERNAME);
			elUsername.appendChild(document.createTextNode(utente.getUsername()));
			Element elPassword = document.createElement(EL_ABILITA_ACCREDITAMENTO_PASSWORD);
			elPassword.appendChild(document.createTextNode(utente.getPassWord()));
			datiAccred.appendChild(elUsername);
			datiAccred.appendChild(elPassword);

			document.appendChild(abilitazioneAzienda);

			String inputXML = XmlUtils.domToString(document);
			log.debug("SARE xml:" + inputXML);
			XmlUtils.validateXml(inputXML, "autenticazione" + File.separator + "CambioPasswordAzienda.xsd");

			// da recuperare l'ENDPOINT del aare
			String codProvincia = aziendaInfo.getDeProvincia().getCodProvincia();
			TsWsClient endpointSare = tsWsClientEJB.find(deSistemaEJB.findById(DeSistemaEnum.SARE.toString()),
					deTipoServizioEJB.findById(ConstantsSingleton.DeTipoServizio.SARE), codProvincia);
			ServiceMyPortalSoapProxy service = new ServiceMyPortalSoapProxy(endpointSare.getEndpoint());
			String response = service.modificaPasswordUtente(endpointSare.getUsername(), endpointSare.getPassword(),
					inputXML);
			log.debug("Risposta SARE xml:" + response);

			JSONObject resp = XML.toJSONObject(response);
			JSONObject risposta = resp.getJSONObject(EL_RISPOSTA_AGGIORNA_PASSWORD);
			descrizioneRisposta = risposta.getString(EL_DESCRIZIONE);
			Integer codEsito = risposta.getInt(EL_ESITO);
			if (codEsito.intValue() == ESITO_POSITIVO) {
				return risposta.toString();
			} else {
				// Exception e = new Exception(descrizioneRisposta);
				throw new MyAccountException(
						"La richiesta di modifica password verso il sistema SARE ha notificato un errore: "
								+ descrizioneRisposta);
			}
		} catch (Exception e) {
			throw new MyAccountException(
					"La richiesta di modifica password verso il sistema SARE ha notificato un errore: "
							+ e.getMessage());
		}
	}

	/**
	 * Sincronizza la password dell'utente CPI su SARE
	 * 
	 * @param utente
	 * @param provinciaDTO
	 * @return
	 * @throws MyAccountException
	 */
	public String modificaPasswordCpiSare(PfPrincipal utente, Provincia provincia) throws MyAccountException {

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		String descrizioneRisposta;
		try {

			builder = builderFactory.newDocumentBuilder();
			Document document = builder.newDocument();
			Element abilitazioneAzienda = (Element) document.createElement(EL_ABILITA_AZ);
			Node root = document.appendChild(abilitazioneAzienda);

			Element datiAbiliazione = getElementDatiUtente(document, abilitazioneAzienda, utente);
			root.appendChild(datiAbiliazione);

			String inputXML = XmlUtils.domToString(document);
			log.debug("SARE xml:" + inputXML);
			XmlUtils.validateXml(inputXML, "autenticazione" + File.separator + "CambioPasswordAzienda.xsd");

			// da recuperare l'ENDPOINT del aare
			String codProvincia = provincia.getDeProvincia().getCodProvincia();
			TsWsClient endpointSare = tsWsClientEJB.find(deSistemaEJB.findById(DeSistemaEnum.SARE.toString()),
					deTipoServizioEJB.findById(ConstantsSingleton.DeTipoServizio.SARE), codProvincia);
			ServiceMyPortalSoapProxy service = new ServiceMyPortalSoapProxy(endpointSare.getEndpoint());
			String response = service.modificaPasswordUtente(endpointSare.getUsername(), endpointSare.getPassword(),
					inputXML);
			log.debug("Risposta SARE xml:" + response);

			JSONObject resp = XML.toJSONObject(response);
			JSONObject risposta = resp.getJSONObject(EL_RISPOSTA_AGGIORNA_PASSWORD);
			descrizioneRisposta = risposta.getString(EL_DESCRIZIONE);
			Integer codEsito = risposta.getInt(EL_ESITO);
			if (codEsito.intValue() == ESITO_POSITIVO) {
				return risposta.toString();
			} else {
				throw new Exception(descrizioneRisposta);
			}
		} catch (Exception e) {
			throw new MyAccountException(
					"La richiesta di modifica password verso il sistema SARE ha notificato un errore");
		}
	}

	private Element getElementDatiUtente(Document document, Element registrazioneAzienda, PfPrincipal utente) {
		Element datiAccreditamentoAbilitaz = document.createElement(EL_ACCREDITAMENTO);

		Element elUsername = document.createElement(EL_ABILITA_ACCREDITAMENTO_USERNAME);
		elUsername.appendChild(document.createTextNode(utente.getUsername()));
		Element elPassword = document.createElement(EL_ABILITA_ACCREDITAMENTO_PASSWORD);
		elPassword.appendChild(document.createTextNode(utente.getPassWord()));
		datiAccreditamentoAbilitaz.appendChild(elUsername);
		datiAccreditamentoAbilitaz.appendChild(elPassword);

		return datiAccreditamentoAbilitaz;
	}

	private Element getElementDatiAbilitazione(Document document, Element registrazioneAzienda, AziendaInfo utenteSARE) {
		Element datiAccreditamentoAbilitaz = document.createElement(EL_ACCREDITAMENTO);

		Element elUsername = document.createElement(EL_ABILITA_ACCREDITAMENTO_USERNAME);
		elUsername.appendChild(document.createTextNode(utenteSARE.getPfPrincipal().getUsername()));
		Element elCodAbilitazione = document.createElement(EL_ABILITA_ACCREDITAMENTO_CODABILITAZIONE);
		elCodAbilitazione.appendChild(document.createTextNode(utenteSARE.getDeAutorizzazioneSare()
				.getCodAutorizzazioneSare()));
		datiAccreditamentoAbilitaz.appendChild(elUsername);
		datiAccreditamentoAbilitaz.appendChild(elCodAbilitazione);

		return datiAccreditamentoAbilitaz;
	}

	private Element getElementDatiModificaTipoUtente(Document document, Element registrazioneAzienda,
			AziendaInfo utenteSARE) {
		Element datiAccreditamentoAbilitaz = document.createElement(EL_ACCREDITAMENTO);

		Element elUsername = document.createElement(EL_ABILITA_ACCREDITAMENTO_USERNAME);
		elUsername.appendChild(document.createTextNode(utenteSARE.getPfPrincipal().getUsername()));
		Element elCodAbilitazione = document.createElement(EL_ABILITA_ACCREDITAMENTO_TIPOUTENTESARE);
		elCodAbilitazione.appendChild(document.createTextNode(utenteSARE.getDeTipoUtenteSare().getCodTipoUtenteSare()));
		datiAccreditamentoAbilitaz.appendChild(elUsername);
		datiAccreditamentoAbilitaz.appendChild(elCodAbilitazione);

		return datiAccreditamentoAbilitaz;
	}

	private Element getElementDatiAccreditamento(Document document, Element registrazioneAzienda,
			PfPrincipal pfPrincipalDTO, AziendaInfo aziendaInfo) {
		Element datiAccreditamento = document.createElement(EL_ACCREDITAMENTO);
		// registrazioneAzienda.appendChild(datiAccreditamento);

		Element elUsername = document.createElement(EL_ACCREDITAMENTO_USERNAME);
		elUsername.appendChild(document.createTextNode(pfPrincipalDTO.getUsername()));
		Element elPassword = document.createElement(EL_ACCREDITAMENTO_PASSWORD);
		elPassword.appendChild(document.createTextNode(pfPrincipalDTO.getPassWord()));
		Element elMittSare = document.createElement(EL_ACCREDITAMENTO_MITT_SARE);
		elMittSare.appendChild(document.createTextNode(aziendaInfo.getMittenteSare()));
		Element elSoftUtil = document.createElement(EL_ACCREDITAMENTO_SOFT_UTIL);
		elSoftUtil.appendChild(document.createTextNode("01"));
		Element elTipoUtente = document.createElement(EL_ACCREDITAMENTO_TIPOUTENTESARE);
		if (aziendaInfo.getDeTipoUtenteSare() != null
				|| !("").equals(aziendaInfo.getDeTipoUtenteSare().getCodTipoUtenteSare())) {
			elTipoUtente.appendChild(document.createTextNode(aziendaInfo.getDeTipoUtenteSare().getCodTipoUtenteSare()));
		} else {
			elTipoUtente.appendChild(document.createTextNode("R"));
		}

		// solo per cpi
		// Element elIdCpi = document.createElement(EL_ACCREDITAMENTO_IDCPI);
		// elIdCpi.appendChild(document.createTextNode(""));
		// Element elEnteProv = document.createElement(EL_ACCREDITAMENTO_ENTE_PROV);
		// elEnteProv.appendChild(document.createTextNode(""));
		Element elStrConv = document.createElement(EL_ACCREDITAMENTO_CONVENZIONE);
		elStrConv.appendChild(document.createTextNode("0"));

		datiAccreditamento.appendChild(elUsername);
		datiAccreditamento.appendChild(elPassword);
		datiAccreditamento.appendChild(elMittSare);
		datiAccreditamento.appendChild(elSoftUtil);
		datiAccreditamento.appendChild(elTipoUtente);

		// datiAccreditamento.appendChild(elIdCpi);
		// datiAccreditamento.appendChild(elEnteProv);
		datiAccreditamento.appendChild(elStrConv);

		return datiAccreditamento;
	}

	private Element getElementDatiAccreditamentoProvincia(Document document, Element registrazioneAzienda,
			PfPrincipal pfPrincipalDTO, Provincia provincia) {
		Element datiAccreditamento = document.createElement(EL_ACCREDITAMENTO);
		// registrazioneAzienda.appendChild(datiAccreditamento);

		Element elUsername = document.createElement(EL_ACCREDITAMENTO_USERNAME);
		elUsername.appendChild(document.createTextNode(pfPrincipalDTO.getUsername()));
		Element elPassword = document.createElement(EL_ACCREDITAMENTO_PASSWORD);
		elPassword.appendChild(document.createTextNode(pfPrincipalDTO.getPassWord()));
		Element elSoftUtil = document.createElement(EL_ACCREDITAMENTO_SOFT_UTIL);
		elSoftUtil.appendChild(document.createTextNode("01"));
		Element elTipoUtente = document.createElement(EL_ACCREDITAMENTO_TIPOUTENTESARE);
		if (provincia.getCodTipoUtenteSare() != null || !("").equals(provincia.getCodTipoUtenteSare())) {
			elTipoUtente.appendChild(document.createTextNode(provincia.getCodTipoUtenteSare()));
		} else {
			elTipoUtente.appendChild(document.createTextNode("R"));
		}

		// solo per cpi
		Element elIdCpi = document.createElement(EL_ACCREDITAMENTO_IDCPI);
		elIdCpi.appendChild(document.createTextNode(provincia.getDeCpi().getCodCpi()));
		Element elEnteProv = document.createElement(EL_ACCREDITAMENTO_ENTE_PROV);
		elEnteProv.appendChild(document.createTextNode("0" + provincia.getDeProvincia().getCodProvincia()));
		Element elStrConv = document.createElement(EL_ACCREDITAMENTO_CONVENZIONE);
		elStrConv.appendChild(document.createTextNode("0"));

		datiAccreditamento.appendChild(elUsername);
		datiAccreditamento.appendChild(elPassword);
		datiAccreditamento.appendChild(elSoftUtil);
		datiAccreditamento.appendChild(elTipoUtente);

		datiAccreditamento.appendChild(elIdCpi);
		datiAccreditamento.appendChild(elEnteProv);
		datiAccreditamento.appendChild(elStrConv);

		return datiAccreditamento;
	}

	private Element getElementDatiRichiedente(Document document, Element registrazioneAzienda,
			PfPrincipal pfPrincipalDTO, AziendaInfo aziendaInfo) {
		Element datiRichiedente = document.createElement(EL_RICHIEDENTE);
		// registrazioneAzienda.appendChild(datiRichiedente);

		Element el_nome = document.createElement(EL_RICHIEDENTE_NOME);
		el_nome.appendChild(document.createTextNode(aziendaInfo.getNomeRic()));
		Element el_cognome = document.createElement(EL_RICHIEDENTE_COGNOME);
		el_cognome.appendChild(document.createTextNode(aziendaInfo.getCognomeRic()));
		Element el_datNasc = document.createElement(EL_RICHIEDENTE_DAT_NASC);
		el_datNasc.appendChild(document.createTextNode(formatter1.format(aziendaInfo.getDtDataNascitaRic())));
		Element el_codComNasc = document.createElement(EL_RICHIEDENTE_COD_COM_NASC);
		el_codComNasc.appendChild(document.createTextNode(aziendaInfo.getDeComuneNascitaRic().getCodCom()));
		Element el_email = document.createElement(EL_RICHIEDENTE_EMAIL);
		el_email.appendChild(document.createTextNode(pfPrincipalDTO.getEmail()));
		Element el_indRich = document.createElement(EL_RICHIEDENTE_IND_RICH);
		el_indRich.appendChild(document.createTextNode(aziendaInfo.getIndirizzoRic()));
		Element el_capRich = document.createElement(EL_RICHIEDENTE_CAP_RICH);
		el_capRich.appendChild(document.createTextNode(aziendaInfo.getCapRic()));
		Element el_codComRich = document.createElement(EL_RICHIEDENTE_COD_COM_RICH);
		el_codComRich.appendChild(document.createTextNode(aziendaInfo.getDeComuneRichiedente().getCodCom()));
		Element el_codTipoAbil = document.createElement(EL_RICHIEDENTE_COD_TIPO_ABIL);
		el_codTipoAbil.appendChild(document.createTextNode(aziendaInfo.getDeTipoAbilitato().getCodTipoAbilitato()));
		Element el_codTipoDel = document.createElement(EL_RICHIEDENTE_COD_TIPO_DEL);
		if (aziendaInfo.getDeTipoDelegato() == null) {
			el_codTipoDel.appendChild(document.createTextNode(""));
		} else {
			el_codTipoDel.appendChild(document.createTextNode(aziendaInfo.getDeTipoDelegato().getCodTipoDelegato()));
		}
		datiRichiedente.appendChild(el_nome);
		datiRichiedente.appendChild(el_cognome);
		datiRichiedente.appendChild(el_datNasc);
		datiRichiedente.appendChild(el_codComNasc);
		datiRichiedente.appendChild(el_email);
		datiRichiedente.appendChild(el_indRich);
		datiRichiedente.appendChild(el_capRich);
		datiRichiedente.appendChild(el_codComRich);
		datiRichiedente.appendChild(el_codTipoAbil);
		datiRichiedente.appendChild(el_codTipoDel);

		return datiRichiedente;
	}

	private Element getElementDatiRichiedenteProvincia(Document document, Element registrazioneAzienda,
			PfPrincipal pfPrincipalDTO, Provincia provincia) {
		Element datiRichiedente = document.createElement(EL_RICHIEDENTE);
		// registrazioneAzienda.appendChild(datiRichiedente);

		Element el_email = document.createElement(EL_RICHIEDENTE_EMAIL);
		el_email.appendChild(document.createTextNode(provincia.getEmailPerCo()));

		datiRichiedente.appendChild(el_email);

		return datiRichiedente;
	}

	private Element getElementDatiAzienda(Document document, AziendaInfo aziendaInfo) {
		Element datiAzienda = document.createElement(EL_AZIENDA);
		Element el_cf = document.createElement(EL_AZIENDA_CF);
		el_cf.appendChild(document.createTextNode((aziendaInfo.getCodiceFiscale()).toUpperCase()));
		Element el_pi = document.createElement(EL_AZIENDA_PI);
		el_pi.appendChild(document.createTextNode(aziendaInfo.getPartitaIva()));
		Element el_ragsoc = document.createElement(EL_AZIENDA_RAG_SOC);
		el_ragsoc.appendChild(document.createTextNode(aziendaInfo.getRagioneSociale()));
		Element el_referente = document.createElement(EL_AZIENDA_REF_SARE);
		el_referente.appendChild(document.createTextNode(aziendaInfo.getReferenteSare()));
		Element el_telreferente = document.createElement(EL_AZIENDA_TEL_REF_SARE);
		el_telreferente.appendChild(document.createTextNode(aziendaInfo.getTelefonoReferente()));
		Element el_emailreferente = document.createElement(EL_AZIENDA_EMAIL_REF_SARE);
		el_emailreferente.appendChild(document.createTextNode(aziendaInfo.getEmailReferente()));
		datiAzienda.appendChild(el_cf);
		datiAzienda.appendChild(el_pi);
		datiAzienda.appendChild(el_ragsoc);
		datiAzienda.appendChild(el_referente);
		datiAzienda.appendChild(el_telreferente);
		datiAzienda.appendChild(el_emailreferente);

		return datiAzienda;
	}

	private Element getElementDatiAziendaProvincia(Document document, Provincia provincia) {
		Element datiAzienda = document.createElement(EL_AZIENDA);

		Element el_ragsoc = document.createElement(EL_AZIENDA_RAG_SOC);
		el_ragsoc.appendChild(document.createTextNode(provincia.getDeCpi().getDescrizione()));

		datiAzienda.appendChild(el_ragsoc);

		return datiAzienda;
	}

	private Element getElementDatiSedeLegale(Document document, AziendaInfo aziendaInfo) {
		Element datiSedeLegale = document.createElement(EL_SEDE_LEGALE);
		Element el_indSedeLeg = document.createElement(EL_SEDE_LEGALE_IND);
		el_indSedeLeg.appendChild(document.createTextNode(aziendaInfo.getIndirizzoSedeLegale()));
		Element el_capSedeLeg = document.createElement(EL_SEDE_LEGALE_CAP);
		el_capSedeLeg.appendChild(document.createTextNode(aziendaInfo.getCapSedeLegale()));
		Element el_codComSedeLeg = document.createElement(EL_SEDE_LEGALE_COD_COM);
		el_codComSedeLeg.appendChild(document.createTextNode(aziendaInfo.getDeComuneSedeLegale().getCodCom()));
		Element el_telSedeLeg = document.createElement(EL_SEDE_LEGALE_TEL);
		el_telSedeLeg.appendChild(document.createTextNode(aziendaInfo.getTelefonoSedeLegale()));
		Element el_faxSedeLeg = document.createElement(EL_SEDE_LEGALE_FAX);
		el_faxSedeLeg.appendChild(document.createTextNode(aziendaInfo.getFaxSedeLegale()));
		datiSedeLegale.appendChild(el_indSedeLeg);
		datiSedeLegale.appendChild(el_capSedeLeg);
		datiSedeLegale.appendChild(el_codComSedeLeg);
		datiSedeLegale.appendChild(el_telSedeLeg);
		datiSedeLegale.appendChild(el_faxSedeLeg);

		return datiSedeLegale;
	}

	private Element getElementDatiSedeOperativa(Document document, AziendaInfo aziendaInfo) {
		Element datiSedeOperativa = document.createElement(EL_SEDE_OPERATIVA);
		Element el_indSedeOp = document.createElement(EL_SEDE_OPERATIVA_IND);
		el_indSedeOp.appendChild(document.createTextNode(aziendaInfo.getIndirizzoSede()));
		Element el_capSedeOp = document.createElement(EL_SEDE_OPERATIVA_CAP);
		el_capSedeOp.appendChild(document.createTextNode(aziendaInfo.getCapSede()));
		Element el_codComSedeOp = document.createElement(EL_SEDE_OPERATIVA_COD_COM);
		el_codComSedeOp.appendChild(document.createTextNode(aziendaInfo.getDeComuneSede().getCodCom()));
		Element el_telSedeOp = document.createElement(EL_SEDE_OPERATIVA_TEL);
		el_telSedeOp.appendChild(document.createTextNode(aziendaInfo.getTelefonoSede()));
		Element el_faxSedeOp = document.createElement(EL_SEDE_OPERATIVA_FAX);
		el_faxSedeOp.appendChild(document.createTextNode(aziendaInfo.getFaxSede()));
		datiSedeOperativa.appendChild(el_indSedeOp);
		datiSedeOperativa.appendChild(el_capSedeOp);
		datiSedeOperativa.appendChild(el_codComSedeOp);
		datiSedeOperativa.appendChild(el_telSedeOp);
		datiSedeOperativa.appendChild(el_faxSedeOp);

		return datiSedeOperativa;
	}

	private Element getElementDatiSedeOperativaProvincia(Document document, Provincia provincia) {
		Element datiSedeOperativa = document.createElement(EL_SEDE_OPERATIVA);
		Element el_indSedeOp = document.createElement(EL_SEDE_OPERATIVA_IND);
		el_indSedeOp.appendChild(document.createTextNode(provincia.getDeCpi().getIndirizzo()));
		Element el_telSedeOp = document.createElement(EL_SEDE_OPERATIVA_TEL);
		el_telSedeOp.appendChild(document.createTextNode(provincia.getDeCpi().getTel()));

		datiSedeOperativa.appendChild(el_indSedeOp);
		datiSedeOperativa.appendChild(el_telSedeOp);

		return datiSedeOperativa;
	}

	private Element getElementDatiAgenziaSomministrazione(Document document, AziendaInfo aziendaInfo) {
		Element datiAgenziaSomministrazione = document.createElement(EL_SOMMINISTRAZIONE);
		Element el_flgAgEst = document.createElement(EL_SOMMINISTRAZIONE_FLG_AG_ESTERA);
		String checkFlgAgEsterna = "0";
		if (aziendaInfo.getFlagAgenziaEstera()) {
			checkFlgAgEsterna = "1";
		}
		el_flgAgEst.appendChild(document.createTextNode(checkFlgAgEsterna));
		Element el_numProvv = document.createElement(EL_SOMMINISTRAZIONE_NUM_PROV);
		el_numProvv.appendChild(document.createTextNode(aziendaInfo.getIscrProvvedNumero()));
		Element el_datProvv = document.createElement(EL_SOMMINISTRAZIONE_DAT_PROV);
		if (aziendaInfo.getDtIscrProvvedData() == null) {
			el_datProvv.appendChild(document.createTextNode(null));
		} else {
			el_datProvv.appendChild(document.createTextNode(formatter1.format(aziendaInfo.getDtIscrProvvedData())));
		}
		Element el_codComIscr = document.createElement(EL_SOMMINISTRAZIONE_COD_COM_ISCR);
		if (aziendaInfo.getDeComuneIscrizione() == null) {
			el_codComIscr.appendChild(document.createTextNode(null));
		} else {
			el_codComIscr.appendChild(document.createTextNode(aziendaInfo.getDeComuneIscrizione().getCodCom()));
		}
		Element el_numIscrAlbo = document.createElement(EL_SOMMINISTRAZIONE_NUM_ISCR_ALBO);
		if (aziendaInfo.getIscrNumero() == null) {
			el_numIscrAlbo.appendChild(document.createTextNode(null));
		} else {
			el_numIscrAlbo.appendChild(document.createTextNode(aziendaInfo.getIscrNumero()));
		}
		Element el_datIscr = document.createElement(EL_SOMMINISTRAZIONE_DAT_ISCR);
		if (aziendaInfo.getDtIscrData() == null) {
			el_datIscr.appendChild(document.createTextNode(null));
		} else {
			el_datIscr.appendChild(document.createTextNode(formatter1.format(aziendaInfo.getDtIscrData())));
		}
		datiAgenziaSomministrazione.appendChild(el_flgAgEst);
		datiAgenziaSomministrazione.appendChild(el_numProvv);
		datiAgenziaSomministrazione.appendChild(el_datProvv);
		datiAgenziaSomministrazione.appendChild(el_codComIscr);
		datiAgenziaSomministrazione.appendChild(el_numIscrAlbo);
		datiAgenziaSomministrazione.appendChild(el_datIscr);

		return datiAgenziaSomministrazione;
	}

	private Element getElementDatiSoggettoAbilitato(Document document, AziendaInfo aziendaInfo) {
		Element datiSoggettoAbilitato = document.createElement(EL_SOGG_ABILITATO);
		Element el_iscrOrd = document.createElement(EL_SOGG_ABILITATO_ISCR_ORD);

		if (aziendaInfo.getIscrOrdine() == null) {
			el_iscrOrd.appendChild(document.createTextNode(null));
		} else {
			el_iscrOrd.appendChild(document.createTextNode(aziendaInfo.getIscrOrdine()));
		}
		Element el_codComIscrOrd = document.createElement(EL_SOGG_ABILITATO_COD_COM_ISCR_ORD);
		if (aziendaInfo.getDeComuneIscrizione() == null) {
			el_codComIscrOrd.appendChild(document.createTextNode(null));
		} else {
			el_codComIscrOrd.appendChild(document.createTextNode(aziendaInfo.getDeComuneIscrizione().getCodCom()));
		}

		Element el_numIscrOrd = document.createElement(EL_SOGG_ABILITATO_NUM_ISCR_ORD);
		if (aziendaInfo.getIscrNumero() == null) {
			el_numIscrOrd.appendChild(document.createTextNode(null));
		} else {
			el_numIscrOrd.appendChild(document.createTextNode(aziendaInfo.getIscrNumero()));
		}
		Element elDatIscrOrd = document.createElement(EL_SOGG_ABILITATO_DAT_ISCR_ORD);
		if (aziendaInfo.getDtIscrData() == null) {
			elDatIscrOrd.appendChild(document.createTextNode(null));
		} else {
			elDatIscrOrd.appendChild(document.createTextNode(formatter1.format(aziendaInfo.getDtIscrData())));
		}

		datiSoggettoAbilitato.appendChild(el_iscrOrd);
		datiSoggettoAbilitato.appendChild(el_codComIscrOrd);
		datiSoggettoAbilitato.appendChild(el_numIscrOrd);
		datiSoggettoAbilitato.appendChild(elDatIscrOrd);

		return datiSoggettoAbilitato;
	}

}