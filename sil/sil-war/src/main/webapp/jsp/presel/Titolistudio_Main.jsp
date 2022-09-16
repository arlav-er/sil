
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

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
  it.eng.sil.security.*

"%>

<%
	String cdnLavoratore= (String )serviceRequest.getAttribute("CDNLAVORATORE");
	String _current_page = (String) serviceRequest.getAttribute("PAGE"); 
  _current_page="CurrStudiMainPage";
	ProfileDataFilter filter = new ProfileDataFilter(user, _current_page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
  PageAttribs attributi = new PageAttribs(user, _current_page);
	
	boolean canModify = false;
	boolean canDelete = false;
  	boolean readOnlyStr = true;

	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
      canModify = attributi.containsButton("inserisci");
      canDelete = attributi.containsButton("rimuovi");
    	
    	if(!canModify){
    		canModify=false;
    	}else{
    		canModify=filter.canEditLavoratore();
    	}

    	if(!canDelete){
    		canDelete=false;
    	}else{
    		canDelete=filter.canEditLavoratore();
    	}
      
    }
    readOnlyStr = !canModify;
%>

<!-- --- NOTE: Gestione Patto
-->
<%@ taglib uri="patto" prefix="pt" %>
<!-- --- -->
<%@ include file="../global/Function_CommonRicercaComune.inc" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  // --- NOTE: Gestione Patto
  String PRG_TAB_DA_ASSOCIARE = null;
  String COD_LST_TAB = "PR_STU";
  // ---

  //String cdnLavoratore= (String)serviceRequest.getAttribute("CDNLAVORATORE");

  Vector map_stato_titoli_flagcompletato = serviceResponse.getAttributeAsVector("M_Map_Cod_Stato_Tit_studio_flagcompletato.ROWS.ROW");
  
  Vector tipiTitoliRows=null;
  tipiTitoliRows= serviceResponse.getAttributeAsVector("M_GETTIPOTITOLI.ROWS.ROW");  

  Vector titoliLavoratoreRows=null;
  titoliLavoratoreRows= serviceResponse.getAttributeAsVector("M_GETLAVORATORETITOLI.ROWS.ROW");
  SourceBean row = null;
  SourceBean row_titoliLavoratore= null;
  String prgStudio="";
  String codTitolo="";
  String desTitolo="";
  String codTipoTitolo="";
  String desTipoTitolo="";
  String strSpecifica="";
  String numAnno="";
  String flgPrincipale="";
 
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
  String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE"); 
  /*PageAttribs attributi = new PageAttribs(user, "CurrStudiMainPage");
    boolean canModify = attributi.containsButton("inserisci");
    boolean canDelete = attributi.containsButton("rimuovi");
/////////////////////////
    boolean readOnlyStr = !canModify;*/
  ///////////////////////////
  boolean nuovo = true;
  String apriDiv = "none";
  
%>

<html>

<head>
  <title>Corsi di studio</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  <af:linkScript path="../../js/"/>
  <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>

  <SCRIPT TYPE="text/javascript">

  // --- NOTE: Gestione Patto
  <%@ include file="../patto/_associazioneXPatto_scripts.inc" %>
  <%@ include file="../patto/_sezioneDinamica_script.inc"%>
  function getFormObj() {return document.Frm1;}
  // ---
  
<!--
var principale_gia_inserito=false;
var des_principale_gia_inserito="";
var altri_completi=false;
var flagChanged = false;

function controllaTitoloStudio(codTitolo) {
  var strCodTitolo = new String(codTitolo);
  if (strCodTitolo.substring(strCodTitolo.length-3,strCodTitolo.length) != '000') {
    return true;
  }
  else {
    if (confirm('Non è stato indicato un titolo di studio specifico, continuare ?')) {
      return true;
    }
    else {
      return false;
    }
  }
}

