<!-- @author: Giovanni D'Auria - 20 Maggio 2005 
Questa jsp si occupa della modifica di aluni dati di un movimento senza precedente
-->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../../global/noCaching.inc" %>
<%@ include file="../../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.*,
                  com.engiweb.framework.util.*,
                  it.eng.sil.module.movimenti.constant.Properties,
                  it.eng.sil.module.movimenti.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.*,
                  java.util.*,
                  java.text.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
// CM 07/02/2007 Savino: incentivi arg. 13
String datFineSgravio = "";
String decImportoConcesso = "";
String prgMovimentoArt13 = "";
//Recupero i dati del movimento
SourceBean data = (SourceBean)serviceResponse.getAttribute("GetMovimento.ROWS.ROW");

String dataInizioAttuale = (String) data.getAttribute("DATINIZIOMOV");
String prgMov =  ((BigDecimal)data.getAttribute("PRGMOVIMENTO")).toString();
String codContrattoLavoro = StringUtils.getAttributeStrNotNull(data, "codcontrattolavoro");
String codTipoMov = StringUtils.getAttributeStrNotNull(data, "codTipoMov");
String numKloMov = (String)data.getAttribute("NUMKLOMOV");
String gestionedecreto150 = StringUtils.getAttributeStrNotNull(data, "gestionedecreto150");
BigDecimal cdnUtIns = new BigDecimal ((String)data.getAttribute("CDNUTINS"));
BigDecimal cdnLavMovimento = (BigDecimal)data.getAttribute("cdnLavoratore");
String dtmIns = (String) data.getAttribute("DTMINS");
BigDecimal cdnUtMod = new BigDecimal  ((String)data.getAttribute("CDNUTMOD"));
String dtmMod = (String) data.getAttribute("DTMMOD");
Testata testata = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
String prgAzienda = ((BigDecimal)data.getAttribute("PRGAZIENDA")).toString();  
String prgUnita = ((BigDecimal)data.getAttribute("PRGUNITA")).toString(); 
String _funzione = (String)serviceRequest.getAttribute("CDNFUNZIONE");
String strEsito = (serviceResponse.containsAttribute("UPDATEMOVIMENTO.ESITO"))?(String)serviceResponse.getAttribute("UPDATEMOVIMENTO.ESITO"):"";
boolean esito = (!strEsito.equals("") && strEsito.equalsIgnoreCase("OK"));
boolean precedente = false;
if(serviceRequest.containsAttribute("PRECEDENTE")){
	precedente = (((String)serviceRequest.getAttribute("PRECEDENTE")).equalsIgnoreCase("true")) ?true :false ; 	
}
// 06/02/2007 Savino: CM incentivi ant. 13
datFineSgravio = StringUtils.getAttributeStrNotNull(data, "DATFINESGRAVIO");
decImportoConcesso = StringUtils.getAttributeStrNotNull(data, "DECIMPORTOCONCESSO");
prgMovimentoArt13 = StringUtils.getAttributeStrNotNull(data, "PRGMOVIMENTO_ART13");

String strPartitaIvaAz = "";
String strCodiceFiscaleAz   = "";
String strRagioneSocialeAz = "";
String strIndirizzoUAz = "";
String strComuneUAz    = "";
String codTipoAzienda = "";

//Genero informazioni sull'azienda
if (!prgAzienda.equals("") && !prgUnita.equals("") && !prgAzienda.equals("null") && !prgUnita.equals("null")) {
      InfoAzienda datiAz = new InfoAzienda(prgAzienda, prgUnita);
      strPartitaIvaAz = datiAz.getPIva();
      strCodiceFiscaleAz = datiAz.getCodiceFiscale();
      strRagioneSocialeAz = datiAz.getRagioneSociale();
      strIndirizzoUAz = datiAz.getIndirizzo();
      strComuneUAz = datiAz.getComune();
      codTipoAzienda = datiAz.getTipoAz();   
}
 

 String htmlStreamTop = StyleUtils.roundTopTable(true);
 String htmlStreamBottom = StyleUtils.roundBottomTable(true);
 
