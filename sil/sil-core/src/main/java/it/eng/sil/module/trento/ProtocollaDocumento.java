package it.eng.sil.module.trento;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.bean.Documento;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.security.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigServlet;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.sil.module.firma.grafometrica.FirmaDocumenti;


public class ProtocollaDocumento  extends AbstractSimpleModule {
	
	private static final long serialVersionUID = -2248405266403803703L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ProtocollaDocumento.class.getName());
	
	public ProtocollaDocumento() {
	}

	public void service(SourceBean request, SourceBean response) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		
		// Segnalazione soli errori/problemi
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		int errorCode = MessageCodes.General.OPERATION_FAIL;
		Documento doc = null;
		TransactionQueryExecutor trans = null;
		BigDecimal prgDocumento = null;
		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		BigDecimal userid = new BigDecimal(user.getCodut());

		boolean protocollazione = (request.containsAttribute("GENERASTAMPA") && 
								   request.getAttribute("GENERASTAMPA").toString().equalsIgnoreCase("STAMPA"));
		
		SourceBean serviceResponse = getResponseContainer().getServiceResponse();
		SourceBean rowRegione = (SourceBean) serviceResponse.getAttribute("M_GetCodRegione.ROWS.ROW");
		String regione = StringUtils.getAttributeStrNotNull(rowRegione, "codregione"); 
		
