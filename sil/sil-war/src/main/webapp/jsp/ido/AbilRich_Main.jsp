<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.User,
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  com.engiweb.framework.dbaccess.sql.TimestampDecorator,
  it.eng.sil.security.PageAttribs,
  it.eng.sil.security.ProfileDataFilter,
  com.engiweb.framework.security.*"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  String strFlgIndispensabile="";
  String codTipoAbilitazioneGen="";
  String codAbilitazioneGen="";
  String descAbilitazioneGen="";
  String cdnUtins="";
  String dtmins="";
  String cdnUtmod="";
  String dtmmod="";
  String flgInvioCL = "";
  Testata operatoreInfo=null;
  
  boolean nuovo = true;
  if(serviceResponse.containsAttribute("M_SelectAbilRich")) { 
     nuovo = false; 
  }
  else { 
    nuovo = true;
  }
  if(!nuovo) {
    SourceBean row = (SourceBean)serviceResponse.getAttribute("M_SelectAbilRich.ROWS.ROW");
    if (row != null) {     
      strFlgIndispensabile = (String) row.getAttribute("FLGINDISPENSABILE");
      codTipoAbilitazioneGen = (String) row.getAttribute("DESCRIZIONETIPOABILITAZIONE");
      descAbilitazioneGen = (String) row.getAttribute("STRDESCRIZIONE");
      codAbilitazioneGen = (String) row.getAttribute("CODABILITAZIONEGEN");
      flgInvioCL = StringUtils.getAttributeStrNotNull(row, "flgInvioCL");
      cdnUtins= row.containsAttribute("cdnUtins") ? row.getAttribute("cdnUtins").toString() : "";
      dtmins=row.containsAttribute("dtmins") ? row.getAttribute("dtmins").toString() : "";
      cdnUtmod=row.containsAttribute("cdnUtmod") ? row.getAttribute("cdnUtmod").toString() : "";
      dtmmod=row.containsAttribute("dtmmod") ? row.getAttribute("dtmmod").toString() : "";
      operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);    
    }
  }  

  String apriDiv = (String) serviceRequest.getAttribute("APRIDIV");
  if(apriDiv == null) { 
    apriDiv = "none"; 
  }
  else { 
    apriDiv = ""; 
  }
  Vector tipiAbilitazioniRows=null;
  tipiAbilitazioniRows=serviceResponse.getAttributeAsVector("M_GETTIPIABILITAZIONE.ROWS.ROW");
  SourceBean row_tipiAbilitazione= null;
   
  String cdnStatoRich = "";
  boolean canModify= false;
  boolean canDelete= false;
  boolean canManage = false;
  boolean canInsert = false;
  BigDecimal prgAzienda=null;
  BigDecimal prgUnita=null;
  String strPrgAziendaMenu="";
  String strPrgUnitaMenu="";
  SourceBean rigaTestata = null;
  SourceBean contTestata = (SourceBean) serviceResponse.getAttribute("M_GETTESTATARICHIESTA");
  Vector rows_VectorTestata = null;
  rows_VectorTestata = contTestata.getAttributeAsVector("ROWS.ROW");
  if (rows_VectorTestata.size()!=0) {
    rigaTestata=(SourceBean) rows_VectorTestata.elementAt(0);
    cdnStatoRich = rigaTestata.getAttribute("cdnStatoRich").toString();
    prgAzienda = (BigDecimal) rigaTestata.getAttribute("PRGAZIENDA");
    if (prgAzienda != null) {  
      strPrgAziendaMenu = prgAzienda.toString();
    }
    prgUnita = (BigDecimal) rigaTestata.getAttribute("PRGUNITA");
    if (prgUnita != null) {
      strPrgUnitaMenu = prgUnita.toString();
    }
  }

  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  String prgRichiestaAz = (String)serviceRequest.getAttribute("PRGRICHIESTAAZ");
  
  Object codiceUtente= sessionContainer.getAttribute("_CDUT_");

  String codAbilitazioneGenList      = "";
  String strDescrizioneList      = "";
  String strDescrizioneVisualizza = "";

  Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");

	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
  filter.setPrgAzienda(prgAzienda);
  filter.setPrgUnita(prgUnita);
  
	boolean canView=filter.canViewUnitaAzienda();
	if ( !canView ){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
    PageAttribs attributi = new PageAttribs(user, _page);
  
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
  
     if(nuovo) {
   		canManage = canInsert;
   	}
	else {
		canManage = canModify;
	}
  
  Linguette linguette = new Linguette( user, _funzione, _page, new BigDecimal(prgRichiestaAz));
  linguette.setCodiceItem("prgRichiestaAz");
/*
  InfCorrentiAzienda infCorrentiAzienda= new InfCorrentiAzienda(sessionContainer, strPrgAziendaMenu,strPrgUnitaMenu, prgRichiestaAz );
  String pagina_back = (String)sessionContainer.getAttribute("PAGE_RIC_BACK_TO_LIST");
  if (pagina_back!=null ) {  	
  	infCorrentiAzienda.setPaginaLista(pagina_back);
  }
  else 
  	infCorrentiAzienda.setSkipLista(true);
  */
  
  String fieldReadOnly;
  
  if (canManage) {fieldReadOnly="false";}
  else {fieldReadOnly="true";}
  
  String url_nuovo = "AdapterHTTP?PAGE=AbilRichPage" + 
                     "&PRGRICHIESTAAZ=" + prgRichiestaAz + 
                     "&CDNFUNZIONE=" + _funzione + 
                     "&APRIDIV=1";
%>

<%@ include file="_infCorrentiAzienda.inc" %> 
<html>

<head>
  <title>Abilitazioni</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  <af:linkScript path="../../js/"/>
  <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
  <SCRIPT TYPE="text/javascript">
  var flagChanged = false;
  window.top.menu.caricaMenuAzienda(<%=_funzione%>, <%=strPrgAziendaMenu%>, <%=strPrgUnitaMenu%>);
  // *****************************
    // Serve per il menu contestuale
    // *****************************

    function caricaAbilitazioni(codAbilitazioneGen) {	 
	  if (codAbilitazioneGen == "AL") {
		  document.Frm1.INVIA_CLIC_LAVORO.disabled = false;
		  //document.Frm1.FLGINVIOCL.readonly = false;
	  }
	  else {
		  document.Frm1.INVIA_CLIC_LAVORO.disabled = true;
		  //document.Frm1.FLGINVIOCL.readonly = true;
	  }
        
      var abil_tipo=new Array();
      var abil_cod=new Array();
      var abil_des=new Array();
  <%  for(int i=0; i<tipiAbilitazioniRows.size(); i++)  { 
        row_tipiAbilitazione = (SourceBean) tipiAbilitazioniRows.elementAt(i);
        out.print("abil_tipo["+i+"]=\""+ row_tipiAbilitazione.getAttribute("TIPO").toString()+"\";\n");
        out.print("abil_cod["+i+"]=\""+ row_tipiAbilitazione.getAttribute("CODICE").toString()+"\";\n");
        out.print("abil_des["+i+"]=\""+ row_tipiAbilitazione.getAttribute("DESCRIZIONE").toString()+"\";\n");              
      }
  %>
       i=0;
       j=0;
       maxcombo=15;


       while (document.Frm1.CODABILITAZIONEGEN.options.length>0) {
            document.Frm1.CODABILITAZIONEGEN.options[0]=null;
        }

          for (i=0; i<abil_tipo.length ;i++) {
           if (abil_tipo[i]==codAbilitazioneGen) {
              document.Frm1.CODABILITAZIONEGEN.options[j]=new Option(abil_des[i], abil_cod[i], false, false);
               j++;
           }
         } 
      if (j>maxcombo) {j=maxcombo;} //imposto un massimo per la lunghezza della combo
      document.Frm1.CODABILITAZIONEGEN.size=j;
    }


    function caricaMenuDX(cdnLavoratore, strNome, strCognome){

      var url = "AdapterHTTP?PAGE=MenuCompletoPage"
      url += "&CDNLAVORATORE=" + cdnLavoratore;
      url += "&STRNOME=" + strNome;
      url += "&STRCOGNOME=" + strCognome;
    
      window.top.menu.location = url;
    }
  
    function AbilRichDelete(codAbil, strAbil) {
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
      if (isInSubmit()) return;

      strAbil=strAbil.replace('\'', '´');    
      if ( confirm("Eliminare abilitazione " + strAbil + " ?") ) {
        var s= "AdapterHTTP?PAGE=AbilRichPage";
        s += "&MODULE=M_DeleteAbilRich";
        s += "&CODABILITAZIONEGEN=" + codAbil;
        s += "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
        s += "&CDNFUNZIONE=<%= _funzione %>";
        setWindowLocation(s);
      }
    }

    function AbilRichSelect(codAbil) {
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
      if (isInSubmit()) return;

      var s= "AdapterHTTP?PAGE=AbilRichPage";
      s += "&DETTAGLIO=True";
      s += "&APRIDIV=1";
      s += "&CODABILITAZIONEGEN=" + codAbil;
      s += "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
      s += "&CDNFUNZIONE=<%= _funzione %>";
      setWindowLocation(s);
    }

    function checkFlagCliclavoro() {
              
      if (document.Frm1.codTipoAbilitazioneGen.value == "AL") {
  		  document.Frm1.INVIA_CLIC_LAVORO.disabled = false;  		 
  	  }
  	  else {
  		  document.Frm1.INVIA_CLIC_LAVORO.disabled = true;  	
	  		if(document.Frm1.INVIA_CLIC_LAVORO.checked) {
	    		document.Frm1.FLGINVIOCL.value="S";
	    	} else {
	    		document.Frm1.FLGINVIOCL.value="N";
	    	}
  	  }
  	  
    }
    
    function settaFlag() {
    	if(document.Frm1.INVIA_CLIC_LAVORO.checked) {
    		document.Frm1.FLGINVIOCL.value="S";
    	} else {
    		document.Frm1.FLGINVIOCL.value="N";
    	}
    }

    function controllaFlagCL() {
    <% if(nuovo) { %>
    var codSelezionati = 0;
    if(document.Frm1.FLGINVIOCL.value=="S") {
         for (i=0;i<document.Frm1.CODABILITAZIONEGEN.length;i++) {
         	if (document.Frm1.CODABILITAZIONEGEN[i].selected) {
            	codSelezionati = codSelezionati + 1;
             }
         }    
         if (codSelezionati > 1) {
         	alert("E' possibile selezionare solo un albo professionale da inviare a Cliclavoro");
            return false;
         }
     }
    <%}%>
     return true;
   }
     


  <% 
    //Genera il Javascript che si occuperà di inserire i links nel footer
    attributi.showHyperLinks(out, requestContainer,responseContainer,"prgAzienda="+prgAzienda+"&prgUnita="+prgUnita);
  %>
  </SCRIPT>
</head>

<body class="gestione" onload="rinfresca();checkFlagCliclavoro()">  
<%
infCorrentiAzienda.show(out); 
linguette.show(out);
%>
<font color="red">
  <af:showErrors/>
</font>
<center>
<font color="green">
  <af:showMessages prefix="M_DeleteAbilRich"/>
  <af:showMessages prefix="M_InsertAbilRich"/>
  <af:showMessages prefix="M_UpdateAbilRich"/>
</font>

<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="controllaFlagCL()">     
<p align="center">
  <af:list moduleName="M_ListAbilRich" skipNavigationButton="1"
         canDelete="<%=canDelete ? \"1\" : \"0\"%>" 
         canInsert="<%=canInsert ? \"1\" : \"0\"%>" 
         jsSelect="AbilRichSelect"
         jsDelete="AbilRichDelete"/>
</p>
<% 
if(canInsert) {
%>
  <p align="center">
    <input type="button" class="pulsanti" onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>')" value="Nuova Abilitazione"/>
  </p>
<%}%>

<input type="hidden" name="PAGE" value="AbilRichPage">
<% if (nuovo) { %>
  <input type="hidden" name="MODULE" value="M_InsertAbilRich"/>
<% } %>
<input type="hidden" name="PRGRICHIESTAAZ" value="<%= prgRichiestaAz%>"/>
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"%>    
<input type="hidden" name="CDNUTINS" value="<%= codiceUtente %>"/>
<input type="hidden" name="CDNUTMOD" value="<%= codiceUtente %>"/>
<input type="hidden" name="PRGAZIENDA" value="<%=strPrgAziendaMenu%>"/>
<input type="hidden" name="PRGUNITA" value="<%=strPrgUnitaMenu%>"/>
<input type="hidden" name="FLGINVIOCL" value="<%=flgInvioCL%>"/>
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
              Nuova abilitazione
            <%} else {%>
              Abilitazione
            <%}%>   
            </td>
            <td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
          </tr>
        </table>

  <%if (nuovo)
  {%>
  
  <table align="center">
  <tr>
    <td align="center" class="etichetta">Tipo di abilitazione</td>
    <td align="center" class="campo">
    <af:comboBox title="Tipo di abilitazione" name="codTipoAbilitazioneGen"  moduleName="M_GETTIPIGENABILITAZIONE" addBlank="true" onChange="javascript:caricaAbilitazioni(Frm1.codTipoAbilitazioneGen.value)" required="true"/>
    </td>
    </tr>
    <tr>
    <td align="center" class="etichetta">Codice abilitazione generale</td>  
    <td align="center" class="campo">
      <af:comboBox multiple="true" title="Abilitazione" name="CODABILITAZIONEGEN" required="True" size="4"/>
    </td>
  </tr>
  <tr>
    <td align="center" class="etichetta" nowrap>Indispensabile &nbsp;</td>
    <td align="center" class="campo" nowrap>
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
  <tr>
	<td class="etichetta">Invia a Cliclavoro</td>
	<td class="campo">		
		<input type="checkbox" name="INVIA_CLIC_LAVORO" value="" <%=flgInvioCL.equals("S") ? "CHECKED" : ""%> disabled="true"  onclick="settaFlag();">
	</td>
  </tr>
  </table>
  </center>
  <br>
  <center>
  <table>
  <tr><td align="center">
  <input class="pulsante" type="submit" name="Inserisci" value="Inserisci">
  </td>
  <td align="center">
  <input class="pulsante" type="button" name="chiudi" value="Chiudi" onClick="ChiudiDivLayer('divLayerDett');">
  </td>
  </tr>
  </table>
  </center>
  <br>
  <% } else { %>
  <table align="center">
  <tr>
    <td align="center" class="etichetta">Tipo di abilitazione</td>
    <td align="center" class="campo">

    <af:textBox name="codTipoAbilitazioneGen" title="Tipo di abilitazione"
                disabled="true"
                value="<%=codTipoAbilitazioneGen%>" />
    </td>
    </tr>
    <tr>
    <td align="center" class="etichetta">Codice abilitazione generale</td>  
    <td align="center" class="campo">
    <af:textBox name="descAbilitazioneGen" title="Abilitazione"
                disabled="true" size="50"
                value="<%=descAbilitazioneGen%>" />
    <input type="hidden" name="codAbilitazioneGen" value="<%=codAbilitazioneGen%>">                
    </td>
  </tr>
  <tr>
    <td align="center" class="etichetta" nowrap>Indispensabile &nbsp;</td>
    <td align="center" class="campo" nowrap>
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
  <tr>
	<td class="etichetta">Invia a Cliclavoro</td>
	<td class="campo">
		<input type="checkbox" name="INVIA_CLIC_LAVORO" disabled="true" value="" <%=flgInvioCL.equals("S") ? "CHECKED" : ""%> onclick="settaFlag();">
	</td>
  </tr>
  </table>
  </center>
  <br>
  <center>
  <table>
  <tr>
<% if (canModify) { %>
  <td align="center">
  <input class="pulsante" type="submit" name="salva" value="Aggiorna">
  </td>
  <td align="center">
  <input class="pulsante" type="button" name="chiudi" value="Chiudi senza aggiornare" onClick="ChiudiDivLayer('divLayerDett');">
  </td>
<% } else { %>
  <td align="center">
  <input class="pulsante" type="button" name="chiudi" value="Chiudi" onClick="ChiudiDivLayer('divLayerDett');">
  </td>
<% } %>
  </tr>
  </table>
  </center>
  <br>
 <% } %>
  <%if (!nuovo) {%>                          
    <table>
    <tr><td colspan="4" align="center">   
      <p align="center">
        <% operatoreInfo.showHTML(out); %>
      </p>
    </td></tr>
    </table>
  <%}%>  
  </div>
 <%out.print(divStreamBottom);%>      
</af:form>
</body>
</html>

<% } %>