/**
 * SchedaAnagraficaProfessionaleHeader.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class SchedaAnagraficaProfessionaleHeader implements java.io.Serializable {
	private java.lang.String comuneNascita;

	private java.lang.String comuneNascitaDesc;

	private java.util.Calendar dataNascita;

	private java.util.Calendar dtmIns;

	private java.lang.String email;

	private java.lang.Integer idSap;

	private java.lang.String userName;

	public SchedaAnagraficaProfessionaleHeader() {
	}

	public SchedaAnagraficaProfessionaleHeader(java.lang.String comuneNascita, java.lang.String comuneNascitaDesc,
			java.util.Calendar dataNascita, java.util.Calendar dtmIns, java.lang.String email, java.lang.Integer idSap,
			java.lang.String userName) {
		this.comuneNascita = comuneNascita;
		this.comuneNascitaDesc = comuneNascitaDesc;
		this.dataNascita = dataNascita;
		this.dtmIns = dtmIns;
		this.email = email;
		this.idSap = idSap;
		this.userName = userName;
	}

	/**
	 * Gets the comuneNascita value for this SchedaAnagraficaProfessionaleHeader.
	 * 
	 * @return comuneNascita
	 */
	public java.lang.String getComuneNascita() {
		return comuneNascita;
	}

	/**
	 * Sets the comuneNascita value for this SchedaAnagraficaProfessionaleHeader.
	 * 
	 * @param comuneNascita
	 */
	public void setComuneNascita(java.lang.String comuneNascita) {
		this.comuneNascita = comuneNascita;
	}

	/**
	 * Gets the comuneNascitaDesc value for this SchedaAnagraficaProfessionaleHeader.
	 * 
	 * @return comuneNascitaDesc
	 */
	public java.lang.String getComuneNascitaDesc() {
		return comuneNascitaDesc;
	}

	/**
	 * Sets the comuneNascitaDesc value for this SchedaAnagraficaProfessionaleHeader.
	 * 
	 * @param comuneNascitaDesc
	 */
	public void setComuneNascitaDesc(java.lang.String comuneNascitaDesc) {
		this.comuneNascitaDesc = comuneNascitaDesc;
	}

	/**
	 * Gets the dataNascita value for this SchedaAnagraficaProfessionaleHeader.
	 * 
	 * @return dataNascita
	 */
	public java.util.Calendar getDataNascita() {
		return dataNascita;
	}

	/**
	 * Sets the dataNascita value for this SchedaAnagraficaProfessionaleHeader.
	 * 
	 * @param dataNascita
	 */
	public void setDataNascita(java.util.Calendar dataNascita) {
		this.dataNascita = dataNascita;
	}

	/**
	 * Gets the dtmIns value for this SchedaAnagraficaProfessionaleHeader.
	 * 
	 * @return dtmIns
	 */
	public java.util.Calendar getDtmIns() {
		return dtmIns;
	}

	/**
	 * Sets the dtmIns value for this SchedaAnagraficaProfessionaleHeader.
	 * 
	 * @param dtmIns
	 */
	public void setDtmIns(java.util.Calendar dtmIns) {
		this.dtmIns = dtmIns;
	}

	/**
	 * Gets the email value for this SchedaAnagraficaProfessionaleHeader.
	 * 
	 * @return email
	 */
	public java.lang.String getEmail() {
		return email;
	}

	/**
	 * Sets the email value for this SchedaAnagraficaProfessionaleHeader.
	 * 
	 * @param email
	 */
	public void setEmail(java.lang.String email) {
		this.email = email;
	}

	/**
	 * Gets the idSap value for this SchedaAnagraficaProfessionaleHeader.
	 * 
	 * @return idSap
	 */
	public java.lang.Integer getIdSap() {
		return idSap;
	}

	/**
	 * Sets the idSap value for this SchedaAnagraficaProfessionaleHeader.
	 * 
	 * @param idSap
	 */
	public void setIdSap(java.lang.Integer idSap) {
		this.idSap = idSap;
	}

	/**
	 * Gets the userName value for this SchedaAnagraficaProfessionaleHeader.
	 * 
	 * @return userName
	 */
	public java.lang.String getUserName() {
		return userName;
	}

	/**
	 * Sets the userName value for this SchedaAnagraficaProfessionaleHeader.
	 * 
	 * @param userName
	 */
	public void setUserName(java.lang.String userName) {
		this.userName = userName;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof SchedaAnagraficaProfessionaleHeader))
			return false;
		SchedaAnagraficaProfessionaleHeader other = (SchedaAnagraficaProfessionaleHeader) obj;
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
				&& ((this.comuneNascita == null && other.getComuneNascita() == null)
						|| (this.comuneNascita != null && this.comuneNascita.equals(other.getComuneNascita())))
				&& ((this.comuneNascitaDesc == null && other.getComuneNascitaDesc() == null)
						|| (this.comuneNascitaDesc != null
								&& this.comuneNascitaDesc.equals(other.getComuneNascitaDesc())))
				&& ((this.dataNascita == null && other.getDataNascita() == null)
						|| (this.dataNascita != null && this.dataNascita.equals(other.getDataNascita())))
				&& ((this.dtmIns == null && other.getDtmIns() == null)
						|| (this.dtmIns != null && this.dtmIns.equals(other.getDtmIns())))
				&& ((this.email == null && other.getEmail() == null)
						|| (this.email != null && this.email.equals(other.getEmail())))
				&& ((this.idSap == null && other.getIdSap() == null)
						|| (this.idSap != null && this.idSap.equals(other.getIdSap())))
				&& ((this.userName == null && other.getUserName() == null)
						|| (this.userName != null && this.userName.equals(other.getUserName())));
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
		if (getComuneNascita() != null) {
			_hashCode += getComuneNascita().hashCode();
		}
		if (getComuneNascitaDesc() != null) {
			_hashCode += getComuneNascitaDesc().hashCode();
		}
		if (getDataNascita() != null) {
			_hashCode += getDataNascita().hashCode();
		}
		if (getDtmIns() != null) {
			_hashCode += getDtmIns().hashCode();
		}
		if (getEmail() != null) {
			_hashCode += getEmail().hashCode();
		}
		if (getIdSap() != null) {
			_hashCode += getIdSap().hashCode();
		}
		if (getUserName() != null) {
			_hashCode += getUserName().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			SchedaAnagraficaProfessionaleHeader.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"schedaAnagraficaProfessionaleHeader"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("comuneNascita");
		elemField.setXmlName(new javax.xml.namespace.QName("", "comuneNascita"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("comuneNascitaDesc");
		elemField.setXmlName(new javax.xml.namespace.QName("", "comuneNascitaDesc"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataNascita");
		elemField.setXmlName(new javax.xml.namespace.QName("", "dataNascita"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dtmIns");
		elemField.setXmlName(new javax.xml.namespace.QName("", "dtmIns"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("email");
		elemField.setXmlName(new javax.xml.namespace.QName("", "email"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idSap");
		elemField.setXmlName(new javax.xml.namespace.QName("", "idSap"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("userName");
		elemField.setXmlName(new javax.xml.namespace.QName("", "userName"));
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
