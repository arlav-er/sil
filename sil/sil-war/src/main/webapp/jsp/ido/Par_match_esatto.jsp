<!-- @author: Stefania Orioli -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                com.engiweb.framework.security.*,
                it.eng.afExt.utils.*, java.math.*,
                it.eng.sil.module.movimenti.constant.Properties,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.*,
                it.eng.sil.util.*"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%

boolean CRESCO = false;
String numConfigCresco = serviceResponse.containsAttribute("M_CONFIG_CRESCO.ROWS.ROW.NUM")?
	serviceResponse.getAttribute("M_CONFIG_CRESCO.ROWS.ROW.NUM").toString():Properties.DEFAULT_CONFIG;
if(Properties.CUSTOM_CONFIG.equalsIgnoreCase(numConfigCresco)){
	CRESCO = true;
}  

String prgRichiestaAz = serviceRequest.getAttribute("PRGRICHIESTAAZ").toString();
String prgOrig = serviceRequest.getAttribute("PRGORIG").toString();
String prgC1 = serviceRequest.getAttribute("C1").toString();
String prgAzienda = serviceRequest.getAttribute("PRGAZIENDA").toString();
String prgUnita = serviceRequest.getAttribute("PRGUNITA").toString();

String _page = serviceRequest.getAttribute("PAGE").toString();
PageAttribs attributi = new PageAttribs(user, _page);

String p_codCpi = user.getCodRif();



int nroMansioni = 0;

int nroAlternative = 0;
SourceBean content = (SourceBean) serviceResponse.getAttribute("MALTERNATIVEINCROCIO");
Vector altIncr = content.getAttributeAsVector("ROWS.ROW");
nroAlternative = altIncr.size();

int cdnUtente = user.getCodut();
String _cdnFunzione = serviceRequest.getAttribute("CDNFUNZIONE").toString();

//SourceBean contStato = (SourceBean) serviceResponse.getAttribute("MATCHSTATORICHORIG");
SourceBean sbStato = (SourceBean) serviceResponse.getAttribute("MATCHSTATORICHORIG.ROWS.ROW");
String cdnStatoRich = StringUtils.getAttributeStrNotNull(sbStato, "CDNSTATORICH");
String codEvasione = StringUtils.getAttributeStrNotNull(sbStato, "CODEVASIONE");
String codMonoCMCategoria = StringUtils.getAttributeStrNotNull(sbStato, "codMonoCMCategoria");
String flagPubblicata = StringUtils.getAttributeStrNotNull(sbStato, "Flgpubblicata");
String flagCresco = StringUtils.getAttributeStrNotNull(sbStato, "Flgpubbcresco");
String dataInizioRich = StringUtils.getAttributeStrNotNull(sbStato, "dataInizio");
String dataFineRich = StringUtils.getAttributeStrNotNull(sbStato, "dataFine");
// Attributi della pagina GestIncrocioPage
PageAttribs attrIncrocio = new PageAttribs(user, "GestIncrocioPage");
boolean gestCopia = attrIncrocio.containsButton("GEST_COPIA");

boolean viewPar = false;
String prgTipoIncrocio = "";


%>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <title>Matching Esatto - Impostazione parametri</title>
  <af:linkScript path="../../js/" />
  <script language="Javascript" src="../../js/utili.js" type="text/javascript"></script>
  <script language="Javascript">
    window.top.menu.caricaMenuAzienda(<%=_cdnFunzione%>,<%=prgAzienda%>, <%=prgUnita%>);
    
    function match_sub(n)
    {
      // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;
	  
	  /*
	  if (document.form_match.codMonoCMCategoria.value == "E" && n==1) {
	  	alert("Attenzione: bisogna segliere una categoria CM per poter effettuare il matching.")	  	
	  	n = 3;
	  }
	  */
	     
      var pag = "";
      var ok = false;
      var checkFunz = true;
      switch(n) {
        case 1 :
          pag = "MatchStoricizzaRichPage";
          checkFunz = controllaFunzTL();
          ok = true;
          break;
        case 2 :
          pag = "GestIncrocioPage";
          ok = true;
          break;
        default :
          pag = "";
          ok = false;
          break;
        }
      if(checkFunz && riportaControlloUtente( ok ) ) {      
        document.form_match.PAGE.value = pag; 
        doFormSubmit(document.form_match);  
      } 
    }
    
    /*
	function selDeselCM() {  	
		if (document.form_match.elements['flgIncMir'].checked) { 
			document.form_match.elements['dataCV'].value = "";			
			document.form_match.elements['dataCV'].disabled = true;		  
		}
		else { 
			document.form_match.elements['dataCV'].disabled = false;					 
		} 	
  	}
  	*/
  	function selCM() {  
  		<%
		if (("MIR").equalsIgnoreCase(codEvasione) || ("MPP").equalsIgnoreCase(codEvasione) || ("MPA").equalsIgnoreCase(codEvasione)) {
		%>	
			if (document.form_match.elements['flgIncMirBck'].checked) { 
				document.form_match.elements['dataCV'].value = "";			
				document.form_match.elements['dataCV'].disabled = true;		  
			}
			else { 
				document.form_match.elements['dataCV'].disabled = false;					 
			} 	
		<%
		}
		%>
  	}
    
  </script>
