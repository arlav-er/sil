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
  it.eng.sil.security.*
"%>

<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  String cdnUt = StringUtils.getAttributeStrNotNull(serviceRequest,"CDNUT");
  String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
  
  String strCollocamentoMirato = StringUtils.getAttributeStrNotNull(serviceRequest,"FLAGCM");
  boolean flagCM = strCollocamentoMirato.equals("true")?true:false;
  
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);  
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<% if (flagCM == true) { %>

<title>RICERCA RICHIESTE DI PERSONALE PUBBLICATE IN COLLOCAMENTO MIRATO</title>
<% }else { %>
<title>Cerca pubblicazioni</title>
<% } %>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>

<%@ include file="../global/Function_CommonRicercaComune.inc"%>
<%@ include file="../anag/Function_CommonRicercaCPI.inc" %>
<SCRIPT TYPE="text/javascript">
  var imgChiusa = "../../img/chiuso.gif";
  var imgAperta = "../../img/aperto.gif";
  
  function ToUpperCase(inputName) {
    // by GG 20-09-2004
    var ctrlObj = eval("document.forms[0]." + inputName);
    ctrlObj.value = ctrlObj.value.toUpperCase();
	return true;
  }

  //Mansione
  /*function selectMansione_onClick(codMansione, codMansioneHid, descMansione, strTipoMansione) {	
	if (codMansione.value==""){

      descMansione.value="";
      strTipoMansione.value="";      
    }
    else if (codMansione.value!=codMansioneHid.value){
       
        window.open("AdapterHTTP?PAGE=RicercaMansionePage&codMansione="+codMansione.value, "Mansioni", 'toolbar=0, scrollbars=1');     
    }
  }

  function ricercaAvanzataMansioni() {
    window.open("AdapterHTTP?PAGE=RicercaMansioneAvanzataPage", "Mansioni", 'toolbar=0, scrollbars=1');
  }*/
  //Fine MAnsione
  
  function enableFieldsAzienda(){

//    document.Frm1.codTipoAzienda.disabled = false;
//    document.Frm1.codTipoAzienda.className = "input";
    
    document.Frm1.cf.disabled = false;
    document.Frm1.cf.className = "input";

    document.Frm1.piva.disabled = false;
    document.Frm1.piva.className = "input";

    document.Frm1.RagioneSociale.disabled = false;
    document.Frm1.RagioneSociale.className = "input";

    document.Frm1.Indirizzo.disabled = false;
    document.Frm1.Indirizzo.className = "input";

    document.Frm1.codCom.disabled = false;
    document.Frm1.codCom.className = "input";

    document.Frm1.desComune.disabled = false;
    document.Frm1.desComune.className = "input";
  }

  function disableFieldsAzienda(){

//      document.Frm1.codTipoAzienda.disabled = true;
//      document.Frm1.codTipoAzienda.className = "inputView";
    
      document.Frm1.cf.disabled = true;
      document.Frm1.cf.className = "inputView";

      document.Frm1.piva.disabled = true;
      document.Frm1.piva.className = "inputView";

      document.Frm1.RagioneSociale.disabled = true;
      document.Frm1.RagioneSociale.className = "inputView";

      document.Frm1.Indirizzo.disabled = true;
      document.Frm1.Indirizzo.className = "inputView";

      document.Frm1.codCom.disabled = true;
      document.Frm1.codCom.className = "inputView";

      document.Frm1.desComune.disabled = true;
      document.Frm1.desComune.className = "inputView";
  }
  
  function resetFieldsAzienda(){
    document.Frm1.prgAzienda.value = "";
    document.Frm1.prgUnita.value = "";
//    document.Frm1.codTipoAzienda.selectedIndex = 0;
    document.Frm1.cf.value = "";
    document.Frm1.piva.value = "";
    document.Frm1.RagioneSociale.value = "";
    document.Frm1.Indirizzo.value = "";
    document.Frm1.codCom.value = "";
    document.Frm1.desComune.value = "";
  }
  
  function annullaRicerca(){
    resetFieldsAzienda();
    enableFieldsAzienda();
  }
  
  function btFindAzienda_onClick(TipoAzienda,CodiceFiscale,PIva,RagioneSociale,Indirizzo,CodComune,DesComune){
    //disableFieldsAzienda();
    var s= "AdapterHTTP?PAGE=RicercaAziendaPage";
    //s = s + "&CodTipoAzienda="+TipoAzienda.value;
    s = s + "&CodiceFiscale="+CodiceFiscale.value;
    s = s + "&PIva="+PIva.value;
    s = s + "&RagioneSociale="+RagioneSociale.value;
    s = s + "&Indirizzo="+Indirizzo.value;
    s = s + "&CodComune="+CodComune.value;
    s = s + "&DesComune="+DesComune.value;    
    window.open(s,"Azienda", 'toolbar=0, scrollbars=1');
  }


  function codComuneUpperCase(inputName){
    var ctrlObj = eval("document.forms[0]." + inputName);
    eval("document.forms[0]."+inputName+".value=document.forms[0]."+inputName+".value.toUpperCase();")
    return true;
  }

