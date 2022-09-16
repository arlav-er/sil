/**
 * SupportPDFG_ProcessConvertToPdfA.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.adobe.idp.services;

public interface SupportPDFG_ProcessConvertToPdfA extends java.rmi.Remote {
	public void invoke(com.adobe.idp.services.BLOB inDoc, java.lang.Boolean in_bool_enableValidationPdfA,
			javax.xml.rpc.holders.BooleanHolder isPDFA, com.adobe.idp.services.holders.BLOBHolder outDoc,
			com.adobe.idp.services.holders.XMLHolder out_xml_output) throws java.rmi.RemoteException;
}
