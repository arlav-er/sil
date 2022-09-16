<!-- @author: Stefania Orioli -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                com.engiweb.framework.security.*,
                it.eng.afExt.utils.*, java.math.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.*,
                it.eng.sil.util.*"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
String prgRichiestaAz = serviceRequest.getAttribute("PRGRICHIESTAAZ").toString();
String prgOrig = serviceRequest.getAttribute("PRGORIG").toString();
String prgC1 = serviceRequest.getAttribute("C1").toString();
String prgAzienda = serviceRequest.getAttribute("PRGAZIENDA").toString();
String prgUnita = serviceRequest.getAttribute("PRGUNITA").toString();

String _page = serviceRequest.getAttribute("PAGE").toString();
PageAttribs attributi = new PageAttribs(user, _page);

String p_codCpi = user.getCodRif();

int nroMansioni = 0;

int nroAlternative = 0;
SourceBean content = (SourceBean) serviceResponse.getAttribute("MALTERNATIVEINCROCIO");
Vector altIncr = content.getAttributeAsVector("ROWS.ROW");
nroAlternative = altIncr.size();

int cdnUtente = user.getCodut();
String _cdnFunzione = serviceRequest.getAttribute("CDNFUNZIONE").toString();

//SourceBean contStato = (SourceBean) serviceResponse.getAttribute("MATCHSTATORICHORIG");
SourceBean sbStato = (SourceBean) serviceResponse.getAttribute("MATCHSTATORICHORIG.ROWS.ROW");
String cdnStatoRich = StringUtils.getAttributeStrNotNull(sbStato, "CDNSTATORICH");
String codEvasione = StringUtils.getAttributeStrNotNull(sbStato, "CODEVASIONE");
String codMonoCMCategoria = StringUtils.getAttributeStrNotNull(sbStato, "codMonoCMCategoria");

// Attributi della pagina GestIncrocioPage
PageAttribs attrIncrocio = new PageAttribs(user, "GestIncrocioPage");
boolean gestCopia = attrIncrocio.containsButton("GEST_COPIA");

boolean viewPar = false;
String prgTipoIncrocio = "";
%>
<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <title>Matching Pesato - Impostazione parametri</title>
  <af:linkScript path="../../js/" />
  <script language="Javascript" src="../../js/utili.js" type="text/javascript"></script>
  <script language="Javascript">
    window.top.menu.caricaMenuAzienda(<%=_cdnFunzione%>,<%=prgAzienda%>, <%=prgUnita%>);
    
    
    function match_sub(n)
    {
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;

	  /*
      if (document.form_match.codMonoCMCategoria.value == "E" && n==1) {
	  	alert("Attenzione: bisogna segliere una categoria CM per poter effettuare il matching.")	  	
	  	n = 3;
	  }
      */
      
      var pag = "";
      var ok = false;
      var checkFunz = true;
      switch(n) {
        case 1 :
          pag = "MatchStoricizzaRichPage";
          checkFunz = controllaFunzTL();
          ok = true;
          break;
        case 2 :
          pag = "GestIncrocioPage";
          ok = true;
          break;
        default :
          pag = "";
          ok = false;
          break;
        }
      if(checkFunz && riportaControlloUtente( ok ) ) {      
        document.form_match.PAGE.value = pag; 
        doFormSubmit(document.form_match);  
      } 
    }
  
  /*  
    function selDeselCM() {  	
		if (document.form_match.elements['flgIncMir'].checked) { 
			document.form_match.elements['dataCV'].value = "";			
			document.form_match.elements['dataCV'].disabled = true;		  
		}
		else { 
			document.form_match.elements['dataCV'].disabled = false;					 
		} 	
  	}
 */
    
    function selCM() {  	
    	<%
		if (("MIR").equalsIgnoreCase(codEvasione) || ("MPP").equalsIgnoreCase(codEvasione) || ("MPA").equalsIgnoreCase(codEvasione)) {
		%>
		if (document.form_match.elements['flgIncMirBck'].checked) { 
			document.form_match.elements['dataCV'].value = "";			
			document.form_match.elements['dataCV'].disabled = true;		  
		}
		else { 
			document.form_match.elements['dataCV'].disabled = false;					 
		} 	
		<%
		}
		%>
  	}
    
  </script>
</head>

