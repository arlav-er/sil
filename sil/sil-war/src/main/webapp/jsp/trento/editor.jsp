
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="net.fckeditor.*"%>

<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page
	import="com.engiweb.framework.base.*,
	        com.engiweb.framework.base.SourceBean,
			com.engiweb.framework.configuration.ConfigSingleton,
			com.engiweb.framework.error.EMFErrorHandler,
			it.eng.sil.module.movimenti.constant.Properties,
    		it.eng.afExt.utils.DateUtils, 
    		it.eng.sil.security.User, 
    		it.eng.sil.security.*, 
    		it.eng.afExt.utils.*,
    		it.eng.sil.util.*, 
    		java.lang.*,
    		java.text.*, 
    		java.math.*,  
    		java.sql.*,   
    		oracle.sql.*,  
    		java.util.*"
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%@ taglib uri="http://java.fckeditor.net" prefix="FCK"%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>FCKeditor - JSP Sample</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="robots" content="noindex, nofollow" />
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" href="../../css/listdetail.css" type="text/css">

<% String queryString = null; %>
<%@ include file="_apriGestioneDoc.inc"%>

<%

String pageToProfile = "RicercaTemplatePage";

ProfileDataFilter filter = new ProfileDataFilter(user, pageToProfile);
if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
}

PageAttribs attributi = new PageAttribs(user, pageToProfile);
boolean canModify = attributi.containsButton("SALVA_EDITOR");
//da modificare

 	SourceBean rowRegione = (SourceBean) serviceResponse.getAttribute("M_GetCodRegione.ROWS.ROW");
	String regione = StringUtils.getAttributeStrNotNull(rowRegione, "codregione"); 

	String TIPOOPERAZIONEEDITOR = (String) serviceRequest
			.getAttribute("TIPOOPERAZIONEEDITOR");
	String DOMINIODATI = (String) serviceRequest
			.getAttribute("DOMINIODATI");
	//System.out.println("DOMINIODATI : "+DOMINIODATI);
	String dominioTemlate = (String) serviceRequest.getAttribute("DOMINIO");
	//System.out.println("dominioTemlate : "+dominioTemlate);
	String FLGFIRMAGRAFO = StringUtils.getAttributeStrNotNull(serviceRequest, "FLGFIRMAGRAFO");
	
	//TIPOEDITOR stabilisce se sis sta valutando l'editor dalla parte "A"mministratore o "L"avoratore
	String TIPOEDITOR = (String) serviceRequest
			.getAttribute("TIPOEDITOR");
	
	String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
	String PRGCONFIGPROT = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGCONFIGPROT");

	String FILETEMPLATE = "";
	String PRGTEMPLATESTAMPA = null;
	BigDecimal NUMKLOTEMP = null;

	//CAMPI USATI NELL'AMBITO DEL LAVORATORE
	String CODTIPODOC = (String) serviceRequest
			.getAttribute("CODTIPODOC");
	String CDNLAVORATORE = (String) serviceRequest
			.getAttribute("CDNLAVORATORE");
	String AMBITO = (String) serviceRequest.getAttribute("AMBITO");
	String PRGTEMPLATE = (String) serviceRequest
			.getAttribute("NOMETEMPLATE");
	String NOMETEMPLATE = (String) serviceRequest
			.getAttribute("NOMETEMPLATEDESC");

	String DATAACQUISIZIONE = (String) serviceRequest
			.getAttribute("DATAACQUISIZIONE");
	String DATAINIZIO = (String) serviceRequest
			.getAttribute("DATAINIZIO");
	String DATAFINE = (String) serviceRequest.getAttribute("DATAFINE");
	String NOTE = (String) serviceRequest.getAttribute("NOTE");
	String MOSTRATEMPLATE = "S";
	
	String CODTIPODOMINIO = StringUtils.getAttributeStrNotNull(serviceRequest, "CODTIPODOMINIO");
	
	if (serviceRequest.containsAttribute("PRGTEMPLATESTAMPA"))
		PRGTEMPLATESTAMPA = (String) serviceRequest
				.getAttribute("PRGTEMPLATESTAMPA");

	if ("A".equalsIgnoreCase(TIPOEDITOR)) {
		if ("modifica".equalsIgnoreCase(TIPOOPERAZIONEEDITOR)) {
			if (serviceRequest.containsAttribute("NUMKLOTEMP"))
				NUMKLOTEMP = new BigDecimal(
						(String) serviceRequest.getAttribute("NUMKLOTEMP"));
			SourceBean cont = (SourceBean) serviceResponse
					.getAttribute("MDETTAGLIOEDITOR");
			SourceBean row = (SourceBean) cont.getAttribute("ROWS.ROW");

			FILETEMPLATE = row.containsAttribute("FILETEMPLATE") ?
					(row.getAttribute("FILETEMPLATE") != null?
						row.getAttribute("FILETEMPLATE").toString():"") : "";
			FILETEMPLATE = FILETEMPLATE.replace("\n", "").replace("\r",
					"");

			//ATTENZIONE QUI LEGGO NUMKLCTEM DALLA RICHIESTA MA PRIMA  SONO STATI AGGIORNATI I DATI GENERALI DEL TEMPLATE
			// CON NUMKLOTEMP + 1 QUINDI DEVO INCREMENTARE DI 1 IL NUMKLOTEMP

			//NUMKLOTEMP = NUMKLOTEMP.add(new BigDecimal(1));
		} else {
			MOSTRATEMPLATE = "N";
			
			//ENTRO NELL'ELSE PERCHè SONO IN INSERIMENTO QUINDI SALVO IN CACHE I DATI GENERALI DEL TEMPLATE INSERITI
			String[] fields = { "STRNOME", "AMBITO", "DATAFINE",
					"DATAINIZIO", "DOMINIO", "NOTE", "CODTIPOPROTOCOLLAZIONE", "FLGPREDISPOSTO", 
					"PRGTITOLARIO", "FLGFIRMAGRAFO", "FLGTRASMISSIONEINTERNA", "FILETEMPLATE" };
			NavigationCache formInserimentoTemplate = null;
			if (sessionContainer.getAttribute("EDITORCACHE") != null) {
				formInserimentoTemplate = (NavigationCache) sessionContainer
						.getAttribute("EDITORCACHE");
				FILETEMPLATE = formInserimentoTemplate.getField(
						"FILETEMPLATE").toString();
				FILETEMPLATE = FILETEMPLATE.replace("\n", "").replace(
						"\r", "");

			} else {
				//salvo in cache
				formInserimentoTemplate = new NavigationCache(fields);
				formInserimentoTemplate.enable();
				for (int i = 0; i < fields.length; i++)
					formInserimentoTemplate.setField(fields[i],
							(String) serviceRequest
									.getAttribute(fields[i]));
				sessionContainer.setAttribute("EDITORCACHE",
						formInserimentoTemplate);
			}

		}
	}

	//CARICA I DATI CPI
	SourceBean cont2 = (SourceBean) serviceResponse
			.getAttribute("MDATICPI");
	SourceBean row2 = (SourceBean) cont2.getAttribute("ROWS.ROW");
	
	String COD_CPI = row2.containsAttribute("COD_CPI") ? row2
			.getAttribute("COD_CPI").toString() : "";
	String DESC_CPI = row2.containsAttribute("DESC_CPI") ? row2
			.getAttribute("DESC_CPI").toString() : "";
	String INDIRIZZO = row2.containsAttribute("INDIRIZZO") ? row2
			.getAttribute("INDIRIZZO").toString() : "";
	String LOCALITA = row2.containsAttribute("LOCALITA") ? row2
			.getAttribute("LOCALITA").toString() : "";
	String CAP = row2.containsAttribute("CAP") ? row2.getAttribute(
			"CAP").toString() : "";
	String CODCOM = row2.containsAttribute("CODCOM") ? row2
			.getAttribute("CODCOM").toString() : "";
	String CODPROVINCIA = row2.containsAttribute("CODPROVINCIA") ? row2
			.getAttribute("CODPROVINCIA").toString() : "";
	String COMUNE = row2.containsAttribute("COMUNE") ? row2
			.getAttribute("COMUNE").toString() : "";
	String PROVINCIA = row2.containsAttribute("PROVINCIA") ? row2
			.getAttribute("PROVINCIA").toString() : "";
	String TELEFONO = row2.containsAttribute("TELEFONO") ? row2
			.getAttribute("TELEFONO").toString() : "";
	String FAX = row2.containsAttribute("FAX") ? row2.getAttribute(
			"FAX").toString() : "";
	String EMAIL = row2.containsAttribute("EMAIL") ? row2.getAttribute(
			"EMAIL").toString() : "";
	String ORARIO = row2.containsAttribute("ORARIO") ? row2
			.getAttribute("ORARIO").toString() : "";
	String RESPONSABILE = row2.containsAttribute("RESPONSABILE") ? row2
			.getAttribute("RESPONSABILE").toString() : "";
	String EMAIL_PEC = row2.containsAttribute("EMAILPEC") ? row2
			.getAttribute("EMAILPEC").toString() : "";
	String EMAIL_ADL = row2.containsAttribute("EMAILADL") ? row2
			.getAttribute("EMAILADL").toString() : "";
	
  	String htmlStreamTop = StyleUtils.roundTopTable(true);
  	String htmlStreamBottom = StyleUtils.roundBottomTable(true);
