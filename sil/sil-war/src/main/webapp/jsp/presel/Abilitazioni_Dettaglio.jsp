
<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
   
  it.eng.sil.util.*, 
  it.eng.afExt.utils.StringUtils,
  it.eng.sil.security.ProfileDataFilter,  
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
	String cdnLavoratore= (String )serviceRequest.getAttribute("CDNLAVORATORE");
	String _current_page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _current_page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
  PageAttribs attributi = new PageAttribs(user, _current_page);
	
	boolean canModify = false;

	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
      canModify = attributi.containsButton("aggiorna");
    	
    	if(!canModify){
    		canModify=false;
    	}else{
    		canModify=filter.canEditLavoratore();
    	}
      
    }
%>

<%

  //String cdnLavoratore= (String)serviceRequest.getAttribute("CDNLAVORATORE");
  String prgAbilitazione= (String) serviceRequest.getAttribute("PRGABILITAZIONE");

  Vector abilitazioniLavoratoreRows=null;
  abilitazioniLavoratoreRows= serviceResponse.getAttributeAsVector("M_GETLAVORATOREABILITAZIONI.ROWS.ROW");

  SourceBean row_abilitazioniLavoratore= (SourceBean) serviceResponse.getAttribute("M_GETABILITAZIONE.ROWS.ROW");
  String codTipoAbilitazioneGen=StringUtils.getAttributeStrNotNull(row_abilitazioniLavoratore, "codtipoabilitazionegen");
  String codAbilitazioneGen=StringUtils.getAttributeStrNotNull(row_abilitazioniLavoratore, "CODABILITAZIONEGEN");
  String strNote=StringUtils.getAttributeStrNotNull(row_abilitazioniLavoratore, "STRNOTE");

  String cdnUtins=row_abilitazioniLavoratore.containsAttribute("cdnUtins") ? row_abilitazioniLavoratore.getAttribute("cdnUtins").toString() : "";
  String dtmins=row_abilitazioniLavoratore.containsAttribute("dtmins") ? row_abilitazioniLavoratore.getAttribute("dtmins").toString() : "";
  String cdnUtmod=row_abilitazioniLavoratore.containsAttribute("cdnUtmod") ? row_abilitazioniLavoratore.getAttribute("cdnUtmod").toString() : "";
  String dtmmod=row_abilitazioniLavoratore.containsAttribute("dtmmod") ? row_abilitazioniLavoratore.getAttribute("dtmmod").toString() : "";
//gestione delle codifiche, devono essere gestite in modo "speciale"
  Vector tipiAbilitazioniRows=null;
  tipiAbilitazioniRows=serviceResponse.getAttributeAsVector("M_GETTIPIABILITAZIONE.ROWS.ROW");
  SourceBean row_tipiAbilitazione= null;

  
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
  Testata operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);

 // NOTE: Attributi della pagina (pulsanti e link) 
/*    PageAttribs attributi = new PageAttribs(user, "CurrAbilMainPage");
    boolean canModify = attributi.containsButton("aggiorna");
*/

%>
<html>

<head>
  <title>Abilitazioni</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/"/>


<SCRIPT TYPE="text/javascript">
<!--
// Per rilevare la modifica dei dati da parte dell'utente
var flagChanged = false;  


  function fieldChanged() {
    <%if (canModify) {out.print("flagChanged = true;");}%>
  }

function chiudi() {
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;

  ok=true;
  if (flagChanged) {
     if (!confirm("I dati sono cambiati.\nProcedere lo stesso?")){
         ok=false;
     }
  }
  if (ok) {
      var url = 'AdapterHTTP?PAGE=CurrAbilMainPage&cdnLavoratore=<%=cdnLavoratore%>&cdnFunzione=<%=_funzione%>';
      setWindowLocation(url);
  }
}


  //dichiaro gli array per riempire la mia supercombo
   var abil_tipo=new Array();
   var abil_cod=new Array();
   var abil_des=new Array();
  <%     for(int i=0; i<tipiAbilitazioniRows.size(); i++)  { 
              row_tipiAbilitazione = (SourceBean) tipiAbilitazioniRows.elementAt(i);
              out.print("abil_tipo["+i+"]=\""+ row_tipiAbilitazione.getAttribute("TIPO").toString()+"\";\n");
              out.print("abil_cod["+i+"]=\""+ row_tipiAbilitazione.getAttribute("CODICE").toString()+"\";\n");
              out.print("abil_des["+i+"]=\""+ row_tipiAbilitazione.getAttribute("DESCRIZIONE").toString()+"\";\n");              
        }
  %>
