<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page
	import="com.engiweb.framework.base.*,com.engiweb.framework.dispatching.module.AbstractModule,com.engiweb.framework.util.QueryExecutor,it.eng.sil.security.*,it.eng.afExt.utils.*,it.eng.sil.util.*,java.util.*,java.math.*,java.io.*,com.engiweb.framework.security.*"%>


<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>


<%
	int _funzione = 0;
	InfCorrentiLav infCorrentiLav = null;

	Testata operatoreInfo = null;
	String prgDiagnosiFunzionale = null;
	String cdnLavoratore = null;

	String strIpotesiInsLav = null;
	String strNotaComitatoTec = null;
	String strProfiloSocioLav = null;
	//Nuovo campo - flgdisplim
	String flgdisplim = null;	
	BigDecimal cdnUtIns = null;
	BigDecimal cdnUtMod = null;
	String dtmIns = null;
	String dtmMod = null;

	prgDiagnosiFunzionale = ""
			+ RequestContainer.getRequestContainer()
					.getSessionContainer().getAttribute(
							"prgDiagnosiFunzionale");

	cdnLavoratore = SourceBeanUtils.getAttrStrNotNull(serviceRequest,
			"cdnLavoratore");

	boolean esisteComTec = false;

	//Profilatura ------------------------------------------------
	String _page = (String) serviceRequest.getAttribute("PAGE");
	PageAttribs attributi = new PageAttribs(user, _page);

	boolean canModify = attributi.containsButton("AGGIORNA");

	boolean readOnlyStr = !canModify;
	String fieldReadOnly = canModify ? "false" : "true";

	/*------------------------ inserire il parametro cdnFunzione ---------------------------*/
	_funzione = Integer.parseInt((String) serviceRequest
			.getAttribute("CDNFUNZIONE"));

	//Servono per gestire il layout grafico
	String htmlStreamTop = StyleUtils.roundTopTable(true);
	String htmlStreamBottom = StyleUtils.roundBottomTable(true);

	//conf CMPART

	String conf_CMPART = serviceResponse
			.containsAttribute("M_GetConfig_CMPART.ROWS.ROW.NUM") ? serviceResponse
			.getAttribute("M_GetConfig_CMPART.ROWS.ROW.NUM").toString()
			: "0";
%>

<html>
<head>
<title>SocioLav.jsp</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/" />