function checkDate(objData1, objData2) {

  strData1=objData1.value;
  strData2=objData2.value;

  //costruisco la data della richiesta
  d1giorno=parseInt(strData1.substr(0,2));
  d1mese=parseInt(strData1.substr(3, 2)-1); //il conteggio dei mesi parte da zero :P
  d1anno=parseInt(strData1.substr(6,4));
  data1=new Date(d1anno, d1mese, d1giorno);

  //costruisce la data di scadenza
  d2giorno=parseInt(strData2.substr(0,2));
  d2mese=parseInt(strData2.substr(3,2)-1);
  d2anno=parseInt(strData2.substr(6,4));
  data2=new Date(d2anno, d2mese, d2giorno);
  
  ok=true;
  if (data2 < data1) {
      alert("La "+ objData2.title +" è precedente alla "+ objData1.title);
      objData2.focus();
      ok=false;
   }
  return ok;
}

function controllaDateRichiestaScadenza(inputName) {
  return checkDate(document.forms[0].datRichiesta, document.forms[0].datScadenza);
}

function controllaDatePubblicazione(inputName) {
  return checkDate(document.forms[0].datPubblicazione, document.forms[0].datScadenzaPubblicazione);
}

function enableFields(){
}

function cambiaStato(immagine,sezione) {
	if (immagine.alt == "Apri"){
		//apri
		divVar = document.getElementById(sezione);
		divVar.style.display = "inline";  
		immagine.src=imgAperta;
		immagine.alt="Chiudi";
	}
	else {
		//chiudi
		divVar = document.getElementById(sezione);
		divVar.style.display = "none";
		immagine.src=imgChiusa;
		immagine.alt="Apri";
	}
}

function selectMansione() {

	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;
	
		var valueMansione= " ";
		var url = "AdapterHTTP?PAGE=RicercaMansionePage&desMansione="+valueMansione;
      	<%if(flagCM){%>
      		url = url + "&flagCM=true";
      	<%}%>
      	window.open(url, "Mansioni", 'toolbar=0, scrollbars=1');      
   }

</SCRIPT>


</head>

<body class="gestione" onload="rinfresca();">

<% if (flagCM == true) { %>
	<p class="titolo">RICERCA RICHIESTE DI PERSONALE PUBBLICATE IN COLLOCAMENTO MIRATO</p>
<% }else { %>
	<p class="titolo">Ricerca pubblicazioni</p>
<% } %>




