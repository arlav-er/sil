<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ include file="getAziendaRichiesta.inc" %>

<%@ page import="com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.User,
  it.eng.sil.security.ProfileDataFilter,  
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  com.engiweb.framework.dbaccess.sql.TimestampDecorator,
  it.eng.sil.security.PageAttribs,
  com.engiweb.framework.security.*"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
  String codAgevolazione="";
  String strTitolo="Consulta Agevolazione";
  Object objFlgIndispensabile = null;
  String strFlgIndispensabile="";
  String cdnUtins="";
  String dtmins="";
  String cdnUtmod="";
  String dtmmod="";
  String strPrgAgevolazione="";   
  String btnSalva="Aggiorna";
  String btnAnnulla;
  String btnChiudiDettaglio="";

  boolean canInsert= false;
  boolean canModify= false;
  boolean canDelete= false;
  boolean canManage = false;
  
  String cdnStatoRich = serviceResponse.getAttribute("M_GetTestataRichiesta.ROWS.ROW.cdnStatoRich").toString();
  boolean nuovo = true;


  ProfileDataFilter filter = new ProfileDataFilter(user, "IdoAgevolazioniPage");
  filter.setPrgAzienda(prgAzienda);
  filter.setPrgUnita(prgUnita);
  
  boolean canView=filter.canViewUnitaAzienda();
  if ( !canView ){
    response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
  }else{
    PageAttribs attributi = new PageAttribs(user, "IdoAgevolazioniPage");
    if (cdnStatoRich.compareTo("4")!=0 && cdnStatoRich.compareTo("5")!=0){
      canInsert= attributi.containsButton("INSERISCI");      
      canModify= attributi.containsButton("AGGIORNA");
      canDelete= attributi.containsButton("CANCELLA");
    }

    if ( !canModify && !canDelete && !canInsert) {
      
       } else {
        boolean canEdit = filter.canEditUnitaAzienda();
        if ( !canEdit ) {
          canInsert = false;
          canModify = false;
          canDelete = false;
        }
      }
  
  // int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  String _page = (String) serviceRequest.getAttribute("PAGE");
  Object prgAlternativa = (Object)sessionContainer.getAttribute("prgAlternativa");
  String strAlternativa="";
  if (prgAlternativa != null) {
  strAlternativa=prgAlternativa.toString();
  }

  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
    
//  InfCorrentiAzienda infCorrentiAzienda= new InfCorrentiAzienda(prgAzienda,prgUnita,prgRichiestaAz);
  PageAttribs attributiProfilo = new PageAttribs(user, "IdoEtaEsperienzaPage");
  boolean canInsertProfile= attributiProfilo.containsButton("INSERISCI");

  LinguettaAlternativa linguettaAlternativa= new LinguettaAlternativa(prgAzienda,prgUnita, prgRichiestaAz, prgAlternativa,  _funzione, _page, (!canInsertProfile));
  Linguette linguette = new Linguette( user, _funzione, _page, new BigDecimal(prgRichiestaAz));
  linguette.setCodiceItem("prgRichiestaAz");


    
   String apriDiv = (String) serviceRequest.getAttribute("APRIDIV");

  if(apriDiv == null) { apriDiv = "none"; }
  else { apriDiv = ""; }
  
  String url_nuovo = "AdapterHTTP?PAGE=IdoAgevolazioniPage" + 
                     "&PRGAZIENDA=" + prgAzienda + 
                     "&PRGRICHIESTAAZ=" + prgRichiestaAz +                      
                     "&PRGUNITA=" + prgUnita + 
                     "&CDNFUNZIONE=" + _funzione + 
                     "&APRIDIV=1";
                     
 
    if(serviceResponse.containsAttribute("M_GetDettaglioAgevolazioneRichiesta")) { 
    nuovo = false; 
  }
  else { 
    nuovo = true; 
  }

   if(!nuovo) {


  SourceBean cont = (SourceBean) serviceResponse.getAttribute("M_GETDETTAGLIOAGEVOLAZIONERICHIESTA");
  SourceBean row = (SourceBean) cont.getAttribute("ROWS.ROW");
  BigDecimal prgAgevolazione = (BigDecimal) row.getAttribute("PRGAGEVOLAZIONE");
 strPrgAgevolazione = prgAgevolazione.toString();
   
        codAgevolazione= row.containsAttribute("CODAGEVOLAZIONE") ? row.getAttribute("CODAGEVOLAZIONE").toString() : "";
        cdnUtins= row.containsAttribute("cdnUtins") ? row.getAttribute("cdnUtins").toString() : "";
        dtmins=row.containsAttribute("dtmins") ? row.getAttribute("dtmins").toString() : "";
        cdnUtmod=row.containsAttribute("cdnUtmod") ? row.getAttribute("cdnUtmod").toString() : "";
        dtmmod=row.containsAttribute("dtmmod") ? row.getAttribute("dtmmod").toString() : "";
  
        linguette.setCodiceItem("prgRichiestaAz");

        // NOTE: Attributi della pagina (pulsanti e link) 
        btnAnnulla= canModify ? "Chiudi senza aggiornare" : "Chiudi";
       
        String htmlStreamTop = StyleUtils.roundTopTable(canModify);
        String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);



 
  if (prgAlternativa != null) {
    strAlternativa=prgAlternativa.toString();
  }

  objFlgIndispensabile=row.getAttribute("FLGINDISPENSABILE");
  if (objFlgIndispensabile != null) {
    strFlgIndispensabile = objFlgIndispensabile.toString();
  }


   }
   
    if(nuovo) {
   		canManage = canInsert;
   	}
	else {
		canManage = canModify;
	}
	
    Testata operatoreInfo = null;
  	operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);

