/**
 * 
 */
package it.eng.sil.module.clicLavoro;

import java.math.BigDecimal;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.error.EMFUserError;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.coop.webservices.clicLavoro.CLUtility;
import it.eng.sil.util.xml.FieldFormatException;
import it.eng.sil.util.xml.MandatoryFieldException;
import it.eng.sil.util.xml.XMLValidator;

/**
 * @author iescone
 *
 */
public class DatiSistemaCL extends CLUtility {
	static Logger _logger = Logger.getLogger(DatiSistemaCL.class.getName());
	Object[] inputParameters;
	private String dataInvio;

	public DatiSistemaCL(Document doc, String codCpi, String dataInvio, BigDecimal cdnLavoratore) {
		this.doc = doc;
		this.dataInvio = dataInvio;
		inputParameters = new Object[3];
		inputParameters[0] = codCpi;
		inputParameters[1] = cdnLavoratore;
		inputParameters[2] = dataInvio;
	}

	/**
	 * Estrae tutti i campi relativi ai dati di sistema
	 * 
	 * @param Element
	 *            element xml su cui scrivere i campi
	 * @return Element element
	 * @throws EMFUserError
	 */
	public Element createElementDatiSistemaCandidatura(Element element, DataConnection dc, List<String> codAmbDiff,
			String codCandidatura, String dataScad) throws MandatoryFieldException, FieldFormatException, EMFUserError {
		SourceBean ret = null;
		if (codAmbDiff == null) {
			ret = executeSelect("CL_GET_DATI_SISTEMA_CANDIDATURA_AMBITO_DIFF", inputParameters, dc);
		} else {
			try {
				ret = new SourceBean("ROWS");
				for (String codambitodiffusione : codAmbDiff) {

					SourceBean row = new SourceBean("ROW");
					row.setAttribute("AMBITODIFFUSIONE", codambitodiffusione);

					ret.setAttribute(row);
				}
			} catch (SourceBeanException e) {
				_logger.error("Errore durante il recupero degli ambiti diffusione: " + e.getMessage());
			}
		}
		// Validazione per la parte applicativa
		// XMLValidator.checkFieldExists(ret, "ambitodiffusione", true, dispTerritCheck, "\"ambito diffusione (Dati
		// Sistema)\"");
		addTag(element, ret);

		SourceBean ret1 = null;
		if (codAmbDiff == null) {
			Object[] inputParametersInvio = new Object[4];
			inputParametersInvio[0] = dataScad;
			inputParametersInvio[1] = inputParameters[0];
			inputParametersInvio[2] = inputParameters[1];
			inputParametersInvio[3] = inputParameters[2];
			ret1 = executeSelect("CL_GET_DATI_SISTEMA_CANDIDATURA", inputParametersInvio, dc);
		} else {
			Object[] inputParametersPreInvio = new Object[4];
			inputParametersPreInvio[0] = dataScad;
			inputParametersPreInvio[1] = codCandidatura;
			inputParametersPreInvio[2] = inputParameters[0];
			inputParametersPreInvio[3] = inputParameters[2];

			ret1 = executeSelect("CL_GET_DATI_SISTEMA_CANDIDATURA_PREINVIO", inputParametersPreInvio, dc);

		}

		// Verifico l'esistenza dell'intemrediario
		Vector<SourceBean> rows = ret1.getAttributeAsVector("ROW");
		boolean checkIntermediario = false, checkTipocandidatura = false;
		if (rows.size() > 0) {
			SourceBean sourceBean = rows.get(0);
			String intermediario = sourceBean.getAttribute("idintermediario").toString();
			String tipocandidatura = sourceBean.getAttribute("tipocandidatura").toString();
			if (!StringUtils.isEmptyNoBlank(intermediario)) {
				checkIntermediario = true;
			}
			if (!StringUtils.isEmptyNoBlank(tipocandidatura) && tipocandidatura.equalsIgnoreCase("chiusura")) {
				checkTipocandidatura = true;
			}
		}

		XMLValidator.checkFieldExists(ret1, "datainserimento", true, dataTimeCheck,
				"\"data inserimento (Dati Sistema)\"");
		// XMLValidator.checkFieldExists(ret1, "datascadenza", true, dataCheck, "\"data scadenza (Dati Sistema)\"");
		XMLValidator.checkFieldExists(ret1, "idintermediario", true, lengthRangeCheck(0, 11), "\"intermediario\"");
		XMLValidator.checkFieldExists(ret1, "codicefiscaleintermediario", false, lengthRangeCheck(1, 16),
				"\"codice fiscale intermediario (Dati Sistema)\"");
		XMLValidator.checkFieldExists(ret1, "denominazioneintermediario", false, lengthRangeCheck(0, 200),
				"\"denominazione intermediario (Dati Sistema)\"");
		XMLValidator.checkFieldExists(ret1, "indirizzo", false, lengthRangeCheck(0, 100),
				"\"indirizzo (Dati Sistema)\"");
		XMLValidator.checkFieldExists(ret1, "idcomune", checkIntermediario, comuneCheck, "\"comune (Dati Sistema)\"");
		XMLValidator.checkFieldExists(ret1, "cap", false, capCheck, "\"cap (Dati Sistema)\"");
		XMLValidator.checkFieldExists(ret1, "telefono", false, lengthRangeCheck(0, 15), "\"telefono (Dati Sistema)\"");
		XMLValidator.checkFieldExists(ret1, "fax", false, lengthRangeCheck(0, 15), "\"fax (Dati Sistema)\"");
		XMLValidator.checkFieldExists(ret1, "email", true, emailCheck, "\"email (Dati Sistema)\"");
		XMLValidator.checkFieldExists(ret1, "visibilita", checkIntermediario, siNoCheck,
				"\"visibilita (Dati Sistema)\"");
		XMLValidator.checkFieldExists(ret1, "tipocandidatura", true, tipoComunicazioneCheck,
				"\"tipo candidatura (Dati Sistema)\"");
		XMLValidator.checkFieldExists(ret1, "motivochiusura", checkTipocandidatura, lengthRangeCheck(0, 2),
				"\"motivo chiusura (Dati Sistema)\"");
		XMLValidator.checkFieldExists(ret1, "codicecandidatura", true, lengthRangeCheck(25, 25),
				"\"codice candidatura (Dati Sistema)\"");
		// XMLValidator.checkFieldExists(ret1, "codicecandidatura", true, lengthRangeCheck(25, 25), "\"codice
		// candidatura (Dati Sistema)\"");

		XMLValidator.checkFieldExists(ret1, "percettore", false, siNoCheck, "\"percettore (Dati Sistema)\"");

		addTag(element, ret1);

		return element;
	}

	public String getDataInvio() {
		return dataInvio;
	}

	public void setDataInvio(String dataInvio) {
		this.dataInvio = dataInvio;
	}

}
