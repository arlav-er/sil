/**
 * Execute.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.eng.sil.coordinamento.wsClient.np;

public class Execute implements java.io.Serializable {
	private java.lang.String mittente;

	private java.lang.String destinatario;

	private java.lang.String nomeServizio;

	private java.lang.String nomeMetodo;

	private java.lang.String dati;

	private java.lang.String token;

	public Execute() {
	}

	public Execute(java.lang.String mittente, java.lang.String destinatario, java.lang.String nomeServizio,
			java.lang.String nomeMetodo, java.lang.String dati, java.lang.String token) {
		this.mittente = mittente;
		this.destinatario = destinatario;
		this.nomeServizio = nomeServizio;
		this.nomeMetodo = nomeMetodo;
		this.dati = dati;
		this.token = token;
	}

	/**
	 * Gets the mittente value for this Execute.
	 * 
	 * @return mittente
	 */
	public java.lang.String getMittente() {
		return mittente;
	}

	/**
	 * Sets the mittente value for this Execute.
	 * 
	 * @param mittente
	 */
	public void setMittente(java.lang.String mittente) {
		this.mittente = mittente;
	}

	/**
	 * Gets the destinatario value for this Execute.
	 * 
	 * @return destinatario
	 */
	public java.lang.String getDestinatario() {
		return destinatario;
	}

	/**
	 * Sets the destinatario value for this Execute.
	 * 
	 * @param destinatario
	 */
	public void setDestinatario(java.lang.String destinatario) {
		this.destinatario = destinatario;
	}

	/**
	 * Gets the nomeServizio value for this Execute.
	 * 
	 * @return nomeServizio
	 */
	public java.lang.String getNomeServizio() {
		return nomeServizio;
	}

	/**
	 * Sets the nomeServizio value for this Execute.
	 * 
	 * @param nomeServizio
	 */
	public void setNomeServizio(java.lang.String nomeServizio) {
		this.nomeServizio = nomeServizio;
	}

	/**
	 * Gets the nomeMetodo value for this Execute.
	 * 
	 * @return nomeMetodo
	 */
	public java.lang.String getNomeMetodo() {
		return nomeMetodo;
	}

	/**
	 * Sets the nomeMetodo value for this Execute.
	 * 
	 * @param nomeMetodo
	 */
	public void setNomeMetodo(java.lang.String nomeMetodo) {
		this.nomeMetodo = nomeMetodo;
	}

	/**
	 * Gets the dati value for this Execute.
	 * 
	 * @return dati
	 */
	public java.lang.String getDati() {
		return dati;
	}

	/**
	 * Sets the dati value for this Execute.
	 * 
	 * @param dati
	 */
	public void setDati(java.lang.String dati) {
		this.dati = dati;
	}

	/**
	 * Gets the token value for this Execute.
	 * 
	 * @return token
	 */
	public java.lang.String getToken() {
		return token;
	}

	/**
	 * Sets the token value for this Execute.
	 * 
	 * @param token
	 */
	public void setToken(java.lang.String token) {
		this.token = token;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Execute))
			return false;
		Execute other = (Execute) obj;
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
				&& ((this.mittente == null && other.getMittente() == null)
						|| (this.mittente != null && this.mittente.equals(other.getMittente())))
				&& ((this.destinatario == null && other.getDestinatario() == null)
						|| (this.destinatario != null && this.destinatario.equals(other.getDestinatario())))
				&& ((this.nomeServizio == null && other.getNomeServizio() == null)
						|| (this.nomeServizio != null && this.nomeServizio.equals(other.getNomeServizio())))
				&& ((this.nomeMetodo == null && other.getNomeMetodo() == null)
						|| (this.nomeMetodo != null && this.nomeMetodo.equals(other.getNomeMetodo())))
				&& ((this.dati == null && other.getDati() == null)
						|| (this.dati != null && this.dati.equals(other.getDati())))
				&& ((this.token == null && other.getToken() == null)
						|| (this.token != null && this.token.equals(other.getToken())));
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
		if (getMittente() != null) {
			_hashCode += getMittente().hashCode();
		}
		if (getDestinatario() != null) {
			_hashCode += getDestinatario().hashCode();
		}
		if (getNomeServizio() != null) {
			_hashCode += getNomeServizio().hashCode();
		}
		if (getNomeMetodo() != null) {
			_hashCode += getNomeMetodo().hashCode();
		}
		if (getDati() != null) {
			_hashCode += getDati().hashCode();
		}
		if (getToken() != null) {
			_hashCode += getToken().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			Execute.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://adapter.coap.welfare.it", ">execute"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("mittente");
		elemField.setXmlName(new javax.xml.namespace.QName("http://adapter.coap.welfare.it", "mittente"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("destinatario");
		elemField.setXmlName(new javax.xml.namespace.QName("http://adapter.coap.welfare.it", "destinatario"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("nomeServizio");
		elemField.setXmlName(new javax.xml.namespace.QName("http://adapter.coap.welfare.it", "nomeServizio"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("nomeMetodo");
		elemField.setXmlName(new javax.xml.namespace.QName("http://adapter.coap.welfare.it", "nomeMetodo"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dati");
		elemField.setXmlName(new javax.xml.namespace.QName("http://adapter.coap.welfare.it", "dati"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("token");
		elemField.setXmlName(new javax.xml.namespace.QName("http://adapter.coap.welfare.it", "token"));
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

	public String toString() {
		return new StringBuffer().append("mittente=").append(mittente).append(", destinatario=").append(destinatario)
				.append(", nomeServizio=").append(this.nomeServizio).append(", nomeMetodo=").append(this.nomeMetodo)
				.append(", token=").append(this.token).toString();
	}

	/**
	 * Crea una stringa in formato xml dei parametri ad esclusione dei dati.
	 * 
	 * @param mitt
	 * @param servizio
	 * @param metodo
	 * @param dest
	 * @param token
	 * @return
	 */
	public static String toXMLString(String mitt, String servizio, String metodo, String dest, String token) {
		// questi due parametri possono non essere presenti nella chiamata del web service o valere null
		if (mitt == null)
			mitt = "";
		if (token == null)
			token = "";

		StringBuffer xml = new StringBuffer();
		xml.append("<ParametriRichiesta ").append(" mittente=\"").append(mitt).append("\"").append(" servizio=\"")
				.append(servizio).append("\"").append(" metodo=\"").append(metodo).append("\"")
				.append(" destinatario=\"").append(dest).append("\"").append(" token=\"").append(token).append("\"")
				.append(" />");
		return xml.toString();
	}

	/**
	 * Restituisce una stringa in formato xml dei parametri ad esclusione dei dati.
	 * 
	 * @param mitt
	 * @param servizio
	 * @param metodo
	 * @param dest
	 * @param token
	 * @return
	 */
	public String toXMLString() {
		// questi due parametri possono non essere presenti nella chiamata del web service o valere null
		String mitt = mittente;
		String _token = token;
		if (mitt == null)
			mitt = "";
		if (_token == null)
			_token = "";

		StringBuffer xml = new StringBuffer();
		xml.append("<ParametriRichiesta ").append(" mittente=\"").append(mitt).append("\"").append(" servizio=\"")
				.append(nomeServizio).append("\"").append(" metodo=\"").append(nomeMetodo).append("\"")
				.append(" destinatario=\"").append(destinatario).append("\"").append(" token=\"").append(_token)
				.append("\"").append(" />");
		return xml.toString();
	}

}