<%out.print(htmlStreamTop);%>
<af:form name="Frm1" method="POST" action="AdapterHTTP" >
<input type="hidden" name="PAGE" value="WebListaPubbPage"/>
<input type="hidden" name="cdnFunzione" value="<%=_funzione%>" />
<input type="hidden" name="CDNUT" value="<%=cdnUt%>"/>
<input type="hidden" name="prgAzienda"/>
<input type="hidden" name="prgUnita"/>
<p align="center">
<table class="main"> 
<%if(!cdnUt.equals("")) {%>
      <tr>
        <td class="etichetta" valign="top">Stato pubblicazione</td>
        <td class="campo">
          <input type="radio" name="FLGPUBBLICATA" value="DAPUB"> Da pubblicare<br>        
          <input type="radio" name="FLGPUBBLICATA" value="INPUB" checked> In pubblicazione<br>
          <input type="radio" name="FLGPUBBLICATA" value="SCAD"> Scaduta
        </td>
      </tr>

      <!--tr ><td colspan="4" ><hr width="90%"/></td></tr-->      
      <tr><td colspan="2" align="left"><div class="sezione2">Azienda</div></td></tr>
      <tr>
        <td class="etichetta">Codice Fiscale</td>
        <td class="campo" colspan="3">
          <input type="text" name="cf" value="" size="20" maxlength="16"/>
        </td>
      </tr>
      <tr>
        <td class="etichetta">Partita IVA</td>
        <td class="campo" colspan="3">
          <input type="text" name="piva" value="" size="20" maxlength="11"/>
        </td>
      </tr>
      <tr>
        <td class="etichetta">Ragione Sociale</td>
        <td class="campo" colspan="3">
          <input type="text" name="RagioneSociale" value="" size="20" maxlength="100"/>
        </td>
      </tr>
      <tr>
        <td class="etichetta">Indirizzo</td>
        <td class="campo" colspan="3">
          <input type="text" name="Indirizzo" value="" size="20" maxlength="100"/>
        </td>
      </tr>
            
      <tr>
        <td class="etichetta">Comune</td>
        <td class="campo" colspan="3">
          <INPUT type="text" name="codCom" size="4" value="" maxlength="4" class="inputEdit" onKeyUp="PulisciRicerca(document.Frm1.codCom, document.Frm1.codComHid, document.Frm1.desComune, document.Frm1.desComuneHid, null, null, 'codice');" />
          <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="trimObject('codCom')"; _arrFunz[_arrIndex++]="codComuneUpperCase('codCom')"; </SCRIPT>&nbsp;
          <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codCom, document.Frm1.desComune, null, 'codice','');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
          <INPUT type="HIDDEN" name="codComHid" size="20" value="" />
          <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="trimObject('codComHid')"; </SCRIPT>
          <INPUT type="text" name="desComune" size="30" value="" maxlength="50" class="inputEdit" onKeyUp="PulisciRicerca(document.Frm1.codCom, document.Frm1.codComHid, document.Frm1.desComune, document.Frm1.desComuneHid, null, null, 'descrizione');" onkeypress="if(event.keyCode==13) { event.keyCode=9; this.blur(); }" />
          <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="trimObject('desComune')"; </SCRIPT>&nbsp;
          <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codCom, document.Frm1.desComune, null, 'descrizione','');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
          <INPUT type="HIDDEN" name="desComuneHid" size="20" value="" />
          <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="trimObject('desComuneHid')"; </SCRIPT>&nbsp;
       </td>                
      </tr>

      <tr>
        <td colspan="2" align="center">
          <input class="pulsante" type="button" name="ricercaAzienda" value="Ricerca azienda" onclick="btFindAzienda_onClick('',document.Frm1.cf,document.Frm1.piva,document.Frm1.RagioneSociale,document.Frm1.Indirizzo,document.Frm1.codCom,document.Frm1.desComune)"/>
          <input class="pulsante" type="button" name="annullaRicercaAzienda" value="Cancella" onclick="annullaRicerca()"/>
        </td>
      </tr>
      <!--tr ><td colspan="4" ><hr width="90%"/></td></tr-->      
      <tr><td colspan="2" align="left"><div class="sezione2">&nbsp;</div></td></tr>
      <tr>
        <td class="etichetta">Numero</td>
        <td class="campo" colspan="3">
          <input type="text" name="NUMRICHIESTA" value="" size="6" maxlength="8"/>
          &nbsp;&nbsp;Anno&nbsp;&nbsp;&nbsp;
          <input type="text" name="ANNO" value="" size="4" maxlength="4"/>
        </td>
      </tr>
      <tr><td colspan="2" align="left"><div class="sezione2">&nbsp;</div></td></tr>
