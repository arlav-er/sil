package it.eng.sil.util.amministrazione.impatti;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.module.movimenti.processors.Warning;

/**
 * @author De Simone
 * 
 *         Classe per il calcolo degli impatti per la registrazione di movimenti senza precedente
 */
public class MovimentiNonCollegati {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MovimentiNonCollegati.class.getName());

	// TransactionQueryExecutor da utilizzare per le transazioni
	private TransactionQueryExecutor trans;
	private SourceBean movimento;
	private BigDecimal cdnLavoratore;
	// private BigDecimal prgMovimento;
	private RequestContainer requestContainer;
	private SourceBean request;
	// private SourceBean response;
	// Data di inizio del mov., da cui effettuare la ricostruzione e in cui
	// inserire lo S.O.
	private String dataInizio;
	private String codTipoMov;
	private StatoOccupazionaleBean statoOccIniziale;
	private StatoOccupazionaleBean statoOccDaAssociareMov = null;

	private static final int STATO_ALTRO = 0;
	private static final int STATO_OCCUPATO = 1;
	private static final int ERROR = -1;
	private int statoOccRaggruppamento;
	private boolean statoDiPartenza = false;

	/**
	 * @params transQueryExec, per la transazione; data, data di inizio del movimento; req, request; reqCont,
	 *         RequestContainer
	 * 
	 *         Costruttore
	 * 
	 */
	public MovimentiNonCollegati(Map req, RequestContainer reqCont, TransactionQueryExecutor transQueryExec) {
		try {
			requestContainer = reqCont;
			request = requestContainer.getServiceRequest();
			trans = transQueryExec;
			cdnLavoratore = new BigDecimal(req.get("CDNLAVORATORE").toString());
			codTipoMov = req.get("CODTIPOMOV").toString();
			dataInizio = req.get("DATINIZIOMOV").toString();
			// prgMovimento = new
			// BigDecimal(req.getAttribute("prgMovimento").toString());
			movimento = Controlli.getMovimento(req);

			// Recupero lo stato occupazionale del lavoratore nella data
			// indicata
			statoOccIniziale = DBLoad.getStatoOccupazionaleUltimo(cdnLavoratore, dataInizio, trans);
			if (statoOccIniziale == null) {
				// Se non ve ne è uno lo creo in base alle info che gli passo
				Vector dids = DBLoad.getDichiarazioniDisponibilitaNonProtocollate(cdnLavoratore, "01/01/0001", trans);
				Vector movimentiAperti = DBLoad.getMovimentiApertiDa(dataInizio, cdnLavoratore);
				movimentiAperti = togliMovimentoNonProt(movimentiAperti);
				movimentiAperti = togliMovimentoInDataFutura(movimentiAperti);
				movimentiAperti = MovimentoBean.gestisciTuttiPeriodiIntermittentiApertiDa(movimentiAperti, dataInizio,
						trans);
				// recupero eventuale lista mobilita del lavoratore
				ListaMobilita mobilita = new ListaMobilita(cdnLavoratore, transQueryExec);
				Vector rowsMobilita = mobilita.getMobilita();
				statoOccIniziale = StatoOccupazionaleBean.creaStatoOccupazionaleBeanIniziale(dataInizio, cdnLavoratore,
						null, dids, rowsMobilita, movimentiAperti, trans);
			}

			if (statoOccIniziale.getStatoOccupazRagg().equals("A")) {
				statoOccRaggruppamento = STATO_ALTRO;
			} else {
				if (statoOccIniziale.getStatoOccupazRagg().equals("O")) {
					statoOccRaggruppamento = STATO_OCCUPATO;
				} else {
					if (statoOccIniziale.getStatoOccupazRagg().equals("D") && codTipoMov.equals("TRA")) {
						statoOccRaggruppamento = MessageCodes.CollegaMov.ERR_TRASFO_TI_NON_COLLEGATA_DOPO_297;
					} else {
						statoOccRaggruppamento = ERROR;
					}
				}
			}
		}

		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "Errore nell'inizializzazione dei parametri", ex);

		}
	}// end MovimentiNonCollegati()

	/**
	 * Metodo per la memorizzazione dello stato occupazionale associato e della ricostruzione della storia a partire
	 * dalla dataInizio
	 * 
	 * @return nuovoStatoOcc, l'utimo stato occupazionale generato con la ricostruzione della storia
	 */
	public StatoOccupazionaleBean generaImpatti() throws Exception {
		StatoOccupazionaleBean nuovoStatoOcc = null;
		boolean isPrecNormativa = false;
		boolean ricostruisci = false;
		String dataNormativa297 = null;

		try {
			UtilsConfig utility = new UtilsConfig("AM_297");
			dataNormativa297 = utility.getValoreConfigurazione();
			if (Controlli.movimentoPrecedenteNormativa(movimento, dataNormativa297)) {
				isPrecNormativa = true;
				ArrayList warnings = null;
				if (requestContainer.getServiceRequest()
						.containsAttribute(StatoOccupazionaleManager.WARNING_STATO_OCCUPAZIONALE))
					warnings = (ArrayList) requestContainer.getServiceRequest()
							.getAttribute(StatoOccupazionaleManager.WARNING_STATO_OCCUPAZIONALE);
				else {
					warnings = new ArrayList();
					requestContainer.getServiceRequest()
							.setAttribute(StatoOccupazionaleManager.WARNING_STATO_OCCUPAZIONALE, warnings);
				}
				Vector paramV = new Vector(1);
				paramV.add(dataNormativa297);
				warnings.add(new it.eng.sil.module.movimenti.processors.Warning(
						MessageCodes.StatoOccupazionale.MOVIMENTO_PREC_NORMATIVA,
						MessageCodes.StatoOccupazionale.MOVIMENTO_PREC_DATA_NORMATIVA, paramV));
				request.setAttribute("MOV_IS_PREC_NORMATIVA", "0");
			}
		} catch (Exception ex) {
			_logger.fatal("Errore nel controllo di se si tratta di un movimento con data precedentenormativa."
					+ ex.toString());

			return null;
		}
		if (!isPrecNormativa) {
			if (codTipoMov.equals("CES")) {
				switch (statoOccRaggruppamento) {
				case STATO_ALTRO:
					String codMotivoFine = "";
					if (statoOccIniziale.getStatoOccupaz().equals("C0")) {
						codMotivoFine = movimento.containsAttribute("CODMVCESSAZIONE")
								? movimento.getAttribute("CODMVCESSAZIONE").toString()
								: "";
						if (codMotivoFine.equals("SC")) {
							ricostruisci = true;
						} else {
							/*
							 * Se si è 'cessati non rientrati' non viene restituito un errore ma lo stato occupazionale
							 * iniziale
							 */
							nuovoStatoOcc = statoOccIniziale;
							ricostruisci = false;
							setAlertNonCambiamentoStoriaLav();
						}
					} else {
						ricostruisci = true;
					}
					break;
				case STATO_OCCUPATO:
					if (statoOccIniziale.getStatoOccupaz().equals("A0")) {
						/*
						 * Se si è sospesi per contrazione d'attività non viene restituito null
						 */
						// Non fare nulla
						/*
						 * nuovoStatoOcc = statoOccIniziale; setAlertNonCambiamentoStoriaLav();
						 */
						ricostruisci = false;
						setWarningCasoNonGestito();
						try {
							request.setAttribute("CASO_NON_GESTITO", "0");
						} catch (Exception ex) {
							_logger.fatal("Errore: " + ex.toString());

							return null;
						}
					} else
						ricostruisci = true;
					break;
				case ERROR:
					// In caso di errore restituisce come nuovoStatoOcc = null
					// codiceErrore = -1;
					_logger.fatal("Errore nel ricavo dello stato occupazionale di raggruppamento.");

					ricostruisci = false;
					break;
				}// end switch(statoOccRaggruppamento)
			} // end if(codTipoMov.equals("CES"))
			else {
				// Trasformazione
				switch (statoOccRaggruppamento) {
				case STATO_ALTRO:
					ricostruisci = true;
					break;
				case STATO_OCCUPATO:
					/*
					 * Se si è occupato o occupato in cerca di altra occupazione non viene restituito un errore ma lo
					 * stato occupazionale iniziale
					 */
					if (statoOccIniziale.getStatoOccupaz().equals("B")
							|| statoOccIniziale.getStatoOccupaz().equals("A0")
							|| statoOccIniziale.getStatoOccupaz().equals("A1")) {
						nuovoStatoOcc = statoOccIniziale;
						ricostruisci = true;
						setAlertNonCambiamentoStoriaLav();
						if (nuovoStatoOcc.virtuale()) {
							String dataInizioSo = nuovoStatoOcc.getDataInizio();
							DBStore.creaNuovoStatoOcc(nuovoStatoOcc, dataInizioSo, requestContainer, trans);
						}
					} else {
						ricostruisci = false;// non fare nulla xchè in
												// sospesi per contrazione
												// d'attività
						setWarningCasoNonGestito();
						try {
							request.setAttribute("CASO_NON_GESTITO", "0");
						} catch (Exception ex) {
							_logger.fatal("Errore: " + ex.toString());

							return null;
						}
					}
					break;

				case MessageCodes.CollegaMov.ERR_TRASFO_TI_NON_COLLEGATA_DOPO_297:
					// In caso di errore restituisce come nuovoStatoOcc = null
					_logger.fatal(
							"Trasformazione senza movimento collegato dopo un periodo in cui il lavoratore si trova in 150.");

					throw new ProTrasfoException(MessageCodes.CollegaMov.ERR_TRASFO_TI_NON_COLLEGATA_DOPO_297);

				case ERROR:
					// In caso di errore restituisce come nuovoStatoOcc = null
					_logger.fatal("Errore nel ricavo dello stato occupazionale di raggruppamento.");

					ricostruisci = false;
					break;
				}
			} // end else
		}
		/*
		 * RICOSTRUZIONE STORIA
		 */
		if (ricostruisci) {
			StatoOccupazionaleBean statoOccPrecAlCorrente = null;
			StatoOccupazionaleBean nuovoStatoOccupazionale = null;
			SituazioneAmministrativa sitAmm = null;
			int anno = 0;

			try {
				anno = DateUtils.getAnno(DateUtils.getNow());
				String dataRif = "";
				String dataIn = "01/01/" + anno;
				String dataFine = "31/12/" + anno;

				// Considero tutti i movimenti del lavoratore
				Vector vect = DBLoad.getMovimentiLavoratore(cdnLavoratore, trans);
				vect = togliMovimentoNonProt(vect);
				// vect = aggiungiMovimentoInEsame(vect);
				SourceBean ce = new SourceBean(movimento);
				Controlli.inserisciCessazione(vect, ce);
				// throw new Exception("cessato non collegato collegato!");
				// recupero gli stati occupaz (compreso quello appena inserito),
				// did e patti del lavoratore
				Vector statiOccupazionali = DBLoad.getStatiOccupazionali(cdnLavoratore, trans);
				Vector patti = DBLoad.getPattiStoricizzati(cdnLavoratore, "01/01/0001", trans);
				Vector dids = DBLoad.getDichiarazioniDisponibilitaNonProtocollate(cdnLavoratore, "01/01/0001", trans);
				dataRif = dataInizio;

				sitAmm = new SituazioneAmministrativa(vect, statiOccupazionali, patti, dids, dataInizio, trans,
						requestContainer);
				// movimento.setAttribute(Movimento.FLAG_IN_INSERIMENTO,"");//Mov
				// che sto inserendo
				sitAmm.ricrea(movimento);
				if (!sitAmm.getPrgStatoOccMovNonCollegato().equals(""))
					request.updAttribute("PRG_STATO_OCC_MOV_NON_COLLEGATO", sitAmm.getPrgStatoOccMovNonCollegato());
				StatoOccupazionaleManager.impostaAlert(sitAmm, request, cdnLavoratore);
			}

			catch (ProTrasfoException proTraExc) {
				_logger.fatal("Errore nella ricostruzione della storia." + proTraExc.toString());

				nuovoStatoOcc = null;
				throw proTraExc;
			}

			catch (ControlliException ce) {
				_logger.fatal("Errore nella ricostruzione della storia." + ce.toString());

				nuovoStatoOcc = null;
				throw ce;
			}

			catch (MobilitaException me) {
				_logger.fatal("Errore nella ricostruzione della storia." + me.toString());

				nuovoStatoOcc = null;
				throw me;
			}

			catch (Exception ex) {
				_logger.fatal("Errore nella ricostruzione della storia." + ex.toString());

				nuovoStatoOcc = null;
				return nuovoStatoOcc;
			}
		}

		return nuovoStatoOcc;
	}// end generaImpatti()

	/**
	 * cancella dalla lista i movimenti non protocollati, e quindi che non conseguenze sugli impatti
	 */
	private Vector togliMovimentoNonProt(Vector vect) throws Exception {
		Vector v = (vect.size() >= 1) ? new Vector(vect.size() - 1) : new Vector(0);
		for (int i = 0; i < vect.size(); i++) {
			SourceBean tmp = (SourceBean) vect.get(i);
			if (!tmp.getAttribute("CODSTATOATTO").equals("PR"))
				continue;
			v.add(tmp);
		} // end for
		return v;
	}// end togliMovimentoNonProt

	private Vector togliMovimentoInDataFutura(Vector vect) throws Exception {
		Vector v = new Vector();
		String oggi = DateUtils.getNow();
		for (int i = 0; i < vect.size(); i++) {
			SourceBean tmp = (SourceBean) vect.get(i);
			if (DateUtils.compare(tmp.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString(), oggi) > 0)
				continue;
			v.add(tmp);
		} // end for
		return v;
	}

	/**
	 * Aggiunge alla lista dei movimenti il movimento che si sta inserendo, in modo da poter ricalcolare lo stato
	 * occupazionale
	 * 
	 * @return il vettore dei movimenti nell'anno con quello che si sta inserendo
	 * @param il
	 *            vettore dei movimenti nell'anno
	 */
	private Vector aggiungiMovimentoInEsame(Vector vect) throws Exception {
		Vector v = new Vector(vect.size() - 1);
		boolean inserito = false;
		for (int i = 0; i < vect.size(); i++) {
			SourceBean tmp = (SourceBean) vect.get(i);
			String dataVect = tmp.getAttribute("DATINIZIOMOV").toString();
			String dataMov = movimento.getAttribute("DATINIZIOMOV").toString();
			int dv = Integer.parseInt(dataVect.substring(6, 9) + dataVect.substring(3, 5) + dataVect.substring(0, 2));
			int dm = Integer.parseInt(dataMov.substring(6, 9) + dataMov.substring(3, 5) + dataMov.substring(0, 2));
			if (dv <= dm)
				v.add(tmp);
			else {
				v.add(movimento);
				v.add(tmp);
				inserito = true;
			}
		} // end for
		if (!inserito)
			v.add(movimento);
		return v;
	}// end aggiungiMovimentoInEsame

	private void setAlertNonCambiamentoStoriaLav() {
		String msg = "";
		ArrayList alerts = null;
		try {
			if (request.containsAttribute(StatoOccupazionaleManager.ALERT_STATO_OCCUPAZIONALE))
				alerts = (java.util.ArrayList) request
						.getAttribute(StatoOccupazionaleManager.ALERT_STATO_OCCUPAZIONALE);
			else {
				alerts = new java.util.ArrayList();
				request.setAttribute(StatoOccupazionaleManager.ALERT_STATO_OCCUPAZIONALE, alerts);
			}
			msg = "Ricostruita storia del lavoratore\\n\\r";
			msg += "Lo stato occupazionale del lavoratore non e' cambiato";
			alerts.add(msg);
		} catch (Exception e) {
			_logger.fatal("Errore nell'impostazione dell'alert." + e.toString());

		}
	}

	private void setWarningCasoNonGestito() {
		ArrayList warnings = new ArrayList();
		try {
			if (request.containsAttribute(StatoOccupazionaleManager.WARNING_STATO_OCCUPAZIONALE))
				warnings = (ArrayList) request.getAttribute(StatoOccupazionaleManager.WARNING_STATO_OCCUPAZIONALE);
			else
				request.setAttribute(StatoOccupazionaleManager.ALERT_STATO_OCCUPAZIONALE, warnings);
			warnings.add(new Warning(MessageCodes.ImportMov.WAR_CASO_NON_GESTITO,
					"Il lavoratore risulta essere in SOSPENSIONE PER CONTRAZIONE ATTIVITA'"));
		} catch (Exception e) {
			_logger.fatal(
					"Errore nell'impostazione del warning per i sospesi per contrazione d'attività." + e.toString());

		}
	}

}// end class