-->
  </SCRIPT>



  <SCRIPT TYPE="text/javascript">

<!--
function caricaAbilitazioni(codAbilitazioneGen) {
   i=0;
   j=0;

   while (document.Frm1.tipoAbilitazione.options.length>0) {
        document.Frm1.tipoAbilitazione.options[0]=null;
    }

      for (i=0; i<abil_tipo.length ;i++) {
       if (abil_tipo[i]==codAbilitazioneGen) {
          document.Frm1.tipoAbilitazione.options[j]=new Option(abil_des[i], abil_cod[i], false, (abil_cod[i]=="<%=codAbilitazioneGen%>"?true:false));
           j++;
       }
     } 
}

-->


  </SCRIPT>

</head>

<body class="gestione">

  <%
    
    Linguette l = new Linguette( user,  _funzione, "CurrAbilMainPage", new BigDecimal(cdnLavoratore));
    infCorrentiLav.show(out); 
    l.show(out);
  %>
    <p align="center">
    <af:form name="Frm1" method="POST" action="AdapterHTTP">
    <table class="main">
      <tr>
        <td/>
      </tr>     
      
          <tr>
            <td colspan="2" ><center><b>Abilitazione</b></center></td>
          </tr>
          <tr>
            <td>
              <font color="green">
                <af:showMessages prefix="M_SaveAbilitazione" />
              </font>
            </td>
          </tr>
          <tr>
            <td>
              <br/>
            </td>
          </tr>
          <tr valign="top">
            <td class="etichetta">Tipo di abilitazione</td>
            <td class="campo">
              <af:comboBox classNameBase="input" title="Tipo di abilitazione" name="codTipoAbilitazioneGen" moduleName="M_GETTIPIGENABILITAZIONE" addBlank="true" selectedValue="<%=codTipoAbilitazioneGen%>" onChange="javascript:caricaAbilitazioni(Frm1.codTipoAbilitazioneGen.value);fieldChanged();" required="true" disabled="<%= String.valueOf(!canModify) %>"  />
            </td>
          </tr>
          <tr valign="top">
            <td class="etichetta">Abilitazione</td>
            <td class="campo">
              <af:comboBox classNameBase="input" onChange="fieldChanged();" title="Abilitazione" name="tipoAbilitazione" required="true" disabled="<%= String.valueOf(!canModify) %>"   >
              <OPTION value=""></OPTION>
        <%    String selected="";
              for(int i=0; i<tipiAbilitazioniRows.size(); i++)  { 
                    row_tipiAbilitazione = (SourceBean) tipiAbilitazioniRows.elementAt(i);
                    selected=row_tipiAbilitazione.getAttribute("CODICE").toString().equals(codAbilitazioneGen)?"selected=\"true\"":"";
                    if (row_tipiAbilitazione.getAttribute("TIPO").equals(codTipoAbilitazioneGen)) {
                         out.print("<option value=\""+ row_tipiAbilitazione.getAttribute("CODICE").toString()+"\" "+selected+" >");
                         out.print(row_tipiAbilitazione.getAttribute("DESCRIZIONE").toString()+"</option>");              
                    }
              }
  %>
              </af:comboBox>
            </td>
          </tr>
           <tr valign="top">
            <td class="etichetta">Note</td>
            <td class="campo">
              <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="strNote" cols="25" value="<%=strNote%>" readonly="<%= String.valueOf(!canModify) %>" />
            </td>
          </tr>

    </td>
  </tr>
</table>

  <br/>
  <center>
      <input type="hidden" name="PAGE" value="CurrAbilitazionePage">
      <input type="hidden" name="cdnLavoratore" value="<%= cdnLavoratore %>"/>
      <input type="hidden" name="cdnFunzione" value="<%= _funzione %>"/>
      <input type="hidden" name="PRGABILITAZIONE" value="<%= prgAbilitazione %>"/>      

<%if (canModify) { %>
     <input class="pulsante" type="submit" name="salva" value="Aggiorna" />
<%}%>
     <input class="pulsante" type="button" name="annulla" value="<%= canModify ? "Chiudi senza aggiornare" : "Chiudi" %>" onclick="chiudi();">     
  </center>
</af:form>     
<br/>
<p align="center">
<%operatoreInfo.showHTML(out);%>
</p>
<br/>



</body>

</html>
