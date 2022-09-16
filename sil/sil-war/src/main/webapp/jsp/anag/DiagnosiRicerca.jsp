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
 // NOTE: Attributi della pagina (pulsanti e link) 
 //PageAttribs attributi = new PageAttribs(user, "AnagMainPage");
 //boolean canModify = attributi.containsButton("nuovo");

 
 String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
 
 String strCodiceFiscale = StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscale");
 String strCognome       = StringUtils.getAttributeStrNotNull(serviceRequest,"strCognome");
 String strNome          = StringUtils.getAttributeStrNotNull(serviceRequest,"strNome");
 String datDiagnosiIn    = StringUtils.getAttributeStrNotNull(serviceRequest,"datDiagnosiIn");
 String datDiagnosiFin   = StringUtils.getAttributeStrNotNull(serviceRequest,"datDiagnosiFin");
 String CODAZIENDAASL    = StringUtils.getAttributeStrNotNull(serviceRequest,"CODAZIENDAASL");
 String descrASL         = StringUtils.getAttributeStrNotNull(serviceRequest,"descrASL"); 
 String tipoRicerca      = StringUtils.getAttributeStrNotNull(serviceRequest,"tipoRicerca");
 String cercaASL         = StringUtils.getAttributeStrNotNull(serviceRequest,"cercaASL");
 String codCPIComp       = StringUtils.getAttributeStrNotNull(serviceRequest,"codCPIComp");
 String invalidFisica    = StringUtils.getAttributeStrNotNull(serviceRequest, "invalidFisica");
 String invalidPsichica  = StringUtils.getAttributeStrNotNull(serviceRequest, "invalidPsichica");
 String invalidIntelletiva = StringUtils.getAttributeStrNotNull(serviceRequest, "invalidIntelletiva");
 String invalidSensoriale= StringUtils.getAttributeStrNotNull(serviceRequest, "invalidSensoriale");
 String iscrizL68Aperte  = StringUtils.getAttributeStrNotNull(serviceRequest, "iscrizL68Aperte");

 String configuraLabelPsichica_mentale=(String)Utils.getConfigValue("LABEL").getAttribute("ROW.NUM"); 	//La Valle d'Aosta vuole l'etichetta "mentale" al posto di "psichica"
 boolean labelMentale = false;
 if (configuraLabelPsichica_mentale.equals("1")) //Siamo in Valle d'Aosta, enjoy skiing!
	 labelMentale = true;
 
 /*
  NOTA: le pagine di ricerca devono avere lo stile prof_ro -> quindi invece di
  canModify si deve passare il valore false
*/
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<html>
<head>
	 <title>Ricerca sull'anagrafica lavoratori</title>
   	 <link rel="stylesheet" href="../../css/stili.css" type="text/css">
	 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>    	 
   	 <af:linkScript path="../../js/" />
   	 <SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>

     <script language="Javascript">
	      <% 
	     	//Genera il Javascript che si occuperà di inserire i links nel footer
	        //attributi.showHyperLinks(out, requestContainer,responseContainer,"");
	      %>

	      function checkCampiObbligatori()
	      { if( ((document.Frm1.strCodiceFiscale.value != null) && (document.Frm1.strCodiceFiscale.value.length >= 6)) 
	            || (document.Frm1.strCognome.value.length >= 2) ) return true;
	        alert("Inserire almeno sei caratteri del codice fiscale\no almeno due caratteri del cognome");
	        return false;
	      }

		  function cercaASLCompetente(criterio){
			  var f;
 		      f = "AdapterHTTP?PAGE=CMDiagnosiRicercaPage&cercaASL=1";
 		      f = f + "&CDNFUNZIONE=<%=_funzione%>";
 		      //f = f + "&prgDiagnosiFunzionale=<%--=prgDiagnosiFunzionale--%>";
 		      //f = f + "&cdnLavoratore=<%--=cdnLavoratore--%>";
			  f = f + "&CRITERIO=" + criterio;
			  f = f + "&cod=" + document.Frm1.CODAZIENDAASL.value;
			  f = f + "&descr=" + document.Frm1.descrASL.value;
			  
			  var t = "_blank";
			  var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=500,height=400,top=100,left=100";
			  if(criterio=="codice" && document.Frm1.CODAZIENDAASL.value!="")
			  	window.open(f, t, feat);
			  if(criterio=="descrizione" && document.Frm1.descrASL.value!="")
			  	window.open(f, t, feat);
		  }	 
		  
		  function AggiornaForm (codice, descrizione) {
			  window.opener.document.Frm1.CODAZIENDAASL.value = codice;
			  window.opener.document.Frm1.descrASL.value = descrizione;
			  window.close();
		  }
		  
		  function campiObligatori(){
		    alert("pippo = "+document.Frm1.codCPIComp.value);
		  	if(document.Frm1.codCPIComp.value == ""){
		  		alert("il campo centro per l'impiego competente è obligatorio");
		  		return false;
		  	}
		  	else return true;
		  }
		  
		  function cercaDiagnosi(){
			var msg;
			if (isInSubmit()) return;
			checkFormatDate(document.Frm1.datDiagnosiIn);
			checkFormatDate(document.Frm1.datDiagnosiFin);  
			//var datiOk = controllaFunzTL();
			if(document.Frm1.codCPIComp.value!=""){
				document.Frm1.descCPI_H.value = document.Frm1.codCPIComp.options[document.Frm1.codCPIComp.selectedIndex].text;
   			}
		
			var cosAsl = document.Frm1.CODAZIENDAASL.value;
			//var cdnLav = document.Frm1.CDNLAVORATORE.value;
			var codCPIComp = document.Frm1.codCPIComp.value;
			
			var datDiagnosiDa = document.Frm1.datDiagnosiIn.value;
			var datDiagnosiA = document.Frm1.datDiagnosiFin.value;
			
			//var datMovA = document.Frm1.datmovimentoa.value;
			
			var dataDiOggi = new Date();
			var giornoOggi=dataDiOggi.getDate().toString();
			var meseOggi=(dataDiOggi.getMonth() +1).toString();
			
			if(giornoOggi.length == 1){
				giornoOggi = '0' + giornoOggi;
			}
			if(meseOggi.length == 1){
				meseOggi = '0' + meseOggi;	 	
			}
			dataDiOggi = giornoOggi + '/' + meseOggi + '/' + dataDiOggi.getFullYear().toString();
			var periodoDataMov = confrontaDate(datDiagnosiDa, datDiagnosiA) + 1;
			var esito = false; 
			
			if((datDiagnosiDa != "") && (datDiagnosiA != "")){
				if(periodoDataMov <= 365){
					esito = true;
				}
			}
			if((datDiagnosiDa != "") && (datDiagnosiA == "")){
				var periodoOggi = confrontaDate(datDiagnosiDa, dataDiOggi) + 1;
				if(periodoOggi <= 365){
				 	esito = true;
				}
			}
		
			if((cosAsl != "") || (codCPIComp != "")){
				 esito = true;	
			}		
			
				
			if (esito) { 
				doFormSubmit(document.Frm1);				
			}else{
				msg = "Parametri generici.\n" + 
				"OPZIONE 1 - Uno dei seguenti valori: Azienda A.S.L. / Centro per l'Impiego.\n" + 
				"OPZIONE 2 - Periodo data diagnosi dal... al... con periodo max 365 gg.\n" + 
				"OPZIONE 3 - Periodo data diagnosi dal... alla data odierna con periodo max 365 gg.";			
			
				alert (msg);
				undoSubmit();
			}			
		  }
		  
	         
     </script>	    
