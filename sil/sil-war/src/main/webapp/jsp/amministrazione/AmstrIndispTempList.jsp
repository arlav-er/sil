<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ taglib uri="presel" prefix="ps" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
  com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.*,
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  it.eng.sil.security.ProfileDataFilter,  
  java.util.*,
  java.math.*,
  java.io.*,
  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String resultConfigCond = serviceResponse.containsAttribute("M_Controlla_Config_Condizione.ROWS.ROW.NUM")?serviceResponse.getAttribute("M_Controlla_Config_Condizione.ROWS.ROW.NUM").toString():"0";
	String cdnLavoratore= (String )serviceRequest.getAttribute("CDNLAVORATORE");
	String _page = (String) serviceRequest.getAttribute("PAGE");
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
  	PageAttribs attributi = new PageAttribs(user, _page);

  	boolean readOnlyStr     = true;
  	boolean canInsert       = false;
  	boolean infStorButt     = false;
  	boolean canDelete   =  false;

	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
      infStorButt = attributi.containsButton("INF_STOR");
    	canInsert = attributi.containsButton("INSERISCI");
      readOnlyStr = !attributi.containsButton("AGGIORNA");
      canDelete   =  attributi.containsButton("RIMUOVI");

    	if((!canInsert) && (readOnlyStr) && (!canDelete)){
    		//canInsert=false;
        //canDelete=false;
        //rdOnly=true;
    	}else{
        boolean canEdit=filter.canEditLavoratore();
        if (canInsert){
          canInsert=canEdit;
        }
        if (canDelete){
          canDelete=canEdit;
        }        
        if (!readOnlyStr){
          readOnlyStr=!canEdit;
        }        
    	}
  }
%>
<%   
    Vector rows = serviceResponse.getAttributeAsVector("M_GETINDISPTEMPDETT.ROWS.ROW");
    boolean infStor = serviceRequest.containsAttribute("INFSTOR");

    SourceBean row           = null;
    //String cdnLavoratore     = null;
    String descrizione       = null;
    String dataInizio        = null;
    String dataFine          = null;
    String codIndTempLetto   = null;
    String strNote           = null;
    String dtmIns            = null;
    String dtmMod            = null;
    String flagDocumentata = null;
    BigDecimal cdnUtMod      = null;
    BigDecimal cdnUtIns      = null;
    BigDecimal keyLock       = null;
    String prgIndispTemp = null;
    Testata operatoreInfo   = null;   
    InfCorrentiLav testata = null;
//////
/*
    String PRG_TAB_DA_ASSOCIARE=null; 
    String COD_LST_TAB="AM_IND_T";
    String PRG_PATTO_LAVORATORE=null;
    String STATO_ATTO=null;
    String DATA_STIPULA=null;
    String TIPOLOGIA=null; 
    String PRG_LAV_PATTO_SCELTA=null;
    */
    String COD_LST_TAB="AM_IND_T";
    String PRG_TAB_DA_ASSOCIARE=null; 
   /////////
    
    //cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
    prgIndispTemp   = (String) serviceRequest.getAttribute("PRGINDISPTEMP");
    int _cdnFunz = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE")); 
    int prgIndTemp = -1;
    if(rows != null && !rows.isEmpty()) {   
        prgIndTemp = ((BigDecimal)((SourceBean) rows.elementAt(0)).getAttribute("PRGINDISPTEMP")).intValue();
    }
%>
<%

  Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");

  InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);

  //String _page = (String) serviceRequest.getAttribute("PAGE"); 
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  Linguette linguette = new Linguette(user, _funzione, _page, new BigDecimal(cdnLavoratore));
  String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE"); 
  // NOTE: Attributi della pagina (pulsanti e link) 
