<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/Function_CommonRicercaComune.inc" %>
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

<%
 String _funzione=(String) serviceRequest.getAttribute("cdnFunzione");
 
 String strCodiceFiscale = StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscale");
 String strCognome = StringUtils.getAttributeStrNotNull(serviceRequest,"strCognome");
 String strNome = StringUtils.getAttributeStrNotNull(serviceRequest,"strNome");
 String tipoRicerca = StringUtils.getAttributeStrNotNull(serviceRequest,"tipoRicerca");
 String dataNotificaDa	= StringUtils.getAttributeStrNotNull(serviceRequest, "dataNotificaDa");
 String dataNotificaA = StringUtils.getAttributeStrNotNull(serviceRequest, "dataNotificaA");
 String codMotivoModifica = StringUtils.getAttributeStrNotNull(serviceRequest, "codMotivoModifica");
 String codMinSap = StringUtils.getAttributeStrNotNull(serviceRequest, "codMinSap");
 String ricercaDidAttiva = StringUtils.getAttributeStrNotNull(serviceRequest, "ricercaDidAttiva");
 String ricercaPattoAttivo = StringUtils.getAttributeStrNotNull(serviceRequest, "ricercaPattoAttivo");
 
 String htmlStreamTop = StyleUtils.roundTopTable(false);
 String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<html>
<head>
   	 <link rel="stylesheet" href="../../css/stili.css" type="text/css">
	 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>    	 
   	 <af:linkScript path="../../js/" />
   	 <SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>

     <script language="Javascript">
     
     	function controllaCampi() {
     		var msg;
     		var esito = false;
     		
     		var cfLav = document.Frm1.strCodiceFiscale.value;
    		var nomeLav = document.Frm1.strCognome.value;
    		var cognomeLav = document.Frm1.strNome.value;
    		var codMinSap = document.Frm1.codMinSap.value;
    		var dataNotificaDa = document.Frm1.dataNotificaDa.value;
    		var dataNotificaA = document.Frm1.dataNotificaA.value;

    		if ((cfLav != "") || (nomeLav != "") || (cognomeLav != "")) {
   				esito = true;	
   			}

    		if (codMinSap != "") {
   				esito = true;	
   			}

    		if ((dataNotificaDa != "") && (dataNotificaA != "")) {
   				esito = true;	
   			}
   			
    		if (esito) {
    			return true;
    		}
    		else {
    			msg = "Parametri generici.\n" + 
    			"OPZIONE 1 - Uno dei seguenti valori: Codice Fiscale / Cognome / Nome \n" + 
    			"OPZIONE 2 - Codice Ministeriale SAP \n" + 
    			"OPZIONE 3 - Periodo data notifica da... a... \n";
    			alert (msg);
    			return false;
    		}
   		}

</script>	    
</head>
<body class="gestione" onload="rinfresca()">
<br>
<p class="titolo">Ricerca Notifica SAP</p>
<p align="center">
<af:form action="AdapterHTTP" method="POST"	name="Frm1" onSubmit="controllaCampi()">
	<%out.print(htmlStreamTop);%>
	
	<table class="main">
		<tr><td colspan="2"/>&nbsp;</td></tr>
	   	<tr>
	    	<td class="etichetta">Codice Fiscale</td>
	        <td class="campo">
	        	<af:textBox type="text" classNameBase="input" name="strCodiceFiscale" value="<%=strCodiceFiscale%>" size="20" maxlength="16" />
	        </td>
	   	</tr>
	   	<tr>
	   		<td class="etichetta">Cognome</td>
	    	<td class="campo">
	    		<af:textBox type="text" classNameBase="input" name="strCognome" value="<%=strCognome%>" size="20" maxlength="50" />
	    	</td>
	    </tr>
	    <tr>
	    	<td class="etichetta">Nome</td>
	        <td class="campo">
	        	<af:textBox type="text" classNameBase="input" name="strNome" value="<%=strNome%>" size="20" maxlength="50" />
	       </td>
	   	</tr>
		<tr>
			<td class="etichetta">tipo ricerca</td>
		    <td class="campo">
		    	<table colspacing="0" colpadding="0" border="0">
		      		<tr>
		      			<%if (tipoRicerca.equals("iniziaPer")) {%>
		       				<td>
		       					<input type="radio" classNameBase="input" name="tipoRicerca" value="esatta"/> esatta&nbsp;&nbsp;&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" classNameBase="input" name="tipoRicerca" value="iniziaPer" CHECKED /> inizia per
		       				</td>
		      			<%} else {%>
		       				<td>
		       					<input type="radio" classNameBase="input" name="tipoRicerca" value="esatta" CHECKED /> esatta&nbsp;&nbsp;&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" classNameBase="input" name="tipoRicerca" value="iniziaPer" /> inizia per
		       				</td>
		      			<%}%>
		        	</tr>
		        </table>
		    </td>
		</tr>	        
	  	<tr><td colspan="2"><hr width="90%"/></td></tr>
		<tr>
			<td class="etichetta">Codice Ministeriale SAP</td>
				<td class="campo">
					<af:textBox validateOnPost="true" type="text" name="codMinSap" title="Codice Ministeriale SAP" value="<%=codMinSap%>" 
						classNameBase="input" size="20" maxlength="20" />	
			</td>
		</tr>
		<tr>
			<td class="etichetta">Data notifica dal</td>
	     	<td class="campo">
	        	<af:textBox validateOnPost="true" classNameBase="input" type="date" name="dataNotificaDa" title="Data notifica dal" value="<%=dataNotificaDa%>" 
	        	size="10" maxlength="10" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;al&nbsp;&nbsp;&nbsp;<af:textBox validateOnPost="true" type="date" name="dataNotificaA" 
	        	classNameBase="input" title="Data notifica al" value="<%=dataNotificaA%>" size="10" maxlength="10" />
	     	</td>
		</tr>
		<tr>
	         <td class="etichetta">Motivo notifica</td>
	         <td class="campo">
	   	  		<af:comboBox classNameBase="input" title="Motivo notifica" name="codMotivoModifica" moduleName="M_ComboMotivoNotifica" 
	   	  			addBlank="true" selectedValue="<%=codMotivoModifica%>" />
	      	 </td>
	    </tr>
		<tr>			
  			<td class="etichetta">DID attiva</td>
  			<td class="campo">
     			<input type="checkbox" classNameBase="input" name="ricercaDidAttiva" value="si"
     			<%if (ricercaDidAttiva.equals("si")) { out.print("CHECKED"); }%>>
			</td>
       	</tr> 
		<tr>			
  			<td class="etichetta">Patto attivo</td>
  			<td class="campo">
     			<input type="checkbox" classNameBase="input" name="ricercaPattoAttivo" value="si"
     			<%if (ricercaPattoAttivo.equals("si")) { out.print("CHECKED"); }%>>
			</td>
       	</tr>         	     		    
		<tr><td colspan="2">&nbsp;</td></tr>
	    <tr>
	    	<td colspan="2" align="center">
		    	<input class="pulsanti" type="submit" name="cerca" value="Cerca" onClick=""/>
		          &nbsp;&nbsp;
		          <input type="reset" class="pulsanti" value="Annulla" />				
	        </td>
	    </tr>
	    <input type="hidden" name="PAGE" value="NotificheSAPListaPage"/>
	    <input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
		</table>
	</af:form>
      
	<%out.print(htmlStreamBottom);%>
	
	</body>
</html>

