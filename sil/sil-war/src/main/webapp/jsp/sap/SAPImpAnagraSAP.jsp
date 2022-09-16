<%@ page import="it.eng.sil.module.sap.SelectSAP" %>
<%@ page import="com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult" %>

<%
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    Date dtmDataNascita = sapPortaleLav.getDataNascita().getTime();
    String strDataNascita = sdf.format(dtmDataNascita);
    
%>

<table class="sapTabella">

  <tr>
    <td class="sapTitoloSezione" colspan="2">
      <input id="btnAnagraSAP" type="image" src="../../img/aperto.gif" onClick="toggle('btnAnagraSAP', 'tabAnagraSAP');toggle('btnAnagraSIL', 'tabAnagraSIL');return false; return false" />
      <font size="2">Dati Anagrafici</font>
    </td>
  </tr>

</table>

<table class="sapTabella" id="tabAnagraSAP">

  <tr>
    <td class="etichetta, grassetto, indenta">Codice fiscale</td>
    <td id="lbl.frmAnagra.codiceFiscale" align="left" class="inputView"><%=Utils.notNull(strCodiceFiscale)%></td>
    <td id="edt.frmAnagra.codiceFiscale" style="display: none" align="left" class="inputView">
    	<af:textBox type="text" name="strCodiceFiscale" title="Codice fiscale" required="true" value="<%=Utils.notNull(strCodiceFiscale)%>" />
    </td>
  </tr>
  <tr>
    <td class="etichetta, grassetto, indenta">Cognome</td>
    <td id="lbl.frmAnagra.cognome" align="left" class="inputView"><%=Utils.notNull(sapPortaleLav.getCognome())%></td>
    <td id="edt.frmAnagra.cognome" style="display: none" align="left" class="inputView">
    	<af:textBox type="text" name="strCognome" title="cognome" required="true" value="<%=Utils.notNull(sapPortaleLav.getCognome())%>" />
    </td>
  </tr>
  <tr>
    <td class="etichetta, grassetto, indenta">Nome</td>
    <td id="lbl.frmAnagra.nome" align="left" class="inputView"><%=Utils.notNull(sapPortaleLav.getNome())%></td>
    <td id="edt.frmAnagra.nome" style="display: none" align="left" class="inputView">
    	<af:textBox type="text" name="strNome" title="nome" required="true" value="<%=Utils.notNull(sapPortaleLav.getNome())%>" />
    </td>
  </tr>
  <tr>
    <td class="etichetta, grassetto, indenta">Sesso</td>
    <td id="lbl.frmAnagra.sesso" align="left" class="inputView"><%=strSesso%></td>
    <td id="edt.frmAnagra.sesso" style="display: none" align="left" class="inputView">
      <af:comboBox name="strSesso" title="sesso" required="true" >
          <OPTION value="M" <%if (strSesso.equals("M")) out.print("SELECTED=\"true\"");%>>M</OPTION>
          <OPTION value="F" <%if (strSesso.equals("F")) out.print("SELECTED=\"true\"");%>>F</OPTION>     
      </af:comboBox>      
    </td>
  </tr>
  <tr>
    <td class="etichetta, grassetto, indenta">Data di nascita</td>
    <td id="lbl.frmAnagra.dataNascita" align="left" class="inputView"><%=strDataNascita%></td>
    <td id="edt.frmAnagra.dataNascita" style="display: none" align="left" class="inputView"><af:textBox type="text" name="datNasc" title="data di nascita" required="false" value="<%=strDataNascita%>" /></td>
  </tr>
  <tr>
    <td class="etichetta, grassetto, indenta">Comune di nascita</td>
    <td id="lbl.frmAnagra.comuneNascita" align="left" class="inputView"><%=Utils.notNull(sapPortaleLav.getDescComuneNascita())%></td>
    <td id="edt.frmAnagra.comuneNascita" style="display: none" align="left" class="inputView"><af:textBox type="text" name="strComuneNascita" title="comune di nascita" required="false" value="<%=Utils.notNull(sapPortaleLav.getDescComuneNascita())%>" /></td>
    <input type="hidden" name="codComNas" value="<%=Utils.notNull(sapPortaleLav.getCodComuneNascita())%>"/>
  </tr>
  <tr>
    <td class="etichetta, grassetto, indenta">Nazione di cittadinanza</td>
    <td id="lbl.frmAnagra.cittadinanza" align="left" class="inputView"><%=Utils.notNull(sapPortaleLav.getDescNazioneCittadinanza())%></td>
    <td id="edt.frmAnagra.cittadinanza" style="display: none" align="left" class="inputView"><af:textBox required="true" type="text" name="strCittadinanza" title="cittadinanza" required="false" value="<%=Utils.notNull(sapPortaleLav.getDescNazioneCittadinanza())%>" /></td>
    <input type="hidden" name="codCittadinanzaHid" value="<%=Utils.notNull(sapPortaleLav.getCodNazioneCittadinanza())%>"/>
  </tr>
  
