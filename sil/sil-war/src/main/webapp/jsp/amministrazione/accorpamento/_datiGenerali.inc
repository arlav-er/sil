
<%
  boolean canScadenzeLav = false;
  boolean canVerificheLav = false;
  
  String codCpi = "";
  String inserimentoLav="";
  
  //Recupero CODCPI
  int cdnUt = user.getCodut();
  int cdnTipoGruppo = user.getCdnTipoGruppo();
  if(cdnTipoGruppo == 1) {
    codCpi =  user.getCodRif();
  }
  it.eng.sil.module.amministrazione.accorpamento.DatiLavoratore dati1 = null;
  it.eng.sil.module.amministrazione.accorpamento.DatiLavoratore dati2 = null;
  //try {
  	dati1 = new it.eng.sil.module.amministrazione.accorpamento.DatiLavoratore((SourceBean)infoLav1.getAttribute("GET_AN_LAVORATORE_ANAG.ROWS.ROW"));
  	dati2 = new it.eng.sil.module.amministrazione.accorpamento.DatiLavoratore((SourceBean)infoLav2.getAttribute("GET_AN_LAVORATORE_ANAG.ROWS.ROW"));
 // }catch(Exception e) {
 //   com.engiweb.framework.tracing.TracerSingleton.log(it.eng.sil.Values.APP_NAME, com.engiweb.framework.tracing.TracerSingleton.CRITICAL, "", e);	
 // }
%>


<table  class="main" border="0">
<tr>
    <td class="colonna1">Codice Fiscale</td>
    <td class="colonna2"><af:textBox classNameBase="input"  title="Codice fiscale" required="true" type="text" name="strCodiceFiscale" value="<%=dati1.strCodiceFiscale%>" size="21" maxlength="16" readonly="<%= String.valueOf(!canModify) %>" />&nbsp;    
    &nbsp;        
    &nbsp;&nbsp;
    <%--
    val.&nbsp;
    <af:comboBox 
      name="FLGCFOK"
      classNameBase="input"
      disabled="<%= String.valueOf(!canModify) %>"   
      >   
      <option value=""  <% if ( "".equalsIgnoreCase(dati1.strFlgcfOk) )  { %>SELECTED="true"<% } %> ></option>
      <option value="S" <% if ( "S".equalsIgnoreCase(dati1.strFlgcfOk) ) { %>SELECTED="true"<% } %> >Sì</option>
      <option value="N" <% if ( "N".equalsIgnoreCase(dati1.strFlgcfOk) ) { %>SELECTED="true"<% } %> >No</option>
    </af:comboBox>
    --%>
    </td>
    <td class="colonna2"><af:textBox classNameBase="input"  title="Codice fiscale" required="true" type="text" name="strCodiceFiscale" value="<%=dati2.strCodiceFiscale%>" size="21" maxlength="16"  readonly="<%=String.valueOf(!canModify) %>" />&nbsp;    
    &nbsp;        
    &nbsp;&nbsp;
    <%--
    val.&nbsp;
    <af:comboBox 
      name="FLGCFOK"
      classNameBase="input"
      disabled="<%= String.valueOf(!canModify) %>"      
      >
      <option value=""  <% if ( "".equalsIgnoreCase(dati2.strFlgcfOk) )  { %>SELECTED="true"<% } %> ></option>
      <option value="S" <% if ( "S".equalsIgnoreCase(dati2.strFlgcfOk) ) { %>SELECTED="true"<% } %> >Sì</option>
      <option value="N" <% if ( "N".equalsIgnoreCase(dati2.strFlgcfOk) ) { %>SELECTED="true"<% } %> >No</option>
    </af:comboBox>
    --%>
    </td>
    <!-- SUBMIT -->
</tr>
<%if ((dati1.strFlgcfOk.equals("") || dati1.strFlgcfOk.equals("N"))||(dati2.strFlgcfOk.equals("") || dati2.strFlgcfOk.equals("N"))) {%>
<tr>
	<td>&nbsp;</td>
	<%if (dati1.strFlgcfOk.equals("") || dati1.strFlgcfOk.equals("N") ) {%>
	  <td align="left">Codice fiscale non controllato</td>
	<%} else {%>
	<td>&nbsp;</td>
	<%}%>
	<%if (dati2.strFlgcfOk.equals("") || dati2.strFlgcfOk.equals("N")) {%>
	  <td align="left">Codice fiscale non controllato</td>
	<%} else {%>
	<td>&nbsp;</td>
	<%}%>
</tr>
<%}%>
<!--<script language="javascript">cfIsChecked();</script>-->

<tr>
    <td class="colonna1">Cognome</td>
    <td class="colonna2"><af:textBox classNameBase="input"  title="Cognome" required="true" type="text" name="strCognome" value="<%=dati1.strCognome%>" size="30" maxlength="30" readonly="<%= String.valueOf(!canModify) %>" />
    </td>
    <td class="colonna2"><af:textBox classNameBase="input"  title="Cognome" required="true" type="text" name="strCognome" value="<%=dati2.strCognome%>" size="30" maxlength="30" readonly="<%= String.valueOf(!canModify) %>" />    
    </td>
