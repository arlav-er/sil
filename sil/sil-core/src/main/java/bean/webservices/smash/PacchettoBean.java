/**
 * PacchettoBean.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package bean.webservices.smash;

public class PacchettoBean  implements java.io.Serializable {
    private int numeroSmsResidui;

    private int flagOnOffNet;

    private int numeroSmsIniziali;

    private java.lang.String idPacchetto;

    private int cessato;

    private java.lang.String dataScadenza;

    public PacchettoBean() {
    }

    public PacchettoBean(
           int numeroSmsResidui,
           int flagOnOffNet,
           int numeroSmsIniziali,
           java.lang.String idPacchetto,
           int cessato,
           java.lang.String dataScadenza) {
           this.numeroSmsResidui = numeroSmsResidui;
           this.flagOnOffNet = flagOnOffNet;
           this.numeroSmsIniziali = numeroSmsIniziali;
           this.idPacchetto = idPacchetto;
           this.cessato = cessato;
           this.dataScadenza = dataScadenza;
    }


    /**
     * Gets the numeroSmsResidui value for this PacchettoBean.
     * 
     * @return numeroSmsResidui
     */
    public int getNumeroSmsResidui() {
        return numeroSmsResidui;
    }


    /**
     * Sets the numeroSmsResidui value for this PacchettoBean.
     * 
     * @param numeroSmsResidui
     */
    public void setNumeroSmsResidui(int numeroSmsResidui) {
        this.numeroSmsResidui = numeroSmsResidui;
    }


    /**
     * Gets the flagOnOffNet value for this PacchettoBean.
     * 
     * @return flagOnOffNet
     */
    public int getFlagOnOffNet() {
        return flagOnOffNet;
    }


    /**
     * Sets the flagOnOffNet value for this PacchettoBean.
     * 
     * @param flagOnOffNet
     */
    public void setFlagOnOffNet(int flagOnOffNet) {
        this.flagOnOffNet = flagOnOffNet;
    }


    /**
     * Gets the numeroSmsIniziali value for this PacchettoBean.
     * 
     * @return numeroSmsIniziali
     */
    public int getNumeroSmsIniziali() {
        return numeroSmsIniziali;
    }


    /**
     * Sets the numeroSmsIniziali value for this PacchettoBean.
     * 
     * @param numeroSmsIniziali
     */
    public void setNumeroSmsIniziali(int numeroSmsIniziali) {
        this.numeroSmsIniziali = numeroSmsIniziali;
    }


    /**
     * Gets the idPacchetto value for this PacchettoBean.
     * 
     * @return idPacchetto
     */
    public java.lang.String getIdPacchetto() {
        return idPacchetto;
    }


    /**
     * Sets the idPacchetto value for this PacchettoBean.
     * 
     * @param idPacchetto
     */
    public void setIdPacchetto(java.lang.String idPacchetto) {
        this.idPacchetto = idPacchetto;
    }


    /**
     * Gets the cessato value for this PacchettoBean.
     * 
     * @return cessato
     */
    public int getCessato() {
        return cessato;
    }


    /**
     * Sets the cessato value for this PacchettoBean.
     * 
     * @param cessato
     */
    public void setCessato(int cessato) {
        this.cessato = cessato;
    }


    /**
     * Gets the dataScadenza value for this PacchettoBean.
     * 
     * @return dataScadenza
     */
    public java.lang.String getDataScadenza() {
        return dataScadenza;
    }


    /**
     * Sets the dataScadenza value for this PacchettoBean.
     * 
     * @param dataScadenza
     */
    public void setDataScadenza(java.lang.String dataScadenza) {
        this.dataScadenza = dataScadenza;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PacchettoBean)) return false;
        PacchettoBean other = (PacchettoBean) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.numeroSmsResidui == other.getNumeroSmsResidui() &&
            this.flagOnOffNet == other.getFlagOnOffNet() &&
            this.numeroSmsIniziali == other.getNumeroSmsIniziali() &&
            ((this.idPacchetto==null && other.getIdPacchetto()==null) || 
             (this.idPacchetto!=null &&
              this.idPacchetto.equals(other.getIdPacchetto()))) &&
            this.cessato == other.getCessato() &&
            ((this.dataScadenza==null && other.getDataScadenza()==null) || 
             (this.dataScadenza!=null &&
              this.dataScadenza.equals(other.getDataScadenza())));
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
        _hashCode += getNumeroSmsResidui();
        _hashCode += getFlagOnOffNet();
        _hashCode += getNumeroSmsIniziali();
        if (getIdPacchetto() != null) {
            _hashCode += getIdPacchetto().hashCode();
        }
        _hashCode += getCessato();
        if (getDataScadenza() != null) {
            _hashCode += getDataScadenza().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PacchettoBean.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("java:smash.webservices.bean", "PacchettoBean"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numeroSmsResidui");
        elemField.setXmlName(new javax.xml.namespace.QName("", "numeroSmsResidui"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("flagOnOffNet");
        elemField.setXmlName(new javax.xml.namespace.QName("", "flagOnOffNet"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numeroSmsIniziali");
        elemField.setXmlName(new javax.xml.namespace.QName("", "numeroSmsIniziali"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("idPacchetto");
        elemField.setXmlName(new javax.xml.namespace.QName("", "idPacchetto"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cessato");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cessato"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dataScadenza");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dataScadenza"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
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
