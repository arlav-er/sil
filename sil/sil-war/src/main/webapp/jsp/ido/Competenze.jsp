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


  Object objFlgIndispensabile = null;
  String strFlgIndispensabile="";
  String strCompetenza="Consulta Competenza";
  String cdnUtins="";
  String dtmins="";
  String cdnUtmod="";
  String dtmmod="";
  String moduleName="MAggiornaCompetenzaRichiesta";
  String btnSalva="Aggiorna";
  String btnAnnulla="";
  String codTipoCompetenza="";
  String strDescTipoCompetenza="";
  String codCompetenza="";
  String strDescCompetenza="";
  String strPrgCompetenza="";

  boolean canInsert= false;
  boolean canModify= false;
  boolean canDelete= false;
  boolean canManage = false;
  
  String cdnStatoRich = serviceResponse.getAttribute("M_GetTestataRichiesta.ROWS.ROW.cdnStatoRich").toString();
  

  
    Object prgAlternativa = (Object)sessionContainer.getAttribute("prgAlternativa");
    String strAlternativa="";
    if (prgAlternativa != null) {
    strAlternativa=prgAlternativa.toString();
    }

  String _page = (String) serviceRequest.getAttribute("PAGE");

  
    Testata operatoreInfo   = null;
    boolean nuovo = true;

    ProfileDataFilter filter = new ProfileDataFilter(user, _page);
    filter.setPrgAzienda(prgAzienda);
    filter.setPrgUnita(prgUnita);
  
    boolean canView=filter.canViewUnitaAzienda();
    if ( !canView ){
      response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
    }else{
      PageAttribs attributi = new PageAttribs(user, "IdoCompetenzePage");
      if (cdnStatoRich.compareTo("4")!=0 && cdnStatoRich.compareTo("5")!=0){
	      canInsert= attributi.containsButton("INSERISCI");      
	      canModify= attributi.containsButton("AGGIORNA");
	      canDelete= attributi.containsButton("CANCELLA");
	     }

    if ( !canModify && !canDelete && ! canInsert) {
      
      } else {
        boolean canEdit = filter.canEditUnitaAzienda();
        if ( !canEdit ) {
        	canInsert = false;
	        canModify = false;
            canDelete = false;
        }
      }
    
    String fieldReadOnly;
    String btnChiudiDettaglio="";
    
    int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
    
    //InfCorrentiAzienda infCorrentiAzienda= new InfCorrentiAzienda(prgAzienda,prgUnita,prgRichiestaAz);
    PageAttribs attributiProfilo = new PageAttribs(user, "IdoEtaEsperienzaPage");
    boolean canInsertProfile= attributiProfilo.containsButton("INSERISCI");
    
    LinguettaAlternativa linguettaAlternativa= new LinguettaAlternativa(prgAzienda,prgUnita, prgRichiestaAz, prgAlternativa,  _funzione, _page, (!canInsertProfile));
    Linguette linguette = new Linguette( user, _funzione, _page, new BigDecimal(prgRichiestaAz));
    linguette.setCodiceItem("prgRichiestaAz");


    
     String apriDiv = (String) serviceRequest.getAttribute("APRIDIV");

  if(apriDiv == null) { apriDiv = "none"; }
  else { apriDiv = ""; }
  
  String url_nuovo = "AdapterHTTP?PAGE=IdoCompetenzePage" + 
                     "&PRGAZIENDA=" + prgAzienda + 
                     "&PRGRICHIESTAAZ=" + prgRichiestaAz +                      
                     "&PRGUNITA=" + prgUnita + 
                     "&CDNFUNZIONE=" + _funzione + 
                     "&APRIDIV=1";
                     


    if(serviceResponse.containsAttribute("M_GETDETTAGLIOCOMPETENZARICHIESTA")) { 
    nuovo = false; 
  }
  else { 
    nuovo = true; 
  }

                  SourceBean row_dettInfo = null;
                  SourceBean row_Dett = null;
                   Vector competenze_Rows = null;
                  competenze_Rows=serviceResponse.getAttributeAsVector("M_GETCOMPETENZE.ROWS.ROW");
                  SourceBean cont = (SourceBean) serviceResponse.getAttribute("M_GETDETTAGLIOCOMPETENZARICHIESTA");



  if(!nuovo) {
  //Sono in modalità dettaglio
    Vector vectConoInfo= serviceResponse.getAttributeAsVector("M_GETDETTAGLIOCOMPETENZARICHIESTA.ROWS.ROW");
  //    if ( (vectConoInfo != null) && (vectConoInfo.size() > 0) ) {
        



                SourceBean row = (SourceBean) cont.getAttribute("ROWS.ROW");
                BigDecimal prgCompetenza = (BigDecimal) row.getAttribute("PRGCOMPETENZA");
                strPrgCompetenza = prgCompetenza.toString();
                strDescCompetenza=row.containsAttribute("DESCRIZIONECOMPETENZA") ? row.getAttribute("DESCRIZIONECOMPETENZA").toString() : "";
                strDescTipoCompetenza=row.containsAttribute("DESCRIZIONETIPOCOMPETENZA") ? row.getAttribute("DESCRIZIONETIPOCOMPETENZA").toString() : "";
                codTipoCompetenza= row.containsAttribute("CODTIPOCOMPETENZA") ? row.getAttribute("CODTIPOCOMPETENZA").toString() : "";
                codCompetenza= row.containsAttribute("CODCOMPETENZA") ? row.getAttribute("CODCOMPETENZA").toString() : "";
                cdnUtins= row.containsAttribute("cdnUtins") ? row.getAttribute("cdnUtins").toString() : "";
                dtmins=row.containsAttribute("dtmins") ? row.getAttribute("dtmins").toString() : "";
                cdnUtmod=row.containsAttribute("cdnUtmod") ? row.getAttribute("cdnUtmod").toString() : "";
                dtmmod=row.containsAttribute("dtmmod") ? row.getAttribute("dtmmod").toString() : "";
  
                

                  // NOTE: Attributi della pagina (pulsanti e link) 
                  btnAnnulla= canModify ? "Chiudi senza aggiornare" : "Chiudi";
                  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
                  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
                
  
                  BigDecimal cdnGradoConoscenza=row.containsAttribute("CDNGRADO") ? (BigDecimal)row.getAttribute("CDNGRADO") : null;


               
  
                    objFlgIndispensabile=row.getAttribute("FLGINDISPENSABILE");
                    if (objFlgIndispensabile != null) {
                      strFlgIndispensabile = objFlgIndispensabile.toString();
                    }


                    operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);

    //  }
 
  }
  if(nuovo) {
   		canManage = canInsert;
  	}
	else {
		canManage = canModify;
	}
	
  if (canModify) {
      btnChiudiDettaglio = "Chiudi senza aggiornare";
      fieldReadOnly="false";
    }
    else {
      btnChiudiDettaglio="Chiudi";
      fieldReadOnly="true";
    }
    

