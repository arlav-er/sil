<%@ page contentType="text/html;charset=utf-8"%>
 
<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*,
                it.eng.sil.module.voucher.*,
                it.eng.sil.util.*,
                it.eng.sil.security.*,
                java.math.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<% 

  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  ProfileDataFilter filter = new ProfileDataFilter(user, _page);
  //NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, _page);
  boolean canModify = false;
  
  if (!filter.canView()) {
  	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
  	return;
  }
  
  // NOTE: Attributi della pagina (pulsanti e link)
  canModify = attributi.containsButton("SALVA");
  
  String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
  String prgVoucher = StringUtils.getAttributeStrNotNull(serviceRequest,"PRGVOUCHER");
  String titlePagina = "Obiettivi e criticità";
  Testata operatoreInfo = null;
  String strNoteObiettivi = "";
  String strNoteCriticità = "";
  String cdnUtIns = "";
  String dtmIns = "";
  String cdnUtMod = "";
  String dtmMod = "";
  BigDecimal numklovoucher = null;
  String sPrgVoucher = "";
  String sNumkloVoucher = "";
  String codStatoVch = "";
  String codMansione1= "",  descMansione1= "", desTipoMansione1="";
  String codMansione2= "",  descMansione2= "", desTipoMansione2="";
  String cdnLavoratore = "";
  String descAzioneTda = "";
  boolean canViewMansioni = false;
  
  sPrgVoucher = StringUtils.getAttributeStrNotNull(serviceRequest,"PRGVOUCHER");
  
  SourceBean rowObiettivi = (SourceBean)serviceResponse.getAttribute("M_ListaObiettiviTDA.ROWS.ROW");
  if (rowObiettivi != null) {
	strNoteObiettivi = SourceBeanUtils.getAttrStrNotNull(rowObiettivi, "noteObiettivo");
	strNoteCriticità = SourceBeanUtils.getAttrStrNotNull(rowObiettivi, "noteCriticita");
	codStatoVch = SourceBeanUtils.getAttrStrNotNull(rowObiettivi, "codstatovoucher");
	cdnUtIns = rowObiettivi.getAttribute("cdnutins").toString();
	dtmIns = rowObiettivi.getAttribute("dtmins").toString();
	cdnUtMod = rowObiettivi.getAttribute("cdnutmod").toString();
	dtmMod = rowObiettivi.getAttribute("dtmmod").toString();
	operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
	numklovoucher = (BigDecimal) rowObiettivi.getAttribute("numklovoucher");
	numklovoucher = numklovoucher.add(new BigDecimal(1));
	sNumkloVoucher = numklovoucher.toString();
	cdnLavoratore = SourceBeanUtils.getAttrStrNotNull(rowObiettivi, "cdnlavoratore");
	codMansione1 =  SourceBeanUtils.getAttrStrNotNull(rowObiettivi, "CODMANSIONE1");
	desTipoMansione1= SourceBeanUtils.getAttrStrNotNull(rowObiettivi, "desTipoMansione1");
	descMansione1= SourceBeanUtils.getAttrStrNotNull(rowObiettivi, "DESC_MANSIONE1");
	if(StringUtils.isFilledNoBlank(codMansione1)) {
		descMansione1 = codMansione1 + " - " + descMansione1;
	}
	codMansione2 =  SourceBeanUtils.getAttrStrNotNull(rowObiettivi, "CODMANSIONE2");
	desTipoMansione2= SourceBeanUtils.getAttrStrNotNull(rowObiettivi, "desTipoMansione2");
	descMansione2= SourceBeanUtils.getAttrStrNotNull(rowObiettivi, "DESC_MANSIONE2");
	if(StringUtils.isFilledNoBlank(codMansione2)) {
		descMansione2 = codMansione2 + " - " + descMansione2;
	}
	descAzioneTda = SourceBeanUtils.getAttrStrNotNull(rowObiettivi, "descAzioneTda");
	if (!descAzioneTda.equals("") && descAzioneTda.length() > 3 && descAzioneTda.substring(0, 3).equalsIgnoreCase(Voucher.TIPO_TDA_2Ce)) {
		canViewMansioni = true;	
	}
  }
  
  if (!codStatoVch.equalsIgnoreCase(StatoEnum.ATTIVATO.getCodice())) {
	  canModify = false;  
  }
  
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>

