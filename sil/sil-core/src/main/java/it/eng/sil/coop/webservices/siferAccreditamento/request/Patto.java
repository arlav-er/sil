/**
 * Patto.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.sil.coop.webservices.siferAccreditamento.request;

public class Patto implements java.io.Serializable {
	private java.lang.Integer indice_svantaggio;

	private java.lang.Integer indice_svantaggio_vecchio;

	private java.lang.String indice_data_riferimento;

	private java.lang.Integer profiling_150;

	private java.lang.Float profiling_150_p;

	private java.lang.String data_riferimento_150;

	private java.lang.String patto_data;

	private java.lang.Integer patto_protocollo;

	private java.lang.String patto_cpi;

	private java.lang.String data_chiusura_patto;

	private java.lang.String motivo_chiusura_patto;

	private java.lang.String tipo_misura_patto;

	private java.lang.String data_adesione;

	private java.lang.String titolo_studio_patto;

	private java.lang.Integer condizione_occupazionale;

	private java.lang.Integer durata_ricerca_occupazione;

	private java.lang.Integer durata_disoccupazione;

	private it.eng.sil.coop.webservices.siferAccreditamento.request.Svantaggio[] svantaggi;

	private java.lang.String contratto;

	private java.lang.String nome_responsabile;

	private java.lang.String cognome_responsabile;

	private java.lang.String email_responsabile;

	private java.lang.String dt_mod_patto;

	private java.lang.Integer anno_programmazione;

	private it.eng.sil.coop.webservices.siferAccreditamento.request.PoliticaAttiva[] politicheAttive;

	public Patto() {
	}

	public Patto(java.lang.Integer indice_svantaggio, java.lang.Integer indice_svantaggio_vecchio,
			java.lang.String indice_data_riferimento, java.lang.Integer profiling_150, java.lang.Float profiling_150_p,
			java.lang.String data_riferimento_150, java.lang.String patto_data, java.lang.Integer patto_protocollo,
			java.lang.String patto_cpi, java.lang.String data_chiusura_patto, java.lang.String motivo_chiusura_patto,
			java.lang.String tipo_misura_patto, java.lang.String data_adesione, java.lang.String titolo_studio_patto,
			java.lang.Integer condizione_occupazionale, java.lang.Integer durata_ricerca_occupazione,
			java.lang.Integer durata_disoccupazione,
			it.eng.sil.coop.webservices.siferAccreditamento.request.Svantaggio[] svantaggi, java.lang.String contratto,
			java.lang.String nome_responsabile, java.lang.String cognome_responsabile,
			java.lang.String email_responsabile, java.lang.String dt_mod_patto, java.lang.Integer anno_programmazione,
			it.eng.sil.coop.webservices.siferAccreditamento.request.PoliticaAttiva[] politicheAttive) {
		this.indice_svantaggio = indice_svantaggio;
		this.indice_svantaggio_vecchio = indice_svantaggio_vecchio;
		this.indice_data_riferimento = indice_data_riferimento;
		this.profiling_150 = profiling_150;
		this.profiling_150_p = profiling_150_p;
		this.data_riferimento_150 = data_riferimento_150;
		this.patto_data = patto_data;
		this.patto_protocollo = patto_protocollo;
		this.patto_cpi = patto_cpi;
		this.data_chiusura_patto = data_chiusura_patto;
		this.motivo_chiusura_patto = motivo_chiusura_patto;
		this.tipo_misura_patto = tipo_misura_patto;
		this.data_adesione = data_adesione;
		this.titolo_studio_patto = titolo_studio_patto;
		this.condizione_occupazionale = condizione_occupazionale;
		this.durata_ricerca_occupazione = durata_ricerca_occupazione;
		this.durata_disoccupazione = durata_disoccupazione;
		this.svantaggi = svantaggi;
		this.contratto = contratto;
		this.nome_responsabile = nome_responsabile;
		this.cognome_responsabile = cognome_responsabile;
		this.email_responsabile = email_responsabile;
		this.dt_mod_patto = dt_mod_patto;
		this.anno_programmazione = anno_programmazione;
		this.politicheAttive = politicheAttive;
	}

	/**
	 * Gets the indice_svantaggio value for this Patto.
	 * 
	 * @return indice_svantaggio
	 */
	public java.lang.Integer getIndice_svantaggio() {
		return indice_svantaggio;
	}

	/**
	 * Sets the indice_svantaggio value for this Patto.
	 * 
	 * @param indice_svantaggio
	 */
	public void setIndice_svantaggio(java.lang.Integer indice_svantaggio) {
		this.indice_svantaggio = indice_svantaggio;
	}

	/**
	 * Gets the indice_svantaggio_vecchio value for this Patto.
	 * 
	 * @return indice_svantaggio_vecchio
	 */
	public java.lang.Integer getIndice_svantaggio_vecchio() {
		return indice_svantaggio_vecchio;
	}

	/**
	 * Sets the indice_svantaggio_vecchio value for this Patto.
	 * 
	 * @param indice_svantaggio_vecchio
	 */
	public void setIndice_svantaggio_vecchio(java.lang.Integer indice_svantaggio_vecchio) {
		this.indice_svantaggio_vecchio = indice_svantaggio_vecchio;
	}

	/**
	 * Gets the indice_data_riferimento value for this Patto.
	 * 
	 * @return indice_data_riferimento
	 */
	public java.lang.String getIndice_data_riferimento() {
		return indice_data_riferimento;
	}

	/**
	 * Sets the indice_data_riferimento value for this Patto.
	 * 
	 * @param indice_data_riferimento
	 */
	public void setIndice_data_riferimento(java.lang.String indice_data_riferimento) {
		this.indice_data_riferimento = indice_data_riferimento;
	}

	/**
	 * Gets the profiling_150 value for this Patto.
	 * 
	 * @return profiling_150
	 */
	public java.lang.Integer getProfiling_150() {
		return profiling_150;
	}

	/**
	 * Sets the profiling_150 value for this Patto.
	 * 
	 * @param profiling_150
	 */
	public void setProfiling_150(java.lang.Integer profiling_150) {
		this.profiling_150 = profiling_150;
	}

	/**
	 * Gets the profiling_150_p value for this Patto.
	 * 
	 * @return profiling_150_p
	 */
	public java.lang.Float getProfiling_150_p() {
		return profiling_150_p;
	}

	/**
	 * Sets the profiling_150_p value for this Patto.
	 * 
	 * @param profiling_150_p
	 */
	public void setProfiling_150_p(java.lang.Float profiling_150_p) {
		this.profiling_150_p = profiling_150_p;
	}

	/**
	 * Gets the data_riferimento_150 value for this Patto.
	 * 
	 * @return data_riferimento_150
	 */
	public java.lang.String getData_riferimento_150() {
		return data_riferimento_150;
	}

	/**
	 * Sets the data_riferimento_150 value for this Patto.
	 * 
	 * @param data_riferimento_150
	 */
	public void setData_riferimento_150(java.lang.String data_riferimento_150) {
		this.data_riferimento_150 = data_riferimento_150;
	}

	/**
	 * Gets the patto_data value for this Patto.
	 * 
	 * @return patto_data
	 */
	public java.lang.String getPatto_data() {
		return patto_data;
	}

	/**
	 * Sets the patto_data value for this Patto.
	 * 
	 * @param patto_data
	 */
	public void setPatto_data(java.lang.String patto_data) {
		this.patto_data = patto_data;
	}

	/**
	 * Gets the patto_protocollo value for this Patto.
	 * 
	 * @return patto_protocollo
	 */
	public java.lang.Integer getPatto_protocollo() {
		return patto_protocollo;
	}

	/**
	 * Sets the patto_protocollo value for this Patto.
	 * 
	 * @param patto_protocollo
	 */
	public void setPatto_protocollo(java.lang.Integer patto_protocollo) {
		this.patto_protocollo = patto_protocollo;
	}

	/**
	 * Gets the patto_cpi value for this Patto.
	 * 
	 * @return patto_cpi
	 */
	public java.lang.String getPatto_cpi() {
		return patto_cpi;
	}

	/**
	 * Sets the patto_cpi value for this Patto.
	 * 
	 * @param patto_cpi
	 */
	public void setPatto_cpi(java.lang.String patto_cpi) {
		this.patto_cpi = patto_cpi;
	}

	/**
	 * Gets the data_chiusura_patto value for this Patto.
	 * 
	 * @return data_chiusura_patto
	 */
	public java.lang.String getData_chiusura_patto() {
		return data_chiusura_patto;
	}

	/**
	 * Sets the data_chiusura_patto value for this Patto.
	 * 
	 * @param data_chiusura_patto
	 */
	public void setData_chiusura_patto(java.lang.String data_chiusura_patto) {
		this.data_chiusura_patto = data_chiusura_patto;
	}

	/**
	 * Gets the motivo_chiusura_patto value for this Patto.
	 * 
	 * @return motivo_chiusura_patto
	 */
	public java.lang.String getMotivo_chiusura_patto() {
		return motivo_chiusura_patto;
	}

	/**
	 * Sets the motivo_chiusura_patto value for this Patto.
	 * 
	 * @param motivo_chiusura_patto
	 */
	public void setMotivo_chiusura_patto(java.lang.String motivo_chiusura_patto) {
		this.motivo_chiusura_patto = motivo_chiusura_patto;
	}

	/**
	 * Gets the tipo_misura_patto value for this Patto.
	 * 
	 * @return tipo_misura_patto
	 */
	public java.lang.String getTipo_misura_patto() {
		return tipo_misura_patto;
	}

	/**
	 * Sets the tipo_misura_patto value for this Patto.
	 * 
	 * @param tipo_misura_patto
	 */
	public void setTipo_misura_patto(java.lang.String tipo_misura_patto) {
		this.tipo_misura_patto = tipo_misura_patto;
	}

	/**
	 * Gets the data_adesione value for this Patto.
	 * 
	 * @return data_adesione
	 */
	public java.lang.String getData_adesione() {
		return data_adesione;
	}

	/**
	 * Sets the data_adesione value for this Patto.
	 * 
	 * @param data_adesione
	 */
	public void setData_adesione(java.lang.String data_adesione) {
		this.data_adesione = data_adesione;
	}

	/**
	 * Gets the titolo_studio_patto value for this Patto.
	 * 
	 * @return titolo_studio_patto
	 */
	public java.lang.String getTitolo_studio_patto() {
		return titolo_studio_patto;
	}

	/**
	 * Sets the titolo_studio_patto value for this Patto.
	 * 
	 * @param titolo_studio_patto
	 */
	public void setTitolo_studio_patto(java.lang.String titolo_studio_patto) {
		this.titolo_studio_patto = titolo_studio_patto;
	}

	/**
	 * Gets the condizione_occupazionale value for this Patto.
	 * 
	 * @return condizione_occupazionale
	 */
	public java.lang.Integer getCondizione_occupazionale() {
		return condizione_occupazionale;
	}

	/**
	 * Sets the condizione_occupazionale value for this Patto.
	 * 
	 * @param condizione_occupazionale
	 */
	public void setCondizione_occupazionale(java.lang.Integer condizione_occupazionale) {
		this.condizione_occupazionale = condizione_occupazionale;
	}

	/**
	 * Gets the durata_ricerca_occupazione value for this Patto.
	 * 
	 * @return durata_ricerca_occupazione
	 */
	public java.lang.Integer getDurata_ricerca_occupazione() {
		return durata_ricerca_occupazione;
	}

	/**
	 * Sets the durata_ricerca_occupazione value for this Patto.
	 * 
	 * @param durata_ricerca_occupazione
	 */
	public void setDurata_ricerca_occupazione(java.lang.Integer durata_ricerca_occupazione) {
		this.durata_ricerca_occupazione = durata_ricerca_occupazione;
	}

	/**
	 * Gets the durata_disoccupazione value for this Patto.
	 * 
	 * @return durata_disoccupazione
	 */
	public java.lang.Integer getDurata_disoccupazione() {
		return durata_disoccupazione;
	}

	/**
	 * Sets the durata_disoccupazione value for this Patto.
	 * 
	 * @param durata_disoccupazione
	 */
	public void setDurata_disoccupazione(java.lang.Integer durata_disoccupazione) {
		this.durata_disoccupazione = durata_disoccupazione;
	}

	/**
	 * Gets the svantaggi value for this Patto.
	 * 
	 * @return svantaggi
	 */
	public it.eng.sil.coop.webservices.siferAccreditamento.request.Svantaggio[] getSvantaggi() {
		return svantaggi;
	}

	/**
	 * Sets the svantaggi value for this Patto.
	 * 
	 * @param svantaggi
	 */
	public void setSvantaggi(it.eng.sil.coop.webservices.siferAccreditamento.request.Svantaggio[] svantaggi) {
		this.svantaggi = svantaggi;
	}

	/**
	 * Gets the contratto value for this Patto.
	 * 
	 * @return contratto
	 */
	public java.lang.String getContratto() {
		return contratto;
	}

	/**
	 * Sets the contratto value for this Patto.
	 * 
	 * @param contratto
	 */
	public void setContratto(java.lang.String contratto) {
		this.contratto = contratto;
	}

	/**
	 * Gets the nome_responsabile value for this Patto.
	 * 
	 * @return nome_responsabile
	 */
	public java.lang.String getNome_responsabile() {
		return nome_responsabile;
	}

	/**
	 * Sets the nome_responsabile value for this Patto.
	 * 
	 * @param nome_responsabile
	 */
	public void setNome_responsabile(java.lang.String nome_responsabile) {
		this.nome_responsabile = nome_responsabile;
	}

	/**
	 * Gets the cognome_responsabile value for this Patto.
	 * 
	 * @return cognome_responsabile
	 */
	public java.lang.String getCognome_responsabile() {
		return cognome_responsabile;
	}

	/**
	 * Sets the cognome_responsabile value for this Patto.
	 * 
	 * @param cognome_responsabile
	 */
	public void setCognome_responsabile(java.lang.String cognome_responsabile) {
		this.cognome_responsabile = cognome_responsabile;
	}

	/**
	 * Gets the email_responsabile value for this Patto.
	 * 
	 * @return email_responsabile
	 */
	public java.lang.String getEmail_responsabile() {
		return email_responsabile;
	}

	/**
	 * Sets the email_responsabile value for this Patto.
	 * 
	 * @param email_responsabile
	 */
	public void setEmail_responsabile(java.lang.String email_responsabile) {
		this.email_responsabile = email_responsabile;
	}

	/**
	 * Gets the dt_mod_patto value for this Patto.
	 * 
	 * @return dt_mod_patto
	 */
	public java.lang.String getDt_mod_patto() {
		return dt_mod_patto;
	}

	/**
	 * Sets the dt_mod_patto value for this Patto.
	 * 
	 * @param dt_mod_patto
	 */
	public void setDt_mod_patto(java.lang.String dt_mod_patto) {
		this.dt_mod_patto = dt_mod_patto;
	}

	/**
	 * Gets the anno_programmazione value for this Patto.
	 * 
	 * @return anno_programmazione
	 */
	public java.lang.Integer getAnno_programmazione() {
		return anno_programmazione;
	}

	/**
	 * Sets the anno_programmazione value for this Patto.
	 * 
	 * @param anno_programmazione
	 */
	public void setAnno_programmazione(java.lang.Integer anno_programmazione) {
		this.anno_programmazione = anno_programmazione;
	}

	/**
	 * Gets the politicheAttive value for this Patto.
	 * 
	 * @return politicheAttive
	 */
	public it.eng.sil.coop.webservices.siferAccreditamento.request.PoliticaAttiva[] getPoliticheAttive() {
		return politicheAttive;
	}

	/**
	 * Sets the politicheAttive value for this Patto.
	 * 
	 * @param politicheAttive
	 */
	public void setPoliticheAttive(
			it.eng.sil.coop.webservices.siferAccreditamento.request.PoliticaAttiva[] politicheAttive) {
		this.politicheAttive = politicheAttive;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Patto))
			return false;
		Patto other = (Patto) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true && ((this.indice_svantaggio == null && other.getIndice_svantaggio() == null)
				|| (this.indice_svantaggio != null && this.indice_svantaggio.equals(other.getIndice_svantaggio())))
				&& ((this.indice_svantaggio_vecchio == null && other.getIndice_svantaggio_vecchio() == null)
						|| (this.indice_svantaggio_vecchio != null
								&& this.indice_svantaggio_vecchio.equals(other.getIndice_svantaggio_vecchio())))
				&& ((this.indice_data_riferimento == null && other.getIndice_data_riferimento() == null)
						|| (this.indice_data_riferimento != null
								&& this.indice_data_riferimento.equals(other.getIndice_data_riferimento())))
				&& ((this.profiling_150 == null && other.getProfiling_150() == null)
						|| (this.profiling_150 != null && this.profiling_150.equals(other.getProfiling_150())))
				&& ((this.profiling_150_p == null && other.getProfiling_150_p() == null)
						|| (this.profiling_150_p != null && this.profiling_150_p.equals(other.getProfiling_150_p())))
				&& ((this.data_riferimento_150 == null && other.getData_riferimento_150() == null)
						|| (this.data_riferimento_150 != null
								&& this.data_riferimento_150.equals(other.getData_riferimento_150())))
				&& ((this.patto_data == null && other.getPatto_data() == null)
						|| (this.patto_data != null && this.patto_data.equals(other.getPatto_data())))
				&& ((this.patto_protocollo == null && other.getPatto_protocollo() == null)
						|| (this.patto_protocollo != null && this.patto_protocollo.equals(other.getPatto_protocollo())))
				&& ((this.patto_cpi == null && other.getPatto_cpi() == null)
						|| (this.patto_cpi != null && this.patto_cpi.equals(other.getPatto_cpi())))
				&& ((this.data_chiusura_patto == null && other.getData_chiusura_patto() == null)
						|| (this.data_chiusura_patto != null
								&& this.data_chiusura_patto.equals(other.getData_chiusura_patto())))
				&& ((this.motivo_chiusura_patto == null && other.getMotivo_chiusura_patto() == null)
						|| (this.motivo_chiusura_patto != null
								&& this.motivo_chiusura_patto.equals(other.getMotivo_chiusura_patto())))
				&& ((this.tipo_misura_patto == null && other.getTipo_misura_patto() == null)
						|| (this.tipo_misura_patto != null
								&& this.tipo_misura_patto.equals(other.getTipo_misura_patto())))
				&& ((this.data_adesione == null && other.getData_adesione() == null)
						|| (this.data_adesione != null && this.data_adesione.equals(other.getData_adesione())))
				&& ((this.titolo_studio_patto == null && other.getTitolo_studio_patto() == null)
						|| (this.titolo_studio_patto != null
								&& this.titolo_studio_patto.equals(other.getTitolo_studio_patto())))
				&& ((this.condizione_occupazionale == null && other.getCondizione_occupazionale() == null)
						|| (this.condizione_occupazionale != null
								&& this.condizione_occupazionale.equals(other.getCondizione_occupazionale())))
				&& ((this.durata_ricerca_occupazione == null && other.getDurata_ricerca_occupazione() == null)
						|| (this.durata_ricerca_occupazione != null
								&& this.durata_ricerca_occupazione.equals(other.getDurata_ricerca_occupazione())))
				&& ((this.durata_disoccupazione == null && other.getDurata_disoccupazione() == null)
						|| (this.durata_disoccupazione != null
								&& this.durata_disoccupazione.equals(other.getDurata_disoccupazione())))
				&& ((this.svantaggi == null && other.getSvantaggi() == null)
						|| (this.svantaggi != null && java.util.Arrays.equals(this.svantaggi, other.getSvantaggi())))
				&& ((this.contratto == null && other.getContratto() == null)
						|| (this.contratto != null && this.contratto.equals(other.getContratto())))
				&& ((this.nome_responsabile == null && other.getNome_responsabile() == null)
						|| (this.nome_responsabile != null
								&& this.nome_responsabile.equals(other.getNome_responsabile())))
				&& ((this.cognome_responsabile == null && other.getCognome_responsabile() == null)
						|| (this.cognome_responsabile != null
								&& this.cognome_responsabile.equals(other.getCognome_responsabile())))
				&& ((this.email_responsabile == null && other.getEmail_responsabile() == null)
						|| (this.email_responsabile != null
								&& this.email_responsabile.equals(other.getEmail_responsabile())))
				&& ((this.dt_mod_patto == null && other.getDt_mod_patto() == null)
						|| (this.dt_mod_patto != null && this.dt_mod_patto.equals(other.getDt_mod_patto())))
				&& ((this.anno_programmazione == null && other.getAnno_programmazione() == null)
						|| (this.anno_programmazione != null
								&& this.anno_programmazione.equals(other.getAnno_programmazione())))
				&& ((this.politicheAttive == null && other.getPoliticheAttive() == null)
						|| (this.politicheAttive != null
								&& java.util.Arrays.equals(this.politicheAttive, other.getPoliticheAttive())));
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
		if (getIndice_svantaggio() != null) {
			_hashCode += getIndice_svantaggio().hashCode();
		}
		if (getIndice_svantaggio_vecchio() != null) {
			_hashCode += getIndice_svantaggio_vecchio().hashCode();
		}
		if (getIndice_data_riferimento() != null) {
			_hashCode += getIndice_data_riferimento().hashCode();
		}
		if (getProfiling_150() != null) {
			_hashCode += getProfiling_150().hashCode();
		}
		if (getProfiling_150_p() != null) {
			_hashCode += getProfiling_150_p().hashCode();
		}
		if (getData_riferimento_150() != null) {
			_hashCode += getData_riferimento_150().hashCode();
		}
		if (getPatto_data() != null) {
			_hashCode += getPatto_data().hashCode();
		}
		if (getPatto_protocollo() != null) {
			_hashCode += getPatto_protocollo().hashCode();
		}
		if (getPatto_cpi() != null) {
			_hashCode += getPatto_cpi().hashCode();
		}
		if (getData_chiusura_patto() != null) {
			_hashCode += getData_chiusura_patto().hashCode();
		}
		if (getMotivo_chiusura_patto() != null) {
			_hashCode += getMotivo_chiusura_patto().hashCode();
		}
		if (getTipo_misura_patto() != null) {
			_hashCode += getTipo_misura_patto().hashCode();
		}
		if (getData_adesione() != null) {
			_hashCode += getData_adesione().hashCode();
		}
		if (getTitolo_studio_patto() != null) {
			_hashCode += getTitolo_studio_patto().hashCode();
		}
		if (getCondizione_occupazionale() != null) {
			_hashCode += getCondizione_occupazionale().hashCode();
		}
		if (getDurata_ricerca_occupazione() != null) {
			_hashCode += getDurata_ricerca_occupazione().hashCode();
		}
		if (getDurata_disoccupazione() != null) {
			_hashCode += getDurata_disoccupazione().hashCode();
		}
		if (getSvantaggi() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getSvantaggi()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getSvantaggi(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getContratto() != null) {
			_hashCode += getContratto().hashCode();
		}
		if (getNome_responsabile() != null) {
			_hashCode += getNome_responsabile().hashCode();
		}
		if (getCognome_responsabile() != null) {
			_hashCode += getCognome_responsabile().hashCode();
		}
		if (getEmail_responsabile() != null) {
			_hashCode += getEmail_responsabile().hashCode();
		}
		if (getDt_mod_patto() != null) {
			_hashCode += getDt_mod_patto().hashCode();
		}
		if (getAnno_programmazione() != null) {
			_hashCode += getAnno_programmazione().hashCode();
		}
		if (getPoliticheAttive() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getPoliticheAttive()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getPoliticheAttive(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(Patto.class,
			true);

	static {
		typeDesc.setXmlType(
				new javax.xml.namespace.QName("http://sifer.local/app_dev.php/WebService/sil/partecipante", "Patto"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("indice_svantaggio");
		elemField.setXmlName(new javax.xml.namespace.QName("", "indice_svantaggio"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("indice_svantaggio_vecchio");
		elemField.setXmlName(new javax.xml.namespace.QName("", "indice_svantaggio_vecchio"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("indice_data_riferimento");
		elemField.setXmlName(new javax.xml.namespace.QName("", "indice_data_riferimento"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("profiling_150");
		elemField.setXmlName(new javax.xml.namespace.QName("", "profiling_150"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("profiling_150_p");
		elemField.setXmlName(new javax.xml.namespace.QName("", "profiling_150_p"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("data_riferimento_150");
		elemField.setXmlName(new javax.xml.namespace.QName("", "data_riferimento_150"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("patto_data");
		elemField.setXmlName(new javax.xml.namespace.QName("", "patto_data"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("patto_protocollo");
		elemField.setXmlName(new javax.xml.namespace.QName("", "patto_protocollo"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("patto_cpi");
		elemField.setXmlName(new javax.xml.namespace.QName("", "patto_cpi"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("data_chiusura_patto");
		elemField.setXmlName(new javax.xml.namespace.QName("", "data_chiusura_patto"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("motivo_chiusura_patto");
		elemField.setXmlName(new javax.xml.namespace.QName("", "motivo_chiusura_patto"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("tipo_misura_patto");
		elemField.setXmlName(new javax.xml.namespace.QName("", "tipo_misura_patto"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("data_adesione");
		elemField.setXmlName(new javax.xml.namespace.QName("", "data_adesione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("titolo_studio_patto");
		elemField.setXmlName(new javax.xml.namespace.QName("", "titolo_studio_patto"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("condizione_occupazionale");
		elemField.setXmlName(new javax.xml.namespace.QName("", "condizione_occupazionale"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("durata_ricerca_occupazione");
		elemField.setXmlName(new javax.xml.namespace.QName("", "durata_ricerca_occupazione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("durata_disoccupazione");
		elemField.setXmlName(new javax.xml.namespace.QName("", "durata_disoccupazione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("svantaggi");
		elemField.setXmlName(new javax.xml.namespace.QName("", "Svantaggi"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://sifer.local/app_dev.php/WebService/sil/partecipante",
				"Svantaggio"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("contratto");
		elemField.setXmlName(new javax.xml.namespace.QName("", "contratto"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("nome_responsabile");
		elemField.setXmlName(new javax.xml.namespace.QName("", "nome_responsabile"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("cognome_responsabile");
		elemField.setXmlName(new javax.xml.namespace.QName("", "cognome_responsabile"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("email_responsabile");
		elemField.setXmlName(new javax.xml.namespace.QName("", "email_responsabile"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dt_mod_patto");
		elemField.setXmlName(new javax.xml.namespace.QName("", "dt_mod_patto"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("anno_programmazione");
		elemField.setXmlName(new javax.xml.namespace.QName("", "anno_programmazione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("politicheAttive");
		elemField.setXmlName(new javax.xml.namespace.QName("", "PoliticheAttive"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://sifer.local/app_dev.php/WebService/sil/partecipante",
				"PoliticaAttiva"));
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

}
