<%--
	aggiunto parametro PAGE_DA_CHIAMARE (pageDaChiamare):  e' la page che chiamera' la action. 
		La page di default rimane REPORTFRAMEPAGE
	aggiunto parametro FORZA_PROTOCOLLAZIONE (forzaProtocollazione): viene forzata la protocollazione senza la 
		possibilità di disattivarla
--%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.*,
                  it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<head>
<title>Gestione documenti</title>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">

<af:linkScript path="../../js/"/>

<% 
   String strIO = "";
   String strNumProt = "";
   String strAnnoProt = "";
   String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
   String rptAction     = (String) serviceRequest.getAttribute("rptAction");
   String parametri     = (String) serviceRequest.getAttribute("parametri");
   String tipoDoc       = (String) serviceRequest.getAttribute("tipoDoc");
   // si decide quale page chiamare, se va chiamata
   String pageDaChiamare = (String)serviceRequest.getAttribute("PAGE_DA_CHIAMARE");
   String forzaProtocollazione = (String)serviceRequest.getAttribute("FORZA_PROTOCOLLAZIONE");
   
   String strCheckDB = (String)serviceRequest.getAttribute("CHECKDB")==null?"":(String)serviceRequest.getAttribute("CHECKDB");
   boolean checkedDB = strCheckDB.equals("true")?true:false;
   
   String rptInPopup = (String)serviceRequest.getAttribute("RPT_IN_POPUP");
   boolean  forzaSalvaDB = false;
   pageDaChiamare = pageDaChiamare!=null && pageDaChiamare.length()>0 ? pageDaChiamare:"REPORTFRAMEPAGE";
   Vector rows = null;
   String disabilitaProtocollazione = "";
    //Reperisco la data e ora corrente
    Calendar oggi = Calendar.getInstance();
    
    String giorno = Integer.toString(oggi.get(Calendar.DATE));    if(giorno.length()<=1) { giorno = "0" + giorno; }
    String mese   = Integer.toString(oggi.get(Calendar.MONTH)+1); if(mese.length()<=1)   { mese   = "0" + mese; }
    String anno   = Integer.toString(oggi.get(Calendar.YEAR));    if(anno.length()<=1)   { anno   = "0" + anno; }
    String dataProt = giorno +"/"+ mese +"/"+ anno; 
    
    String ora = Integer.toString(oggi.get(Calendar.HOUR_OF_DAY)); if(ora.length()<=1)    { ora    = "0" + ora; }
    String minuti = Integer.toString(oggi.get(Calendar.MINUTE));   if(minuti.length()<=1) { minuti = "0" + minuti; }
    String oraProt = ora + ":" + minuti;


   //Reperisco il tipi di documento che si vuole gestire (Curiculum, scheda anagrafica,...)
   String nomeDoc = null;  //(String) serviceRequest.getAttribute("nomeDoc");
   String docRif  = null; 
   rows = serviceResponse.getAttributeAsVector("M_GetTipoDoc.ROWS.ROW");
   if(rows != null && !rows.isEmpty())
   { SourceBean row = (SourceBean) rows.elementAt(0);
     nomeDoc  = (String) row.getAttribute("DESCRIZIONE");
     docRif   = (String) row.getAttribute("RIFERIMENTO");
   }

   rows = serviceResponse.getAttributeAsVector("M_GetValoreDoc.ROWS.ROW");
   if(rows != null && !rows.isEmpty())
   { SourceBean row = (SourceBean) rows.elementAt(0);
     strIO  = (String) row.getAttribute("STRIO");
   }

   //Reperisco le informazioni necessarie alla protocollazione
   rows = serviceResponse.getAttributeAsVector("M_GetProtocollazione.ROWS.ROW");
   String prAutomatica     = null; 
   String estReportDefautl = null;
   BigDecimal numProt      = null;
   BigDecimal numAnnoProt  = null;
   BigDecimal kLockProt    = null;
   //La linea di codice successiva è provvisoria in attesa che venga creata la tabella in cui reperire quali sono 
   //i documenti che devono obbligatoriamente protocollati
   boolean prObbligatoria  = (tipoDoc.equalsIgnoreCase("IM") || tipoDoc.equalsIgnoreCase("PT297") || tipoDoc.equalsIgnoreCase("ACLA") || 
                              tipoDoc.equalsIgnoreCase("SA") || tipoDoc.equalsIgnoreCase("SAP"))
                              || tipoDoc.equalsIgnoreCase("TRCPI")  || tipoDoc.equalsIgnoreCase("TRDOC")
                              || tipoDoc.equals("DICH") || tipoDoc.equals("ATT") 
                              || tipoDoc.equals("ALMOBO") ? true : false;
   // Si vuole che il documento sia salvato obbligatoriamente sul db                              
   forzaSalvaDB = tipoDoc.equalsIgnoreCase("DICH") || tipoDoc.equalsIgnoreCase("ATT");
   if (forzaProtocollazione !=null && forzaProtocollazione.equals("true")) {
   	prObbligatoria = true;
   	disabilitaProtocollazione = " disabled='true' ";
   }
   if(rows != null && !rows.isEmpty())
   { SourceBean row = (SourceBean) rows.elementAt(0);
     prAutomatica     = (String) row.getAttribute("FLGPROTOCOLLOAUT");
     estReportDefautl = (String) row.getAttribute("CODTIPOFILEESTREPORT");
     
     if(prAutomatica != null && prAutomatica.equalsIgnoreCase("S")) {
       numProt          = (BigDecimal) row.getAttribute("NUMPROTOCOLLO");
       numAnnoProt      = (BigDecimal) row.getAttribute("NUMANNOPROT");
     } else {
       numAnnoProt      = new BigDecimal(anno);
     }
     
     kLockProt        = (BigDecimal) row.getAttribute("NUMKLOPROTOCOLLO");
   }

   boolean fileEsistente = false;
   BigDecimal prgDoc = null;
   BigDecimal numProtEsist    = null;
   String annoProtEsist   = null;
   rows = serviceResponse.getAttributeAsVector("M_ExistGenericDocument.ROWS.ROW");
   if(rows != null && !rows.isEmpty())
   { SourceBean row = (SourceBean) rows.elementAt(0);
     prgDoc  = (BigDecimal) row.getAttribute("PRGDOCUMENTO");
     numProtEsist   = SourceBeanUtils.getAttrBigDecimal(row, "NUMPROTOCOLLO", null);
     BigDecimal annoP = (BigDecimal) row.getAttribute("NUMANNOPROT");
     annoProtEsist = annoP!=null ? annoP.toString() : "";
     fileEsistente = true;
   }

	int tipoProtocollo =  ProtocolloDocumentoUtil.tipoProtocollo();
	switch(tipoProtocollo) {
		case ProtocolloDocumentoUtil.TIPO_DOCAREA_VER_161:
		case ProtocolloDocumentoUtil.TIPO_DOCAREA_SARE:
			strNumProt  = "";
			strAnnoProt = "";
			break;
		case ProtocolloDocumentoUtil.TIPO_SIL_LOCALE_MANUALE:
			strNumProt  = "";
			strAnnoProt = Utils.notNull(numAnnoProt);
			break;
		case ProtocolloDocumentoUtil.TIPO_SIL_LOCALE_AUTOMATICA:
			strNumProt  = numProt.toString();
			strAnnoProt = numAnnoProt.toString();
			break;
		default:
			throw new Exception("Tipo di protocollazione non gestita: "+tipoProtocollo);
	}

      

    //Servono per gestire il layout grafico
    String htmlStreamTop = StyleUtils.roundTopTable(false);
    String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>