<body class="gestione" onload="rinfresca();selCM();">
<%@ include file="InfoCorrRichiesta.inc" %>
<h2>Matching Pesato - Impostazione dei Parametri</h2>
<af:form name="form_match" action="AdapterHTTP" method="POST">
<input name="PAGE" type="hidden" value="MatchStoricizzaRichPage"/>
<input name="PRGRICHIESTAAZ" type="hidden" value="<%=prgRichiestaAz%>"/>
<input name="C1" type="hidden" value="<%=prgC1%>"/>
<input name="PRGORIG" type="hidden" value="<%=prgOrig%>"/>
<input name="PRGAZIENDA" type="hidden" value="<%=prgAzienda%>"/>
<input name="PRGUNITA" type="hidden" value="<%=prgUnita%>"/>
<input name="CDNFUNZIONE" type="hidden" value="<%=_cdnFunzione%>"/>

<input name="CERCA" type="hidden" value="cerca"/>

<input type="hidden" name="P_CODCPI" value="<%=p_codCpi%>"/>
<input type="hidden" name="P_CDNUTENTE" value="<%=cdnUtente%>"/>
<input type="hidden" name="EM" value="2"/>
<input type="hidden" name="db" value="3"/>
<%
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<%out.print(htmlStreamTop);%>
<table class="main">
<%@ include file="match_par_comuni.inc" %>
<!--tr><td colspan="2">&nbsp;</td></tr-->
<tr>
  <td colspan="2"><div class="sezione2">Parametri Specifici</div></td>
</tr>
<%
// INIT-PARTE-TEMP
if (Sottosistema.CM.isOff()) {	
// END-PARTE-TEMP
%>

<%	
// INIT-PARTE-TEMP
} else {
// END-PARTE-TEMP
%>

<%
if (("MIR").equalsIgnoreCase(codEvasione) || ("MPP").equalsIgnoreCase(codEvasione) || ("MPA").equalsIgnoreCase(codEvasione)) {
%>
	<tr class="note">
		<td class="etichetta"><b>Incrocio Mirato</b></td>
		<td class="campo">		
			<input type="CHECKBOX" name="flgIncMirBck" value="1" checked="checked" disabled="disabled">
			<input name="flgIncMir" type="hidden" value="1"/>				
		</td>	
	</tr>
	
	<tr class="note">
		<td class="etichetta"><b>Categoria CM</b></td>
		<td class="campo">		
			<af:comboBox 
	          name="codMonoCMCategoria"
	          classNameBase="input" >
	          <option value="E">Entrambi</option>
	          <option value="D" <% if ( "D".equalsIgnoreCase(codMonoCMCategoria) ) { %>SELECTED<% } %> >Disabile</option>
	          <option value="A" <% if ( "A".equalsIgnoreCase(codMonoCMCategoria) ) { %>SELECTED<% } %> >Categoria protetta ex. Art. 18</option>
	        </af:comboBox>		
	        
	        <script language="javascript">
	        <%
	        if (("D").equalsIgnoreCase(codMonoCMCategoria) || ("A").equalsIgnoreCase(codMonoCMCategoria)) {
			%>
				 document.form_match.codMonoCMCategoria.disabled = true;
			<%
			}
			%>
	        </script>
	        
	        <%
	        if (("D").equalsIgnoreCase(codMonoCMCategoria) || ("A").equalsIgnoreCase(codMonoCMCategoria)) {
			%>
				 <input name="codMonoCMCategoria" type="hidden" value="<%=codMonoCMCategoria%>"/>						 
			<%
			}
			%>
		</td>	
	</tr>
	
	<tr><td colspan="2" class="sottolineato">&nbsp;</td>
<%
}		
%>
<%
// INIT-PARTE-TEMP
}
// END-PARTE-TEMP
%>
<!-- ETA -->
<tr class="note" style="display: none">
  <td class="etichetta"><b>Et&agrave;</b></td>
  <td class="campo">
    <table>
    <tr><td>Fascia esatta</td><td>100%</td></tr>
    <tr><td>Fascia &plusmn;5</td>
        <td>
        <select name="numPFasciaEtaPrima">
          <option value="90">90%</option>
          <option value="80" SELECTED>80%</option>
          <option value="70">70%</option>
          <option value="60">60%</option>
          <option value="50">50%</option>
          <option value="40">40%</option>
          <option value="30">30%</option>
          <option value="20">20%</option>
          <option value="10">10%</option>          
          <option value="0">Non considerare</option>
        </select>
        </td>
    </tr>
    <tr><td>Fascia &plusmn;10</td>
        <td>
        <select name="numPFasciaEtaSeconda">
          <option value="90">90%</option>
          <option value="80">80%</option>
          <option value="70">70%</option>
          <option value="60" selected>60%</option>
          <option value="50">50%</option>
          <option value="40">40%</option>
          <option value="30">30%</option>
          <option value="20">20%</option>
          <option value="10">10%</option>          
          <option value="0">Non considerare</option>
        </select>
        </td>
    </tr>
    </table>
  </td>
