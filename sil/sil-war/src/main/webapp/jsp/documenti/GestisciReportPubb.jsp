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
                  it.eng.sil.util.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<head>
<title>GestisciReportPubb.jsp</title>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">

<af:linkScript path="../../js/"/>

<% String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
   String rptAction     = (String) serviceRequest.getAttribute("rptAction");
   String nomeDoc       = null;  //(String) serviceRequest.getAttribute("nomeDoc");
   String parametri     = (String) serviceRequest.getAttribute("parametri");
   String tipoDoc       = (String) serviceRequest.getAttribute("tipoDoc");

   Vector rows = null;

   String strIO = "";
   
   rows = serviceResponse.getAttributeAsVector("M_GetTipoDoc.ROWS.ROW");
   if(rows != null && !rows.isEmpty())
   { SourceBean row = (SourceBean) rows.elementAt(0);
     nomeDoc       = (String) row.getAttribute("DESCRIZIONE");
     strIO         = (String) row.getAttribute("STRIO");
   }

   rows = serviceResponse.getAttributeAsVector("M_GetProtocollazione.ROWS.ROW");
   String prAutomatica     = null; 
   String estReportDefautl = null;
   BigDecimal numProt      = null;
   BigDecimal numAnnoProt  = null;
   BigDecimal kLockProt    = null;
   //La linea di codice successiva è provvisoria in attesa che venga creata la tabella in cui reperire quali sono 
   //i documenti che devono obbligatoriamente protocollati
   boolean prObbligatoria  = (tipoDoc.equalsIgnoreCase("IM") || tipoDoc.equalsIgnoreCase("PT297") || tipoDoc.equalsIgnoreCase("ACLA") ||
                              tipoDoc.equalsIgnoreCase("SA") || tipoDoc.equalsIgnoreCase("SAP")) ? true : false;
   
   if(rows != null && !rows.isEmpty())
   { SourceBean row = (SourceBean) rows.elementAt(0);
     prAutomatica     = (String) row.getAttribute("FLGPROTOCOLLOAUT");
     estReportDefautl = (String) row.getAttribute("CODTIPOFILEESTREPORT");
     numProt          = (BigDecimal) row.getAttribute("NUMPROTOCOLLO");
     numAnnoProt      = (BigDecimal) row.getAttribute("NUMANNOPROT");
     kLockProt        = (BigDecimal) row.getAttribute("NUMKLOPROTOCOLLO");
   }

   boolean fileEsistente = false;
   BigDecimal prgDoc = null;
   BigDecimal numProtEsist    = null;
   String annoProtEsist   = null;
   rows = serviceResponse.getAttributeAsVector("M_ExistDocument.ROWS.ROW");
   if(rows != null && !rows.isEmpty())
   { SourceBean row = (SourceBean) rows.elementAt(0);
     prgDoc  = (BigDecimal) row.getAttribute("PRGDOCUMENTO");
     numProtEsist   = SourceBeanUtils.getAttrBigDecimal(row, "NUMPROTOCOLLO", null);
     BigDecimal annoP = (BigDecimal) row.getAttribute("NUMANNOPROT");
     annoProtEsist = annoP!=null ? annoP.toString() : "";
     fileEsistente = true;
   }

    //Servono per gestire il layout grafico
    String htmlStreamTop = StyleUtils.roundTopTable(false);
    String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>