</tr>
<tr>
    <td class="colonna1">Nome</td>
    <td class="colonna2"><af:textBox classNameBase="input"  title="Nome" required="true" type="text" name="strNome" value="<%=dati1.strNome%>" size="30" maxlength="30" readonly="<%= String.valueOf(!canModify) %>" /></td>
    <td class="colonna2"><af:textBox classNameBase="input"  title="Nome" required="true" type="text" name="strNome" value="<%=dati2.strNome%>" size="30" maxlength="30" readonly="<%= String.valueOf(!canModify) %>" /></td>
</tr>
<tr>
    <td class="colonna1">Sesso</td>
    <td class="colonna2">
    	<af:comboBox classNameBase="input" name="strSesso" required="true" title="sesso"  disabled="<%= String.valueOf(!canModify) %>" >
          <OPTION value="M" <%if (dati1.strSesso.equals("M")) out.print("SELECTED=\"true\"");%>>M</OPTION>
          <OPTION value="F" <%if (dati1.strSesso.equals("F")) out.print("SELECTED=\"true\"");%>>F</OPTION>
          <!--<OPTION value="N" >Non so</OPTION>-->
        </af:comboBox>
    </td>
    <td class="colonna2">
    	<af:comboBox classNameBase="input" name="strSesso" required="true" title="sesso" disabled="<%= String.valueOf(!canModify) %>" >
          <OPTION value="M" <%if (dati2.strSesso.equals("M")) out.print("SELECTED=\"true\"");%>>M</OPTION>
          <OPTION value="F" <%if (dati2.strSesso.equals("F")) out.print("SELECTED=\"true\"");%>>F</OPTION>
          <!--<OPTION value="N" >Non so</OPTION>-->
        </af:comboBox>
    </td>
</tr>

<tr>
<td class="colonna1">Stato civile</td>
<td class="colonna2">
  <af:comboBox name="CODSTATOCIVILE"
        title="Stato civile"
        required="false"
        moduleName="M_GetStatiCivili"
        classNameBase="input"
        disabled="<%= String.valueOf( !canModify ) %>"        
        selectedValue="<%=dati1.codstatoCivile %>" 
        addBlank="true" />
</td>
<td class="colonna2">
  <af:comboBox name="CODSTATOCIVILE"
        title="Stato civile"
        required="false"
        moduleName="M_GetStatiCivili"
        classNameBase="input"
        disabled="<%= String.valueOf( !canModify ) %>"        
        selectedValue="<%=dati2.codstatoCivile %>" 
        addBlank="true" />
</td>
</tr>

<tr>
    <td class="colonna1">Data di Nascita</td>
    <td class="colonna2"><af:textBox classNameBase="input"  title="Data di nascita" type="date" required="true" name="datNasc" value="<%=dati1.datNasc%>" size="11" maxlength="12" validateOnPost="true" readonly="<%= String.valueOf(!canModify) %>" /></td>
    <td class="colonna2"><af:textBox classNameBase="input"  title="Data di nascita" type="date" required="true" name="datNasc" value="<%=dati2.datNasc%>" size="11" maxlength="12" validateOnPost="true" readonly="<%= String.valueOf(!canModify) %>" /></td>
</tr>
<tr>
    <td class="colonna1">Comune di nascita</td>
    <td class="colonna2">
      <af:textBox classNameBase="input" title="Codice comune di nascita" 
                  type="text" name="codComNas" value="<%=dati1.codComNas%>" size="4" maxlength="4"
                  readonly="<%= String.valueOf(!canModify) %>"/>&nbsp;
    
    <af:textBox type="text" classNameBase="input" 
                name="strComNas"  value="<%=dati1.strComNas%>" size="30" maxlength="50"
                readonly="<%= String.valueOf(!canModify) %>" title="comune di nascita" />&nbsp;*&nbsp;&nbsp;        
    </td>
    <td class="colonna2">
      <af:textBox classNameBase="input" title="Codice comune di nascita" 
                  type="text" name="codComNas" value="<%=dati2.codComNas%>" size="4" maxlength="4"
                  readonly="<%= String.valueOf(!canModify) %>"/>&nbsp;    
    <af:textBox type="text" classNameBase="input" 
                name="strComNas"  value="<%=dati2.strComNas%>" size="30" maxlength="50"
                readonly="<%= String.valueOf(!canModify) %>" title="comune di nascita" />&nbsp;*&nbsp;&nbsp;    
    </td>

