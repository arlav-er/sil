/*
 * Created on Feb 28, 2007
 */
package it.eng.sil.bean.protocollo;

import java.io.File;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFInternalError;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.bean.Documento;
import it.eng.sil.module.movimenti.processors.ProcessorsUtils;
import it.eng.sil.util.pdfStomper;

/**
 * Protocollazione standard del SIL. Inserimento documento da non protocollare nel db.<br>
 * <ul>
 * <ol>
 * <li>lettura info protocollo
 * <li>inserimento documento
 * <li>creazione report (se esiste)
 * <li>inserimento file (se esiste)
 * <li>aggiornamento protocollo e controllo di concorrenza
 * </ol>
 * </ul>
 * Nel caso in cui lo stato del documento non sia PR le operazione sulla tabella AM_PROTOCOLLO vengono ugualmente fatte
 * (come accadeva prima).
 * 
 * @author Savino
 */
public class RDS_DBLocale implements RegistraDocumentoStrategy {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RDS_DBLocale.class.getName());

	private pdfStomper stomper = new pdfStomper();

	public void registra(Documento doc, TransactionQueryExecutor tex) throws Exception {
		// lettura delle info di protocollo dalla tabella AM_PROTOCOLLO. Il
		// numero di lock verra' utilizzato in fase di registrazione (metodo
		// registra())
		// TODO DOCAREA: controllare se nel vecchio Documento quando non si
		// protocolla si locca ugualmente la tabella am_protocollo
		// solo se si deve protocollare
		if ("PR".equals(doc.getCodStatoAtto()) && !doc.protocollatoEsternamente())
			leggiProtocollo(doc, tex);
		// inserimento nelle tre tabelle dei documenti
		if (doc.inInserimento())
			doc.inserisciDocumento(tex);
		else
			// oppure aggiornamento della sola tabella AM_DOCUMENTO
			doc.aggiornaDocumento(tex);
		// creazione del report se esiste e inserimento del file nel db
		// il file puo' essere anche il file inviato dal browser.
		doc.creaEinserisciReport(tex);

		// si aggiorna la tabella del protocollo. Viene utilizzato per il
		// controllo di concorrenza il numero di lock letto in precedenza
		if ("PR".equals(doc.getCodStatoAtto()) && "S".equals(doc.getTipoProt()) && !doc.protocollatoEsternamente()) {
			// ma solo se la protocollazione e' automatica. Per quella manuale
			// non
			// e' necessaria.

			/* Se il report e' tra quelli 'pesanti' */
			if (doc.isProtPosticipated()) {
				/*
				 * il protocollo viene inserito solo alla fine, qui ancora null
				 */
				doc.setNumProtInserito(null);

				Monitor monitor1 = null;
				Monitor monitor3 = null;
				Monitor monitor4 = null;

				String codRegione = null;

				String query = "select codregione " + "from de_provincia "
						+ "inner join ts_generale on (ts_generale.CODPROVINCIASIL = de_provincia.CODPROVINCIA)";

				SourceBean result = null;
				try {
					result = ProcessorsUtils.executeSelectQuery(query, tex);
				} catch (Exception e) {
					_logger.error("Impossibile ottenere il codice della provincia. Errore nella query" + query
							+ ". \nErrore: " + e.getMessage());
					throw new Exception(e);

				}

				// Esamino il risultato
				codRegione = (String) result.getAttribute("ROW.CODREGIONE");

				monitor1 = MonitorFactory.start("RDS_DBLocale: [ leggiNewNumProt ]");

				/* Recupero nuovo num prot. */
				doc.leggiNewNumProt(tex);
				String numProt = String.valueOf(doc.getNumProtInserito());

				monitor1.stop();

				if (doc.getPrgTemplateStampa() == null) {

					monitor3 = MonitorFactory.start("RDS_DBLocale: [ aggiungi_timbro ]");
					/* Ora lo appongo definitivamente sul pdf */

					File fileTemp = doc.getTempFile();

					if (fileTemp == null) {
						Documento docBlob = new Documento(doc.getPrgDocumento());
						docBlob.selectExtBlob();
						fileTemp = docBlob.getTempFile();
					}

					File newFile = stomper.aggiungi_timbro(numProt, fileTemp, doc, codRegione);
					/* Salvataggio file i insert sul DB */
					doc.setTempFile(newFile);
					monitor3.stop();
				}
				monitor4 = MonitorFactory.start("RDS_DBLocale: [ inserisciBlob ]");
				doc.inserisciBlob(tex);
				/* Update sul DB del nuovo numProt */
				doc.aggiornaSoloNumProt(tex);
				monitor4.stop();

			}

			aggiornaProtocollo(doc, tex);
			// end transazione

		} else {
			doc.setNumProtInserito(null);
			/* Salvataggio file i insert sul DB */
			doc.inserisciBlob(tex);
		}
	}

	private void leggiProtocollo(Documento doc, TransactionQueryExecutor tex) throws Exception {
		if (doc.getNumProtInserito() != null && doc.getNumProtocollo() != null
				&& doc.getNumProtInserito().equals(doc.getNumProtocollo())) {
			// in caso di aggiornamento del documento protocollato non bisogna
			// andare a leggere il numero di protocollo.
			// Da notare che in fase di lettura del documento
			// "Documento.loadDocument()" vendono valorizzati sia
			// il campo "numProtocollo" che "numProtInserito". Quindi se il
			// documento letto e' protocollato entrambi i campi
			// saranno valorizzati con lo stesso numero. Se non protocollato
			// saranno entrabi null.
			return;
		} else if (!doc.isProtPosticipated()) {
			doc.leggiNewNumProt(tex);
		} else {
			doc.setNumProtInserito(null);
		}
		/*
		 * TODO DOCAREA questo codice e' piu' pulito ma e' da verificare
		 * 
		 * Object[] objS = new Object[1]; objS[0] = this.doc.getNumAnnoProt(); SourceBean rowsS = (SourceBean)
		 * tex.executeQuery("GET_KEYLOCK_NUMPR", objS, "SELECT");
		 * 
		 * if (rowsS != null) { // Se trovato, recupero num prot. corrente
		 * this.doc.setNumKeyLock(SourceBeanUtils.getAttrBigDecimal(rowsS, "ROW.NUMKLOPROTOCOLLO"));
		 * this.doc.setNumProtocollo(SourceBeanUtils.getAttrBigDecimal(rowsS, "ROW.NUMPROTOCOLLO"));
		 * this.doc.setNumProtInserito(this.doc.getNumProtocollo()); } else { // Non esiste alcun record associato a
		 * quell'anno // E' il primo documento che protocolliamo in quell'anno: // inseriamo il nuovo record con
		 * protocollo "1". Object[] objI = new Object[3];
		 * 
		 * this.doc.setNumKeyLock(new BigDecimal(1D)); this.doc.setNumProtocollo(new BigDecimal(1D));
		 * this.doc.setNumProtInserito(this.doc.getNumProtocollo());
		 * 
		 * objI[0] = this.doc.getNumKeyLock(); objI[1] = this.doc.getNumProtocollo(); objI[2] =
		 * this.doc.getNumAnnoProt(); Boolean resultI = (Boolean) tex.executeQuery("INSERT_AM_PROTOCOLLO", objI,
		 * "INSERT"); if (!resultI.booleanValue()) { throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "inserimento
		 * INSERT_AM_PROTOCOLLO non possibile"); } }
		 */
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.tempuri.DOCAREAProtoIFACE#registra()
	 */
	public void aggiornaProtocollo(Documento doc, TransactionQueryExecutor tex) throws EMFInternalError {
		doc.unlockNewNumProt(tex);
		/*
		 * Object[] obj = new Object[3];
		 * 
		 * obj[0] = this.doc.getNumProtInserito(); obj[1] = this.doc.getNumKeyLock().add(new BigDecimal(1D)); //
		 * aggiunge "+1" obj[2] = this.doc.getNumAnnoProt();
		 * 
		 * Boolean result = (Boolean) this.tex.executeQuery("UPDATE_AM_PROTOCOLLO", obj, "UPDATE"); if
		 * (!result.booleanValue()) { throw new EMFInternalError( EMFErrorSeverity.BLOCKING, "Impossibile aggiornare
		 * AM_PROTOCOLLO, probabile concorrenza."); }
		 */
	}

}