<%}%>
<!-- Inizio parte pubblica della ricerca -->
      <tr>
        <td class="etichetta">Dal giorno</td>
        <td class="campo">
          <af:textBox type="date" name="DATPUBBLICAZIONE" title="Data richiesta" value="" size="12" maxlength="10" validateOnPost="true"/>&nbsp;&nbsp;
          Al giorno
          <af:textBox type="date" name="DATSCADENZAPUBBLICAZIONE" title="Data richiesta" value="" size="12" maxlength="10" validateOnPost="true" />
        </td>
      </tr>

      <tr ><td colspan="4" ><hr width="90%"/></td></tr>      
      <tr>
        <td class="etichetta" valign="top">Macrocategoria</td>
        <td class="campo">
          <af:comboBox multiple="true" size="2" title="Macrocategoria" name="Macrocategoria" >
            <OPTION value="1">Annunci Pubblici</OPTION>
            <OPTION value="2">Annunci Riservati</OPTION>
          </af:comboBox>
        </td>
      </tr>
      
      <tr>
      	<td colspan="2" align="left">
      		<div class="sezione2">
      			<img id='tendinaAzienda' alt="Apri" src="../../img/chiuso.gif" onclick="cambiaStato(this,'sezioneRicercaAvanzata');"/>&nbsp;&nbsp;&nbsp;Ricerca avanzata
      		</div>
       	</td>
      </tr>
      
      <tr>
	      <td colspan="2">
	      	<div id="sezioneRicercaAvanzata" style="display: none">
	      		<table class="main">
	      			<tr>
	        			<td class="etichetta">Mansione richiesta</td>
	        			<td class="campo">     
	          				<af:textBox classNameBase="input" name="CODMANSIONE" size="7" maxlength="6" value="" readonly="true"/>
				          	<af:textBox type="hidden" name="codMansioneHid"/>
				          	
				          	<a href="javascript:selectMansione();">
				          		<img src="../../img/binocolo.gif" alt="Cerca">
				          	</a>
					        
					    </td>
				    </tr>           
				    
				    <tr valign="top">
				        <td class="etichetta">Tipo</td>
				        <td class="campo">
				       		<af:textBox type="hidden" name="CODTIPOMANSIONE" value="" />
				        	<af:textBox classNameBase="input" name="strTipoMansione" value="" readonly="true" size="48" />
			       		</td>
	        		</tr>
	        		
	      			<tr>
				        <td class="etichetta">Mansione</td>
				        <td class="campo">
				            <af:textArea cols="60" rows="2" title="Mansione" name="DESCMANSIONE" classNameBase="textarea" readonly="true" maxlength="100" value="" />
				        </td>
				    </tr>
	           
	
			      	<tr>
			      		<td colspan="2" align="left"><div class="sezione2">&nbsp;</div></td>
			      	</tr>
			      	
			      	<tr>
				        <td class="etichetta">Categoria</td>
				        <td class="campo">
				            <af:comboBox name="codQualifica" size="7" title="Categoria" multiple="true" disabled="false" required="false" focusOn="false" moduleName="M_GetIdoTipiQualificaPub" />
				        </td>
			      	</tr> 
			      	     
			      	<tr>
			      		<td colspan="2" align="left"><div class="sezione2">&nbsp;</div></td>
			      	</tr>
			      	
			        <tr class="note">
			        	<td class="etichetta">Tipo di Rapporto</td>
			        	<td class="campo">
			            	<af:comboBox name="codContratto" size="10" title="Tipo di Rapporto" multiple="true" disabled="false" required="false" focusOn="false" moduleName="M_ListContrattiRichiesta"/>
			        	</td>
			      	</tr>
			      	
	      		</div>
	      	</table>
	      </td>
	      </tr> 
      
      <tr><td colspan="2" align="left"><div class="sezione2">Luogo di Lavoro</div></td></tr>
      <tr>
       <td class="etichetta" nowrap>Comune</td>
       <td class="campo" nowrap colspan="3">
          <af:textBox type="text" name="CODCOMLAV" value="" size="4" maxlength="4" validateWithFunction="ToUpperCase" onKeyUp="javascript:PulisciRicerca(document.Frm1.CODCOMLAV, document.Frm1.CODCOMLAVHid, document.Frm1.STRCOMUNELAV, document.Frm1.STRCOMUNELAVHid, null, null, 'codice');"/>&nbsp;
          <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.CODCOMLAV, document.Frm1.STRCOMUNELAV, null,'codice','');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
          <af:textBox type="hidden" name="CODCOMLAVHid" value=""/>
          <af:textBox type="hidden" name="STRCOMUNELAVHid" value=""/>
          <af:textBox type="text" name="STRCOMUNELAV" value="" size="30" maxlength="50" onKeyUp="javascript:PulisciRicerca(document.Frm1.CODCOMLAV, document.Frm1.CODCOMLAVHid, document.Frm1.STRCOMUNELAV, document.Frm1.STRCOMUNELAVHid, null, null, 'descrizione');"/>&nbsp;
          <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.CODCOMLAV, document.Frm1.STRCOMUNELAV, null, 'descrizione','');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>&nbsp;
      </td>
      </tr>
      <tr class="note">
        <td class="etichetta">Provincia</td>
        <td class="campo">
            <af:comboBox name="CODPROVINCIALAV"
               title="Provincia" required="false"
               multiple="false"
               size="1" addBlank="true" blankValue=""
               moduleName="M_GetIDOProvince"/>
        </td>
      </tr>
      
		<tr>
			<td colspan="2" align="left"><div class="sezione2">Ricerca per parola chiave</div></td>
		</tr>
  		<tr>
  			<td class="etichetta">Parola chiave<td class="campo"><af:textBox name="keyWord"/></td>
  		</tr>