</tr>
<tr>
    <td class="colonna1">Nazione di cittadinanza</td>
    <td class="colonna2">
        <af:textBox classNameBase="input"  title="Cittadinanza" type="text" name="strNazione" value="<%=dati1.strNazione%>" 
        size="30" maxlength="40" required="true" readonly="<%= String.valueOf(!canModify) %>" />
    <af:textBox title="Cittadinanza" classNameBase="input" type="text"  name="codCittadinanza" value="<%=dati1.codCittadinanza%>" size="5" maxlength="5" readonly="true" />&nbsp;    
    <af:textBox type="text" classNameBase="input" name="strCittadinanza" value="<%=dati1.strCittadinanza%>" size="20" readonly="true"  />&nbsp;    
    </td>
    <td class="colonna2">
        <af:textBox classNameBase="input"  title="Cittadinanza" type="text" name="strNazione" value="<%=dati2.strNazione%>" 
        size="30" maxlength="40" required="true" readonly="<%= String.valueOf(!canModify) %>" />
    <af:textBox title="Cittadinanza" classNameBase="input" type="text"  name="codCittadinanza" value="<%=dati2.codCittadinanza%>" size="5" maxlength="5" readonly="true"  />&nbsp;
    <af:textBox type="text" classNameBase="input" name="strCittadinanza" value="<%=dati2.strCittadinanza%>" size="20" readonly="true"  />&nbsp;    
    </td>
</tr>
<tr>
    <td class="colonna1">Seconda Nazione di cittadinanza</td>
    <td class="colonna2">
        <af:textBox classNameBase="input"  type="text" name="strNazione2" value="<%=dati1.strNazione2%>" size="30" maxlength="40"
          required="false" readonly="<%= String.valueOf(!canModify) %>" />    
    <af:textBox classNameBase="input" type="text" name="codCittadinanza2" value="<%=dati1.codCittadinanza2%>" size="5" maxlength="5" readonly="true" />&nbsp;    
    <af:textBox classNameBase="input" type="text" name="strCittadinanza2" value="<%=dati1.strCittadinanza2%>" size="20" readonly="true"  />&nbsp;    
    </td>
    <td class="colonna2">
        <af:textBox classNameBase="input"  type="text" name="strNazione2" value="<%=dati2.strNazione2%>" size="30" maxlength="40"
        	 required="false" readonly="<%= String.valueOf(!canModify) %>" />    
    	<af:textBox classNameBase="input" type="text" name="codCittadinanza2" value="<%=dati2.codCittadinanza2%>" size="5" maxlength="5" readonly="true" />&nbsp;    
    	<af:textBox classNameBase="input" type="text" name="strCittadinanza2" value="<%=dati2.strCittadinanza2%>" size="20" readonly="true"  />&nbsp;
    </td>
</tr>

<tr>
   <td class="colonna1">Milite esente/assolto</td>
    <td class="colonna2">
      <af:comboBox 
        name="flgMilite"
        classNameBase="input"
        disabled="<%= String.valueOf(!canModify) %>">
        <option value=""  <% if ( "".equalsIgnoreCase(dati1.flgMilite) )  { %>SELECTED="true"<% } %> ></option>
        <option value="S" <% if ( "S".equalsIgnoreCase(dati1.flgMilite) ) { %>SELECTED="true"<% } %> >Sì</option>
        <option value="N" <% if ( "N".equalsIgnoreCase(dati1.flgMilite) ) { %>SELECTED="true"<% } %> >No</option>
      </af:comboBox>
    </td>
    <td class="colonna2">
      <af:comboBox 
        name="flgMilite"
        classNameBase="input"
        disabled="<%= String.valueOf(!canModify) %>"
        >
        <option value=""  <% if ( "".equalsIgnoreCase(dati2.flgMilite) )  { %>SELECTED="true"<% } %> ></option>
        <option value="S" <% if ( "S".equalsIgnoreCase(dati2.flgMilite) ) { %>SELECTED="true"<% } %> >Sì</option>
        <option value="N" <% if ( "N".equalsIgnoreCase(dati2.flgMilite) ) { %>SELECTED="true"<% } %> >No</option>
      </af:comboBox>
    </td>
</tr>


<tr>
    <td class="colonna1">Note</td>
    <td class="colonna2"><af:textArea classNameBase="textarea"  cols="30" rows="4" name="strNote" value="<%=dati1.strNote%>" maxlength="1000"  readonly="<%= String.valueOf(!canModify) %>"  /></td>
    <td class="colonna2"><af:textArea classNameBase="textarea"  cols="30" rows="4" name="strNote" value="<%=dati2.strNote%>" maxlength="1000"  readonly="<%= String.valueOf(!canModify) %>"  /></td>    
</tr>

<%--
<tr>
	<td>
	  <table width="80%" >
	  	<tr align=center>
	  <%if (canScadenzeLav) {%>  
	    	<td><input class="pulsante" type="button" name="scadenziario" value="Scadenze" onClick="javascript:ApriScadenzeLavoratore('<%=dati1.cdnLavoratore%>')"></td>
	  <%}
	  if (canVerificheLav) {%>
	    	<td><input class="pulsante" type="button" name="verifica" value="Verifiche" onClick="javascript:ApriVerificheLavoratore('<%=dati2.cdnLavoratore%>','<%=codCpi%>')"></td>
	  <%}%>		
	  </tr>
	 </table>
</td></tr>
--%>
</table>