function DettaglioTitolo(prgStudio) {

    var s= "AdapterHTTP?PAGE=CurrStudiTitoloPage";
    s += "&MODULE=M_GetTitolo";
    s += "&PRGSTUDIO=" + prgStudio;
    s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
    s += "&CDNFUNZIONE=<%= _funzione %>";

    setWindowLocation(s);
  }

 function DeleteTitolo(prgStudio, tipo, principale, PRGLAVPATTOSCELTA) {

    var boolprincipale;
    if (principale=='S') {
       boolprincipale=true;
       }
    else {
       boolprincipale=false;   
      }
    var s="Sicuri di voler rimuovere il titolo '";
    s+= tipo.replace('^','\'')+"'?\n";
    s+=(boolprincipale)?"\nAttenzione: si sta eliminando il titolo di studio principale.":"";
    
    if ( confirm(s) ) {

      var s= "AdapterHTTP?PAGE=CurrStudiDelTitoloPage";
      s += "&MODULE=M_DelTitolo";
      s += "&PRGSTUDIO=" + prgStudio;
      s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
      s += "&CDNFUNZIONE=<%=_funzione%>";
      // --- NOTE: Gestione Patto
      s += "&" + getParametersForPatto(PRGLAVPATTOSCELTA);
      // ---

      setWindowLocation(s);
    }
  }

function selectTitolo_onClick(codTitolo, codTitoloHid, strTitolo, strTipoTitolo) {	

  if (codTitolo.value==""){
    strTitolo.value="";
    strTipoTitolo.value="";
      
  }
  else if (codTitolo.value!=codTitoloHid.value){
    window.open("AdapterHTTP?PAGE=RicercaTitoloStudioPage&codTitolo="+codTitolo.value, "Corsi", 'toolbar=0, scrollbars=1');
  }
}

function codComuneUpperCase(inputName){

  var ctrlObj = eval("document.forms[0]." + inputName);
  eval("document.forms[0]."+inputName+".value=document.forms[0]."+inputName+".value.toUpperCase();")
	return true;
}

function toggleVisStato(){
	var divtesi = document.getElementById("tesi");
	var divcompletamento = document.getElementById("completamento");
	var divabbandono= document.getElementById("abbandono");
	var divfreq_prev=document.getElementById("freq_prev");

	var vis_tesi=false;
	var vis_completamento=false;
	var vis_abbandono=false;
	var vis_freq_prev=false;
	
	var map = new Array();
	<%//creo un array [codice][flag] per confrontare se il valore della combo condiziona la visualizzazione
	//di altri campi. Inserire un record in de_titolo_italiano con flgcompleto = S per visualizzare i campi
	//relativi al completamento del titolo di studi.
	String codice = null;
	String flag= null;
	SourceBean rowFlag = null;
	for (int i=0;i<map_stato_titoli_flagcompletato.size();i++){
		rowFlag = (SourceBean) map_stato_titoli_flagcompletato.elementAt(i);
		codice = (String)rowFlag.getAttribute("codice");
		flag = StringUtils.getAttributeStrNotNull(rowFlag,"flag").equalsIgnoreCase("S")?"true;":"false;";
		out.print("map['"+codice+"']="+flag);
	}
	%>
	var codiceSelezionato = document.Frm1.codMonoStato.value;

	if (map[codiceSelezionato]){
		vis_tesi = (document.forms[0].flgLaurea.value=="S") ? true : false;
        vis_completamento=true;
        vis_abbandono=false;
        vis_freq_prev=false;
        document.forms[0].codMonoStatoTit.disabled = false;//conseguito all'estero
        document.forms[0].flgPrincipale.disabled=false;
	}else{
		vis_tesi=false;
        vis_completamento=false;
        vis_freq_prev=true;
		if (codiceSelezionato=='A'){
			vis_abbandono=true;
		}else{
        	vis_abbandono=false;
		}
		if(codiceSelezionato==''){
			vis_freq_prev=false;
		}
        document.forms[0].flgPrincipale.value="N";
        document.forms[0].flgPrincipale.disabled=true;
        document.forms[0].codMonoStatoTit.disabled=true;
	}

	divtesi.style.display=(vis_tesi)?"":"none";
	divcompletamento.style.display=(vis_completamento)?"":"none";
	divabbandono.style.display=(vis_abbandono)?"":"none";
	divfreq_prev.style.display=(vis_freq_prev)?"":"none";
}

