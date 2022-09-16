<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

        
<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.StringUtils,
                  java.util.*,
                  com.engiweb.framework.security.*,
                  it.eng.afExt.utils.SourceBeanUtils" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
	String htmlStreamTop = StyleUtils.roundTopTable(false);
 	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
 	String cdnFunzione = (String)serviceRequest.getAttribute("CDNFUNZIONE");
    String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");
    
 	String cf=StringUtils.getAttributeStrNotNull(serviceRequest,"cf");
  	String cognome=StringUtils.getAttributeStrNotNull(serviceRequest,"cognome");
  	String nome=StringUtils.getAttributeStrNotNull(serviceRequest,"nome");
  	String codcpi=StringUtils.getAttributeStrNotNull(serviceRequest,"codcpi");
  	String AttiAperti=StringUtils.getAttributeStrNotNull(serviceRequest,"AttiAperti");      
  	String dataStipulaDa=StringUtils.getAttributeStrNotNull(serviceRequest,"dataStipulaDa");
  	String dataStipulaA=StringUtils.getAttributeStrNotNull(serviceRequest,"dataStipulaA");      
  	String dataColloquioDa=StringUtils.getAttributeStrNotNull(serviceRequest,"dataColloquioDa");
  	String dataColloquioA=StringUtils.getAttributeStrNotNull(serviceRequest,"dataColloquioA");      
  	String dataPattoDa=StringUtils.getAttributeStrNotNull(serviceRequest,"dataPattoDa");
  	String dataPattoA=StringUtils.getAttributeStrNotNull(serviceRequest,"dataPattoA");  
  	String dataFineAttoDa=StringUtils.getAttributeStrNotNull(serviceRequest,"dataFineAttoDa");
  	String dataFineAttoA=StringUtils.getAttributeStrNotNull(serviceRequest,"dataFineAttoA");
  	String MotivoFine=StringUtils.getAttributeStrNotNull(serviceRequest,"MotivoFine");    
  	String tipoRicerca=StringUtils.getAttributeStrNotNull(serviceRequest,"tipoRicerca");
  	if (tipoRicerca.equals("")) tipoRicerca="esatta";
%>

<%
	//configurazione chiusura did multipla
	boolean canChiusuraDidMultipla = false;
	String numConfigurazioneChiusuraDidMultipla = SourceBeanUtils.getAttrStrNotNull(serviceResponse, "M_GetNumConfigurazioneChiusuraDidMultipla.ROWS.ROW.NUM");
	if ("1".equals(numConfigurazioneChiusuraDidMultipla)) {
		canChiusuraDidMultipla = true;
	}
	
	String numConfigDidL68 = serviceResponse.containsAttribute("M_CONFIG_DID_L68.ROWS.ROW.NUM")?
  			serviceResponse.getAttribute("M_CONFIG_DID_L68.ROWS.ROW.NUM").toString():it.eng.sil.module.movimenti.constant.Properties.DEFAULT_CONFIG;
%>

<%	String visualizza_display = "none";
	String imgChiudi = "../../img/chiuso.gif";
	String imgApri = "../../img/aperto.gif"; %>
	
<html>
<head>
	<title>Ricerca Lavoratore</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>  
<af:linkScript path="../../js/"/>
<script language="Javascript">

