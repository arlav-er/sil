/*
 * Created on 7-nov-07
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.coop.webservices.madreperla.servizi;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.DateUtils;
import it.eng.sil.security.handlers.Utility;

//import com.engiweb.framework.base.SourceBean;

/**
 * @author esposito
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class Madreperla {

	public String coopSilMadreperla(String operation, String inputXml, String username, String password) {
		String response = "";
		boolean error = false;
		String errorMessage = "";

		if (operation.equals("getCareGiver")) {
			GetCareGiverImpl getCG = new GetCareGiverImpl();

			try {

				check_credenziali(username, password);

			} catch (Exception e) {
				return getCG.creaMessaggioErrore(e.getMessage());
			}

			try {

				getCG.caricaInputXml(inputXml);

			} catch (ValidazioneException v) {
				return getCG.creaMessaggioErrore("Errore nella validazione dell'input");
			}

			try {

				getCG.esegui();

			} catch (CareGiverNotFoundException e) {
				error = true;
				errorMessage = e.getError();
			} catch (ParserConfigurationException pce) {
				error = true;
				errorMessage = "Errore nell'elaborazione";
			} catch (TransformerException e) {
				error = true;
				errorMessage = "Errore nell'elaborazione";
			} finally {
				if (error)
					return getCG.creaMessaggioErrore(errorMessage);
			}
			// A questo punto controllare la presenza di altri errori non dovuti alla validazione

			// Se non si sono verificati errori, prelevo il risultato dell'elaborazione tramite getResponse
			// e lo invio a Madreperla
			try {

				response = getCG.getResponse();

			} catch (ValidazioneException v) {
				// Si è verificato un errore nella validazione dell'output, mettersi daccordo sull'output da inviare
				// a Madreperla
				return getCG.creaMessaggioErrore("Errore nell'elaborazione");
			}
			return response;
		}

		if (operation.equals("getLavoratore")) {
			GetLavoratoreImpl getL = new GetLavoratoreImpl();

			try {

				check_credenziali(username, password);

			} catch (Exception e) {
				return getL.creaMessaggioErrore(e.getMessage());
			}

			try {

				getL.caricaInputXml(inputXml);

			} catch (ValidazioneException v) {
				return getL.creaMessaggioErrore("Errore nella validazione dell'input");
			}

			try {

				getL.esegui();

			} catch (LavoratoreNotFoundException e) {
				error = true;
				errorMessage = e.getError();
			} catch (ParserConfigurationException pce) {
				error = true;
				errorMessage = "Errore nell'elaborazione";
			} catch (TransformerException e) {
				error = true;
				errorMessage = "Errore nell'elaborazione";
			} finally {
				if (error)
					return getL.creaMessaggioErrore(errorMessage);
			}
			// A questo punto controllare la presenza di altri errori non dovuti alla validazione

			// Se non si sono verificati errori, prelevo il risultato dell'elaborazione tramite getResponse
			// e lo invio a Madreperla
			try {

				response = getL.getResponse();

			} catch (ValidazioneException v) {
				return getL.creaMessaggioErrore("Errore nell'elaborazione");
			}
			return response;
		}

		if (operation.equals("getContratti")) {
			GetContrattiImpl getC = new GetContrattiImpl();

			try {

				check_credenziali(username, password);

			} catch (Exception e) {
				return getC.creaMessaggioErrore(e.getMessage());
			}

			try {

				getC.caricaInputXml(inputXml);

			} catch (ValidazioneException v) {
				return getC.creaMessaggioErrore("Errore nella validazione dell'input");
			}

			try {

				getC.esegui();

			} catch (ContrattoNotFoundException e) {
				error = true;
				errorMessage = e.getError();
			} catch (ParserConfigurationException pce) {
				error = true;
				errorMessage = "Errore nell'elaborazione";
			} catch (TransformerException e) {
				error = true;
				errorMessage = "Errore nell'elaborazione";
			} finally {
				if (error)
					return getC.creaMessaggioErrore(errorMessage);
			}
			// A questo punto controllare la presenza di altri errori non dovuti alla validazione

			// Se non si sono verificati errori, prelevo il risultato dell'elaborazione tramite getResponse
			// e lo invio a Madreperla
			try {

				response = getC.getResponse();

			} catch (ValidazioneException v) {
				return getC.creaMessaggioErrore("Errore nell'elaborazione");
			}

			return response;
		}

		if (operation.equals("sendIncontroDO")) {
			SendIncontroDOImpl getI = new SendIncontroDOImpl();

			try {

				check_credenziali(username, password);

			} catch (Exception e) {
				return getI.creaMessaggioErrore(e.getMessage());
			}

			try {

				getI.caricaInputXml(inputXml);

			} catch (ValidazioneException v) {
				return getI.creaMessaggioErrore("Si è verificato un errore nella validazione dell'input");
			}

			try {

				getI.esegui();

			} catch (GenericErrorException ge) {
				error = true;
				errorMessage = ge.getError();
			} catch (EMFInternalError emf) {
				error = true;
				errorMessage = "Errore nell'elaborazione. Contattare il fornitore del servizio.";
			} catch (ParserConfigurationException pce) {
				error = true;
				errorMessage = "Errore nell'elaborazione. Contattare il fornitore del servizio.";
			} catch (TransformerException e) {
				error = true;
				errorMessage = "Errore nell'elaborazione. Contattare il fornitore del servizio.";
			} finally {
				if (error)
					return getI.creaMessaggioErrore(errorMessage);
			}
			// A questo punto controllare la presenza di altri errori non dovuti alla validazione

			// Se non si sono verificati errori, prelevo il risultato dell'elaborazione tramite getResponse
			// e lo invio a Madreperla
			try {

				response = getI.getResponse();

			} catch (ValidazioneException v) {
				return getI.creaMessaggioErrore("Errore nell'elaborazione");
			}
			return response;
		}

		return "Operazione non valida : " + operation;
	}

	private void check_credenziali(String _login, String _pwd) throws Exception {
		String userLocal = "";
		String pwdLocal = "";
		String propName = "";
		String[] prop;
		Date oggi = DateUtils.convertStringToDate(DateUtils.getNow());
		Date dataInizioVal = null;
		Date dataFineVal = null;
		String decPwd = "";

		if (_login.equals("") || _pwd.equals(""))
			throw new Exception("Username o Password errati");

		List props = Utility.loadPropertiesAsUnorderedMap();

		Iterator iter = props.iterator();

		if (!iter.hasNext())
			throw new Exception("Username o Password errati");

		do {
			prop = (String[]) iter.next();
			propName = prop[0];
		} while (iter.hasNext() && !propName.equals("MADREPERLA_RegioneEmiliaRomagna.Descrizione"));

		if (iter.hasNext()) {
			prop = (String[]) iter.next();
			userLocal = prop[1];
		} else
			throw new Exception("Username o Password errati");

		if (iter.hasNext()) {
			prop = (String[]) iter.next();
			pwdLocal = prop[1];
		} else
			throw new Exception("Username o Password errati");

		decPwd = Utility.decrypt(pwdLocal);

		if (iter.hasNext()) {
			prop = (String[]) iter.next();
			dataInizioVal = DateUtils.convertStringToDate(prop[1]);
		} else
			throw new Exception("Username o Password errati");

		if (iter.hasNext()) {
			prop = (String[]) iter.next();
			dataFineVal = DateUtils.convertStringToDate(prop[1]);
		} else
			throw new Exception("Username o Password errati");

		if (!_login.equals(userLocal))
			throw new Exception("Username o Password errati");

		if (!_pwd.equals(decPwd))
			throw new Exception("Username o Password errati");

		if (oggi.compareTo(dataInizioVal) < 0)
			throw new Exception("Account non ancora valido");

		if (oggi.compareTo(dataFineVal) > 0)
			throw new Exception("Account scaduto");
	}

}