//		// FV non server più		
//		String strTemplateStampa =  StringUtils.getAttributeStrNotNull(request, "PRGTEMPLATESTAMPA");
//		BigDecimal prgTemplateStampa = null;
//		if (!"".equals(strTemplateStampa)){
//			prgTemplateStampa= new BigDecimal(strTemplateStampa);
//		} else {
//			_logger.error("PRGTEMPLATESTAMPA non presente");
//			reportOperation.reportFailure(errorCode);
//		}
		
		try {
			String htmlDoc = null;
			htmlDoc = StringUtils.getAttributeStrNotNull(request, "EditorDefault");
			String token = "src=\"";
			String protocol = "";
			String host = "";
			//String port = "";
			String address = "";
			if(request.getAttribute("HOST")!=null&&!request.getAttribute("HOST").equals("")){
				protocol = (String)request.getAttribute("PROTOCOL");
				host = (String)request.getAttribute("HOST");
				address = protocol + "//"+host;
//				if(request.getAttribute("PORT")!=null&&!request.getAttribute("PORT").equals("")){
//					port = (String)request.getAttribute("PORT");
//					address = address + ":"  + port;
//				}
				htmlDoc= htmlDoc.replaceAll(token,token+address);
				
			}
			
			boolean footerContent = false;
			boolean footerContentPAT = false;
			boolean footerContentModCert = false;
			
			boolean footerContentUMB = false;
//					"Centro per l'Impiego di " +request.getAttribute("DESC_CPI") + " - " +request.getAttribute("INDIRIZZO") + ""
//					+ " - " + request.getAttribute("LOCALITA") + " "  + request.getAttribute("CAP") + " " + request.getAttribute("PROVINCIA") + ""
//					+ " - Tel. " + request.getAttribute("TELEFONO") + " - Fax " + request.getAttribute("FAX") + " - " + request.getAttribute("EMAIL");	
			
			//TODO FOOTER
			String tagFooter = "@Footer";
			int pos =htmlDoc.indexOf( tagFooter);
			//htmlDoc.toLowerCase().contains("@Footer".toLowerCase())
			if(pos !=-1){
				//commento perchè si vuole un footer con una dicitura statica
				//footerContent= (String)request.getAttribute("INDIRIZZO_STAMPA");
				//footerContent= "numero verde 800 264 760 ()dal lunedì al venerdì dalle 08:00 alle 18:00"
				//htmlDoc=htmlDoc.replace("\b@Footer\b","");
				htmlDoc = htmlDoc.substring(0,pos).concat(htmlDoc.substring(pos  + (tagFooter.length())));
				footerContent=true;
			}
			
			
			String tagFooterPAT = "@PATFooter";
			int posPAT =htmlDoc.indexOf( tagFooterPAT);
			if(posPAT !=-1){
				//commento perchè si vuole un footer con una dicitura statica
				//htmlDoc=htmlDoc.replace("\b@FooterPAT\b","");
				htmlDoc = htmlDoc.substring(0,posPAT).concat(htmlDoc.substring(posPAT  + (tagFooterPAT.length())));
				footerContentPAT=true;
			}

			
			String tagFooterModCert = "@ModCertificatoFooter";
			int posModCert =htmlDoc.indexOf( tagFooterModCert);
			if(posModCert !=-1){
				//commento perchè si vuole un footer con una dicitura statica
				//htmlDoc=htmlDoc.replace("\b@FooterModCertificato\b","");
				htmlDoc = htmlDoc.substring(0,posModCert).concat(htmlDoc.substring(posModCert  + (tagFooterModCert.length())));
				footerContentModCert=true;
			}
			
			String tagFooterUMB = "@UMBFooter";
			int posUMB =htmlDoc.indexOf( tagFooterUMB);
			if(posUMB !=-1){
				htmlDoc = htmlDoc.substring(0,posUMB).concat(htmlDoc.substring(posUMB  + (tagFooterUMB.length())));
				footerContentUMB=true;
			}

						
			//eliminato il flag predisposto dai template delle stampe parametriche.
			//Aggiunto il segnaposto per il footer nei template
			doc = new Documento();
			
			
			if (protocollazione) {
			
				trans = new TransactionQueryExecutor(getPool());
				this.enableTransactions(trans);
				trans.initTransaction();
				
				prgDocumento = request.getAttribute("PRGDOCUMENTO") != null?new BigDecimal(request.getAttribute("PRGDOCUMENTO").toString()):null;
				
				doc = new Documento();
				doc.setPrgDocumento(prgDocumento);
				doc.selectStampaParam();
				
				SourceBean rowProt = (SourceBean)trans.executeQuery("GET_PROTOCOLLAZIONE", null, "SELECT");
				if (rowProt == null) {
					throw new Exception("impossibile protocollare il documento di identificazione");
				}
				rowProt = (rowProt.containsAttribute("ROW")?(SourceBean)rowProt.getAttribute("ROW"):rowProt);
			
			
				BigDecimal numAnnoProt = (BigDecimal) rowProt.getAttribute("NUMANNOPROT");
				BigDecimal numProtocollo = (BigDecimal) rowProt.getAttribute("NUMPROTOCOLLO");
				String datProtocollazione = (String ) rowProt.getAttribute("DATAORAPROT");
				String flgprotocolloaut = (String ) rowProt.getAttribute("flgprotocolloaut");
	
				
				doc.setNumAnnoProt(numAnnoProt);
				doc.setNumProtocollo(numProtocollo);
				doc.setDatProtocollazione(datProtocollazione);
				doc.setTipoProt(flgprotocolloaut);
				doc.setStrNomeDoc("StampaParametrica.PDF");
				
				doc.leggiNewNumProt(trans);
				doc.setCodStatoAtto(Properties.STATO_ATTO_PROTOC);
				doc.setCdnUtMod(userid);
				BigDecimal newNumklo = doc.getNumKloDocumento();
				newNumklo = newNumklo.add(new BigDecimal("1"));
				doc.setNumKloDocumento(newNumklo);
				doc.setChiaveTabella(prgDocumento.toString());	
					
					
				htmlDoc=htmlDoc.replaceAll("@AnnoProt", doc.getNumAnnoProt().toPlainString());
				htmlDoc=htmlDoc.replaceAll("@NumProt", doc.getNumProtocollo().toPlainString());
				htmlDoc=htmlDoc.replaceAll("@DataProt", doc.getDatProtocollazione());
					
				//htmlDoc=htmlDoc.replaceAll("<input.+?type=\"checkbox\".+?checked.+?/>", "[x] ");  
				_logger.debug("HTML prima della checked box transformation: \n" + htmlDoc);
				StringBuffer strCheckedBoxes = findCheckedBoxes(new StringBuffer(htmlDoc));
				htmlDoc=strCheckedBoxes.toString();
				_logger.debug("HTML dopo la checked box transformation: \n" + htmlDoc);
				
				htmlDoc=htmlDoc.replaceAll("<input.+?type=\"checkbox\".+?/>", "[ ] "); 
					
//				htmlDoc=htmlDoc.replaceAll("<input.+?type=\"checkbox\".+?/>", "[ ] "); 
					
				
				String dd1 = "<span style=\"font-size:15%;opacity: 0.4;\" >@DD_1</span>";
				htmlDoc=htmlDoc.replaceAll("@DD_1",dd1);
				String dd2 = "<span style=\"font-size:15%;opacity: 0.4;\">@DD_2</span>";
				htmlDoc=htmlDoc.replaceAll("@DD_2",dd2);
				String di1 = "<span style=\"font-size:15%;opacity: 0.4;\">@DI_1</span>";
				htmlDoc=htmlDoc.replaceAll("@DI_1",di1);
				String di2 = "<span style=\"font-size:15%;opacity: 0.4;\">@DI_2</span>";
				htmlDoc=htmlDoc.replaceAll("@DI_2",di2);
				
				_logger.debug("inizio htmlDoc: "+ htmlDoc + "FINE");
				_logger.debug("****END TESTO TO PDF****");
				
				if(htmlDoc.equals(""))
					htmlDoc = "<p><br /></p>";
				_logger.debug("inizio htmlDoc: "+ htmlDoc + "FINE");
				
				_logger.debug("Inserisco il footer");
				
				
				
				it.eng.sil.module.trento.ConvertToPdf convertToPdf = new it.eng.sil.module.trento.ConvertToPdf();
				it.eng.sil.module.umbria.ConvertToPdf convertToPdfUMB = new it.eng.sil.module.umbria.ConvertToPdf();
				
				File tempFile = File.createTempFile("~rpt", ".out");

				_logger.info("firmaGrafometrica "+ request.getAttribute("firmaGrafometrica"));
				doc.setTempFile(tempFile);
				OutputStream osInf = new FileOutputStream(doc.getTempFile().getAbsoluteFile());
				
				OutputStream osOutf = null;
				if (regione.equals(Properties.UMB)) 
					osOutf = convertToPdfUMB.createPDF(htmlDoc, osInf, footerContentUMB);
				 else if (regione.equals(Properties.TN)) 
					osOutf = convertToPdf.createPDF(htmlDoc, osInf, footerContent, footerContentPAT, footerContentModCert);
				
				doc.inserisciBlob(trans);
				
				doc.update(trans);
				
				trans.commitTransaction();
				
				if( request.getAttribute("firmaGrafometrica") != null &&  request.getAttribute("firmaGrafometrica").toString().equalsIgnoreCase("true")){
					
					ArrayList messagesWarning = new ArrayList();
					
					try{
						/*
						Documento actualDocument = doc;
						Documento prgDocumentoDoc = new Documento(actualDocument.getPrgDocumento());
						actualDocument.setNumKloDocumento(prgDocumentoDoc.getNumKloDocumento());
						_logger.debug("[Protocolla Documento] --> firmaGrafometrica --> NumKlo: " + actualDocument.getNumKloDocumento());
						*/
						
						FirmaDocumenti firma = new FirmaDocumenti();
						String ipOperatore = it.eng.afExt.utils.StringUtils.getAttributeStrNotNull(request, "ipOperatore");
						boolean esitoFirma = firma.firmaDocumento(request, response, user, null, doc, ipOperatore);
						_logger.info("***esitoFirma*****"+esitoFirma);
						if(!esitoFirma){
							//response.updAttribute("messageWarning", "Errore durante la firma grafometrica del documento.");
							errorCode = MessageCodes.FirmaGrafometrica.WS_OPERATION_FAIL;
							messagesWarning.add(MessageCodes.FirmaGrafometrica.WS_OPERATION_FAIL);
							throw new Exception();
						}else{
							
							osOutf.flush();
							osOutf.close();
							//da verificare
							sessionContainer.setAttribute("FILE_TO_VIEW", doc.getTempFile().getAbsolutePath());
							TransactionQueryExecutor txBlobFirma = null;
							
							try{
								txBlobFirma = new TransactionQueryExecutor(getPool());
								txBlobFirma.initTransaction();
								doc.inserisciBlob(txBlobFirma);
								txBlobFirma.commitTransaction();
							} catch (Throwable ex) {
								if (txBlobFirma != null) {
									try {
										txBlobFirma.rollBackTransaction();
									} catch (EMFInternalError e) {
										e.printStackTrace();
									}
								}
							}
						}
					}catch(Exception firmaEx){
						if(errorCode!=MessageCodes.FirmaGrafometrica.WS_OPERATION_FAIL){
							//response.updAttribute("messageWarning", "Il Servizio di Firma Grafometrica non è al momento disponibile. Procedere con la firma autografa del documento");
							errorCode = MessageCodes.FirmaGrafometrica.WS_NON_RAGGIUNGIBILE;
							messagesWarning.add(MessageCodes.FirmaGrafometrica.WS_NON_RAGGIUNGIBILE);
						}
						//setOperationSuccessWithWarning(request, response,messagesWarning);
						response.setAttribute("WARNINGREPORT", messagesWarning);
						
						//return;
					}
				}else{
					htmlDoc=htmlDoc.replaceAll("@DD_2","");
					String testoFirmaLav = "<p style=\"font-size:85%\">il/la lavoratore/trice ___________________</p>";
					htmlDoc=htmlDoc.replaceAll("@DD_1",testoFirmaLav);
					
					htmlDoc=htmlDoc.replaceAll("@DI_2","");
					String testoFirmaOp = "<p style=\"font-size:85%\">il/la operatore/trice ___________________</p>";
					htmlDoc=htmlDoc.replaceAll("@DI_1",testoFirmaOp);

					OutputStream osIn = new FileOutputStream(tempFile.getAbsoluteFile());
					OutputStream osOut = convertToPdf.createPDF(htmlDoc, osIn, footerContent, footerContentPAT, footerContentModCert);

					osOut.flush();
					osOut.close();
					sessionContainer.setAttribute("FILE_TO_VIEW", tempFile.getAbsolutePath());
				}	
				
				
//				OutputStream osIn = new FileOutputStream(tempFile);
//				OutputStream osOut = convertToPdf.createPDF(htmlDoc, osIn,footerContent);
//				
//				osOut.flush();
//				osOut.close();
				
//	
//				doc.setNumAnnoProt(numAnnoProt);
//				doc.setNumProtocollo(numProtocollo);
//				doc.setDatProtocollazione(datProtocollazione);
//				doc.setStrNomeDoc("StampaParametrica.PDF");
//				doc.setTipoProt("S");
//				doc.setCodStatoAtto(Properties.STATO_ATTO_PROTOC);
//				doc.setCdnUtMod(userid);
//				BigDecimal newNumklo = doc.getNumKloDocumento();
//				newNumklo = newNumklo.add(new BigDecimal("1"));
//				doc.setNumKloDocumento(newNumklo);
//				
//				doc.setTempFile(tempFile);
				
			}
			else {
				
				_logger.debug("***TESTO TO PDF*****");


				_logger.debug("****ip:"+request.getAttribute("HOST")+"*****");
				_logger.debug("****port:"+request.getAttribute("PORT")+"*****");
				_logger.debug("****"+htmlDoc+"****");
				_logger.debug("****END TESTO TO PDF****");
				if(htmlDoc.equals(""))
					htmlDoc = "<p><br /></p>";
				
				//ConvertToPdf convertToPdf = new ConvertToPdf();
				it.eng.sil.module.trento.ConvertToPdf convertToPdf = new it.eng.sil.module.trento.ConvertToPdf();
				it.eng.sil.module.umbria.ConvertToPdf convertToPdfUMB = new it.eng.sil.module.umbria.ConvertToPdf();
				
				File tempFile = File.createTempFile("~rpt", ".out");
				OutputStream osIn = new FileOutputStream(tempFile.getAbsoluteFile());
				//OutputStream osOut = convertToPdf.createPDF(htmlDoc, osIn, footerContent, footerContentPAT, footerContentModCert);
				
				OutputStream osOut = null;
				if (regione.equals(Properties.UMB)) 
					osOut = convertToPdfUMB.createPDF(htmlDoc, osIn, footerContentUMB);
				 else if (regione.equals(Properties.TN)) 
					 osOut = convertToPdf.createPDF(htmlDoc, osIn, footerContent, footerContentPAT, footerContentModCert);
				
				
				osOut.flush();
				osOut.close();
				
				sessionContainer.setAttribute("FILE_TO_VIEW", tempFile.getAbsolutePath());
			}
			reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
		}
		
		catch (EMFInternalError emf) {
			if (trans != null) {
				try {
					trans.rollBackTransaction();
				} catch (EMFInternalError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			_logger.error(emf.getMessage());
			
			if (emf.getNativeException() instanceof SQLException) {
				if( ((SQLException)emf.getNativeException()).getErrorCode() == MessageCodes.General.CONCORRENZA ){
					reportOperation.reportFailure(MessageCodes.General.CONCORRENZA);
					response.setAttribute("ECCEZIONEPROTOCOLLA", MessageCodes.General.CONCORRENZA);
				} else{
					reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
					response.setAttribute("ECCEZIONEPROTOCOLLA", MessageCodes.General.OPERATION_FAIL);
				}
			} else{
				reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
				response.setAttribute("ECCEZIONEPROTOCOLLA", MessageCodes.General.OPERATION_FAIL);
			}
			
		}
		
		catch (Throwable ex) {
			if (trans != null) {
				try {
					trans.rollBackTransaction();
				} catch (EMFInternalError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			_logger.error(ex.getMessage());
			reportOperation.reportFailure(errorCode);
			response.setAttribute("ECCEZIONEPROTOCOLLA", errorCode);
		}
	}
	
	public StringBuffer findCheckedBoxes(StringBuffer strHtml){
		Pattern pattern = Pattern.compile("<input.+?type=\"checkbox\".+?/>");
        Matcher matcher = pattern.matcher(strHtml);
        
        while (matcher.find()) {
        	
        	int start = matcher.start();
        	int end = matcher.end();
        	
        	if(matcher.group().contains("checked")){
            	strHtml.replace(start, end, "[x]");

            	StringBuffer strHtmlChecked = findCheckedBoxes(strHtml);
            	break;
            }
        	
        }
		
		return strHtml;
	}
	
}