/*
 * Creato il 27-mar-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.action.report;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.error.EMFInternalError;
import com.inet.report.Engine;

/**
 * @author melandri
 *
 * Per modificare il modello associato al commento di questo tipo generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AccessoSemplificato;
import it.eng.sil.security.User;
import it.eng.sil.util.EncryptDecryptUtils;

public class AdesioneChiamata extends AbstractSimpleReport {

	 static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AdesioneChiamata.class.getName());

	private String fileType= Engine.EXPORT_PDF;

	public void setFileType(String eft) {
		if (eft.equalsIgnoreCase("PDF"))
		   this.fileType = Engine.EXPORT_PDF;
		else if (eft.equalsIgnoreCase("RTF"))
		   this.fileType = Engine.EXPORT_RTF;
		else if (eft.equalsIgnoreCase("HTML"))
		   this.fileType = Engine.EXPORT_HTML;
		else if (eft.equalsIgnoreCase("XLS"))
		   this.fileType = Engine.EXPORT_XLS;
		else this.fileType = Engine.EXPORT_PDF; 
	} 
	
	public String getFileType(){
		return this.fileType;
	}	
		
	public void service(SourceBean request, SourceBean response) {
		
		super.service(request, response);

		TransactionQueryExecutor txExec = null;
		try {
			
			String apriFile = (String)request.getAttribute("apriFileBlob");
			if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
				BigDecimal prgDoc = new BigDecimal((String)request.getAttribute("prgDocumento"));
				this.openDocument(request, response, prgDoc);
			} else {
				String tipoFile = (String)request.getAttribute("tipoFile");
				if (tipoFile != null)
					setStrNomeDoc("AdChiamAvvNum." + tipoFile);
				else
					setStrNomeDoc("AdChiamAvvNum.pdf");
				
				setStrDescrizione("Adesione chiamata avviamento numerico");
				setReportPath("Amministrazione/AdChiamAvvNum.rpt");


				//String numProtoc  = Utils.notNull(request.getAttribute("numProt"));
				//String numAnno  = Utils.notNull(request.getAttribute("annoProt"));
				//String dataProt = Utils.notNull(request.getAttribute("dataOraProt"));
				/*String docInOut = Utils.notNull(request.getAttribute("docInOut"));
				String descrizioneInOut;
				if (docInOut.equalsIgnoreCase("I")){
					descrizioneInOut = "Input";
				}else{
					descrizioneInOut = "Output";
				}*/
				
				AccessoSemplificato _db = new AccessoSemplificato(this);				
				SourceBean beanRows = null;
				_db.setSectionQuerySelect("GET_REDDITO");
				beanRows = _db.doSelect(request, response, false);
				SourceBean provRows = null;
				_db.setSectionQuerySelect("GET_CODICE_PROVINCIA_ADESIONE");
				provRows = _db.doSelect(request, response, false);
				String targaProvincia = SourceBeanUtils.getAttrStrNotNull(provRows, "ROW.strtarga");
				String codRegioneSil = SourceBeanUtils.getAttrStrNotNull(provRows, "ROW.codregione");
				
				SourceBean iseeRows = null;
				_db.setSectionQuerySelect("GET_ISEE");
				iseeRows = _db.doSelect(request, response, false);
				BigDecimal numValoreIsee = SourceBeanUtils.getAttrBigDecimal(iseeRows, "ROW.numvaloreisee", null);
				String dataInizioValIsee = SourceBeanUtils.getAttrStrNotNull(iseeRows, "ROW.datainizioval");
				
				// ------ impostazione parametri del report
				Map prompts = new HashMap();

