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
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	
	//Oggetti per l'applicazione dello stile grafico
	String htmlStreamTop = StyleUtils.roundTopTable(true);
	String htmlStreamBottom = StyleUtils.roundBottomTable(true);
	
	//Inizializzazione variabili
	String strCodiceFiscale = "";
	String strRagioneSociale = "";
	String strPartitaIva = "";
	String strFlgDatiOk = "";
	String codNatGiuridica = "";
	String codTipoAzienda = "";
	String strSitoInternet = "";
	String strDescAttivita = "";
	String numSoci = "";
	String numDipendenti = "";
	String numCollaboratori = "";
	String numAltraPosizione = "";
	String patInail1 = "";
	String patInail2 = "";
	String patInail = "";
	String strRepartoInail = "";
	String strNumAlboInterinali = "";
	String datInizio = "";
	String datFine = "";
	String strHistory = "";	
	String strNote = "";
	String prgMovimentoApp = "";
	String prgMobilitaApp = "";
	
	prgMovimentoApp = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGMOVIMENTOAPP");
	prgMobilitaApp = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGMOBILITAISCRAPP");
	String funz_agg = StringUtils.getAttributeStrNotNull(serviceRequest, "FUNZ_AGGIORNAMENTO");
	String context = StringUtils.getAttributeStrNotNull(serviceRequest, "CONTEXT");
		
	//Mi dice se sto operando per l'azienda principale o per quella utilizzatrice
	String contesto = StringUtils.getAttributeStrNotNull(serviceRequest, "CONTESTO");
	String daMovimentiNew = StringUtils.getAttributeStrNotNull(serviceRequest, "daMovimentiNew");
	boolean contestoAzienda = contesto.equalsIgnoreCase("AZIENDA");
	boolean contestoAzUtil = contesto.equalsIgnoreCase("AZUTIL");
	
	//Indica se è fallito l'inserimento della testata
	String erroreTestata = (String) serviceResponse.getAttribute("M_InsertTestataAzienda.ERR_TESTATA");
	boolean inserimentoFallito = (erroreTestata != null);
	
	if (inserimentoFallito) {
		//In questo caso i dati li riprendo dalla request
		strCodiceFiscale = StringUtils.getAttributeStrNotNull(serviceRequest, "strCodiceFiscale");
		strRagioneSociale = StringUtils.getAttributeStrNotNull(serviceRequest, "strRagioneSociale");
		strPartitaIva = StringUtils.getAttributeStrNotNull(serviceRequest, "strPartitaIva");
		strFlgDatiOk = StringUtils.getAttributeStrNotNull(serviceRequest, "FLGDATIOK");
		codNatGiuridica = StringUtils.getAttributeStrNotNull(serviceRequest, "codNatGiuridica");
		codTipoAzienda = StringUtils.getAttributeStrNotNull(serviceRequest, "codTipoAzienda");
		strSitoInternet = StringUtils.getAttributeStrNotNull(serviceRequest, "strSitoInternet");
		strDescAttivita = StringUtils.getAttributeStrNotNull(serviceRequest, "strDescAttivita");
		numSoci = StringUtils.getAttributeStrNotNull(serviceRequest, "numSoci");
		numDipendenti = StringUtils.getAttributeStrNotNull(serviceRequest, "numDipendenti");
		numCollaboratori = StringUtils.getAttributeStrNotNull(serviceRequest, "numCollaboratori");
		numAltraPosizione = StringUtils.getAttributeStrNotNull(serviceRequest, "numAltraPosizione");
		patInail = StringUtils.getAttributeStrNotNull(serviceRequest, "STRPATINAIL");
		strRepartoInail = StringUtils.getAttributeStrNotNull(serviceRequest, "strRepartoInail");
		strNumAlboInterinali = StringUtils.getAttributeStrNotNull(serviceRequest, "strNumAlboInterinali");
		datInizio = StringUtils.getAttributeStrNotNull(serviceRequest, "datInizio");
		datFine = StringUtils.getAttributeStrNotNull(serviceRequest, "datFine");
		strHistory = StringUtils.getAttributeStrNotNull(serviceRequest, "strHistory");		
		strNote = StringUtils.getAttributeStrNotNull(serviceRequest, "strNote");
	} else {
		//Non ho ancora provato ad inserire, i dati mi arrivano dal movimenti sulla tabella di appoggio del DB
		SourceBean dataOrigin = null;
		if (!prgMobilitaApp.equals("")) {
			dataOrigin = (SourceBean) serviceResponse.getAttribute("M_MobilitaGetDettaglioMobApp.ROWS.ROW");
			if (contestoAzienda && dataOrigin != null) {	
				//Recupero quelli dell'azienda principale
				strCodiceFiscale = StringUtils.getAttributeStrNotNull(dataOrigin, "STRAZCODICEFISCALE");
				strRagioneSociale = StringUtils.getAttributeStrNotNull(dataOrigin, "STRAZRAGIONESOCIALE");
				strPartitaIva = StringUtils.getAttributeStrNotNull(dataOrigin, "STRAZPARTITAIVA");
				codTipoAzienda = StringUtils.getAttributeStrNotNull(dataOrigin, "CODAZTIPOAZIENDA");
				strNumAlboInterinali = StringUtils.getAttributeStrNotNull(dataOrigin, "STRAZNUMALBOINTERINALI");
			}
		}
		else {
			dataOrigin = (SourceBean) serviceResponse.getAttribute("M_MovGetDettMovApp.ROWS.ROW");
			if (contestoAzienda && dataOrigin != null) {
				//Recupero quelli dell'azienda principale
				strCodiceFiscale = StringUtils.getAttributeStrNotNull(dataOrigin, "strCodiceFiscaleAz");
				strRagioneSociale = StringUtils.getAttributeStrNotNull(dataOrigin, "strRagioneSocialeAz");
				strPartitaIva = StringUtils.getAttributeStrNotNull(dataOrigin, "strPartitaIvaAz");
				codTipoAzienda = StringUtils.getAttributeStrNotNull(dataOrigin, "codTipoAzienda");
				numDipendenti = StringUtils.getAttributeStrNotNull(dataOrigin, "NUMDIPENDENTIAz");
				patInail = StringUtils.getAttributeStrNotNull(dataOrigin, "STRPATINAIL");
				strNumAlboInterinali = StringUtils.getAttributeStrNotNull(dataOrigin, "strNumAlboInterinali");
			} else if (contestoAzUtil && dataOrigin != null) {
				//Recupero quelli dell'azienda utilizzatrice
				strCodiceFiscale = StringUtils.getAttributeStrNotNull(dataOrigin, "strCodiceFiscaleAzUtil");
				strRagioneSociale = StringUtils.getAttributeStrNotNull(dataOrigin, "strRagioneSocialeAzUtil");
				strPartitaIva = StringUtils.getAttributeStrNotNull(dataOrigin, "strPartitaIvaAzUtil");
				codTipoAzienda = StringUtils.getAttributeStrNotNull(dataOrigin, "codTipoAziendaUtil");
				numDipendenti = StringUtils.getAttributeStrNotNull(dataOrigin, "NUMAZINTDIPENDENTI");			
			}
		}	
	}
	
	//Gestione spezzatino Pat Inail
	patInail1 = patInail.substring(0, (patInail.length() >= 8 ? 8 : patInail.length()));
	patInail2 = patInail.substring((patInail.length() >= 8 ? 8 : patInail.length()), (patInail.length() >= 10 ? 10 : patInail.length()));
	 
