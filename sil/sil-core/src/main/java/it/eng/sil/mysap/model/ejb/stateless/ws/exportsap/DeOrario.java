/**
 * DeOrario.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class DeOrario extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseTabellaDecodificaEntity
		implements java.io.Serializable {
	private java.lang.String codOrario;

	private java.lang.String descrizione;

	private java.lang.String id;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.MaOrario maOrario;

	private int occurencyRicerca;

	public DeOrario() {
	}

	public DeOrario(java.util.Calendar dtFineVal, java.util.Calendar dtInizioVal, java.lang.String codOrario,
			java.lang.String descrizione, java.lang.String id,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.MaOrario maOrario, int occurencyRicerca) {
		super(dtFineVal, dtInizioVal);
		this.codOrario = codOrario;
		this.descrizione = descrizione;
		this.id = id;
		this.maOrario = maOrario;
		this.occurencyRicerca = occurencyRicerca;
	}

	/**
	 * Gets the codOrario value for this DeOrario.
	 * 
	 * @return codOrario
	 */
	public java.lang.String getCodOrario() {
		return codOrario;
	}

	/**
	 * Sets the codOrario value for this DeOrario.
	 * 
	 * @param codOrario
	 */
	public void setCodOrario(java.lang.String codOrario) {
		this.codOrario = codOrario;
	}

	/**
	 * Gets the descrizione value for this DeOrario.
	 * 
	 * @return descrizione
	 */
	public java.lang.String getDescrizione() {
		return descrizione;
	}

	/**
	 * Sets the descrizione value for this DeOrario.
	 * 
	 * @param descrizione
	 */
	public void setDescrizione(java.lang.String descrizione) {
		this.descrizione = descrizione;
	}

	/**
	 * Gets the id value for this DeOrario.
	 * 
	 * @return id
	 */
	public java.lang.String getId() {
		return id;
	}

	/**
	 * Sets the id value for this DeOrario.
	 * 
	 * @param id
	 */
	public void setId(java.lang.String id) {
		this.id = id;
	}

	/**
	 * Gets the maOrario value for this DeOrario.
	 * 
	 * @return maOrario
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.MaOrario getMaOrario() {
		return maOrario;
	}

	/**
	 * Sets the maOrario value for this DeOrario.
	 * 
	 * @param maOrario
	 */
	public void setMaOrario(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.MaOrario maOrario) {
		this.maOrario = maOrario;
	}

	/**
	 * Gets the occurencyRicerca value for this DeOrario.
	 * 
	 * @return occurencyRicerca
	 */
	public int getOccurencyRicerca() {
		return occurencyRicerca;
	}

	/**
	 * Sets the occurencyRicerca value for this DeOrario.
	 * 
	 * @param occurencyRicerca
	 */
	public void setOccurencyRicerca(int occurencyRicerca) {
		this.occurencyRicerca = occurencyRicerca;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof DeOrario))
			return false;
		DeOrario other = (DeOrario) obj;
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
				&& ((this.codOrario == null && other.getCodOrario() == null)
						|| (this.codOrario != null && this.codOrario.equals(other.getCodOrario())))
				&& ((this.descrizione == null && other.getDescrizione() == null)
						|| (this.descrizione != null && this.descrizione.equals(other.getDescrizione())))
				&& ((this.id == null && other.getId() == null) || (this.id != null && this.id.equals(other.getId())))
				&& ((this.maOrario == null && other.getMaOrario() == null)
						|| (this.maOrario != null && this.maOrario.equals(other.getMaOrario())))
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
		if (getCodOrario() != null) {
			_hashCode += getCodOrario().hashCode();
		}
		if (getDescrizione() != null) {
			_hashCode += getDescrizione().hashCode();
		}
		if (getId() != null) {
			_hashCode += getId().hashCode();
		}
		if (getMaOrario() != null) {
			_hashCode += getMaOrario().hashCode();
		}
		_hashCode += getOccurencyRicerca();
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			DeOrario.class, true);

	static {
		typeDesc.setXmlType(
				new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/", "deOrario"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codOrario");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codOrario"));
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
		elemField.setFieldName("maOrario");
		elemField.setXmlName(new javax.xml.namespace.QName("", "maOrario"));
		elemField.setXmlType(
				new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/", "maOrario"));
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
