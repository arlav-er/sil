package it.eng.sil.module.clicLavoro.candidatura;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.error.EMFUserError;

import it.eng.sil.coop.webservices.clicLavoro.CLUtility;
import it.eng.sil.util.xml.FieldFormatException;
import it.eng.sil.util.xml.MandatoryFieldException;
import it.eng.sil.util.xml.XMLValidator;

public class DatiAnagraficiLavoratore extends CLUtility {
	static Logger _logger = Logger.getLogger(DatiAnagraficiLavoratore.class.getName());
	Object[] inputParameters;

	public DatiAnagraficiLavoratore(Document doc, BigDecimal cdnLavoratore) {
		this.doc = doc;
		inputParameters = new Object[1];
		inputParameters[0] = cdnLavoratore;
	}

	/**
	 * Estrae tutti i campi relativi ai dati personali dei Lavoratori
	 * 
	 * @param Element
	 *            element xml su cui scrivere i campi
	 * @return Element element
	 * @throws FieldFormatException
	 * @throws MandatoryFieldException
	 * @throws EMFUserError
	 */
	public Element createElementDatiAnagrafici(Element element, DataConnection dc)
			throws MandatoryFieldException, FieldFormatException, EMFUserError {
		SourceBean ret = executeSelect("CL_GET_DATIANAGRAFICI", inputParameters, dc);

		// Validazione per la parte applicativa
		XMLValidator.checkFieldExists(ret, "codicefiscale", true, XMLValidator.codFiscRegEx,
				"\"codice fiscale del lavoratore\"");
		XMLValidator.checkFieldExists(ret, "cognome", true, lengthRangeCheck(1, 50), "\"cognome del lavoratore\"");
		XMLValidator.checkFieldExists(ret, "nome", true, lengthRangeCheck(1, 50), "\"nome del lavoratore\"");
		XMLValidator.checkFieldExists(ret, "sesso", true, sessoCheck, "\"sesso del lavoratore\"");
		XMLValidator.checkFieldExists(ret, "datanascita", true, dataCheck, "\"data di nascita del lavoratore\"");
		XMLValidator.checkFieldExists(ret, "idcomune", true, comuneCheck, "\"comune di nascita del lavoratore\"");
		XMLValidator.checkFieldExists(ret, "idcittadinanza", true, cittadinanzaCheck,
				"\"cittadinanza del lavoratore\"");

		generateTag(element, TAG_DATIANAGRAFICI, ret);

		return element;
	}

	/**
	 * Estrae tutti i campi relativi ai dati di Domicilio dei Lavoratori
	 * 
	 * @param Element
	 *            element xml su cui scrivere i campi
	 * @return Element element
	 * @throws EMFUserError
	 */
	public Element createElementDomicilio(Element element, DataConnection dc)
			throws MandatoryFieldException, FieldFormatException, EMFUserError {
		SourceBean ret = executeSelect("CL_GET_DOMICILIO", inputParameters, dc);

		// Validazione per la parte applicativa
		XMLValidator.checkFieldExists(ret, "idcomune", true, comuneCheck, "\"comune di domicilio del lavoratore\"");
		XMLValidator.checkFieldExists(ret, "cap", false, capCheck, "\"cap di domicilio del lavoratore\"");

		generateTag(element, TAG_DOMICILIO, ret);

		return element;
	}

	/**
	 * Estrae tutti i campi relativi ai dati di Recapito dei Lavoratori
	 * 
	 * @param Element
	 *            element xml su cui scrivere i campi
	 * @return Element element
	 * @throws EMFUserError
	 */
	public Element createElementRecapiti(Element element, String codCPI, DataConnection dc)
			throws MandatoryFieldException, FieldFormatException, EMFUserError {
		// Object[] inputParam = new Object[2];
		// inputParam[0] = codCPI;
		// inputParam[1] = inputParameters[0];
		Object[] inputParam = new Object[1];
		inputParam[0] = inputParameters[0];
		SourceBean ret = executeSelect("CL_GET_RECAPITI", inputParam, dc);

		// Validazione per la parte applicativa
		XMLValidator.checkFieldExists(ret, "indirizzo", false, lengthRangeCheck(0, 100),
				"\"indirizzo del lavoratore\"");
		XMLValidator.checkFieldExists(ret, "telefono", false, lengthRangeCheck(0, 15), "\"telefono del lavoratore\"");
		XMLValidator.checkFieldExists(ret, "cellulare", false, lengthRangeCheck(0, 15), "\"cellulare del lavoratore\"");
		XMLValidator.checkFieldExists(ret, "fax", false, lengthRangeCheck(0, 15), "\"fax del lavoratore\"");
		XMLValidator.checkFieldExists(ret, "email", true, emailCheck, "\"email del lavoratore\"");
		generateTag(element, TAG_RECAPITI, ret);

		return element;
	}

	/**
	 * Estrae tutti i campi relativi alle altre informazioni dei Lavoratori
	 * 
	 * @param Element
	 *            element xml su cui scrivere i campi
	 * @return Element element
	 */
	public Element createElementAltreInformazioni(Element element)
			throws MandatoryFieldException, FieldFormatException {
		/*
		 * String encrypterKey = System.getProperty("_ENCRYPTER_KEY_"); Object[] inputParam = new Object[2];
		 * inputParam[0] = encrypterKey; inputParam[1] = inputParameters[0]; SourceBean ret =
		 * executeSelect("CL_GET_ALTRE_INFORMAZIONI_3", inputParam); Vector<SourceBean> rows =
		 * ret.getAttributeAsVector("ROW"); //Se non esiste alcun record verifico le altre query if (rows.size() == 0) {
		 * inputParam = new Object[1]; inputParam[0] = inputParameters[0]; ret =
		 * executeSelect("CL_GET_ALTRE_INFORMAZIONI_1", inputParam); rows = ret.getAttributeAsVector("ROW"); //Se non
		 * esiste alcun record verifico l'altra query if (rows.size() == 0) { ret =
		 * executeSelect("CL_GET_ALTRE_INFORMAZIONI_2", inputParam); } } //Validazione per la parte applicativa
		 * XMLValidator.checkFieldExists(ret, "listespeciali", false, listeSpecialiCheck, "\"liste speciali\"");
		 * 
		 * generateTag(element, TAG_ALTREINFO, ret);
		 */
		return element;
	}

}
