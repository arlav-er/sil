package it.eng.sil.coop.webservices.requisitiAdesione;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.sil.Values;

/**
 * WS per la verifica dei requisiti di garanzia Over
 * 
 * @author Giacomo Pandini
 *
 */
public class VerificaRequisitiGaranziaOver {

	static org.apache.log4j.Logger logger = org.apache.log4j.Logger
			.getLogger(VerificaRequisitiGaranziaOver.class.getName());

	private static final BigDecimal userSP = new BigDecimal("150");
	private static final String codBandoProgramma = new String("UMBAT");
	private static final String over30 = new String("OVER30");
	private static final String under30 = new String("UNDER30");

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Funzioni helper

	// Controlla che non ci sia già un'adesione per quel CFString
	private Boolean checkEsistenzaAdesione(String CFString) {
		Object[] params = new Object[] { CFString };
		SourceBean lSourceBean = (SourceBean) QueryExecutor.executeQuery("GET_ESISTENZA_ADESIONE_GARANZIAOVER", params,
				"SELECT", Values.DB_SIL_DATI);
		lSourceBean = (SourceBean) lSourceBean.getAttribute("ROW");
		if (lSourceBean != null && ((BigDecimal) lSourceBean.getAttribute("num_adesione")).intValue() > 0) {
			// Presente nel DB
			return true;
		} else
			return false;
	}

	// Controlla lavoratore presente, ottengo suo CDNLAVORATORE
	private BigDecimal checkLavoratore(String CFString) {
		Object[] params = new Object[] { CFString };
		SourceBean lSourceBean = (SourceBean) QueryExecutor.executeQuery("GET_LAVORATORE_CDN_GARANZIA_OVER", params,
				"SELECT", Values.DB_SIL_DATI);
		lSourceBean = (SourceBean) lSourceBean.getAttribute("ROW");
		if (lSourceBean != null) {
			// Presente nel DB
			return ((BigDecimal) lSourceBean.getAttribute("CDNLAVORATORE"));
		} else
			return null;
	}

	// Controlla CPI di competenza, ottengo le date di iscrizione o cessazione
	private String checkCPI(BigDecimal cdnLavoratore) {
		// cdnLavoratore = new BigDecimal(164252); //TOGLIERE TEST
		Object[] params = new Object[] { cdnLavoratore };
		SourceBean lSourceBean = (SourceBean) QueryExecutor.executeQuery("CHECK_CPI_COMPETENZA_GARANZIA_OVER", params,
				"SELECT", Values.DB_SIL_DATI);
		lSourceBean = (SourceBean) lSourceBean.getAttribute("ROW");
		if (lSourceBean != null) {
			// CPI in umbria
			return ((String) lSourceBean.getAttribute("STRDESCRIZIONE").toString());
		} else
			return null;
	}

	// Controlla presenza DID, ottengo la data di inizio della DID
	private java.util.Date checkDID(BigDecimal cdnLavoratore, SimpleDateFormat format) throws ParseException {
		// cdnLavoratore = new BigDecimal(105819); //TOGLIERE
		Object[] params = new Object[] { cdnLavoratore };
		SourceBean lSourceBean = (SourceBean) QueryExecutor.executeQuery("CHECK_DID_GARANZIA_OVER", params, "SELECT",
				Values.DB_SIL_DATI);
		lSourceBean = (SourceBean) lSourceBean.getAttribute("ROW");
		if (lSourceBean != null) {
			return format.parse((String) lSourceBean.getAttribute("DATDICHIARAZIONE").toString());
		} else
			return null;
	}

	// Controlla residenza in Umbria
	private boolean checkResidenza(BigDecimal cdnLavoratore) {
		// cdnLavoratore = new BigDecimal(60889); //TOGLIERE
		Object[] params = new Object[] { cdnLavoratore };
		SourceBean lSourceBean = (SourceBean) QueryExecutor.executeQuery("CHECK_RESIDENZA_UMBRIA_GARANZIA_OVER", params,
				"SELECT", Values.DB_SIL_DATI);
		lSourceBean = (SourceBean) lSourceBean.getAttribute("ROW");
		if (lSourceBean != null)
			return true;
		else
			return false;
	}

