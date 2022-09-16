
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
	String _current_page = (String) serviceRequest.getAttribute("PAGE"); 
%>

<%@ include file="../global/Function_CommonRicercaComune.inc" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%

  String codTitolo="";
  String codTitoloSimile="";
  String flgTipo="";
  
  String desTitolo="";
  String desTitoloSimile="";

 
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE"); 
  boolean nuovo = true;
  String apriDiv = "none";
  
%>

<html>

<head>
  <title>Associazione Titoli di Studio</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  <af:linkScript path="../../js/"/>
  <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>

  <SCRIPT TYPE="text/javascript">
<!--

var flagChanged = false;

function controllaTitoloStudio(codTitolo) {
  var strCodTitolo = new String(codTitolo);
  if (strCodTitolo.substring(strCodTitolo.length-2,strCodTitolo.length) != '00') {
    ChiudiDivLayer('divLayerAssTitolo');
    return true;
  }
  alert('E\' stato indicato un gruppo anzichè un titolo di studi specifico!\nScegliere un titolo di studi.');
  return false;
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


function clearTitolo() {

  if (Frm1.codTitolo.value=="") {   
    Frm1.codTitoloHid.value=""; 
    Frm1.strTitolo.value=""; 
    Frm1.strTipoTitolo.value=""; 
    Frm1.flgTipo.value="";
  }

}

function fieldChanged() {

  // DEBUG: Scommentare per vedere "field changed !" ad ogni cambiamento
  //alert("field changed !")  
    
  // NOTE: field-check solo se canModify 
    flagChanged = true;
}

function ricercaAvanzataTitoliStudio() {
  window.open("AdapterHTTP?PAGE=RicercaTitoloStudioAvanzataPage", "Titoli", 'toolbar=0, scrollbars=1');
}

function apriMascheraAssociazione(nomeDiv, codTitolo, desTitolo)
{
  var collDiv = document.getElementsByName(nomeDiv);
  var objDiv = collDiv.item(0);
  objDiv.style.display = "";
  // alert(document.body.scrollTop);
  objDiv.style.top = document.body.scrollTop;
  
  document.Frm1.codTitoloPrincipale.value=codTitolo;
  document.Frm1.desTitoloPrincipale.value=desTitolo.replace('`', '\'');
}


function apriMascheraInserimento(nomeDiv)
{
  var collDiv = document.getElementsByName(nomeDiv);
  var objDiv = collDiv.item(0);
  objDiv.style.display = "";
}



  function TitoloDelete(codTitoloSimile, descTitoloSimile, codTitolo, descTitolo) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var s="Eliminare il titolo \n" 
      + codTitoloSimile + "\n"
      + descTitoloSimile.replace('`', '\'')
      + "\nassociato a \n"
      + codTitolo + "\n"
      + descTitolo.replace('`', '\'') 
      + " ?" ;
    
    if ( confirm(s) ) {
      var s= "AdapterHTTP?PAGE=AssTitoliStudioPage";
      s += "&MODULE=M_DeleteAssTitoliStudio";
      s += "&CODTITOLO=" + codTitolo;
      s += "&CODTITOLOSIMILE=" + codTitoloSimile;      
      s += "&CDNFUNZIONE=<%=_funzione%>";

      setWindowLocation(s);
    }
  }


function toggleVisStato() 
{
}


function nuovoTitolo()
{
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;

  var s= "AdapterHTTP?PAGE=NuovaAssTitoliStudioPage";
  s += "&MODULE=M_NuovaAssTitoliStudio";
  s += "&PRINCIPALE=true";      
  s += "&CDNFUNZIONE=<%=_funzione%>";

  setWindowLocation(s);
}


-->
  </SCRIPT>


</head>

<body class="gestione">

  <af:showMessages prefix="M_InsertAssTitoliStudio" />
  <af:showMessages prefix="M_DeleteAssTitoliStudio" />  
  <af:showErrors />


<p align="center">

<div align="center" id="listaTitoliSimili">