%>

<script type="text/javascript"
	src="../../js/fckeditor/editor/js/jquery-1.11.2.js"> </script>
<script type="text/javascript"
	src="../../js/fckeditor/editor/js/jquery.fileDownload.js"> </script>
<script type="text/javascript">
	
	
	//variabili passati dal form di inserimento della stampa parametrica

	var obj = {
			 codTipoDoc : "<%=CODTIPODOC%>",
			 cdnLavoratore : "<%=CDNLAVORATORE%>",
			 ambito  : "<%=AMBITO%>",
			 prgTemplate : "<%=PRGTEMPLATE%>",
			 nomeTemplate : "<%=NOMETEMPLATE%>",
			 dataAcquisizione  : "<%=DATAACQUISIZIONE%>",
			 dataInizio : "<%=DATAINIZIO%>",
			 dataFine : "<%=DATAFINE%>"
	}
</script>
<script type="text/javascript">
var contextPath = "<%=request.getContextPath()%>";
var scheme =      "<%=request.getScheme()     %>";
var header =      '<%=request.getHeader("Host")%>';
</script>


<script type="text/javascript"
	src="../../js/fckeditor/editor/js/customeditor.js"></script>
<script type="text/javascript"
	src="../../js/fckeditor/editor/js/dragdrop.js"></script>
