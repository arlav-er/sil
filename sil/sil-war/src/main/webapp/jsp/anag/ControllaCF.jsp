<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                 java.lang.*,
                 java.text.*,
                 java.util.*, 
                 it.eng.afExt.utils.StringUtils,
                 it.eng.sil.security.*,
                 it.eng.sil.util.*,
                 it.eng.sil.util.grabber.*"%>
            
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<% 
    String nome          = StringUtils.getAttributeStrNotNull(serviceRequest,"nome");
    String cognome       = StringUtils.getAttributeStrNotNull(serviceRequest,"cognome");
    String sesso         = StringUtils.getAttributeStrNotNull(serviceRequest,"sesso");
    String dataNasc      = StringUtils.getAttributeStrNotNull(serviceRequest,"datNasc");
    String comNasc       = StringUtils.getAttributeStrNotNull(serviceRequest,"comNasc");
    String provNasc      = StringUtils.getAttributeStrNotNull(serviceRequest,"provNasc");
    String CFinserito    = StringUtils.getAttributeStrNotNull(serviceRequest,"CF");

    String campoNome     = StringUtils.getAttributeStrNotNull(serviceRequest,"campoNome");
    String campoCognome  = StringUtils.getAttributeStrNotNull(serviceRequest,"campoCognome");
    String campoSesso    = StringUtils.getAttributeStrNotNull(serviceRequest,"campoSesso");
    String campoDataNasc = StringUtils.getAttributeStrNotNull(serviceRequest,"campoDatNasc");
    String campoComNasc  = StringUtils.getAttributeStrNotNull(serviceRequest,"campoComNasc");
    String campoCodComNasc = StringUtils.getAttributeStrNotNull(serviceRequest,"campoCodComNasc");
    String campoCF       = StringUtils.getAttributeStrNotNull(serviceRequest,"campoCF");
    String campoFlagOkCF = StringUtils.getAttributeStrNotNull(serviceRequest,"campoFlagOkCF");

    String CFministero   = "";
    
    String chkCF    = StringUtils.getAttributeStrNotNull(serviceRequest,"checkCF");
    boolean checkCF = chkCF.equalsIgnoreCase("true") ? true : false;

    String cognomeOttenuto  = "datoDiProva";
    String nomeOttenuto     = "datoDiProva";
    String dataNascOttenuta = "datoDiProva";
    String comNascOttenuto  = "datoDiProva";
    String provNascOttenuta = "datoDiProva";
    String sessoOttenuto    = "datoDiProva";
    String codComuneRicavato= "datoDiProva";
    
    int errorCode = 0;
    int errCodeAnag = 0;
    String strErrMsg = "Nessun messaggio di errore";
    String errorMSG  = "NESSUN MSG";
    boolean esito     = true;
    boolean esitoCF   = true;
    boolean esitoAnag = true;
    String tr_td = "";
    
    PersonaFiscale pf = new PersonaFiscale();
    
        

    try 
    {
    
      if(checkCF) 
      { pf.setCognome(cognome);      
        pf.setNome(nome);
        pf.setDataNascita(dataNasc);
        pf.setSesso(sesso);
        pf.setComuneNascita(comNasc);
        pf.setProvinciaNascita(provNasc);
        
        pf.eseguiCtrlDatiAnag();
        CFministero = pf.getCodiceFiscale();
        if (!CFinserito.equals("") && !CFinserito.equalsIgnoreCase(CFministero)) esitoCF = false;
        else CFinserito = CFministero;
      }
      
      else
      { pf.setCodiceFiscale(CFinserito);
        pf.eseguiCtrlCodFiscale();
        
        nomeOttenuto     = pf.getNome();
        cognomeOttenuto  = pf.getCognome();  
        dataNascOttenuta = pf.getDataNascita();
        comNascOttenuto  = pf.getComuneNascita();
        provNascOttenuta = pf.getProvinciaNascita();
        sessoOttenuto    = pf.getSesso();
        CFministero      = pf.getCodiceFiscale();
        codComuneRicavato = CFministero.substring(11,15);


        if(!nome.equals("") && !nome.trim().equalsIgnoreCase(nomeOttenuto.trim())) 
        { esitoAnag = false; errCodeAnag = 1; 
          tr_td += "<tr><td align=\"right\">Nome</td><td align=\"right\"><strong>"+StringUtils.formatValue4Html(nome)+"</strong>&nbsp;&nbsp;&nbsp;</td><td align=\"left\"><strong>"+nomeOttenuto+"</strong></td><tr>\n";
        }
        if(!cognome.equals("") && !cognome.trim().equalsIgnoreCase(cognomeOttenuto.trim())) 
        { esitoAnag = false; errCodeAnag = 2; 
          tr_td += "<tr><td align=\"right\">Cognome</td><td align=\"right\"><strong>"+StringUtils.formatValue4Html(cognome)+"</strong>&nbsp;&nbsp;&nbsp;</td><td align=\"left\"><strong>"+cognomeOttenuto+"</strong></td><tr>\n";
        }
        if(!dataNasc.equals("") && !dataNasc.equalsIgnoreCase(dataNascOttenuta)) 
        { esitoAnag = false; errCodeAnag = 3; 
          tr_td += "<tr><td align=\"right\">Data di nascita</td><td align=\"right\"><strong>"+dataNasc+"</strong>&nbsp;&nbsp;&nbsp;</td><td align=\"left\"><strong>"+dataNascOttenuta+"</strong></td><tr>\n";
        }
        if(!comNasc.equals("") && !comNasc.trim().equalsIgnoreCase(comNascOttenuto.trim())) 
        { esitoAnag = false; errCodeAnag = 4;
          tr_td += "<tr><td align=\"right\">Comune di nascita</td><td align=\"right\"><strong>"+StringUtils.formatValue4Html(comNasc)+"</strong>&nbsp;&nbsp;&nbsp;</td><td align=\"left\"><strong>"+comNascOttenuto+"</strong></td><tr>\n";
        }
        if(!provNasc.equals("") && !provNasc.equalsIgnoreCase(provNascOttenuta)) 
        { esitoAnag = false; errCodeAnag = 5;
          tr_td += "<tr><td align=\"right\">Provincia di nascita</td><td align=\"right\"><strong>"+provNasc+"</strong>&nbsp;&nbsp;&nbsp;</td><td align=\"left\"><strong>"+provNascOttenuta+"</strong></td><tr>\n";
        }
        if(!sesso.equals("") && !sesso.equalsIgnoreCase(sessoOttenuto)) 
        { esitoAnag = false; errCodeAnag = 6;
          tr_td += "<tr><td align=\"right\">Sesso</td><td align=\"right\"><strong>"+sesso+"</strong>&nbsp;&nbsp;&nbsp;</td><td align=\"left\"><strong>"+sessoOttenuto+"</strong></td><tr>\n";
        }
        if(!CFinserito.equals("") && !CFinserito.equalsIgnoreCase(CFministero)) 
        { esitoAnag = false; errCodeAnag = 6;
          tr_td += "<tr><td align=\"right\">Codice fiscale</td><td align=\"right\"><strong>"+CFinserito+"</strong>&nbsp;&nbsp;&nbsp;</td><td align=\"left\"><strong>"+CFministero+"</strong></td><tr>\n";
        }
      }//else

    } 
    catch (PersonaFiscaleException ex)
    { 
      //  System.out.println("*** ERRORE ***");
      //  System.out.println("Codice di errore: " + ex.getErrorCode());
      //  System.out.println("Descrizione     : " + ex.getMessage());
      errorCode= ex.getErrorCode();
      errorMSG = ex.getMessage();
      esito = false;
    }
    
    
    
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>



