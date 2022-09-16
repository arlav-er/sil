<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                 
                com.engiweb.framework.security.*,java.math.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.*,
                it.eng.sil.util.*, it.eng.sil.module.agenda.ShowApp,
                it.eng.afExt.utils.*"
%>


<%@ taglib uri="aftags" prefix="af" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
String fScad = StringUtils.getAttributeStrNotNull(serviceRequest, "SCAD");
int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));

String MODULE_NAME="MDETTEVIDENZA";
SourceBean cont = (SourceBean) serviceResponse.getAttribute(MODULE_NAME);
SourceBean row = (SourceBean) cont.getAttribute("ROW");
String prgEvidenza = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGEVIDENZA");;
String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "CDNLAVORATORE");
String prgTipoEvidenza = "";
String datDataScad = "";
String strEvidenza = "";
String numKloEvidenza =  "";
String cdnUtIns = "";
String dtmIns = "";
String cdnUtMod = "";
String dtmMod = "";
Testata testata = new Testata(null,null,null,null);

if(row!=null) {
        prgTipoEvidenza = row.containsAttribute("PRGTIPOEVIDENZA")?row.getAttribute("PRGTIPOEVIDENZA").toString():"";
        datDataScad = StringUtils.getAttributeStrNotNull(row, "DATDATASCAD");
        strEvidenza = StringUtils.getAttributeStrNotNull(row, "STREVIDENZA");
		numKloEvidenza = StringUtils.getAttributeStrNotNull(row, "NUMKLOEVIDENZA");
        cdnUtIns = StringUtils.getAttributeStrNotNull(row, "CDNUTINS");
        dtmIns = StringUtils.getAttributeStrNotNull(row, "DTMINS");
        cdnUtMod = StringUtils.getAttributeStrNotNull(row, "CDNUTMOD");
        dtmMod = StringUtils.getAttributeStrNotNull(row, "DTMMOD");
        testata = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
}


String btnSalva = "Aggiorna";
String btnChiudi = "Chiudi senza aggiornare";

String _page = "ListaEvidenzePage"; 
ProfileDataFilter filter = new ProfileDataFilter(user, _page);
filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));

boolean canView=filter.canViewLavoratore();
boolean canModify = false;


PageAttribs attributi = new PageAttribs(user, _page);

if (! canView){
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
}else{
	canModify = attributi.containsButton("AGGIORNA");
}

if ( !canModify) {
	// do nothing
} else {
	boolean canEdit = filter.canEditLavoratore();
	if ( !canEdit ) { canModify = false;	}
}


if(!canModify) { btnChiudi = "Chiudi"; }

String mess = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE");
String lp = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE");

/*
int cdnGruppo=user.getCdnGruppo();
int cdnProfilo=user.getCdnProfilo();
*/

%>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Dettaglio Evidenza</title>
  
  <script type="text/javascript">
  	function aggiornaEvidenza(){
		var url = "AdapterHTTP?PAGE=RisultatiRicercaEvidenzePage&CDNFUNZIONE=1044&MODULE=MSalvaEvidenza&CDNLAVORATORE=<%=cdnLavoratore%>"
			+"&PRGEVIDENZA=<%=prgEvidenza%>&NUMKLOEVIDENZA=<%=(Integer.parseInt(numKloEvidenza)+1)%>&LIST_PAGE=<%=lp.equals("")?"1":lp%>";

			url+="&MESSAGE=<%=mess%>&SCAD=<%=fScad.equals("")?"N":fScad%>";
			
			url+="&DATDATASCAD="+document.frmEv.DATDATASCAD.value+""
			+"&PRGTIPOEVIDENZA="+document.frmEv.PRGTIPOEVIDENZA[document.frmEv.PRGTIPOEVIDENZA.selectedIndex].value+""
			+"&STREVIDENZA="+document.frmEv.STREVIDENZA.value+"&SALVA=AGGIORNA";

		url+=window.opener.document.form.backURL.value;
		window.opener.location=url;
		window.close();
  	}
  </script>
  
</head>

<body class="gestione">
<br>
<p class="titolo">Dettaglio Evidenza</p>

<af:form name="frmEv" action="AdapterHTTP" method="POST" onSubmit="checkDataScad()">
<input type="hidden" name="PAGE" value="DettaglioEvidenzaRicercataPAGE"/>
<input type="hidden" name="MODULE" value="MSalvaEvidenza"/>
<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>"/>
<input type="hidden" name="PRGEVIDENZA" value="<%=prgEvidenza%>"/>
<input type="hidden" name="NUMKLOEVIDENZA" value="<%=(Integer.parseInt(numKloEvidenza)+1)%>"/>
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
<%if(!mess.equals("")) {%>
	<input type="hidden" name="MESSAGE" value="<%=mess%>"/>
<%}%>