function checkPrincipale(inputName)
{
  if (principale_gia_inserito && (document.forms[0].flgPrincipale.value=="S") 
  ) {
          if (!(confirm("Titolo di studio principale già inserito: "+des_principale_gia_inserito +"\n\n Assegnare lo status di principale al titolo corrente?")))
            { document.forms[0].flgPrincipale.focus();
              return false;}
          
  } 
  return true;
}

function clearTitolo() {

  if (Frm1.codTitolo.value=="") {   
    Frm1.codTitoloHid.value=""; 
    Frm1.strTitolo.value=""; 
    Frm1.strTipoTitolo.value=""; 
  }

}

function controllaAnnoConseguimento(){
	var strAnno =document.Frm1.numAnno.value;
	var anno = parseInt(strAnno, 10);	
	if(anno >= 1900 && anno <= 2100 || strAnno == ""){
		return true;
	}else{
		alert("Anno di conseguimento non valido");
		return false;
	}
}


function fieldChanged() {

  // DEBUG: Scommentare per vedere "field changed !" ad ogni cambiamento
  //alert("field changed !")  
    
  // NOTE: field-check solo se canModify 
  <% if ( canModify ) { %> 
    flagChanged = true;
  <% } %> 
}

function ricercaAvanzataTitoliStudio() {
	var w=800; var l=((screen.availWidth)-w)/2;
  	var h=500; var t=((screen.availHeight)-h)/2;
	//var feat = "status=YES,location=YES,toolbar=NO,scrollbars=YES,resizable=YES,height="+h+",width="+w+",top="+t+",left="+l;
 	var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=YES,height="+h+",width="+w+",top="+t+",left="+l;
  	window.open("AdapterHTTP?PAGE=RicercaTitoloStudioAvanzataPage", "Titoli", feat);
}

function apriMascheraInserimento(nomeDiv)
{
  var collDiv = document.getElementsByName(nomeDiv);
  var objDiv = collDiv.item(0);
  objDiv.style.display = "";
}

-->
  </SCRIPT>
<script language="Javascript">
<%
    attributi.showHyperLinks(out, requestContainer, responseContainer,"cdnLavoratore=" + cdnLavoratore);    
%>
</script>

<script language="Javascript">
  window.top.menu.caricaMenuLav(<%=cdnFunzione%>,<%=cdnLavoratore%>);
</script>
<script language="javascript">
  var flgInsert = true;
</script>
</head>
<body class="gestione" onload="rinfresca();<%= canModify ? "document.forms[0].flgPrincipale.disabled=true;" : "" %> ">

  <%
    
    Linguette l = new Linguette( user, _funzione, "CurrStudiMainPage", new BigDecimal(cdnLavoratore));
    infCorrentiLav.show(out); 
    l.show(out);

  %>
<!--
  <af:showMessages prefix="M_InsertTitolo" />
  <af:showMessages prefix="M_SaveTitolo" /> -->
  <af:showMessages prefix="M_DelTitolo" />
  <af:showErrors />

  <p align="center">
      <af:list moduleName="M_GetLavoratoreTitoli" skipNavigationButton="1"
             canDelete="<%=canDelete ? \"1\" : \"0\"%>" 
             canInsert="<%=canModify ? \"1\" : \"0\"%>" 
             jsSelect="DettaglioTitolo" jsDelete="DeleteTitolo"/>          
  </p>


      <%
      if(canModify) {
      %>
          <p align="center">
            <input type="button" class="pulsanti" onClick="apriMascheraInserimento('divLayerDett');document.location='#aLayerIns';" value="Nuovo corso di studio"/>   
          
          </p>
      <%}%>
      
      