/*  PageAttribs attributi = new PageAttribs(user, _page);

  boolean readOnlyStr = !attributi.containsButton("AGGIORNA");
   boolean canInsert   =  attributi.containsButton("INSERISCI");
   boolean canDelete   =  attributi.containsButton("RIMUOVI");
  boolean infStorButt =  attributi.containsButton("INF_STOR");
  */


  boolean nuovo = true;
  if(serviceResponse.containsAttribute("M_GetIndispTempDett")) { nuovo = false; }
  else { nuovo = true; }

  String apriDiv = (String) serviceRequest.getAttribute("APRIDIV");
  if(apriDiv == null) { apriDiv = "none"; }
  else { apriDiv = ""; }
  
  String url_nuovo = "AdapterHTTP?PAGE=AmstrIndispTempPage" +
                     "&CDNLAVORATORE=" + cdnLavoratore +
                     "&CDNFUNZIONE=" + _funzione +
                     "&APRIDIV=1";
                 

  String SprgIndispTemp="";
  String cdngruppo="";
  String datautorizzazione="";
  String strautorizzazione="";
  if(!nuovo) {
    // Sono in modalità "Dettaglio"
    if(rows != null && !rows.isEmpty())  { 
        row  = (SourceBean) rows.elementAt(0);    
        descrizione     = (String)      row.getAttribute("DESCRIZIONE");
        codIndTempLetto = (String)      row.getAttribute("CODINDISPTEMP");         
        dataInizio      = (String)      row.getAttribute("DATINIZIO");         
        dataFine        = (String)      row.getAttribute("DATFINE");         
        strNote         = (String)      row.getAttribute("STRNOTE");         
        dtmIns          = (String)      row.getAttribute("DTMINS");         
        dtmMod          = (String)      row.getAttribute("DTMMOD");         
        cdnUtIns        = (BigDecimal)  row.getAttribute("CDNUTINS");         
        cdnUtMod        = (BigDecimal)  row.getAttribute("CDNUTMOD");
        flagDocumentata = (String)row.getAttribute("FLGDOCUMENTATA");
        if (flagDocumentata==null) flagDocumentata = "";
        keyLock         = (BigDecimal)  row.getAttribute("NUMKLOINDISPTEMP");
        ////////////////////////////////////////////////////////////////////
        ////  ASSOCIAZIONE AL PATTO 150 ////////////////////////////////////
        PRG_TAB_DA_ASSOCIARE =((BigDecimal)  row.getAttribute("PRGINDISPTEMP")).toString();
        ////////////////////////////////////////////////////////////////////
        
    }
  }
   operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
	

  //Check di esistenza dello storico delle indisponibilita'
   boolean storicoInd = serviceResponse.containsAttribute("M_HasStorIndisp.ROWS.ROW");


%>

<html>