<% if( flagCM ){%>
  		<tr>
			<td colspan="2" align="left"><div class="sezione2">Categoria Coll. Mirato</div></td>
		</tr>
  		<tr>
  			<td class="etichetta">Categoria CM</td>
  			<td class="campo">		
  				  <af:comboBox classNameBase="input" 
	              				name="codMonoCMcatPubb" 
	              				title="Categoria CM">
	              	<option value=""></option>
	              	<option value="D">Disabili</option> 
	              	<option value="A">Categoria protetta ex. Art. 18</option>  
	              </af:comboBox>
  			</td>
  		</tr>
  		
  		<input type="hidden" name="flagCM" value="<%=String.valueOf(flagCM)%>"/>
<%}%>   
     <!--tr><td colspan="2" align="left"><div class="sezione2">&nbsp;</div></td></tr-->
<%if(!cdnUt.equals("")) {%>
      <!--tr ><td colspan="4" ><hr width="90%"/></td></tr-->
      <tr><td colspan="2" align="left"><div class="sezione2">&nbsp;</div></td></tr>
      <tr ><td colspan="4" >Utente di inserimento</td></tr>      
      <tr ><td colspan="4" >
          <input type="radio" name="utente" value="1" checked> Mie
          <input type="radio" name="utente" value="2"> Mio gruppo
          <input type="radio" name="utente" value=""> Tutte
      </td></tr>      
<%}%>
</table>

<table class="main">
          <tr><td colspan="2">&nbsp;</td></tr>
          <tr>
            <td colspan="2" align="center">
              <input class="pulsanti" type="submit" name="cerca" value="Cerca"/>&nbsp;&nbsp;
              <input type="reset" class="pulsanti" value="Annulla" />
            </td>
          </tr>
        </table>
 <P align="center"><a href="../../index.html">Home Sezione Pubblica del SIL</a></P>

</af:form>
<%out.print(htmlStreamBottom);%>


<%@ include file="/jsp/MIT.inc" %>

</body>
</html>
