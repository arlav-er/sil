<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
   
  it.eng.sil.util.*, 
  it.eng.afExt.utils.StringUtils,
  java.lang.*,
  java.text.*, 
  java.util.*,
  java.math.*,
  it.eng.sil.security.*"%>

<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%

	boolean dopoAccorpamento = serviceRequest.containsAttribute("DOACCORPAMENTO");
	String prgAzienda="";
	prgAzienda= (String)serviceRequest.getAttribute("prgAzienda");
    int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
	
	String prgUnita="";
	String strRagioneSociale="";
    String strPartitaIva="";
    String strCodiceFiscale="";
    
	String strIndirizzo="";
	String strLocalita="";
	String strCap="";
	String desComune="";
	String flgMezziPub="";
	String codAzStato="";
	String codAteco="";
	String tipo_ateco="";
	String strDesAteco="";
	String strDesTipoAzienda="";
	String strResponsabile="";
	String strReferente="";
	String strTel="";
	String strFax="";
	String strEmail="";
	String datInizio="";
	String datFine="";
	String strNote="";
	String flgSede="";
	String numREA="";
  	String codCCNL="";
	String tipo_ccnl="";
  	String desCCNL="";

	String prgUnita_2="";
	String strIndirizzo_2="";
	String strLocalita_2="";
	String strCap_2="";
	String desComune_2="";
	String flgMezziPub_2="";
	String codAzStato_2="";
	String codAteco_2="";
	String tipo_ateco_2="";
	String strDesAteco_2="";
	String strDesTipoAzienda_2="";
	String strResponsabile_2="";
	String strReferente_2="";
	String strTel_2="";
	String strFax_2="";
	String strEmail_2="";
	String datInizio_2="";
	String datFine_2="";
	String strNote_2="";
	String flgSede_2="";
	String numREA_2="";
  	String codCCNL_2="";
	String tipo_ccnl_2="";
  	String desCCNL_2="";

// da request, utili per il bottone di CHIUDI
prgUnita   = StringUtils.notNull( (String)serviceRequest.getAttribute("prgUnitaAccorpante") );
prgUnita_2 = StringUtils.notNull( (String)serviceRequest.getAttribute("prgUnitaAccorpata")  );

