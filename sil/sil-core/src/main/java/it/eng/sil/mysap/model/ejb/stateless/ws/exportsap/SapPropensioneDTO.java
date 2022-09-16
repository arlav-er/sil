/**
 * SapPropensioneDTO.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.mysap.model.ejb.stateless.ws.exportsap;

public class SapPropensioneDTO implements java.io.Serializable {
	private java.lang.String codMansione;

	private java.lang.String codMansioneDesc;

	private java.lang.String codMansioneMin;

	private java.lang.String codMansioneMinDesc;

	private java.lang.Boolean flgAutomunito;

	private java.lang.Boolean flgEsperienza;

	private java.lang.Boolean flgMezzipub;

	private java.lang.Boolean flgMotomunito;

	private java.lang.Integer orderField;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaComuneDTO[] sapDisponibilitaComuneList;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaOrarioDTO[] sapDisponibilitaOrarioList;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaProvinciaDTO[] sapDisponibilitaProvinciaList;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaRegioneDTO[] sapDisponibilitaRegioneList;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaStatoDTO[] sapDisponibilitaStatoList;

	private it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaTurnoDTO[] sapDisponibilitaTurnoList;

	private java.lang.String strDescrizione;

	public SapPropensioneDTO() {
	}

	public SapPropensioneDTO(java.lang.String codMansione, java.lang.String codMansioneDesc,
			java.lang.String codMansioneMin, java.lang.String codMansioneMinDesc, java.lang.Boolean flgAutomunito,
			java.lang.Boolean flgEsperienza, java.lang.Boolean flgMezzipub, java.lang.Boolean flgMotomunito,
			java.lang.Integer orderField,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaComuneDTO[] sapDisponibilitaComuneList,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaOrarioDTO[] sapDisponibilitaOrarioList,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaProvinciaDTO[] sapDisponibilitaProvinciaList,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaRegioneDTO[] sapDisponibilitaRegioneList,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaStatoDTO[] sapDisponibilitaStatoList,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaTurnoDTO[] sapDisponibilitaTurnoList,
			java.lang.String strDescrizione) {
		this.codMansione = codMansione;
		this.codMansioneDesc = codMansioneDesc;
		this.codMansioneMin = codMansioneMin;
		this.codMansioneMinDesc = codMansioneMinDesc;
		this.flgAutomunito = flgAutomunito;
		this.flgEsperienza = flgEsperienza;
		this.flgMezzipub = flgMezzipub;
		this.flgMotomunito = flgMotomunito;
		this.orderField = orderField;
		this.sapDisponibilitaComuneList = sapDisponibilitaComuneList;
		this.sapDisponibilitaOrarioList = sapDisponibilitaOrarioList;
		this.sapDisponibilitaProvinciaList = sapDisponibilitaProvinciaList;
		this.sapDisponibilitaRegioneList = sapDisponibilitaRegioneList;
		this.sapDisponibilitaStatoList = sapDisponibilitaStatoList;
		this.sapDisponibilitaTurnoList = sapDisponibilitaTurnoList;
		this.strDescrizione = strDescrizione;
	}

	/**
	 * Gets the codMansione value for this SapPropensioneDTO.
	 * 
	 * @return codMansione
	 */
	public java.lang.String getCodMansione() {
		return codMansione;
	}

	/**
	 * Sets the codMansione value for this SapPropensioneDTO.
	 * 
	 * @param codMansione
	 */
	public void setCodMansione(java.lang.String codMansione) {
		this.codMansione = codMansione;
	}

	/**
	 * Gets the codMansioneDesc value for this SapPropensioneDTO.
	 * 
	 * @return codMansioneDesc
	 */
	public java.lang.String getCodMansioneDesc() {
		return codMansioneDesc;
	}

	/**
	 * Sets the codMansioneDesc value for this SapPropensioneDTO.
	 * 
	 * @param codMansioneDesc
	 */
	public void setCodMansioneDesc(java.lang.String codMansioneDesc) {
		this.codMansioneDesc = codMansioneDesc;
	}

	/**
	 * Gets the codMansioneMin value for this SapPropensioneDTO.
	 * 
	 * @return codMansioneMin
	 */
	public java.lang.String getCodMansioneMin() {
		return codMansioneMin;
	}

	/**
	 * Sets the codMansioneMin value for this SapPropensioneDTO.
	 * 
	 * @param codMansioneMin
	 */
	public void setCodMansioneMin(java.lang.String codMansioneMin) {
		this.codMansioneMin = codMansioneMin;
	}

	/**
	 * Gets the codMansioneMinDesc value for this SapPropensioneDTO.
	 * 
	 * @return codMansioneMinDesc
	 */
	public java.lang.String getCodMansioneMinDesc() {
		return codMansioneMinDesc;
	}

	/**
	 * Sets the codMansioneMinDesc value for this SapPropensioneDTO.
	 * 
	 * @param codMansioneMinDesc
	 */
	public void setCodMansioneMinDesc(java.lang.String codMansioneMinDesc) {
		this.codMansioneMinDesc = codMansioneMinDesc;
	}

	/**
	 * Gets the flgAutomunito value for this SapPropensioneDTO.
	 * 
	 * @return flgAutomunito
	 */
	public java.lang.Boolean getFlgAutomunito() {
		return flgAutomunito;
	}

	/**
	 * Sets the flgAutomunito value for this SapPropensioneDTO.
	 * 
	 * @param flgAutomunito
	 */
	public void setFlgAutomunito(java.lang.Boolean flgAutomunito) {
		this.flgAutomunito = flgAutomunito;
	}

	/**
	 * Gets the flgEsperienza value for this SapPropensioneDTO.
	 * 
	 * @return flgEsperienza
	 */
	public java.lang.Boolean getFlgEsperienza() {
		return flgEsperienza;
	}

	/**
	 * Sets the flgEsperienza value for this SapPropensioneDTO.
	 * 
	 * @param flgEsperienza
	 */
	public void setFlgEsperienza(java.lang.Boolean flgEsperienza) {
		this.flgEsperienza = flgEsperienza;
	}

	/**
	 * Gets the flgMezzipub value for this SapPropensioneDTO.
	 * 
	 * @return flgMezzipub
	 */
	public java.lang.Boolean getFlgMezzipub() {
		return flgMezzipub;
	}

	/**
	 * Sets the flgMezzipub value for this SapPropensioneDTO.
	 * 
	 * @param flgMezzipub
	 */
	public void setFlgMezzipub(java.lang.Boolean flgMezzipub) {
		this.flgMezzipub = flgMezzipub;
	}

	/**
	 * Gets the flgMotomunito value for this SapPropensioneDTO.
	 * 
	 * @return flgMotomunito
	 */
	public java.lang.Boolean getFlgMotomunito() {
		return flgMotomunito;
	}

	/**
	 * Sets the flgMotomunito value for this SapPropensioneDTO.
	 * 
	 * @param flgMotomunito
	 */
	public void setFlgMotomunito(java.lang.Boolean flgMotomunito) {
		this.flgMotomunito = flgMotomunito;
	}

	/**
	 * Gets the orderField value for this SapPropensioneDTO.
	 * 
	 * @return orderField
	 */
	public java.lang.Integer getOrderField() {
		return orderField;
	}

	/**
	 * Sets the orderField value for this SapPropensioneDTO.
	 * 
	 * @param orderField
	 */
	public void setOrderField(java.lang.Integer orderField) {
		this.orderField = orderField;
	}

	/**
	 * Gets the sapDisponibilitaComuneList value for this SapPropensioneDTO.
	 * 
	 * @return sapDisponibilitaComuneList
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaComuneDTO[] getSapDisponibilitaComuneList() {
		return sapDisponibilitaComuneList;
	}

	/**
	 * Sets the sapDisponibilitaComuneList value for this SapPropensioneDTO.
	 * 
	 * @param sapDisponibilitaComuneList
	 */
	public void setSapDisponibilitaComuneList(
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaComuneDTO[] sapDisponibilitaComuneList) {
		this.sapDisponibilitaComuneList = sapDisponibilitaComuneList;
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaComuneDTO getSapDisponibilitaComuneList(
			int i) {
		return this.sapDisponibilitaComuneList[i];
	}

	public void setSapDisponibilitaComuneList(int i,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaComuneDTO _value) {
		this.sapDisponibilitaComuneList[i] = _value;
	}

	/**
	 * Gets the sapDisponibilitaOrarioList value for this SapPropensioneDTO.
	 * 
	 * @return sapDisponibilitaOrarioList
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaOrarioDTO[] getSapDisponibilitaOrarioList() {
		return sapDisponibilitaOrarioList;
	}

	/**
	 * Sets the sapDisponibilitaOrarioList value for this SapPropensioneDTO.
	 * 
	 * @param sapDisponibilitaOrarioList
	 */
	public void setSapDisponibilitaOrarioList(
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaOrarioDTO[] sapDisponibilitaOrarioList) {
		this.sapDisponibilitaOrarioList = sapDisponibilitaOrarioList;
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaOrarioDTO getSapDisponibilitaOrarioList(
			int i) {
		return this.sapDisponibilitaOrarioList[i];
	}

	public void setSapDisponibilitaOrarioList(int i,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaOrarioDTO _value) {
		this.sapDisponibilitaOrarioList[i] = _value;
	}

	/**
	 * Gets the sapDisponibilitaProvinciaList value for this SapPropensioneDTO.
	 * 
	 * @return sapDisponibilitaProvinciaList
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaProvinciaDTO[] getSapDisponibilitaProvinciaList() {
		return sapDisponibilitaProvinciaList;
	}

	/**
	 * Sets the sapDisponibilitaProvinciaList value for this SapPropensioneDTO.
	 * 
	 * @param sapDisponibilitaProvinciaList
	 */
	public void setSapDisponibilitaProvinciaList(
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaProvinciaDTO[] sapDisponibilitaProvinciaList) {
		this.sapDisponibilitaProvinciaList = sapDisponibilitaProvinciaList;
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaProvinciaDTO getSapDisponibilitaProvinciaList(
			int i) {
		return this.sapDisponibilitaProvinciaList[i];
	}

	public void setSapDisponibilitaProvinciaList(int i,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaProvinciaDTO _value) {
		this.sapDisponibilitaProvinciaList[i] = _value;
	}

	/**
	 * Gets the sapDisponibilitaRegioneList value for this SapPropensioneDTO.
	 * 
	 * @return sapDisponibilitaRegioneList
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaRegioneDTO[] getSapDisponibilitaRegioneList() {
		return sapDisponibilitaRegioneList;
	}

	/**
	 * Sets the sapDisponibilitaRegioneList value for this SapPropensioneDTO.
	 * 
	 * @param sapDisponibilitaRegioneList
	 */
	public void setSapDisponibilitaRegioneList(
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaRegioneDTO[] sapDisponibilitaRegioneList) {
		this.sapDisponibilitaRegioneList = sapDisponibilitaRegioneList;
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaRegioneDTO getSapDisponibilitaRegioneList(
			int i) {
		return this.sapDisponibilitaRegioneList[i];
	}

	public void setSapDisponibilitaRegioneList(int i,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaRegioneDTO _value) {
		this.sapDisponibilitaRegioneList[i] = _value;
	}

	/**
	 * Gets the sapDisponibilitaStatoList value for this SapPropensioneDTO.
	 * 
	 * @return sapDisponibilitaStatoList
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaStatoDTO[] getSapDisponibilitaStatoList() {
		return sapDisponibilitaStatoList;
	}

	/**
	 * Sets the sapDisponibilitaStatoList value for this SapPropensioneDTO.
	 * 
	 * @param sapDisponibilitaStatoList
	 */
	public void setSapDisponibilitaStatoList(
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaStatoDTO[] sapDisponibilitaStatoList) {
		this.sapDisponibilitaStatoList = sapDisponibilitaStatoList;
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaStatoDTO getSapDisponibilitaStatoList(
			int i) {
		return this.sapDisponibilitaStatoList[i];
	}

	public void setSapDisponibilitaStatoList(int i,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaStatoDTO _value) {
		this.sapDisponibilitaStatoList[i] = _value;
	}

	/**
	 * Gets the sapDisponibilitaTurnoList value for this SapPropensioneDTO.
	 * 
	 * @return sapDisponibilitaTurnoList
	 */
	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaTurnoDTO[] getSapDisponibilitaTurnoList() {
		return sapDisponibilitaTurnoList;
	}

	/**
	 * Sets the sapDisponibilitaTurnoList value for this SapPropensioneDTO.
	 * 
	 * @param sapDisponibilitaTurnoList
	 */
	public void setSapDisponibilitaTurnoList(
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaTurnoDTO[] sapDisponibilitaTurnoList) {
		this.sapDisponibilitaTurnoList = sapDisponibilitaTurnoList;
	}

	public it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaTurnoDTO getSapDisponibilitaTurnoList(
			int i) {
		return this.sapDisponibilitaTurnoList[i];
	}

	public void setSapDisponibilitaTurnoList(int i,
			it.eng.sil.mysap.model.ejb.stateless.ws.exportsap.SapDisponibilitaTurnoDTO _value) {
		this.sapDisponibilitaTurnoList[i] = _value;
	}

	/**
	 * Gets the strDescrizione value for this SapPropensioneDTO.
	 * 
	 * @return strDescrizione
	 */
	public java.lang.String getStrDescrizione() {
		return strDescrizione;
	}

	/**
	 * Sets the strDescrizione value for this SapPropensioneDTO.
	 * 
	 * @param strDescrizione
	 */
	public void setStrDescrizione(java.lang.String strDescrizione) {
		this.strDescrizione = strDescrizione;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof SapPropensioneDTO))
			return false;
		SapPropensioneDTO other = (SapPropensioneDTO) obj;
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
				&& ((this.codMansione == null && other.getCodMansione() == null)
						|| (this.codMansione != null && this.codMansione.equals(other.getCodMansione())))
				&& ((this.codMansioneDesc == null && other.getCodMansioneDesc() == null)
						|| (this.codMansioneDesc != null && this.codMansioneDesc.equals(other.getCodMansioneDesc())))
				&& ((this.codMansioneMin == null && other.getCodMansioneMin() == null)
						|| (this.codMansioneMin != null && this.codMansioneMin.equals(other.getCodMansioneMin())))
				&& ((this.codMansioneMinDesc == null && other.getCodMansioneMinDesc() == null)
						|| (this.codMansioneMinDesc != null
								&& this.codMansioneMinDesc.equals(other.getCodMansioneMinDesc())))
				&& ((this.flgAutomunito == null && other.getFlgAutomunito() == null)
						|| (this.flgAutomunito != null && this.flgAutomunito.equals(other.getFlgAutomunito())))
				&& ((this.flgEsperienza == null && other.getFlgEsperienza() == null)
						|| (this.flgEsperienza != null && this.flgEsperienza.equals(other.getFlgEsperienza())))
				&& ((this.flgMezzipub == null && other.getFlgMezzipub() == null)
						|| (this.flgMezzipub != null && this.flgMezzipub.equals(other.getFlgMezzipub())))
				&& ((this.flgMotomunito == null && other.getFlgMotomunito() == null)
						|| (this.flgMotomunito != null && this.flgMotomunito.equals(other.getFlgMotomunito())))
				&& ((this.orderField == null && other.getOrderField() == null)
						|| (this.orderField != null && this.orderField.equals(other.getOrderField())))
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
		int _hashCode = 1;
		if (getCodMansione() != null) {
			_hashCode += getCodMansione().hashCode();
		}
		if (getCodMansioneDesc() != null) {
			_hashCode += getCodMansioneDesc().hashCode();
		}
		if (getCodMansioneMin() != null) {
			_hashCode += getCodMansioneMin().hashCode();
		}
		if (getCodMansioneMinDesc() != null) {
			_hashCode += getCodMansioneMinDesc().hashCode();
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
		if (getOrderField() != null) {
			_hashCode += getOrderField().hashCode();
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
		if (getStrDescrizione() != null) {
			_hashCode += getStrDescrizione().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			SapPropensioneDTO.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapPropensioneDTO"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codMansione");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codMansione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codMansioneDesc");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codMansioneDesc"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codMansioneMin");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codMansioneMin"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codMansioneMinDesc");
		elemField.setXmlName(new javax.xml.namespace.QName("", "codMansioneMinDesc"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
		elemField.setFieldName("orderField");
		elemField.setXmlName(new javax.xml.namespace.QName("", "orderField"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sapDisponibilitaComuneList");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sapDisponibilitaComuneList"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapDisponibilitaComuneDTO"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sapDisponibilitaOrarioList");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sapDisponibilitaOrarioList"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapDisponibilitaOrarioDTO"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sapDisponibilitaProvinciaList");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sapDisponibilitaProvinciaList"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapDisponibilitaProvinciaDTO"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sapDisponibilitaRegioneList");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sapDisponibilitaRegioneList"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapDisponibilitaRegioneDTO"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sapDisponibilitaStatoList");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sapDisponibilitaStatoList"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapDisponibilitaStatoDTO"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		elemField.setMaxOccursUnbounded(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sapDisponibilitaTurnoList");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sapDisponibilitaTurnoList"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://exportsap.ws.stateless.ejb.model.mysap.sil.eng.it/",
				"sapDisponibilitaTurnoDTO"));
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
