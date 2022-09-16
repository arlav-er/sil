<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.util.*,
                  it.eng.sil.util.amministrazione.impatti.Controlli,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.PageAttribs,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
    SourceBean row          = null;
    String cdnLavoratore    = null;  
    String codStatoOcc      = null;
    String indennizzo       = null;
    String flg40790         = null;
    String flgCat181DonnaReinsLav = null;
    String dataAnzDisoc     = null;
    String codMonoCat181Eta           = null;
    BigDecimal mesiAnzPrec      = null;
    String codMonoCalcAnzPrec   = null;
    String datcalcoloanzianita = null;
    String datcalcolomesisosp = null;
    BigDecimal numMesiSospPrec  = null;
    String pensionato       = null;
    BigDecimal mesiAnz      = null;
    BigDecimal numMesiSosp  = null;
    String codMonoCat181LunDurata = null;
    String disoccInoccText = "";
    String totNumMesiSosp = null;
    String totNumMesiAnz = null;
    String cpiTit           = null;
    boolean visualizzazione = false;
    //boolean  donnaInReinserimento = false;
    int anniInattivita = -1;
    String strVuota = "";
    
    String htmlStreamTop = StyleUtils.roundTopTable(true);
    String htmlStreamBottom = StyleUtils.roundBottomTable(true);
    cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
    
    row = (SourceBean)serviceResponse.getAttribute("M_GetInfoStatoOccupaz.ROWS.ROW");
    if(row != null)  { 
      indennizzo      = (String)      row.getAttribute("FLGINDENNIZZATO");
      pensionato      = (String)      row.getAttribute("FLGPENSIONATO");
      numMesiSosp     = (BigDecimal)  row.getAttribute("NUMMESISOSP");
      if (numMesiSosp == null) numMesiSosp = new BigDecimal(0);
      numMesiSospPrec = (BigDecimal)  row.getAttribute("NUMMESISOSP");
      if (numMesiSospPrec == null) numMesiSospPrec = new BigDecimal(0);
      totNumMesiSosp  = String.valueOf(numMesiSospPrec.add(numMesiSosp));
      codStatoOcc     = (String)      row.getAttribute("CODSTATOOCCUPAZ");
      dataAnzDisoc    = (String)      row.getAttribute("DATANZIANITADISOC");
      mesiAnz         = (BigDecimal)  row.getAttribute("NUMMESIANZIANITA");
      if (mesiAnz == null) mesiAnz = new BigDecimal(0);
      mesiAnzPrec         = (BigDecimal)  row.getAttribute("NUMANZIANITAPREC297");
      if (mesiAnzPrec == null) mesiAnzPrec = new BigDecimal(0);
      totNumMesiAnz = String.valueOf(mesiAnzPrec.add(mesiAnz).subtract(new BigDecimal(totNumMesiSosp)));
      codMonoCalcAnzPrec  = (String)      row.getAttribute("CODMONOCALCOLOANZIANITAPREC297");
      datcalcolomesisosp = (String)      row.getAttribute("dacalcolomesisosp");
      datcalcoloanzianita = (String)      row.getAttribute("datcalcoloanzianita");
      codMonoCat181LunDurata = (String)      row.getAttribute("CODMONOCAT181LUNGADURATA");
      
      if (codMonoCat181LunDurata != null && codMonoCat181LunDurata.equalsIgnoreCase("D"))
        disoccInoccText = "Disoccupato di lunga durata (Min: nessuna corrispondenza)";
      else 
        if (codMonoCat181LunDurata != null && codMonoCat181LunDurata.equalsIgnoreCase("I"))
          disoccInoccText = "Inoccupato di lunga durata (Min: nessuna corrispondenza)";
      
      //disoccInoccText = (codMonoCat181LunDurata !=null) ? codMonoCat181LunDurata : "";
      flg40790 = (String)      row.getAttribute("FLG40790");
      flgCat181DonnaReinsLav = (String)      row.getAttribute("FLGCAT181DONNAREINSLAV");
      codMonoCat181Eta = (String)      row.getAttribute("CODMONOCAT181ETA");
      pensionato = (String)row.getAttribute("FLGPENSIONATO");
      //cpiTit = (String) row.getAttribute("CODCPITIT");
  }
%>
<html>
<head>
  <title>Stato Occupazionale</title>
  <link rel="stylesheet" href=" ../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/"/>
