/**
 * Profiling.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0;

public class Profiling implements java.io.Serializable {
	private long IDSProfiling;

	private it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.CodiceFiscale codiceFiscale;

	private int eta;

	private it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.Genere genere;

	private java.lang.String cittadinanza;

	private java.lang.String durataPresenzaInItalia;

	private java.lang.String titoloDiStudio;

	private java.lang.String provinciaDiResidenza;

	private java.lang.String regioneDiResidenza;

	private boolean haLavoratoAlmenoUnaVolta;

	private java.lang.String condizioneProfessionaleAnnoPrecedente;

	private java.lang.Integer durataDellaDisoccupazione;

	private java.lang.String posizioneUltimaOccupazione;

	private int durataRicercaLavoro;

	private java.lang.String attualmenteIscrittoScuolaUniversitaOCorsoFormazione;

	private int numeroComponentiFamiglia;

	private boolean presenzaFigliACarico;

	private java.lang.Boolean presenzaFigliMinoriACarico;

	private java.math.BigDecimal probabilita;

	private java.util.Calendar dataInserimento;

	private java.lang.String condizioneOccupazionaleAnnoPrecedenteCalcolata;

	private java.lang.Integer durataDisoccupazioneCalcolata;

	public Profiling() {
	}

	public Profiling(long IDSProfiling,
			it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.CodiceFiscale codiceFiscale, int eta,
			it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.Genere genere,
			java.lang.String cittadinanza, java.lang.String durataPresenzaInItalia, java.lang.String titoloDiStudio,
			java.lang.String provinciaDiResidenza, java.lang.String regioneDiResidenza,
			boolean haLavoratoAlmenoUnaVolta, java.lang.String condizioneProfessionaleAnnoPrecedente,
			java.lang.Integer durataDellaDisoccupazione, java.lang.String posizioneUltimaOccupazione,
			int durataRicercaLavoro, java.lang.String attualmenteIscrittoScuolaUniversitaOCorsoFormazione,
			int numeroComponentiFamiglia, boolean presenzaFigliACarico, java.lang.Boolean presenzaFigliMinoriACarico,
			java.math.BigDecimal probabilita, java.util.Calendar dataInserimento,
			java.lang.String condizioneOccupazionaleAnnoPrecedenteCalcolata,
			java.lang.Integer durataDisoccupazioneCalcolata) {
		this.IDSProfiling = IDSProfiling;
		this.codiceFiscale = codiceFiscale;
		this.eta = eta;
		this.genere = genere;
		this.cittadinanza = cittadinanza;
		this.durataPresenzaInItalia = durataPresenzaInItalia;
		this.titoloDiStudio = titoloDiStudio;
		this.provinciaDiResidenza = provinciaDiResidenza;
		this.regioneDiResidenza = regioneDiResidenza;
		this.haLavoratoAlmenoUnaVolta = haLavoratoAlmenoUnaVolta;
		this.condizioneProfessionaleAnnoPrecedente = condizioneProfessionaleAnnoPrecedente;
		this.durataDellaDisoccupazione = durataDellaDisoccupazione;
		this.posizioneUltimaOccupazione = posizioneUltimaOccupazione;
		this.durataRicercaLavoro = durataRicercaLavoro;
		this.attualmenteIscrittoScuolaUniversitaOCorsoFormazione = attualmenteIscrittoScuolaUniversitaOCorsoFormazione;
		this.numeroComponentiFamiglia = numeroComponentiFamiglia;
		this.presenzaFigliACarico = presenzaFigliACarico;
		this.presenzaFigliMinoriACarico = presenzaFigliMinoriACarico;
		this.probabilita = probabilita;
		this.dataInserimento = dataInserimento;
		this.condizioneOccupazionaleAnnoPrecedenteCalcolata = condizioneOccupazionaleAnnoPrecedenteCalcolata;
		this.durataDisoccupazioneCalcolata = durataDisoccupazioneCalcolata;
	}

	/**
	 * Gets the IDSProfiling value for this Profiling.
	 * 
	 * @return IDSProfiling
	 */
	public long getIDSProfiling() {
		return IDSProfiling;
	}

	/**
	 * Sets the IDSProfiling value for this Profiling.
	 * 
	 * @param IDSProfiling
	 */
	public void setIDSProfiling(long IDSProfiling) {
		this.IDSProfiling = IDSProfiling;
	}

	/**
	 * Gets the codiceFiscale value for this Profiling.
	 * 
	 * @return codiceFiscale
	 */
	public it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.CodiceFiscale getCodiceFiscale() {
		return codiceFiscale;
	}

	/**
	 * Sets the codiceFiscale value for this Profiling.
	 * 
	 * @param codiceFiscale
	 */
	public void setCodiceFiscale(
			it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.CodiceFiscale codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	/**
	 * Gets the eta value for this Profiling.
	 * 
	 * @return eta
	 */
	public int getEta() {
		return eta;
	}

	/**
	 * Sets the eta value for this Profiling.
	 * 
	 * @param eta
	 */
	public void setEta(int eta) {
		this.eta = eta;
	}

	/**
	 * Gets the genere value for this Profiling.
	 * 
	 * @return genere
	 */
	public it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.Genere getGenere() {
		return genere;
	}

	/**
	 * Sets the genere value for this Profiling.
	 * 
	 * @param genere
	 */
	public void setGenere(it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.Genere genere) {
		this.genere = genere;
	}

	/**
	 * Gets the cittadinanza value for this Profiling.
	 * 
	 * @return cittadinanza
	 */
	public java.lang.String getCittadinanza() {
		return cittadinanza;
	}

	/**
	 * Sets the cittadinanza value for this Profiling.
	 * 
	 * @param cittadinanza
	 */
	public void setCittadinanza(java.lang.String cittadinanza) {
		this.cittadinanza = cittadinanza;
	}

	/**
	 * Gets the durataPresenzaInItalia value for this Profiling.
	 * 
	 * @return durataPresenzaInItalia
	 */
	public java.lang.String getDurataPresenzaInItalia() {
		return durataPresenzaInItalia;
	}

	/**
	 * Sets the durataPresenzaInItalia value for this Profiling.
	 * 
	 * @param durataPresenzaInItalia
	 */
	public void setDurataPresenzaInItalia(java.lang.String durataPresenzaInItalia) {
		this.durataPresenzaInItalia = durataPresenzaInItalia;
	}

	/**
	 * Gets the titoloDiStudio value for this Profiling.
	 * 
	 * @return titoloDiStudio
	 */
	public java.lang.String getTitoloDiStudio() {
		return titoloDiStudio;
	}

	/**
	 * Sets the titoloDiStudio value for this Profiling.
	 * 
	 * @param titoloDiStudio
	 */
	public void setTitoloDiStudio(java.lang.String titoloDiStudio) {
		this.titoloDiStudio = titoloDiStudio;
	}

	/**
	 * Gets the provinciaDiResidenza value for this Profiling.
	 * 
	 * @return provinciaDiResidenza
	 */
	public java.lang.String getProvinciaDiResidenza() {
		return provinciaDiResidenza;
	}

	/**
	 * Sets the provinciaDiResidenza value for this Profiling.
	 * 
	 * @param provinciaDiResidenza
	 */
	public void setProvinciaDiResidenza(java.lang.String provinciaDiResidenza) {
		this.provinciaDiResidenza = provinciaDiResidenza;
	}

	/**
	 * Gets the regioneDiResidenza value for this Profiling.
	 * 
	 * @return regioneDiResidenza
	 */
	public java.lang.String getRegioneDiResidenza() {
		return regioneDiResidenza;
	}

	/**
	 * Sets the regioneDiResidenza value for this Profiling.
	 * 
	 * @param regioneDiResidenza
	 */
	public void setRegioneDiResidenza(java.lang.String regioneDiResidenza) {
		this.regioneDiResidenza = regioneDiResidenza;
	}

	/**
	 * Gets the haLavoratoAlmenoUnaVolta value for this Profiling.
	 * 
	 * @return haLavoratoAlmenoUnaVolta
	 */
	public boolean isHaLavoratoAlmenoUnaVolta() {
		return haLavoratoAlmenoUnaVolta;
	}

	/**
	 * Sets the haLavoratoAlmenoUnaVolta value for this Profiling.
	 * 
	 * @param haLavoratoAlmenoUnaVolta
	 */
	public void setHaLavoratoAlmenoUnaVolta(boolean haLavoratoAlmenoUnaVolta) {
		this.haLavoratoAlmenoUnaVolta = haLavoratoAlmenoUnaVolta;
	}

	/**
	 * Gets the condizioneProfessionaleAnnoPrecedente value for this Profiling.
	 * 
	 * @return condizioneProfessionaleAnnoPrecedente
	 */
	public java.lang.String getCondizioneProfessionaleAnnoPrecedente() {
		return condizioneProfessionaleAnnoPrecedente;
	}

	/**
	 * Sets the condizioneProfessionaleAnnoPrecedente value for this Profiling.
	 * 
	 * @param condizioneProfessionaleAnnoPrecedente
	 */
	public void setCondizioneProfessionaleAnnoPrecedente(java.lang.String condizioneProfessionaleAnnoPrecedente) {
		this.condizioneProfessionaleAnnoPrecedente = condizioneProfessionaleAnnoPrecedente;
	}

	/**
	 * Gets the durataDellaDisoccupazione value for this Profiling.
	 * 
	 * @return durataDellaDisoccupazione
	 */
	public java.lang.Integer getDurataDellaDisoccupazione() {
		return durataDellaDisoccupazione;
	}

	/**
	 * Sets the durataDellaDisoccupazione value for this Profiling.
	 * 
	 * @param durataDellaDisoccupazione
	 */
	public void setDurataDellaDisoccupazione(java.lang.Integer durataDellaDisoccupazione) {
		this.durataDellaDisoccupazione = durataDellaDisoccupazione;
	}

	/**
	 * Gets the posizioneUltimaOccupazione value for this Profiling.
	 * 
	 * @return posizioneUltimaOccupazione
	 */
	public java.lang.String getPosizioneUltimaOccupazione() {
		return posizioneUltimaOccupazione;
	}

	/**
	 * Sets the posizioneUltimaOccupazione value for this Profiling.
	 * 
	 * @param posizioneUltimaOccupazione
	 */
	public void setPosizioneUltimaOccupazione(java.lang.String posizioneUltimaOccupazione) {
		this.posizioneUltimaOccupazione = posizioneUltimaOccupazione;
	}

	/**
	 * Gets the durataRicercaLavoro value for this Profiling.
	 * 
	 * @return durataRicercaLavoro
	 */
	public int getDurataRicercaLavoro() {
		return durataRicercaLavoro;
	}

	/**
	 * Sets the durataRicercaLavoro value for this Profiling.
	 * 
	 * @param durataRicercaLavoro
	 */
	public void setDurataRicercaLavoro(int durataRicercaLavoro) {
		this.durataRicercaLavoro = durataRicercaLavoro;
	}

	/**
	 * Gets the attualmenteIscrittoScuolaUniversitaOCorsoFormazione value for this Profiling.
	 * 
	 * @return attualmenteIscrittoScuolaUniversitaOCorsoFormazione
	 */
	public java.lang.String getAttualmenteIscrittoScuolaUniversitaOCorsoFormazione() {
		return attualmenteIscrittoScuolaUniversitaOCorsoFormazione;
	}

	/**
	 * Sets the attualmenteIscrittoScuolaUniversitaOCorsoFormazione value for this Profiling.
	 * 
	 * @param attualmenteIscrittoScuolaUniversitaOCorsoFormazione
	 */
	public void setAttualmenteIscrittoScuolaUniversitaOCorsoFormazione(
			java.lang.String attualmenteIscrittoScuolaUniversitaOCorsoFormazione) {
		this.attualmenteIscrittoScuolaUniversitaOCorsoFormazione = attualmenteIscrittoScuolaUniversitaOCorsoFormazione;
	}

	/**
	 * Gets the numeroComponentiFamiglia value for this Profiling.
	 * 
	 * @return numeroComponentiFamiglia
	 */
	public int getNumeroComponentiFamiglia() {
		return numeroComponentiFamiglia;
	}

	/**
	 * Sets the numeroComponentiFamiglia value for this Profiling.
	 * 
	 * @param numeroComponentiFamiglia
	 */
	public void setNumeroComponentiFamiglia(int numeroComponentiFamiglia) {
		this.numeroComponentiFamiglia = numeroComponentiFamiglia;
	}

	/**
	 * Gets the presenzaFigliACarico value for this Profiling.
	 * 
	 * @return presenzaFigliACarico
	 */
	public boolean isPresenzaFigliACarico() {
		return presenzaFigliACarico;
	}

	/**
	 * Sets the presenzaFigliACarico value for this Profiling.
	 * 
	 * @param presenzaFigliACarico
	 */
	public void setPresenzaFigliACarico(boolean presenzaFigliACarico) {
		this.presenzaFigliACarico = presenzaFigliACarico;
	}

	/**
	 * Gets the presenzaFigliMinoriACarico value for this Profiling.
	 * 
	 * @return presenzaFigliMinoriACarico
	 */
	public java.lang.Boolean getPresenzaFigliMinoriACarico() {
		return presenzaFigliMinoriACarico;
	}

	/**
	 * Sets the presenzaFigliMinoriACarico value for this Profiling.
	 * 
	 * @param presenzaFigliMinoriACarico
	 */
	public void setPresenzaFigliMinoriACarico(java.lang.Boolean presenzaFigliMinoriACarico) {
		this.presenzaFigliMinoriACarico = presenzaFigliMinoriACarico;
	}

	/**
	 * Gets the probabilita value for this Profiling.
	 * 
	 * @return probabilita
	 */
	public java.math.BigDecimal getProbabilita() {
		return probabilita;
	}

	/**
	 * Sets the probabilita value for this Profiling.
	 * 
	 * @param probabilita
	 */
	public void setProbabilita(java.math.BigDecimal probabilita) {
		this.probabilita = probabilita;
	}

	/**
	 * Gets the dataInserimento value for this Profiling.
	 * 
	 * @return dataInserimento
	 */
	public java.util.Calendar getDataInserimento() {
		return dataInserimento;
	}

	/**
	 * Sets the dataInserimento value for this Profiling.
	 * 
	 * @param dataInserimento
	 */
	public void setDataInserimento(java.util.Calendar dataInserimento) {
		this.dataInserimento = dataInserimento;
	}

	/**
	 * Gets the condizioneOccupazionaleAnnoPrecedenteCalcolata value for this Profiling.
	 * 
	 * @return condizioneOccupazionaleAnnoPrecedenteCalcolata
	 */
	public java.lang.String getCondizioneOccupazionaleAnnoPrecedenteCalcolata() {
		return condizioneOccupazionaleAnnoPrecedenteCalcolata;
	}

	/**
	 * Sets the condizioneOccupazionaleAnnoPrecedenteCalcolata value for this Profiling.
	 * 
	 * @param condizioneOccupazionaleAnnoPrecedenteCalcolata
	 */
	public void setCondizioneOccupazionaleAnnoPrecedenteCalcolata(
			java.lang.String condizioneOccupazionaleAnnoPrecedenteCalcolata) {
		this.condizioneOccupazionaleAnnoPrecedenteCalcolata = condizioneOccupazionaleAnnoPrecedenteCalcolata;
	}

	/**
	 * Gets the durataDisoccupazioneCalcolata value for this Profiling.
	 * 
	 * @return durataDisoccupazioneCalcolata
	 */
	public java.lang.Integer getDurataDisoccupazioneCalcolata() {
		return durataDisoccupazioneCalcolata;
	}

	/**
	 * Sets the durataDisoccupazioneCalcolata value for this Profiling.
	 * 
	 * @param durataDisoccupazioneCalcolata
	 */
	public void setDurataDisoccupazioneCalcolata(java.lang.Integer durataDisoccupazioneCalcolata) {
		this.durataDisoccupazioneCalcolata = durataDisoccupazioneCalcolata;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Profiling))
			return false;
		Profiling other = (Profiling) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true && this.IDSProfiling == other.getIDSProfiling()
				&& ((this.codiceFiscale == null && other.getCodiceFiscale() == null)
						|| (this.codiceFiscale != null && this.codiceFiscale.equals(other.getCodiceFiscale())))
				&& this.eta == other.getEta()
				&& ((this.genere == null && other.getGenere() == null)
						|| (this.genere != null && this.genere.equals(other.getGenere())))
				&& ((this.cittadinanza == null && other.getCittadinanza() == null)
						|| (this.cittadinanza != null && this.cittadinanza.equals(other.getCittadinanza())))
				&& ((this.durataPresenzaInItalia == null && other.getDurataPresenzaInItalia() == null)
						|| (this.durataPresenzaInItalia != null
								&& this.durataPresenzaInItalia.equals(other.getDurataPresenzaInItalia())))
				&& ((this.titoloDiStudio == null && other.getTitoloDiStudio() == null)
						|| (this.titoloDiStudio != null && this.titoloDiStudio.equals(other.getTitoloDiStudio())))
				&& ((this.provinciaDiResidenza == null && other.getProvinciaDiResidenza() == null)
						|| (this.provinciaDiResidenza != null
								&& this.provinciaDiResidenza.equals(other.getProvinciaDiResidenza())))
				&& ((this.regioneDiResidenza == null && other.getRegioneDiResidenza() == null)
						|| (this.regioneDiResidenza != null
								&& this.regioneDiResidenza.equals(other.getRegioneDiResidenza())))
				&& this.haLavoratoAlmenoUnaVolta == other.isHaLavoratoAlmenoUnaVolta()
				&& ((this.condizioneProfessionaleAnnoPrecedente == null
						&& other.getCondizioneProfessionaleAnnoPrecedente() == null)
						|| (this.condizioneProfessionaleAnnoPrecedente != null
								&& this.condizioneProfessionaleAnnoPrecedente
										.equals(other.getCondizioneProfessionaleAnnoPrecedente())))
				&& ((this.durataDellaDisoccupazione == null && other.getDurataDellaDisoccupazione() == null)
						|| (this.durataDellaDisoccupazione != null
								&& this.durataDellaDisoccupazione.equals(other.getDurataDellaDisoccupazione())))
				&& ((this.posizioneUltimaOccupazione == null && other.getPosizioneUltimaOccupazione() == null)
						|| (this.posizioneUltimaOccupazione != null
								&& this.posizioneUltimaOccupazione.equals(other.getPosizioneUltimaOccupazione())))
				&& this.durataRicercaLavoro == other.getDurataRicercaLavoro()
				&& ((this.attualmenteIscrittoScuolaUniversitaOCorsoFormazione == null
						&& other.getAttualmenteIscrittoScuolaUniversitaOCorsoFormazione() == null)
						|| (this.attualmenteIscrittoScuolaUniversitaOCorsoFormazione != null
								&& this.attualmenteIscrittoScuolaUniversitaOCorsoFormazione
										.equals(other.getAttualmenteIscrittoScuolaUniversitaOCorsoFormazione())))
				&& this.numeroComponentiFamiglia == other.getNumeroComponentiFamiglia()
				&& this.presenzaFigliACarico == other.isPresenzaFigliACarico()
				&& ((this.presenzaFigliMinoriACarico == null && other.getPresenzaFigliMinoriACarico() == null)
						|| (this.presenzaFigliMinoriACarico != null
								&& this.presenzaFigliMinoriACarico.equals(other.getPresenzaFigliMinoriACarico())))
				&& ((this.probabilita == null && other.getProbabilita() == null)
						|| (this.probabilita != null && this.probabilita.equals(other.getProbabilita())))
				&& ((this.dataInserimento == null && other.getDataInserimento() == null)
						|| (this.dataInserimento != null && this.dataInserimento.equals(other.getDataInserimento())))
				&& ((this.condizioneOccupazionaleAnnoPrecedenteCalcolata == null
						&& other.getCondizioneOccupazionaleAnnoPrecedenteCalcolata() == null)
						|| (this.condizioneOccupazionaleAnnoPrecedenteCalcolata != null
								&& this.condizioneOccupazionaleAnnoPrecedenteCalcolata
										.equals(other.getCondizioneOccupazionaleAnnoPrecedenteCalcolata())))
				&& ((this.durataDisoccupazioneCalcolata == null && other.getDurataDisoccupazioneCalcolata() == null)
						|| (this.durataDisoccupazioneCalcolata != null && this.durataDisoccupazioneCalcolata
								.equals(other.getDurataDisoccupazioneCalcolata())));
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
		_hashCode += new Long(getIDSProfiling()).hashCode();
		if (getCodiceFiscale() != null) {
			_hashCode += getCodiceFiscale().hashCode();
		}
		_hashCode += getEta();
		if (getGenere() != null) {
			_hashCode += getGenere().hashCode();
		}
		if (getCittadinanza() != null) {
			_hashCode += getCittadinanza().hashCode();
		}
		if (getDurataPresenzaInItalia() != null) {
			_hashCode += getDurataPresenzaInItalia().hashCode();
		}
		if (getTitoloDiStudio() != null) {
			_hashCode += getTitoloDiStudio().hashCode();
		}
		if (getProvinciaDiResidenza() != null) {
			_hashCode += getProvinciaDiResidenza().hashCode();
		}
		if (getRegioneDiResidenza() != null) {
			_hashCode += getRegioneDiResidenza().hashCode();
		}
		_hashCode += (isHaLavoratoAlmenoUnaVolta() ? Boolean.TRUE : Boolean.FALSE).hashCode();
		if (getCondizioneProfessionaleAnnoPrecedente() != null) {
			_hashCode += getCondizioneProfessionaleAnnoPrecedente().hashCode();
		}
		if (getDurataDellaDisoccupazione() != null) {
			_hashCode += getDurataDellaDisoccupazione().hashCode();
		}
		if (getPosizioneUltimaOccupazione() != null) {
			_hashCode += getPosizioneUltimaOccupazione().hashCode();
		}
		_hashCode += getDurataRicercaLavoro();
		if (getAttualmenteIscrittoScuolaUniversitaOCorsoFormazione() != null) {
			_hashCode += getAttualmenteIscrittoScuolaUniversitaOCorsoFormazione().hashCode();
		}
		_hashCode += getNumeroComponentiFamiglia();
		_hashCode += (isPresenzaFigliACarico() ? Boolean.TRUE : Boolean.FALSE).hashCode();
		if (getPresenzaFigliMinoriACarico() != null) {
			_hashCode += getPresenzaFigliMinoriACarico().hashCode();
		}
		if (getProbabilita() != null) {
			_hashCode += getProbabilita().hashCode();
		}
		if (getDataInserimento() != null) {
			_hashCode += getDataInserimento().hashCode();
		}
		if (getCondizioneOccupazionaleAnnoPrecedenteCalcolata() != null) {
			_hashCode += getCondizioneOccupazionaleAnnoPrecedenteCalcolata().hashCode();
		}
		if (getDurataDisoccupazioneCalcolata() != null) {
			_hashCode += getDurataDisoccupazioneCalcolata().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			Profiling.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "Profiling"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("IDSProfiling");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "IDSProfiling"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("codiceFiscale");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "CodiceFiscale"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "CodiceFiscale"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("eta");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "Eta"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("genere");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "Genere"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "Genere"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("cittadinanza");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "Cittadinanza"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("durataPresenzaInItalia");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "DurataPresenzaInItalia"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("titoloDiStudio");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "TitoloDiStudio"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("provinciaDiResidenza");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "ProvinciaDiResidenza"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("regioneDiResidenza");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "RegioneDiResidenza"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("haLavoratoAlmenoUnaVolta");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "HaLavoratoAlmenoUnaVolta"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("condizioneProfessionaleAnnoPrecedente");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0",
						"CondizioneProfessionaleAnnoPrecedente"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("durataDellaDisoccupazione");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "DurataDellaDisoccupazione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("posizioneUltimaOccupazione");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "PosizioneUltimaOccupazione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("durataRicercaLavoro");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "DurataRicercaLavoro"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("attualmenteIscrittoScuolaUniversitaOCorsoFormazione");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0",
						"AttualmenteIscrittoScuolaUniversitaOCorsoFormazione"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("numeroComponentiFamiglia");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "NumeroComponentiFamiglia"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("presenzaFigliACarico");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "PresenzaFigliACarico"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("presenzaFigliMinoriACarico");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "PresenzaFigliMinoriACarico"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("probabilita");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "Probabilita"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dataInserimento");
		elemField.setXmlName(new javax.xml.namespace.QName(
				"http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0", "DataInserimento"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("condizioneOccupazionaleAnnoPrecedenteCalcolata");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0",
						"CondizioneOccupazionaleAnnoPrecedenteCalcolata"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("durataDisoccupazioneCalcolata");
		elemField.setXmlName(
				new javax.xml.namespace.QName("http://mlps.gov.it/DataModels/InformationDelivery/ConferimentoDID/1.0",
						"DurataDisoccupazioneCalcolata"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
