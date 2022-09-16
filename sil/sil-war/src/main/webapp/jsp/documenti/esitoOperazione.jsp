<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  com.engiweb.framework.message.*,
                  it.eng.sil.util.*,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  it.eng.afExt.utils.StringUtils,
                  it.eng.afExt.utils.MessageCodes,
                  it.eng.sil.bean.Documento,
                  com.engiweb.framework.util.JavaScript,
                  com.engiweb.framework.error.EMFUserError" %>

<%!  
	private String getQueryString(SourceBean _serviceRequest) {
		StringBuffer queryStringBuffer = new StringBuffer();
		Vector queryParameters = _serviceRequest.getContainedAttributes();
		for (int i = 0; i < queryParameters.size(); i++) {
			SourceBeanAttribute parameter = (SourceBeanAttribute) queryParameters.get(i);
			String parameterKey = parameter.getKey();
			if ( parameterKey.equalsIgnoreCase("confirmOperation")) {
				String parameterValue = parameter.getValue().toString();
				if (parameterValue.equalsIgnoreCase("false")) {
					queryStringBuffer.append(JavaScript.escape(parameterKey.toUpperCase()));
					queryStringBuffer.append("=");
					queryStringBuffer.append("true");
					queryStringBuffer.append("&");	
				}
			}
			else {
				String parameterValue = parameter.getValue().toString();
				queryStringBuffer.append(JavaScript.escape(parameterKey.toUpperCase()));
				queryStringBuffer.append("=");
				queryStringBuffer.append(JavaScript.escape(parameterValue));
				queryStringBuffer.append("&");	
			}
		} // for (int i = 0; i < queryParameters.size(); i++)
		return queryStringBuffer.toString();
	}

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger("it.eng.sil._jsp.esitoOperazione.jsp");
%>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<% String opRes = null;
   if (serviceResponse.containsAttribute("operationResult") == true) 
   { opRes = (String) serviceResponse.getAttribute("operationResult"); }
   else opRes ="";
   if(opRes==null) opRes = "";
   boolean confirmOp = false;
   String msgConferma = "";
   String confirmIns = StringUtils.getAttributeStrNotNull(serviceRequest,"confirmOperation");
   String pageReload = StringUtils.getAttributeStrNotNull(serviceRequest,"pageReload");
   if(opRes.equals("WARNING")) {
	   if (confirmIns.equalsIgnoreCase("false")) {
		   confirmOp = true;	   
	   }
	   if (serviceResponse.containsAttribute("messageResult")) { 
		   msgConferma = (String) serviceResponse.getAttribute("messageResult");
   		} 	   
   }
   
   
   String asAttach = "";
   boolean showButton = true;
   asAttach = (String) serviceResponse.getAttribute("asAttachment");
   if(asAttach!=null && asAttach.equalsIgnoreCase("false")) showButton = false;
   
  String numProtocollo = "";StringUtils.getAttributeStrNotNull(serviceRequest,"numProt");
  String numAnnoProt   = "";StringUtils.getAttributeStrNotNull(serviceRequest,"annoProt");
  String dataOraProt   = "";StringUtils.getAttributeStrNotNull(serviceRequest,"dataOraProt");
  
  
  //Servono per il layout
  String htmlStreamTop = StyleUtils.roundTopTable(showButton);
  String htmlStreamBottom = StyleUtils.roundBottomTable(showButton);
  
  Documento theDocument = (Documento) serviceResponse.getAttribute("theDocument");
  
  if (theDocument!=null) {
  // se si e' verificato un errore bisognera' stampare il numero di protocollo.
  // se l'errore e' avvenuto prima della lettura del numero di protocollo dal db o dal web service allora il valore inviato dal client 
  // (solo nel caso di protocollazione locale) si trovera' nella variabile numProtocollo e non in numProtInserito di Documento.java.
  	numProtocollo = (theDocument.getNumProtInserito()==null)    ? "" : theDocument.getNumProtInserito().toString();
  	if ("".equals(numProtocollo))
	  	numProtocollo = (theDocument.getNumProtocollo()==null || theDocument.getNumProtocollo().intValue()<=0) ? "" : theDocument.getNumProtocollo().toString();
  	numAnnoProt   = (theDocument.getNumAnnoProt()==null)        ? "" : theDocument.getNumAnnoProt().toString();
  	dataOraProt   = (theDocument.getDatProtocollazione()==null) ? "" : theDocument.getDatProtocollazione();
  }
  
  if (opRes.equals("SUCCESS")) {
  	// DOCAREA: SE E' PRESENTE UN FILE TEMPORANEO INVIATO A DOCAREA VIENE CANCELLALTO
  	// VIENE ANCHE STAMPATO A VIDEO UN MESSAGGIO
		 if (theDocument.getTempFilePreProtocollo()!=null) {
			if (!theDocument.getTempFilePreProtocollo().delete()) {
				response.setContentType("text/html");
				out.write("<html><body><p>Impossibile cancellare il file temporaneo inviato a DOCAREA. Numero prot. " + numProtocollo + "<br>Contattare l'amministratore di sistema.</p></body></html>");
				_logger.fatal( "Impossibile cancellare il file PROTOCOLLATO inviato a DocArea: "+theDocument.getTempFilePreProtocollo().getAbsolutePath());
				_logger.fatal( "DATI REQUEST: "+ serviceRequest.toXML());
				_logger.fatal( "DATI DOCUMENTO: "+theDocument.toString());
				_logger.fatal( "CONTATTARE L'AMMINISTRATORE DI SISTEMA");
				return;
			}
		}
    }
  
