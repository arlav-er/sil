/*
 * Created on May 31, 2007
 */
package it.eng.sil.coordinamento.servizi;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFErrorHandler;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.bean.Documento;
import it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil;
import it.eng.sil.coordinamento.wsClient.np.Execute;
import it.eng.sil.coordinamento.wsClient.np.RispostaXML;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.security.User;
import it.eng.sil.util.StatementUtils;
import it.eng.sil.util.Utils;

/**
 * @author savino
 */
public class CambioRagioneSociale implements ServizioSoap {

	private static Logger log = Logger.getLogger(CambioRagioneSociale.class);
	private RequestContainer requestContainer;

	/**
	 * Esegue il cambio ragione sociale richiesto dal nodo di coordinamento regionale a seguito di una comunicazione
	 * obbligatoria proveniente dal sare o dalla porta di dominio.
	 */
	public String elabora(Execute parametri) {

		String datiXML = parametri.getDati();

		boolean protocollaDocumento = true;
		String risultato = null;
		RispostaXML rispostaXML = null;
		TransactionQueryExecutor tex = null;

		log.debug("servizio cambio ragione sociale chiamato. " + parametri.getDati());

		try {
			SourceBean dati = SourceBean.fromXMLString(datiXML);
			String ragioneSocialePrec = (String) dati.getAttribute("VariazioneRagioneSociale.DenominazionePrecedente");
			//
			String dataInvio = null;
			dataInvio = (String) dati.getAttribute("VariazioneRagioneSociale.DataVariazione");
			if (dataInvio == null || dataInvio.equals("")) {
				dataInvio = (String) dati.getAttribute("dataInvio");
			}
			String ragioneSocialeNuova = (String) dati.getAttribute("DatoreAttuale.denominazione");
			String strCodiceFiscale = (String) dati.getAttribute("DatoreAttuale.codiceFiscale");
			String codiceComunicazione = (String) dati.getAttribute("codiceComunicazione");
			log.info("SILERService: comunicazione obbligatoria: codice comunicazione=" + codiceComunicazione);

			// ---------- log richiesta
			StringBuffer msg = new StringBuffer(100);
			msg.append("servizio cambio ragione sociale da NCR: ");
			msg.append("ragioneSocialePrec=");
			msg.append(ragioneSocialePrec);
			msg.append("strCodiceFiscale=");
			msg.append(strCodiceFiscale);
			msg.append("ragioneSocialeNuova=");
			msg.append(ragioneSocialeNuova);
			msg.append("dataInvio=");
			msg.append(dataInvio);
			if (log.isEnabledFor(Level.DEBUG))
				log.debug(msg.toString());
			// ---------- log richiesta

			// la data e' nel formato aaaa-mm-dd
			// bisogna trasformarla nel nostro formato dd/mm/yyyy
			dataInvio = dataInvio.substring(8, 10) + "/" + dataInvio.substring(5, 7) + "/" + dataInvio.substring(0, 4);

			// -- INIZIO TRANSAZIONE
			tex = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			tex.initTransaction();

			// Controllo esistenza azienda e recupero progressivo e numero di lock
			SourceBean row = (SourceBean) tex.executeQuery("GET_PRG_AZIENDA", new Object[] { strCodiceFiscale },
					"SELECT");
			BigDecimal prgAzienda = (BigDecimal) row.getAttribute("row.prgAzienda");
			BigDecimal numKLoAzienda = (BigDecimal) row.getAttribute("row.numKLoAzienda");

			BigDecimal prgUnita = null;
			String motivo = null;

			if (prgAzienda != null) {
				// L'AZIENDA ESISTE: LA AGGIORNIAMO
				aggiornaRagioneSociale(prgAzienda, dataInvio, ragioneSocialeNuova, ragioneSocialePrec, numKLoAzienda,
						tex);
				prgUnita = cercaPrgUnita(prgAzienda, tex);
				motivo = null;
				rispostaXML = new RispostaXML("101", "Cambio ragione sociale avvenuto correttamente.", "I");
			} else {
				// L'AZIENDA NON ESISTE: LA INSERIAMO (solo se la sede è nella provincia o regione a seconda della
				// configurazione del polo)
				UtilsConfig utility = new UtilsConfig("MOV_REG");
				String configRegProv = utility.getConfigurazioneDefault_Custom();
				// configurazione provinciale o regionale
				if (configRegProv.equals(Properties.DEFAULT_CONFIG)) {
					if (sedeFuoriProvincia(dati, tex)) {
						// comunicazione di una azienda che non e' della provincia. (Proveniente da una comunicazione in
						// broadcasting). La scartiamo.
						rispostaXML = new RispostaXML("101",
								"Sede azienda esterna alla provincia del SIL. Comunicazione scartata.", "I");
						tex.commitTransaction();
						risultato = rispostaXML.toXMLString();
						protocollaDocumento = false;
					} else {
						prgAzienda = getNextPrgAzienda(tex);
						inserisciAzienda(tex, prgAzienda, strCodiceFiscale, ragioneSocialeNuova, ragioneSocialePrec);
						prgUnita = getNextPrgUnita(tex);
						inserisciUnita(tex, prgAzienda, prgUnita, dati);
						motivo = "L'azienda e' stata inserita non essendo presente in archivio al momento della ricezione della comunicazione.";

						rispostaXML = new RispostaXML("101", "Azienda non trovata. E' stata aggiunta al db.", "I");
					}
				} else {
					if (sedeFuoriRegione(dati, tex)) {
						// comunicazione di una azienda che non e' della regione. La scartiamo.
						rispostaXML = new RispostaXML("101",
								"Sede azienda esterna alla regione del SIL. Comunicazione scartata.", "I");
						tex.commitTransaction();
						risultato = rispostaXML.toXMLString();
						protocollaDocumento = false;
					} else {
						prgAzienda = getNextPrgAzienda(tex);
						inserisciAzienda(tex, prgAzienda, strCodiceFiscale, ragioneSocialeNuova, ragioneSocialePrec);
						prgUnita = getNextPrgUnita(tex);
						inserisciUnita(tex, prgAzienda, prgUnita, dati);
						motivo = "L'azienda e' stata inserita non essendo presente in archivio al momento della ricezione della comunicazione.";

						rispostaXML = new RispostaXML("101", "Azienda non trovata. E' stata aggiunta al db.", "I");
					}
				}
			}
			// ORA PROTOCOLLIAMO IL DOCUMENTO SE LA RAGIONE SOCIALE E' STATA AGGIORNATA O INSERITA NEL DB
			if (protocollaDocumento) {
				Documento doc = protocollaDocumento(prgAzienda, prgUnita, strCodiceFiscale, dataInvio,
						ragioneSocialeNuova, ragioneSocialePrec, motivo, codiceComunicazione, tex);
				if (doc != null) {
					// -- COMMIT TRANSAZIONE
					tex.commitTransaction();
				} else {
					tex.rollBackTransaction();
				}
				ProtocolloDocumentoUtil.cancellaFileDocarea(doc);
			}

			risultato = rispostaXML.toXMLString();

		} catch (Exception e) {
			log.debug("Impossibile eseguire il cambio ragione sociale da NCR. " + parametri.getDati(), e);

			if (tex != null)
				try {
					// -- ROLLBACK TRANSAZIONE
					tex.rollBackTransaction();
				} catch (Exception e1) {
					log.debug("Impossibile eseguire ROLLBACK sul cambio ragione sociale. " + parametri.getDati(), e1);
				}

			rispostaXML = new RispostaXML("999", e.getMessage(), "E");
			risultato = rispostaXML.toXMLString();
		}
		return risultato;
	}