<%
String strIndirizzoDomicilio = Utils.notNull(sapPortaleLav.getIndirizzoDomicilio()); 
String strLocalitaDomicilio = Utils.notNull(sapPortaleLav.getDescComuneDomicilio());
String codComDomicilio = Utils.notNull(sapPortaleLav.getCodComuneDomicilio());
%>  
  
  <tr>
    <td class="etichetta, grassetto, indenta">Indirizzo domicilio</td>
    <td id="lbl.frmAnagra.indirizzoDom" align="left" class="inputView"><%=strIndirizzoDomicilio%></td>
    <td id="edt.frmAnagra.indirizzoDom" style="display: none" align="left" class="inputView">
    	<af:textBox type="text" name="strIndirizzoDom" title="indirizzo domicilio" required="false" value="<%=strIndirizzoDomicilio%>" />
    </td>
  </tr>
  <tr>
    <td class="etichetta, grassetto, indenta">Comune domicilio
    <td id="lbl.frmAnagra.localitaDom" class="inputView"><%=strLocalitaDomicilio%></td>
    <td id="edt.frmAnagra.localitaDom" style="display: none" align="left" class="inputView">
    	<af:textBox type="text" name="strLocalitaDom" title="località domicilio" required="false" value="<%=strLocalitaDomicilio%>"/>
    </td>
    <input type="hidden" name="codComDom" value="<%=codComDomicilio%>"/>
  </tr>

<%
String strCAPDomicilio = "";
String codCPIDomicilio = "";
try {
  SelectSAP sltCAPCPI = new SelectSAP("SELECT_SAP_CAP_CPI_COMUNE");
  sltCAPCPI.parametro("codCom", codComDomicilio);
  ScrollableDataResult sdrCAPCPI = sltCAPCPI.esegui();
  if (sdrCAPCPI != null && sdrCAPCPI.hasRows()) {
	  strCAPDomicilio = sdrCAPCPI.getDataRow(1).getColumn("strCAP").getStringValue();
	  codCPIDomicilio = sdrCAPCPI.getDataRow(1).getColumn("codCPI").getStringValue();
  }  
  sltCAPCPI.chiudi();
} catch (Exception e) {
  SelectSAP.getLogger().error(e.getMessage());
}
%>

  <tr>
    <td class="etichetta, grassetto, indenta">Cap domicilio</td>
    <td id="lbl.frmAnagra.capDom" class="inputView"><%=strCAPDomicilio%></td>
    <td id="edt.frmAnagra.capDom" style="display: none" align="left" class="inputView">
    	<af:textBox type="text" name="strCapDom" title="CAP domicilio" required="false" value="<%=strCAPDomicilio%>" />
    </td>
    <input type="hidden" name="codCPIDom" value="<%=codCPIDomicilio%>"/>
  </tr>

