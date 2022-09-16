<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
   
  it.eng.sil.util.*, 
  it.eng.afExt.utils.StringUtils,
  it.eng.sil.security.ProfileDataFilter,  
  it.eng.sil.module.coop.GetDatiPersonali,
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
	String cdnLavoratore= "0";//(String )serviceRequest.getAttribute("CDNLAVORATORE");
	String _current_page = (String) serviceRequest.getAttribute("PAGE"); 
	// uso la profilatura della pagina della lista
	ProfileDataFilter filter = new ProfileDataFilter(user, "CoopConoscenzeStudiPage");
	//filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
    PageAttribs attributi = new PageAttribs(user, _current_page);
	
	boolean canModify = false;
	boolean canDelete = false;
    boolean readOnlyStr = true;

	boolean canView=filter.canView();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}	
    readOnlyStr = !canModify;
%>

<%
  SourceBean row = null;
  // ---
 
  //String cdnLavoratore= (String)serviceRequest.getAttribute("CDNLAVORATORE");
  boolean insert_done= serviceRequest.containsAttribute("inserisci");
  //String prgStudio=(!insert_done)? (String)serviceRequest.getAttribute("prgStudio") : (serviceResponse.containsAttribute("M_GETPROSSIMOTITOLO.ROWS.ROW.prgstudio")?serviceResponse.getAttribute("M_GETPROSSIMOTITOLO.ROWS.ROW.prgstudio").toString():"");

  Vector map_stato_titoli_flagcompletato = serviceResponse.getAttributeAsVector("M_Map_Cod_Stato_Tit_studio_flagcompletato.ROWS.ROW");
  
  Vector tipiTitoliRows=null;
  tipiTitoliRows= serviceResponse.getAttributeAsVector("M_COOP_ConoscenzeStudi_dalla_cache.ROWS.ROW");  

  SourceBean row_titoloLavoratore= (SourceBean) serviceResponse.getAttribute("M_COOP_DettaglioStudio_dalla_cache.ROWS.ROW");
  String codTitolo=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "CODTITOLO");
  String desTitolo=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "desTitolo");
  String codTipoTitolo=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "codTipoTitolo");
  String desTipoTitolo=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "desTipoTitolo");
  String strSpecifica=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "strSpecifica");
  String numAnno= row_titoloLavoratore.containsAttribute("numAnno") ? row_titoloLavoratore.getAttribute("numAnno").toString() : "";
  String flgPrincipale= StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "flgPrincipale");
  String strIstScolastico= StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "strIstScolastico");
  String strIndirizzo= StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "strIndirizzo"); 
  String strLocalita= StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "strLocalita");  
  String codCom=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "codCom");  
  String strCom=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "strCom"); 
  String provincia=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "provincia");
  strCom=strCom+(!provincia.equals("")?" ("+provincia+")":"");
  String CodMonoStatoTit=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "CodMonoStatoTit");

  String codMonoStato=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "codMonoStato"); 
  String flgCompletato=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "flgCompletato"); //visualizzare i dettagli del corso
  String numAnniFreq= row_titoloLavoratore.containsAttribute("numAnniFreq") ? row_titoloLavoratore.getAttribute("numAnniFreq").toString() : "";
  String numAnniPrev=row_titoloLavoratore.containsAttribute("numAnniPrev") ? row_titoloLavoratore.getAttribute("numAnniPrev").toString() : "";
  String strMotAbbandono=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "strMotAbbandono");
  String strVoto=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "strVoto");
  String strEsimi=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "strEsimi");
  String strTitTesi=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "strTitTesi");
  String strArgTesi=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "strArgTesi");
  String flgLode=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "flgLode");
  String flgLaurea=StringUtils.getAttributeStrNotNull(row_titoloLavoratore, "flgLaurea");
  
  String cdnUtins=row_titoloLavoratore.containsAttribute("cdnUtins") ? row_titoloLavoratore.getAttribute("cdnUtins").toString() : "";
  String dtmins=row_titoloLavoratore.containsAttribute("dtmins") ? row_titoloLavoratore.getAttribute("dtmins").toString() : "";
  String cdnUtmod=row_titoloLavoratore.containsAttribute("cdnUtmod") ? row_titoloLavoratore.getAttribute("cdnUtmod").toString() : "";
  String dtmmod=row_titoloLavoratore.containsAttribute("dtmmod") ? row_titoloLavoratore.getAttribute("dtmmod").toString() : "";
  

  String prgPrincipaleGiaInserito = serviceResponse.containsAttribute("M_GetPrincTitolo.ROWS.ROW.prgstudio")?serviceResponse.getAttribute("M_GetPrincTitolo.ROWS.ROW.prgstudio").toString():"";
  String desPrincipaleGiaInserito = serviceResponse.containsAttribute("M_GetPrincTitolo.ROWS.ROW.strdescrizione")?serviceResponse.getAttribute("M_GetPrincTitolo.ROWS.ROW.strdescrizione").toString():"";
  
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  //InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
  //Testata operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);

  row = row_titoloLavoratore;
  // ---