<html>
<head>
<title>Controlla Codice Fiscale</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/" />

<SCRIPT TYPE="text/javascript">
function AggiornaCF (CF, validitaCF) {
  if (CF != '')
        window.opener.document.forms[0].<%=campoCF%>.value = CF;
  if (validitaCF != '')
        window.opener.document.forms[0].<%=campoFlagOkCF%>.value = validitaCF;  
         window.opener.cfIsChecked();
  window.close();
}

function AggiornaAnag()
{ window.opener.document.forms[0].<%=campoNome    %>.value = '<%=StringUtils.formatValue4Javascript(nomeOttenuto)    %>';
  window.opener.document.forms[0].<%=campoCognome %>.value = '<%=StringUtils.formatValue4Javascript(cognomeOttenuto) %>';
  window.opener.document.forms[0].<%=campoSesso   %>.value = '<%=sessoOttenuto   %>';
  window.opener.document.forms[0].<%=campoDataNasc%>.value = '<%=dataNascOttenuta%>';
  window.opener.document.forms[0].<%=campoComNasc %>.value = '<%=StringUtils.formatValue4Javascript(comNascOttenuto)%>'+' ('+'<%=provNascOttenuta%>'+')';
  window.opener.document.forms[0].<%=campoCodComNasc %>.value = '<%=codComuneRicavato%>'; 
  window.opener.document.forms[0].<%=campoFlagOkCF%>.value = 'S';    
  window.close();
}
</SCRIPT>


