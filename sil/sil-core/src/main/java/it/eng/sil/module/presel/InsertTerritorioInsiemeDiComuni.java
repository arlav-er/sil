package it.eng.sil.module.presel;

import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.CodSepStrUtils;
import it.eng.afExt.utils.LogUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;

/**
 * Inserimento di un insieme di comuni scelto.
 * 
 * @author Luigi Antenucci
 */
public class InsertTerritorioInsiemeDiComuni extends AbstractInsertDisponibilita {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(InsertTerritorioInsiemeDiComuni.class.getName());
	protected String className = StringUtils.getClassName(this);

	protected String CODE_FIELD_NAME = "CODCOM";

	protected String getCodeFieldName() {
		return CODE_FIELD_NAME;
	}

	/**
	 * Gestisce l'inserimento di tutti i comuni elencati.
	 */
	public void service(SourceBean request, SourceBean response) {
		_logger.debug(className + ".service() INIZIO");

		try {
			Vector vectCodComuni = getInsiemeDiComuni(request);

			String codeFieldName = getCodeFieldName();
			request.delAttribute(codeFieldName);
			request.setAttribute(codeFieldName, vectCodComuni);
		} catch (SourceBeanException ex) {
			LogUtils.logError("service", "", ex, this);
		}

		// Imposto il messaggio di errore in caso di elemento già esistente
		// (lo faccio qua e non nel metodo di "disable" poiché lo voglio anche
		// su un solo elemento!)
		setMessageIdElementDuplicate(MessageCodes.General.ELEMENT_COMUNE_DUPLICATED_WITH_KEY);
		setKeyForElementDuplicate("COMUNE_DESC");

		super.service(request, response);

		_logger.debug(className + ".service() FINE");

	}

	/**
	 * Rende l'insieme dei comuni da inserire. può essere ridefinita nelle sottoclassi.
	 */
	protected Vector getInsiemeDiComuni(SourceBean request) {

		String insiemeDiComuni = SourceBeanUtils.getAttrStrNotNull(request, "INSIEMEDICOMUNI");

		List listComuni = CodSepStrUtils.getList(insiemeDiComuni);

		return new Vector(listComuni);
	}

	/**
	 * GG: Sovrascrivo il metodo affinché non faccia nulla. Faccio così perché VOGLIO mostrare un messaggio di ERRORE
	 * per ogni elemento GIA' ESISTENTE.
	 */
	public int disableMessageIdElementDuplicate() {
		// NON DISABILITO!!! Uso il "messageIdElementDuplicate" e
		// "keyForElementDuplicate"
		return getMessageIdElementDuplicate();
	}

}