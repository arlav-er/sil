package com.engiweb.framework.dbaccess.pool;

import com.engiweb.framework.error.EMFInternalError;

/**
 * @author Bernabei Angelo
 * @version 1.0
 */
public interface IDecriptAlgorithm {
	/**
	 * Interfaccia implementata dalla classe che decifra la password
	 * 
	 * @return String: Passord in chiaro
	 * @param String:
	 *            Password cifrata
	 */
	public String decipher(String psw) throws EMFInternalError;

}
