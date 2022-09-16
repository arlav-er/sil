package it.eng.sil.action.report.collocamentoMirato;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.error.EMFUserError;
import com.inet.report.Engine;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.security.User;
import it.eng.sil.util.EncryptDecryptUtils;

public class StampaCollMiratoprovv extends AbstractSimpleReport {

	 static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(StampaCollMiratoprovv.class.getName());
	private String fileType = Engine.EXPORT_PDF;
	
	private String getPool() {
		//System.out.println(getConfig());
		return (String) getConfig().getAttribute("POOL");
	}
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
		DataConnection conn = null;
		String apriFile = (String) request.getAttribute("apriFileBlob");
		if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {
			try {
				String tipoFile = (String) request.getAttribute("tipoFile");
				if (tipoFile != null)
					setStrNomeDoc("IscrizioneCollocamentoMirato." + tipoFile);
				else
					setStrNomeDoc("IscrizioneCollocamentoMirato.pdf");

				setStrDescrizione("Iscrizione Collocamento Mirato");
				setReportPath("Collocamento_Mirato/IscrCollMirato_CC_prov.rpt");

				// impostazione parametri del report
				Map prompts = new HashMap();
				RequestContainer req = getRequestContainer();
				SessionContainer seco = req.getSessionContainer();
				User siluser = (User) seco.getAttribute(User.USERID);
				prompts.put("usergr",String.valueOf(siluser.getCdnGruppo()) );
				//int deb = siluser.getCdnGruppo();
				prompts.put("definitiva", request.getAttribute("def"));
				prompts.put("cdnLavoratore", request.getAttribute("cdnLavoratore"));
				prompts.put("cdnLavoratoreEncrypt", request.getAttribute("cdnLavoratoreEncrypt"));
				prompts.put("prgCmIscr", request.getAttribute("prgCmIscr"));
				prompts.put("numProt", "");
				
				boolean isIncrocioCM=true;
				if (isIncrocioCM) {		// per la gestione del collocamento mirato
					SessionContainer sessione = RequestContainer.getRequestContainer().getSessionContainer();
					User user = (User) sessione.getAttribute(User.USERID);
					String cpi = user.getCodRif();
					if (cpi != null &&  !cpi.equals("")) {
						prompts.put("codCpiUser", cpi);           
					}			
					else{
						prompts.put("codCpiUser", "-1");             
					}
					
					// 0 config. default - 1 config. custom (RER)
					UtilsConfig utility = new UtilsConfig("CM_STAMP");
					String configStampeCustom = utility.getConfigurazioneDefault_Custom();
					if (configStampeCustom != null &&  !configStampeCustom.equals("")) {
						prompts.put("configStampeCustom", configStampeCustom);
					}			
					else{
						prompts.put("configStampeCustom", "0");           
					}
				}
								
				try {
					addPromptFieldsProtocollazione(prompts, request);
				} catch (EMFUserError ue) {
					setOperationFail(request, response, ue);
					return;
				}
				setPromptFields(prompts);

				com.inet.report.Engine eng = null;
				
				String salva = (String) request.getAttribute("salvaDB");
				String apri = (String) request.getAttribute("apri");
				if (salva != null && salva.equalsIgnoreCase("true"))
					insertDocument(request, response);
				else if (apri != null && apri.equalsIgnoreCase("true")) {
					txExec = new TransactionQueryExecutor(getPool());
					txExec.initTransaction();
					conn = txExec.getDataConnection();
					eng = apriSubReport(request, conn.getInternalConnection(), null);

					showDocument(request, response, eng);
				}
			} catch (Exception e) {
it.eng.sil.util.TraceWrapper.fatal( _logger,getClass().getName() + "::service()", e);

			} finally{
				com.engiweb.framework.dbaccess.Utils.releaseResources(conn, null, null);
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

		//			---------------------------------------------------- Sottoreport "cm" del report CV_CC.rpt  
		java.sql.Statement st1 = conn1.createStatement();
		/*String sql1 =
			" SELECT AM_CM_ISCR.CODCMTIPOISCR, "
				+ " 	     AM_CM_ISCR.DATDATAINIZIO, "
				+ " 	     AM_CM_ISCR.DATDATAFINE, "
				+ " 	     AM_CM_ISCR.NUMISCRIZIONE, "
				+ " 	     AM_CM_ISCR.CDNLAVORATORE, "
				+ " 	     AM_CM_ISCR.DATANZIANITA68, "
				+ " 	     DE_CM_TIPO_ISCR.STRDESCRIZIONE ,"
				+ " 	     DE_CM_TIPO_ISCR.CODMONOTIPORAGG "
				+ "   		FROM AM_CM_ISCR AM_CM_ISCR, "
				+ " 	     DE_CM_TIPO_ISCR DE_CM_TIPO_ISCR "
				+ " 	     "
				+ " WHERE (AM_CM_ISCR.CDNLAVORATORE = '"
				+ cdnLavoratoreCrypted
				+ "' ) and AM_CM_ISCR.PRGCMISCR =' "
				+ request.getAttribute("prgCmIscr")
				+ "' and "
				+ " 	     AM_CM_ISCR.CODCMTIPOISCR=DE_CM_TIPO_ISCR.CODCMTIPOISCR "
				+ " 	       "; */
		//java.sql.ResultSet rs1 = st1.executeQuery(sql1);
		//			----------------------------------------------------

		String path = ConfigSingleton.getRootPath() + "/WEB-INF/report/Collocamento_Mirato/IscrCollMirato_CC_prov.rpt";
		File f = new File(path);
		engine.setReportFile("file:" + f.getAbsolutePath());

		com.inet.report.Engine subreport1 = engine.getSubReport(0);
		String nomeReport = subreport1.getReportTitle();
		// nome Sottoreport n°13
		//subreport1.setData(rs1);

		// chiusura dei ResultSet
		//rs1.close();
		st1.close();

		return engine;
	}
}