<script language="JavaScript">
<!--
function Protocolla() {
  var myform = window.document.forms['form1'];
  var PR = myform.protocolla.checked;
  if(PR==true) { 
    mostra("datiPerProtocol");
    myform.salvaDB.checked=true;
    myform.salvaDB.disabled=true; 
    //
    myform.salvaInLocale.disabled=true; 
    myform.salvaInLocale.checked=false;
    //
    mostra("apriCHK");
  }
  else {
    nascondi("datiPerProtocol");
  <%if (!forzaSalvaDB) {%>
    myform.salvaDB.checked=false;    
    myform.salvaDB.disabled=false; 
    <%} else {%>
    myform.salvaDB.checked=true;    
    myform.salvaDB.disabled=true;     
    <%}%>
    myform.salvaInLocale.disabled=false; 
  }
}

function apriFileEsistente(PR) {
  var myform = window.document.forms['form1'];
  if (myform.apriFileBlob.checked) {
    myform.protocolla.checked=false;
    myform.protocolla.disabled=true;
    Protocolla();
    myform.salvaDB.checked=false;
    myform.salvaDB.disabled=true;
    myform.apri.checked  = true;
    nascondi("apriCHK");
  }
  else {
    if(PR) myform.protocolla.checked= true;
    myform.protocolla.disabled=false;
    Protocolla();
    mostra("apriCHK");
  }
}

