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

  String strNoteLivelloLim = null;
  String codLivelloLim = null;
  String codSuggerimento = null;
  String strNoteSuggerimento = null;
  

  String numklodiagnosifunzionale = null;
  BigDecimal cdnUtIns = null;
  BigDecimal cdnUtMod = null;
  String dtmIns = null;
  String dtmMod = null;
  
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
           <% if (!readOnlyStr){ %> 
              flagChanged = true;
           <%}%> 
          }

		  var urlpage="AdapterHTTP?";	

		  function getURLPageBase() {    
		    urlpage+="CDNFUNZIONE=<%=_funzione%>&";
		    urlpage+="CDNLAVORATORE=<%=cdnLavoratore%>&";
		    return urlpage;
 		  }

		  /*function indietro() {
			  if (isInSubmit()) return;
			  if(flagChanged)
			  { if(!confirm("I dati sono cambiati.\nProcedere lo stesso?"))
			    { return false;
			    }
			  }
			  urlpage = getURLPageBase();
			  urlpage+="PAGE=CMDiagnosiListaPage";
			  setWindowLocation(urlpage);
		  }*/
          		  
	</script>

</head>


<body class="gestione" onload="rinfresca()">

	<%
	InfCorrentiLav testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user, true);
	testata.setSkipLista(true);
	
	testata.show(out);
		
	//Linguette l = new Linguette(user, _funzione, "CMSchConclusivaPage", new BigDecimal(cdnLavoratore));
	//Linguette_Parametro l = new Linguette_Parametro(user, _funzione, "CMSchConclusivaPage", cdnLavoratore, "1", true);
	LinguetteConfigurazioneRegione l = new LinguetteConfigurazioneRegione (user, _funzione, "CMSchConclusivaPage" , cdnLavoratore , "1", true , "LNDGNFNZ");
		
	l.show(out);
    %>


	<af:showErrors />
	
	<af:error />
	
	<p class="titolo"></p>
	
	<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="">

		<input type="hidden" name="PAGE" value="CMSchConclusivaPage" />		
		<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>" />
		<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
		<input type="hidden" name="inserisciSchConclusiva" value="1"/>
			
<%
      SourceBean row = null;
      Vector rows= serviceResponse.getAttributeAsVector("M_Load_SchConclusiva.ROWS.ROW");
      if (rows.size()==1) {
	        row = (SourceBean)rows.get(0);
	        codLivelloLim = (String) row.getAttribute("codLivelloLim");
	        strNoteLivelloLim = (String) row.getAttribute("strNoteLivelloLim");
	        codSuggerimento = (String) row.getAttribute("codSuggerimento");
	        strNoteSuggerimento = (String) row.getAttribute("strNoteSuggerimento");
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
		    	<td class="etichetta" width="30%">Livello Limitazione&nbsp;</td>
		    	<td class="campo" width="70%">
	                  <af:comboBox name="codLivelloLim"
					        title="Livello Limitazione"
					        selectedValue="<%=codLivelloLim%>"
					        required="false"
					        moduleName="M_Combo_Livello_Lim"
					        disabled="<%= String.valueOf(!canModify) %>"
					        classNameBase="input"
					        onChange="fieldChanged()"
					        addBlank="true"/>		    	
		    	</td>
		    </tr>
			<tr>
				<td class="etichetta" width="30%">&nbsp;</td>
				<td class="campo" width="70%">
		        	<af:textArea cols="80" rows="5" maxlength="500" readonly="<%=fieldReadOnly%>" classNameBase="input"  
                		name="strNoteLivelloLim" 
                		value="<%=strNoteLivelloLim%>" validateOnPost="true" 
                        required="false" title="Note" onKeyUp="fieldChanged();"/>
                </td>
			</tr>
			<tr>
		    	<td colspan="2"/>&nbsp;</td>
		    </tr>
			<tr>
		    	<td class="etichetta" width="30%">Suggerimenti inserimento&nbsp;</td>
		    	<td class="campo" width="70%">
	                  <af:comboBox name="codSuggerimento"
					        title="Livello Limitazione"
					        selectedValue="<%=codSuggerimento%>"
					        required="false"
					        moduleName="M_Combo_Suggerimenti"
					        disabled="<%= String.valueOf(!canModify) %>"
					        classNameBase="input"
					        onChange="fieldChanged()"
					        addBlank="true"/>		    	
		    	</td>
		    </tr>
			<tr>
				<td class="etichetta" width="30%">&nbsp;</td>
				<td class="campo" width="70%">
		        	<af:textArea cols="80" rows="5" maxlength="500" readonly="<%=fieldReadOnly%>" classNameBase="input"  
                		name="strNoteSuggerimento" 
                		value="<%=strNoteSuggerimento%>" validateOnPost="true" 
                        required="false" title="Note" onKeyUp="fieldChanged();"/>
                </td>
			</tr>
			<tr>
		    	<td colspan="2"/>&nbsp;</td>
		    </tr>
<%if (canModify) { %>		    
			<tr>
		    	<td colspan="2"/>&nbsp;</td>
		    </tr>
		    <tr>
		        <td colspan="2" align="center">
			          <input type="submit" class="pulsanti" name="aggiorna" value="Aggiorna" onclick="">
			          &nbsp;&nbsp;
			          <input type="reset" class="pulsanti" value="Annulla" />
			          &nbsp;&nbsp;
			          <!--input type="button" onclick="indietro()" value = "Torna alla lista" class="pulsanti"-->
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