<!-- <script type="text/javascript" 	src="../../fckeditor/editor/js/editor.js"></script> -->
<script>
// per internet explorer 10 e 11 viene usata la gestione degli eventi html (in selectBoxDati*.inc)
function onDblClick(voce) {
  var fck = FCKeditorAPI.GetInstance('EditorDefault');
  if((navigator.userAgent.indexOf("MSIE") != -1 ) || (!!document.documentMode == true ))
    fck.InsertHtml(voce)
}

function controlloAngoliFirma(){
	var oEditorStr = FCKeditorAPI.GetInstance('EditorDefault');
	
	var str2 = oEditorStr.GetXHTML(true);
	var numeroOccorrenzeDI_2 = (str2.match(new RegExp("@DI_2","g"))||[]).length;
	var numeroOccorrenzeDI_1 = (str2.match(new RegExp("@DI_1","g"))||[]).length;
	var numeroOccorrenzeDD_2 = (str2.match(new RegExp("@DD_2","g"))||[]).length;
	var numeroOccorrenzeDD_1 = (str2.match(new RegExp("@DD_1","g"))||[]).length;
// 	alert("trovate "+ numeroOccorrenzeDI_2 +" in str2 di @DI_2");
// 	alert("trovate "+ numeroOccorrenzeDI_1 +" in str2 di @DI_1");
// 	alert("trovate "+ numeroOccorrenzeDD_2 +" in str2 di @DD_2");
// 	alert("trovate "+ numeroOccorrenzeDD_1 +" in str2 di @DD_1");
	if((numeroOccorrenzeDI_2==0&&numeroOccorrenzeDI_1>0)||(numeroOccorrenzeDI_2>0&&numeroOccorrenzeDI_1==0)){
		alert("Attenzione, gli angoli di firma dell'operatore non sono stati inseriti correttamente per cui, in fase di generazione stampa, si procederà con la firma autografa dell'operatore");
	}
	if((numeroOccorrenzeDD_2==0&&numeroOccorrenzeDD_1==0)||(numeroOccorrenzeDD_2>0&&numeroOccorrenzeDD_1==0)||(numeroOccorrenzeDD_2==0&&numeroOccorrenzeDD_1>0)){
		
		alert("Attenzione, gli angoli di firma del lavoratore non sono stati inseriti per cui, in fase di generazione stampa, si procederà con la firma autografa del lavoratore");
	}
	
	//return false;
}
</script>

