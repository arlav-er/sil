/**
 * DatiGradus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.wsproxy.servsocwsproxy;

public class DatiGradus extends it.eng.sil.coop.webservices.wsproxy.servsocwsproxy.DatiServizio
		implements java.io.Serializable {
	private long codiceDomanda;

	private long annoDomanda;

	private long progressivoRevisione;

	private long tipoRevisione;

	private long codiceNodoPresentazione;

	private long codiceGraduatoria;

	private java.lang.String graduatoria;

	private long codiceServizio;

	private java.lang.String servizio;

	private java.lang.String stato;

	private java.util.Calendar dataPresentazione;

	public DatiGradus() {
	}

	public DatiGradus(java.lang.String criticita, java.lang.String idCriticita, java.lang.String relazione,
			long codiceDomanda, long annoDomanda, long progressivoRevisione, long tipoRevisione,
			long codiceNodoPresentazione, long codiceGraduatoria, java.lang.String graduatoria, long codiceServizio,
			java.lang.String servizio, java.lang.String stato, java.util.Calendar dataPresentazione) {
		super(criticita, idCriticita, relazione);
		this.codiceDomanda = codiceDomanda;
		this.annoDomanda = annoDomanda;
		this.progressivoRevisione = progressivoRevisione;
		this.tipoRevisione = tipoRevisione;
		this.codiceNodoPresentazione = codiceNodoPresentazione;
		this.codiceGraduatoria = codiceGraduatoria;
		this.graduatoria = graduatoria;
		this.codiceServizio = codiceServizio;
		this.servizio = servizio;
		this.stato = stato;
		this.dataPresentazione = dataPresentazione;
	}

	/**
	 * Gets the codiceDomanda value for this DatiGradus.
	 * 
	 * @return codiceDomanda
	 */
	public long getCodiceDomanda() {
		return codiceDomanda;
	}

	/**
	 * Sets the codiceDomanda value for this DatiGradus.
	 * 
	 * @param codiceDomanda
	 */
	public void setCodiceDomanda(long codiceDomanda) {
		this.codiceDomanda = codiceDomanda;
	}

	/**
	 * Gets the annoDomanda value for this DatiGradus.
	 * 
	 * @return annoDomanda
	 */
	public long getAnnoDomanda() {
		return annoDomanda;
	}

	/**
	 * Sets the annoDomanda value for this DatiGradus.
	 * 
	 * @param annoDomanda
	 */
	public void setAnnoDomanda(long annoDomanda) {
		this.annoDomanda = annoDomanda;
	}

	/**
	 * Gets the progressivoRevisione value for this DatiGradus.
	 * 
	 * @return progressivoRevisione
	 */
	public long getProgressivoRevisione() {
		return progressivoRevisione;
	}

	/**
	 * Sets the progressivoRevisione value for this DatiGradus.
	 * 
	 * @param progressivoRevisione
	 */
	public void setProgressivoRevisione(long progressivoRevisione) {
		this.progressivoRevisione = progressivoRevisione;
	}

	/**
	 * Gets the tipoRevisione value for this DatiGradus.
	 * 
	 * @return tipoRevisione
	 */
	public long getTipoRevisione() {
		return tipoRevisione;
	}

	/**
	 * Sets the tipoRevisione value for this DatiGradus.
	 * 
	 * @param tipoRevisione
	 */
	public void setTipoRevisione(long tipoRevisione) {
		this.tipoRevisione = tipoRevisione;
	}

	/**
	 * Gets the codiceNodoPresentazione value for this DatiGradus.
	 * 
	 * @return codiceNodoPresentazione
	 */
	public long getCodiceNodoPresentazione() {
		return codiceNodoPresentazione;
	}

	/**
	 * Sets the codiceNodoPresentazione value for this DatiGradus.
	 * 
	 * @param codiceNodoPresentazione
	 */
	public void setCodiceNodoPresentazione(long codiceNodoPresentazione) {
		this.codiceNodoPresentazione = codiceNodoPresentazione;
	}

	/**
	 * Gets the codiceGraduatoria value for this DatiGradus.
	 * 
	 * @return codiceGraduatoria
	 */
	public long getCodiceGraduatoria() {
		return codiceGraduatoria;
	}

	/**
	 * Sets the codiceGraduatoria value for this DatiGradus.
	 * 
	 * @param codiceGraduatoria
	 */
	public void setCodiceGraduatoria(long codiceGraduatoria) {
		this.codiceGraduatoria = codiceGraduatoria;
	}

	/**
	 * Gets the graduatoria value for this DatiGradus.
	 * 
	 * @return graduatoria
	 */
	public java.lang.String getGraduatoria() {
		return graduatoria;
	}

	/**
	 * Sets the graduatoria value for this DatiGradus.
	 * 
	 * @param graduatoria
	 */
	public void setGraduatoria(java.lang.String graduatoria) {
		this.graduatoria = graduatoria;
	}

	/**
	 * Gets the codiceServizio value for this DatiGradus.
	 * 
	 * @return codiceServizio
	 */
	public long getCodiceServizio() {
		return codiceServizio;
	}

	/**
	 * Sets the codiceServizio value for this DatiGradus.
	 * 
	 * @param codiceServizio
	 */
	public void setCodiceServizio(long codiceServizio) {
		this.codiceServizio = codiceServizio;
	}

	/**
	 * Gets the servizio value for this DatiGradus.
	 * 
	 * @return servizio
	 */
	public java.lang.String getServizio() {
		return servizio;
	}

	/**
	 * Sets the servizio value for this DatiGradus.
	 * 
	 * @param servizio
	 */
	public void setServizio(java.lang.String servizio) {
		this.servizio = servizio;
	}

	/**
	 * Gets the stato value for this DatiGradus.
	 * 
	 * @return stato
	 */
	public java.lang.String getStato() {
		return stato;
	}

	/**
	 * Sets the stato value for this DatiGradus.
	 * 
	 * @param stato
	 */
	public void setStato(java.lang.String stato) {
		this.stato = stato;
	}

	/**
	 * Gets the dataPresentazione value for this DatiGradus.
	 * 
	 * @return dataPresentazione
	 */
	public java.util.Calendar getDataPresentazione() {
		return dataPresentazione;
	}

	/**
	 * Sets the dataPresentazione value for this DatiGradus.
	 * 
	 * @param dataPresentazione
	 */
	public void setDataPresentazione(java.util.Calendar dataPresentazione) {
		this.dataPresentazione = dataPresentazione;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof DatiGradus))
			return false;
		DatiGradus other = (DatiGradus) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = super.equals(obj) && this.codiceDomanda == other.getCodiceDomanda()
				&& this.annoDomanda == other.getAnnoDomanda()
				&& this.progressivoRevisione == other.getProgressivoRevisione()
				&& this.tipoRevisione == other.getTipoRevisione()
				&& this.codiceNodoPresentazione == other.getCodiceNodoPresentazione()
				&& this.codiceGraduatoria == other.getCodiceGraduatoria()
				&& ((this.graduatoria == null && other.getGraduatoria() == null)
						|| (this.graduatoria != null && this.graduatoria.equals(other.getGraduatoria())))
				&& this.codiceServizio == other.getCodiceServizio()
				&& ((this.servizio == null && other.getServizio() == null)
						|| (this.servizio != null && this.servizio.equals(other.getServizio())))
				&& ((this.stato == null && other.getStato() == null)
						|| (this.stato != null && this.stato.equals(other.getStato())))
				&& ((this.dataPresentazione == null && other.getDataPresentazione() == null)
						|| (this.dataPresentazione != null
								&& this.dataPresentazione.equals(other.getDataPresentazione())));
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
		_hashCode += new Long(getCodiceDomanda()).hashCode();
		_hashCode += new Long(getAnnoDomanda()).hashCode();
		_hashCode += new Long(getProgressivoRevisione()).hashCode();
		_hashCode += new Long(getTipoRevisione()).hashCode();
		_hashCode += new Long(getCodiceNodoPresentazione()).hashCode();
		_hashCode += new Long(getCodiceGraduatoria()).hashCode();
		if (getGraduatoria() != null) {
			_hashCode += getGraduatoria().hashCode();
		}
		_hashCode += new Long(getCodiceServizio()).hashCode();
		if (getServizio() != null) {
			_hashCode += getServizio().hashCode();
		}
		if (getStato() != null) {
			_hashCode += getStato().hashCode();
		}
		if (getDataPresentazione() != null) {
			_hashCode += getDataPresentazione().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			DatiGradus.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://web.wslegacy.softech_engineering.it/", "datiGradus"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codiceDomanda");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codiceDomanda"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("annoDomanda");
		elemField.setXmlName(new javax.xml.namespace.QName("", "annoDomanda"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("progressivoRevisione");
		elemField.setXmlName(new javax.xml.namespace.QName("", "progressivoRevisione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("tipoRevisione");
		elemField.setXmlName(new javax.xml.namespace.QName("", "tipoRevisione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codiceNodoPresentazione");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codiceNodoPresentazione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codiceGraduatoria");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codiceGraduatoria"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("graduatoria");
		elemField.setXmlName(new javax.xml.namespace.QName("", "graduatoria"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
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
		elemField.setFieldName("stato");
		elemField.setXmlName(new javax.xml.namespace.QName("", "stato"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataPresentazione");
		elemField.setXmlName(new javax.xml.namespace.QName("", "dataPresentazione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
