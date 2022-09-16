/*
 * Creato il Nov 24, 2004
 *
 */
package it.eng.sil.module.movimenti.processors;

import java.util.ArrayList;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;

/**
 * @author savino Si controlla (solo in validazione massiva e manuale) che ad un tipo di assunzione (tirocinio,
 *         apprendistato) sia associato un codice grado (per il tirocinio -> generale, per l'apprendistato ->
 *         apprendista). Solo in validazione massiva per il lavoro autonomo si controlla che il codice fiscale del
 *         lavoratore e dell' azienda coincidano.
 */
public class ControlloTipoAssunzione extends BaseProcessor {

	/**
	 * @param name
	 * @param txExec
	 */
	public ControlloTipoAssunzione(String name, TransactionQueryExecutor txExec) {
		super(name, txExec);
	}

	public SourceBean processRecord(Map record) throws SourceBeanException {
		warnings = new ArrayList();
		nested = new ArrayList();
		SourceBean ret = null;
		String codMonoTipo = null, codGrado = null, contesto = null, codTipoAss;
		//
		contesto = record.get("CONTEXT").toString().toLowerCase();
		codTipoAss = (String) record.get("CODTIPOASS");
		String codTipoMov = (String) record.get("CODTIPOMOV");

		try {
			String stm = "select codMonoTipo from de_tipo_contratto where codTipoContratto = '" + codTipoAss + "'";
			SourceBean row = (SourceBean) ProcessorsUtils.executeSelectQuery(stm, txExec);
			codMonoTipo = (String) row.getAttribute("row.codMonoTipo");
		} catch (Exception e) {
			logErr("Impossibile leggere le informazioni sul tipo assunzione " + codTipoAss, e);
			return createResponse(MessageCodes.General.GET_ROW_FAIL,
					"Impossibile leggere le informazioni sul tipo di assunzione");
		}
		if (codMonoTipo != null) {
			// al codice codTipoAss e' associato il tipo generale (T=tirocinio, A=apprendistato, etc.)
			codGrado = (String) record.get("CODGRADO");

			/*
			 * 27/06/2008 - author: Donato Coticone Non è più previsto il controllo (in validazione massiva e manuale)
			 * che al tipo di assunzione apprendistato sia associato un codice grado generale in quanto sono consentiti
			 * tutti i tipi di gradi.
			 */
			/*
			 * if ("A".equals(codMonoTipo) && "AVV".equalsIgnoreCase(codTipoMov)) { // apprendistato: 15=apprendista if
			 * (codGrado == null || !codGrado.equals("15")) { ret =
			 * createResponse(MessageCodes.ImportMov.ERR_LAVORO_APPRENDISTATO,""); } }
			 */

			/*
			 * 13/02/2007 - author: Mauro Riccardi Non è più previsto il controllo (in validazione massiva e manuale)
			 * che al tipo di assunzione tirocinio sia associato un codice grado generale in quanto sono consentiti
			 * tutti i tipi di gradi.
			 */
			/*
			 * if ("T".equals(codMonoTipo)) { // tirocinio: 14=generale if (codGrado == null || !codGrado.equals("14"))
			 * { ret = createResponse(MessageCodes.ImportMov.ERR_LAVORO_TIROCINIO,""); } }
			 */
			if ("N".equals(codMonoTipo) && contesto.equals("validazionemassiva")) {
				// lavoro autonomo: i codici fiscali debbono essere uguali
				String codiceFiscaleAz = (String) record.get("STRAZCODICEFISCALE");
				String codiceFiscaleLav = (String) record.get("STRCODICEFISCALE");
				if (codiceFiscaleLav != null && codiceFiscaleLav != null && !codiceFiscaleLav.equals(codiceFiscaleAz)) {
					addWarning(MessageCodes.ImportMov.WAR_LAVORO_AUTONOMO, "");
					ret = createResponse();
				}
			}
		}

		return ret;
	}
}