function mostra(id) {
  var div = document.getElementById(id);
  div.style.display="";
}

function nascondi(id) {
  var div = document.getElementById(id);
  div.style.display="none";
}


function nascondiApri() {
  var myform = window.document.forms['form1'];
  if(myform.salvaInLocale.checked) {
    myform.apri.checked  = true;
    nascondi("apriCHK");
  }
  else mostra("apriCHK");
}


function checkAndFormatTime(oraObj) {
  var strTime = oraObj.value;
  var strHours = "";
  var strMin   = "";
  var separator = ":";
  var strTimeArray;

  var titleObj = "ora";
  if(strTime.title != null) { titleObj = strTime.title; }
  
   if (strTime.indexOf(separator) != -1) {
      strTimeArray = strTime.split(separator);
      if (strTimeArray.length != 2) {
         alert("Il campo "+titleObj+" non ha un orario scritto nel formato corretto:\nscrivere HH"+separator+"MM   es: 08"+separator+"12")
         return false;
      }
      else {
       strHours = strTimeArray[0];
       strMin   = strTimeArray[1];
      }
   }
   else if(strTime.length == 4) {
     //Non c'è il separatore, probabilmente è stata inserita un'orario nel formato 1215 -> 12:15
     //che comunque reputiamo valido...
     strHours = strTime.substr(0,2);
     strMin   = strTime.substr(2,4);
   }
   else {
     alert("Il campo "+titleObj+" non ha un orario scritto nel formato corretto:\nscrivere HH"+separator+"MM   es: 08"+separator+"12");
     return false;
   }

   var hours = parseInt(strHours, 10);
   var min   = parseInt(strMin, 10);
   
   if(isNaN(hours)) {
     alert("L'ora inserita non è un numero:\nscrivere HH"+separator+"MM   es: 08"+separator+"12");
     return false;
   }
   if(isNaN(min)) {
     alert("I minuti inseriti non sono un numero:\nscrivere HH"+separator+"MM   es: 08"+separator+"12");
     return false;
   }

   hours = parseInt(strHours, 10);
   min   = parseInt(strMin, 10);
   
   if( hours<0 || hours > 23 ) {
     alert("L'ora inserita non è orario valido.\nInserire un'ora compresa fra 0 e 23");
     return false;
   }
   if( min<0 || min > 59 ) {
     alert("I minuti inseriti non sono corretti.\nInserire un numero compresa fra 0 e 59");
     return false;
   }

	oraObj.value = (hours<10?"0":"") + hours + separator + (min<10?"0":"") + min;
	return true;	  
   
}//end function