</head>
<%
	FCKeditor fckEditor = new FCKeditor(request, "EditorDefault");
	fckEditor.setToolbarSet("MyTB");
	fckEditor.setBasePath("/js/fckeditor");
	fckEditor.setWidth("750px");
	fckEditor.setHeight("600px");
	fckEditor.setConfig("language", "it");
	fckEditor.setValue(FILETEMPLATE);
	
	
	String tagFirma = "@DI_2";
	int pos = -1;
	
	String existTagFirma = "";
	if(FLGFIRMAGRAFO.equals("1")){
		pos = FILETEMPLATE.indexOf( tagFirma);
		if(pos !=-1){
			existTagFirma = "OK";
		}
	}
	
	
%>
<body>
	<p class="titolo"><b>Creazione modello stampa</b></p>
	
<!-- 	<form name="frmTemplate" action="AdapterHTTP" method="POST" id="formSpil" onsubmit="controlloAngoliFirma()"> -->
	
			<%
		if(FLGFIRMAGRAFO.equals("1")){					
				 %>
	<form name="frmTemplate" action="AdapterHTTP" method="POST" id="formSpil" onsubmit="controlloAngoliFirma()">
	<%
			}else{
	%>
	<form name="frmTemplate" action="AdapterHTTP" method="POST" id="formSpil">
	<%
			}
	%>
		<%out.print(htmlStreamTop);%>
