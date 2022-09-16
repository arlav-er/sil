/**
 * AcquisizioneFirmaGrafometrica.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.firmagrafometrica;

public interface AcquisizioneFirmaGrafometrica extends java.rmi.Remote {
	public it.eng.sil.coop.webservices.firmagrafometrica.XML uploadDocFirmato(
			it.eng.sil.coop.webservices.firmagrafometrica.BLOB in_document_pdf, java.lang.String in_string_pdfname,
			java.lang.String in_string_xmlparams) throws java.rmi.RemoteException;
}