%>

<html>
<head>
<title>Stato documento</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">

<af:linkScript path="../../js/"/>
<script>
<%if (opRes.equals("SUCCESS")) {
        // E' possibile che il percorso sia : patto ->(popUp) dich. disp.  -> stampa -> aggiornamento pagina  dich. Disp -> aggiornamento pagina patto
        // Per aggiornare la pagina della dich. disp ho bisogno della query string e del nome del frame della popUp
        // Per aggiornare la pagina del patto dalla dich. disp. ho bisogno del parametro "fromPattoDettaglio" in    questo modo si attiva un pulsante preposto a cio'
        
            String urlPage = (String)serviceRequest.getAttribute("QUERY_STRING");
            String target = (String)serviceRequest.getAttribute("FRAME_NAME");
            //target = target==null?"main":target;
   
            if (target!=null && !target.equals("main")) {
                urlPage +="&fromPattoDettaglio=1";            
%>
				window.open("AdapterHTTP?<%=urlPage%>", "<%=target%>");
				window.close();
<%          } else {
%>
			    window.opener.location="AdapterHTTP?<%=urlPage%>";
			    window.close();			
<%          }
}%>
</script>

<script language="JavaScript">
function conferma() {
	<%
	if (confirmOp) {
	%>
		if (confirm("<%=msgConferma%>")) {
			var url = "AdapterHTTP?PAGE=<%=pageReload%>&<%=getQueryString(serviceRequest)%>";
			setWindowLocation(url);
		}	
	<%}%>
}
</script>

</head>