%>
<%@ include file="_infCorrentiAzienda.inc" %> 
<html>

<head>
  <title>Competenze</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  <af:linkScript path="../../js/"/>
  <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
  <SCRIPT TYPE="text/javascript">

  </SCRIPT>

  <SCRIPT TYPE="text/javascript">

<!--


var flagChanged = false;

function Select(PRGCOMPETENZA) {
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;
   
    var s= "AdapterHTTP?PAGE=IdoCompetenzePage";
    s += "&MODULE=M_GETDETTAGLIOCOMPETENZARICHIESTA";
    s += "&PRGCOMPETENZA=" + PRGCOMPETENZA;
    s += "&PRGAZIENDA=<%=prgAzienda%>";
    s += "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
    s += "&PRGUNITA=<%=prgUnita%>";
    s += "&CDNFUNZIONE=<%= _funzione %>";
    s+= "&APRIDIV=1";
    setWindowLocation(s);

  }

 function Delete(PRGCOMPETENZA, tipo) {
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;

    var s="Sicuri di voler rimuovere la competenza:\n \'" + tipo.replace('^','\'');

    s += "\' ?";
    
    if ( confirm(s) ) {

      var s= "AdapterHTTP?PAGE=IdoCompetenzePage";
      s+= "&MODULE=MDeleteCompetenzaRichiesta";
      s += "&PRGCOMPETENZA=" + PRGCOMPETENZA;
      s += "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
      s += "&PRGAZIENDA=<%=prgAzienda%>";
      s += "&PRGUNITA=<%=prgUnita%>";
      s += "&CDNFUNZIONE=<%=_funzione%>";

      setWindowLocation(s);
    }
  }

function Insert() {
  document.MainForm.MODULE.value = "MSaveCompetenzaRichiesta";
  if(controllaFunzTL())  {
    doFormSubmit(document.MainForm);
    }
  else
    return;
}
    