function cambiAnnoProt(dataPRObj,annoProtObj) {
  var dataProt = dataPRObj.value;
  var lun = dataProt.length;

  <%-- Stiamo modificando la data di protocollazione. Quindi cambia anche l'anno di protocollazione --%>
  annoProtObj.value = ""; 
  
  if (lun > 5) {
    var tmpDate = new Object();
    tmpDate.value = dataProt;
    if ( checkFormatDate(tmpDate) ) {
       annoProtObj.value = tmpDate.value.substr(6,10);      
    }
    else if (lun==8 || lun==10) {
      alert("La data di protocollazione non è corretta");
    }
  }
} //end function


  function apriStampa(RPT) {
    //RPT: attributo name del tag ACTION nel file action.xml relativo al report da visualizzare
    var urlpage;
    var myform = window.document.forms['form1'];
    
    var salvaPC = myform.salvaInLocale.checked;
    var apri = myform.apri.checked;
    
    disabilitaBottone();
    
	if (salvaPC || !apri) {
    	// il documento viene aperto in un'altra finestra (perchè salvato in locale sul PC), 
		// o non si vuole visualizzarlo. 
		// Non occorre allora "creare il frameset" che permette il ritorno indietro
		urlpage = "AdapterHTTP?ACTION_NAME="+RPT +
							  "&asAttachment=true";
	}
	else {
		// viene aperto nella pagina chiamante: "creiamo il frameset" che permette il ritorno indietro
		urlpage="AdapterHTTP?PAGE=<%=pageDaChiamare%>" +
							"&ACTION_REDIRECT=" + RPT +
							"&asAttachment=false";
	}

	if ((myform.apriFileBlob != null) && myform.apriFileBlob.checked)  {
		urlpage += "&apriFileBlob=true&prgDocumento=" + myform.prgDocumento.value;
	}
	else {
		if ((myform.protocolla != null) && myform.protocolla.checked) {
			//alert("Doc. Protocollabile");

			if (! checkFormatDate(myform.dataProt)) {
				<%-- La funzione 'checkFormatDate()' è contenuta nel file FormCheck.js --%>
				alert("La data di protocollazione non è corretta");
				return;
			}
			if (! checkAndFormatTime(myform.oraProt)) {
				return;
			}
			
			var protLocaleAutomatica = <%= tipoProtocollo==ProtocolloDocumentoUtil.TIPO_SIL_LOCALE_AUTOMATICA %>;
			if (protLocaleAutomatica && (myform.numProt.value == "" || myform.annoProt.value == "")) {
				alert("I campi: \n\n- \"numero protocollo\" e      \n- \"anno\"\n\nsono obbligatori");
				return;
			}
				urlpage +=  "&numProt=" + myform.numProt.value +
							"&annoProt=" + myform.annoProt.value +
							"&dataOraProt=" + myform.dataProt.value + " " + myform.oraProt.value;
				<% if ((prAutomatica != null) && (!prAutomatica.equals(""))) {%>
					urlpage += "&protAutomatica=<%=prAutomatica%>";
				<% } else { %>
					alert("Impossibile procedere.Errore interno:\n-il tipo di protocollazione non è stato specificato");
				<% } %>
				<%-- urlpage += "&kLockProt=<%=kLockProt%>"; il controllo di concorrenza viene fatto direttamente in Documento --%>
			

			if ((myform.docInOut == null) || (myform.docInOut.value == "")) {
				alert("Specificare se il documento è di Input o Output");
				return;
			}
		}

		// GG 18/02/2005 - il valore di "codMonoIO" lo metto sempre! Sia che protocollo o meno
		if ((myform.docInOut == null) || (myform.docInOut.value == "")) {
			urlpage += "&docInOut=<%= strIO %>";
		} else {
			urlpage += "&docInOut=" + myform.docInOut.value;
		}

		urlpage+="&salvaDB=" + myform.salvaDB.checked;
	}
	
	urlpage += "&tipoFile=" + myform.tipoFileEXT.value
	
	var param = location.search;
	var i = param.indexOf("&");
	param = param.substr(i, param.length);
	<%--
	alert("URL_1:: "+location.search+"\nURL_2:: "+param)
	param = param.replace(/\?/gi,"");
	param = param.replace(/PAGE=GestioneStatoDocPage/gi,"");
	--%>
	urlpage += param;
	//alert("URL"+urlpage);
	if (myform.apri.checked) {
		urlpage += "&apri=true";
		// NB: qui l'uso del "setOpenerWindowLocation(urlpage)" presenta
		// il seguente problema: dopo il salvataggio si rimanere col
		// bottone STAMPA disabilitato nella pagina "opener"!
		// Perciò passo il secondo parametro a FALSE: per non fare la "prepareSubmit"
		<% if ("true".equals(rptInPopup)) {	%>
		<%-- 28/8/2006 Savino: il report viene aperto	in una popup e non nella pagina opener. --%>
		window.open(urlpage, '_blank', 'status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=YES,height=400,width=600');
		<%}	else {%>
		setOpenerWindowLocation(urlpage, false);
		<%}%>
		window.close(); //chiude la PopUp
	}
	else {
		urlpage += "&apri=false";
		<%--alert("NON apri"+urlpage);--%>
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
		setWindowLocation(urlpage);
	}
    
  }//apriStampa(_,_,_)


  function disabilitaBottone() {
	document.form1.prosegui.disabled=true;	
	//document.Frm1.insert_dispo.disabled=false;	
	return true;	
  }

