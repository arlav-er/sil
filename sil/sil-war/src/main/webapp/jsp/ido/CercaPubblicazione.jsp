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

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
 // NOTE: Attributi della pagina (pulsanti e link) 
//  PageAttribs attributi = new PageAttribs(user, "AnagMainPage");
//  boolean canModify = attributi.containsButton("nuovo");

 //boolean canInsert=attributi.containsButton("INSERISCI");

  Object codiceUtente= sessionContainer.getAttribute("_CDUT_");

  String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
  PageAttribs attributi = new PageAttribs(user, "IdoPubbRicercaPage");
  boolean canInsert=attributi.containsButton("INSERISCI");
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);
  String anno = StringUtils.getAttributeStrNotNull(serviceRequest,"ANNO");
  String CDNUT = StringUtils.getAttributeStrNotNull(serviceRequest,"CDNUT");
  String codMansione = StringUtils.getAttributeStrNotNull(serviceRequest,"CODMANSIONE");
  String codTipoMansione = StringUtils.getAttributeStrNotNull(serviceRequest,"CODTIPOMANSIONE");
  String datPubblicazione = StringUtils.getAttributeStrNotNull(serviceRequest,"DATPUBBLICAZIONE");
  String datScadenzaPubblicazione = StringUtils.getAttributeStrNotNull(serviceRequest,"DATSCADENZAPUBBLICAZIONE");
  String descMansione = StringUtils.getAttributeStrNotNull(serviceRequest,"DESCMANSIONE");
  String flgPubblicata = StringUtils.getAttributeStrNotNull(serviceRequest,"FLGPUBBLICATA");
  if (flgPubblicata.equals(""))
    flgPubblicata="INPUB";
  String Indirizzo =  StringUtils.getAttributeStrNotNull(serviceRequest,"Indirizzo");
  String numRichiesta = StringUtils.getAttributeStrNotNull(serviceRequest,"NUMRICHIESTA");
  String RagioneSociale = StringUtils.getAttributeStrNotNull(serviceRequest,"RagioneSociale");
    String UTRIC = StringUtils.getAttributeStrNotNull(serviceRequest,"UTRIC");
      
  String codMonoCMcatPubb = StringUtils.getAttributeStrNotNull(serviceRequest,"codMonoCMcatPubb");
      //String cerca = StringUtils.getAttributeStrNotNull(serviceRequest,"cerca");
  String cf = StringUtils.getAttributeStrNotNull(serviceRequest,"cf");
  String codCom = StringUtils.getAttributeStrNotNull(serviceRequest,"codCom");
  String codComHid = StringUtils.getAttributeStrNotNull(serviceRequest,"codComHid");
  String codMansioneHid = StringUtils.getAttributeStrNotNull(serviceRequest,"codMansioneHid");
//  String codTipoAzienda = StringUtils.getAttributeStrNotNull(serviceRequest,"codTipoAzienda");
  String desComune = StringUtils.getAttributeStrNotNull(serviceRequest,"desComune");
  String desComuneHid = StringUtils.getAttributeStrNotNull(serviceRequest,"desComuneHid");
  String piva = StringUtils.getAttributeStrNotNull(serviceRequest,"piva");
  String prgAzienda = StringUtils.getAttributeStrNotNull(serviceRequest,"prgAzienda");
  String prgUnita = StringUtils.getAttributeStrNotNull(serviceRequest,"prgUnita");
  String strTipoMansione = StringUtils.getAttributeStrNotNull(serviceRequest,"strTipoMansione");
  String utente = StringUtils.getAttributeStrNotNull(serviceRequest,"utente");
  if (utente==""){   
    utente="1";
  }

%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Cerca pubblicazioni</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>

<%@ include file="../global/Function_CommonRicercaComune.inc"%>
<%@ include file="../anag/Function_CommonRicercaCPI.inc" %>
<%@ include file="Mansioni_Ido_CommonScripts.inc" %>
<script language="Javascript">
     <% 
      //Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
</script>