	private boolean sedeFuoriProvincia(SourceBean dati, TransactionQueryExecutor tex) throws EMFInternalError {
		String codComSede = (String) dati.getCharacters("DatoreAttuale.Sede.Comune");
		SourceBean row = (SourceBean) tex.executeQuery("COOP_COMUNE_IN_PROVINCIA_SIL", new String[] { codComSede },
				"SELECT");
		String inProvincia = (String) row.getAttribute("row.in_provincia");
		return "N".equalsIgnoreCase(inProvincia);
	}

	private boolean sedeFuoriRegione(SourceBean dati, TransactionQueryExecutor tex) throws EMFInternalError {
		String codComSede = (String) dati.getCharacters("DatoreAttuale.Sede.Comune");
		SourceBean row = (SourceBean) tex.executeQuery("COOP_COMUNE_IN_REGIONE_SIL", new String[] { codComSede },
				"SELECT");
		String inRegione = (String) row.getAttribute("row.in_regione");
		return "N".equalsIgnoreCase(inRegione);
	}

	/**
	 */
	private Documento protocollaDocumento(BigDecimal prgAzienda, BigDecimal prgUnita, String cfAzienda,
			String dataInvio, String ragioneSocialeNuova, String ragioneSocialePrec, String motivo,
			String codiceComunicazione, TransactionQueryExecutor tex) throws Exception {
		Documento doc = null;
		try {
			preparaContesto();
			doc = new Documento();
			String dataOdierna = DateUtils.getNow();
			SimpleDateFormat fd = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			String dataEOraProtocollo = fd.format(new Date());
			String annoProtocollo = dataOdierna.substring(6, 10);

			// NOTA BENE: o entrambi valorizzati o entrambi null
			if (prgUnita == null)
				prgAzienda = null;
			// ----

			String utenteCoordinamento = "190";
			StringBuffer strNote = new StringBuffer(100);
			if (motivo != null) {
				strNote.append(motivo);
				strNote.append("\n");
			}
			strNote.append("Nuova rag. soc.: ");
			strNote.append(ragioneSocialeNuova.toUpperCase());
			strNote.append("\n");
			strNote.append("Vecchia rag. soc.: ");
			strNote.append(ragioneSocialePrec.toLowerCase());
			strNote.append("\n");
			strNote.append("Cod. fiscale az.: ");
			strNote.append(cfAzienda);
			strNote.append("\n");
			strNote.append("Cod. comunicazione: ");
			strNote.append(codiceComunicazione);

			// valori specifici per il cambio ragione sociale
			doc.setPrgAzienda(prgAzienda);
			doc.setPrgUnita(prgUnita);
			doc.setCodTipoDocumento("MVRS");
			doc.setStrDescrizione("CAMBIO RAGIONE SOCIALE");
			doc.setStrNote(strNote.toString());
			doc.setCodMonoIO("I");
			doc.setDatAcqril(dataOdierna);
			doc.setDatInizio(dataOdierna);
			// valori generici necessari per la protocollazione
			doc.setDatProtocollazione(dataEOraProtocollo);
			doc.setNumAnnoProt(new BigDecimal(annoProtocollo));
			doc.setNumProtocollo(new BigDecimal(0));
			doc.setTipoProt("S");
			doc.setCodStatoAtto("PR");
			doc.setCdnUtIns(new BigDecimal(utenteCoordinamento));
			doc.setCdnUtMod(new BigDecimal(utenteCoordinamento));

			doc.insert(tex);
			rimuoviContesto();
			return doc;

		} catch (Exception e) {
			StringBuffer msg = new StringBuffer(100);
			msg.append("protocollaDocumento() fallito. Servizio il cambio ragione sociale proveniente dallo NCR. ");
			msg.append("prgAzienda=");
			msg.append(prgAzienda);
			msg.append(", prgUnita=");
			msg.append(prgUnita);
			msg.append(", Codice Fiscale Azienda=");
			msg.append(cfAzienda);
			msg.append(", dataInvio=");
			msg.append(dataInvio);
			msg.append(", Ragione Sociale Nuova=");
			msg.append(ragioneSocialeNuova);
			msg.append(", Ragione Sociale Precedente=");
			msg.append(ragioneSocialePrec);

			if (log.isEnabledFor(Level.DEBUG))
				log.debug(msg.toString(), e);
			rimuoviContesto();
			throw e;
		}
	}

