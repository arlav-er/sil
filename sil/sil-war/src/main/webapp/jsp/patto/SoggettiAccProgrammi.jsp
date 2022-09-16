<%@page import="it.eng.afExt.utils.StringUtils"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ taglib uri="patto" prefix="pt" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.module.movimenti.constant.Properties,
                  it.eng.sil.security.User,
                  it.eng.sil.security.ProfileDataFilter,                   
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  java.lang.*,
                  java.text.*,it.eng.sil.security.PageAttribs"%>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String moduleNameProgrammi = null;
	String prgPattoLavoratore = null;
	String regione = "";
	String cdnLavoratore = (String )serviceRequest.getAttribute("CDNLAVORATORE");
	SourceBean ultimoPatto = (SourceBean)serviceResponse.getAttribute("M_UltimoPattoAperto.ROWS.ROW");
	if(ultimoPatto!=null && ultimoPatto.containsAttribute("PRGPATTOLAVORATORE")){
		prgPattoLavoratore =  Utils.notNull(ultimoPatto.getAttribute("PRGPATTOLAVORATORE"));
	}
	moduleNameProgrammi = "M_ProgrammiPattoLavNoSoggetto";
	
	String _current_page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _current_page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
 	PageAttribs pageAtts = new PageAttribs(user, _current_page);
 	boolean canAssociaEnte = false;
 	canAssociaEnte = pageAtts.containsButton("ASSOCIASOGGACC");
	boolean canModify = false;
	boolean canView=filter.canViewLavoratore();
	if (!canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}
	
	SourceBean rowRegione = (SourceBean) serviceResponse.getAttribute("M_GetCodRegione.ROWS.ROW");
	if (rowRegione != null){
		regione = StringUtils.getAttributeStrNotNull(rowRegione, "codregione");	
	}
	
    String _funzione = (String)serviceRequest.getAttribute("CDNFUNZIONE");
    String htmlStreamTop = StyleUtils.roundTopTable(canModify);
    String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
    %>
<html>
<head>
<title>Soggetti Accreditati Programmi</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/"/>

<SCRIPT language="JavaScript">

function aggiornaSoggettoAcc(){
    document.Frm1.ragioneSocialeSoggAcc.value = opened.dati.ragioneSociale;
    document.Frm1.codFiscaleSoggAcc.value = opened.dati.codiceFiscaleEnte;
    document.Frm1.indirizzoSoggAcc.value = opened.dati.strIndirizzoEnte;
    document.Frm1.strTelSoggAcc.value = opened.dati.strTelEnte;
    document.Frm1.comuneSoggAcc.value = opened.dati.comuneEnte;
    document.Frm1.codSedeSoggAcc.value = opened.dati.codSoggAcc;
    
    opened.close();
    var imgV = document.getElementById("tendinaSoggettoAcc");
    cambiaLavMC("soggAccSez","inline");
    imgV.src=imgAperta;
    cambiaLavMC("associaEnte","none");
	cambiaLavMC("dissociaEnte","inline");
}

function disassociaSoggettoAcc(){ 
    document.Frm1.ragioneSocialeSoggAcc.value = null;
    document.Frm1.codFiscaleSoggAcc.value = null;
    document.Frm1.indirizzoSoggAcc.value = null;
    document.Frm1.strTelSoggAcc.value = null;
    document.Frm1.comuneSoggAcc.value = null ;
    document.Frm1.codSedeSoggAcc.value = null;
    document.Frm1.strNoteEnte.value = null;
   
    var imgV = document.getElementById("tendinaSoggettoAcc");
    cambiaLavMC("soggAccSez","none");
    imgV.src=imgChiusa;
    cambiaLavMC("associaEnte","inline");
	cambiaLavMC("dissociaEnte","none");
}