if (!dopoAccorpamento) {

	SourceBean unitaRow=(SourceBean) serviceResponse.getAttribute("M_GetUnitaAziendaAccorpamento.ROWS.ROW");
	SourceBean aziendaRow= (SourceBean) serviceResponse.getAttribute("M_GetTestataAzienda.ROWS.ROW");
   
   	prgUnita=unitaRow.containsAttribute("prgUnita") ? unitaRow.getAttribute("prgUnita").toString() : "";
    strRagioneSociale=StringUtils.getAttributeStrNotNull(aziendaRow, "strRagioneSociale");
    strPartitaIva=StringUtils.getAttributeStrNotNull(aziendaRow, "strPartitaIva");
    strCodiceFiscale=StringUtils.getAttributeStrNotNull(aziendaRow, "strCodiceFiscale");
    
	strIndirizzo=StringUtils.getAttributeStrNotNull(unitaRow, "strIndirizzo");
	strLocalita=StringUtils.getAttributeStrNotNull(unitaRow, "strLocalita");
	strCap=StringUtils.getAttributeStrNotNull(unitaRow, "strCap");
	desComune=StringUtils.getAttributeStrNotNull(unitaRow, "codCom") + " " +
					StringUtils.getAttributeStrNotNull(unitaRow, "strDenominazione")+ 
					 " (" + StringUtils.getAttributeStrNotNull(unitaRow, "Provincia")+")";
	flgMezziPub=StringUtils.getAttributeStrNotNull(unitaRow, "flgMezziPub");
	codAzStato=StringUtils.getAttributeStrNotNull(unitaRow, "codAzStato");
	codAteco=StringUtils.getAttributeStrNotNull(unitaRow, "codAteco");
	tipo_ateco=StringUtils.getAttributeStrNotNull(unitaRow, "Tipo_Ateco");  
	strDesAteco=StringUtils.getAttributeStrNotNull(unitaRow, "strDesAteco");
	strDesTipoAzienda="";
	strResponsabile=StringUtils.getAttributeStrNotNull(unitaRow, "strResponsabile");
	strReferente=StringUtils.getAttributeStrNotNull(unitaRow, "strReferente");
	strTel=StringUtils.getAttributeStrNotNull(unitaRow, "strTel");
	strFax=StringUtils.getAttributeStrNotNull(unitaRow, "strFax");
	strEmail=StringUtils.getAttributeStrNotNull(unitaRow, "strEmail");
	datInizio=StringUtils.getAttributeStrNotNull(unitaRow, "datInizio");
	datFine=StringUtils.getAttributeStrNotNull(unitaRow, "datFine");
	strNote=StringUtils.getAttributeStrNotNull(unitaRow, "strNote");
	flgSede=StringUtils.getAttributeStrNotNull(unitaRow, "flgSede");
	numREA=unitaRow.containsAttribute("strREA") ? unitaRow.getAttribute("strREA").toString() : "";
  	codCCNL=StringUtils.getAttributeStrNotNull(unitaRow, "codCCNL");
	tipo_ccnl=StringUtils.getAttributeStrNotNull(unitaRow, "tipoCCNL");
  	desCCNL=StringUtils.getAttributeStrNotNull(unitaRow, "desCCNL");


	prgUnita_2=unitaRow.containsAttribute("prgUnita_2") ? unitaRow.getAttribute("prgUnita_2").toString() : "";
	strIndirizzo_2=StringUtils.getAttributeStrNotNull(unitaRow, "strIndirizzo_2");
	strLocalita_2=StringUtils.getAttributeStrNotNull(unitaRow, "strLocalita_2");
	strCap_2=StringUtils.getAttributeStrNotNull(unitaRow, "strCap_2");
	desComune_2=StringUtils.getAttributeStrNotNull(unitaRow, "codCom_2") + " " +
					 StringUtils.getAttributeStrNotNull(unitaRow, "strDenominazione_2")+ 
					 " (" + StringUtils.getAttributeStrNotNull(unitaRow, "Provincia_2")+")";
	flgMezziPub_2=StringUtils.getAttributeStrNotNull(unitaRow, "flgMezziPub_2");
	codAzStato_2=StringUtils.getAttributeStrNotNull(unitaRow, "codAzStato_2");
	codAteco_2=StringUtils.getAttributeStrNotNull(unitaRow, "codAteco_2");
	tipo_ateco_2=StringUtils.getAttributeStrNotNull(unitaRow, "Tipo_Ateco_2");  
	strDesAteco_2=StringUtils.getAttributeStrNotNull(unitaRow, "strDesAteco_2");
	strDesTipoAzienda_2="";
	strResponsabile_2=StringUtils.getAttributeStrNotNull(unitaRow, "strResponsabile_2");
	strReferente_2=StringUtils.getAttributeStrNotNull(unitaRow, "strReferente_2");
	strTel_2=StringUtils.getAttributeStrNotNull(unitaRow, "strTel_2");
	strFax_2=StringUtils.getAttributeStrNotNull(unitaRow, "strFax_2");
	strEmail_2=StringUtils.getAttributeStrNotNull(unitaRow, "strEmail_2");
	datInizio_2=StringUtils.getAttributeStrNotNull(unitaRow, "datInizio_2");
	datFine_2=StringUtils.getAttributeStrNotNull(unitaRow, "datFine");
	strNote_2=StringUtils.getAttributeStrNotNull(unitaRow, "strNote_2");
	flgSede_2=StringUtils.getAttributeStrNotNull(unitaRow, "flgSede_2");
	numREA_2=unitaRow.containsAttribute("strREA_2") ? unitaRow.getAttribute("strREA_2").toString() : "";
  	codCCNL_2=StringUtils.getAttributeStrNotNull(unitaRow, "codCCNL_2");
	tipo_ccnl_2=StringUtils.getAttributeStrNotNull(unitaRow, "tipoCCNL_2");
  	desCCNL_2=StringUtils.getAttributeStrNotNull(unitaRow, "desCCNL_2");
	
}
		
