//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2020.09.14 alle 02:47:25 PM CEST 
//


package it.eng.myportal.ws.protocolloLavoratore.output;

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
    "protocollo"
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
        @XmlSchemaType(name = "string")
        protected Esito codEsito;
        @XmlElement(name = "Descrizione", required = true)
        protected String descrizione;

        /**
         * Recupera il valore della proprietà codEsito.
         * 
         * @return
         *     possible object is
         *     {@link Esito }
         *     
         */
        public Esito getCodEsito() {
            return codEsito;
        }

        /**
         * Imposta il valore della proprietà codEsito.
         * 
         * @param value
         *     allowed object is
         *     {@link Esito }
         *     
         */
        public void setCodEsito(Esito value) {
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

}
