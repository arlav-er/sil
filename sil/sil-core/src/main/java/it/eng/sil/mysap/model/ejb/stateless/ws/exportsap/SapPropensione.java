/**
 * SapPropensione.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class SapPropensione extends it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.BaseTabellaGestioneEntity
		implements java.io.Serializable {
	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeMansioneMin deMansioneMin;

	private java.lang.Boolean flgAutomunito;

	private java.lang.Boolean flgEsperienza;

	private java.lang.Boolean flgMezzipub;

	private java.lang.Boolean flgMotomunito;

	private java.lang.Integer idSapPropensione;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaComune[] sapDisponibilitaComuneList;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaOrario[] sapDisponibilitaOrarioList;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaProvincia[] sapDisponibilitaProvinciaList;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaRegione[] sapDisponibilitaRegioneList;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaStato[] sapDisponibilitaStatoList;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaTurno[] sapDisponibilitaTurnoList;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPropTipoContratto[] sapPropTipoContrattoList;

	private java.lang.String strDescrizione;

	public SapPropensione() {
	}

	public SapPropensione(java.util.Calendar dtmIns, java.util.Calendar dtmMod, java.lang.Integer idPrincipalIns,
			java.lang.Integer idPrincipalMod,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeMansioneMin deMansioneMin,
			java.lang.Boolean flgAutomunito, java.lang.Boolean flgEsperienza, java.lang.Boolean flgMezzipub,
			java.lang.Boolean flgMotomunito, java.lang.Integer idSapPropensione,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaComune[] sapDisponibilitaComuneList,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaOrario[] sapDisponibilitaOrarioList,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaProvincia[] sapDisponibilitaProvinciaList,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaRegione[] sapDisponibilitaRegioneList,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaStato[] sapDisponibilitaStatoList,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaTurno[] sapDisponibilitaTurnoList,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPropTipoContratto[] sapPropTipoContrattoList,
			java.lang.String strDescrizione) {
		super(dtmIns, dtmMod, idPrincipalIns, idPrincipalMod);
		this.deMansioneMin = deMansioneMin;
		this.flgAutomunito = flgAutomunito;
		this.flgEsperienza = flgEsperienza;
		this.flgMezzipub = flgMezzipub;
		this.flgMotomunito = flgMotomunito;
		this.idSapPropensione = idSapPropensione;
		this.sapDisponibilitaComuneList = sapDisponibilitaComuneList;
		this.sapDisponibilitaOrarioList = sapDisponibilitaOrarioList;
		this.sapDisponibilitaProvinciaList = sapDisponibilitaProvinciaList;
		this.sapDisponibilitaRegioneList = sapDisponibilitaRegioneList;
		this.sapDisponibilitaStatoList = sapDisponibilitaStatoList;
		this.sapDisponibilitaTurnoList = sapDisponibilitaTurnoList;
		this.sapPropTipoContrattoList = sapPropTipoContrattoList;
		this.strDescrizione = strDescrizione;
	}

	/**
	 * Gets the deMansioneMin value for this SapPropensione.
	 * 
	 * @return deMansioneMin
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeMansioneMin getDeMansioneMin() {
		return deMansioneMin;
	}

	/**
	 * Sets the deMansioneMin value for this SapPropensione.
	 * 
	 * @param deMansioneMin
	 */
	public void setDeMansioneMin(it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.DeMansioneMin deMansioneMin) {
		this.deMansioneMin = deMansioneMin;
	}

	/**
	 * Gets the flgAutomunito value for this SapPropensione.
	 * 
	 * @return flgAutomunito
	 */
	public java.lang.Boolean getFlgAutomunito() {
		return flgAutomunito;
	}

	/**
	 * Sets the flgAutomunito value for this SapPropensione.
	 * 
	 * @param flgAutomunito
	 */
	public void setFlgAutomunito(java.lang.Boolean flgAutomunito) {
		this.flgAutomunito = flgAutomunito;
	}

	/**
	 * Gets the flgEsperienza value for this SapPropensione.
	 * 
	 * @return flgEsperienza
	 */
	public java.lang.Boolean getFlgEsperienza() {
		return flgEsperienza;
	}

	/**
	 * Sets the flgEsperienza value for this SapPropensione.
	 * 
	 * @param flgEsperienza
	 */
	public void setFlgEsperienza(java.lang.Boolean flgEsperienza) {
		this.flgEsperienza = flgEsperienza;
	}

	/**
	 * Gets the flgMezzipub value for this SapPropensione.
	 * 
	 * @return flgMezzipub
	 */
	public java.lang.Boolean getFlgMezzipub() {
		return flgMezzipub;
	}

	/**
	 * Sets the flgMezzipub value for this SapPropensione.
	 * 
	 * @param flgMezzipub
	 */
	public void setFlgMezzipub(java.lang.Boolean flgMezzipub) {
		this.flgMezzipub = flgMezzipub;
	}

	/**
	 * Gets the flgMotomunito value for this SapPropensione.
	 * 
	 * @return flgMotomunito
	 */
	public java.lang.Boolean getFlgMotomunito() {
		return flgMotomunito;
	}

	/**
	 * Sets the flgMotomunito value for this SapPropensione.
	 * 
	 * @param flgMotomunito
	 */
	public void setFlgMotomunito(java.lang.Boolean flgMotomunito) {
		this.flgMotomunito = flgMotomunito;
	}

	/**
	 * Gets the idSapPropensione value for this SapPropensione.
	 * 
	 * @return idSapPropensione
	 */
	public java.lang.Integer getIdSapPropensione() {
		return idSapPropensione;
	}

	/**
	 * Sets the idSapPropensione value for this SapPropensione.
	 * 
	 * @param idSapPropensione
	 */
	public void setIdSapPropensione(java.lang.Integer idSapPropensione) {
		this.idSapPropensione = idSapPropensione;
	}

	/**
	 * Gets the sapDisponibilitaComuneList value for this SapPropensione.
	 * 
	 * @return sapDisponibilitaComuneList
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaComune[] getSapDisponibilitaComuneList() {
		return sapDisponibilitaComuneList;
	}

	/**
	 * Sets the sapDisponibilitaComuneList value for this SapPropensione.
	 * 
	 * @param sapDisponibilitaComuneList
	 */
	public void setSapDisponibilitaComuneList(
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaComune[] sapDisponibilitaComuneList) {
		this.sapDisponibilitaComuneList = sapDisponibilitaComuneList;
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaComune getSapDisponibilitaComuneList(
			int i) {
		return this.sapDisponibilitaComuneList[i];
	}

	public void setSapDisponibilitaComuneList(int i,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaComune _value) {
		this.sapDisponibilitaComuneList[i] = _value;
	}

	/**
	 * Gets the sapDisponibilitaOrarioList value for this SapPropensione.
	 * 
	 * @return sapDisponibilitaOrarioList
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaOrario[] getSapDisponibilitaOrarioList() {
		return sapDisponibilitaOrarioList;
	}

	/**
	 * Sets the sapDisponibilitaOrarioList value for this SapPropensione.
	 * 
	 * @param sapDisponibilitaOrarioList
	 */
	public void setSapDisponibilitaOrarioList(
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaOrario[] sapDisponibilitaOrarioList) {
		this.sapDisponibilitaOrarioList = sapDisponibilitaOrarioList;
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaOrario getSapDisponibilitaOrarioList(
			int i) {
		return this.sapDisponibilitaOrarioList[i];
	}

	public void setSapDisponibilitaOrarioList(int i,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaOrario _value) {
		this.sapDisponibilitaOrarioList[i] = _value;
	}

	/**
	 * Gets the sapDisponibilitaProvinciaList value for this SapPropensione.
	 * 
	 * @return sapDisponibilitaProvinciaList
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaProvincia[] getSapDisponibilitaProvinciaList() {
		return sapDisponibilitaProvinciaList;
	}

	/**
	 * Sets the sapDisponibilitaProvinciaList value for this SapPropensione.
	 * 
	 * @param sapDisponibilitaProvinciaList
	 */
	public void setSapDisponibilitaProvinciaList(
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaProvincia[] sapDisponibilitaProvinciaList) {
		this.sapDisponibilitaProvinciaList = sapDisponibilitaProvinciaList;
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaProvincia getSapDisponibilitaProvinciaList(
			int i) {
		return this.sapDisponibilitaProvinciaList[i];
	}

	public void setSapDisponibilitaProvinciaList(int i,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaProvincia _value) {
		this.sapDisponibilitaProvinciaList[i] = _value;
	}

	/**
	 * Gets the sapDisponibilitaRegioneList value for this SapPropensione.
	 * 
	 * @return sapDisponibilitaRegioneList
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaRegione[] getSapDisponibilitaRegioneList() {
		return sapDisponibilitaRegioneList;
	}

	/**
	 * Sets the sapDisponibilitaRegioneList value for this SapPropensione.
	 * 
	 * @param sapDisponibilitaRegioneList
	 */
	public void setSapDisponibilitaRegioneList(
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaRegione[] sapDisponibilitaRegioneList) {
		this.sapDisponibilitaRegioneList = sapDisponibilitaRegioneList;
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaRegione getSapDisponibilitaRegioneList(
			int i) {
		return this.sapDisponibilitaRegioneList[i];
	}

	public void setSapDisponibilitaRegioneList(int i,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaRegione _value) {
		this.sapDisponibilitaRegioneList[i] = _value;
	}

	/**
	 * Gets the sapDisponibilitaStatoList value for this SapPropensione.
	 * 
	 * @return sapDisponibilitaStatoList
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaStato[] getSapDisponibilitaStatoList() {
		return sapDisponibilitaStatoList;
	}

	/**
	 * Sets the sapDisponibilitaStatoList value for this SapPropensione.
	 * 
	 * @param sapDisponibilitaStatoList
	 */
	public void setSapDisponibilitaStatoList(
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaStato[] sapDisponibilitaStatoList) {
		this.sapDisponibilitaStatoList = sapDisponibilitaStatoList;
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaStato getSapDisponibilitaStatoList(int i) {
		return this.sapDisponibilitaStatoList[i];
	}

	public void setSapDisponibilitaStatoList(int i,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaStato _value) {
		this.sapDisponibilitaStatoList[i] = _value;
	}

	/**
	 * Gets the sapDisponibilitaTurnoList value for this SapPropensione.
	 * 
	 * @return sapDisponibilitaTurnoList
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaTurno[] getSapDisponibilitaTurnoList() {
		return sapDisponibilitaTurnoList;
	}

	/**
	 * Sets the sapDisponibilitaTurnoList value for this SapPropensione.
	 * 
	 * @param sapDisponibilitaTurnoList
	 */
	public void setSapDisponibilitaTurnoList(
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaTurno[] sapDisponibilitaTurnoList) {
		this.sapDisponibilitaTurnoList = sapDisponibilitaTurnoList;
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaTurno getSapDisponibilitaTurnoList(int i) {
		return this.sapDisponibilitaTurnoList[i];
	}

	public void setSapDisponibilitaTurnoList(int i,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaTurno _value) {
		this.sapDisponibilitaTurnoList[i] = _value;
	}

	/**
	 * Gets the sapPropTipoContrattoList value for this SapPropensione.
	 * 
	 * @return sapPropTipoContrattoList
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPropTipoContratto[] getSapPropTipoContrattoList() {
		return sapPropTipoContrattoList;
	}

	/**
	 * Sets the sapPropTipoContrattoList value for this SapPropensione.
	 * 
	 * @param sapPropTipoContrattoList
	 */
	public void setSapPropTipoContrattoList(
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPropTipoContratto[] sapPropTipoContrattoList) {
		this.sapPropTipoContrattoList = sapPropTipoContrattoList;
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPropTipoContratto getSapPropTipoContrattoList(int i) {
		return this.sapPropTipoContrattoList[i];
	}

	public void setSapPropTipoContrattoList(int i,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapPropTipoContratto _value) {
		this.sapPropTipoContrattoList[i] = _value;
	}

	/**
	 * Gets the strDescrizione value for this SapPropensione.
	 * 
	 * @return strDescrizione
	 */
	public java.lang.String getStrDescrizione() {
		return strDescrizione;
	}

	/**
	 * Sets the strDescrizione value for this SapPropensione.
	 * 
	 * @param strDescrizione
	 */
	public void setStrDescrizione(java.lang.String strDescrizione) {
		this.strDescrizione = strDescrizione;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof SapPropensione))
			return false;
		SapPropensione other = (SapPropensione) obj;
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
				&& ((this.deMansioneMin == null && other.getDeMansioneMin() == null)
						|| (this.deMansioneMin != null && this.deMansioneMin.equals(other.getDeMansioneMin())))
				&& ((this.flgAutomunito == null && other.getFlgAutomunito() == null)
						|| (this.flgAutomunito != null && this.flgAutomunito.equals(other.getFlgAutomunito())))
				&& ((this.flgEsperienza == null && other.getFlgEsperienza() == null)
						|| (this.flgEsperienza != null && this.flgEsperienza.equals(other.getFlgEsperienza())))
				&& ((this.flgMezzipub == null && other.getFlgMezzipub() == null)
						|| (this.flgMezzipub != null && this.flgMezzipub.equals(other.getFlgMezzipub())))
				&& ((this.flgMotomunito == null && other.getFlgMotomunito() == null)
						|| (this.flgMotomunito != null && this.flgMotomunito.equals(other.getFlgMotomunito())))
				&& ((this.idSapPropensione == null && other.getIdSapPropensione() == null)
						|| (this.idSapPropensione != null && this.idSapPropensione.equals(other.getIdSapPropensione())))
				&& ((this.sapDisponibilitaComuneList == null && other.getSapDisponibilitaComuneList() == null)
						|| (this.sapDisponibilitaComuneList != null && java.util.Arrays
								.equals(this.sapDisponibilitaComuneList, other.getSapDisponibilitaComuneList())))
				&& ((this.sapDisponibilitaOrarioList == null && other.getSapDisponibilitaOrarioList() == null)
						|| (this.sapDisponibilitaOrarioList != null && java.util.Arrays
								.equals(this.sapDisponibilitaOrarioList, other.getSapDisponibilitaOrarioList())))
				&& ((this.sapDisponibilitaProvinciaList == null && other.getSapDisponibilitaProvinciaList() == null)
						|| (this.sapDisponibilitaProvinciaList != null && java.util.Arrays
								.equals(this.sapDisponibilitaProvinciaList, other.getSapDisponibilitaProvinciaList())))
				&& ((this.sapDisponibilitaRegioneList == null && other.getSapDisponibilitaRegioneList() == null)
						|| (this.sapDisponibilitaRegioneList != null && java.util.Arrays
								.equals(this.sapDisponibilitaRegioneList, other.getSapDisponibilitaRegioneList())))
				&& ((this.sapDisponibilitaStatoList == null && other.getSapDisponibilitaStatoList() == null)
						|| (this.sapDisponibilitaStatoList != null && java.util.Arrays
								.equals(this.sapDisponibilitaStatoList, other.getSapDisponibilitaStatoList())))
				&& ((this.sapDisponibilitaTurnoList == null && other.getSapDisponibilitaTurnoList() == null)
						|| (this.sapDisponibilitaTurnoList != null && java.util.Arrays
								.equals(this.sapDisponibilitaTurnoList, other.getSapDisponibilitaTurnoList())))
				&& ((this.sapPropTipoContrattoList == null && other.getSapPropTipoContrattoList() == null)
						|| (this.sapPropTipoContrattoList != null && java.util.Arrays
								.equals(this.sapPropTipoContrattoList, other.getSapPropTipoContrattoList())))
				&& ((this.strDescrizione == null && other.getStrDescrizione() == null)
						|| (this.strDescrizione != null && this.strDescrizione.equals(other.getStrDescrizione())));
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
		if (getDeMansioneMin() != null) {
			_hashCode += getDeMansioneMin().hashCode();
		}
		if (getFlgAutomunito() != null) {
			_hashCode += getFlgAutomunito().hashCode();
		}
		if (getFlgEsperienza() != null) {
			_hashCode += getFlgEsperienza().hashCode();
		}
		if (getFlgMezzipub() != null) {
			_hashCode += getFlgMezzipub().hashCode();
		}
		if (getFlgMotomunito() != null) {
			_hashCode += getFlgMotomunito().hashCode();
		}
		if (getIdSapPropensione() != null) {
			_hashCode += getIdSapPropensione().hashCode();
		}
		if (getSapDisponibilitaComuneList() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getSapDisponibilitaComuneList()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getSapDisponibilitaComuneList(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getSapDisponibilitaOrarioList() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getSapDisponibilitaOrarioList()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getSapDisponibilitaOrarioList(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getSapDisponibilitaProvinciaList() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getSapDisponibilitaProvinciaList()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getSapDisponibilitaProvinciaList(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getSapDisponibilitaRegioneList() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getSapDisponibilitaRegioneList()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getSapDisponibilitaRegioneList(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getSapDisponibilitaStatoList() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getSapDisponibilitaStatoList()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getSapDisponibilitaStatoList(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getSapDisponibilitaTurnoList() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getSapDisponibilitaTurnoList()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getSapDisponibilitaTurnoList(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getSapPropTipoContrattoList() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getSapPropTipoContrattoList()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getSapPropTipoContrattoList(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getStrDescrizione() != null) {
			_hashCode += getStrDescrizione().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			SapPropensione.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapPropensione"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("deMansioneMin");
		elemField.setXmlName(new javax.xml.namespace.QName("", "deMansioneMin"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"deMansioneMin"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("flgAutomunito");
		elemField.setXmlName(new javax.xml.namespace.QName("", "flgAutomunito"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("flgEsperienza");
		elemField.setXmlName(new javax.xml.namespace.QName("", "flgEsperienza"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("flgMezzipub");
		elemField.setXmlName(new javax.xml.namespace.QName("", "flgMezzipub"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("flgMotomunito");
		elemField.setXmlName(new javax.xml.namespace.QName("", "flgMotomunito"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("idSapPropensione");
		elemField.setXmlName(new javax.xml.namespace.QName("", "idSapPropensione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sapDisponibilitaComuneList");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sapDisponibilitaComuneList"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapDisponibilitaComune"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sapDisponibilitaOrarioList");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sapDisponibilitaOrarioList"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapDisponibilitaOrario"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sapDisponibilitaProvinciaList");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sapDisponibilitaProvinciaList"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapDisponibilitaProvincia"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sapDisponibilitaRegioneList");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sapDisponibilitaRegioneList"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapDisponibilitaRegione"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sapDisponibilitaStatoList");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sapDisponibilitaStatoList"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapDisponibilitaStato"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sapDisponibilitaTurnoList");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sapDisponibilitaTurnoList"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapDisponibilitaTurno"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sapPropTipoContrattoList");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sapPropTipoContrattoList"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapPropTipoContratto"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("strDescrizione");
		elemField.setXmlName(new javax.xml.namespace.QName("", "strDescrizione"));
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