%>
<html>

<head>
  <title>Testata Azienda</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
   
<SCRIPT TYPE="text/javascript">
<!--

function unificaPatInail() {
  document.Frm1.STRPATINAIL.value = document.Frm1.STRPATINAIL1.value + document.Frm1.STRPATINAIL2.value;
  return true;
}


function selectATECO_onClick(codAteco, codAtecoHid, strAteco, strTipoAteco) {	
  if (codAteco.value==""){
    strAteco.value="";
    strTipoAteco.value=""      
  }
  else { 
    if (codAteco.value!=codAtecoHid.value) {
      window.open("AdapterHTTP?PAGE=RicercaAtecoPage&codAteco="+codAteco.value, "Attività", 'toolbar=0, scrollbars=1'); 
    }
  }
}

function ricercaAvanzataAteco() {
  window.open("AdapterHTTP?PAGE=RicercaAtecoAvanzataPage", "Attività", 'toolbar=0, scrollbars=1');
}

function checkDataInzioFine(inputName) {
  var objDataInizio = eval("document.forms[0].datInizio");
  var objDataFine=  eval("document.forms[0].datFine");
  strDataInizio=objDataInizio.value;
  strDataFine=objDataFine.value;
  ok=true;

  if (strDataInizio != "" && strDataFine != "") {
    //costruisco la data di inizio
    dIgiorno=parseInt(strDataInizio.substr(0,2),10);
    dImese=parseInt(strDataInizio.substr(3, 2),10)-1; //il conteggio dei mesi parte da zero :P
    dIanno=parseInt(strDataInizio.substr(6,4),10);
    dataInizio=new Date(dIanno, dImese, dIgiorno);

    //costruisce la data di fine
    dFgiorno=parseInt(strDataFine.substr(0,2),10);
    dFmese=parseInt(strDataFine.substr(3,2),10)-1;
    dFanno=parseInt(strDataFine.substr(6,4),10);
    dataFine=new Date(dFanno, dFmese, dFgiorno);
  
    ok=true;
    if (dataFine < dataInizio) {
        alert("La data di inizio attività è successiva alla data di fine attività");
        document.forms[0].datFine.focus();
        ok=false;
    }
  } else if (strDataInizio=="" && strDataFine!="") {
      alert("E' stata specificata la data di fine attività ma non quella di inizio");
      document.forms[0].datInizio.focus();
      ok=false;
  }
  return ok;
}


