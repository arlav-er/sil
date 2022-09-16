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
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  com.engiweb.framework.message.*,
                  it.eng.afExt.utils.MessageCodes,
                  it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<head>
<title>Gestisci Rosa Candidati</title>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">

<af:linkScript path="../../js/"/>

<% String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
   String rptAction     = (String) serviceRequest.getAttribute("rptAction");
   String nomeDoc       = null;  //(String) serviceRequest.getAttribute("nomeDoc");
   String parametri     = (String) serviceRequest.getAttribute("parametri");
   String tipoDoc       = (String) serviceRequest.getAttribute("tipoDoc");
   String prgRosa       = (String) serviceRequest.getAttribute("prgRosa");
   BigDecimal prgSpi 	= null ;
  
   
   // Se prgRosa = 0 --> tutte le rose
   String pagina        = StringUtils.notNull ((String) serviceRequest.getAttribute("pagina"));
   // "pagina" serve per legare il report alla pagina (componente) indicato
   // (vedi la jsp "StampeRosaCandidatiIDO.jsp")
   
   //La linea di codice successiva è provvisoria in attesa che venga creata la tabella in cui reperire quali sono 
   //i documenti che devono obbligatoriamente protocollati
   boolean prObbligatoria = (tipoDoc.equalsIgnoreCase("IM") || tipoDoc.equalsIgnoreCase("PT297") || tipoDoc.equalsIgnoreCase("ACLA") || 
                             tipoDoc.equalsIgnoreCase("SA") || tipoDoc.equalsIgnoreCase("SAP")) ? true : false;
   
   Vector rows = null;
   
   String strIO = "";

   rows = serviceResponse.getAttributeAsVector("M_IDOGETSPI.ROWS.ROW");
   if(rows != null && !rows.isEmpty())
   { SourceBean row = (SourceBean) rows.elementAt(0);
     prgSpi        = (BigDecimal) row.getAttribute("PRGSPI");
     
   }   
   
   
   
   rows = serviceResponse.getAttributeAsVector("M_GetTipoDoc.ROWS.ROW");
   if(rows != null && !rows.isEmpty())
   { SourceBean row = (SourceBean) rows.elementAt(0);
     nomeDoc        = (String) row.getAttribute("DESCRIZIONE");
     strIO          = (String) row.getAttribute("STRIO");
   }

   rows = serviceResponse.getAttributeAsVector("M_GetDataInvioRosae.ROWS.ROW");
   String datInvio = "";
   String datInvioWarning = "";
   if(rows != null && !rows.isEmpty())
   { if(!prgRosa.equals("0")) {  // Caso di "tutti"
      SourceBean row = (SourceBean) rows.elementAt(0);
      datInvio = (String) row.getAttribute("DATINVIO");
     }
     else {
      datInvioWarning = "L\'unione delle rose presenta già una data invio";
     }
   }

   rows = serviceResponse.getAttributeAsVector("M_GetProtocollazione.ROWS.ROW");
   String prAutomatica     = null; 
   String estReportDefault = null;
   BigDecimal numProt      = null;
   BigDecimal numAnnoProt  = null;
   BigDecimal kLockProt    = null;
   if(rows != null && !rows.isEmpty())
   { SourceBean row = (SourceBean) rows.elementAt(0);
     prAutomatica     = (String) row.getAttribute("FLGPROTOCOLLOAUT");
     estReportDefault = (String) row.getAttribute("CODTIPOFILEESTREPORT");
     numProt          = (BigDecimal) row.getAttribute("NUMPROTOCOLLO");
     numAnnoProt      = (BigDecimal) row.getAttribute("NUMANNOPROT");
     kLockProt        = (BigDecimal) row.getAttribute("NUMKLOPROTOCOLLO");
   }

   boolean fileEsistente = false;
   BigDecimal prgDoc = null;
   BigDecimal numProtEsist    = null;
   String annoProtEsist   = null;
   rows = serviceResponse.getAttributeAsVector("M_ExistDocumentPerAzienda.ROWS.ROW");
   if(rows != null && !rows.isEmpty())
   { SourceBean row = (SourceBean) rows.elementAt(0);
     prgDoc  = (BigDecimal) row.getAttribute("PRGDOCUMENTO");
     numProtEsist   = SourceBeanUtils.getAttrBigDecimal(row, "NUMPROTOCOLLO", null);
     BigDecimal annoP = (BigDecimal) row.getAttribute("NUMANNOPROT");
     annoProtEsist = annoP!=null ? annoP.toString() : "";
     fileEsistente = true;
   }

    // NOTE: Attributi della pagina (pulsanti e link) 
    PageAttribs attributi = new PageAttribs(user, "GestioneRosaIDOPage");
    boolean salvaDB = true;
    boolean salvaPC = true;

    //Servono per gestire il layout grafico
    String htmlStreamTop = StyleUtils.roundTopTable(false);
    String htmlStreamBottom = StyleUtils.roundBottomTable(false);
    
    //Mi dice se la richiesta è già chiusa
    boolean isClosedReq = true;
 	SourceBean rowStatoReq = (SourceBean) serviceResponse.getAttribute("M_IdoGetStatoRichOrig.ROWS.ROW");
 	BigDecimal prgRichAzOrig = null;
 	if (rowStatoReq != null) {
 		BigDecimal cdnStatoRich = (BigDecimal) rowStatoReq.getAttribute("CDNSTATORICH");
 		prgRichAzOrig = (BigDecimal) rowStatoReq.getAttribute("PRGRICHIESTAAZ");
 		if (cdnStatoRich != null && 
 			!cdnStatoRich.equals(new BigDecimal(4)) && 
 			!cdnStatoRich.equals(new BigDecimal(5))) {isClosedReq = false;}
 	}
    
    //Profilatura dei pulsanti di stampa
    boolean canViewStampaAperta = attributi.containsButton("STAMPA_LASCIA_APERTA");;
    boolean canViewStampaChiusa = attributi.containsButton("STAMPA_CHIUDI");;    


    boolean enableProto = true;   // GG: per i CV non considero protocollo e salvataggio
    boolean enableSalva = true;
    if (! rptAction.equalsIgnoreCase("RPT_IDO_ROSA")) {
   		prObbligatoria = false;
   		enableProto = false;
   		enableSalva = false;
   		fileEsistente = false;
    }
    
        //Reperisco la data e ora corrente
    Calendar oggi = Calendar.getInstance();
    
    String giorno = Integer.toString(oggi.get(Calendar.DATE));    if(giorno.length()<=1) { giorno = "0" + giorno; }
    String mese   = Integer.toString(oggi.get(Calendar.MONTH)+1); if(mese.length()<=1)   { mese   = "0" + mese; }
    String anno   = Integer.toString(oggi.get(Calendar.YEAR));    if(anno.length()<=1)   { anno   = "0" + anno; }
    String dataProt = giorno +"/"+ mese +"/"+ anno; 
    
    String ora = Integer.toString(oggi.get(Calendar.HOUR_OF_DAY)); if(ora.length()<=1)    { ora    = "0" + ora; }
    String minuti = Integer.toString(oggi.get(Calendar.MINUTE));   if(minuti.length()<=1) { minuti = "0" + minuti; }
    String oraProt = ora + ":" + minuti;
    
    int tipoProtocollo =  ProtocolloDocumentoUtil.tipoProtocollo();
	switch(tipoProtocollo) {
		case ProtocolloDocumentoUtil.TIPO_DOCAREA_VER_161:
		case ProtocolloDocumentoUtil.TIPO_DOCAREA_SARE:
			numProt  = null;
			numAnnoProt = null;
			break;
		case ProtocolloDocumentoUtil.TIPO_SIL_LOCALE_MANUALE:
			numProt  = null;
			//strAnnoProt = Utils.notNull(numAnnoProt);
			break;
		case ProtocolloDocumentoUtil.TIPO_SIL_LOCALE_AUTOMATICA:
			//strNumProt  = numProt.toString();
			//strAnnoProt = numAnnoProt.toString();
			break;
		default:
			throw new Exception("Tipo di protocollazione non gestita: "+tipoProtocollo);
	}
    
