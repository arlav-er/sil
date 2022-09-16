<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.afExt.utils.StringUtils,
                  it.eng.sil.module.voucher.*,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.security.ProfileDataFilter,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  it.eng.sil.util.*
                  " %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
String _page = (String) serviceRequest.getAttribute("PAGE");
ProfileDataFilter filter = new ProfileDataFilter(user, _page);
PageAttribs pageAtts = new PageAttribs(user, _page);
boolean canProrogaVoucher = false;
boolean canModify = false;
boolean canProrogaAtt = false;
boolean canProrogaChi = false;

if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
}
else {
	canProrogaVoucher = pageAtts.containsButton("PROROGAVCH");
	canModify = canProrogaVoucher;
	canProrogaAtt = canProrogaVoucher;
	canProrogaChi = canProrogaVoucher;
}

String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);

String cdnLav = serviceRequest.containsAttribute("CDNLAVORATORE")?serviceRequest.getAttribute("CDNLAVORATORE").toString():"";
String cdnFunzione = serviceRequest.containsAttribute("CDNFUNZIONE")?serviceRequest.getAttribute("CDNFUNZIONE").toString():"";
String prgPercorso = serviceRequest.containsAttribute("prgPercorso")?serviceRequest.getAttribute("prgPercorso").toString():"";
String prgColloquio = serviceRequest.containsAttribute("prgColloquio")?serviceRequest.getAttribute("prgColloquio").toString():"";
String prgVoucher = serviceRequest.containsAttribute("prgVoucher")?serviceRequest.getAttribute("prgVoucher").toString():"";
String datMaxAttivazione = "";
String codStatoVch = "";
String codAnnullVch = "";
String codTipoServizio = "";
BigDecimal numkloVoucher = null;
String strNumkloVoucher = "";
String codAttivazione = "";
String descStatoVch = "";
String descAnnullVch = "";
String descAzione = "";
String descRaggAzione = "";
String cfLav = "";
String cognomeLav = "";
String nomeLav = "";
String datAssegnazione = "";
String datAttivazione = "";
String datChiusura = "";
String datMaxChiusura = "";
String cfEnteAccreditato = "";
String sedeEnteAccreditato = "";
String decValTotaleStr = "";
String decValSpesoStr = "";
String decValUnitarioStr = "";
Object decValTotale = null;
Object decValSpeso = null;
Object decValUnitario = null;
String descRisultatoRaggiunto = "";
String codVchTipoRisultato = "";
String codVchStatoPagamento = "";
Object decSpesoPagamento = null;
String decSpesoPagamentoStr = "";
String cpiCompTit = "";

String datProrogaAttivazione = "";
String utenteProrogaAttivazione = "";
BigDecimal giorniProrogaAttivazione = null;
String strGiorniProrogaAttivazione = "";
String messaggioProrogaAttivazione = null;
String datProrogaChiusura = "";
String utenteProrogaChiusura = "";
BigDecimal giorniProrogaChiusura = null;
String strGiorniProrogaChiusura = "";
String messaggioProrogaChiusura = null;
String codiceIBAN = "";
String strFlagCM = "";

SourceBean rowVoucher = (SourceBean)serviceResponse.getAttribute("M_DettaglioVoucher.ROWS.ROW");

