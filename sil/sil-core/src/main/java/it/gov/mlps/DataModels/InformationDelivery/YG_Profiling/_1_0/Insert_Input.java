/**
 * Insert_Input.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.mlps.DataModels.InformationDelivery.YG_Profiling._1_0;

public class Insert_Input implements java.io.Serializable {
	private java.lang.String cf;

	private java.lang.String titoloStudio;

	private java.lang.String codProv;

	private java.lang.String presenzaIt;

	private java.lang.String occupazAp;

	public Insert_Input() {
	}

	public Insert_Input(java.lang.String cf, java.lang.String titoloStudio, java.lang.String codProv,
			java.lang.String presenzaIt, java.lang.String occupazAp) {
		this.cf = cf;
		this.titoloStudio = titoloStudio;
		this.codProv = codProv;
		this.presenzaIt = presenzaIt;
		this.occupazAp = occupazAp;
	}

	/**
	 * Gets the cf value for this Insert_Input.
	 * 
	 * @return cf
	 */
	public java.lang.String getCf() {
		return cf;
	}

	/**
	 * Sets the cf value for this Insert_Input.
	 * 
	 * @param cf
	 */
	public void setCf(java.lang.String cf) {
		this.cf = cf;
	}

	/**
	 * Gets the titoloStudio value for this Insert_Input.
	 * 
	 * @return titoloStudio
	 */
	public java.lang.String getTitoloStudio() {
		return titoloStudio;
	}

	/**
	 * Sets the titoloStudio value for this Insert_Input.
	 * 
	 * @param titoloStudio
	 */
	public void setTitoloStudio(java.lang.String titoloStudio) {
		this.titoloStudio = titoloStudio;
	}

	/**
	 * Gets the codProv value for this Insert_Input.
	 * 
	 * @return codProv
	 */
	public java.lang.String getCodProv() {
		return codProv;
	}

	/**
	 * Sets the codProv value for this Insert_Input.
	 * 
	 * @param codProv
	 */
	public void setCodProv(java.lang.String codProv) {
		this.codProv = codProv;
	}

	/**
	 * Gets the presenzaIt value for this Insert_Input.
	 * 
	 * @return presenzaIt
	 */
	public java.lang.String getPresenzaIt() {
		return presenzaIt;
	}

	/**
	 * Sets the presenzaIt value for this Insert_Input.
	 * 
	 * @param presenzaIt
	 */
	public void setPresenzaIt(java.lang.String presenzaIt) {
		this.presenzaIt = presenzaIt;
	}

	/**
	 * Gets the occupazAp value for this Insert_Input.
	 * 
	 * @return occupazAp
	 */
	public java.lang.String getOccupazAp() {
		return occupazAp;
	}

	/**
	 * Sets the occupazAp value for this Insert_Input.
	 * 
	 * @param occupazAp
	 */
	public void setOccupazAp(java.lang.String occupazAp) {
		this.occupazAp = occupazAp;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Insert_Input))
			return false;
		Insert_Input other = (Insert_Input) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true
				&& ((this.cf == null && other.getCf() == null) || (this.cf != null && this.cf.equals(other.getCf())))
				&& ((this.titoloStudio == null && other.getTitoloStudio() == null)
						|| (this.titoloStudio != null && this.titoloStudio.equals(other.getTitoloStudio())))
				&& ((this.codProv == null && other.getCodProv() == null)
						|| (this.codProv != null && this.codProv.equals(other.getCodProv())))
				&& ((this.presenzaIt == null && other.getPresenzaIt() == null)
						|| (this.presenzaIt != null && this.presenzaIt.equals(other.getPresenzaIt())))
				&& ((this.occupazAp == null && other.getOccupazAp() == null)
						|| (this.occupazAp != null && this.occupazAp.equals(other.getOccupazAp())));
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
		if (getCf() != null) {
			_hashCode += getCf().hashCode();
		}
		if (getTitoloStudio() != null) {
			_hashCode += getTitoloStudio().hashCode();
		}
		if (getCodProv() != null) {
			_hashCode += getCodProv().hashCode();
		}
		if (getPresenzaIt() != null) {
			_hashCode += getPresenzaIt().hashCode();
		}
		if (getOccupazAp() != null) {
			_hashCode += getOccupazAp().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			Insert_Input.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/YG_Profiling/1.0", "Insert_Input"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("cf");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/YG_Profiling/1.0", "cf"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("titoloStudio");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/YG_Profiling/1.0", "titoloStudio"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codProv");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/YG_Profiling/1.0", "codProv"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("presenzaIt");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/YG_Profiling/1.0", "presenzaIt"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("occupazAp");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/YG_Profiling/1.0", "occupazAp"));
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
