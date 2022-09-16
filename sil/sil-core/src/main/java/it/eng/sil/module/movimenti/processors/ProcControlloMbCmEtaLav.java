/*
 * Creato il 8-nov-04
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.movimenti.processors;

import java.util.ArrayList;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.base.XMLObject;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.ControlloMbCmEtaLav;

/**
 * @author savino
 *
 *         Questo processor esegue il controllo su alcuni dati del lavoratore: mobilita', collocamento mirato, eta'. I
 *         primi tre in validazione massiva (generando un warning), mentre un ulteriore controllo sull'eta' e' bloccante
 *         se &lt; 15 e il tipo di assunzione e' diverso dallo spettacolo in tutti i contesti.
 */
public class ProcControlloMbCmEtaLav extends BaseProcessor {

	public ProcControlloMbCmEtaLav(String name, TransactionQueryExecutor txExec) {
		super(name, txExec);
	}

	public SourceBean processRecord(Map record) throws SourceBeanException {
		warnings = new ArrayList();
		nested = new ArrayList();
		//
		SourceBean request = new SourceBean("REQUEST");
		setRequest(request, record);
		SourceBean response = new SourceBean("RESPONSE");

		SourceBean ret = null;
		try {
			/*
			 * Viene utilizzato il modulo che esegue la stessa estrazione dei dati per il controllo nella pagina di
			 * inserimento generale di un movimento.
			 */
			ControlloMbCmEtaLav.ControlloDatiLav p = new ControlloMbCmEtaLav.ControlloDatiLav() {
				public SourceBean esegui(SourceBean request, SourceBean response, String stmName) {
					return doSelectWithStatement(request, response, stmName);
				}
			};
			p.perform(request, response);
			SourceBean row = (SourceBean) response.getAttribute("rows");
			ret = controllaDatiLav(row, record);

		} catch (Exception e) {
			logErr("Impossibile controllare i dati del lavoratore.", e);
			ret = createResponse(MessageCodes.General.OPERATION_FAIL, e.getMessage());
		}
		return ret;
	}

	/**
	 * Vedi commento della classe.
	 */
	private SourceBean controllaDatiLav(SourceBean row, Map record) throws Exception {
		SourceBean ret = null;
		boolean validazioneMassiva = false;
		if ("validazioneMassiva".equalsIgnoreCase((String) record.get("CONTEXT"))) {
			validazioneMassiva = true;
		}
		boolean createResponse = false;
		if (validazioneMassiva && row != null && row.containsAttribute("row.CODCMTIPOISCR")) {
			addWarning(MessageCodes.DatiLavoratore.IN_COLLOCAMENTO_MIRATO, "");
			createResponse = true;
		}
		if (validazioneMassiva && row != null && row.containsAttribute("row.CODMOBILITA")) {
			addWarning(MessageCodes.DatiLavoratore.IN_MOBILITA, "");
			createResponse = true;
		}
		if (validazioneMassiva && row != null && row.containsAttribute("row.CODSOSPENSIONE")) {
			addWarning(MessageCodes.DatiLavoratore.IN_MOBILITA_SOSPESO, "");
			createResponse = true;
		}
		if (row != null && (row.containsAttribute("row.etaMinoreDi15") || row.containsAttribute("row.etaMinoreDi16"))) {
			String codTipoAss = record.get("CODTIPOASS") != null ? (String) record.get("CODTIPOASS") : "";
			int warCode = 0;
			int errCode = 0;
			if (row.containsAttribute("row.etaMinoreDi15")) {
				boolean etaMin15 = row.getAttribute("row.etaMinoreDi15").equals("true");
				if ((etaMin15) && !(codTipoAss.equalsIgnoreCase("G.01.00") || codTipoAss.equalsIgnoreCase("G.02.00")
						|| codTipoAss.equalsIgnoreCase("G.03.00") || codTipoAss.equalsIgnoreCase("A.03.01")
						|| codTipoAss.equalsIgnoreCase("A.03.08")))
					warCode = MessageCodes.DatiLavoratore.ETA_LT_15_AND_AVV_SPETTACOLO;
				// codice di errore per lo spettacolo
				else if (etaMin15)
					warCode = MessageCodes.DatiLavoratore.ETA_LT_15;
			} else {
				// Modifica : Tutti i lavoratori assunti dal 01/09/2007 (compreso),
				// per poter lavorare devono avere almeno 16 anni
				if (!codTipoAss.equalsIgnoreCase("A.03.08")) {
					boolean etaMin16 = row.getAttribute("row.etaMinoreDi16").equals("true");
					if ((etaMin16) && !(codTipoAss.equalsIgnoreCase("G.01.00") || codTipoAss.equalsIgnoreCase("G.02.00")
							|| codTipoAss.equalsIgnoreCase("G.03.00") || codTipoAss.equalsIgnoreCase("A.03.01")))
						warCode = MessageCodes.DatiLavoratore.ETA_LT_16_AND_AVV_SPETTACOLO;
					// codice di errore per lo spettacolo
					else if (etaMin16)
						warCode = MessageCodes.DatiLavoratore.ETA_LT_16;
				}
			}

			if (validazioneMassiva && errCode == 0 && warCode > 0) {// il warning solo in validazione massiva
				addWarning(warCode, "");
				createResponse = true;
			} else if (errCode > 0) { // eta < 15 o eta < 16 and not lavoro di spettacolo (in ogni contesto)
				ret = createResponse(errCode, "");
			}
		}
		if (ret == null && createResponse)
			ret = createResponse();

		return ret;
	}

	public SourceBean doSelectWithStatement(SourceBean request, SourceBean response, String stmName) {
		Object params[] = getParams(stmName, request);

		SourceBean row = null;
		try {
			row = (SourceBean) txExec.executeQuery(stmName, params, "SELECT");
		} catch (EMFInternalError e) {
			logErr("Impossibile leggere le informazioni sul lavoratore (mobilita, collocamento mirato, eta)",
					(XMLObject) e);
		}
		return row;
	}

	/**
	 * Restituisce il parametri da utilizzare per la query. Se nella classe del modulo venisse aggiunta una query
	 * bisognera' aggiungere la costruzione dei suoi parametri qui.
	 */
	private Object[] getParams(String stmName, SourceBean request) {
		Object[] params = null;
		if (stmName.toUpperCase().equals("GET_INFO_MB_CM_ETA_LAV")) {
			params = new Object[] { request.getAttribute("cdnLavoratore") };
		} else if (stmName.toUpperCase().equals("GET_PROVINCIA_SIL")) {
			params = new Object[] {};
		} else if (stmName.toUpperCase().equals("GET_LAV_PROVINCIA")) {
			params = new Object[] { request.getAttribute("cdnLavoratore") };
		} else if (stmName.toUpperCase().equals("GET_LAV_COLLMIRATO")) {
			params = new Object[] { request.getAttribute("cdnLavoratore") };
		}
		return params;
	}

	private void setRequest(SourceBean request, Map record) throws SourceBeanException {
		if (record.get("CDNLAVORATORE") != null)
			request.setAttribute("CDNLAVORATORE", record.get("CDNLAVORATORE"));
		if (record.get("DATINIZIOMOV") != null)
			request.setAttribute("DATINIZIOMOV", record.get("DATINIZIOMOV"));
		if (record.get("CODTIPOMOV") != null)
			request.setAttribute("CODTIPOMOV", record.get("CODTIPOMOV"));
	}
}
