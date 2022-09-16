/*
 * Creato il 22-giu-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.agenda;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dispatching.module.impl.DynamicStatementListModule;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import it.eng.sil.sms.ContattoSMS;
import it.eng.sil.sms.Sms;
import it.eng.sil.util.Utils;

/**
 * @author gritti
 * 
 * Per modificare il modello associato al commento di questo tipo generato,
 * aprire Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e
 * commenti
 */
public class AgendaSMSRecuperaContatto extends AbstractSimpleModule {
	
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AgendaSMSRecuperaContatto.class
			.getName());
	public void service(SourceBean request, SourceBean response) throws Exception {
		/** LOGGING **/
		String className = this.getClass().getName();	
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		_logger.warn(className + " Partenza invio sms con data :" +dateFormat.format(date));
		/** Fine LOGGING **/
		
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();

		SourceBean response_rows = new SourceBean("ROWS");

		String codcpi = "";
		String prgapp = "";
		String cdnlav = "";
		
		String prgStr ="";
		
		if(request.containsAttribute("SMSFILTRATI") && (request.getAttribute("SMSFILTRATI").toString().equals("1"))) {//provengo dall'invio massivo filtrati
			
			_logger.warn(className + " mod invio sms MASSIVO recupera dati con data :" + dateFormat.format(new Date()));
			
			SourceBean srcResponse = getResponseContainer().getServiceResponse();
			Vector<SourceBean> vettRis = srcResponse.getAttributeAsVector(("M_AGENDA_SMS_LISTALAVORATORI_FILTRATI.ROWS.ROW"));
		
			for (SourceBean recupera : vettRis) {
				SourceBean response_row = new SourceBean("ROW");
				response_row.setAttribute("CODCPI", recupera.getAttribute("CODCPI"));
				response_row.setAttribute("PRGAPPUNTAMENTO", recupera.getAttribute("PRGAPPUNTAMENTO"));
				response_row.setAttribute("CDNLAVORATORE",recupera.getAttribute("CDNLAVORATORE")+"");
				
				response_row.setAttribute("DATA", recupera.getAttribute("data"));
				response_row.setAttribute("ORARIO", StringUtils.getAttributeStrNotNull(recupera, "ORA"));
				response_row.setAttribute("DESCRIZIONECPI", StringUtils.getAttributeStrNotNull(recupera,
						"STRDESCRIZIONE"));

				response_row.setAttribute("STRCELL", Sms.cleanCellNumber(StringUtils.getAttributeStrNotNull(recupera,
						"STRCELL")));
				response_row.setAttribute("STRCOGNOME", StringUtils.getAttributeStrNotNull(recupera, "STRCOGNOME"));
				response_row.setAttribute("STRNOME", StringUtils.getAttributeStrNotNull(recupera, "STRNOME"));
				response_row.setAttribute("STRCODICEFISCALE", StringUtils.getAttributeStrNotNull(recupera,
						"codicefiscale"));
				
				response_row.setAttribute("CODSERVIZIO", StringUtils.getAttributeStrNotNull(recupera, "CODSERVIZIO"));
				response_row.setAttribute("TELCPI", StringUtils.getAttributeStrNotNull(recupera,
						"TELCPI"));
				response_row.setAttribute("TELRDCCPI", StringUtils.getAttributeStrNotNull(recupera,
						"TELRDCCPI"));
				response_row.setAttribute("INDIRIZZOCPI", StringUtils.getAttributeStrNotNull(recupera,
						"INDIRIZZOCPI"));
				response_row.setAttribute("FLGINVIOSMS", StringUtils.getAttributeStrNotNull(recupera,
						"FLGINVIOSMS"));
				response_rows.setAttribute(response_row);
			}
		
			/* commentata forse inutile con nuova gestione !!!
			SourceBean tmp = null;
			for (SourceBean object : vettRis) {
				tmp =  object;				
				if(prgStr.length()>0) { 
					prgStr += ","; 
				}
				prgStr += tmp.getAttribute("SMSKEY");	  			
			}
			*/
			_logger.warn(className + " mod invio sms MASSIVO fine recupera dati con data :" + dateFormat.format(new Date()));
		}else {
			 prgStr = (String) request.getAttribute("SELEZIONE");
		 }
		
		
		// ciclo per recuperare i singoli appuntamento
	if(!request.containsAttribute("SMSFILTRATI") || (request.containsAttribute("SMSFILTRATI") && !request.getAttribute("SMSFILTRATI").toString().equals("1"))) {//se non provengo dall'invio massivo filtrati
		_logger.warn(className + " mod invio sms recupera dati con data :" + dateFormat.format(new Date()));
		String prg = null;
		int virgola;
		boolean ret = true;
		while (prgStr.length() > 0) {
			virgola = prgStr.indexOf(',');
			if (virgola != -1) {
				prg = prgStr.substring(0, virgola);
				prgStr = prgStr.substring(virgola + 1, prgStr.length()); // cancello
																			// il
																			// progressivo
																			// trovato
			} else { // prgStr è >0, ma non abbiamo virgole. Ciò significa
						// necessariamente
				prg = prgStr; // che la stringa è composta da un solo codice.
				prgStr = ""; // ora che abbiamo consumato l'ultimo codice
								// impostiamo prgStr a null
			}
			if (prg != null) {

				// tokenizer
				StringTokenizer st = new StringTokenizer(prg, "|");
				if (st.hasMoreTokens()) {
					codcpi = st.nextToken();
				}
				if (st.hasMoreTokens()) {
					prgapp = st.nextToken();
				}
				if (st.hasMoreTokens()) {
					cdnlav = st.nextToken();
				}

				request.setAttribute("CODCPI", codcpi);
				request.setAttribute("PRGAPPUNTAMENTO", prgapp);
				request.setAttribute("CDNLAVORATORE", cdnlav);

				// query per ottenere parametri per l'sms
				this.setSectionQuerySelect("QUERY_SELECT_DATI");
				SourceBean recupera = doSelect(request, response, false);
				request.delAttribute("CODCPI");
				request.delAttribute("PRGAPPUNTAMENTO");
				request.delAttribute("CDNLAVORATORE");

				// SourceBean response_row=new SourceBean("ROW");

				SourceBean response_row = new SourceBean("ROW");

				response_row.setAttribute("CODCPI", codcpi);
				response_row.setAttribute("PRGAPPUNTAMENTO", prgapp);
				response_row.setAttribute("CDNLAVORATORE", cdnlav);
				response_row.setAttribute("DATA", StringUtils.getAttributeStrNotNull(recupera, "ROW.DATA"));
				response_row.setAttribute("ORARIO", StringUtils.getAttributeStrNotNull(recupera, "ROW.ORA"));
				response_row.setAttribute("DESCRIZIONECPI", StringUtils.getAttributeStrNotNull(recupera,
						"ROW.STRDESCRIZIONE"));

				response_row.setAttribute("STRCELL", Sms.cleanCellNumber(StringUtils.getAttributeStrNotNull(recupera,
						"ROW.STRCELL")));
				response_row.setAttribute("STRCOGNOME", StringUtils.getAttributeStrNotNull(recupera, "ROW.STRCOGNOME"));
				response_row.setAttribute("STRNOME", StringUtils.getAttributeStrNotNull(recupera, "ROW.STRNOME"));
				response_row.setAttribute("STRCODICEFISCALE", StringUtils.getAttributeStrNotNull(recupera,
						"ROW.STRCODICEFISCALE"));
				
				response_row.setAttribute("CODSERVIZIO", StringUtils.getAttributeStrNotNull(recupera, "ROW.CODSERVIZIO"));
				response_row.setAttribute("TELCPI", StringUtils.getAttributeStrNotNull(recupera,
						"ROW.TELCPI"));
				response_row.setAttribute("TELRDCCPI", StringUtils.getAttributeStrNotNull(recupera,
						"ROW.TELRDCCPI"));
				response_row.setAttribute("INDIRIZZOCPI", StringUtils.getAttributeStrNotNull(recupera,
						"ROW.INDIRIZZOCPI"));
				response_row.setAttribute("FLGINVIOSMS", StringUtils.getAttributeStrNotNull(recupera,
						"ROW.FLGINVIOSMS"));
				response_rows.setAttribute(response_row);

			}

			// } catch (Exception ex) {
			// reportOperation.reportFailure(idFail);
			// ret = false;
			// }
			// }
			// }
			// if (ret) {
			// reportOperation.reportSuccess(idSuccess);
			// }
			// reportSuccess(reportOperation);

		  }
		_logger.warn(className + " mod invio sms fine recupera dati con data :" + dateFormat.format(new Date()));    
	}

		ContattoSMS contattoSms = new ContattoSMS();
		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		String lenghtMaxSms = (String) request.getAttribute("MAXLENGTHSMS");
		if (lenghtMaxSms != null && !lenghtMaxSms.equals("")) {
			contattoSms.setMaxLength(new Integer(lenghtMaxSms).intValue());
		}

		String invioPers = (String) request.getAttribute("INVIOPERS");
		if (invioPers!=null && "true".equals(invioPers)) {
			contattoSms.setTxtcontatto((String) request.getAttribute("TestoSms"));
			
		}
		 
		SourceBean nonInviatiSB = contattoSms.creaPerPromemoria(response_rows, user);
		response.setAttribute(response_rows);
		response.setAttribute(nonInviatiSB);
		_logger.warn(className + " fine invio sms con data :" +dateFormat.format(date));
	}

	private void setKeyinRequest(Object prg, SourceBean request) throws Exception {
		if (request.getAttribute("SELEZIONE") != null) {
			request.delAttribute("SELEZIONE");
		}
		request.setAttribute("SELEZIONE", prg);
	}
	// creazione ed invio dell'sms

}
