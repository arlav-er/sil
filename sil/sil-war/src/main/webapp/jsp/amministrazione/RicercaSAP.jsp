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
 String dataInvioMinDa	= StringUtils.getAttributeStrNotNull(serviceRequest, "dataInvioMinDa");
 String dataInvioMinA = StringUtils.getAttributeStrNotNull(serviceRequest, "dataInvioMinA");
 String dtmModDa = StringUtils.getAttributeStrNotNull(serviceRequest, "dtmModDa");
 String dtmModA = StringUtils.getAttributeStrNotNull(serviceRequest, "dtmModA");
 String codMinSap = StringUtils.getAttributeStrNotNull(serviceRequest, "codMinSap");
 String codStatoSap = StringUtils.getAttributeStrNotNull(serviceRequest, "codStatoSap");
 String ricercaSoloUltimoStato  = StringUtils.getAttributeStrNotNull(serviceRequest,"ricercaSoloUltimoStato");
 String ricercaSoloConNotificheMin = StringUtils.getAttributeStrNotNull(serviceRequest,"ricercaSoloConNotificheMin");
 String ricercaSoloMieiLavoratori = StringUtils.getAttributeStrNotNull(serviceRequest,"ricercaSoloMieiLavoratori"); 
 String ricercaDidAttiva = StringUtils.getAttributeStrNotNull(serviceRequest, "ricercaDidAttiva");
 String ricercaPattoAttivo = StringUtils.getAttributeStrNotNull(serviceRequest, "ricercaPattoAttivo");
 String ricercaAutoSAP = StringUtils.getAttributeStrNotNull(serviceRequest, "ricercaAutoSAP");
 String ricercaErroreSAP = StringUtils.getAttributeStrNotNull(serviceRequest, "ricercaErroreSAP");
 
%>

<%
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
			var cf = document.Frm1.strCodiceFiscale.value;
			var cognome = document.Frm1.strCognome.value;
			var nome = document.Frm1.strNome.value;
			var codMinist = document.Frm1.codMinSap.value;
			var statoSap = document.Frm1.codStatoSap.selectedIndex;      	
			var dataInvioMinDa = document.Frm1.dataInvioMinDa.value;
			var dataInvioMinA = document.Frm1.dataInvioMinA.value;
			var dtmModDa = document.Frm1.dtmModDa.value;
			var dtmModA = document.Frm1.dtmModA.value;
			var ckErroreSAP = document.Frm1.ricercaErroreSAP.checked;
			var ckUlStato = document.Frm1.ricercaSoloUltimoStato.checked;
			var ckNotMin = document.Frm1.ricercaSoloConNotificheMin.checked;
			var ckMieiLav = document.Frm1.ricercaSoloMieiLavoratori.checked;
			var ckDidAtt = document.Frm1.ricercaDidAttiva.checked;
			var ckPattoAtt = document.Frm1.ricercaPattoAttivo.checked;
			var ckAutoSAP = document.Frm1.ricercaAutoSAP.checked;
			

			if(codMinist != ""){
				if (confirm('La ricerca per uno specifico codice ministeriale esclude gli altri criteri. Continuare?')){
					return true;
				} else return false;
			}
				  		
			if(dataInvioMinDa != "" && dataInvioMinA != ""){
				if (compareDate(dataInvioMinDa, dataInvioMinA)>0) {
					alert('Data invio Ministeriale dal maggiore della Data invio Ministeriale al');
					return false;
				}
			}
				
			if(dtmModDa != "" && dtmModA != ""){
				if (compareDate(dtmModDa, dtmModA)>0) {
					alert('Data modifica Stato dal maggiore della Data modifica Stato al');
					return false;
				}
			}

			if ((cf != "" || cognome != "" || nome != "") &&
				(codMinist != "" || statoSap != 0 ||
						dataInvioMinDa != "" || dataInvioMinA != "" || dtmModDa != "" || dtmModA != "" || 
						ckErroreSAP|| ckUlStato || ckNotMin || ckMieiLav || ckDidAtt || ckPattoAtt || ckAutoSAP)) {
				alert('Attenzione: e\' consigliabile non utilizzare nella stessa ricerca ulteriori criteri oltre quelli sui dati anagrafici del lavoratore');
			}
			
			return true;
   		}
		  
		function aggiornaDescrizione(){
			var descStatoSap = document.Frm1.descrCodStatoSaphid;
			var comboStatoSap = document.Frm1.codStatoSap;
			
			if(comboStatoSap.value != ""){
				descStatoSap.value = comboStatoSap.options[comboStatoSap.selectedIndex].text;
			}
			return true;
		}

