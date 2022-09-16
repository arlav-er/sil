<%--
	Savino 24/06/05
	
La jsp e' la composizione di tre pagine gia' esistenti: anag/AnagDettaglio_Anag.jsp, anag/AnagDettaglio_Indirizzi.jsp,
	patto/DispoDettaglio.jsp, le cui parti dati sono state inserite in file inclusi staticamente.
	Le informazioni visualizzate sono prelevate con le query usate dai moduli delle page associate alle jsp di partenza.
	Queste query vengono eseguite due volte,una per ogni lavoratore, ed i risultati sono inseriti in due SourceBean 
	diversi, accessibili in "GET_DETT_LAV_DA_ACCORPARE.LAV1" e "GET_DETT_LAV_DA_ACCORPARE.LAV2". 
	Dato che e' stato necessario duplicare i campi valore delle tre jsp di partenza, per semplicita' ho utilizzato 
	dei Bean (o quasi, dato che le proprieta' sono pubbliche e non ci sono i metodi getter), Did, DatiLavoratore, 
	Indirizzi, per recuperare le informazioni. 
	I costruttori utilizzano il codice copiato di sana pianta dalle jsp originarie, per cui la valorizzazione e' identica.
	Anche la dichiarazione e' stata copiata, quindi sono identici anche i valori di default.
	In questo modo ho evitato di duplicare un mare di variabili.
	
--%>

<%@ page
	contentType="text/html;charset=utf-8"
	
	import="com.engiweb.framework.base.*,
			it.eng.sil.security.*,
			it.eng.afExt.utils.*,
			it.eng.sil.util.*,
			java.math.*,
			java.util.*"
	
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../../global/noCaching.inc" %>
<%@ include file="../../global/getCommonObjects.inc" %>

<%
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE");

	// Lettura parametri dalla REQUEST
	int     cdnFunzione      = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	String  cdnFunzioneStr   = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione");
	// il cdnLavoratore della prima sezione della pagina di ricerca
	String  cdnLavoratore1    = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore1");
	// il cdnLavoratore della seconda sezione della pagina di ricerca
	String  cdnLavoratore2    = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore2");
	
	// CONTROLLO ACCESSO ALLA PAGINA
	ProfileDataFilter filter = new ProfileDataFilter(user, "AccorpaLavRicercaPage");
	boolean canView = filter.canView();
	if (! canView){
		response.sendRedirect("../../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}

	// CONTROLLO PERMESSI SULLA PAGINA
	PageAttribs attributi = new PageAttribs(user, "AccorpaLavRicercaPage");

	boolean canManage = attributi.containsButton("ACCORPA");
	// voglio la pagina con i campi non modificabili in sola lettura
	boolean canModify = false;
	
	Object cdnUtCorrente    = sessionContainer.getAttribute("_CDUT_");
	String cdnUtCorrenteStr = StringUtils.getStringValueNotNull(cdnUtCorrente);

	///-----------------------------------------------------------------------------------------///////
	///-----------------------------------------------------------------------------------------///////
	// RECUPERO INFORMAZIONI DEI DUE LAVORATORI DA ACCORPARE
	//  vedi il modulo GET_DETT_LAV_DA_ACCORPARE in modules/amministrazione4.xml e relativa classe
	//
	SourceBean infoLav1 = (SourceBean)serviceResponse.getAttribute("GET_DETT_LAV_DA_ACCORPARE.LAV1");
	SourceBean infoLav2 = (SourceBean)serviceResponse.getAttribute("GET_DETT_LAV_DA_ACCORPARE.LAV2");
	///-----------------------------------------------------------------------------------------///////
	///-----------------------------------------------------------------------------------------///////
	String codiceFiscale1 =(String) infoLav1.getAttribute("GET_AN_LAVORATORE_ANAG.rows.row.STRCODICEFISCALE");
	String codiceFiscale2 = (String)infoLav2.getAttribute("GET_AN_LAVORATORE_ANAG.rows.row.STRCODICEFISCALE");
	
	// Sola lettura: viene usato per tutti i campi di input
	String readonly = "true";

	// Stringhe con HTML per layout tabelle
	String htmlStreamTop    = StyleUtils.roundTopTable(canManage);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canManage);
%>

<html>
<head>
<title>Accorpamento lavoratori</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" href="../../css/listdetail.css" type="text/css">

<af:linkScript path="../../js/"/>
<%@ include file="../../global/fieldChanged.inc" %>

<script language="Javascript">

	function goBack() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;		
		goTo("Page=AccorpaLavRicercaPage&cdnfunzione=<%=cdnFunzione%>&cdnLavoratore1=<%=cdnLavoratore1%>&cdnLavoratore2=<%=cdnLavoratore2%>&CARICA_LAVORATORI=1");
	}

	function onLoad() {
		rinfresca();
	}
	
	function accorpa (cdnLavDaAccorpare, cdnLavInCuiAccorpare, nLav1, nLav2) {
		msg = "Verrà accorpato il lavoratore " + nLav1 + " al " + nLav2 + ".\n"
		      + "Il lavoratore " + nLav1 + " verrà cancellato.\n"
		      + "Procedere con l'accorpamento?";
		if (!confirm(msg))
			return;
		cf1 = document.Frm1.codiceFiscale1.value;
		cf2 = document.Frm1.codiceFiscale2.value;
		cdnLavoratore1 = document.Frm1.cdnLavoratore1.value;
		if (cdnLavoratore1==cdnLavDaAccorpare) {
			// il lavoratore da cancellare e' il codiceFiscale1
			cf2 = cf1;
			cf1 = document.Frm1.codiceFiscale2.value;

		}
		<%-- il parametro codiceFiscale2 conterra' il codice fiscale del lavoratore che dovra' essere cancellato sull'indice regionale --%>
		urlPage = "AdapterHTTP?PAGE=ACCORPALAVESITOPAGE&cdnLavInCuiAccorpare="+cdnLavInCuiAccorpare+ 
					  "&cdnLavoratoreDaAccorpare="+cdnLavDaAccorpare+"&cdnFunzione=<%=cdnFunzione%>"+ 
					  "&cdnLavoratore1=<%=cdnLavoratore1%>&cdnLavoratore2=<%=cdnLavoratore2%>"+
					  "&codiceFiscale1="+cf1+"&codiceFiscale2="+cf2+"&codiceFiscaleTo="+cf1+"&codiceFiscaleFrom="+cf2;
					  
		setWindowLocation(urlPage);
	}
	
	var sezioni = new Array();

	function cambia(immagine, sezione) {
		if (sezione.aperta==null) sezione.aperta=true;
		if (sezione.aperta) {
			sezione.style.display="none";
			sezione.aperta=false;
			immagine.src="../../img/chiuso.gif";
		}
		else {
			sezione.style.display="inline";
			sezione.aperta=true;
			immagine.src="../../img/aperto.gif";
		}
	}
	function Sezione(sezione, img,aperta){    
	    this.sezione=sezione;
	    this.sezione.aperta=aperta;
	    this.img=img;
	}

	function initSezioni(sezione){
		sezioni.push(sezione);
	}

    <%-- Chiudo il menù contestuale (se aperto)--%>
	if (window.top.menu != undefined){
  		window.top.menu.location="AdapterHTTP?PAGE=MenuCompletoPage";
	}

<%--
	// Genera il Javascript che si occuperà di inserire i links nel footer
	attributi.showHyperLinks(out, requestContainer, responseContainer, null);
--%>

</script>
<style type="text/css">
	td.colonna1 {
		width:16%;
		color: #000066; 
		font-family: Verdana, Arial, Helvetica, Sans-serif; 
		font-size: 11px;  
		text-align: right;
		font-weight: normal;
		text-decoration: none;
		padding-right: 12px;
	}
	td.colonna2, td.colonna3 {
		width:42%; 
		color: #000066; 
		font-family: Verdana, Arial, Helvetica, Sans-serif;  
		font-size: 11px; 
		text-align: left;
		text-decoration: none;
	}
	table.sezione2 {
		border-collapse:collapse;
		font-family: Verdana,"Times New Roman", Arial, Sans-serif; 
		font-size: 12px;
		font-weight: bold;
		border-bottom-width: 2px; 
		border-bottom-style: solid;
		border-bottom-color: #000080;
		color:#000080; 
		position: relative;
		width: 94%;
		left: 4%;
		text-align: left;
		text-decoration: none;	
	}
</style>
</head>

<body class="gestione" onload="onLoad()">

<p class="titolo">Accorpamento lavoratore</p>

<af:showErrors />

<%= htmlStreamTop %>
<af:form name="Frm1" action="AdapterHTTP" method="POST">

	<%-- LA FORM NON VERRA' MAI INVIATA, PERO' I CAMPI CE LI METTO LO STESSO --%>
	<af:textBox type="hidden" name="cdnLavoratore1" value="<%= cdnLavoratore1 %>" />
	<af:textBox type="hidden" name="cdnLavoratore2" value="<%= cdnLavoratore2 %>" />
	<af:textBox type="hidden" name="PAGE" value="AccorpaLavEsitoPage" />
	<af:textBox type="hidden" name="cdnfunzione" value="<%= cdnFunzioneStr %>" />
	<af:textBox type="hidden" name="codiceFiscale1" value="<%= codiceFiscale1 %>" />
	<af:textBox type="hidden" name="codiceFiscale2" value="<%= codiceFiscale2 %>" />
	
<table class="main">
	<tr>
		<td>
			<table width="100%">
				<tr>
					<td class="colonna1">&nbsp;</td>
					<td class="colonna2" style="text-align:left">Lavoratore 1</td>
					<td class="colonna3" style="text-align:left">Lavoratore 2</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td><div class="sezione">Dati personali</div></td>
	</tr>
	<tr>
		<td>
	<%{ // per evitare dei conflitti con variabili presenti tra i vari file include e' preferibile
		// limitare il loro scope 
		// raccogliendo tra parentesi graffe l'istruzione di include		
	%>
	<%@ include file="_datiGenerali.inc" %>
	<%}%>	
		</td>		
	</tr>       	                                 
	<tr>
		<td>
	<%{// vedi commento sezione precedente %>
	<%@ include file="_indirizzi.inc" %>
	<%}%>	       
		</td>		
	</tr>
	<tr>
		<td>
	<%{// vedi commento sezione precedente %>
	<%@ include file="_did.inc" %>
	<%}%>	
		</td>		                             
	</tr>
	<tr>
		<td>
	<%{// vedi commento sezione precedente %>
	<%@ include file="_sap.inc" %>
	<%}%>	
		</td>		                             
	</tr>
	<tr>
		<td>
	<%{// vedi commento sezione precedente %>
	<%@ include file="_gestRose.inc" %>
	<%}%>
		</td>		
	</tr>
	<tr>
		<td>
	<%{// vedi commento sezione precedente %>
    <%@ include file="_gestCig.inc" %>  
    <%}%>
		</td>		
	</tr>	
	<tr>
        <td>
    <%{// vedi commento sezione precedente %>
    <%@ include file="_gestDidInps.inc" %>  
    <%}%>
        </td>       
    </tr>
    <tr>
        <td>
    <%{// vedi commento sezione precedente %>
    <%@ include file="_gestDichNeet.inc" %>  
    <%}%>
        </td>       
    </tr>  	
	<tr>
		<td colspan="3">
			<table class="main">  
				<tr><%if (canManage) {%>              
					<td class="colonna1">&nbsp;</td>
					<td class="colonna2" style="text-align:center">
						<input type="button" class="pulsanti"  value="Accorpa e cancella &gt;&gt;" 
							onclick="accorpa(document.Frm1.cdnLavoratore1.value,
											 document.Frm1.cdnLavoratore2.value,
											 1,2 )">&nbsp;
					<td class="colonna2" style="text-align:center">
						<input type="button" class="pulsanti"  value="&lt;&lt; Accorpa e cancella"
							onclick="accorpa(document.Frm1.cdnLavoratore2.value,
											 document.Frm1.cdnLavoratore1.value,
											 2,1 )">
					<%}%>
				</tr>
				<tr colspan="3"><td>&nbsp;</td></tr>
				<tr>
					<td class="colonna1">&nbsp;</td>
					<td align="center" colspan="2">
						<input type="button" class="pulsante" name="back" value="Chiudi" onclick="goBack()" />
					</td>
				</tr>
			</table>	
		</td>
	</tr>
</table>
<%= htmlStreamBottom %>

</af:form>

</body>
</html>