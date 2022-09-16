<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
  it.eng.sil.util.*, 
  it.eng.afExt.utils.StringUtils,
  java.lang.*,
  java.text.*, 
  java.util.*,
  java.math.*,
  it.eng.sil.security.*"%>

<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ include file="../global/Function_CommonRicercaComune.inc" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
 // NOTE: Attributi della pagina (pulsanti e link) 
//  PageAttribs attributi = new PageAttribs(user, "AnagMainPage");
//  boolean canModify = attributi.containsButton("nuovo");

 //boolean canInsert=attributi.containsButton("INSERISCI");
 
  PageAttribs attributi = new PageAttribs(user, "IdoPubbRicercaPage");
 
 String htmlStreamTop    = StyleUtils.roundTopTable(false);
 String htmlStreamBottom = StyleUtils.roundBottomTable(false); 
 
  String codComuni= StringUtils.getAttributeStrNotNull(serviceRequest, "codComuni");
  Object codiceUtente= sessionContainer.getAttribute("_CDUT_");

  String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
 
  String tipoRicerca = 	StringUtils.getAttributeStrNotNull(serviceRequest,"tipoRicerca");
  

	
	boolean canModify = true;
	String strCap="";
    String strCom="";
    String codCom="";
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Soggetti Accreditati per i Voucher </title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>


<script language="Javascript">
     <% 
      //Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
</script>

<SCRIPT TYPE="text/javascript">

function controlloCampi(){
	var procedi = false;
	var elem = document.Frm1.codiceFiscale;
	if (elem.value!=""){
		procedi=true;
		if(elem.value.length<6){
		alert("Digitare almeno 6 caratteri del codice fiscale.");
		return false;
		}
	}
	elem = document.Frm1.denominazione;
	if(elem.value!=""){
		procedi=true; 
		if(elem.value.length<3){
		alert("Digitare almeno 3 caratteri del denominazione.");
		return false;
		}
	}

	elem = document.Frm1.codComuni;
	if(elem.selectedIndex>0){
		procedi=true;
	}

	if(!procedi){
		alert("Valorizzare almeno un filtro di ricerca.");
		return false;
	}
	return procedi;
	
}  


function callRicercaSogAccVoucher()
{
	var    url = "AdapterHTTP?PAGE=VisualizzaSogAccVoucherPage" 
		   url += "&cfSel="+document.Frm1.codiceFiscale.value;
	   	   url += "&denominazioneSel="+document.Frm1.denominazione.value;
	   	   url += "&descComuniSel="+document.Frm1.strCom.value;
	   	   url += "&codComuneSel="+document.Frm1.codCom.value;	
	   	   url += "&tipoRicerca="+document.Frm1.tipoRicerca.value;
		//alert("Il valore di url "+url);
	  setWindowLocation(url);
	
		  
}
</SCRIPT>

 <SCRIPT TYPE="text/javascript">
// Per rilevare la modifica dei dati da parte dell'utente
var flagChanged = false;  


  function fieldChanged() {
    <%if (canModify) {out.print("flagChanged = true;");}%>
  }

</SCRIPT> 

</head>

<body class="gestione" onload="rinfresca();">
<p class="titolo">Soggetti Accreditati per i Voucher </p>
<font color="red">
<af:showErrors />
</font>
<font color="green">
<af:showMessages prefix="" />
</font>


<%out.print(htmlStreamTop);%>
<af:form name="Frm1" method="POST" action="AdapterHTTP"   >
<input type="hidden" name="PAGE" value="Page"/>
<input type="hidden" name="cdnFunzione" value="<%=_funzione%>" />
<input type="hidden" name="CDNUT" value="<%= codiceUtente %>"/>





