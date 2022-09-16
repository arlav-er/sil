/*
 * Created on 3-nov-06
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.engiweb.framework.util;

import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;

/**
 * @author vuoto
 * 
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class QueryExecutorObjectExt extends QueryExecutorObject {

	public Object execExt() throws EMFInternalError, EMFUserError {
		return execWithException();
	}

	/**
	 * @deprecated
	 */
	public Object exec() {
		return super.exec();
	}

}