%>

<HTML>
<HEAD>
<title>Modifica Movimento</title>
<SCRIPT language="javascript">
<%@ include file="../../patto/_controlloDate_script.inc"%>
	var _funzione = '<%=_funzione%>';
	var precedente = '<%=precedente%>';
	var imgChiusa = "../../img/chiuso.gif";
	var imgAperta = "../../img/aperto.gif";
</SCRIPT>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
<script type="text/javascript" src="../../js/movimenti/common/MovimentiSezioniATendina.js" language="JavaScript"></script>
    <script type="text/javascript" src="../../js/movimenti/common/MovimentiRicercaSoggetto.js" language="JavaScript"></script>
<af:linkScript path="../../js/"/>
<SCRIPT language="javascript">

	function aggiornaAzienda(){
		document.frm.strCodiceFiscaleAzN.value = opened.dati.codiceFiscaleAzienda;	
		document.frm.ragioneSocialeAzN.value = opened.dati.ragioneSociale;
		document.frm.pIvaAziendaN.value = opened.dati.partitaIva;
		document.frm.IndirizzoAzN.value = opened.dati.strIndirizzoAzienda ;
		document.frm.descrTipoAzN.value = opened.dati.descrTipoAz;		
		document.frm.prgAzienda.value = opened.dati.prgAzienda;
		document.frm.prgUnita.value = opened.dati.prgUnita;
		var imgV = document.getElementById("tendinaAziendaProvenienza");
        cambiaLavMC("sezioneAziendaProv","inline");
        imgV.src=imgAperta;
		opened.close();
	}	
	
	function cambiaLavMC(elem,stato){
	  divVar = document.getElementById(elem);
	  divVar.style.display = stato;
	}
