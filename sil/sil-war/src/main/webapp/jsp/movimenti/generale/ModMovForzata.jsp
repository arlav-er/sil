<!-- @author: Giovanni D'Auria - 20 Maggio 2005 
Questa jsp si occupa della modifica di aluni dati di un movimento senza precedente
Alessandro Pegoraro - Maggio 2010 - Modificata la pagina per le modifiche ai movimenti richieste da Trento
-->

<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../../global/noCaching.inc"%>
<%@ include file="../../global/getCommonObjects.inc"%>



<%@ page
	import="com.engiweb.framework.base.*,com.engiweb.framework.dispatching.module.*,com.engiweb.framework.util.*,it.eng.sil.module.movimenti.*,it.eng.afExt.utils.*,it.eng.sil.security.*,it.eng.sil.util.*,it.eng.sil.*,java.util.*,java.text.*,java.math.*,java.io.*,com.engiweb.framework.security.*"%>


<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%
	//Recupero i dati del movimento
	SourceBean data = (SourceBean) serviceResponse
			.getAttribute("GetMovimento.ROWS.ROW");

	String codfiscale = (String) serviceRequest
			.getAttribute("STRCODICEFISCALE");

	String dataInizioAttuale = (String) data
			.getAttribute("DATINIZIOMOV");
	String dataFineAttuale = (String) data.getAttribute("DATFINEMOV");
	String codtipomov = (String) data.getAttribute("CODTIPOMOV");
	BigDecimal prgmovimentoprecB = (BigDecimal) data
			.getAttribute("PRGMOVIMENTOPREC");
	BigDecimal prgmovimentosuccB = (BigDecimal) data
			.getAttribute("PRGMOVIMENTOSUCC");
	String prgmovimentoprec = prgmovimentoprecB == null ? ""
			: prgmovimentoprecB.toString();
	String prgmovimentosucc = prgmovimentosuccB == null ? ""
			: prgmovimentosuccB.toString();
	
	String movseg = (String) data.getAttribute("CODMONOTIPOFINE");
	BigDecimal cdnLavMovimento = (BigDecimal)data.getAttribute("cdnLavoratore");

	if (codtipomov.compareTo("TRA") == 0)
		codtipomov = "TRASFORMAZIONE";
	else if (codtipomov.compareTo("PRO") == 0)
		codtipomov = "PROROGA";
	else if (codtipomov.compareTo("CES") == 0)
		codtipomov = "CESSAZIONE";
	else if (codtipomov.compareTo("AVV") == 0)
		codtipomov = "AVVIAMENTO";

	String prgMov = ((BigDecimal) data.getAttribute("PRGMOVIMENTO"))
			.toString();
	String numKloMov = (String) data.getAttribute("NUMKLOMOV");
	BigDecimal cdnUtIns = new BigDecimal((String) data
			.getAttribute("CDNUTINS"));
	String dtmIns = (String) data.getAttribute("DTMINS");
	BigDecimal cdnUtMod = new BigDecimal((String) data
			.getAttribute("CDNUTMOD"));
	String dtmMod = (String) data.getAttribute("DTMMOD");
	Testata testata = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
	String prgAzienda = ((BigDecimal) data.getAttribute("PRGAZIENDA"))
			.toString();
	String prgUnita = ((BigDecimal) data.getAttribute("PRGUNITA"))
			.toString();
	String _funzione = (String) serviceRequest
			.getAttribute("CDNFUNZIONE");
	String strEsito = (serviceResponse
			.containsAttribute("UPDATEMOVIMENTO.ESITO")) ? (String) serviceResponse
			.getAttribute("UPDATEMOVIMENTO.ESITO")
			: "";
	boolean esito = (!strEsito.equals("") && strEsito
			.equalsIgnoreCase("OK"));
	boolean precedente = false;
	if (serviceRequest.containsAttribute("PRECEDENTE")) {
		precedente = (((String) serviceRequest
				.getAttribute("PRECEDENTE")).equalsIgnoreCase("true")) ? true
				: false;
	}

	String strPartitaIvaAz = "";
	String strCodiceFiscaleAz = "";
	String strRagioneSocialeAz = "";
	String strIndirizzoUAz = "";
	String strComuneUAz = "";
	String codTipoAzienda = "";

	//Genero informazioni sull'azienda
	if (!prgAzienda.equals("") && !prgUnita.equals("")
			&& !prgAzienda.equals("null") && !prgUnita.equals("null")) {
		InfoAzienda datiAz = new InfoAzienda(prgAzienda, prgUnita);
		strPartitaIvaAz = datiAz.getPIva();
		strCodiceFiscaleAz = datiAz.getCodiceFiscale();
		strRagioneSocialeAz = datiAz.getRagioneSociale();
		strIndirizzoUAz = datiAz.getIndirizzo();
		strComuneUAz = datiAz.getComune();
		codTipoAzienda = datiAz.getTipoAz();
	}

	String htmlStreamTop = StyleUtils.roundTopTable(true);
	String htmlStreamBottom = StyleUtils.roundBottomTable(true);