<table maxwidth="96%" width="96%" align="center" margin="0" cellpadding="0" cellspacing="0">
  <tr>
    <td class="sfondo_lista" valign="top" align="left" width="6" height="6px"><img src="../../img/angoli/bia1.gif" width="6" height="6"></td>
    <td class="sfondo_lista" height="6px">&nbsp;</td>
    <td class="sfondo_lista" valign="top" align="right" width="6" height="6px"><img src="../../img/angoli/bia2.gif" width="6" height="6"></td>
  </tr>
  <tr>
    <td class="sfondo_lista" width="6">&nbsp;</td>
    <td class="sfondo_lista" align="center">
      <TABLE class="lista" align="center">
        <TR>
          <TH class="lista">&nbsp;Titolo principale&nbsp;</TH>
          <TH class="lista">&nbsp;Titolo associato&nbsp;</TH>
        </TR>
      <%
        String strDesTitolo = "";
        String strCodTitolo = "";
        String strDesTitoloSimile = "";
        String strCodTitoloSimile = "";
        String strFlgTipo = "";
        
        boolean pari = false;
        String classeLista = pari ? "lista_pari" : "lista_dispari" ;

        SourceBean sbTitolo = null;
        Vector vettTitoli = serviceResponse.getAttributeAsVector("M_ListAssTitoliStudio.TITOLI.ROWS.ROW");
        Vector vettTitoliSimili = null;
        
        if (vettTitoli.size() ==0){ %>
          <tr><td colspan="6"><b>Non &egrave; stato trovato alcun risultato.</b></td></tr></TABLE>
        <% }
        else { %>
            <%for (int i=0; i< vettTitoli.size(); i++) {
               sbTitolo = (SourceBean) vettTitoli.get(i);
               strDesTitolo = StringUtils.getAttributeStrNotNull(sbTitolo,"DESTITOLO");
               strCodTitolo = StringUtils.getAttributeStrNotNull(sbTitolo,"CODTITOLO"); %>
            <TR class="lista">
              <TD class="<%=classeLista%>" align="center">
                <TABLE class="<%=classeLista%>" align="left">
                  <TR>
                    <TD>
                      <%=strCodTitolo%>
                    </TD>
                    <TD>                      
                      <%=strDesTitolo%>
                    </TD>                      
                  </TR>
                </TABLE>
              </TD>
              <%vettTitoliSimili = sbTitolo.getAttributeAsVector ("TITOLI_SIMILI.ROWS.ROW");%>
              <TD class="<%=classeLista%>" align="center">
                <TABLE class="<%=classeLista%>" align="left">
              <% if (vettTitoliSimili.size()==0) %>
                   &nbsp;
              <% for (int j=0; j<vettTitoliSimili.size(); j++) {
                  SourceBean sbTitoloSimile = ((SourceBean) vettTitoliSimili.get(j));
                  strDesTitoloSimile = StringUtils.getAttributeStrNotNull(sbTitoloSimile,"DESTITOLOSIMILE");
                  strCodTitoloSimile = StringUtils.getAttributeStrNotNull(sbTitoloSimile,"CODTITOLOSIMILE");
                  strFlgTipo = StringUtils.getAttributeStrNotNull(sbTitoloSimile,"FLGTIPO"); %>               
                  <TR>
                    <TD>
                    <%=strCodTitoloSimile%>
                    </TD>
                    <TD>
                    <%=strDesTitoloSimile%>
                    </TD>
                    <TD align="right">
                    <%=strFlgTipo%>
                    </TD>
                    <TD align="right">
                      <A href="javascript://" onclick="TitoloDelete('<%=strCodTitoloSimile%>','<%=strDesTitoloSimile.replace('\'', '`')%>','<%=strCodTitolo%>','<%=strDesTitolo.replace('\'', '`')%>'); return false;"><IMG name="image" border="0" src="../../img/del.gif" alt="Cancellare una riga"/></A>
                    </TD>
                  </TR>
              <% } // end for titolisimili %>
                </TABLE>
              </TD>
              <TR class="<%=classeLista%>">
                <td>&nbsp;</td>                            
                <TD class="<%=classeLista%>" align="center">

                        <table width="70%" align="center">
                        <tr>
                          <td align="center">
                          <input type="button" class="pulsanti" onClick="apriMascheraAssociazione('divLayerAssTitolo','<%=strCodTitolo%>','<%=strDesTitolo.replace('\'', '`')%>')" value="Associa nuovo titolo"/>
                          </td>
                        </tr>
                        <tr>
                        <td>&nbsp;</td>
                        </tr>
                        </table>

                </TD>
              </TR>
            <% 
                pari = !pari;
                classeLista = pari ? "lista_pari" : "lista_dispari" ;
              }  // end for titoli%>
            </TR>
      </TABLE>
          <% } // end else %>
    </td>
    <td class="sfondo_lista" width="6">&nbsp;</td>




  <TR>
    <td>&nbsp;</td>              
    <TD align="center">
      <table width="70%" align="center">
      <tr>
        <td align="center">
          <input type="button" class="pulsanti" onClick="nuovoTitolo()" value="Nuovo titolo principale"/>
        </td>
      </tr>
      <tr>
        <td>&nbsp;</td>
      </tr>
      </table>
    </TD>
  </TR>
  











  </tr>
  <tr valign="bottom">
    <td class="sfondo_lista" valign="bottom" align="left" width="6" height="6px"><img src="../../img/angoli/bia4.gif" width="6" height="6"></td>
    <td class="sfondo_lista" height="6px">&nbsp;</td><td class="sfondo_lista" valign="bottom" align="right" width="6" height="6px"><img src="../../img/angoli/bia3.gif" width="6" height="6"></td>
  </tr>
