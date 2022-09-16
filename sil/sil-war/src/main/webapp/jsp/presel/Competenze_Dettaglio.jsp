
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
  String prgCompetenza= (String) serviceRequest.getAttribute("PRGCOMPETENZA");


  //gestione delle codifiche, devono essere gestite in modo "speciale"
  Vector tipiCompetenzeRows=null;
  tipiCompetenzeRows=serviceResponse.getAttributeAsVector("M_GETCOMPETENZE.ROWS.ROW");
  SourceBean row_tipiCompetenze= null;

  SourceBean row_competenzeLavoratore= (SourceBean) serviceResponse.getAttribute("M_GETCOMPETENZA_SINGOLA.ROWS.ROW");
  String codTipoCompetenza=StringUtils.getAttributeStrNotNull(row_competenzeLavoratore, "tipocompetenza");
  String codCompetenza=StringUtils.getAttributeStrNotNull(row_competenzeLavoratore, "codCompetenza");
  String strNote=StringUtils.getAttributeStrNotNull(row_competenzeLavoratore, "NOTE");

  String cdnUtins=row_competenzeLavoratore.containsAttribute("cdnUtins") ? row_competenzeLavoratore.getAttribute("cdnUtins").toString() : "";
  String dtmins=row_competenzeLavoratore.containsAttribute("dtmins") ? row_competenzeLavoratore.getAttribute("dtmins").toString() : "";
  String cdnUtmod=row_competenzeLavoratore.containsAttribute("cdnUtmod") ? row_competenzeLavoratore.getAttribute("cdnUtmod").toString() : "";
  String dtmmod=row_competenzeLavoratore.containsAttribute("dtmmod") ? row_competenzeLavoratore.getAttribute("dtmmod").toString() : "";
  
 
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
  Testata operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);

  // NOTE: Attributi della pagina (pulsanti e link) 
  /*  PageAttribs attributi = new PageAttribs(user, "CurrAbilMainPage");
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
    <%= canModify? "flagChanged = true;":"" %>
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
    var url = 'AdapterHTTP?PAGE=CurrCompMainPage&cdnLavoratore=<%=cdnLavoratore%>&cdnFunzione=<%=_funzione%>';
    setWindowLocation(url);
  }
}



  //dichiaro gli array per riempire la mia supercombo
   var comp_tipo=new Array();
   var comp_cod=new Array();
   var comp_des=new Array();
  <%     for(int i=0; i<tipiCompetenzeRows.size(); i++)  { 
              row_tipiCompetenze = (SourceBean) tipiCompetenzeRows.elementAt(i);
              out.print("comp_tipo["+i+"]=\""+ row_tipiCompetenze.getAttribute("TIPO").toString()+"\";\n");
              out.print("comp_cod["+i+"]=\""+ row_tipiCompetenze.getAttribute("CODICE").toString()+"\";\n");
              out.print("comp_des["+i+"]=\""+ row_tipiCompetenze.getAttribute("DESCRIZIONE").toString()+"\";\n");              
        } 
  %>
-->
  </SCRIPT>



  <SCRIPT TYPE="text/javascript">

<!--
function caricaCompetenze(codTipoCompetenza) {
   i=0;
   j=0;

   while (document.Frm1.codCompetenza.options.length>0) {
        document.Frm1.codCompetenza.options[0]=null;
    }

      for (i=0; i<comp_tipo.length ; i++) {
       if (comp_tipo[i]==codTipoCompetenza) {
          document.Frm1.codCompetenza.options[j]=new Option(comp_des[i], comp_cod[i], false, false);
           j++;
       }
     } 
}


-->


  </SCRIPT>

</head>

<body class="gestione" >

  <%
    
    Linguette l = new Linguette( user,  _funzione, "CurrCompMainPage", new BigDecimal(cdnLavoratore));
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
            <td colspan="2" ><center><b>Competenza</b></center></td>
          </tr>
          <tr>
            <td>
              <font color="green">
                <af:showMessages prefix="M_SaveCompetenza" />
              </font>
            </td>
          </tr>
          <tr>
            <td>
              <br/>
            </td>
          </tr>
          <tr valign="top">
            <td class="etichetta">Tipo di competenza</td>
              <td class="campo">
                <af:comboBox classNameBase="input" title="Tipo di competenza" name="codTipoCompetenza" moduleName="M_GETTIPICOMPETENZA" addBlank="true" onChange="javascript:caricaCompetenze(Frm1.codTipoCompetenza.value);fieldChanged();" selectedValue="<%=codTipoCompetenza%>" required="true" disabled="<%= String.valueOf(!canModify) %>"  /> 
              </td>
           </tr>
         <tr valign="top">
                <td class="etichetta">Competenza</td>
                <td class="campo">
              <af:comboBox classNameBase="input" onChange="fieldChanged();" title="Tipo di competenza" name="codCompetenza" required="true" disabled="<%= String.valueOf(!canModify) %>" >
              <option value="babababa">pipipipi</option>
            <%    String selected="";
                        for(int i=0; i<tipiCompetenzeRows.size(); i++)  { 
                              row_tipiCompetenze = (SourceBean) tipiCompetenzeRows.elementAt(i);
                              selected=row_tipiCompetenze.getAttribute("CODICE").toString().equals(codCompetenza)?"selected=\"true\"":"";
                              if (row_tipiCompetenze.getAttribute("TIPO").equals(codTipoCompetenza)) {
                                   out.print("<option value=\""+ row_tipiCompetenze.getAttribute("CODICE").toString()+"\" "+selected+" >");
                                   out.print(row_tipiCompetenze.getAttribute("DESCRIZIONE").toString()+"</option>");              
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
      <input type="hidden" name="PAGE" value="CurrCompetenzaPage">
      <input type="hidden" name="cdnLavoratore" value="<%= cdnLavoratore %>"/>
      <input type="hidden" name="cdnFunzione" value="<%= _funzione %>"/>
      <input type="hidden" name="PRGCompetenza" value="<%= prgCompetenza %>"/>      
<% if (canModify) { %>
      <input class="pulsante" type="submit" name="salva" value="Aggiorna" />
<% } %>
      <input class="pulsante" type="button" name="annulla" value="<%= canModify? "Chiudi senza aggiornare" : "Chiudi" %>" onclick="chiudi();">
  </center>
</af:form>     

<br/>
<p align="center">
<%operatoreInfo.showHTML(out);%>
</p>
<br/>
</body>
</html>