</tr>
<tr style="display: none"><td colspan="2" class="sottolineato">&nbsp;</td>
<!-- TITOLO DI STUDIO -->
<tr class="note">
      <td class="etichetta"><b>Titolo di Studio</b></td>
      <td class="campo">
        <table>
        <tr><td>Corrispondenza esatta</td><td>100%</td></tr>
        <tr><td>nello stesso gruppo</td>
            <td>
            <select name="numPStudioGruppo">
              <option value="90" selected>90%</option>
              <option value="80">80%</option>
              <option value="70">70%</option>
              <option value="60">60%</option>
              <option value="50">50%</option>
              <option value="40">40%</option>
              <option value="30">30%</option>
              <option value="20">20%</option>
              <option value="10">10%</option>
              <option value="0">Non considerare</option>
            </select>
            </td>
        </tr>
        <tr><td>appartenente a gruppi simili</td>
            <td>
            <select name="numPStudioAlias">
              <option value="90">90%</option>
              <option value="80" selected>80%</option>
              <option value="70">70%</option>
              <option value="60">60%</option>
              <option value="50">50%</option>
              <option value="40">40%</option>
              <option value="30">30%</option>
              <option value="20">20%</option>
              <option value="10">10%</option>            
              <option value="0">Non considerare</option>
            </select>
            </td>
        </tr>
        </table>
  </td>
</tr>
<tr><td colspan="2" class="sottolineato">&nbsp;</td>
<!-- MANSIONE -->
<tr class="note">
      <td class="etichetta"><b>Mansione</b></td>
      <td class="campo">
        <table>
        <tr><td>Corrispondenza esatta</td><td>100%</td></tr>
        <tr><td>nello stesso gruppo</td>
            <td>
            <select name="numPMansioneGruppo">
              <option value="90" selected>90%</option>
              <option value="80">80%</option>
              <option value="70">70%</option>
              <option value="60">60%</option>
              <option value="50">50%</option>
              <option value="40">40%</option>
              <option value="30">30%</option>
              <option value="20">20%</option>
              <option value="10">10%</option>              
              <option value="0">Non considerare</option>
            </select>
            </td>
        </tr>
        <tr><td>appartenente a gruppi simili</td>
            <td>
            <select name="numPMansioneAlias">
              <option value="90">90%</option>
              <option value="80" selected>80%</option>
              <option value="70">70%</option>
              <option value="60">60%</option>
              <option value="50">50%</option>
              <option value="40">40%</option>
              <option value="30">30%</option>
              <option value="20">20%</option>
              <option value="10">10%</option>              
              <option value="0">Non considerare</option>
            </select>
            </td>
        </tr>
        </table>
  </td>
</tr>
<tr><td colspan="2" class="sottolineato">&nbsp;</td>
<!-- ESPERIENZA NELLA MANSIONE -->
<tr class="note">
  <td class="etichetta"><b>Esperienza nella mansione</b></td>
  <td class="campo">
    <table>
    <tr><td>con esperienza</td><td>100%</td></tr>
    <tr><td>nessuna esperienza</td>
        <td>
        <select name="numPNoEsperienza">
          <option value="90">90%</option>
          <option value="80">80%</option>
          <option value="70" selected>70%</option>
          <option value="60">60%</option>
          <option value="50">50%</option>
          <option value="40">40%</option>
          <option value="30">30%</option>
          <option value="20">20%</option>
          <option value="10">10%</option>          
          <option value="0">Non considerare</option>
        </select>
        </td>
    </tr>
    <tr><td>esperienza in mansione simile</td>
        <td>
        <select name="numPEsperienzaAlias">
          <option value="90">90%</option>
          <option value="80">80%</option>
          <option value="70" selected>70%</option>
          <option value="60">60%</option>
          <option value="50">50%</option>
          <option value="40">40%</option>
          <option value="30">30%</option>
          <option value="20">20%</option>
          <option value="10">10%</option>          
          <option value="0">Non considerare</option>
        </select>
        </td>
    </tr>
    </table>
  </td>
