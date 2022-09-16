/*
 * Creato il 2-ago-06
 */
package it.eng.sil.module.coop;

import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.bean.XMLCoopMessage;
import it.eng.sil.coop.messages.TestataMessageTO;
import it.eng.sil.coop.messages.jmsmessages.PresaAttoCoopMessage;
import it.eng.sil.coop.queues.OutQ;
import it.eng.sil.coop.utils.MessageBundle;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import it.eng.sil.util.InfoProvinciaSingleton;

public class PresaAttoCoop extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(PresaAttoCoop.class.getName());

	ReportOperationResult reportOperation;

	public void service(SourceBean request, SourceBean response) throws Exception {

		String codiceFiscale = null;
		String cognome = null;
		String nome = null;
		String datNasc = null;
		String codComNas = null;
		String strComNas = null;
		String datTrasferimento = null;
		String codComuneDomicilio = null;
		String strComuneDomicilio = null;
		String indirizzoDomicilio = null;
		String origProvCpi = null;
		String codCpiMittente = null;
		String strCpiMittente = null;

		String cdnUtente = null;
		String cdnGruppo = null;
		String cdnProfilo = null;
		String strMittente = null;
		String poloMittente = null;

		String retCode = "";

		// prelevo dalla session l'utente
		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);

		// parametri di inserimento
		codiceFiscale = (String) request.getAttribute("strCodiceFiscale");
		cognome = (String) request.getAttribute("strCognome");
		nome = (String) request.getAttribute("strNome");
		datNasc = (String) request.getAttribute("datNasc");
		codComNas = (String) request.getAttribute("codComNas");
		strComNas = (String) request.getAttribute("strComNas");
		datTrasferimento = (String) request.getAttribute("DATTRASFERIMENTO");
		codComuneDomicilio = (String) request.getAttribute("codComDom");
		strComuneDomicilio = (String) request.getAttribute("strComDom");
		indirizzoDomicilio = (String) request.getAttribute("STRINDIRIZZODOM");
		origProvCpi = (String) request.getAttribute("origProvCpi");
		codCpiMittente = (String) request.getAttribute("codCPI");
		strCpiMittente = (String) request.getAttribute("strCPI");
		cdnGruppo = "" + user.getCdnGruppo() + "";
		cdnProfilo = "" + user.getCdnProfilo() + "";
		strMittente = InfoProvinciaSingleton.getInstance().getNome();
		cdnUtente = InfoProvinciaSingleton.getInstance().getCodice();
		poloMittente = InfoProvinciaSingleton.getInstance().getCodice();

		/***********************************************************************
		 * codiceFiscale="MNUJKP61S46Z335M"; cognome="UMANA"; nome="JULIA AKPAN"; datNasc="06/11/1961";
		 * strComNas="NIGERIA (EX) "; datTrasferimento="18/08/2006"; origProvCpi="36"; codCpiMittente="081300100";
		 * strCpiMittente="bologna"; codComuneDomicilio = "A944"; indirizzoDomicilio="via po";
		 **********************************************************************/

		reportOperation = new ReportOperationResult(this, response);

		TestataMessageTO testata = new TestataMessageTO();
		testata.setPoloMittente(poloMittente);
		testata.setCdnUtente(cdnUtente);
		testata.setCdnGruppo(cdnGruppo);
		testata.setCdnProfilo(cdnProfilo);
		testata.setStrMittente(strMittente);
		testata.setDestinazione(origProvCpi);
		testata.setServizio("PresaAttoCoop");

		PresaAttoCoopMessage messaggioPresaAttoCoop = new PresaAttoCoopMessage();
		messaggioPresaAttoCoop.setTestata(testata);
		/*
		 * SourceBean msgXMLPresaAttoCoop = new SourceBean ("InfoPresaAttoCoop"); SourceBean provenienza = new
		 * SourceBean ("provenienza"); SourceBean servizio = new SourceBean ("servizio"); SourceBean dati = new
		 * SourceBean ("dati"); SourceBean messaggio = new SourceBean ("messaggio");
		 * provenienza.setAttribute("codiceCPI",codCpiMittente); provenienza.setAttribute("descrizione",strCpiMittente);
		 * msgXMLPresaAttoCoop.setAttribute(provenienza); dati.setAttribute("cognome",cognome);
		 * dati.setAttribute("nome",nome); dati.setAttribute("codicefiscale",codiceFiscale);
		 * dati.setAttribute("codcomnasc",codComNas); dati.setAttribute("comunenascita",strComNas);
		 * dati.setAttribute("datanascita",datNasc); dati.setAttribute("datatrasf",datTrasferimento);
		 * dati.setAttribute("codcomunedom",codComuneDomicilio);
		 * dati.setAttribute("comunedomicilio",strComuneDomicilio);
		 * dati.setAttribute("indirizzodom",indirizzoDomicilio); servizio.setAttribute(dati);
		 * servizio.setAttribute("nome","presaAtto"); msgXMLPresaAttoCoop.setAttribute(servizio);
		 */
		/*
		 * String msgDaTrasf = "Il lavoratore "+cognome+" "+nome+" "+codiceFiscale+" nato a " +strComNas+" il
		 * "+datNasc+", è stato trasferito al " +"nostro centro per l'impiego di "+strCpiMittente+" in data "
		 * +datTrasferimento;
		 */
		Vector v = new Vector(6);
		v.add(cognome + " " + nome);
		v.add(codiceFiscale);
		v.add(strComNas);
		v.add(datNasc);
		v.add(strCpiMittente);
		v.add(datTrasferimento);

		String messaggioNotifica = MessageBundle.getMessage(MessageCodes.Coop.NOTIFICA_TRASFERIMENTO_LAV, v);

		XMLCoopMessage xmlMessage = new XMLCoopMessage();
		xmlMessage.setCodiceCPIMitt(codCpiMittente);
		xmlMessage.setDescrizioneMitt(strCpiMittente);
		xmlMessage.setNomeServizio("presaAtto");
		xmlMessage.setDati("cognome", cognome);
		xmlMessage.setDati("nome", nome);
		xmlMessage.setDati("codicefiscale", codiceFiscale);
		xmlMessage.setDati("codcomnasc", codComNas);
		xmlMessage.setDati("comunenascita", strComNas);
		xmlMessage.setDati("datanascita", datNasc);
		xmlMessage.setDati("datatrasf", datTrasferimento);
		xmlMessage.setDati("codcomunedom", codComuneDomicilio);
		xmlMessage.setDati("comunedomicilio", strComuneDomicilio);
		xmlMessage.setDati("indirizzodom", indirizzoDomicilio);

		xmlMessage.setMessaggio(messaggioNotifica);
		// msgXMLPresaAttoCoop.setAttribute(messaggio);

		messaggioPresaAttoCoop.setXmlRequest(xmlMessage.toXML());

		try {
			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();

			messaggioPresaAttoCoop.setDataSourceJndi(dataSourceJndiName);

			OutQ outQ = new OutQ();

			messaggioPresaAttoCoop.send(outQ);
			response.setAttribute("retCode", "0");
			setSuccessMessage(MessageCodes.Coop.RICH_PRESA_ATTO_SUCCESS);

		} catch (Exception exc) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "PresaAttoCoop::service()", exc);

			setFailureMessage(MessageCodes.Coop.ERR_RICH_PRESA_ATTO);
		}
	}

	public void setFailureMessage(int message) {
		reportOperation.reportFailure(message);
	}

	public void setSuccessMessage(int message) {
		reportOperation.reportSuccess(message);
	}

}