%>

<HTML>
<HEAD>
<title>Modifica Movimento</title>
<SCRIPT language="javascript">
	var _funzione = '<%=_funzione%>';
	var precedente = '<%=precedente%>';
	var imgChiusa = "../../img/chiuso.gif";
	var imgAperta = "../../img/aperto.gif";
</SCRIPT>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<script type="text/javascript"
	src="../../js/movimenti/common/MovimentiSezioniATendina.js"
	language="JavaScript"></script>
<script type="text/javascript"
	src="../../js/movimenti/common/MovimentiRicercaSoggetto.js"
	language="JavaScript"></script>
<af:linkScript path="../../js/" />
<SCRIPT language="javascript">
	
	function cambiaLavMC(elem,stato){
	  divVar = document.getElementById(elem);
	  divVar.style.display = stato;
	}
	function refreshPage(){
		window.opener.reload('<%=codfiscale%>');
	    window.close();
		}
	function aggiorna(){
		var cambiaTutto= false;
		if (isInSubmit()) return;
		
		
		if(document.frm.dataInizioMov.value == "" && document.frm.dataFineMov.value == ""){
			alert("E' necessario che almeno una data sia valorizzata.");
			return;
		}
		
		
		if (document.frm.dataInizioMov.value == "" || precedente == "true"){
			document.frm.dataInizioMov.value  = document.frm.datInizioAttuale.value;			
		}
		
			var url ="AdapterHTTP?PAGE=ModificaMovForzataPage&DATINIZIOMOV="+ document.frm.dataInizioMov.value;
			url = url + "&CDNLAVORATORE=<%=cdnLavMovimento%>";
			url = url + "&NUMKLOMOV="+<%=numKloMov%>;
			url = url + "&DATFINEMOV="+document.frm.dataFineMov.value ;
			url = url + "&PRGMOV="+<%=prgMov%>+"&AGGIORNA_MOV=";
			url = url + "&prgmovimentoprec=" + document.frm.prgprec.value + "&prgmovimentosucc="+ document.frm.prgsucc.value;
			url = url + "&PRECEDENTE=" + precedente;
			url = url + "&MOVSEG=" + document.frm.movseg.value;
			url = url + "&STRCODICEFISCALE="+'<%=codfiscale%>'; 
			if(cambiaTutto){
				url = url + "&cambiaTutto=";
			}
			
			setWindowLocation(url);
		//}		
	}

	 function fieldChanged() {} // lasciata per compatibilità
	 
	 function caricaMsg(){
	 	var esito = <%=esito%>;
	 	if(esito){
	 			alert("Per visualizzare le modifiche \nuscire e rientrare nel dettaglio del movimento o consultare la lista")
	 	}
	 }
