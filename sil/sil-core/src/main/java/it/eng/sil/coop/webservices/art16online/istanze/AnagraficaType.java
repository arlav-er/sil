/**
 * AnagraficaType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.art16online.istanze;

public class AnagraficaType  implements java.io.Serializable {
    private java.lang.String codicefiscale;

    private java.lang.String cognome;

    private java.lang.String nome;

    private it.eng.sil.coop.webservices.art16online.istanze.AnagraficaTypeSesso sesso;

    private java.util.Date datanascita;

    private java.lang.String comune;

    private java.lang.String cittadinanza;

    public AnagraficaType() {
    }

    public AnagraficaType(
           java.lang.String codicefiscale,
           java.lang.String cognome,
           java.lang.String nome,
           it.eng.sil.coop.webservices.art16online.istanze.AnagraficaTypeSesso sesso,
           java.util.Date datanascita,
           java.lang.String comune,
           java.lang.String cittadinanza) {
           this.codicefiscale = codicefiscale;
           this.cognome = cognome;
           this.nome = nome;
           this.sesso = sesso;
           this.datanascita = datanascita;
           this.comune = comune;
           this.cittadinanza = cittadinanza;
    }


    /**
     * Gets the codicefiscale value for this AnagraficaType.
     * 
     * @return codicefiscale
     */
    public java.lang.String getCodicefiscale() {
        return codicefiscale;
    }


    /**
     * Sets the codicefiscale value for this AnagraficaType.
     * 
     * @param codicefiscale
     */
    public void setCodicefiscale(java.lang.String codicefiscale) {
        this.codicefiscale = codicefiscale;
    }


    /**
     * Gets the cognome value for this AnagraficaType.
     * 
     * @return cognome
     */
    public java.lang.String getCognome() {
        return cognome;
    }


    /**
     * Sets the cognome value for this AnagraficaType.
     * 
     * @param cognome
     */
    public void setCognome(java.lang.String cognome) {
        this.cognome = cognome;
    }


    /**
     * Gets the nome value for this AnagraficaType.
     * 
     * @return nome
     */
    public java.lang.String getNome() {
        return nome;
    }


    /**
     * Sets the nome value for this AnagraficaType.
     * 
     * @param nome
     */
    public void setNome(java.lang.String nome) {
        this.nome = nome;
    }


    /**
     * Gets the sesso value for this AnagraficaType.
     * 
     * @return sesso
     */
    public it.eng.sil.coop.webservices.art16online.istanze.AnagraficaTypeSesso getSesso() {
        return sesso;
    }


    /**
     * Sets the sesso value for this AnagraficaType.
     * 
     * @param sesso
     */
    public void setSesso(it.eng.sil.coop.webservices.art16online.istanze.AnagraficaTypeSesso sesso) {
        this.sesso = sesso;
    }


    /**
     * Gets the datanascita value for this AnagraficaType.
     * 
     * @return datanascita
     */
    public java.util.Date getDatanascita() {
        return datanascita;
    }


    /**
     * Sets the datanascita value for this AnagraficaType.
     * 
     * @param datanascita
     */
    public void setDatanascita(java.util.Date datanascita) {
        this.datanascita = datanascita;
    }


    /**
     * Gets the comune value for this AnagraficaType.
     * 
     * @return comune
     */
    public java.lang.String getComune() {
        return comune;
    }


    /**
     * Sets the comune value for this AnagraficaType.
     * 
     * @param comune
     */
    public void setComune(java.lang.String comune) {
        this.comune = comune;
    }


    /**
     * Gets the cittadinanza value for this AnagraficaType.
     * 
     * @return cittadinanza
     */
    public java.lang.String getCittadinanza() {
        return cittadinanza;
    }


    /**
     * Sets the cittadinanza value for this AnagraficaType.
     * 
     * @param cittadinanza
     */
    public void setCittadinanza(java.lang.String cittadinanza) {
        this.cittadinanza = cittadinanza;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AnagraficaType)) return false;
        AnagraficaType other = (AnagraficaType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.codicefiscale==null && other.getCodicefiscale()==null) || 
             (this.codicefiscale!=null &&
              this.codicefiscale.equals(other.getCodicefiscale()))) &&
            ((this.cognome==null && other.getCognome()==null) || 
             (this.cognome!=null &&
              this.cognome.equals(other.getCognome()))) &&
            ((this.nome==null && other.getNome()==null) || 
             (this.nome!=null &&
              this.nome.equals(other.getNome()))) &&
            ((this.sesso==null && other.getSesso()==null) || 
             (this.sesso!=null &&
              this.sesso.equals(other.getSesso()))) &&
            ((this.datanascita==null && other.getDatanascita()==null) || 
             (this.datanascita!=null &&
              this.datanascita.equals(other.getDatanascita()))) &&
            ((this.comune==null && other.getComune()==null) || 
             (this.comune!=null &&
              this.comune.equals(other.getComune()))) &&
            ((this.cittadinanza==null && other.getCittadinanza()==null) || 
             (this.cittadinanza!=null &&
              this.cittadinanza.equals(other.getCittadinanza())));
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
        if (getCodicefiscale() != null) {
            _hashCode += getCodicefiscale().hashCode();
        }
        if (getCognome() != null) {
            _hashCode += getCognome().hashCode();
        }
        if (getNome() != null) {
            _hashCode += getNome().hashCode();
        }
        if (getSesso() != null) {
            _hashCode += getSesso().hashCode();
        }
        if (getDatanascita() != null) {
            _hashCode += getDatanascita().hashCode();
        }
        if (getComune() != null) {
            _hashCode += getComune().hashCode();
        }
        if (getCittadinanza() != null) {
            _hashCode += getCittadinanza().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AnagraficaType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", "AnagraficaType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("codicefiscale");
        elemField.setXmlName(new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", "codicefiscale"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cognome");
        elemField.setXmlName(new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", "cognome"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nome");
        elemField.setXmlName(new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", "nome"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sesso");
        elemField.setXmlName(new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", "sesso"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", ">AnagraficaType>sesso"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("datanascita");
        elemField.setXmlName(new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", "datanascita"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("comune");
        elemField.setXmlName(new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", "comune"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cittadinanza");
        elemField.setXmlName(new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", "cittadinanza"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