<%-- 08/02/2007 Savino: incentivi art. 13 --%>
<% 
	// INIT-PARTE-TEMP
	if (Sottosistema.CM.isOn()) { 
	// END-PARTE-TEMP
%>
	function aggiorna(){
		var cambiaTutto= false;
		if (isInSubmit()) return;
		
		if ( document.frm.dataInizioMov.value == "" && document.frm.prgAzienda.value == "" && document.frm.prgUnita.value == "" && 
		   		document.frm.DECIMPORTOCONCESSO.value == "" && document.frm.DATFINESGRAVIO.value == "" ) {
			alert("Bisogna scegliere un'azienda oppure inserire una nuova data oppure nuovi incentivi.");
			return;
		}
		<%-- Controllo prima se debbo inserire solo gli incentivi --%>
		<%-- Attenzione: se si inserisce uno spazio nei campi degli incentivi, questi non devono essere trimmati. Infatti se l'operatore
				inserisce uno scazio significa che vuole cancellare il valore registrato. La funzione controllaFunzTL() esegui il trim di 
				tutti i campi che valida.  --%>
		datFineSgravio = document.frm.DATFINESGRAVIO.value;
		decImportoConcesso =  document.frm.DECIMPORTOCONCESSO.value;
		if ( document.frm.dataInizioMov.value == "" && document.frm.prgAzienda.value == "" && document.frm.prgUnita.value == "" ) {
			// vengono inseriti gli incentivi ma il movimento non verra' modificato
			if(!controllaFunzTL()) return;
			<%-- controllaFunzTL() oltre a validare provvede a formattare i campi. Se ci sono dati nei campi degli incentivi
					allora li riprendo in modo da sfruttare la formattazione. --%>
			if (document.frm.DATFINESGRAVIO.value!="") 
					datFineSgravio = document.frm.DATFINESGRAVIO.value;
			if (document.frm.DECIMPORTOCONCESSO.value!="") 
				decImportoConcesso =  document.frm.DECIMPORTOCONCESSO.value;
			var url ="AdapterHTTP?PAGE=ModificaMovimentoPage&AggiornaSoloIncentivi=1";
			url = url + "&CDNLAVORATORE=<%=cdnLavMovimento%>"
			url = url + "&NUMKLOMOV="+<%=numKloMov%>
			url = url + "&PRGMOV="+<%=prgMov%>+"&AGGIORNA_MOV="
			url = url + "&prgAzienda=" + document.frm.prgAzienda.value + "&prgunita="+ document.frm.prgUnita.value
			url = url + "&PRECEDENTE=" + precedente 
			url = url + "&DECIMPORTOCONCESSO=" + decImportoConcesso
			url = url + "&DATFINESGRAVIO=" + datFineSgravio
			url = url + "&DECIMPORTOCONCESSO_OLD=" + document.frm.DECIMPORTOCONCESSO_OLD.value
			url = url + "&DATFINESGRAVIO_OLD=" + document.frm.DATFINESGRAVIO_OLD.value 
			url = url + "&PRGMOVIMENTO_ART13=" + document.frm.PRGMOVIMENTO_ART13.value
			
			setWindowLocation(url);
			return;
		}
		
		if (document.frm.dataInizioMov.value == "" || precedente == "true"){
			document.frm.dataInizioMov.value  = document.frm.datInizioAttuale.value;			
		}
		else{			
			document.frm.dataInizioMovHidden.value = document.frm.dataInizioMov.value;
		}
			
		if(document.frm.prgAzienda.value =="" && document.frm.prgUnita.value == ""){
		
			document.frm.prgAzienda.value = document.frm.prgAziendaOrig.value;
			document.frm.prgUnita.value = document.frm.prgUnitaOrig.value;
		}else if ( (document.frm.prgAzienda.value != document.frm.prgAziendaOrig.value || document.frm.prgUnita.value != document.frm.prgUnitaOrig.value) ){
		
			if(confirm("Vuoi cambiare la sede a tutti i movimenti successivi?\n Attenzione: la propagazione della sede non si fermerà in caso di trasferimenti o trasformazioni azienda!")){
				cambiaTutto = true;
			}
		}else if (document.frm.dataInizioMov.value == ""){
			alert("L'azienda non è cambiata.")
			return
		}
		
	    if(controllaFunzTL()){
		    <%-- controllaFunzTL() oltre a validare provvede a formattare i campi. Se ci sono dati nei campi degli incentivi
					allora li riprendo in modo da sfruttare la formattazione. --%>
			if (document.frm.DATFINESGRAVIO.value!="") 
					datFineSgravio = document.frm.DATFINESGRAVIO.value;
			if (document.frm.DECIMPORTOCONCESSO.value!="") 
				decImportoConcesso =  document.frm.DECIMPORTOCONCESSO.value;
			var url ="AdapterHTTP?PAGE=ModificaMovimentoPage&DATINIZIOMOV="+ document.frm.dataInizioMov.value + "&DATINIZIOATTUALE="+ document.frm.datInizioAttuale.value
			url = url + "&CDNLAVORATORE=<%=cdnLavMovimento%>"
			url = url + "&NUMKLOMOV="+<%=numKloMov%>
			url = url + "&PRGMOV="+<%=prgMov%>+"&AGGIORNA_MOV="
			url = url + "&prgAzienda=" + document.frm.prgAzienda.value + "&prgunita="+ document.frm.prgUnita.value
			url = url + "&PRECEDENTE=" + precedente 
			url = url + "&DECIMPORTOCONCESSO=" + decImportoConcesso
			url = url + "&DATFINESGRAVIO=" + datFineSgravio
			url = url + "&DECIMPORTOCONCESSO_OLD=" + document.frm.DECIMPORTOCONCESSO_OLD.value
			url = url + "&DATFINESGRAVIO_OLD=" + document.frm.DATFINESGRAVIO_OLD.value 
			url = url + "&PRGMOVIMENTO_ART13=" + document.frm.PRGMOVIMENTO_ART13.value
			if(cambiaTutto){
				url = url + "&cambiaTutto=" 
			}
			
			setWindowLocation(url);
		}		
	}
<% 
	// INIT-PARTE-TEMP
	} else {
	// END-PARTE-TEMP
%>
	function aggiorna(){
		var cambiaTutto= false;
		if (isInSubmit()) return;
		
		if ( document.frm.dataInizioMov.value == "" && document.frm.prgAzienda.value == "" && document.frm.prgUnita.value == "" ) {
			alert("Bisogna scegliere un'azienda oppure inserire una nuova data.");
			return;
		}
		
		if (document.frm.dataInizioMov.value == "" || precedente == "true"){
			document.frm.dataInizioMov.value  = document.frm.datInizioAttuale.value;			
		}
		else{			
			document.frm.dataInizioMovHidden.value = document.frm.dataInizioMov.value;
		}
			
		if(document.frm.prgAzienda.value =="" && document.frm.prgUnita.value == ""){
		
			document.frm.prgAzienda.value = document.frm.prgAziendaOrig.value;
			document.frm.prgUnita.value = document.frm.prgUnitaOrig.value;
		}else if ( (document.frm.prgAzienda.value != document.frm.prgAziendaOrig.value || document.frm.prgUnita.value != document.frm.prgUnitaOrig.value) ){
		
			if(confirm("Vuoi cambiare la sede a tutti i movimenti successivi?\n Attenzione: la propagazione della sede non si fermerà in caso di trasferimenti o trasformazioni azienda!")){
				cambiaTutto = true;
			}
		}else if (document.frm.dataInizioMov.value == "") {
			alert("L'azienda non è cambiata.")
			return
		}
		
	    if(controllaFunzTL()){
			var url ="AdapterHTTP?PAGE=ModificaMovimentoPage&DATINIZIOMOV="+ document.frm.dataInizioMov.value + "&DATINIZIOATTUALE="+ document.frm.datInizioAttuale.value
			url = url + "&CDNLAVORATORE=<%=cdnLavMovimento%>"
			url = url + "&NUMKLOMOV="+<%=numKloMov%>
			url = url + "&PRGMOV="+<%=prgMov%>+"&AGGIORNA_MOV="
			url = url + "&prgAzienda=" + document.frm.prgAzienda.value + "&prgunita="+ document.frm.prgUnita.value
			url = url + "&PRECEDENTE=" + precedente
			if(cambiaTutto){
				url = url + "&cambiaTutto=" 
			}
			
			setWindowLocation(url);
		}		
	}
<% 
	// INIT-PARTE-TEMP
	} 
	// END-PARTE-TEMP
