<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ include file="../amministrazione/openPage.inc" %>

<%@ page import="
  com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.User,
  it.eng.afExt.utils.*,
  it.eng.sil.security.ProfileDataFilter,  
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  it.eng.sil.security.PageAttribs,  
  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%

	String _current_page = (String) serviceRequest.getAttribute("PAGE"); 
    PageAttribs attributi = new PageAttribs(user, _current_page);
	boolean canModify = false;
    canModify = attributi.containsButton("aggiorna");
%>

<%  
  


  
  Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");

  String strCodiceUtenteCorrente=codiceUtenteCorrente.toString();


  String _page = (String) serviceRequest.getAttribute("PAGE"); 

  String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE"); 
   
//   	canModify = true;
   
   	//
   	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>

<%
	SourceBean row = (SourceBean)serviceResponse.getAttribute("M_GetCpiMigrazioni.ROWS.ROW");
	  
	String comune = null;
	String provincia = null;
	String strCap = null;
	String strDescrizione = null;
	String strEMail = null;
	String strEMailMigrazioni= null;
	String strFax = null;
	String strIndirizzo = null;
	String strLocalita = null;
	String strTel = null;
	String tipoFile = null;
	String codCpi = null;
	String codMonoTipoFile = null;
	BigDecimal numKloCpi = null;
	
	String strEmailPec = null;
	String strResponsabile = null;
	String strIndirizzoStampa = null;
 	
	
	
	codCpi =         Utils.notNull(row.getAttribute("codCpi"));
	comune =         Utils.notNull(row.getAttribute("comune"));
	provincia =      Utils.notNull(row.getAttribute("provincia"));
	strCap =         Utils.notNull(row.getAttribute("strCap"));
	strDescrizione = Utils.notNull(row.getAttribute("strDescrizione"));
	strEMail =       Utils.notNull(row.getAttribute("strEmail"));
	strEMailMigrazioni = Utils.notNull(row.getAttribute("strEmailMigrazione"));
	strFax =         Utils.notNull(row.getAttribute("strFax"));
	strIndirizzo =   Utils.notNull(row.getAttribute("strIndirizzo"));
	strLocalita =    Utils.notNull(row.getAttribute("strLocalita"));
	strTel =         Utils.notNull(row.getAttribute("strTel"));
	codMonoTipoFile = Utils.notNull(row.getAttribute("codMonoTipoFile"));
	numKloCpi = (BigDecimal)row.getAttribute("numKloCpi");
	tipoFile = deCodMonoTipoFile(codMonoTipoFile);
	//
	numKloCpi = numKloCpi.add(new BigDecimal(1));
	//
	addCustomModuleDeMonoTipoFile(serviceResponse);
	
	strEmailPec = Utils.notNull(row.getAttribute("stremailpec"));
	strResponsabile = Utils.notNull(row.getAttribute("strresponsabile"));
	strIndirizzoStampa = Utils.notNull(row.getAttribute("strindirizzostampa"));
	
%>
<%! 
	/**
	*	decodifica del codMonoTipoFile
	*/
	String deCodMonoTipoFile (String codMonoTipoFile) {
		String ret = null;
		if (codMonoTipoFile==null)
			ret = "";
		else if (codMonoTipoFile.equals("T"))
			ret = "TXT";
		else if (codMonoTipoFile.equals("P"))
			ret = "PDF";
		else if (codMonoTipoFile.equals("E"))
			ret = "TXT e PDF";
		else if (codMonoTipoFile.equals("C"))
			ret = "cooperazione applicativa";
		return ret;
	}

	void addCustomModuleDeMonoTipoFile(SourceBean res) throws Exception {
		SourceBean m = new SourceBean ("CUSTOM_DE_MONO_TIPO_FILE");
		SourceBean r = new SourceBean ("ROW");
		SourceBean rows = new SourceBean ("rows");		
		
		/* r.setAttribute("descrizione", "file TXT");
		r.setAttribute("codice", "T");
		rows.setAttribute( r); */
		
		r = new com.engiweb.framework.base.SourceBean ("ROW");
		r.setAttribute("descrizione", "file PDF");
		r.setAttribute("codice", "P");
		rows.setAttribute( r);
		
		/*r = new com.engiweb.framework.base.SourceBean ("ROW");
		r.setAttribute("descrizione", "file PDF e TXT");
		r.setAttribute("codice", "E");
		rows.setAttribute(r);*/
		
		r = new com.engiweb.framework.base.SourceBean ("ROW");
		r.setAttribute("descrizione", "Cooperaziona Applicativa");
		r.setAttribute("codice", "C");
		rows.setAttribute(r);
		//		
		m.setAttribute(rows);
		res.setAttribute(m);
	}
