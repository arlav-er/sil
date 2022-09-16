<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ taglib uri="presel" prefix="ps" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.*,
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  boolean checkVdA = false;
  String cdnLavoratore = (String) serviceRequest.getAttribute("CDNLAVORATORE");
  Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");
  InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  Linguette linguette = new Linguette(user, _funzione, _page, new BigDecimal(cdnLavoratore));

  // NOTE: Attributi della pagina (pulsanti e link) 
  //PageAttribs attributi = new PageAttribs(user, _page);
  //boolean canInsert = attributi.containsButton("inserisci");
  //boolean canDelete = attributi.containsButton("rimuovi");
  //boolean canModify = attributi.containsButton("salva");
    ProfileDataFilter filter = new ProfileDataFilter(user, _page);
    filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
    boolean canView=filter.canViewLavoratore();
    if (! canView){
      response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
    }else{

      PageAttribs attributi = new PageAttribs(user, _page);
      boolean canInsert = attributi.containsButton("inserisci");
      boolean canDelete = attributi.containsButton("rimuovi");
      boolean canModify = attributi.containsButton("salva");
      //checkVdA = attributi.containsButton("CHECKVDA");
      
      String M_config_turismo = serviceResponse.containsAttribute("M_CONFIG_PR_MOBG.ROWS.ROW.NUM")?serviceResponse.getAttribute("M_CONFIG_PR_MOBG.ROWS.ROW.NUM").toString():"0";



      
      if ( !canModify && !canInsert && !canDelete ) {
      
      } else {
        boolean canEdit = filter.canEditLavoratore();
        if ( !canEdit ) {
          canModify = false;
          canInsert = false;
          canDelete = false;
        }
      
      }

  boolean nuovo = true;
  if(serviceResponse.containsAttribute("MDETTMOBGEOMANSIONE")) { nuovo = false; }
  else { nuovo = true; }

  String apriDiv = (String) serviceRequest.getAttribute("APRIDIV");
  if(apriDiv == null) { apriDiv = "none"; }
  else { apriDiv = ""; }
  
  String url_nuovo = "AdapterHTTP?PAGE=MobilitaGeoPage" +
                     "&CDNLAVORATORE=" + cdnLavoratore +
                     "&CDNFUNZIONE=" + _funzione +
                     "&APRIDIV=1";
  
%>

<%

  /* Lista Disponibilità inserite */
  SourceBean content = (SourceBean) serviceResponse.getAttribute("MLISTMOBGEOMANSIONI");
  Vector rows = content.getAttributeAsVector("ROWS.ROW");

  
  String prgMansioneD = "";
  String flgAutoD = "";
  String flgMotoD = "";
  String flgMezziPub = "";
  String flgPendoD = "";
  String flgMobSettD = "";
  String flgTurismo = "";
  String flgAlloggio = "";
  String flgVitto = "";
  String maxOreD = "";
  String codTrasfD = "";
  String strNoteD = "";
  String descMansioneD = "";

  Testata testata = null;

  if(!nuovo) {
    // Sono in modalità "Dettaglio"
    SourceBean cont_dett = (SourceBean) serviceResponse.getAttribute("MDETTMOBGEOMANSIONE");
    SourceBean rowDett = (SourceBean) cont_dett.getAttribute("ROWS.ROW");
    BigDecimal prgMansD = (BigDecimal) rowDett.getAttribute("PRGMANSIONE");
    if(prgMansD!=null) { prgMansioneD = prgMansD.toString(); }
    descMansioneD = StringUtils.getAttributeStrNotNull(rowDett, "STRDESCRIZIONE");
    flgAutoD = StringUtils.getAttributeStrNotNull(rowDett, "FLGDISPAUTO");
    flgMotoD = StringUtils.getAttributeStrNotNull(rowDett, "FLGDISPMOTO");
    flgMezziPub = StringUtils.getAttributeStrNotNull(rowDett, "flgMezziPub");
    flgPendoD = StringUtils.getAttributeStrNotNull(rowDett, "FLGPENDOLARISMO");
    flgMobSettD = StringUtils.getAttributeStrNotNull(rowDett, "FLGMOBSETT");
    flgTurismo = StringUtils.getAttributeStrNotNull(rowDett, "FLGTURISMO");
    flgAlloggio = StringUtils.getAttributeStrNotNull(rowDett, "FLGALLOGGIO");
    flgVitto = StringUtils.getAttributeStrNotNull(rowDett, "FLGVITTO");
    BigDecimal maxOreRow = (BigDecimal) rowDett.getAttribute("NUMOREPERC");
    if(maxOreRow != null) { maxOreD = maxOreRow.toString(); }
    codTrasfD = StringUtils.getAttributeStrNotNull(rowDett, "CODTRASFERTA");
    strNoteD = StringUtils.getAttributeStrNotNull(rowDett, "STRNOTE");

    BigDecimal cdnUtIns = (BigDecimal) rowDett.getAttribute("CDNUTINS");
    String dtmIns = (String) rowDett.getAttribute("DTMINS");
    BigDecimal cdnUtMod = (BigDecimal) rowDett.getAttribute("CDNUTMOD");
    String dtmMod = (String) rowDett.getAttribute("DTMMOD");

    testata = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
  } else {
    testata = new Testata(null, null, null, null);
  }
 
%>


<html>

<head>
  <title>Disponibilit&agrave; sulla Mobilit&agrave; Geografica per Mansioni</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

  <af:linkScript path="../../js/"/>
  <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
  
  <SCRIPT TYPE="text/javascript" language="Javascript">
  var flagChanged = false;
  function InsMobGeo()
  {
    var datiOk = controllaFunzTL() && riportaControlloUtente( IsElementiSelezionati('PRGMANSIONE') );
    if (datiOk) {
      document.MainForm.MODULE.value = "MInsMobGeoMansione";
      doFormSubmit(document.MainForm);
    }
  }

  function DelMobGeo(prgMan, descMan) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var s="Cancellare la disponibilità sulla mobilità geografica\n" 
      + "relativa alla mansione \""
      + descMan.replace(/\^/g, '\'') 
      + "\" ?" ;
    
    if ( confirm(s) ) {

      var s= "AdapterHTTP?PAGE=MobilitaGeoPage";
      s += "&MODULE=MDelMobGeoMansione";
      s += "&PRGMANSIONE=" + prgMan;
      s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
      s += "&CDNFUNZIONE=<%=_funzione%>";

      setWindowLocation(s);
    }
  }

  function ModMobGeo(prgMan)
  {
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;
  
    var w = "AdapterHTTP?PAGE=MobilitaGeoPage";
    w += "&PRGMANSIONE=" + prgMan;
    w += "&CDNLAVORATORE=<%= cdnLavoratore %>";
    w += "&CDNFUNZIONE=<%=_funzione%>";
    w += "&APRIDIV=1";

    setWindowLocation(w);
  }

  // Verifica che ci sia almeno 
  // un elemento selezionato
  // nella lista a multiselezione.
  //
  // @param listName nome del controllo lista multiselezione 
  // @return true se c'è almeno una selezione.
  function IsElementiSelezionati(listName) {

    var isElemScelto= false;
    var elementi= document.forms[0].elements[listName];
    
    for (var i= 0; i < elementi.length; i++) {
      
      if ( elementi[i].selected ) {
        isElemScelto= true;
        break;
      }
    }
    
    if ( isElemScelto == false ) {
      alert("E' necessario scegliere\nalmeno una mansione");
      return false;
    }
    return true;
  }


  // Funzioni relative al dettaglio
  function SalvaMobGeo()
  {
    var datiOk = controllaFunzTL();
    if (datiOk) {
      document.MainForm.MODULE.value = "MUpdMobGeoMansione";
      doFormSubmit(document.MainForm);
    }
  }

  // NOTE: Rilevazione Modifiche da parte dell'utente
  var flagChanged = false;
  
  function fieldChanged() {

    // DEBUG: Scommentare per vedere "field changed !" ad ogni cambiamento
    //alert("field changed !")  
    
    // NOTE: field-check solo se canModify 
    <% if ( canModify ) { %> 
      flagChanged = true;
    <% } %> 
  }
  
  
  //gestione checkbox
  function selDeselMob()
  {
  	var coll = document.getElementsByName("SD_Mob");
  	var b = coll[0].checked;
  	//alert(b);
  	var i;
  	coll= document.getElementsByName("CK_CANCMOB");
  	for(i=0; i<coll.length; i++) {
  		coll[i].checked = b;
  	}
  }
  
  function cancellaMassiva() {
  	
  	 if ( confirm("Eliminare tutte disponibilità selezionate?") ) {

      var s= "AdapterHTTP?PAGE=MobilitaGeoPage&CANCELLAMASSIVA=true";
      
      var chkboxObjEval = document.getElementsByName("CK_CANCMOB");           
      var chkboxObj=eval(chkboxObjEval);      
      var strPrgDisMansione="";
      for(i=0; i<chkboxObj.length; i++) {
  		if(chkboxObj[i].checked) {
  			if(strPrgDisMansione.length>0) { strPrgDisMansione += ","; }
  			strPrgDisMansione += chkboxObj[i].value;
  		}
  	}
     
      s += "&PRGMANSIONECANCMASSIVA=" + strPrgDisMansione;
      s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
      s += "&CDNFUNZIONE=<%=_funzione%>";

      setWindowLocation(s);
     
    }
  }
  

  </SCRIPT>
