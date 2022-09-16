/**
 * ConditionFilter.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.firmagrafometrica;

public class ConditionFilter extends it.eng.sil.coop.webservices.firmagrafometrica.PurgeFilter
		implements java.io.Serializable {
	private it.eng.sil.coop.webservices.firmagrafometrica.ConditionEnum condition;

	private java.lang.String value;

	private java.lang.String variable;

	public ConditionFilter() {
	}

	public ConditionFilter(it.eng.sil.coop.webservices.firmagrafometrica.ConditionEnum condition,
			java.lang.String value, java.lang.String variable) {
		this.condition = condition;
		this.value = value;
		this.variable = variable;
	}

	/**
	 * Gets the condition value for this ConditionFilter.
	 * 
	 * @return condition
	 */
	public it.eng.sil.coop.webservices.firmagrafometrica.ConditionEnum getCondition() {
		return condition;
	}

	/**
	 * Sets the condition value for this ConditionFilter.
	 * 
	 * @param condition
	 */
	public void setCondition(it.eng.sil.coop.webservices.firmagrafometrica.ConditionEnum condition) {
		this.condition = condition;
	}

	/**
	 * Gets the value value for this ConditionFilter.
	 * 
	 * @return value
	 */
	public java.lang.String getValue() {
		return value;
	}

	/**
	 * Sets the value value for this ConditionFilter.
	 * 
	 * @param value
	 */
	public void setValue(java.lang.String value) {
		this.value = value;
	}

	/**
	 * Gets the variable value for this ConditionFilter.
	 * 
	 * @return variable
	 */
	public java.lang.String getVariable() {
		return variable;
	}

	/**
	 * Sets the variable value for this ConditionFilter.
	 * 
	 * @param variable
	 */
	public void setVariable(java.lang.String variable) {
		this.variable = variable;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof ConditionFilter))
			return false;
		ConditionFilter other = (ConditionFilter) obj;
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
				&& ((this.condition == null && other.getCondition() == null)
						|| (this.condition != null && this.condition.equals(other.getCondition())))
				&& ((this.value == null && other.getValue() == null)
						|| (this.value != null && this.value.equals(other.getValue())))
				&& ((this.variable == null && other.getVariable() == null)
						|| (this.variable != null && this.variable.equals(other.getVariable())));
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
		if (getCondition() != null) {
			_hashCode += getCondition().hashCode();
		}
		if (getValue() != null) {
			_hashCode += getValue().hashCode();
		}
		if (getVariable() != null) {
			_hashCode += getVariable().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			ConditionFilter.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://firmagrafometrica.webservices.coop.sil.eng.it",
				"ConditionFilter"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("condition");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://firmagrafometrica.webservices.coop.sil.eng.it", "condition"));
		elemField.setXmlType(
				new javax.xml.namespace.QName("http://firmagrafometrica.webservices.coop.sil.eng.it", "ConditionEnum"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("value");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://firmagrafometrica.webservices.coop.sil.eng.it", "value"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("variable");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://firmagrafometrica.webservices.coop.sil.eng.it", "variable"));
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
