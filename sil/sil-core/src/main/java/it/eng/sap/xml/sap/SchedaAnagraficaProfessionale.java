/**
 * SchedaAnagraficaProfessionale.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sap.xml.sap;

public class SchedaAnagraficaProfessionale extends
		it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseTabellaGestioneEntity implements java.io.Serializable {
	private java.lang.Integer idSap;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapAlbo[] sapAlboList;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapConoscenzeInfo[] sapConoscenzeInfoList;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapEsperienzaLav[] sapEsperienzaLavList;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapFormazione[] sapFormazioneList;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapLingua[] sapLinguaList;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPatente[] sapPatenteList;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPatentino[] sapPatentinoList;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPropensione[] sapPropensioneList;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapTitoloStudio[] sapTitoloStudioList;

	private java.lang.String strAnnotazioniColloquio;

	private java.lang.String strCodiceFiscale;

	public SchedaAnagraficaProfessionale() {
	}

	public SchedaAnagraficaProfessionale(java.util.Calendar dtmIns, java.util.Calendar dtmMod,
			java.lang.Integer idPrincipalIns, java.lang.Integer idPrincipalMod, java.lang.Integer idSap,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapAlbo[] sapAlboList,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapConoscenzeInfo[] sapConoscenzeInfoList,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapEsperienzaLav[] sapEsperienzaLavList,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapFormazione[] sapFormazioneList,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapLingua[] sapLinguaList,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPatente[] sapPatenteList,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPatentino[] sapPatentinoList,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPropensione[] sapPropensioneList,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapTitoloStudio[] sapTitoloStudioList,
			java.lang.String strAnnotazioniColloquio, java.lang.String strCodiceFiscale) {
		super(dtmIns, dtmMod, idPrincipalIns, idPrincipalMod);
		this.idSap = idSap;
		this.sapAlboList = sapAlboList;
		this.sapConoscenzeInfoList = sapConoscenzeInfoList;
		this.sapEsperienzaLavList = sapEsperienzaLavList;
		this.sapFormazioneList = sapFormazioneList;
		this.sapLinguaList = sapLinguaList;
		this.sapPatenteList = sapPatenteList;
		this.sapPatentinoList = sapPatentinoList;
		this.sapPropensioneList = sapPropensioneList;
		this.sapTitoloStudioList = sapTitoloStudioList;
		this.strAnnotazioniColloquio = strAnnotazioniColloquio;
		this.strCodiceFiscale = strCodiceFiscale;
	}

	/**
	 * Gets the idSap value for this SchedaAnagraficaProfessionale.
	 * 
	 * @return idSap
	 */
	public java.lang.Integer getIdSap() {
		return idSap;
	}

	/**
	 * Sets the idSap value for this SchedaAnagraficaProfessionale.
	 * 
	 * @param idSap
	 */
	public void setIdSap(java.lang.Integer idSap) {
		this.idSap = idSap;
	}

	/**
	 * Gets the sapAlboList value for this SchedaAnagraficaProfessionale.
	 * 
	 * @return sapAlboList
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapAlbo[] getSapAlboList() {
		return sapAlboList;
	}

	/**
	 * Sets the sapAlboList value for this SchedaAnagraficaProfessionale.
	 * 
	 * @param sapAlboList
	 */
	public void setSapAlboList(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapAlbo[] sapAlboList) {
		this.sapAlboList = sapAlboList;
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapAlbo getSapAlboList(int i) {
		return this.sapAlboList[i];
	}

	public void setSapAlboList(int i, it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapAlbo _value) {
		this.sapAlboList[i] = _value;
	}

	/**
	 * Gets the sapConoscenzeInfoList value for this SchedaAnagraficaProfessionale.
	 * 
	 * @return sapConoscenzeInfoList
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapConoscenzeInfo[] getSapConoscenzeInfoList() {
		return sapConoscenzeInfoList;
	}

	/**
	 * Sets the sapConoscenzeInfoList value for this SchedaAnagraficaProfessionale.
	 * 
	 * @param sapConoscenzeInfoList
	 */
	public void setSapConoscenzeInfoList(
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapConoscenzeInfo[] sapConoscenzeInfoList) {
		this.sapConoscenzeInfoList = sapConoscenzeInfoList;
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapConoscenzeInfo getSapConoscenzeInfoList(int i) {
		return this.sapConoscenzeInfoList[i];
	}

	public void setSapConoscenzeInfoList(int i,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapConoscenzeInfo _value) {
		this.sapConoscenzeInfoList[i] = _value;
	}

	/**
	 * Gets the sapEsperienzaLavList value for this SchedaAnagraficaProfessionale.
	 * 
	 * @return sapEsperienzaLavList
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapEsperienzaLav[] getSapEsperienzaLavList() {
		return sapEsperienzaLavList;
	}

	/**
	 * Sets the sapEsperienzaLavList value for this SchedaAnagraficaProfessionale.
	 * 
	 * @param sapEsperienzaLavList
	 */
	public void setSapEsperienzaLavList(
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapEsperienzaLav[] sapEsperienzaLavList) {
		this.sapEsperienzaLavList = sapEsperienzaLavList;
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapEsperienzaLav getSapEsperienzaLavList(int i) {
		return this.sapEsperienzaLavList[i];
	}

	public void setSapEsperienzaLavList(int i,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapEsperienzaLav _value) {
		this.sapEsperienzaLavList[i] = _value;
	}

	/**
	 * Gets the sapFormazioneList value for this SchedaAnagraficaProfessionale.
	 * 
	 * @return sapFormazioneList
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapFormazione[] getSapFormazioneList() {
		return sapFormazioneList;
	}

	/**
	 * Sets the sapFormazioneList value for this SchedaAnagraficaProfessionale.
	 * 
	 * @param sapFormazioneList
	 */
	public void setSapFormazioneList(
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapFormazione[] sapFormazioneList) {
		this.sapFormazioneList = sapFormazioneList;
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapFormazione getSapFormazioneList(int i) {
		return this.sapFormazioneList[i];
	}

	public void setSapFormazioneList(int i, it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapFormazione _value) {
		this.sapFormazioneList[i] = _value;
	}

	/**
	 * Gets the sapLinguaList value for this SchedaAnagraficaProfessionale.
	 * 
	 * @return sapLinguaList
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapLingua[] getSapLinguaList() {
		return sapLinguaList;
	}

	/**
	 * Sets the sapLinguaList value for this SchedaAnagraficaProfessionale.
	 * 
	 * @param sapLinguaList
	 */
	public void setSapLinguaList(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapLingua[] sapLinguaList) {
		this.sapLinguaList = sapLinguaList;
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapLingua getSapLinguaList(int i) {
		return this.sapLinguaList[i];
	}

	public void setSapLinguaList(int i, it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapLingua _value) {
		this.sapLinguaList[i] = _value;
	}

	/**
	 * Gets the sapPatenteList value for this SchedaAnagraficaProfessionale.
	 * 
	 * @return sapPatenteList
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPatente[] getSapPatenteList() {
		return sapPatenteList;
	}

	/**
	 * Sets the sapPatenteList value for this SchedaAnagraficaProfessionale.
	 * 
	 * @param sapPatenteList
	 */
	public void setSapPatenteList(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPatente[] sapPatenteList) {
		this.sapPatenteList = sapPatenteList;
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPatente getSapPatenteList(int i) {
		return this.sapPatenteList[i];
	}

	public void setSapPatenteList(int i, it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPatente _value) {
		this.sapPatenteList[i] = _value;
	}

	/**
	 * Gets the sapPatentinoList value for this SchedaAnagraficaProfessionale.
	 * 
	 * @return sapPatentinoList
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPatentino[] getSapPatentinoList() {
		return sapPatentinoList;
	}

	/**
	 * Sets the sapPatentinoList value for this SchedaAnagraficaProfessionale.
	 * 
	 * @param sapPatentinoList
	 */
	public void setSapPatentinoList(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPatentino[] sapPatentinoList) {
		this.sapPatentinoList = sapPatentinoList;
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPatentino getSapPatentinoList(int i) {
		return this.sapPatentinoList[i];
	}

	public void setSapPatentinoList(int i, it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPatentino _value) {
		this.sapPatentinoList[i] = _value;
	}

	/**
	 * Gets the sapPropensioneList value for this SchedaAnagraficaProfessionale.
	 * 
	 * @return sapPropensioneList
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPropensione[] getSapPropensioneList() {
		return sapPropensioneList;
	}

	/**
	 * Sets the sapPropensioneList value for this SchedaAnagraficaProfessionale.
	 * 
	 * @param sapPropensioneList
	 */
	public void setSapPropensioneList(
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPropensione[] sapPropensioneList) {
		this.sapPropensioneList = sapPropensioneList;
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPropensione getSapPropensioneList(int i) {
		return this.sapPropensioneList[i];
	}

	public void setSapPropensioneList(int i, it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPropensione _value) {
		this.sapPropensioneList[i] = _value;
	}

	/**
	 * Gets the sapTitoloStudioList value for this SchedaAnagraficaProfessionale.
	 * 
	 * @return sapTitoloStudioList
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapTitoloStudio[] getSapTitoloStudioList() {
		return sapTitoloStudioList;
	}

	/**
	 * Sets the sapTitoloStudioList value for this SchedaAnagraficaProfessionale.
	 * 
	 * @param sapTitoloStudioList
	 */
	public void setSapTitoloStudioList(
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapTitoloStudio[] sapTitoloStudioList) {
		this.sapTitoloStudioList = sapTitoloStudioList;
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapTitoloStudio getSapTitoloStudioList(int i) {
		return this.sapTitoloStudioList[i];
	}

	public void setSapTitoloStudioList(int i,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapTitoloStudio _value) {
		this.sapTitoloStudioList[i] = _value;
	}

	/**
	 * Gets the strAnnotazioniColloquio value for this SchedaAnagraficaProfessionale.
	 * 
	 * @return strAnnotazioniColloquio
	 */
	public java.lang.String getStrAnnotazioniColloquio() {
		return strAnnotazioniColloquio;
	}

	/**
	 * Sets the strAnnotazioniColloquio value for this SchedaAnagraficaProfessionale.
	 * 
	 * @param strAnnotazioniColloquio
	 */
	public void setStrAnnotazioniColloquio(java.lang.String strAnnotazioniColloquio) {
		this.strAnnotazioniColloquio = strAnnotazioniColloquio;
	}

	/**
	 * Gets the strCodiceFiscale value for this SchedaAnagraficaProfessionale.
	 * 
	 * @return strCodiceFiscale
	 */
	public java.lang.String getStrCodiceFiscale() {
		return strCodiceFiscale;
	}

	/**
	 * Sets the strCodiceFiscale value for this SchedaAnagraficaProfessionale.
	 * 
	 * @param strCodiceFiscale
	 */
	public void setStrCodiceFiscale(java.lang.String strCodiceFiscale) {
		this.strCodiceFiscale = strCodiceFiscale;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof SchedaAnagraficaProfessionale))
			return false;
		SchedaAnagraficaProfessionale other = (SchedaAnagraficaProfessionale) obj;
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
				&& ((this.idSap == null && other.getIdSap() == null)
						|| (this.idSap != null && this.idSap.equals(other.getIdSap())))
				&& ((this.sapAlboList == null && other.getSapAlboList() == null) || (this.sapAlboList != null
						&& java.util.Arrays.equals(this.sapAlboList, other.getSapAlboList())))
				&& ((this.sapConoscenzeInfoList == null && other.getSapConoscenzeInfoList() == null)
						|| (this.sapConoscenzeInfoList != null && java.util.Arrays.equals(this.sapConoscenzeInfoList,
								other.getSapConoscenzeInfoList())))
				&& ((this.sapEsperienzaLavList == null && other.getSapEsperienzaLavList() == null)
						|| (this.sapEsperienzaLavList != null
								&& java.util.Arrays.equals(this.sapEsperienzaLavList, other.getSapEsperienzaLavList())))
				&& ((this.sapFormazioneList == null && other.getSapFormazioneList() == null)
						|| (this.sapFormazioneList != null
								&& java.util.Arrays.equals(this.sapFormazioneList, other.getSapFormazioneList())))
				&& ((this.sapLinguaList == null && other.getSapLinguaList() == null) || (this.sapLinguaList != null
						&& java.util.Arrays.equals(this.sapLinguaList, other.getSapLinguaList())))
				&& ((this.sapPatenteList == null && other.getSapPatenteList() == null) || (this.sapPatenteList != null
						&& java.util.Arrays.equals(this.sapPatenteList, other.getSapPatenteList())))
				&& ((this.sapPatentinoList == null && other.getSapPatentinoList() == null)
						|| (this.sapPatentinoList != null
								&& java.util.Arrays.equals(this.sapPatentinoList, other.getSapPatentinoList())))
				&& ((this.sapPropensioneList == null && other.getSapPropensioneList() == null)
						|| (this.sapPropensioneList != null
								&& java.util.Arrays.equals(this.sapPropensioneList, other.getSapPropensioneList())))
				&& ((this.sapTitoloStudioList == null && other.getSapTitoloStudioList() == null)
						|| (this.sapTitoloStudioList != null
								&& java.util.Arrays.equals(this.sapTitoloStudioList, other.getSapTitoloStudioList())))
				&& ((this.strAnnotazioniColloquio == null && other.getStrAnnotazioniColloquio() == null)
						|| (this.strAnnotazioniColloquio != null
								&& this.strAnnotazioniColloquio.equals(other.getStrAnnotazioniColloquio())))
				&& ((this.strCodiceFiscale == null && other.getStrCodiceFiscale() == null)
						|| (this.strCodiceFiscale != null
								&& this.strCodiceFiscale.equals(other.getStrCodiceFiscale())));
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
		if (getIdSap() != null) {
			_hashCode += getIdSap().hashCode();
		}
		if (getSapAlboList() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getSapAlboList()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getSapAlboList(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getSapConoscenzeInfoList() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getSapConoscenzeInfoList()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getSapConoscenzeInfoList(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getSapEsperienzaLavList() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getSapEsperienzaLavList()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getSapEsperienzaLavList(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getSapFormazioneList() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getSapFormazioneList()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getSapFormazioneList(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getSapLinguaList() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getSapLinguaList()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getSapLinguaList(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getSapPatenteList() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getSapPatenteList()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getSapPatenteList(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getSapPatentinoList() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getSapPatentinoList()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getSapPatentinoList(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getSapPropensioneList() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getSapPropensioneList()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getSapPropensioneList(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getSapTitoloStudioList() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getSapTitoloStudioList()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getSapTitoloStudioList(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getStrAnnotazioniColloquio() != null) {
			_hashCode += getStrAnnotazioniColloquio().hashCode();
		}
		if (getStrCodiceFiscale() != null) {
			_hashCode += getStrCodiceFiscale().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			SchedaAnagraficaProfessionale.class, true);

	static {
		typeDesc.setXmlType(
				new javax.xml.namespace.QName("http://sap.eng.it/xml/sap", "schedaAnagraficaProfessionale"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idSap");
		elemField.setXmlName(new javax.xml.namespace.QName("", "idSap"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sapAlboList");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sapAlboList"));
		elemField.setXmlType(
				new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/", "sapAlbo"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sapConoscenzeInfoList");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sapConoscenzeInfoList"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapConoscenzeInfo"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sapEsperienzaLavList");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sapEsperienzaLavList"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapEsperienzaLav"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sapFormazioneList");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sapFormazioneList"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapFormazione"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sapLinguaList");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sapLinguaList"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapLingua"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sapPatenteList");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sapPatenteList"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapPatente"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sapPatentinoList");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sapPatentinoList"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapPatentino"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sapPropensioneList");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sapPropensioneList"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapPropensione"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sapTitoloStudioList");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sapTitoloStudioList"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapTitoloStudio"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strAnnotazioniColloquio");
		elemField.setXmlName(new javax.xml.namespace.QName("", "strAnnotazioniColloquio"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strCodiceFiscale");
		elemField.setXmlName(new javax.xml.namespace.QName("", "strCodiceFiscale"));
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
