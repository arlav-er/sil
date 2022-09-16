package it.eng.sil.module.patto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dispatching.service.DefaultRequestContext;
import com.engiweb.framework.error.EMFErrorHandler;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageAppender;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.sil.Values;
import it.eng.sil.module.AbstractSimpleModule;

public class ChiusuraDidMultipla extends AbstractSimpleModule {

	private static final long serialVersionUID = 3134930323114663706L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ChiusuraDidMultipla.class.getName());

	public void service(SourceBean request, SourceBean response) throws SourceBeanException {

		List<String> codiciFiscaliInErrorePerPresenzaDoppiaDid = new ArrayList<String>();
		List<String> codiciFiscaliInErrorePerAssenzaDid = new ArrayList<String>();
		List<String> codiciFiscaliInErroreDuranteChiusuraDid = new ArrayList<String>();
		List<String> codiciFiscaliSuccessoChiusuraDid = new ArrayList<String>();

		Vector<String> azioniConcordate = request.getAttributeAsVector("checkbox_azione_concordata");

		Vector dids = null;
		Object params[];
		SourceBean didSb = null, did = null;
		SourceBean lavSb = null;
		String strCodiceFiscale = null;

		if (azioniConcordate.size() == 0) {
			MessageAppender.appendMessage(response,
					MessageCodes.ChiusuraDidMultipla.WARNING_NESSUNA_SELEZIONE_EFFETTUATA, null);
			return;
		}

		if ("".equals(SourceBeanUtils.getAttrStrNotNull(request, "datfine"))
				|| "".equals(SourceBeanUtils.getAttrStrNotNull(request, "codmotivofineatto"))) {

			MessageAppender.appendMessage(response, MessageCodes.ChiusuraDidMultipla.PARAMETRI_MANCANTI, null);
			return;

		}

		for (String azioneConcordata : azioniConcordate) {

			Object lavParams[] = new Object[2];
			String[] pkAzioneConcordata = azioneConcordata.split("-");
			lavParams[0] = pkAzioneConcordata[0];
			lavParams[1] = pkAzioneConcordata[1];
			SourceBean cdnLavSb = (SourceBean) QueryExecutor.executeQuery("GET_CDNLAVORATORE_DA_PERCORSO", lavParams,
					"SELECT", Values.DB_SIL_DATI);
			BigDecimal cdnLavoratore = (BigDecimal) cdnLavSb.getAttribute("ROW.cdnLavoratore");

			// ricerca did lavoratore
			params = new Object[1];
			params[0] = cdnLavoratore;
			didSb = (SourceBean) QueryExecutor.executeQuery("GET_DID_LAVORATORE_CLOSEDID", params, "SELECT",
					Values.DB_SIL_DATI);
			dids = didSb.getAttributeAsVector("ROW");

			lavSb = (SourceBean) QueryExecutor.executeQuery("GET_CF_LAVORATORE_CLOSEDID", params, "SELECT",
					Values.DB_SIL_DATI);
			strCodiceFiscale = lavSb.getAttribute("ROW.strcodicefiscale").toString();

			if (dids.size() == 0) {

				// nessuna did trovata
				if (!codiciFiscaliInErrorePerAssenzaDid.contains(strCodiceFiscale)
						&& !codiciFiscaliSuccessoChiusuraDid.contains(strCodiceFiscale)) {
					codiciFiscaliInErrorePerAssenzaDid.add(strCodiceFiscale);
				}
				continue;

			} else if (dids.size() > 1) {

				// trovata più di una did
				if (!codiciFiscaliInErrorePerPresenzaDoppiaDid.contains(strCodiceFiscale)) {
					codiciFiscaliInErrorePerPresenzaDoppiaDid.add(strCodiceFiscale);
				}
				continue;

			}

			did = (SourceBean) dids.get(0);
			request.updAttribute("prgdichdisponibilita",
					SourceBeanUtils.getAttrStrNotNull(did, "prgdichdisponibilita"));
			request.updAttribute("datdichiarazione", SourceBeanUtils.getAttrStrNotNull(did, "datdichiarazione"));
			request.updAttribute("cdnlavoratore", SourceBeanUtils.getAttrStrNotNull(did, "cdnlavoratore"));
			request.updAttribute("codtipodichdisp", SourceBeanUtils.getAttrStrNotNull(did, "codtipodichdisp"));
			request.updAttribute("codultimocontratto", SourceBeanUtils.getAttrStrNotNull(did, "codultimocontratto"));
			request.updAttribute("datscadconferma", SourceBeanUtils.getAttrStrNotNull(did, "datscadconferma"));
			request.updAttribute("datscaderogazservizi",
					SourceBeanUtils.getAttrStrNotNull(did, "datscaderogazservizi"));
			request.updAttribute("codstatoatto", SourceBeanUtils.getAttrStrNotNull(did, "codstatoatto"));
			request.updAttribute("strnote", SourceBeanUtils.getAttrStrNotNull(did, "strnote"));
			request.updAttribute("numklodichdisp", SourceBeanUtils.getAttrStrNotNull(did, "numklodichdisp"));
			// i seguenti attributi sono automaticamente
			// valorizzati durante l'inserimento del patto
			// nel caso in cui dataFine <= dataAttuale
			request.delAttribute("STRNOTESTATOOCCUPAZ");
			request.delAttribute("FLGPENSIONATO");

			try {
				chiudiDid(request, response);
				// verifica se si sono verificati
				// degli errori durante
				// la chiusura della did
				boolean isPresenteErroreChiusuraDidCorrente = false;
				EMFErrorHandler errorHandler = getErrorHandler();
				if (errorHandler != null) {
					if (errorHandler.getErrors().size() > 0) {
						isPresenteErroreChiusuraDidCorrente = true;
						errorHandler.clear();
						if (!codiciFiscaliInErroreDuranteChiusuraDid.contains(strCodiceFiscale)) {
							codiciFiscaliInErroreDuranteChiusuraDid.add(strCodiceFiscale);
						}
					}
				}

				if (!isPresenteErroreChiusuraDidCorrente) {
					codiciFiscaliSuccessoChiusuraDid.add(strCodiceFiscale);
				}

			} catch (Exception e) {
				_logger.error(e.getMessage());
				codiciFiscaliInErroreDuranteChiusuraDid.add(strCodiceFiscale);
			}

		}

		// elimina i messaggi inseriti dal modulo SaveDispo
		// richiamato dal metodo chiudiDid
		response.delAttribute("USER_MESSAGE");

		// MessageAppender.appendMessage(response, MessageCodes.General.OPERATION_SUCCESS, null);

		if (codiciFiscaliSuccessoChiusuraDid.size() > 0) {
			Vector<String> messageParams = new Vector<String>();
			messageParams.add(getCodiciFiscali(codiciFiscaliSuccessoChiusuraDid));
			MessageAppender.appendMessage(response, MessageCodes.ChiusuraDidMultipla.SUCCESS_CHIUSURA_DID,
					messageParams);
		}

		if (codiciFiscaliInErrorePerAssenzaDid.size() > 0) {
			Vector<String> messageParams = new Vector<String>();
			messageParams.add(getCodiciFiscali(codiciFiscaliInErrorePerAssenzaDid));
			MessageAppender.appendMessage(response, MessageCodes.ChiusuraDidMultipla.WARNING_NON_PRESENTE_DID,
					messageParams);
		}

		if (codiciFiscaliInErrorePerPresenzaDoppiaDid.size() > 0) {
			Vector<String> messageParams = new Vector<String>();
			messageParams.add(getCodiciFiscali(codiciFiscaliInErrorePerPresenzaDoppiaDid));
			MessageAppender.appendMessage(response, MessageCodes.ChiusuraDidMultipla.WARNING_DOPPIA_DID, messageParams);
		}

		if (codiciFiscaliInErroreDuranteChiusuraDid.size() > 0) {
			Vector<String> messageParams = new Vector<String>();
			messageParams.add(getCodiciFiscali(codiciFiscaliInErroreDuranteChiusuraDid));
			MessageAppender.appendMessage(response, MessageCodes.ChiusuraDidMultipla.ERRORE_GENERICO_CHIUSURA_DID,
					messageParams);
		}

	}

	protected void chiudiDid(SourceBean request, SourceBean response) throws Exception {

		SaveDispo saveDispo = new SaveDispo();
		saveDispo.init(getConfig());
		saveDispo.setRequestContext(new DefaultRequestContext(getRequestContainer(), getResponseContainer()));
		saveDispo.service(request, response);

	}

	protected String getCodiciFiscali(List<String> codiciFiscali) {

		String codiciFiscaliString = "";
		int numCodiciFiscali = codiciFiscali.size();
		for (int i = 0; i < numCodiciFiscali; i++) {
			codiciFiscaliString += codiciFiscali.get(i);
			if (i < (numCodiciFiscali - 1)) {
				codiciFiscaliString += ", ";
			}
		}
		return codiciFiscaliString;

	}

}
