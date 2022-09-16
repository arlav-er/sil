<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.afExt.utils.*,
                  java.util.*, 
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  it.eng.sil.util.*, it.eng.sil.security.PageAttribs" %>

      


<%
SourceBean serviceRequest = (SourceBean)request.getAttribute("SERVICE_REQUEST");
SourceBean sb = (SourceBean)request.getAttribute("SOURCE_BEAN");
Vector rows = sb.getAttributeAsVector("AG_LAV.ROWS.ROW");
SourceBean pattoApertoSb = (SourceBean)sb.getAttribute("PATTO_APERTO.ROWS.ROW");
SourceBean row = null;
boolean existsPattoAperto = false;
//BigInteger countObj = (BigInteger)request.getAttribute("COUNT_IMG");
//int count = countObj.intValue();

if((pattoApertoSb != null) && !pattoApertoSb.getAttribute("PRGPATTOLAVORATORE").equals("")){ existsPattoAperto = true; }
%>
<SCRIPT language="javascript">
<!--
  function aggiungiAppuntamento(){
    // Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;
  
    var url = "AdapterHTTP?PAGE=ScadAppuntamentoPage&CDNFUNZIONE=<%=(String)serviceRequest.getAttribute("CDNFUNZIONE")%>";
    url = url + "&cdnLavoratore=<%=(String)serviceRequest.getAttribute("CDNLAVORATORE")%>";
    url = url + "&PAGEPROVENIENZA=AssociazioneAlPattoTemplatePage";
    <%Vector codLstTab = serviceRequest.getAttributeAsVector("COD_LST_TAB");
      for (int i=0;i<codLstTab.size();i++) {
    %>
    url+="&COD_LST_TAB=<%=codLstTab.get(i)%>";
    <%}%>
    url = url + "&statoSezioni=<%=StringUtils.getAttributeStrNotNull(serviceRequest,"statoSezioni")%>";
    url += "&pageChiamante=<%=StringUtils.getAttributeStrNotNull(serviceRequest,"PAGECHIAMANTE")%>";//Per il ritorno dall'associa
    url += "&PATTO_AZIONI=true";
    <%if (serviceRequest.containsAttribute("NONFILTRARE")) {%>    
    //Serve per fare in modo che non si filtri per data corrente nelle azioni, dopo l'associazione
    url+="&NONFILTRARE=<%=serviceRequest.getAttribute("NONFILTRARE")%>";
    <%}%>
    setWindowLocation(url);
  }
//-->
</SCRIPT>
<table width=90% >
    <tr>
        <td>
            <table style="border-collapse:collapse" width="100%">
                <tr style="border: 1 solid #000080; color: #000080;  font-size: 12px;font-weight: normal;text-align: center;vertical-align:middle">
                    <td style="border: 1 solid;border-right: none"  align=center>Rinvio a servizio/appuntamento</td>
                    <td style="border: 1 solid; border-left:none" align=right>        
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <tr><td></td></tr>       
    <tr>
        <td>
            <table width="100%">  

<%   
    for (int i=0;i<rows.size(); i++) {
        row = (SourceBean)rows.get(i);
        /*
         * Non vengono visualizzati gli appuntamenti con data non compresa nel
         * range di validità del patto o accordo.
         */
        if(existsPattoAperto){
          String dataStipula = Utils.notNull(pattoApertoSb.getAttribute("DATSTIPULA"));
          String dataScad =  Utils.notNull(pattoApertoSb.getAttribute("DATSCADCONFERMA"));
          String dataApp =  Utils.notNull(row.getAttribute("DATA"));          
          
          boolean inRange = DateUtils.compare(dataApp,dataStipula)>=0 && (dataScad.equals("") || (!dataScad.equals("") && DateUtils.compare(dataApp,dataScad)<=0));
          if (!inRange) continue;
        }
%>
                <tr>
                    <td width="25" height=25 valign=center>
                        <input type="checkbox" id="I_AG_LAV<%=i%>">
                        <%
                            String cdnLavoratore = row.getAttribute("cdnLavoratore").toString();
                            String codCpi = (String)row.getAttribute("codCpi");
                            String prgAppuntamento = row.getAttribute("prgAppuntamento").toString();
                            String pkeys = cdnLavoratore +","+codCpi +","+prgAppuntamento;
                        %>
                        <script>inputs.push(new Riga('AG_LAV','AG_LAV','<%=pkeys%>',document.getElementById("I_AG_LAV<%=i%>")));</script>
                    </td>
                    <td  class="etichetta2" style="width:78">Data&nbsp;</td>
                    <td class="campo2" style="font-weight:bold;"><%=row.getAttribute("DATA")%></td>
                    <td  class="etichetta2" width="108">Orario&nbsp;</td>
                    <td  class="campo2" style="font-weight:bold"><%=row.getAttribute("ORARIO")%></td>
                    <td class="etichetta2">Durata&nbsp;</td>
                    <td class="campo2" style="font-weight:bold" nowrap><%=Utils.notNull(row.getAttribute("DURATA"))%>&nbsp;min.</td>
                    <td class="etichetta2">Servizio <td class="campo3"><%= Utils.notNull(row.getAttribute("DesServizio"))%></td>
                    <td class="etichetta2">Esito  &nbsp;</td>
                    <td class="campo2" style="font-weight:bold"><%=row.getAttribute("DESESITO")%></td>
                </tr>
    
<%      //count++;
    }  %>
</table>
        </td>
    </tr>
    <tr>
        <td>
            <table width="100%">
                <tr>
                    <% if(rows.size()>0) {%>
                    <td align=right><input type="checkbox" onclick="tutti(this,'AG_LAV')">Seleziona tutti</td> 
                     <%}%>
                    <td width=20></td>
                    <td align=right width="30%">
                      <input type="button" class="pulsante" name="addAppuntamento" value="Aggiungi appuntamento" onClick="javascript:aggiungiAppuntamento();">
                    </td>
                </tr>
                <tr>
                    <td><br/></td>
                </tr>
            </table>
        </td>
    </tr>
    <tr> 
        <td align="left">N.B.: per poter associare un appuntamento al patto bisogna che sia stato specificato il servizio.</td>
        <td align=right></td>
        <td align=right></td>
    </tr>
    <tr> 
        <td align="left">N.B.: possono essere associati al patto/accordo solo servizi/appuntamenti che rientrano nel range di validità del patto/accordo stesso.</td>
        <td align=right></td>
        <td align=right></td>
    </tr>
</table>
    

<%
    //request.setAttribute("COUNT_IMG", BigInteger.valueOf(count));
%>