%>
<%@ include file="_infCorrentiAzienda.inc" %> 
<html>

<head>
  <title>Conoscenze Informatiche</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  <af:linkScript path="../../js/"/>
  <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
  <SCRIPT TYPE="text/javascript">

  </SCRIPT>

  <SCRIPT TYPE="text/javascript">

<!--


var flagChanged = false;

function Select(PRGAGEVOLAZIONE) {
   // Se la pagina è già in submit, ignoro questo nuovo invio!
   if (isInSubmit()) return;
   
    var s= "AdapterHTTP?PAGE=IdoAgevolazioniPage";
    s += "&MODULE=M_GetDettaglioAgevolazioneRichiesta";
    s += "&PRGAGEVOLAZIONE=" + PRGAGEVOLAZIONE;
    s += "&PRGAZIENDA=<%=prgAzienda%>";
    s += "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
    s += "&PRGUNITA=<%=prgUnita%>";
    s += "&CDNFUNZIONE=<%= _funzione %>";
    s+= "&APRIDIV=1";
    setWindowLocation(s);

  }

 function Delete(PRGAGEVOLAZIONE, tipo) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;

    var s="Sicuri di voler rimuovere l'agevolazione:\n \'" + tipo.replace('^','\'');

    s += "\' ?";
    
    if ( confirm(s) ) {

      var s= "AdapterHTTP?PAGE=IdoAgevolazioniPage";
      s+= "&MODULE=M_DeleteAgevolazioneRichiesta";
      s += "&PRGAGEVOLAZIONE=" + PRGAGEVOLAZIONE;
      s += "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
      s += "&PRGAZIENDA=<%=prgAzienda%>";
      s += "&PRGUNITA=<%=prgUnita%>";
      s += "&CDNFUNZIONE=<%=_funzione%>";

      setWindowLocation(s);
    }
  }

function Insert() {
  document.MainForm.MODULE.value = "M_SaveAgevolazioniRichiesta";
  if(controllaFunzTL())  {
    doFormSubmit(document.MainForm);
    }
  else
    return;
}
    
function Update()
  {
    if (controllaFunzTL()) {
      document.MainForm.MODULE.value = "M_UpdateAgevolazioneRichiesta";
      doFormSubmit(document.MainForm);
    }
  }
      
function fieldChanged() {
    flagChanged = true;
  }
  
   function getFormObj() {
     return document.MainForm;
   }
-->
</SCRIPT>

<SCRIPT TYPE="text/javascript">
 
  window.top.menu.caricaMenuAzienda(<%=_funzione%>, <%=prgAzienda%>, <%=prgUnita%>);
  
</SCRIPT> 


  <script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"prgAzienda="+prgAzienda+"&prgUnita="+prgUnita);
      %>
  </script>


</head>