<script language="JavaScript">
<!--
function Protocolla()
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
    window.document.forms['form1'].salvaDB.disabled=false; 
    window.document.forms['form1'].salvaInLocale.disabled=false; 
  }
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
  { if(PR) window.document.forms['form1'].protocolla.checked= true;
    window.document.forms['form1'].protocolla.disabled=false;
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


  function apriStampa(RPT)
  { //RPT: attributo name del tag ACTION nel file action.xml relativo al report da visualizzare
    var urlpage;
    var salvaPC = window.document.forms['form1'].salvaInLocale.checked;
    var apri = window.document.forms['form1'].apri.checked;
    
    if(salvaPC || !apri)
    { // il documento viene aperto in un'altra finestra (perchè salvato in locale sul PC), 
      // o non si vuole visualizzarlo. 
      // Non occorr allora "creare il frameset" che permette il ritorno indietro
      
      urlpage="AdapterHTTP?ACTION_NAME="+RPT;
      urlpage += "&asAttachment=true";
    }
    else
    { //viene aperto nella pagina chiamante: "creiamo il frameset" che permette il ritorno indietro
    
      urlpage="AdapterHTTP?PAGE=REPORTFRAMEPAGE&ACTION_REDIRECT="+RPT;
      urlpage += "&asAttachment=false";
    }

    if (window.document.forms['form1'].apriFileBlob != null &&
        window.document.forms['form1'].apriFileBlob.checked ) 
       urlpage += "&apriFileBlob=true&prgDocumento=" + window.document.forms['form1'].prgDocumento.value;
    else
    {
        if( window.document.forms['form1'].protocolla != null &&
            window.document.forms['form1'].protocolla.checked )
        { //alert("Doc. Protocollabile");
          var numProt  = window.document.forms['form1'].numProt;
          var annoProt = window.document.forms['form1'].annoProt;
          if(numProt.value != "" && annoProt != "")
          { urlpage += "&numProt="+numProt.value;
            urlpage += "&annoProt="+annoProt.value;
            urlpage += "&kLockProt=<%=kLockProt%>";
          }
          else 
          { alert("I campi: \n\n- \"numero protocollo\" e      \n- \"anno\"\n\nsono obbligatori");
            return;
          }
        }
        urlpage+="&salvaDB=" + window.document.forms['form1'].salvaDB.checked;
    }
    
    urlpage += "&tipoFile=" + window.document.forms['form1'].tipoFileEXT.value

	// GG 18/2/05
    urlpage += "&docInOut=<%= strIO %>";

    var param = location.search;
    var i = param.indexOf("&");
    param = param.substr(i, param.length);
    urlpage += param;
    //alert("URL"+urlpage);

    if(window.document.forms['form1'].apri.checked==true)
    { urlpage += "&apri=true"
      //alert("Apri"+urlpage);
      // NB: qui l'uso del "setOpenerWindowLocation(urlpage)" presenta
      // il seguente problema: dopo il salvataggio si rimanere col
      // bottone STAMPA disabilitato nella pagina "opener"!
      // Perciò passo il secondo parametro a FALSE: per non fare la "prepareSubmit"
      setOpenerWindowLocation(urlpage, false);
      window.close(); //chiude la PopUp
    }
    else
    { urlpage += "&apri=false"
      //alert("NON apri"+urlpage);
      // Se la pagina è già in submit, ignoro questo nuovo invio!
      if (isInSubmit()) return;
      setWindowLocation(urlpage);
    }
    
  }//apriStampa(_,_,_)

//-->
</script>



</head>
<body class="gestione">
<!--<body bgcolor="gold">-->

<af:form name="form1" method="POST" action="AdapterHTTP">
<br/>
<p align="center">
<%out.print(htmlStreamTop);%>
<table class="main">

<tr><td><br/></td></tr>
<tr><td class="azzurro_bianco" colspan="2">
    <!-- <div align="center" style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc"> -->
    <!-- div align="center" style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:lightblue" -->
    <b><%=nomeDoc%></b><!-- </div> -->
    </td>
</tr>

<tr><td width="5%">&nbsp;</td><td>&nbsp;</td></tr>
<tr>
    <td><img name="timbro" alt="Salva sul DataBase" src="../../img/timbro.gif"></td>
    <td>
        <table cellpadding="0" cellspacing="0" border="0" width="100%">
            <tr>
            <%if(prObbligatoria){%>
                <td width="20%"><input name="protocolla" type="checkbox" onClick="Protocolla()" checked/>Protocollare</td>
                <td>
                    <div align="left" id="datiPerProtocol" style="display:">
            <%} else {%>
                <td><input name="protocolla" type="checkbox" onClick="Protocolla()"/>Protocollare</td>
                <td>
                    <div align="left" id="datiPerProtocol" style="display:none">
            <%}%>
            <%if(prAutomatica != null && prAutomatica.equalsIgnoreCase("S")){%>
                        <table cellpadding="0" cellspacing="0" border="0" width="100%">
                            <tr>
                                <td width="25%">anno 
                                   <af:textBox name="annoProt" type="" value="<%=Utils.notNull(numAnnoProt)%>"
                                        size="5" title="Anno di protocollazione" 
                                        classNameBase="input" readonly="true" validateOnPost="false" 
                                        required="false" trim ="false"
                                    />&nbsp;*
                                </td>
                                <td>numero di protocollo 
                                   <af:textBox name="numProt" type="" value="<%=Utils.notNull(numProt)%>" size="7"
                                        title="Numero di protocollo"  classNameBase="input" readonly="true" validateOnPost="false" 
                                       required="false" trim ="false"
                                    />&nbsp;*</td>
                            </tr>
                        </table>
                    </div>
                </td>
            </tr>
        </table>
        <%} else {%>
                        <table cellpadding="0" cellspacing="0" border="0" width="100%">
                            <tr>
                                <td align="right">anno 
                                   <af:textBox name="annoProt" type="" value="<%=Utils.notNull(numAnnoProt)%>" size="5"
                                          title="Anno di protocollazione"  classNameBase="input" readonly="true" validateOnPost="false" 
                                          required="false" trim ="false"
                                    />&nbsp;*
                                </td>
                               <td align="right">numero di protocollo 
                                   <af:textBox name="numProt" type="" value="<%=Utils.notNull(numProt)%>" size="7"
                                           title="Numero di protocollo"  classNameBase="input" readonly="true" validateOnPost="false" 
                                           required="false" trim ="false"
                                    />&nbsp;*
                                </td>
                            </tr>
                        </table>
                    </div>
                </td>
            </tr>
        </table>
        <%}%>
    </td>
</tr>

<tr><td><img name="DBImg" alt="Salva sul DataBase" src="../../img/DB_img.gif"></td>
    <td><table cellpadding="0" cellspacing="0" border="0" width="100%">
        <tr><td><%if(prObbligatoria){%>
                  <input name="salvaDB" type="checkbox" checked disabled="true"/>Salvare nel DataBase
                 <%} else {%>
                  <input name="salvaDB" type="checkbox"/>Salvare nel DataBase
                 <%}%>
            </td>
            <td colspan="2" align="right">
            <af:comboBox classNameBase="input" name="tipoFileEXT" moduleName="M_GetEstFileProt"
                     selectedValue="<%=estReportDefautl%>" title="Stato occupazionale" required="true" />
            
            </td>
        </tr></table>
    </td>
</tr>

<tr>    
    <td><img name="downloadImg" alt="Salva in locale" src="../../img/download.gif"></td>
    <td align="left">
        <input name="salvaInLocale" type="checkbox" onClick="nascondiApri()"  />Salva una copia sul tuo PC
        <script>if (document.form1.protocolla.checked) document.form1.salvaInLocale.disabled="true";</script>
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

<%if(fileEsistente) {%>
<tr><td><br/></td></tr>
<tr><td valign="top"><IMG SRC="../../img/upload.gif" BORDER="0"></td>
    <td><div style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:lightblue">
        <%if (numProtEsist != null) { %>
          <input name="apriFileBlob" type="checkbox" onClick="apriFileEsistente('<%=prObbligatoria%>')"/>
          Il file esiste già. Protocollo: anno 
               <af:textBox name="annoProtEx" title="Anno di protocollazione" type="" value="<%=Utils.notNull(annoProtEsist)%>" size="5"
                           classNameBase="input" readonly="true" validateOnPost="false" required="false" trim ="false"/>&nbsp;*
               numero
               <af:textBox name="numProtEx" title="Numero di protocollo" type="" value="<%= numProtEsist.toString() %>" size="7"
                           classNameBase="input" readonly="true" validateOnPost="false" required="false" trim ="false"/>&nbsp;*
          Vuoi aprirlo?&nbsp;
          <input name="prgDocumento" type="hidden" value="<%=Utils.notNull(prgDoc)%>" />
        <%} else {%>
          <div style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:lightblue">
          <input name="apriFileBlob" type="checkbox" onClick="apriFileEsistente('<%=prObbligatoria%>')"/>
          Il file esiste già. Vuoi aprirlo?&nbsp;
          <input name="prgDocumento" type="hidden" value="<%=Utils.notNull(prgDoc)%>" />
        <%}%>
    </div></td>
</tr>
<%}%>

<tr><td><br/></td></tr>
<tr><td colspan="2" align="center">
    <input class="pulsante" name="prosegui" type="button" value="  OK   "  onclick="apriStampa('<%=rptAction%>')"/>&nbsp;&nbsp;&nbsp;
    <input class="pulsante" name="prosegui" type="button" value=" Chiudi " onclick="window.close()"/>
    <%--
    <input style="text-color: #3366cc;background-color:#cce4ff" name="prosegui" type="button" value="  OK   "
                   onclick="apriStampa('<%=rptAction%>')"/>&nbsp;&nbsp;&nbsp;
    <input style="text-color: #000080;background-color:lightblue" name="prosegui" type="button" value=" Chiudi "
                   onclick="window.close()"/>
    --%>
</td></tr>
<!-- <tr><td colspan="2" align="center">
    <input class="button" name="prosegui" type="button" value="  OK   "
                   onclick="apriStampa('<%=rptAction%>')"/>&nbsp;&nbsp;&nbsp;
    <input class="button" name="prosegui" type="button" value=" Chiudi "
                   onclick="window.close()"/>
</td></tr>-->




<!--<tr><td bgcolor="red">red</td></tr>
<tr><td bgcolor="orange">orange</td></tr>
<tr><td bgcolor="yellow">yellow</td></tr>
<tr><td bgcolor="green">green</td></tr>
<tr><td bgcolor="blue">blue</td></tr>
<tr><td bgcolor="pink">pink</td></tr>
<tr><td bgcolor="violet">violet</td></tr>
<tr><td bgcolor="maroon">maroon</td></tr>
<tr><td bgcolor="white">white</td></tr>
<tr><td bgcolor="gray">gray</td></tr>
<tr><td bgcolor="black">black</td></tr>
<tr><td bgcolor="gold">gold</td></tr>
<tr><td bgcolor="silver">silver</td></tr>

<tr><td bgcolor="lightyellow">lightyellow</td></tr>
<tr><td bgcolor="lightgreen">lightgreen</td></tr>
<tr><td bgcolor="lightblue">lightblue</td></tr>
<tr><td bgcolor="lightpink">lightpink</td></tr>
<tr><td bgcolor="lightviolet">lightviolet</td></tr>
<tr><td bgcolor="lightmaroon">lightmaroon</td></tr>
<tr><td bgcolor="lightwhite">lightwhite</td></tr>
<tr><td bgcolor="lightgray">lightgray</td></tr>
<tr><td bgcolor="lightblack">lightblack</td></tr>-->
<!-- <tr><td bgcolor="lightred">lightred</td></tr>
<tr><td bgcolor="lightorange">lightorange</td></tr> -->
</table>
<%out.print(htmlStreamBottom);%>

</af:form>
</body>
</html>
