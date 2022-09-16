/**
 * DatiGarsia.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.wsproxy.servsocwsproxy;

public class DatiGarsia extends it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.DatiServizio
		implements java.io.Serializable {
	private java.lang.String servizio;

	private java.lang.String esito;

	private java.lang.String operatoreSST;

	private java.lang.String operatoreUVG;

	private java.util.Calendar dataEsito;

	private java.util.Calendar dataValutazioneRC;

	private java.util.Calendar dataValutazioneUVG;

	public DatiGarsia() {
	}

	public DatiGarsia(java.lang.String criticita, java.lang.String idCriticita, java.lang.String relazione,
			java.lang.String servizio, java.lang.String esito, java.lang.String operatoreSST,
			java.lang.String operatoreUVG, java.util.Calendar dataEsito, java.util.Calendar dataValutazioneRC,
			java.util.Calendar dataValutazioneUVG) {
		super(criticita, idCriticita, relazione);
		this.servizio = servizio;
		this.esito = esito;
		this.operatoreSST = operatoreSST;
		this.operatoreUVG = operatoreUVG;
		this.dataEsito = dataEsito;
		this.dataValutazioneRC = dataValutazioneRC;
		this.dataValutazioneUVG = dataValutazioneUVG;
	}

	/**
	 * Gets the servizio value for this DatiGarsia.
	 * 
	 * @return servizio
	 */
	public java.lang.String getServizio() {
		return servizio;
	}

	/**
	 * Sets the servizio value for this DatiGarsia.
	 * 
	 * @param servizio
	 */
	public void setServizio(java.lang.String servizio) {
		this.servizio = servizio;
	}

	/**
	 * Gets the esito value for this DatiGarsia.
	 * 
	 * @return esito
	 */
	public java.lang.String getEsito() {
		return esito;
	}

	/**
	 * Sets the esito value for this DatiGarsia.
	 * 
	 * @param esito
	 */
	public void setEsito(java.lang.String esito) {
		this.esito = esito;
	}

	/**
	 * Gets the operatoreSST value for this DatiGarsia.
	 * 
	 * @return operatoreSST
	 */
	public java.lang.String getOperatoreSST() {
		return operatoreSST;
	}

	/**
	 * Sets the operatoreSST value for this DatiGarsia.
	 * 
	 * @param operatoreSST
	 */
	public void setOperatoreSST(java.lang.String operatoreSST) {
		this.operatoreSST = operatoreSST;
	}

	/**
	 * Gets the operatoreUVG value for this DatiGarsia.
	 * 
	 * @return operatoreUVG
	 */
	public java.lang.String getOperatoreUVG() {
		return operatoreUVG;
	}

	/**
	 * Sets the operatoreUVG value for this DatiGarsia.
	 * 
	 * @param operatoreUVG
	 */
	public void setOperatoreUVG(java.lang.String operatoreUVG) {
		this.operatoreUVG = operatoreUVG;
	}

	/**
	 * Gets the dataEsito value for this DatiGarsia.
	 * 
	 * @return dataEsito
	 */
	public java.util.Calendar getDataEsito() {
		return dataEsito;
	}

	/**
	 * Sets the dataEsito value for this DatiGarsia.
	 * 
	 * @param dataEsito
	 */
	public void setDataEsito(java.util.Calendar dataEsito) {
		this.dataEsito = dataEsito;
	}

	/**
	 * Gets the dataValutazioneRC value for this DatiGarsia.
	 * 
	 * @return dataValutazioneRC
	 */
	public java.util.Calendar getDataValutazioneRC() {
		return dataValutazioneRC;
	}

	/**
	 * Sets the dataValutazioneRC value for this DatiGarsia.
	 * 
	 * @param dataValutazioneRC
	 */
	public void setDataValutazioneRC(java.util.Calendar dataValutazioneRC) {
		this.dataValutazioneRC = dataValutazioneRC;
	}

	/**
	 * Gets the dataValutazioneUVG value for this DatiGarsia.
	 * 
	 * @return dataValutazioneUVG
	 */
	public java.util.Calendar getDataValutazioneUVG() {
		return dataValutazioneUVG;
	}

	/**
	 * Sets the dataValutazioneUVG value for this DatiGarsia.
	 * 
	 * @param dataValutazioneUVG
	 */
	public void setDataValutazioneUVG(java.util.Calendar dataValutazioneUVG) {
		this.dataValutazioneUVG = dataValutazioneUVG;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof DatiGarsia))
			return false;
		DatiGarsia other = (DatiGarsia) obj;
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
				&& ((this.servizio == null && other.getServizio() == null)
						|| (this.servizio != null && this.servizio.equals(other.getServizio())))
				&& ((this.esito == null && other.getEsito() == null)
						|| (this.esito != null && this.esito.equals(other.getEsito())))
				&& ((this.operatoreSST == null && other.getOperatoreSST() == null)
						|| (this.operatoreSST != null && this.operatoreSST.equals(other.getOperatoreSST())))
				&& ((this.operatoreUVG == null && other.getOperatoreUVG() == null)
						|| (this.operatoreUVG != null && this.operatoreUVG.equals(other.getOperatoreUVG())))
				&& ((this.dataEsito == null && other.getDataEsito() == null)
						|| (this.dataEsito != null && this.dataEsito.equals(other.getDataEsito())))
				&& ((this.dataValutazioneRC == null && other.getDataValutazioneRC() == null)
						|| (this.dataValutazioneRC != null
								&& this.dataValutazioneRC.equals(other.getDataValutazioneRC())))
				&& ((this.dataValutazioneUVG == null && other.getDataValutazioneUVG() == null)
						|| (this.dataValutazioneUVG != null
								&& this.dataValutazioneUVG.equals(other.getDataValutazioneUVG())));
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
		if (getServizio() != null) {
			_hashCode += getServizio().hashCode();
		}
		if (getEsito() != null) {
			_hashCode += getEsito().hashCode();
		}
		if (getOperatoreSST() != null) {
			_hashCode += getOperatoreSST().hashCode();
		}
		if (getOperatoreUVG() != null) {
			_hashCode += getOperatoreUVG().hashCode();
		}
		if (getDataEsito() != null) {
			_hashCode += getDataEsito().hashCode();
		}
		if (getDataValutazioneRC() != null) {
			_hashCode += getDataValutazioneRC().hashCode();
		}
		if (getDataValutazioneUVG() != null) {
			_hashCode += getDataValutazioneUVG().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			DatiGarsia.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://web.wslegacy.softech_engineering.it/", "datiGarsia"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("servizio");
		elemField.setXmlName(new javax.xml.namespace.QName("", "servizio"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("esito");
		elemField.setXmlName(new javax.xml.namespace.QName("", "esito"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("operatoreSST");
		elemField.setXmlName(new javax.xml.namespace.QName("", "operatoreSST"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("operatoreUVG");
		elemField.setXmlName(new javax.xml.namespace.QName("", "operatoreUVG"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataEsito");
		elemField.setXmlName(new javax.xml.namespace.QName("", "dataEsito"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataValutazioneRC");
		elemField.setXmlName(new javax.xml.namespace.QName("", "dataValutazioneRC"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataValutazioneUVG");
		elemField.setXmlName(new javax.xml.namespace.QName("", "dataValutazioneUVG"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
