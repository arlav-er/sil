package it.eng.sil.action.report.ido;

import java.io.File;
import java.math.BigDecimal;
import java.util.Hashtable;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutor;
import com.inet.report.Engine;

import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.action.report.StampaInternaResultSet;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil;
import it.eng.sil.module.AccessoSemplificato;
import it.eng.sil.security.User;
import it.eng.sil.util.Utils;
import it.eng.sil.util.amministrazione.impatti.DBLoad;
/**
 * Cambiata la classe il 10/05/2010 per stampare due gruppi distinti, Lista o Graduatoria.
 * il campo e' DO_NOMINATIVO.CODCMANNOTA (quando null Graduatoria, quando diverso da null Lista)
 * 
 * Inserita la configurazione per VDA. Ci sono due gruppi distinti, Lista o Graduatoria.
 * il campo e' DO_NOMINATIVO.CDNQUALIFICATO (quando 1 o 2 Graduatoria, quando 3 Lista )
 * 
 * @author donisi
 */
public class StampaInterna extends AbstractSimpleReport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(StampaInterna.class.getName());

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

			AccessoSemplificato _db = new AccessoSemplificato(this);
			// per recuperare le informazioni utilizzate per la generazione del
			// report non e' necessario
			// l'utilizzo della transazione. Se necessaria nei passi successivi
			// verra' abilitata.
			_db.enableSimpleQuery();
			String apriFile = (String) request.getAttribute("apriFileBlob");
			//Stampa Grad o Lista
			String reportpath = "";
			
			SourceBean rowsSourceBean = (SourceBean) QueryExecutor.executeQuery("st_get_config_tipograd_cm",null,"SELECT","SIL_DATI");
			String ConfigGraduatoria_cm = (String) rowsSourceBean.getAttribute("ROW.codmonotipogradcm");		
			
			//ts_generale
			SourceBean sbGenerale = DBLoad.getInfoGenerali();
			String codRegioneSil = sbGenerale.getAttribute("CODREGIONESIL").toString();
			
			String prgRosa = (String) request.getAttribute("prgRosa");
			String prgTipoIncrocio = (String) request.getAttribute("prgTipoIncrocio");
			String reportpathVDA = "";
			String posizione = (String) request.getAttribute("posizione");
			boolean isIncrocioCM = prgTipoIncrocio.equals("10") || prgTipoIncrocio.equals("11") || prgTipoIncrocio.equals("12");
			
			//Tipo Stampa Gradu oppure Lista
			String tipo = (String) request.getAttribute("tipo");
			if(tipo != null && !tipo.equals("") && tipo.equals("list")){
				reportpath = "psi";
				reportpathVDA = "qualifica";
			}
								

			if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
				BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
				this.openDocument(request, response, prgDoc);
			} else {
				String tipoFile = (String) request.getAttribute("tipoFile");
				if (tipoFile != null) {
					setStrNomeDoc("StampaInterna." + tipoFile);
				} else {
					setStrNomeDoc("StampaInterna.pdf");
				}
				setStrDescrizione("Stampa Interna");
				
				/*if("3".equals(ConfigGraduatoria_cm)){
					if (prgTipoIncrocio.equals("10") || prgTipoIncrocio.equals("11") || prgTipoIncrocio.equals("12"))
						setReportPath("pubb/StampaInternaGrezza"+reportpath+"_CC.rpt");					
					else
						setReportPath("pubb/StampaInterna"+reportpath+"_CC.rpt");
				}else {
					if (prgTipoIncrocio.equals("10") || prgTipoIncrocio.equals("11") || prgTipoIncrocio.equals("12"))
						setReportPath("pubb/O_StampaInternaGrezza_CC.rpt");					
					else
						setReportPath("pubb/O_StampaInterna_CC.rpt");
					
				}*/
				
				
				if("3".equals(ConfigGraduatoria_cm)){
					if (isIncrocioCM)
						setReportPath("pubb/StampaInternaGrezza"+reportpath+"_CC.rpt");					
					else
						setReportPath("pubb/StampaInterna"+reportpath+"_CC.rpt");
				}else if("4".equals(ConfigGraduatoria_cm)){
					if (isIncrocioCM)
						setReportPath("pubb/StampaInternaGrezza"+"VDA"+reportpathVDA+"_CC.rpt");					
					else
						setReportPath("pubb/StampaInterna"+"VDA"+reportpathVDA+"_CC.rpt");
				
				}else{
					if (isIncrocioCM){
						setReportPath("pubb/O_StampaInternaGrezza_CC.rpt");					
					}else{
						//Regione Calabria
						if(codRegioneSil.equalsIgnoreCase("18")){
							setReportPath("pubb/O_StampaInterna_Cal_CC.rpt");
						}else{
							setReportPath("pubb/O_StampaInterna_CC.rpt");
						}
					}	
				}
					
					
					
			}

			String descrizione = (String) request.getAttribute("ConcatenaCpi");
			/*
			 * if (descrizione != null && !descrizione.equals("")) {
			 * setStrEnteRilascio(descrizione); }
			 */
			// recupero la descrizione del cpi
			_db.setSectionQuerySelect("GET_DESCRCPI_STAMPA");
			SourceBean beanRows = null;
			beanRows = (SourceBean) _db.doSelect(request, response);

			String strDescrizione = StringUtils.getAttributeStrNotNull(beanRows, "ROW.STRDESCRIZIONE");
			if (strDescrizione != null && !strDescrizione.equals("")) {
				setStrEnteRilascio(strDescrizione);
			}

			String codCpi = (String) request.getAttribute("CodCPI");
			if (codCpi != null && !codCpi.equals("")) {
				setCodCpi(codCpi);
			}
			
			Vector params = null;
			params = new Vector(5);

			String prgRichiestaAz = (String) request.getAttribute("prgRichiestaAz");
			if (prgRichiestaAz != null && !prgRichiestaAz.equals("")) {
				params.add(request.getAttribute("prgRichiestaAz"));
			} else {
				params.add("");
			}

			// String prgTipoIncrocio=(String)
			// request.getAttribute("prgTipoIncrocio");
			if (prgTipoIncrocio != null && !prgTipoIncrocio.equals("")) {
				params.add(request.getAttribute("prgTipoIncrocio"));
			} else {
				params.add("");
			}

			//String posizione = (String) request.getAttribute("posizione");
			if (posizione != null && !posizione.equals("")) {
				params.add(request.getAttribute("posizione"));
			} else {
				params.add("");
			}
			
			if (prgRosa != null && !prgRosa.equals("")) {
				params.add(request.getAttribute("prgRosa"));
			}
			else {
				params.add("");
			}
			
			RequestContainer requestContainer = getRequestContainer();
			SessionContainer session = requestContainer.getSessionContainer();
			String encryptKey = (String) session.getAttribute("_ENCRYPTER_KEY_");
			if (encryptKey != null && !encryptKey.equals("")) {
				params.add(encryptKey);
			}
			else {
				params.add("");
			}
			
			if (isIncrocioCM) {		// per la gestione del collocamento mirato
				SessionContainer sessione = RequestContainer.getRequestContainer().getSessionContainer();
				User user = (User) sessione.getAttribute(User.USERID);
				String cpi = user.getCodRif();
				if (cpi != null &&  !cpi.equals(""))
					params.add(cpi);            			
				else
					params.add("-1");              
				
				// 0 config. default - 1 config. custom (RER)
				UtilsConfig utility = new UtilsConfig("CM_STAMP");
				String configStampeCustom = utility.getConfigurazioneDefault_Custom();
				if (configStampeCustom != null &&  !configStampeCustom.equals(""))
					params.add(configStampeCustom);            
				else
					params.add("0");              
			}
								
			setParams(params);

			String tipoDoc = (String) request.getAttribute("tipoDoc");
			if (tipoDoc != null)
				setCodTipoDocumento(tipoDoc);

			String strChiaveTabella = it.eng.sil.util.Utils.notNull(request.getAttribute("PRGRICHIESTAAZ"));
			if (!strChiaveTabella.equals("")) {
				setStrChiavetabella(strChiaveTabella);
			}
			BigDecimal prgAzienda = new BigDecimal((String) request.getAttribute("prgAzienda"));
			BigDecimal prgUnita = new BigDecimal((String) request.getAttribute("prgUnita"));
			if (("10").equals(prgTipoIncrocio) || ("11").equals(prgTipoIncrocio) || ("12").equals(prgTipoIncrocio)) {
				setPagina("CMGestGraduatoriePage");
			} else {
				setPagina("ASGestGraduatoriePage");
			}
			setPrgAzienda(prgAzienda);
			setPrgUnita(prgUnita);

			com.inet.report.Engine eng = null;

			String salva = (String) request.getAttribute("salvaDB");
			String apri = (String) request.getAttribute("apri");

			if (salva != null && salva.equalsIgnoreCase("true")) {
				if (!prgTipoIncrocio.equals("10") && !prgTipoIncrocio.equals("11") && !prgTipoIncrocio.equals("12")) {
					insertDocument(request, response);
				} else {
					txExec = new TransactionQueryExecutor(getPool());
					txExec.initTransaction();
					eng = apriRep(request, txExec.getDataConnection().getInternalConnection());
					if (insertDocument(request, response, txExec, eng)) // se
																		// FAIL
																		// il
																		// metodo
																		// ritorna
																		// false
						txExec.commitTransaction();
					else
						throw new Exception("Errore nella creazione del documento");
				}
			} else if (apri != null && apri.equalsIgnoreCase("true")) {
				if (!prgTipoIncrocio.equals("10") && !prgTipoIncrocio.equals("11") && !prgTipoIncrocio.equals("12")) {
					showDocument(request, response);
				} else {
					eng = apriRep(request, DataConnectionManager.getInstance().getConnection().getInternalConnection());
					showDocument(request, response, eng);
				}
			}
		} catch (Exception e) {
			setOperationFail(request, response, e);
			it.eng.sil.util.TraceWrapper.fatal( _logger,getClass().getName() + "::service()", e);
			// reportFailure(MessageCodes.General.OPERATION_FAIL, e,
			// "DiagnosiFunzionale.service()", "");
			if (txExec != null) {
				try {
					txExec.rollBackTransaction();
				} catch (EMFInternalError e1) {
					it.eng.sil.util.TraceWrapper.debug(_logger,
							"Stampa DiagnosiFunzionale fallita. Fallito anche il tentativo successivo di roolback.",
							(Exception) e1);

				}
			}
		}
	}

	private Engine apriRep(SourceBean request, java.sql.Connection conn1) throws Exception {

		String prgRosa = "" + request.getAttribute("prgRosa");

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer session = requestContainer.getSessionContainer();
		String encryptKey = (String) session.getAttribute("_ENCRYPTER_KEY_");

		String tipoFile = (String) request.getAttribute("tipoFile");
		SourceBean rowsSourceBean = (SourceBean) QueryExecutor.executeQuery("st_get_config_tipograd_cm",null,"SELECT","SIL_DATI");
		String ConfigGraduatoria_cm = (String) rowsSourceBean.getAttribute("ROW.codmonotipogradcm");
		
		//Add 10/05/2010
		String reportpath = "";
		//String codmannota = "";
		String strwhere = "";
		int numsubreport = 5;
		
		String tipo = (String) request.getAttribute("tipo");
		if("3".equals(ConfigGraduatoria_cm)){					
			numsubreport = 3;
			if(tipo != null && !tipo.equals("") && tipo.equals("list")){
				reportpath = "psi";
				strwhere = " and r.CODCMANNOTA is not null ";
			}else {			
				strwhere = " and r.CODCMANNOTA is null ";
			}
				
		} 
		
		
		//Add  17/10/2010  -  Graduatoria e Lista VDA
		
		if("4".equals(ConfigGraduatoria_cm)){				
			reportpath = "VDA";
			numsubreport = 3;
			if(tipo != null && !tipo.equals("") && tipo.equals("list")){				
				strwhere = " and r.CDNQUALIFICATO = 3 ";
				reportpath = reportpath + "qualifica";
			} else{
				strwhere = " and (r.CDNQUALIFICATO = 1 or r.CDNQUALIFICATO = 2 )";	
				
			}
		} 
		
		
		
		
		

		if (tipoFile != null)
			setFileType(tipoFile);

		Engine engine = new Engine(getFileType());

		// ---------------------------------------------------- lista lavoratori
		// in graduatoria
		java.sql.Statement st5 = conn1.createStatement();
		String sql5 = "				 select r.CDNLAVORATORE, " + "				  (SELECT 			        "
				+ "						 tab.codTipoMov || ' - ' || TO_CHAR (tab.datiniziomov, 'dd/mm/yyyy') "
				+ "					  FROM am_movimento tab, de_contratto con "
				+ "					 WHERE tab.cdnlavoratore = r.CDNLAVORATORE " + "					   AND tab.codstatoatto = 'PR' "
				+ "					   AND (       (   (    tab.datfinemovEffettiva IS NULL " + "										AND tab.datiniziomov = "
				+ "											   (SELECT MAX (tab1.datiniziomov) " + "												  FROM am_movimento tab1 "
				+ "												 WHERE tab1.datfinemovEffettiva IS NULL "
				+ "												   AND tab1.cdnlavoratore = tab.cdnlavoratore "
				+ "												   AND tab1.codstatoatto = 'PR' " + "												   AND tab1.codtipomov <> 'CES') "
				+ "									   ) " + "									OR (    NOT EXISTS ( " + "											   SELECT tab1.datiniziomov "
				+ "												 FROM am_movimento tab1 " + "												WHERE tab1.datfinemovEffettiva IS NULL "
				+ "												  AND tab1.cdnlavoratore = tab.cdnlavoratore "
				+ "												  AND tab1.codstatoatto = 'PR' " + "												  AND tab1.codtipomov <> 'CES') "
				+ "										AND tab.datfinemovEffettiva = " + "											   (SELECT MAX (tab1.datfinemovEffettiva) "
				+ "												  FROM am_movimento tab1 " + "												 WHERE NOT tab1.datfinemovEffettiva IS NULL "
				+ "												   AND tab1.cdnlavoratore = tab.cdnlavoratore "
				+ "												   AND tab1.codstatoatto = 'PR' " + "												   AND tab1.codtipomov <> 'CES') "
				+ "									   ) " + "								   ) " + "							   AND tab.codtipomov <> 'CES' "
				+ "							OR (    NOT EXISTS ( " + "									   SELECT tab1.datiniziomov "
				+ "										 FROM am_movimento tab1 " + "										WHERE tab1.cdnlavoratore = tab.cdnlavoratore "
				+ "										  AND tab1.codstatoatto = 'PR' " + "										  AND tab1.codtipomov <> 'CES') "
				+ "								AND tab.datiniziomov = " + "									   (SELECT MAX (tab1.datiniziomov) "
				+ "										  FROM am_movimento tab1 " + "										 WHERE tab1.cdnlavoratore = tab.cdnlavoratore "
				+ "										   AND tab1.codstatoatto = 'PR' " + "										   AND tab1.codtipomov = 'CES') "
				+ "								AND tab.codtipomov = 'CES' " + "							   ) " + "						   ) "
				+ "					    AND tab.codcontratto = con.codcontratto(+) "
				+ "					    AND ROWNUM = 1) as codTipoMov		         " + "				from do_nominativo r  "
				+ "						LEFT OUTER JOIN cm_iscr_art1 ia ON (ia.cdnlavoratore = r.cdnlavoratore) "
				+ "						inner join an_lavoratore an on (r.CDNLAVORATORE=an.CDNLAVORATORE) "
				+ "						inner join do_rosa on (r.PRGROSA=do_rosa.PRGROSA)  "
				+ "						inner join de_comune dom on (an.CODCOMDOM=dom.CODCOM)  "
				+ "						inner join de_provincia pr on (dom.CODPROVINCIA=pr.CODPROVINCIA) "
				+ "						left outer join de_cpi on (r.CODCPITIT=de_cpi.CODCPI)  "
				+ "						inner join do_incrocio on (do_rosa.PRGINCROCIO=do_incrocio.PRGINCROCIO) "
				+ "						inner join do_richiesta_az rich1 on (do_incrocio.PRGRICHIESTAAZ=rich1.PRGRICHIESTAAZ) "
				+ "						inner join de_tipo_incrocio ti on ti.prgtipoincrocio = do_incrocio.prgtipoincrocio "
				+ "						inner join de_tipo_rosa tr on tr.prgtiporosa = do_rosa.prgtiporosa "
				+ "						LEFT OUTER JOIN DO_EVASIONE ON (rich1.PRGRICHIESTAAZ=DO_EVASIONE.PRGRICHIESTAAZ) "
				+ "						LEFT OUTER JOIN DE_EVASIONE_RICH ON (DO_EVASIONE.CODEVASIONE=DE_EVASIONE_RICH.CODEVASIONE) "
				+ "						where r.prgrosa = " + prgRosa + "		"
				+ strwhere
				+ "						and r.CODTIPOCANC is null and ia.datfine is null " 
				+ "						and (ia.prgiscrart1 in (select to_number(dc.strchiavetabella) from am_documento_coll dc "
				+ "						inner join am_documento ad ON (ad.prgdocumento = dc.prgdocumento and ad.codtipodocumento = 'ILS' and ad.CODSTATOATTO  != 'AN')) "
				+ "						or ia.prgiscrart1 is null) "
				+ "						and (ia.codtipolista is null or ia.codtipolista = rich1.codtipolista) order by numordine ";

		// ---------------------------------------------------- date CM
		java.sql.Statement st0 = conn1.createStatement();
		String sql0 = "	select r.PRGNOMINATIVO, " + "				r.CDNLAVORATORE, "
				+ " 			to_char(ia.datiscrlistaprov, 'dd/mm/yyyy')   as datiscrlistaprov, "
				+ " 			to_char(ia.datiscralbo, 'dd/mm/yyyy') as datiscralbo, ia.numannopunteggio  as numannopunteggio, ia.numpunteggio as numpunteggio, "
				+ "				(select to_char(i.datdatainizio, 'dd/mm/yyyy') " + "						from am_cm_iscr i "
				+ "						inner join de_cm_tipo_iscr t on t.codcmtipoiscr = i.codcmtipoiscr "
				+ "						inner join AM_DOCUMENTO_COLL COLL on COLL.STRCHIAVETABELLA = I.PRGCMISCR "
				+ "						inner join AM_DOCUMENTO DOC on DOC.PRGDOCUMENTO = COLL.PRGDOCUMENTO  "
				+ "						where DECRYPT(i.cdnlavoratore,'"
				+ encryptKey
				+ "') = r.CDNLAVORATORE "
				+ "						and i.datdatafine is null "
				+ "						and t.codmonotiporagg = DECODE(do_incrocio.prgtipoincrocio, 10, 'D', 11, 'A') "
				+ "						AND I.PRGCMISCR = COLL.STRCHIAVETABELLA  "
				+ "						AND DOC.CODTIPODOCUMENTO = 'L68'  "
				+ "						AND DOC.CODSTATOATTO = 'PR' AND DOC.CDNLAVORATORE = r.CDNLAVORATORE)    "
				+ "						 as datdatainizio, "
				+ "				(select to_char(i.datanzianita68, 'dd/mm/yyyy') "
				+ "						from am_cm_iscr i "
				+ "						inner join de_cm_tipo_iscr t on t.codcmtipoiscr = i.codcmtipoiscr "
				+ "						inner join AM_DOCUMENTO_COLL COLL on COLL.STRCHIAVETABELLA = I.PRGCMISCR "
				+ "						inner join AM_DOCUMENTO DOC on DOC.PRGDOCUMENTO = COLL.PRGDOCUMENTO  "
				+ "						where DECRYPT(i.cdnlavoratore,'"
				+ encryptKey
				+ "') = r.CDNLAVORATORE "
				+ "						and i.datdatafine is null "
				+ "						and t.codmonotiporagg = DECODE(do_incrocio.prgtipoincrocio, 10, 'D', 11, 'A') "
				+ "						AND I.PRGCMISCR = COLL.STRCHIAVETABELLA  "
				+ "						AND DOC.CODTIPODOCUMENTO = 'L68'  "
				+ "						AND DOC.CODSTATOATTO = 'PR' AND DOC.CDNLAVORATORE = r.CDNLAVORATORE)      "
				+ "						 as datanzianita68	   "
				+ "				from do_nominativo r  "
				+ "						LEFT OUTER JOIN cm_iscr_art1 ia ON (ia.cdnlavoratore = r.cdnlavoratore) "
				+ "						inner join an_lavoratore an on (r.CDNLAVORATORE=an.CDNLAVORATORE) "
				+ "						inner join do_rosa on (r.PRGROSA=do_rosa.PRGROSA)  "
				+ "						inner join de_comune dom on (an.CODCOMDOM=dom.CODCOM)  "
				+ "						inner join de_provincia pr on (dom.CODPROVINCIA=pr.CODPROVINCIA) "
				+ "						left outer join de_cpi on (r.CODCPITIT=de_cpi.CODCPI)  "
				+ "						inner join do_incrocio on (do_rosa.PRGINCROCIO=do_incrocio.PRGINCROCIO) "
				+ "						inner join do_richiesta_az rich1 on (do_incrocio.PRGRICHIESTAAZ=rich1.PRGRICHIESTAAZ) "
				+ "						inner join de_tipo_incrocio ti on ti.prgtipoincrocio = do_incrocio.prgtipoincrocio "
				+ "						inner join de_tipo_rosa tr on tr.prgtiporosa = do_rosa.prgtiporosa "
				+ "						LEFT OUTER JOIN DO_EVASIONE ON (rich1.PRGRICHIESTAAZ=DO_EVASIONE.PRGRICHIESTAAZ) "
				+ "						LEFT OUTER JOIN DE_EVASIONE_RICH ON (DO_EVASIONE.CODEVASIONE=DE_EVASIONE_RICH.CODEVASIONE) "
				+ "						where r.prgrosa = " + prgRosa + "						" 
				+ strwhere			
				+ "						and r.CODTIPOCANC is null and ia.datfine is null " 
				+ "						and (ia.prgiscrart1 in (select to_number(dc.strchiavetabella) from am_documento_coll dc "
				+ "						inner join am_documento ad ON (ad.prgdocumento = dc.prgdocumento and ad.codtipodocumento = 'ILS' and ad.CODSTATOATTO  != 'AN')) "
				+ "						or ia.prgiscrart1 is null) "
				+ "						and (ia.codtipolista is null or ia.codtipolista = rich1.codtipolista) order by numordine ";

		java.sql.ResultSet rs5 = st5.executeQuery(sql5);
		java.sql.ResultSet rs0 = st0.executeQuery(sql0);
		// String ultimoMov = rs6.getString("ultimoMovimento");
		
	
		//String path = ConfigSingleton.getRootPath() + "/WEB-INF/report/pubb/StampaInternaGrezza_CC.rpt";
		/*String path1 = "";
		if("3".equals(ConfigGraduatoria_cm)){
			path1="/WEB-INF/report/pubb/StampaInternaGrezza"+reportpath+"_CC.rpt";
		} else {
			path1="/WEB-INF/report/pubb/O_StampaInternaGrezza"+reportpath+"_CC.rpt";
		}*/
		
		String path1 = "";
		if("3".equals(ConfigGraduatoria_cm)){
			path1="/WEB-INF/report/pubb/StampaInternaGrezza"+reportpath+"_CC.rpt";
		} else if  ("4".equals(ConfigGraduatoria_cm)){  //VDA
			path1="/WEB-INF/report/pubb/StampaInternaGrezza"+reportpath+"_CC.rpt";
		} else {
			path1="/WEB-INF/report/pubb/O_StampaInternaGrezza"+reportpath+"_CC.rpt";
		}
		
		
		
		
		String path = ConfigSingleton.getRootPath() + path1;
		// engine.setReportFile(path);
		File f = new File(path);
		engine.setReportFile("file:" + f.getAbsolutePath());

		// engine.setPrompt("parPrgDiagnosiFunzionale", cdnLavoratore);

		// Parametri di protocollazione
		String numProtoc = Utils.notNull(request.getAttribute("numProt"));
		String numAnno = Utils.notNull(request.getAttribute("annoProt"));
		String dataProt = Utils.notNull(request.getAttribute("dataOraProt"));
		String docInOut = Utils.notNull(request.getAttribute("docInOut"));
		if (!numProtoc.equals("") && !numAnno.equals("")) {
			String descrizioneInOut;
			if (docInOut.equalsIgnoreCase("I"))
				descrizioneInOut = "Input";
			else
				descrizioneInOut = "Output";
			engine.setPrompt("numProt", numProtoc);
			engine.setPrompt("numAnnoProt", numAnno);
			engine.setPrompt("dataProt", dataProt);
			engine.setPrompt("docInOut", descrizioneInOut);
		}

		String nomeReport = engine.getSubReport(numsubreport).getReportTitle();
		String nomeNewReport = engine.getSubReport(0).getReportTitle();

		com.inet.report.Engine subreport5 = engine.getSubReport(numsubreport);
		subreport5.setData(rs5);

		com.inet.report.Engine subreport0 = engine.getSubReport(0);
		subreport0.setData(rs0);

		Hashtable rsData = new Hashtable();
		rsData.put("subreport5", rs5);
		rsData.put("subreport0", rs0);
		// gli Statement andranno chiusi
		
		rsData.put("statements", st5);
		rsData.put("statements", st0);

		// Rifare per ogni sotto-report
		StampaInternaResultSet dfrs = new StampaInternaResultSet(rsData);
		// ora i result set verranno impostati nei subreports. Alla fine
		// verranno chiusi sia i result set che gli statement.
		dfrs.setDataTo(engine);

		if (ProtocolloDocumentoUtil.tipoProtocollo() == ProtocolloDocumentoUtil.TIPO_DOCAREA_VER_161) {

			st5 = conn1.createStatement();
			rs5 = st5.executeQuery(sql5);

			st0 = conn1.createStatement();
			rs0 = st0.executeQuery(sql0);

			rsData = new Hashtable();
			rsData.put("subreport5", rs5);
			rsData.put("subreport0", rs0);

			// gli Statement andranno chiusi
			rsData.put("statements", new Object[] { st5, st0 });
			dfrs = new StampaInternaResultSet(rsData);
			Hashtable userData = new Hashtable();
			// si passa l'oggetto all'engine che lo utilizzara' prima della
			// creazione del report numero due (SOLO caso DOCAREA)
			userData.put("ReportResultSet", dfrs);

			engine.setUserData(userData);
		}

		return engine;
	}

	private String getPool() {
		return (String) getConfig().getAttribute("POOL");
	}

}
