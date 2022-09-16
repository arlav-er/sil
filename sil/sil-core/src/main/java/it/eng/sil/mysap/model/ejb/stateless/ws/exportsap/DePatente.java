/**
 * DePatente.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class DePatente extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseTabellaDecodificaEntity
		implements java.io.Serializable {
	private java.lang.String codPatente;

	private java.lang.String descrizione;

	private java.lang.String id;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.MaPatente maPatente;

	private int occurencyRicerca;

	public DePatente() {
	}

	public DePatente(java.util.Calendar dtFineVal, java.util.Calendar dtInizioVal, java.lang.String codPatente,
			java.lang.String descrizione, java.lang.String id,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.MaPatente maPatente, int occurencyRicerca) {
		super(dtFineVal, dtInizioVal);
		this.codPatente = codPatente;
		this.descrizione = descrizione;
		this.id = id;
		this.maPatente = maPatente;
		this.occurencyRicerca = occurencyRicerca;
	}

	/**
	 * Gets the codPatente value for this DePatente.
	 * 
	 * @return codPatente
	 */
	public java.lang.String getCodPatente() {
		return codPatente;
	}

	/**
	 * Sets the codPatente value for this DePatente.
	 * 
	 * @param codPatente
	 */
	public void setCodPatente(java.lang.String codPatente) {
		this.codPatente = codPatente;
	}

	/**
	 * Gets the descrizione value for this DePatente.
	 * 
	 * @return descrizione
	 */
	public java.lang.String getDescrizione() {
		return descrizione;
	}

	/**
	 * Sets the descrizione value for this DePatente.
	 * 
	 * @param descrizione
	 */
	public void setDescrizione(java.lang.String descrizione) {
		this.descrizione = descrizione;
	}

	/**
	 * Gets the id value for this DePatente.
	 * 
	 * @return id
	 */
	public java.lang.String getId() {
		return id;
	}

	/**
	 * Sets the id value for this DePatente.
	 * 
	 * @param id
	 */
	public void setId(java.lang.String id) {
		this.id = id;
	}

	/**
	 * Gets the maPatente value for this DePatente.
	 * 
	 * @return maPatente
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.MaPatente getMaPatente() {
		return maPatente;
	}

	/**
	 * Sets the maPatente value for this DePatente.
	 * 
	 * @param maPatente
	 */
	public void setMaPatente(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.MaPatente maPatente) {
		this.maPatente = maPatente;
	}

	/**
	 * Gets the occurencyRicerca value for this DePatente.
	 * 
	 * @return occurencyRicerca
	 */
	public int getOccurencyRicerca() {
		return occurencyRicerca;
	}

	/**
	 * Sets the occurencyRicerca value for this DePatente.
	 * 
	 * @param occurencyRicerca
	 */
	public void setOccurencyRicerca(int occurencyRicerca) {
		this.occurencyRicerca = occurencyRicerca;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof DePatente))
			return false;
		DePatente other = (DePatente) obj;
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
				&& ((this.codPatente == null && other.getCodPatente() == null)
						|| (this.codPatente != null && this.codPatente.equals(other.getCodPatente())))
				&& ((this.descrizione == null && other.getDescrizione() == null)
						|| (this.descrizione != null && this.descrizione.equals(other.getDescrizione())))
				&& ((this.id == null && other.getId() == null) || (this.id != null && this.id.equals(other.getId())))
				&& ((this.maPatente == null && other.getMaPatente() == null)
						|| (this.maPatente != null && this.maPatente.equals(other.getMaPatente())))
				&& this.occurencyRicerca == other.getOccurencyRicerca();
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
		if (getCodPatente() != null) {
			_hashCode += getCodPatente().hashCode();
		}
		if (getDescrizione() != null) {
			_hashCode += getDescrizione().hashCode();
		}
		if (getId() != null) {
			_hashCode += getId().hashCode();
		}
		if (getMaPatente() != null) {
			_hashCode += getMaPatente().hashCode();
		}
		_hashCode += getOccurencyRicerca();
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			DePatente.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"dePatente"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codPatente");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codPatente"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("descrizione");
		elemField.setXmlName(new javax.xml.namespace.QName("", "descrizione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("id");
		elemField.setXmlName(new javax.xml.namespace.QName("", "id"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("maPatente");
		elemField.setXmlName(new javax.xml.namespace.QName("", "maPatente"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"maPatente"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("occurencyRicerca");
		elemField.setXmlName(new javax.xml.namespace.QName("", "occurencyRicerca"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