<script language="Javascript">
<!--Contiene il javascript che si occupa di aggiornare i link del footer-->
  <% 
       //Genera il Javascript che si occuperà di inserire i links nel footer
       attributi.showHyperLinks(out, requestContainer,responseContainer,"cdnLavoratore=" + cdnLavoratore);
  %>
</script>
</head>

<body class="gestione" onload="rinfresca()">

  <%
    //infCorrentiLav.generaHTML(out); 
    infCorrentiLav.show(out);
    linguette.show(out);
  %>
  
  <af:form method="POST" action="AdapterHTTP" name="MainForm">

    <input type="hidden" name="PAGE" value="MobilitaGeoPage">
    <input type="hidden" name="MODULE" value=""/>
    <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
    <input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore %>"/>

<p align="center">
    <center>
      <font color="green">
        <af:showMessages prefix="MInsMobGeoMansione"/>
        <af:showMessages prefix="M_DeleteMASSIVAMobGeoMansione"/>
      </font>
      <font color="red">
        <af:showErrors />
      </font>
    </center>
</p>
    <div align="center">
    
<% if (canDelete) { %>    
	<table width="96%" align="center">
		<tr valign="middle">
			<td align="left" class="azzurro_bianco">
			<input type="checkbox" name="SD_Mob" onClick="selDeselMob();"/>&nbsp;Seleziona/Deseleziona tutti
			&nbsp;&nbsp;
			<button type="button" onClick="cancellaMassiva();" class="ListButtonChangePage">
			<img src="../../img/del.gif" alt="">&nbsp;Cancella selezionati
			</button>
			</td>
			<td>&nbsp;</td>
		</tr>
		</table>    
<% } %>    
    
      <!-- DISPONIBILITA' SULLA MOBILITA' GEOGRAFICA GIA' INSERITE -->
      <af:list moduleName="MLISTMOBGEOMANSIONI" skipNavigationButton="1"
               canDelete="<%=canDelete ? \"1\" : \"0\"%>" 
               canInsert="<%=canInsert ? \"1\" : \"0\"%>" 
               jsSelect="ModMobGeo" jsDelete="DelMobGeo"
               configProviderClass="it.eng.sil.module.presel.DynListConfigListMobGeoMansioni"
      />
    
    
    <%if(canInsert) {%>
          <br>
          <input type="button" class="pulsanti" onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>');document.location='#aLayerIns';" value="Nuova disponibilit&agrave;"/>
        
    <%}%>
  </p>