</head>
<body class="gestione">
  <br/><br/>
  <%out.print(htmlStreamTop);%>
<%if(!esito){%>
  <table class="main">
   <tr><td colspan="2">Non &egrave; stato possibile verificare i dati richiesti<br/>per il seguente motivo:</td></tr>
   <tr><td colspan="2">&nbsp;</td></tr>
   <tr><td colspan="2"><strong><%=errorMSG%></strong></td></tr>
   <tr><td colspan="2">&nbsp;</td></tr>
   <tr><td colspan="2"><input type="button" class="pulsante" name="chiudi" value=" Chiudi" onClick="window.close()"/></td></tr>
  </table>
<%} else {
  if(checkCF) {%>
  <table class="main">
      <%if(!esitoCF) {%>
       <tr><td colspan="2"><strong>ATTENZIONE!!</strong></td></tr>
       <tr><td colspan="2">&nbsp;</td></tr>
       <tr><td colspan="2">Il codice fiscale digitato è diverso da quello ottenuto dal Ministero delle Finanaze</td></tr>
       <tr><td colspan="2">&nbsp;</td></tr>
       <tr><td colspan="2">&nbsp;</td></tr>
       <tr><td class="etichetta">digitato</td><td class="campo"><strong><%=CFinserito%></strong></td></tr>
       <tr><td class="etichetta">ottenuto</td><td class="campo"><strong><%=CFministero%></strong></td></tr>
       <tr><td colspan="2">&nbsp;</td></tr>
       <tr><td colspan="2"><input type="button" class="pulsante" name="ok" value="Inserisci il codice ottenuto" onClick="AggiornaCF('<%=CFministero%>','S')"/><td></tr>
       <tr><td colspan="2"><input type="button" class="pulsante" name="ok" value=" Chiudi " onClick="window.close()"/><td></tr>
  </table>
      <%} else {%>
        <script language="javascript">AggiornaCF('<%=CFinserito%>','S')</script>
      <%}%>
  <%}//if(checkCF)
  else {
      if(!esitoAnag){%>
  <table class="main">
       <tr><td colspan="3"><strong>ATTENZIONE!</strong></td></tr>
       <tr><td colspan="3">I seguenti dati anagrafici sono differenti</td></tr>
       <tr><td colspan="3">&nbsp;</td></tr>
       <tr><td width="30%">&nbsp;</td><td align="right" width="20%">Inseriti&nbsp;&nbsp;&nbsp;</td><td align="left" width="50%">Ottenuti</td><tr>
           <%=tr_td%>
       <tr><td colspan="3">&nbsp;</td></tr>
       <tr><td colspan="3">
           <input type="button" class="pulsante" name="ok" value=" Inserisci dati ottenuti" onClick="AggiornaAnag()"/>
           </td>
       </tr>
       <tr><td colspan="3">
           <input type="button" class="pulsante" name="chiudi" value=" Chiudi" onClick="window.close()"/>
           </td>
       </tr>
  </table>
      <%} else {%>
        <script language="javascript">AggiornaAnag()</script>
      <%}
  }  
  }%>
<%out.print(htmlStreamBottom);%>

</body>
</html>