%>
<html>

<head>
  <title>Titoli di studio</title>

  <link rel="stylesheet" href="../../css/stiliCoop.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetailCoop.css"/>
  <af:linkScript path="../../js/"/>
  <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
    <%@ include file="../global/Function_CommonRicercaComune.inc" %>
  <SCRIPT TYPE="text/javascript">

  <%@ include file="../patto/_sezioneDinamica_script.inc"%>
  

function chiudi() {
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;

  ok=true;  
  if (ok) {
     var url = 'AdapterHTTP?PAGE=CoopConoscenzeStudiPage&cdnLavoratore=<%=cdnLavoratore%>&cdnFunzione=<%=_funzione%>';
     setWindowLocation(url);
  }
}



function toggleVisStato(){
	var divtesi = document.getElementById("tesi");
	var divcompletamento = document.getElementById("completamento");
	var divabbandono= document.getElementById("abbandono");
	var divfreq_prev=document.getElementById("freq_prev");
	var divconseguito = document.getElementById("conseguito");

	var vis_tesi=false;
	var vis_completamento=<%=flgCompletato.equals("S")?"true;":"false;"%>
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
        vis_conseguito_estero=true;
        document.forms[0].flgPrincipale.disabled=false;
        document.forms[0].CodMonoStatoTit.disabled=false;
	}else{
		vis_tesi=false;
        vis_completamento=false;
        vis_freq_prev=true;
        if (codiceSelezionato=='A'){
    		vis_abbandono=true;
        }
    	else{
    		vis_abbandono=false;	
    	}
    	if(codiceSelezionato==''){
    		vis_freq_prev=false;
    	}
        vis_conseguito_estero=false;
        document.forms[0].flgPrincipale.value="N";
        document.forms[0].flgPrincipale.disabled=true;
        document.forms[0].CodMonoStatoTit.disabled=true;
	}


	divtesi.style.display=(vis_tesi)?"":"none";
	divcompletamento.style.display=(vis_completamento)?"":"none";
	divabbandono.style.display=(vis_abbandono)?"":"none";
	divfreq_prev.style.display=(vis_freq_prev)?"":"none";
	divconseguito.style.display=(vis_conseguito_estero)?"":"none";
}

function DettaglioTitolo(prgStudio) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var s= "AdapterHTTP?PAGE=CurrStudiTitoloPage";
    s += "&MODULE=M_GetTitolo";
    s += "&PRGSTUDIO=" + prgStudio;
    s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
    s += "&CDNFUNZIONE=<%= _funzione %>";

    setWindowLocation(s);
  }

</SCRIPT>

</head>

<body class="gestione" onLoad="toggleVisStato();">


<%@ include file="_testataLavoratore.inc" %>
<% // SI UTILIZZA LA PAGINA DI LISTA E NON LA PAGINA ATTUALE ( CHE NON E' STATA PROFILATA )
Linguette l = new Linguette(user, _funzione, "CoopConoscenzeStudiPage", new BigDecimal(0));
l.show(out); 
%>
  
  <p align="center">
      <af:list moduleName="M_COOP_ConoscenzeStudi_dalla_cache" skipNavigationButton="1"             
             jsSelect="DettaglioTitolo" /> 
  </p>
  
<!-- LAYER -->
<%
String divStreamTop = StyleUtils.roundLayerTop(canModify);
String divStreamBottom = StyleUtils.roundLayerBottom(canModify);
%>
  
<div id="divLayerDett" name="divLayerDett" class="t_layerDett"
 style="position:absolute; width:80%; left:50; top:100px; z-index:6; display:'';">


<!-- Stondature ELEMENTO TOP -->
<%out.print(divStreamTop);%>
  <af:showErrors />
<af:form name="Frm1" method="POST" action="AdapterHTTP" >

