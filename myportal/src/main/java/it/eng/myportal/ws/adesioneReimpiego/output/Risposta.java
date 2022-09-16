//
// Questo file � stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andr� persa durante la ricompilazione dello schema di origine. 
// Generato il: 2019.05.13 alle 03:31:52 PM CEST 
//


package it.eng.myportal.ws.adesioneReimpiego.output;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


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
 *                   &lt;element name="codice">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;length value="2"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="descrizione">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="250"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="datiStatoOccupazionale" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="CodiceFiscale" type="{}CodiceFiscale"/>
 *                   &lt;element name="Cognome" type="{}Stringa50"/>
 *                   &lt;element name="Nome" type="{}Stringa50"/>
 *                   &lt;element name="DataNascita" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *                   &lt;element name="StatoOccupazionale">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="CodiceSO">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                   &lt;maxLength value="8"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                             &lt;element name="DescrizioneSO" type="{}Stringa100"/>
 *                             &lt;element name="MesiAnzianita" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                             &lt;element name="DataDid" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="DatiCPI">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="CodiceCPI" type="{}CodCPI"/>
 *                             &lt;element name="DescrCPI" type="{}Stringa100"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="schemaVersion" type="{http://www.w3.org/2001/XMLSchema}string" />
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
    "datiStatoOccupazionale"
})
@XmlRootElement(name = "Risposta")
public class Risposta {

    @XmlElement(name = "Esito", required = true)
    protected Risposta.Esito esito;
    protected Risposta.DatiStatoOccupazionale datiStatoOccupazionale;
    @XmlAttribute(name = "schemaVersion")
    protected String schemaVersion;

    /**
     * Recupera il valore della propriet� esito.
     * 
     * @return
     *     possible object is
     *     {@link Risposta.Esito }
     *     
     */
    public Risposta.Esito getEsito() {
        return esito;
    }

    /**
     * Imposta il valore della propriet� esito.
     * 
     * @param value
     *     allowed object is
     *     {@link Risposta.Esito }
     *     
     */
    public void setEsito(Risposta.Esito value) {
        this.esito = value;
    }

    /**
     * Recupera il valore della propriet� datiStatoOccupazionale.
     * 
     * @return
     *     possible object is
     *     {@link Risposta.DatiStatoOccupazionale }
     *     
     */
    public Risposta.DatiStatoOccupazionale getDatiStatoOccupazionale() {
        return datiStatoOccupazionale;
    }

    /**
     * Imposta il valore della propriet� datiStatoOccupazionale.
     * 
     * @param value
     *     allowed object is
     *     {@link Risposta.DatiStatoOccupazionale }
     *     
     */
    public void setDatiStatoOccupazionale(Risposta.DatiStatoOccupazionale value) {
        this.datiStatoOccupazionale = value;
    }

    /**
     * Recupera il valore della propriet� schemaVersion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSchemaVersion() {
        return schemaVersion;
    }

    /**
     * Imposta il valore della propriet� schemaVersion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSchemaVersion(String value) {
        this.schemaVersion = value;
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
     *         &lt;element name="Cognome" type="{}Stringa50"/>
     *         &lt;element name="Nome" type="{}Stringa50"/>
     *         &lt;element name="DataNascita" type="{http://www.w3.org/2001/XMLSchema}date"/>
     *         &lt;element name="StatoOccupazionale">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="CodiceSO">
     *                     &lt;simpleType>
     *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                         &lt;maxLength value="8"/>
     *                       &lt;/restriction>
     *                     &lt;/simpleType>
     *                   &lt;/element>
     *                   &lt;element name="DescrizioneSO" type="{}Stringa100"/>
     *                   &lt;element name="MesiAnzianita" type="{http://www.w3.org/2001/XMLSchema}integer"/>
     *                   &lt;element name="DataDid" type="{http://www.w3.org/2001/XMLSchema}date"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="DatiCPI">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="CodiceCPI" type="{}CodCPI"/>
     *                   &lt;element name="DescrCPI" type="{}Stringa100"/>
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
        "codiceFiscale",
        "cognome",
        "nome",
        "dataNascita",
        "statoOccupazionale",
        "datiCPI"
    })
    public static class DatiStatoOccupazionale {

        @XmlElement(name = "CodiceFiscale", required = true)
        protected String codiceFiscale;
        @XmlElement(name = "Cognome", required = true)
        protected String cognome;
        @XmlElement(name = "Nome", required = true)
        protected String nome;
        @XmlElement(name = "DataNascita", required = true)
        @XmlSchemaType(name = "date")
        protected XMLGregorianCalendar dataNascita;
        @XmlElement(name = "StatoOccupazionale", required = true)
        protected Risposta.DatiStatoOccupazionale.StatoOccupazionale statoOccupazionale;
        @XmlElement(name = "DatiCPI", required = true)
        protected Risposta.DatiStatoOccupazionale.DatiCPI datiCPI;

        /**
         * Recupera il valore della propriet� codiceFiscale.
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
         * Imposta il valore della propriet� codiceFiscale.
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
         * Recupera il valore della propriet� cognome.
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
         * Imposta il valore della propriet� cognome.
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
         * Recupera il valore della propriet� nome.
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
         * Imposta il valore della propriet� nome.
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
         * Recupera il valore della propriet� dataNascita.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getDataNascita() {
            return dataNascita;
        }

        /**
         * Imposta il valore della propriet� dataNascita.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setDataNascita(XMLGregorianCalendar value) {
            this.dataNascita = value;
        }

        /**
         * Recupera il valore della propriet� statoOccupazionale.
         * 
         * @return
         *     possible object is
         *     {@link Risposta.DatiStatoOccupazionale.StatoOccupazionale }
         *     
         */
        public Risposta.DatiStatoOccupazionale.StatoOccupazionale getStatoOccupazionale() {
            return statoOccupazionale;
        }