	private void preparaContesto() {
		try {
			requestContainer = new RequestContainer();
			ResponseContainer respCont = new ResponseContainer();

			RequestContainer.setRequestContainer(requestContainer);

			SourceBean serviceRequest = new SourceBean("SERVICE_REQUEST");
			SourceBean serviceResponse = new SourceBean("SERVICE_RESPONSE");

			SessionContainer sessionCont = new SessionContainer(false);
			requestContainer.setServiceRequest(serviceRequest);
			requestContainer.setSessionContainer(sessionCont);
			respCont.setErrorHandler(new EMFErrorHandler());
			respCont.setServiceResponse(serviceResponse);

			BigDecimal codUtente = new BigDecimal(190);
			sessionCont.setAttribute(Values.CODUTENTE, codUtente);

			SourceBean sb = StatementUtils.getSourceBeanByStatement("GET_UTENTE_INFO", codUtente.toString());
			String username = Utils.notNull(sb.getAttribute("ROW.STRLOGIN"));
			String nome = Utils.notNull(sb.getAttribute("ROW.STRNOME"));
			String cognome = Utils.notNull(sb.getAttribute("ROW.STRCOGNOME"));

			User user = new User(codUtente.intValue(), username, nome, cognome);
			sessionCont.setAttribute(User.USERID, user);

			user.setCdnProfilo(4); // Per i permessi sul lavoratore o sull'azienda // TODO
			SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_CODCPICAPOLUOGO", null, "SELECT",
					Values.DB_SIL_DATI);
			String codCpiCapoluogo = (String) row.getAttribute("ROW.RESULT");
			user.setCodRif(codCpiCapoluogo); // Per il calcolo del CPI di competenza
		} catch (Exception e) {
			log.error("impossibile creare il contesto framework del servizio per la protocollazione del documento", e);

		}
	}

	private void rimuoviContesto() {

		if (this.requestContainer != null)
			this.requestContainer.delRequestContainer();
	}

	/**
	 * Cerca e ritorna il prgUnita dell'azienda. Se non esiste ritorna null.
	 * 
	 * @param prgAzienda
	 * @param tex
	 * @return
	 */
	private BigDecimal cercaPrgUnita(BigDecimal prgAzienda, TransactionQueryExecutor tex) throws EMFInternalError {
		String query = "select min(prgUnita) as prgUnita from an_unita_azienda where prgazienda = ?";
		SourceBean row = (SourceBean) tex.executeQueryByStringStatement(query, new Object[] { prgAzienda }, "SELECT");
		BigDecimal prgUnita = (BigDecimal) row.getAttribute("row.prgUnita");
		return prgUnita;
	}

	private void aggiornaRagioneSociale(BigDecimal prgAzienda, String dataInvio, String ragioneSocialeNuova,
			String ragioneSocialePrec, BigDecimal numKlo, TransactionQueryExecutor tex) throws EMFInternalError {
		try {
			String dataComunicazione = dataInvio;
			String utenteCoordinamento = "190";
			String storiaRagioneSociale = "OGGETTO `CAMBIO RAGIONE SOCIALE` " + ragioneSocialePrec + " (comunicata il "
					+ dataComunicazione + ")";
			Object params[] = new Object[] { ragioneSocialeNuova, storiaRagioneSociale, utenteCoordinamento,
					numKlo.add(new BigDecimal(1)), dataInvio, prgAzienda };

			tex.executeQuery("UPDATE_RAGIONE_SOCIALE_COMUNICAZIONE_OBBLIGATORIA", params, "UPDATE");

		} catch (EMFInternalError e) {
			StringBuffer msg = new StringBuffer(100);
			msg.append("aggiornaRagioneSociale() fallito. Servizio il cambio ragione sociale proveniente dallo NCR. ");
			msg.append(" prgAzienda = ");
			msg.append(prgAzienda);
			msg.append(", dataInvio = ");
			msg.append(dataInvio);
			msg.append(", ragione Sociale Nuova = ");
			msg.append(ragioneSocialeNuova);
			msg.append(", ragione Sociale Precedente = ");
			msg.append(ragioneSocialePrec);
			msg.append(", numKlockAzienda = ");
			msg.append(numKlo);

			log.debug(msg.toString(), (Exception) e);
			throw e;
		}
	}

	/**
	 * Ritorna il nuovo prgUnita (sequence) o viene lanciata una eccezione
	 */
	private BigDecimal getNextPrgUnita(TransactionQueryExecutor tex) throws Exception {
		String query = "select s_an_unita_azienda.nextval from dual";
		SourceBean row = (SourceBean) tex.executeQueryByStringStatement(query, new Object[] {}, "SELECT");
		BigDecimal nextval = (BigDecimal) row.getAttribute("row.nextval");

		if (nextval == null)
			throw new Exception("Impossibile calcolare la sequence per la tabella an_unita_azienda");
		return nextval;
	}

	/**
	 * Ritorna il nuovo prgAzienda (sequence) o viene lanciata una eccezione
	 * 
	 * @param tex
	 * @return
	 * @throws Exception
	 */
	private BigDecimal getNextPrgAzienda(TransactionQueryExecutor tex) throws Exception {

		String query = "select s_an_azienda.nextval from dual";
		SourceBean row = (SourceBean) tex.executeQueryByStringStatement(query, new Object[] {}, "SELECT");
		BigDecimal nextval = (BigDecimal) row.getAttribute("row.nextval");

		if (nextval == null)
			throw new Exception("Impossibile calcolare la sequence per la tabella an_azienda");
		return nextval;

	}

	private void inserisciAzienda(TransactionQueryExecutor tex, BigDecimal prgAzienda, String strCodiceFiscale,
			String ragioneSocialeNuova, String ragioneSocialePrec) throws Exception {
		// 1. controllo codice fiscale
		// CF_utils.verificaCFAzienda(strCodiceFiscale);
		// 2. la partita iva non c'e'. Non va controllata

		try {

			Object pAzienda[] = new Object[25];
			pAzienda[0] = prgAzienda;
			pAzienda[1] = strCodiceFiscale;
			pAzienda[2] = null;// partita iva che nella comunicazione vardatori non c'e'
			pAzienda[3] = ragioneSocialeNuova;
			// codNatGiuridica
			pAzienda[5] = "NT"; // codTipoAzienda
			// strSitoInternet
			// strDescAttivita
			// numSoci
			// numDipendenti
			// numCollaboratori
			// numAltraPosizione
			// datInizio
			// datFine
			// DATAGGINFORMAZIONE
			pAzienda[15] = "OGGETTO `CAMBIO RAGIONE SOCIALE` " + ragioneSocialePrec + " (comunicata il "
					+ DateUtils.getNow() + ")"; // strHistory
			pAzienda[16] = "Azienda inserita a seguito di comunicazione obbligatoria di cambio di ragione sociale (azienda non presente nel DB)"; // strNote
			pAzienda[17] = "190"; // cdnUtins
			pAzienda[18] = "190"; // cdnUtmod
			// flgdatiok
			// strNumAlboInterinali
			// strRepartoInail
			// strPatInail
			// flgObbligoL68
			// strNumAgSomministrazione

			tex.executeQuery("INSERT_TESTATA_AZIENDA", pAzienda, "INSERT");
		} catch (Exception e) {
			StringBuffer msg = new StringBuffer(100);
			msg.append("inserisciAzienda() fallito. Servizio il cambio ragione sociale proveniente dallo NCR. ");
			msg.append(" prgAzienda = ");
			msg.append(prgAzienda);
			msg.append(", strCodiceFiscale = ");
			msg.append(strCodiceFiscale);
			msg.append(", ragioneSocialeNuova = ");
			msg.append(ragioneSocialeNuova);
			msg.append(", ragioneSocialePrec = ");
			msg.append(ragioneSocialePrec);

			log.debug(msg.toString(), (Exception) e);
			throw e;
		}
	}

	private void inserisciUnita(TransactionQueryExecutor tex, BigDecimal prgAzienda, BigDecimal prgUnita,
			SourceBean dati) throws Exception {
		try {
			String strIndirizzoSede, codComSede, strFaxSede, strEmailSede, strTelefonoSede, strCodAteco;
			strIndirizzoSede = (String) dati.getCharacters("DatoreAttuale.Sede.Indirizzo");
			codComSede = (String) dati.getCharacters("DatoreAttuale.Sede.Comune");
			strFaxSede = (String) dati.getCharacters("DatoreAttuale.Sede.Fax");
			strEmailSede = (String) dati.getCharacters("DatoreAttuale.Sede.e-mail");
			strTelefonoSede = (String) dati.getCharacters("DatoreAttuale.Sede.Telefono");
			strCodAteco = (String) dati.getCharacters("DatoreAttuale.Settore");
			if (strCodAteco == null) {
				strCodAteco = "NT";
			}
			Object pUnita[] = new Object[29];

			pUnita[0] = prgAzienda;
			pUnita[1] = prgUnita;
			pUnita[2] = strIndirizzoSede; // strIndirizzo
			pUnita[3] = "S"; // flgSede
			pUnita[4] = null; // strRea
			pUnita[5] = null; // strLocalita
			pUnita[6] = codComSede;
			pUnita[7] = null; // strCap
			pUnita[8] = null; // flgMezziPub
			pUnita[9] = "1"; // codAzStato
			pUnita[10] = null; // strResponsabile
			pUnita[11] = null; // strReferente
			pUnita[12] = strTelefonoSede; // strTel
			pUnita[13] = strFaxSede;
			pUnita[14] = strEmailSede;
			pUnita[15] = strCodAteco; // codAteco
			pUnita[16] = "NT"; // codCCNL
			pUnita[17] = null; // datInizio
			pUnita[18] = null; // datFine
			pUnita[19] = "Sede inserita a seguito di comunicazione obbligatoria di cambio di ragione sociale (sede non presente nel DB)"; // strNote,
			pUnita[20] = "190"; // cdnUtins
			pUnita[21] = "190"; // cdnUtmod
			pUnita[22] = null; // strnumeroinps,
			pUnita[23] = null; // strNumRegistroCommitt,
			pUnita[24] = null; // DATREGISTROCOMMIT,
			pUnita[25] = null; // STRRIFERIMENTOSARE,
			pUnita[26] = null; // STRREPARTOINPS,
			pUnita[27] = null; // STRDENOMINAZIONE
			pUnita[28] = null; // TODO: "strPECemail"

			tex.executeQuery("INSERT_UNITA_AZIENDA", pUnita, "INSERT");

		} catch (Exception e) {
			StringBuffer msg = new StringBuffer(100);
			msg.append("inserisciUnita() fallito. Servizio il cambio ragione sociale proveniente dallo NCR. ");
			msg.append(" prgAzienda = ");
			msg.append(prgAzienda);
			msg.append(", prgUnita = ");
			msg.append(prgUnita);
			msg.append(", dati = ");
			msg.append(dati.toString());

			log.debug(msg.toString(), (Exception) e);
			throw e;
		}
	}
}
