//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2020.10.08 alle 08:51:54 AM CEST 
//


package it.sintesi.getprotocollo.out;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per anonymous complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Esito">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="CodEsito" type="{}Esito"/>
 *                   &lt;element name="Descrizione" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="TipologiaRichiesta" type="{}TipoServizio" minOccurs="0"/>
 *         &lt;element name="CodiceFiscaleRichiedente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Protocollo" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="NumeroProtocollo" type="{}Stringa100"/>
 *                   &lt;element name="DataProtocollo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="IdProvincia" type="{}Stringa3"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="StatoOccupazionale" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="CodCPI_netlabor" type="{}Stringa100"/>
 *                   &lt;element name="CodSTO" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="DescrizioneSTO" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="dtDecorrenzaSTO" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "esito",
    "tipologiaRichiesta",
    "codiceFiscaleRichiedente",
    "protocollo",
    "statoOccupazionale"
})
@XmlRootElement(name = "GetProtocollo")
public class GetProtocollo {

    @XmlElement(name = "Esito", required = true)
    protected Esito esito;
    @XmlElement(name = "TipologiaRichiesta")
    protected String tipologiaRichiesta;
    @XmlElement(name = "CodiceFiscaleRichiedente")
    protected String codiceFiscaleRichiedente;
    @XmlElement(name = "Protocollo")
    protected Protocollo protocollo;
    @XmlElement(name = "StatoOccupazionale")
    protected StatoOccupazionale statoOccupazionale;

    /**
     * Recupera il valore della proprietà esito.
     * 
     * @return
     *     possible object is
     *     {@link Esito }
     *     
     */
    public Esito getEsito() {
        return esito;
    }

    /**
     * Imposta il valore della proprietà esito.
     * 
     * @param value
     *     allowed object is
     *     {@link Esito }
     *     
     */
    public void setEsito(Esito value) {
        this.esito = value;
    }

    /**
     * Recupera il valore della proprietà tipologiaRichiesta.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipologiaRichiesta() {
        return tipologiaRichiesta;
    }

    /**
     * Imposta il valore della proprietà tipologiaRichiesta.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipologiaRichiesta(String value) {
        this.tipologiaRichiesta = value;
    }

    /**
     * Recupera il valore della proprietà codiceFiscaleRichiedente.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodiceFiscaleRichiedente() {
        return codiceFiscaleRichiedente;
    }

    /**
     * Imposta il valore della proprietà codiceFiscaleRichiedente.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodiceFiscaleRichiedente(String value) {
        this.codiceFiscaleRichiedente = value;
    }

    /**
     * Recupera il valore della proprietà protocollo.
     * 
     * @return
     *     possible object is
     *     {@link Protocollo }
     *     
     */
    public Protocollo getProtocollo() {
        return protocollo;
    }

    /**
     * Imposta il valore della proprietà protocollo.
     * 
     * @param value
     *     allowed object is
     *     {@link Protocollo }
     *     
     */
    public void setProtocollo(Protocollo value) {
        this.protocollo = value;
    }

    /**
     * Recupera il valore della proprietà statoOccupazionale.
     * 
     * @return
     *     possible object is
     *     {@link StatoOccupazionale }
     *     
     */
    public StatoOccupazionale getStatoOccupazionale() {
        return statoOccupazionale;
    }

    /**
     * Imposta il valore della proprietà statoOccupazionale.
     * 
     * @param value
     *     allowed object is
     *     {@link StatoOccupazionale }
     *     
     */
    public void setStatoOccupazionale(StatoOccupazionale value) {
        this.statoOccupazionale = value;
    }


    /**
     * <p>Classe Java per anonymous complex type.
     * 
     * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="CodEsito" type="{}Esito"/>
     *         &lt;element name="Descrizione" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "codEsito",
        "descrizione"
    })
    public static class Esito {

        @XmlElement(name = "CodEsito", required = true)
        protected it.sintesi.getprotocollo.out.Esito codEsito;
        @XmlElement(name = "Descrizione", required = true)
        protected String descrizione;

        /**
         * Recupera il valore della proprietà codEsito.
         * 
         * @return
         *     possible object is
         *     {@link it.sintesi.getprotocollo.out.Esito }
         *     
         */
        public it.sintesi.getprotocollo.out.Esito getCodEsito() {
            return codEsito;
        }