%>
		
	 function fieldChanged() {} // lasciata per compatibilità
	 
	 function caricaMsg(){
	 	var esito = <%=esito%>;
	 	if(esito){
	 			alert("Per visualizzare le modifiche \nuscire e rientrare nel dettaglio del movimento o consultare la lista");
	 			window.close();
	 	}
	 
	 }
</SCRIPT>
</HEAD>
<BODY onload="caricaMsg()">
	<af:form name="frm" action="AdapterHTTP" method="post" >
		<br>
		<p class="titolo">Modifica movimento</p>
		<af:showMessages prefix="UpdateMovimento"/>
		<af:showErrors/>
		<%//out.print(htmlStreamTop);%>
		<table class="main" border="0">
			<TR>	
				<TD>
				<%out.print(htmlStreamTop);%>
					<TABLE width="100%">
						<TR>
							<TD>
								<B>Situazione attuale</B>
							</TD>														
						</TR>
						<TR><TD>&nbsp;</TD></TR>
						<TR><TD>&nbsp;</TD></TR>
						<TR>
							<TD>
								Data inizio &nbsp; <af:textBox onKeyUp="fieldChanged();" classNameBase="input" type="date" name="datInizioAttuale" value="<%=dataInizioAttuale%>"  
		                        readonly="true" size="11" maxlength="11"/>
							</TD>
						</TR>
						<tr>
				            <td colspan= "4" valign="top">          
								<div class='sezione2' id='SedeAzienda' >
								<img id='tendinaAzienda' 
              							alt="Chiudi" 
              							src="../../img/aperto.gif" 
              							onclick="cambiaTendina(this,'sezioneAzienda', '');"/>Sede Azienda&nbsp;&nbsp; 
					            </div>					 
					        </td>
					       </tr>
					       <TR>
					       <TD>
					       	<div id="sezioneAzienda" style="display: inline">
					       	<table class="main"  border="0">
                    			<tr>
                      				<td class="etichetta" nowrap>Codice Fiscale</td>
                      				<td class="campo">
                        				<af:textBox classNameBase="input" 
                        							type="text" 
                        							name="codFiscaleAzProv" 
                        							readonly="true" 
                        							value="<%=strCodiceFiscaleAz%>" 
				                        			size="30" 
                        							maxlength="16"/>                       
                      				</td>
                    			</tr>
                    			<tr valign="top">
                      				<td class="etichetta" nowrap>Partita IVA</td>
                      				<td class="campo">
			                        <af:textBox classNameBase="input" 
			                        			type="text" 
			                        			name="pIvaAzProv" 
			                        			readonly="true" 
			                        			value="<%=strPartitaIvaAz%>" 
			                        			size="30" 
			                        			maxlength="11"/>
			                      	</td>
			                    </tr>
			                    <tr>
			                      	<td class="etichetta" nowrap>Ragione Sociale</td>
			                      	<td class="campo">
			                        	<af:textBox classNameBase="input" 
			                        				type="text" 
			                        				name="ragioneSocialeAzProv" 
			                        				readonly="true" 
			                        				value="<%=strRagioneSocialeAz%>" 
			                        				size="60" 
			                        				maxlength="150"/>
			                      	</td>
			                    </tr>
			                    <tr>
			                      	<td class="etichetta" nowrap>Indirizzo (Comune)</td>
			                      	<td class="campo">
			                        	<af:textBox classNameBase="input" 
			                        				type="text" 
			                        				name="IndirizzoAzProv" 
			                        				readonly="true" 
			                        				value="<%= strIndirizzoUAz %>" 
			                        				size="60" 
			                        				maxlength="150"/>
			                      	</td>
			                    </tr>
			                    <tr>
			                      	<td class="etichetta" nowrap>Tipo Azienda</td>
			                      	<td class="campo">
			                        	<af:textBox classNameBase="input" 
			                        				type="text" 
			                        				name="descrTipoAzProv" 
			                        				readonly="true" 
			                        				value="<%=codTipoAzienda%>" 
			                        				size="30" 
			                        				maxlength="30"/>
			                      	</td>
			                    </tr>
							</table>
							</div>
							
					       </TD>
					       </TR>       			
