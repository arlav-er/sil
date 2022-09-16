/**
 * AnagraficaTypeSesso.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.art16online.istanze;

public class AnagraficaTypeSesso implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected AnagraficaTypeSesso(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _F = "F";
    public static final java.lang.String _M = "M";
    public static final AnagraficaTypeSesso F = new AnagraficaTypeSesso(_F);
    public static final AnagraficaTypeSesso M = new AnagraficaTypeSesso(_M);
    public java.lang.String getValue() { return _value_;}
    public static AnagraficaTypeSesso fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        AnagraficaTypeSesso enumeration = (AnagraficaTypeSesso)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static AnagraficaTypeSesso fromString(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        return fromValue(value);
    }
    public boolean equals(java.lang.Object obj) {return (obj == this);}
    public int hashCode() { return toString().hashCode();}
    public java.lang.String toString() { return _value_;}
    public java.lang.Object readResolve() throws java.io.ObjectStreamException { return fromValue(_value_);}
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumSerializer(
            _javaType, _xmlType);
    }
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumDeserializer(
            _javaType, _xmlType);
    }
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AnagraficaTypeSesso.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", ">AnagraficaType>sesso"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
