package it.eng.sil.module.ido;

import java.sql.Types;
import java.util.ArrayList;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dispatching.module.AbstractModule;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.security.User;

public class ReiteraRichAz extends AbstractModule {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ReiteraRichAz.class.getName());

	private String className = this.getClass().getName();

	public ReiteraRichAz() {
	}

	public void service(SourceBean request, SourceBean response) {
		// ArrayList retList = null;
		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;
		ReportOperationResult ror = new ReportOperationResult(this, response);

		try {
			String pool = (String) getConfig().getAttribute("POOL");
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			conn = dcm.getConnection(pool);
			SourceBean statementSB = (SourceBean) getConfig().getAttribute("QUERY");
			String statement = statementSB.getAttribute("STATEMENT").toString();
			String sqlStr = SQLStatements.getStatement(statement);
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			// Prelevo i valori dei parametri di Input dalla Request
			String prgRichiestaAZ = StringUtils.getAttributeStrNotNull(request, "prgRichiestaAZ");
			String prgAzienda = StringUtils.getAttributeStrNotNull(request, "prgAzienda");
			String prgUnita = StringUtils.getAttributeStrNotNull(request, "prgUnita");
			String flgPubblicata = StringUtils.getAttributeStrNotNull(request, "flgPubblicata");
			String datPubblicazione = StringUtils.getAttributeStrNotNull(request, "datPubblicazione");
			String datScadenzaPubbl = StringUtils.getAttributeStrNotNull(request, "datScadenzaPubblicazione");
			String datScadenza = StringUtils.getAttributeStrNotNull(request, "datScadenza");
			String datRichiesta = StringUtils.getAttributeStrNotNull(request, "datRichiesta");
			String pSTRMANSIONEPUBB = StringUtils.getAttributeStrNotNull(request, "strMansionePub");
			String pCODCPI = StringUtils.getAttributeStrNotNull(request, "codCpi");
			String pFLGART16 = StringUtils.getAttributeStrNotNull(request, "flgArt16");
			String pSTRSESSO = StringUtils.getAttributeStrNotNull(request, "strSesso");
			String pcodMotGenere = StringUtils.getAttributeStrNotNull(request, "codMotGenere");
			String pSTRMOTIVSESSO = StringUtils.getAttributeStrNotNull(request, "strMotivSesso");
			String pNUMPROFRICHIESTIstr = StringUtils.getAttributeStrNotNull(request, "numProfRichiesti");
			String pSTRLOCALITA = StringUtils.getAttributeStrNotNull(request, "strLocalita");
			String pPRGSPIstr = StringUtils.getAttributeStrNotNull(request, "prgSpi");
			String pSTRCOGNOMERIFERIMENTO = StringUtils.getAttributeStrNotNull(request, "strCognomeRiferimento");
			String pSTRNOMERIFERIMENTO = StringUtils.getAttributeStrNotNull(request, "strNomeRiferimento");
			String pSTRTELRIFERIMENTO = StringUtils.getAttributeStrNotNull(request, "strTelRiferimento");
			String pSTRFAXRIFERIMENTO = StringUtils.getAttributeStrNotNull(request, "strFaxRiferimento");
			String pSTREMAILRIFERIMENTO = StringUtils.getAttributeStrNotNull(request, "strEmailRiferimento");
			String pFLGAUTOMUNITO = StringUtils.getAttributeStrNotNull(request, "flgAutomunito");
			String pFLGMILITE = StringUtils.getAttributeStrNotNull(request, "flgMilite");
			String pFLGMOTOMUNITO = StringUtils.getAttributeStrNotNull(request, "flgMotomunito");
			String pFLGVITTOALLOGGIO = StringUtils.getAttributeStrNotNull(request, "flgVittoAlloggio");
			String pFLGVITTO = StringUtils.getAttributeStrNotNull(request, "flgVitto");
			String pFLGTURISMO = StringUtils.getAttributeStrNotNull(request, "flgTurismo");
			String pCODTRASFERTA = StringUtils.getAttributeStrNotNull(request, "codTrasferta");
			String pFLGFUORISEDE = StringUtils.getAttributeStrNotNull(request, "flgFuoriSede");
			String pTXTFIGURAPROFESSIONALE = StringUtils.getAttributeStrNotNull(request, "txtFiguraProfessionale");
			String pTXTCARATTERISTFIGPROF = StringUtils.getAttributeStrNotNull(request, "txtCaratteristFigProf");
			String pTXTCONDCONTRATTUALE = StringUtils.getAttributeStrNotNull(request, "txtCondContrattuale");
			String pTXTNOTEOPERATORE = StringUtils.getAttributeStrNotNull(request, "txtNoteOperatore");
			String pCODAREA = StringUtils.getAttributeStrNotNull(request, "codArea");
			String cdnGruppo = StringUtils.getAttributeStrNotNull(request, "cdnGruppo");

			String pNumPostoAS = StringUtils.getAttributeStrNotNull(request, "numPostoAS");
			String pNumPostoLSU = StringUtils.getAttributeStrNotNull(request, "numPostoLSU");
			String pNumPostoMilitare = StringUtils.getAttributeStrNotNull(request, "numPostoMilitare");
			String pNumPostoMB = StringUtils.getAttributeStrNotNull(request, "numPostoMB");
			String pFlgRiusoGraduatoria = StringUtils.getAttributeStrNotNull(request, "flgRiusoGraduatoria");
			String pTipoLsu = StringUtils.getAttributeStrNotNull(request, "tipoLSU");
			String pDatChiamata = StringUtils.getAttributeStrNotNull(request, "datChiamata");
			String pFlagAS = StringUtils.getAttributeStrNotNull(request, "flagAS");
			String pFlagParAsOnline = StringUtils.getAttributeStrNotNull(request, "FLGASONLINE");

			if (("0").equalsIgnoreCase(pNumPostoLSU) || ("").equalsIgnoreCase(pNumPostoLSU) || pNumPostoLSU == null) {
				pTipoLsu = "";
			}

			// CollocamentoMirato
			String pCodMonoTipoGrad = StringUtils.getAttributeStrNotNull(request, "codMonoTipoGrad");
			String pNumPostiCM = StringUtils.getAttributeStrNotNull(request, "numPostiCM");
			// modifica Esposito
			String pNumAnnoRedditoCM = StringUtils.getAttributeStrNotNull(request, "numAnnoRedditoCM");

			String pDatChiamataCM = StringUtils.getAttributeStrNotNull(request, "datChiamataCM");
			String pCodTipoLista = StringUtils.getAttributeStrNotNull(request, "codTipoLista");
			String pFlagCM = StringUtils.getAttributeStrNotNull(request, "flagCM");

			String pFlgSvantaggiati = StringUtils.getAttributeStrNotNull(request, "FLGSVANTAGGIATI");
			String pStrMotSvantaggiati = StringUtils.getAttributeStrNotNull(request, "STRMOTSVANTAGGIATI");
			String pDatVerificaSvan = StringUtils.getAttributeStrNotNull(request, "DATVERIFICASVAN");

			String pFlgDisNonIscr = StringUtils.getAttributeStrNotNull(request, "FLGDISNONISCR");
			String pStrMotNonIscr = StringUtils.getAttributeStrNotNull(request, "STRMOTNONISCR");
			String pDatVerificaDis = StringUtils.getAttributeStrNotNull(request, "DATVERIFICADIS");

			// Trovo l'utente connesso
			RequestContainer requestContainer = getRequestContainer();
			SessionContainer sessionContainer = requestContainer.getSessionContainer();
			// Recupero utente
			User user = (User) sessionContainer.getAttribute(User.USERID);
			String pCdnUtInsMod = Integer.toString(user.getCodut());

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(47);

			// Parametro di Ritorno
			parameters.add(conn.createDataField("newPrgRichAz", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("prgRichiestaAZ", Types.VARCHAR, prgRichiestaAZ));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("prgAzienda", Types.VARCHAR, prgAzienda));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("prgUnita", Types.VARCHAR, prgUnita));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("datRichiesta", Types.VARCHAR, datRichiesta));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("datScadenza", Types.VARCHAR, datScadenza));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("flgPubblicata", Types.VARCHAR, flgPubblicata));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("datPubblicazione", Types.VARCHAR, datPubblicazione));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("datScadPubbl", Types.VARCHAR, datScadenzaPubbl));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("pCdnUtInsMod", Types.VARCHAR, pCdnUtInsMod));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("strMansionePub", Types.VARCHAR, pSTRMANSIONEPUBB));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("codCpi", Types.VARCHAR, pCODCPI));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("flgArt16", Types.VARCHAR, pFLGART16));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("strSesso", Types.VARCHAR, pSTRSESSO));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("codMotGenere", Types.VARCHAR, pcodMotGenere));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("strMotivSesso", Types.VARCHAR, pSTRMOTIVSESSO));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("numProfRichiesti", Types.VARCHAR, pNUMPROFRICHIESTIstr));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("strLocalita", Types.VARCHAR, pSTRLOCALITA));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("prgSpi", Types.VARCHAR, pPRGSPIstr));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("strCognomeRiferimento", Types.VARCHAR, pSTRCOGNOMERIFERIMENTO));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("strNomeRiferimento", Types.VARCHAR, pSTRNOMERIFERIMENTO));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("strTelRiferimento", Types.VARCHAR, pSTRTELRIFERIMENTO));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("strFaxRiferimento", Types.VARCHAR, pSTRFAXRIFERIMENTO));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("strEmailRiferimento", Types.VARCHAR, pSTREMAILRIFERIMENTO));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("flgAutomunito", Types.VARCHAR, pFLGAUTOMUNITO));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("flgMilite", Types.VARCHAR, pFLGMILITE));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("flgMotomunito", Types.VARCHAR, pFLGMOTOMUNITO));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("flgVittoAlloggio", Types.VARCHAR, pFLGVITTOALLOGGIO));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("flgVitto", Types.VARCHAR, pFLGVITTO));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("flgTurismo", Types.VARCHAR, pFLGTURISMO));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("codTrasferta", Types.VARCHAR, pCODTRASFERTA));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("flgFuoriSede", Types.VARCHAR, pFLGFUORISEDE));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("txtFiguraProfessionale", Types.VARCHAR, pTXTFIGURAPROFESSIONALE));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("txtCaratteristFigProf", Types.VARCHAR, pTXTCARATTERISTFIGPROF));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("txtCondContrattuale", Types.VARCHAR, pTXTCONDCONTRATTUALE));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("txtNoteOperatore", Types.VARCHAR, pTXTNOTEOPERATORE));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("codArea", Types.VARCHAR, pCODAREA));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("cdnGruppo", Types.VARCHAR, cdnGruppo));
			command.setAsInputParameters(paramIndex++);

			// parametri per l'Art.16
			parameters.add(conn.createDataField("numPostoAS", Types.VARCHAR, pNumPostoAS));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("numPostoLSU", Types.VARCHAR, pNumPostoLSU));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("numPostoMilitare", Types.VARCHAR, pNumPostoMilitare));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("numPostoMB", Types.VARCHAR, pNumPostoMB));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("flgRiusoGraduatoria", Types.VARCHAR, pFlgRiusoGraduatoria));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("tipoLSU", Types.VARCHAR, pTipoLsu));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("datChiamata", Types.VARCHAR, pDatChiamata));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("flagAS", Types.VARCHAR, pFlagAS));
			command.setAsInputParameters(paramIndex++);

			// parametri per il Collocamento mirato
			parameters.add(conn.createDataField("codMonoTipoGrad", Types.VARCHAR, pCodMonoTipoGrad));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("numPostiCM", Types.VARCHAR, pNumPostiCM));
			command.setAsInputParameters(paramIndex++);
			// modifica Esposito
			parameters.add(conn.createDataField("numAnnoRedditoCM", Types.VARCHAR, pNumAnnoRedditoCM));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("datChiamataCM", Types.VARCHAR, pDatChiamataCM));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("codTipoLista", Types.VARCHAR, pCodTipoLista));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("flagCM", Types.VARCHAR, pFlagCM));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("flgSvantaggiati", Types.VARCHAR, pFlgSvantaggiati));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("strMotSvantaggiati", Types.VARCHAR, pStrMotSvantaggiati));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("datVerificaSvan", Types.VARCHAR, pDatVerificaSvan));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("flgDisNonIscr", Types.VARCHAR, pFlgDisNonIscr));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("strMotNonIscr", Types.VARCHAR, pStrMotNonIscr));
			command.setAsInputParameters(paramIndex++);
			parameters.add(conn.createDataField("datVerificaDis", Types.VARCHAR, pDatVerificaDis));
			command.setAsInputParameters(paramIndex++);

			// Art16 online
			parameters.add(conn.createDataField("flagParAsOnline", Types.VARCHAR, pFlagParAsOnline));
			command.setAsInputParameters(paramIndex++);
			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);
			PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();

			// Reperisco i valori di output della stored e preparo il SourceBean della response
			DataField df = pdr.getPunctualDatafield();
			String newPrgRichAz = (String) df.getObjectValue();

			// response.setAttribute(cdr.getSourceBean());
			SourceBean row = new SourceBean("ROW");
			row.setAttribute("prgRichiestaAZ", newPrgRichAz);
			response.setAttribute((SourceBean) row);

			sessionContainer.setAttribute("IDO_NEW_PRGRICHIESTAAZ", newPrgRichAz);

			ror.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);
			it.eng.sil.util.TraceWrapper.debug(_logger, this.getClass().getName() + "::Chiamata_StoredProcedure_OK:",
					request);

		} catch (Exception e) {
			String msg = "Errore nella chiamata alla Stored Procedure ";
			ror.reportFailure(e, className, msg);
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					this.getClass().getName() + "::Errore_chiamata_StoredProcedure:", request);

		} finally {
			Utils.releaseResources(conn, command, dr);
		}
	}

}// class ReiteraRichAz