if (rowVoucher != null) {
	datMaxAttivazione = StringUtils.getAttributeStrNotNull(rowVoucher,"datmaxattivazione");
	codStatoVch = StringUtils.getAttributeStrNotNull(rowVoucher,"codstatovoucher"); 
	codAnnullVch = StringUtils.getAttributeStrNotNull(rowVoucher,"CODVCHMOTIVOANNULLAMENTO");
	numkloVoucher = (BigDecimal)rowVoucher.getAttribute("numklovoucher");
	if (numkloVoucher != null) {
		strNumkloVoucher = numkloVoucher.toString();
	}
	codAttivazione = StringUtils.getAttributeStrNotNull(rowVoucher,"codattivazione");
	descStatoVch = StringUtils.getAttributeStrNotNull(rowVoucher,"descstatovoucher");
	descAnnullVch = StringUtils.getAttributeStrNotNull(rowVoucher,"descannullvoucher");
	descAzione = StringUtils.getAttributeStrNotNull(rowVoucher,"descazione");
	descRaggAzione = StringUtils.getAttributeStrNotNull(rowVoucher,"descraggazione");
	cfLav = StringUtils.getAttributeStrNotNull(rowVoucher,"strcodicefiscale");
	cognomeLav = StringUtils.getAttributeStrNotNull(rowVoucher,"strcognome");
	nomeLav = StringUtils.getAttributeStrNotNull(rowVoucher,"strnome");
	datAssegnazione = StringUtils.getAttributeStrNotNull(rowVoucher,"datassegnazione");
	datAttivazione = StringUtils.getAttributeStrNotNull(rowVoucher,"datattivazione");
	datChiusura = StringUtils.getAttributeStrNotNull(rowVoucher,"datfineerogazione");
	datMaxChiusura = StringUtils.getAttributeStrNotNull(rowVoucher,"datmaxerogazione");
	cfEnteAccreditato = StringUtils.getAttributeStrNotNull(rowVoucher,"strcfenteaccreditato");
	sedeEnteAccreditato = StringUtils.getAttributeStrNotNull(rowVoucher,"sedeEnteAccreditato");
	codTipoServizio = StringUtils.getAttributeStrNotNull(rowVoucher,"codtiposervizio");
	codVchTipoRisultato = StringUtils.getAttributeStrNotNull(rowVoucher,"codVchTipoRisultato");
	cpiCompTit = StringUtils.getAttributeStrNotNull(rowVoucher,"cpiCompTit");
	descRisultatoRaggiunto = StringUtils.getAttributeStrNotNull(rowVoucher,"strdesrisultato");
	codVchStatoPagamento = StringUtils.getAttributeStrNotNull(rowVoucher,"codVchStatoPagamento");
	if (codTipoServizio.equalsIgnoreCase(it.eng.sil.module.voucher.Properties.SERVIZIO_A_RISULTATO) && 
		codStatoVch.equalsIgnoreCase(StatoEnum.CONCLUSO.getCodice()) &&
		codVchTipoRisultato.equals("")) {
		codVchTipoRisultato = it.eng.sil.module.voucher.Properties.COD_RISULTATO_NON_CONSEGUITO;
	}
	decValTotale = rowVoucher.getAttribute("decvaltot");
	decValSpeso = rowVoucher.getAttribute("decspesaeffettiva");
	decValUnitario = rowVoucher.getAttribute("decvalunitario");
	decSpesoPagamento = rowVoucher.getAttribute("decpagato");
	if (decValTotale != null) {
		decValTotaleStr = decValTotale.toString();
	}
	if (decValSpeso != null) {
		decValSpesoStr = decValSpeso.toString();
	}
	if (decValUnitario != null) {
		decValUnitarioStr = decValUnitario.toString();
	}
	if (decSpesoPagamento != null) {
		decSpesoPagamentoStr = decSpesoPagamento.toString();
	}
	
	datProrogaAttivazione = StringUtils.getAttributeStrNotNull(rowVoucher,"strDataProrogaAtt");
	utenteProrogaAttivazione = StringUtils.getAttributeStrNotNull(rowVoucher,"utenteProrogaAtt"); 
	giorniProrogaAttivazione = (BigDecimal)rowVoucher.getAttribute("giorniProrogaAtt");
	if (giorniProrogaAttivazione != null && giorniProrogaAttivazione.intValue()>0) {
		canProrogaAtt = false;
		strGiorniProrogaAttivazione = giorniProrogaAttivazione.toString();
		messaggioProrogaAttivazione = "Proroga Attivazione effettuata  di  <b>"+strGiorniProrogaAttivazione+"  Giorni </b> il <b>"+datProrogaAttivazione+"</b> da <b>"+utenteProrogaAttivazione + "</b>";

	}
	datProrogaChiusura = StringUtils.getAttributeStrNotNull(rowVoucher,"strDataProrogaChi");
	utenteProrogaChiusura = StringUtils.getAttributeStrNotNull(rowVoucher,"utenteProrogaChi"); 
	giorniProrogaChiusura = (BigDecimal)rowVoucher.getAttribute("giorniProrogaChi");
	if (giorniProrogaChiusura != null && giorniProrogaChiusura.intValue()>0) {
		canProrogaChi = false;
		strGiorniProrogaChiusura = giorniProrogaChiusura.toString();
		messaggioProrogaChiusura = "Proroga Chiusura effettuata  di  <b>"+strGiorniProrogaChiusura+"  Giorni </b> il <b>"+datProrogaChiusura+"</b> da <b>"+utenteProrogaChiusura + "</b>";
	}
	codiceIBAN = StringUtils.getAttributeStrNotNull(rowVoucher,"stribanfattura");
	strFlagCM = StringUtils.getAttributeStrNotNull(rowVoucher,"flgCM");
	if(StringUtils.isFilledNoBlank(strFlagCM) && strFlagCM.equalsIgnoreCase("S")){
		strFlagCM = "Si";
	}else{
		strFlagCM = "No";
	}
}

