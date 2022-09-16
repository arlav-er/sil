/**
 * ArchivioBean.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package bean.webservices.smash;

public class ArchivioBean  implements java.io.Serializable {
    private int numDestOffNet;

    private int utenzeRaggiunte;

    private java.lang.String referente;

    private int utenzeNonRaggiunte;

    private int lunghezzaMessaggio;

    private java.lang.String listaDestinatari;

    private java.lang.String dataInvio;

    private java.lang.String testoMessaggio;

    private int numDestOnNet;

    public ArchivioBean() {
    }

    public ArchivioBean(
           int numDestOffNet,
           int utenzeRaggiunte,
           java.lang.String referente,
           int utenzeNonRaggiunte,
           int lunghezzaMessaggio,
           java.lang.String listaDestinatari,
           java.lang.String dataInvio,
           java.lang.String testoMessaggio,
           int numDestOnNet) {
           this.numDestOffNet = numDestOffNet;
           this.utenzeRaggiunte = utenzeRaggiunte;
           this.referente = referente;
           this.utenzeNonRaggiunte = utenzeNonRaggiunte;
           this.lunghezzaMessaggio = lunghezzaMessaggio;
           this.listaDestinatari = listaDestinatari;
           this.dataInvio = dataInvio;
           this.testoMessaggio = testoMessaggio;
           this.numDestOnNet = numDestOnNet;
    }


    /**
     * Gets the numDestOffNet value for this ArchivioBean.
     * 
     * @return numDestOffNet
     */
    public int getNumDestOffNet() {
        return numDestOffNet;
    }


    /**
     * Sets the numDestOffNet value for this ArchivioBean.
     * 
     * @param numDestOffNet
     */
    public void setNumDestOffNet(int numDestOffNet) {
        this.numDestOffNet = numDestOffNet;
    }


    /**
     * Gets the utenzeRaggiunte value for this ArchivioBean.
     * 
     * @return utenzeRaggiunte
     */
    public int getUtenzeRaggiunte() {
        return utenzeRaggiunte;
    }


    /**
     * Sets the utenzeRaggiunte value for this ArchivioBean.
     * 
     * @param utenzeRaggiunte
     */
    public void setUtenzeRaggiunte(int utenzeRaggiunte) {
        this.utenzeRaggiunte = utenzeRaggiunte;
    }


    /**
     * Gets the referente value for this ArchivioBean.
     * 
     * @return referente
     */
    public java.lang.String getReferente() {
        return referente;
    }


    /**
     * Sets the referente value for this ArchivioBean.
     * 
     * @param referente
     */
    public void setReferente(java.lang.String referente) {
        this.referente = referente;
    }


    /**
     * Gets the utenzeNonRaggiunte value for this ArchivioBean.
     * 
     * @return utenzeNonRaggiunte
     */
    public int getUtenzeNonRaggiunte() {
        return utenzeNonRaggiunte;
    }


    /**
     * Sets the utenzeNonRaggiunte value for this ArchivioBean.
     * 
     * @param utenzeNonRaggiunte
     */
    public void setUtenzeNonRaggiunte(int utenzeNonRaggiunte) {
        this.utenzeNonRaggiunte = utenzeNonRaggiunte;
    }


    /**
     * Gets the lunghezzaMessaggio value for this ArchivioBean.
     * 
     * @return lunghezzaMessaggio
     */
    public int getLunghezzaMessaggio() {
        return lunghezzaMessaggio;
    }


    /**
     * Sets the lunghezzaMessaggio value for this ArchivioBean.
     * 
     * @param lunghezzaMessaggio
     */
    public void setLunghezzaMessaggio(int lunghezzaMessaggio) {
        this.lunghezzaMessaggio = lunghezzaMessaggio;
    }


    /**
     * Gets the listaDestinatari value for this ArchivioBean.
     * 
     * @return listaDestinatari
     */
    public java.lang.String getListaDestinatari() {
        return listaDestinatari;
    }


    /**
     * Sets the listaDestinatari value for this ArchivioBean.
     * 
     * @param listaDestinatari
     */
    public void setListaDestinatari(java.lang.String listaDestinatari) {
        this.listaDestinatari = listaDestinatari;
    }


    /**
     * Gets the dataInvio value for this ArchivioBean.
     * 
     * @return dataInvio
     */
    public java.lang.String getDataInvio() {
        return dataInvio;
    }


    /**
     * Sets the dataInvio value for this ArchivioBean.
     * 
     * @param dataInvio
     */
    public void setDataInvio(java.lang.String dataInvio) {
        this.dataInvio = dataInvio;
    }


    /**
     * Gets the testoMessaggio value for this ArchivioBean.
     * 
     * @return testoMessaggio
     */
    public java.lang.String getTestoMessaggio() {
        return testoMessaggio;
    }


    /**
     * Sets the testoMessaggio value for this ArchivioBean.
     * 
     * @param testoMessaggio
     */
    public void setTestoMessaggio(java.lang.String testoMessaggio) {
        this.testoMessaggio = testoMessaggio;
    }


    /**
     * Gets the numDestOnNet value for this ArchivioBean.
     * 
     * @return numDestOnNet
     */
    public int getNumDestOnNet() {
        return numDestOnNet;
    }


    /**
     * Sets the numDestOnNet value for this ArchivioBean.
     * 
     * @param numDestOnNet
     */
    public void setNumDestOnNet(int numDestOnNet) {
        this.numDestOnNet = numDestOnNet;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArchivioBean)) return false;
        ArchivioBean other = (ArchivioBean) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.numDestOffNet == other.getNumDestOffNet() &&
            this.utenzeRaggiunte == other.getUtenzeRaggiunte() &&
            ((this.referente==null && other.getReferente()==null) || 
             (this.referente!=null &&
              this.referente.equals(other.getReferente()))) &&
            this.utenzeNonRaggiunte == other.getUtenzeNonRaggiunte() &&
            this.lunghezzaMessaggio == other.getLunghezzaMessaggio() &&
            ((this.listaDestinatari==null && other.getListaDestinatari()==null) || 
             (this.listaDestinatari!=null &&
              this.listaDestinatari.equals(other.getListaDestinatari()))) &&
            ((this.dataInvio==null && other.getDataInvio()==null) || 
             (this.dataInvio!=null &&
              this.dataInvio.equals(other.getDataInvio()))) &&
            ((this.testoMessaggio==null && other.getTestoMessaggio()==null) || 
             (this.testoMessaggio!=null &&
              this.testoMessaggio.equals(other.getTestoMessaggio()))) &&
            this.numDestOnNet == other.getNumDestOnNet();
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        _hashCode += getNumDestOffNet();
        _hashCode += getUtenzeRaggiunte();
        if (getReferente() != null) {
            _hashCode += getReferente().hashCode();
        }
        _hashCode += getUtenzeNonRaggiunte();
        _hashCode += getLunghezzaMessaggio();
        if (getListaDestinatari() != null) {
            _hashCode += getListaDestinatari().hashCode();
        }
        if (getDataInvio() != null) {
            _hashCode += getDataInvio().hashCode();
        }
        if (getTestoMessaggio() != null) {
            _hashCode += getTestoMessaggio().hashCode();
        }
        _hashCode += getNumDestOnNet();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ArchivioBean.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("java:smash.webservices.bean", "ArchivioBean"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numDestOffNet");
        elemField.setXmlName(new javax.xml.namespace.QName("", "numDestOffNet"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("utenzeRaggiunte");
        elemField.setXmlName(new javax.xml.namespace.QName("", "utenzeRaggiunte"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("referente");
        elemField.setXmlName(new javax.xml.namespace.QName("", "referente"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("utenzeNonRaggiunte");
        elemField.setXmlName(new javax.xml.namespace.QName("", "utenzeNonRaggiunte"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lunghezzaMessaggio");
        elemField.setXmlName(new javax.xml.namespace.QName("", "lunghezzaMessaggio"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("listaDestinatari");
        elemField.setXmlName(new javax.xml.namespace.QName("", "listaDestinatari"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dataInvio");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dataInvio"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("testoMessaggio");
        elemField.setXmlName(new javax.xml.namespace.QName("", "testoMessaggio"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numDestOnNet");
        elemField.setXmlName(new javax.xml.namespace.QName("", "numDestOnNet"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