<body class="gestione" onload="conferma();">
<af:showErrors/>
<br/><br/>
<%out.print(htmlStreamTop);%>
<table align="center" width="100%" border="0">
  <tr><td><br/><td></tr>
  <tr><td><br/></td></tr>
  <tr><td><br/></td></tr>
  <%if(opRes.equalsIgnoreCase("SUCCESS")) {%>
  <tr><td colspan="2" align="center"><b>Operazione eseguita correttamente</b></td></tr>
  <tr><td><br/></td></tr>
  <!-- 
  	Modificato il 23 Marzo 2006
  	riccardi
  -->
  <%} else if(opRes.equalsIgnoreCase("ERROR")){
  	  Vector errorsCode = serviceResponse.getAttributeAsVector("errorCode"); 
  %>
  <tr><td colspan="2" align="center"><b>Operazione fallita!</b></td></tr>
  <tr><td><br/></td></tr>
  <%	for (int i = 0;i<errorsCode.size();i++) {
	      //String errorCode = StringUtils.getAttributeStrNotNull(serviceResponse,"errorCode");
	      String errorCode = Utils.notNull(errorsCode.get(i));
	      String errorMes = "";
	      if (errorCode != "") {
	          switch (Integer.parseInt(errorCode)) {
	            case MessageCodes.Protocollazione.ERR_GENERICO_NELLA_SP:
	                 errorMes = "Errore generico nella Stored Procedure di protocollazione";
	                 break;
	            case MessageCodes.Protocollazione.NUM_PROT_TROPPO_GRANDE: 
	                 errorMes = "Esiste già un documento protocollato con data successiva a quella inserita<br/>avente un numero di protocollo inferiore a quello in serito <br/>(numero di protocollo troppo grande)";
	                 break;
	            case MessageCodes.Protocollazione.NUM_PROT_TROPPO_PICCOLO:
	                 errorMes = "Esiste già un documento protocollato con data precedente a quella inserita<br/>avente un numero di protocollo superiore a quello in serito <br/>(numero di protocollo troppo piccolo)";
	                 break;
	            case MessageCodes.Protocollazione.DATA_PROT_NULLA:
	                 errorMes = "Non è stata inserita la data di protocollazione";
	                 break;
	            case MessageCodes.Protocollazione.DATA_PROT_ERRATA:
	                 errorMes = "La data o l'ora di protocollazione sono errati";
	                 break;
	                 
	            case MessageCodes.ConfermaAnnualeDid.DICH_FUTURA:
	            	 errorMes = "Non e' possibile rilasciare una dichiarazione con data futura";
	                 break;
	                 
	            case MessageCodes.ConfermaAnnualeDid.DICH_ESISTENTE_ANNO:
	            	 errorMes = "E' stata gia' rilasciata una dichiarazione nel corso dello stesso anno";
	                 break;
	                 
	            case MessageCodes.ConfermaAnnualeDid.DICH_ANNO_DID:
	            	 errorMes = "Non e' possibile rilasciare una dichiarazione nello stesso anno(o anno precedente) della did";
	                 break;
	                 
	            default: errorMes = MessageBundle.getMessage(errorCode.toString());
	                 break;
	          }  
	          if ("0".equals(errorMes)) errorMes=""; 	  
	    } %>
	<tr><td colspan="2" align="center"><b><%=errorMes%></b></td></tr>	
  
  <%	} // ciclo for 
  // faccio un ciclo anche sugli errori nel response container
  		String errorMsg="";
	    java.util.Iterator errors = responseContainer.getErrorHandler().getErrors().iterator(); 
	    while (errors.hasNext()) {
	    	EMFUserError error = (EMFUserError)errors.next();
	    	errorMsg = error.getDescription();
	    	
	%>
	<tr><td colspan="2" align="center"><b><%=errorMsg%></b></td></tr>	
	<%  }
  
  		if (numProtocollo != "" || numAnnoProt !="" || dataOraProt !="") {
  %>
  <tr><td><br/></td></tr>
  <tr><td align="right" width="50%">Dati inseriti:</td><td align="left">&nbsp;</td></tr>
  <tr><td><br/></td></tr>
  <tr><td align="right" width="50%">Numero</td><td align="left"><b><%=numProtocollo%></b></td></tr>
  <tr><td align="right">Anno</td><td align="left"><b><%=numAnnoProt%></b></td></tr>
  <tr><td align="right">Data e ora</td><td align="left"><b><%=dataOraProt%></b></td></tr>
  <%    }
  	} 
  	else if(opRes.equals("WARNING")){
  		%>
  		<tr><td colspan="2" align="center"><b><%=msgConferma%></b></td></tr>		
  	<%}
  	else if(opRes.equals("")){%>
  	<tr><td align="center"><b>Non &egrave stata selezionata nessuna operazione!</b></td></tr>
  	<%}%>
  <tr><td><br/></td></tr>
  <tr><td><br/></td></tr>
  <!--tr><td colspan="2" align="center">
      <%if(showButton){%><input class="pulsante" type="button" value="Chiudi" onClick="window.close()"><%}%>
      </td>
  </tr-->
  <tr><td><br/></td></tr>
</table>
<%out.print(htmlStreamBottom);%>

</body>
</html>
