package it.eng.sil.module.profiling.gg;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.afExt.dbaccess.sql.DBKeyGenerator;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.Values;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPointSingleton;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import it.eng.sil.util.Utils;
import it.eng.sil.utils.gg.GG_Utils;
import it.eng.sil.utils.gg.Properties;
import it.gov.mlps.DataModels.InformationDelivery.YG_Profiling._1_3.Esito;
import it.gov.mlps.DataModels.InformationDelivery.YG_Profiling._1_3.Insert_Input;
import it.gov.mlps.DataModels.InformationDelivery.YG_Profiling._1_3.Select_Input;
import it.gov.mlps.DataModels.InformationDelivery.YG_Profiling._1_3.YG_Profiling;
import it.gov.mlps.Services.InformationDelivery.YG_Profiling._1_3.YG_Profiling_PortTypeProxy;

public class GestioneProfilingGG extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7849438887415565421L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GestioneProfilingGG.class.getName());

	private String END_POINT_NAME_INPUT = "ServiziProfilingYg_Input";
	private String END_POINT_NAME_SELECT = "ServiziProfilingYg_Select";

	Object utility = null;

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);

		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		BigDecimal p_cdnUtente = new BigDecimal(user.getCodut());

		String cdnLavoratore = Utils.notNull(serviceRequest.getAttribute("CDNLAVORATORE"));
		String strCodiceFiscale = Utils.notNull(serviceRequest.getAttribute("codiceFiscale"));
		String codiceProvincia = Utils.notNull(serviceRequest.getAttribute("provincia"));
		String codiceProvinciaInvio = Utils.notNull(serviceRequest.getAttribute("codiceProvinciaInvio"));

		String operazione = Utils.notNull(serviceRequest.getAttribute("OPERAZIONE_GG"));

		disableMessageIdFail();
		disableMessageIdSuccess();

		QueryExecutorObject qExec = null;
		DataConnection dc = null;

		boolean canInsertAmProfiling = false;
		try {

			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();
			EndPointSingleton eps = EndPointSingleton.getInstance(dataSourceJndiName);
			String endPointProfilingGG = operazione.equalsIgnoreCase(Properties.OPERAZIONE_CALCOLO)
					? eps.getUrl(END_POINT_NAME_INPUT)
					: eps.getUrl(END_POINT_NAME_SELECT);
			YG_Profiling_PortTypeProxy serviziWSProxy = new YG_Profiling_PortTypeProxy(endPointProfilingGG);
			YG_Profiling[] allResponse = null;
			YG_Profiling profiling = null;
			Esito esito = null;
			String descrTitoloStudio = null;
			if (operazione.equalsIgnoreCase(Properties.OPERAZIONE_CALCOLO)) {
				String condOccupaz = Utils.notNull(serviceRequest.getAttribute("condOccupaz"));
				String presenzaIt = Utils.notNull(serviceRequest.getAttribute("presenzaItalia"));
				String codTitoloStudio = Utils.notNull(serviceRequest.getAttribute("codTitolo"));
				Insert_Input inputProfiling = new Insert_Input();
				inputProfiling.setCf(strCodiceFiscale);
				inputProfiling.setCodProv(codiceProvinciaInvio);
				inputProfiling.setPresenzaIt(presenzaIt);
				inputProfiling.setOccupazAp(condOccupaz);
				inputProfiling.setTitoloStudio(codTitoloStudio);
				allResponse = serviziWSProxy.insert(inputProfiling);
				canInsertAmProfiling = true;
			} else {
				setSectionQuerySelect("QUERY_SELECT");
				SourceBean beanLav = this.doSelect(serviceRequest, serviceResponse);
				strCodiceFiscale = (String) beanLav.getAttribute("ROW.STRCODICEFISCALE");
				// codiceProvincia = (String) beanLav.getAttribute("ROW.codProvCpi");
				codiceProvincia = (String) beanLav.getAttribute("ROW.codProvCpiInvio");
				Select_Input selectProfiling = new Select_Input();
				selectProfiling.setCf(strCodiceFiscale);
				selectProfiling.setProvincia(codiceProvincia);
				allResponse = serviziWSProxy.select(selectProfiling);
			}
			profiling = allResponse[0];

			esito = profiling.getEsito();

			if (esito.equals(Esito.E01)) {
				qExec = GG_Utils.getQueryExecutorObject();
				dc = qExec.getDataConnection();

				/* GET_CODPROVINCIA_FROM_CODMIN */
				qExec.setStatement(SQLStatements.getStatement("GET_CODPROVINCIA_FROM_CODMIN"));
				qExec.setType(QueryExecutorObject.SELECT);
				List<DataField> param = new ArrayList<DataField>();
				param.add(dc.createDataField("codmin", Types.VARCHAR, profiling.getCodProv()));
				qExec.setInputParameters(param);
				SourceBean sbProv = (SourceBean) qExec.exec();

				if (sbProv.containsAttribute("ROW")) {
					profiling.setCodProv((String) sbProv.getAttribute("ROW.codprovincia"));
				}

				/* GET DESCRIZIONE PARLANTE TITOLO STUDIO */
				qExec.setStatement(SQLStatements.getStatement("SELECT_DESC_PARLANTE_SCAD"));
				qExec.setType(QueryExecutorObject.SELECT);
				List<DataField> paramT = new ArrayList<DataField>();
				paramT.add(dc.createDataField("codTitolo", Types.VARCHAR, profiling.getTitoloStudio()));
				qExec.setInputParameters(paramT);
				SourceBean sbDescrTit = (SourceBean) qExec.exec();

				if (sbDescrTit.containsAttribute("ROW")) {
					descrTitoloStudio = (String) sbDescrTit.getAttribute("ROW.descrTitolo");
				}

				if (operazione.equalsIgnoreCase(Properties.OPERAZIONE_VERIFICA)) {
					// controllo esistenza su db
					qExec.setStatement(SQLStatements.getStatement("CheckEsistenzaProfiling"));
					qExec.setType(QueryExecutorObject.SELECT);
					List<DataField> params = new ArrayList<DataField>();
					params.add(dc.createDataField("DTMCALCOLO", Types.TIMESTAMP,
							GG_Utils.getTSorNull(profiling.getDataInserimento().getTime())));
					params.add(dc.createDataField("STRCODICEFISCALE", Types.VARCHAR, profiling.getCf()));
					params.add(dc.createDataField("CODPROVINCIA", Types.NUMERIC, profiling.getCodProv()));

					qExec.setInputParameters(params);
					SourceBean profiloDb = (SourceBean) qExec.exec();

					if (!profiloDb.containsAttribute("ROW")) {
						canInsertAmProfiling = true;
					} else {
						canInsertAmProfiling = false;
						serviceResponse.setAttribute("PROFILING_GG_TITOLO_STUDIO", descrTitoloStudio);
						serviceResponse.setAttribute("PROFILING_GG_WS", profiling);
					}
				}
				if (canInsertAmProfiling) {
					if (insertAmYgProfiling(serviceResponse, reportOperation, p_cdnUtente, cdnLavoratore, qExec, dc,
							profiling, operazione)) {
						serviceResponse.setAttribute("PROFILING_GG_TITOLO_STUDIO", descrTitoloStudio);
						serviceResponse.setAttribute("PROFILING_GG_WS", profiling);
					}
				}
				reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);

			} else {
				if (operazione.equalsIgnoreCase(Properties.OPERAZIONE_CALCOLO)) {
					reportOperation.reportFailure(Properties.CALCOLO_KO);
				} else {
					reportOperation.reportFailure(Properties.NO_RECUPERO_PROFILING);
				}
			}
		} catch (Throwable e) {
			_logger.error("Errore: " + e);
		} finally {
			_logger.info("Connessione rilasciata");
			com.engiweb.framework.dbaccess.Utils.releaseResources(dc, null, null);
		}
	}

	private boolean insertAmYgProfiling(SourceBean serviceResponse, ReportOperationResult reportOperation,
			BigDecimal p_cdnUtente, String cdnLavoratore, QueryExecutorObject qExec, DataConnection dc,
			YG_Profiling profiling, String operazione) throws SourceBeanException {

		// GET PRG AM_YG_PROFILING
		BigDecimal prgProfiling = DBKeyGenerator.getNextSequence(Values.DB_SIL_DATI, "S_AM_YG_PROFILING");

		// inserisco il record in INSERT_AM_YG_PROFILING
		qExec.setStatement(SQLStatements.getStatement("INSERT_AM_YG_PROFILING"));
		qExec.setType(QueryExecutorObject.INSERT);
		List<DataField> params = new ArrayList<DataField>();
		params.add(dc.createDataField("PRGYGPROFILING", Types.NUMERIC, prgProfiling));
		params.add(dc.createDataField("CDNLAVORATORE", Types.NUMERIC, new BigDecimal(cdnLavoratore)));
		params.add(dc.createDataField("STRCODICEFISCALE", Types.VARCHAR, profiling.getCf()));
		params.add(dc.createDataField("CODPROVINCIA", Types.VARCHAR, profiling.getCodProv()));
		params.add(dc.createDataField("DTMCALCOLO", Types.TIMESTAMP,
				GG_Utils.getTSorNull(profiling.getDataInserimento().getTime())));
		params.add(dc.createDataField("CODPFPRESENZAIT", Types.VARCHAR, profiling.getPresenzaIt()));
		params.add(dc.createDataField("CODPFCONDOCCUP", Types.VARCHAR, profiling.getOccupazAp()));
		params.add(dc.createDataField("CODTITOLO", Types.VARCHAR, profiling.getTitoloStudio()));
		params.add(dc.createDataField("STRSESSO", Types.VARCHAR, profiling.getCodGenere().getValue()));
		params.add(dc.createDataField("NUMETA", Types.NUMERIC, new BigDecimal(profiling.getEta())));
		params.add(dc.createDataField("NUMINDICE1", Types.NUMERIC, new BigDecimal(profiling.getIndice())));
		params.add(dc.createDataField("STRDESCINDICE1", Types.VARCHAR, profiling.getDesIndice()));
		params.add(dc.createDataField("NUMINDICE2", Types.NUMERIC, new BigDecimal(profiling.getIndice2())));
		params.add(dc.createDataField("STRDESCINDICE2", Types.VARCHAR, profiling.getDesIndice2()));
		if (operazione.equalsIgnoreCase(Properties.OPERAZIONE_CALCOLO)) {
			params.add(dc.createDataField("CODMONOTIPO", Types.VARCHAR, Properties.PROFILING_CALCOLATO));
		} else {
			params.add(dc.createDataField("CODMONOTIPO", Types.VARCHAR, Properties.PROFILING_SCARICATO));
		}
		params.add(dc.createDataField("CDNUTINS", Types.NUMERIC, p_cdnUtente));
		params.add(dc.createDataField("CDNUTMOD", Types.NUMERIC, p_cdnUtente));
		qExec.setInputParameters(params);
		Object result = qExec.exec();
		boolean insertAmProfilingYg;
		if (result == null || !(result instanceof Boolean && ((Boolean) result).booleanValue() == true)) {
			_logger.error("Impossibile eseguire insert dei dati in INSERT_AM_YG_PROFILING");
			insertAmProfilingYg = Boolean.FALSE;
		} else {
			insertAmProfilingYg = Boolean.TRUE;
			serviceResponse.setAttribute("PRGYGPROFILING", prgProfiling);
		}

		return insertAmProfilingYg;
	}

}
