<%@ page
	contentType="text/html;charset=utf-8"
	
	import="com.engiweb.framework.base.*,
			it.eng.sil.security.User,
			it.eng.sil.security.*,
			it.eng.afExt.utils.*,
			it.eng.sil.util.*,
			java.math.*,
			java.util.*"
	
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%
	
	String  titolo = "Budget dei centri per l'impiego";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

	// Lettura parametri dalla REQUEST
	//int     cdnfunzione      = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	//int cdnfunzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));

	String  cdnfunzioneStr   = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione");
	String  cdnLavoratore    = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");
	String  prgAzienda       = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgAzienda");
	String  prgUnita         = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgUnita");

	

	// CONTROLLO ACCESSO ALLA PAGINA
	/* Commentato per test
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	boolean canView = filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}
	*/

	// CONTROLLO PERMESSI SULLA PAGINA
	PageAttribs attributi = new PageAttribs(user, _page);

	boolean canModify = attributi.containsButton("aggiorna");
	boolean canDelete = attributi.containsButton("rimuovi");
	
	Object cdnUtCorrente    = sessionContainer.getAttribute("_CDUT_");
	String cdnUtCorrenteStr = StringUtils.getStringValueNotNull(cdnUtCorrente);
	
	
	// Recupero URL della LISTA da cui sono venuto al dettaglio (se esiste)
	String token = "_TOKEN_" + "ListaPage";
	String goBackInf = "Torna alla pagina di ricerca";
	String anno=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "AnnoSel");
	String codCpi=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "codiceCPISel");
	
	String goBackUrl = (String) sessionContainer.getAttribute(token.toUpperCase());
	if (StringUtils.isEmpty(goBackUrl)) {
		goBackInf = "Torna alla ricerca";
		goBackUrl = "PAGE=CercaBudgetPerCpiPage&cdnfunzione=" + cdnfunzioneStr;
		goBackUrl = goBackUrl + "&AnnoSel=" + anno;
		goBackUrl = goBackUrl + "&codiceCPISel=" + codCpi;
	}

	// Sola lettura: viene usato per tutti i campi di input
	String readonly = String.valueOf( ! canModify );
	
	// Stringhe con HTML per layout tabelle
	String htmlStreamTop    = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
	
	
	
	int cdnUt = user.getCodut();
	int cdnTipoGruppo = user.getCdnTipoGruppo();
	String strCodCpi;
	strCodCpi =  user.getCodRif();
	//Solamente per testare ricordare di eliminare la riga
	canModify=true;
	
	
   //Gestire qui la risposta del modulo 	
   String importoTot="0";
   String impegnato="0";
   String speso="0";
   String residuo="0";
   
   if (codCpi==null || codCpi.equals("")){
	   //CODCPI
	   codCpi=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "CODCPI");
   }
   
   String strResiduo=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "residuoTot");
   String strImpegnatotot=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "impegnatoTot");
   String strTotBudget=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "TotBudget");
   
   SourceBean cont = (SourceBean) serviceResponse.getAttribute("M_CAMPITOTALIBUDGET");
   SourceBean row = (SourceBean) cont.getAttribute("ROWS.ROW");
   if (row.getAttribute("IMPEGNATO")!=null){
   	impegnato=row.getAttribute("IMPEGNATO").toString();
   }
   if (row.getAttribute("RESIDUO")!=null){
	   residuo=row.getAttribute("RESIDUO").toString();
   }
   if (row.getAttribute("SPESO")!=null){
	   speso=row.getAttribute("SPESO").toString();
   }
   if (row.getAttribute("TOTBUDGET")!=null){
	   importoTot=row.getAttribute("TOTBUDGET").toString();
   }  
	
%>
<html>
<head>
<title><%= titolo %></title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

<af:linkScript path="../../js/"/>
<%@ include file="../global/fieldChanged.inc" %>