</SCRIPT>
<%@ include file="PattoSezioniATendina.inc" %>
<%@ include file="PattoRicercaSoggetto.inc" %>
</head>
<body class="gestione" onload="rinfresca()">
<%
InfCorrentiLav _testata= new InfCorrentiLav(RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
Linguette _linguetta = new Linguette(user, Integer.parseInt(_funzione), _current_page, new BigDecimal(cdnLavoratore));
_testata.setPaginaLista("ListaPattoLavPage");
_testata.show(out);
_linguetta.show(out);
%>
<font color="red">
   <af:showErrors/>
</font>
<font color="green">
	<af:showMessages prefix="M_InserisciSoggettoProgrammaPattoLav"/>
	<af:showMessages prefix="M_CancellaSoggettoProgrammaPattoLav"/>
</font>
<br>
<center>
<%out.print(htmlStreamTop);%>
<table class="main">
<tr><td colspan="2">
	<af:list moduleName="M_ListaSoggettoAccProgrammi" canDelete="<%= canAssociaEnte ? \"1\" : \"0\" %>"/>
</td></tr>
</table>

<%out.print(htmlStreamBottom);%>
</center>

<% if (canAssociaEnte) {%>
<af:form method="post" name="Frm1" action="AdapterHTTP">
	<%out.print(htmlStreamTop);%>
	<table  class="main">
	<tr>
    	<td class="etichetta">Programma</td> 
       	<td class="campo">
		<af:comboBox name="CODPROGRAMMA" classNameBase="input" addBlank="true" title="Programma" 
             		moduleName="<%=moduleNameProgrammi%>" selectedValue="" required="true"/>
           </td>
       	</tr>
       	
       	<tr class="note">
	      <td colspan="2">
	      <div class="sezione2">
	        <img id='tendinaSoggettoAcc' alt="Chiudi" src="../../img/aperto.gif" onclick="cambiaTendina(this,'soggAccSez',document.Frm1.codFiscaleSoggAcc);"/>&nbsp;&nbsp;&nbsp;Soggetto Accreditato
	      &nbsp;&nbsp;
		      <span style="float:right;">
		    	  <div id="associaEnte" style="display: inline;"><a href="#" onClick="javascript:apriSelezionaSoggetto('aggiornaSoggettoAcc','<%=cdnLavoratore%>');"><img src="../../img/patto_ass.gif" alt="Associa"></a></div>
			  <div id="dissociaEnte" style="display: none;"><a href="#" onClick="disassociaSoggettoAcc()"><img src="../../img/patto_elim.gif" alt="Elimina"></a></div>
			  </span>  
	      </div>
	      </td>
		</tr>
       	
       	<tr>
	      <td colspan="2">
	      	<div id="soggAccSez" style="display: none;">
	          <table class="main" width="100%" border="0">
	              <tr>
	                <td class="etichetta">Codice Fiscale</td>
	                <td class="campo">
	                  <af:textBox classNameBase="input" required="true" type="text" name="codFiscaleSoggAcc" 
	                  	title="Soggetto Accreditato" readonly="true" value="" size="30" maxlength="16"/>
	                  <input type="hidden" name="codSedeSoggAcc" value="">               
	                </td>
	              </tr>
	              <tr>
	                <td class="etichetta">Ragione Sociale</td>
	                <td class="campo">
	                  <af:textBox classNameBase="input" type="text" name="ragioneSocialeSoggAcc" readonly="true" value="" size="80" maxlength="200"/>
	                </td>
	              </tr>
	              <tr>
	                <td class="etichetta">Indirizzo</td>
	                <td class="campo">
	                  <af:textBox classNameBase="input" type="text" name="indirizzoSoggAcc" readonly="true" value="" size="60" maxlength="100"/>
	                </td>
	              </tr>
	              <tr>
    				<td class="etichetta">Telefono</td>
    				<td class="campo">
    					<af:textBox classNameBase="input" type="text" name="strTelSoggAcc" readonly="true" value="" size="15" maxlength="15"/>
    				</td>
				  </tr>
	              <tr>
	                <td class="etichetta">Comune</td>
	                <td class="campo">
	                  <af:textBox classNameBase="input" type="text" name="comuneSoggAcc" readonly="true" value="" size="60" maxlength="100"/>
	                </td>
	              </tr>
	              <%if(Properties.RER.equalsIgnoreCase(regione) || Properties.CAL.equalsIgnoreCase(regione)) {%>
		              <tr>
					    <td class="etichetta">
					      Appuntamento&nbsp;
					    </td>
					    <td class="campo">
					      <af:textArea name="strNoteEnte" value="" required="true" title="Appuntamento"
					                    cols="60" rows="4" maxlength="2000" classNameBase="textarea"/>
					    </td>
					  </tr>
				<%} else {%>
					<tr>
						<td class="etichetta">
					      Appuntamento&nbsp;
					    </td>
					    <td class="campo">
					      <af:textArea name="strNoteEnte" value="" required="false" title="Appuntamento"
					                    cols="60" rows="4" maxlength="2000" classNameBase="textarea"/>
					    </td>
					</tr>
				<%}%>
	            </table>
	        </div>
	    </td>
	  </tr>
	  
	 <tr>
		<td colspan="2">
			<input type="submit" class="pulsanti" value="Associa soggetto accreditato" />
		</td>
		</tr>
		<tr>
		<td colspan="2">&nbsp;</td>
	</tr>
       	
	</table>
	<%out.print(htmlStreamBottom);%>
	
	<af:textBox type="hidden" name="PAGE" value="SoggettoAccProgrammiPage" />
	<af:textBox type="hidden" name="cdnfunzione" value="<%= _funzione %>" />
	<af:textBox type="hidden" name="ASSOCIASOGGETTOPROGRAMMA" value="ASSOCIA" />
	<af:textBox type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>" />
	<af:textBox type="hidden" name="PRGPATTOLAVORATORE" value="<%= prgPattoLavoratore %>" />
	
</af:form>
<%}%>

</body>
</html>
