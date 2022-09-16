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
  String cdnUtins="";
  String dtmins="";
  String cdnUtmod="";
  String dtmmod="";
  String moduleName="MAggiornaInfoRichiesta";
  String btnSalva="Aggiorna";
  String btnAnnulla;
  String codTipo="";
  String strDescTipo="";
  String strDescDettTipo="";
  String codDettTipo="";
  String strPrgInfo = "";
  String strGradoConoscenza = "";
  Object objFlgIndispensabile = null;
  String strFlgIndispensabile="";
  Object prgAlternativa = (Object)sessionContainer.getAttribute("prgAlternativa");
  String strAlternativa="";

  boolean canInsert= false;
  boolean canModify= false;
  boolean canDelete= false;
  boolean canManage = false;
  
  String cdnStatoRich = serviceResponse.getAttribute("M_GetTestataRichiesta.ROWS.ROW.cdnStatoRich").toString();

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
    PageAttribs attributi = new PageAttribs(user, "IdoInformaticaPage");
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
  
  String url_nuovo = "AdapterHTTP?PAGE=IdoInformaticaPage" + 
                     "&PRGAZIENDA=" + prgAzienda + 
                     "&PRGRICHIESTAAZ=" + prgRichiestaAz +                      
                     "&PRGUNITA=" + prgUnita + 
                     "&CDNFUNZIONE=" + _funzione + 
                     "&APRIDIV=1";
                     


    if(serviceResponse.containsAttribute("M_GetDettaglioInfoRichiesta")) { 
    nuovo = false; 
  }
  else { 
    nuovo = true; 
  }

                  Vector tipiDettInfoRows = null;
                  tipiDettInfoRows=serviceResponse.getAttributeAsVector("M_LISTDETTAGLIALLCONOSCENZAINFO.ROWS.ROW");
                  SourceBean row_dettInfo = null;
  
                  SourceBean cont = (SourceBean) serviceResponse.getAttribute("M_GETDETTAGLIOINFORICHIESTA");



  if(!nuovo) {
  //Sono in modalità dettaglio
    Vector vectConoInfo= serviceResponse.getAttributeAsVector("M_GetDettaglioInfoRichiesta.ROWS.ROW");
      if ( (vectConoInfo != null) && (vectConoInfo.size() > 0) ) {
        



                  SourceBean row = (SourceBean) cont.getAttribute("ROWS.ROW");

                  BigDecimal prgInfo = (BigDecimal) row.getAttribute("PRGINFO");
                  strPrgInfo = prgInfo.toString();
                  strDescTipo=row.containsAttribute("STRDESCRIZIONE") ? row.getAttribute("STRDESCRIZIONE").toString() : "";
                  codTipo= row.containsAttribute("CODTIPOINFO") ? row.getAttribute("CODTIPOINFO").toString() : "";
                  codDettTipo= row.containsAttribute("CODICE") ? row.getAttribute("CODICE").toString() : "";
                  cdnUtins= row.containsAttribute("cdnUtins") ? row.getAttribute("cdnUtins").toString() : "";
                  dtmins=row.containsAttribute("dtmins") ? row.getAttribute("dtmins").toString() : "";
                  cdnUtmod=row.containsAttribute("cdnUtmod") ? row.getAttribute("cdnUtmod").toString() : "";
                  dtmmod=row.containsAttribute("dtmmod") ? row.getAttribute("dtmmod").toString() : "";
                  prgRichiestaAz=row.containsAttribute("PRGRICHIESTAAZ") ? row.getAttribute("PRGRICHIESTAAZ").toString() : "";

               //   prgAzienda=row.containsAttribute("PRGAZIENDA") ? row.getAttribute("PRGAZIENDA").toString() : "";
               //   prgUnita=row.containsAttribute("PRGUNITA") ? row.getAttribute("PRGRICHIESTAAZ").toString() : "";              
                

                  // NOTE: Attributi della pagina (pulsanti e link) 
                  btnAnnulla= canModify ? "Chiudi senza aggiornare" : "Chiudi";
                  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
                  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
                
  
                  BigDecimal cdnGradoConoscenza=row.containsAttribute("CDNGRADO") ? (BigDecimal)row.getAttribute("CDNGRADO") : null;


                  if (cdnGradoConoscenza != null) {
                    strGradoConoscenza = cdnGradoConoscenza.toString();
                  }
  
                  objFlgIndispensabile=row.getAttribute("FLGINDISPENSABILE");
                  if (objFlgIndispensabile != null) {
                    strFlgIndispensabile = objFlgIndispensabile.toString();
                  }


                    if (cdnGradoConoscenza != null) {
                      strGradoConoscenza = cdnGradoConoscenza.toString();
                    }
  
                    objFlgIndispensabile=row.getAttribute("FLGINDISPENSABILE");
                    if (objFlgIndispensabile != null) {
                      strFlgIndispensabile = objFlgIndispensabile.toString();
                    }


                    operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);

      }
 
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