<!-- LAYER -->
<%
String divStreamTop = StyleUtils.roundLayerTop(canModify);
String divStreamBottom = StyleUtils.roundLayerBottom(canModify);
%>
  <div id="divLayerDett" name="divLayerDett" class="t_layerDett"
   style="position:absolute; width:80%; left:50; top:100px; z-index:6; display:<%=apriDiv%>;">
  <!-- Stondature ELEMENTO TOP -->
  <a name="aLayerIns"></a>
  <%out.print(divStreamTop);%>
    
    <table width="100%">
      <tr>
        <td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerDett');return false"></td>
        <td height="16" class="azzurro_bianco">
        <%if(nuovo){%>
          Nuovo corso di studio
        <%} else {%>
          Corso di studio
        <%}%>   
        </td>
        <td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
      </tr>
    </table>

    <!-- NOTE: Gestione Patto
        aggiungere onSubmit="controllaPatto()"
    -->
    <af:form  name="Frm1" method="POST" action="AdapterHTTP" onSubmit="controllaPatto() && controllaTitoloStudio(Frm1.codTitolo.value) && controllaStatoAtto(flgInsert,this) && controllaAnnoConseguimento()">
        <table align="center"  width="100%" border="0"> 
           <tr valign="top">
              <td class="etichetta">Codice</td>
              <td class="campo">
                    <af:textBox classNameBase="textbox" onKeyUp="fieldChanged();" title="Codice del titolo" name="codTitolo" size="10" maxlength="8" onBlur="clearTitolo();" required="True" />&nbsp;                    
                    <af:textBox type="hidden" name="codTitoloHid" />
                    <A href="javascript:selectTitolo_onClick(Frm1.codTitolo, Frm1.codTitoloHid, Frm1.strTitolo,  Frm1.strTipoTitolo);">
                      <img src="../../img/binocolo.gif" alt="Cerca"></A>&nbsp;&nbsp;
                    <A href="javascript:ricercaAvanzataTitoliStudio();">
                      Ricerca avanzata
                    </A>                
              </td>
            </tr>
            <tr valign="top">
              <td class="etichetta">Tipo</td>
              <td class="campo">
                  <af:textBox type="hidden" name="flgLaurea" />
                  <af:textArea cols="50" 
                               rows="3"
                               classNameBase="textarea" title="Tipo del titolo" name="strTipoTitolo" readonly="true"  /> 
              </td>
            </tr>
            <tr>
              <td class="etichetta">Corso</td>
              <td class="campo">
                  <af:textArea cols="50" 
                               rows="4"  
                               classNameBase="textarea" name="strTitolo" readonly="true" required="true" />
              </td>
            </tr>
            <tr>
              <td class="etichetta">Specifica</td>
              <td class="campo">
                  <af:textBox size="50" maxlength="200" name="strSpecifica" onKeyUp="fieldChanged();"/>&nbsp;
              </td>
            </tr>
            <tr>
              <td class="etichetta">Stato</td>
              <td  class="campo">
                    <af:comboBox moduleName="M_GetStatiTitoliStudio" addBlank="true" onChange="toggleVisStato();fieldChanged();" name="codMonoStato" title="Stato" required="true" />
                  &nbsp;&nbsp;Principale&nbsp;
                    <af:comboBox name="flgPrincipale" validateWithFunction="checkPrincipale" onChange="fieldChanged();"  >
                      <OPTION value="S">S</OPTION>
                      <OPTION value="N" SELECTED="selected">N</OPTION>
                    </af:comboBox>
              </td>
            </tr>
            <tr>
              <td class="etichetta" nowrap>Conseguito all'estero</td>
              <td class="campo" nowrap>
                    <af:comboBox name="codMonoStatoTit" size="1" title="Corso Estero"
                       multiple="false" onChange="fieldChanged();" 
                       focusOn="false" moduleName="M_ListTitoloEstero"
                       addBlank="true" blankValue="" selectedValue=""/>
              </td>
            </tr>       
            <!--tr><td colspan="2"><HR></td></tr-->
            <tr><td colspan="2"><div class="sezione2">&nbsp;</div></td></tr>