<table width="100%" cellpadding="0" cellspacing="0">

 <tr width="100%"><td>
    <table width="100%" cellpadding="0" cellspacing="0">
      <tr width="100%">
        <td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerDett');return false"></td>
        <td height="16" class="azzurro_bianco">Corso di studio</td>
        <td width="16" height="16" onClick="chiudi()" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
      </tr>
    </table>
  </td></tr>

          <!-- NOTE: Gestione Patto
                   aggiungere onSubmit="controllaPatto()"
          -->
  
  <tr><td>        
            <table class="main" width="100%">
              <tr valign="top">
                <td class="etichetta">Codice</td>
                <td class="campo" colspan="3">
                    <af:textBox classNameBase="input" title="Codice del titolo" value="<%=codTitolo%>" name="codTitolo" size="10" maxlength="6" required="True" readonly="<%= String.valueOf(!canModify) %>" />&nbsp;
                    <af:textBox type="hidden" name="codTitoloHid" value="<%=codTitolo%>" />
                </td>
              </tr>
              <tr valign="top">
                <td class="etichetta">Tipo</td>
                <td class="campo" colspan="3">
                  <af:textBox type="hidden" name="flgLaurea" value="<%=flgLaurea%>" />
                  <af:textBox size="50" value="<%=desTipoTitolo%>"
                            classNameBase="input" title="Tipo del titolo" name="strTipoTitolo" readonly="true"  /> 
                </td>
              </tr>
              <tr>
                <td class="etichetta">Corso</td>
                <td class="campo" colspan="3">
                  <af:textArea cols="30" 
                               rows="4"  
                               classNameBase="textarea" name="strTitolo" value="<%=desTitolo%>" readonly="true" required="true" />
                </td>
              </tr>
              <tr>
                <td class="etichetta">Specifica</td>
                <td class="campo" colspan="3">
                  <af:textBox classNameBase="input" name="strSpecifica" value="<%=strSpecifica%>" size="50" maxlength="200" readonly="<%= String.valueOf(!canModify) %>" />&nbsp;
                </td>
              </tr>
              <tr>
                <td class="etichetta">Principale</td>
                <td class="campo" colspan="3">
                  <af:comboBox classNameBase="input"  name="flgPrincipale" disabled="<%= String.valueOf(!canModify) %>">
                    <OPTION value="S" <%if (flgPrincipale.equals("S")) out.print(" selected=\"true\" ");%>>S</OPTION>
                    <OPTION value="N" <%if (flgPrincipale.equals("N")) out.print (" selected=\"true\" ");%>>N</OPTION>
                  </af:comboBox>
                </td>
              </tr>
              <tr>
                <td colspan="4"><HR></td>
              </tr>
             
              <tr>
                <td class="etichetta">Stato</td>
                <td class="campo" colspan="3">
                    <af:comboBox  moduleName="M_GetStatiTitoliStudio" addBlank="true" selectedValue="<%=codMonoStato %>" onChange="toggleVisStato();fieldChanged();" classNameBase="input" name="codMonoStato" required="true"  disabled="<%= String.valueOf(!canModify) %>"/>
                </td>
              </tr>
              <tr><td>
              	
           	  </td></tr>           	  
            </table>
  </td></tr>
    <tr><td>
<table class="main" width="100%" id="conseguito" style="display:none">
              		<tr>
                		<td class="etichetta" nowrap>Conseguito all'estero</td>
                		<td class="campo" nowrap>
                      <af:comboBox name="CodMonoStatoTit" size="1" title="Corso Estero"
                         multiple="false" classNameBase="input"
                         focusOn="false" moduleName="M_ListTitoloEstero"
                         addBlank="true" blankValue="" selectedValue="<%=CodMonoStatoTit%>" 
                         disabled="<%= String.valueOf(!canModify) %>"/>                         
                		</td>
              		</tr>
           		</table>
