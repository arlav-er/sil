/**
 * InserimentoResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.wsClient.docareaProto;

public class InserimentoResponse implements java.io.Serializable {
	private it.eng.sil.coop.wsClient.docareaProto.InserimentoRet inserimentoResult;

	public InserimentoResponse() {
	}

	public InserimentoResponse(it.eng.sil.coop.wsClient.docareaProto.InserimentoRet inserimentoResult) {
		this.inserimentoResult = inserimentoResult;
	}

	/**
	 * Gets the inserimentoResult value for this InserimentoResponse.
	 * 
	 * @return inserimentoResult
	 */
	public it.eng.sil.coop.wsClient.docareaProto.InserimentoRet getInserimentoResult() {
		return inserimentoResult;
	}

	/**
	 * Sets the inserimentoResult value for this InserimentoResponse.
	 * 
	 * @param inserimentoResult
	 */
	public void setInserimentoResult(it.eng.sil.coop.wsClient.docareaProto.InserimentoRet inserimentoResult) {
		this.inserimentoResult = inserimentoResult;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof InserimentoResponse))
			return false;
		InserimentoResponse other = (InserimentoResponse) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true && ((this.inserimentoResult == null && other.getInserimentoResult() == null)
				|| (this.inserimentoResult != null && this.inserimentoResult.equals(other.getInserimentoResult())));
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
		if (getInserimentoResult() != null) {
			_hashCode += getInserimentoResult().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			InserimentoResponse.class, true);

	// configurazione namespace della busta soap
	private static String targetNamespace = DocAreaWSConfig.getTargetNamespace();
	// configurazione namespace del tipo di ritorno della busta soap
	private static String targetNamespaceResponse = DocAreaWSConfig.getTargetNamespaceResponseType();

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(targetNamespace, ">InserimentoResponse"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("inserimentoResult");
		elemField.setXmlName(new javax.xml.namespace.QName(targetNamespace, "InserimentoResult"));
		elemField.setXmlType(new javax.xml.namespace.QName(targetNamespaceResponse, "InserimentoRet"));
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