        /**
         * Imposta il valore della proprietà codEsito.
         * 
         * @param value
         *     allowed object is
         *     {@link it.sintesi.getprotocollo.out.Esito }
         *     
         */
        public void setCodEsito(it.sintesi.getprotocollo.out.Esito value) {
            this.codEsito = value;
        }

        /**
         * Recupera il valore della proprietà descrizione.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDescrizione() {
            return descrizione;
        }

        /**
         * Imposta il valore della proprietà descrizione.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDescrizione(String value) {
            this.descrizione = value;
        }

    }


    /**
     * <p>Classe Java per anonymous complex type.
     * 
     * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="NumeroProtocollo" type="{}Stringa100"/>
     *         &lt;element name="DataProtocollo" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="IdProvincia" type="{}Stringa3"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "numeroProtocollo",
        "dataProtocollo",
        "idProvincia"
    })
    public static class Protocollo {

        @XmlElement(name = "NumeroProtocollo", required = true)
        protected String numeroProtocollo;
        @XmlElement(name = "DataProtocollo", required = true)
        protected String dataProtocollo;
        @XmlElement(name = "IdProvincia", required = true)
        protected String idProvincia;

        /**
         * Recupera il valore della proprietà numeroProtocollo.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNumeroProtocollo() {
            return numeroProtocollo;
        }

        /**
         * Imposta il valore della proprietà numeroProtocollo.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNumeroProtocollo(String value) {
            this.numeroProtocollo = value;
        }

        /**
         * Recupera il valore della proprietà dataProtocollo.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDataProtocollo() {
            return dataProtocollo;
        }

        /**
         * Imposta il valore della proprietà dataProtocollo.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDataProtocollo(String value) {
            this.dataProtocollo = value;
        }

        /**
         * Recupera il valore della proprietà idProvincia.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getIdProvincia() {
            return idProvincia;
        }

        /**
         * Imposta il valore della proprietà idProvincia.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setIdProvincia(String value) {
            this.idProvincia = value;
        }

    }


    /**
     * <p>Classe Java per anonymous complex type.
     * 
     * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="CodCPI_netlabor" type="{}Stringa100"/>
     *         &lt;element name="CodSTO" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="DescrizioneSTO" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="dtDecorrenzaSTO" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "codCPINetlabor",
        "codSTO",
        "descrizioneSTO",
        "dtDecorrenzaSTO"
    })
    public static class StatoOccupazionale {

        @XmlElement(name = "CodCPI_netlabor", required = true)
        protected String codCPINetlabor;
        @XmlElement(name = "CodSTO", required = true)
        protected String codSTO;
        @XmlElement(name = "DescrizioneSTO", required = true)
        protected String descrizioneSTO;
        @XmlElement(required = true)
        protected String dtDecorrenzaSTO;

        /**
         * Recupera il valore della proprietà codCPINetlabor.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCodCPINetlabor() {
            return codCPINetlabor;
        }

        /**
         * Imposta il valore della proprietà codCPINetlabor.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCodCPINetlabor(String value) {
            this.codCPINetlabor = value;
        }

        /**
         * Recupera il valore della proprietà codSTO.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCodSTO() {
            return codSTO;
        }

        /**
         * Imposta il valore della proprietà codSTO.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCodSTO(String value) {
            this.codSTO = value;
        }

        /**
         * Recupera il valore della proprietà descrizioneSTO.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDescrizioneSTO() {
            return descrizioneSTO;
        }

        /**
         * Imposta il valore della proprietà descrizioneSTO.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDescrizioneSTO(String value) {
            this.descrizioneSTO = value;
        }

        /**
         * Recupera il valore della proprietà dtDecorrenzaSTO.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDtDecorrenzaSTO() {
            return dtDecorrenzaSTO;
        }

        /**
         * Imposta il valore della proprietà dtDecorrenzaSTO.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDtDecorrenzaSTO(String value) {
            this.dtDecorrenzaSTO = value;
        }

    }

}
