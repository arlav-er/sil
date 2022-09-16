/**
 * 
 */
package it.eng.sil.module.clicLavoro.candidatura;

import java.math.BigDecimal;
import java.util.Vector;

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

/**
 * @author iescone
 *
 */
public class DatiDisponibilitaProfLavoratore extends CLUtility {
	static Logger _logger = Logger.getLogger(DatiDisponibilitaProfLavoratore.class.getName());
	private String dataInvio;
	private BigDecimal prgMansione = null;
	private boolean insertTagDisponibilita;

	public DatiDisponibilitaProfLavoratore(Document doc, BigDecimal prgMansione, String dataInvio) {
		this.doc = doc;
		this.dataInvio = dataInvio;
		this.prgMansione = prgMansione;
		this.insertTagDisponibilita = false;
	}

	public Element createElementDatiDisponibilitaTurni(Element element, DataConnection dc)
			throws MandatoryFieldException, FieldFormatException, EMFUserError {

		Object[] inputParam = new Object[2];
		inputParam[0] = dataInvio;
		inputParam[1] = this.prgMansione;
		SourceBean ret = executeSelect("CL_GET_DISPONIBILITA_TURNI", inputParam, dc);

		Vector<SourceBean> rows = ret.getAttributeAsVector("ROW");
		if (rows.size() > 0) {
			this.insertTagDisponibilita = true;
			// Validazione per la parte applicativa
			XMLValidator.checkFieldExists(ret, "turno", false, lengthRangeCheck(0, 8),
					"\"turno disponibile per la mansione\"");

			addTag(element, ret);
		}

		return element;
	}

	public Element createElementDatiDisponibilitaTerritorio(Element element, DataConnection dc)
			throws MandatoryFieldException, FieldFormatException, EMFUserError {

		Object[] inputParam = new Object[2];
		inputParam[0] = dataInvio;
		inputParam[1] = this.prgMansione;
		SourceBean ret = executeSelect("CL_GET_DISPONIBILITA_TERRITORIO_COMUNI", inputParam, dc);

		Vector<SourceBean> rows = ret.getAttributeAsVector("ROW");
		if (rows.size() > 0) {
			this.insertTagDisponibilita = true;
			Element territorioElement = doc.createElement(TAG_DISPONIBILITATERRITORIO);
			// Validazione per la parte applicativa
			XMLValidator.checkFieldExists(ret, "codcomdisp", false, comuneCheck,
					"\"turno disponibile per la mansione\"");
			XMLValidator.checkFieldExists(ret, "notedispcomune", false, lengthRangeCheck(0, 500),
					"\"comune disponibile per la mansione\"");

			generateTag(territorioElement, TAG_DISPONIBILITATERRITORIOCOMUNE, ret);

			ret = executeSelect("CL_GET_DISPONIBILITA_TERRITORIO_PROVINCE", inputParam, dc);

			// Validazione per la parte applicativa
			XMLValidator.checkFieldExists(ret, "codprovinciadisp", false, lengthRangeCheck(0, 8),
					"\"provincia disponibile per la mansione\"");
			addTag(territorioElement, ret);

			ret = executeSelect("CL_GET_DISPONIBILITA_TERRITORIO_REGIONE", inputParam, dc);

			// Validazione per la parte applicativa
			XMLValidator.checkFieldExists(ret, "codregionedisp", false, lengthRangeCheck(0, 8),
					"\"provincia disponibile per la mansione\"");
			addTag(territorioElement, ret);

			ret = executeSelect("CL_GET_DISPONIBILITA_TERRITORIO_STATO", inputParam, dc);

			// Validazione per la parte applicativa
			XMLValidator.checkFieldExists(ret, "codstatodisp", false, lengthRangeCheck(0, 8),
					"\"provincia disponibile per la mansione\"");
			addTag(territorioElement, ret);

			element.appendChild(territorioElement);
		}

		return element;
	}

	public Element createElementDatiDisponibilitaMobilita(Element element, DataConnection dc)
			throws MandatoryFieldException, FieldFormatException, EMFUserError {

		Object[] inputParam = new Object[2];
		inputParam[0] = dataInvio;
		inputParam[1] = this.prgMansione;
		SourceBean ret = executeSelect("CL_GET_DISPONIBILITA_MOBILITA_MANSIONE", inputParam, dc);

		Vector<SourceBean> rows = ret.getAttributeAsVector("ROW");
		if (rows.size() > 0) {
			this.insertTagDisponibilita = true;
			// Validazione per la parte applicativa
			XMLValidator.checkFieldExists(ret, "dispauto", false, siNoCheck, "\"disponibilità auto\"");
			XMLValidator.checkFieldExists(ret, "dispmoto", false, siNoCheck, "\"disponibilità moto\"");
			XMLValidator.checkFieldExists(ret, "dispmezzipubblici", false, siNoCheck,
					"\"disponibilità mezzi pubblici\"");
			XMLValidator.checkFieldExists(ret, "pendolare", false, siNoCheck, "\"pendolare\"");
			XMLValidator.checkFieldExists(ret, "percorrenza", false, lengthRangeCheck(0, 8),
					"\"durata di percorrenza massima in ore\"");
			XMLValidator.checkFieldExists(ret, "mobsettimanale", false, siNoCheck,
					"\"disponibilità alla mobilità settimanale\"");
			XMLValidator.checkFieldExists(ret, "tipotrasferta", false, lengthRangeCheck(0, 8),
					"\"codice della tipologia di trasferta\"");
			XMLValidator.checkFieldExists(ret, "notedisponibilita", false, lengthRangeCheck(0, 100),
					"\"note disponibilità\"");

			generateTag(element, TAG_DISPONIBILITAMOBILITA, ret);
		}

		return element;
	}

	public String getDataInvio() {
		return dataInvio;
	}

	public void setDataInvio(String dataInvio) {
		this.dataInvio = dataInvio;
	}

	public boolean getInsertTagDisponibilita() {
		return insertTagDisponibilita;
	}

}
