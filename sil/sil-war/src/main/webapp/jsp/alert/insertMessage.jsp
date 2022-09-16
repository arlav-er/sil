<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.StringUtils,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	User utente = (User) getRequestContainer(request).getSessionContainer().getAttribute(User.USERID);
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>

<head>
	<title>Inserimento nuovo messaggio</title>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css"/>
    <af:linkScript path="../../js/" />
    <script language="JavaScript">

function checkDate() {
  var objData1 = document.getElementsByName("datainizio");
  var objData2 = document.getElementsByName("datafine");
  var time1 = document.getElementsByName("orainizio").item(0).value;
  var time2 = document.getElementsByName("orafine").item(0).value;
  
	  strData1=objData1.item(0).value;
	  strData2=objData2.item(0).value;
	
	  //costruisco la data di inizio
	  d1giorno=parseInt(strData1.substr(0,2),10);
	  d1mese=parseInt(strData1.substr(3, 2),10)-1; //il conteggio dei mesi parte da zero 
	  d1anno=parseInt(strData1.substr(6,4),10);
	  d1ora=parseInt(time1.substr(0,2),10);
	  d1min=parseInt(time1.substr(3,5),10);
	  data1=new Date(d1anno, d1mese, d1giorno, d1ora, d1min, 0);
	
	  //costruisce la data di fine
	  d2giorno=parseInt(strData2.substr(0,2),10);
	  d2mese=parseInt(strData2.substr(3,2),10)-1;
	  d2anno=parseInt(strData2.substr(6,4),10);
	  d2ora=parseInt(time2.substr(0,2),10);
	  d2min=parseInt(time2.substr(3,5),10);
	  data2=new Date(d2anno, d2mese, d2giorno, d2ora, d2min, 0);
	  
	  ok=true;
	  if (data2 <= data1) {
	      alert("La data di fine validità è precedente o uguale alla data di inizio validità");
	      document.getElementsByName("datainizio").item(0).focus();
	      ok=false;
	   }
	  return ok;
}


</script>	
</head>

<body class="gestione">
	<af:error/>
	<af:showMessages prefix="InsertMessage"/>
	<af:showErrors/>
	<p class="titolo">Inserisci un nuovo alert</p>
	
    <%out.print(htmlStreamTop);%>
	<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="checkDate()">
		<input type="hidden" name="PAGE" value="ListaMessagePage"/>
		<input type="hidden" name="INSERISCI" value="TRUE"/>
		<input type="hidden" name="mittente" value="<%=utente.getCodut()%>"/>
		<table class="main">
			<tr>
          		<td class="etichetta">Oggetto</td>
          		<td class="campo">
            		<af:textBox type="text" name="oggetto" value="" size="20" maxlength="30"/>
          		</td>
			</tr>
			<tr>
				<td class="etichetta">Corpo</td>
				<td class="campo" valign="baseline">
					<af:textArea title="Corpo" required="true" classNameBase="textarea" name="corpo" readonly="false" value="" rows="10" cols="50"/>
				</td>
			</tr>
			<tr>
          		<td class="etichetta">Data inizio validità</td>
          		<td class="campo">
	          		<af:textBox required="true" title="Data inizio validità" type="date" name="datainizio" value="" size="12" maxlength="10" validateOnPost="true"/>
	          		Ora inizio validità
	          		<af:textBox name="orainizio" size="5" maxlength="5" type="time" title="Orario inizio validità" readonly="false" classNameBase="input" value="00:00" validateOnPost="true"/>
	          	</td>
	        </tr>
	        <tr>
          		<td class="etichetta">Data fine validità</td>
          		<td class="campo">
	          		<af:textBox title="Data fine validità" required="true" type="date" name="datafine" value="" size="12" maxlength="10" validateOnPost="true"/>
	          		Ora fine validità
	          		<af:textBox name="orafine" size="5" maxlength="5" type="time" title="Orario fine validità" readonly="false" classNameBase="input" value="00:00" validateOnPost="true"/>
      			</td>
	        </tr>
	        <tr>
	        	<td class="etichetta">Priorità</td>
	        	<td class="campo">
	        		<af:comboBox classNameBase="input" title="priorità" name="priorita" required="false" disabled="">
	                	<option value="1"></option>
	                	<option value="1">Bassa</option>
	                	<option value="2">Media</option>
	        	        <option value="3">Alta</option>
    	            </af:comboBox>
	        	</td>
	        </tr>
	        
		</table>
		<br>&nbsp;
		<input class="pulsante" type="submit" name="salva" value="Inserisci"/>
	</af:form>
	<%out.print(htmlStreamBottom);%>
	
</body>
</html>
