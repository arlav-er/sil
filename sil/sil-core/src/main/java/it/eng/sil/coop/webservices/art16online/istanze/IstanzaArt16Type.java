/**
 * IstanzaArt16Type.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.art16online.istanze;

public class IstanzaArt16Type implements java.io.Serializable {
	private int numero;

	private int anno;

	private it.eng.sil.coop.webservices.art16online.istanze.CandidaturaType[] listaCandidature;

	public IstanzaArt16Type() {
	}

	public IstanzaArt16Type(int numero, int anno,
			it.eng.sil.coop.webservices.art16online.istanze.CandidaturaType[] listaCandidature) {
		this.numero = numero;
		this.anno = anno;
		this.listaCandidature = listaCandidature;
	}

	/**
	 * Gets the numero value for this IstanzaArt16Type.
	 * 
	 * @return numero
	 */
	public int getNumero() {
		return numero;
	}

	/**
	 * Sets the numero value for this IstanzaArt16Type.
	 * 
	 * @param numero
	 */
	public void setNumero(int numero) {
		this.numero = numero;
	}

	/**
	 * Gets the anno value for this IstanzaArt16Type.
	 * 
	 * @return anno
	 */
	public int getAnno() {
		return anno;
	}

	/**
	 * Sets the anno value for this IstanzaArt16Type.
	 * 
	 * @param anno
	 */
	public void setAnno(int anno) {
		this.anno = anno;
	}

	/**
	 * Gets the listaCandidature value for this IstanzaArt16Type.
	 * 
	 * @return listaCandidature
	 */
	public it.eng.sil.coop.webservices.art16online.istanze.CandidaturaType[] getListaCandidature() {
		return listaCandidature;
	}

	/**
	 * Sets the listaCandidature value for this IstanzaArt16Type.
	 * 
	 * @param listaCandidature
	 */
	public void setListaCandidature(
			it.eng.sil.coop.webservices.art16online.istanze.CandidaturaType[] listaCandidature) {
		this.listaCandidature = listaCandidature;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof IstanzaArt16Type))
			return false;
		IstanzaArt16Type other = (IstanzaArt16Type) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true && this.numero == other.getNumero() && this.anno == other.getAnno()
				&& ((this.listaCandidature == null && other.getListaCandidature() == null)
						|| (this.listaCandidature != null
								&& java.util.Arrays.equals(this.listaCandidature, other.getListaCandidature())));
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
		_hashCode += getNumero();
		_hashCode += getAnno();
		if (getListaCandidature() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getListaCandidature()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getListaCandidature(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			IstanzaArt16Type.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it",
				"IstanzaArt16Type"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("numero");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", "numero"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("anno");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", "anno"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("listaCandidature");
		elemField.setXmlName(new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it",
				"ListaCandidature"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it",
				"CandidaturaType"));
		elemField.setNillable(false);
		elemField.setItemQName(
				new javax.xml.namespace.QName("http://istanze.art16online.webservices.coop.sil.eng.it", "Candidatura"));
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
	public static org.apache.axis.encoding.Serializer getSerializer(java.lang.String mechType,
			java.lang.Class _javaType, javax.xml.namespace.QName _xmlType) {
		return new org.apache.axis.encoding.ser.BeanSerializer(_javaType, _xmlType, typeDesc);
	}

	/**
	 * Get Custom Deserializer
	 */
	public static org.apache.axis.encoding.Deserializer getDeserializer(java.lang.String mechType,
			java.lang.Class _javaType, javax.xml.namespace.QName _xmlType) {
		return new org.apache.axis.encoding.ser.BeanDeserializer(_javaType, _xmlType, typeDesc);
	}

}