<body class="gestione" onload="rinfresca()">
<% 
  infCorrentiAzienda.show(out); 
  linguettaAlternativa.show(out); 
%>
<BR/>
<%
linguette.show(out);
%>  
<af:form method="POST" action="AdapterHTTP" name="MainForm">
<input type="hidden" name="PAGE" value="IdoAgevolazioniPage"/>
<input type="hidden" name="MODULE" value="M_SaveAgevolazioniRichiesta"/>
<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
<input type="hidden" name="PRGRICHIESTAAZ" value="<%=prgRichiestaAz%>">
<input type="hidden" name="PRGAGEVOLAZIONE" value="<%=strPrgAgevolazione%>">
<input type="hidden" name="CODAGEVOLAZIONE" value="<%=codAgevolazione%>"/>

  <center>
        <font color="green">
          <af:showMessages prefix="M_SaveAgevolazioniRichiesta"/>
          <af:showMessages prefix="M_DeleteAgevolazioneRichiesta"/>
          <af:showMessages prefix="M_UpdateAgevolazioneRichiesta"/>
        </font>
        <font color="red">
          <af:showErrors />
        </font>
      </center>
  
      <p align="center">
          <af:list moduleName="M_GetAgevolazioniRichiesta" skipNavigationButton="1"
                 canDelete="<%=canDelete ? \"1\" : \"0\"%>" 
                 canInsert="<%=canModify ? \"1\" : \"0\"%>" 
                 jsSelect="Select" jsDelete="Delete"/>          
      </p>
      <%
      if(canInsert) {
      %>
          <p align="center">
            <input type="button" class="pulsanti" onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>')" value="Nuova agevolazione"/>   
          
          </p>
      <%}%>

<%
  String divStreamTop = StyleUtils.roundLayerTop(canManage);
  String divStreamBottom = StyleUtils.roundLayerBottom(canManage);
%>
   
        <div id="divLayerDett" name="divLayerDett" class="t_layerDett"
         style="position:absolute; width:80%; left:50; top:200px; z-index:6; display:<%=apriDiv%>;">
    
         <%out.print(divStreamTop);%>

        <table width="100%">
          <tr>
            <td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerDett');return false"></td>
            <td height="16" class="azzurro_bianco">
            <%if(nuovo){%>
              Nuova agevolazione
            <%} else {%>
              Agevolazione
            <%}%>   
            </td>
            <td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
          </tr>
        </table>


  <% if(!nuovo) { %>
  <table align="center"  width="100%">
  <%@ include file="Agevolazione_Elemento.inc" %>
  </table>
  <% } else  { %>
  <table align="center"  width="100%">
  <%@ include file="Agevolazione_Elemento.inc" %>
  </table>

  
    <% }  %>
  
          <!-- ************** -->
          <!--<td colspan="2">-->
                      <table class="main" width="100%">
                        <tr><td>&nbsp;</td></tr>
                            <%if(nuovo && canInsert) {%>
                              <tr>
                                <td colspan="2" align="center">
                                <input type="button" class="pulsanti" name="inserisci" value="Inserisci" onClick="Insert();">
                                <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="ChiudiDivLayer('divLayerDett')">
                                </td>
                              </tr>
                           <%}
                           else {%>
                              <td width="40%" align="right">
                            <%if (canModify) {%>    
                              <input type="button" class="pulsanti" name="salva" value="Aggiorna" onClick="Update();">                
                              </td>
                          <%}
                          }%>
                          <td width="60%" align="left"> 
                           <%if((canModify) && (!nuovo)){%>
                                <input class="pulsante" type="button" class="pulsanti" name="annulla" value="Chiudi senza aggiornare"
                                onclick="ChiudiDivLayer('divLayerDett');">
                           <%}
                              else {
                                    if(!canModify) {%>
                                      <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="ChiudiDivLayer('divLayerDett');">                                                                
                                  <%}
                                }%>
                          </td>        
                    </td>
                  </tr>
      </div>
     
    </TABLE>
<%if (!nuovo) {%>                          
  <p align="center">
<% operatoreInfo.showHTML(out); %>
  </p>
<%}%>
   <%out.print(divStreamBottom);%>    
  </af:form>
</body>
<script language="javascript">
</script>
</html>

<% } %>  

  