%>
<html>
<head>
	<%@ include file="../global/fieldChanged.inc" %>
<title>Dettaglio Voucher</title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
 <af:linkScript path="../../js/" />
 
 <script language="JavaScript">

 function controlla() {
	if (document.FrmProroga.numGiorniProAttivazione.value != "" && document.FrmProroga.numGiorniProChiusura.value != "") {
		alert("Puoi prorogare o la scadenza di attivazione o la scadenza di conclusione, non entrambe contemporaneamente.\nRicordiamo che la proroga dell'attivazione provoca uno uguale slittamento della data di conclusione automaticamente");
		return false;
	}
	if (document.FrmProroga.numGiorniProAttivazione.value != "" || document.FrmProroga.numGiorniProChiusura.value != "") {
		if (document.FrmProroga.numGiorniProAttivazione.value != "") {
			if (parseInt(document.FrmProroga.numGiorniProAttivazione.value,10) <= 0) {
				alert("Il campo \"giorni di proroga\" di attivazione deve essere maggiore di zero");
				return false;
			}
		}
		if (document.FrmProroga.numGiorniProChiusura.value != "") {
			if (parseInt(document.FrmProroga.numGiorniProChiusura.value,10) <= 0) {
				alert("Il campo \"giorni di proroga\" di chiusura deve essere maggiore di zero");
				return false;
			}
		}
		return true;
	}
	/*else {
		alert("Valorizzare almeno uno dei campi \"giorni di proroga\"");
		return false;
	}*/
 }
 
 </script>
 
 </head>
 
 <body class="gestione">
 
<p class="titolo">Titolo di Acquisto</p>
 
<p>
	<font color="green">
 		<af:showMessages prefix="M_ProrogaVoucher"/>
  	</font>
  	<font color="red"><af:showErrors /></font>
</p>