<head>
<title>Amministrazione - Disponibilita Temporanea</title>

  <link rel="stylesheet" href=" ../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href=" ../../css/listdetail.css"/>
  <link rel="stylesheet" type="text/css" href="../../css/jqueryui/jquery.selectBoxIt.css">
  <link rel="stylesheet" type="text/css" href="../../css/jqueryui/jqueryui-sil.css">  

  <af:linkScript path="../../js/"/>
  <script src="../../js/jqueryui/jquery-1.12.4.min.js"></script>
  <script src="../../js/jqueryui/jquery-ui.min.js"></script>
  <script src="../../js/jqueryui/jquery.selectBoxIt.min.js"></script>	
  <script type="text/javascript">
    $(function() {
    	$("[name='codIndTemp']").selectBoxIt({
            theme: "default",
            defaultText: "Seleziona una modalità...",
            autoWidth: false
        });
       
    });
    $(function() {
    	$(":reset").click(function() {
    		$("select").data("selectBox-selectBoxIt").selectOption("<%=codIndTempLetto%>");
	    });
    });
    </script>  
  <SCRIPT language="JavaScript" src=" ../../js/layers.js"></SCRIPT>
  <SCRIPT TYPE="text/javascript">
	var configurazioneCondiz = '<%=resultConfigCond%>';
	//Controlla se la data di fine è successiva alla data inizio
	function controllaDate(fineTagName) {
    	var ret = false;
		var dataInizio = new String(document.Frm1.datInizio.value);
		var dataFine = new String(document.Frm1.datFine.value);
    
    	if (dataFine!="") {
      		var giornoInizio = dataInizio.substring(0,2);
      		var giornoFine = dataFine.substring(0,2);		
      		var meseInizio = dataInizio.substring(3,5);
      		var meseFine = dataFine.substring(3,5);
      		var annoInizio = dataInizio.substring(6,10);
      		var annoFine = dataFine.substring(6,10);		
      		if (annoInizio < annoFine) { 
      			ret = true;
      		}
      		if (annoInizio == annoFine) {
        		if (meseInizio < meseFine) { 
        			ret = true;
        		}
        		if (meseInizio == meseFine) {
        			if(configurazioneCondiz == '0') { 
        				if (giornoInizio < giornoFine) { 
        					ret = true;
        				}   
        			}
        			else {
        				if(giornoInizio <= giornoFine) {
        					ret = true;
        				} 
        			}
        		}
      		}
    	}
    	else { 
      		ret = true; 
    	}
    	if (ret) { 
    		return (checkDatFine()) ; 
    	}
    	else {
    		if(configurazioneCondiz == '0') { 
      			alert("La Data di fine deve essere maggiore della Data di inizio.");
      			ret = false;
      	  	}
      	  	else { 
      			alert("La Data di fine deve essere maggiore o uguale della Data di inizio.");
      			ret = false;
      		}
      		return (ret); 
    	}
	}


  function Select(prg){
      // Se la pagina è già in submit, ignoro questo nuovo invio!
      if (isInSubmit()) return;
  
      var s= "AdapterHTTP?PAGE=AmstrIndispTempPage";
      s += "&MODULE=M_GetIndispTempDett";
      s += "&prgIndispTemp=" + prg;
      s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
      s += "&CDNFUNZIONE=<%=_funzione%>";
      s += "&APRIDIV=1";
      setWindowLocation(s);
  }
  
  function Delete(prg, descrizione, prgLavPattoScelta) {
    // Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;

    var s="Eliminare la visibilità per l'utente specifico\n";    
    s = s + descrizione.replace(/\^/g, '\'') + " ?" ;
    if ( confirm(s) ) {

      var s= "AdapterHTTP?PAGE=AmstrIndispTempPage";
      s += "&MODULE=M_DelIndispTemp";
      s += "&prgIndispTemp=" + prg;
      s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
      s += "&CDNFUNZIONE=<%=_funzione%>";
      s += "&" + getParametersForPatto(prgLavPattoScelta);
      setWindowLocation(s);
    }
  }  


    
  function Insert() {
    if ( document.Frm1.PRGINDISPTEMP.value == "" ) {

      alert("Selezionare un tipo utente");
      return;
    }
    
    document.Frm1.MODULE.value= "M_InsIndispTemp";
    if(controllaFunzTL() && 
    	riportaControlloUtente( controllaPatto() && controllaStatoAtto(flgInsert,document.Frm1) )
      )  {
      doFormSubmit(document.Frm1);
      }
    else
    return;
  }




function Update()
  {
    var datiOk = controllaFunzTL();
    if (datiOk) {
      document.Frm1.MODULE.value = "M_SaveIndispTemp";
      if (riportaControlloUtente( controllaPatto() && controllaStatoAtto(flgInsert,document.Frm1) ))
        doFormSubmit(document.Frm1);
      else return false;
    }
  }


  function getFormObj() {return document.Frm1;}
  <%@ include file="../patto/_associazioneXPatto_scripts.inc" %>
  
