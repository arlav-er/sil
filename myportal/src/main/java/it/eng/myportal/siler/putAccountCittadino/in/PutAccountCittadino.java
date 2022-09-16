//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.06.03 at 10:52:49 AM CEST 
//


package it.eng.myportal.siler.putAccountCittadino.in;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="idPfPrincipal" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *         &lt;element name="username">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="15"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="cognome">
 *           &lt;simpleType>
 *             &lt;restriction base="{}Stringa">
 *               &lt;maxLength value="50"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="nome">
 *           &lt;simpleType>
 *             &lt;restriction base="{}Stringa">
 *               &lt;maxLength value="50"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="email" type="{}EMail"/>
 *         &lt;element name="comuneNascita" type="{}Comune"/>
 *         &lt;element name="comuneDomicilio" type="{}Comune"/>
 *         &lt;element name="indirizzoDomicilio">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="60"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="codiceFiscale" type="{}CodiceFiscale"/>
 *         &lt;element name="dataNascita" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="cittadinanza" type="{}Cittadinanza"/>
 *         &lt;element name="codProvinciaSil">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="3"/>
 *               &lt;minLength value="1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="documentoIdentita">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="255"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="numeroDocumento">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="255"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="dtScadenzaDocumento" type="{http://www.w3.org/2001/XMLSchema}date"/>
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
    "idPfPrincipal",
    "username",
    "cognome",
    "nome",
    "email",
    "comuneNascita",
    "comuneDomicilio",
    "indirizzoDomicilio",
    "codiceFiscale",
    "dataNascita",
    "cittadinanza",
    "codProvinciaSil",
    "documentoIdentita",
    "numeroDocumento",
    "dtScadenzaDocumento"
})
@XmlRootElement(name = "putAccountCittadino")
public class PutAccountCittadino {

    protected Object idPfPrincipal;
    @XmlElement(required = true)
    protected String username;
    @XmlElement(required = true)
    protected String cognome;
    @XmlElement(required = true)
    protected String nome;
    @XmlElement(required = true)
    protected String email;
    @XmlElement(required = true)
    protected String comuneNascita;
    @XmlElement(required = true)
    protected String comuneDomicilio;
    @XmlElement(required = true)
    protected String indirizzoDomicilio;
    @XmlElement(required = true)
    protected String codiceFiscale;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataNascita;
    @XmlElement(required = true)
    protected String cittadinanza;
    @XmlElement(required = true)
    protected String codProvinciaSil;
    @XmlElement(required = true)
    protected String documentoIdentita;
    @XmlElement(required = true)
    protected String numeroDocumento;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dtScadenzaDocumento;

    /**
     * Gets the value of the idPfPrincipal property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getIdPfPrincipal() {
        return idPfPrincipal;
    }

    /**
     * Sets the value of the idPfPrincipal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setIdPfPrincipal(Object value) {
        this.idPfPrincipal = value;
    }

    /**
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of the username property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsername(String value) {
        this.username = value;
    }

    /**
     * Gets the value of the cognome property.
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
     * Sets the value of the cognome property.
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
     * Gets the value of the nome property.
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
     * Sets the value of the nome property.
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
     * Gets the value of the email property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Gets the value of the comuneNascita property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComuneNascita() {
        return comuneNascita;
    }

    /**
     * Sets the value of the comuneNascita property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComuneNascita(String value) {
        this.comuneNascita = value;
    }

    /**
     * Gets the value of the comuneDomicilio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComuneDomicilio() {
        return comuneDomicilio;
    }

    /**
     * Sets the value of the comuneDomicilio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComuneDomicilio(String value) {
        this.comuneDomicilio = value;
    }

    /**
     * Gets the value of the indirizzoDomicilio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIndirizzoDomicilio() {
        return indirizzoDomicilio;
    }

    /**
     * Sets the value of the indirizzoDomicilio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIndirizzoDomicilio(String value) {
        this.indirizzoDomicilio = value;
    }

    /**
     * Gets the value of the codiceFiscale property.
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
     * Sets the value of the codiceFiscale property.
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
     * Gets the value of the dataNascita property.
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
     * Sets the value of the dataNascita property.
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
     * Gets the value of the cittadinanza property.
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
     * Sets the value of the cittadinanza property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCittadinanza(String value) {
        this.cittadinanza = value;
    }

    /**
     * Gets the value of the codProvinciaSil property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodProvinciaSil() {
        return codProvinciaSil;
    }

    /**
     * Sets the value of the codProvinciaSil property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodProvinciaSil(String value) {
        this.codProvinciaSil = value;
    }

    /**
     * Gets the value of the documentoIdentita property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocumentoIdentita() {
        return documentoIdentita;
    }

    /**
     * Sets the value of the documentoIdentita property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocumentoIdentita(String value) {
        this.documentoIdentita = value;
    }

    /**
     * Gets the value of the numeroDocumento property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    /**
     * Sets the value of the numeroDocumento property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumeroDocumento(String value) {
        this.numeroDocumento = value;
    }

    /**
     * Gets the value of the dtScadenzaDocumento property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDtScadenzaDocumento() {
        return dtScadenzaDocumento;
    }

    /**
     * Sets the value of the dtScadenzaDocumento property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDtScadenzaDocumento(XMLGregorianCalendar value) {
        this.dtScadenzaDocumento = value;
    }

}