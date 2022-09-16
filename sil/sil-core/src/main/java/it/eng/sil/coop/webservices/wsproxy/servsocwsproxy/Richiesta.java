/**
 * Richiesta.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.wsproxy.servsocwsproxy;

public class Richiesta implements java.io.Serializable {
	private it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.TipoSistema[] sistemi;

	private java.lang.String user;

	private java.lang.String password;

	private java.lang.String operatore;

	private java.lang.String soggettoNome;

	private java.lang.String soggettoCognome;

	private java.util.Calendar soggettoDataNascita;

	private java.lang.String soggettoCF;

	private java.lang.String soggettoComuneNascita;

	public Richiesta() {
	}

	public Richiesta(it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.TipoSistema[] sistemi, java.lang.String user,
			java.lang.String password, java.lang.String operatore, java.lang.String soggettoNome,
			java.lang.String soggettoCognome, java.util.Calendar soggettoDataNascita, java.lang.String soggettoCF,
			java.lang.String soggettoComuneNascita) {
		this.sistemi = sistemi;
		this.user = user;
		this.password = password;
		this.operatore = operatore;
		this.soggettoNome = soggettoNome;
		this.soggettoCognome = soggettoCognome;
		this.soggettoDataNascita = soggettoDataNascita;
		this.soggettoCF = soggettoCF;
		this.soggettoComuneNascita = soggettoComuneNascita;
	}

	/**
	 * Gets the sistemi value for this Richiesta.
	 * 
	 * @return sistemi
	 */
	public it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.TipoSistema[] getSistemi() {
		return sistemi;
	}

	/**
	 * Sets the sistemi value for this Richiesta.
	 * 
	 * @param sistemi
	 */
	public void setSistemi(it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.TipoSistema[] sistemi) {
		this.sistemi = sistemi;
	}

	public it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.TipoSistema getSistemi(int i) {
		return this.sistemi[i];
	}

	public void setSistemi(int i, it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.TipoSistema _value) {
		this.sistemi[i] = _value;
	}

	/**
	 * Gets the user value for this Richiesta.
	 * 
	 * @return user
	 */
	public java.lang.String getUser() {
		return user;
	}

	/**
	 * Sets the user value for this Richiesta.
	 * 
	 * @param user
	 */
	public void setUser(java.lang.String user) {
		this.user = user;
	}

	/**
	 * Gets the password value for this Richiesta.
	 * 
	 * @return password
	 */
	public java.lang.String getPassword() {
		return password;
	}

	/**
	 * Sets the password value for this Richiesta.
	 * 
	 * @param password
	 */
	public void setPassword(java.lang.String password) {
		this.password = password;
	}

	/**
	 * Gets the operatore value for this Richiesta.
	 * 
	 * @return operatore
	 */
	public java.lang.String getOperatore() {
		return operatore;
	}

	/**
	 * Sets the operatore value for this Richiesta.
	 * 
	 * @param operatore
	 */
	public void setOperatore(java.lang.String operatore) {
		this.operatore = operatore;
	}

	/**
	 * Gets the soggettoNome value for this Richiesta.
	 * 
	 * @return soggettoNome
	 */
	public java.lang.String getSoggettoNome() {
		return soggettoNome;
	}

	/**
	 * Sets the soggettoNome value for this Richiesta.
	 * 
	 * @param soggettoNome
	 */
	public void setSoggettoNome(java.lang.String soggettoNome) {
		this.soggettoNome = soggettoNome;
	}

	/**
	 * Gets the soggettoCognome value for this Richiesta.
	 * 
	 * @return soggettoCognome
	 */
	public java.lang.String getSoggettoCognome() {
		return soggettoCognome;
	}

	/**
	 * Sets the soggettoCognome value for this Richiesta.
	 * 
	 * @param soggettoCognome
	 */
	public void setSoggettoCognome(java.lang.String soggettoCognome) {
		this.soggettoCognome = soggettoCognome;
	}

	/**
	 * Gets the soggettoDataNascita value for this Richiesta.
	 * 
	 * @return soggettoDataNascita
	 */
	public java.util.Calendar getSoggettoDataNascita() {
		return soggettoDataNascita;
	}

	/**
	 * Sets the soggettoDataNascita value for this Richiesta.
	 * 
	 * @param soggettoDataNascita
	 */
	public void setSoggettoDataNascita(java.util.Calendar soggettoDataNascita) {
		this.soggettoDataNascita = soggettoDataNascita;
	}

	/**
	 * Gets the soggettoCF value for this Richiesta.
	 * 
	 * @return soggettoCF
	 */
	public java.lang.String getSoggettoCF() {
		return soggettoCF;
	}

	/**
	 * Sets the soggettoCF value for this Richiesta.
	 * 
	 * @param soggettoCF
	 */
	public void setSoggettoCF(java.lang.String soggettoCF) {
		this.soggettoCF = soggettoCF;
	}

	/**
	 * Gets the soggettoComuneNascita value for this Richiesta.
	 * 
	 * @return soggettoComuneNascita
	 */
	public java.lang.String getSoggettoComuneNascita() {
		return soggettoComuneNascita;
	}

	/**
	 * Sets the soggettoComuneNascita value for this Richiesta.
	 * 
	 * @param soggettoComuneNascita
	 */
	public void setSoggettoComuneNascita(java.lang.String soggettoComuneNascita) {
		this.soggettoComuneNascita = soggettoComuneNascita;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Richiesta))
			return false;
		Richiesta other = (Richiesta) obj;
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
				&& ((this.sistemi == null && other.getSistemi() == null)
						|| (this.sistemi != null && java.util.Arrays.equals(this.sistemi, other.getSistemi())))
				&& ((this.user == null && other.getUser() == null)
						|| (this.user != null && this.user.equals(other.getUser())))
				&& ((this.password == null && other.getPassword() == null)
						|| (this.password != null && this.password.equals(other.getPassword())))
				&& ((this.operatore == null && other.getOperatore() == null)
						|| (this.operatore != null && this.operatore.equals(other.getOperatore())))
				&& ((this.soggettoNome == null && other.getSoggettoNome() == null)
						|| (this.soggettoNome != null && this.soggettoNome.equals(other.getSoggettoNome())))
				&& ((this.soggettoCognome == null && other.getSoggettoCognome() == null)
						|| (this.soggettoCognome != null && this.soggettoCognome.equals(other.getSoggettoCognome())))
				&& ((this.soggettoDataNascita == null && other.getSoggettoDataNascita() == null)
						|| (this.soggettoDataNascita != null
								&& this.soggettoDataNascita.equals(other.getSoggettoDataNascita())))
				&& ((this.soggettoCF == null && other.getSoggettoCF() == null)
						|| (this.soggettoCF != null && this.soggettoCF.equals(other.getSoggettoCF())))
				&& ((this.soggettoComuneNascita == null && other.getSoggettoComuneNascita() == null)
						|| (this.soggettoComuneNascita != null
								&& this.soggettoComuneNascita.equals(other.getSoggettoComuneNascita())));
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
		if (getSistemi() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getSistemi()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getSistemi(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getUser() != null) {
			_hashCode += getUser().hashCode();
		}
		if (getPassword() != null) {
			_hashCode += getPassword().hashCode();
		}
		if (getOperatore() != null) {
			_hashCode += getOperatore().hashCode();
		}
		if (getSoggettoNome() != null) {
			_hashCode += getSoggettoNome().hashCode();
		}
		if (getSoggettoCognome() != null) {
			_hashCode += getSoggettoCognome().hashCode();
		}
		if (getSoggettoDataNascita() != null) {
			_hashCode += getSoggettoDataNascita().hashCode();
		}
		if (getSoggettoCF() != null) {
			_hashCode += getSoggettoCF().hashCode();
		}
		if (getSoggettoComuneNascita() != null) {
			_hashCode += getSoggettoComuneNascita().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			Richiesta.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://web.wslegacy.softech_engineering.it/", "richiesta"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sistemi");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sistemi"));
		elemField.setXmlType(
				new javax.xml.namespace.QName("http://web.wslegacy.softech_engineering.it/", "tipoSistema"));
		elemField.setNillable(false);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("user");
		elemField.setXmlName(new javax.xml.namespace.QName("", "user"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("password");
		elemField.setXmlName(new javax.xml.namespace.QName("", "password"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("operatore");
		elemField.setXmlName(new javax.xml.namespace.QName("", "operatore"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("soggettoNome");
		elemField.setXmlName(new javax.xml.namespace.QName("", "soggettoNome"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("soggettoCognome");
		elemField.setXmlName(new javax.xml.namespace.QName("", "soggettoCognome"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("soggettoDataNascita");
		elemField.setXmlName(new javax.xml.namespace.QName("", "soggettoDataNascita"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("soggettoCF");
		elemField.setXmlName(new javax.xml.namespace.QName("", "soggettoCF"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("soggettoComuneNascita");
		elemField.setXmlName(new javax.xml.namespace.QName("", "soggettoComuneNascita"));
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