/*				if (!numProtoc.equals("") && ! numAnno.equals("")) { 			     
					String descrizioneInOut;
					if (docInOut.equalsIgnoreCase("I")){
						descrizioneInOut = "Input";
					}else{
						descrizioneInOut = "Output";
					}        
					prompts.put("numProt", numProtoc);
					prompts.put("numAnnoProt",numAnno);
					prompts.put("dataProt", dataProt);
					prompts.put("docInOut", descrizioneInOut);
				}
*/				
				BigDecimal numReddito = SourceBeanUtils.getAttrBigDecimal(beanRows, "ROW.NUMREDDITO", null);
				BigDecimal numAnno = SourceBeanUtils.getAttrBigDecimal(beanRows, "ROW.NUMANNO", null);
				
				//String numReddito = SourceBeanUtils.getAttrBigDecimal(beanRows, "ROW.NUMREDDITO") != null ? ((BigDecimal)SourceBeanUtils.getAttrBigDecimal(beanRows, "ROW.NUMREDDITO")).toString():"";
				//String numAnno = SourceBeanUtils.getAttrBigDecimal(beanRows, "ROW.NUMANNO") != null ? ((BigDecimal)SourceBeanUtils.getAttrBigDecimal(beanRows, "ROW.NUMANNO")).toString():""; 
				//String numReddito = SourceBeanUtils.getAttrBigDecimal(beanRows, "ROW.NUMREDDITO") != null ? ((BigDecimal)SourceBeanUtils.getAttrBigDecimal(beanRows, "ROW.NUMREDDITO")).toString():"";
				
				String lav = ""+request.getAttribute("cdnLavoratore");
				String az = ""+request.getAttribute("PRGRICHIESTAAZ");
				prompts.put("prgRichiesta", request.getAttribute("PRGRICHIESTAAZ"));
				prompts.put("cdnLavoratore", request.getAttribute("cdnLavoratore"));
				prompts.put("prgNominativo", request.getAttribute("PRGNOMINATIVO"));
					
				if(numAnno != null) {
					prompts.put("numAnno", numAnno.toString());
				} else prompts.put("numAnno", "");
				
				if(numReddito != null) {
					prompts.put("numReddito", numReddito.toString());
				} else prompts.put("numReddito", "");
				
				if (codRegioneSil.equalsIgnoreCase(PropertiesReport.COD_REGIONE_RER)) {
					prompts.put("codTarga", targaProvincia);
				}
				else prompts.put("codTarga", "");
				
				// donato
				String strChiaveTabella = it.eng.sil.util.Utils.notNull(request.getAttribute("PRGRICHIESTAAZ"));				
				if (!strChiaveTabella.equals("")) {
					setStrChiavetabella(strChiaveTabella);
				}	  
				
				setPagina("CMListaAdesioniPage");
				
				
				// per la gestione del collocamento mirato
				SessionContainer sessione = RequestContainer.getRequestContainer().getSessionContainer();
				User user = (User) sessione.getAttribute(User.USERID);
				String cpi = user.getCodRif();
				if (cpi != null &&  !cpi.equals(""))
					prompts.put("codCpiUser",cpi);
				else
					prompts.put("codCpiUser","-1");              
				// 0 config. default - 1 config. custom (RER)
				UtilsConfig utility = new UtilsConfig("CM_STAMP");
				String configStampeCustom = utility.getConfigurazioneDefault_Custom();
				if (configStampeCustom != null &&  !configStampeCustom.equals(""))
					prompts.put("configStampeCustom",configStampeCustom);
				else
					prompts.put("configStampeCustom","0");             
				
				
				String cdnLav = SourceBeanUtils.getAttrStrNotNull(request, "cdnLavoratore");
				String cdnLavoratoreCrypted = EncryptDecryptUtils.encrypt(cdnLav);
				prompts.put("cdnLavoratoreEncrypt", cdnLavoratoreCrypted);
				
				if (numValoreIsee != null){
					prompts.put("numValoreIsee", numValoreIsee.toString());
				} else prompts.put("numValoreIsee", "");
				
				prompts.put("datInizioValIsee", dataInizioValIsee);
				
						
				//prompts.put("docInOut", descrizioneInOut);
				//prompts.put("numProt", numProt);
				//prompts.put("numAnnoProt", annoProt);
				//prompts.put("dataProt", dataProtocollo);
			
				// ------ solo se e' richiesta la protocollazione i parametri vengono inseriti nella Map
				addPromptFieldsProtocollazione(prompts, request);

				// ------ ora si chiede di usare il passaggio dei parametri per nome e 
				// ------ non per posizione (col vettore, passaggio di default)
				setPromptFields(prompts);

				com.inet.report.Engine eng = null;
				
				String salva = (String)request.getAttribute("salvaDB");
				String apri = (String)request.getAttribute("apri");
				if (salva != null && salva.equalsIgnoreCase("true")){		
					//insertDocument(request, response);
					txExec = new TransactionQueryExecutor(getPool());
					txExec.initTransaction();

					eng = apriSubReport(request, txExec.getDataConnection().getInternalConnection(), null);
					// dopo aver recuperato le info per generare il report abilito la transazione
					// per l'inserimento del documento e per le eventuali operazioni successive
					if (insertDocument(request, response, txExec, eng)){ // se FAIL il metodo ritorna false 
						txExec.commitTransaction();
					}else{ 
						throw new Exception("Errore nella creazione del documento");					
					}				
					
				}else if (apri != null && apri.equalsIgnoreCase("true")){
					//showDocument(request, response);
					
					DataConnection dc = DataConnectionManager.getInstance().getConnection();
					
					try{
						eng = apriSubReport(request, dc.getInternalConnection(), null);
					}finally{
						com.engiweb.framework.dbaccess.Utils.releaseResources(dc, null, null);
					}
					showDocument(request, response, eng);										
				}
					
					
			} 