</SCRIPT>
</HEAD>
<BODY onload="caricaMsg()">
<af:form name="frm" action="AdapterHTTP" method="post">
	<p class="titolo">Modifica Forzata movimento</p>
	<br/>
	<af:showMessages prefix="M_UpdateMovimentoForz" />
	<af:showErrors />
	<%
		//out.print(htmlStreamTop);
	%>
	<table class="main" border="0">

		<TD>
		<%
			out.print(htmlStreamTop);
		%>
		<TABLE width="100%">
			<TR>
				<TD><B>Situazione attuale:</B></TD>
			</TR>
			<TR>
				<TD>Movimento di &nbsp; <af:textBox classNameBase="input"
					type="date" name="datInizioAttuale" value="<%=codtipomov%>"
					readonly="true" size="14" maxlength="14" /></TD>
			</TR>
			<TR>
				<TD>Data inizio: <af:textBox onKeyUp="fieldChanged();"
					classNameBase="input" type="date" name="datInizioAttuale"
					value="<%=dataInizioAttuale%>" readonly="true" size="11"
					maxlength="11" /></TD>
			</TR>
			<TR>
				<TD>Data fine: &nbsp; <af:textBox onKeyUp="fieldChanged();"
					classNameBase="input" type="date" name="datFineAttuale"
					value="<%=dataFineAttuale%>" readonly="true" size="11"
					maxlength="11" /></TD>
			</TR>
			<tr>
				<td colspan="4" valign="top">
				<div class='sezione2' id='SedeAzienda'><img
					id='tendinaAzienda' alt="Apri" src="../../img/chiuso.gif"
					onclick="cambiaTendina(this,'sezioneAzienda', '');" />Sede
				Azienda&nbsp;&nbsp;</div>
				</td>
			</tr>
			<TR>
				<TD>
				<div id="sezioneAzienda" style="display: none">
				<table class="main" border="0">
					<tr>
						<td class="etichetta" nowrap="true">Codice Fiscale</td>
						<td class="campo"><af:textBox classNameBase="input"
							type="text" name="codFiscaleAzProv" readonly="true"
							value="<%=strCodiceFiscaleAz%>" size="30" maxlength="16" /></td>
					</tr>
					<tr valign="top">
						<td class="etichetta" nowrap="true">Partita IVA</td>
						<td class="campo"><af:textBox classNameBase="input"
							type="text" name="pIvaAzProv" readonly="true"
							value="<%=strPartitaIvaAz%>" size="30" maxlength="11" /></td>
					</tr>
					<tr>
						<td class="etichetta" nowrap="true">Ragione Sociale</td>
						<td class="campo"><af:textBox classNameBase="input"
							type="text" name="ragioneSocialeAzProv" readonly="true"
							value="<%=strRagioneSocialeAz%>" size="60" maxlength="150" /></td>
					</tr>
					<tr>
						<td class="etichetta" nowrap="true">Indirizzo (Comune)</td>
						<td class="campo"><af:textBox classNameBase="input"
							type="text" name="IndirizzoAzProv" readonly="true"
							value="<%= strIndirizzoUAz %>" size="60" maxlength="150" /></td>
					</tr>
					<tr>
						<td class="etichetta" nowrap="true">Tipo Azienda</td>
						<td class="campo"><af:textBox classNameBase="input"
							type="text" name="descrTipoAzProv" readonly="true"
							value="<%=codTipoAzienda%>" size="30" maxlength="30" /></td>
					</tr>
				</table>
				</div>

				</TD>
			</TR>

		</TABLE>
		<%
			out.print(htmlStreamBottom);
		%>
		
		<%out.print(htmlStreamTop);%>

		<TABLE width="100%">
			<TR>
				<TD width="25%"><B>Nuove informazioni</B></TD>
				<TD></TD>
			</TR>
			<TR>
				<TD>&nbsp;</TD><TD></TD>
			</TR>
			<TR>
				<TD>Data inizio: </TD><TD><af:textBox onKeyUp="fieldChanged();"
					classNameBase="input" type="date" name="dataInizioMov"
					value="<%=dataInizioAttuale%>" validateOnPost="true" size="11"
					maxlength="11" required="true" title="Data inizio movimento" /></TD>
			</TR>
			<TR>
				<TD>Data fine: &nbsp;&nbsp;</TD><TD><af:textBox
					onKeyUp="fieldChanged();" classNameBase="input" type="date"
					name="dataFineMov" value="<%=dataFineAttuale%>"
					validateOnPost="true" size="11" maxlength="11" required="false"
					title="Data fine movimento" /></TD>
			</TR>

			<TR>
				<TD>Movimento Precedente:&nbsp;&nbsp;</TD><TD align="left"> <af:textBox
					onKeyUp="fieldChanged();" classNameBase="input" type="text"
					name="prgprec" value="<%=prgmovimentoprec%>" size="11"
					maxlength="11" required="false" title="Movimento Precedente" /></TD>
			</TR>

			<TR>
				<TD>Movimento Successivo: &nbsp;&nbsp;</TD><TD><af:textBox
					onKeyUp="fieldChanged();" classNameBase="input" type="text"
					name="prgsucc" value="<%=prgmovimentosucc%>" size="11"
					maxlength="11" required="false" title="Movimento Successivo" /></TD>
			</TR>
			
			<TR>
                <TD>Tipo Movimento Seg.: &nbsp;&nbsp;</TD><TD><af:textBox
                    onKeyUp="fieldChanged();" classNameBase="input" type="text"
                    name="movseg" value="<%=movseg%>" size="1"
                    maxlength="1" required="false" title="Movimento Seguente" /></TD>
            </TR>


		</TABLE>
		<%
			out.print(htmlStreamBottom);
		%>
		</TD>

	</table>


	<table class="main">

		<td><input type="button" class="pulsanti" value="Chiudi"
			onClick="refreshPage()"> <input type="button"
			class="pulsanti" value="Aggiorna" onclick="aggiorna()"></td>
	</table>


	<%
		out.print(htmlStreamBottom);
	%>
</af:form>
<%
	if (testata != null) {
%>
<div align="center">
<%
	testata.showHTML(out);
%>
</div>
<%
	}
%>
<br>

<table width="100%">
	<tr>
		<td colspan="3" style="">N.B.: Le modifiche effettuate in questa
		pagina hanno lo scopo di correggere anomalie dei movimenti del
		lavoratore. Non vengono effettuati controlli sui valori inseriti,
		l'intervento è manuale. Eventuali inesattezze nei valori inseriti in
		questa pagina possono compromettere il funzionamento del sistema per
		questo lavoratore.</td>
	</tr>
</table>
</BODY>
</HTML>
