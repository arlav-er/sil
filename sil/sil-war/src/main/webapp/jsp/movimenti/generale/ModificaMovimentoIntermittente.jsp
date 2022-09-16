<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../../global/noCaching.inc" %>
<%@ include file="../../global/getCommonObjects.inc" %>


<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.*,
                  com.engiweb.framework.util.*,
                  it.eng.sil.coop.webservices.movimenti.constant.ContrattiConstant,
                  it.eng.sil.module.movimenti.constant.DeTipoContrattoConstant,
                  it.eng.sil.module.movimenti.constant.Properties,
                  it.eng.sil.module.movimenti.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.*,
                  java.util.*,
                  java.text.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
//Recupero i dati del movimento
SourceBean data = (SourceBean)serviceResponse.getAttribute("GetMovimento.ROWS.ROW");

String dataInizioAttuale = (String) data.getAttribute("DATINIZIOMOV");
String dataFineRapporto = StringUtils.getAttributeStrNotNull(data, "datfinerapporto");
String prgMov =  ((BigDecimal)data.getAttribute("PRGMOVIMENTO")).toString();
String codContrattoLavoro = StringUtils.getAttributeStrNotNull(data, "codcontrattolavoro");
String codStatoAtto = StringUtils.getAttributeStrNotNull(data, "codStatoAtto");
boolean isContrattoIntermittente = codContrattoLavoro.equalsIgnoreCase(Properties.CONTRATTO_LAVORO_INTERMITTENTE);
String codTipoMov = StringUtils.getAttributeStrNotNull(data, "codTipoMov");
String numKloMov = (String)data.getAttribute("NUMKLOMOV");
String gestionedecreto150 = StringUtils.getAttributeStrNotNull(data, "gestionedecreto150");
if (isContrattoIntermittente){
 	if (!gestionedecreto150.equals("1") || !codTipoMov.equalsIgnoreCase("AVV") || !codStatoAtto.equalsIgnoreCase("PR")) {
  		isContrattoIntermittente = false;
	}	  
}
BigDecimal cdnUtIns = new BigDecimal ((String)data.getAttribute("CDNUTINS"));
BigDecimal cdnLavMovimento = (BigDecimal)data.getAttribute("cdnLavoratore");
String dtmIns = (String) data.getAttribute("DTMINS");
BigDecimal cdnUtMod = new BigDecimal  ((String)data.getAttribute("CDNUTMOD"));
String dtmMod = (String) data.getAttribute("DTMMOD");
Testata testata = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
String prgAzienda = ((BigDecimal)data.getAttribute("PRGAZIENDA")).toString();  
String prgUnita = ((BigDecimal)data.getAttribute("PRGUNITA")).toString(); 
String _funzione = (String)serviceRequest.getAttribute("CDNFUNZIONE");
String strEsito = (serviceResponse.containsAttribute("UpdateMovimentoIntermittente.ESITO"))?(String)serviceResponse.getAttribute("UpdateMovimentoIntermittente.ESITO"):"";
if (strEsito.equals("")) {
	strEsito = (serviceResponse.containsAttribute("MovCancellaPeriodoIntermittente.ESITO"))?(String)serviceResponse.getAttribute("MovCancellaPeriodoIntermittente.ESITO"):"";	
}
boolean esito = (!strEsito.equals("") && strEsito.equalsIgnoreCase("OK"));
String htmlStreamTop = StyleUtils.roundTopTable(true);
String htmlStreamBottom = StyleUtils.roundBottomTable(true);
%>

<HTML>
<HEAD>
<title>Modifica Movimento Intermittente</title>
<SCRIPT language="javascript">
<%@ include file="../../patto/_controlloDate_script.inc"%>
	var _funzione = '<%=_funzione%>';
	var imgChiusa = "../../img/chiuso.gif";
	var imgAperta = "../../img/aperto.gif";
</SCRIPT>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<script type="text/javascript" src="../../js/movimenti/common/MovimentiSezioniATendina.js" language="JavaScript"></script>
<script type="text/javascript" src="../../js/movimenti/common/MovimentiRicercaSoggetto.js" language="JavaScript"></script>
<af:linkScript path="../../js/"/>
<SCRIPT language="javascript">
	
	function cambiaLavMC(elem,stato){
	  divVar = document.getElementById(elem);
	  divVar.style.display = stato;
	}
		
	function fieldChanged() {}
	 
	function caricaMsg(){
	 	var esito = <%=esito%>;
	 	if (esito) {
			alert("Stato occupazionale ricalcolato correttamente");
	 	}
	}

	function aggiornaOperazione(operazione){
		document.frm.OPERAZIONE.value = operazione;
	}

	function controllaCampi(){
		if (document.frm.OPERAZIONE.value == "INSERISCIPERIODO") {
			if (document.frm.dataInizioPeriodo.value == "" || document.frm.dataFinePeriodo.value == "") {
				alert("Data inizio periodo e Data fine periodo sono obbligatori.");
				return false;
			}
			else {
				return true;
			}
		}
		else {
			return true;
		}	
	}
	 