<%@ include file="../documenti/RicercaCheck.inc" %>
</script>
<script language="JavaScript" src="../../js/script_comuni.js"></script>
<script language="Javascript"> 
	
	function cambia(immagine, sezione) {
		if (sezione.aperta==null) {
			sezione.aperta=true;
		}
		if (sezione.aperta) {
			sezione.style.display="none";
			sezione.aperta=false;
			immagine.src="../../img/chiuso.gif";
		}
		else {
			sezione.style.display="inline";
			sezione.aperta=true;
			immagine.src="../../img/aperto.gif";
		}
	}
	
	function Sezione(sezione, img,aperta){    
	    this.sezione=sezione;
	    this.aperta=aperta;
	    this.img=img;
	}

	function checkForm(){
		
		var dataStipDa = document.Form.dataStipulaDa.value;
    	var dataStipA = document.Form.dataStipulaA.value; 
		var numeroGiorni = confrontaDate(dataStipDa, dataStipA) + 1;
		 if (document.Form.AttiAperti.value == ""){
    		if ( ((dataStipDa == "") && (dataStipA == "")) || (numeroGiorni > 90) ){ 
    			alert ("Parametri generici.\n" + "Data Stipula da... a... con periodo max 90 gg.");
			 	return false;
			 }
			if ( ((dataStipDa != "") && (dataStipA == ""))){
				alert("Il campo Data Stipula a è obbligatorio");
				return false;
			}
			if ( ((dataStipDa == "") && (dataStipA != ""))){
				alert("Il campo Data Stipula da è obbligatorio");
				return false;
			}	
			if ( (document.Form.cf.value==null || document.Form.cf.value=='') 
				&& (document.Form.cognome.value==null || document.Form.cognome.value=='')
				&& (document.Form.nome.value==null || document.Form.nome.value=='')
				&& (document.Form.dataColloquioDa.value==null || document.Form.dataColloquioDa.value=='')
				&& (document.Form.dataColloquioA.value==null || document.Form.dataColloquioA.value=='')
				&& (document.Form.dataPattoDa.value==null || document.Form.dataPattoDa.value=='')
				&& (document.Form.dataPattoA.value==null || document.Form.dataPattoA.value=='')
				&& (document.Form.dataFineAttoDa.value==null || document.Form.dataFineAttoDa.value=='')
				&& (document.Form.dataFineAttoA.value==null || document.Form.dataFineAttoA.value=='')
				&& (document.Form.MotivoFine.value==null || document.Form.MotivoFine.value=='')
				&& (document.Form.NUMDELIBERA.value==null || document.Form.NUMDELIBERA.value=='') ){
				
					return confirm("ATTENZIONE!\r\n La ricerca potrebbe essere pesante e\r\n rallentare l'applicazione.\r\n\r\n Vuoi continuare?");
			}
			return true;
		}
		//controllo che le date siano corrette			 
		var objDataDa = document.Form.dataStipulaDa;
    	var objDataA = document.Form.dataStipulaA; 
		if ((objDataDa.value != "") && (objDataA.value != "")) {
			if (compareDate(objDataDa.value,objDataA.value) > 0) {
      			alert(objDataDa.title + " maggiore di " + objDataA.title);
      			objDataDa.focus();
	    		return false;
	  		}	
		}
		objDataDa = document.Form.dataColloquioDa;
    	objDataA = document.Form.dataColloquioA;	
		if ((objDataDa.value != "") && (objDataA.value != "")) {
      		if (compareDate(objDataDa.value,objDataA.value) > 0) {
      			alert(objDataDa.title + " maggiore di " + objDataA.title);
      			objDataDa.focus();
	    		return false;
	  		}
	  	}
	  	objDataDa = document.Form.dataPattoDa;
    	objDataA = document.Form.dataPattoA;	
		if ((objDataDa.value != "") && (objDataA.value != "")) {
      		if (compareDate(objDataDa.value,objDataA.value) > 0) {
      			alert(objDataDa.title + " maggiore di " + objDataA.title);
      			objDataDa.focus();
	    		return false;
	  		}
	  	}
	  	objDataDa = document.Form.dataFineAttoDa;
    	objDataA = document.Form.dataFineAttoA;	
		if ((objDataDa.value != "") && (objDataA.value != "")) {
      		if (compareDate(objDataDa.value,objDataA.value) > 0) {
      			alert(objDataDa.title + " maggiore di " + objDataA.title);
      			objDataDa.focus();
	    		return false;
	  		}
	  	}
	  	
				
		//controllo se non è stato selezionato nessun campo selezionato
		if( (document.Form.cf.value==null || document.Form.cf.value=='') 
			&& (document.Form.cognome.value==null || document.Form.cognome.value=='')
			&& (document.Form.nome.value==null || document.Form.nome.value=='')
			&& (document.Form.dataStipulaDa.value==null || document.Form.dataStipulaDa.value=='')
			&& (document.Form.dataStipulaA.value==null || document.Form.dataStipulaA.value=='')
			&& (document.Form.dataColloquioDa.value==null || document.Form.dataColloquioDa.value=='')
			&& (document.Form.dataColloquioA.value==null || document.Form.dataColloquioA.value=='')
			&& (document.Form.dataPattoDa.value==null || document.Form.dataPattoDa.value=='')
			&& (document.Form.dataPattoA.value==null || document.Form.dataPattoA.value=='')
			&& (document.Form.dataFineAttoDa.value==null || document.Form.dataFineAttoDa.value=='')
			&& (document.Form.dataFineAttoA.value==null || document.Form.dataFineAttoA.value=='')
			&& (document.Form.MotivoFine.value==null || document.Form.MotivoFine.value=='')
			&& (document.Form.NUMDELIBERA.value==null || document.Form.NUMDELIBERA.value=='')
			
		){
			return confirm("ATTENZIONE!\r\n La ricerca potrebbe essere pesante e\r\n rallentare l'applicazione.\r\n\r\n Vuoi continuare?");
		}
		return true;
	}
	
	function cambiaCpi(){
		//campo hidden
		var desc = document.Form.cpiSelected;
		//combo cpi
		var comboCPI = document.Form.codcpi;
		desc.value = comboCPI.options[comboCPI.selectedIndex].text;		
	}
	
	function visualizza(val) {
	var sezioneTitolo = document.getElementById("titoloSezione");
    var sezione = document.getElementById("TBL0");
    var image = document.getElementById("IMG0");
    
    if ((val == "No") || (val == "")) {
    	  sezioneTitolo.style.display="inline";
    	  sezione.style.display="inline";
    	  image.src="../../img/aperto.gif"; }
    else{
    	sezioneTitolo.style.display="none";
      	sezione.style.display="none";
      	image.src="../../img/chiuso.gif";}
	}  

	function ripristina(){
		var sezione = document.getElementById("TBL0");
		var sezioneTitolo = document.getElementById("titoloSezione");
		var image = document.getElementById("IMG0");
	   	sezione.style.display="none";
	   	sezioneTitolo.style.display="none";
	   	image.src="../../img/chiuso.gif";
   	}
   	
   	function cambiaMotivo(){
	   var mot = document.Form.motSelected;
	   var comboMOTIVO = document.Form.MotivoFine;
	   mot.value = comboMOTIVO.options[comboMOTIVO.selectedIndex].text;		
	}
	