<script language="JavaScript">
            <!--
            var showButtonImg = new Image();
            var hideButtonImg = new Image();
            showButtonImg.src="../../img/chiuso.gif";
            hideButtonImg.src="../../img/aperto.gif"
            
            
            
            function onOff()
            {	var div1 = document.getElementById("dett");
              var idImm = document.getElementById("imm1");
              if (div1.style.display=="")
              {	nascondi("dett");
                mostra  ("labelVisulizza");
                nascondi("labelNascondi");
                idImm.src = showButtonImg.src;
              } 
              else
              {	mostra  ("dett");
                nascondi("labelVisulizza");
                mostra  ("labelNascondi");
                idImm.src = hideButtonImg.src
              }
            }//onOff()
            
            function mostra(id)
            { var div = document.getElementById(id);
              div.style.display="";
            }
            
            function nascondi(id)
            { var div = document.getElementById(id);
              div.style.display="none";
            }
            
            //-->
</script>
            <tr> 
              <td colspan="4">
                <table align="center" width="100%" id="abbandono" style="display:none" border="0">
                    <tr>
                      <td class="etichetta">Motivo dell'abbandono</td>
                      <td class="campo" colspan="3"><af:textBox type="text" name="strMotAbbandono" maxlength="100" onKeyUp="fieldChanged();"/></td>
                    </tr>
                </table>
                <table align="center" border="0" width="100%" id="freq_prev" style="display:none">
                    <tr>
                      <td class="etichetta">Numero anni frequentati/previsti</td>
                      <td class="campo" colspan="3"><af:textBox type="integer" name="numAnniFreq" onKeyUp="fieldChanged();"/>&nbsp;/&nbsp;<af:textBox type="integer" name="numAnniPrev"  onKeyUp="fieldChanged();"/></td>
                    </tr>              
                    <tr>
                      <td colspan="4"><div class="sezione2">&nbsp;</div></td>
                    </tr>
                </table>
                <table align="center" border="0" width="100%" id="completamento" style="display:none">
                    <tr>
                      <td class="etichetta">Anno di conseguimento</td>
                      <td class="campo" colspan="3"><af:textBox type="integer" name="numAnno" onKeyUp="fieldChanged();" /></td>
                    </tr>
                    <tr>
                      <td class="etichetta">Voto</td>
                      <td class="campo" colspan="3"><af:textBox type="text" maxlength="10" name="strVoto" onKeyUp="fieldChanged();"/>&nbsp;/&nbsp;<af:textBox type="text" maxlength="100" name="strEsimi"  onKeyUp="fieldChanged();"/></td>
                    </tr>
                    <!--tr>
                      <td colspan="4"><HR></td>
                    </tr-->
                </table>
              </td>
            </tr>
            <tr>
              <td colspan="4">
                <table cellpadding="1" cellspacing="0" width="100%" border="0">
                  <tr>
                    <td><div class="sezione2">Istituto Scolastico<!--/td-->
                    <!--td--><a  href="#" onClick="onOff()" style="CURSOR: hand;"> 
                      <img id="imm1" alt="mostra/nascondi" src="../../img/chiuso.gif" border="0"></a>
                      <span id="labelVisulizza" style="display:">(visualizza)</span>
                      <span id="labelNascondi" style="display:none">(nascondi)</span>
                      </div>
                    </td>
                    <!--td width="70%">
                      <div id="labelVisulizza" style="display:">(visualizza)</div>
                      <div id="labelNascondi" style="display:none">(nascondi)</div>
                    </td-->
                  </tr>
                </table>
              </td>
            </tr>
            <tr>
              <td colspan="4">
                <div id="dett" style="display:none">
                  <table cellpadding="0" cellspacing="0" border="0" width="100%">
                    <!--tr><td colspan="4"><HR></td></tr-->
                    <tr>
                      <td class="etichetta">Istituto scolastico</td>
                      <td class="campo">
                        <af:textBox name="strIstScolastico" maxlength="100" onKeyUp="fieldChanged();"/>
                      </td>
                    </tr>
                    <tr>
                      <td class="etichetta">Indirizzo</td>
                      <td class="campo"><af:textBox name="strIndirizzo" maxlength="60" onKeyUp="fieldChanged();"/></td>
                    </tr>
                    <tr>
                      <td class="etichetta">Località</td>
                      <td class="campo"><af:textBox name="strLocalita" maxlength="50" onKeyUp="fieldChanged();"/></td>
                    </tr>
                    <tr>
                      <td class="etichetta">Comune</td>
                      <td class="campo"><af:textBox type="text" name="codCom" value="" size="4" maxlength="4" validateWithFunction="codComuneUpperCase" onKeyUp="javascript:PulisciRicerca(Frm1.codCom, Frm1.codComHid, Frm1.strCom, Frm1.strComHid, null, null, 'codice');"/>&nbsp;
                      <A HREF="javascript:btFindComuneCAP_onclick(Frm1.codCom, Frm1.strCom, null, 'codice','',null,'inserisciComuneNonScaduto()');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
                      <af:textBox type="hidden" name="codComHid" value=""/>
                      <af:textBox type="text" name="strCom" value="" size="30" maxlength="50" inline="
                          onkeypress=\"if(event.keyCode==13) { event.keyCode=9; this.blur(); }\"" onKeyUp="javascript:PulisciRicerca(Frm1.codCom, Frm1.codComHid, Frm1.strCom, Frm1.strComHid, null, null, 'descrizione');"/>&nbsp;
                      <A HREF="javascript:btFindComuneCAP_onclick(Frm1.codCom, Frm1.strCom, null,  'descrizione','',null,'inserisciComuneNonScaduto()');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>&nbsp;
                      <af:textBox type="hidden" name="strComHid" value=""/>&nbsp;
                       </td>                
                    </tr>
                  </table>
                </div>
              </td>
            </tr>
            <tr>
              <td colspan="4">
                <table align="center" border="0" width="100%" id="tesi" style="display:none">
                    <tr><td colspan="6"><div class="sezione2">&nbsp;</div></td></tr>
                    <tr>
                        <td class="etichetta">Titolo tesi</td>
                        <td class="campo" colspan="3"><af:textArea name="strTitTesi" cols="30" maxlength="200" onKeyUp="fieldChanged();"/></td>
            
                        <td class="etichetta">Argomento tesi</td>
                        <td class="campo" colspan="3"><af:textArea name="strArgTesi" cols="30" maxlength="1000" onKeyUp="fieldChanged();"/></td>
            
            
                        <td class="etichetta">Lode</td>
                        <td class="campo">
                            <af:comboBox name="flgLode" onChange="fieldChanged();" >
                                <OPTION value=""></OPTION>
                                <OPTION value="S">S</OPTION>
                                <OPTION value="N">N</OPTION>
                            </af:comboBox>
                        </td>
                    </tr>      
                </table>
              </td>
            </tr>
            <tr>
              <td colspan="4">
                <table align="center"  border="0" width="100%" >
                  <tr><td>
                  <!--  NOTE: Gestione Patto  -->
                  <%@ include file="../patto/_associazioneDettaglioXPatto.inc" %>
                  </td></tr>
                </table>
              </td>
            </tr>
            <tr>
              <td colspan="4" align="center">   
<!--                <input type="hidden" name="PAGE" value="CurrStudiMainPage"> -->
				<input type="hidden" name="PAGE" value="CurrStudiTitoloPage">
                <input type="hidden" name="cdnLavoratore" value="<%= cdnLavoratore %>"/>
                <input type="hidden" name="cdnFunzione" value="<%= _funzione %>"/>
                <input class="pulsante" type="submit" name="inserisci" value="Inserisci" />
                <input type="button" class="pulsanti" name="chiudi" value="Chiudi senza inserire" onClick="ChiudiDivLayer('divLayerDett')">
              </td>
            </tr>
        </table>
    </af:form>
    
    <!-- Stondature ELEMENTO BOTTOM -->
    <%out.print(divStreamBottom);%>
</div>
<!-- LAYER - END -->
      
<!--
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
-->
</body>

</html>
