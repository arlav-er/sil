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

  PageAttribs attributi = new PageAttribs(user, _current_page);
	

  boolean canInsert = true;
	boolean canModify = true;
	boolean canDelete = true;
  boolean readOnlyStr = true;

    readOnlyStr = !canModify;

%>

<%@ include file="../global/Function_CommonRicercaComune.inc" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
 
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
  String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE"); 

  boolean nuovo = true;
  String apriDiv = "none";
  
%>

<html>

<head>
  <title>Associazione Mansioni</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  <af:linkScript path="../../js/"/>
  <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>

  <SCRIPT TYPE="text/javascript">
<!--

var flagChanged = false;

function clearMansione() {

  if (document.Frm1.CODMANSIONE.value=="") {   
    document.Frm1.codMansioneHid.value=""; 
    document.Frm1.strTipoMansione.value=""; 
    document.Frm1.DESCMANSIONE.value=""; 
    document.Frm1.flgTipo.value="";
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


function apriMascheraMansione(nomeDiv, codMansione, descMansione)
{
  var collDiv = document.getElementsByName(nomeDiv);
  var objDiv = collDiv.item(0);
  objDiv.style.display = "";
  // alert(document.body.scrollTop);
  objDiv.style.top = document.body.scrollTop;
  
  document.Frm1.codMansionePrincipale.value=codMansione;
  document.Frm1.desMansionePrincipale.value=descMansione.replace('`', '\'');
}


  function MansioneDelete(codGruppo, descGruppo, codMansione, descMansione) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var s="Eliminare il gruppo \n" 
      + codGruppo + "\n"
      + descGruppo.replace('`', '\'')
      + "\nassociato alla mansione \n"
      + codMansione + "\n"
      + descMansione.replace('`', '\'') 
      + " ?" ;
    
    if ( confirm(s) ) {
      var s= "AdapterHTTP?PAGE=AssMansioniPage";
      s += "&MODULE=M_DeleteAssMansioni";
      s += "&CODMANSIONE=" + codMansione;
      s += "&CODGRUPPO=" + codGruppo;      
      s += "&CDNFUNZIONE=<%=_funzione%>";

      setWindowLocation(s);
    }
  }


function toggleVisStato() 
{
}


  function controllaCodiceMansione (codMansione) 
  {
    var strCodMansione = new String(codMansione);
    var strSubCodMansione = strCodMansione.substring(strCodMansione.length-2,strCodMansione.length);

    if (strSubCodMansione != '00') {
      alert('E\' stata indicata una mansione specifica anzichè un gruppo!\nScegliere un gruppo.');
      return false;
    }
    document.Frm1.flgTipo.value = '2';      
    ChiudiDivLayer('divLayerAssMansione');
    return true;
  }

function nuovaMansione()
{
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;

  var s= "AdapterHTTP?PAGE=NuovaAssMansioniPage";
  s += "&MODULE=M_NuovaAssMansioni";
  s += "&CDNFUNZIONE=<%=_funzione%>";

  setWindowLocation(s);
}



-->
  </SCRIPT>

  <%@ include file="Mansioni_CommonScripts.inc" %>

</head>

<body class="gestione">

  <af:showMessages prefix="M_InsertAssMansioni" />
  <af:showMessages prefix="M_DeleteAssMansioni" />  
  <af:showErrors />


<p align="center">

<div align="center" id="listaMansioniSimili">

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
          <TH class="lista">&nbsp;Mansione principale&nbsp;</TH>
          <TH class="lista">&nbsp;Gruppo associato&nbsp;</TH>
        </TR>
      <%
        String strDesMansione = "";
        String strCodMansione = "";
        String strDesGruppo = "";
        String strCodGruppo = "";
        String strFlgTipo = "";
        
        boolean pari = false;
        String classeLista = pari ? "lista_pari" : "lista_dispari" ;

        SourceBean sbMansione = null;
        Vector vettMansioni = serviceResponse.getAttributeAsVector("M_ListAssMansioni.MANSIONI.ROWS.ROW");
        Vector vettGruppoAssociato = null;
        
        if (vettMansioni.size() ==0){ %>
          <tr><td colspan="6"><b>Non &egrave; stato trovato alcun risultato.</b></td></tr></TABLE>
        <% }
        else { %>
            <%for (int i=0; i< vettMansioni.size(); i++) {
               sbMansione = (SourceBean) vettMansioni.get(i);
               strDesMansione = StringUtils.getAttributeStrNotNull(sbMansione,"DESMANSIONE");
               strCodMansione = StringUtils.getAttributeStrNotNull(sbMansione,"CODMANSIONE"); %>
            <TR class="lista">
              <TD class="<%=classeLista%>" align="center" >
                <TABLE class="<%=classeLista%>" align="left">
                  <TR>
                    <TD>
                      <%=strCodMansione%>
                    </TD>
                    <TD align="left">                      
                      <%=strDesMansione%>
                    </TD>                      
                  </TR>
                </TABLE>
              </TD>
            <%vettGruppoAssociato = sbMansione.getAttributeAsVector ("GRUPPO_ASSOCIATO.ROWS.ROW");%>
            <TD class="<%=classeLista%>"  valign="top"><TABLE width="100%" height="100%" class="<%=classeLista%>" align="left">
            <% if (vettGruppoAssociato.size()==0) %>
                 &nbsp;
            <%for (int j=0; j<vettGruppoAssociato.size(); j++) {
                SourceBean sbGruppo = ((SourceBean) vettGruppoAssociato.get(j));
                strDesGruppo = StringUtils.getAttributeStrNotNull(sbGruppo,"DESGRUPPO");
                strCodGruppo = StringUtils.getAttributeStrNotNull(sbGruppo,"CODGRUPPO");
                strFlgTipo = StringUtils.getAttributeStrNotNull(sbGruppo,"FLGTIPO"); %>               
              <TR>
                <TD width="2%">
                <%=strCodGruppo%>
                </TD>
                <TD align="left" >
                <%=strDesGruppo%>
                </TD>
                <TD align="right">
                <%=strFlgTipo%>
                </TD >
              <%if(canDelete) {%>
                 <TD width="2%" align="right">
                   <A href="javascript://" onclick="MansioneDelete('<%=strCodGruppo%>','<%=strDesGruppo.replace('\'', '`')%>','<%=strCodMansione%>','<%=strDesMansione.replace('\'', '`')%>'); return false;"><IMG name="image" border="0" src="../../img/del.gif" alt="Cancellare una riga"/></A>
                 </TD>
              <%}%>
              </TR>
              <% } // end for gruppoassociato %>
            </TABLE>
            </TD>
              <TR class="<%=classeLista%>">
                <td>&nbsp;</td>              
                <TD align="center">
                        <table width="70%" align="center">
                        <tr>
                          <td align="center">
                          <input type="button" class="pulsanti" onClick="apriMascheraMansione('divLayerAssMansione','<%=strCodMansione%>','<%=strDesMansione.replace('\'', '`')%>')" value="Associa nuova mansione"/>
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
            classeLista = pari ? "lista_pari" : "lista_dispari" ; %>
            <% } // end for mansioni %>
            </TR>
          </TABLE>
          <% } // end else%>
    </td>
    <td class="sfondo_lista" width="6">&nbsp;</td>
  </tr>


  <TR>
    <td>&nbsp;</td>              
    <TD align="center">
      <table width="70%" align="center">
      <tr>
        <td align="center">
        <input type="button" class="pulsanti" onClick="nuovaMansione()" value="Nuova mansione"/>
        </td>
      </tr>
      <tr>
        <td>&nbsp;</td>
      </tr>
      </table>
    </TD>
  </TR>
  


  
  <tr valign="bottom">
    <td class="sfondo_lista" valign="bottom" align="left" width="6" height="6px"><img src="../../img/angoli/bia4.gif" width="6" height="6"></td>
    <td class="sfondo_lista" height="6px">&nbsp;</td><td class="sfondo_lista" valign="bottom" align="right" width="6" height="6px"><img src="../../img/angoli/bia3.gif" width="6" height="6"></td>
  </tr>
</table>
</div></p>


      
      
<!-- LAYER -->
<%
String divStreamTop = StyleUtils.roundLayerTop(canModify);
String divStreamBottom = StyleUtils.roundLayerBottom(canModify);
%>

  <div id="divLayerAssMansione" name="divLayerAssMansione" class="t_layerDett"
   style="position:absolute; width:80%; left:50; z-index:6; display:<%=apriDiv%>;">
  <!-- Stondature ELEMENTO TOP -->
  <%out.print(divStreamTop);%>
    
    <table width="100%">
      <tr width="100%">
        <td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerAssMansione');return false"></td>
        <td height="16" class="azzurro_bianco">
          Associa Mansione
        </td>
        <td width="16" height="16" onClick="ChiudiDivLayer('divLayerAssMansione')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
      </tr>
    </table>

    <af:form  name="Frm1" method="POST" action="AdapterHTTP" onSubmit="controllaCodiceMansione(Frm1.CODMANSIONE.value)">
        <table align="center"  width="100%" border="0"> 
        
        <tr align="top">
          <td class="etichetta">Mansione principale</td>
          <td class="campo">
            <af:textBox type="hidden" name="codMansionePrincipale" />
            <af:textBox size="50" classNameBase="input" title="" name="desMansionePrincipale" readonly="true"  /> 
          </td>
        </tr>
        <tr>
           <td>&nbsp;</td>
        </tr>

        <tr align="top">
           <td class="etichetta">Mansione associata</td>
        </tr>
        <tr valign="top">
            <td class="etichetta">Ricerca limitata alle sole mansioni di uso frequente</td>
            <td class="campo">
                <input type="checkbox" name="flgFrequente" value="" checked="true"/>        
            </td>
        </tr>
        <tr>
          <td class="etichetta">Codice mansione</td>
          <td class="campo">
            <af:textBox 
              classNameBase="input" 
              name="CODMANSIONE" 
              size="7" 
              maxlength="7" 
              onBlur="clearMansione();"
              required="true"
            />

            <af:textBox 
              type="hidden" 
              name="codMansioneHid" 
            />
      
            <% if (canModify) { %>
                <a href="javascript:selectMansione_onClick(Frm1.CODMANSIONE, Frm1.codMansioneHid, Frm1.DESCMANSIONE,  Frm1.strTipoMansione);"><img src="../../img/binocolo.gif" alt="Cerca"></A>&nbsp;&nbsp;
                <A href="javascript:ricercaAvanzataMansioni();">
                    Ricerca avanzata
                </A>
            <%}%>
          </td>
        </tr>           
        <tr valign="top">
          <td class="etichetta">Tipo</td>
          <td class="campo">
            <af:textBox classNameBase="input" name="strTipoMansione" readonly="true" size="48" />
          </td>
        </tr>
        <tr>
          <td class="etichetta">Descrizione</td>
          <td class="campo">
              <af:textArea cols="30" 
                           rows="4" 
                           name="DESCMANSIONE" 
                           classNameBase="textarea"
                           readonly="true" 
                           required="true"
                           maxlength="100"  />
          </td>
        </tr>
        <tr>
          <td class="campo">
              <af:textBox type="hidden" name="flgTipo" />
          </td>
        </tr>
        <tr>
          <td colspan="4" align="center">   
            <input type="hidden" name="PAGE" value="AssMansioniPage">
            <input type="hidden" name="MODULE" value="M_InsertAssMansioni">                                
            <input type="hidden" name="cdnFunzione" value="<%= _funzione %>">
            <input class="pulsante" type="submit" name="associa" value="Associa" >
            <input type="button" class="pulsanti" name="chiudi" value="Chiudi senza associare" onClick="ChiudiDivLayer('divLayerAssMansione')">
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