String htmlStreamTop = StyleUtils.roundTopTable(true);
String htmlStreamBottom = StyleUtils.roundBottomTable(true);
%>	  

<html>

<head>
  <title>Accorpamento Unità Aziende</title>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
    <af:linkScript path="../../js/"/>

<script language="Javascript">

	function accorpa(prgAccorpata, prgAccorpante) {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

		target = "AdapterHTTP?PAGE=AccorpamentoUnitaPage" +
					"&DOACCORPAMENTO=true" +
					"&cdnFunzione=<%=_funzione%>" +
					"&prgAzienda=<%=prgAzienda%>";
  		
  		if (confirm("Verrà accorpata l'unità " + prgAccorpata + " alla " + prgAccorpante+ ".\r\n" +
  					"L'unità " + prgAccorpata + " verrà cancellata.\r\n" +
  					"Procedere con l'accorpamento?")) {
  					
			if (prgAccorpata == 1) {
				target+="&prgUnitaAccorpante=<%=prgUnita_2%>" +
						"&prgUnitaAccorpata=<%=prgUnita%>";
			}
		    else {
		    	target+="&prgUnitaAccorpante=<%=prgUnita%>" +
		    			"&prgUnitaAccorpata=<%=prgUnita_2%>";
		    }
		    setWindowLocation(target);
		}
	}
      
	function goTestata() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

		var url = "AdapterHTTP?PAGE=IdoTestataAziendaPage" +
					"&prgAzienda=<%=prgAzienda%>" +
					"&cdnFunzione=<%=_funzione%>" +
					"&exSelezione1=<%=prgUnita%>" +
					"&exSelezione2=<%=prgUnita_2%>" +
					"#unitalocali";
		setWindowLocation(url);
	}

	if (window.top.menu != undefined){
  		window.top.menu.location="AdapterHTTP?PAGE=MenuCompletoPage";
	}
</script>

</head>

<body class="gestione" onload="rinfresca();">

<h2>Accorpamento Unità Aziende</h2>
<p>
  <font color="green"><af:showMessages prefix="M_DoAccorpamento" /></font>
  <font color="red"><af:showErrors /></font>
</p>
<af:form name="Frm1" method="POST" action="AdapterHTTP">