<script language="Javascript">

	/* Funzione per tornare alla pagina precedente */
	function goBack() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
		
		goTo("<%= goBackUrl %>");
	}
	
	
	/* Funzione chiamata al caricamento della pagina */
	function onLoad() {
		rinfresca();
		// altri funzioni da richiamare sulla onLoad...
	}

<%
	// Genera il Javascript che si occuperà di inserire i links nel footer
	attributi.showHyperLinks(out, requestContainer, responseContainer, "cdnLavoratore="+cdnLavoratore);
%>

</script>
</head>

<body class="gestione" onload="onLoad()">



<p class="titolo"><%= titolo %></p>
<%String attr   = null;
  String valore = null;
  String txtOut = "";
%>
     <%attr= "AnnoSel";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "Anno selezionato <strong>"+ valore +"</strong>; ";
       }%>
     <%attr= "codiceCPIDescr";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "CPI <strong>"+ valore +"</strong>; ";
       }%>
    
    
<p align="center">
<%if(txtOut.length() > 0)
  { txtOut = "Filtri di ricerca:<br/> " + txtOut;%>
    <table cellpadding="2" cellspacing="10" border="0" width="100%">
     <tr><td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
      <%out.print(txtOut);%>
     </td></tr>
    </table>
<%}%>

<font color="red">
<af:showErrors />
</font>
<font color="green">
<af:showMessages />
</font>



	<af:form name="form" action="AdapterHTTP" method="POST">

	<input type="hidden" name="PAGE" value="CercaBudgetPerCpiPage"/>
	<input type="hidden" name="cdnfunzione" value="<%= cdnfunzioneStr %>"/>
	<input type="hidden" name="importoTot" value="<%= importoTot %>"/>
	<input type="hidden" name="impegnato" value="<%= impegnato %>"/>
	<input type="hidden" name="speso" value="<%= speso %>"/>
	<input type="hidden" name="residuo" value="<%= residuo %>"/>
	<input type="hidden" name="cpi" value="<%= codCpi %>"/>
	<input type="hidden" name="anno" value="<%= anno %>"/>
	
	
	
	<%= htmlStreamTop %>
	<table class="main">
	

	<%--
		Campi  della Totali Budget non presenti
	--%>
	<%-- ***************************************************************************** --%>
	<p align="center">
	<tr>
			
			<td class="campo" id="tabellaElencoTotali">
					<af:list moduleName="M_ListaTotaliBudget"  />
			</td>
	</tr>
	<tr>
		<td class="campo">&nbsp;</td>
	</tr>

	</p>
	</table>
	
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
<TH class="lista">&nbsp;</TH>
<TH class="lista">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</TH>
<TH class="lista">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</TH>
<TH class="lista">Importo TOT</TH>
<TH class="lista">Impegnato</TH>
<TH class="lista">Speso</TH>
<TH class="lista">Residuo</TH>
</TR>
<TR class="lista">
<TD class="lista_dispari" colspan="3" style="width:46%; text-align:right; font-weight: bold;"> TOTALI</TD>
<TD class="lista_dispari" style="width:17%; text-align:right; font-weight: bold;"><%=importoTot %></TD>
<TD class="lista_dispari" style="width:15%; text-align:right; font-weight: bold;"><%=impegnato %></TD>
<TD class="lista_dispari" style="font-weight: bold; text-align:right;"><%=speso %></TD>
<TD class="lista_dispari" style="font-weight: bold; text-align:right;"><%=residuo %></TD>
</TR>
</TABLE>
</td>
</tr>	
</table>
	<table align="center">
	

	<% if (StringUtils.isFilled(goBackUrl)) { %>
		<tr>
			<td colspan="2">
				<input type="button" class="pulsante" name="back" value="<%= goBackInf %>"
						onclick="goBack()" />
			</td>
		</tr>
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
	<% } %>

	</table>
	
	<%= htmlStreamBottom %>
	
</af:form>
</body>
</html>	