</SCRIPT>
</HEAD>
<BODY onload="caricaMsg()">
<p>
<font color="green">
	<af:showMessages prefix="UpdateMovimentoIntermittente"/>
  	<af:showMessages prefix="MovInserisciPeriodoIntermittente"/>
  	<af:showMessages prefix="MovCancellaPeriodoIntermittente"/>
</font>
<font color="red">
     <af:showErrors/>
</font>
</p>

<af:form name="frm" action="AdapterHTTP" method="POST" onSubmit="controllaCampi()">
	
	<p class="titolo">Modifica movimento intermittente</p>
	
	<input type="hidden" name="PAGE" value="ModificaMovimentoIntermittentePage"/>
	<input type="hidden" name="PRGMOV" value="<%=prgMov%>"/>
	<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
	<input type="hidden" name="OPERAZIONE" value=""/>
	<input type="hidden" name="CODCONTRATTO" value="<%=codContrattoLavoro%>"/>
	<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavMovimento%>"/>
	
	<%out.print(htmlStreamTop);%>
	<table class="main">
	<tr>
		<td><div class="sezione2"/>Situazione attuale</td>
	</tr>
	</table>
	
	<table class="main">
		<tr><td>
			<table class="main"  border="0">
				<tr>
				<td class="etichetta">Data inizio rapporto</td>
				<td class="campo"><af:textBox classNameBase="input" type="date" name="datInizioAttuale" value="<%=dataInizioAttuale%>" readonly="true" size="11"/></td>
				</tr>
				<tr>
				<td class="etichetta">Data fine rapporto</td>
				<td class="campo"><af:textBox classNameBase="input" type="date" name="datFineAttuale" value="<%=dataFineRapporto%>" readonly="true" size="11"/></td>
				</tr>
			</table>
		</td></tr>
	</table>
		
	<p>
	<af:list moduleName="MovGetPeriodiIntermittente" />
	</p>
	
	<%out.print(htmlStreamBottom);%>
	
	<%out.print(htmlStreamTop);%>
	
	<table class="main">
	<tr>
		<td><div class="sezione2"/>Nuove informazioni</td>
	</tr>
	</table>
	
	
	<%if (isContrattoIntermittente) {%>
	<table class="main">
	<tr> 
	<td valign="top">
		<div class="sezione2">
			<img id='tendinaLavoroIntermittente' 
				alt="Apri" 
				src="../../img/aperto.gif"
				onclick="cambiaTendina(this,'sezioneLavoroIntermittente', '');"/>Lavoro Intermittente&nbsp;&nbsp;
		</div>	      				
	</td>
	</tr>
	<tr>
		<td>
   			<div id="sezioneLavoroIntermittente" style="display:inline">
     			<table class="main"  border="0">
     				<tr>
           				<td class="etichetta" nowrap>Data inizio periodo</td>
           				<td class="campo">
             				<af:textBox classNameBase="input" onKeyUp="fieldChanged();"
             							type="date"
             							name="dataInizioPeriodo"
             							readonly="false"
                       					validateOnPost="true"
             							value=""
             							size="11" maxlength="11" title="Data inizio periodo"/>
           				</td>
         			</tr>
         			<tr>
           				<td class="etichetta" nowrap>Data fine periodo</td>
           				<td class="campo">
             				<af:textBox classNameBase="input" onKeyUp="fieldChanged();"
             							type="date"
             							name="dataFinePeriodo"
             							readonly="false"
                       					validateOnPost="true"
             							value=""
             							size="11" maxlength="11" title="Data fine periodo"/>
           				</td>
         			</tr>
				</table>
     		</div>	      				
		</td>
	</tr>
	</table>
	<table class="main">
	  	<tr>
	  		<td>
	  			<input type="submit" class="pulsanti" value="Inserisci Periodo" onClick="aggiornaOperazione('INSERISCIPERIODO');">
			</td>
		</tr>
	</table>
	<%}%>
	<%out.print(htmlStreamBottom);%>
	
	<table class="main">
	  	<tr>
	  		<td>
	  			<input type="submit" class="pulsanti" value="Calcola" onClick="aggiornaOperazione('CALCOLASTATOOCC');">
	  			<input type="button" class="pulsanti" value="Chiudi" onClick="window.close();">
			</td>
		</tr>
	</table>
	
</af:form>
<% if(testata!=null) { %>
  	  <div align="center">
      <%testata.showHTML(out);%>
      </div>
 <%}%>
</BODY>
</HTML>
