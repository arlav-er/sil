//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2020.10.08 alle 08:49:20 AM CEST 
//


package it.sintesi.getprotocollo.in;

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
 *         &lt;element name="TipologiaRichiesta" type="{}TipoServizio"/>
 *         &lt;element name="Mittente">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="CodiceFiscale" type="{}CodiceFiscale"/>
 *                   &lt;element name="Cognome" type="{}Stringa100"/>
 *                   &lt;element name="Nome" type="{}Stringa100"/>
 *                   &lt;element name="OperatoreCF" type="{}CodiceFiscale"/>
 *                   &lt;element name="OperatoreUsername" type="{}Stringa100"/>
 *                   &lt;element name="OperatoreUserId" type="{}Stringa100"/>
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
    "tipologiaRichiesta",
    "mittente"
})
@XmlRootElement(name = "GetProtocollo")
public class GetProtocollo {

    @XmlElement(name = "TipologiaRichiesta", required = true)
    @XmlSchemaType(name = "string")
    protected TipoServizio tipologiaRichiesta;
    @XmlElement(name = "Mittente", required = true)
    protected Mittente mittente;

    /**
     * Recupera il valore della proprietà tipologiaRichiesta.
     * 
     * @return
     *     possible object is
     *     {@link TipoServizio }
     *     
     */
    public TipoServizio getTipologiaRichiesta() {
        return tipologiaRichiesta;
    }

    /**
     * Imposta il valore della proprietà tipologiaRichiesta.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoServizio }
     *     
     */
    public void setTipologiaRichiesta(TipoServizio value) {
        this.tipologiaRichiesta = value;
    }

    /**
     * Recupera il valore della proprietà mittente.
     * 
     * @return
     *     possible object is
     *     {@link Mittente }
     *     
     */
    public Mittente getMittente() {
        return mittente;
    }

    /**
     * Imposta il valore della proprietà mittente.
     * 
     * @param value
     *     allowed object is
     *     {@link Mittente }
     *     
     */
    public void setMittente(Mittente value) {
        this.mittente = value;
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
     *         &lt;element name="CodiceFiscale" type="{}CodiceFiscale"/>
     *         &lt;element name="Cognome" type="{}Stringa100"/>
     *         &lt;element name="Nome" type="{}Stringa100"/>
     *         &lt;element name="OperatoreCF" type="{}CodiceFiscale"/>
     *         &lt;element name="OperatoreUsername" type="{}Stringa100"/>
     *         &lt;element name="OperatoreUserId" type="{}Stringa100"/>
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
        "codiceFiscale",
        "cognome",
        "nome",
        "operatoreCF",
        "operatoreUsername",
        "operatoreUserId"
    })
    public static class Mittente {

        @XmlElement(name = "CodiceFiscale", required = true)
        protected String codiceFiscale;
        @XmlElement(name = "Cognome", required = true)
        protected String cognome;
        @XmlElement(name = "Nome", required = true)
        protected String nome;
        @XmlElement(name = "OperatoreCF", required = true)
        protected String operatoreCF;
        @XmlElement(name = "OperatoreUsername", required = true)
        protected String operatoreUsername;
        @XmlElement(name = "OperatoreUserId", required = true)
        protected String operatoreUserId;

        /**
         * Recupera il valore della proprietà codiceFiscale.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCodiceFiscale() {
            return codiceFiscale;
        }

        /**
         * Imposta il valore della proprietà codiceFiscale.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCodiceFiscale(String value) {
            this.codiceFiscale = value;
        }

        /**
         * Recupera il valore della proprietà cognome.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCognome() {
            return cognome;
        }

        /**
         * Imposta il valore della proprietà cognome.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCognome(String value) {
            this.cognome = value;
        }

        /**
         * Recupera il valore della proprietà nome.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNome() {
            return nome;
        }

        /**
         * Imposta il valore della proprietà nome.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNome(String value) {
            this.nome = value;
        }

        /**
         * Recupera il valore della proprietà operatoreCF.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getOperatoreCF() {
            return operatoreCF;
        }

        /**
         * Imposta il valore della proprietà operatoreCF.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setOperatoreCF(String value) {
            this.operatoreCF = value;
        }

        /**
         * Recupera il valore della proprietà operatoreUsername.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getOperatoreUsername() {
            return operatoreUsername;
        }

        /**
         * Imposta il valore della proprietà operatoreUsername.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setOperatoreUsername(String value) {
            this.operatoreUsername = value;
        }

        /**
         * Recupera il valore della proprietà operatoreUserId.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getOperatoreUserId() {
            return operatoreUserId;
        }

        /**
         * Imposta il valore della proprietà operatoreUserId.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setOperatoreUserId(String value) {
            this.operatoreUserId = value;
        }

    }

}