%>


<script language="JavaScript">
<!--
function Protocolla()
{ 
  if(window.document.forms['form1'].protocolla != null)
  { var PR = window.document.forms['form1'].protocolla.checked;
    if(PR==true) 
    { mostra("datiPerProtocol");
      window.document.forms['form1'].salvaDB.checked=true;
      window.document.forms['form1'].salvaDB.disabled=true; 
      //
      window.document.forms['form1'].salvaInLocale.disabled=true; 
      window.document.forms['form1'].salvaInLocale.checked=false;
      //
      mostra("apriCHK");
    }
    else
    { nascondi("datiPerProtocol");
      window.document.forms['form1'].salvaDB.checked=false;    
      window.document.forms['form1'].salvaDB.disabled=<%= salvaDB ? "false" : "true" %>; 
      window.document.forms['form1'].salvaInLocale.disabled=<%= salvaDB ? "false" : "true" %>; 
    }
  }//if
}

function apriFileEsistente(PR)
{ if (window.document.forms['form1'].apriFileBlob.checked)
  { window.document.forms['form1'].protocolla.checked=false;
    window.document.forms['form1'].protocolla.disabled=true;
    Protocolla();
    window.document.forms['form1'].salvaDB.checked=false;
    window.document.forms['form1'].salvaDB.disabled=true;
    window.document.forms['form1'].apri.checked  = true;
    nascondi("apriCHK");
  }
  else
  { if(PR) {
  		window.document.forms['form1'].protocolla.checked = true;
  	}
    window.document.forms['form1'].protocolla.disabled = <%= salvaDB ? "false" : "true" %>;
    Protocolla();
    mostra("apriCHK");
  }
}

