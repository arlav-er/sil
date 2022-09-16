/**
 * SapTitoloStudio.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class SapTitoloStudio extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseTabellaGestioneEntity
		implements java.io.Serializable {
	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.CodMonoStatoEnum codMonoStato;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeComune deComune;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeTitolo deTitolo;

	private java.lang.Boolean flgPrincipale;

	private java.lang.Integer idSapTitoloStudio;

	private java.lang.Integer numAnno;

	private java.lang.String strNomeIstituto;

	private java.lang.String strSpecifica;

	private java.lang.String strVotazione;

	public SapTitoloStudio() {
	}

	public SapTitoloStudio(java.util.Calendar dtmIns, java.util.Calendar dtmMod, java.lang.Integer idPrincipalIns,
			java.lang.Integer idPrincipalMod,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.CodMonoStatoEnum codMonoStato,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeComune deComune,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeTitolo deTitolo, java.lang.Boolean flgPrincipale,
			java.lang.Integer idSapTitoloStudio, java.lang.Integer numAnno, java.lang.String strNomeIstituto,
			java.lang.String strSpecifica, java.lang.String strVotazione) {
		super(dtmIns, dtmMod, idPrincipalIns, idPrincipalMod);
		this.codMonoStato = codMonoStato;
		this.deComune = deComune;
		this.deTitolo = deTitolo;
		this.flgPrincipale = flgPrincipale;
		this.idSapTitoloStudio = idSapTitoloStudio;
		this.numAnno = numAnno;
		this.strNomeIstituto = strNomeIstituto;
		this.strSpecifica = strSpecifica;
		this.strVotazione = strVotazione;
	}

	/**
	 * Gets the codMonoStato value for this SapTitoloStudio.
	 * 
	 * @return codMonoStato
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.CodMonoStatoEnum getCodMonoStato() {
		return codMonoStato;
	}

	/**
	 * Sets the codMonoStato value for this SapTitoloStudio.
	 * 
	 * @param codMonoStato
	 */
	public void setCodMonoStato(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.CodMonoStatoEnum codMonoStato) {
		this.codMonoStato = codMonoStato;
	}

	/**
	 * Gets the deComune value for this SapTitoloStudio.
	 * 
	 * @return deComune
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeComune getDeComune() {
		return deComune;
	}

	/**
	 * Sets the deComune value for this SapTitoloStudio.
	 * 
	 * @param deComune
	 */
	public void setDeComune(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeComune deComune) {
		this.deComune = deComune;
	}

	/**
	 * Gets the deTitolo value for this SapTitoloStudio.
	 * 
	 * @return deTitolo
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeTitolo getDeTitolo() {
		return deTitolo;
	}

	/**
	 * Sets the deTitolo value for this SapTitoloStudio.
	 * 
	 * @param deTitolo
	 */
	public void setDeTitolo(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeTitolo deTitolo) {
		this.deTitolo = deTitolo;
	}

	/**
	 * Gets the flgPrincipale value for this SapTitoloStudio.
	 * 
	 * @return flgPrincipale
	 */
	public java.lang.Boolean getFlgPrincipale() {
		return flgPrincipale;
	}

	/**
	 * Sets the flgPrincipale value for this SapTitoloStudio.
	 * 
	 * @param flgPrincipale
	 */
	public void setFlgPrincipale(java.lang.Boolean flgPrincipale) {
		this.flgPrincipale = flgPrincipale;
	}

	/**
	 * Gets the idSapTitoloStudio value for this SapTitoloStudio.
	 * 
	 * @return idSapTitoloStudio
	 */
	public java.lang.Integer getIdSapTitoloStudio() {
		return idSapTitoloStudio;
	}

	/**
	 * Sets the idSapTitoloStudio value for this SapTitoloStudio.
	 * 
	 * @param idSapTitoloStudio
	 */
	public void setIdSapTitoloStudio(java.lang.Integer idSapTitoloStudio) {
		this.idSapTitoloStudio = idSapTitoloStudio;
	}

	/**
	 * Gets the numAnno value for this SapTitoloStudio.
	 * 
	 * @return numAnno
	 */
	public java.lang.Integer getNumAnno() {
		return numAnno;
	}

	/**
	 * Sets the numAnno value for this SapTitoloStudio.
	 * 
	 * @param numAnno
	 */
	public void setNumAnno(java.lang.Integer numAnno) {
		this.numAnno = numAnno;
	}

	/**
	 * Gets the strNomeIstituto value for this SapTitoloStudio.
	 * 
	 * @return strNomeIstituto
	 */
	public java.lang.String getStrNomeIstituto() {
		return strNomeIstituto;
	}

	/**
	 * Sets the strNomeIstituto value for this SapTitoloStudio.
	 * 
	 * @param strNomeIstituto
	 */
	public void setStrNomeIstituto(java.lang.String strNomeIstituto) {
		this.strNomeIstituto = strNomeIstituto;
	}

	/**
	 * Gets the strSpecifica value for this SapTitoloStudio.
	 * 
	 * @return strSpecifica
	 */
	public java.lang.String getStrSpecifica() {
		return strSpecifica;
	}

	/**
	 * Sets the strSpecifica value for this SapTitoloStudio.
	 * 
	 * @param strSpecifica
	 */
	public void setStrSpecifica(java.lang.String strSpecifica) {
		this.strSpecifica = strSpecifica;
	}

	/**
	 * Gets the strVotazione value for this SapTitoloStudio.
	 * 
	 * @return strVotazione
	 */
	public java.lang.String getStrVotazione() {
		return strVotazione;
	}

	/**
	 * Sets the strVotazione value for this SapTitoloStudio.
	 * 
	 * @param strVotazione
	 */
	public void setStrVotazione(java.lang.String strVotazione) {
		this.strVotazione = strVotazione;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof SapTitoloStudio))
			return false;
		SapTitoloStudio other = (SapTitoloStudio) obj;
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
				&& ((this.codMonoStato == null && other.getCodMonoStato() == null)
						|| (this.codMonoStato != null && this.codMonoStato.equals(other.getCodMonoStato())))
				&& ((this.deComune == null && other.getDeComune() == null)
						|| (this.deComune != null && this.deComune.equals(other.getDeComune())))
				&& ((this.deTitolo == null && other.getDeTitolo() == null)
						|| (this.deTitolo != null && this.deTitolo.equals(other.getDeTitolo())))
				&& ((this.flgPrincipale == null && other.getFlgPrincipale() == null)
						|| (this.flgPrincipale != null && this.flgPrincipale.equals(other.getFlgPrincipale())))
				&& ((this.idSapTitoloStudio == null && other.getIdSapTitoloStudio() == null)
						|| (this.idSapTitoloStudio != null
								&& this.idSapTitoloStudio.equals(other.getIdSapTitoloStudio())))
				&& ((this.numAnno == null && other.getNumAnno() == null)
						|| (this.numAnno != null && this.numAnno.equals(other.getNumAnno())))
				&& ((this.strNomeIstituto == null && other.getStrNomeIstituto() == null)
						|| (this.strNomeIstituto != null && this.strNomeIstituto.equals(other.getStrNomeIstituto())))
				&& ((this.strSpecifica == null && other.getStrSpecifica() == null)
						|| (this.strSpecifica != null && this.strSpecifica.equals(other.getStrSpecifica())))
				&& ((this.strVotazione == null && other.getStrVotazione() == null)
						|| (this.strVotazione != null && this.strVotazione.equals(other.getStrVotazione())));
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
		if (getCodMonoStato() != null) {
			_hashCode += getCodMonoStato().hashCode();
		}
		if (getDeComune() != null) {
			_hashCode += getDeComune().hashCode();
		}
		if (getDeTitolo() != null) {
			_hashCode += getDeTitolo().hashCode();
		}
		if (getFlgPrincipale() != null) {
			_hashCode += getFlgPrincipale().hashCode();
		}
		if (getIdSapTitoloStudio() != null) {
			_hashCode += getIdSapTitoloStudio().hashCode();
		}
		if (getNumAnno() != null) {
			_hashCode += getNumAnno().hashCode();
		}
		if (getStrNomeIstituto() != null) {
			_hashCode += getStrNomeIstituto().hashCode();
		}
		if (getStrSpecifica() != null) {
			_hashCode += getStrSpecifica().hashCode();
		}
		if (getStrVotazione() != null) {
			_hashCode += getStrVotazione().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			SapTitoloStudio.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapTitoloStudio"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codMonoStato");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codMonoStato"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"codMonoStatoEnum"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("deComune");
		elemField.setXmlName(new javax.xml.namespace.QName("", "deComune"));
		elemField.setXmlType(
				new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/", "deComune"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("deTitolo");
		elemField.setXmlName(new javax.xml.namespace.QName("", "deTitolo"));
		elemField.setXmlType(
				new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/", "deTitolo"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("flgPrincipale");
		elemField.setXmlName(new javax.xml.namespace.QName("", "flgPrincipale"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idSapTitoloStudio");
		elemField.setXmlName(new javax.xml.namespace.QName("", "idSapTitoloStudio"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("numAnno");
		elemField.setXmlName(new javax.xml.namespace.QName("", "numAnno"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strNomeIstituto");
		elemField.setXmlName(new javax.xml.namespace.QName("", "strNomeIstituto"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strSpecifica");
		elemField.setXmlName(new javax.xml.namespace.QName("", "strSpecifica"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strVotazione");
		elemField.setXmlName(new javax.xml.namespace.QName("", "strVotazione"));
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