function Update()
  {
    if (controllaFunzTL()) {
      document.MainForm.MODULE.value = "MAggiornaCompetenzaRichiesta";
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

<%@ include file="Competenze_CommonScripts.inc" %>
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
      
          <input type="hidden" name="PAGE" value="IdoCompetenzePage">
          <input type="hidden" name="MODULE" value="MSaveCompetenzaRichiesta"/>
          <input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
          <input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"/>
          <input type="hidden" name="PRGUNITA" value="<%=prgUnita%>"/>

          <input type="hidden" name="PRGRICHIESTAAZ" value="<%=prgRichiestaAz%>"/>
          <input type="hidden" name="PRGCOMPETENZA" value="<%=strPrgCompetenza%>"/>

          
  
      
 


      <center>
          <af:showMessages prefix="MSaveCompetenzaRichiesta"/>
          <af:showMessages prefix="MDeleteCompetenzaRichiesta"/>
          <af:showMessages prefix="MAggiornaCompetenzaRichiesta"/>
          <af:showErrors />
      </center>
  
      <p align="center">
          <af:list moduleName="M_GetCompetenzeRichiesta" skipNavigationButton="1"
                 canDelete="<%=canDelete ? \"1\" : \"0\"%>" 
                 canInsert="<%=canInsert ? \"1\" : \"0\"%>" 
                 jsSelect="Select" jsDelete="Delete"/>          
      </p>
      <%
      if(canInsert) {
      %>
          <p align="center">
            <input type="button" class="pulsanti" onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>')" value="Nuova competenza"/>   
          
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
              Nuova Competenza
            <%} else {%>
              Competenza
            <%}%>   
            </td>
            <td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
          </tr>
        </table>
 <table align="center"  width="100%" border="0"> 

 <% if (!nuovo) { %>   
  <%@ include file="Competenza_Elemento.inc" %>
<% } else {%>
<table align="center"  width="100%">
  <tr>
  <td class="etichetta" nowrap>Profilo n.&nbsp;</td>
  <td class="campo" nowrap><INPUT type="text" name="prgAlternativa" size="2" value="<%=strAlternativa%>" READONLY class="inputView"/></td>
  </tr>
  <tr>
  <td class="etichetta" nowrap>Tipo di competenza &nbsp;</td>
  <td class="campo" nowrap>
  <af:comboBox name="CODTIPOCOMPETENZA" size="1" title="Tipo di competenza"
                     multiple="false" required="true"
                     focusOn="false" moduleName="M_GetTipiCompetenza"
                     addBlank="true" blankValue=""
                     onChange="javascript:caricaCompetenze(MainForm.CODTIPOCOMPETENZA.value,'','nuovo');"/>
  </td></tr>

  <tr>
  <td class="etichetta" nowrap>Competenza &nbsp;</td>
  <td class="campo" nowrap>
  <af:comboBox multiple="true" title="Competenza" name="CODCOMPETENZA" required="true" size="4"/>
  </tr>
  <tr>
  <td class="etichetta" nowrap>Indispensabile &nbsp;</td>
    <td class="campo" nowrap>
    <af:comboBox name="FLGINDISPENSABILE"
                 title="Indispensabile" required="false"
                 disabled="<%= String.valueOf(!canManage) %>"
                 selectedValue="<%= strFlgIndispensabile %>">
      <option value=""  <% if ( "".equals(strFlgIndispensabile) )  { out.print("SELECTED=\"true\""); } %> ></option>
      <option value="S" <% if ( "S".equals(strFlgIndispensabile) ) { out.print("SELECTED=\"true\""); } %> >Sì</option>
      <option value="N" <% if ( "N".equals(strFlgIndispensabile) ) { out.print("SELECTED=\"true\""); } %> >No</option>
    </af:comboBox>  
    </td>
  </tr>
  </table>
  
  <% } %>    
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
                           else if (canModify) {
                           %> 
                           	 <tr>
                           	  <td width="40%" align="right">
                              <input type="button" class="pulsanti" name="salva" value="Aggiorna" onClick="Update();">
                              <input class="pulsante" type="button" class="pulsanti" name="annulla" value="<%=btnChiudiDettaglio %>"
                                onclick="ChiudiDivLayer('divLayerDett');">
                              </td>                                
                             </tr>
                          <%} else { %>
                             <tr>
                          		<td>
                                  <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="ChiudiDivLayer('divLayerDett');">                                                                
                                </td>
                              </tr>
                          <% }%>
     
   					 </TABLE>
<%if (!nuovo) {%>                          
  <p align="center">
<% operatoreInfo.showHTML(out); %>
  </p>
<%}%>
   <%out.print(divStreamBottom);%>    
  </af:form>
</body>

</html>

<% } %>