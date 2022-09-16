package it.eng.myportal.entity.ejb;

import it.eng.myportal.dtos.AgenziaDTO;
import it.eng.myportal.dtos.AziendaInfoDTO;
import it.eng.myportal.dtos.PfPrincipalDTO;
import it.eng.myportal.dtos.ProvinciaDTO;
import it.eng.myportal.dtos.SoggettoDTO;
import it.eng.myportal.dtos.UtenteSAREDTO;
import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.enums.TipoServizio;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;
import it.eng.sare.secure.services.ServiceMyPortalSoapProxy;

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

/**
 * EJB per i servizi SARE. Permette la comunicazione dati verso SARE.
 * 
 * @author Coticone D., Rodi A.
 * 
 */
@Stateless
public class UtenteAziendaSARE {

	protected static Log log = LogFactory.getLog(UtenteAziendaSARE.class);

	// una serie di costanti che corrispondono ai tag degli xml di input e di
	// risposta.
	private final static int ESITO_POSITIVO = 0;

	// TAG XML INPUT
	private final static String EL_REG_AZ = "RegistrazioneAzienda";
	private final static String EL_ACCREDITAMENTO = "DatiAccredidamento";
	private final static String EL_ACCREDITAMENTO_USERNAME = "username";
	private final static String EL_ACCREDITAMENTO_PASSWORD = "password";
	private final static String EL_ACCREDITAMENTO_MITT_SARE = "mittenteSARE";
	private final static String EL_ACCREDITAMENTO_SOFT_UTIL = "softwareUtilizzato";
	private final static String EL_ACCREDITAMENTO_TIPOUTENTESARE = "tipoUtenteSare";
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
	private final static String EL_ABILITA_ACCREDITAMENTO = "DatiAccredidamento";
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
	private WsEndpointHome wsEndpointHome;

	/**
	 * Registra un utente su SARE
	 * 
	 * @param pfPrincipalDTO
	 * @param aziendaDTO
	 * @param aziendaInfoDTO
	 * @return
	 */
	public String registraUtenteSare(PfPrincipalDTO pfPrincipalDTO, AziendaInfoDTO aziendaInfoDTO) {
		String descrizioneRisposta;
		try {
			String inputXML = getXMLUtenteSARE(pfPrincipalDTO, aziendaInfoDTO);
			log.debug("SARE xml:" + inputXML);
			Utils.validateXml(inputXML, "autenticazione" + File.separator + "registrazioneAzienda.xsd");

			// da recuperare l'ENDPOINT del aare
			String id = aziendaInfoDTO.getProvinciaRiferimento().getId();
			ServiceMyPortalSoapProxy service = new ServiceMyPortalSoapProxy(getEndPointSARE(id));
			String[] user = getUserEndPointSARE(id);
			String response = service.registraUtente(user[0], user[1], inputXML);
			log.debug("Risposta SARE xml:" + response);

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
			throw new MyPortalException("La richiesta di registrazione verso il sistema SARE ha notificato un errore",
					e, true);
		}
	}

	/**
	 * Aggiorna le info di un utente su SARE
	 * 
	 * @param pfPrincipalDTO
	 * @param aziendaDTO
	 * @param aziendaInfoDTO
	 * @return
	 */
	public String modificaUtenteSare(PfPrincipalDTO pfPrincipalDTO, AziendaInfoDTO aziendaInfoDTO) {
		String descrizioneRisposta;
		try {
			String inputXML = getXMLUtenteSARE(pfPrincipalDTO, aziendaInfoDTO);
			Utils.validateXml(inputXML, "autenticazione" + File.separator + "registrazioneAzienda.xsd");

			// da recuperare l'ENDPOINT del aare
			String id = aziendaInfoDTO.getProvinciaRiferimento().getId();
			ServiceMyPortalSoapProxy service = new ServiceMyPortalSoapProxy(getEndPointSARE(id));
			String[] user = getUserEndPointSARE(id);
			String response = service.aggiornaUtente(user[0], user[1], inputXML);
			log.debug("Risposta SARE xml:" + response);

			JSONObject resp = XML.toJSONObject(response);
			JSONObject risposta = resp.getJSONObject(EL_RISPOSTA_AGGIORNAMENTO);
			descrizioneRisposta = risposta.getString(EL_DESCRIZIONE);
			Integer codEsito = risposta.getInt(EL_ESITO);
			if (codEsito.intValue() == ESITO_POSITIVO) {
				return risposta.toString();
			} else {
				throw new Exception(descrizioneRisposta);
			}
		} catch (Exception e) {
			throw new MyPortalException(
					"La richiesta di aggiornamento dati verso il sistema SARE ha notificato un errore", e, true);
		}
	}
	