-->

</SCRIPT>

</head>

<body class="gestione">

<h2>Testata Azienda</h2>

<p>
  <font color="green"><af:showMessages prefix="M_SaveTestataAzienda"/></font>
  <font color="red"><af:showErrors /></font>
</p>

<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="unificaPatInail()">
<%out.print(htmlStreamTop);%>

<table class="main">
<tr valign="top">
  <td class="etichetta">Codice fiscale</td>
  <td class="campo">
    <af:textBox classNameBase="input" name="strCodiceFiscale" title="Codice Fiscale" size="20" value="<%=strCodiceFiscale%>" maxlength="16" required="true"/>
  </td>
</tr>

<tr valign="top">
  <td class="etichetta">Partita Iva </td>
  <td class="campo">
    <af:textBox classNameBase="input" name="strPartitaIva" title="Partita Iva" size="13" value="<%=strPartitaIva%>" maxlength="11"/>
    &nbsp;&nbsp;
    Validità C.F. / P. IVA&nbsp;
    <af:comboBox 
      name="FLGDATIOK"
      classNameBase="input"
      disabled="false">
      <option value=""  <% if ( "".equalsIgnoreCase(strFlgDatiOk) )  { %>SELECTED="selected"<% } %> ></option>
      <option value="S" <% if ( "S".equalsIgnoreCase(strFlgDatiOk) ) { %>SELECTED="selected"<% } %> >Sì</option>
      <option value="N" <% if ( "N".equalsIgnoreCase(strFlgDatiOk) ) { %>SELECTED="selected"<% } %> >No</option>
    </af:comboBox>
  </td>
</tr>

<tr valign="top">
  <td class="etichetta">Ragione Sociale</td>
  <td class="campo">
    <af:textBox classNameBase="input" name="strRagioneSociale" title="Ragione Sociale" size="50" maxlength="100" value="<%=strRagioneSociale%>" required="true"/>
  </td>
</tr>

<tr valign="top">
  <td class="etichetta">Natura Giuridica</td>
  <td class="campo">
    <af:comboBox classNameBase="input" name="codNatGiuridica" title="Natura Giuridica" moduleName="M_GETTIPINATGIURIDICA" addBlank="true" selectedValue="<%=codNatGiuridica%>"/>
  </td>
</tr>

<tr valign="top">
  <td class="etichetta">Tipo di azienda</td>
  <td class="campo">
    <af:comboBox classNameBase="input" name="codTipoAzienda" title="Tipo di Azienda" moduleName="M_GETTIPIAZIENDA" addBlank="true" required="true" selectedValue="<%=codTipoAzienda%>"/>
  </td>
</tr>

<tr valign="top">
  <td class="etichetta">Sito Internet</td>
  <td class="campo">
    <af:textBox classNameBase="input" name="strSitoInternet" title="Sito Internet" size="50" maxlength="100" value="<%=strSitoInternet%>"/>
  </td>
</tr>

<tr valign="top">
  <td class="etichetta">Descrizione dell'attività</td>
  <td class="campo">
    <af:textArea classNameBase="textarea" name="strDescAttivita" title="Descirizone dell'Attività" cols="50" value="<%=strDescAttivita%>" maxlength="100"/>
  </td>
</tr>


<tr>
  <td colspan="2"><div class="sezione2">Organico</div></td>
</tr>

