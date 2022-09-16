<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<% 
	boolean isRicerca= (!(boolean)serviceRequest.containsAttribute("apprendistato"));
	
  /*
  Vector tipiAtecoRows = serviceResponse.getAttributeAsVector("M_GetTipiAttivita.ROWS.ROW");
  */
	
  Vector CCNLRows = serviceResponse.getAttributeAsVector("M_GETCCNL.ROWS.ROW");
  
  SourceBean row_CCNL= null;
  String tipo_ccnl="";
  String codCCNL="";
 
%>

<html>
<head>
<title>Codici di attività</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>
<SCRIPT TYPE="text/javascript">
var flagChanged = false;
<!--

  //dichiaro gli array per riempire la mia supercombo
   var CCNL_tipo=new Array();
   var CCNL_cod=new Array();
   var CCNL_des=new Array();
   var mesi_apprendistato=new Array();
   
   <%     
   String mesiApprendistat = "";
   
    for(int i=0; i<CCNLRows.size(); i++)  { 
      row_CCNL = (SourceBean) CCNLRows.elementAt(i);
      if((boolean)row_CCNL.containsAttribute("NUMDURATAAPPRENDIST")&& !isRicerca){
  	 		mesiApprendistat = row_CCNL.getAttribute("NUMDURATAAPPRENDIST").toString();
   	  }else{
   	  		mesiApprendistat = "";
   	  }
      out.print("CCNL_tipo["+i+"]=\""+ row_CCNL.getAttribute("TIPO").toString()+"\";\n");
      out.print("CCNL_cod["+i+"]=\""+ row_CCNL.getAttribute("CODICE").toString()+"\";\n");
      out.print("CCNL_des["+i+"]=\""+ row_CCNL.getAttribute("DESCRIZIONE").toString()+"\";\n");  
      out.print("mesi_apprendistato["+i+"]=\""+ mesiApprendistat.toString() +"\";\n");  
    } 
  %>
  function fieldChanged() {
    flagChanged = true;
  }
-->
  </SCRIPT>
<SCRIPT TYPE="text/javascript">

<!--

//document.Frm1.codCCNL.options[document.Frm1.codCCNL.selectedIndex].text
//codCCNL
function caricaMesiApprendistato(codCCNL){
	var i;
	var campoMesi = document.getElementById("mesiApprend");
	for (i=0; i<CCNL_cod.length ;i++) {
			if (CCNL_cod[i] == codCCNL) {
			  	campoMesi.value = mesi_apprendistato[i];
		      	break;
       		}
    } 
}


function caricaCCNL(codTipoCCNL) {
   i=0;
   j=0; 

   objCCNL= eval("document.forms[0].codCCNL");
   
   var selectElement = eval("document.Frm1.codTipoCCNL");
   var index = selectElement.selectedIndex;
   var optionSelected = selectElement[index];
   //alert(optionSelected.text);
   //alert(optionSelected.value);

 //  if (isScaduto(optionSelected.text)) {
 //  	   alert("Non è possibile inserire un Tipo CCNL scaduto");
 //  	   if (document.Frm1.codCCNL.selectedIndex >= 0) {
	   		////document.Frm1.codCCNL.options[document.Frm1.codCCNL.selectedIndex].text = "";
//	   		var lunghezza = document.Frm1.codCCNL.options.length;
//	   		for (var punt=lunghezza-1; punt>=0; punt--) {
//	   			document.Frm1.codCCNL.options[punt] = null;
//	   		}
//	   }
 //  } else {
	   objCCNL.options[0]=new Option();//salta una riga (aggiunge un blank)
	   
	   while (document.Frm1.codCCNL.options.length>0) {
	        document.Frm1.codCCNL.options[0]=null;
	    }
	    for (i=0; i<CCNL_tipo.length ;i++) {
	       if (CCNL_tipo[i]==codTipoCCNL) {
	      	  objCCNL.options[j]=new Option(CCNL_des[i], CCNL_cod[i]);
	          //setto il selected
	          objCCNL.options[j].selected=(CCNL_cod[i]=="<%=codCCNL%>");
	           j++;
	       }
       }
   // } 
}