<%if(!lp.equals("")) {%>
	<input type="hidden" name="LIST_PAGE" value="<%=lp%>"/>
<%}%>

<%if(!fScad.equals("")) {%>
	<input type="hidden" name="SCAD" value="N"/>
<%}%>

<%
String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>
<script type="text/javascript">
    // Per rilevare la modifica dei dati da parte dell'utente
    var flagChanged = false;  
    
    
    function fieldChanged() {
        <%if (canModify) {out.print("flagChanged = true;");}%>
    }
        
    function conferma(azione){
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;

      if (azione=="BACK"){
        if (flagChanged==true){
          if (!confirm("I dati sono cambiati.\nProcedere lo stesso ?")) { return; }
          else {
              document.frmEv.MODULE.value = "";
              document.frmEv.MODULE.disabled = true;
              doFormSubmit(document.frmEv);
			   }
        } else {
          document.frmEv.MODULE.value = "";
          document.frmEv.MODULE.disabled = true;
          doFormSubmit(document.frmEv);
        }
      }
      
    }
    
    function checkDataScad()
    {
    	var dataIn = "<%=datDataScad%>";
    	var oggi = new Date();
    	var data = new Date();
    	var dataFrm = document.frmEv.DATDATASCAD.value;
    	
    	g = oggi.getDate();
    	m = oggi.getMonth();
    	a = oggi.getFullYear();
    	oggi = new Date(a, m, g);
    	
    	if(dataFrm != "") {
    		g = parseInt(dataFrm.substr(0,2),10);
    		m = parseInt(dataFrm.substr(3, 2),10)-1;
    		a = parseInt(dataFrm.substr(6,4),10);
  			data = new Date(a, m, g);
  		}
  		    
  		if(dataIn!=dataFrm) {
  			// se ho modificato una data questa non puo' essere inferiore a oggi
  			if(data < oggi) { 
  				// Come prolabor non devo poter inserire una data anteriore a oggi
  				alert("La data di scadenza non puo' essere anteriore al giorno attuale");
  				return(false);  			
  			} else { return(true); }
  		} else { return true; }
  		
    }

	function annulla(){			   
		document.frmEv.STREVIDENZA.value="";
		document.frmEv.DATDATASCAD.value="";
		document.frmEv.PRGTIPOEVIDENZA.value="";
	}   

</script>

<af:showErrors/>
<af:showMessages prefix="MSalvaEvidenza"/>

<%out.print(htmlStreamTop);%>
<table class="main">
<tr>
  <td class="etichetta">Data scadenza</td>
  <td class="campo">
      <af:textBox name="DATDATASCAD"
      		  title="Data scadenza"
      		  type="date"
              size="11"
              maxlength="10"
              required="true"
              validateOnPost="true"
              onKeyUp="fieldChanged();"
              classNameBase="input"
              readonly="<%=String.valueOf(!canModify)%>"
              value="<%=datDataScad%>"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Tipo ev.</td>
    <td class="campo">
      <af:comboBox name="PRGTIPOEVIDENZA" size="1" title="Tipo evidenza"
                     multiple="false" required="true"
                     focusOn="false" moduleName="MTipiEvidenze"
                     selectedValue="<%=prgTipoEvidenza%>" addBlank="true" blankValue=""
                     classNameBase="input"
                     disabled="<%= String.valueOf( !canModify ) %>"
                     onChange="fieldChanged()"/>    
    </td>
</tr>
<tr class="note">
  <td class="etichetta">Messaggio</td>
  <td class="campo">
    <af:textArea name="STREVIDENZA" 
                 cols="60" 
                 rows="4" 
                 required="true" 
                 title="Messaggio evidenza"
                 maxlength="3000"
                 onKeyUp="fieldChanged();"
                 classNameBase="input"
                 readonly="<%=String.valueOf(!canModify)%>"
                 value="<%=strEvidenza%>"
    />
  </td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>
<%if(canModify) { %>
  <tr>
    <td colspan="2" align="center">
    <input type="button" class="pulsanti" name="SALVA" value="<%=btnSalva%>" onclick="aggiornaEvidenza()">  
    &nbsp;&nbsp;
    <input type="reset" class="pulsanti" name="reset" value="Annulla" onClick="annulla()">
    </td>
  </tr>
  <tr><td colspan="2">&nbsp;</td></tr>
<%}%>
<tr>
  <td colspan="2" align="center">
  <input type="button" class="pulsanti" name="INDIETRO" value="<%=btnChiudi%>" onCLick="javascript:window.close();">  
  </td>
</tr>
</table>

<%out.print(htmlStreamBottom);%>
<% if(testata!=null) { %>
  <div align="center">
  <%testata.showHTML(out);%>
  </div>
<%}%>

</af:form>

</body>
</html>