<!-- LAYER -->
<%
String divStreamTop = StyleUtils.roundLayerTop(canModify);
String divStreamBottom = StyleUtils.roundLayerBottom(canModify);
%>

    <div id="divLayerDett" name="divLayerDett" class="t_layerDett"
    style="position:absolute; width: 60%; left:200; top:100px; z-index:6; display:<%=apriDiv%>;">
	
	<a name="aLayerIns"></a>
    <!-- Stondature ELEMENTO TOP -->
    <%out.print(divStreamTop);%>


        <!-- CONTENUTO DEL LAYER -->
        <table margin="0" cellpadding="0" cellspacing="0" width="100%">    
        <tr width="100%">
          <td class="azzurro_bianco" width="13" height="13" class="menu"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerDett');return false"></td>
          <td class="azzurro_bianco" align="center">
          <%if(nuovo){%>
              Nuova disponibilit&agrave;
            <%} else {%>
              Disponibilit&agrave; sulla Mobilit&agrave; Geografica
            <%}%>
          </td>
          <td  class="azzurro_bianco" width="13" height="13" onClick="ChiudiDivLayer('divLayerDett')" class="menu"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
        </tr>
        </table>
            <!--
            <%if(nuovo){%>
              <p class="titolo">Nuova disponibilit&agrave;</p>
            <%} else {%>
              <p class="titolo">Disponibilit&agrave; sulla Mobilit&agrave; Geografica</p>
            <%}%> --><br>
            <table width="100%" cellpadding="0" cellspacing="0">
            <%if(nuovo) {%>
              <tr width="100%">
                  <td class="etichetta">Mansioni</td>
                  <td class="campo">
                    <ps:mansioniComboBoxTag 
                      name = "PRGMANSIONE" 
                      moduleName= "M_ListMansioniDisponibileLavoro"/>
                  </td>
                </tr>
              <%} else {%>
                <tr>
                  <td class="etichetta">Mansione</td>
                  <td class="campo"><b><%=descMansioneD%></b></td>
                </tr>
                <input type="hidden" name="prgMansione" value="<%=prgMansioneD%>">
              <%}%>
           <tr class="note">
              <td class="etichetta">Disponibilit&agrave; utilizzo<br>Automobile</td>
              <td class="campo">
                <table>
                <tr>
                  <td class="campo2">
                      <af:comboBox name="flgDispAuto"
                                   size="1"
                                   title="Utilizzo Automobile"
                                   multiple="false"
                                   addBlank="true"
                                   blankValue=""
		                           required="true"                                   
                                   classNameBase="input"
                                   disabled="<%= String.valueOf( !canModify ) %>"
                                   onChange="fieldChanged()">
                          <OPTION value="S" <%=(flgAutoD.equals("S")?"SELECTED":"")%>>Sì</OPTION>
                          <OPTION value="N" <%=(flgAutoD.equals("N")?"SELECTED":"")%>>No</OPTION>
                    </af:comboBox>
                      
                  </td>
                  <td class="etichetta2">&nbsp;</td>
                  <td class="etichetta2">Disponibilit&agrave; utilizzo<br>Motoveicolo</td>
                  <td class="campo2">
                    <af:comboBox name="flgDispMoto"
                                   size="1"
                                   title="Utilizzo Motoveicolo"
                                   multiple="false"
                                   addBlank="true"
                                   blankValue=""
                                   classNameBase="input"
                                   disabled="<%= String.valueOf( !canModify ) %>"
                                   onChange="fieldChanged()">
                          <OPTION value="S" <%=(flgMotoD.equals("S")?"SELECTED":"")%>>Sì</OPTION>
                          <OPTION value="N" <%=(flgMotoD.equals("N")?"SELECTED":"")%>>No</OPTION>              
                    </af:comboBox>
                  </td>
                </tr>
                </table>
              </td>
           </tr>
            <tr>
                  <td class="etichetta2">Disponibilit&agrave; utilizzo<br>Mezzi pubblici</td>
                  <td class="campo2">
                    <af:comboBox name="flgMezziPub"
                                   size="1"
                                   title="Utilizzo Mezzi pubblici"
                                   multiple="false"
                                   addBlank="true"
                                   blankValue=""
                                   classNameBase="input"
                                   disabled="<%= String.valueOf( !canModify ) %>"
                                   onChange="fieldChanged()">
                          <OPTION value="S" <%=(flgMezziPub.equals("S")?"SELECTED":"")%>>Sì</OPTION>
                          <OPTION value="N" <%=(flgMezziPub.equals("N")?"SELECTED":"")%>>No</OPTION>              
                    </af:comboBox>
                     
            </td>
          </tr>
           <tr class="note">
              <td class="etichetta">Pendolarismo<br>Giornaliero</td>
              <td class="campo">
              <table>
              <tr>
                  <td class="campo2">
                    <af:comboBox name="flgPendolarismo"
                             size="1"
                             title="Pendolarismo Giornaliero"
                             multiple="false"
                             addBlank="true"
                             blankValue=""
                             classNameBase="input"
                             disabled="<%= String.valueOf( !canModify ) %>"
                             onChange="fieldChanged()">
                          <OPTION value="S" <%= (flgPendoD.equals("S") ? "SELECTED=\"true\"" : "" )%> >Sì</OPTION>
                          <OPTION value="N" <%= (flgPendoD.equals("N") ? "SELECTED=\"true\"" : "" )%> >No</OPTION>              
                    </af:comboBox>
                             
                  </td>
                  <td class="etichetta2">&nbsp;</td>
                  <td class="etichetta2">Durata di Percorrenza Max. in minuti</td>
                  <td class="campo2">
                    <af:textBox name="numOrePerc"
                            value="<%=maxOreD%>"
                            size="3"
                            maxlength="2"
                            title="Durata di Percorrenza"
                            type="integer"
                            validateOnPost="true"
                            classNameBase="textarea"
                            readonly="<%= String.valueOf( !canModify ) %>"
                            onKeyUp="fieldChanged()"
                    />
                  </td>
              </tr>
              </table>
              </td>
           </tr>
           <tr>
              <td class="etichetta">Mobilit&agrave;<br>Settimanale</td>
              <td class="campo">
                <af:comboBox name="flgMobSett"
                         size="1"
                         title="Mobilità Settimanale"
                         multiple="false"
                         selectedValue="<%=flgMobSettD%>"
                         addBlank="true"
                         blankValue=""
                         classNameBase="input"
                         disabled="<%= String.valueOf( !canModify ) %>"
                         onChange="fieldChanged()">
                          <OPTION value="S" <%=(flgMobSettD.equals("S")?"SELECTED":"")%>>Sì</OPTION>
                          <OPTION value="N" <%=(flgMobSettD.equals("N")?"SELECTED":"")%>>No</OPTION>
                    </af:comboBox>
                         
              </td>
            </tr>
        
             <%
                 if ("1".equals(M_config_turismo)){ 
             %>
            <tr>
              <td class="etichetta">Turismo</td>
              <td class="campo">
                <af:comboBox name="flgTurismo"
                         size="1"
                         title="Turismo"
                         multiple="false"
                         selectedValue="<%=flgTurismo%>"
                         addBlank="true"
                         blankValue=""
                         classNameBase="input"
                         disabled="<%= String.valueOf( !canModify ) %>"
                         onChange="fieldChanged()">
                          <OPTION value="S" <%=(flgTurismo.equals("S")?"SELECTED":"")%>>Sì</OPTION>
                          <OPTION value="N" <%=(flgTurismo.equals("N")?"SELECTED":"")%>>No</OPTION>
                    </af:comboBox>
              </td>
            </tr>
            <tr>
              <td class="etichetta">solo richieste<br>con alloggio</td>
              <td class="campo">
                <af:comboBox name="flgAlloggio"
                         size="1"
                         title="solo richieste con alloggio"
                         multiple="false"
                         selectedValue="<%=flgAlloggio%>"
                         addBlank="true"
                         blankValue=""
                         classNameBase="input"
                         disabled="<%= String.valueOf( !canModify ) %>"
                         onChange="fieldChanged()">
                          <OPTION value="S" <%=(flgAlloggio.equals("S")?"SELECTED":"")%>>Sì</OPTION>
                          <OPTION value="N" <%=(flgAlloggio.equals("N")?"SELECTED":"")%>>No</OPTION>
                    </af:comboBox>
              </td>
            </tr>            
            <tr>            
              <td class="etichetta">solo richieste<br>con vitto</td>
              <td class="campo">
                <af:comboBox name="flgVitto"
                         size="1"
                         title="solo richieste con vitto"
                         multiple="false"
                         selectedValue="<%=flgVitto%>"
                         addBlank="true"
                         blankValue=""
                         classNameBase="input"
                         disabled="<%= String.valueOf( !canModify ) %>"
                         onChange="fieldChanged()">
                          <OPTION value="S" <%=(flgVitto.equals("S")?"SELECTED":"")%>>Sì</OPTION>
                          <OPTION value="N" <%=(flgVitto.equals("N")?"SELECTED":"")%>>No</OPTION>
                    </af:comboBox>
              </td>            
            </tr>
            <% 
            }
            else {
            %>
            <tr>
            	<td class="etichetta"></td>
              	<td class="campo">
              		<input type="hidden" name="flgTurismo" value=""/>
              		<input type="hidden" name="flgAlloggio" value=""/>
              		<input type="hidden" name="flgVitto" value=""/>
              	</td>
            </tr>
            <%	
            }
            %>
            <tr>
              <td class="etichetta">Tipo di Trasferta</td>
              <td class="campo">
                <af:comboBox name="codTrasferta"
                             size="1"
                             title="Tipo di Trasferta"
                             multiple="false"
                             moduleName="MListTrasferte"
                             selectedValue="<%=codTrasfD%>"
                             addBlank="true"
                             blankValue=""
                             classNameBase="input"
                             disabled="<%= String.valueOf( !canModify ) %>"
                             onChange="fieldChanged()"
                />
              </td>
            </tr>
            <tr class="note">
              <td class="etichetta">Note</td>
              <td class="campo">
                <af:textArea name="strNote" 
                  rows="4" 
                  cols="30"
                  classNameBase="textarea"
                  readonly="<%= String.valueOf( !canModify ) %>"
                  onKeyUp="fieldChanged()"
                  value="<%= strNoteD %>" />
              </td>
            </tr>
            <tr><td colspan="2">&nbsp;</td></tr>
            <%if(nuovo) {%>
              <tr>
                  <td colspan="2" align="center">
                  <input type="button" class="pulsanti" name="inserisci" value="Inserisci" onClick="InsMobGeo()">
                  &nbsp;&nbsp;
                  <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="ChiudiDivLayer('divLayerDett')">
                  </td>
                </tr>
            <%}%>
            <%if(!nuovo && canModify) {%>
              <tr>
                <td colspan="2" align="center">
                <% if ( canModify ) { %>
                  <input type="button" class="pulsanti" name="salva" value="Aggiorna" onClick="SalvaMobGeo()">
                <% } %>
                  <input type="button" 
                    class="pulsanti" 
                    name="annulla" 
                    value="<%= canModify ? "Chiudi senza aggiornare" : "Chiudi" %>" 
                    onClick="ChiudiDivLayer('divLayerDett')">
                  <!-- NOTE: "reset" commentato perchè non è implementata nelle altre pagine equivalenti a questa
                             sebbene sia una funzione utile,
                   
                  &nbsp;&nbsp;
                  <input type="reset" class="pulsanti" value="Annulla">-->
                </td>
              </tr>
            <%}%>
            <tr><td colspan="2" width="70%" align="left"><%testata.showHTML(out);%></td></tr>
            </table>
      
      <!-- Stondature ELEMENTO BOTTOM -->
    <%out.print(divStreamBottom);%>
    </div>
<!-- LAYER - END -->    
    
</af:form>
</body>

</html>

<% } %>