<!-- 		<p id="p_testo"></p> -->
		<table  class="main" border="0">
			<tr align="center">
				<td id="testoToPDF">
					
				<%
					out.println(fckEditor);
				%>
					
				<!-- questi campi servono per la modifica dell'editor -->
				<input type="hidden" id="page" name="PAGE" value="">
				<input type="hidden" id="module" name="MODULE" value="">
				<% if ("L".equalsIgnoreCase(TIPOEDITOR)) {%>
					<input type="hidden" id="action_name" name="ACTION_NAME" value="">
				<%} %>
				<input type="hidden" id="filetemplate" name="FILETEMPLATE" value="">
				<input type="hidden" name="PRGTEMPLATESTAMPA" value="<%=PRGTEMPLATESTAMPA%>">
				<input type="hidden" name="NUMKLOTEMP" value="<%=NUMKLOTEMP%>">
				<input type="hidden" name="PRGCONFIGPROT" value="<%=PRGCONFIGPROT%>">
				<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
		
				<!-- questi campi sono la trasposizione dei campi presenti nel popup GestisciStatoDoc -->		
				<input type="hidden" name="CDNLAVORATORE" value="<%=CDNLAVORATORE%>">
				<input type="hidden" name="CODTIPODOC" value="<%=CODTIPODOC%>">
				<input type="hidden" name="AMBITO" value="<%=AMBITO%>">		
				<input type="hidden" name="dominio" value="<%=Utils.notNull(dominioTemlate)%>">
				<input type="hidden" name="NOMETEMPLATE" value="<%=NOMETEMPLATE%>">
				<input type="hidden" name="DATAACQUISIZIONE" value="<%=DATAACQUISIZIONE%>">
				<input type="hidden" name="DATAINIZIO" value="<%=DATAINIZIO%>">
				<input type="hidden" name="DATAFINE" value="<%=DATAFINE%>">
				<input type="hidden" name="NOTE" value="<%=NOTE%>">
				<input type="hidden" name="salvaDB" type="checkbox" checked="false" value="SalvaDB"/>
				<input type="hidden" name="apri" type="checkbox" checked="true" value="Apri"/>
				<input type="hidden" name="asAttachment" value=""/>
				<input type="hidden" name="apriFileBlob" value=""/>
				<input type="hidden" name="protocolla" value=""/>
				<input type="hidden" name="annoProt" value="" />
		  		<input type="hidden" name="numProt"  value="" />
		  		<input type="hidden" name="dataProt" value="" />
		  		<input type="hidden" name="oraProt"  value="" />
				<input type="hidden" name="prgDocumento"  value="" />
				<input type="hidden" name="dataOraProt"  value="" />
				<input type="hidden" name="protAutomatica"  value="" />
				<input type="hidden" name="docInOut"  value="" />
				
				<!-- i dati CPI sono per la Servlet  -->
		        <input type="hidden" name="COD_CPI" value="<%=COD_CPI%>">
		        <input type="hidden" name="DESC_CPI" value="<%=DESC_CPI%>">
		        <input type="hidden" name="INDIRIZZO" value="<%=INDIRIZZO%>">
		        <input type="hidden" name="LOCALITA" value="<%=LOCALITA%>">
		        <input type="hidden" name="CAP" value="<%=CAP%>">
		        <input type="hidden" name="CODCOM" value="<%=CODCOM%>">
		        <input type="hidden" name="CODPROVINCIA" value="<%=CODPROVINCIA%>">
		        <input type="hidden" name="COMUNE" value="<%=COMUNE%>">
		        <input type="hidden" name="PROVINCIA" value="<%=PROVINCIA%>">
		        <input type="hidden" name="TELEFONO" value="<%=TELEFONO%>">
		        <input type="hidden" name="FAX" value="<%=FAX%>">
		        <input type="hidden" name="EMAIL" value="<%=EMAIL%>">
		        <input type="hidden" name="ORARIO" value="<%=ORARIO%>">
		        <input type="hidden" name="RESPONSABILE" value="<%=RESPONSABILE%>">
		        <input type="hidden" name="EMAIL_PEC" value="<%=EMAIL_PEC%>">
		        <input type="hidden" name="EMAIL_ADL" value="<%=EMAIL_ADL%>">
				
				<input type="hidden" name="MOSTRATEMPLATE" value="<%=MOSTRATEMPLATE%>">
				<input type="hidden" name="existTagFirma" value="<%=existTagFirma%>">
				
				<input type="hidden" name="CODTIPODOMINIO" value="<%=CODTIPODOMINIO%>">
				
				</td>
				<td>
					<% if (dominioTemlate.equals("DL"))  {%>
						<%@ include file="selectBoxLavoratore.inc" %>
						<%@ include file="selectBoxDatiCPI.inc" %>
					<%}else if(dominioTemlate.equals("DA")){%>
						<%@ include file="selectBoxAzienda.inc" %>
						<%@ include file="selectBoxDatiCPI.inc" %>
					<%} %>	
					<%//gestione firma grafometrica rimandata
					if(FLGFIRMAGRAFO.equals("1")) {%>	
						<%@ include file="selectBoxAngoliFirme.inc" %>
					<%}%>	
					<%@ include file="selectBoxDatiProt.inc" %>	
					<%@ include file="selectBoxListaAllegati.inc" %>	
					<%@ include file="selectBoxFooter.inc" %>	
				</td>
			</tr>
			<tr>
				<%@ include file="editorPulsanti.inc" %>
			</tr>
		</table>
		<%out.print(htmlStreamBottom);%>
	</form>
</body>
</html>