<html>
<head>
  <title>Obiettivi e criticità</title>
  <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  <%@ include file="../global/fieldChanged.inc" %>
  <%@ include file="../presel/Mansioni_CommonScripts.inc" %>
  
  
  <af:linkScript path="../../js/"/>

  <script type="text/Javascript">

  	 function tornaLista() {
		if (isInSubmit()) return;
		<%
		// Recupero l'eventuale URL generato dalla LISTA precedente
		String token = "_TOKEN_" + "ListaTDAPage";
		String goBackListUrl = (String) sessionContainer.getAttribute(token.toUpperCase());	
		%>
		setWindowLocation("AdapterHTTP?<%= StringUtils.formatValue4Javascript(goBackListUrl) %>");
	  }
  
  </script>
 
</head>
<body  class="gestione" onload="rinfresca();">
<p>
	<font color="green">
		<af:showMessages prefix="M_AggiornaObiettiviTDA"/>
  	</font>
  	<font color="red"><af:showErrors /></font>
</p>

<%
Linguette _linguetta = new Linguette(user, new Integer(_funzione).intValue() , _page, new BigDecimal(prgVoucher));
_linguetta.setCodiceItem("prgVoucher");
_linguetta.show(out);
%>

<af:form method="POST" action="AdapterHTTP" name="Frm1">
	<p class="titolo"><%=titlePagina%></p>
	<% out.print(htmlStreamTop); %>
	<br>
	<table class="main">
		<tr>
			<td class="etichetta" nowrap>Obiettivi concordati alla fine del percorso&nbsp;</td>
			<td class="campo">
				<af:textArea name="obiettivi" cols="120" maxlength="2000" onKeyUp="fieldChanged();"
						rows="15" title="Obiettivi" readonly="<%=String.valueOf(!canModify)%>" classNameBase="input"
						value="<%=strNoteObiettivi%>"/>
            </td>
		</tr>
		<%if (canViewMansioni) {%>
			<tr><td colspan="2">&nbsp;</td><tr>
			<tr>
			      <td class="etichetta">Indicare la o le qualifiche obiettivo</td>
			      <td>&nbsp;</td>
			</tr>
			<tr>
			      <td class="etichetta">Ricerca limitata alle sole mansioni di uso frequente</td>
			      <td class="campo">
			          <input type="checkbox" name="flgFrequente" value="" <%if (!canModify){%>disabled="true" <%}%>/>
			      </td>
			  </tr>
			 <tr>
			    <td class="etichetta">Codice mansione 1</td>
			    <td class="campo">
			      <af:textBox 
			      	readonly="<%= String.valueOf(!canModify) %>"
			        name="CODMANSIONE" 
			        size="7" 
			        maxlength="7" 
			        value="<%= codMansione1.toString() %>" 
			      />
			      <af:textBox 
			        type="hidden" 
			        name="codMansioneHid" 
			        value="<%= codMansione1.toString() %>" 
			      />
				  <%if (canModify) {%>
			          <A id="binocolo_link_1" href="javascript:selectMansione_onClick(Frm1.CODMANSIONE, Frm1.codMansioneHid, Frm1.DESCMANSIONE,  Frm1.strTipoMansione);"><img src="../../img/binocolo.gif" alt="Cerca"></A>&nbsp;&nbsp;
			          <A id="avanzata_link_1" href="javascript:ricercaAvanzataMansioni();">
			              Ricerca avanzata
			          </A>
			     <%}%>
			    </td>
			</tr>           
			<tr>
			    <td class="etichetta">Tipo</td>
			    <td class="campo">
			      <af:textBox type="hidden" name="CODTIPOMANSIONE" value="" />
			      <af:textBox classNameBase="input" name="strTipoMansione" value="<%=desTipoMansione1%>" readonly="true" size="48" />
			    </td>
			</tr>
			<tr>
			  <td class="etichetta">Descrizione</td>
			  <td class="campo">
			      <af:textArea cols="30" 
			                   rows="2" 
			                   name="DESCMANSIONE" 
			                   classNameBase="textarea"
			                   readonly="true" 
			                   maxlength="100"
			                   value="<%= descMansione1 %>" />
			  	</td>
			 </tr>
			
			<tr><td colspan="2">&nbsp;</td><tr>
			
			<tr>
			      <td class="etichetta">Ricerca limitata alle sole mansioni di uso frequente</td>
			      <td class="campo">
			          <input type="checkbox" name="flgFrequente2" value="" <%if (!canModify){%>disabled="true" <%}%>/>
			      </td>
			  </tr>
			 <tr>
			    <td class="etichetta">Codice mansione 2</td>
			    <td class="campo">
			      <af:textBox 
			      	readonly="<%= String.valueOf(!canModify) %>"
			        name="CODMANSIONE2" 
			        size="7" 
			        maxlength="7" 
			        value="<%= codMansione2.toString() %>" 
			      />
			      <af:textBox 
			        type="hidden" 
			        name="codMansioneHid2" 
			        value="<%= codMansione2.toString() %>" 
			      />
				  <%if (canModify) {%>
			          <A id="binocolo_link_2" href="javascript:selectMansioneGenerica_onClick('2', Frm1.flgFrequente2, Frm1.CODMANSIONE2, Frm1.codMansioneHid2, Frm1.DESCMANSIONE2,  Frm1.strTipoMansione2);"><img src="../../img/binocolo.gif" alt="Cerca"></A>&nbsp;&nbsp;
			          <A id="avanzata_link_2" href="javascript:ricercaAvanzataMansioniGenerica('2');">
			              Ricerca avanzata
			          </A>
			     <%}%>
			    </td>
			</tr>           
			<tr>
			    <td class="etichetta">Tipo</td>
			    <td class="campo">
			      <af:textBox type="hidden" name="CODTIPOMANSIONE2" value="" />
			      <af:textBox classNameBase="input" name="strTipoMansione2" value="<%=desTipoMansione2%>" readonly="true" size="48" />
			    </td>
			</tr>
			<tr>
			  <td class="etichetta">Descrizione</td>
			  <td class="campo">
			      <af:textArea cols="30" 
			                   rows="2" 
			                   name="DESCMANSIONE2" 
			                   classNameBase="textarea"
			                   readonly="true" 
			                   maxlength="100"
			                   value="<%= descMansione2 %>" />
			  	</td>
			 </tr>
		<%}%>
		<tr><td colspan="2">&nbsp;</td><tr>
		<tr>
			<td class="etichetta" nowrap>Criticità&nbsp;</td>
			<td class="campo">
				<af:textArea name="criticita" cols="120" maxlength="500" onKeyUp="fieldChanged();"
						rows="10" title="Criticità" readonly="<%=String.valueOf(!canModify)%>" classNameBase="input"
						value="<%=strNoteCriticità%>"/>
            </td>
		</tr>
	</table>
	
	<br>
    <table>
    <tr>
    <td align="center">
    <%
    if (canModify) {%>
    	<input type="submit" class="pulsanti" name="AGGIORNAOBBIETTIVI" value="Aggiorna">
    <%}%>
    <!-- <input class="pulsante" type = "button" name="torna" value="Torna alla lista" onClick="tornaLista();"/> -->
    </td>
    </tr>
    </table>
    
	<%if (operatoreInfo != null) {%>
  		<br>
		<center>
		<% 
		operatoreInfo.showHTML(out);
		%>
		</center>
	<%}
	out.print(htmlStreamBottom); 
	%>
	<input type="hidden" name="PAGE" value="TDAObiettiviPage">
	<input type="hidden" name="cdnFunzione" value="<%=Utils.notNull(_funzione)%>">
	<input type="hidden" name="PRGVOUCHER" value="<%=sPrgVoucher%>">
	<input type="hidden" name="NUMKLOVOUCHER" value="<%=sNumkloVoucher%>">
</af:form>


</body>
</html>