</head>
<body class="gestione" onload="rinfresca()">
  <br/>
  <table class="main" width="96%">
    <tr><td class="titolo" colspan="5"><p class="titolo">Stato Occupazionale storicizzato in fase di stipula della DID</p></td></tr>
  </table>
  <br/>    
  <%out.print(htmlStreamTop);%>
    <table class="main" width="96%">
      <tr>
        <td class="etichetta">Stato occupazionale</td>
        <td class="campo">
          <af:comboBox classNameBase="input" name="codStatoOcc" moduleName="M_GETDESTATOOCC" selectedValue="<%=codStatoOcc%>" addBlank="true"
                  title="Stato occupazionale" disabled="true" />
        </td>
      </tr>
      <tr>
        <td class="etichetta" align="top">Anzianità di disoccupazione dal </td>
        <td colspan="5">
          <table border="0" width="100%">
            <tr>
              <td width="25%">
                  <af:textBox classNameBase="input" type="date" name="datAnzianitaDisoc" value="<%=dataAnzDisoc%>" readonly="true" size="11" maxlength="10"/>
              </td>               
            </tr>
          </table>              
        </td>
      </tr>
      <tr><td>
      <!--   17/03/2005 :i campi relativi alla sospensione e all'anzianità vengono nascosti
      		 perchè in fase di stipula della did sono uguali a zero (0).Se viene
      		 registrata una did vecchia (data precedente a oggi) si potrebbe 
      		 storicizzare l'anzianità al netto della sospensione già maturata a oggi.
      		 (eventualmente va rivista la parte nascosta)
      -->
      <div id="sezioneMesi" style="display:none">
      <table>
      <tr>
        <td class="etichetta">mesi sosp.</td>
        <td colspan=3 class="campo">
          <table border="0" width="100%" class="main">
            <tr>
                <% if ( (datcalcolomesisosp!=null) && (datcalcoloanzianita!=null) && !dataAnzDisoc.equals(datcalcolomesisosp) && !dataAnzDisoc.equals(datcalcoloanzianita) ) {
                       visualizzazione = true;
                %>
                <td class="etichetta" nowrap>
                  prima del&nbsp;
                  <af:textBox classNameBase="input" type="date" name="datcalcolomesisosp" value="<%=datcalcolomesisosp%>"
                              readonly="true" size="11" maxlength="10"/>
                </td>
                <td nowrap>
                  <af:textBox classNameBase="input" type="number" name="numMesiSosp" value="<%=(numMesiSospPrec!=null)?numMesiSospPrec.toString():strVuota%>"
                              readonly="true" size="4" maxlength="3"/>
                </td>
                <td class="etichetta" nowrap>                            
                  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;+&nbsp;succ.
                </td>
              <%}%>
              <td class="campo" nowrap>
                <af:textBox classNameBase="input" type="number" name="numMesiSospSucc" value="<%=numMesiSosp!=null?numMesiSosp.toString():strVuota%>" 
                          readonly="true"  size="4" maxlength="3"/>
              </td>
              <td class="etichetta">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
              <td class="campo">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
              <td nowrap>
                =
              </td>
              <td class="campo" nowrap>
                <af:textBox classNameBase="input" type="number" name="totNumMesiSosp" value="<%=totNumMesiSosp%>" 
                          readonly="true" size="4" maxlength="3"/>
              </td>
              <% if ( !visualizzazione ) {%>
                  <td class="etichetta">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                  <td class="campo">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                  <td class="etichetta">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                  <td class="campo">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                  <td class="etichetta">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                  <td class="campo">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
              <% } %>
            </tr>
          </table>
        </td>
      </tr>	
      <tr>
         <td class="etichetta">Mesi anzianità</td>
         <td colspan="3" nowrap>
           <table border="0" width="100%" class="main">
           <tr>
            <% if ( (datcalcolomesisosp!=null) && (datcalcoloanzianita!=null) && !dataAnzDisoc.equals(datcalcolomesisosp) && !dataAnzDisoc.equals(datcalcoloanzianita) ) {
                  visualizzazione = true;
            %>
              <td class="etichetta" nowrap>
                  prima del&nbsp;
                    <af:textBox classNameBase="input" type="date" name="datcalcoloanzianita" value="<%=datcalcoloanzianita%>"
                                readonly="true"  size="11" maxlength="10"/>
              </td>
              <td nowrap>
                <af:textBox classNameBase="input" type="number" name="numAnzianitaPrec297" value="<%=mesiAnzPrec!=null?mesiAnzPrec.toString():strVuota%>"
                            readonly="true" size="4" maxlength="38"/>
                &nbsp;
                <af:comboBox classNameBase="input" name="CODMONOCALCOLOANZIANITAPREC297" addBlank="false" disabled="true">
                  <OPTION value="" <%if (codMonoCalcAnzPrec == null) out.print("SELECTED=\"true\"");%>></OPTION>
                  <OPTION value="A" <%if ((codMonoCalcAnzPrec!=null) && codMonoCalcAnzPrec.equalsIgnoreCase("A")) out.print("SELECTED=\"true\"");%>>A</OPTION>
                  <OPTION value="M" <%if ((codMonoCalcAnzPrec!=null) && codMonoCalcAnzPrec.equalsIgnoreCase("M")) out.print("SELECTED=\"true\"");%>>M</OPTION>
                </af:comboBox>                    
              </td>
              <td class="etichetta" nowrap>
                &nbsp;&nbsp;+&nbsp;succ.
              </td>
            <%}%>
            <td class="campo" nowrap>
              <af:textBox classNameBase="input" type="text" name="numMesiAnzianita" value="<%=mesiAnz!=null?mesiAnz.toString():strVuota%>"
                          readonly="true" size="4" maxlength="3"/>
        
            </td>
            <td class="etichetta" nowrap>
              -&nbsp;sosp.
            </td>
            <td class="campo" nowrap>
              <af:textBox classNameBase="input" type="number" name="totNumMesiSosp" value="<%=totNumMesiSosp%>"
                          readonly="true" size="4" maxlength="3"/>
            </td>
            <td nowrap>
              =
            </td>
            <td class="campo" nowrap>
              <af:textBox classNameBase="input" type="number" name="totNumMesiAnz" value="<%=totNumMesiAnz%>" 
                          readonly="true" size="4" maxlength="3"/>
            </td>
            <% if ( !visualizzazione ) {%>
                <td class="etichetta">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                <td class="campo">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                <td class="etichetta">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                <td class="campo">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                <td class="etichetta">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                <td class="campo">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
            <% } %>
           </tr>
           </table>
         </td>
      </tr>
      </table>
      </div>
      </td></tr>
            <tr>
              <td>&nbsp;</td>
              <td class="campo">
                <%if(flg40790 != null && flg40790.equals("S"))  { %>
                  <af:textBox classNameBase="input" type="text" name="tipoAnzianita" readonly="true"
                               value="soggetto alla legge 407/90" size="40"/>
                <% } %>
             </td>
            </tr>
        </td>
      </tr>

      <tr><td colspan=4>&nbsp;</td></tr>
      <tr>
          <td align="center" colspan="4">
            <div class="sezione2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Categoria D.lgs. 181</div>
          </td>
      </tr>
      <%   if (codMonoCat181Eta != null) {%>       
      <tr>
          <td></td>
          <td colspan=3 class="campo">
              <af:comboBox classNameBase="input" name="codMonoCat181Eta" addBlank="false" disabled="true">
                <OPTION value="A" <%if ((codMonoCat181Eta!=null) && codMonoCat181Eta.equalsIgnoreCase("A")) out.print("SELECTED=\"true\"");%>>Adolescente (Min: Adolescenti)</OPTION>
                <OPTION value="G" <%if ((codMonoCat181Eta!=null) && codMonoCat181Eta.equalsIgnoreCase("G")) out.print("SELECTED=\"true\"");%>>Giovane (Min: Giovani)</OPTION>
              </af:comboBox>
          </td>
      </tr>
      <%   }%>
      <% if (disoccInoccText.length()>0) { %>
      <tr>
          <td></td>
          <td colspan=3 class="campo">
                  <af:textBox classNameBase="input" type="text" name="tipoAnzianita" readonly="true" 
                                       value="<%= disoccInoccText %>" size="40"/>
          </td>
      </tr>
      <%}%>

      <%   if (flgCat181DonnaReinsLav != null && flgCat181DonnaReinsLav.equals("S")) {%>        
      <tr>
          <td></td>
          <td colspan=3 class="campo"><af:textBox classNameBase="input" type="text" name="donnaInserimento" readonly="true" 
                                   value="Donna in reinserimento lavorativo (Min: Donna in reinserimento)" size="40"/> 
          </td>
      </tr>
      <%   } %>
      <tr><td colspan=4><br></td></tr>

      <tr><td align="center" colspan="4"><div class="sezione2"></div></td></tr>
      <tr>
          <td class="etichetta">Pensionato</td>
          <td colspan="3" class="campo">
              <af:comboBox classNameBase="input" name="flgPensionato" addBlank="false" onChange="fieldChanged();" disabled="true">
                  <OPTION value=""  <%if (pensionato == null) out.print("SELECTED=\"true\"");%>></OPTION>
                  <OPTION value="S" <%if (pensionato != null && pensionato.equalsIgnoreCase("S")) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
                  <OPTION value="N" <%if (pensionato != null && pensionato.equalsIgnoreCase("N")) out.print("SELECTED=\"true\"");%>>No</OPTION>
              </af:comboBox>     
          </td>
      </tr>
      <tr>
        <td colspan="4">
          <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="javascript:window.close();">
        </td>
      </tr>
    </table>
  <%out.print(htmlStreamBottom);%>

</body>
</html>