        /**
         * Imposta il valore della propriet� statoOccupazionale.
         * 
         * @param value
         *     allowed object is
         *     {@link Risposta.DatiStatoOccupazionale.StatoOccupazionale }
         *     
         */
        public void setStatoOccupazionale(Risposta.DatiStatoOccupazionale.StatoOccupazionale value) {
            this.statoOccupazionale = value;
        }

        /**
         * Recupera il valore della propriet� datiCPI.
         * 
         * @return
         *     possible object is
         *     {@link Risposta.DatiStatoOccupazionale.DatiCPI }
         *     
         */
        public Risposta.DatiStatoOccupazionale.DatiCPI getDatiCPI() {
            return datiCPI;
        }

        /**
         * Imposta il valore della propriet� datiCPI.
         * 
         * @param value
         *     allowed object is
         *     {@link Risposta.DatiStatoOccupazionale.DatiCPI }
         *     
         */
        public void setDatiCPI(Risposta.DatiStatoOccupazionale.DatiCPI value) {
            this.datiCPI = value;
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
         *         &lt;element name="CodiceCPI" type="{}CodCPI"/>
         *         &lt;element name="DescrCPI" type="{}Stringa100"/>
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
            "codiceCPI",
            "descrCPI"
        })
        public static class DatiCPI {

            @XmlElement(name = "CodiceCPI", required = true)
            protected String codiceCPI;
            @XmlElement(name = "DescrCPI", required = true)
            protected String descrCPI;

            /**
             * Recupera il valore della propriet� codiceCPI.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCodiceCPI() {
                return codiceCPI;
            }

            /**
             * Imposta il valore della propriet� codiceCPI.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCodiceCPI(String value) {
                this.codiceCPI = value;
            }

            /**
             * Recupera il valore della propriet� descrCPI.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getDescrCPI() {
                return descrCPI;
            }

            /**
             * Imposta il valore della propriet� descrCPI.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setDescrCPI(String value) {
                this.descrCPI = value;
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
         *         &lt;element name="CodiceSO">
         *           &lt;simpleType>
         *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *               &lt;maxLength value="8"/>
         *             &lt;/restriction>
         *           &lt;/simpleType>
         *         &lt;/element>
         *         &lt;element name="DescrizioneSO" type="{}Stringa100"/>
         *         &lt;element name="MesiAnzianita" type="{http://www.w3.org/2001/XMLSchema}integer"/>
         *         &lt;element name="DataDid" type="{http://www.w3.org/2001/XMLSchema}date"/>
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
            "codiceSO",
            "descrizioneSO",
            "mesiAnzianita",
            "dataDid"
        })
        public static class StatoOccupazionale {

            @XmlElement(name = "CodiceSO", required = true)
            protected String codiceSO;
            @XmlElement(name = "DescrizioneSO", required = true)
            protected String descrizioneSO;
            @XmlElement(name = "MesiAnzianita", required = true)
            protected BigInteger mesiAnzianita;
            @XmlElement(name = "DataDid", required = true, nillable = true)
            @XmlSchemaType(name = "date")
            protected XMLGregorianCalendar dataDid;

            /**
             * Recupera il valore della propriet� codiceSO.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCodiceSO() {
                return codiceSO;
            }

            /**
             * Imposta il valore della propriet� codiceSO.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCodiceSO(String value) {
                this.codiceSO = value;
            }

            /**
             * Recupera il valore della propriet� descrizioneSO.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getDescrizioneSO() {
                return descrizioneSO;
            }

            /**
             * Imposta il valore della propriet� descrizioneSO.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setDescrizioneSO(String value) {
                this.descrizioneSO = value;
            }

            /**
             * Recupera il valore della propriet� mesiAnzianita.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getMesiAnzianita() {
                return mesiAnzianita;
            }

            /**
             * Imposta il valore della propriet� mesiAnzianita.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setMesiAnzianita(BigInteger value) {
                this.mesiAnzianita = value;
            }

            /**
             * Recupera il valore della propriet� dataDid.
             * 
             * @return
             *     possible object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public XMLGregorianCalendar getDataDid() {
                return dataDid;
            }

            /**
             * Imposta il valore della propriet� dataDid.
             * 
             * @param value
             *     allowed object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public void setDataDid(XMLGregorianCalendar value) {
                this.dataDid = value;
            }

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
     *         &lt;element name="codice">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;length value="2"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="descrizione">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="250"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
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
        "codice",
        "descrizione"
    })
    public static class Esito {

        @XmlElement(required = true)
        protected String codice;
        @XmlElement(required = true)
        protected String descrizione;

        /**
         * Recupera il valore della propriet� codice.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCodice() {
            return codice;
        }

        /**
         * Imposta il valore della propriet� codice.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCodice(String value) {
            this.codice = value;
        }

        /**
         * Recupera il valore della propriet� descrizione.
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
         * Imposta il valore della propriet� descrizione.
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

}
