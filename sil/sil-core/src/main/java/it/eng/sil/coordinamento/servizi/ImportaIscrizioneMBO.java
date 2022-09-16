package it.eng.sil.coordinamento.servizi;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.dbaccess.sql.DBKeyGenerator;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.cig.bean.ImportaIscrizioneMBOBean;
import it.eng.sil.coordinamento.wsClient.np.Execute;
import it.eng.sil.coordinamento.wsClient.np.RispostaXML;
import it.eng.sil.module.mobilita.ValidazioneXMLMbo;
import it.eng.sil.util.InfoProvinciaSingleton;
import it.eng.sil.util.InfoRegioneSingleton;
import it.eng.sil.util.xml.XMLValidator;

/**
 * Servizio di ricezione di un'iscrizione MBO. L'XML arriva da NCR, viene processato e viene avviata la validazione
 * automatica.
 * 
 * @author manuel
 */
public class ImportaIscrizioneMBO implements ServizioSoap {

	private static Logger log = org.apache.log4j.Logger.getLogger(ImportaIscrizioneMBO.class.getName());

	public String elabora(Execute parametri) {
		String risultato = null;

		log.info("[IMPORTAZIONE ISCR MBO] Il servizio di importazione iscrizione MBO e' stato invocato");

		ImportaIscrizioneMBOBean importaIscrMBOBean = new ImportaIscrizioneMBOBean();

		TransactionQueryExecutor transEx = null;

		BigDecimal prgAmMobIscrApp = null;

		boolean canValidate = false;

		try {

			transEx = new TransactionQueryExecutor(Values.DB_SIL_DATI);

			transEx.initTransaction();

			// verifico se posso lanciare la validazione automatica
			log.info("[IMPORTAZIONE ISCR MBO] Verifica se e' possibile lanciare la validazione automatica");
			String codProvinciaLocale = InfoProvinciaSingleton.getInstance().getCodice();
			String codRegioneLocale = InfoRegioneSingleton.getInstance().getCodice();
			Object[] params = new Object[1];
			params[0] = codProvinciaLocale;
			SourceBean autValSb = (SourceBean) transEx.executeQuery("GET_MB_AUT_VAL", params, "SELECT");
			BigDecimal mbAutVal = (BigDecimal) autValSb.getAttribute("ROW.MBAUTVAL");

			if (mbAutVal.intValue() == 1)
				canValidate = true;

			// PARSE DELL'XML ARRIVATO OTTENENDO UN OGGETTO it.eng.sil.cig.bean.ImportaIscrizioneMBOBean
			log.info("[IMPORTAZIONE ISCR MBO] Parse dell'XML arrivato da NCR");

			Document document = XMLValidator.parseXmlFile(parametri.getDati());
			importaIscrMBOBean = new ImportaIscrizioneMBOBean(document.getDocumentElement());

			importaIscrMBOBean.parseIscrizione();

			// IL LAVORATORE E' DI COMPETENZA DI QUESTA PROVINCIA?
			log.info("[IMPORTAZIONE ISCR MBO] Verifica che il lavoratore sia di competenza di questa provincia");
			params = new Object[1];
			params[0] = importaIscrMBOBean.getCodiceFiscaleLav();
			SourceBean existLav = (SourceBean) transEx.executeQuery("MB_IS_DOM_ANAG_IN_PROV", params, "SELECT");
			String isLavCompetente = (String) existLav.getAttribute("ROW.FLAG");

			if ("S".equalsIgnoreCase(isLavCompetente)) {

				log.info(
						"[IMPORTAZIONE ISCR MBO] Il lavoratore e' competente di questa provincia: inserisco in AM_MOBILITA_ISCR_APP");
				// IMPORTAZIONE ISCRIZIONE: INSERIMENTO NELLA TABELLA DI APPOGGIO AM_MOBILITA_ISCR_APP
				prgAmMobIscrApp = popolaAppoggio(importaIscrMBOBean, transEx);

			} else {

				// IL COMUNE DI DOMICILIO E' NELLA PROVINCIA?
				log.info("[IMPORTAZIONE ISCR MBO] Verifica che il comune di domicilio sia nella provincia");
				params = new Object[2];
				params[0] = importaIscrMBOBean.getComuneDom();
				params[1] = codProvinciaLocale;
				SourceBean existCom = (SourceBean) transEx.executeQuery("CHECK_COMUNE_IN_PROV", params, "SELECT");
				BigDecimal countCom = (BigDecimal) existCom.getAttribute("ROW.COUNTCOM");

				if (countCom.intValue() > 0) {

					log.info(
							"[IMPORTAZIONE ISCR MBO] Il comune di domicilio e' nella provincia: inserisco in AM_MOBILITA_ISCR_APP");
					// IMPORTAZIONE ISCRIZIONE: INSERIMENTO NELLA TABELLA DI APPOGGIO AM_MOBILITA_ISCR_APP
					prgAmMobIscrApp = popolaAppoggio(importaIscrMBOBean, transEx);

				} else {

					// IL COMUNE DI DOMICILIO NON E' NELLA REGIONE?
					log.info("[IMPORTAZIONE ISCR MBO] Verifica che il comune di domicilio non sia nella regione");
					params = new Object[2];
					params[0] = importaIscrMBOBean.getComuneDom();
					params[1] = codRegioneLocale;
					existCom = (SourceBean) transEx.executeQuery("CHECK_COMUNE_IN_REG", params, "SELECT");
					BigDecimal countComDom = (BigDecimal) existCom.getAttribute("ROW.COUNTCOM");

					if (countComDom.intValue() == 0) {

						// IL COMUNE DI RESIDENZA E' NELLA PROVINCIA?
						log.info("[IMPORTAZIONE ISCR MBO] Verifica che il comune di residenza sia nella provincia");
						params = new Object[2];
						params[0] = importaIscrMBOBean.getComuneRes();
						params[1] = codProvinciaLocale;
						existCom = (SourceBean) transEx.executeQuery("CHECK_COMUNE_IN_PROV", params, "SELECT");
						countCom = (BigDecimal) existCom.getAttribute("ROW.COUNTCOM");

						if (countCom.intValue() > 0) {

							log.info(
									"[IMPORTAZIONE ISCR MBO] Il comune di residenza e' nella provincia: inserisco in AM_MOBILITA_ISCR_APP");
							// IMPORTAZIONE ISCRIZIONE: INSERIMENTO NELLA TABELLA DI APPOGGIO AM_MOBILITA_ISCR_APP
							prgAmMobIscrApp = popolaAppoggio(importaIscrMBOBean, transEx);

						} else {

							// IL COMUNE DI RESIDENZA NON E' NELLA REGIONE?
							log.info(
									"[IMPORTAZIONE ISCR MBO] Verifica che il comune di residenza non sia nella regione");
							params = new Object[2];
							params[0] = importaIscrMBOBean.getComuneRes();
							params[1] = codRegioneLocale;
							existCom = (SourceBean) transEx.executeQuery("CHECK_COMUNE_IN_REG", params, "SELECT");
							BigDecimal countComRes = (BigDecimal) existCom.getAttribute("ROW.COUNTCOM");

							if (countComRes.intValue() == 0) {

								// IL COMUNE DELL'UNITAAZ E' NELLA PROVINCIA?
								log.info(
										"[IMPORTAZIONE ISCR MBO] Verifica che il comune della sede sia nella provincia");
								params = new Object[2];
								params[0] = importaIscrMBOBean.getComuneAz();
								params[1] = codProvinciaLocale;
								existCom = (SourceBean) transEx.executeQuery("CHECK_COMUNE_IN_PROV", params, "SELECT");
								countCom = (BigDecimal) existCom.getAttribute("ROW.COUNTCOM");

								if (countCom.intValue() > 0) {

									log.info(
											"[IMPORTAZIONE ISCR MBO] Il comune della sede e' nella provincia: inserisco in AM_MOBILITA_ISCR_APP");
									// IMPORTAZIONE ISCRIZIONE: INSERIMENTO NELLA TABELLA DI APPOGGIO
									// AM_MOBILITA_ISCR_APP
									prgAmMobIscrApp = popolaAppoggio(importaIscrMBOBean, transEx);

								} else {
									transEx.commitTransaction();
									// SECONDO IL GRAFO, A QUESTO PUNTO NON SI FA NULLA: COMUNICO CHE IL SERVIZIO
									// TERMINA SENZA IMPORTAZIONE
									log.info("[IMPORTAZIONE ISCR MBO] "
											+ "Il servizio di importazione iscrizione MBO ha completato il suo lavoro senza importare nulla");
									RispostaXML risposta = new RispostaXML("999", "Impossibile inserire iscrizione",
											"I");
									return risposta.toXMLString();

								}
							} else {
								transEx.commitTransaction();
								// SECONDO IL GRAFO, A QUESTO PUNTO NON SI FA NULLA: COMUNICO CHE IL SERVIZIO TERMINA
								// SENZA IMPORTAZIONE
								log.info("[IMPORTAZIONE ISCR MBO] "
										+ "Il servizio di importazione iscrizione MBO ha completato il suo lavoro senza importare nulla");
								RispostaXML risposta = new RispostaXML("999", "Impossibile inserire iscrizione", "I");
								return risposta.toXMLString();

							}
						}
					} else {
						transEx.commitTransaction();
						// SECONDO IL GRAFO, A QUESTO PUNTO NON SI FA NULLA: COMUNICO CHE IL SERVIZIO TERMINA SENZA
						// IMPORTAZIONE
						log.info("[IMPORTAZIONE ISCR MBO] "
								+ "Il servizio di importazione iscrizione MBO ha completato il suo lavoro senza importare nulla");
						RispostaXML risposta = new RispostaXML("999", "Impossibile inserire iscrizione", "I");
						return risposta.toXMLString();

					}
				}
			}
			transEx.commitTransaction();
		} catch (SAXException | IOException | XPathExpressionException | ParserConfigurationException e) {
			return logError(importaIscrMBOBean, e, transEx);
		} catch (ParseException e) {
			return logError(importaIscrMBOBean, e, transEx);
		} catch (EMFInternalError e) {
			return logError(importaIscrMBOBean, e, transEx);
		} catch (Exception e) {
			return logError(importaIscrMBOBean, e, transEx);
		}

		try {
			// VALIDAZIONE AUTOMATICA: questo scatena il thread della validazione
			if (canValidate) {
				log.info("[IMPORTAZIONE ISCR MBO] " + "Avvio processo di validazione automatica");
				ValidazioneXMLMbo valXML = new ValidazioneXMLMbo(prgAmMobIscrApp, importaIscrMBOBean);
				valXML.valida();
			}
		} catch (SourceBeanException e) {
			return logError(importaIscrMBOBean, e, null);
		}

		RispostaXML risposta = new RispostaXML("101", "Successo. Iscrizione inserita.", "I");
		risultato = risposta.toXMLString();

		log.info("Il servizio di importazione iscrizione MBO ha completato il suo lavoro");

		return risultato;
	}

