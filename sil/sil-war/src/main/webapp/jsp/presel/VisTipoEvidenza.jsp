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
                  oracle.xdb.XMLType,
			      javax.xml.transform.*,
				  javax.xml.transform.stream.*
	" %>
<%!  
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger("it.eng.sil._jsp.VisTipoEvidenza.jsp");
%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>
<%

	String cdnfunzione =  (String) serviceRequest.getAttribute("cdnfunzione");
	String codTipoEvidenza =  (String) serviceResponse.getAttribute("MDETTTIPOEVIDENZA.ROW.codTipoEvidenza");
	String descTipoEvidenza= (String) serviceResponse.getAttribute("MDETTTIPOEVIDENZA.ROW.STRDESCRIZIONE");
	String prgTipoEvidenza = (String) serviceRequest.getAttribute("prgTipoEvidenza");
 
	 // Attributi della pagina (pulsanti e link) 
    String _page = (String) serviceRequest.getAttribute("PAGE"); 
	PageAttribs attributi = new PageAttribs(user, _page);
	boolean readOnlyStr= false;
  	
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	boolean canSave   =  attributi.containsButton("AGGIORNA");
	readOnlyStr = !canSave;    
  

    String htmlStreamTop = StyleUtils.roundTopTable(canSave);   
    String htmlStreamBottom = StyleUtils.roundBottomTable(canSave);
	
	
    Linguette  l = new Linguette(user, Integer.parseInt(cdnfunzione) , _page, new BigDecimal (prgTipoEvidenza));
	l.setCodiceItem("prgTipoEvidenza");
	
%>



<html>
	<head>
	  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/"/>

 	  <script language="JavaScript">
		     

  function selectCK() {
      var s= "AdapterHTTP?PAGE=VisTipoEvidenzaPage";      
      var chkboxObjEval = document.getElementsByName("PG");
      var chkboxObj=eval(chkboxObjEval);
      var strProf_Gruppi="";

      for(i=0; i<chkboxObj.length; i++) {
  		if(chkboxObj[i].checked) {
  			if(strProf_Gruppi.length>0) { strProf_Gruppi += ","; }
  			strProf_Gruppi += chkboxObj[i].value;
  		}
  	  }
      s += "&PRGTIPOEVIDENZA=<%=prgTipoEvidenza%>";
      s += "&profGruppi="+strProf_Gruppi;
      s += "&CDNFUNZIONE=<%=cdnfunzione%>";
	 setWindowLocation(s);
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

<BR/>
 
       <table align="center" cellspacing="0" margin="0" cellpadding="0" border="0px" width="94%" noshade><tr><td align="left" valign="top" width="6" height="6" class="prof_mod"><img src="../../img/angoli/bia1.gif" width="6" height="6"></td><td class="prof_mod" align="center" valign="middle" cellpadding="2px"></td><td class="prof_mod" align="right" valign="top" width="6" height="6"><img src="../../img/angoli/bia2.gif" width="6" height="6"></td></tr><tr><td class="prof_mod" width="6"></td><td class="prof_mod" width="100%" align="center">
      <table class="main">

                      <tr>
			            <td> 
			            Tipo Evidenza: <b><%=codTipoEvidenza.toUpperCase()%></b>			            
			            </td>
			          </tr>		
			          <tr>
			            <td> 
							<%=descTipoEvidenza%>
			            </td>
			          </tr>		
	   </table>
  </td><td class="prof_mod" width="6"></td></tr><tr><td class="prof_mod" align="left" valign="bottom" width="6" height="6"><img src="../../img/angoli/bia4.gif"></td><td class="prof_mod" height="6" align="center" valign="middle"&nbsp;></td><td class="prof_mod" align="right" valign="bottom" width="6" height="6"><img src="../../img/angoli/bia3.gif"></td></tr></table><br>&nbsp;

 
 <% l.show(out); %>

    <center>
      <font color="green">
        <af:showMessages prefix="M_SalvaVisEvidenza" />
      </font>
      <font color="red">
        <af:showErrors/>
      </font>
    </center>


	<p class="titolo">Modifica visibilit&agrave;</p>
	
	
	

<af:form action="AdapterHTTP" method="POST" dontValidate="true" name="Frm1">

   <af:textBox type="hidden" name="cdnfunzione" value="<%=cdnfunzione%>" />
   <af:textBox type="hidden" name="PAGE" value="ProfilaturaTipoEvidenzaSalvaPage" />
   
    <%out.print(htmlStreamTop);%>
      <table class="main">
      
        <tr>
          <td>
   
   
<%
	StringBuffer xml = (StringBuffer) serviceResponse.getAttribute("M_GETVISEVIDENZAXML.BUFXML");
	
	StringWriter result = new StringWriter();
	
	//Formattazione risultati attraverso un transformer
			
	
	//Creo il nome del file di configurazione per il transformer
	String filename = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "xsl" + 
	File.separator + "presel" + File.separator + "VisTipoEvidenzaXML.xsl";
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
			<td>&nbsp;</td>
		</tr>
	
		<tr>
			<td align="center">
				<input class="pulsante" type="button" name="SALVA" VALUE="Aggiorna" onClick="selectCK();"/>
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

