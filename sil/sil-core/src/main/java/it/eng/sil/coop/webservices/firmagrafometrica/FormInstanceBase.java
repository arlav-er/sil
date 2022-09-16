/**
 * FormInstanceBase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.firmagrafometrica;

public class FormInstanceBase implements java.io.Serializable {
	private byte[] XFAData;

	private java.lang.Object[] actionList;

	private java.lang.String hardenedTemplatePath;

	private java.lang.String selectedAction;

	private java.lang.String templatePath;

	public FormInstanceBase() {
	}

	public FormInstanceBase(byte[] XFAData, java.lang.Object[] actionList, java.lang.String hardenedTemplatePath,
			java.lang.String selectedAction, java.lang.String templatePath) {
		this.XFAData = XFAData;
		this.actionList = actionList;
		this.hardenedTemplatePath = hardenedTemplatePath;
		this.selectedAction = selectedAction;
		this.templatePath = templatePath;
	}

	/**
	 * Gets the XFAData value for this FormInstanceBase.
	 * 
	 * @return XFAData
	 */
	public byte[] getXFAData() {
		return XFAData;
	}

	/**
	 * Sets the XFAData value for this FormInstanceBase.
	 * 
	 * @param XFAData
	 */
	public void setXFAData(byte[] XFAData) {
		this.XFAData = XFAData;
	}

	/**
	 * Gets the actionList value for this FormInstanceBase.
	 * 
	 * @return actionList
	 */
	public java.lang.Object[] getActionList() {
		return actionList;
	}

	/**
	 * Sets the actionList value for this FormInstanceBase.
	 * 
	 * @param actionList
	 */
	public void setActionList(java.lang.Object[] actionList) {
		this.actionList = actionList;
	}

	/**
	 * Gets the hardenedTemplatePath value for this FormInstanceBase.
	 * 
	 * @return hardenedTemplatePath
	 */
	public java.lang.String getHardenedTemplatePath() {
		return hardenedTemplatePath;
	}

	/**
	 * Sets the hardenedTemplatePath value for this FormInstanceBase.
	 * 
	 * @param hardenedTemplatePath
	 */
	public void setHardenedTemplatePath(java.lang.String hardenedTemplatePath) {
		this.hardenedTemplatePath = hardenedTemplatePath;
	}

	/**
	 * Gets the selectedAction value for this FormInstanceBase.
	 * 
	 * @return selectedAction
	 */
	public java.lang.String getSelectedAction() {
		return selectedAction;
	}

	/**
	 * Sets the selectedAction value for this FormInstanceBase.
	 * 
	 * @param selectedAction
	 */
	public void setSelectedAction(java.lang.String selectedAction) {
		this.selectedAction = selectedAction;
	}

	/**
	 * Gets the templatePath value for this FormInstanceBase.
	 * 
	 * @return templatePath
	 */
	public java.lang.String getTemplatePath() {
		return templatePath;
	}

	/**
	 * Sets the templatePath value for this FormInstanceBase.
	 * 
	 * @param templatePath
	 */
	public void setTemplatePath(java.lang.String templatePath) {
		this.templatePath = templatePath;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof FormInstanceBase))
			return false;
		FormInstanceBase other = (FormInstanceBase) obj;
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
				&& ((this.XFAData == null && other.getXFAData() == null)
						|| (this.XFAData != null && java.util.Arrays.equals(this.XFAData, other.getXFAData())))
				&& ((this.actionList == null && other.getActionList() == null)
						|| (this.actionList != null && java.util.Arrays.equals(this.actionList, other.getActionList())))
				&& ((this.hardenedTemplatePath == null && other.getHardenedTemplatePath() == null)
						|| (this.hardenedTemplatePath != null
								&& this.hardenedTemplatePath.equals(other.getHardenedTemplatePath())))
				&& ((this.selectedAction == null && other.getSelectedAction() == null)
						|| (this.selectedAction != null && this.selectedAction.equals(other.getSelectedAction())))
				&& ((this.templatePath == null && other.getTemplatePath() == null)
						|| (this.templatePath != null && this.templatePath.equals(other.getTemplatePath())));
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
		if (getXFAData() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getXFAData()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getXFAData(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getActionList() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getActionList()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getActionList(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getHardenedTemplatePath() != null) {
			_hashCode += getHardenedTemplatePath().hashCode();
		}
		if (getSelectedAction() != null) {
			_hashCode += getSelectedAction().hashCode();
		}
		if (getTemplatePath() != null) {
			_hashCode += getTemplatePath().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			FormInstanceBase.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://firmagrafometrica.webservices.coop.sil.eng.it",
				"FormInstanceBase"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("XFAData");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://firmagrafometrica.webservices.coop.sil.eng.it", "XFAData"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("actionList");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://firmagrafometrica.webservices.coop.sil.eng.it", "actionList"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
		elemField.setNillable(true);
		elemField.setItemQName(
				new javax.xml.namespace.QName("http://firmagrafometrica.webservices.coop.sil.eng.it", "item"));
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("hardenedTemplatePath");
		elemField.setXmlName(new javax.xml.namespace.QName("http://firmagrafometrica.webservices.coop.sil.eng.it",
				"hardenedTemplatePath"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("selectedAction");
		elemField.setXmlName(new javax.xml.namespace.QName("http://firmagrafometrica.webservices.coop.sil.eng.it",
				"selectedAction"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("templatePath");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://firmagrafometrica.webservices.coop.sil.eng.it", "templatePath"));
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