<% if (!dopoAccorpamento) { %>

<%= htmlStreamTop %>
<table class="main" align="center" border="0" width="80%" >
    <tr>
	    <td class="etichetta" width="10%">Azienda</td>
	    <td class="campo" width="90%"><b><%=strRagioneSociale%></b></td>
    </tr>
    <tr>
	    <td class="etichetta">Partita Iva:</td>
	    <td class="campo"><b><%=strPartitaIva%></b></td>
    </tr>
    <tr>
	    <td class="etichetta">Codice Fiscale</td>
	    <td class="campo"><b><%=strCodiceFiscale%></b></td>
    </tr>
</table>
<%= htmlStreamBottom %>

<%= htmlStreamTop %>
<table class="main" align="center" border="0" width="80%" >

      <tr valign="top">
        <td class="etichetta" width="20%"></td>
        <td class="campo" width="40%" ><center>Unità 1</center></td>
		<td class="campo" width="40%" ><center>Unità 2</center></td>
      </tr>

      <tr valign="top">
    	<td class="etichetta">Sede legale</td>
        <td class="campo">
          <af:comboBox classNameBase="input" name="flgSede" title="Sede" disabled="true"> 
            <OPTION value=""></OPTION>
            <OPTION value="S" <%if (flgSede.equals("S")) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
            <OPTION value="N" <%if (flgSede.equals("N")) out.print("SELECTED=\"true\"");%>>No</OPTION>
          </af:comboBox>
        </td>
        <td class="campo">
          <af:comboBox classNameBase="input" name="flgSede_2" title="Sede" disabled="true">
            <OPTION value=""></OPTION>
            <OPTION value="S" <%if (flgSede_2.equals("S")) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
            <OPTION value="N" <%if (flgSede_2.equals("N")) out.print("SELECTED=\"true\"");%>>No</OPTION>
          </af:comboBox>
        </td>
      </tr>
        
      <tr valign="top">
        <td class="etichetta">Numero REA</td>
        <td class="campo">
          <af:textBox classNameBase="input"  name="strREA" title="Numero REA" maxlength="11" size="12" value="<%=numREA%>" readonly="true" />
        </td>
	    <td class="campo">
          <af:textBox classNameBase="input"  name="strREA_2" title="Numero REA" maxlength="11" size="12" value="<%=numREA_2%>" readonly="true" />
        </td>    
      </tr>
      
      <tr valign="top">
        <td class="etichetta">Indirizzo </td>
        <td class="campo">
          <af:textBox classNameBase="input" title="Indirizzo unità azienda" name="strIndirizzo" size="50" maxlength="60" value="<%=strIndirizzo%>" readonly="true"/>
        </td>
        <td class="campo">
          <af:textBox classNameBase="input" title="Indirizzo unità azienda" name="strIndirizzo_2" size="50" maxlength="60" value="<%=strIndirizzo_2%>" readonly="true"/>
        </td>
      </tr>

      <tr valign="top">
        <td class="etichetta">Localita</td>
        <td class="campo">
          <af:textBox classNameBase="input" name="strLocalita" size="50" maxlength="100" value="<%=strLocalita%>" readonly="true" />
        </td>
        <td class="campo">
          <af:textBox classNameBase="input" name="strLocalita_2" size="50" maxlength="100" value="<%=strLocalita_2%>" readonly="true" />
        </td>
      </tr>
    
      <tr>
        <td class="etichetta">Comune</td>
        <td class="campo">
        	<af:textBox classNameBase="input" title="comune unità azienda"  type="text" name="desComune" value="<%=desComune%>" size="40" maxlength="50" readonly="true"/>
        </td>                
        <td class="campo">
        	<af:textBox classNameBase="input" title="comune unità azienda"  type="text" name="desComune_2" value="<%=desComune_2%>" size="40" maxlength="50" readonly="true" />
        </td>                
      </tr>   

      <tr valign="top">
        <td class="etichetta">Cap</td>
        <td class="campo">
          <af:textBox classNameBase="input" name="strCap" size="5" value="<%= strCap %>" readonly="true" maxlength="5" />
        </td>
        <td class="campo">
          <af:textBox classNameBase="input" name="strCap_2" size="5" value="<%= strCap_2 %>" readonly="true" maxlength="5" />
        </td>
      </tr>

      <tr valign="top">
        <td class="etichetta">Stato azienda</td>
        <td class="campo">
          <af:comboBox classNameBase="input"  title="Stato azienda" name="codAzStato" selectedValue="<%=codAzStato%>" disabled="true" moduleName="M_GetStatiAzienda" addBlank="true" />
        </td>
        <td class="campo">
          <af:comboBox classNameBase="input"  title="Stato azienda" name="codAzStato_2" selectedValue="<%=codAzStato_2%>" disabled="true" moduleName="M_GetStatiAzienda" addBlank="true" />
        </td>
      </tr>

      <tr valign="top">
        <td class="etichetta">Codice Attività</td>
        <td class="campo">
          <af:textBox classNameBase="input" name="codAteco" title="Codice di Attività" size="6" maxlength="6" value="<%=codAteco%>" readonly="true" />
        </td>
        <td class="campo">
          <af:textBox classNameBase="input" name="codAteco_2" title="Codice di Attività" size="6" maxlength="6" value="<%=codAteco_2%>" readonly="true" />
        </td>
      </tr> 


      <tr valign="top">
        <td class="etichetta">Tipo</td>
        <td class="campo">
          <af:textBox classNameBase="input" name="strTipoAteco" value="<%=tipo_ateco%>" readonly="true" size="48" />
        </td>
        <td class="campo">
          <af:textBox classNameBase="input" name="strTipoAteco_2" value="<%=tipo_ateco_2%>" readonly="true" size="48" />
        </td>
      </tr>

      <tr>
        <td class="etichetta">Attività</td>
        <td class="campo">
             <af:textBox classNameBase="input" name="strAteco" size="48" readonly="true" value="<%=strDesAteco%>" maxlength="100"/>
        </td>
        <td class="campo">
             <af:textBox classNameBase="input" name="strAteco_2" size="48" readonly="true" value="<%=strDesAteco_2%>" maxlength="100"/>
        </td>
      </tr>
    
      <tr valign="top">
        <td class="etichetta">Raggiungibile con mezzi pubblici</td>
        <td class="campo">
        <af:comboBox classNameBase="input" name="flgMezziPub" title="Raggiungibile con mezzi pubblici" disabled="true" >
            <OPTION value=""></OPTION>
            <OPTION value="S" <%if (flgMezziPub.equals("S")) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
            <OPTION value="N" <%if (flgMezziPub.equals("N")) out.print("SELECTED=\"true\"");%>>No</OPTION>
          </af:comboBox>
        </td>
        <td class="campo">
        <af:comboBox classNameBase="input" name="flgMezziPub_2" title="Raggiungibile con mezzi pubblici" disabled="true" >
            <OPTION value=""></OPTION>
            <OPTION value="S" <%if (flgMezziPub_2.equals("S")) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
            <OPTION value="N" <%if (flgMezziPub_2.equals("N")) out.print("SELECTED=\"true\"");%>>No</OPTION>
          </af:comboBox>
        </td>
        
      </tr>
      
      <tr valign="top">
        <td class="etichetta">Telefono</td>
        <td class="campo">
          <af:textBox classNameBase="input" name="strTel" size="50" value="<%=strTel%>" readonly="true" maxlength="20" />
        </td>
        <td class="campo">
          <af:textBox classNameBase="input" name="strTel_2" size="50" value="<%=strTel_2%>" readonly="true" maxlength="20" />
        </td>        
      </tr>


      <tr valign="top">
        <td class="etichetta">Fax</td>
        <td class="campo">
          <af:textBox classNameBase="input"  name="strFax" size="50" value="<%=strFax%>" readonly="true" maxlength="20" />
        </td>
        <td class="campo">
          <af:textBox classNameBase="input"  name="strFax_2" size="50" value="<%=strFax_2%>" readonly="true" maxlength="20" />
        </td>        
      </tr>

      <tr valign="top">
        <td class="etichetta">Email</td>
        <td class="campo">
          <af:textBox classNameBase="input" name="strEmail" size="50" value="<%=strEmail%>" readonly="true" maxlength="80" />
        </td>
        <td class="campo">
          <af:textBox classNameBase="input" name="strEmail_2" size="50" value="<%=strEmail_2%>" readonly="true" maxlength="80" />
        </td>
      </tr>

      <tr valign="top">
        <td class="etichetta">CCNL</td>
        <td class="campo">
          <af:textBox type="text" classNameBase="input" name="codCCNL" value="<%=codCCNL%>" readonly="true" maxlength="8" size="8"  />
          &nbsp;
          <af:textBox type="text" classNameBase="input" name="desCCNL" value="<%=desCCNL%>" readonly="true" size="35" maxlength="50" />
        </td>
        <td class="campo">
          <af:textBox type="text" classNameBase="input" name="codCCNL_2" value="<%=codCCNL_2%>" readonly="true" maxlength="8" size="8"  />
          &nbsp;
          <af:textBox type="text" classNameBase="input" name="desCCNL_2" value="<%=desCCNL_2%>" readonly="true" size="35" maxlength="50" />
        </td>
      </tr>

      <tr valign="top">
        <td class="etichetta"></td>
        <td class="campo">
        	<br/>        
			<input type="button" name="Accorpa1su2" class="pulsanti" value="Accorpa e cancella &gt;&gt;"
					onClick="accorpa(1, 2);"/>
        </td>
        <td class="campo">
        	<br/>
        	<input type="button" name="Accorpa2su1" class="pulsanti" value="&lt;&lt; Accorpa e cancella"
        			onClick="accorpa(2,1);"/>
        </td>
      </tr>
            
</table>
<%= htmlStreamBottom %>

<%
} // if !dopoAccorpamento
%>

<br/>
<center>
	<input type="button" name="torna" class="pulsanti" value="  Chiudi  "
			onClick="goTestata();" />
</center>
<br/>

</af:form>     

</body>

</html>
