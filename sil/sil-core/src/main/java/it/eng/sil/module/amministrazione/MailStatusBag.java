/*
 * Creato il 23-nov-04
 * Author: vuoto
 * 
 */
package it.eng.sil.module.amministrazione;

import java.util.List;

/**
 * @author vuoto
 * 
 */
public class MailStatusBag {
	private String email;
	private String cpi;
	private String provincia;
	private String msg;

	private List fileList;
	private boolean OK;

	/**
	 * @return
	 */
	public String getCpi() {
		return cpi;
	}

	/**
	 * @return
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @return
	 */
	public String getProvincia() {
		return provincia;
	}

	/**
	 * @param string
	 */
	public void setCpi(String string) {
		cpi = string;
	}

	/**
	 * @param string
	 */
	public void setEmail(String string) {
		email = string;
	}

	/**
	 * @param string
	 */
	public void setProvincia(String string) {
		provincia = string;
	}

	/**
	 * @return
	 */
	public List getFileList() {
		return fileList;
	}

	/**
	 * @param list
	 */
	public void setFileList(List list) {
		fileList = list;
	}

	/**
	 * @return
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @param string
	 */
	public void setMsg(String string) {
		msg = string;
	}

	/**
	 * @return
	 */
	public boolean isOK() {
		return OK;
	}

	/**
	 * @param b
	 */
	public void setOK(boolean b) {
		OK = b;
	}

}