</script>
<style>
table.sezione2 {
	border-collapse:collapse;
	font-family: Verdana,"Times New Roman", Arial, Sans-serif; 
	font-size: 12px;
	font-weight: bold;
	border-bottom-width: 2px; 
	border-bottom-style: solid;
	border-bottom-color: #000080;
	color:#000080; 
	position: relative;
	width: 94%;
	text-align: left;
	text-decoration: none;	
}
</STYLE>
</head>

<body class="gestione" onload="rinfresca();cambiaCpi();">
	<br>
	<p class="titolo">Ricerca Lavoratori Immediatamente Disponibili</p>
	<p align="center">
		
		<af:form action="AdapterHTTP?PAGE=ListaDispoPage" name="Form" method="POST" onSubmit="checkForm()">        	
  			<%out.print(htmlStreamTop);%> 
  				<table class="main">  
  				<tr>
					<td colspan="2"/>&nbsp;</td>
				</tr>
  				<tr>
				    <td class="etichetta">Codice Fiscale</td>
				    <td class="campo">
				    	<input type="text" name="cf" value="<%=cf%>" size="20" maxlength="16"/>
				    </td>
				</tr>
  				<tr>
				    <td class="etichetta">Cognome</td>
				    <td class="campo">
				    	<af:textBox type="text" name="cognome" value="<%=cognome%>" size="20" maxlength="50"/>
				    </td>
				</tr>
				<tr>
				    <td class="etichetta">Nome</td>          
				    <td class="campo">
				    	<af:textBox type="text" name="nome" value="<%=nome%>" size="20" maxlength="50"/>
				    </td>
				</tr>
				<tr>
				    <td class="etichetta">tipo ricerca</td>
				    <td class="campo">
				    	<table colspacing="0" colpadding="0" border="0">
				    		<tr>
				     			<td>
				     				<input type="radio" name="tipoRicerca" value="esatta" CHECKED/> esatta&nbsp;&nbsp;&nbsp;&nbsp;
				     			</td>
				     			<td>
				     				<input type="radio" name="tipoRicerca" value="iniziaPer"/> inizia per
				     			</td>
							 </tr>
						</table>
					</td>
				</tr>

			   	<tr>
			   		<td colspan="2"><hr width="90%"/></td>
			   	</tr>
			   	<tr>
    				<td class="etichetta">Atti aperti</td>
    				<td class="campo">
        				<af:comboBox classNameBase="input" name="AttiAperti" selectedValue="<%=user.getCodRif()%>" onChange="visualizza(this.value)"
                       	addBlank="true"> 
            				<option value="Si"selected>Si</option>
            				<option value="No">No</option>
            			</af:comboBox>
    				</td>
  				</tr>
			   	<tr>
			   		<td class="etichetta">
			   			Data stipula da
			   		</td>
			   		<td class="campo">
			   			<af:textBox type="date" title="Data stipula da" name="dataStipulaDa" value="<%=dataStipulaDa%>" size="12" maxlength="10" validateOnPost="true"/>
			   			&nbsp;&nbsp;a&nbsp;&nbsp;
			   			<af:textBox type="date" title="Data stipula a" name="dataStipulaA" value="<%=dataStipulaA%>" size="12" maxlength="10" validateOnPost="true"/>
			   		</td>
			    </tr>
			   	<tr>
    				<td class="etichetta">Data scadenza colloquio di orientamento da</td>
    				<td class="campo">
      					<af:textBox type="date" title="Data scadenza colloquio di orientamento da" name="dataColloquioDa" value="<%=dataColloquioDa%>" size="12" maxlength="10" validateOnPost="true"/>
      					&nbsp;&nbsp;a&nbsp;&nbsp;
      					<af:textBox type="date" title="Data scadenza colloquio di orientamento a" name="dataColloquioA" value="<%=dataColloquioA%>" size="12" maxlength="10" validateOnPost="true"/>
    				</td>
   				</tr>
				<tr>
					<td class="etichetta">Data scadenza stipula patto da</td>
				    <td class="campo">
				    	<af:textBox type="date" title="Data scadenza stupula patto da" name="dataPattoDa" value="<%=dataPattoDa%>" size="12" maxlength="10" validateOnPost="true" />
				    	&nbsp;&nbsp;a&nbsp;&nbsp;
				    	<af:textBox type="date" title="Data scadenza stupula patto a" name="dataPattoA" value="<%=dataPattoA%>" size="12" maxlength="10" validateOnPost="true" />
				    </td>
				</tr>
				
				<tr>
					<td class="etichetta">Centro per l'Impiego competente</td>
				    <td class="campo">
				    	<af:comboBox name="codcpi" title="Centro per l'Impiego competente" moduleName="M_ELENCOCPI" addBlank="true" selectedValue="<%=user.getCodRif()%>" required="true" onChange="cambiaCpi()"/>
				    </td>
				</tr>
				
				<tr>
				 	<td class="etichetta" nowrap>Did telematica</td>
				 	<td class="campo">
			 			<input type="checkbox" name="didTelematica" value="S"/>
			 		</td>            
			  	</tr>
			  	
			  	<tr>
				 	<td class="etichetta" nowrap>Did riaperta telematicamente</td>
				 	<td class="campo">
			 			<input type="checkbox" name="didRiapertaTelematica" value="S"/>
			 		</td>            
			  	</tr>
			  	
			  	<tr>			
		  			<td class="etichetta" nowrap>Precario alla data DID</td>
		  			<td class="campo">
		     			<input type="checkbox" name="precarioDataDid" value="S"/>
					</td>
		       	</tr>
		       	
		       	<%if (numConfigDidL68.equals(it.eng.sil.module.movimenti.constant.Properties.CUSTOM_CONFIG)) {%>
			       	<tr>
					 	<td class="etichetta" nowrap>DID fittizia finalizzata all'iscrizione L.68/99</td>
					 	<td class="campo">
				 			<input type="checkbox" name="flgDidL68" value="S"/>
				 		</td>            
				  	</tr>
			  	<%}%>
			  			  	
				<tr><td colspan=2>&nbsp;</td></tr>
				
				<tr>
			    <td colspan=2>    
			        <table id="titoloSezione" class="sezione2" style='display:<%=visualizza_display%>'>
			            <tr>
			                <td width='1%'>
			                    <img id='IMG0' src='<%=imgChiudi %>' onclick='cambia(this, document.getElementById("TBL0"))'></td>
			                        <td width='9%' nowrap class='titolo_sezione'>Dati Chiusura dell' atto</td>
			            <td align='right' width='90%'></td>
			            </tr>
			        </table>
			    </td>
			 </tr>
			 
				<tr><td colspan=2>&nbsp;</td></tr>	
				<tr>
    			<td colspan=2>
    			<table id='TBL0' style='display:<%=visualizza_display%>'>  
    			<script>new Sezione(document.getElementById('TBL0'),document.getElementById('IMG0'),false);</script>
        		<tr>
                <td class="etichetta">Data fine atto da</td>
                <td class="campo">
                  <af:textBox title="Data fine atto da" type="date" name="dataFineAttoDa" size="12" value="<%=dataFineAttoDa%>" maxlength="10" validateOnPost="true"/>
                  &nbsp;&nbsp;a&nbsp;&nbsp;
				  <af:textBox type="date" title="Data fine atto a" name="dataFineAttoA" value="<%=dataFineAttoA%>" size="12" maxlength="10" validateOnPost="true" />
				</td>
            	</tr>
           		<tr>
           		<td class="etichetta">Motivo fine atto</td>
                <td class="campo">
                  <af:comboBox name="MotivoFine" title="Motivo fine atto" moduleName="M_MotFineAtto" classNameBase ="input" selectedValue="<%=MotivoFine%>" addBlank="true" onChange="cambiaMotivo()"/>
                </td>
                </tr>
                <% if (canChiusuraDidMultipla) { %>
			  	<tr>
				 	<td class="etichetta" nowrap>Numero determina</td>
				 	<td class="campo">
			 			<af:textBox classNameBase="input" 
										name="NUMDELIBERA" 
										value=""
		               					size="15" 
		               					maxlength="15" 
		               					title="Numero di determina"
		               					required="false"
		               					type="string"
		               					validateOnPost="true" 
		               		/>
			 		</td>
			  	</tr>
				<% } %>
                </table>
      		
			<tr>
				<tr><td colspan="2">&nbsp;</td></tr>
			   	<tr> 
			      	<td colspan="2" align="center">
			      		<af:textBox name="cpiSelected" type="hidden" value=""/>
			      		<af:textBox name="motSelected" type="hidden" value=""/>
			      		<input name="Invia" type="submit" class="pulsanti" value="Cerca">
			      		<%
			      			String CDNFUNZIONE = (String) serviceRequest.getAttribute("CDNFUNZIONE");
			      		%>
			      		<input name="CDNFUNZIONE" type="hidden" value="<%=CDNFUNZIONE%>"/>&nbsp;&nbsp;
				      	<input name="reset" type="reset" class="pulsanti" value="Annulla" onClick="ripristina()">
			      	</td>
			   	</tr>   				
   			</table>
   			<%out.print(htmlStreamBottom);%> 
   		</af:form>  	
</p>
<table>
<tr><td class="campo2">N.B.: Se il campo "Atti aperti" é nullo vengono filtrati sia gli atti aperti che quelli chiusi.</td></tr>	    
</table> 
</body>
</html>