<%-- 08/02/2007 Savino: incentivi art. 13 --%>
<% 
	// INIT-PARTE-TEMP
	if (Sottosistema.CM.isOn()) { 
	// END-PARTE-TEMP
%>
							<tr>
								<td colspan= "4" valign="top">          
									<div class='sezione2' id='IncentiviArt13_s' >
										<img id='tendinaArt13' 
		              							alt="Chiudi" 
		              							src="../../img/aperto.gif" 
		              							onclick="cambiaTendina(this,'IncentiviArt13', '');"/>Incentivi art. 13&nbsp;&nbsp; 
							            </div>					 
								</td>
							</tr>
							<TR>
								<TD>
									<div id="IncentiviArt13" style="display: inline">
										<table class="main"  border="0">
			                    			<tr>
			                      				<td class="etichetta" nowrap>Data</td>
			                      				<td class="campo">
			                        				<af:textBox classNameBase="input" 
			                        							type="date" 
			                        							name="DATFINESGRAVIO_OLD" 
			                        							readonly="true" 
			                        							value="<%= datFineSgravio %>" 
							                        			size="12" 
			                        							maxlength="10"/>                       
			                      				</td>
			                    			</tr>
			                    			<tr valign="top">
			                      				<td class="etichetta" nowrap>Importo</td>
			                      				<td class="campo">
						                        <af:textBox classNameBase="input" 
						                        			type="integer" 
						                        			name="DECIMPORTOCONCESSO_OLD" 
						                        			readonly="true" 
						                        			value="<%= decImportoConcesso %>" 
						                        			size="11" 
						                        			maxlength="10"/>
						                        <input type="hidden" name="PRGMOVIMENTO_ART13" value="<%=prgMovimentoArt13%>">
						                      	</td>
						                    </tr>
										</table>
									</div>
								</TD>
							</TR>
<% 
	// INIT-PARTE-TEMP
	} 
	// END-PARTE-TEMP