%>
<html>

<head>
  <title>Cpi Migrazioni</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

  <af:linkScript path="../../js/"/>
  
  <SCRIPT TYPE="text/javascript">
  	var flagChanged = false;
  	function fieldChanged() {
   	 flagChanged = true;
  	}
  	function checkEMail(s) {
		ret = true;
		var regExp = new RegExp("^[^@]+@[^@]+\.[^@]+$");
		res = s.match(regExp);
		if (res==null||res.length==0)
			ret = false;
		return ret;
	}
	function controlla(eMail) {	
		ret = true;
		if (eMail!=null && eMail!="" && !checkEMail(eMail))  {
			if (eMail==document.Frm1.STREMAIL.value) {
				alert("Indirizzo E-mail non corretto");
				document.Frm1.STREMAIL.focus();
			} else if (eMail==document.Frm1.STREMAILPEC.value) {
				alert("Indirizzo E-mail PEC non corretto");
				document.Frm1.STREMAILPEC.focus();
				}
			ret = false;
		}
		return ret;
	} 
  </SCRIPT>
<script language="Javascript">
<!--Contiene il javascript che si occupa di aggiornare i link del footer-->
  <% 
       //Genera il Javascript che si occuperà di inserire i links nel footer
       attributi.showHyperLinks(out, requestContainer,responseContainer,"");
  %>
</script>
  

</head>