function Select(prgInfo) {
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;
   
    var s= "AdapterHTTP?PAGE=IdoInformaticaPage";
    s += "&MODULE=M_GetDettaglioInfoRichiesta";
    s += "&PRGINFO=" + prgInfo;
    s += "&PRGAZIENDA=<%=prgAzienda%>";
    s += "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
    s += "&PRGUNITA=<%=prgUnita%>";
    s += "&CDNFUNZIONE=<%= _funzione %>";
    s+= "&APRIDIV=1";
    setWindowLocation(s);  

  }
  
  
 
  

 function Delete(prgInfo, dettaglio) {
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;

    var s="Sicuri di voler rimuovere la conoscenza informatica:\n \'";
    s+= dettaglio + " ?";
    <!--s += tipo.replace(/\^/g, '\'');-->   

    s += "\' ?";
    
    if ( confirm(s) ) {
   
      var s= "AdapterHTTP?PAGE=IdoInformaticaPage";
      s+= "&MODULE=MDeleteInfoRichiesta";
      s += "&PRGINFO=" + prgInfo;
      s += "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
      s += "&PRGAZIENDA=<%=prgAzienda%>";
      s += "&PRGUNITA=<%=prgUnita%>";
      s += "&CDNFUNZIONE=<%=_funzione%>";

      setWindowLocation(s);
    }
  }

function Insert() {
  document.MainForm.MODULE.value = "MSaveInfoRichiesta";   
  if(controllaFunzTL())  {
    doFormSubmit(document.MainForm);
    }
  else
    return;
}


function codificaInesistente(combo, scelta){
	if(combo[combo.selectedIndex].value == scelta){
		return true;
	}else{
		return false;
	}
}
    