function mostra(id)
{ var div = document.getElementById(id);
  div.style.display="";
}

function nascondi(id)
{ var div = document.getElementById(id);
  div.style.display="none";
}


function nascondiApri()
{
  if(window.document.forms['form1'].salvaInLocale.checked)
  { window.document.forms['form1'].apri.checked  = true;
    nascondi("apriCHK");
  }
  else mostra("apriCHK");
}


function checkField()
{ var PR = window.document.forms['form1'].protocolla.checked;
  if(PR==true) 
  { if( (window.document.forms['form1'].numProt.value != null) &&
        (window.document.forms['form1'].annoProt.value != null) )
      alert("I campi numero protocolle e/o anno non sono inizializzati");
      return false;
  }
  else return true;
}


  function apriStampa(RPT, chiudiRichiestaCollegata)
  { //RPT: attributo name del tag ACTION nel file action.xml relativo al report da visualizzare
    var urlpage;
    var myform = window.document.forms['form1'];
    
    var salvaPC = myform.salvaInLocale.checked;
    var apri = myform.apri.checked;

    if(salvaPC || !apri) {
      // il documento viene aperto in un'altra finestra (perchè salvato in locale sul PC), 
      // o non si vuole visualizzarlo. 
      // Non occorr allora "creare il frameset" che permette il ritorno indietro
      
      urlpage="AdapterHTTP?ACTION_NAME="+RPT;
      urlpage += "&asAttachment=true";
      urlpage += "&chiudiRichColl=" + chiudiRichiestaCollegata + "&prgRichAzOrig=<%=String.valueOf(prgRichAzOrig)%>";
    }
    else {
      //viene aperto nella pagina chiamante: "creiamo il frameset" che permette il ritorno indietro

      urlpage="AdapterHTTP?PAGE=REPORTFRAMEPAGE&ACTION_REDIRECT="+RPT;
      urlpage += "&asAttachment=false";
      urlpage += "&chiudiRichColl=" + chiudiRichiestaCollegata + "&prgRichAzOrig=<%=String.valueOf(prgRichAzOrig)%>";
    }

    if (myform.apriFileBlob != null &&
        myform.apriFileBlob.checked ) {
         
       urlpage += "&apriFileBlob=true"+
       			  "&prgDocumento=" + myform.prgDocumento.value;
	}    
    else {
    
		if (! controllaFunzTL()) {
			//alert("return");
			return;
		}
    
        if( myform.protocolla != null &&
            myform.protocolla.checked ) {
          //alert("Doc. Protocollabile");
          var numProt  = myform.numProt;
          var annoProt = myform.annoProt;
		  var protLocaleAutomatica = <%= tipoProtocollo==ProtocolloDocumentoUtil.TIPO_SIL_LOCALE_AUTOMATICA %>;
		  if (protLocaleAutomatica && (myform.numProt.value == "" || myform.annoProt.value == "")) {
				alert("I campi: \n\n- \"numero protocollo\" e      \n- \"anno\"\n\nsono obbligatori");
				return;
		  }
          else {
            urlpage += "&numProt="+numProt.value;
            urlpage += "&annoProt="+annoProt.value;
            urlpage += "&kLockProt=<%=kLockProt%>";
            urlpage += "&dataOraProt=" + myform.dataProt.value + " " + myform.oraProt.value;
            urlpage += "&protAutomatica=<%=prAutomatica%>";
          }          
        }
	    
	    if (myform.salvaDB != null) { 
        	urlpage+="&salvaDB=" + myform.salvaDB.checked;
        	<% if (StringUtils.isFilled(pagina)) {%>
        		//alert("urlpage="+urlpage);
        		var param = location.search;  // questi li metterò dopo
        		if (param.indexOf("&pagina=") == -1) {
		        	urlpage+="&pagina=<%= pagina %>";	// Lo metto se non verrà messo
	        	}
        	<% } %>
        }
    }
    
    urlpage += "&tipoFile=" + myform.tipoFileEXT.value;
    urlpage += "&datInvio=" + myform.datInvio.value;

	// GG 18/2/05
    urlpage += "&docInOut=<%= strIO %>";
	// Davide 26/06/2006
    if (myform.inviaSMS != null &&
        myform.inviaSMS.checked ) {
        urlpage += "&inviaSMS=" + myform.inviaSMS.checked;
    }

    var param = location.search;
    var i = param.indexOf("&");
    param = param.substr(i, param.length);
    //alert("param: "+param);
    urlpage += param;
    //alert("URL "+urlpage);

    if(myform.apri.checked)
    { urlpage += "&apri=true";
      //alert("Apri "+urlpage);
      // NB: qui l'uso del "setOpenerWindowLocation(urlpage)" presenta
      // il seguente problema: dopo il salvataggio si rimanere col
      // bottone STAMPA disabilitato nella pagina "opener"!
      // Perciò passo il secondo parametro a FALSE: per non fare la "prepareSubmit"
      setOpenerWindowLocation(urlpage, false);
      window.close(); //chiude la PopUp
    }
    else
    { urlpage += "&apri=false"
      // alert("NON apri "+urlpage);
      // Se la pagina è già in submit, ignoro questo nuovo invio!
      // NO: ho già fatto la "controllaFunzTL":  if (isInSubmit()) return;
      setWindowLocation(urlpage);
    }
  }//apriStampa(_,_,_)

