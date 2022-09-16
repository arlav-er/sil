<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page import="
  com.engiweb.framework.base.*,
  
  it.eng.sil.security.User,
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  it.eng.sil.security.PageAttribs,
  java.math.*,
  it.eng.sil.security.*" %>
  
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">


<%
String _page = (String) serviceRequest.getAttribute("PAGE"); 
ProfileDataFilter filter = new ProfileDataFilter(user, _page);

	if (! filter.canView()){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}

String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<af:linkScript path="../../js/"/>

 <script type="text/Javascript">
 
  
  function viewRecordDetail(prgwstracciamento) {
	//funzionalità disattivata (per il momento)
	
	// Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;
	url="AdapterHTTP?Page=WS_TRACCIAMENTO_RECORD_PAGE";
  	url+="&prgwstracciamento="+prgwstracciamento;
  	
  	var w=800; var l=((screen.availWidth)-w)/1.2;
    var h=600; var t=((screen.availHeight)-h)/1.2;
  	var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=YES,height="+h+",width="+w+",top="+t+",left="+l;
  	var titolo = "ts_ws_tracciamento record view";
  	var opened = window.open(url, "_blank", feat);
  	opened.focus();
  }
  
 </script>

<title>Ricerca TS_WS_TRACCIAMENTO</title>
</head>


<%
	String prgWsTracciamento = StringUtils.getAttributeStrNotNull(serviceRequest,"prgwstracciamento");
	String strOperazione = StringUtils.getAttributeStrNotNull(serviceRequest,"STROPERAZIONE");
	String strUrl = StringUtils.getAttributeStrNotNull(serviceRequest,"strurl");
	String strVerso = StringUtils.getAttributeStrNotNull(serviceRequest,"STRVERSO");
	String strTipo = StringUtils.getAttributeStrNotNull(serviceRequest,"strtipo");
	String dataDa = StringUtils.getAttributeStrNotNull(serviceRequest,"datada");
	String dataA = StringUtils.getAttributeStrNotNull(serviceRequest,"dataa");
	String testoC = StringUtils.getAttributeStrNotNull(serviceRequest,"testo");
	

%>

<body class="gestione">
 <p class="titolo">Ricerca sulla TS_WS_TRACCIAMENTO</p>
 <%out.print(htmlStreamTop);%>
<af:form  dontValidate="false">
	<table class="main">
	 <tr>
          <td class="etichetta">Progressivo</td>
          <td class="campo">
            <af:textBox type="text" name="prgwstracciamento" value="<%=prgWsTracciamento%>" size="20" maxlength="16"/>
          </td>
        </tr>
        <tr>
          <td class="etichetta">Operazione</td>
          <td class="campo">
            <af:comboBox addBlank="true"  name="stroperazione"  selectedValue="<%=strOperazione%>" moduleName="TS_WS_TRACCIAMENTO_OPERAZIONE_MOD"/>
          </td>
        </tr>
        
        <tr>
          <td class="etichetta">Url</td>
          <td class="campo">
            <af:comboBox addBlank="true"  name="strurl"  selectedValue="<%=strUrl%>" moduleName="TS_WS_TRACCIAMENTO_URL_MOD"/>
          </td>
        </tr>
	
		<tr>
          <td class="etichetta">Verso</td>
          <td class="campo">
            <af:comboBox addBlank="true"  name="strverso"  selectedValue="<%=strVerso%>" moduleName="TS_WS_TRACCIAMENTO_VERSO_MOD"/>
          </td>
        </tr>
        
        <tr>
          <td class="etichetta">Tipo</td>
          <td class="campo">
            <af:comboBox addBlank="true"  name="strtipo"  selectedValue="<%=strTipo%>" moduleName="TS_WS_TRACCIAMENTO_TIPO_MOD"/>
          </td>
        </tr>
        
        <tr>
          
          <td class="etichetta">Data da:</td>
          <td class="campo">
            <af:textBox type="date" name="datada" value="<%=dataDa%>"/>
          </td>
          
         
        </tr>
        
        <tr>
          <td class="etichetta">Data a:</td>
          <td class="campo">
            <af:textBox type="date" name="dataa" value="<%=dataA%>"/>
          </td>
        </tr>
	
	
        <tr>
          <td class="etichetta">Testo contenuto all'interno del messaggio SOAP (l'utilizzo di questo filtro richiede diversi secondi o addirittura minuti, ore, giorni)</td>
          <td class="campo">
            <af:textBox type="text" name="testo" value="<%=testoC%>" size="100"/>
          </td>
        </tr>
	
	</table>
	
	<input type="hidden" name="PAGE" value="WS_TRACCIAMENTO_PAGE" />
	
	<center>
	 <input class="pulsanti" type="submit" name="cerca" value="Cerca"/>
	 </center>
	 </af:form>
	 <%out.print(htmlStreamBottom);%>
</body>
</html>