<%
String strIndirizzoResidenza = Utils.notNull(sapPortaleLav.getIndirizzoResidenza()); 
String strLocalitaResidenza = Utils.notNull(sapPortaleLav.getDescComuneResidenza());
String codComResidenza = Utils.notNull(sapPortaleLav.getCodComuneResidenza());
if ("".equals(codComResidenza)) {
	strIndirizzoResidenza = strIndirizzoDomicilio; 
	strLocalitaResidenza = strLocalitaDomicilio;
	codComResidenza = codComDomicilio;	
}
%>
  
  <tr>
    <td class="etichetta, grassetto, indenta">Indirizzo residenza</td>
    <td id="lbl.frmAnagra.indirizzoRes" align="left" class="inputView"><%=strIndirizzoResidenza%></td>
    <td id="edt.frmAnagra.indirizzoRes" style="display: none" align="left" class="inputView"><af:textBox type="text" name="strIndirizzoRes" title="indirizzo residenza" required="false" value="<%=strIndirizzoResidenza%>" /></td>
  </tr>
  <tr>
    <td class="etichetta, grassetto, indenta">Comune residenza</td>
    <td id="lbl.frmAnagra.localitaRes" class="inputView"><%=strLocalitaResidenza%></td>
    <td id="edt.frmAnagra.localitaRes" style="display: none" align="left" class="inputView"><af:textBox type="text" name="strLocalitaRes" title="località residenza" required="false" value="<%=strLocalitaResidenza%>" /></td>
    <input type="hidden" name="codComRes" value="<%=codComResidenza%>"/>
  </tr>
<%
String strCAPResidenza = "";
String codCPIResidenza = "";
try {
  SelectSAP sltCAPCPI = new SelectSAP("SELECT_SAP_CAP_CPI_COMUNE");
  sltCAPCPI.parametro("codCom", codComResidenza);
  ScrollableDataResult sdrCAPCPI = sltCAPCPI.esegui();
  if (sdrCAPCPI != null && sdrCAPCPI.hasRows()) {
	  strCAPResidenza = sdrCAPCPI.getDataRow(1).getColumn("strCAP").getStringValue();
	  codCPIResidenza = sdrCAPCPI.getDataRow(1).getColumn("codCPI").getStringValue();
  }  
  sltCAPCPI.chiudi();
} catch (Exception e) {
  SelectSAP.getLogger().error(e.getMessage());
}
%>
  <tr>
    <td class="etichetta, grassetto, indenta">Cap residenza</td>
    <td id="lbl.frmAnagra.capRes" class="inputView"><%=strCAPResidenza%></td>
    <td id="edt.frmAnagra.capRes" style="display: none" align="left" class="inputView"><af:textBox type="text" name="strCapRes" title="CAP residenza" required="false" value="<%=strCAPResidenza%>" /></td>
    <input type="hidden" name="codCPIRes" value="<%=codCPIResidenza%>"/>
  </tr>

  <tr>
    <td class="etichetta, grassetto, indenta">Cellulare</td>
    <td id="lbl.frmAnagra.cell" align="left" class="inputView"><%=Utils.notNull(sapPortaleLav.getCellulare())%></td>
    <td id="edt.frmAnagra.cell" style="display: none" align="left" class="inputView"><af:textBox type="text" name="strCell" title="cellulare" required="false" value="<%=Utils.notNull(sapPortaleLav.getCellulare())%>" /></td>
  </tr>
  <tr>
    <td class="etichetta, grassetto, indenta">E-mail</td>
    <td id="lbl.frmAnagra.email" align="left" class="inputView"><%=Utils.notNull(sapPortaleLav.getEmail())%></td>
    <td id="edt.frmAnagra.email" style="display: none" align="left" class="inputView"><af:textBox type="text" name="strEmail" title="email" required="false" value="<%=Utils.notNull(sapPortaleLav.getEmail())%>" /></td>
  </tr>
  <tr>
    <td class="etichetta, grassetto, indenta">Fax</td>
    <td id="lbl.frmAnagra.fax" align="left" class="inputView"><%=Utils.notNull(sapPortaleLav.getFax())%></td>
    <td id="edt.frmAnagra.fax" style="display: none" align="left" class="inputView"><af:textBox type="text" name="strFax" title="fax" required="false" value="<%=Utils.notNull(sapPortaleLav.getFax())%>" /></td>
  </tr>
    <tr>
      <td class="etichetta">&nbsp;</td>
      <td class="inputView">&nbsp;</td>
    </tr>
  
</table>


