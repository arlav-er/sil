/**
 * MaOrario.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class MaOrario extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseTabellaMappaturaEntity
		implements java.io.Serializable {
	private java.lang.String codModalitaLavoro;

	private java.lang.String codOrario;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeOrario deOrario;

	public MaOrario() {
	}

	public MaOrario(java.util.Calendar dtFineVal, java.util.Calendar dtInizioVal, java.lang.String codModalitaLavoro,
			java.lang.String codOrario, it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeOrario deOrario) {
		super(dtFineVal, dtInizioVal);
		this.codModalitaLavoro = codModalitaLavoro;
		this.codOrario = codOrario;
		this.deOrario = deOrario;
	}

	/**
	 * Gets the codModalitaLavoro value for this MaOrario.
	 * 
	 * @return codModalitaLavoro
	 */
	public java.lang.String getCodModalitaLavoro() {
		return codModalitaLavoro;
	}

	/**
	 * Sets the codModalitaLavoro value for this MaOrario.
	 * 
	 * @param codModalitaLavoro
	 */
	public void setCodModalitaLavoro(java.lang.String codModalitaLavoro) {
		this.codModalitaLavoro = codModalitaLavoro;
	}

	/**
	 * Gets the codOrario value for this MaOrario.
	 * 
	 * @return codOrario
	 */
	public java.lang.String getCodOrario() {
		return codOrario;
	}

	/**
	 * Sets the codOrario value for this MaOrario.
	 * 
	 * @param codOrario
	 */
	public void setCodOrario(java.lang.String codOrario) {
		this.codOrario = codOrario;
	}

	/**
	 * Gets the deOrario value for this MaOrario.
	 * 
	 * @return deOrario
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeOrario getDeOrario() {
		return deOrario;
	}

	/**
	 * Sets the deOrario value for this MaOrario.
	 * 
	 * @param deOrario
	 */
	public void setDeOrario(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeOrario deOrario) {
		this.deOrario = deOrario;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof MaOrario))
			return false;
		MaOrario other = (MaOrario) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = super.equals(obj) && ((this.codModalitaLavoro == null && other.getCodModalitaLavoro() == null)
				|| (this.codModalitaLavoro != null && this.codModalitaLavoro.equals(other.getCodModalitaLavoro())))
				&& ((this.codOrario == null && other.getCodOrario() == null)
						|| (this.codOrario != null && this.codOrario.equals(other.getCodOrario())))
				&& ((this.deOrario == null && other.getDeOrario() == null)
						|| (this.deOrario != null && this.deOrario.equals(other.getDeOrario())));
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
		if (getCodModalitaLavoro() != null) {
			_hashCode += getCodModalitaLavoro().hashCode();
		}
		if (getCodOrario() != null) {
			_hashCode += getCodOrario().hashCode();
		}
		if (getDeOrario() != null) {
			_hashCode += getDeOrario().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			MaOrario.class, true);

	static {
		typeDesc.setXmlType(
				new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/", "maOrario"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codModalitaLavoro");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codModalitaLavoro"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codOrario");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codOrario"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("deOrario");
		elemField.setXmlName(new javax.xml.namespace.QName("", "deOrario"));
		elemField.setXmlType(
				new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/", "deOrario"));
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
