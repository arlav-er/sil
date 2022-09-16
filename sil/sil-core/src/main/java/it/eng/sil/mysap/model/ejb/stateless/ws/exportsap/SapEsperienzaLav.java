/**
 * SapEsperienzaLav.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class SapEsperienzaLav extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseTabellaGestioneEntity
		implements java.io.Serializable {
	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeAttivitaMin deAttivitaMin;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeContratto deContratto;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeMansione deMansione;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeMansioneMin deMansioneMin;

	private java.util.Calendar dtFine;

	private java.util.Calendar dtInizio;

	private java.lang.Integer idSapEsperienzaLav;

	private java.lang.String strDatoreLavoro;

	private java.lang.String strDescrAttivita;

	public SapEsperienzaLav() {
	}

	public SapEsperienzaLav(java.util.Calendar dtmIns, java.util.Calendar dtmMod, java.lang.Integer idPrincipalIns,
			java.lang.Integer idPrincipalMod,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeAttivitaMin deAttivitaMin,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeContratto deContratto,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeMansione deMansione,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeMansioneMin deMansioneMin, java.util.Calendar dtFine,
			java.util.Calendar dtInizio, java.lang.Integer idSapEsperienzaLav, java.lang.String strDatoreLavoro,
			java.lang.String strDescrAttivita) {
		super(dtmIns, dtmMod, idPrincipalIns, idPrincipalMod);
		this.deAttivitaMin = deAttivitaMin;
		this.deContratto = deContratto;
		this.deMansione = deMansione;
		this.deMansioneMin = deMansioneMin;
		this.dtFine = dtFine;
		this.dtInizio = dtInizio;
		this.idSapEsperienzaLav = idSapEsperienzaLav;
		this.strDatoreLavoro = strDatoreLavoro;
		this.strDescrAttivita = strDescrAttivita;
	}

	/**
	 * Gets the deAttivitaMin value for this SapEsperienzaLav.
	 * 
	 * @return deAttivitaMin
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeAttivitaMin getDeAttivitaMin() {
		return deAttivitaMin;
	}

	/**
	 * Sets the deAttivitaMin value for this SapEsperienzaLav.
	 * 
	 * @param deAttivitaMin
	 */
	public void setDeAttivitaMin(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeAttivitaMin deAttivitaMin) {
		this.deAttivitaMin = deAttivitaMin;
	}

	/**
	 * Gets the deContratto value for this SapEsperienzaLav.
	 * 
	 * @return deContratto
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeContratto getDeContratto() {
		return deContratto;
	}

	/**
	 * Sets the deContratto value for this SapEsperienzaLav.
	 * 
	 * @param deContratto
	 */
	public void setDeContratto(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeContratto deContratto) {
		this.deContratto = deContratto;
	}

	/**
	 * Gets the deMansione value for this SapEsperienzaLav.
	 * 
	 * @return deMansione
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeMansione getDeMansione() {
		return deMansione;
	}

	/**
	 * Sets the deMansione value for this SapEsperienzaLav.
	 * 
	 * @param deMansione
	 */
	public void setDeMansione(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeMansione deMansione) {
		this.deMansione = deMansione;
	}

	/**
	 * Gets the deMansioneMin value for this SapEsperienzaLav.
	 * 
	 * @return deMansioneMin
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeMansioneMin getDeMansioneMin() {
		return deMansioneMin;
	}

	/**
	 * Sets the deMansioneMin value for this SapEsperienzaLav.
	 * 
	 * @param deMansioneMin
	 */
	public void setDeMansioneMin(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeMansioneMin deMansioneMin) {
		this.deMansioneMin = deMansioneMin;
	}

	/**
	 * Gets the dtFine value for this SapEsperienzaLav.
	 * 
	 * @return dtFine
	 */
	public java.util.Calendar getDtFine() {
		return dtFine;
	}

	/**
	 * Sets the dtFine value for this SapEsperienzaLav.
	 * 
	 * @param dtFine
	 */
	public void setDtFine(java.util.Calendar dtFine) {
		this.dtFine = dtFine;
	}

	/**
	 * Gets the dtInizio value for this SapEsperienzaLav.
	 * 
	 * @return dtInizio
	 */
	public java.util.Calendar getDtInizio() {
		return dtInizio;
	}

	/**
	 * Sets the dtInizio value for this SapEsperienzaLav.
	 * 
	 * @param dtInizio
	 */
	public void setDtInizio(java.util.Calendar dtInizio) {
		this.dtInizio = dtInizio;
	}

	/**
	 * Gets the idSapEsperienzaLav value for this SapEsperienzaLav.
	 * 
	 * @return idSapEsperienzaLav
	 */
	public java.lang.Integer getIdSapEsperienzaLav() {
		return idSapEsperienzaLav;
	}

	/**
	 * Sets the idSapEsperienzaLav value for this SapEsperienzaLav.
	 * 
	 * @param idSapEsperienzaLav
	 */
	public void setIdSapEsperienzaLav(java.lang.Integer idSapEsperienzaLav) {
		this.idSapEsperienzaLav = idSapEsperienzaLav;
	}

	/**
	 * Gets the strDatoreLavoro value for this SapEsperienzaLav.
	 * 
	 * @return strDatoreLavoro
	 */
	public java.lang.String getStrDatoreLavoro() {
		return strDatoreLavoro;
	}

	/**
	 * Sets the strDatoreLavoro value for this SapEsperienzaLav.
	 * 
	 * @param strDatoreLavoro
	 */
	public void setStrDatoreLavoro(java.lang.String strDatoreLavoro) {
		this.strDatoreLavoro = strDatoreLavoro;
	}

	/**
	 * Gets the strDescrAttivita value for this SapEsperienzaLav.
	 * 
	 * @return strDescrAttivita
	 */
	public java.lang.String getStrDescrAttivita() {
		return strDescrAttivita;
	}

	/**
	 * Sets the strDescrAttivita value for this SapEsperienzaLav.
	 * 
	 * @param strDescrAttivita
	 */
	public void setStrDescrAttivita(java.lang.String strDescrAttivita) {
		this.strDescrAttivita = strDescrAttivita;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof SapEsperienzaLav))
			return false;
		SapEsperienzaLav other = (SapEsperienzaLav) obj;
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
				&& ((this.deAttivitaMin == null && other.getDeAttivitaMin() == null)
						|| (this.deAttivitaMin != null && this.deAttivitaMin.equals(other.getDeAttivitaMin())))
				&& ((this.deContratto == null && other.getDeContratto() == null)
						|| (this.deContratto != null && this.deContratto.equals(other.getDeContratto())))
				&& ((this.deMansione == null && other.getDeMansione() == null)
						|| (this.deMansione != null && this.deMansione.equals(other.getDeMansione())))
				&& ((this.deMansioneMin == null && other.getDeMansioneMin() == null)
						|| (this.deMansioneMin != null && this.deMansioneMin.equals(other.getDeMansioneMin())))
				&& ((this.dtFine == null && other.getDtFine() == null)
						|| (this.dtFine != null && this.dtFine.equals(other.getDtFine())))
				&& ((this.dtInizio == null && other.getDtInizio() == null)
						|| (this.dtInizio != null && this.dtInizio.equals(other.getDtInizio())))
				&& ((this.idSapEsperienzaLav == null && other.getIdSapEsperienzaLav() == null)
						|| (this.idSapEsperienzaLav != null
								&& this.idSapEsperienzaLav.equals(other.getIdSapEsperienzaLav())))
				&& ((this.strDatoreLavoro == null && other.getStrDatoreLavoro() == null)
						|| (this.strDatoreLavoro != null && this.strDatoreLavoro.equals(other.getStrDatoreLavoro())))
				&& ((this.strDescrAttivita == null && other.getStrDescrAttivita() == null)
						|| (this.strDescrAttivita != null
								&& this.strDescrAttivita.equals(other.getStrDescrAttivita())));
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
		if (getDeAttivitaMin() != null) {
			_hashCode += getDeAttivitaMin().hashCode();
		}
		if (getDeContratto() != null) {
			_hashCode += getDeContratto().hashCode();
		}
		if (getDeMansione() != null) {
			_hashCode += getDeMansione().hashCode();
		}
		if (getDeMansioneMin() != null) {
			_hashCode += getDeMansioneMin().hashCode();
		}
		if (getDtFine() != null) {
			_hashCode += getDtFine().hashCode();
		}
		if (getDtInizio() != null) {
			_hashCode += getDtInizio().hashCode();
		}
		if (getIdSapEsperienzaLav() != null) {
			_hashCode += getIdSapEsperienzaLav().hashCode();
		}
		if (getStrDatoreLavoro() != null) {
			_hashCode += getStrDatoreLavoro().hashCode();
		}
		if (getStrDescrAttivita() != null) {
			_hashCode += getStrDescrAttivita().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			SapEsperienzaLav.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapEsperienzaLav"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("deAttivitaMin");
		elemField.setXmlName(new javax.xml.namespace.QName("", "deAttivitaMin"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"deAttivitaMin"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("deContratto");
		elemField.setXmlName(new javax.xml.namespace.QName("", "deContratto"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"deContratto"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("deMansione");
		elemField.setXmlName(new javax.xml.namespace.QName("", "deMansione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"deMansione"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("deMansioneMin");
		elemField.setXmlName(new javax.xml.namespace.QName("", "deMansioneMin"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"deMansioneMin"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dtFine");
		elemField.setXmlName(new javax.xml.namespace.QName("", "dtFine"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dtInizio");
		elemField.setXmlName(new javax.xml.namespace.QName("", "dtInizio"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idSapEsperienzaLav");
		elemField.setXmlName(new javax.xml.namespace.QName("", "idSapEsperienzaLav"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strDatoreLavoro");
		elemField.setXmlName(new javax.xml.namespace.QName("", "strDatoreLavoro"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strDescrAttivita");
		elemField.setXmlName(new javax.xml.namespace.QName("", "strDescrAttivita"));
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
