//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.05.22 at 12:41:01 PM CEST 
//

package it.eng.sil.coop.webservices.myportal.servizicittadino.bean.getaccountcittadino.out;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for RispostaAccountCittadino element declaration.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;element name="RispostaAccountCittadino">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;sequence>
 *           &lt;element name="Esito">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                   &lt;sequence>
 *                     &lt;element name="codice">
 *                       &lt;simpleType>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                           &lt;length value="2"/>
 *                         &lt;/restriction>
 *                       &lt;/simpleType>
 *                     &lt;/element>
 *                     &lt;element name="descrizione">
 *                       &lt;simpleType>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                           &lt;maxLength value="250"/>
 *                         &lt;/restriction>
 *                       &lt;/simpleType>
 *                     &lt;/element>
 *                   &lt;/sequence>
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;element name="DatiAccount" minOccurs="0">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                   &lt;sequence>
 *                     &lt;element name="AccountCittadino" maxOccurs="unbounded">
 *                       &lt;complexType>
 *                         &lt;complexContent>
 *                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                             &lt;sequence>
 *                               &lt;element name="username">
 *                                 &lt;simpleType>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                     &lt;maxLength value="15"/>
 *                                   &lt;/restriction>
 *                                 &lt;/simpleType>
 *                               &lt;/element>
 *                               &lt;element name="cognome">
 *                                 &lt;simpleType>
 *                                   &lt;restriction base="{}Stringa">
 *                                     &lt;maxLength value="50"/>
 *                                   &lt;/restriction>
 *                                 &lt;/simpleType>
 *                               &lt;/element>
 *                               &lt;element name="nome">
 *                                 &lt;simpleType>
 *                                   &lt;restriction base="{}Stringa">
 *                                     &lt;maxLength value="50"/>
 *                                   &lt;/restriction>
 *                                 &lt;/simpleType>
 *                               &lt;/element>
 *                               &lt;element name="email" type="{}EMail"/>
 *                               &lt;element name="idPfPrincipal" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                               &lt;element name="AbilitatoServiziAmministrativi">
 *                                 &lt;simpleType>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                     &lt;length value="1"/>
 *                                     &lt;enumeration value="S"/>
 *                                     &lt;enumeration value="N"/>
 *                                   &lt;/restriction>
 *                                 &lt;/simpleType>
 *                               &lt;/element>
 *                               &lt;element name="Abilitato">
 *                                 &lt;simpleType>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                     &lt;length value="1"/>
 *                                     &lt;enumeration value="S"/>
 *                                     &lt;enumeration value="N"/>
 *                                   &lt;/restriction>
 *                                 &lt;/simpleType>
 *                               &lt;/element>
 *                             &lt;/sequence>
 *                           &lt;/restriction>
 *                         &lt;/complexContent>
 *                       &lt;/complexType>
 *                     &lt;/element>
 *                   &lt;/sequence>
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *         &lt;/sequence>
 *       &lt;/restriction>
 *     &lt;/complexContent>
 *   &lt;/complexType>
 * &lt;/element>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "esito", "datiAccount" })
@XmlRootElement(name = "RispostaAccountCittadino")
public class RispostaAccountCittadino {

	@XmlElement(name = "Esito", required = true)
	protected Esito esito;
	@XmlElement(name = "DatiAccount")
	protected DatiAccount datiAccount;

	/**
	 * Gets the value of the esito property.
	 * 
	 * @return possible object is {@link Esito }
	 * 
	 */
	public Esito getEsito() {
		return esito;
	}

	/**
	 * Sets the value of the esito property.
	 * 
	 * @param value
	 *            allowed object is {@link Esito }
	 * 
	 */
	public void setEsito(Esito value) {
		this.esito = value;
	}

	/**
	 * Gets the value of the datiAccount property.
	 * 
	 * @return possible object is {@link DatiAccount }
	 * 
	 */
	public DatiAccount getDatiAccount() {
		return datiAccount;
	}

	/**
	 * Sets the value of the datiAccount property.
	 * 
	 * @param value
	 *            allowed object is {@link DatiAccount }
	 * 
	 */
	public void setDatiAccount(DatiAccount value) {
		this.datiAccount = value;
	}

	/**
	 * <p>
	 * Java class for anonymous complex type.
	 * 
	 * <p>
	 * The following schema fragment specifies the expected content contained within this class.
	 * 
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;sequence>
	 *         &lt;element name="AccountCittadino" maxOccurs="unbounded">
	 *           &lt;complexType>
	 *             &lt;complexContent>
	 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *                 &lt;sequence>
	 *                   &lt;element name="username">
	 *                     &lt;simpleType>
	 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *                         &lt;maxLength value="15"/>
	 *                       &lt;/restriction>
	 *                     &lt;/simpleType>
	 *                   &lt;/element>
	 *                   &lt;element name="cognome">
	 *                     &lt;simpleType>
	 *                       &lt;restriction base="{}Stringa">
	 *                         &lt;maxLength value="50"/>
	 *                       &lt;/restriction>
	 *                     &lt;/simpleType>
	 *                   &lt;/element>
	 *                   &lt;element name="nome">
	 *                     &lt;simpleType>
	 *                       &lt;restriction base="{}Stringa">
	 *                         &lt;maxLength value="50"/>
	 *                       &lt;/restriction>
	 *                     &lt;/simpleType>
	 *                   &lt;/element>
	 *                   &lt;element name="email" type="{}EMail"/>
	 *                   &lt;element name="idPfPrincipal" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
	 *                   &lt;element name="AbilitatoServiziAmministrativi">
	 *                     &lt;simpleType>
	 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *                         &lt;length value="1"/>
	 *                         &lt;enumeration value="S"/>
	 *                         &lt;enumeration value="N"/>
	 *                       &lt;/restriction>
	 *                     &lt;/simpleType>
	 *                   &lt;/element>
	 *                   &lt;element name="Abilitato">
	 *                     &lt;simpleType>
	 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *                         &lt;length value="1"/>
	 *                         &lt;enumeration value="S"/>
	 *                         &lt;enumeration value="N"/>
	 *                       &lt;/restriction>
	 *                     &lt;/simpleType>
	 *                   &lt;/element>
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
	@XmlType(name = "", propOrder = { "accountCittadino" })
	public static class DatiAccount {

		@XmlElement(name = "AccountCittadino", required = true)
		protected List<AccountCittadino> accountCittadino;

		/**
		 * Gets the value of the accountCittadino property.
		 * 
		 * <p>
		 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you
		 * make to the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE>
		 * method for the accountCittadino property.
		 * 
		 * <p>
		 * For example, to add a new item, do as follows:
		 * 
		 * <pre>
		 * getAccountCittadino().add(newItem);
		 * </pre>
		 * 
		 * 
		 * <p>
		 * Objects of the following type(s) are allowed in the list {@link AccountCittadino }
		 * 
		 * 
		 */
		public List<AccountCittadino> getAccountCittadino() {
			if (accountCittadino == null) {
				accountCittadino = new ArrayList<AccountCittadino>();
			}
			return this.accountCittadino;
		}

