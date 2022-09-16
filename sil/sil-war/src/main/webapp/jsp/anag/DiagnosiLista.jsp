<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<%
	String configuraLabelPsichica_mentale=(String)Utils.getConfigValue("LABEL").getAttribute("ROW.NUM"); 	//La Valle d'Aosta vuole l'etichetta "mentale" al posto di "psichica"
	boolean labelMentale = false;
	if (configuraLabelPsichica_mentale.equals("1")) //Siamo in Valle d'Aosta, enjoy skiing!
		 labelMentale = true;

  String _page = (String) serviceRequest.getAttribute("PAGE");
  ProfileDataFilter filter = new ProfileDataFilter(user, _page);

  String fromRicerca = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"fromRicerca");  

  String cdnFunzione = (String) serviceRequest.getAttribute("cdnFunzione");

  String cdnLavoratore = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");  
  
  String cdnLavoratoreEncrypt = EncryptDecryptUtils.encrypt(cdnLavoratore);
//  String cdnLavoratoreDecrypt = EncryptDecryptUtils.decrypt(cdnLavoratore);
    
  InfCorrentiLav testata = new InfCorrentiLav(RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user, false);
  testata.setSkipLista(true);
  
  String strCodiceFiscale = StringUtils.getAttributeStrNotNull(serviceRequest, "strCodiceFiscale");
  String strCognome = StringUtils.getAttributeStrNotNull(serviceRequest, "strCognome");
  String strNome = StringUtils.getAttributeStrNotNull(serviceRequest, "strNome");
  String datDiagnosiIn = StringUtils.getAttributeStrNotNull(serviceRequest, "datDiagnosiIn");
  String datDiagnosiFin = StringUtils.getAttributeStrNotNull(serviceRequest, "datDiagnosiFin");
  String CODAZIENDAASL = StringUtils.getAttributeStrNotNull(serviceRequest, "CODAZIENDAASL");
  String descrASL = StringUtils.getAttributeStrNotNull(serviceRequest, "descrASL");
  String codCPIComp = StringUtils.getAttributeStrNotNull(serviceRequest, "codCPIComp");
  String descCPI_H = StringUtils.getAttributeStrNotNull(serviceRequest, "descCPI_H");
  String invalidFisica = StringUtils.getAttributeStrNotNull(serviceRequest, "invalidFisica");
  String invalidPsichica = StringUtils.getAttributeStrNotNull(serviceRequest, "invalidPsichica");
  String invalidIntelletiva = StringUtils.getAttributeStrNotNull(serviceRequest, "invalidIntelletiva");
  String invalidSensoriale = StringUtils.getAttributeStrNotNull(serviceRequest, "invalidSensoriale");
  String iscrizL68Aperte = StringUtils.getAttributeStrNotNull(serviceRequest, "iscrizL68Aperte");
   
  if (! filter.canView()){
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
  }else{
 
	  //Profilatura ------------------------------------------------
	  PageAttribs attributi = new PageAttribs(user, _page);
	  
	  boolean canInsert= attributi.containsButton("INSERISCI");
	  boolean canDelete= attributi.containsButton("RIMUOVI");
			
	  //Servono per gestire il layout grafico
	  String htmlStreamTop = StyleUtils.roundTopTable(true);
	  String htmlStreamBottom = StyleUtils.roundBottomTable(true);
%>



<html>
<head>
    <title>DiagnosiLista.jsp</title>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
    <af:linkScript path="../../js/"/>


<script language="JavaScript">

	var urlpage="AdapterHTTP?";	

	function tornaAllaRicerca() {
	  	if (isInSubmit()) return;
	  	urlpage += "PAGE=CMDiagnosiRicercaPage";
 	  	urlpage = urlpage + "&CDNFUNZIONE=<%=cdnFunzione%>";
 	  	urlpage = urlpage + "&strCodiceFiscale=<%=strCodiceFiscale%>";
 	  	urlpage = urlpage + "&strCognome=<%=strCognome%>";
 	  	urlpage = urlpage + "&strNome=<%=strNome%>";
 	  	urlpage = urlpage + "&datDiagnosiIn=<%=datDiagnosiIn%>";
 	  	urlpage = urlpage + "&datDiagnosiFin=<%=datDiagnosiFin%>";
 	  	urlpage = urlpage + "&CODAZIENDAASL=<%=CODAZIENDAASL%>";
 	  	urlpage = urlpage + "&descrASL=<%=descrASL%>";
	  	urlpage = urlpage + "&codCPIComp=<%=codCPIComp%>";
	  	urlpage = urlpage + "&invalidFisica=<%=invalidFisica%>";
	  	urlpage = urlpage + "&invalidPsichica=<%=invalidPsichica%>";
	  	urlpage = urlpage + "&invalidIntelletiva=<%=invalidIntelletiva%>";
	  	urlpage = urlpage + "&invalidSensoriale=<%=invalidSensoriale%>";
	  	urlpage = urlpage + "&iscrizL68Aperte=<%=iscrizL68Aperte%>";
	  
	  	setWindowLocation(urlpage);
	}
	
	function NuovaDiagnosi(){
	<%	SourceBean rowCount = null;
	    rowCount = (SourceBean) serviceResponse.getAttribute("M_CountDataFine.ROWS.ROW");
  		int conta = Integer.valueOf(""+rowCount.getAttribute("conta")).intValue();
  		if(conta > 0){ %>
			alert("Per inserire una nuova Diagnosi occorre chiudere quelle attive!");
			return false;
		<%} else {%>
			var url;
			url = "AdapterHTTP?PAGE=CMDiagnosiMinorPage&inserisciNuovo=1";
 			url = url + "&CDNFUNZIONE=<%=cdnFunzione%>";
 			url = url + "&CDNLAVORATORE=<%=cdnLavoratoreEncrypt%>";
 			setWindowLocation(url);
 			return true;
 	<%}%>
 	}

</script>

<script language="Javascript">
	<%--if(!(!cdnLavoratore.equals("") && cdnLavoratore != null)){ %>
		var url;
		url = "AdapterHTTP?PAGE=MenuCompletoPage";
		url = url + "&cdnFunzione=<%=cdnFunzione%>";		
		if (window.top.menu != undefined){         
			window.top.menu.location = url;
		}
    <%}--%>
	<%--if(!cdnLavoratore.equals("") && cdnLavoratore != null){ %>
		  if (window.top.menu != undefined){
	      	  window.top.menu.caricaMenuLav(<%=cdnFunzione%>, <%=cdnLavoratore%>);
	      }
    <%}--%>    
</script>

</head>


<body class="gestione" onload="rinfresca()">

	<af:showErrors />

<%
	int numero = 0;
	if(!fromRicerca.equals("1")){
%>
		<%testata.show(out);%>
	
	    <script language="Javascript">
	   	   if((window.top != null) && (window.top.menu != null))
		       window.top.menu.caricaMenuLav(<%=cdnFunzione%>,<%=cdnLavoratore%>);
	    </script>
<%
		//int numero = 0;
	    SourceBean row = null;
	    Vector rows= serviceResponse.getAttributeAsVector("M_Controllo_disabilita.ROWS.ROW");
	    if (rows.size()==1) {
		        row = (SourceBean)rows.get(0);
	        	numero = Integer.valueOf(""+row.getAttribute("numero")).intValue();
	    }
	    // modifica 15/05/2007 dona: possono inserire la disagnosi anche se non iscritti
	    //if(numero < 1) canInsert = false;	    
%>

		<af:list moduleName="M_GetDiagnosiFunz" 
	          canDelete="<%= canDelete ? \"1\" : \"0\" %>" 
	          canInsert="<%= canInsert ? \"1\" : \"0\" %>"   />

<%
	}else{
%>
		<%-- FILTRO --%>
		<%String attr   = null;
		  String valore = null;
		  String txtOut = "";%>
		
		<%attr= "strCodiceFiscale";
		  valore = (String) serviceRequest.getAttribute(attr);
		  if(valore != null && !valore.equals(""))
		  {txtOut += "codice fiscale <strong>"+ valore +"</strong>; ";
		  }%>
		<%attr= "strCognome";
		  valore = (String) serviceRequest.getAttribute(attr);
		  if(valore != null && !valore.equals(""))
		  {txtOut += "cognome <strong>"+ valore +"</strong>; ";
		  }%>		       
		<%attr= "strNome";
		  valore = (String) serviceRequest.getAttribute(attr);
		  if(valore != null && !valore.equals(""))
		  {txtOut += "nome <strong>"+ valore +"</strong>; ";
		  }%>
		<%attr= "datDiagnosiIn";
		  valore = (String) serviceRequest.getAttribute(attr);
		  if(valore != null && !valore.equals(""))
		  {txtOut += "data diagnosi maggiore di <strong>"+ valore +"</strong>; ";
		  }%>
		<%attr= "datDiagnosiFin";
		  valore = (String) serviceRequest.getAttribute(attr);
		  if(valore != null && !valore.equals(""))
		  {txtOut += "data diagnosi minore di <strong>"+ valore +"</strong>; ";
		  }%>
		<%attr= "CODAZIENDAASL";
		  valore = (String) serviceRequest.getAttribute(attr);
		  if(valore != null && !valore.equals(""))
		  {txtOut += "codice azienda ASL: <strong>"+ valore +"</strong>; ";
		  }%>
		<%attr= "descrASL";
		  valore = (String) serviceRequest.getAttribute(attr);
		  if(valore != null && !valore.equals(""))
		  {txtOut += "descr azienda ASL: <strong>"+ valore +"</strong>; ";
		  }%>
		<%attr= "descCPI_H";
		  valore = (String) serviceRequest.getAttribute(attr);
		  if(valore != null && !valore.equals(""))
		  {txtOut += "CPI: <strong>"+ valore +"</strong>; ";
		  }%>
		<%boolean invalid = false;
		  attr= "invalidFisica";
		  valore = (String) serviceRequest.getAttribute(attr);
		  if(valore != null && !valore.equals(""))
		  {
			  txtOut += "Tipo invalidità: <strong>Fisica</strong>";
			  invalid = true;
		  }%>
		<%attr= "invalidPsichica";
		  valore = (String) serviceRequest.getAttribute(attr);
		  if(valore != null && !valore.equals(""))
		  {
			  if(invalid){
				  txtOut += ",";
			  }else{
				  txtOut += "Tipo invalidità:";
			  }
			  txtOut += " <strong>"+(labelMentale?"Mentale":"Psichica")+"</strong>";
			  invalid = true;
		  }%>
		<%attr= "invalidIntelletiva";
		  valore = (String) serviceRequest.getAttribute(attr);
		  if(valore != null && !valore.equals(""))
		  {
			  if(invalid){
				  txtOut += ",";
			  }else{
				  txtOut += "Tipo invalidità:";
			  }
			  txtOut += " <strong>Intelletiva</strong>";
			  invalid = true;
		  }%>
		<%attr= "invalidSensoriale";
		  valore = (String) serviceRequest.getAttribute(attr);
		  if(valore != null && !valore.equals(""))
		  {
			  if(invalid){
				  txtOut += ",";
			  }else{
				  txtOut += "Tipo invalidità:";
			  }
			  txtOut += " <strong>Sensoriale</strong>";
			  invalid = true;
		  }
		  if(invalid){
			  txtOut += "; ";
		  }
		  %>
		<%attr= "iscrizL68Aperte";
		  valore = (String) serviceRequest.getAttribute(attr);
		  if(valore != null && !valore.equals(""))
		  {txtOut += "Iscrizione L.68: <strong>Aperte</strong>; ";
		  }%>
		
		<p align="center">
		<%if(txtOut.length() > 0)
		  { txtOut = "Filtri di ricerca:<br/> " + txtOut;%>
		    <table cellpadding="2" cellspacing="10" border="0" width="100%">
		     <tr><td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
		      <%out.print(txtOut);%>
		     </td></tr>
		    </table>
		<%}%>
 
   
		<af:list moduleName="M_GetListaDaRicerca"
		         canDelete="<%= canDelete ? \"1\" : \"0\" %>" 
		         canInsert="<%= canInsert ? \"1\" : \"0\" %>"   
		         />
		
		<table align="center">
			<tr>
			   <td align="center">
		          <input type="button" onclick="tornaAllaRicerca()" value = "Torna alla pagina di ricerca" class="pulsanti">	         
	
			   </td>
			</tr>		          		    
		</table>

<%  }
%>
<%
	// modifica 15/05/2007 dona: possono inserire la disagnosi anche se non iscritti
	//if(numero >= 1){
	   if(!cdnLavoratore.equals("")){
	      if(canInsert){%>	      	 
			 <center><input type="button" class="pulsanti" name="inserisci" value="Nuova diagnosi" onClick="NuovaDiagnosi();"/></center>
			 <br>
<%        }
	   }
    //}

	if(numero < 1){
%>	
<%--
		  <center>
		    <table>		   	  
		   	  <tr>
		   		<td align="center">
		   			<p class="titolo">
						Il lavoratore non è disabile (manca iscrizione L. 68/99)!
					</p> 
		    	</td>
		      </tr>
		    </table>
		  </center>
--%>
<%	
	}
%>
</body>
</html>
<%}%>