	// Controllo stato occupazionale, ritorno data inizio stato occupazionale
	private String checkStatoOccupazionale(java.util.Date dataAdesione, BigDecimal cdnLavoratore,
			SimpleDateFormat format) throws ParseException {
		// cdnLavoratore = new BigDecimal(436589); //TOGLIERE
		Object[] params = new Object[] { cdnLavoratore };
		SourceBean lSourceBean = (SourceBean) QueryExecutor.executeQuery("CHECK_STATO_OCCUPAZIONALE_GARANZIA_OVER",
				params, "SELECT", Values.DB_SIL_DATI);
		lSourceBean = (SourceBean) lSourceBean.getAttribute("ROW");
		if (lSourceBean != null) {

			return (String) lSourceBean.getAttribute("codstatooccupaz");
		} else {
			return null;
		}
	}

	// Controllo patto di servizio, ritorno data inizio stato patto di servizio
	private java.util.Date checkPattoServizio(java.util.Date dataAdesione, BigDecimal cdnLavoratore,
			SimpleDateFormat format) throws ParseException {
		// cdnLavoratore = new BigDecimal(441091); //TOGLIERE
		Object[] params = new Object[] { cdnLavoratore };
		SourceBean lSourceBean = (SourceBean) QueryExecutor.executeQuery("CHECK_PATTO_SERVIZIO_GARANZIA_OVER", params,
				"SELECT", Values.DB_SIL_DATI);
		lSourceBean = (SourceBean) lSourceBean.getAttribute("ROW");
		if (lSourceBean != null) {
			return format.parse((String) lSourceBean.getAttribute("DATSTIPULA").toString());
		} else {
			return null;
		}
	}