</td></tr>           		
  <tr><td>
          <table class="main" width="100%" id="abbandono" style="display:none">
              <tr>
                <td class="etichetta">Motivo dell'abbandono</td>
                <td class="campo" colspan="3"><af:textBox classNameBase="input" type="text" name="strMotAbbandono" value="<%=strMotAbbandono%>" size="50" maxlength="100" readonly="<%= String.valueOf(!canModify) %>" />
                </td>
              </tr>
           </table>
  </td></tr>
  <tr><td>        
          <table class="main" width="100%" id="freq_prev" style="display:none">           
              <tr>
                <td class="etichetta">Numero anni frequentati/previsti</td>
                <td class="campo" colspan="3">
                        <af:textBox classNameBase="input" type="integer" name="numAnniFreq" value="<%=numAnniFreq%>" readonly="<%= String.valueOf(!canModify) %>" />&nbsp;/&nbsp;
                        <af:textBox classNameBase="input" type="integer" name="numAnniPrev" value="<%=numAnniPrev%>" readonly="<%= String.valueOf(!canModify) %>" />
                </td>
              </tr>
          </table>
  </td></tr>
          
  <tr><td>
          <table class="main" width="100%" id="completamento" style="display:none">            
              <tr>
                <td class="etichetta">Anno di conseguimento</td>
                <td class="campo" colspan="3"><af:textBox classNameBase="input" type="integer" name="numAnno" value="<%=numAnno%>" readonly="<%= String.valueOf(!canModify) %>" />
                </td>
              </tr>
              <tr>
                <td class="etichetta">Voto</td>
                <td class="campo" colspan="3">
                    <af:textBox classNameBase="input"  type="text" name="strVoto" maxlength="10" value="<%=strVoto%>" readonly="<%= String.valueOf(!canModify) %>"/>&nbsp;/&nbsp;
                    <af:textBox classNameBase="input"  type="text" name="strEsimi" maxlength="100" value="<%=strEsimi%>" readonly="<%= String.valueOf(!canModify) %>"/>
                </td>
              </tr>
          </table>
  </td></tr>

  <tr><td>
         <table class="main" width="100%">
              <tr>
                <td colspan="4"><HR></td>
              </tr>

              <tr>
                <td class="etichetta">Istituto scolastico</td>
                <td class="campo" colspan="3">
                  <af:textBox classNameBase="input" name="strIstScolastico" value="<%=strIstScolastico%>" size="50" maxlength="100" readonly="<%= String.valueOf(!canModify) %>" />
                </td>
              </tr>
              <tr>
                <td class="etichetta">Indirizzo</td>
                <td class="campo" colspan="3" ><af:textBox classNameBase="input"  name="strIndirizzo" value="<%=strIndirizzo%>" size="50" maxlength="60" readonly="<%= String.valueOf(!canModify) %>" />
                </td>
              </tr>
              <tr>
                <td class="etichetta">Località</td>
                <td class="campo" colspan="3"><af:textBox classNameBase="input" name="strLocalita" value="<%=strLocalita%>" size="50" maxlength="50" readonly="<%= String.valueOf(!canModify) %>" />
                </td>
              </tr>
              <tr>
                <td class="etichetta">Comune</td>
                <td class="campo" colspan="3">
                  <af:textBox classNameBase="input"
                              type="text"
                              name="codCom"
                              value="<%=codCom%>"
                              size="4"
                              maxlength="4" 
                              readonly="<%= String.valueOf(!canModify) %>"/>&nbsp;
                <af:textBox type="hidden" name="codComHid" value="<%=codCom%>" />
                <af:textBox classNameBase="input"
                            type="text"
                            name="strCom"
                            value="<%=strCom%>"
                            size="30"
                            maxlength="50"
                            readonly="<%= String.valueOf(!canModify) %>"/>&nbsp;
                <af:textBox type="hidden" name="strComHid" value="<%=strCom%>" />&nbsp;
                </td>                
              </tr>    
              <tr>
                 <td colspan="4"><HR></td>
              </tr>

            </table>
   </td></tr>

   <tr><td>
            <table id="tesi" style="display:none">
               <tr>
                  <td class="etichetta">Titolo tesi</td>
                  <td class="campo"><af:textArea classNameBase="textarea" name="strTitTesi" value="<%=strTitTesi%>" cols="30" maxlength="200" readonly="<%= String.valueOf(!canModify) %>" />
                  </td>
                  <td class="etichetta">Argomento tesi</td>
                  <td class="campo"><af:textArea classNameBase="textarea" name="strArgTesi" value="<%=strArgTesi%>" cols="30" maxlength="1000" readonly="<%= String.valueOf(!canModify) %>" />
                  </td>
                  <td class="etichetta">Lode</td>
                  <td class="campo">
                      <af:comboBox name="flgLode" disabled="<%= String.valueOf(!canModify) %>" classNameBase="input">
                          <OPTION value=""></OPTION>
                          <OPTION value="S" <%if (flgLode.equals("S")) out.print(" selected=\"true\" ");%>>S</OPTION>
                          <OPTION value="N" <%if (flgLode.equals("N")) out.print(" selected=\"true\" ");%>>N</OPTION>
                      </af:comboBox>
                  </td>
                </tr>
              </table>
  </td></tr>
   <tr>
   	<td align="center">
     <input class="pulsante" type="button" name="annulla" value="Chiudi" onclick="chiudi();">&nbsp; 
   </td></tr>
  </table>
</af:form>  
  <!-- Stondature ELEMENTO BOTTOM -->
    <%out.print(divStreamBottom);%>
    </div>
<!-- LAYER - END --> 


</body>

</html>