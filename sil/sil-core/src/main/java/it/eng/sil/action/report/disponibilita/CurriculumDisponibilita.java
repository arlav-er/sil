package it.eng.sil.action.report.disponibilita;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.error.EMFInternalError;
import com.inet.report.Engine;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.util.EncryptDecryptUtils;
import it.eng.sil.util.Sottosistema;
import it.eng.sil.util.Utils;

public class CurriculumDisponibilita extends AbstractSimpleReport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CurriculumDisponibilita.class.getName());

	private String fileType = Engine.EXPORT_PDF;

	public void setFileType(String eft) {
		if (eft.equalsIgnoreCase("PDF"))
			this.fileType = Engine.EXPORT_PDF;
		else if (eft.equalsIgnoreCase("RTF"))
			this.fileType = Engine.EXPORT_RTF;
		else if (eft.equalsIgnoreCase("HTML"))
			this.fileType = Engine.EXPORT_HTML;
		else if (eft.equalsIgnoreCase("XLS"))
			this.fileType = Engine.EXPORT_XLS;
		else
			this.fileType = Engine.EXPORT_PDF;
	}

	public String getFileType() {
		return this.fileType;
	}

	public void service(SourceBean request, SourceBean response) {

		super.service(request, response);
		TransactionQueryExecutor txExec = null;

		try {
			String apriFile = (String) request.getAttribute("apriFileBlob");
			if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
				BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
				this.openDocument(request, response, prgDoc);
			} else {
				String tipoFile = (String) request.getAttribute("tipoFile");
				if (tipoFile != null)
					setStrNomeDoc("CurriculumDisponibilita." + tipoFile);
				else
					setStrNomeDoc("CurriculumDisponibilita.pdf");
				setStrDescrizione("Curriculum disponibiità");

				// interruttore CM
				/*
				 * if (Sottosistema.CM.isOff()) { setReportPath("Curriculum/CV_CC.rpt"); }else{
				 * setReportPath("Curriculum/CV_CC_L68.rpt"); }
				 */

				setReportPath("Curriculum/CV_CC.rpt");

				// impostazione parametri del report
				Map prompts = new HashMap();

				prompts.put("pCdnLavoratore", request.getAttribute("cdnLavoratore"));
				prompts.put("pMostraPerLavoratore", request.getAttribute("mostraPerLavoratore"));

				String isCM = "" + request.getAttribute("isCM");

				if (Sottosistema.CM.isOn() && !isCM.equals("false")) {
					prompts.put("interCM", "true");
				} else {
					prompts.put("interCM", "false");
				}

				prompts.put("showNoteCPI", Utils.notNull(request.getAttribute("showNoteCPI")));

				// solo se e' richiesta la protocollazione i parametri vengono
				// inseriti nella Map
				addPromptFieldsProtocollazione(prompts, request);

				// ora si chiede di usare il passaggio dei parametri per nome e
				// non per posizione (col vettore, passaggio di default)
				setPromptFields(prompts);

				/*
				 * la superclasse ha gia' impostato il tipo documento, quindi ripetere qui l'istruzione e' inutile.
				 * String tipoDoc = (String)request.getAttribute("tipoDoc"); if (tipoDoc != null)
				 * setCodTipoDocumento(tipoDoc);
				 */

				com.inet.report.Engine eng = null;

				String salva = (String) request.getAttribute("salvaDB");
				String apri = (String) request.getAttribute("apri");
				if (salva != null && salva.equalsIgnoreCase("true")) {

					txExec = new TransactionQueryExecutor(getPool());
					txExec.initTransaction();

					eng = apriSubReport(request, txExec.getDataConnection().getInternalConnection(), null);
					// dopo aver recuperato le info per generare il report
					// abilito la transazione
					// per l'inserimento del documento e per le eventuali
					// operazioni successive
					if (insertDocument(request, response, txExec, eng)) { // se
																			// FAIL
																			// il
																			// metodo
																			// ritorna
																			// false
						txExec.commitTransaction();
					} else {
						throw new Exception("Errore nella creazione del documento");
					}
					// insertDocument(request, response);

				} else if (apri != null && apri.equalsIgnoreCase("true")) {
					// showDocument(request, response);

					DataConnection dc = DataConnectionManager.getInstance().getConnection();
					try {
						eng = apriSubReport(request, dc.getInternalConnection(), null);
					} finally {
						com.engiweb.framework.dbaccess.Utils.releaseResources(dc, null, null);
					}

					showDocument(request, response, eng);
				}
			} // else
			/*
			 * } catch (EMFUserError ue) { setOperationFail(request, response, ue); reportFailure(ue,
			 * "CurriculumDisponibilita.service()", "");
			 */
		} catch (Exception e) {
			setOperationFail(request, response, e);
			reportFailure(MessageCodes.General.OPERATION_FAIL, e, "CurriculumDisponibilita.service()", "");
			if (txExec != null) {
				try {
					txExec.rollBackTransaction();
				} catch (EMFInternalError e1) {
					it.eng.sil.util.TraceWrapper.debug(_logger,
							"Stampa CurriculumDisponibilita fallita. Fallito anche il tentativo successivo di roolback.",
							(Exception) e1);

				}
			}
		}
	}

	private Engine apriSubReport(SourceBean request, Connection conn1, String codStatoAtto) throws Exception {

		String cdnLavoratore = "" + request.getAttribute("cdnLavoratore");

		String tipoFile = (String) request.getAttribute("tipoFile");
		if (tipoFile != null)
			setFileType(tipoFile);

		Engine engine = new Engine(getFileType());

		// Codifica del cdnLavoratore
		String cdnLavoratoreCrypted = EncryptDecryptUtils.encrypt(cdnLavoratore);

		// ---------------------------------------------------- Sottoreport "cm"
		// del report CV_CC.rpt
		java.sql.Statement st1 = conn1.createStatement();
		String sql1 = " SELECT AM_CM_ISCR.CODCMTIPOISCR, " + " 	     AM_CM_ISCR.DATDATAINIZIO, "
				+ " 	     AM_CM_ISCR.DATDATAFINE, " + " 	     AM_CM_ISCR.CDNLAVORATORE, "
				+ " 	     DE_CM_TIPO_ISCR.STRDESCRIZIONE, " + " 	     AM_DOCUMENTO.CODTIPODOCUMENTO, "
				+ " 	     AM_DOCUMENTO.CODSTATOATTO " + "   FROM AM_CM_ISCR AM_CM_ISCR, "
				+ " 	     DE_CM_TIPO_ISCR DE_CM_TIPO_ISCR, " + " 	     AM_DOCUMENTO AM_DOCUMENTO, "
				+ " 	     AM_DOCUMENTO_COLL AM_DOCUMENTO_COLL " + " WHERE ((((AM_CM_ISCR.CDNLAVORATORE = '"
				+ cdnLavoratoreCrypted + "' ) and AM_CM_ISCR.DATDATAFINE is "
				+ " 	     null) and (AM_DOCUMENTO.CODTIPODOCUMENTO = 'L68')) and (AM_DOCUMENTO.CDNLAVORATORE = "
				+ cdnLavoratore + ") and " + " 	     (AM_DOCUMENTO.CODSTATOATTO = 'PR')) and  "
				+ " 	     AM_CM_ISCR.CODCMTIPOISCR=DE_CM_TIPO_ISCR.CODCMTIPOISCR and "
				+ " 	     AM_CM_ISCR.PRGCMISCR=AM_DOCUMENTO_COLL.STRCHIAVETABELLA and  "
				+ " 	     AM_DOCUMENTO_COLL.PRGDOCUMENTO=AM_DOCUMENTO.PRGDOCUMENTO	";
		java.sql.ResultSet rs1 = st1.executeQuery(sql1);
		// ----------------------------------------------------

		String path = ConfigSingleton.getRootPath() + "/WEB-INF/report/Curriculum/CV_CC.rpt";
		File f = new File(path);
		engine.setReportFile("file:" + f.getAbsolutePath());

		com.inet.report.Engine subreport1 = engine.getSubReport(13);
		String nomeReport = subreport1.getReportTitle(); // nome Sottoreport
															// n'13
		subreport1.setData(rs1);

		// chiusura dei ResultSet
		rs1.close();
		st1.close();

		return engine;
	}

	private String getPool() {
		return (String) getConfig().getAttribute("POOL");
	}

} // class