	/**
	 * Popola la AM_MOBILITA_ISCR_APP con i dati della mobilita in ingresso
	 * 
	 * @param importaIscrMBOBean
	 *            bean con i dati della mobilita
	 * @param transEx
	 *            TransactionQueryExecutor
	 * @return BigDecimal chiave della mobilita inserita, <code>null</code> in caso di errore
	 * @throws EMFInternalError
	 */
	private BigDecimal popolaAppoggio(ImportaIscrizioneMBOBean importaIscrMBOBean, TransactionQueryExecutor transEx)
			throws EMFInternalError {

		/* recupero la sequence per l'inserimento */
		BigDecimal prgAmMobIscrApp = DBKeyGenerator.getNextSequence(Values.DB_SIL_DATI, "S_AM_MOBILITA_ISCR_APP");

		/* recupero il codcpi */
		Object[] params = new Object[1];
		params[0] = importaIscrMBOBean.getComuneDom();
		SourceBean result = (SourceBean) transEx.executeQuery("GET_COD_CPI", params, "SELECT");
		String codCpi = StringUtils.getAttributeStrNotNull(result, "ROW.CODCPI");
		String codMotivoFineDomanda = "";
		String dataCRT = importaIscrMBOBean.getDataCRT();
		String numCRT = importaIscrMBOBean.getNumAttoMob();
		if ((dataCRT == null || dataCRT.equals("")) && (numCRT == null || numCRT.equals(""))) {
			codMotivoFineDomanda = "I"; // identifica una domanda non concessa
		}
		int i = -1;
		params = new Object[47];
		params[++i] = prgAmMobIscrApp;
		params[++i] = importaIscrMBOBean.getDataInvio();
		params[++i] = importaIscrMBOBean.getCodiceFiscaleAz();
		params[++i] = importaIscrMBOBean.getRagioneSociale();
		params[++i] = importaIscrMBOBean.getIndirizzoAz();
		params[++i] = importaIscrMBOBean.getComuneAz();
		params[++i] = importaIscrMBOBean.getCapAz();
		params[++i] = importaIscrMBOBean.getTelefonoAz();
		params[++i] = importaIscrMBOBean.getFax();
		params[++i] = importaIscrMBOBean.geteMail();
		params[++i] = importaIscrMBOBean.getSettore();
		params[++i] = importaIscrMBOBean.getCcnlApplicato();
		params[++i] = importaIscrMBOBean.getMatricolaINPS();
		params[++i] = importaIscrMBOBean.getCodiceFiscaleLav();
		params[++i] = importaIscrMBOBean.getCognome();
		params[++i] = importaIscrMBOBean.getNome();
		params[++i] = importaIscrMBOBean.getSesso();
		params[++i] = importaIscrMBOBean.getDataNascita();
		params[++i] = importaIscrMBOBean.getComuneNascita();
		params[++i] = importaIscrMBOBean.getCittadinanza();
		params[++i] = importaIscrMBOBean.getComuneDom();
		params[++i] = importaIscrMBOBean.getIndirizzoDom();
		params[++i] = importaIscrMBOBean.getCapDom();
		params[++i] = importaIscrMBOBean.getTelefono();
		params[++i] = importaIscrMBOBean.getCellulare();
		params[++i] = importaIscrMBOBean.getTipoIscrMbo();
		params[++i] = numCRT;
		params[++i] = dataCRT;
		params[++i] = importaIscrMBOBean.getDataInizioMob();
		params[++i] = importaIscrMBOBean.getDataFineMob();
		params[++i] = importaIscrMBOBean.getDataMaxDiff();
		params[++i] = dataCRT;
		params[++i] = importaIscrMBOBean.getCodEnteDetermina();
		params[++i] = importaIscrMBOBean.getDataInizioMov();
		params[++i] = importaIscrMBOBean.getDataFineMov();
		params[++i] = importaIscrMBOBean.getCodMansione();
		params[++i] = importaIscrMBOBean.getCodCCNL();
		params[++i] = "F"; // TODO da codificare
		params[++i] = importaIscrMBOBean.getComuneRes();
		params[++i] = importaIscrMBOBean.getIndirizzoRes();
		params[++i] = importaIscrMBOBean.getCapRes();
		params[++i] = importaIscrMBOBean.getTelefono();
		params[++i] = importaIscrMBOBean.getFlgNonImprenditore();
		params[++i] = importaIscrMBOBean.getFlgCasoDubbio();
		params[++i] = codCpi;
		params[++i] = importaIscrMBOBean.getCodiceComunicazione();
		params[++i] = codMotivoFineDomanda;

		boolean esito = ((Boolean) transEx.executeQuery("INSERT_AM_MOB_ISCR_APP", params, "INSERT")).booleanValue();
		if (!esito) {
			return null;
		}

		return prgAmMobIscrApp;
	}

