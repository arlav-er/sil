<!-- @author: Stefania Orioli -->
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
    com.engiweb.framework.configuration.ConfigSingleton,
    
    com.engiweb.framework.error.EMFErrorHandler,
    it.eng.afExt.utils.*,
    it.eng.sil.security.User,
    it.eng.sil.util.*,
    java.lang.*,
    java.text.*,
    java.math.*,
    java.sql.*,
    oracle.sql.*,
    java.util.*"%>
    
<%@ taglib uri="aftags" prefix="af"%>
<%
    String prgAzienda        = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGAZIENDA");
    String prgUnita          = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGUNITA");
    String strRagioneSociale = StringUtils.getAttributeStrNotNull(serviceRequest, "STRRAGIONESOCIALE");
    strRagioneSociale = StringUtils.replace(strRagioneSociale,"'","\\'");
    String strPartitaIva     = StringUtils.getAttributeStrNotNull(serviceRequest, "STRPARTITAIVA");
    String strIndirizzo      = StringUtils.getAttributeStrNotNull(serviceRequest, "STRINDIRIZZO");
    strIndirizzo = StringUtils.replace(strIndirizzo,"'","\\'");
    String comuneAz          = StringUtils.getAttributeStrNotNull(serviceRequest, "COMUNE_AZ");
    comuneAz = StringUtils.replace(comuneAz,"'","\\'");
    String strTel            = StringUtils.getAttributeStrNotNull(serviceRequest, "STRTEL");
    String inizio            = StringUtils.getAttributeStrNotNull(serviceRequest, "DATINIZIOMOV");
    String fine              = StringUtils.getAttributeStrNotNull(serviceRequest, "DATFINEMOV");
    String prgMOV            = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGMovimento");
    String mans              = StringUtils.getAttributeStrNotNull(serviceRequest, "MANSIONE");
    mans = StringUtils.replace(mans,"'","\\'");
    String codMans              = StringUtils.getAttributeStrNotNull(serviceRequest, "CODMANSIONE");
    String dataInizioMob     = DateUtils.giornoSuccessivo(fine);
    SourceBean sbComExCassaMezz = (SourceBean)serviceResponse.getAttribute("M_MOB_MESIMOBEXCASSAMEZZ.ROWS.ROW");
    String codGrado = StringUtils.getAttributeStrNotNull(serviceRequest, "CODGRADO");
    String numOreSett = StringUtils.getAttributeStrNotNull(serviceRequest, "NUMORESETTIMANALI");
    String livello = StringUtils.getAttributeStrNotNull(serviceRequest, "NUMLIVELLO");
    livello = StringUtils.replace(livello,"'","\\'");
    String codCCNL = StringUtils.getAttributeStrNotNull(serviceRequest, "CODCCNL");
    //String strDescCCNL = StringUtils.getAttributeStrNotNull(serviceRequest, "DESCCCNL");
    //strDescCCNL = StringUtils.replace(strDescCCNL,"'","\\'");
    
    BigDecimal numMesiComExCassaMezz = null;
    String strMesiComExCassaMezz = "0";
    if (sbComExCassaMezz != null) {
    	strMesiComExCassaMezz = sbComExCassaMezz.containsAttribute("NUMMESIMOBEXCASSAMEZZ")?sbComExCassaMezz.getAttribute("NUMMESIMOBEXCASSAMEZZ").toString():"0";
    	if (strMesiComExCassaMezz.equals("")) {
			strMesiComExCassaMezz = "0";
		}
    	numMesiComExCassaMezz = new BigDecimal(strMesiComExCassaMezz);
    }
    BigDecimal numMesi = new BigDecimal(serviceResponse.getAttribute("M_MOB_DURATAMOBILITA.DURATA_MOB.MESI").toString());
    String dataFineMob = DateUtils.aggiungiMesi(dataInizioMob, numMesi.intValue(), -1);
	String dataFineMaxDiff = DateUtils.aggiungiMesi(dataInizioMob, (2 * numMesi.intValue()), -1);
    if (!strMesiComExCassaMezz.equals("0")) {
    	dataFineMob = DateUtils.aggiungiMesi(dataFineMob, numMesiComExCassaMezz.intValue(), 0);
		dataFineMaxDiff = DateUtils.aggiungiMesi(dataFineMaxDiff, numMesiComExCassaMezz.intValue(), 0);
    }
    