//-->
</script>



</head>
<body class="gestione">

<af:form name="form1" method="POST" action="AdapterHTTP">
<br/>
<p align="center">
<%out.print(htmlStreamTop);%>
<table class="main" border="0">

<tr><td><br/></td></tr>
<tr><td class="azzurro_bianco" colspan="2">
    <b><%=nomeDoc%></b>
    </td>
</tr>

<tr><td width="5%">&nbsp;</td><td>&nbsp;</td></tr>
<tr>
    <td valign="top"><img name="timbro" alt="Protocolla" src="../../img/timbro.gif"></td>
    <td>
        <table cellpadding="0" cellspacing="0" border="0" width="100%">
            <tr>
            <%if ((tipoDoc.equals("STMOV")) || (tipoDoc.equals("PRM")) || (tipoDoc.equals("RAP")) || (tipoDoc.equals("RGR")) || (tipoDoc.equals("PUB")) 
            	|| (tipoDoc.equals("PUB_A")) || (tipoDoc.equals("PUB_G")) || tipoDoc.equals("SLCOOP") || tipoDoc.equals("ALSEAO") || tipoDoc.equals("L68_ETA") || tipoDoc.equals("L68_MOV") 
            	|| tipoDoc.equals("NULOST")  || tipoDoc.equals("L68_AZI") || tipoDoc.equals("PROF_DIA_RURER") 
            	|| tipoDoc.equals("NOF") ) {
            //documenti che non possono essere protocolati %>
              <td width="20%"><input name="protocolla" type="checkbox" disabled="true"/>Protocollare</td>
            <%
            }
            else {
              if(prObbligatoria){%>
                  <td width="20%" valign="top"><input name="protocolla" type="checkbox" onClick="Protocolla()" checked <%=disabilitaProtocollazione%>/>Protocollare</td>
                  <td><div align="left" id="datiPerProtocol" style="display:">
              <%} else {%>
                  <td width="20%" valign="top"><input name="protocolla" type="checkbox" onClick="Protocolla()"/>Protocollare</td>
                  <td><div align="left" id="datiPerProtocol" style="display:none">
              <%}%>
              <% if(!ProtocolloDocumentoUtil.protocollazioneLocale()){ %>
					<input type="hidden" name="annoProt" value="<%= strAnnoProt %>" >
              		<input type="hidden" name="numProt"  value="<%= strNumProt %>" >
              		<input type="hidden" name="dataProt" value="<%= dataProt %>" >
              		<input type="hidden" name="oraProt"  value="<%= oraProt %>" >
                    <table cellpadding="0" cellspacing="0" border="0" width="100%">
						<!--<tr><td colspan="2">Protocollazione DOCAREA...<br/>&nbsp;</td></tr>-->
                      	<tr><td nowrap>Doc. di 
                              <af:textBox type="hidden" name="docInOut" value="<%=strIO%>"/>
                              <af:comboBox classNameBase="input" name="DocDiIn" title="documento di input/output" 
                                disabled="true" required="false" addBlank="false" selectedValue="<%=strIO%>">
                                <option value="O" <% if((strIO!=null) && strIO.equals("I")){%>selected="selected"<%}%>>Input</option>
                                <option value="I" <% if((strIO!=null) && strIO.equals("O")){%>selected="selected"<%}%>>Output</option>              
                              </af:comboBox>&nbsp;*
                          </td>
                          <td align="left" nowrap >Rif.
                             <af:textBox name="docRif" value="<%=docRif%>" size="<%=docRif.length()+10%>" maxlength="100"
                                  title="riferimento"  classNameBase="input" readonly="true" validateOnPost="false" 
                                  required="false" trim ="false"
                              />&nbsp;*</td>
                      	</tr>
					</table>
              <% } else 
              if(prAutomatica != null && prAutomatica.equalsIgnoreCase("S")){%>
                  <table cellpadding="0" cellspacing="0" border="0" width="100%">
                      <tr>
                          <td>anno 
                             <af:textBox name="annoProt" type="" value="<%=strAnnoProt%>" size="5" maxlength="38"
                                  title="Anno di protocollazione" classNameBase="input" readonly="true" validateOnPost="false" 
                                  required="false" trim ="false"
                              />&nbsp;*</td>
                          <td align="right">numero
                             <af:textBox name="numProt" type="" value="<%=strNumProt %>" size="7" maxlength="100"
                                  title="Numero di protocollo"  classNameBase="input" readonly="true" validateOnPost="false" 
                                  required="false" trim ="false"
                              />&nbsp;*</td>
                          <td align="right">data
                             <af:textBox name="dataProt" type="date" value="<%=dataProt%>" size="11" maxlength="10"
                                  title="data di protocollazione"  classNameBase="input" readonly="true" validateOnPost="true" 
                                  required="false" trim ="false"
                              />&nbsp;*</td>
                          <td align="right">ora
                             <af:textBox name="oraProt" type="time" value="<%=oraProt%>" size="6" maxlength="5"
                                  title="ora di protocollazione"  classNameBase="input" readonly="true" validateOnPost="false" 
                                  required="false" trim ="false"
                              />&nbsp;*</td>
                      </tr>
                      <tr><td colspan="4">(In caso di concorrenza, il num di protocollo proposto potrebbe essere differente da quello inserito nel documento)<br/>&nbsp;</td></tr>
                      <tr><td nowrap>Doc. di 
                              <af:textBox type="hidden" name="docInOut" value="<%=strIO%>"/>
                              <!--
                              <af:textBox name="DocDiIn" value="Input" size="6" maxlength="6"
                                  title="documento di input/output"  classNameBase="input" readonly="true" validateOnPost="false" 
                                  required="false" trim ="false"
                              />-->
                              <af:comboBox classNameBase="input" name="DocDiIn" title="documento di input/output" 
                                disabled="true" required="false" addBlank="false" selectedValue="<%=strIO%>">
                                <option value="O" <% if((strIO!=null) && strIO.equals("I")){%>selected="selected"<%}%>>Input</option>
                                <option value="I" <% if((strIO!=null) && strIO.equals("O")){%>selected="selected"<%}%>>Output</option>              
                              </af:comboBox>&nbsp;*
                          </td>
                          <td colspan="3" align="left" nowrap>Rif.
                             <af:textBox name="docRif" value="<%=docRif%>" size="<%=docRif.length()+10%>" maxlength="100"
                                  title="riferimento"  classNameBase="input" readonly="true" validateOnPost="false" 
                                  required="false" trim ="false"
                              />&nbsp;*</td>
                      </tr>
                  </table>
              </div>
          </td>
        <%} else if (prAutomatica != null && prAutomatica.equalsIgnoreCase("N") && ProtocolloDocumentoUtil.protocollazioneLocale()){%>
            <table cellpadding="0" cellspacing="0" border="0" width="100%">
                <tr>
                    <td align="right">anno 
                       <af:textBox name="annoProt" type="" value="<%=strAnnoProt %>" size="5"
                              title="Anno di protocollazione"  classNameBase="input" readonly="true" validateOnPost="false" 
                              required="false" trim ="false"
                        />&nbsp;*
                    </td>
                   <td align="right">numero di protocollo 
                       <af:textBox name="numProt" type="" value="" size="7"
                               title="Numero di protocollo"  classNameBase="input" readonly="false" validateOnPost="false" 
                               required="false" trim ="false"
                        />&nbsp;*
                    </td>
                      <td align="right">data
                         <af:textBox name="dataProt" type="date" value="<%=dataProt%>" size="11" maxlength="10"
                              title="data di protocollazione"  classNameBase="input" readonly="false" validateOnPost="true" 
                              required="false" trim ="false" onKeyUp="cambiAnnoProt(this,annoProt)" onBlur="checkFormatDate(this)"
                          />&nbsp;*</td>
                      <td align="right">ora
                         <af:textBox name="oraProt" type="time" value="<%=oraProt%>" size="6" maxlength="5"
                              title="ora di protocollazione"  classNameBase="input" readonly="false" validateOnPost="false" 
                              required="false" trim ="false"
                          />&nbsp;*</td>
                  <tr><td colspan="4">(In caso di concorrenza, il num di protocollo proposto potrebbe essere differente da quello inserito nel documento)<br/>&nbsp;</td></tr>
                  <tr><td>Doc. di 
                          <af:textBox type="hidden" name="docInOut" value="I"/>
                          <af:textBox name="DocDiIn" value="Input" size="6" maxlength="6"
                              title="documento di input/output"  classNameBase="input" readonly="true" validateOnPost="false" 
                              required="false" trim ="false"
                          />&nbsp;*</td>
                      <td colspan="3" align="left">Rif.
                         <af:textBox name="docRif" value="<%=docRif%>" size="<%=docRif.length()%>" maxlength="100"
                              title="riferimento"  classNameBase="input" readonly="true" validateOnPost="false" 
                              required="false" trim ="false"
                          />&nbsp;*</td>
                  </tr>
                
            </table>
        </div>
    	</td>
   <%}
}%>
</tr>
</table>
</td>
</tr>
<tr><td><img name="DBImg" alt="Salva sul DataBase" src="../../img/DB_img.gif"></td>
    <td><table cellpadding="0" cellspacing="0" border="0" width="100%">
        <tr><td>
          <%if ((tipoDoc.equals("STMOV")) || (tipoDoc.equals("PRM")) || (tipoDoc.equals("RAP")) || (tipoDoc.equals("RGR")) || (tipoDoc.equals("PUB")) || (tipoDoc.equals("PUB_A")) 
          || (tipoDoc.equals("PUB_G")) || tipoDoc.equals("SLCOOP") || tipoDoc.equals("PROF_DIA_RURER")){%>        
            <input name="salvaDB" type="checkbox" disabled="disabled"/>Salvare nel DataBase
          <%
          } else {
            if(prObbligatoria || forzaSalvaDB){%>
                <input name="salvaDB" type="checkbox" checked disabled="disbled"/>Salvare nel DataBase
             <%} else {%>
             	 <input name="salvaDB" type="checkbox" <% if( checkedDB ){%>checked<%}%>/>Salvare nel DataBase
             <%}
          }%>
            </td>
            <td colspan="2" align="right">Tipo file
            <af:comboBox classNameBase="input" name="tipoFileEXT" moduleName="M_GetEstFileProt"
                     selectedValue="<%=estReportDefautl%>" title="Stato occupazionale" required="true"/>
            </td>
          </tr></table>
      </td>
  </tr>

