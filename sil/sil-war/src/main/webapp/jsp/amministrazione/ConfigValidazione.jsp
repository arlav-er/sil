<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                 
                com.engiweb.framework.security.*,java.math.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.User,
                it.eng.sil.util.*, it.eng.sil.module.agenda.ShowApp,
                it.eng.afExt.utils.*"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
  int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));

  String moduleName="";
  String btnSalva="Aggiorna";
  String btnAnnulla="Chiudi senza aggiornare";

  Vector tipiAggiornamentoRows = serviceResponse.getAttributeAsVector("M_ComboAziValidazione.ROWS.ROW");
  SourceBean row_dettAggiornamento = null;


  boolean canModify = true;
   
%>
<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>  

<script type="text/javascript">
  <!--
  // Per rilevare la modifica dei dati da parte dell'utente
  var flagChanged = false;  
  
  
  function fieldChanged() {
      <%if (canModify) {out.print("flagChanged = true;");}%>
  }

  
  function annulla() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    if (flagChanged==true){
          if (!confirm("I dati sono cambiati.\nProcedere lo stesso ?")) { return; }
          else {
              document.Frm1.MODULE.value = "";
              document.Frm1.MODULE.disabled = true;
              doFormSubmit(document.Frm1);
          }
    } else {
          document.Frm1.MODULE.value = "";
          document.Frm1.MODULE.disabled = true;
          doFormSubmit(document.Frm1);
    }
  }

  function apriDivLayer(nomeDiv)
  {
	  var collDiv = document.getElementsByName(nomeDiv);
	  var objDiv = collDiv.item(0);
	  objDiv.style.display = "";
	  objDiv.style.top = document.body.scrolltop;
  }

  function apriMascheraNuovoAggiornamento(nomeDiv)
  {
    var collDiv = document.getElementsByName(nomeDiv);
    var objDiv = collDiv.item(0);
    objDiv.style.display = "";
    // alert(document.body.scrollTop);
    objDiv.style.top = document.body.scrollTop + 50;
  }


  //function caricaDettInfo(codiceTipoAggiornamento,codDettTipo,strProvenienza) {
  function caricaDettAggiornamento(codiceTipoAggiornamento) {  
    var dett_codtipo=new Array();
    var dett_cod=new Array();
    var dett_des=new Array();
    var indiceDett=0;
    
<%  for(int i=0; i<tipiAggiornamentoRows.size(); i++)  { 
      row_dettAggiornamento = (SourceBean) tipiAggiornamentoRows.elementAt(i);
      out.print("dett_codtipo["+i+"]=\""+ row_dettAggiornamento.getAttribute("CODICETIPO").toString()+"\";\n");
      out.print("dett_cod["+i+"]=\""+ row_dettAggiornamento.getAttribute("CODICE").toString()+"\";\n");
      out.print("dett_des["+i+"]=\""+ row_dettAggiornamento.getAttribute("DESCRIZIONE").toString()+"\";\n");
    }
%>
     i=0;
     j=0;
     maxcombo=15;
     while (document.Frm1.STRNOMECAMPO.options.length>0) {
          document.Frm1.STRNOMECAMPO.options[0]=null;
      }

	if (codiceTipoAggiornamento != "") {
		if (codiceTipoAggiornamento=="AZIENDA" || codiceTipoAggiornamento=="AZIENDA_UTIL") {
			codiceTipoInfo = 'AZI';
		} else {
			codiceTipoInfo = 'UNI';
		}
	} else {
		codiceTipoInfo = '';
	}
	
    for (i=0; i<dett_codtipo.length ;i++) {
      if (dett_codtipo[i]==codiceTipoInfo) {
        document.Frm1.STRNOMECAMPO.options[j]=new Option(dett_des[i], dett_cod[i], false, false);
        j++;
      }
    } 

    if (j>maxcombo) {j=maxcombo;} //imposto un massimo per la lunghezza della combo
    
    document.Frm1.STRNOMECAMPO.selectedIndex=-1;
    document.Frm1.STRNOMECAMPO.size=j;
    
  } 
  

    function Delete(prgConfVal, codTipo, dettaglio) {

      var s="Eliminare la voce ";

      if ( (dettaglio != null) && (dettaglio.length > 0) ) {

        s += dettaglio.replace(/\^/g, '\'');
      }

      s += " ?";
    
      if ( confirm(s) ) {

        var s= "AdapterHTTP?PAGE=ConfigValidazionePage";
        s += "&MODULE=M_DeleteConfigValidazione";
        s += "&PRGCONFIGVALIDAZIONE=" + prgConfVal;
		s += "&CDNFUNZIONE=<%=_funzione%>";

        setWindowLocation(s);
      }
    }

    function Insert() {
      document.Frm1.MODULE.value= "M_InsertConfigValidazione";
      if(controllaFunzTL())  {
        doFormSubmit(document.Frm1);
        }
      else
        return;
    }
  
  -->