<af:form name="FrmProroga" method="POST" action="AdapterHTTP" onSubmit="controlla()">
<%out.print(htmlStreamTop);%>
	<table class="main">
	<tr>
  		<td class="etichetta">Obiettivo Misura&nbsp;</td>
  		<td class="campo">
		  <af:textBox type="text" name="obiettivoAz" title="Obiettivo Misura"
			                        value="<%=descRaggAzione%>"
			                        classNameBase="input"
			                        readonly="true"
			                        size="70"/>
		</td>
	</tr>
	<tr>
  		<td class="etichetta">Servizio/Azione&nbsp;</td>
  		<td class="campo">
		  <af:textBox type="text" name="azionedesc" title="Servizio/Azione"
			                        value="<%=descAzione%>"
			                        classNameBase="input"
			                        readonly="true"
			                        size="70"/>
		</td>
	</tr>
	<tr>
  		<td class="etichetta">TDA soggetti in CM o svantaggiati&nbsp;</td>
  		<td class="campo">
		  <af:textBox type="text" name="tdaCM" title="TDA soggetti in CM o svantaggiati"
			                        value="<%=strFlagCM%>"
			                        classNameBase="input"
			                        readonly="true"
			                        size="70"/>
		</td>
	</tr>
	<tr>
  		<td class="etichetta">Ente attivato&nbsp;</td>
  		<td class="campo">
  		  <af:textBox type="text" name="enteattivato" title="Ente Attivato"
			                        value="<%=cfEnteAccreditato%>"
			                        classNameBase="input"
			                        readonly="true"
			                        size="30"/>
		</td>
	</tr>
	<tr>
  		<td class="etichetta">Sede Ente attivato&nbsp;</td>
  		<td class="campo">
  		  <af:textBox type="text" name="sedeenteattivato" title="Sede Ente Attivato"
			                        value="<%=sedeEnteAccreditato%>"
			                        classNameBase="input"
			                        readonly="true"
			                        size="70"/>
		</td>
	</tr>
	<tr>
  		<td class="etichetta">CPI&nbsp;</td>
  		<td class="campo">
  		  <af:textBox type="text" name="cpiCompTit" title="CPI"
			                        value="<%=cpiCompTit%>"
			                        classNameBase="input"
			                        readonly="true"
			                        size="70"/>
		</td>
	</tr>
	<tr>
  		<td class="etichetta" nowrap>Codice Attivazione&nbsp;</td>
  		<td>
	  		<table class="main" style="width: 50%">
			<tr>
  				<td class="campo">
  					<af:textBox type="text" name="codattivazione" title="Codice Attivazione"
				                        value="<%=codAttivazione%>"
				                        classNameBase="input"
				                        readonly="true"
				                        size="21"/>
				</td>
				<td class="etichetta" nowrap>Stato Titolo&nbsp;</td>
				<td class="campo">
					<af:textBox type="text" name="statotitolo" title="Stato Titolo"
		                        value="<%=descStatoVch%>"
		                        classNameBase="input"
		                        readonly="true"
		                        size="10"/>
				</td>
			</tr>

			</table>
		</td>
	</tr>
	<tr>
  		<td class="etichetta" nowrap>Motivo annullamento voucher&nbsp;</td>
  		<td class="campo">
			<af:textBox type="text" name="descAnnullVch" title="Descrizione Annullamento"
				                        value="<%=descAnnullVch%>"
				                        classNameBase="input"
				                        readonly="true"
				                        size="50"/>
	     </td>
	</tr>
	<tr>
  		<td class="etichetta" nowrap>Lavoratore&nbsp;</td>
  		<td class="campo">
			<af:textBox type="text" name="cognomelav" title="Lavoratore"
		                        value='<%=nomeLav+" "+cognomeLav%>'
		                        classNameBase="input"
		                        readonly="true"
		                        size="30"/>
	     </td>
	</tr>
	<tr>
  		<td class="etichetta" nowrap>CF&nbsp;</td>
  		<td class="campo">
			  <af:textBox type="text" name="cflavoratore" title="Lavoratore"
				                        value="<%=cfLav%>"
				                        classNameBase="input"
				                        readonly="true"
				                        size="20"/>
		</td>
	</tr>
	<tr>
		<td class="etichetta" nowrap>Assegnato il&nbsp;</td>
  		<td>
	  		<table class="main" style="width: 50%">
			<tr>
				<td class="campo">
					<af:textBox type="date" name="datassegnato" title="Assegnato il"
				                        value="<%=datAssegnazione%>"
				                        classNameBase="input"
				                        readonly="true"
				                        size="11" maxlength="10"/>
				</td>
				<td class="etichetta" nowrap>Attivato il&nbsp;</td>
	  			<td class="campo">
	  				<af:textBox type="date" name="dataattivato" title="Attivato il"
				                        value="<%=datAttivazione%>"
				                        classNameBase="input"
				                        readonly="true"
				                        size="11" maxlength="10"/>
				</td>
			</tr>
			</table>
		</td>
	</tr>
	<tr>
  		<td class="etichetta" nowrap>Valore massimo EURO&nbsp;</td>
  		<td class="campo">
			  <af:textBox type="text" name="decValMassimo" title="Valore massimo EURO"
				                        value="<%=decValTotaleStr%>"
				                        classNameBase="input"
				                        readonly="true"
				                        size="10"/>
		</td>
	</tr>
	<%if (codTipoServizio.equalsIgnoreCase(it.eng.sil.module.voucher.Properties.SERVIZIO_A_RISULTATO)) {%>
	<tr>
  		<td class="etichetta" nowrap>Risultato raggiunto&nbsp;</td>
  		<td>
	  		<table class="main" style="width: 50%">
			<tr>
  				<td class="campo">
  					<af:comboBox classNameBase="input" name="codVchTipoRisultato" moduleName="M_GetTipoRisultatoTDA"
            			addBlank="true" selectedValue="<%=codVchTipoRisultato%>" title="Risultato raggiunto" disabled="true" />  
				</td>
			</tr>
			</table>
		</td>
	</tr>
	<%}%>
	<tr>
		<td class="etichetta" nowrap>Concluso il&nbsp;</td>
  		<td>
	  		<table class="main" style="width: 50%">
			<tr>
				<td class="campo" nowrap>
					<af:textBox type="date" name="dataattivato" title="Concluso il"
				                        value="<%=datChiusura%>"
				                        classNameBase="input"
				                        readonly="true"
				                        size="11" maxlength="10"/>
				</td>
				<td class="etichetta" nowrap>Rimborso Previsto EURO&nbsp;</td>
	  			<td class="campo">
			  		<af:textBox type="text" name="decValSpeso" title="Rimborso Previsto EURO"
			                        value="<%=decValSpesoStr%>"
			                        classNameBase="input"
			                        readonly="true"
			                        size="10"/>
				</td>
			</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td class="etichetta" nowrap>Stato Pagamento&nbsp;</td>
  		<td>
	  		<table class="main" style="width: 50%">
			<tr>
				<td class="campo" nowrap>
			  		<af:comboBox classNameBase="input" name="codVchStatoPagamento" moduleName="M_GetStatiPagamentiVoucher"
           				addBlank="true" selectedValue="<%=codVchStatoPagamento%>" title="Stato Pagamento" disabled="true"/>
				</td>
				<td class="etichetta" nowrap>Importo Pagato EURO&nbsp;</td>
	  			<td class="campo">
			  		<af:textBox type="number" name="decpagato" title="Importo Pagato EURO"
			                        value="<%=decSpesoPagamentoStr%>"
			                        classNameBase="input" readonly="true"
			                        size="10"/>
				</td>
			</tr>
			</table>
		</td>
	</tr>
	
	
	
	<tr>
  		<td class="etichetta" nowrap>IBAN/Numero Fattura&nbsp;</td>
  		<td class="campo">
		<af:textBox type="text" name="codiceIBAN" title="IBAN/Numero Fattura" maxlength="27" readonly="true"
			value="<%=codiceIBAN%>" classNameBase="input" size="30"/>
		</td>
	</tr>
	
	
	
	<tr>
	<td colspan="2">
		<table class="main">
		
		<%if(messaggioProrogaAttivazione != null){ %>
			<tr>
				<td colspan="4" style="text-align: inherit; width: 100%;">
				<%=messaggioProrogaAttivazione %>
			  	</td>   
			</tr>
		<%}%>
		<tr>
	  		<td class="etichetta" nowrap>Attivare entro il&nbsp;</td>
	  		<td class="campo">
			  <af:textBox type="text" name="datmaxattivazione" title="Attivare entro il"
				                        value="<%=datMaxAttivazione%>"
				                        classNameBase="input"
				                        readonly="true"
				                        size="20"/>
			</td>
			<%if (canProrogaAtt && canModify && codStatoVch.equalsIgnoreCase(StatoEnum.ASSEGNATO.getCodice())) {%>
				<td class="etichetta" nowrap>+&nbsp;Giorni di proroga</td>
		  		<td class="campo">
				  <af:textBox type="integer" name="numGiorniProAttivazione" title="Giorni proroga attivazione" value=""
					                        classNameBase="input" validateOnPost="true"
					                        readonly="<%=String.valueOf(!canModify)%>"
					                        size="5"/>
				</td>
			<%} else {
				canProrogaAtt = false;
			%>
				<td class="campo">
					<input type="hidden" name="numGiorniProAttivazione" value="">
				</td>
			<%}%>
		</tr>
		</table>
	</td>
	</tr>
	<tr>
	<td colspan="2">
		<table class="main">
		<%if(messaggioProrogaChiusura != null){ %>
			<tr>
				<td colspan="4" style="text-align: inherit; width: 100%;">
				<%=messaggioProrogaChiusura %>
			  	</td>   
			</tr>
		<%}%>
		<tr>
	  		<td class="etichetta" nowrap>Concludere entro&nbsp;</td>
	  		<td class="campo">
			  <af:textBox type="text" name="datmaxconclusione" title="Concludere entro"
				                        value="<%=datMaxChiusura%>"
				                        classNameBase="input"
				                        readonly="true"
				                        size="20"/>
			</td>
			<%if ( (canProrogaChi && canModify) && (codStatoVch.equalsIgnoreCase(StatoEnum.ASSEGNATO.getCodice()) || codStatoVch.equalsIgnoreCase(StatoEnum.ATTIVATO.getCodice())) ) {%>
				<td class="etichetta" nowrap>+&nbsp;Giorni di proroga</td>
		  		<td class="campo">
				  <af:textBox type="integer" name="numGiorniProChiusura" title="Giorni proroga chiusura" value=""
					                        classNameBase="input" validateOnPost="true"
					                        readonly="<%=String.valueOf(!canModify)%>"
					                        size="5"/>
				</td>
			<%} else {
				canProrogaChi = false;
			%>
				<td class="campo">
					<input type="hidden" name="numGiorniProChiusura" value="">
				</td>
			<%}%>
		</tr>
		</table>
	</td>
	</tr>
	</table>
	
	<br>
	<center>
	<table>
		<tr>
		<% boolean viewPulsanteProroga=  canProrogaVoucher && (canProrogaAtt || canProrogaChi) && 
			  !codStatoVch.equalsIgnoreCase(StatoEnum.ANNULLATO.getCodice()) && !codStatoVch.equalsIgnoreCase(StatoEnum.CONCLUSO.getCodice());
			if (viewPulsanteProroga) {%>
			<td><input class="pulsante" type="submit" name="confermaProroghe" value="Conferma proroghe"></td>
			<td>&nbsp;</td>
		<%}%>
		<td><input type="button" class="pulsante" name="chiudi" value="Chiudi" onClick="javascript:window.close();"/></td>
		</tr>
	</table>
	</center>
	<%out.print(htmlStreamBottom);%>
	
	<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLav%>">
	<input type="hidden" name="PAGE" value="DettaglioScadenzaVoucherPage">
	<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
	<input type="hidden" name="prgPercorso" value="<%=prgPercorso%>">  
	<input type="hidden" name="prgColloquio" value="<%=prgColloquio%>">
	<input type="hidden" name="prgVoucher" value="<%=prgVoucher%>">
	<input type="hidden" name="numklovoucher" value="<%=strNumkloVoucher%>">

</af:form>
</body>
</html>