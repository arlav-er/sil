/*
 * Creato il 30-ago-04
 */
package it.eng.sil.module.movimenti.trasfRamoAzienda;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.message.MessageBundle;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.bean.Documento;
import it.eng.sil.bean.ProtocollaException;
import it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.NavigationCache;

/**
 * @author roccetti Modulo che effettua il trasferimento del ramo aziendale per i movimenti selezionati e li protocolla
 */
public class TrasferisciRamoAzienda extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(TrasferisciRamoAzienda.class.getName());

	// Variabili per protocollazione
	private boolean primoDocDaProt = true;
	// Indica se sto eseguendo la prima protocollazione della lista
	private BigDecimal numProtPrecedente;
	// Indica il precedente numero di protocollo utilizzato
	private boolean error = false;

	// Indica se ci sono errori che impediscono l'intero trasferimento

	/*
	 * (non Javadoc)
	 * 
	 * @see com.engiweb.framework.dispatching.service.ServiceIFace#service(com.engiweb.framework.base.SourceBean,
	 * com.engiweb.framework.base.SourceBean)
	 */
	public void service(SourceBean request, SourceBean response) throws Exception {

		// recupero dei codici movimenti selezionati dalla sessione
		RequestContainer reqCont = getRequestContainer();
		SessionContainer sesCont = reqCont.getSessionContainer();
		NavigationCache cache = (NavigationCache) sesCont.getAttribute("TRASFRAMOAZIENDACACHE");
		Vector listaMov = new Vector();
		Object checkedMovObj = "EMPTY";

		if (cache != null) {
			// Controllo coerenza cache
			String PRGAZIENDAPROVENIENZA = StringUtils.getAttributeStrNotNull(request, "PRGAZIENDAPROVENIENZA");
			String PRGUNITAPROVENIENZA = StringUtils.getAttributeStrNotNull(request, "PRGUNITAPROVENIENZA");
			String PRGAZIENDAPROVCACHE = (String) cache.getField("PRGAZIENDAPROVENIENZA");
			String PRGUNITAPROVCACHE = (String) cache.getField("PRGUNITAPROVENIENZA");
			if (PRGAZIENDAPROVENIENZA.equals(PRGAZIENDAPROVCACHE) && PRGUNITAPROVENIENZA.equals(PRGUNITAPROVCACHE)) {
				// cache coerente, estraggo i dati
				checkedMovObj = cache.getField("CHECKBOXMOV");
			}
		}

		if (checkedMovObj instanceof Vector) {
			listaMov = (Vector) checkedMovObj;
		} else if (!"EMPTY".equalsIgnoreCase(checkedMovObj.toString())) {
			listaMov.addElement(checkedMovObj.toString());
		}

		SourceBean result = new SourceBean("RESULT");
		SourceBean rows = new SourceBean("ROWS");
		result.setAttribute(rows);

		String codTipoTrasf = StringUtils.getAttributeStrNotNull(request, "CODTIPOTRASF");
		String dataFineAffittoRamo = StringUtils.getAttributeStrNotNull(request, "DATFINEAFFITTORAMO");
		String strWarning = "";
		if (codTipoTrasf.equals("02")) {
			if (dataFineAffittoRamo.equals("")) {
				strWarning = "Attenzione, la data fine affitto ramo non è stata valorizzata.";
			}
		} else {
			if (!dataFineAffittoRamo.equals("")) {
				strWarning = "Attenzione, la data fine affitto ramo non deve essere valorizzata.";
			}
		}
		if (!strWarning.equals("")) {
			response.setAttribute("WARNING_TRASFERIMENTO", strWarning);
		}

		// Giovanni D'Auria 03/02/05 inizio modifica

		// Per ogni movimento controllo che la data di trasferimento non sia
		// esterna alla durata del rapporto
		Iterator i = listaMov.iterator();
		boolean erroreAffittoRamo = false;
		while (i.hasNext()) {
			erroreAffittoRamo = false;
			// La stringa memorizzata in sessione ha la forma
			// <prgMovimento>_<numKloMov>
			String chiaveMovimento = (String) i.next();

			// Separo il prgmovimento e il numklomov
			int underscore = chiaveMovimento.indexOf("_");
			String prgMov = "";
			String numklomov = "";
			if (underscore >= 0) {
				prgMov = chiaveMovimento.substring(0, underscore);
				numklomov = chiaveMovimento.substring(underscore + 1, chiaveMovimento.length());
			}

			// cancello i precedenti valori in request
			request.delAttribute("PRGMOVIMENTOPREC");
			request.delAttribute("NUMKLOMOVPREC");

			// Imposto il valore corrente del prgmovimento in request e il
			// numklomov
			request.setAttribute("PRGMOVIMENTOPREC", prgMov);
			request.setAttribute("NUMKLOMOVPREC", numklomov);

			// recupero dati del movimento originale
			SourceBean datiMov = doSelect(request, response);
			SourceBean rowMov = (SourceBean) datiMov.getAttribute("ROW");
			String dataInAvviamento = (String) rowMov.getAttribute("DATINIZIOAVV");
			if (dataInAvviamento == null) {
				dataInAvviamento = (String) rowMov.getAttribute("DATINIZIOMOV");
			}
			String dataFineMov = (String) rowMov.getAttribute("DATFINEMOV");
			String tipoMovimento = (String) rowMov.getAttribute("TIPOMOVIMENTO");
			String dataTrasf = StringUtils.getAttributeStrNotNull(request, "DATTRASFERIMENTO");

			if (codTipoTrasf.equals("02")) {
				if (!dataFineAffittoRamo.equals("") && !dataTrasf.equals("")) {
					if (DateUtils.compare(dataFineAffittoRamo, dataTrasf) < 0) {
						rowMov.setAttribute("MOTIVOERRORE", MessageBundle.getMessage(
								String.valueOf(MessageCodes.AffittoRamoAzienda.ERR_DT_FINE_AFFITTO_PREC_TRA)));
						rows.setAttribute(rowMov);
						erroreAffittoRamo = true;
					}
				}
			}

			if (!erroreAffittoRamo) {
				// Controllo i movimenti a tempo determinato
				if (tipoMovimento.equalsIgnoreCase("D")) {
					// Se la data di fine movimento è minore della data del
					// trasferimento (dataInMovimento)OR
					// la data di inizio avviamento è maggiore della data del
					// trasferimento (dataInMovimento),
					// allora blocco il trasferimento

					// DateUtils.compare ritorna -1 se date1 < date2, 0 se date1 =
					// date2, 1 se date1 > date2
					if ((DateUtils.compare(dataFineMov, dataTrasf) == -1)
							|| (DateUtils.compare(dataInAvviamento, dataTrasf) == 1)) {
						rowMov.setAttribute("MOTIVOERRORE", MessageBundle
								.getMessage(String.valueOf(MessageCodes.TrasferimentoRamoAzienda.ERR_DATE_TRASF)));
						rows.setAttribute(rowMov);
					}
					// Controllo i movimenti a tempo Indeterminato
				} else if (tipoMovimento.equalsIgnoreCase("I")) {
					// Se la data di trasferimento è precedente l'inizio
					// dell'avviamento blocco il trasferimento
					if (DateUtils.compare(dataInAvviamento, dataTrasf) == 1) {
						rowMov.setAttribute("MOTIVOERRORE", MessageBundle
								.getMessage(String.valueOf(MessageCodes.TrasferimentoRamoAzienda.ERR_DATA_TRASF)));
						rows.setAttribute(rowMov);
					}
				}
			}
		} // fine while

		// Se nel SourceBean RESULT.ROWS è presente l'attributo ROW
		// vuol dire che ci sono lavoratori che non possono essere trasferiti
		if (rows.containsAttribute("ROW")) {
			error = true;
			response.setAttribute("ERRORE_BLOCCANTE", "");
		}
		// Giovanni D'Auria 03/02/05 fine modifica

		// Per ogni movimento selezionato creo una cessazione e un avviamento in
		// transazione
		i = listaMov.iterator();
		while (i.hasNext() && !error) {

			// La stringa memorizzata in sessione ha la forma
			// <prgMovimento>_<numKloMov>
			String chiaveMovimento = (String) i.next();

			// Separo il prgmovimento e il numklomov
			int underscore = chiaveMovimento.indexOf("_");
			String prgMov = "";
			String numklomov = "";
			if (underscore >= 0) {
				prgMov = chiaveMovimento.substring(0, underscore);
				numklomov = chiaveMovimento.substring(underscore + 1, chiaveMovimento.length());
			}

			// cancello i precedenti valori in request
			request.delAttribute("PRGMOVIMENTOPREC");
			request.delAttribute("NUMKLOMOVPREC");
			request.delAttribute("PRGMOVTRA");

			// Imposto il valore corrente del prgmovimento in request e il
			// numklomov
			request.setAttribute("PRGMOVIMENTOPREC", prgMov);
			request.setAttribute("NUMKLOMOVPREC", numklomov);

			// recupero dati del movimento originale
			SourceBean datiMov = doSelect(request, response);
			SourceBean rowMov = (SourceBean) datiMov.getAttribute("ROW");

			String dataInAvviamento = (String) rowMov.getAttribute("DATINIZIOAVV");
			if (dataInAvviamento == null) {
				dataInAvviamento = (String) rowMov.getAttribute("DATINIZIOMOV");
			}

			// Controllo che l'attributo non sia già presente in request, se è
			// così
			// prima cancello il vecchio valore e poi ne inserisco uno nuovo
			if (request.containsAttribute("DATINIZIOAVV")) {
				request.delAttribute("DATINIZIOAVV");
			}
			request.setAttribute("DATINIZIOAVV", dataInAvviamento);

			// Apertura transazione
			TransactionQueryExecutor trans = new TransactionQueryExecutor(getPool(), this);
			enableTransactions(trans);
			trans.initTransaction();

			// Modifica Giovanni D'Auria il 10/02/2005 inizio

			// Scelgo la query di inserimento della trasformazione
			setSectionQueryInsert("QUERY_INSERT_TRA");

			// Inserisco la trasformazione
			boolean inseritoTrasferimento = doInsert(request, response, "PRGMOVTRA");

			// Protocollo la trasformazione se l'ho inserita correttamente
			boolean protocollatoTrasferimento = false;
			// Savino: 10/09/2007 modifica del modo di cancellare il file in
			// docarea. Meglio usare il riferimento diretto al documento
			Documento doc = null;
			if (inseritoTrasferimento) {
				doc = new Documento();
				protocollatoTrasferimento = protocolla((BigDecimal) request.getAttribute("PRGMOVTRA"), "MVTRA", request,
						rowMov, doc, trans);
			}

			// Eseguo l'update del movimento precedente
			boolean aggiornatoPrecedente = false;
			if (protocollatoTrasferimento) {
				aggiornatoPrecedente = doUpdate(request, response);
			}

			if (protocollatoTrasferimento) {
				trans.commitTransaction();
				// DOCAREA
				ProtocolloDocumentoUtil.cancellaFileDocarea(doc);
			} else {
				trans.rollBackTransaction();
			}

			// Torno alla modalità di esecuzione semplice delle query
			enableSimpleQuery();

			// Salvataggio dei risultati
			if (!protocollatoTrasferimento) {

				// Se ho recuperato il movimento imposto il motivo del
				// fallimento nel trasferimento
				if (rowMov != null) {
					if (!inseritoTrasferimento) {
						rowMov.setAttribute("MOTIVOERRORE", MessageBundle
								.getMessage(String.valueOf(MessageCodes.TrasferimentoRamoAzienda.ERR_INSER_TRA)));
					} else if (!protocollatoTrasferimento) {
						rowMov.setAttribute("MOTIVOERRORE", MessageBundle
								.getMessage(String.valueOf(MessageCodes.TrasferimentoRamoAzienda.ERR_PROT_TRA)));
					} else if (!aggiornatoPrecedente) {
						rowMov.setAttribute("MOTIVOERRORE", MessageBundle
								.getMessage(String.valueOf(MessageCodes.TrasferimentoRamoAzienda.ERR_UPDATE_PREC)));
					}
					rows.setAttribute(rowMov);
				}
			}

			// Modifica Giovanni D'Auria il 10/02/2005 fine
		}

		rows.setAttribute("CURRENT_PAGE", new Integer(1));
		rows.setAttribute("NUM_PAGES", new Integer(1));
		rows.setAttribute("NUM_RECORDS", new Integer(rows.getAttributeAsVector("ROW").size()));
		rows.setAttribute("ROWS_X_PAGE", new Integer(2147483647));
		response.setAttribute(result);
	}

	// Metodo privato per la protocollazione dei movimenti inseriti,
	// ritorna true se riesce a protocollare correttamente, altrimenti false
	public boolean protocolla(BigDecimal prgMovimento, String codTipoDoc, SourceBean request, SourceBean rowMov,
			Documento doc, TransactionQueryExecutor transExec) {

		try {
			// Creazione e configurazione oggetto documento
			doc = settaDocumento(request, prgMovimento, codTipoDoc, rowMov, doc);
			// DOCAREA:
			ProtocolloDocumentoUtil.putInRequest(doc);
			// Protocollazione
			doc.insert(transExec);
			numProtPrecedente = doc.getNumProtInserito();
		} catch (ProtocollaException ex) {
			it.eng.sil.util.TraceWrapper.error(_logger,
					"it.eng.sil.module.movimenti.trasfRamoAzienda.TrasferisciRamoAzienda::protocolla():"
							+ ex.getMessage(),
					ex);

			return false;
		} catch (Exception emf) {
			it.eng.sil.util.TraceWrapper.error(_logger,
					"it.eng.sil.module.movimenti.trasfRamoAzienda.TrasferisciRamoAzienda::protocolla():"
							+ emf.getMessage(),
					emf);

			return false;
		}

		return true;
	}

	// Metodo per la configurazione del documento in protocollazione
	private Documento settaDocumento(SourceBean request, BigDecimal prgMov, String codTipoDoc, SourceBean rowMov,
			Documento doc) throws SourceBeanException {

		// Documento doc = new Documento();

		// Settaggio dati protocollazione dalla request
		BigDecimal numProt = SourceBeanUtils.getAttrBigDecimal(request, "numProt", null);
		String annoProt = StringUtils.getAttributeStrNotNull(request, "numAnnoProt");
		String dataProt = StringUtils.getAttributeStrNotNull(request, "dataProt");
		String oraProt = StringUtils.getAttributeStrNotNull(request, "oraProt");
		String tipoProt = StringUtils.getAttributeStrNotNull(request, "tipoProt");
		String prgAzDest = StringUtils.getAttributeStrNotNull(request, "PRGAZIENDADESTINAZIONE");
		String prgUAzDest = StringUtils.getAttributeStrNotNull(request, "PRGUNITADESTINAZIONE");
		String datComunicaz = StringUtils.getAttributeStrNotNull(request, "DATCOMUNICAZ");
		String datInizioMov = StringUtils.getAttributeStrNotNull(request, "DATTRASFERIMENTO");
		String strEnteRilascio = StringUtils.getAttributeStrNotNull(request, "STRENTERILASCIO");

		if (!annoProt.equals(""))
			doc.setNumAnnoProt(new BigDecimal(annoProt));
		if (!dataProt.equals("") && !oraProt.equals(""))
			doc.setDatProtocollazione(dataProt + " " + oraProt);
		if (!tipoProt.equals(""))
			doc.setTipoProt(tipoProt);
		if (!prgAzDest.equals(""))
			doc.setPrgAzienda(new BigDecimal(prgAzDest));
		if (!prgUAzDest.equals(""))
			doc.setPrgUnita(new BigDecimal(prgUAzDest));
		if (!datComunicaz.equals(""))
			doc.setDatAcqril(datComunicaz);
		if (!datInizioMov.equals(""))
			doc.setDatInizio(datInizioMov);
		if (!strEnteRilascio.equals(""))
			doc.setStrEnteRilascio(strEnteRilascio);

		// Recupero dati protocollazione dal movimento
		String codCpi = StringUtils.getAttributeStrNotNull(rowMov, "CODCPI");
		String cdnLav = StringUtils.getAttributeStrNotNull(rowMov, "CDNLAVORATORE");

		if (!codCpi.equals(""))
			doc.setCodCpi(codCpi);
		if (!cdnLav.equals(""))
			doc.setCdnLavoratore(new BigDecimal(cdnLav));

		doc.setCodTipoDocumento(codTipoDoc);
		doc.setCodMonoIO("I");
		doc.setPagina("MovDettaglioGeneraleConsultaPage");
		doc.setCdnUtMod((BigDecimal) getRequestContainer().getSessionContainer().getAttribute("_CDUT_"));
		doc.setCdnUtIns((BigDecimal) getRequestContainer().getSessionContainer().getAttribute("_CDUT_"));
		doc.setChiaveTabella(prgMov.toString());

		// Scelta numero protocollo corrente
		if (primoDocDaProt) {
			numProtPrecedente = numProt;
			primoDocDaProt = false;
			doc.setNumProtocollo(numProt);
		} else {
			BigDecimal numProPiu1 = numProtPrecedente.add(new BigDecimal(1)); // numPrPre
																				// :=
																				// numPrPre
																				// + 1
			doc.setNumProtocollo(numProPiu1);
		}

		return doc;
	}
}