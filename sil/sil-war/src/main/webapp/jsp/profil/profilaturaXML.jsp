<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
			      javax.xml.transform.*,
				  javax.xml.transform.stream.*
	" %>
<%!  
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger("it.eng.sil._jsp.profilaturaXML.jsp");
%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>
<%

	String cdnfunzione =  (String) serviceRequest.getAttribute("cdnfunzione");
	String cdnprofilo =  (String) serviceRequest.getAttribute("cdnprofilo");
	
    int cdnFunzione = new Integer ( cdnfunzione).intValue();
    String _page = (String) serviceRequest.getAttribute("PAGE"); 
 
	 // Attributi della pagina (pulsanti e link) 
     PageAttribs attributi = new PageAttribs(user, _page);
     boolean canSave=attributi.containsButton("salva");


    String htmlStreamTop = StyleUtils.roundTopTable(canSave);   
    String htmlStreamBottom = StyleUtils.roundBottomTable(canSave);
	

     Linguette  l = new Linguette(user, cdnFunzione , _page, new BigDecimal(cdnprofilo));
	   
     l.setCodiceItem("cdnprofilo");

	
%>



<html>
	<head>
	  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/"/>

 	  <script language="JavaScript">
		      function onOffTF(myID ){
		      		
		      		  var img = document.getElementById("IMG_" + myID);
		      		  var tab1=document.getElementById("TF_" + myID);
		              if (img.alt=="Apri"){
		              		//alert("Apri");
		                  tab1.style.display="";
		                  img.alt="Chiudi"
		                  img.src="../../img/aperto.gif"
		              }    
		              else {    
		              	//	alert("Chiudi");
		                   tab1.style.display="none";
		
		                  img.alt="Apri"
		                                    img.src="../../img/chiuso.gif"
		              }
		      }

	</script>
	
	 <script language="JavaScript">
        // Rilevazione Modifiche da parte dell'utente
        var flagChanged = false;
        
        function fieldChanged() {
            flagChanged = false;
        }
    </script>
	
</head>	
<body class="gestione" onLoad="rinfresca()">

 <%@ include file="./testataProfilo.inc" %>
 <% l.show(out); %>


  <br/>
	<p class="titolo">Modifica componenti ed attributi del profilo</p>

<af:form action="AdapterHTTP" method="POST" dontValidate="true">

   <af:textBox type="hidden" name="cdnfunzione" value="<%=cdnfunzione%>" />
   <af:textBox type="hidden" name="cdnprofilo" value="<%=cdnprofilo%>" />
   <af:textBox type="hidden" name="PAGE" value="ProfProfilXMLSalvaPage" />
   
    <%out.print(htmlStreamTop);%>
      <table class="main">
      
        <tr>
          <td>
   
   
<%
	StringBuffer xml = (StringBuffer) serviceResponse.getAttribute("PROFPROFILATURAXML.BUFXML");
	
	StringWriter result = new StringWriter();
	
	//Formattazione risultati attraverso un transformer
			
	
	//Creo il nome del file di configurazione per il transformer
	String filename = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "xsl" + 
	File.separator + "profil" + File.separator + "profilaturaXML.xsl";
	File f = new File(filename);
	
	try{
	//Creo il transformer
	TransformerFactory factory = TransformerFactory.newInstance();
	Transformer transformer = factory.newTransformer(new StreamSource(f));
	//Trasformo il risultato
	transformer.transform(new StreamSource(new StringReader(xml.toString())), new StreamResult(result));
	}catch(Exception ex){
		_logger.fatal( 
			"trasformazione XML-HTML per filtri i profili", ex);
	
	}
	
	out.print(result.toString());
	
%>
 <%if (canSave){%>
	<table width="90%">
		<tr>
			<td align="center">
				<input class="pulsante" type="submit" name="SALVA" VALUE="Aggiorna" />
  				&nbsp;
        		<input class="pulsante" type="reset" VALUE="Annulla" />
	  		</td>
		</tr>
	</table>
	  	 <%}%>	
	  	 
	  	  </td>
        </tr>
      </table>
      <%out.print(htmlStreamBottom);%>
	  	 	
</af:form>
</body>
</html>