</head>
<body class="gestione" onload="rinfresca()">

  <%if(!cercaASL.equals("")){%>
		<br><br>
		<af:list moduleName="CercaASLCompetente"  skipNavigationButton="1" jsSelect="AggiornaForm"/>
		
	    <table align="center">
	    	<tr>
			    <td>
			      <input type="button" class="pulsanti" value="Chiudi" onClick="window.close()">
			    </td>	
    		</tr>    
        </table>
  <%
  }else{
  %>
	
  <p class="titolo">Ricerca Diagnosi Funzionale</p>
    <%out.print(htmlStreamTop);%>
	  <af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="">
	      <table class="main" border="0">
	        <tr><td colspan="2"/><br/></td></tr>
	        <tr><td colspan="2"/>&nbsp;</td></tr>
	        <tr>
	          <td class="etichetta">Codice fiscale</td>
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
	           <td><input type="radio" name="tipoRicerca" value="esatta"/> esatta&nbsp;&nbsp;&nbsp;&nbsp;</td>
	           <td><input type="radio" name="tipoRicerca" value="iniziaPer" CHECKED /> inizia per</td>
	          <%} else {%>
	           <td><input type="radio" name="tipoRicerca" value="esatta" CHECKED /> esatta&nbsp;&nbsp;&nbsp;&nbsp;</td>
	           <td><input type="radio" name="tipoRicerca" value="iniziaPer" /> inizia per</td>
	          <%}%>
	          </tr>
	          </table>
	          </td>
	        </tr>	        
	        <tr><td class="etichetta">&nbsp;</td></tr>
	        <tr>
	          <td class="etichetta">Data diagnosi&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;dal</td>
	          <td class="campo">
	          	<af:textBox validateOnPost="true" type="date" name="datDiagnosiIn" value="<%=datDiagnosiIn%>" size="10" maxlength="10" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	          	al&nbsp;&nbsp;&nbsp;<af:textBox validateOnPost="true" type="date" name="datDiagnosiFin" value="<%=datDiagnosiFin%>" size="10" maxlength="10" />
	          </td>
	        </tr>
	        <tr>
	          <td class="etichetta">Azienda A.S.L. competente</td>
	          <td class="campo">
	              <%--af:textBox type="text" name="codAziendaAsl" value="<%=codAziendaAsl%>" size="20" maxlength="50"/--%>
                  <af:textBox title="Codice del tipo di Azienda A.S.L. Competente" value="<%=CODAZIENDAASL%>" classNameBase="input" name="CODAZIENDAASL" size="6"  /> 
                  &nbsp;
                  <a href="javascript:cercaASLCompetente('codice');">
                  	<img src="../../img/binocolo.gif" alt="Cerca per codice"></a>&nbsp;&nbsp;&nbsp;
		                  
                  <af:textBox title="Descrizione del tipo di Azienda A.S.L. Competente" value="<%=descrASL%>" classNameBase="input" name="descrASL" size="30"  />&nbsp;
                  <a href="javascript:cercaASLCompetente('descrizione');">
                  	<img src="../../img/binocolo.gif" alt="Cerca per descrizione">
                  </a>   
	          </td>
	        </tr>
			<tr>
			    <td class="etichetta">Centro per l'Impiego competente</td>
			    <td class="campo">
			      <af:comboBox title="Centro per l'Impiego competente" name="codCPIComp" moduleName="M_DE_CPI" addBlank="true" selectedValue="<%=codCPIComp%>"/>
			      <%--SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="isRequired('codCPIComp')"; </SCRIPT--%>
			    </td>
			</tr>
			<tr>	        
				<td class="etichetta">Tipo invalidità</td>
				<td class="campo">
					<input type="checkbox" name="invalidFisica" <% if(invalidFisica.length() > 0){ out.print("checked='unchecked'");}%> />Fisica
					<input type="checkbox" name="invalidPsichica" <% if(invalidPsichica.length() > 0){ out.print("checked='unchecked'");}%> /><%=labelMentale?"Mentale":"Psichica" %>
					<input type="checkbox" name="invalidIntelletiva" <% if(invalidIntelletiva.length() > 0){ out.print("checked='unchecked'");}%> />Intellettiva
					<input type="checkbox" name="invalidSensoriale" <% if(invalidSensoriale.length() > 0){ out.print("checked='unchecked'");}%> />Sensoriale
				</td>
			</tr>
			<tr>	        
				<td class="etichetta" >
					Iscrizione L.68
				</td>
				<td class="campo">
					<input type="checkbox" name="iscrizL68Aperte" <% if(iscrizL68Aperte.length() > 0){ out.print("checked='unchecked'");}%>/>Iscrizione aperta
				</td>
			</tr>
	        <tr><td colspan="2">&nbsp;</td></tr>
	        <tr>
	          <td colspan="2" align="center">
		          <%--input class="pulsanti" type="submit" name="cerca" value="Cerca" onClick="cercaDiagnosi();"/--%>
		          <input class="pulsanti" type="button" name="cerca" value="Cerca" onClick="cercaDiagnosi();"/>
		          &nbsp;&nbsp;
		          <input type="reset" class="pulsanti" value="Annulla" />				
	          </td>
	        </tr>
	        <input type="hidden" name="PAGE" value="CMDiagnosiListaPage"/>
	        <input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
	        <input type="hidden" name="fromRicerca" value="1"/>
	        <input type="hidden" name="descCPI_H" value=""/>
	      </table>
      </af:form>
      
	<%out.print(htmlStreamBottom);%>
	<%
	}//else
	%>
	</body>
</html>

