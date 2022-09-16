/**
 * DatiSosia.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.wsproxy.servsocwsproxy;

public class DatiSosia extends it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.DatiServizio
		implements java.io.Serializable {
	private long codiceServizio;

	private java.lang.String servizio;

	private java.lang.Long codiceSottoServizio;

	private java.lang.String sottoServizio;

	private long codiceIstituto;

	private java.lang.String istituto;

	private java.util.Calendar decorrenza;

	private java.util.Calendar scadenza;

	public DatiSosia() {
	}

	public DatiSosia(java.lang.String criticita, java.lang.String idCriticita, java.lang.String relazione,
			long codiceServizio, java.lang.String servizio, java.lang.Long codiceSottoServizio,
			java.lang.String sottoServizio, long codiceIstituto, java.lang.String istituto,
			java.util.Calendar decorrenza, java.util.Calendar scadenza) {
		super(criticita, idCriticita, relazione);
		this.codiceServizio = codiceServizio;
		this.servizio = servizio;
		this.codiceSottoServizio = codiceSottoServizio;
		this.sottoServizio = sottoServizio;
		this.codiceIstituto = codiceIstituto;
		this.istituto = istituto;
		this.decorrenza = decorrenza;
		this.scadenza = scadenza;
	}

	/**
	 * Gets the codiceServizio value for this DatiSosia.
	 * 
	 * @return codiceServizio
	 */
	public long getCodiceServizio() {
		return codiceServizio;
	}

	/**
	 * Sets the codiceServizio value for this DatiSosia.
	 * 
	 * @param codiceServizio
	 */
	public void setCodiceServizio(long codiceServizio) {
		this.codiceServizio = codiceServizio;
	}

	/**
	 * Gets the servizio value for this DatiSosia.
	 * 
	 * @return servizio
	 */
	public java.lang.String getServizio() {
		return servizio;
	}

	/**
	 * Sets the servizio value for this DatiSosia.
	 * 
	 * @param servizio
	 */
	public void setServizio(java.lang.String servizio) {
		this.servizio = servizio;
	}

	/**
	 * Gets the codiceSottoServizio value for this DatiSosia.
	 * 
	 * @return codiceSottoServizio
	 */
	public java.lang.Long getCodiceSottoServizio() {
		return codiceSottoServizio;
	}

	/**
	 * Sets the codiceSottoServizio value for this DatiSosia.
	 * 
	 * @param codiceSottoServizio
	 */
	public void setCodiceSottoServizio(java.lang.Long codiceSottoServizio) {
		this.codiceSottoServizio = codiceSottoServizio;
	}

	/**
	 * Gets the sottoServizio value for this DatiSosia.
	 * 
	 * @return sottoServizio
	 */
	public java.lang.String getSottoServizio() {
		return sottoServizio;
	}

	/**
	 * Sets the sottoServizio value for this DatiSosia.
	 * 
	 * @param sottoServizio
	 */
	public void setSottoServizio(java.lang.String sottoServizio) {
		this.sottoServizio = sottoServizio;
	}

	/**
	 * Gets the codiceIstituto value for this DatiSosia.
	 * 
	 * @return codiceIstituto
	 */
	public long getCodiceIstituto() {
		return codiceIstituto;
	}

	/**
	 * Sets the codiceIstituto value for this DatiSosia.
	 * 
	 * @param codiceIstituto
	 */
	public void setCodiceIstituto(long codiceIstituto) {
		this.codiceIstituto = codiceIstituto;
	}

	/**
	 * Gets the istituto value for this DatiSosia.
	 * 
	 * @return istituto
	 */
	public java.lang.String getIstituto() {
		return istituto;
	}

	/**
	 * Sets the istituto value for this DatiSosia.
	 * 
	 * @param istituto
	 */
	public void setIstituto(java.lang.String istituto) {
		this.istituto = istituto;
	}

	/**
	 * Gets the decorrenza value for this DatiSosia.
	 * 
	 * @return decorrenza
	 */
	public java.util.Calendar getDecorrenza() {
		return decorrenza;
	}

	/**
	 * Sets the decorrenza value for this DatiSosia.
	 * 
	 * @param decorrenza
	 */
	public void setDecorrenza(java.util.Calendar decorrenza) {
		this.decorrenza = decorrenza;
	}

	/**
	 * Gets the scadenza value for this DatiSosia.
	 * 
	 * @return scadenza
	 */
	public java.util.Calendar getScadenza() {
		return scadenza;
	}

	/**
	 * Sets the scadenza value for this DatiSosia.
	 * 
	 * @param scadenza
	 */
	public void setScadenza(java.util.Calendar scadenza) {
		this.scadenza = scadenza;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof DatiSosia))
			return false;
		DatiSosia other = (DatiSosia) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = super.equals(obj) && this.codiceServizio == other.getCodiceServizio()
				&& ((this.servizio == null && other.getServizio() == null)
						|| (this.servizio != null && this.servizio.equals(other.getServizio())))
				&& ((this.codiceSottoServizio == null && other.getCodiceSottoServizio() == null)
						|| (this.codiceSottoServizio != null
								&& this.codiceSottoServizio.equals(other.getCodiceSottoServizio())))
				&& ((this.sottoServizio == null && other.getSottoServizio() == null)
						|| (this.sottoServizio != null && this.sottoServizio.equals(other.getSottoServizio())))
				&& this.codiceIstituto == other.getCodiceIstituto()
				&& ((this.istituto == null && other.getIstituto() == null)
						|| (this.istituto != null && this.istituto.equals(other.getIstituto())))
				&& ((this.decorrenza == null && other.getDecorrenza() == null)
						|| (this.decorrenza != null && this.decorrenza.equals(other.getDecorrenza())))
				&& ((this.scadenza == null && other.getScadenza() == null)
						|| (this.scadenza != null && this.scadenza.equals(other.getScadenza())));
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
		_hashCode += new Long(getCodiceServizio()).hashCode();
		if (getServizio() != null) {
			_hashCode += getServizio().hashCode();
		}
		if (getCodiceSottoServizio() != null) {
			_hashCode += getCodiceSottoServizio().hashCode();
		}
		if (getSottoServizio() != null) {
			_hashCode += getSottoServizio().hashCode();
		}
		_hashCode += new Long(getCodiceIstituto()).hashCode();
		if (getIstituto() != null) {
			_hashCode += getIstituto().hashCode();
		}
		if (getDecorrenza() != null) {
			_hashCode += getDecorrenza().hashCode();
		}
		if (getScadenza() != null) {
			_hashCode += getScadenza().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			DatiSosia.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://web.wslegacy.softech_engineering.it/", "datiSosia"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codiceServizio");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codiceServizio"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("servizio");
		elemField.setXmlName(new javax.xml.namespace.QName("", "servizio"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codiceSottoServizio");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codiceSottoServizio"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sottoServizio");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sottoServizio"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codiceIstituto");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codiceIstituto"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("istituto");
		elemField.setXmlName(new javax.xml.namespace.QName("", "istituto"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("decorrenza");
		elemField.setXmlName(new javax.xml.namespace.QName("", "decorrenza"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("scadenza");
		elemField.setXmlName(new javax.xml.namespace.QName("", "scadenza"));
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