/*		} catch (Exception e) {
			setOperationFail(request, response, e);
			reportFailure(MessageCodes.General.OPERATION_FAIL, e, "AdChiamAvvNum.service()", "");	
		}*/
		} catch (Exception e) {
			setOperationFail(request, response, e);
			reportFailure(MessageCodes.General.OPERATION_FAIL, e, "AdesioneChiamata.service()", "");
			if (txExec!=null) {
				try {
					txExec.rollBackTransaction();
				} catch (EMFInternalError e1) {
it.eng.sil.util.TraceWrapper.debug( _logger, "Stampa AdesioneChiamata fallita. Fallito anche il tentativo successivo di roolback.",  (Exception)e1);

				}
			}			
		}		
	}

	private Engine apriSubReport(SourceBean request, Connection conn1, String codStatoAtto) throws Exception  {
		
		String cdnLavoratore = "" + request.getAttribute("cdnLavoratore");
		String prgRichiestaAz = "" + request.getAttribute("PRGRICHIESTAAZ");
		
    	
		String tipoFile = (String)request.getAttribute("tipoFile");
		if(tipoFile != null)
			setFileType(tipoFile);
			
		Engine engine = new Engine( getFileType() );

		// Codifica del cdnLavoratore
		String cdnLavoratoreCrypted = EncryptDecryptUtils.encrypt(cdnLavoratore);

//		---------------------------------------------------- Sottoreport "CollocamentoMirato" del report AdChiamAvvNum.rpt  
		java.sql.Statement st1 = conn1.createStatement();
		String sql1 = "	select iscr.DATDATAINIZIO,tipo_iscr.STRDESCRIZIONE, iscr.DATANZIANITA68, iscr.DATDATAFINE "+
					  "   from am_cm_iscr iscr, de_cm_tipo_iscr tipo_iscr, AM_DOCUMENTO_COLL COLL, AM_DOCUMENTO DOC "+
					  "  where iscr.CDNLAVORATORE = '"+cdnLavoratoreCrypted+"' "+
					  "	   and iscr.CODCMTIPOISCR=tipo_iscr.CODCMTIPOISCR "+
					  "    and iscr.DATDATAFINE is null "+
					  "    and iscr.PRGCMISCR = COLL.STRCHIAVETABELLA "+
					  "    and COLL.PRGDOCUMENTO = DOC.PRGDOCUMENTO AND DOC.CODTIPODOCUMENTO = 'L68' "+
					  "    and DOC.CDNLAVORATORE = " + cdnLavoratore + " and DOC.CODSTATOATTO = 'PR' ";
					  
		
	    java.sql.ResultSet rs1 = st1.executeQuery(sql1);
		
//		----------------------------------------------------

		String path = ConfigSingleton.getRootPath()+ "/WEB-INF/report/Amministrazione/AdChiamAvvNum.rpt";
		File f = new File(path);
		engine.setReportFile("file:"+f.getAbsolutePath());

		
		com.inet.report.Engine subreport1 = engine.getSubReport(6);		
		String nomeReport = subreport1.getReportTitle(); // nome Sottoreport n°6
		subreport1.setData(rs1);
		
		// chiusura dei ResultSet
		rs1.close();
		st1.close();

		
		return engine;
	}


	private  String getPool() {
		return (String) getConfig().getAttribute("POOL");
	}

} //class