</head>

<body class="gestione" onload="rinfresca();selCM();">
<%@ include file="InfoCorrRichiesta.inc" %>
<h2>Matching Esatto - Impostazione dei Parametri</h2>
<af:form name="form_match" action="AdapterHTTP" method="POST" >
<input name="PAGE" type="hidden" value="MatchStoricizzaRichPage"/>
<input name="PRGRICHIESTAAZ" type="hidden" value="<%=prgRichiestaAz%>"/>
<input name="C1" type="hidden" value="<%=prgC1%>"/>
<input name="PRGORIG" type="hidden" value="<%=prgOrig%>"/>
<input name="PRGAZIENDA" type="hidden" value="<%=prgAzienda%>"/>
<input name="PRGUNITA" type="hidden" value="<%=prgUnita%>"/>
<input name="CDNFUNZIONE" type="hidden" value="<%=_cdnFunzione%>"/>

<input name="CERCA" type="hidden" value="cerca"/>


<input type="hidden" name="P_CODCPI" value="<%=p_codCpi%>"/>
<input type="hidden" name="P_CDNUTENTE" value="<%=cdnUtente%>"/>
<input type="hidden" name="EM" value="1"/>
<input type="hidden" name="db" value="3"/>
<%
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<%out.print(htmlStreamTop);%>
<table class="main">
<%@ include file="match_par_comuni.inc" %>
<!--tr><td colspan="2">&nbsp;</td></tr-->
<tr>
  <td colspan="2"><div class="sezione2">Parametri Specifici</div></td>
</tr>
			<%if(CRESCO && "S".equals(flagCresco) && "S".equals(flagPubblicata)){ %>
				<tr>
				  <td colspan="2" align="left">
				          La verifica dei requisiti (Pacchetto Adulti, Garanzia Giovani, Disoccupati/Inoccupati) sar&agrave; effettuata nell'intervallo temporale che va dalla data richiesta alla data scadenza pubblicazione: <b><%=dataInizioRich%> - <%=dataFineRich%></b>.
				  </td>
				</tr>
			<%} %>	
