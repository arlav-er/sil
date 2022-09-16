<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.afExt.utils.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  it.eng.sil.util.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%   Vector rows = serviceResponse.getAttributeAsVector("M_GETPATTOLAV.ROWS.ROW");
    Vector rowsStati = serviceResponse.getAttributeAsVector("M_STATOATTOPATTO.ROWS.ROW");
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>
Patti aperti
</title>
<af:linkScript path="../../js/"/>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<script>
<!--
function associa(tipologia, dataStipula, stato, prgPatto) {
/*
        window.opener.document.form1.PRG_PATTO_LAVORATORE.value=prgPatto;
        window.opener.document.form1.TipoPatto_TMP.value=tipologia;
        window.opener.document.form1.DATSTIPULA_TMP.value=dataStipula;
        window.opener.document.form1.StatoPatto_TMP.value=stato;*/
        if (window.opener.associazioneAlPattoPossibile!=null ){
        	msgPatto = window.opener.associazioneAlPattoPossibile();
        	if (msgPatto !=null ) {
        		alert(msgPatto);
        		return;
        	}
        }
        window.opener.setPattoAssociato(tipologia,dataStipula,stato,prgPatto);
       // window.opener.showSezionePatto(true);
        window.close();
}
//-->
</script>
</head>
<body>
<br/>
<center><input type="button" name="chiudi" value="chiudi" class="pulsante" onClick="javascript:window.close();"/></center>
<br/>
<TABLE class="lista" align="center">
    <TR class="lista">
        <TH class="lista">&nbsp;</TH>
        <TH class="lista">&nbsp;Data stipula&nbsp;</TH>
        <TH class="lista">&nbsp;Data scad. conf.&nbsp;</TH>
        <TH class="lista">&nbsp;Tipologia&nbsp;</TH>
        <TH class="lista">&nbsp;Stato patto&nbsp;</TH>
    </TR>


<%
    String dataStipula  =null;    
    String dataScadenza=null;
    String tipologia=null;
    String stato =null;
    String codStato = null;
    BigDecimal prgPatto =null;
    String dataUltimoProt = null;
    String strDescrizionePatto = null;
    
    for (int i=0;i<rows.size();i++) {
        stato=null;
        SourceBean row= (SourceBean)rows.get(i);
        codStato = (String)row.getAttribute("CODSTATOATTO");
        for (int j=0;j<rowsStati.size() && stato==null;j++) {
            SourceBean sb = (SourceBean)rowsStati.get(j);
            if (sb.getAttribute("codice").equals(codStato))
                stato = (String)sb.getAttribute("descrizione");
        }
        if (stato!=null && stato.length()>30) stato = stato.substring(0,28)+"...";
		dataUltimoProt  = StringUtils.getAttributeStrNotNull(row, "DATULTIMOPROTOCOLLO");
		if(dataUltimoProt!=null && !dataUltimoProt.equals("")) stato += " / " + dataUltimoProt;
        tipologia= (String)row.getAttribute("FLGPATTO297");
        strDescrizionePatto=(String)row.getAttribute("strdescrizionepatto");
        dataScadenza= (String)row.getAttribute("DATSCADCONFERMA");
        dataStipula= (String)row.getAttribute("DATSTIPULA");
        prgPatto =  (BigDecimal)row.getAttribute("PRGPATTOLAVORATORE");
        if (strDescrizionePatto!=null && !strDescrizionePatto.equals("")) {
        	tipologia = strDescrizionePatto;	
        }
        else {
        	if (tipologia!=null) {
            	tipologia=tipologia.equalsIgnoreCase("S")?"Patto 150":"Accordo Generico";	
            }	
        }
%>
        <tr class="lista">
                <td class="lista"><a href="#" onclick="javascript:associa('<%= Utils.notNull(tipologia) %>','<%= Utils.notNull(dataStipula) %>','<%=Utils.notNull(stato)  %>','<%=Utils.notNull(prgPatto)%>')"><IMG name="image" border="0"  src="../../img/add.gif" alt="Seleziona"/></a></td>
                <td class="lista"><%=Utils.notNull(dataStipula)%></td>
                <td class="lista"><%=Utils.notNull(dataScadenza)%></td>
                <td class="lista"><%=Utils.notNull(tipologia)%></td>
                <td class="lista"><%=Utils.notNull(stato)%></td>
        </tr>
<%
    }
%>

</table>
</body>
</html>