</tr>
<tr><td colspan="2" class="sottolineato">&nbsp;</td>
<!-- CONOSCENZE INFORMATICHE -->
<tr class="note">
  <td class="etichetta"><b>Conoscenze Informatiche</b></td>
  <td class="campo">
    <table>
    <tr><td>Corrispondenza esatta e livello maggiore o uguale</td><td>100%</td></tr>
    <tr><td>corrispondenza esatta e livello inferiore</td>
        <td>
        <select name="numPInfoMin">
          <option value="90">90%</option>
          <option value="80" selected>80%</option>
          <option value="70">70%</option>
          <option value="60">60%</option>
          <option value="50">50%</option>
          <option value="40">40%</option>
          <option value="30">30%</option>
          <option value="20">20%</option>
          <option value="10">10%</option>          
          <option value="0">Non considerare</option>
        </select>
        </td>
    </tr>
    <tr><td>nello stesso gruppo e livello maggiore o uguale</td>
        <td>
        <select name="numPInfogruppo">
          <option value="90">90%</option>
          <option value="80">80%</option>
          <option value="70">70%</option>
          <option value="60" selected>60%</option>
          <option value="50">50%</option>
          <option value="40">40%</option>
          <option value="30">30%</option>
          <option value="20">20%</option>
          <option value="10">10%</option>          
          <option value="0">Non considerare</option>
        </select>
        </td>
    </tr>
    <tr><td>nello stesso gruppo e livello inferiore</td>
        <td>
        <select name="numPInfogruppoMin">
          <option value="90">90%</option>
          <option value="80">80%</option>
          <option value="70">70%</option>
          <option value="60">60%</option>
          <option value="50">50%</option>
          <option value="40" selected>40%</option>
          <option value="30">30%</option>
          <option value="20">20%</option>
          <option value="10">10%</option>          
          <option value="0">Non considerare</option>
        </select>
        </td>
    </tr>
    </table>
  </td>
</tr>
<tr><td colspan="2" class="sottolineato">&nbsp;</td>
<!-- CONOSCENZE LINGUISTICHE -->
<tr class="note">
  <td class="etichetta"><b>Conoscenze Linguistiche</b></td>
  <td class="campo">
    <table>
    <tr><td>corrispondenza esatta</td><td>100%</td></tr>
    <tr><td>corrispondenza esatta e livello inferiore</td>
        <td>
        <select name="numPLinguaInf">
          <option value="90">90%</option>
          <option value="80">80%</option>
          <option value="70">70%</option>
          <option value="60">60%</option>
          <option value="50" selected>50%</option>
          <option value="40">40%</option>
          <option value="30">30%</option>
          <option value="20">20%</option>
          <option value="10">10%</option>          
          <option value="0">Non considerare</option>
        </select>
        </td>
    </tr>
    </table>
  </td>
</tr>
<tr><td colspan="2" class="sottolineato">&nbsp;</td>
<tr class="note">
  <td class="etichetta"><b>Soglia di Vicinanza</b></td>
  <td class="campo">
    <select name="numPSogliaRichiesta">
      <option value="100">100%</option>
      <option value="90">90%</option>
      <option value="80" selected>80%</option>
      <option value="70">70%</option>
      <option value="60">60%</option>
      <option value="50">50%</option>
      <option value="40">40%</option>
      <option value="30">30%</option>
      <option value="20">20%</option>
      <option value="10">10%</option>      
    </select>
  </td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>
<tr>
  <td colspan="2" align="center">  
  <%if(!cdnStatoRich.equals("4") && !cdnStatoRich.equals("5")) {%>
  <input class="pulsanti" type="button" name="sub" value="Esegui Matching" onClick="match_sub(1)" />
  &nbsp;&nbsp;
  <%}%>
  <input class="pulsanti" type="button" name="back" value="Chiudi"  onClick="match_sub(2)" />
  </td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>
</table>
<%out.print(htmlStreamBottom);%>

</af:form>
  
</body>
</html>
