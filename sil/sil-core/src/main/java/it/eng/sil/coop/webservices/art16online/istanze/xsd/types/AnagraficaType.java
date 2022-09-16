//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2020.12.17 alle 03:34:33 PM CET 
//


package it.eng.sil.coop.webservices.art16online.istanze.xsd.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Classe Java per AnagraficaType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="AnagraficaType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codicefiscale" type="{}CodiceFiscaleType"/>
 *         &lt;element name="cognome" type="{}Stringa50Obbl"/>
 *         &lt;element name="nome" type="{}Stringa50Obbl"/>
 *         &lt;element name="sesso">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;length value="1"/>
 *               &lt;enumeration value="F"/>
 *               &lt;enumeration value="M"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="datanascita" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="comune" type="{}ComuneType"/>
 *         &lt;element name="cittadinanza" type="{}CittadinanzaType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AnagraficaType", propOrder = {
    "codicefiscale",
    "cognome",
    "nome",
    "sesso",
    "datanascita",
    "comune",
    "cittadinanza"
})
public class AnagraficaType {

    @XmlElement(required = true)
    protected String codicefiscale;
    @XmlElement(required = true)
    protected String cognome;
    @XmlElement(required = true)
    protected String nome;
    @XmlElement(required = true)
    protected String sesso;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar datanascita;
    @XmlElement(required = true)
    protected String comune;
    @XmlElement(required = true)
    protected String cittadinanza;

    /**
     * Recupera il valore della proprietà codicefiscale.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodicefiscale() {
        return codicefiscale;
    }

    /**
     * Imposta il valore della proprietà codicefiscale.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodicefiscale(String value) {
        this.codicefiscale = value;
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
     * Recupera il valore della proprietà sesso.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSesso() {
        return sesso;
    }

    /**
     * Imposta il valore della proprietà sesso.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSesso(String value) {
        this.sesso = value;
    }

    /**
     * Recupera il valore della proprietà datanascita.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDatanascita() {
        return datanascita;
    }

    /**
     * Imposta il valore della proprietà datanascita.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDatanascita(XMLGregorianCalendar value) {
        this.datanascita = value;
    }

    /**
     * Recupera il valore della proprietà comune.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComune() {
        return comune;
    }

    /**
     * Imposta il valore della proprietà comune.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComune(String value) {
        this.comune = value;
    }

    /**
     * Recupera il valore della proprietà cittadinanza.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCittadinanza() {
        return cittadinanza;
    }

    /**
     * Imposta il valore della proprietà cittadinanza.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCittadinanza(String value) {
        this.cittadinanza = value;
    }

}
