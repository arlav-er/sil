/**
 * RisultatoArchivioBean.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package bean.webservices.smash;

public class RisultatoArchivioBean  implements java.io.Serializable {
    private int numeroRecord;

    private bean.webservices.smash.ArchivioBean[] listaInvii;

    private java.lang.String messaggio;

    private java.lang.String esito;

    public RisultatoArchivioBean() {
    }

    public RisultatoArchivioBean(
           int numeroRecord,
           bean.webservices.smash.ArchivioBean[] listaInvii,
           java.lang.String messaggio,
           java.lang.String esito) {
           this.numeroRecord = numeroRecord;
           this.listaInvii = listaInvii;
           this.messaggio = messaggio;
           this.esito = esito;
    }


    /**
     * Gets the numeroRecord value for this RisultatoArchivioBean.
     * 
     * @return numeroRecord
     */
    public int getNumeroRecord() {
        return numeroRecord;
    }


    /**
     * Sets the numeroRecord value for this RisultatoArchivioBean.
     * 
     * @param numeroRecord
     */
    public void setNumeroRecord(int numeroRecord) {
        this.numeroRecord = numeroRecord;
    }


    /**
     * Gets the listaInvii value for this RisultatoArchivioBean.
     * 
     * @return listaInvii
     */
    public bean.webservices.smash.ArchivioBean[] getListaInvii() {
        return listaInvii;
    }


    /**
     * Sets the listaInvii value for this RisultatoArchivioBean.
     * 
     * @param listaInvii
     */
    public void setListaInvii(bean.webservices.smash.ArchivioBean[] listaInvii) {
        this.listaInvii = listaInvii;
    }

    public bean.webservices.smash.ArchivioBean getListaInvii(int i) {
        return this.listaInvii[i];
    }

    public void setListaInvii(int i, bean.webservices.smash.ArchivioBean _value) {
        this.listaInvii[i] = _value;
    }


    /**
     * Gets the messaggio value for this RisultatoArchivioBean.
     * 
     * @return messaggio
     */
    public java.lang.String getMessaggio() {
        return messaggio;
    }


    /**
     * Sets the messaggio value for this RisultatoArchivioBean.
     * 
     * @param messaggio
     */
    public void setMessaggio(java.lang.String messaggio) {
        this.messaggio = messaggio;
    }


    /**
     * Gets the esito value for this RisultatoArchivioBean.
     * 
     * @return esito
     */
    public java.lang.String getEsito() {
        return esito;
    }


    /**
     * Sets the esito value for this RisultatoArchivioBean.
     * 
     * @param esito
     */
    public void setEsito(java.lang.String esito) {
        this.esito = esito;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RisultatoArchivioBean)) return false;
        RisultatoArchivioBean other = (RisultatoArchivioBean) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.numeroRecord == other.getNumeroRecord() &&
            ((this.listaInvii==null && other.getListaInvii()==null) || 
             (this.listaInvii!=null &&
              java.util.Arrays.equals(this.listaInvii, other.getListaInvii()))) &&
            ((this.messaggio==null && other.getMessaggio()==null) || 
             (this.messaggio!=null &&
              this.messaggio.equals(other.getMessaggio()))) &&
            ((this.esito==null && other.getEsito()==null) || 
             (this.esito!=null &&
              this.esito.equals(other.getEsito())));
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
        _hashCode += getNumeroRecord();
        if (getListaInvii() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getListaInvii());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getListaInvii(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getMessaggio() != null) {
            _hashCode += getMessaggio().hashCode();
        }
        if (getEsito() != null) {
            _hashCode += getEsito().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RisultatoArchivioBean.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("java:smash.webservices.bean", "RisultatoArchivioBean"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numeroRecord");
        elemField.setXmlName(new javax.xml.namespace.QName("", "numeroRecord"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("listaInvii");
        elemField.setXmlName(new javax.xml.namespace.QName("", "listaInvii"));
        elemField.setXmlType(new javax.xml.namespace.QName("java:smash.webservices.bean", "ArchivioBean"));
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
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
