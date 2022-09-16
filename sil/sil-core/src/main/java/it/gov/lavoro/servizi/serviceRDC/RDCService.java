/**
 * RDCService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.lavoro.servizi.serviceRDC;

public interface RDCService extends javax.xml.rpc.Service {
	public java.lang.String getRDCAddress();

	public it.gov.lavoro.servizi.serviceRDC.RDC getRDC() throws javax.xml.rpc.ServiceException;

	public it.gov.lavoro.servizi.serviceRDC.RDC getRDC(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
