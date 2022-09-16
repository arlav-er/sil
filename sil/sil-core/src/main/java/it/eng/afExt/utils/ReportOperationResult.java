package it.eng.afExt.utils;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.service.DefaultRequestContext;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;

/**
 * Permette di semplificare le comunicazioni all'utente riguardanti il successo o il fallimento di una operazione, come
 * ad esempio il salvataggio di un dato.
 * 
 * E' sufficiente istanziare l'oggetto ReportOperationResult nel metodo <code>service<code> di un modulo e richiamare i
 * metodi reportSuccess o reportFailure nel caso rispettivamente di successo o fallimento di una operazione.
 * 
 * I messaggi inseriti verranno visualizzati, ad esempio, dalla pagina JSP del modulo utilizzando due speciali
 * Custom-Tag chiamati ShowErrors e ShowMessages contenuti nel package com.engiweb.framework.tags.
 * 
 * --Esempio--
 * 
 * Nel modulo si avrà un codice del tipo:
 * 
 * ReportOperationResult result= new ReportOperationResult(this, response); ... try { ... salva dato X ...
 * result.reportSuccess(ErrorCodes.DATO_X_SALVA_SUCCESSO); } catch (...) { ... gestione exception ...
 * result.reportFailure(ErrorCodes.DATO_X_SALVA_FALLITO); }
 * 
 * Mentre nella pagina JSP si includerà questo codice nel punto in cui si vuole vedere i messaggi visualizzati (alla
 * fine dell'esecuzione del modulo):
 * 
 * <font color="green"> <af:showMessages prefix="M_AnagMain"/> </font> <font color="red"> <af:showErrors /> </font>
 * 
 * Per vedere i possibili parametri passabili ai due tag e altre informazioni, si rimanda alla documentazione delle
 * classi com.engiweb.framework.tags.ShowMessages e com.engiweb.framework.tags.ShowErrors.
 * 
 * @author Corrado Vaccari
 */
public class ReportOperationResult {

	/**
	 * Modulo che utilizza l'istanza di questa classe.
	 */
	private DefaultRequestContext currentModule;
	/**
	 * Response del modulo.
	 */
	private SourceBean response;

	/**
	 * Classe del modulo.
	 */
	private String className;

	/**
	 * Costruttore.
	 * 
	 * @param currentModule
	 *            Modulo a cui la operazione si riferisce.
	 * @param response
	 *            Response che contiene i messaggi all'utente.
	 */
	public ReportOperationResult(DefaultRequestContext currentModule, SourceBean response) {

		this.currentModule = currentModule;
		this.response = response;
		this.className = currentModule.getClass().getName();
	}

	/**
	 * Informa l'utente del successo dell'operazione.
	 * 
	 * @param messageCode
	 *            Uno dei messaggi definiti nella classe ErrorCodes se <= 0 allora non viene inserito nulla.
	 */
	public void reportSuccess(int messageCode) {

		if (messageCode > 0)
			MessageAppender.appendMessage(this.response, messageCode);
	}

	/**
	 * Informa l'utente del fallimento dell'operazione, inserendo i dati dell'errore nel log del framework.
	 * 
	 * @param exp
	 *            Come definito nella classe ErrorCodes
	 * @param methodName
	 *            Nome del metodo in cui si è verificato l'errore
	 * @param problem
	 *            Breve descrizione dell'errore
	 */
	public void reportFailure(Exception exp, String methodName, String problem) {

		reportFailure(-1, exp, methodName, problem);
	}

	/**
	 * Informa l'utente del fallimento dell'operazione, inserendo il messaggio corrispondente al codice
	 * nell'error-handler del modulo.
	 * 
	 * @param messageCode
	 *            Uno dei messaggi definiti nella classe ErrorCodes
	 */
	public void reportFailure(int messageCode) {
		reportFailure(messageCode, null, "", "");
	}

	public void reportFailure(int messageCode, boolean existDettaglio, String dettaglio) {
		if (existDettaglio) {
			reportFailure(messageCode, null, "", "", true, dettaglio);
		} else {
			reportFailure(messageCode, null, "", "");
		}
	}

	/**
	 * Informa l'utente del fallimento dell'operazione. (Viene inserito un errore EMFUserError nell'error handler)
	 * 
	 * @param messageCode
	 *            Uno dei messaggi definiti nella classe ErrorCodes, si genera lo EMFUserError corrispondente; se <= 0
	 *            allora non viene inserito nulla.
	 * @param exp
	 *            Exception
	 * @param methodName
	 *            Nome del metodo in cui si è verificato l'errore
	 * @param problem
	 *            Breve descrizione dell'errore
	 * 
	 * @return l'errore inserito nell' error handler (Modificato da Savino 21/08/2006: ritorna l'errore inserito)
	 */
	public EMFUserError reportFailure(int messageCode, Exception exp, String methodName, String problem) {
		EMFUserError error = null;
		if (messageCode > 0) {
			error = new EMFUserError(EMFErrorSeverity.ERROR, messageCode);
			this.currentModule.getErrorHandler().addError(error);
		}
		LogUtils.logError(methodName, problem, exp, currentModule);
		return error;
	}