<script language="JavaScript">
          
          var flagChanged = false;
        
          function fieldChanged() {
           <%if (!readOnlyStr) {%> 
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
	InfCorrentiLav testata = new InfCorrentiLav(RequestContainer
			.getRequestContainer().getSessionContainer(),
			cdnLavoratore, user, true);
	testata.setSkipLista(true);

	testata.show(out);

	//Linguette l = new Linguette(user, _funzione, "CMSchConclusivaPage", new BigDecimal(cdnLavoratore));
	//Linguette_Parametro l = new Linguette_Parametro(user, _funzione,"CMNoteComitatoTecnicoPage", cdnLavoratore, "1", true);
	LinguetteConfigurazioneRegione l = new LinguetteConfigurazioneRegione (user, _funzione, "CMNoteComitatoTecnicoPage" , cdnLavoratore , "1", true , "LNDGNFNZ");

	l.show(out);
%>


<font color="red"><af:showErrors /></font>
<af:showMessages prefix="M_Insert_ComitatoTec" />
<af:showMessages prefix="M_Save_ComitatoTec" />

<af:showErrors />
<af:error />

<p class="titolo"></p>

<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="">

	<input type="hidden" name="PAGE" value="CMNoteComitatoTecnicoPage" />
	<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>" />
	<input type="hidden" name="cdnFunzione" value="<%=_funzione%>" />

	<%
		SourceBean row = null;
			Vector rows = serviceResponse
					.getAttributeAsVector("M_Load_ComitatoTec.ROWS.ROW");
			if (rows.size() == 1) {
				row = (SourceBean) rows.get(0);
				strIpotesiInsLav = (String) row
						.getAttribute("strIpotesiInsLav");
				strNotaComitatoTec = (String) row
						.getAttribute("strNotaComitatoTec");
				strProfiloSocioLav = (String) row
						.getAttribute("strProfiloSocioLav");
				//nuovo
				flgdisplim = (String) row
						.getAttribute("flgdisplim");
				cdnUtIns = (BigDecimal) row.getAttribute("CDNUTINS");
				dtmIns = (String) row.getAttribute("DTMINS");
				cdnUtMod = (BigDecimal) row.getAttribute("CDNUTMOD");
				dtmMod = (String) row.getAttribute("DTMMOD");
				operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod,
						dtmMod);
				esisteComTec = true;
			} else if (rows.size() == 0) {
				esisteComTec = false;
			}
	%>


	
	<input type="hidden" name="message" value="<%=esisteComTec%>">
	
	<input type="hidden" name="CDNUTMOD" value="<%=cdnUtMod%>">


	<%=htmlStreamTop%>
	<table class="main" border="0">
		<tr>
			<td colspan="2" />&nbsp;</td>
		</tr>
		<%
			if ("1".equals(conf_CMPART)) {
		%>


		<tr>
			<td class="etichetta">Disponibilità/Limitazione a part-time ai
			fini della copertura
			</td>
			
			<td class="campo">			
				<af:comboBox name="flgdisplim" classNameBase="input"  disabled="<%=fieldReadOnly%>">	  	
		    		<option value=""  <% if ( "".equalsIgnoreCase(flgdisplim) )  { %>SELECTED="true"<% } %> ></option>            
            		<option value="S" <% if ( "S".equalsIgnoreCase(flgdisplim) )  { %>SELECTED="true"<% } %>>Si</option>
            		<option value="N" <% if ( "N".equalsIgnoreCase(flgdisplim) )  { %>SELECTED="true"<% } %>>No</option>               
            	</af:comboBox> 
			</td>
		</tr>


		<%
			}
		%>
		<tr>
			<td class="etichetta">Annotazioni del comitato tecnico</td>
			<td class="campo"><af:textArea cols="60" rows="4"
				title="Annotazioni del comitato tecnico"
				value="<%=strNotaComitatoTec%>" classNameBase="input"
				name="strNotaComitatoTec" maxlength="2000" onKeyUp="fieldChanged();"
				readonly="<%=fieldReadOnly%>" /></td>
		</tr>
		<tr>
			<td class="etichetta">Profilo socio-lavorativo</td>
			<td class="campo"><af:textArea cols="60" rows="4"
				title="Profilo socio-lavorativo" value="<%=strProfiloSocioLav%>"
				classNameBase="input" name="strProfiloSocioLav" maxlength="2000"
				onKeyUp="fieldChanged();" readonly="<%=fieldReadOnly%>" /></td>
		</tr>
		<tr>
			<td class="etichetta">Eventuale ipotesi di inserimento
			lavorativo</td>
			<td class="campo"><af:textArea cols="60" rows="4"
				title="Eventuale ipotesi di inserimento lavorativo"
				value="<%=strIpotesiInsLav%>" classNameBase="input"
				name="strIpotesiInsLav" maxlength="2000" onKeyUp="fieldChanged();"
				readonly="<%=fieldReadOnly%>" /></td>
		</tr>

		<%
			if (canModify) {
		%>
		<tr>
			<td colspan="2" />&nbsp;</td>
		</tr>
		<tr>
			<td colspan="2" align="center">
			<%
				if (esisteComTec) {
			%> <input type="submit" class="pulsanti" name="Aggiorna"
				value="Aggiorna"> <%
 	} else {
 %> <input type="submit" class="pulsanti" name="Inserisci"
				value="Inserisci"> <%
 	}
 %> &nbsp;&nbsp; <input type="reset" class="pulsanti" value="Annulla" />
			</td>
		</tr>
		<%
			}
		%>

	</table>
	<br>
	<br>

	<%
		out.print(htmlStreamBottom);
	%>

	<%
		if (esisteComTec) {
	%>
	<center>
	<table>
		<tr>
			<td align="center">
			<%
				operatoreInfo.showHTML(out);
			%>
			</td>
		</tr>
	</table>
	</center>
	<%
		}
	%>

</af:form>

</body>
</html>