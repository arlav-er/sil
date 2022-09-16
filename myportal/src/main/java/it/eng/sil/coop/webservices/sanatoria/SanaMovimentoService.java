/**
 * SanaMovimentoService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.sanatoria;

public interface SanaMovimentoService extends javax.xml.rpc.Service {
    public java.lang.String getSanaMovimentoAddress();

    public it.eng.sil.coop.webservices.sanatoria.SanaMovimento getSanaMovimento() throws javax.xml.rpc.ServiceException;

    public it.eng.sil.coop.webservices.sanatoria.SanaMovimento getSanaMovimento(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