//-->
</script>



</head>
<body class="gestione">
<!--<body style="background-color:pink">-->

<af:form name="form1" method="POST" action="AdapterHTTP" onSubmit="false">
<br/>
<p align="center">
<%out.print(htmlStreamTop);%>
<table class="main" border="0">

<!--<tr><td rowspan="8"><img align="absmiddle" name="dettImg" alt="Rosa" src="../../img/rosaCheSboccia2.gif"></td></tr>-->
<tr><td class="azzurro_bianco" colspan="2">
    <!--<div align="center" style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:violet">-->
    <b><%=nomeDoc%></b>
    </td>
</tr>

<tr><td width="5%">&nbsp;</td><td>&nbsp;</td></tr>
<% if (enableProto) { %>
<tr>
    <td><img name="timbro" alt="Protocolla" src="../../img/timbro.gif"></td>
    <td>
        <table cellpadding="0" cellspacing="0" border="0" width="100%">
            <tr>
                <td width="20%"><input type="checkbox" name="protocolla"
                		onClick="Protocolla()"
                		<%= prObbligatoria ? "checked=\"true\"" : "" %>
                		<%= salvaDB ? "" : "disabled=\"true\"" %> />Protocollare</td>
                <td>
                    <div id="datiPerProtocol" style="display:<%= prObbligatoria ? "" : "none" %>" align="left">
                <% if(!ProtocolloDocumentoUtil.protocollazioneLocale()){ %>
					<input type="hidden" name="annoProt" value="<%= Utils.notNull(numAnnoProt) %>" >
              		<input type="hidden" name="numProt"  value="" >
              		<input type="hidden" name="dataProt" value="<%= dataProt %>" >
              		<input type="hidden" name="oraProt"  value="<%= oraProt %>" >
				<%} else {%>                   
						<table cellpadding="0" cellspacing="0" border="0" width="100%">
	                            <tr>
	                                <td align="right">anno 
	                                   <af:textBox name="annoProt" type="" value="<%=Utils.notNull(numAnnoProt)%>"
	                                        size="4" title="Anno di protocollazione" 
	                                        classNameBase="input" readonly="true" validateOnPost="false" 
	                                        required="false" trim ="false"
	                                    />&nbsp;*
	                                </td> 
	                                <td align="left">&nbsp;
	                                   numero di protocollo 
	                                   <af:textBox name="numProt" type="" value="<%=Utils.notNull(numProt)%>" size="7"
	                                           title="Numero di protocollo"  classNameBase="input" readonly="true" validateOnPost="false" 
	                                           required="false" trim ="false"
	                                    />&nbsp;*
	                                </td>
	                                <td align="right">data
                                     <af:textBox name="dataProt" type="date" value="<%=dataProt%>" size="11" maxlength="10"
                                          title="data di protocollazione"  classNameBase="input" readonly="true" validateOnPost="true" 
                                          required="false" trim ="false"
                                      />&nbsp;*</td>
                                  <td align="right">ora
                                     <af:textBox name="oraProt" type="date" value="<%=oraProt%>" size="5" maxlength="5"
                                          title="data di protocollazione"  classNameBase="input" readonly="true" validateOnPost="false" 
                                          required="false" trim ="false"
                                      />&nbsp;*</td>
	                            </tr>
	                        </table>
					<%}%>
                    </div>
                </td>
            </tr>
        </table>
    </td>
</tr>
<% } %>