function Update()
  {
  	if(codificaInesistente(document.MainForm.CODTIPOINFO, 'NT')){
  		alert("Scelta non valida.")
  		return;
  	}
  	
    if (controllaFunzTL()) {
      document.MainForm.MODULE.value = "MAggiornaInfoRichiesta";
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

<%@ include file="ConoInfo_CommonScripts.inc" %>
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
        
          <input type="hidden" name="PAGE" value="IdoInformaticaPage">
          <input type="hidden" name="MODULE" value="MSaveInfoRichiesta"/>
          <input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
          <input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"/>
          <input type="hidden" name="PRGUNITA" value="<%=prgUnita%>"/>

          <input type="hidden" name="PRGRICHIESTAAZ" value="<%=prgRichiestaAz%>"/>
          <input type="hidden" name="PRGINFO" value="<%=strPrgInfo%>"/>
  
      
 


      <center>
        <font color="green">
          <af:showMessages prefix="MSaveInfoRichiesta"/>
          <af:showMessages prefix="MDeleteInfoRichiesta"/>
          <af:showMessages prefix="MAggiornaInfoRichiesta"/>
        </font>
        <font color="red">
          <af:showErrors />
        </font>
      </center>
  
      <p align="center">
          <af:list moduleName="M_GetInformaticaRichiesta" skipNavigationButton="1"
                 canDelete="<%=canDelete ? \"1\" : \"0\"%>" 
                 canInsert="<%=canInsert ? \"1\" : \"0\"%>" 
                 jsSelect="Select" jsDelete="Delete"/>          
      </p>
      <%
      if(canInsert) {
      %>
          <p align="center">
            <input type="button" class="pulsanti" onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>')" value="Nuova conoscenza informatica"/>   
          
          </p>
      <%}
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
              Nuova conoscenza informatica
            <%} else {%>
              Conoscenza Informatica
            <%}%>   
            </td>
            <td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
          </tr>
        </table>
		<br>
 <% if (!nuovo) { %>   
<%@ include file="ConoInfo_Elemento.inc" %>
<% } else {%>

 <table align="center"  width="100%">

  <tr>
  <td class="etichetta" nowrap>Profilo n.&nbsp;</td>
  <td class="campo" nowrap><INPUT type="text" name="prgAlternativa" size="2" value="<%=strAlternativa%>" READONLY class="inputView"/></td>
  </tr>
  <tr>
  <td class="etichetta" nowrap>Tipo &nbsp;</td>
  <td class="campo" nowrap>
  <af:comboBox name="CODTIPOINFO" size="1" title="Tipo Conoscenza"
                     multiple="false" required="true"
                     focusOn="false" moduleName="M_ListTipiConoscenzaInfo"
                     addBlank="true" blankValue=""
                     onChange="inibisciScelta(this, 'NT');javascript:caricaDettInfo(MainForm.CODTIPOINFO.value,'','nuovo');"/>
  </td></tr>

  <tr>
  <td class="etichetta">Dettaglio &nbsp;</td>
  <td class="campo">
  <af:comboBox multiple="true" title="Dettaglio" name="CODICE" required="true" disabled="false" size="4"/>
  </tr>
  <tr>
  <td class="etichetta" nowrap>Livello &nbsp;</td>
  <td class="campo" nowrap>
  <af:comboBox name="CDNGRADO" size="1" title="Livello Conoscenza"
                     multiple="false" required="true"
                     focusOn="false" moduleName="M_ListGradiConoscenzaInfo"
                     addBlank="true" blankValue=""/>
  </td></tr>
  <tr>
  <td class="etichetta" nowrap>Indispensabile &nbsp;</td>
    <td class="campo" nowrap>
    <af:comboBox name="FLGINDISPENSABILE"
                 title="Indispensabile" required="false"
                 disabled="<%= String.valueOf(!canModify) %>"
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
                            <%if(nuovo) {%>
                              <tr>
                                <td colspan="2" align="center">
                                <input type="button" class="pulsanti" name="inserisci" value="Inserisci" onClick="Insert();">
                                <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="ChiudiDivLayer('divLayerDett')">
                                </td>
                              </tr>
                           <%}
                           else {%>
                              <td width="40%" align="right">
                            <%if (canModify) {
                           %>    
                              <input type="button" class="pulsanti" name="salva" value="Aggiorna" onClick="Update();">
                              </td>
                          <%}
                          }%>
                          <td width="60%" align="left"> 
                           <%if((canModify) && (!nuovo)){%>
                                <input class="pulsante" type="button" class="pulsanti" name="annulla" value="<%=btnChiudiDettaglio %>"
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
				  <tr>
				  <td colspan="4" align="center">         
					<%if (!nuovo) {%>                          
					  <p align="center">
					<% operatoreInfo.showHTML(out); %>
					  </p>
					<%}%>
				  </td>
				  </tr>
      </div>
    </TABLE>
 <%out.print(divStreamBottom);%>                               
  </af:form>
</body>
<script language="javascript">
  caricaDettInfo('<%=codTipo%>','<%=codDettTipo%>','dettaglio');    
  if (!<%=canManage%>) { document.MainForm.CODICE.disabled = true; }
</script>
</html>

<% } %>