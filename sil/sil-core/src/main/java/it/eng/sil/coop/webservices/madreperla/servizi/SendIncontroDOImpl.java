/*
 * Created on 14-nov-07
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.coop.webservices.madreperla.servizi;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dbaccess.sql.result.std.CompositeDataResult;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.sil.Values;

/**
 * @author loc_esposito
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class SendIncontroDOImpl extends MadreperlaServiceImpl {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SendIncontroDOImpl.class.getName());

	static final private String SENDINCONTRODOINPUTSCHEMA = "sendIncontroDO_input.xsd";

	static final private String SENDINCONTRODOOUTPUTSCHEMA = "sendIncontroDO_output.xsd";

	public SendIncontroDOImpl() {
		super(SENDINCONTRODOINPUTSCHEMA, SENDINCONTRODOOUTPUTSCHEMA, "SendIncontroDOImpl");
	}

	public void esegui()
			throws ParserConfigurationException, TransformerException, EMFInternalError, GenericErrorException {
		StoredProcedureCommand command = null;
		DataConnection conn = null;
		String codiceRichiesta = "";
		String dataRichiesta = "";
		String dataScadenza = "";
		String figureProfessionali = "";
		String mansione = "";
		String territorio = "";
		String codiceFiscaleCareGiver = "";
		String numRichiesta = "";
		String sessoValore = "";
		String sessoMotivazione = "";
		String etaMin = "";
		String etaMax = "";
		String etaMotivazione = "";
		String xmlIn = "";
		String numAnno = "";
		String anno_e_numRichiesta = "";

		NodeList listaNodi = docInput.getElementsByTagName("incontroDO");
		Node CrNode = listaNodi.item(0);

		anno_e_numRichiesta = UtilityXml.getElementValue(CrNode, "codiceRichiesta");

		if (!anno_e_numRichiesta.equals("")) {

			StringTokenizer st = new StringTokenizer(anno_e_numRichiesta, "#");

			numAnno = st.nextToken();
			codiceRichiesta = st.nextToken();

		}
		dataRichiesta = UtilityXml.getElementValue(CrNode, "dataRichiesta");
		dataScadenza = UtilityXml.getElementValue(CrNode, "dataScadenza");
		figureProfessionali = UtilityXml.getElementValue(CrNode, "figureProfessionali");
		mansione = UtilityXml.getElementValue(CrNode, "mansione");
		territorio = UtilityXml.getElementValue(CrNode, "territorio");
		codiceFiscaleCareGiver = UtilityXml.getElementValue(CrNode, "codiceFiscaleCareGiver");

		if (anno_e_numRichiesta.equals("")) {
			String msg = "";
			if (dataRichiesta.equals(""))
				msg = "Il campo dataRichiesta non può essere vuoto";
			if (dataScadenza.equals(""))
				msg = "Il campo dataScadenza non può essere vuoto";
			if (figureProfessionali.equals(""))
				msg = "Il campo figureProfessionali non può essere vuoto";
			if (mansione.equals(""))
				msg = "Il campo mansione non può essere vuoto";
			if (codiceFiscaleCareGiver.equals(""))
				msg = "Il campo codiceFiscaleCareGiver non può essere vuoto";
			if (!msg.equals(""))
				throw new GenericErrorException(msg);
		}

		listaNodi = docInput.getElementsByTagName("sesso");
		Node SNode = listaNodi.item(0);
		if (SNode != null) {
			sessoValore = UtilityXml.getElementValue(SNode, "valore");
			sessoMotivazione = UtilityXml.getElementValue(SNode, "motivazione");
		}

		listaNodi = docInput.getElementsByTagName("eta");
		Node ENode = listaNodi.item(0);
		if (ENode != null) {
			etaMin = UtilityXml.getElementValue(ENode, "min");
			etaMax = UtilityXml.getElementValue(ENode, "max");
			etaMotivazione = UtilityXml.getElementValue(ENode, "motivazione");
		}

		listaNodi = docInput.getElementsByTagName("listaLavoratori");

		Node listaLNode = listaNodi.item(0);

		xmlIn = UtilityXml.nodeToString(listaLNode);

		// controllare se sono presenti tutti i dati necessari in input in modo da poter effettuare l'operazione
		// ...
		// ...

		// a questo punto ho tutti i dati per eseguire l'operazione
		try {

			DataConnectionManager dcm = DataConnectionManager.getInstance();
			conn = dcm.getConnection(Values.DB_SIL_DATI);

			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(
					"{ call ? := PG_MADREPERLA.Richiesta( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }");

			// imposto i parametri
			List parameters = new ArrayList(16);
			parameters.add(conn.createDataField("result", Types.VARCHAR, null));
			command.setAsOutputParameters(0);
			parameters.add(conn.createDataField("dataRichiesta", Types.VARCHAR, dataRichiesta));
			command.setAsInputParameters(1);
			parameters.add(conn.createDataField("dataScadenza", Types.VARCHAR, dataScadenza));
			command.setAsInputParameters(2);
			parameters.add(conn.createDataField("figureProfessionali", Types.NUMERIC, figureProfessionali));
			command.setAsInputParameters(3);
			parameters.add(conn.createDataField("sessoMotivazione", Types.VARCHAR, sessoMotivazione));
			command.setAsInputParameters(4);
			parameters.add(conn.createDataField("sessoSelezionato", Types.CHAR, sessoValore));
			command.setAsInputParameters(5);
			parameters.add(conn.createDataField("codiceFiscaleCareGiver", Types.VARCHAR, codiceFiscaleCareGiver));
			command.setAsInputParameters(6);
			parameters.add(conn.createDataField("mansione", Types.VARCHAR, mansione));
			command.setAsInputParameters(7);
			parameters.add(conn.createDataField("territorio", Types.VARCHAR, territorio));
			command.setAsInputParameters(8);
			parameters.add(conn.createDataField("listaLavoratori", Types.VARCHAR, xmlIn));
			command.setAsInputParameters(9);
			parameters.add(conn.createDataField("etaMin", Types.VARCHAR, etaMin));
			command.setAsInputParameters(10);
			parameters.add(conn.createDataField("etaMax", Types.VARCHAR, etaMax));
			command.setAsInputParameters(11);
			parameters.add(conn.createDataField("etaMotivazione", Types.VARCHAR, etaMotivazione));
			command.setAsInputParameters(12);
			parameters.add(conn.createDataField("numAnno", Types.VARCHAR, numAnno));
			command.setAsInputParameters(13);
			parameters.add(conn.createDataField("numRichiesta", Types.VARCHAR, codiceRichiesta));
			command.setAsInputParameters(14);
			parameters.add(conn.createDataField("codRitorno", Types.VARCHAR, null));
			command.setAsOutputParameters(15);

			// eseguo la stored
			DataResult dr = command.execute(parameters);
			CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
			List outputParams = cdr.getContainedDataResult();
			// Reperisco i valori di output della stored
			// 1. Xml da restituire a Madreperla
			PunctualDataResult pdr = (PunctualDataResult) outputParams.get(0);
			DataField df = pdr.getPunctualDatafield();
			this.response = (String) df.getObjectValue();
			// 2. codRitorno
			pdr = (PunctualDataResult) outputParams.get(1);
			df = pdr.getPunctualDatafield();
			String codRitorno = (String) df.getObjectValue();

			if (codRitorno.equals("-2"))
				throw new GenericErrorException("Anno o numero richiesta errati.");

			if (codRitorno.equals("-1"))
				throw new GenericErrorException("Errore generico nel database. Contattare il fornitore del servizio.");

		} catch (GenericErrorException ge) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "Madreperla:Esegui", ge);

			throw ge;
		} catch (EMFInternalError emf) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "Madreperla:Esegui", new Exception(emf.getMessage()));

			throw emf;
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "Madreperla:Esegui", e);

		} finally {
			Utils.releaseResources(conn, command, null);
		}
	}

	public String creaMessaggioErrore(String msg) {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<tns:sendIncontroDO_output xmlns:tns=\"http://www.satanet.it/Madreperla-SIL\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.satanet.it/Madreperla-SIL sendIncontroDO_output.xsd\">"
				+ "<esito ok=\"False\" dettaglio=\"" + msg + "\"/>" + "</tns:sendIncontroDO_output>";
	}
}