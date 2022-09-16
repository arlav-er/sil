<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor, 
                  it.eng.sil.security.User,
                  java.util.*, 
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  it.eng.sil.util.*, 
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.PageAttribs" %>

<%
SourceBean serviceRequest = (SourceBean)request.getAttribute("SERVICE_REQUEST");
SourceBean sb = (SourceBean)request.getAttribute("SOURCE_BEAN");
Vector rows = sb.getAttributeAsVector("OR_PER.ROWS.ROW");
String nonFiltrare = Utils.notNull(serviceRequest.getAttribute("NONFILTRARE"));
SourceBean row = null;
boolean existsPattoAperto = false;
SourceBean pattoApertoSb = (SourceBean)sb.getAttribute("PATTO_APERTO.ROWS.ROW");
if((pattoApertoSb != null) && !pattoApertoSb.getAttribute("PRGPATTOLAVORATORE").equals("")){ existsPattoAperto = true; }
//BigInteger countObj = (BigInteger)request.getAttribute("COUNT_IMG");
//int count = 0; //countObj.intValue();

%>
<SCRIPT language="javascript">
<!--
  function aggiungiAzione() {
        // Se la pagina è già in submit, ignoro questo nuovo invio!
        if (isInSubmit()) return;
  
        var urlpage="";
        urlpage+="AdapterHTTP?";
        urlpage+="cdnLavoratore="+<%=(String)serviceRequest.getAttribute("CDNLAVORATORE")%>+"&";
        urlpage+="CDNFUNZIONE="+<%=(String)serviceRequest.getAttribute("CDNFUNZIONE")%>+"&";
        <%
        List p = new ArrayList();
        Object o = serviceRequest.getAttribute("COD_LST_TAB");
        List cods = null;
        if (o instanceof Vector) cods = (List)o;
        else {cods = new ArrayList(1);cods.add(o);}
        for (int i=0;i<cods.size();i++) {
        %>
            urlpage+="COD_LST_TAB=<%=cods.get(i)%>&";
        <%} %>
            
        urlpage+="statoSezioni=<%=(String)serviceRequest.getAttribute("statoSezioni")%>&";
        urlpage+="PAGECHIAMANTE=<%=serviceRequest.getAttribute("pageChiamante")%>&";
        urlpage+="PAGE=COLLOQUIOPAGE&";
        urlpage+="ONLY_INSERT=1";
        urlpage+="&inserisciNuovo=1";
        urlpage+="&NONFILTRARE=<%=nonFiltrare%>";
        setWindowLocation(urlpage);
  }
//-->
</SCRIPT>
<table width=90% >
    <tr>
        <td>
            <table style="border-collapse:collapse" width="100%">
                <tr style="border: 1 solid #000080; color: #000080;  font-size: 12px;font-weight: normal;text-align: center;vertical-align:middle">
                    <td style="border: 1 solid;border-right: none"  align=center>Azioni/Obiettivi concordati</td>
                    <td style="border: 1 solid; border-left:none" align=right></td>
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
        String dataStipula = Utils.notNull(row.getAttribute("datstipula"));
        String dataScadenza = Utils.notNull(row.getAttribute("datscadconferma"));
        String dataStimata = Utils.notNull(row.getAttribute("datstimata"));
        boolean inRange =  true;
        if (existsPattoAperto) {
        	inRange = DateUtils.compare(dataStimata,dataStipula)>=0 && (dataScadenza.equals("") || (!dataScadenza.equals("") && DateUtils.compare(dataStimata,dataScadenza)<=0));
            if (!inRange) continue;
        }        
%>
                <tr>
                	<td width="25" height=25 valign=center>
                        <input type="checkbox" id="I_OR_PER<%=i%>" >                    
                        <script>inputs.push(new Riga('OR_PER','OR_PER',<%=row.getAttribute("PRGPERCORSO").toString()%>,document.getElementById("I_OR_PER<%=i%>")));</script>
                    </td>                    
                    <td class="etichetta2">Azione <td class="campo3"><%= Utils.notNull(row.getAttribute("AZIONE"))%></td>
                    <td class="etichetta2" nowrap>Entro il <td class="campo3">
                    <%= Utils.notNull(row.getAttribute("DATSTIMATA"))%></td>                                        
                    <td class="etichetta2">Obiettivo <td class="campo3"><%= Utils.notNull(row.getAttribute("AZIONE_RAGG"))%></td>
                    <td class="etichetta2">Esito <td class="campo3"><%= Utils.notNull(row.getAttribute("ESITO"))%></td>
                </tr>
                <tr>
                    <td width="25" height=25 valign=center>&nbsp;</td>
                    <td class="etichetta2">Note </td>
                    <td colspan="3" class="campo3"><%= Utils.notNull(row.getAttribute("strNote"))%></td>
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
                        <td align=right><input type="checkbox" onclick="tutti(this,'OR_PER')">Seleziona tutti</td> 
                         <%}%>
                        <td width=20></td>
                        <td align=right width="30%">
                          <input type="button" class="pulsante" name="addAzione" value="Aggiungi azioni" onClick="javascript:aggiungiAzione();">
                        </td>
                    </tr>    
                </table>
            </td>
        </tr>
		<tr> 
        <td align="left">N.B.: possono essere associati al patto/accordo solo azioni/obiettivi che rientrano nel range di validità del patto/accordo stesso.</td>
        <td align=right></td>
        <td align=right></td>
    </tr>
</table>
    

<%!
   private String formatta(String d) {
        java.util.StringTokenizer st = new java.util.StringTokenizer(d, "/");
        String gg = (String)st.nextElement();
        String mm = (String)st.nextElement();
        String aa = (String)st.nextElement();
        return aa+mm+gg;
   }
%>