	public EMFUserError reportFailure(int messageCode, Exception exp, String methodName, String problem,
			boolean existDettaglio, String dettaglio) {
		EMFUserError error = null;
		if (messageCode > 0) {
			error = new EMFUserError(EMFErrorSeverity.ERROR, messageCode, existDettaglio, dettaglio);
			this.currentModule.getErrorHandler().addError(error);
		}
		LogUtils.logError(methodName, problem, exp, currentModule);
		return error;
	}

	/**
	 * 
	 */
	public void reportFailure(EMFUserError err, String methodName, String problem) {
		if (err.getCode() > 0) {
			this.currentModule.getErrorHandler().addError(err);
		}
		LogUtils.logError(methodName, problem, err, currentModule);
	}

	/**
	 * Informa l'utente di un WARNING nell'operazione. (Viene inserito un errore EMFUserError nell'error handler)
	 * 
	 * @param messageCode
	 *            Uno dei messaggi definiti nella classe ErrorCodes, si genera lo EMFUserError corrispondente; se <= 0
	 *            allora non viene inserito nulla.
	 * @param methodName
	 *            Nome del metodo in cui si è verificato il warning
	 * @param problem
	 *            Breve descrizione del warning
	 * 
	 *            (Aggiunto da Riccardi 02/10/2007)
	 */
	public void reportWarning(int messageCode, String methodName, String problem) {
		EMFUserError error = null;
		if (messageCode > 0) {
			error = new EMFUserError(EMFErrorSeverity.WARNING, messageCode);
			this.currentModule.getErrorHandler().addError(error);
		}
		LogUtils.logWarning(methodName, problem, currentModule);
	}

	/**
	 * Informa l'utente di un WARNING nell'operazione. (Viene inserito un errore EMFUserError nell'error handler)
	 * 
	 * @param messageCode
	 *            Uno dei messaggi definiti nella classe ErrorCodes, si genera lo EMFUserError corrispondente; se <= 0
	 *            allora non viene inserito nulla.
	 * @param methodName
	 *            Nome del metodo in cui si è verificato il warning
	 * @param problem
	 *            Breve descrizione del warning
	 * 
	 *            (Aggiunto da coticone 21/01/2008)
	 */
	public void reportWarning(int messageCode, String methodName, String problem, Vector params) {
		EMFUserError error = null;
		if (messageCode > 0) {
			error = new EMFUserError(EMFErrorSeverity.WARNING, messageCode, params);
			this.currentModule.getErrorHandler().addError(error);
		}
		LogUtils.logWarning(methodName, problem, currentModule);
	}

	/**
	 * Informa l'utente di un WARNING nell'operazione, inserendo il messaggio corrispondente al codice
	 * nell'error-handler del modulo.
	 * 
	 * @param messageCode
	 *            Uno dei messaggi definiti nella classe ErrorCodes
	 * 
	 *            (Aggiunto da Riccardi 02/10/2007)
	 */
	public void reportWarning(int messageCode) {

		reportWarning(messageCode, "", "");
	}

	/**
	 * Informa l'utente di un ERRORE nell'operazione
	 * 
	 * @param messageCode
	 *            Uno dei messaggi definiti nella classe ErrorCodes, si genera lo EMFUserError corrispondente; se <= 0
	 *            allora non viene inserito nulla.
	 * @param methodName
	 *            Nome del metodo in cui si e verificato il warning
	 * @param problem
	 *            Breve descrizione del warning
	 * @param params
	 *            parametri per il mesaggio
	 */
	public void reportFailure(int messageCode, String methodName, String problem, Vector<String> params) {
		EMFUserError error = null;
		if (messageCode > 0) {
			error = new EMFUserError(EMFErrorSeverity.ERROR, messageCode, params);
			this.currentModule.getErrorHandler().addError(error);
		}
		LogUtils.logWarning(methodName, problem, currentModule);
	}

	/**
	 * Informa l'utente di un ERRORE nell'operazione
	 * 
	 * @param messageCode
	 *            Uno dei messaggi definiti nella classe ErrorCodes, si genera lo EMFUserError corrispondente; se <= 0
	 *            allora non viene inserito nulla.
	 * @param methodName
	 *            Nome del metodo in cui si e verificato il warning
	 * @param problem
	 *            Breve descrizione del warning nel log
	 * @param dettaglio
	 *            Dettaglio dell'ERRORE visualizzato a video
	 * @param params
	 *            parametri per il mesaggio
	 */
	public void reportFailure(int messageCode, String methodName, String problem, String dettaglio,
			Vector<String> params) {
		EMFUserError error = null;
		if (messageCode > 0) {
			error = new EMFUserError(EMFErrorSeverity.ERROR, messageCode, params, problem, dettaglio);
			this.currentModule.getErrorHandler().addError(error);
		}
		LogUtils.logWarning(methodName, problem, currentModule);
	}

}