function chiudiLayer() {

  ok=true;
  if (flagChanged) {
     if (!confirm("I dati sono cambiati.\nProcedere lo stesso?")){
         ok=false;
     } else { 
         // Vogliamo chiudere il layer. 
         // Pongo il flag = false per evitare che mi venga riproposto 
         // un "confirm" quando poi navigo con le linguette nella pagina principale
         flagChanged = false;
     }
     
  }
  if (ok) {
     ChiudiDivLayer('divLayerDett');
  }
}

function checkDatFine(){
  var dataFine = new String(document.Frm1.datFine.value);
  if (dataFine!="") {
    var now = new Date();
    var giornoFine = dataFine.substring(0,2);		
    var meseFine = dataFine.substring(3,5);
    var annoFine = dataFine.substring(6,10);		
    now = now.getTime();
    var dateToCheck = new Date();
    dateToCheck.setYear(annoFine);
    dateToCheck.setMonth(meseFine-1);
    dateToCheck.setDate(giornoFine);
    var checkDate = dateToCheck.getTime();
    if(configurazioneCondiz == '0') { 
    	var pastDate = (now >= checkDate);
    }
    else {
    	var pastDate = (now > checkDate);
    }
    
    if (pastDate) {
      	alert("La condizione verrà storicizzata");
    }  
  }
  return (true);
}

</SCRIPT>
<script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"cdnLavoratore=" + cdnLavoratore);
      %>
      <%@ include file="../patto/_sezioneDinamica_script.inc"%>
</script>

<script language="Javascript">
  window.top.menu.caricaMenuLav(<%=cdnFunzione%>,<%=cdnLavoratore%>);
</script>

<%@ include file="CommonScript.inc"%>

</head>

<body class="gestione" onload="rinfresca()">

  <%
    infCorrentiLav.show(out); 
    linguette.show(out);
  %>
            <%if(!readOnlyStr){
          if (keyLock != null) keyLock= keyLock.add(new BigDecimal(1));%>
     
        <%}%>
  <script language="javascript">
    var flgInsert = <%=nuovo%>;
  </script>        
  <af:form method="POST" action="AdapterHTTP" name="Frm1">
    <input type="hidden" name="PAGE" value="AmstrIndispTempPage">
    <input type="hidden" name="MODULE" value="M_InsIndispTemp"/>
    <input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>"/>
    <input type="hidden" name="keyLockIndispTemp" value="<%=keyLock%>"/>
    <input type="hidden" name="CDNFUNZIONE" value="<%=_cdnFunz%>"/>
    <input type="hidden" name="PRGINDISPTEMP" value="<%=prgIndispTemp%>"/>

    <p align="center">
      <center>
        <font color="green">
          <af:showMessages prefix="M_InsIndispTemp"/>
          <af:showMessages prefix="M_DelIndispTemp"/>
          <af:showMessages prefix="M_SaveIndispTemp"/>
        </font>
        <font color="red">
          <af:showErrors />
        </font>
      </center>
      <div align="center">
          
      <af:list moduleName="M_GetIndispTemp" skipNavigationButton="1"
               canDelete="<%=canDelete ? \"1\" : \"0\"%>" 
               canInsert="<%=canInsert ? \"1\" : \"0\"%>" 
               jsSelect="Select" jsDelete="Delete"
      />
    
    
    <%if(canInsert) {%>
          <br>

          <input type="button" class="pulsanti" onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>')" value="Nuova condizione"/>  
              <%if(infStorButt){%>
              &nbsp;&nbsp;
          <input type="button" name="infSotriche" class="pulsanti<%=((storicoInd)?"":"Disabled")%>" value="Informazioni storiche"
               onClick="infoStoriche('IndispTempInfStorPage','&cdnLavoratore=<%=cdnLavoratore%>&cdnfunzione=<%=_cdnFunz%>')"
               <%=(!storicoInd)?"disabled=\"True\"":""%>>
      <br/>
      <%}%>
    <%}%>
    </div> 
    <p/>