function selectCCNL(strCCNL) {
	if (strCCNL.value!="") {
	      //window.open("AdapterHTTP?PAGE=RicercaAtecoPage&strAteco="+strAteco.value, "Attività", 'toolbar=0, scrollbars=1');
		  if(<%=isRicerca%>){
		  	var url = "AdapterHTTP?PAGE=RicercaCCNLPage&TORNALISTA=&strdenominazione=" + strCCNL.value;
		  }else{
		      var url = "AdapterHTTP?PAGE=RicercaCCNLPage&TORNALISTA=&strdenominazione=" + strCCNL.value +"&apprendistato=";
		  }
	      
	      setWindowLocation(url);
	}
}


function aggiornaForm (codCCNL,strCCNL,Descpadre) {
  if (codCCNL != "" && strCCNL != "") {
  	
    if(window.opener.document.Frm1.strCCNLPadre == undefined){
	 	var mesiApprendistato = document.getElementById("mesiApprend").value;
	 	if(<%=!isRicerca%>)
		 	window.opener.document.Frm1.NUMMESIAPPRENDISTATO.value=mesiApprendistato;		 	
	}else{
		window.opener.document.Frm1.strCCNLPadre.value=Descpadre;		
	}
	window.opener.document.Frm1.codCCNL.value = codCCNL;
    window.opener.document.Frm1.codCCNLHid.value=codCCNL;
	window.opener.document.Frm1.strCCNL.value = strCCNL.replace('^', '\'');
 	window.opener.document.Frm1.strCCNLHid.value = strCCNL.replace('^', '\'');
 	window.close();	
  } 
} 


function isScaduto(str) {
	if (opener.flagRicercaPage != "S") { // Se NON provengo da una page di ricerca ammetto solo quelli non scaduti.
		if (str.substring(str.length-9) == "(scaduto)") {
			return true;
		}
	}
	return false;
}

-->
</SCRIPT>

</head>
<body class="gestione" >
<af:form name="Frm1" method="POST" action="" >
<br/>
<p class="titolo"><b>CCNL - ricerca avanzata</b></p>
<TABLE class="lista" align="center">
<%
  //SourceBean row_tipiAteco= null;
  //String tipiAteco_cod= null;
  //String tipiAteco_des=null;
%>

    <tr valign="top">
      <td class="etichetta" nowrap=true>Tipo di CCNL</td>
      <td class="campo" >
        <af:comboBox classNameBase="input" name="codTipoCCNL" selectedValue="<%=tipo_ccnl%>" moduleName="M_GetTipiCCNL" addBlank="True" onChange="caricaCCNL(document.Frm1.codTipoCCNL.value);fieldChanged();"  />
      </td>
    </tr>
    <tr>
      <td class="etichetta" nowrap=true>CCNL</td>
      <td class="campo">
        <af:comboBox classNameBase="input" onChange="fieldChanged();" name="codCCNL" selectedValue="" />
        <input type="hidden" name="mesi" value="" id="mesiApprend" />
        &nbsp;
          <img src="../../img/add.gif" name="seleziona" value="seleziona"  onClick="caricaMesiApprendistato(document.Frm1.codCCNL.value);aggiornaForm(document.Frm1.codCCNL.value,document.Frm1.codCCNL.options[document.Frm1.codCCNL.selectedIndex].text,document.Frm1.codTipoCCNL.options[document.Frm1.codTipoCCNL.selectedIndex].text);"/>
      </td>
    </tr>                   
    
    <tr>
	    <td colspan="2"><HR width="1"/></td>
    </tr>
    
    <tr>
      <td class="etichetta" nowrap=true>Ricerca per descrizione</td>
      <td class="campo">
         <af:textBox size="30"  classNameBase="input" name="strCCNL" />
         
         <A href="javascript:selectCCNL(document.Frm1.strCCNL);">
                                <img src="../../img/binocolo.gif" alt="Cerca"></A>
     </td>
    </tr>                   
    
    
</TABLE>
<br/>
<center>
  <input type="button" name="chiudi" value="chiudi" class="pulsante" onClick="window.close();"/>  
</center>
</af:form>
<br/>
</body>
</html>