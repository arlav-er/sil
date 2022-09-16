package it.eng.sil.action.report.amministrazione;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.bean.Documento;
import it.eng.sil.module.AccessoSemplificato;
import it.eng.sil.security.User;

	public class StampaNullaOsta extends AbstractSimpleReport {

	 static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(StampaNullaOsta.class.getName());

		public void service(SourceBean request, SourceBean response) {
			super.service(request, response);
			
			TransactionQueryExecutor txExec = null;
				try {
					
					AccessoSemplificato _db = new AccessoSemplificato(this);				
					txExec = new TransactionQueryExecutor(_db.getPool());
					_db.enableTransactions(txExec);
					txExec.initTransaction();
					String tipoFile = "";
					SourceBean beanRows = null;
					_db.setSectionQuerySelect("GET_PROSP_STOR");
					beanRows = _db.doSelect(request, response, false);
					BigDecimal prgDoc = null;
					BigDecimal prgDocBlob = null;
					
					String apriFile = (String) request.getAttribute("apriFileBlob");
					if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
						prgDoc = new BigDecimal( (String) request.getAttribute("prgDocumento") );
						this.openDocument(request, response,prgDoc);
					} else {
						setStrDescrizione("Nulla Osta");
						tipoFile = (String)request.getAttribute("tipoFile");
						if (tipoFile != null)
							setStrNomeDoc("NullaOsta." + tipoFile);
						else
							setStrNomeDoc("NullaOsta.pdf");
						setReportPath("Amministrazione/NullaOsta_CC.rpt");
					
						prgDoc = new BigDecimal( (String) request.getAttribute("prgDoc") );
					
						Vector params = null;
						params = new Vector(6);  		
	  			
						String prgNullaOsta=(String) request.getAttribute("prgNullaOsta");
						if (prgNullaOsta != null && !prgNullaOsta.equals("")) {
							params.add(request.getAttribute("prgNullaOsta"));            
						}			
						else{
							params.add("");              
						}
						String cdnLavoratoreEncrypt=(String) request.getAttribute("cdnLavoratoreEncrypt");
						if (cdnLavoratoreEncrypt != null && !cdnLavoratoreEncrypt.equals("")) {
							params.add(request.getAttribute("cdnLavoratoreEncrypt"));            
						}			
						else{
							params.add("");              
						}
					
						params.add(StringUtils.getAttributeStrNotNull(beanRows, "ROW.DATRIFINFORZA"));
					
						String diagnosi=(String) request.getAttribute("diagnosi");
						if (diagnosi != null && !diagnosi.equals("")) {
							params.add(request.getAttribute("diagnosi"));            
						}			
						else{
							params.add("");              
						}
					
						// per la gestione del collocamento mirato
						SessionContainer sessione = RequestContainer.getRequestContainer().getSessionContainer();
						User user = (User) sessione.getAttribute(User.USERID);
						String cpi = user.getCodRif();
						if (cpi != null &&  !cpi.equals("")) {
							params.add(cpi);            
						}			
						else{
							params.add("-1");              
						}
						
						// per la gestione del collocamento mirato
						// 0 config. default - 1 config. custom (RER)
						UtilsConfig utility = new UtilsConfig("CM_STAMP");
						String configStampeCustom = utility.getConfigurazioneDefault_Custom();
						if (configStampeCustom != null &&  !configStampeCustom.equals("")) {
							params.add(configStampeCustom);            
						}			
						else{
							params.add("-1");              
						}
						
						setParams(params);
				
						String tipoDoc = (String) request.getAttribute("tipoDoc");
						if (tipoDoc != null ) setCodTipoDocumento(tipoDoc);
        				Documento doc = new Documento(prgDoc);
						String salva = (String) request.getAttribute("salvaDB");
						String apri  = (String) request.getAttribute("apri");
						if(salva != null && salva.equalsIgnoreCase("true")) {
							doc.setStrNomeDoc("NullaOsta."+tipoFile);
							doc.setStrDescrizione("Nulla Osta");
							doc.setCrystalClearRelativeReportFile("Amministrazione/NullaOsta_CC.rpt");
							doc.setCrystalClearPrompts(params);
							doc.createReportTempFile(txExec);
							doc.inserisciBlob(txExec);
							doc.setNumKloDocumento(doc.getNumKloDocumento().add(new BigDecimal(1)));
							doc.aggiornaDocumento(txExec);
							setOperationSuccess(request, response);
						}
					
						if ((apri != null) && apri.equalsIgnoreCase("true")) {
							showDocument(request, response);
						}
					}
				
				txExec.commitTransaction();	
					
		} catch (Exception e) {
			if (txExec!=null) try {
				txExec.rollBackTransaction();
			} catch (EMFInternalError e1) {
it.eng.sil.util.TraceWrapper.fatal( _logger,"Impossibile eseguire la rollBack nella transazione della stampa del Nulla Osta", (Exception)e1);

			}
it.eng.sil.util.TraceWrapper.fatal( _logger,"Errore nella stampa del Nulla Osta", e);

			setOperationFail(request, response, e);
		}
	}
}
			


