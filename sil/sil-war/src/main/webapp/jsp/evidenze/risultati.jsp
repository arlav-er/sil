<%@ page
	contentType="text/html;charset=utf-8"
	
	import="com.engiweb.framework.base.*,
			it.eng.afExt.utils.*,
			it.eng.sil.security.*,
			it.eng.sil.util.*,
			java.math.*,
			java.util.*"
	
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%
//CANCELLA L'ATTRIBUTO SALVA, PERCHE' DOPO L'AGGIORNAMENTO DELL'EVIDENZA SI AGGIORNA LA LISTA,
//SE CAMBIO PAGINA, NON DEVE LANCIARE IL MODULO AGGIORNA DI NUOVO.
try{
	if (serviceRequest.containsAttribute("SALVA"))
		serviceRequest.delAttribute("SALVA");
}catch(SourceBeanException sbe){}
	int cdnfunzione = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");

	String htmlStreamTop    = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false); 
	
	String codiceFiscale =  StringUtils.getAttributeStrNotNull(serviceRequest,"codiceFiscale");
	String cognome =		StringUtils.getAttributeStrNotNull(serviceRequest,"cognome");
	String nome =			StringUtils.getAttributeStrNotNull(serviceRequest,"nome");
	String tipoRicerca = 	StringUtils.getAttributeStrNotNull(serviceRequest,"tipoRicerca");
	String codTipoValidita=	StringUtils.getAttributeStrNotNull(serviceRequest,"codTipoValidita");
	String strTipoEvidenza=	StringUtils.getAttributeStrNotNull(serviceRequest,"strTipoEvidenza");//TRASFORMARE IN VECTOR
	Vector codTipoEvidenzaVector = serviceRequest.getAttributeAsVector("codTipoEvidenza");
	String dataScad_DAL=	StringUtils.getAttributeStrNotNull(serviceRequest,"dataScadenza_DAL");
	String dataScad_AL=		StringUtils.getAttributeStrNotNull(serviceRequest,"dataScadenza_AL");
	String messaggio = 		StringUtils.getAttributeStrNotNull(serviceRequest,"messaggio");
	String strCPI=			StringUtils.getAttributeStrNotNull(serviceRequest,"strCPI");
	String codCPI=			StringUtils.getAttributeStrNotNull(serviceRequest,"codCPI"); //Solo per il ritorno alla ricerca
	String filtriRicerca="";
	String ritornoRicerca="&tipoRicerca="+tipoRicerca;
	if(!codiceFiscale.equals("")){
		ritornoRicerca+="&codiceFiscale="+codiceFiscale;
		filtriRicerca+="codice fiscale: <strong>"+codiceFiscale+"</strong>"+(tipoRicerca.equals("esatta")?" (esatta)":" (inizia per)")+"; ";}
	if(!cognome.equals("")){
		ritornoRicerca+="&cognome="+cognome;
		filtriRicerca+="cognome: <strong>"+cognome+"</strong>"+(tipoRicerca.equals("esatta")?" (esatta)":" (inizia per)")+"; ";
	}
	if(!nome.equals("")){
		ritornoRicerca+="&nome="+nome;
		filtriRicerca+="nome: <strong>"+nome+"</strong>"+(tipoRicerca.equals("esatta")?" (esatta)":" (inizia per)")+"; ";
	}
	if(!codTipoValidita.equals(""))	{
		ritornoRicerca+="&codTipoValidita="+codTipoValidita;
		filtriRicerca+="tipo validità: <strong>"+(codTipoValidita.equals("V")?"Valida":"Scaduta")+"</strong>; ";
	}
	
	if(codTipoEvidenzaVector.size()>0){
		String codTipoEvidenza="";
		ritornoRicerca+="&codTipoEvidenza=";
		for (int i=0;i<codTipoEvidenzaVector.size();i++){
			codTipoEvidenza=(String)codTipoEvidenzaVector.get(i);
			ritornoRicerca+=codTipoEvidenza+",";
		}
		ritornoRicerca = ritornoRicerca.substring(0,ritornoRicerca.length()-1);
	}
	
	if(!strTipoEvidenza.equals("")){
		filtriRicerca+="tipo evidenza: <strong>"+strTipoEvidenza+"</strong>; ";
		ritornoRicerca+="&strtipoevidenza="+strTipoEvidenza;
	}
	
	
	if(!dataScad_DAL.equals("")){
		ritornoRicerca+="&dataScadenza_DAL="+dataScad_DAL;
		filtriRicerca+="data di scadenza dal: <strong>"+dataScad_DAL+"</strong>; ";
	}
	if(!dataScad_AL.equals(""))		{
		ritornoRicerca+="&dataScadenza_AL="+dataScad_AL;
		filtriRicerca+="data di scadenza al: <strong>"+dataScad_AL+"</strong>; ";
	}
	if(!messaggio.equals("")){
		ritornoRicerca+="&messaggio="+messaggio;
		filtriRicerca+="messaggio: <strong>"+messaggio+"</strong>; ";
	}
	if (!codCPI.equals(""))
		ritornoRicerca+="&codCPI="+codCPI;
		
	if(!strCPI.equals(""))	{
		filtriRicerca+="CPI: <strong>"+strCPI+"</strong>; ";
		ritornoRicerca+="&strCPI="+strCPI;
	}
	
	%>
	
<html>
<head>
<title>Ricerca evidenze lavoratore</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>
<script type="text/javascript">
function toRicerca(){
	if(isInSubmit())return;
	var url = "AdapterHTTP?PAGE=RicercaEvidenzePage&cdnfunzione=1044";
	url+="<%=ritornoRicerca%>";
	setWindowLocation(url);
}

function mostraDettaglio(prgevidenza,page,cdnlavoratore,cdnfunzione,list_page){
	var url = "AdapterHTTP?CDNLAVORATORE="+cdnlavoratore+"&CDNFUNZIONE=1044&PAGE="+page+"&prgevidenza="+prgevidenza;
		url +="&LIST_PAGE="+list_page;
	window.open(url,'_blank','height=400,width=850,left=200,top=200,scrollbars=yes,status=no,location=no,toolbar=no');
}
</script>

</head>
<body class="gestione" onload="rinfresca();rinfresca_laterale();">

<af:showErrors/>
<af:showMessages prefix="MSalvaEvidenza"/>
<p class="titolo">RISULTATI EVIDENZE CERCATE</p>
<table width="100%" cellspacing="10" cellpadding="2" border="0">
<tr>
<td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
Filtri di ricerca:<br>
<%=filtriRicerca %>
</td>
</tr>		  
</table>


<af:form name="form">
<input type="hidden" name="backURL" value="<%=ritornoRicerca%>"/>
<af:list moduleName="M_RicercaEvidenze" jsSelect="mostraDettaglio"/>

<p align="center">
<input type="button" class="pulsanti" value="Ritorna alla ricerca" onclick="toRicerca()"/>
</p>

</af:form>
</body>
</html>