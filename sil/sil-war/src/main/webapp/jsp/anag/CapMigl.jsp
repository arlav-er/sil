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
 
  int _funzione=0;
  InfCorrentiLav infCorrentiLav= null;
  	
  Testata operatoreInfo = null;
  String prgDiagnosiFunzionale = null;
  String cdnLavoratore = null;

  String strCapacitaMigliori = null;
  


  String numklodiagnosifunzionale = null;
  BigDecimal cdnUtIns = null;
  BigDecimal cdnUtMod = null;
  String dtmIns = null;
  String dtmMod = null;
  
  boolean readCodiciScaduti = false;
  
  prgDiagnosiFunzionale = "" + RequestContainer.getRequestContainer().getSessionContainer().getAttribute("prgDiagnosiFunzionale");

  cdnLavoratore = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");
  
  //Profilatura ------------------------------------------------
  String _page = (String) serviceRequest.getAttribute("PAGE");   
  PageAttribs attributi = new PageAttribs(user, _page);
  
  boolean canModify= attributi.containsButton("aggiorna");
  boolean canDelete= attributi.containsButton("RIMUOVI");
  boolean canInsert= attributi.containsButton("INSERISCI");
   
  boolean readOnlyStr = !canModify;
  String fieldReadOnly = canModify?"false":"true"; 
  
  /*------------------------ inserire il parametro cdnFunzione ---------------------------*/
  _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
 
  //Servono per gestire il layout grafico
  String htmlStreamTop = StyleUtils.roundTopTable(true);
  String htmlStreamBottom = StyleUtils.roundBottomTable(true);
  
  String codice = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"codice");
  String descrizione = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"descrizione");  
  
  BigDecimal scad = (BigDecimal) serviceResponse.getAttribute ("M_VerificaCodValidi.ROWS.ROW.scadCodici");
  
  if (scad != null && scad.intValue() > 0) {
	  readCodiciScaduti = true;
  } 

%>

<html>
<head>
    <title>SocioLav.jsp</title>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
    <af:linkScript path="../../js/"/>

	<script language="JavaScript">
          
          var flagChanged = false;
        
          function fieldChanged() {
           <% if (!readCodiciScaduti){ %> 
              flagChanged = true;
           <%}%> 
          }

		  var urlpage="AdapterHTTP?";	

		  function getURLPageBase() {    
		    urlpage+="CDNFUNZIONE=<%=_funzione%>&";
		    urlpage+="CDNLAVORATORE=<%=cdnLavoratore%>&";
		    return urlpage;
 		  }

		  function indietro_lista() {
			  if (isInSubmit()) return;
			  if(flagChanged)
			  { if(!confirm("I dati sono cambiati.\nProcedere lo stesso?"))
			    { return false;
			    }
			  }
			  urlpage = getURLPageBase();
			  urlpage+="PAGE=CMDiagnosiListaPage";
			  setWindowLocation(urlpage);
		  }
          		  
	</script>

</head>


<body class="gestione" onload="rinfresca()">

	<%
	InfCorrentiLav testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user, true);
	testata.setSkipLista(true);
	
	testata.show(out);
		
	//Linguette l = new Linguette(user, _funzione, "CMCapMiglPage", new BigDecimal(cdnLavoratore));
	//Linguette_Parametro l = new Linguette_Parametro(user, _funzione, "CMCapMiglPage", cdnLavoratore, "1", true);
	LinguetteConfigurazioneRegione l = new LinguetteConfigurazioneRegione (user, _funzione, "CMCapMiglPage" , cdnLavoratore , "1", true , "LNDGNFNZ");
	
	l.show(out);
    %>


	<af:showErrors/>
	<af:showMessages prefix="M_Aggiorna_CapMigl"/>
	
	<p class="titolo"></p>

	<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="">

		<input type="hidden" name="PAGE" value="CMCapMiglPage" />		
		<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>" />
		<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
		<input type="hidden" name="inserisciCapacitaMigliori" value="1"/>
<%
      SourceBean row = null;
      Vector rows= serviceResponse.getAttributeAsVector("M_Load_CapMigl.ROWS.ROW");
      if (rows.size()==1) {
	        row = (SourceBean)rows.get(0);
	        strCapacitaMigliori = (String) row.getAttribute("strCapacitaMigliori");
	        numklodiagnosifunzionale = String.valueOf(((BigDecimal)row.getAttribute("NUMKLODIAGNOSIFUNZIONALE")).intValue());
	        cdnUtIns = (BigDecimal) row.getAttribute("CDNUTINS");
	        dtmIns = (String) row.getAttribute("DTMINS");
	        cdnUtMod = (BigDecimal) row.getAttribute("CDNUTMOD");
	        dtmMod = (String) row.getAttribute("DTMMOD");
	        operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);        
	  }
%>
		<input type="hidden" name="CDNUTMOD" value="<%=cdnUtMod%>">
		<input type="hidden" name="NUMKLODIAGNOSIFUNZIONALE" value="<%= numklodiagnosifunzionale %>">


		<%= htmlStreamTop %>
		<table class="main" border="0">
			<tr>
		    	<td colspan="2"/>&nbsp;</td>
		    </tr>
			<tr>
		    	<td width="75%">&nbsp;</td>
		    	<td width="25%">&nbsp;</td>
		    </tr>
			<tr>
				<td colspan="2" align="center">
		        	<af:textArea cols="80" rows="5" maxlength="2000" readonly="<%= String.valueOf(readCodiciScaduti) %>" classNameBase="input"  
                		name="strCapacitaMigliori" 
                		value="<%=strCapacitaMigliori%>" validateOnPost="true" 
                        required="false" title="Note" onKeyUp="fieldChanged();"/>
                </td>
			</tr>
			<tr>
		    	<td colspan="2"/>&nbsp;</td>
		    </tr>
	<%if (canModify && !readCodiciScaduti) { %>
			<tr>
		    	<td colspan="2"/>&nbsp;</td>
		    </tr>
		    <tr>
		        <td colspan="2" align="center">
			          <input type="submit" class="pulsanti" name="aggiorna" value="Aggiorna" onclick="">
			          &nbsp;&nbsp;
			          <input type="reset" class="pulsanti" value="Annulla" />
			          &nbsp;&nbsp;
			          <!--input type="button" onclick="indietro_lista()" value = "Torna alla lista" class="pulsanti"-->
		        </td>
		    </tr>
	<%}%>		    				
		</table>
		<br><br>
		
		<%out.print(htmlStreamBottom);%>

	    <center>
	    	<table>
	      		<tr>
	      			<td align="center">
	      				<% operatoreInfo.showHTML(out); %>
	      			</td>
	      		</tr>
	      	</table>
	    </center>
		
	</af:form>

</body>
</html>