</script>	    
</head>
<body class="gestione" onload="rinfresca()">
<br>
<p class="titolo">Ricerca SAP</p>
<p align="center">
<af:form action="AdapterHTTP" method="POST"	name="Frm1" onSubmit="controllaCampi() && aggiornaDescrizione()">
	<%out.print(htmlStreamTop);%>
	
	<table class="main">
		<tr><td colspan="2"/>&nbsp;</td></tr>
	   	<tr>
	    	<td class="etichetta">Codice Fiscale</td>
	        <td class="campo">
	        	<af:textBox type="text" name="strCodiceFiscale" value="<%=strCodiceFiscale%>" size="20" maxlength="16" />
	        </td>
	   	</tr>
	   	<tr>
	   		<td class="etichetta">Cognome</td>
	    	<td class="campo">
	    		<af:textBox type="text" name="strCognome" value="<%=strCognome%>" size="20" maxlength="50" />
	    	</td>
	    </tr>
	    <tr>
	    	<td class="etichetta">Nome</td>
	        <td class="campo">
	        	<af:textBox type="text" name="strNome" value="<%=strNome%>" size="20" maxlength="50" />
	       </td>
	   	</tr>
		<tr>
			<td class="etichetta">tipo ricerca</td>
		    <td class="campo">
		    	<table colspacing="0" colpadding="0" border="0">
		      		<tr>
		      			<%if (tipoRicerca.equals("iniziaPer")) {%>
		       				<td>
		       					<input type="radio" name="tipoRicerca" value="esatta"/> esatta&nbsp;&nbsp;&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="tipoRicerca" value="iniziaPer" CHECKED /> inizia per
		       				</td>
		      			<%} else {%>
		       				<td>
		       					<input type="radio" name="tipoRicerca" value="esatta" CHECKED /> esatta&nbsp;&nbsp;&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="tipoRicerca" value="iniziaPer" /> inizia per
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
	         <td class="etichetta">Stato SAP</td>
	         <td class="campo">
	   	  		<af:comboBox classNameBase="input" title="Stato SAP" name="codStatoSap" moduleName="M_ComboStatoSAP" 
	   	  			addBlank="true" selectedValue="<%=codStatoSap%>" />
	      	  		<input name="descrCodStatoSaphid" type="hidden" value=""/>	
	      	 </td>
	    </tr>
		<tr>
			<td class="etichetta">Data invio Ministeriale dal</td>
	     	<td class="campo">
	        	<af:textBox validateOnPost="true" type="date" name="dataInvioMinDa" title="Data invio Ministeriale dal" value="<%=dataInvioMinDa%>" 
	        	size="10" maxlength="10" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;al&nbsp;&nbsp;&nbsp;<af:textBox validateOnPost="true" type="date" name="dataInvioMinA" 
	        	title="Data invio Ministeriale al" value="<%=dataInvioMinA%>" size="10" maxlength="10" />
	     	</td>
		</tr>
		<tr>
		   <td class="etichetta">Data modifica Stato dal</td>
	     	<td class="campo">
	        	<af:textBox validateOnPost="true" type="date" name="dtmModDa" title="Data modifica Stato dal" value="<%=dtmModDa%>" 
	        	size="10" maxlength="10" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;al&nbsp;&nbsp;&nbsp;<af:textBox validateOnPost="true" type="date" name="dtmModA" 
	        	title="Data modifica Stato al" value="<%=dtmModA%>" size="10" maxlength="10" />
	     	</td>
		</tr>	
		<tr>
  			<td class="etichetta">Solo ultimo Stato</td>
  			<td class="campo">
     			<input type="checkbox" name="ricercaSoloUltimoStato" value="si"
     			<%if (!ricercaSoloUltimoStato.equals("no")) { out.print("CHECKED"); }%>>
			</td>
       	</tr>	    
		<tr>			
  			<td class="etichetta">Solo con notifiche Ministeriali</td>
  			<td class="campo">
     			<input type="checkbox" name="ricercaSoloConNotificheMin" value="si"
     			<%if (!ricercaSoloConNotificheMin.equals("no")) { out.print("CHECKED"); }%>>
			</td>
       	</tr>
		<tr>			
  			<td class="etichetta">Solo miei lavoratori</td>
  			<td class="campo">
     			<input type="checkbox" name="ricercaSoloMieiLavoratori" value="si"
     			<%if (!ricercaSoloMieiLavoratori.equals("no")) { out.print("CHECKED"); }%>>
			</td>
       	</tr>  
		<tr>			
  			<td class="etichetta">DID attiva</td>
  			<td class="campo">
     			<input type="checkbox" name="ricercaDidAttiva" value="si"
     			<%if (ricercaDidAttiva.equals("si")) { out.print("CHECKED"); }%>>
			</td>
       	</tr> 
		<tr>			
  			<td class="etichetta">Patto attivo</td>
  			<td class="campo">
     			<input type="checkbox" name="ricercaPattoAttivo" value="si"
     			<%if (ricercaPattoAttivo.equals("si")) { out.print("CHECKED"); }%>>
			</td>
       	</tr> 
       	<tr>			
  			<td class="etichetta">Invio automatico SAP</td>
  			<td class="campo">
     			<input type="checkbox" name="ricercaAutoSAP" value="si"
     			<%if (ricercaAutoSAP.equals("si")) { out.print("CHECKED"); }%>>
			</td>
       	</tr>       	
       	<tr>			
  			<td class="etichetta">SAP in errore</td>
  			<td class="campo">
     			<input type="checkbox" name="ricercaErroreSAP" value="si"
     			<%if (ricercaErroreSAP.equals("si")) { out.print("CHECKED"); }%>>
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
	    <input type="hidden" name="cercaNellaLista" value=""/>
	    <input type="hidden" name="PAGE" value="SAPListaPage"/>
	    <input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
		</table>
	</af:form>
      
	<%out.print(htmlStreamBottom);%>
	
	</body>
</html>