%>
					</TABLE>
					<%out.print(htmlStreamBottom);%>
				</TD>
				<tr>
				</tr>				
				<tr>
				</tr>				
				<TD>
				<%out.print(htmlStreamTop);%>
					<TABLE width="100%">
						<TR>
							<TD >
								<B>Nuove informazioni</B>
							</TD>
						</TR>
						<TR><TD>&nbsp;</TD></TR>
						<TR><TD>&nbsp;</TD></TR>
						<TR>
							<TD >
								Data inizio &nbsp; <af:textBox onKeyUp="fieldChanged();" classNameBase="input" type="date" name="dataInizioMov" value="" validateOnPost="true" 
		                           size="11" maxlength="11" readonly="<%=String.valueOf(precedente)%>" required="false" title="Data inizio movimento"/>
							</TD>
						</TR>
					    <tr> 
	      					<td valign="top">
            					<div class="sezione2">
              						<img id='tendinaAziendaProvenienza' 
              							alt="Apri" 
              							src="../../img/chiuso.gif" 
              							onclick="cambiaTendina(this,'sezioneAziendaProv', '');"/>Nuova Azienda&nbsp;&nbsp;
            							&nbsp;&nbsp;
              				 		<a id='ricercaAzienda' href="#" onClick="javascript:apriSelezionaSoggetto('Aziende', 'aggiornaAzienda', '', '', '');"><img src="../../img/binocolo.gif" alt="Cerca azienda nel DB"></a>
            					</div>	      				
	      					</td>
	      				</tr>
	      				<tr>
			      			<td>
		              			<div id="sezioneAziendaProv" style="display: none">
		                			
		                			

		                			<table class="main"  border="0">
		                    			<tr>
		                      				<td class="etichetta" nowrap>Codice Fiscale</td>
		                      				<td class="campo">
		                        				<af:textBox classNameBase="input"
		                        							type="text"
		                        							name="strCodiceFiscaleAzN"
		                        							readonly="true"
		                        							value=""
		                        							size="30"
		                        							maxlength="16"/>
		                      				</td>
		                    			</tr>
		                    			<tr valign="top">
		                      				<td class="etichetta" nowrap>Partita IVA</td>
		                      				<td class="campo">
					                        <af:textBox classNameBase="input"
					                        			type="text"
					                        			name="pIvaAziendaN"
					                        			readonly="true"
					                        			value=""
					                        			size="30"
					                        			maxlength="11"/>
					                      	</td>
					                    </tr>
					                    <tr>
					                      	<td class="etichetta" nowrap>Ragione Sociale</td>
					                      	<td class="campo">
					                        	<af:textBox classNameBase="input"
					                        				type="text"
					                        				name="ragioneSocialeAzN"
					                        				readonly="true"
					                        				value=""
					                        				size="60"
					                        				maxlength="100"/>
					                      	</td>
					                    </tr>
					                    <tr>
					                      	<td class="etichetta" nowrap>Indirizzo (Comune)</td>
					                      	<td class="campo">
					                        	<af:textBox classNameBase="input"
					                        				type="text"
					                        				name="IndirizzoAzN"
					                        				readonly="true"
					                        				value=""
					                        				size="60"
					                        				maxlength="100"/>
					                      	</td>
					                    </tr>
					                    <tr>
					                      	<td class="etichetta" nowrap>Tipo Azienda</td>
					                      	<td class="campo">
					                        	<af:textBox classNameBase="input"
					                        				type="text"
					                        				name="descrTipoAzN"
					                        				readonly="true"
					                        				value=""
					                        				size="30"
					                        				maxlength="30"/>
					                      	</td>
					                    </tr>
									</table>
		                			
									
					             </div>	      				
			      			</td>
			      		</tr>			      					      		
