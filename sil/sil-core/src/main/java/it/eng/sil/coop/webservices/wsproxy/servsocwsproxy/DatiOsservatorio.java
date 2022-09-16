/**
 * DatiOsservatorio.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.wsproxy.servsocwsproxy;

public class DatiOsservatorio extends it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.DatiServizio
		implements java.io.Serializable {
	private java.util.Calendar dataContatto;

	private java.lang.String luogo;

	private java.lang.String areaBisogno;

	private java.lang.String tipoRichiesta;

	private java.lang.String inviatoA;

	public DatiOsservatorio() {
	}

	public DatiOsservatorio(java.lang.String criticita, java.lang.String idCriticita, java.lang.String relazione,
			java.util.Calendar dataContatto, java.lang.String luogo, java.lang.String areaBisogno,
			java.lang.String tipoRichiesta, java.lang.String inviatoA) {
		super(criticita, idCriticita, relazione);
		this.dataContatto = dataContatto;
		this.luogo = luogo;
		this.areaBisogno = areaBisogno;
		this.tipoRichiesta = tipoRichiesta;
		this.inviatoA = inviatoA;
	}

	/**
	 * Gets the dataContatto value for this DatiOsservatorio.
	 * 
	 * @return dataContatto
	 */
	public java.util.Calendar getDataContatto() {
		return dataContatto;
	}

	/**
	 * Sets the dataContatto value for this DatiOsservatorio.
	 * 
	 * @param dataContatto
	 */
	public void setDataContatto(java.util.Calendar dataContatto) {
		this.dataContatto = dataContatto;
	}

	/**
	 * Gets the luogo value for this DatiOsservatorio.
	 * 
	 * @return luogo
	 */
	public java.lang.String getLuogo() {
		return luogo;
	}

	/**
	 * Sets the luogo value for this DatiOsservatorio.
	 * 
	 * @param luogo
	 */
	public void setLuogo(java.lang.String luogo) {
		this.luogo = luogo;
	}

	/**
	 * Gets the areaBisogno value for this DatiOsservatorio.
	 * 
	 * @return areaBisogno
	 */
	public java.lang.String getAreaBisogno() {
		return areaBisogno;
	}

	/**
	 * Sets the areaBisogno value for this DatiOsservatorio.
	 * 
	 * @param areaBisogno
	 */
	public void setAreaBisogno(java.lang.String areaBisogno) {
		this.areaBisogno = areaBisogno;
	}

	/**
	 * Gets the tipoRichiesta value for this DatiOsservatorio.
	 * 
	 * @return tipoRichiesta
	 */
	public java.lang.String getTipoRichiesta() {
		return tipoRichiesta;
	}

	/**
	 * Sets the tipoRichiesta value for this DatiOsservatorio.
	 * 
	 * @param tipoRichiesta
	 */
	public void setTipoRichiesta(java.lang.String tipoRichiesta) {
		this.tipoRichiesta = tipoRichiesta;
	}

	/**
	 * Gets the inviatoA value for this DatiOsservatorio.
	 * 
	 * @return inviatoA
	 */
	public java.lang.String getInviatoA() {
		return inviatoA;
	}

	/**
	 * Sets the inviatoA value for this DatiOsservatorio.
	 * 
	 * @param inviatoA
	 */
	public void setInviatoA(java.lang.String inviatoA) {
		this.inviatoA = inviatoA;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof DatiOsservatorio))
			return false;
		DatiOsservatorio other = (DatiOsservatorio) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = super.equals(obj)
				&& ((this.dataContatto == null && other.getDataContatto() == null)
						|| (this.dataContatto != null && this.dataContatto.equals(other.getDataContatto())))
				&& ((this.luogo == null && other.getLuogo() == null)
						|| (this.luogo != null && this.luogo.equals(other.getLuogo())))
				&& ((this.areaBisogno == null && other.getAreaBisogno() == null)
						|| (this.areaBisogno != null && this.areaBisogno.equals(other.getAreaBisogno())))
				&& ((this.tipoRichiesta == null && other.getTipoRichiesta() == null)
						|| (this.tipoRichiesta != null && this.tipoRichiesta.equals(other.getTipoRichiesta())))
				&& ((this.inviatoA == null && other.getInviatoA() == null)
						|| (this.inviatoA != null && this.inviatoA.equals(other.getInviatoA())));
		__equalsCalc = null;
		return _equals;
	}

	private boolean __hashCodeCalc = false;

	public synchronized int hashCode() {
		if (__hashCodeCalc) {
			return 0;
		}
		__hashCodeCalc = true;
		int _hashCode = super.hashCode();
		if (getDataContatto() != null) {
			_hashCode += getDataContatto().hashCode();
		}
		if (getLuogo() != null) {
			_hashCode += getLuogo().hashCode();
		}
		if (getAreaBisogno() != null) {
			_hashCode += getAreaBisogno().hashCode();
		}
		if (getTipoRichiesta() != null) {
			_hashCode += getTipoRichiesta().hashCode();
		}
		if (getInviatoA() != null) {
			_hashCode += getInviatoA().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			DatiOsservatorio.class, true);

	static {
		typeDesc.setXmlType(
				new javax.xml.namespace.QName("http://web.wslegacy.softech_engineering.it/", "datiOsservatorio"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataContatto");
		elemField.setXmlName(new javax.xml.namespace.QName("", "dataContatto"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("luogo");
		elemField.setXmlName(new javax.xml.namespace.QName("", "luogo"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("areaBisogno");
		elemField.setXmlName(new javax.xml.namespace.QName("", "areaBisogno"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("tipoRichiesta");
		elemField.setXmlName(new javax.xml.namespace.QName("", "tipoRichiesta"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("inviatoA");
		elemField.setXmlName(new javax.xml.namespace.QName("", "inviatoA"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
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
