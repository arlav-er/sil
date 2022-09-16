/**
 * RisultatoInvioBean.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package bean.webservices.smash;

public class RisultatoInvioBean  implements java.io.Serializable {
    private java.lang.String messaggio;

    private java.lang.String esito;

    private bean.webservices.smash.PacchettoBean[] listaPacchetti;

    private int numeroPacchetti;

    public RisultatoInvioBean() {
    }

    public RisultatoInvioBean(
           java.lang.String messaggio,
           java.lang.String esito,
           bean.webservices.smash.PacchettoBean[] listaPacchetti,
           int numeroPacchetti) {
           this.messaggio = messaggio;
           this.esito = esito;
           this.listaPacchetti = listaPacchetti;
           this.numeroPacchetti = numeroPacchetti;
    }


    /**
     * Gets the messaggio value for this RisultatoInvioBean.
     * 
     * @return messaggio
     */
    public java.lang.String getMessaggio() {
        return messaggio;
    }


    /**
     * Sets the messaggio value for this RisultatoInvioBean.
     * 
     * @param messaggio
     */
    public void setMessaggio(java.lang.String messaggio) {
        this.messaggio = messaggio;
    }


    /**
     * Gets the esito value for this RisultatoInvioBean.
     * 
     * @return esito
     */
    public java.lang.String getEsito() {
        return esito;
    }


    /**
     * Sets the esito value for this RisultatoInvioBean.
     * 
     * @param esito
     */
    public void setEsito(java.lang.String esito) {
        this.esito = esito;
    }


    /**
     * Gets the listaPacchetti value for this RisultatoInvioBean.
     * 
     * @return listaPacchetti
     */
    public bean.webservices.smash.PacchettoBean[] getListaPacchetti() {
        return listaPacchetti;
    }


    /**
     * Sets the listaPacchetti value for this RisultatoInvioBean.
     * 
     * @param listaPacchetti
     */
    public void setListaPacchetti(bean.webservices.smash.PacchettoBean[] listaPacchetti) {
        this.listaPacchetti = listaPacchetti;
    }

    public bean.webservices.smash.PacchettoBean getListaPacchetti(int i) {
        return this.listaPacchetti[i];
    }

    public void setListaPacchetti(int i, bean.webservices.smash.PacchettoBean _value) {
        this.listaPacchetti[i] = _value;
    }


    /**
     * Gets the numeroPacchetti value for this RisultatoInvioBean.
     * 
     * @return numeroPacchetti
     */
    public int getNumeroPacchetti() {
        return numeroPacchetti;
    }


    /**
     * Sets the numeroPacchetti value for this RisultatoInvioBean.
     * 
     * @param numeroPacchetti
     */
    public void setNumeroPacchetti(int numeroPacchetti) {
        this.numeroPacchetti = numeroPacchetti;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RisultatoInvioBean)) return false;
        RisultatoInvioBean other = (RisultatoInvioBean) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.messaggio==null && other.getMessaggio()==null) || 
             (this.messaggio!=null &&
              this.messaggio.equals(other.getMessaggio()))) &&
            ((this.esito==null && other.getEsito()==null) || 
             (this.esito!=null &&
              this.esito.equals(other.getEsito()))) &&
            ((this.listaPacchetti==null && other.getListaPacchetti()==null) || 
             (this.listaPacchetti!=null &&
              java.util.Arrays.equals(this.listaPacchetti, other.getListaPacchetti()))) &&
            this.numeroPacchetti == other.getNumeroPacchetti();
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
        if (getMessaggio() != null) {
            _hashCode += getMessaggio().hashCode();
        }
        if (getEsito() != null) {
            _hashCode += getEsito().hashCode();
        }
        if (getListaPacchetti() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getListaPacchetti());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getListaPacchetti(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += getNumeroPacchetti();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RisultatoInvioBean.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("java:smash.webservices.bean", "RisultatoInvioBean"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("messaggio");
        elemField.setXmlName(new javax.xml.namespace.QName("", "messaggio"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("esito");
        elemField.setXmlName(new javax.xml.namespace.QName("", "esito"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("listaPacchetti");
        elemField.setXmlName(new javax.xml.namespace.QName("", "listaPacchetti"));
        elemField.setXmlType(new javax.xml.namespace.QName("java:smash.webservices.bean", "PacchettoBean"));
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numeroPacchetti");
        elemField.setXmlName(new javax.xml.namespace.QName("", "numeroPacchetti"));
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