	/**
	 * Generico wrapper per l'eccezione tirata
	 * 
	 * @param importaIscrMBOBean
	 *            ImportaIscrizioneMBOBean
	 * @param e
	 *            Exception tirata
	 * @param transEx
	 *            TransactionQueryExecutor
	 * @return String messaggio di errore
	 * @throws EMFInternalError
	 */
	private String logError(ImportaIscrizioneMBOBean importaIscrMBOBean, Exception e,
			TransactionQueryExecutor transEx) {

		String risultato = "";
		RispostaXML risposta = null;

		try {
			if (transEx != null) {
				transEx.rollBackTransaction();
			}

			risposta = new RispostaXML("999", "[Codice domanda: " + importaIscrMBOBean.getCodiceComunicazione() + "] "
					+ "Errore durante l'esecuzione del servizio di importazione iscrizione MBO: " + e.getMessage(),
					"E");
			risultato = risposta.toXMLString();

			log.error(risultato);

			return risultato;
		} catch (EMFInternalError e1) {
			risposta = new RispostaXML("999", "[Codice domanda: " + importaIscrMBOBean.getCodiceComunicazione() + "] "
					+ "Errore durante l'esecuzione del servizio di importazione iscrizione MBO: " + e.getMessage(),
					"E");
			risultato = risposta.toXMLString();

			log.error(risultato);

			return risultato;
		}
	}

}