<p align="center">
<table class="main"> 
	<tr>
	  		<td align="center">
	  			
					<table align="center"  border="0">
						 <tr>
						 	<td  width="15%">&nbsp;</td>
						 	<td  width="35%" class="etichetta" align="right" >Codice Fiscale</td>
							<td  width="25%" align="left" ><input type="text" name="codiceFiscale" value="" size="16" maxlength="16" /></td>
							<td  width="25%">&nbsp;</td>
						</tr>
					</table>
					 <br/>
					<table align="center"  border="0">
						<tr>
							
							<td  width="15%">&nbsp;</td>
							<td  width="35%" class="etichetta" align="right" >Denominazione</td>
							<td  width="25%" align="left">
								 <input type="text" name="denominazione" value="" size="16" maxlength="16" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							</td>
							<td  width="25%">&nbsp;</td>
						</tr>
					</table>
					
          </td>
       </tr>

	<br/>
	<tr>
	  		<td align="center">
	 				<table align="center"   border="0">
						
						 <tr>
						 		<td  width="15%">&nbsp;</td>
							  	<td  width="35%"  class="etichetta" align="right">Tipo ricerca</td>
							   	<td  width="25%"  align="left"><input type="radio" name="tipoRicerca" value="esatta" <%= !tipoRicerca.equals("iniziaPer")?"CHECKED":""%>/> esatta&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" name="tipoRicerca" value="iniziaPer" <%= tipoRicerca.equals("iniziaPer")?"CHECKED":""%> /> inizia per</td>
								<td  width="25%"  align="left">&nbsp;</td>
					    </tr>
					</table>
	   			</td>
       </tr>
	
	<tr>
	  		<td align="center">
	<table align="center" border="0" > 
	<tr>
	  		
							 	
								
									<td class="etichetta">Comune&nbsp;</td>
      							   <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();PulisciRicerca(document.Frm1.codCom, document.Frm1.codComHid, document.Frm1.strCom, document.Frm1.strComHid, document.Frm1.strCap, document.Frm1.strCapHid, 'codice');" 
                                  type="text" name="codCom" value="<%=codCom%>" title="codice comune "
                                  size="4" maxlength="4" validateWithFunction="" readonly="<%= String.valueOf(!canModify) %>"/>&nbsp;
    							<% if(canModify) { %>
   												 <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codCom, 
                                                document.Frm1.strCom, 
                                                document.Frm1.strCap, 
                                                'codice','',null,'');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
    							<% } %>                                                
    							<af:textBox type="hidden" name="codComHid" value="<%=codCom%>"/>
    							<af:textBox classNameBase="input" onKeyUp="fieldChanged();PulisciRicerca(document.Frm1.codCom, document.Frm1.codComHid, document.Frm1.strCom, document.Frm1.strComHid, document.Frm1.strCap, document.Frm1.strCapHid, 'descrizione');"
                							type="text" name="strCom" value="<%=strCom%>" size="50" maxlength="50" title="descrizione comune "
                							inline="onkeypress=\"if (event.keyCode==13) { event.keyCode=9; this.blur(); }\""
                							readonly="<%= String.valueOf(!canModify) %>"/>&nbsp;*&nbsp;&nbsp;
    							<% if(canModify) { %>
    											<A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codCom, 
                                                document.Frm1.strCom, 
                                                document.Frm1.strCap, 
                                                'descrizione','',null,'');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
    							<% } %>
    								<af:textBox type="hidden" name="strComHid" value="<%=strCom%>" />
									<af:textBox type="hidden" name="strCap" value="<%=strCap%>" />
    								</td>						
																
							   
				
    </tr>   
     <br/>
     </table>
     </td>
       </tr>
			<tr>
	  		<td align="center">
			
					<table align="center">
			          <tr><td colspan="2">&nbsp;</td></tr>
			          <tr>
			            <td colspan="2" align="center">
			              <input class="pulsanti" type="button" name="cerca" value="Cerca"   onclick="callRicercaSogAccVoucher()"  />
			             
			            </td>
			          </tr>
			        </table>
			         <br/>
			</table>
	        </td>
       </tr>
</p>
</af:form>
<%out.print(htmlStreamBottom);%>
</body>
</html>