<tr>
	<td colspan="2" align="left">
		<table width="400">
			<tr>
			  <td class="etichetta">usa Preferibili</td>
			  <td class="campo"><input type="CHECKBOX" name="usaPref" value="1"></td>
			  <%
			  // INIT-PARTE-TEMP
			  if (Sottosistema.CM.isOff()) {	
			  // END-PARTE-TEMP
			  %>
				<td colspan="2"></td>
			  <%	
			  // INIT-PARTE-TEMP
			  } else {
			  // END-PARTE-TEMP
			  %>			  
			  	<td align="left" class="etichetta" nowrap="nowrap">
			  	<%
				if (("MIR").equalsIgnoreCase(codEvasione) || ("MPP").equalsIgnoreCase(codEvasione) || ("MPA").equalsIgnoreCase(codEvasione)) {
				%>
			  		Incrocio Mirato
			  	<%
				}		
				%>
			  	</td>
			  	<td class="campo">
			  	<%
				if (("MIR").equalsIgnoreCase(codEvasione) || ("MPP").equalsIgnoreCase(codEvasione) || ("MPA").equalsIgnoreCase(codEvasione)) {
				%>
					<input type="CHECKBOX" name="flgIncMirBck" value="1" checked="checked" disabled="disabled">
					<input name="flgIncMir" type="hidden" value="1"/>					
				<%
				}
				%>				
			  	</td>					
			  <%
			  // INIT-PARTE-TEMP
			  }
			  // END-PARTE-TEMP
			  %>			  
			</tr>
			<tr>
			  <td class="etichetta">usa Non Indispensabili</td>
			  <td class="campo"><input type="CHECKBOX" name="usaNonInd" value="1"></td>
			  <%
			  // INIT-PARTE-TEMP
			  if (Sottosistema.CM.isOff()) {	
			  // END-PARTE-TEMP
			  %>
				<td colspan="2"></td>
			  <%	
			  // INIT-PARTE-TEMP
			  } else {
			  // END-PARTE-TEMP
			  %>			  
			  	<td align="left" class="etichetta" nowrap="nowrap">
			  	<%
				if (("MIR").equalsIgnoreCase(codEvasione) || ("MPP").equalsIgnoreCase(codEvasione) || ("MPA").equalsIgnoreCase(codEvasione)) {
				%>
			  		Categoria CM
			  	<%
				}		
				%>
			  	</td>
			  	<td class="campo">
			  	<%
				if (("MIR").equalsIgnoreCase(codEvasione) || ("MPP").equalsIgnoreCase(codEvasione) || ("MPA").equalsIgnoreCase(codEvasione)) {
				%>
					<af:comboBox 
			          name="codMonoCMCategoria"
			          classNameBase="input">
			          <option value="E" >Entrambi</option>
			          <option value="D" <% if ( "D".equalsIgnoreCase(codMonoCMCategoria) ) { %>SELECTED<% } %> >Disabile</option>
			          <option value="A" <% if ( "A".equalsIgnoreCase(codMonoCMCategoria) ) { %>SELECTED<% } %> >Categoria protetta ex. Art. 18</option>
			        </af:comboBox>					        			        
			        <script language="javascript">
			        <%
			        if (("D").equalsIgnoreCase(codMonoCMCategoria) || ("A").equalsIgnoreCase(codMonoCMCategoria)) {
					%>
						 document.form_match.codMonoCMCategoria.disabled = true;						 
					<%
					}
					%>
			        </script>		
			        <%
			        if (("D").equalsIgnoreCase(codMonoCMCategoria) || ("A").equalsIgnoreCase(codMonoCMCategoria)) {
					%>
						 <input name="codMonoCMCategoria" type="hidden" value="<%=codMonoCMCategoria%>"/>						 
					<%
					}
					%>
				<%
				}
				%>				
			  	</td>					
			  <%
			  // INIT-PARTE-TEMP
			  }
			  // END-PARTE-TEMP
			  %> 
			</tr> 
			<tr>
			  	<td class="etichetta">Solo Disoccupati/Inoccupati</td>
			  	<td class="campo"><input type="CHECKBOX" name="flagDI" value="1"></td>
			  	<td colspan="2"></td>	 
			</tr>
			<tr>
			  <td class="etichetta" nowrap="nowrap">Incrocio slegato dalla mansione</td>
			  <td class="campo"><input type="CHECKBOX" name="flagNoMansione" value="1"></td>
			  <td colspan="2"></td>	  
			</tr>
			<tr>
			  <td class="etichetta" nowrap="nowrap">Garanzia Giovani</td>
			  <td class="campo"><input type="CHECKBOX" name="flagGG" value="1"></td>
			  <td colspan="2"></td>	  
			</tr>	
		</table>
	</td>
</tr>

<tr><td colspan="2">&nbsp;</td></tr>
<tr>
  <td colspan="2" align="center">
  <%if(!cdnStatoRich.equals("4") && !cdnStatoRich.equals("5")) {%>
  <input class="pulsanti" type="button" name="sub" value="Esegui Matching" onClick="match_sub(1)" />
  &nbsp;&nbsp;
  <%}%>
  <input class="pulsanti" type="button" name="back" value="Chiudi" onClick="match_sub(2)" />
  </td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>
</table>
<%out.print(htmlStreamBottom);%>

</af:form>
  
</body>
</html>