<body class="gestione" onload="rinfresca()">

  
  <center>
        <font color="green">
          <af:showMessages prefix="M_UpdateCpiMigrazioni"/>    
        </font>
        <font color="red">
          <af:showErrors />
        </font>
      </center>
        <br>
        <br>
           <p class="titolo">Impostazione Cpi</p>      
  <af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="controlla(this.STREMAILPEC.value)&& controlla(this.STREMAIL.value)">

    <input type="hidden" name="PAGE" value="CPIMigrazioniPage">
    <input type="hidden" name="NUMKLOCPI" value="<%=numKloCpi%>">
    <input type="hidden" name="CDNFUNZIONE" value="<%= cdnFunzione %>"/>
    
    <input type="hidden" name="STREMAILMIGRAZIONI" value="<%=strEMailMigrazioni%>">
    <input type="hidden" name="codMonoTipoFile" value="<%=codMonoTipoFile%>">
                    
        <%out.print(htmlStreamTop);%>
    
          
          
             <center>              
          <TABLE>
        	  <tr>
                <td class="etichetta">Codice</td>
                <td class="campo">    
                <af:textBox classNameBase="input" type="text" name="CODCPI" value="<%=codCpi%>" 
                	validateOnPost="true" 
                    readonly="true"  size="50" maxlength="100"/>
                </td>
              </tr>
              <tr>
                <td class="etichetta">Descrizione</td>
                <td class="campo">    
                <af:textBox classNameBase="input" type="text" name="STRDESCRIZIONE" value="<%=strDescrizione%>" 
                	validateOnPost="true" 
                    readonly="true"  size="50" maxlength="100"/>
                </td>
              </tr>
              <tr>
                <td class="etichetta">Indirizzo</td>
                <td class="campo">    
                <af:textBox classNameBase="input" type="text" name="STRINDIRIZZO" value="<%=strIndirizzo%>" 
                	validateOnPost="true" 
                    readonly="<%=String.valueOf(!canModify)%>"  size="50" maxlength="100"/>
                </td>
              </tr>
              <tr>
                <td class="etichetta">Localit&agrave;</td>
                <td class="campo">    
                <af:textBox classNameBase="input" type="text" name="STRLOCALITA" value="<%=strLocalita%>" 
                	validateOnPost="true" 
                    readonly="true"  size="50" maxlength="100"/>
                </td>
              </tr>
              <tr>
                <td class="etichetta">CAP</td>
                <td class="campo">    
                <af:textBox classNameBase="input" type="text" name="STRCAP" value="<%=strCap%>" 
                	validateOnPost="true" 
                    readonly="<%=String.valueOf(!canModify)%>" size="11" maxlength="10"/>
                </td>
              </tr>
              <tr>
                <td class="etichetta">Provincia</td>
                <td class="campo">    
                <af:textBox classNameBase="input" type="text" name="PROVINCIA" value="<%=provincia%>" 
                	validateOnPost="true" 
                    readonly="true"  size="50" maxlength="100"/>
                </td>
              </tr>
              <tr>
                <td class="etichetta">Comune</td>
                <td class="campo">    
                <af:textBox classNameBase="input" type="text" name="COMUNE" value="<%=comune%>" 
                	validateOnPost="true" 
                    readonly="true"  size="50" maxlength="100"/>
                </td>
              </tr>
              <tr>
                <td class="etichetta">Tel.</td>
                <td class="campo">    
                <af:textBox classNameBase="input" type="text" name="STRTEL" value="<%=strTel%>" 
                	validateOnPost="true" 
                    readonly="<%=String.valueOf(!canModify)%>" size="20" maxlength="20"/>
                </td>
              </tr>
              <tr>
                <td class="etichetta">Fax</td>
                <td class="campo">    
                <af:textBox classNameBase="input" type="text" name="STRFAX" value="<%=strFax%>" 
                	validateOnPost="true" 
                    readonly="<%=String.valueOf(!canModify)%>"  size="20" maxlength="20"/>
                </td>
              </tr>
              <tr>
                <td class="etichetta">E-Mail</td>
                <td class="campo">    
                <af:textBox classNameBase="input" type="text" name="STREMAIL" value="<%=strEMail%>" 
                	validateOnPost="true" 
                    readonly="<%=String.valueOf(!canModify)%>" onKeyUp="fieldChanged();" size="50" maxlength="80"/>
                </td>
              </tr>  
              
              <tr>
                <td class="etichetta">E-Mail PEC</td>
                <td class="campo">    
                <af:textBox classNameBase="input" type="text" name="STREMAILPEC" value="<%=strEmailPec%>" 
                	validateOnPost="true" 
                    readonly="<%=String.valueOf(!canModify)%>" onKeyUp="fieldChanged();" size="50" maxlength="80"/>
                </td>
              </tr> 
              <tr>
                <td class="etichetta">Responsabile CPI</td>
                <td class="campo">    
                <af:textBox classNameBase="input" type="text" name="STRRESPONSABILE" value="<%=strResponsabile%>" 
                	validateOnPost="true" 
                    readonly="<%=String.valueOf(!canModify)%>" onKeyUp="fieldChanged();" size="50" maxlength="80"/>
                </td>
              </tr> 
              <tr>
                <td class="etichetta">Indirizzo stampa piè pagina</td>
                <td class="campo">    
                <af:textArea classNameBase="textarea" name="STRINDIRIZZOSTAMPA" 
                	readonly="<%=String.valueOf(!canModify)%>" 
					value="<%=strIndirizzoStampa%>" maxlength="600"  rows="7" cols="50"/>    
                </td>
              </tr> 
              
              <tr>
                <td colspan="2">
                  <table class="main" width="100%">
                    <tr><td>&nbsp;</td></tr>
                    <% if (canModify) {%>
                      <tr>
                        <td colspan="2" align="center">
                            <input type="submit" class="pulsanti" name="aggiorna" value="Aggiorna">
                            <input type="reset" class="pulsanti" name="reset" value="Annulla">    
                        </td>
                      </tr>            
                      <%}%>                                           
                      <tr><td colspan="2" align="center">
                      <%= InfCorrentiAzienda.formatBackList(sessionContainer, "ListaCpiMigrazioniPage") %>
                      </td></tr>
                   </table>
                </td>
              </tr>
        </TABLE>    
        </center>
    <%out.print(htmlStreamBottom);%>
  </af:form>
   <% if (canModify) {%>
	  <table>
		<tr>
			<td class="campo2">N.B.: La modifica del campo "Indirizzo stampa piè pagina" ha impatto su tutte le stampe che mostrano 
			nel piè pagina le anagrafiche dei CPI.</td>
		</tr>
	  </table>
  <%}%> 
</body>
</html>