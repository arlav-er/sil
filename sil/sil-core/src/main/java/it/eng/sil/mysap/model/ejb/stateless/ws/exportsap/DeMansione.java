/**
 * DeMansione.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class DeMansione extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseTabellaDecodificaEntity
		implements java.io.Serializable {
	private java.lang.String codMansione;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeMansioneSil[] deMansioneSils;

	private java.lang.String descrizione;

	private java.lang.String id;

	private int occurencyRicerca;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeMansione padre;

	public DeMansione() {
	}

	public DeMansione(java.util.Calendar dtFineVal, java.util.Calendar dtInizioVal, java.lang.String codMansione,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeMansioneSil[] deMansioneSils,
			java.lang.String descrizione, java.lang.String id, int occurencyRicerca,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeMansione padre) {
		super(dtFineVal, dtInizioVal);
		this.codMansione = codMansione;
		this.deMansioneSils = deMansioneSils;
		this.descrizione = descrizione;
		this.id = id;
		this.occurencyRicerca = occurencyRicerca;
		this.padre = padre;
	}

	/**
	 * Gets the codMansione value for this DeMansione.
	 * 
	 * @return codMansione
	 */
	public java.lang.String getCodMansione() {
		return codMansione;
	}

	/**
	 * Sets the codMansione value for this DeMansione.
	 * 
	 * @param codMansione
	 */
	public void setCodMansione(java.lang.String codMansione) {
		this.codMansione = codMansione;
	}

	/**
	 * Gets the deMansioneSils value for this DeMansione.
	 * 
	 * @return deMansioneSils
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeMansioneSil[] getDeMansioneSils() {
		return deMansioneSils;
	}

	/**
	 * Sets the deMansioneSils value for this DeMansione.
	 * 
	 * @param deMansioneSils
	 */
	public void setDeMansioneSils(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeMansioneSil[] deMansioneSils) {
		this.deMansioneSils = deMansioneSils;
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeMansioneSil getDeMansioneSils(int i) {
		return this.deMansioneSils[i];
	}

	public void setDeMansioneSils(int i, it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeMansioneSil _value) {
		this.deMansioneSils[i] = _value;
	}

	/**
	 * Gets the descrizione value for this DeMansione.
	 * 
	 * @return descrizione
	 */
	public java.lang.String getDescrizione() {
		return descrizione;
	}

	/**
	 * Sets the descrizione value for this DeMansione.
	 * 
	 * @param descrizione
	 */
	public void setDescrizione(java.lang.String descrizione) {
		this.descrizione = descrizione;
	}

	/**
	 * Gets the id value for this DeMansione.
	 * 
	 * @return id
	 */
	public java.lang.String getId() {
		return id;
	}

	/**
	 * Sets the id value for this DeMansione.
	 * 
	 * @param id
	 */
	public void setId(java.lang.String id) {
		this.id = id;
	}

	/**
	 * Gets the occurencyRicerca value for this DeMansione.
	 * 
	 * @return occurencyRicerca
	 */
	public int getOccurencyRicerca() {
		return occurencyRicerca;
	}

	/**
	 * Sets the occurencyRicerca value for this DeMansione.
	 * 
	 * @param occurencyRicerca
	 */
	public void setOccurencyRicerca(int occurencyRicerca) {
		this.occurencyRicerca = occurencyRicerca;
	}

	/**
	 * Gets the padre value for this DeMansione.
	 * 
	 * @return padre
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeMansione getPadre() {
		return padre;
	}

	/**
	 * Sets the padre value for this DeMansione.
	 * 
	 * @param padre
	 */
	public void setPadre(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeMansione padre) {
		this.padre = padre;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof DeMansione))
			return false;
		DeMansione other = (DeMansione) obj;
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
				&& ((this.codMansione == null && other.getCodMansione() == null)
						|| (this.codMansione != null && this.codMansione.equals(other.getCodMansione())))
				&& ((this.deMansioneSils == null && other.getDeMansioneSils() == null) || (this.deMansioneSils != null
						&& java.util.Arrays.equals(this.deMansioneSils, other.getDeMansioneSils())))
				&& ((this.descrizione == null && other.getDescrizione() == null)
						|| (this.descrizione != null && this.descrizione.equals(other.getDescrizione())))
				&& ((this.id == null && other.getId() == null) || (this.id != null && this.id.equals(other.getId())))
				&& this.occurencyRicerca == other.getOccurencyRicerca()
				&& ((this.padre == null && other.getPadre() == null)
						|| (this.padre != null && this.padre.equals(other.getPadre())));
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
		if (getCodMansione() != null) {
			_hashCode += getCodMansione().hashCode();
		}
		if (getDeMansioneSils() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getDeMansioneSils()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getDeMansioneSils(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getDescrizione() != null) {
			_hashCode += getDescrizione().hashCode();
		}
		if (getId() != null) {
			_hashCode += getId().hashCode();
		}
		_hashCode += getOccurencyRicerca();
		if (getPadre() != null) {
			_hashCode += getPadre().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			DeMansione.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"deMansione"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codMansione");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codMansione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("deMansioneSils");
		elemField.setXmlName(new javax.xml.namespace.QName("", "deMansioneSils"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"deMansioneSil"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
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
		elemField.setFieldName("occurencyRicerca");
		elemField.setXmlName(new javax.xml.namespace.QName("", "occurencyRicerca"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("padre");
		elemField.setXmlName(new javax.xml.namespace.QName("", "padre"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"deMansione"));
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