<%-- 08/02/2007 Savino: incentivi art. 13 --%>
<% 
	// INIT-PARTE-TEMP
	if (Sottosistema.CM.isOn()) { 
	// END-PARTE-TEMP
%>
							<tr>
								<td colspan= "4" valign="top">          
									<div class='sezione2' id='IncentiviArt13_new_s' >
										<img id='tendinaArt13_new' 
		              							alt="Apri" 
		              							src="../../img/chiuso.gif" 
		              							onclick="cambiaTendina(this,'IncentiviArt13_new', '');"/>Incentivi art. 13&nbsp;&nbsp; 
							            </div>					 
								</td>
							</tr>
							<TR>
								<TD>
									<div id="IncentiviArt13_new" style="display: none">
										<table class="main"  border="0">
			                    			<tr>
			                      				<td class="etichetta" nowrap>Data</td>
			                      				<td class="campo">
			                        				<af:textBox classNameBase="input" 
			                        							type="date" 
			                        							name="DATFINESGRAVIO" 
			                        							readonly="false"
			                        							validateOnPost="true" 
			                        							value="" 
							                        			size="12" 
			                        							maxlength="10"/>                       
			                      				</td>
			                    			</tr>
			                    			<tr valign="top">
			                      				<td class="etichetta" nowrap>Importo</td>
			                      				<td class="campo">
						                        <af:textBox classNameBase="input" 
						                        			type="integer" 
						                        			name="DECIMPORTOCONCESSO" 
						                        			readonly="false" 
		                        							validateOnPost="true" 
						                        			value="" 
						                        			size="11" 
						                        			maxlength="10"/>
						                      	</td>
						                    </tr>
										</table>
									</div>
								</TD>
							</TR>
<% 
	// INIT-PARTE-TEMP
	} 
	// END-PARTE-TEMP
%>
		
		</TABLE>
		<%out.print(htmlStreamBottom);%>
		
		</TD>
			
	</table>
		
		
		<br>
		<br>
		
		<table class="main">
		  	<tr>
		  		<td>
		  			<input type="button" class="pulsanti" value="Chiudi" onClick="window.close();">
		  			<input type="button" class="pulsanti" value="Aggiorna" onclick="aggiorna()">
		  			
		  			<input type="hidden" name="dataInizioMovHidden" value="" >
		  			<input type="hidden" name="prgAziendaOrig" value="<%=prgAzienda%>" >
    				<input type="hidden" name="prgUnitaOrig" value="<%=prgUnita%>" >
    				<input type="hidden" name="prgAzienda" value="" >
    				<input type="hidden" name="prgUnita" value="" >
    				
				</td>
			</tr>
		</table>
		
		
		<%out.print(htmlStreamBottom);%> 
	</af:form>
	<% if(testata!=null) { %>
	  	  <div align="center">
	      <%testata.showHTML(out);%>
	      </div>
	  <%}%>
	  
	<br>  
	<br>
	
	
	<table width="100%">
	    <tr><td colspan=3 style="campo2">N.B.:  Non verranno effettuati controlli sull'azienda ed in particolare sul numero di proroghe in caso di azienda interinale.</td></tr>	    
  </table>
</BODY>
</HTML>