		/**
		 * <p>
		 * Java class for anonymous complex type.
		 * 
		 * <p>
		 * The following schema fragment specifies the expected content contained within this class.
		 * 
		 * <pre>
		 * &lt;complexType>
		 *   &lt;complexContent>
		 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
		 *       &lt;sequence>
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
		 *         &lt;element name="idPfPrincipal" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
		 *         &lt;element name="AbilitatoServiziAmministrativi">
		 *           &lt;simpleType>
		 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
		 *               &lt;length value="1"/>
		 *               &lt;enumeration value="S"/>
		 *               &lt;enumeration value="N"/>
		 *             &lt;/restriction>
		 *           &lt;/simpleType>
		 *         &lt;/element>
		 *         &lt;element name="Abilitato">
		 *           &lt;simpleType>
		 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
		 *               &lt;length value="1"/>
		 *               &lt;enumeration value="S"/>
		 *               &lt;enumeration value="N"/>
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
		@XmlType(name = "", propOrder = { "username", "cognome", "nome", "email", "idPfPrincipal",
				"abilitatoServiziAmministrativi", "abilitato" })
		public static class AccountCittadino {

			@XmlElement(required = true)
			protected String username;
			@XmlElement(required = true)
			protected String cognome;
			@XmlElement(required = true)
			protected String nome;
			@XmlElement(required = true)
			protected String email;
			@XmlElement(required = true)
			protected Object idPfPrincipal;
			@XmlElement(name = "AbilitatoServiziAmministrativi", required = true)
			protected String abilitatoServiziAmministrativi;
			@XmlElement(name = "Abilitato", required = true)
			protected String abilitato;

			/**
			 * Gets the value of the username property.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getUsername() {
				return username;
			}

			/**
			 * Sets the value of the username property.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setUsername(String value) {
				this.username = value;
			}

			/**
			 * Gets the value of the cognome property.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getCognome() {
				return cognome;
			}

			/**
			 * Sets the value of the cognome property.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setCognome(String value) {
				this.cognome = value;
			}

			/**
			 * Gets the value of the nome property.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getNome() {
				return nome;
			}

			/**
			 * Sets the value of the nome property.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setNome(String value) {
				this.nome = value;
			}

			/**
			 * Gets the value of the email property.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getEmail() {
				return email;
			}

			/**
			 * Sets the value of the email property.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setEmail(String value) {
				this.email = value;
			}

			/**
			 * Gets the value of the idPfPrincipal property.
			 * 
			 * @return possible object is {@link Object }
			 * 
			 */
			public Object getIdPfPrincipal() {
				return idPfPrincipal;
			}

			/**
			 * Sets the value of the idPfPrincipal property.
			 * 
			 * @param value
			 *            allowed object is {@link Object }
			 * 
			 */
			public void setIdPfPrincipal(Object value) {
				this.idPfPrincipal = value;
			}

			/**
			 * Gets the value of the abilitatoServiziAmministrativi property.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getAbilitatoServiziAmministrativi() {
				return abilitatoServiziAmministrativi;
			}

			/**
			 * Sets the value of the abilitatoServiziAmministrativi property.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setAbilitatoServiziAmministrativi(String value) {
				this.abilitatoServiziAmministrativi = value;
			}

			/**
			 * Gets the value of the abilitato property.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getAbilitato() {
				return abilitato;
			}

			/**
			 * Sets the value of the abilitato property.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setAbilitato(String value) {
				this.abilitato = value;
			}

		}

	}

	/**
	 * <p>
	 * Java class for anonymous complex type.
	 * 
	 * <p>
	 * The following schema fragment specifies the expected content contained within this class.
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
	@XmlType(name = "", propOrder = { "codice", "descrizione" })
	public static class Esito {

		@XmlElement(required = true)
		protected String codice;
		@XmlElement(required = true)
		protected String descrizione;

		/**
		 * Gets the value of the codice property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getCodice() {
			return codice;
		}

		/**
		 * Sets the value of the codice property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setCodice(String value) {
			this.codice = value;
		}

		/**
		 * Gets the value of the descrizione property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getDescrizione() {
			return descrizione;
		}

		/**
		 * Sets the value of the descrizione property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setDescrizione(String value) {
			this.descrizione = value;
		}

	}

}