<% if (enableSalva) { %>
<tr><td><img name="DBImg" alt="Salva sul DataBase" src="../../img/DB_img.gif"></td>
    <td><table cellpadding="0" cellspacing="0" border="0" width="100%">
        <tr><td>
        	<%if(prObbligatoria){%>
               <input name="salvaDB" type="checkbox" checked="true" disabled="true" />Salvare nel DataBase
            <%} else {%>
               <input name="salvaDB" type="checkbox" <%= salvaDB ? "" : "disabled=\"true\"" %>/>Salvare nel DataBase
            <%}%>
            </td>

            <td colspan="2" align="right">Tipo file
            <af:comboBox classNameBase="input" name="tipoFileEXT" moduleName="M_GetEstFileProt"
                     selectedValue="<%=estReportDefault%>" title="Stato occupazionale" required="true" />
            </td>
        </tr></table>
    </td>
</tr>
<% } %>


<tr>    
    <td><img name="downloadImg" alt="Salva in locale" src="../../img/download.gif"></td>
    <td><table cellpadding="0" cellspacing="0" border="0" width="100%">
        <tr><td>
        	<input name="salvaInLocale" type="checkbox" onClick="nascondiApri()" <%= salvaPC ? "" : "disabled=\"true\"" %> />Salva una copia sul tuo PC
            </td>

			<% if (! enableSalva) { %>
	            <td colspan="2" align="right">Tipo file
	            <af:comboBox classNameBase="input" name="tipoFileEXT" moduleName="M_GetEstFileProt"
	                     selectedValue="<%=estReportDefault%>" title="Stato occupazionale" required="true" />
	            </td>
			<% } %>
        </tr></table>
    </td>

    <td align="left">
    	&nbsp;
    </td>
</tr>

<tr>
    <td><img name="monitor" alt="Mostra" src="../../img/monitor.gif"></td>
    <td><table cellpadding="0" cellspacing="0" border="0" width="100%">
        <tr><td><div align="left" id="apriCHK" style="display:">
                  <input name="apri" type="checkbox" checked="checked" value="Apri"/>Visualizzare
            </div></td>
            <td colspan="2" align="right">
            <%if (rptAction.equalsIgnoreCase("RPT_IDO_ROSA")) {%>
               Data invio rosa:
               <af:textBox name="datInvio" title="Data invio" type="date" 
                           value="<%=Utils.notNull(datInvio)%>" size="11" maxlength="10"
                           classNameBase="input" validateOnPost="true" required="true"/>
            <%} else {%>
               Data invio rosa:
               <af:textBox name="datInvio" title="Data invio" type="date" 
                           value="<%=Utils.notNull(datInvio)%>" size="11" maxlength="10"
                           classNameBase="input" required="false"
                           disabled="true"/>
            <%}%>
            </td>
        </tr></table>
    </td>