%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<HEAD>
<af:linkScript path="../../js/" />
<script type="text/javascript">
  function aggiorna(prgAzienda,prgUnita,strRagioneSociale,strPIva,strInd,com,tel,InizMov,
                    FineMov,
                    //mansione,
                    pMovim,datInizioMob, datFineMob, datFineMaxDiff, codMansione,
                    codGrado, livello, codCCNL, numOreSettimanali
                    //, strDescCCNL
                    )
  { window.opener.document.Frm1.PRGAZIENDA.value = prgAzienda;
    window.opener.document.Frm1.PRGUNITA.value   = prgUnita;
    window.opener.document.Frm1.Ragione.value    = strRagioneSociale;
    window.opener.document.Frm1.Indirizzo.value  = strInd;
    window.opener.document.Frm1.Comune.value     = com;
    window.opener.document.Frm1.PIva.value       = strPIva;
    window.opener.document.Frm1.strTel.value     = tel;
    window.opener.document.Frm1.datInizMov.value     = InizMov;
    window.opener.document.Frm1.datFineMov.value     = FineMov;
    window.opener.document.Frm1.flgIndennita.value   = "";
    window.opener.document.Frm1.dataInizioIndenn.value   = "";
    window.opener.document.Frm1.dataFineIndenn.value   = "";
    var objIndennita = window.opener.document.getElementById('opzioni_indennita');
    objIndennita.style.display = "none";
    //window.opener.document.Frm1.DESCMANSIONE.value   = mansione;
    window.opener.document.Frm1.CODMANSIONE.value    = codMansione;
    window.opener.document.Frm1.codCCNL.value = codCCNL;
    //window.opener.document.Frm1.strCCNL.value = strDescCCNL; 
    window.opener.document.Frm1.codGrado.value = codGrado;
    window.opener.document.Frm1.strLivello.value = livello; 
    window.opener.document.Frm1.prgMovimento.value   = pMovim;
    window.opener.document.Frm1.datInizio.value      = datInizioMob;
    window.opener.document.Frm1.datFine.value        = datFineMob;
    window.opener.document.Frm1.datFineOrig.value    = datFineMob;
    window.opener.document.Frm1.datMaxDiff.value     = datFineMaxDiff;
    window.opener.document.Frm1.numOreSett.value = numOreSettimanali;
    //quando associo il movimento alla mobilità devo 
    //rendere non editabili i seguenti campi
    var objCercaAzienda = window.opener.document.getElementById('sezione_azienda');
    objCercaAzienda.style.display = "none";
  	var objDate = window.opener.document.getElementById('sezione_valorizza_date');
    objDate.style.display = "none";
    
    window.opener.document.Frm1.datInizMov.readOnly = true;
    window.opener.document.Frm1.datInizMov.className="inputView";
    window.opener.document.Frm1.datFineMov.readOnly = true;
    window.opener.document.Frm1.datFineMov.className="inputView";
    window.opener.document.Frm1.datInizio.readOnly = true;
    window.opener.document.Frm1.datInizio.className="inputView";
    window.opener.document.Frm1.datInizMov.disabled = true;
    window.opener.document.Frm1.datInizMov.className="inputView";
    window.opener.document.Frm1.datFineMov.disabled = true;
    window.opener.document.Frm1.datFineMov.className="inputView";
    window.opener.document.Frm1.datInizio.disabled = true;
    window.opener.document.Frm1.datInizio.className="inputView";
    
    // dona           
    window.opener.selectMansioneOnClick(window.opener.document.Frm1.CODMANSIONE, '', '', '');         
    window.opener.btFindCCNL_onclick(window.opener.document.Frm1.codCCNL, '', '', '', 'codice');
    
    window.close();
  }

  function addDay(data, day)
  { //ricavo giorno mese e anno dalla stringa date passata
    //e sommo un giorno. Tutti i controlli (es. 28 Feb +1 -> 1 Mar)
    //vengono fatti dal costruttore: Date(anno,mese,gior).
    var anno = parseInt(data.substring(6,10),10);
    var mese = parseInt(data.substring(3,5),10) - 1;//NOTA:nel costruttore Date() i mesi vanno da 0 a 11
    var gior = parseInt(data.substring(0,2),10) + eval(day); //incremento il giorno
    var dataTmp = new Date(anno,mese,gior);
    //riconverto la data in una stringa con il solto formato gg/mm/aaaa
    //inserendo degli "0" quando necessario
    anno = dataTmp.getYear(); if(anno<100) {anno = anno + 1900;}
    mese = dataTmp.getMonth()+1;
    gior = dataTmp.getDate();
    var dataStr="**/**/****";
    if(gior<10){dataStr = "0"+gior;} else{dataStr = gior;}
    dataStr = dataStr + "/";
    if(mese<10){dataStr = dataStr + "0"+mese;} else{dataStr = dataStr + mese;}
    dataStr = dataStr + "/";
    dataStr = dataStr + anno;
    return dataStr
  }

  //eliminati i due parametri anche nella funzione onload mansione e ccnl

</script>
</head>

<body class="gestione" onload="aggiorna('<%=prgAzienda%>','<%=prgUnita%>','<%=strRagioneSociale%>','<%=strPartitaIva%>','<%=strIndirizzo%>',
                                        '<%=comuneAz%>','<%=strTel%>','<%=inizio%>','<%=fine%>','<%=prgMOV%>','<%=dataInizioMob%>',
                                        '<%=dataFineMob%>','<%=dataFineMaxDiff%>','<%=codMans%>', '<%=codGrado%>', '<%=livello%>', '<%=codCCNL%>', '<%=numOreSett%>');">
</body>
</html>