<tr>    
    <td><img name="downloadImg" alt="Salva in locale" src="../../img/download.gif"></td>
    <td align="left">
    <%if ((tipoDoc.equals("PRM")) || tipoDoc.equals("RGR") || (tipoDoc.equals("PUB")) || (tipoDoc.equals("PUB_A"))|| (tipoDoc.equals("PUB_G"))) {
    %>
      <input name="salvaInLocale" type="checkbox" disabled="disabled"/>Salva una copia sul tuo PC
    <%
    }
    else {
    %>
        <input name="salvaInLocale" type="checkbox" onClick="nascondiApri()"/>Salva una copia sul tuo PC
        <script>if (document.form1.protocolla.checked) document.form1.salvaInLocale.disabled="true";</script>
    <%
    }
    %>
    </td>
</tr>
<!-- <tr><td colspan="2"><table></table></td></tr> -->
<tr>
    <td><img name="monitor" alt="Mostra" src="../../img/monitor.gif"></td>
    <td>
        <div align="left" id="apriCHK" style="display:">
            <input name="apri" type="checkbox" checked="true" value="Apri"/>Visualizzare
        </div>
    </td>
</tr>
<%
  if(fileEsistente) {%>
  <tr><td><br/></td></tr>
  <tr><td valign="top"><IMG SRC="../../img/upload.gif" BORDER="0"></td>
      <td class="azzurro_bianco">
          <%if (numProtEsist != null) {%>
            <input name="apriFileBlob" type="checkbox" onClick="apriFileEsistente('<%=prObbligatoria%>')"/>
            Il file esiste già. Protocollo: anno 
                 <af:textBox name="annoProtEx" title="Anno di protocollazione" type="" value="<%=Utils.notNull(annoProtEsist)%>" size="5"
                             classNameBase="input" readonly="true" validateOnPost="false" required="false" trim ="false"/>
                 &nbsp;&nbsp;&nbsp;numero
                 <af:textBox name="numProtEx" title="Numero di protocollo" type="" value="<%= numProtEsist.toString() %>" size="7"
                             classNameBase="input" readonly="true" validateOnPost="false" required="false" trim ="false"/>
            &nbsp;&nbsp;&nbsp;Vuoi aprirlo?&nbsp;
            <input name="prgDocumento" type="hidden" value="<%=Utils.notNull(prgDoc)%>" />
          <%} else {%>
            <div style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:lightblue">
            <input name="apriFileBlob" type="checkbox" onClick="apriFileEsistente('<%=prObbligatoria%>')"/>
            Il file esiste già. Vuoi aprirlo?&nbsp;
            <input name="prgDocumento" type="hidden" value="<%=Utils.notNull(prgDoc)%>" />
            </div>
          <%}%>
    </td>
  </tr>
  <%}
%>

<tr><td><br/></td></tr>
<tr><td colspan="2" align="center">
    <input class="pulsante" name="prosegui" type="button" value="  OK   "  onclick="apriStampa('<%=rptAction%>')"/>&nbsp;&nbsp;&nbsp;
    <input class="pulsante" name="prosegui" type="button" value=" Chiudi " onclick="window.opener.undoSubmit();window.close()"/>
</td></tr>

</table>
<%out.print(htmlStreamBottom);%>

</af:form>

</body>
</html>