	// Controllo patto di servizio, ritorno data inizio stato patto di servizio
	private SourceBean checkPattoServizio(BigDecimal cdnLavoratore) throws ParseException {
		// cdnLavoratore = new BigDecimal(441091); //TOGLIERE
		Object[] params = new Object[] { cdnLavoratore };
		SourceBean lSourceBean = (SourceBean) QueryExecutor.executeQuery("CHECK_PATTO_SERVIZIO_GARANZIA_OVER", params,
				"SELECT", Values.DB_SIL_DATI);
		if (lSourceBean != null) {
			lSourceBean = (SourceBean) lSourceBean.getAttribute("ROW");
			return lSourceBean;
		} else {
			return null;
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Metodo WS

	public VerificaRequisitiGaranziaOverResponse getVerificaRequisitiGaranziaOver(
			VerificaRequisitiGaranziaOverRequest req) throws ParseException {

		java.util.Date dataInizioPatto = null;
		java.util.Date dataInizioDID = null;
		BigDecimal prgpattoLavoratore = null;
		BigDecimal numklopatto = null;
		String codStatoOcc = null;
		String lString = null;
		int diffMonth = -1;
		BigDecimal cdnLavoratore = null;
		String nomeCPI = null;
		boolean risiede = false;

		VerificaRequisitiGaranziaOverResponse res = new VerificaRequisitiGaranziaOverResponse();
		String CFString = req.getCodiceFiscale();
		String PAString = req.getPercettoreAmmortizzatori();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calDataAdesione = req.getDataAdesione();
		java.util.Date dataAdesione = new java.util.Date(calDataAdesione.getTimeInMillis());
		String dichiarazione = req.getDichiarazione();
		SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
		String da = formatDate.format(dataAdesione);

		try {
			/////////////////////////////////////// Controllo valori in ingresso
			// Controllo presenza valori
			if ((CFString == null) || (req.getDataAdesione() == null) || (PAString == null) || (dichiarazione == null))
				throw new NullPointerException();
			// Controllo formato Percettore Ammortizzatori
			if (!(PAString.equals("S") || PAString.equals("N")))
				throw new IllegalArgumentException();
			// Controllo formato Codice Fiscale
			if (!it.eng.sil.util.xml.XMLValidator.codFiscRegEx.matcher(CFString).matches())
				throw new IllegalArgumentException();

			// Controllo esistenza Adesione
			if (checkEsistenzaAdesione(CFString)) {
				lString = "Non puoi aderire perche' hai gia' aderito al programma Umbriattiva Tirocini";
				logger.error(lString);
				res.setCodice((byte) 17);
				res.setDescrizione(lString);
				return res;
			}

			// se il tipo dichiarazione e' uguale a OVER30
			if ((dichiarazione != null && dichiarazione.equalsIgnoreCase(over30))) {
				///////////////////////////////////// Controlli del metodo
				// Controlla esistenza lavoratore
				cdnLavoratore = checkLavoratore(CFString);
				if (cdnLavoratore == null) {
					// Lavoratore non presente nel DB
					lString = "Il lavoratore non risulta iscritto a nessuno dei CpI della Regione";
					logger.error(lString);
					res.setCodice((byte) 12);
					res.setDescrizione(lString);
					return res;
				}
				// Controlla compentezna in Umbria
				nomeCPI = checkCPI(cdnLavoratore);
				if (nomeCPI == null) {
					// CPI di competenza non in Umbria o non esistente
					lString = "Il lavoratore non risulta essere di competenza di nessuno dei CpI della Regione";
					logger.error(lString);
					res.setCodice((byte) 13);
					res.setDescrizione(lString);
					return res;
				}
				// Controlla esistenza DID valida
				dataInizioDID = checkDID(cdnLavoratore, format);
				if (dataInizioDID != null) {
					// Calcola differenza tra la data ricevuta in input e quella trovata sul DB
					Calendar inizioCalendar = new GregorianCalendar();
					inizioCalendar.setTime(dataInizioDID);
					Calendar adesioneCalendar = new GregorianCalendar();
					adesioneCalendar.setTime(dataAdesione);

					diffMonth = DateUtils.monthsBetween(inizioCalendar, adesioneCalendar);
				}
				// Controlla residenza in Umbria
				if (checkResidenza(cdnLavoratore)) {
					// Risiede
					risiede = true;
					if (PAString.equals("S")) {
						// Riceve ammortizzatori
						if (diffMonth < 4) { // DID non valida o durata non congrua
							lString = "Il lavoratore risiede sul territorio della Regione Umbria, e' percettore di ammortizzatori ma non risulta avere una DID valida da almeno 4 mesi.";
							logger.error(lString);
							res.setCodice((byte) 16);
							res.setDescrizione(lString);
							return res;
						}
					} else {
						// Non riceve ammortizzatori
						if (diffMonth < 6) { // DID non valida o durata non congrua
							lString = "Il lavoratore risiede sul territorio della Regione Umbria, non e' percettore di ammortizzatori e non risulta avere una DID valida da almeno 6 mesi.";
							logger.error(lString);
							res.setCodice((byte) 15);
							res.setDescrizione(lString);
							return res;
						}
					}
				} else {
					// Non risiede
					if (diffMonth < 12) { // DID non valida o durata non congrua
						lString = "Il lavoratore non risiede sul territorio della Regione Umbria e non risulta avere una DID valida da almeno 12 mesi. ";
						res.setCodice((byte) 14);
						logger.error(lString);
						res.setDescrizione(lString);
						return res;
					}
				}
				// Controlla stato occupazionale
				codStatoOcc = checkStatoOccupazionale(dataAdesione, cdnLavoratore, format);
				if (codStatoOcc == null) {
					// Stato occupazionale non disoccupato o inoccupato
					lString = "Il lavoratore non si trova nella stato occupazionale Disoccupato/Inoccupato.";
					logger.error(lString);
					res.setCodice((byte) 11);
					res.setDescrizione(lString);
					return res;
				}
				// Controlla patto servizio
				SourceBean patto = checkPattoServizio(cdnLavoratore);
				if (patto != null) {
					dataInizioPatto = format.parse((String) patto.getAttribute("DATSTIPULA").toString());
					prgpattoLavoratore = (BigDecimal) patto.getAttribute("prgpattolavoratore");
					numklopatto = (BigDecimal) patto.getAttribute("numklopattolavoratore");
					numklopatto = numklopatto.add(new BigDecimal(1));
				}
				if (dataInizioPatto == null) {
					// Non esiste nessun patto per l'utente
					lString = "Non esiste un patto 150 valido.";
					logger.error(lString);
					res.setCodice((byte) 10);
					res.setDescrizione(lString);
					return res;
				}

				// Controlli superati
				format.applyPattern("dd-MM-yyyy");
				if (risiede)
					lString = "Lavoratore risiede sul territorio della Regione e risulta iscritto al Cpi di " + nomeCPI
							+ ", con DID stipulata in data " + format.format(dataInizioDID)
							+ ", con Patto stipulato in data " + format.format(dataInizioPatto)
							+ ". Stato occupazionale " + codStatoOcc;
				else
					lString = "Lavoratore non risiede sul territorio della Regione e risulta iscritto al Cpi di "
							+ nomeCPI + ", con DID stipulata in data " + format.format(dataInizioDID)
							+ ", con Patto stipulato in data " + format.format(dataInizioPatto)
							+ ". Stato occupazionale " + codStatoOcc;
				logger.debug(lString);
				res.setCodice((byte) 00);
				res.setDescrizione(lString);
				if (prgpattoLavoratore != null) {

					// Object []params = new Object[4];
					// params[0] = da;
					// params[1] = userSP;
					// params[2] = numklopatto;
					// params[3] = prgpattoLavoratore;
					// Boolean risultato =
					// (Boolean)QueryExecutor.executeQuery("UPDATE_PATTO_DATAADESIONE_GARANZIA_OVER", params, "UPDATE",
					// Values.DB_SIL_DATI);

					Object[] paramsInsert = new Object[7];
					paramsInsert[0] = CFString;
					paramsInsert[1] = cdnLavoratore;
					paramsInsert[2] = dichiarazione;
					paramsInsert[3] = da;
					paramsInsert[4] = codBandoProgramma;
					paramsInsert[5] = userSP;
					paramsInsert[6] = userSP;
					Boolean risultatoInsert = (Boolean) QueryExecutor.executeQuery("INSERT_BD_ADESIONE_GARANZIA_OVER",
							paramsInsert, "INSERT", Values.DB_SIL_DATI);
				}
			}
			// caso UNDER30
			else if (dichiarazione.equalsIgnoreCase(under30)) {
				Object[] paramsInsert = new Object[7];
				paramsInsert[0] = CFString;
				paramsInsert[1] = cdnLavoratore;
				paramsInsert[2] = dichiarazione;
				paramsInsert[3] = da;
				paramsInsert[4] = codBandoProgramma;
				paramsInsert[5] = userSP;
				paramsInsert[6] = userSP;
				Boolean risultatoInsert = (Boolean) QueryExecutor.executeQuery("INSERT_BD_ADESIONE_GARANZIA_OVER",
						paramsInsert, "INSERT", Values.DB_SIL_DATI);
				lString = "Adesione salvata correttamente";
				logger.debug(lString);
				res.setCodice((byte) 00);
				res.setDescrizione(lString);

			}

		} catch (NullPointerException e) {
			lString = "Errore generico";
			logger.error(lString);
			res.setCodice((byte) 99);
			res.setDescrizione(lString);
		} catch (IllegalArgumentException e) {
			lString = "Errore di validazione XSD";
			logger.error(lString);
			res.setCodice((byte) 02);
			res.setDescrizione(lString);
		} catch (java.text.ParseException e) {
			lString = "Errore generico";
			logger.error(lString);
			res.setCodice((byte) 99);
			res.setDescrizione(lString);
		}
		return res;
	}
}
