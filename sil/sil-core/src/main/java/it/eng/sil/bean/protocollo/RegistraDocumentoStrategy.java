/*
 * Created on Feb 28, 2007
 */
package it.eng.sil.bean.protocollo;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.bean.Documento;

/**
 * @author Savino
 */
public interface RegistraDocumentoStrategy {
	public void registra(Documento doc, TransactionQueryExecutor tex) throws Exception;
}
