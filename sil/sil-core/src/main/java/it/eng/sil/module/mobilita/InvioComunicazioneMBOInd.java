package it.eng.sil.module.mobilita;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.afExt.utils.CfException;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

/**
 * Modulo per l'invio di un'iscrizione di mobilita' individuale all'NCR.<br>
 * <ul>
 * <li>recupera i dati a partire dal prg dell'iscrizione</li>
 * <li>costruisce l'xml facendo riferimento all'xsd comunicazioneMBOIndividuale.xsd</li>
 * <li>invia la comunicazione all'NCR</li>
 * <li>aggiorno lo stato dell'iscrizione ad INVIATO</li>
 * </ul>
 * 
 * @author uberti
 * @see it.eng.sil.module.mobilita.InvioComunicazioneMBOIndUtil
 */
public class InvioComunicazioneMBOInd extends AbstractSimpleModule {

	private static final long serialVersionUID = -725498958103640628L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(InvioComunicazioneMBOInd.class.getName());

	// STATO DELLA RICHIESTA
	final String INVIATO = "3";

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		ReportOperationResult report = new ReportOperationResult(this, serviceResponse);

		SessionContainer sessionContainer = getRequestContainer().getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);

		String prgMobilitaIscrStr = StringUtils.getAttributeStrNotNull(serviceRequest, "prgMobilitaIscr");
		String cdnLav = StringUtils.getAttributeStrNotNull(serviceRequest, "cdnlavoratore");

		String flgCasoDubbio = "";
		String statement = null;
		QueryExecutorObject qExec = null;
		List<DataField> params = null;
		BigDecimal numklomobiscr = null;
		DataConnection dataConnection = null;
		SourceBean iscrizione = null;

		try {
			// 1 RECUPERO DATI XML
			statement = SQLStatements.getStatement("GET_ISCRIZIONE_MOB_X_XML");
			qExec = new QueryExecutorObject();
			dataConnection = getDataConnection();
			qExec.setDataConnection(dataConnection);
			qExec.setStatement(statement);
			qExec.setType(QueryExecutorObject.SELECT);

			params = new ArrayList<DataField>();
			params.add(dataConnection.createDataField("PRGMOBILITAISCR", Types.NUMERIC, prgMobilitaIscrStr));
			qExec.setInputParameters(params);

			iscrizione = (SourceBean) qExec.exec();
			if (iscrizione == null) {
				report.reportFailure(MessageCodes.InvioDomandaMobilita.ERRORE_RECUPERO_DATI_DOMANDA);
				return;
			}

			/*
			 * Angela richiede espressamente un controllo lato server della congruenza data inizio mobilità <* di data
			 * fine mobilità. *Modificato lato client in data inizio mobilita <= data fine il 28/09/2011 su richiesta di
			 * Angela.
			 * 
			 * INIZIO
			 */
			String dataInizio = StringUtils.getAttributeStrNotNull(iscrizione, "ROW.dataInizioIscrMbo");
			String dataFine = StringUtils.getAttributeStrNotNull(iscrizione, "ROW.dataFineIscrMbo");
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Date dtinizio = df.parse(dataInizio);
			Date dtfine = df.parse(dataFine);
			if (dtinizio.compareTo(dtfine) > 0) {
				_logger.error("La data inizio (" + dataInizio
						+ ") iscrizione mobilità non può essere maggiore della data fine (" + dataFine + ").");
				report.reportFailure(MessageCodes.InvioDomandaMobilita.ERRORE_DATA_FINE_MOBILITA);
				return;
			}
			/*
			 * FINE
			 */
			numklomobiscr = (BigDecimal) iscrizione.getAttribute("ROW.numklomobiscr");
			// 1.1 VERIFICO CHE SIA POSSIBILE INVIARE LA DOMANDA
			BigDecimal codMBStato = (BigDecimal) iscrizione.getAttribute("ROW.CDNMBSTATORICH");
			String codMonoAttiva = StringUtils.getAttributeStrNotNull(iscrizione, "ROW.CODMONOATTIVA");
			flgCasoDubbio = StringUtils.getAttributeStrNotNull(iscrizione, "ROW.CASODUBBIO");

			if ((!new BigDecimal("2").equals(codMBStato)) && (!"S".equals(codMonoAttiva))) {
				report.reportFailure(MessageCodes.InvioDomandaMobilita.ERRORE_DATA_FINE_MOBILITA);
				return;
			}
		} catch (Exception e) {
			report.reportFailure(MessageCodes.General.OPERATION_FAIL);
			return;
		} finally {
			Utils.releaseResources(dataConnection, null, null);
		}

		// 2 COSTRUZIONE XML
		Map<String, String> iscrizioneMap = new HashMap<String, String>();
		String codiceComunicazione = "";
		String xmlIscrizione = "";
		String tokenUrlAllegato = "";
		try {

			iscrizioneMap = InvioComunicazioneMBOIndUtil.buildXml(iscrizione);
			if (iscrizioneMap == null) {
				report.reportFailure(MessageCodes.InvioDomandaMobilita.ERRORE_CREAZIONE_XML);
				return;
			}

			codiceComunicazione = iscrizioneMap.get(InvioComunicazioneMBOIndUtil.CODICE_COMUNICAZIONE);
			xmlIscrizione = iscrizioneMap.get(InvioComunicazioneMBOIndUtil.XML_ISCRIZIONE);

		} catch (MandatoryFieldException e) {
			_logger.error(e.getExceptionMessage());
			Vector<String> parametri = new Vector<String>();
			parametri.add(e.getMessageParameter());
			report.reportFailure(MessageCodes.InvioDomandaMobilita.ERRORE_CAMPO_OBBLIGATORIO, "", "", parametri);
			return;
		} catch (FieldFormatException e) {
			_logger.error(e.getExceptionMessage());
			Vector<String> parametri = new Vector<String>();
			parametri.add(e.getMessageParameter());
			report.reportFailure(MessageCodes.InvioDomandaMobilita.ERRORE_FORMATO_CAMPO, "", "", parametri);
			return;
		} catch (ValidazioneXMLException e) {
			_logger.error(e.getExceptionMessage());
			Vector<String> parametri = new Vector<String>();
			report.reportFailure(MessageCodes.InvioDomandaMobilita.ERRORE_VALIDAZIONE_XML, "", "", parametri);
			return;
		} catch (CfException e) {
			_logger.error("Eccezione nel formato del codice fiscale");
			report.reportFailure(e.getMessageIdFail());
			return;
		}

		// 3 INVIO ALL'NCR E UPDATE DELL'ISCRIZIONE AD 'INVIATO'
		DataConnection dc = null;
		try {
			numklomobiscr = numklomobiscr.add(new BigDecimal("1"));
			if (flgCasoDubbio.equalsIgnoreCase("S")) {
				// controllo presenza allegati associati alla MBO
				String statementDoc = SQLStatements.getStatement("EXIST_GENERIC_DOCUMENT_COMPONENTE_KEY");
				QueryExecutorObject qExecutor = new QueryExecutorObject();
				dc = getDataConnection();
				qExecutor.setDataConnection(dc);
				qExecutor.setStatement(statementDoc);
				qExecutor.setType(QueryExecutorObject.SELECT);

				List<DataField> paramsDoc = new ArrayList<DataField>();
				paramsDoc.add(dc.createDataField("CDNLAVORATORE1", Types.NUMERIC, cdnLav));
				paramsDoc.add(dc.createDataField("CDNLAVORATORE2", Types.NUMERIC, cdnLav));
				paramsDoc.add(dc.createDataField("CODTIPODOCUMENTO1", Types.VARCHAR, "MREAAO"));
				paramsDoc.add(dc.createDataField("CODTIPODOCUMENTO2", Types.VARCHAR, "MREABO"));
				paramsDoc.add(dc.createDataField("STRCHIAVETABELLA", Types.NUMERIC, prgMobilitaIscrStr));
				paramsDoc.add(dc.createDataField("STRPAGE", Types.VARCHAR, "AMMINISTRLISTESPECPAGE"));
				qExecutor.setInputParameters(paramsDoc);

				SourceBean docAllegati = (SourceBean) qExecutor.exec();
				if (docAllegati != null) {
					Vector docAllegatiMBO = docAllegati.getAttributeAsVector("ROW");
					if (docAllegatiMBO.size() == 0) {
						report.reportFailure(MessageCodes.InvioDomandaMobilita.ERRORE_ALLEGATO_ASSENTE_INVIO_DOMANDA);
						return;
					}
					BigDecimal prgDomandaMBO = (BigDecimal) iscrizione.getAttribute("ROW.prgmobilitaiscr");
					tokenUrlAllegato = "servlet/fv/AdapterHTTP?PAGE=DOCALLEGATOMBOPAGE&CDNLAV=" + cdnLav + "&CHIAVE="
							+ prgDomandaMBO.toString() + "&PAGECOMP=AMMINISTRLISTESPECPAGE&NEW_SESSION=true";
				} else {
					report.reportFailure(MessageCodes.InvioDomandaMobilita.ERRORE_RECUPERO_ALLEGATO_INVIO_DOMANDA);
					return;
				}
			}

			boolean risInvio = InvioComunicazioneMBOIndUtil.sendComunicazioneToNCR(xmlIscrizione, tokenUrlAllegato);
			if (risInvio) {
				statement = SQLStatements.getStatement("UPDATE_AM_MOBILITA_STATO");
				qExec = new QueryExecutorObject();
				if (dc == null) {
					dc = getDataConnection();
				}
				qExec.setDataConnection(dc);
				qExec.setStatement(statement);
				qExec.setType(QueryExecutorObject.UPDATE);

				params.clear();
				params.add(dc.createDataField("CDNMBSTATORICH", Types.NUMERIC, INVIATO));
				params.add(dc.createDataField("CDNUTMOD", Types.NUMERIC, user.getCodut()));
				params.add(dc.createDataField("CODDOMANDA", Types.VARCHAR, codiceComunicazione));
				params.add(dc.createDataField("NUMKLOMOBISCR", Types.NUMERIC, numklomobiscr));
				params.add(dc.createDataField("PRGMOBILITAISCR", Types.NUMERIC, prgMobilitaIscrStr));
				qExec.setInputParameters(params);

				boolean esitoUpdate = ((Boolean) qExec.exec()).booleanValue();
				if (!esitoUpdate) {
					report.reportFailure(MessageCodes.InvioDomandaMobilita.INVIO_KO);
				} else {
					report.reportSuccess(MessageCodes.InvioDomandaMobilita.INVIO_OK);
				}
			} else {
				report.reportFailure(MessageCodes.InvioDomandaMobilita.INVIO_KO);
			}
		} catch (Exception e) {
			_logger.error("Errore nell'invio della domanda: " + e);
			report.reportFailure(MessageCodes.InvioDomandaMobilita.INVIO_KO);
		} finally {
			Utils.releaseResources(dc, null, null);
		}
	}

}