<!-- LAYER -->
<%
boolean canModify = !readOnlyStr;
String divStreamTop = StyleUtils.roundLayerTop(canModify);
String divStreamBottom = StyleUtils.roundLayerBottom(canModify);
%>   
  <div id="divLayerDett" name="divLayerDett" class="t_layerDett"
     style="position:absolute; width:80%; left:50; top:200px; z-index:6; display:<%=apriDiv%>;">

  <!-- Stondature ELEMENTO TOP -->
  <%out.print(divStreamTop);%>

    <table width="100%">
      <tr>
        <td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerDett');return false"></td>
        <td height="16" class="azzurro_bianco">
        <%if(nuovo){%>
          Nuova Condizione
        <%} else {%>
          Condizione
        <%}%>   
        </td>
        <td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
      </tr>
    </table>

  <%if(nuovo) {
  	if (resultConfigCond.equals("1")) {
  		dataInizio = DateUtils.getNow();	
  	}
   %>          
    <table width="100%">
    <tr><td><br/></td></tr>
    <tr>
        <td class="etichetta">Tipo&nbsp;condizione</td>
        <td class="campo"><af:comboBox classNameBase="input" name="codIndTemp" moduleName="M_GETDEINDISPTEMP" selectedValue="<%=codIndTempLetto%>"
                addBlank="true" title="Tipo condizione" required="true" disabled="<%=String.valueOf(readOnlyStr)%>"/>
        </td>
        <td class="etichetta">Documentata</td>
        <td class="campo"><af:comboBox classNameBase="input" name="flgDocumentata" 
                addBlank="true" title="Documentata" required="false" disabled="<%=String.valueOf(readOnlyStr)%>">  
                <option value="S" <%="S".equals(flagDocumentata)?"selected='true'":""%>>S</option>
                <option value="N" <%="N".equals(flagDocumentata)?"selected='true'":""%>>N</option>
            </af:comboBox>
        </td>
    </tr>
    <tr>
     <td class="etichetta">Data inizio</td>
     <td colspan="3"><af:textBox classNameBase="input" title="Data inizio" type="date" name="datInizio" value="<%=dataInizio%>" validateOnPost="true" 
              required="true" readonly="<%=String.valueOf(readOnlyStr)%>" size="11" maxlength="10"/>
     </td> 
    </tr>
    <tr>
     <td class="etichetta">Data fine</td>
     <td colspan="3"><af:textBox classNameBase="input" validateWithFunction="controllaDate" title="Data fine" type="date" name="datFine" value="<%=dataFine%>" validateOnPost="true" 
              required="false" readonly="<%=String.valueOf(readOnlyStr)%>" size="11" maxlength="10"/>
     </td>
    </tr>
    <tr>        
        <td class="etichetta">Note<br/></td>
        <td class="campo" colspan=3> 
            <af:textArea classNameBase="textarea" name="strNote" value="<%=strNote%>"
                     cols="60" rows="4" maxlength="1000"
                     onKeyUp="fieldChanged()" readonly="<%=String.valueOf(readOnlyStr)%>"  />
        </td>                        
    </tr>
    </table>
    <%if(!infStor){%>
    	<%@ include file="../patto/_associazioneDettaglioXPatto.inc"%>      
	<%}%>
	<table class="main" width="100%">
	  <tr><td>&nbsp;</td>
	  </tr>
	  <tr>
	   <td width="33%"></td>
	   <td width="33%" align="center"></td>  
	  <tr>
	    <td colspan="2" align="center">
	      <input type="button" class="pulsanti" name="inserisci" value="Inserisci" onClick="Insert();">
	      <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="chiudiLayer()">
	    </td>
	  </tr>
	  <tr>
	    <td width="33%" align="center">
	    <%if(infStor){%>
	      <input class="pulsante" type="button" class="pulsanti" name="annulla" value="Torna alle lista"
	             onclick="openPage('IndispTempInfStorPage','&cdnLavoratore=<%=cdnLavoratore%>&cdnfunzione=<%=_cdnFunz%>')">
	    <%}%>
	    </td>
	  </tr>
	</table>
  <%}else {%>





    <table width="100%">
    <tr><td><br/></td></tr>
    <tr>
        <td class="etichetta">Tipo&nbsp;condizione</td>
        <td class="campo"><af:comboBox classNameBase="input" name="codIndTemp" moduleName="M_GETDEINDISPTEMP" selectedValue="<%=codIndTempLetto%>"
                 onChange="fieldChanged();" addBlank="true" title="Tipo condizione" required="true" disabled="<%=String.valueOf(readOnlyStr)%>"/>
        </td>
        <td class="etichetta">Documentata</td>
        <td class="campo"><af:comboBox classNameBase="input" name="flgDocumentata" onChange="fieldChanged();"
                addBlank="true" title="Documentata" required="false" disabled="<%=String.valueOf(readOnlyStr)%>">  
                <option value="S" <%="S".equals(flagDocumentata)?"selected='true'":""%>>S</option>
                <option value="N" <%="N".equals(flagDocumentata)?"selected='true'":""%>>N</option>
            </af:comboBox>
        </td>
    </tr>
    <tr>
     <td class="etichetta">Data inizio</td>
     <td colspan="3"><af:textBox classNameBase="input" type="date" title="Data inizio" name="datInizio" value="<%=dataInizio%>" validateOnPost="true" 
              required="true" readonly="<%=String.valueOf(readOnlyStr)%>" onKeyUp="fieldChanged();" size="11" maxlength="10"/>
     </td> 
    </tr>
    <tr>
     <td class="etichetta">Data fine</td>
     <td colspan="3"><af:textBox classNameBase="input" validateWithFunction="controllaDate" type="date" title="Data fine" name="datFine" value="<%=dataFine%>" validateOnPost="true" 
              required="false" readonly="<%=String.valueOf(readOnlyStr)%>" onKeyUp="fieldChanged();" size="11" maxlength="10"/>
     </td>
    </tr>
    <tr>        
        <td class="etichetta">Note<br/></td>
        <td class="campo" colspan=3> 
            <af:textArea classNameBase="textarea" name="strNote" value="<%=strNote%>"
                     cols="60" rows="4" maxlength="1000"
                     onKeyUp="fieldChanged();" readonly="<%=String.valueOf(readOnlyStr)%>"  />
        </td>                        
    </tr>
    </table>
    <%if(!infStor){%>
    	<%@ include file="../patto/_associazioneDettaglioXPatto.inc"%>      
	<%}%>
	<table class="main" width="100%">
	  <tr><td>&nbsp;</td>
	  </tr>
	  <tr>
	   <td width="33%"></td>
	   <td width="33%" align="center"></td>  
	  <tr>
	    <td colspan="2" align="center">
        <%if(!readOnlyStr){
          if (keyLock != null) keyLock= keyLock.add(new BigDecimal(1));%>
            <input type="button" class="pulsanti" name="salva" value="Aggiorna" onClick="Update()">
        <%}%>
	    <%if(infStor){%>
	        <input class="pulsante" type="button" class="pulsanti" name="annulla" value="Torna alle lista"
	               onclick="openPage('IndispTempInfStorPage','&cdnLavoratore=<%=cdnLavoratore%>&cdnfunzione=<%=_cdnFunz%>')">
	    <%} else {%>
	        <input class="pulsante" type="button" class="pulsanti" name="annulla" value="Chiudi senza aggiornare"
	              onclick="chiudiLayer()">
	    <%}%>
	    </td>
	  </tr>
	</table>
    <table>
	  <tr><td colspan="2" align="center">
	  	<% if (operatoreInfo!=null) operatoreInfo.showHTML(out); %>
	  </td></tr>    
    </table>
  <%}%>
  </div>
    <!-- Stondature ELEMENTO BOTTOM -->
    <%out.print(divStreamBottom);%>

  </af:form>
</body>

</html>