</table>

</div></p>
      
      
<!-- LAYER -->
<%
String divStreamTop = StyleUtils.roundLayerTop(true);
String divStreamBottom = StyleUtils.roundLayerBottom(true);
%>

  <div id="divLayerAssTitolo" name="divLayerAssTitolo" class="t_layerDett"
   style="position:absolute; width:80%; left:50; z-index:6; display:<%=apriDiv%>;">
  <!-- Stondature ELEMENTO TOP -->
  <%out.print(divStreamTop);%>
    
    <table width="100%">
      <tr width="100%">
        <td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerAssTitolo');return false"></td>
        <td height="16" class="azzurro_bianco">
          Associa Titolo
        </td>
        <td width="16" height="16" onClick="ChiudiDivLayer('divLayerAssTitolo')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
      </tr>
    </table>

    <af:form  name="Frm1" method="POST" action="AdapterHTTP" onSubmit="controllaTitoloStudio(Frm1.codTitolo.value)">
        <table align="center"  width="100%" border="0"> 
           <tr align="top">
              <td class="etichetta">Titolo principale</td>
              <td class="campo">
                  <af:textBox type="hidden" name="codTitoloPrincipale" />
                  <af:textBox size="50" classNameBase="input" title="" name="desTitoloPrincipale" readonly="true"  /> 
           </tr>
           <tr>
             <td>&nbsp;</td>
           </tr>
           <tr align="top">
              <td class="etichetta">Titolo associato</td>
           </tr>
           <tr valign="top">
              <td class="etichetta">Codice</td>
              <td class="campo">
                    <af:textBox classNameBase="textbox" title="Codice del titolo" name="codTitolo" size="10" maxlength="8" onBlur="clearTitolo();" required="True" />&nbsp;                    
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
                  <af:textBox size="50"
                               classNameBase="input" title="Tipo del titolo" name="strTipoTitolo" readonly="true"  /> 
              </td>
            </tr>
            <tr>
              <td class="etichetta">Corso</td>
              <td class="campo">
                  <af:textArea cols="30" 
                               rows="4"  
                               classNameBase="textarea" name="strTitolo" readonly="true" required="true" />
              </td>
            </tr>
            <tr>
              <td class="etichetta">Seleziona tipo</td>
              <td class="campo">
                <af:comboBox name="flgTipo" title="Seleziona tipo" required="True">
                  <OPTION value=""></OPTION>
                  <OPTION value="1">Primario</OPTION>
                  <OPTION value="2">Secondario</OPTION>
                </af:comboBox>
              </td>
           </tr>
            <tr>
              <td colspan="4" align="center">   
                <input type="hidden" name="PAGE" value="AssTitoliStudioPage">
                <input type="hidden" name="MODULE" value="M_InsertAssTitoliStudio">                
                <input type="hidden" name="cdnFunzione" value="<%= _funzione %>"/>
                <input class="pulsante" type="submit" name="salva" value="Associa" />
                <input type="button" class="pulsanti" name="chiudi" value="Chiudi senza associare" onClick="ChiudiDivLayer('divLayerAssTitolo')">
              </td>
            </tr>

        </table>
    </af:form>
    
    <!-- Stondature ELEMENTO BOTTOM -->
    <%out.print(divStreamBottom);%>
</div>
<!-- LAYER - END -->



  
</body>

</html>
