<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
 
<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.StringUtils,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

 


<% String titolo = "";
  Linguette l=null;
    // Gestione delle linguette
    int cdnFunzione = new Integer ( (String) serviceRequest.getAttribute("cdnfunzione")).intValue();
    String _page = (String) serviceRequest.getAttribute("PAGE"); 
    String cdnStr =   (String) serviceRequest.getAttribute("cdnprofilo"); 
    
    BigDecimal cdnprofilo = null;
    BigDecimal cdnprofilopadre = null;
	String profPadre = "";
   
     // Attributi della pagina (pulsanti e link) 
     PageAttribs attributi = new PageAttribs(user, _page);
     boolean canSave   = attributi.containsButton("salva");
     boolean canInsert = true;//attributi.containsButton("inserisci");


 
    String readonlyStr ="false" ;

    boolean nuovoProf = serviceRequest.containsAttribute("NUOVO");
    boolean inserito  = serviceRequest.containsAttribute("INSERISCI");
    
    String htmlStreamTop = StyleUtils.roundTopTable(canSave);   
    String htmlStreamBottom = StyleUtils.roundBottomTable(canSave);
  
    
    SourceBean row;
    String strdenominazione = "";
    String flgstandard = "";
    String strnota = "";
    

    if (!nuovoProf)
    {   titolo = "Modifica profilo";
    
     
        if (inserito)
        {
          row = (SourceBean) serviceResponse.getAttribute("ProfDettProfNuovo.rows.row");
          cdnprofilo = (BigDecimal) row.getAttribute("cdnprofilo");
          cdnprofilopadre = (BigDecimal) row.getAttribute("cdnprofilopadre");
          profPadre        =     (String) row.getAttribute("STRDENOMINAZIONEPADRE");
          strdenominazione= (String) row.getAttribute("strdenominazione");             
          flgstandard = "N";
          strnota= (String) row.getAttribute("strnota");
        }
        else
        {
          row = (SourceBean) serviceResponse.getAttribute("ProfDettaglioProfilo.rows.row");
          if ( row != null )
          {
            if ((cdnStr!=null) && cdnStr.equals("") ){
               cdnprofilo = (BigDecimal) row.getAttribute("cdnprofilo");   
          	   cdnprofilopadre = (BigDecimal) row.getAttribute("cdnprofilopadre");
               profPadre  =           (String) row.getAttribute("strdenominazionepadre");
                        
            }else{
              cdnprofilo = new  BigDecimal(cdnStr);
              profPadre  =     (String) row.getAttribute("strdenominazionepadre");
              cdnprofilopadre = (BigDecimal) row.getAttribute("cdnprofilopadre");
            }
          } 
          else
          { //Si è clonato un profilo
            row = (SourceBean) serviceResponse.getAttribute("ProfDettProfClonato.rows.row");
            cdnprofilo = (BigDecimal) row.getAttribute("cdnprofilo");             
            profPadre  =     (String) row.getAttribute("strdenominazionepadre");
            cdnprofilopadre = (BigDecimal) row.getAttribute("cdnprofilopadre");            	  
            
          }
      
          strdenominazione= (String) row.getAttribute("strdenominazione");             
          flgstandard= (String) row.getAttribute("flgstandard");          
          strnota= (String) row.getAttribute("strnota");
        }
        
         // Gestione delle linguette
	    
	   
		 
        l = new Linguette(user, cdnFunzione , _page, cdnprofilo);
	   
	    // Utilizzato solo da Franco  
	    l.setCodiceItem("cdnprofilo");
        
    }
    else
    {   titolo="Nuovo profilo";
        flgstandard = "N";
    }

    // Inizio della pagina vera e propria
%>
 
 

<html>
	<head>
	  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/"/>



   <script language="JavaScript">
        // Rilevazione Modifiche da parte dell'utente
        var flagChanged = false;
        
        function fieldChanged() {
         <% if (readonlyStr.equalsIgnoreCase("false")){ %> 
            flagChanged = true;
         <%}%> 
        }
    </script>



	</head>
	<body class="gestione" onLoad="rinfresca()">
	

	 
	
	 <% 
            if (!nuovoProf) {%>
            	 <%@ include file="testataProfilo.inc" %>
            	 
            	 <%
            	  l.show(out);        
        }
        %>
	
	 
	<br/>
	<p class="titolo"><%=titolo%></p>
	



      <%@ include file="dettaglioProfilo.inc" %>



	</body>
</html>