</script>

</head>

<body onload="rinfresca()">
<br/>
<p class="titolo">Tabella di configurazione per la validazione massiva dei movimenti</p>
<%//out.print(htmlStreamTop);%>


<af:form name="Frm1" action="AdapterHTTP" method="POST">
<input type="hidden" name="PAGE" value="ConfigValidazionePage">
<input type="hidden" name="MODULE" value="<%=moduleName%>">
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">
<!--<input type="hidden" name="PRGCONFIGVALIDAZIONE" value="">-->

<p align="center">
	<font color="green">
	  <af:showMessages prefix="M_InsertConfigValidazione"/>
	  <af:showMessages prefix="M_DeleteConfigValidazione"/>
	</font>
	<font color="red">
	  <af:showErrors/>
	</font>
</p>

<p align="center">
  <af:list moduleName="M_ListConfigValidazione" canDelete="1" jsDelete="Delete" skipNavigationButton="1"/>
</p>

<%if(canModify) {%>
  <p align="center">
    <input type="button" class="pulsanti" onClick="apriMascheraNuovoAggiornamento('divLayerDett')" value="Nuovo aggiornamento"/>        
  </p>
<%}%>

<%
  String divStreamTop = StyleUtils.roundLayerTop(canModify);
  String divStreamBottom = StyleUtils.roundLayerBottom(canModify);        
%>

    <div id="divLayerDett" name="divLayerDett" class="t_layerDett"
     style="position:absolute; width:80%; left:50; z-index:6; display:none;">
    
    <%out.print(divStreamTop);%>
    <table width="100%">
      <tr>
        <td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerDett');return false"></td>
        <td height="16" class="azzurro_bianco">
          Nuovo aggiornamento
        </td>
        <td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
      </tr>
    </table>
    <br>        
          
    <TABLE>
	  <tr>
	    <td class="etichetta">Tabella di aggiornamento</td>
	    <td class="campo">    
		  <af:comboBox classNameBase="input" onChange="caricaDettAggiornamento(document.Frm1.STRTIPOAGGIORNAMENTO.value);fieldChanged();" title="Tabella di aggiornamento" name="STRTIPOAGGIORNAMENTO" required="true" disabled="<%= String.valueOf(!canModify) %>">
			<OPTION value=""></OPTION>
	    	<OPTION value="AZIENDA">Testata azienda</OPTION>
		    <OPTION value="UNITA_PRODUTTIVA">Unit&agrave; azienda</OPTION>
		    <OPTION value="SEDE_LEGALE">Sede legale azienda</OPTION>
		    <OPTION value="AZIENDA_UTIL">Testata azienda utilizzatrice</OPTION>
		    <OPTION value="UNITA_UTIL">Unit&agrave; azienda utilizzatrice</OPTION>
		  </af:comboBox>
	    </td>
	  </tr>

      <tr>
        <td class="etichetta">Campo di aggiornamento</td>
        <td class="campo">
          <af:comboBox multiple="true" title="Campo di aggiornamento" name="STRNOMECAMPO" required="true"/>&nbsp;
      </tr>
	  
   </TABLE>



   <table class="main" width="100%">
      <tr><td>&nbsp;</td></tr>
      <tr>
        <td colspan="2" align="center">    
	        <input type="button" class="pulsanti" name="inserisci" value="Inserisci" onClick="Insert();">
	        <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="ChiudiDivLayer('divLayerDett')">
        </td>
      </tr>
   </table>
  </div>
 <%out.print(divStreamBottom);%>        
</af:form>
</body>
</html>