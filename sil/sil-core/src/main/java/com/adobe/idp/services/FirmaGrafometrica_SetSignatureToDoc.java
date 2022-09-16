/**
 * FirmaGrafometrica_SetSignatureToDoc.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.adobe.idp.services;

public interface FirmaGrafometrica_SetSignatureToDoc extends java.rmi.Remote {
	public void invoke(com.adobe.idp.services.BLOB in_doc, com.adobe.idp.services.XML in_xml_dati,
			javax.xml.rpc.holders.StringHolder numPages, javax.xml.rpc.holders.ShortHolder out_berror,
			com.adobe.idp.services.holders.BLOBHolder out_doc_pdf, javax.xml.rpc.holders.StringHolder out_error,
			javax.xml.rpc.holders.StringHolder t) throws java.rmi.RemoteException;
}