<SCRIPT TYPE="text/javascript">

  var flagRicercaPage = "S";
  
  function enableFieldsAzienda(){

//    Frm1.codTipoAzienda.disabled = false;
//    Frm1.codTipoAzienda.className = "input";
    
    Frm1.cf.disabled = false;
    Frm1.cf.className = "input";

    Frm1.piva.disabled = false;
    Frm1.piva.className = "input";

    Frm1.RagioneSociale.disabled = false;
    Frm1.RagioneSociale.className = "input";

    Frm1.Indirizzo.disabled = false;
    Frm1.Indirizzo.className = "input";

    Frm1.codCom.disabled = false;
    Frm1.codCom.className = "input";

    Frm1.desComune.disabled = false;
    Frm1.desComune.className = "input";
  }

  function disableFieldsAzienda(){

//      Frm1.codTipoAzienda.disabled = true;
//      Frm1.codTipoAzienda.className = "inputView";
    
      Frm1.cf.disabled = true;
      Frm1.cf.className = "inputView";

      Frm1.piva.disabled = true;
      Frm1.piva.className = "inputView";

      Frm1.RagioneSociale.disabled = true;
      Frm1.RagioneSociale.className = "inputView";

      Frm1.Indirizzo.disabled = true;
      Frm1.Indirizzo.className = "inputView";

      Frm1.codCom.disabled = true;
      Frm1.codCom.className = "inputView";

      Frm1.desComune.disabled = true;
      Frm1.desComune.className = "inputView";
  }
  
  function resetFieldsAzienda(){
    Frm1.prgAzienda.value = "";
    Frm1.prgUnita.value = "";
//    Frm1.codTipoAzienda.selectedIndex = 0;
    Frm1.cf.value = "";
    Frm1.piva.value = "";
    Frm1.RagioneSociale.value = "";
    Frm1.Indirizzo.value = "";
    Frm1.codCom.value = "";
    Frm1.desComune.value = "";
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
  d1giorno=parseInt(strData1.substr(0,2), 10);
  d1mese=parseInt(strData1.substr(3, 2),10)-1; //il conteggio dei mesi parte da zero :P
  d1anno=parseInt(strData1.substr(6,4),10);
  data1=new Date(d1anno, d1mese, d1giorno);

  //costruisce la data di scadenza
  d2giorno=parseInt(strData2.substr(0,2),10);
  d2mese=parseInt(strData2.substr(3,2),10)-1;
  d2anno=parseInt(strData2.substr(6,4),10);
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

</SCRIPT>


</head>

<body class="gestione" onload="rinfresca();">
<p class="titolo">Ricerca pubblicazioni</p>

<%out.print(htmlStreamTop);%>
<af:form name="Frm1" method="POST" action="AdapterHTTP" >
<input type="hidden" name="PAGE" value="IdoListaPubbPage"/>
<input type="hidden" name="cdnFunzione" value="<%=_funzione%>" />
<input type="hidden" name="CDNUT" value="<%= codiceUtente %>"/>
<input type="hidden" name="prgAzienda"/>
<input type="hidden" name="prgUnita"/>
<!--input type="hidden" name="codTipoAzienda"/-->

<br/>
<p align="center">
<table class="main"> 

      <tr>
        <td class="etichetta" valign="top">Stato pubblicazione</td>
        <td class="campo">
          <input type="radio" name="FLGPUBBLICATA" value="DAPUB" <%if(flgPubblicata.equals("DAPUB")){out.print("checked");}%>> Da pubblicare<br>        
          <input type="radio" name="FLGPUBBLICATA" value="INPUB" <%if(flgPubblicata.equals("INPUB")){out.print("checked");}%>> In pubblicazione<br>
          <input type="radio" name="FLGPUBBLICATA" value="SCAD" <%if(flgPubblicata.equals("SCAD")){out.print("checked");}%>> Scaduta
        </td>
      </tr>

      <tr ><td colspan="4" ><hr width="90%"/></td></tr>      

      <tr>
        <td class="etichetta" valign="top">Macrocategoria</td>
        <td class="campo">
          <af:comboBox multiple="true" title="Macrocategoria" name="Macrocategoria" >
            <OPTION value="1">Annunci Pubblici</OPTION>
            <OPTION value="2">Annunci Riservati</OPTION>
          </af:comboBox>
        </td>
      </tr>
      
      <tr ><td colspan="4" ><hr width="90%"/></td></tr>      

      <tr>
        <td class="etichetta" valign="top">Modalità pubblicazione</td>
        <td class="campo">
          <af:comboBox multiple="true" title="Modalità Pubblicazione" name="modPubblicazione">
            <OPTION value="1">Web      </OPTION>
            <OPTION value="2">Giornali </OPTION>
            <OPTION value="3">Bacheca  </OPTION>
          </af:comboBox>
        </td>
      </tr>

      <tr ><td colspan="4" ><hr width="90%"/></td></tr>      
      
      <tr>
        <td class="etichetta">Codice Fiscale</td>
        <td class="campo" colspan="3">
          <input type="text" name="cf" value="<%=cf%>" size="20" maxlength="16"/>
        </td>
      </tr>
      <tr>
        <td class="etichetta">Partita IVA</td>
        <td class="campo" colspan="3">
          <input type="text" name="piva" value="<%=piva%>" size="20" maxlength="11"/>
        </td>
      </tr>
      <tr>
        <td class="etichetta">Ragione Sociale</td>
        <td class="campo" colspan="3">
          <input type="text" name="RagioneSociale" value="<%=RagioneSociale%>" size="20" maxlength="100"/>
        </td>
      </tr>
      <tr>
        <td class="etichetta">Indirizzo</td>
        <td class="campo" colspan="3">
          <input type="text" name="Indirizzo" value="<%=Indirizzo%>" size="20" maxlength="100"/>
        </td>
      </tr>
            
      <tr>
        <td class="etichetta">Comune</td>
        <td class="campo" colspan="3">
          <INPUT type="text" name="codCom" size="4" value="<%=codCom%>" maxlength="4" class="inputEdit" onKeyUp="PulisciRicerca(Frm1.codCom, Frm1.codComHid, Frm1.desComune, Frm1.desComuneHid, null, null, 'codice');" />
          <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="trimObject('codCom')"; _arrFunz[_arrIndex++]="codComuneUpperCase('codCom')"; </SCRIPT>&nbsp;
          <A HREF="javascript:btFindComuneCAP_onclick(Frm1.codCom, Frm1.desComune, null, 'codice','');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
          <INPUT type="HIDDEN" name="codComHid" size="20" value="<%=codComHid%>" />
          <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="trimObject('codComHid')"; </SCRIPT>
          <INPUT type="text" name="desComune" size="30" value="<%=desComune%>" maxlength="50" class="inputEdit" onKeyUp="PulisciRicerca(Frm1.codCom, Frm1.codComHid, Frm1.desComune, Frm1.desComuneHid, null, null, 'descrizione');" onkeypress="if(event.keyCode==13) { event.keyCode=9; this.blur(); }" />
          <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="trimObject('desComune')"; </SCRIPT>&nbsp;
          <A HREF="javascript:btFindComuneCAP_onclick(Frm1.codCom, Frm1.desComune, null, 'descrizione','');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
          <INPUT type="HIDDEN" name="desComuneHid" size="20" value="<%=desComuneHid%>" />
          <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="trimObject('desComuneHid')"; </SCRIPT>&nbsp;
       </td>                
      </tr>

      <tr>
        <td colspan="2" align="center">
          <input class="pulsante" type="button" name="ricercaAzienda" value="Ricerca azienda" onclick="btFindAzienda_onClick('',Frm1.cf,Frm1.piva,Frm1.RagioneSociale,Frm1.Indirizzo,Frm1.codCom,Frm1.desComune)"/>
          <input class="pulsante" type="button" name="annullaRicercaAzienda" value="Cancella" onclick="annullaRicerca()"/>
        </td>
      </tr>

      <tr ><td colspan="4" ><hr width="90%"/></td></tr>      

      <tr>
        <td class="etichetta">Dal giorno</td>
        <td class="campo">
          <af:textBox type="date" name="DATPUBBLICAZIONE" title="Data richiesta" value="<%=datPubblicazione%>" size="12" maxlength="10" validateOnPost="true"/>&nbsp;&nbsp;
          Al giorno
          <af:textBox type="date" name="DATSCADENZAPUBBLICAZIONE" title="Data richiesta" value="<%=datScadenzaPubblicazione%>" size="12" maxlength="10" validateOnPost="true" />
        </td>
      </tr>

      <tr ><td colspan="4" ><hr width="90%"/></td></tr>      

      <tr>
        <td class="etichetta">Numero</td>
        <td class="campo">
          <input type="text" name="NUMRICHIESTA" value="<%=numRichiesta%>" size="6" maxlength="8"/>
        </td>
        <td class="etichetta">Anno</td>
        <td class="campo">
          <input type="text" name="ANNO" value="<%=anno%>" size="4" maxlength="4"/>
        </td>
      </tr>

      <tr ><td colspan="4" ><hr width="90%"/></td></tr>
      <!-- Mansione -->
      <tr>
        <td class="etichetta">Codice mansione</td>
        <td class="campo">
          <af:textBox 
            classNameBase="input" 
            name="CODMANSIONE" 
            size="7" 
            maxlength="7"
            value="<%=codMansione%>"
          />
      
          <af:textBox 
            type="hidden" 
            name="codMansioneHid" 
          />
      
          <a href="javascript:selectMansione_onClick(Frm1.CODMANSIONE, Frm1.codMansioneHid, Frm1.DESCMANSIONE,  Frm1.strTipoMansione);"><img src="../../img/binocolo.gif" alt="Cerca"></A>&nbsp;&nbsp;
          <A href="javascript:ricercaAvanzataMansioni();">Ricerca avanzata</A>
        </td>
      </tr>           
      <tr valign="top">
          <td class="etichetta">Tipo</td>
          <td class="campo">
            <af:textBox type="hidden" name="CODTIPOMANSIONE" value="" />
            <af:textBox classNameBase="input" name="strTipoMansione" value="<%=strTipoMansione%>" readonly="true" size="48" />
          </td>
        </tr>
      <tr>
        <td class="etichetta">Mansione</td>
        <td class="campo">
            <af:textArea cols="60" 
                         rows="2" 
                         title="Mansione"
                         name="DESCMANSIONE" 
                         classNameBase="textarea"
                         readonly="true" 
                         maxlength="100"
                         value="<%=descMansione%>" />
        </td>
      </tr>
      <!-- Fine Mansione -->
      <tr ><td colspan="4" ><hr width="90%"/></td></tr>
      <tr >
	      <td class="etichetta">Categoria CM</td>
	      <td class="campo">
	      	  <af:comboBox classNameBase="input" 
		      				name="codMonoCMcatPubb" 
		      				title="Categoria CM"
		      				required="false"
		      				disabled="false">
		      	<option value="" <% if ( "".equalsIgnoreCase(codMonoCMcatPubb) ) { %>SELECTED="true"<% } %>></option>
		      	<option value="D" <% if ( "D".equalsIgnoreCase(codMonoCMcatPubb) ) { %>SELECTED="true"<% } %> >Disabili</option> 
		      	<option value="A" <% if ( "A".equalsIgnoreCase(codMonoCMcatPubb) ) { %>SELECTED="true"<% } %>>Categoria protetta ex. Art. 18</option> 
		      	<option value="E" <% if ( "E".equalsIgnoreCase(codMonoCMcatPubb) ) { %>SELECTED="true"<% } %>>Entrambi</option> 
		      </af:comboBox>
	      </td>
      </tr>
      <tr ><td colspan="4" ><hr width="90%"/></td></tr>
      <tr ><td colspan="4" >Utente di inserimento</td></tr>      
      <tr ><td colspan="4" >
          <input type="radio" name="utente" value="1" <%if(utente.equals("1")){out.print("checked");}%>> Mie
          <input type="radio" name="utente" value="2" <%if(utente.equals("2")){out.print("checked");}%>> Mio gruppo
          <input type="radio" name="utente" value="" <%if(utente.equals("")){out.print("checked");}%>> Tutte
      </td></tr>      
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
         <br/><br/><br/>
</af:form>
<%out.print(htmlStreamBottom);%>
</body>
</html>