	private String getXMLUtenteSARE(PfPrincipalDTO pfPrincipalDTO, AziendaInfoDTO aziendaInfoDTO) {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = builderFactory.newDocumentBuilder();
			Document document = builder.newDocument();
			Element registrazioneAzienda = (Element) document.createElement(EL_REG_AZ);
			Node root = document.appendChild(registrazioneAzienda);

			Element datiAccreditamento = getElementDatiAccreditamento(document, registrazioneAzienda, pfPrincipalDTO,
					aziendaInfoDTO);
			root.appendChild(datiAccreditamento);

			Element datiRichiedente = getElementDatiRichiedente(document, registrazioneAzienda, pfPrincipalDTO,
					aziendaInfoDTO);
			root.appendChild(datiRichiedente);

			Element datiAzienda = getElementDatiAzienda(document, aziendaInfoDTO);
			root.appendChild(datiAzienda);

			Element datiSedeLegale = getElementDatiSedeLegale(document, aziendaInfoDTO);
			root.appendChild(datiSedeLegale);

			Element datiSedeOperativa = getElementDatiSedeOperativa(document, aziendaInfoDTO);
			root.appendChild(datiSedeOperativa);

			if (ConstantsSingleton.DeTipoAbilitato.AGENZIA_SOMMINISTRAZIONE.equals(aziendaInfoDTO.getTipoAbilitato()
					.getId())) {
				Element datiAgenziaSomministrazione = getElementDatiAgenziaSomministrazione(document, aziendaInfoDTO);
				root.appendChild(datiAgenziaSomministrazione);
			}

			if (ConstantsSingleton.DeTipoAbilitato.SOGGETTO_ABILITATO.equals(aziendaInfoDTO.getTipoAbilitato().getId())) {
				Element datiSoggettoAbilitato = getElementDatiSoggettoAbilitato(document, aziendaInfoDTO);
				root.appendChild(datiSoggettoAbilitato);
			}
			String inputXML = Utils.domToString(document);

			return inputXML;
		} catch (Exception e) {
			throw new MyPortalException(
			        "La richiesta di aggiornamento dati verso il sistema SARE ha notificato un errore", e, true);
		}
	}

	/**
	 * Abilita un utente su SARE
	 * 
	 * @param utenteSARE
	 * @param aziendaInfoDTO
	 * @return
	 */
	public String abilitaUtenteSare(UtenteSAREDTO utenteSARE, AziendaInfoDTO aziendaInfoDTO) {

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		String descrizioneRisposta;
		try {

			builder = builderFactory.newDocumentBuilder();
			Document document = builder.newDocument();
			Element abilitazioneAzienda = (Element) document.createElement(EL_ABILITA_AZ);
			Node root = document.appendChild(abilitazioneAzienda);

			Element datiAbiliazione = getElementDatiAbilitazione(document, abilitazioneAzienda, utenteSARE);
			root.appendChild(datiAbiliazione);

			String inputXML = Utils.domToString(document);
			log.debug("SARE xml:" + inputXML);
			Utils.validateXml(inputXML, "autenticazione" + File.separator + "AbilitazioneAzienda.xsd");

			// da recuperare l'ENDPOINT del aare
			String id = aziendaInfoDTO.getProvinciaRiferimento().getId();
			ServiceMyPortalSoapProxy service = new ServiceMyPortalSoapProxy(getEndPointSARE(id));
			String[] user = getUserEndPointSARE(id);
			String response = service.abilitaUtente(user[0], user[1], inputXML);
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
			throw new MyPortalException(
			        "La richiesta di abilitazione utente verso il sistema SARE ha notificato un errore", e, true);
		}
	}

	/**
	 * Modifica la tipologia di un utente su SARE
	 * 
	 * @param utenteSARE
	 * @param aziendaInfoDTO
	 * @return
	 */
	@Deprecated
	public String modificaTipoUtenteSare(UtenteSAREDTO utenteSARE, AziendaInfoDTO aziendaInfoDTO) {

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		String descrizioneRisposta;
		try {

			builder = builderFactory.newDocumentBuilder();
			Document document = builder.newDocument();
			Element abilitazioneAzienda = (Element) document.createElement(EL_ABILITA_AZ);
			Node root = document.appendChild(abilitazioneAzienda);

			Element datiAbiliazione = getElementDatiModificaTipoUtente(document, abilitazioneAzienda, utenteSARE);
			root.appendChild(datiAbiliazione);

			String inputXML = Utils.domToString(document);
			log.debug("SARE xml:" + inputXML);
			Utils.validateXml(inputXML, "autenticazione" + File.separator + "TipoUtenteSare.xsd");

			// da recuperare l'ENDPOINT del aare
			String id = aziendaInfoDTO.getProvinciaRiferimento().getId();
			ServiceMyPortalSoapProxy service = new ServiceMyPortalSoapProxy(getEndPointSARE(id));
			String[] user = getUserEndPointSARE(id);
			String response = service.modificaTipoUtente(user[0], user[1], inputXML);
			log.debug("Risposta SARE xml:" + response);

			JSONObject resp = XML.toJSONObject(response);
			JSONObject risposta = resp.getJSONObject(EL_RISPOSTA_AGGIORNA_TIPOUTENTE);
			descrizioneRisposta = risposta.getString(EL_DESCRIZIONE);
			Integer codEsito = risposta.getInt(EL_ESITO);
			if (codEsito.intValue() == ESITO_POSITIVO) {
				return risposta.toString();
			} else {
				throw new Exception(descrizioneRisposta);
			}
		} catch (Exception e) {
			throw new MyPortalException(
			        "La richiesta di modifica tipo utente verso il sistema SARE ha notificato un errore", e, true);
		}
	}

	/**
	 * Sincrtonizza la password dell'utente su SARE
	 * 
	 * @param utente
	 * @param aziendaInfoDTO
	 * @return
	 */
	public String modificaPasswordUtenteSare(PfPrincipalDTO utente, AziendaInfoDTO aziendaInfoDTO) {

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

			String inputXML = Utils.domToString(document);
			log.debug("SARE xml:" + inputXML);
			Utils.validateXml(inputXML, "autenticazione" + File.separator + "CambioPasswordAzienda.xsd");

			// da recuperare l'ENDPOINT del aare
			String id = aziendaInfoDTO.getProvinciaRiferimento().getId();
			ServiceMyPortalSoapProxy service = new ServiceMyPortalSoapProxy(getEndPointSARE(id));
			String[] user = getUserEndPointSARE(id);
			String response = service.modificaPasswordUtente(user[0], user[1], inputXML);
			log.debug("Risposta SARE xml:" + response);

			JSONObject resp = XML.toJSONObject(response);
			JSONObject risposta = resp.getJSONObject(EL_RISPOSTA_AGGIORNA_PASSWORD);
			descrizioneRisposta = risposta.getString(EL_DESCRIZIONE);
			Integer codEsito = risposta.getInt(EL_ESITO);
			if (codEsito.intValue() == ESITO_POSITIVO) {
				return risposta.toString();
			} else {
				Exception e = new Exception(descrizioneRisposta);
				throw new MyPortalException("La richiesta di modifica password verso il sistema SARE ha notificato un errore", e, true);								
			}
		} catch (Exception e) {
			throw new MyPortalException("La richiesta di modifica password verso il sistema SARE ha notificato un errore", e, true);
		}
	}

	/**
	 * Sincronizza la password dell'utente CPI su SARE
	 * 
	 * @param utente
	 * @param provinciaDTO
	 * @return
	 */
	public String modificaPasswordCpiSare(PfPrincipalDTO utente, ProvinciaDTO provinciaDTO) {

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

			String inputXML = Utils.domToString(document);
			log.debug("SARE xml:" + inputXML);
			Utils.validateXml(inputXML, "autenticazione" + File.separator + "CambioPasswordAzienda.xsd");

			// da recuperare l'ENDPOINT del aare
			String id = provinciaDTO.getProvincia().getId();
			ServiceMyPortalSoapProxy service = new ServiceMyPortalSoapProxy(getEndPointSARE(id));
			String[] user = getUserEndPointSARE(id);
			String response = service.modificaPasswordUtente(user[0], user[1], inputXML);
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
			throw new MyPortalException(
			        "La richiesta di modifica password verso il sistema SARE ha notificato un errore", e, true);
		}
	}

	private Element getElementDatiUtente(Document document, Element registrazioneAzienda, PfPrincipalDTO utente) {
		Element datiAccreditamentoAbilitaz = document.createElement(EL_ACCREDITAMENTO);

		Element elUsername = document.createElement(EL_ABILITA_ACCREDITAMENTO_USERNAME);
		elUsername.appendChild(document.createTextNode(utente.getUsername()));
		Element elPassword = document.createElement(EL_ABILITA_ACCREDITAMENTO_PASSWORD);
		elPassword.appendChild(document.createTextNode(utente.getPassWord()));
		datiAccreditamentoAbilitaz.appendChild(elUsername);
		datiAccreditamentoAbilitaz.appendChild(elPassword);

		return datiAccreditamentoAbilitaz;
	}

	private Element getElementDatiAbilitazione(Document document, Element registrazioneAzienda, UtenteSAREDTO utenteSARE) {
		Element datiAccreditamentoAbilitaz = document.createElement(EL_ACCREDITAMENTO);

		Element elUsername = document.createElement(EL_ABILITA_ACCREDITAMENTO_USERNAME);
		elUsername.appendChild(document.createTextNode(utenteSARE.getUsername()));
		Element elCodAbilitazione = document.createElement(EL_ABILITA_ACCREDITAMENTO_CODABILITAZIONE);
		elCodAbilitazione.appendChild(document.createTextNode(utenteSARE.getModificaAutorizzazioneSARE()));
		datiAccreditamentoAbilitaz.appendChild(elUsername);
		datiAccreditamentoAbilitaz.appendChild(elCodAbilitazione);

		return datiAccreditamentoAbilitaz;
	}

	private Element getElementDatiModificaTipoUtente(Document document, Element registrazioneAzienda,
			UtenteSAREDTO utenteSARE) {
		Element datiAccreditamentoAbilitaz = document.createElement(EL_ACCREDITAMENTO);

		Element elUsername = document.createElement(EL_ABILITA_ACCREDITAMENTO_USERNAME);
		elUsername.appendChild(document.createTextNode(utenteSARE.getUsername()));
		Element elCodAbilitazione = document.createElement(EL_ABILITA_ACCREDITAMENTO_TIPOUTENTESARE);
		elCodAbilitazione.appendChild(document.createTextNode(utenteSARE.getCodTipoUtenteSare()));
		datiAccreditamentoAbilitaz.appendChild(elUsername);
		datiAccreditamentoAbilitaz.appendChild(elCodAbilitazione);

		return datiAccreditamentoAbilitaz;
	}

	private Element getElementDatiAccreditamento(Document document, Element registrazioneAzienda,
			PfPrincipalDTO pfPrincipalDTO, AziendaInfoDTO aziendaInfoDTO) {
		Element datiAccreditamento = document.createElement(EL_ACCREDITAMENTO);
		// registrazioneAzienda.appendChild(datiAccreditamento);

		Element elUsername = document.createElement(EL_ACCREDITAMENTO_USERNAME);
		elUsername.appendChild(document.createTextNode(pfPrincipalDTO.getUsername()));
		Element elPassword = document.createElement(EL_ACCREDITAMENTO_PASSWORD);
		elPassword.appendChild(document.createTextNode(pfPrincipalDTO.getPassWord()));
		Element elMittSare = document.createElement(EL_ACCREDITAMENTO_MITT_SARE);
		elMittSare.appendChild(document.createTextNode(aziendaInfoDTO.getMittenteSare()));
		Element elSoftUtil = document.createElement(EL_ACCREDITAMENTO_SOFT_UTIL);
		elSoftUtil.appendChild(document.createTextNode(aziendaInfoDTO.getSoftwareSAREUtilizzato()
				.getCodSoftwareSAREUtilizzato()));
		Element elTipoUtente = document.createElement(EL_ACCREDITAMENTO_TIPOUTENTESARE);
		if (aziendaInfoDTO.getCodTipoUtenteSare() != null || !("").equals(aziendaInfoDTO.getCodTipoUtenteSare())) {
			elTipoUtente.appendChild(document.createTextNode(aziendaInfoDTO.getCodTipoUtenteSare()));
		} else {
			elTipoUtente.appendChild(document.createTextNode("R"));
		}
		datiAccreditamento.appendChild(elUsername);
		datiAccreditamento.appendChild(elPassword);
		datiAccreditamento.appendChild(elMittSare);
		datiAccreditamento.appendChild(elSoftUtil);
		datiAccreditamento.appendChild(elTipoUtente);

		return datiAccreditamento;
	}

	private Element getElementDatiRichiedente(Document document, Element registrazioneAzienda,
			PfPrincipalDTO pfPrincipalDTO, AziendaInfoDTO aziendaInfoDTO) {
		Element datiRichiedente = document.createElement(EL_RICHIEDENTE);
		// registrazioneAzienda.appendChild(datiRichiedente);

		Element el_nome = document.createElement(EL_RICHIEDENTE_NOME);
		el_nome.appendChild(document.createTextNode(aziendaInfoDTO.getNomeRic()));
		Element el_cognome = document.createElement(EL_RICHIEDENTE_COGNOME);
		el_cognome.appendChild(document.createTextNode(aziendaInfoDTO.getCognomeRic()));
		Element el_datNasc = document.createElement(EL_RICHIEDENTE_DAT_NASC);
		el_datNasc.appendChild(document.createTextNode(formatter1.format(aziendaInfoDTO.getDataNascitaRic())));
		Element el_codComNasc = document.createElement(EL_RICHIEDENTE_COD_COM_NASC);
		el_codComNasc.appendChild(document.createTextNode(aziendaInfoDTO.getComuneNascitaRic().getId()));
		Element el_email = document.createElement(EL_RICHIEDENTE_EMAIL);
		el_email.appendChild(document.createTextNode(pfPrincipalDTO.getEmail()));
		Element el_indRich = document.createElement(EL_RICHIEDENTE_IND_RICH);
		el_indRich.appendChild(document.createTextNode(aziendaInfoDTO.getIndirizzoRic()));
		Element el_capRich = document.createElement(EL_RICHIEDENTE_CAP_RICH);
		el_capRich.appendChild(document.createTextNode(aziendaInfoDTO.getCapRic()));
		Element el_codComRich = document.createElement(EL_RICHIEDENTE_COD_COM_RICH);
		el_codComRich.appendChild(document.createTextNode(aziendaInfoDTO.getComune().getId()));
		Element el_codTipoAbil = document.createElement(EL_RICHIEDENTE_COD_TIPO_ABIL);
		el_codTipoAbil.appendChild(document.createTextNode(aziendaInfoDTO.getTipoAbilitato().getId()));
		Element el_codTipoDel = document.createElement(EL_RICHIEDENTE_COD_TIPO_DEL);
		if (aziendaInfoDTO.getTipoDelegato().getId() == null) {
			el_codTipoDel.appendChild(document.createTextNode(""));
		} else {
			el_codTipoDel.appendChild(document.createTextNode(aziendaInfoDTO.getTipoDelegato().getId()));
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

	private Element getElementDatiAzienda(Document document, AziendaInfoDTO aziendaInfoDTO) {
		Element datiAzienda = document.createElement(EL_AZIENDA);
		Element el_cf = document.createElement(EL_AZIENDA_CF);
		el_cf.appendChild(document.createTextNode((aziendaInfoDTO.getCodiceFiscale()).toUpperCase()));
		Element el_pi = document.createElement(EL_AZIENDA_PI);
		el_pi.appendChild(document.createTextNode(aziendaInfoDTO.getPartitaIva()));
		Element el_ragsoc = document.createElement(EL_AZIENDA_RAG_SOC);
		el_ragsoc.appendChild(document.createTextNode(aziendaInfoDTO.getRagioneSociale()));
		Element el_referente = document.createElement(EL_AZIENDA_REF_SARE);
		el_referente.appendChild(document.createTextNode(aziendaInfoDTO.getReferenteSare()));
		Element el_telreferente = document.createElement(EL_AZIENDA_TEL_REF_SARE);
		el_telreferente.appendChild(document.createTextNode(aziendaInfoDTO.getTelefonoReferente()));
		Element el_emailreferente = document.createElement(EL_AZIENDA_EMAIL_REF_SARE);
		el_emailreferente.appendChild(document.createTextNode(aziendaInfoDTO.getEmailReferente()));
		datiAzienda.appendChild(el_cf);
		datiAzienda.appendChild(el_pi);
		datiAzienda.appendChild(el_ragsoc);
		datiAzienda.appendChild(el_referente);
		datiAzienda.appendChild(el_telreferente);
		datiAzienda.appendChild(el_emailreferente);

		return datiAzienda;
	}

	private Element getElementDatiSedeLegale(Document document, AziendaInfoDTO aziendaInfoDTO) {
		Element datiSedeLegale = document.createElement(EL_SEDE_LEGALE);
		Element el_indSedeLeg = document.createElement(EL_SEDE_LEGALE_IND);
		el_indSedeLeg.appendChild(document.createTextNode(aziendaInfoDTO.getSedeLegale().getIndirizzo()));
		Element el_capSedeLeg = document.createElement(EL_SEDE_LEGALE_CAP);
		el_capSedeLeg.appendChild(document.createTextNode(aziendaInfoDTO.getSedeLegale().getCap()));
		Element el_codComSedeLeg = document.createElement(EL_SEDE_LEGALE_COD_COM);
		el_codComSedeLeg.appendChild(document.createTextNode(aziendaInfoDTO.getSedeLegale().getComune().getId()));
		Element el_telSedeLeg = document.createElement(EL_SEDE_LEGALE_TEL);
		el_telSedeLeg.appendChild(document.createTextNode(aziendaInfoDTO.getSedeLegale().getTelefono()));
		Element el_faxSedeLeg = document.createElement(EL_SEDE_LEGALE_FAX);
		el_faxSedeLeg.appendChild(document.createTextNode(aziendaInfoDTO.getSedeLegale().getFax()));
		datiSedeLegale.appendChild(el_indSedeLeg);
		datiSedeLegale.appendChild(el_capSedeLeg);
		datiSedeLegale.appendChild(el_codComSedeLeg);
		datiSedeLegale.appendChild(el_telSedeLeg);
		datiSedeLegale.appendChild(el_faxSedeLeg);

		return datiSedeLegale;
	}

	private Element getElementDatiSedeOperativa(Document document, AziendaInfoDTO aziendaInfoDTO) {
		Element datiSedeOperativa = document.createElement(EL_SEDE_OPERATIVA);
		Element el_indSedeOp = document.createElement(EL_SEDE_OPERATIVA_IND);
		el_indSedeOp.appendChild(document.createTextNode(aziendaInfoDTO.getSedeOperativa().getIndirizzo()));
		Element el_capSedeOp = document.createElement(EL_SEDE_OPERATIVA_CAP);
		el_capSedeOp.appendChild(document.createTextNode(aziendaInfoDTO.getSedeOperativa().getCap()));
		Element el_codComSedeOp = document.createElement(EL_SEDE_OPERATIVA_COD_COM);
		el_codComSedeOp.appendChild(document.createTextNode(aziendaInfoDTO.getSedeOperativa().getComune().getId()));
		Element el_telSedeOp = document.createElement(EL_SEDE_OPERATIVA_TEL);
		el_telSedeOp.appendChild(document.createTextNode(aziendaInfoDTO.getSedeOperativa().getTelefono()));
		Element el_faxSedeOp = document.createElement(EL_SEDE_OPERATIVA_FAX);
		el_faxSedeOp.appendChild(document.createTextNode(aziendaInfoDTO.getSedeOperativa().getFax()));
		datiSedeOperativa.appendChild(el_indSedeOp);
		datiSedeOperativa.appendChild(el_capSedeOp);
		datiSedeOperativa.appendChild(el_codComSedeOp);
		datiSedeOperativa.appendChild(el_telSedeOp);
		datiSedeOperativa.appendChild(el_faxSedeOp);

		return datiSedeOperativa;
	}

	private Element getElementDatiAgenziaSomministrazione(Document document, AziendaInfoDTO aziendaInfoDTO) {
		Element datiAgenziaSomministrazione = document.createElement(EL_SOMMINISTRAZIONE);
		Element el_flgAgEst = document.createElement(EL_SOMMINISTRAZIONE_FLG_AG_ESTERA);
		String checkFlgAgEsterna = "0";
		AgenziaDTO agenzia = aziendaInfoDTO.getAgenzia();
		if (aziendaInfoDTO.getFlagAgenziaEstera()) {
			checkFlgAgEsterna = "1";
		}
		el_flgAgEst.appendChild(document.createTextNode(checkFlgAgEsterna));
		Element el_numProvv = document.createElement(EL_SOMMINISTRAZIONE_NUM_PROV);
		el_numProvv.appendChild(document.createTextNode(agenzia.getNumeroProvvedimento()));
		Element el_datProvv = document.createElement(EL_SOMMINISTRAZIONE_DAT_PROV);
		if (agenzia.getDataProvvedimento() == null) {
			el_datProvv.appendChild(document.createTextNode(null));
		} else {
			el_datProvv.appendChild(document.createTextNode(formatter1.format(agenzia.getDataProvvedimento())));
		}
		Element el_codComIscr = document.createElement(EL_SOMMINISTRAZIONE_COD_COM_ISCR);
		if (aziendaInfoDTO.getAgenzia().getComune() == null) {
			el_codComIscr.appendChild(document.createTextNode(null));
		} else {
			el_codComIscr.appendChild(document.createTextNode(aziendaInfoDTO.getAgenzia().getComune().getId()));
		}
		Element el_numIscrAlbo = document.createElement(EL_SOMMINISTRAZIONE_NUM_ISCR_ALBO);
		if (aziendaInfoDTO.getAgenzia().getNumeroIscrizione() == null) {
			el_numIscrAlbo.appendChild(document.createTextNode(null));
		} else {
			el_numIscrAlbo.appendChild(document.createTextNode(aziendaInfoDTO.getAgenzia().getNumeroIscrizione()));
		}
		Element el_datIscr = document.createElement(EL_SOMMINISTRAZIONE_DAT_ISCR);
		if (aziendaInfoDTO.getAgenzia().getDataIscrizione() == null) {
			el_datIscr.appendChild(document.createTextNode(null));
		} else {
			el_datIscr.appendChild(document.createTextNode(formatter1.format(aziendaInfoDTO.getAgenzia()
					.getDataIscrizione())));
		}
		datiAgenziaSomministrazione.appendChild(el_flgAgEst);
		datiAgenziaSomministrazione.appendChild(el_numProvv);
		datiAgenziaSomministrazione.appendChild(el_datProvv);
		datiAgenziaSomministrazione.appendChild(el_codComIscr);
		datiAgenziaSomministrazione.appendChild(el_numIscrAlbo);
		datiAgenziaSomministrazione.appendChild(el_datIscr);

		return datiAgenziaSomministrazione;
	}

	private Element getElementDatiSoggettoAbilitato(Document document, AziendaInfoDTO aziendaInfoDTO) {
		Element datiSoggettoAbilitato = document.createElement(EL_SOGG_ABILITATO);
		Element el_iscrOrd = document.createElement(EL_SOGG_ABILITATO_ISCR_ORD);
		SoggettoDTO soggAbil = aziendaInfoDTO.getSoggettoAbilitato();
		if (soggAbil == null) {
			el_iscrOrd.appendChild(document.createTextNode(null));
		} else {
			el_iscrOrd.appendChild(document.createTextNode(soggAbil.getOrdineIscrizione()));
		}
		Element el_codComIscrOrd = document.createElement(EL_SOGG_ABILITATO_COD_COM_ISCR_ORD);
		if (soggAbil.getLuogoIscrizione() == null) {
			el_codComIscrOrd.appendChild(document.createTextNode(null));
		} else {
			el_codComIscrOrd.appendChild(document.createTextNode(soggAbil.getLuogoIscrizione().getId()));
		}

		Element el_numIscrOrd = document.createElement(EL_SOGG_ABILITATO_NUM_ISCR_ORD);
		if (soggAbil == null || soggAbil.getNumeroIscrizione() == null) {
			el_numIscrOrd.appendChild(document.createTextNode(null));
		} else {
			el_numIscrOrd.appendChild(document.createTextNode(soggAbil.getNumeroIscrizione()));
		}
		Element elDatIscrOrd = document.createElement(EL_SOGG_ABILITATO_DAT_ISCR_ORD);
		if (soggAbil == null || soggAbil.getDataIscrizione() == null) {
			elDatIscrOrd.appendChild(document.createTextNode(null));
		} else {
			elDatIscrOrd.appendChild(document.createTextNode(formatter1.format(soggAbil.getDataIscrizione())));
		}

		datiSoggettoAbilitato.appendChild(el_iscrOrd);
		datiSoggettoAbilitato.appendChild(el_codComIscrOrd);
		datiSoggettoAbilitato.appendChild(el_numIscrOrd);
		datiSoggettoAbilitato.appendChild(elDatIscrOrd);

		return datiSoggettoAbilitato;
	}

	private String getEndPointSARE(String id) {
		String endPoint = null;
		endPoint = wsEndpointHome.getSareAddress(id);
		return endPoint;
	}

	private String[] getUserEndPointSARE(String id) {
		String user[] = null;
		user = wsEndpointHome.getWebServiceUser(TipoServizio.SARE, id);
		return user;
	}
}