</tr>

<%if (rptAction.equalsIgnoreCase("RPT_IDO_ROSA")) {%>

<tr>    
    <td><img name="InviaSMS" alt="Invia SMS" src="../../img/cellulare.gif"></td>
    <td><table cellpadding="0" cellspacing="0" border="0" width="100%">
        <tr>
        	<%if (prgSpi == null) {%>
        	<td valign="bottom">
        		<input name="inviaSMS" type="checkbox" disabled="disabled" />Invia SMS
            </td>
            <td valign="bottom">
        		(<%= MessageBundle.getMessage(Integer.toString(MessageCodes.SMS.SPI_MANCANTE_SMS_FALLITO))%>)
            </td>
            <% } else {%>
            <td valign="bottom">
        		<input name="inviaSMS" type="checkbox" />Invia SMS
            </td>
            <%}%>
        </tr>
       </table>
    </td>

    <td align="left">
    	&nbsp;
    </td>
</tr>

<%}%>

<tr><td>&nbsp;</td></tr>
<tr><td class="azzurro_bianco" colspan="2">
    <!--<div align="center" style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:violet">-->
    <b><%=datInvioWarning%></b>
    </td>
</tr>

<%if(fileEsistente) {%>
<tr><td valign="top"><IMG SRC="../../img/upload.gif" BORDER="0"/></td>
    <td class="azzurro_bianco">
    <div style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:lightblue">
		<input name="apriFileBlob" type="checkbox" onClick="apriFileEsistente(<%=prObbligatoria%>)"/>
		Il file esiste già.
        <% if (numProtEsist != null) { %>
          Protocollo: anno 
             <af:textBox name="annoProtEx" title="Anno di protocollazione" type="" value="<%=Utils.notNull(annoProtEsist)%>" size="5"
                         classNameBase="input" readonly="true" validateOnPost="false" required="false" trim ="false"/>&nbsp;
             numero
             <af:textBox name="numProtEx" title="Numero di protocollo" type="" value="<%= numProtEsist.toString() %>" size="7"
                         classNameBase="input" readonly="true" validateOnPost="false" required="false" trim ="false"/>&nbsp;
         <% } %>
         Vuoi aprirlo?&nbsp;
         <input name="prgDocumento" type="hidden" value="<%=Utils.notNull(prgDoc)%>" />
   </div>
    </td>
</tr>
<%}%>

<tr><td><br/></td></tr>
<tr><td colspan="2" align="center">
<% if (rptAction.equalsIgnoreCase("RPT_IDO_ROSA")) {%>
	<%if (!isClosedReq) {%>
		<%if (canViewStampaAperta) {%>
	    <input class="pulsante" name="prosegui" type="button" value="Stampa e lascia aperta"
				onclick="apriStampa('<%=rptAction%>', 'false')"/>&nbsp;&nbsp;&nbsp;
	    <%}%>
		<%if (canViewStampaChiusa) {%>	    
	    <input class="pulsante" name="prosegui" type="button" value="Stampa e chiudi"
	    		onclick="apriStampa('<%=rptAction%>', 'true')"/>&nbsp;&nbsp;&nbsp;
	    <%}%>	
	<%} else {%>
	    <input class="pulsante" name="prosegui" type="button" value="Stampa"
	    		onclick="apriStampa('<%=rptAction%>', 'false')"/>&nbsp;&nbsp;&nbsp;		
	<%}%>
<%} else {%>
    <input class="pulsante" name="prosegui" type="button" value="   Ok   "
    		onclick="apriStampa('<%=rptAction%>', 'false')"/>&nbsp;&nbsp;&nbsp;
<%}%>
    <input class="pulsante" name="prosegui" type="button" value=" Chiudi "
    		onclick="window.close()"/>
</td></tr>

<tr><td><br/></td></tr>
</table>
<%out.print(htmlStreamBottom);%>

</af:form>
</body>
</html>