<tr>
  <td colspan="2">
    <table class="main">
      <tr valign="top">
        <td class="etichetta">Numero di soci</td>
        <td>
        <af:textBox classNameBase="input" name="numSoci" title="Numero di Soci" size="5" maxlength="38" value="<%=numSoci%>"/>
        </td>
        <td class="etichetta">Numero di dipendenti</td>
        <td>
          <af:textBox classNameBase="input" name="numDipendenti" title="Numero di Dipendenti" size="5" maxlength="38" value="<%=numDipendenti%>"/>
        </td>
      </tr>
      <tr valign="top">
        <td class="etichetta">Numero di collaboratori</td>
        <td>
          <af:textBox classNameBase="input" name="numCollaboratori" size="5" maxlength="38" value="<%=numCollaboratori%>"/>
        </td>
        <td class="etichetta">Numero di personale in altre posizioni</td>
        <td>
          <af:textBox classNameBase="input" name="numAltraPosizione" size="5" maxlength="38" value="<%=numAltraPosizione%>"/>
        </td>
      </tr>
    </table>
  </td>
</tr>

<tr>
  <td colspan="2"><div class="sezione2">Altre Informazioni</div></td>
</tr> 

<tr>
  <td colspan="2">
    <table class="main">
      <tr valign="top">      
        <td class="etichetta">PAT INAIL</td>
        <td nowrap>
          <af:textBox classNameBase="input" type="integer" name="STRPATINAIL1" title="Pat INAIL" size="10" maxlength="8" value="<%=patInail1%>"/> - 
          <af:textBox classNameBase="input" type="integer" name="STRPATINAIL2" title="Pat INAIL" size="3" maxlength="2" value="<%=patInail2%>"/>
          <input type="hidden" name="STRPATINAIL" value="<%=patInail%>">
        </td>   
        <td class="etichetta">Reparto INAIL</td>
        <td>
          <af:textBox classNameBase="input" name="strRepartoInail" title="Reparto INAIL" size="50" maxlength="100" value="<%=strRepartoInail%>"/>
        </td>
      </tr>
      <tr valign="top">        
        <td class="etichetta">Numero albo imprese interinali</td>
        <td colspan="5">
          <af:textBox classNameBase="input" name="strNumAlboInterinali" title="Numero albo imprese interinali" size="100" value="<%=strNumAlboInterinali%>"/>
        </td>
      </tr>    
    </table>
  </td>
</tr>

<tr>
  <td colspan="2"><div class="sezione2">&nbsp;</div></td>
</tr> 

<tr valign="top">
  <td colspan="2">
    <table class="main">
      <tr>
        <td class="etichetta">Data di inizio dell'attività</td>
        <td>
          <af:textBox classNameBase="input" type="date" title="Data di inizio dell'attività" name="datInizio" size="10" maxlength="10" value="<%=datInizio%>" validateWithFunction="checkDataInzioFine" validateOnPost="true"/>
        </td>
        <td class="etichetta">Data di fine dell'attività</td>
        <td>
          <af:textBox classNameBase="input" type="date" title="Data di fine dell'attività" name="datFine" size="10" maxlength="10" value="<%=datFine%>" validateOnPost="true"/>
        </td>
      </tr>
    </table>
  </td>
</tr>
<tr valign="top">
  <td class="etichetta">History</td>
  <td class="campo">
    <af:textArea classNameBase="textarea" name="strHistory" cols="50" value="<%=strHistory%>" maxlength="2000" />
  </td>
</tr>
<tr valign="top">
  <td class="etichetta">Note</td>
  <td class="campo">
     <af:textArea classNameBase="textarea" name="strNote" cols="50" maxlength="1000" value="<%=strNote%>"/>
  </td>
</tr>
</table>
<br>

<input class="pulsante" type="submit" name="inserisciTestata" value="Inserisci Testata"/>
<%out.print(htmlStreamBottom);%>

<center>
<br>
<input type="button" class="pulsanti" value="Chiudi" onClick="window.close();"/>
</center>
<input type="hidden" name="PAGE" value="MovimentiUnitaAziendaPage">
<input type="hidden" name="CONTESTO" value="<%=contesto%>">
<input type="hidden" name="daMovimentiNew" value="<%=daMovimentiNew%>">
<%if (!prgMovimentoApp.equals("")) {%>
<input type="hidden" name="PRGMOVIMENTOAPP" value="<%=prgMovimentoApp%>">
<input type="hidden" name="CONTEXT" value="<%=context%>"/>
<%}
else {%>
<input type="hidden" name="PRGMOBILITAISCRAPP" value="<%=prgMobilitaApp%>">
<%}%>
<input type="hidden" name="FUNZ_AGGIORNAMENTO" value="<%=funz_agg%>">
</af:form>
</body>
</html>